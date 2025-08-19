package hu.blackbelt.judo.tatami.core;

/*-
 * #%L
 * Judo :: Tatami :: Loader
 * %%
 * Copyright (C) 2018 - 2022 BlackBelt Technology
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

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.common.util.TreeIterator;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;

import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static org.eclipse.emf.ecore.util.builder.EcoreBuilders.*;

/**
 * This utility class helps to manage model transformation traces. By default Epsilon trace can be used, but to trace
 * map possible add extra entites by code.
 *
 * The trace meta model is a pseudo meta model which are created on the fly with the structure below.
 * It is an model entries which represents the URI's of source and target map. THe model contains trace entries which
 * have one source {@link URI} and several target {@link URI}. The {@link URI} are represented as {@link String}.
 * The {@link URI} based on the source and target {@link URI}, so to be able to resolve the trace later the resource
 * have to be available with the same {@link URI}.
 *
 * <pre>
 * {@code
 * <?xml version="1.0" encoding="UTF-8"?>
 * <ecore:EPackage xmi:version="2.0" xmlns:xmi="http://www.omg.org/XMI" xmlns:xsi="http://www.w3.org/2001/XMLSchema-instance"
 *     xmlns:ecore="http://www.eclipse.org/emf/2002/Ecore" name="trace" nsURI="http:///www.blackbelt.hu/meta/trasformation/trace/psm2asm"
 *     nsPrefix="trace">
 *   <eClassifiers xsi:type="ecore:EClass" name="Trace">
 *     <eStructuralFeatures xsi:type="ecore:EAttribute" name="sourceUri" eType="ecore:EDataType http://www.eclipse.org/emf/2002/Ecore#//EString"/>
 *     <eStructuralFeatures xsi:type="ecore:EAttribute" name="targetUri" upperBound="-1"
 *         eType="ecore:EDataType http://www.eclipse.org/emf/2002/Ecore#//EString"/>
 *   </eClassifiers>
 * </ecore:EPackage>
 * }
 * </pre>
 *
 * Example of trace model:
 *
 * <pre>
 * {@code
 *    <?xml version="1.0" encoding="ASCII"?>
 *    <xmi:XMI xmi:version="2.0" xmlns:xmi="http://www.omg.org/XMI"
 *        xmlns:trace="http:///www.blackbelt.hu/meta/trasformation/trace/psm2asm">
 *        <trace:Trace sourceUri="psm:northwind#_w9fJgCVuEemLpvUY7MQgng">
 *            <targetUri>asm:northwind#_a-XSZLbPEemHPoxuB2rK2w</targetUri>
 *            <targetUri>asm:northwind#_a-aVsLbPEemHPoxuB2rK2w</targetUri>
 *            <targetUri>asm:northwind#_a-cx8LbPEemHPoxuB2rK2w</targetUri>
 *        </trace:Trace>
 *        <trace:Trace sourceUri="psm:northwind#_cg0_UIFIEemFzY3ZWApzVQ">
 *            <targetUri>asm:northwind#_a-BUI7bPEemHPoxuB2rK2w</targetUri>
 *            <targetUri>asm:northwind#_a-CiSLbPEemHPoxuB2rK2w</targetUri>
 *            <targetUri>asm:northwind#_a-EXc7bPEemHPoxuB2rK2w</targetUri>
 *            <targetUri>asm:northwind#_a-E-h7bPEemHPoxuB2rK2w</targetUri>
 *            <targetUri>asm:northwind#_a-Gzs7bPEemHPoxuB2rK2w</targetUri>
 *            <targetUri>asm:northwind#_a-Io4LbPEemHPoxuB2rK2w</targetUri>
 *            <targetUri>asm:northwind#_a-J3ArbPEemHPoxuB2rK2w</targetUri>
 *            <targetUri>asm:northwind#_a-LFIbbPEemHPoxuB2rK2w</targetUri>
 *            <targetUri>asm:northwind#_a-MTQrbPEemHPoxuB2rK2w</targetUri>
 *            <targetUri>asm:northwind#_a-Ry07bPEemHPoxuB2rK2w</targetUri>
 *            <targetUri>asm:northwind#_a-TA8bbPEemHPoxuB2rK2w</targetUri>
 *            <targetUri>asm:northwind#_a-bj2rbPEemHPoxuB2rK2w</targetUri>
 *            <targetUri>asm:northwind#_a-dZB7bPEemHPoxuB2rK2w</targetUri>
 *        </trace:Trace>
 * }
 * </pre>
 *
 * So to resolve map have to be one or more {@link ResourceSet} presented which contains {@link Resource}
 * with asm:northwind  and psm:northwind {@link URI}. If there is no {@link URI} with that {@link URIHandler} can
 * be used for resolve.
 */
