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

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import org.eclipse.emf.common.util.ECollections;
import org.eclipse.emf.common.util.URI;
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

import java.util.Arrays;
import java.util.List;
import java.util.Map;

import static org.eclipse.emf.ecore.util.builder.EcoreBuilders.*;
import static org.junit.jupiter.api.Assertions.*;

public class MultiSourceTraceServiceTest {

    public static final String HTTP_TEST = "http://test/";
    public static final String MODEL_NAME = "multiSourceTest";

    private TransformationTraceServiceImpl traceService;
    private ModelHolder rootHolder;
    private ModelHolder source1Holder;
    private ModelHolder source2Holder;
    private ModelHolder mergedHolder;

    @BeforeEach
    void setUp() {
        traceService = new TransformationTraceServiceImpl();

        // Create test models
        rootHolder = createTestClassesAndInstances("root", "Root", 1, 2);
        source1Holder = createTestClassesAndInstances("source1", "Source1", 1, 2);
        source2Holder = createTestClassesAndInstances("source2", "Source2", 1, 2);
        mergedHolder = createTestClassesAndInstances("merged", "Merged", 1, 2);
    }

    @Test
    void testGetAscendantsOfInstanceByModelType() {
        // Setup: root -> source1, root -> source2, [source1, source2] -> merged
        RootModel rootModel = new RootModel();
        Level1Model1 source1Model = new Level1Model1();
        Level1Model2 source2Model = new Level1Model2();
        Level2Model1 mergedModel = new Level2Model1();

        // Create traces
        TransformationTraceTest rootToSource1 = TransformationTraceTest.builder()
                .modelName(MODEL_NAME)
                .name("rootToSource1")
                .source(ImmutableList.of(rootModel))
                .sourceResourceSet(ImmutableMap.of(rootModel, rootHolder.resourceSet))
                .sourceURIS(ImmutableMap.of(rootModel, rootHolder.uri))
                .target(source1Model)
                .targetResourceSet(source1Holder.resourceSet)
                .targetURI(source1Holder.uri)
                .trace(ImmutableMap.of(rootHolder.getFirstObject(), ImmutableList.of(source1Holder.getFirstObject())))
                .build();

        TransformationTraceTest rootToSource2 = TransformationTraceTest.builder()
                .modelName(MODEL_NAME)
                .name("rootToSource2")
                .source(ImmutableList.of(rootModel))
                .sourceResourceSet(ImmutableMap.of(rootModel, rootHolder.resourceSet))
                .sourceURIS(ImmutableMap.of(rootModel, rootHolder.uri))
                .target(source2Model)
                .targetResourceSet(source2Holder.resourceSet)
                .targetURI(source2Holder.uri)
                .trace(ImmutableMap.of(rootHolder.getFirstObject(), ImmutableList.of(source2Holder.getFirstObject())))
                .build();

        // Multi-source trace: both source1 and source2 contribute to merged
        TransformationTraceTest sourcesToMerged = TransformationTraceTest.builder()
                .modelName(MODEL_NAME)
                .name("sourcesToMerged")
                .source(ImmutableList.of(source1Model, source2Model))
                .sourceResourceSet(ImmutableMap.of(
                        source1Model, source1Holder.resourceSet,
                        source2Model, source2Holder.resourceSet))
                .sourceURIS(ImmutableMap.of(
                        source1Model, source1Holder.uri,
                        source2Model, source2Holder.uri))
                .target(mergedModel)
                .targetResourceSet(mergedHolder.resourceSet)
                .targetURI(mergedHolder.uri)
                .trace(ImmutableMap.of(
                        source1Holder.getFirstObject(), ImmutableList.of(mergedHolder.getFirstObject()),
                        source2Holder.getFirstObject(), ImmutableList.of(mergedHolder.getObjectByIndex(2))))
                .build();

        traceService.add(rootToSource1);
        traceService.add(rootToSource2);
        traceService.add(sourcesToMerged);

        // Test: get ascendants by model type
        List<EObject> rootAscendants = traceService.getAscendantsOfInstanceByModelType(
                MODEL_NAME, RootModel.class, mergedHolder.getFirstObject());

        assertNotNull(rootAscendants);
        assertTrue(rootAscendants.contains(rootHolder.getFirstObject()));
    }

