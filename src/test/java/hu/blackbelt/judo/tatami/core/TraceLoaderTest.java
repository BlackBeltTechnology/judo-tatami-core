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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EAttribute;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.eclipse.emf.ecore.util.builder.EcoreBuilders.*;
import static org.junit.jupiter.api.Assertions.*;

public class TraceLoaderTest {

    @TempDir
    File tempDir;

    private ResourceSet sourceResourceSet;
    private ResourceSet targetResourceSet;
    private EObject sourceObject1;
    private EObject sourceObject2;
    private EObject targetObject1;
    private EObject targetObject2;
    private EObject targetObject3;

    @BeforeEach
    void setUp() {
        // Create source model
        sourceResourceSet = createResourceSetWithObjects("source", "Source", "src1", "src2");
        Resource sourceResource = sourceResourceSet.getResources().get(1);
        sourceObject1 = sourceResource.getContents().get(0);
        sourceObject2 = sourceResource.getContents().get(1);

        // Create target model
        targetResourceSet = createResourceSetWithObjects("target", "Target", "tgt1", "tgt2", "tgt3");
        Resource targetResource = targetResourceSet.getResources().get(1);
        targetObject1 = targetResource.getContents().get(0);
        targetObject2 = targetResource.getContents().get(1);
        targetObject3 = targetResource.getContents().get(2);
    }

    @Test
    void testTraceFormatDetection() {
        assertEquals(TraceFormat.ZETA_JSON, TraceFormat.detect("trace.json"));
        assertEquals(TraceFormat.ZETA_JSON, TraceFormat.detect("trace.JSON"));
        assertEquals(TraceFormat.ETL_XMI, TraceFormat.detect("trace.xmi"));
        assertEquals(TraceFormat.ETL_XMI, TraceFormat.detect("trace.xml"));
        assertEquals(TraceFormat.ETL_XMI, TraceFormat.detect("trace"));
        assertEquals(TraceFormat.ETL_XMI, TraceFormat.detect(null));
    }

    @Test
    void testTraceEntryBuilder() {
        TraceEntry entry = TraceEntry.builder()
                .source(sourceObject1)
                .target(targetObject1)
                .target(targetObject2)
                .ruleName("TestRule")
                .discriminator("disc1")
                .primary(true)
                .build();

        assertEquals(1, entry.getSources().size());
        assertEquals(sourceObject1, entry.getSource());
        assertEquals(2, entry.getTargets().size());
        assertEquals(targetObject1, entry.getTarget());
        assertEquals("TestRule", entry.getRuleName());
        assertEquals("disc1", entry.getDiscriminator());
        assertTrue(entry.isPrimary());
    }

    @Test
    void testTraceEntryMultipleSources() {
        TraceEntry entry = TraceEntry.builder()
                .source(sourceObject1)
                .source(sourceObject2)
                .target(targetObject1)
                .build();

        assertEquals(2, entry.getSources().size());
        assertTrue(entry.getSources().contains(sourceObject1));
        assertTrue(entry.getSources().contains(sourceObject2));
        assertEquals(sourceObject1, entry.getSource()); // First source
    }

    @Test
    void testZetaTraceLoaderSaveAndLoad() throws Exception {
        // Create entries
        List<TraceEntry> entries = Arrays.asList(
                TraceEntry.builder()
                        .source(sourceObject1)
                        .target(targetObject1)
                        .target(targetObject2)
                        .ruleName("Rule1")
                        .build(),
                TraceEntry.builder()
                        .source(sourceObject2)
                        .target(targetObject3)
                        .ruleName("Rule2")
                        .discriminator("type1")
                        .build()
        );

        // Save
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ZetaTraceLoader loader = new ZetaTraceLoader();
        loader.saveTrace(entries, output);

        String json = output.toString(StandardCharsets.UTF_8);
        assertTrue(json.contains("Rule1"));
        assertTrue(json.contains("Rule2"));
        assertTrue(json.contains("type1"));

        // Load
        ByteArrayInputStream input = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        List<TraceEntry> loaded = loader.loadTrace(input, Arrays.asList(sourceResourceSet, targetResourceSet));

        assertEquals(2, loaded.size());
    }