@Slf4j
public class TransformationTraceLoader {

    private static final String HTTP_WWW_BLACKBELT_HU_META_TRASFORMATION_TRACE = "http:///www.blackbelt.hu/meta/trasformation/trace/";
    private static final String TRACE_CLASS = "Trace";
    private static final String SOURCE_URI = "sourceUri";
    private static final String TARRGET_URIS = "targetUri";
    private static final String TRACE_SELECTOR = "trace:";

    /**
     * Resolves trace:Trace entries from trace model and source / target model. THe model contains one source
     * {@link URI} and several target {@link URI}. This mehod returns the resolved object map,
     * where the key is a source {@link EObject} instance, the value is a {@link List} of target {@link EObject}
     * instances.
     * @param traceEntries the trace model trace:Trace entries.
     * @param resourcesToResolve the {@link ResourceSet} instances which used to resolve URI's
     * @return trace {@link EObject} map
     */
    @SuppressWarnings("unchecked")
    public static Map<EObject, List<EObject>> resolveTransformationTraceAsEObjectMap(List<EObject> traceEntries, List<ResourceSet> resourcesToResolve) {

        EMap<EObject, List<EObject>> ret = ECollections.asEMap(Maps.newHashMap());
        //Map<String, EObject> cache = new HashMap<>();
        Map<String, EObject> cache = new HashMap<>();

        for (ResourceSet rs : resourcesToResolve) {
            for (TreeIterator<Notifier> it = rs.getAllContents(); it.hasNext(); ) {
                Notifier notifier = it.next();
                if (notifier instanceof EObject) {
                    EObject eObject = (EObject) notifier;
                    cache.put(EcoreUtil.getURI(eObject).fragment(), eObject);
                }
            }
        }

        for (EObject tr : traceEntries) {
            EClass traceClass = tr.eClass();
            EAttribute srcUriAttribute = (EAttribute) traceClass.getEStructuralFeature(SOURCE_URI);
            EAttribute targetUriAttributes = (EAttribute) traceClass.getEStructuralFeature(TARRGET_URIS);

            EObject source = null;
            URI sourceURI = URI.createURI((String) tr.eGet(srcUriAttribute, false));
            if (cache.containsKey(sourceURI.fragment())) {
                source = cache.get(sourceURI.fragment());
            }
            if (source == null) {
                throw new RuntimeException("Source entry not found on the given resources: " + sourceURI);
            }

            List<EObject> targetList = ECollections.newBasicEList(); //Lists.newArrayList();
            for (String t : (Collection<String>) tr.eGet(targetUriAttributes, false)) {
                EObject target = null;
                URI targetURI = URI.createURI(t);
                target = cache.get(targetURI.fragment());
                if (target == null) {
                    throw new RuntimeException("Target entry not found on the given resources: " + t);
                }

                targetList.add(target);
            }
            ret.put(source, targetList);
        }
        return new EMapWrapper<>(ret);
    }



    /**
     * Get list of trace:Trace entries for trace {@link EObject} map. This utils converts physical {@link EObject}
     * mao to {@link URI} trace entries based on the {@link EObject} 's  {@link Resource} {@link URI},
     *
     * @param nameSpace the URI postfix for used schema
     * @param traceEObjectMap the source and target {@link EObject} mao
     * @return trace:Trace {@link EObject} entries.
     */
    public static List<EObject> getTransformationTraceFromTraceMap(String nameSpace, Map<EObject, List<EObject>> traceEObjectMap) {
        ResourceSet resourceSet = createTraceResourceSet(nameSpace);
        EPackage tracePackage = resourceSet.getPackageRegistry().getEPackage(HTTP_WWW_BLACKBELT_HU_META_TRASFORMATION_TRACE + nameSpace);
        EClassImpl traceClass = (EClassImpl) tracePackage.getEClassifier(TRACE_CLASS);
        EAttribute srcUriAttribute = (EAttribute) traceClass.getEStructuralFeature(SOURCE_URI);
        EAttribute targetUriAttributes = (EAttribute) traceClass.getEStructuralFeature(TARRGET_URIS);

        List<EObject> traceEntries = ECollections.newBasicEList(); // Lists.newArrayList();
        for (EObject source : traceEObjectMap.keySet()) {
            EObject trace = tracePackage.getEFactoryInstance().create(traceClass);
            trace.eSet(srcUriAttribute, EcoreUtil.getURI(source).toString());

            List<String> targets = Lists.newArrayList();
            for (Object t : traceEObjectMap.get(source)) {
                if (t instanceof EObject) {
                    EObject eo = (EObject) t;
                    if (eo.eResource() != null) {
                        targets.add(EcoreUtil.getURI((EObject) t).toString());
                    } else {
                        log.warn("Target hasn't got resource: " + t.toString());
                    }
                } else {
                    log.warn("Target is not EObject: " + t.toString());
                }
            }
            trace.eSet(targetUriAttributes, targets);
            traceEntries.add(trace);
        }
        return traceEntries;
    }