    @Test
    void testGetRootAscendantsOfInstance() {
        // Setup: root -> source1, root -> source2, [source1, source2] -> merged
        RootModel rootModel = new RootModel();
        Level1Model1 source1Model = new Level1Model1();
        Level2Model1 mergedModel = new Level2Model1();

        TransformationTraceTest rootToSource1 = TransformationTraceTest.builder()
                .modelName(MODEL_NAME)
                .name("rootToSource1")
                .source(ImmutableList.of(rootModel))
                .sourceResourceSet(ImmutableMap.of(rootModel, rootHolder.resourceSet))
                .sourceURIS(ImmutableMap.of(rootModel, rootHolder.uri))
                .target(source1Model)
                .targetResourceSet(source1Holder.resourceSet)
                .targetURI(source1Holder.uri)
                .trace(ImmutableMap.of(rootHolder.getFirstObject(), ImmutableList.of(source1Holder.getFirstObject())))
                .build();

        TransformationTraceTest source1ToMerged = TransformationTraceTest.builder()
                .modelName(MODEL_NAME)
                .name("source1ToMerged")
                .source(ImmutableList.of(source1Model))
                .sourceResourceSet(ImmutableMap.of(source1Model, source1Holder.resourceSet))
                .sourceURIS(ImmutableMap.of(source1Model, source1Holder.uri))
                .target(mergedModel)
                .targetResourceSet(mergedHolder.resourceSet)
                .targetURI(mergedHolder.uri)
                .trace(ImmutableMap.of(source1Holder.getFirstObject(), ImmutableList.of(mergedHolder.getFirstObject())))
                .build();

        traceService.add(rootToSource1);
        traceService.add(source1ToMerged);

        // Test: get all root ascendants
        List<EObject> roots = traceService.getRootAscendantsOfInstance(MODEL_NAME, mergedHolder.getFirstObject());

        assertNotNull(roots);
        assertEquals(1, roots.size());
        assertTrue(roots.contains(rootHolder.getFirstObject()));
    }

    @Test
    void testGetAllAscendantsOfInstanceMultiSource() {
        RootModel rootModel = new RootModel();
        Level1Model1 source1Model = new Level1Model1();
        Level2Model1 mergedModel = new Level2Model1();

        TransformationTraceTest rootToSource1 = TransformationTraceTest.builder()
                .modelName(MODEL_NAME)
                .name("rootToSource1")
                .source(ImmutableList.of(rootModel))
                .sourceResourceSet(ImmutableMap.of(rootModel, rootHolder.resourceSet))
                .sourceURIS(ImmutableMap.of(rootModel, rootHolder.uri))
                .target(source1Model)
                .targetResourceSet(source1Holder.resourceSet)
                .targetURI(source1Holder.uri)
                .trace(ImmutableMap.of(rootHolder.getFirstObject(), ImmutableList.of(source1Holder.getFirstObject())))
                .build();

        TransformationTraceTest source1ToMerged = TransformationTraceTest.builder()
                .modelName(MODEL_NAME)
                .name("source1ToMerged")
                .source(ImmutableList.of(source1Model))
                .sourceResourceSet(ImmutableMap.of(source1Model, source1Holder.resourceSet))
                .sourceURIS(ImmutableMap.of(source1Model, source1Holder.uri))
                .target(mergedModel)
                .targetResourceSet(mergedHolder.resourceSet)
                .targetURI(mergedHolder.uri)
                .trace(ImmutableMap.of(source1Holder.getFirstObject(), ImmutableList.of(mergedHolder.getFirstObject())))
                .build();

        traceService.add(rootToSource1);
        traceService.add(source1ToMerged);

        // Test: get all ascendants with multi-source support
        Map<TransformationTrace, List<EObject>> allAscendants =
                traceService.getAllAscendantsOfInstanceMultiSource(MODEL_NAME, mergedHolder.getFirstObject());

        assertNotNull(allAscendants);
        assertEquals(2, allAscendants.size());
        assertTrue(allAscendants.containsKey(source1ToMerged));
        assertTrue(allAscendants.containsKey(rootToSource1));
    }