    @Test
    void testZetaTraceLoaderMultiSource() throws Exception {
        // Create multi-source entry
        List<TraceEntry> entries = Arrays.asList(
                TraceEntry.builder()
                        .source(sourceObject1)
                        .source(sourceObject2)
                        .target(targetObject1)
                        .ruleName("MergeRule")
                        .build()
        );

        // Save
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        ZetaTraceLoader loader = new ZetaTraceLoader();
        loader.saveTrace(entries, output);

        String json = output.toString(StandardCharsets.UTF_8);
        assertTrue(json.contains("MergeRule"));

        // Load - note: the save format expands multi-source entries,
        // so we get separate entries (one per source-target combination)
        ByteArrayInputStream input = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        List<TraceEntry> loaded = loader.loadTrace(input, Arrays.asList(sourceResourceSet, targetResourceSet));

        // Each source creates a separate entry when saved and loaded
        assertEquals(2, loaded.size());
        // Each entry has 1 source and 1 target
        assertEquals(1, loaded.get(0).getSources().size());
        assertEquals(1, loaded.get(0).getTargets().size());
    }

    @Test
    void testEtlTraceLoaderSaveAndLoad() throws Exception {
        // Create entries
        List<TraceEntry> entries = Arrays.asList(
                TraceEntry.builder()
                        .source(sourceObject1)
                        .target(targetObject1)
                        .target(targetObject2)
                        .build(),
                TraceEntry.builder()
                        .source(sourceObject2)
                        .target(targetObject3)
                        .build()
        );

        // Save
        ByteArrayOutputStream output = new ByteArrayOutputStream();
        EtlTraceLoader loader = new EtlTraceLoader("test");
        loader.saveTrace(entries, output);

        String xmi = output.toString(StandardCharsets.UTF_8);
        assertTrue(xmi.contains("xmi:XMI"));
        assertTrue(xmi.contains("Trace"));
        assertTrue(xmi.contains("sourceUri"));
        assertTrue(xmi.contains("targetUri"));

        // Load
        ByteArrayInputStream input = new ByteArrayInputStream(xmi.getBytes(StandardCharsets.UTF_8));
        List<TraceEntry> loaded = loader.loadTrace(input, Arrays.asList(sourceResourceSet, targetResourceSet));

        assertEquals(2, loaded.size());
    }

    @Test
    void testTransformationTraceLoaderUnifiedApi() throws Exception {
        // Create entries
        List<TraceEntry> entries = Arrays.asList(
                TraceEntry.builder()
                        .source(sourceObject1)
                        .target(targetObject1)
                        .build()
        );

        // Test JSON save/load
        File jsonFile = new File(tempDir, "trace.json");
        TransformationTraceLoader.saveTrace(entries, jsonFile);
        assertTrue(jsonFile.exists());

        List<TraceEntry> loadedJson = TransformationTraceLoader.loadTrace(
                jsonFile, Arrays.asList(sourceResourceSet, targetResourceSet));
        assertEquals(1, loadedJson.size());

        // Test XMI save/load
        File xmiFile = new File(tempDir, "trace.xmi");
        TransformationTraceLoader.saveTrace(entries, xmiFile, TraceFormat.ETL_XMI);
        assertTrue(xmiFile.exists());

        List<TraceEntry> loadedXmi = TransformationTraceLoader.loadTrace(
                xmiFile, Arrays.asList(sourceResourceSet, targetResourceSet));
        assertEquals(1, loadedXmi.size());
    }

    @Test
    void testLegacyFormatConversion() {
        // Create entries
        List<TraceEntry> entries = Arrays.asList(
                TraceEntry.builder()
                        .source(sourceObject1)
                        .target(targetObject1)
                        .target(targetObject2)
                        .build(),
                TraceEntry.builder()
                        .source(sourceObject2)
                        .target(targetObject3)
                        .build()
        );

        // Convert to legacy format
        Map<EObject, List<EObject>> legacy = TransformationTraceLoader.toLegacyFormat(entries);

        assertEquals(2, legacy.size());
        assertTrue(legacy.containsKey(sourceObject1));
        assertTrue(legacy.containsKey(sourceObject2));
        assertEquals(2, legacy.get(sourceObject1).size());
        assertEquals(1, legacy.get(sourceObject2).size());

        // Convert back from legacy format
        List<TraceEntry> converted = TransformationTraceLoader.fromLegacyFormat(legacy);
        assertEquals(2, converted.size());
    }