    /**
     * Create {@link ResourceSet} which can handle pseudo trace models. It created and register pseudo namespace.
     * @param nameSpace pseudo namespace URI postfix
     * @return the {@link ResourceSet} which can handle pseudo trace model
     */
    public static ResourceSet createTraceResourceSet(String nameSpace) {
        return createTraceResourceSet(nameSpace, null);
    }

    /**
     * Create {@link ResourceSet} which can handle pseudo trace models. It created and register pseudo namespace.
     * @param nameSpace pseudo namespace URI postfix
     * @param uriHandler {@link URIHandler} which helps resolve virtual {@link URI}
     * @return the {@link ResourceSet} which can handle pseudo trace model
     */
    @SuppressWarnings("WeakerAccess")
    public static ResourceSet createTraceResourceSet(String nameSpace, URIHandler uriHandler) {
        final EcorePackage ecore = EcorePackage.eINSTANCE;

        final EClass trace = newEClassBuilder()
                .withName(TRACE_CLASS)
                .withEStructuralFeatures(
                        newEAttributeBuilder()
                                .withName(SOURCE_URI)
                                .withEType(ecore.getEString()).build())
                .withEStructuralFeatures(
                        newEAttributeBuilder()
                                .withName(TARRGET_URIS)
                                .withUpperBound(ETypedElement.UNBOUNDED_MULTIPLICITY)
                                .withEType(ecore.getEString()).build())
                .build();

        final EPackage ePackage = newEPackageBuilder()
                .withName("trace")
                .withNsPrefix("trace")
                .withNsURI(HTTP_WWW_BLACKBELT_HU_META_TRASFORMATION_TRACE + nameSpace)
                .withEClassifiers(trace)
                .build();


        final ResourceSet resourceSet = new ResourceSetImpl();
        if (uriHandler != null) {
            resourceSet.getURIConverter().getURIHandlers().add(0, uriHandler);
        }
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new EcoreResourceFactoryImpl());
        final Resource resource = resourceSet.createResource(URI.createURI(TRACE_SELECTOR + nameSpace));
        resource.getContents().add(ePackage);

        // Register packages
        resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
        return resourceSet;
    }

    /**
     * Save the trace object map. It convert it to pseudo trace model.
     *
     * @param traceEObjectMap the source -> target trace object map
     * @param nameSpace the name of trace. (usually source 2 target)
     * @param modelUri the model {@link URI} of the trace.
     * @param uriHandler optional {@link URIHandler} which helps resolve virtual {@link URI}
     * @return the {@link Resource} which contain the translated trace {@link EObject} map
     */
    public static Resource createTraceModelResourceFromEObjectMap(Map<EObject, List<EObject>> traceEObjectMap,
                                                                  String nameSpace,
                                                                  URI modelUri,
                                                                  URIHandler uriHandler) {
        return createTraceModelResourceFromTraceList(
                getTransformationTraceFromTraceMap(nameSpace, traceEObjectMap), nameSpace, modelUri, uriHandler);
    }

    /**
     * Save trace list.
     *
     * @param trace list of trace:Trace entries
     * @param nameSpace the name of trace. (usually source 2 target)
     * @param modelUri the model {@link URI} of the trace.
     * @param uriHandler optional {@link URIHandler} which helps resolve virtual {@link URI}
     * @return the {@link Resource} which contain the trace entries
     */
    @SuppressWarnings("WeakerAccess")
    public static Resource createTraceModelResourceFromTraceList(List<EObject> trace,
                                                                 String nameSpace,
                                                                 URI modelUri,
                                                                 URIHandler uriHandler) {
        Resource traceResource = createTraceModelResource(nameSpace, modelUri, uriHandler);
        traceResource.getContents().addAll(trace);
        return traceResource;
    }

    /**
     * Create trace model {@link Resource}. It can contain pseudo trace model
     * @param nameSpace the name of trace. (usually source 2 target)
     * @param modelUri the model {@link URI} of the trace.
     * @param uriHandler optional {@link URIHandler} which helps resolve virtual {@link URI}
     * @return the empty {@link Resource} which used to add entries.
     */
    @SuppressWarnings("WeakerAccess")
    public static Resource createTraceModelResource(String nameSpace, URI modelUri,
                                                    URIHandler uriHandler) {
        return createTraceResourceSet(nameSpace, uriHandler).createResource(modelUri);
    }
}