    @Test
    void testGetDescendantsOfInstancesByModelType() {
        RootModel rootModel = new RootModel();
        Level1Model1 targetModel = new Level1Model1();

        TransformationTraceTest rootToTarget = TransformationTraceTest.builder()
                .modelName(MODEL_NAME)
                .name("rootToTarget")
                .source(ImmutableList.of(rootModel))
                .sourceResourceSet(ImmutableMap.of(rootModel, rootHolder.resourceSet))
                .sourceURIS(ImmutableMap.of(rootModel, rootHolder.uri))
                .target(targetModel)
                .targetResourceSet(source1Holder.resourceSet)
                .targetURI(source1Holder.uri)
                .trace(ImmutableMap.of(
                        rootHolder.getFirstObject(), ImmutableList.of(source1Holder.getFirstObject()),
                        rootHolder.getObjectByIndex(2), ImmutableList.of(source1Holder.getObjectByIndex(2))))
                .build();

        traceService.add(rootToTarget);

        // Test: get descendants from multiple sources
        List<EObject> descendants = traceService.getDescendantsOfInstancesByModelType(
                MODEL_NAME, Level1Model1.class,
                rootHolder.getFirstObject(), rootHolder.getObjectByIndex(2));

        assertNotNull(descendants);
        assertEquals(2, descendants.size());
        assertTrue(descendants.contains(source1Holder.getFirstObject()));
        assertTrue(descendants.contains(source1Holder.getObjectByIndex(2)));
    }

    @Test
    void testGetAllDescendantsOfInstancesMultiSource() {
        RootModel rootModel = new RootModel();
        Level1Model1 targetModel = new Level1Model1();

        TransformationTraceTest rootToTarget = TransformationTraceTest.builder()
                .modelName(MODEL_NAME)
                .name("rootToTarget")
                .source(ImmutableList.of(rootModel))
                .sourceResourceSet(ImmutableMap.of(rootModel, rootHolder.resourceSet))
                .sourceURIS(ImmutableMap.of(rootModel, rootHolder.uri))
                .target(targetModel)
                .targetResourceSet(source1Holder.resourceSet)
                .targetURI(source1Holder.uri)
                .trace(ImmutableMap.of(
                        rootHolder.getFirstObject(), ImmutableList.of(source1Holder.getFirstObject()),
                        rootHolder.getObjectByIndex(2), ImmutableList.of(source1Holder.getObjectByIndex(2))))
                .build();

        traceService.add(rootToTarget);

        // Test: get all descendants from multiple sources
        Map<TransformationTrace, List<EObject>> allDescendants =
                traceService.getAllDescendantsOfInstancesMultiSource(
                        MODEL_NAME, rootHolder.getFirstObject(), rootHolder.getObjectByIndex(2));

        assertNotNull(allDescendants);
        assertEquals(1, allDescendants.size());
        assertTrue(allDescendants.containsKey(rootToTarget));
        assertEquals(2, allDescendants.get(rootToTarget).size());
    }

    @Test
    void testGetTraceEntriesForInstance() {
        RootModel rootModel = new RootModel();
        Level1Model1 targetModel = new Level1Model1();

        TransformationTraceTest rootToTarget = TransformationTraceTest.builder()
                .modelName(MODEL_NAME)
                .name("rootToTarget")
                .source(ImmutableList.of(rootModel))
                .sourceResourceSet(ImmutableMap.of(rootModel, rootHolder.resourceSet))
                .sourceURIS(ImmutableMap.of(rootModel, rootHolder.uri))
                .target(targetModel)
                .targetResourceSet(source1Holder.resourceSet)
                .targetURI(source1Holder.uri)
                .trace(ImmutableMap.of(rootHolder.getFirstObject(), ImmutableList.of(source1Holder.getFirstObject())))
                .build();

        traceService.add(rootToTarget);

        // Test: get trace entries for source instance
        List<TraceEntry> entriesForSource = traceService.getTraceEntriesForInstance(
                MODEL_NAME, rootHolder.getFirstObject());
        assertNotNull(entriesForSource);
        assertEquals(1, entriesForSource.size());

        // Test: get trace entries for target instance
        List<TraceEntry> entriesForTarget = traceService.getTraceEntriesForInstance(
                MODEL_NAME, source1Holder.getFirstObject());
        assertNotNull(entriesForTarget);
        assertEquals(1, entriesForTarget.size());

        // Test: no entries for unrelated instance
        List<TraceEntry> noEntries = traceService.getTraceEntriesForInstance(
                MODEL_NAME, rootHolder.getObjectByIndex(2));
        assertNotNull(noEntries);
        assertEquals(0, noEntries.size());
    }

