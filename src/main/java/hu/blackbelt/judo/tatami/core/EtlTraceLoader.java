package hu.blackbelt.judo.tatami.core;

/*-
 * #%L
 * Judo :: Tatami :: Core
 * %%
 * Copyright (C) 2018 - 2024 BlackBelt Technology
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 *
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 *
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 * #L%
 */

import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EStructuralFeature;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.util.EcoreUtil;

import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLOutputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.stream.XMLStreamWriter;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * TraceLoader implementation for ETL XMI trace format.
 *
 * <p>The ETL XMI format uses the following structure:</p>
 * <pre>{@code
 * <?xml version="1.0" encoding="UTF-8"?>
 * <xmi:XMI xmi:version="2.0" xmlns:xmi="http://www.omg.org/XMI"
 *     xmlns:trace="http:///www.blackbelt.hu/meta/trasformation/trace/namespace">
 *   <trace:Trace sourceUri="psm:model#_abc123">
 *     <targetUri>asm:model#_def456</targetUri>
 *     <targetUri>asm:model#_ghi789</targetUri>
 *   </trace:Trace>
 * </xmi:XMI>
 * }</pre>
 *
 * <p>Element resolution strategy:</p>
 * <ol>
 *   <li>Try URI fragment match using Resource.getEObject(fragment)</li>
 *   <li>Fall back to ID attribute match if URI fragment fails</li>
 * </ol>
 */
@Slf4j
public class EtlTraceLoader implements TraceLoader {

    private static final String XMI_NAMESPACE = "http://www.omg.org/XMI";
    private static final String TRACE_NAMESPACE_PREFIX = "http:///www.blackbelt.hu/meta/trasformation/trace/";
    private static final String SOURCE_URI_ATTR = "sourceUri";
    private static final String TARGET_URI_ELEMENT = "targetUri";

    private String traceNamespace = "default";

    /**
     * Creates an EtlTraceLoader with default namespace.
     */
    public EtlTraceLoader() {
    }

    /**
     * Creates an EtlTraceLoader with specified namespace.
     *
     * @param namespace the trace namespace (used for save operations)
     */
    public EtlTraceLoader(String namespace) {
        this.traceNamespace = namespace;
    }

    @Override
    public List<TraceEntry> loadTrace(InputStream input, List<ResourceSet> resourcesToResolve)
            throws TraceLoadException {
        try {
            // Build indexes for resolution
            Map<String, EObject> fragmentIndex = buildFragmentIndex(resourcesToResolve);
            Map<String, EObject> idIndex = buildIdIndex(resourcesToResolve);

            List<TraceEntry> result = new ArrayList<>();

            XMLInputFactory factory = XMLInputFactory.newInstance();
            // Security: disable external entities
            factory.setProperty(XMLInputFactory.IS_SUPPORTING_EXTERNAL_ENTITIES, false);
            factory.setProperty(XMLInputFactory.SUPPORT_DTD, false);

            XMLStreamReader reader = factory.createXMLStreamReader(new BufferedInputStream(input));

            String currentSourceUri = null;
            List<String> currentTargetUris = new ArrayList<>();

            while (reader.hasNext()) {
                int event = reader.next();

                if (event == XMLStreamConstants.START_ELEMENT) {
                    String localName = reader.getLocalName();

                    if ("Trace".equals(localName)) {
                        // Start of a new trace entry
                        currentSourceUri = reader.getAttributeValue(null, SOURCE_URI_ATTR);
                        currentTargetUris = new ArrayList<>();
                    }
                } else if (event == XMLStreamConstants.END_ELEMENT) {
                    String localName = reader.getLocalName();

                    if (TARGET_URI_ELEMENT.equals(localName)) {
                        // We collected targetUri text content
                    } else if ("Trace".equals(localName)) {
                        // End of trace entry - create TraceEntry
                        if (currentSourceUri != null) {
                            EObject source = resolveUri(currentSourceUri, fragmentIndex, idIndex);

                            List<EObject> targets = new ArrayList<>();
                            for (String targetUri : currentTargetUris) {
                                targets.add(resolveUri(targetUri, fragmentIndex, idIndex));
                            }

                            result.add(TraceEntry.builder()
                                    .source(source)
                                    .targets(targets)
                                    .build());
                        }
                        currentSourceUri = null;
                        currentTargetUris = new ArrayList<>();
                    }
                } else if (event == XMLStreamConstants.CHARACTERS) {
                    // Check if we're inside targetUri element
                    String text = reader.getText().trim();
                    if (!text.isEmpty() && currentSourceUri != null) {
                        currentTargetUris.add(text);
                    }
                }
            }

            reader.close();
            return result;

        } catch (TraceLoadException e) {
            throw e;
        } catch (Exception e) {
            throw new TraceLoadException("Failed to parse ETL XMI trace: " + e.getMessage(), e);
        }
    }