    @Test
    void testMultiSourceFormatConversion() {
        // Create multi-source entries
        List<TraceEntry> entries = Arrays.asList(
                TraceEntry.builder()
                        .source(sourceObject1)
                        .source(sourceObject2)
                        .target(targetObject1)
                        .build()
        );

        // Convert to multi-source format
        Map<List<EObject>, List<EObject>> multiSource = TransformationTraceLoader.toMultiSourceFormat(entries);

        assertEquals(1, multiSource.size());
        List<EObject> sources = multiSource.keySet().iterator().next();
        assertEquals(2, sources.size());
        assertTrue(sources.contains(sourceObject1));
        assertTrue(sources.contains(sourceObject2));
    }

    @Test
    void testZetaTraceLoaderUnresolvedReference() {
        String json = """
                {
                    "traceEntries": [
                        {
                            "ruleName": "TestRule",
                            "source": { "type": "Source", "id": "nonexistent_id" },
                            "target": { "type": "Target", "id": "tgt1" }
                        }
                    ]
                }
                """;

        ByteArrayInputStream input = new ByteArrayInputStream(json.getBytes(StandardCharsets.UTF_8));
        ZetaTraceLoader loader = new ZetaTraceLoader();

        assertThrows(TraceLoadException.class, () ->
                loader.loadTrace(input, Arrays.asList(sourceResourceSet, targetResourceSet)));
    }

    @Test
    void testEtlTraceLoaderUnresolvedReference() {
        String xmi = """
                <?xml version="1.0" encoding="UTF-8"?>
                <xmi:XMI xmi:version="2.0" xmlns:xmi="http://www.omg.org/XMI"
                    xmlns:trace="http:///www.blackbelt.hu/meta/trasformation/trace/test">
                  <trace:Trace sourceUri="nonexistent:model#_invalid">
                    <targetUri>target:model#_tgt1</targetUri>
                  </trace:Trace>
                </xmi:XMI>
                """;

        ByteArrayInputStream input = new ByteArrayInputStream(xmi.getBytes(StandardCharsets.UTF_8));
        EtlTraceLoader loader = new EtlTraceLoader("test");

        assertThrows(TraceLoadException.class, () ->
                loader.loadTrace(input, Arrays.asList(sourceResourceSet, targetResourceSet)));
    }

    @Test
    void testTraceEntryEmptySourcesAndTargets() {
        TraceEntry entry = TraceEntry.builder().build();

        assertNull(entry.getSource());
        assertNull(entry.getTarget());
        assertTrue(entry.getSources().isEmpty());
        assertTrue(entry.getTargets().isEmpty());
    }

    private ResourceSet createResourceSetWithObjects(String name, String className, String... objectIds) {
        final EcorePackage ecore = EcorePackage.eINSTANCE;

        EAttribute idAttr = newEAttributeBuilder()
                .withName("id")
                .withEType(ecore.getEString())
                .build();

        final EClass clazz = newEClassBuilder()
                .withName(className)
                .withEStructuralFeatures(idAttr)
                .build();

        final EPackage ePackage = newEPackageBuilder()
                .withName(name)
                .withNsPrefix(name)
                .withNsURI("http://test/" + name)
                .withEClassifiers(clazz)
                .build();

        final ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new EcoreResourceFactoryImpl());

        // Package resource
        final Resource packageResource = resourceSet.createResource(URI.createURI("http://test/" + name + ".ecore"));
        packageResource.getContents().add(ePackage);
        resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);

        // Data resource
        final Resource dataResource = resourceSet.createResource(URI.createURI(name + ":model"));

        for (String id : objectIds) {
            EObject obj = ePackage.getEFactoryInstance().create(clazz);
            obj.eSet(idAttr, id);
            dataResource.getContents().add(obj);
        }

        return resourceSet;
    }
}