    @Test
    void testUndefinedModelThrowsException() {
        assertThrows(IllegalArgumentException.class, () ->
                traceService.getAscendantsOfInstanceByModelType("nonexistent", RootModel.class, rootHolder.getFirstObject()));

        assertThrows(IllegalArgumentException.class, () ->
                traceService.getRootAscendantsOfInstance("nonexistent", rootHolder.getFirstObject()));

        assertThrows(IllegalArgumentException.class, () ->
                traceService.getAllAscendantsOfInstanceMultiSource("nonexistent", rootHolder.getFirstObject()));

        assertThrows(IllegalArgumentException.class, () ->
                traceService.getDescendantsOfInstancesByModelType("nonexistent", RootModel.class, rootHolder.getFirstObject()));

        assertThrows(IllegalArgumentException.class, () ->
                traceService.getAllDescendantsOfInstancesMultiSource("nonexistent", rootHolder.getFirstObject()));

        assertThrows(IllegalArgumentException.class, () ->
                traceService.getTraceEntriesForInstance("nonexistent", rootHolder.getFirstObject()));
    }

    private static class ModelHolder {
        final ResourceSet resourceSet;
        final Resource dataResource;
        final List<EObject> objects;
        final URI uri;

        ModelHolder(ResourceSet resourceSet, Resource dataResource, List<EObject> objects, URI uri) {
            this.resourceSet = resourceSet;
            this.dataResource = dataResource;
            this.objects = objects;
            this.uri = uri;
        }

        EObject getFirstObject() {
            return objects.get(0);
        }

        EObject getObjectByIndex(int i) {
            return objects.get(i - 1);
        }
    }

    private ModelHolder createTestClassesAndInstances(String modelTypeName, String modelTypePrefix,
                                                       int classNumber, int instanceNumByClass) {
        final EcorePackage ecore = EcorePackage.eINSTANCE;

        final EPackage ePackage = newEPackageBuilder()
                .withName(modelTypeName)
                .withNsPrefix(modelTypeName)
                .withNsURI(HTTP_TEST + modelTypeName)
                .build();

        final ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new EcoreResourceFactoryImpl());
        final Resource resource = resourceSet.createResource(URI.createURI(HTTP_TEST + modelTypeName));
        resource.getContents().add(ePackage);
        resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);

        URI uri = URI.createURI("uri:" + modelTypeName);
        Resource dataResource = resourceSet.createResource(uri);

        List<EObject> instances = ECollections.newBasicEList();

        int cnt = 1;
        for (int j = 1; j <= classNumber; j++) {
            final EClass clazz = newEClassBuilder()
                    .withName(modelTypePrefix + "C" + j)
                    .withEStructuralFeatures(
                            newEAttributeBuilder()
                                    .withName("name")
                                    .withEType(ecore.getEString()).build())
                    .build();

            ePackage.getEClassifiers().add(clazz);
            for (int i = 1; i <= instanceNumByClass; i++) {
                EObject m = ePackage.getEFactoryInstance().create(clazz);
                m.eSet(clazz.getEStructuralFeature("name"), modelTypePrefix + "O" + cnt);
                dataResource.getContents().add(m);
                instances.add(m);
                cnt++;
            }
        }

        return new ModelHolder(resourceSet, dataResource, instances, uri);
    }
}