    @Override
    public void saveTrace(List<TraceEntry> entries, OutputStream output) throws TraceSaveException {
        try {
            XMLOutputFactory factory = XMLOutputFactory.newInstance();
            XMLStreamWriter writer = factory.createXMLStreamWriter(
                    new BufferedOutputStream(output), "UTF-8");

            writer.writeStartDocument("UTF-8", "1.0");
            writer.writeCharacters("\n");

            // Write XMI root element
            writer.writeStartElement("xmi", "XMI", XMI_NAMESPACE);
            writer.writeNamespace("xmi", XMI_NAMESPACE);
            writer.writeNamespace("trace", TRACE_NAMESPACE_PREFIX + traceNamespace);
            writer.writeAttribute("xmi", XMI_NAMESPACE, "version", "2.0");
            writer.writeCharacters("\n");

            // Write trace entries
            for (TraceEntry entry : entries) {
                EObject source = entry.getSource();
                if (source == null) {
                    continue;
                }

                writer.writeCharacters("  ");
                writer.writeStartElement("trace", "Trace", TRACE_NAMESPACE_PREFIX + traceNamespace);
                writer.writeAttribute(SOURCE_URI_ATTR, EcoreUtil.getURI(source).toString());
                writer.writeCharacters("\n");

                for (EObject target : entry.getTargets()) {
                    writer.writeCharacters("    ");
                    writer.writeStartElement(TARGET_URI_ELEMENT);
                    writer.writeCharacters(EcoreUtil.getURI(target).toString());
                    writer.writeEndElement();
                    writer.writeCharacters("\n");
                }

                writer.writeCharacters("  ");
                writer.writeEndElement(); // Trace
                writer.writeCharacters("\n");
            }

            writer.writeEndElement(); // XMI
            writer.writeEndDocument();
            writer.flush();
            writer.close();

        } catch (Exception e) {
            throw new TraceSaveException("Failed to save ETL XMI trace: " + e.getMessage(), e);
        }
    }

    private Map<String, EObject> buildFragmentIndex(List<ResourceSet> resourceSets) {
        Map<String, EObject> index = new HashMap<>();

        for (ResourceSet rs : resourceSets) {
            for (TreeIterator<Notifier> it = rs.getAllContents(); it.hasNext(); ) {
                Notifier notifier = it.next();
                if (notifier instanceof EObject) {
                    EObject eObject = (EObject) notifier;
                    String fragment = EcoreUtil.getURI(eObject).fragment();
                    if (fragment != null) {
                        index.put(fragment, eObject);
                    }
                }
            }
        }

        return index;
    }

    private Map<String, EObject> buildIdIndex(List<ResourceSet> resourceSets) {
        Map<String, EObject> index = new HashMap<>();

        for (ResourceSet rs : resourceSets) {
            for (TreeIterator<Notifier> it = rs.getAllContents(); it.hasNext(); ) {
                Notifier notifier = it.next();
                if (notifier instanceof EObject) {
                    EObject eObject = (EObject) notifier;
                    String id = getIdAttribute(eObject);
                    if (id != null) {
                        index.put(id, eObject);
                    }
                }
            }
        }

        return index;
    }

    private String getIdAttribute(EObject eObject) {
        EStructuralFeature idFeature = eObject.eClass().getEStructuralFeature("id");
        if (idFeature != null) {
            Object idValue = eObject.eGet(idFeature);
            if (idValue != null) {
                return idValue.toString();
            }
        }
        return null;
    }

    private EObject resolveUri(String uriString, Map<String, EObject> fragmentIndex, Map<String, EObject> idIndex) {
        URI uri = URI.createURI(uriString);
        String fragment = uri.fragment();

        if (fragment == null) {
            throw new TraceLoadException("URI has no fragment: " + uriString);
        }

        // Strategy 1: Try URI fragment match (original behavior)
        EObject result = fragmentIndex.get(fragment);
        if (result != null) {
            return result;
        }

        // Strategy 2: Fall back to ID attribute match
        result = idIndex.get(fragment);
        if (result != null) {
            return result;
        }

        throw new TraceLoadException("Element not found: " + uriString);
    }
}
