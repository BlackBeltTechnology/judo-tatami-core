package hu.blackbelt.judo.tatami.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import lombok.Getter;
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
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
import org.junit.Test;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import static org.eclipse.emf.ecore.util.builder.EcoreBuilders.*;
import static org.junit.Assert.assertEquals;

public class TransformationTraceServiceImplTest {


    public static final String HTTP_TEST = "http://test/";
    public static final String TEST_1 = "test1";

    TransformationTraceServiceImpl transformationTraceService;

    @Test
    public void testBuildGraph() {
        transformationTraceService = new TransformationTraceServiceImpl();


        RootModel rootModel = new RootModel();
        ModelHolder rootModelHolder = createTestClassesAndInstances("root", "Root", 1, 1);

        Level1Model1 level1Model1 = new Level1Model1();
        ModelHolder level1Model1Holder = createTestClassesAndInstances("level1model1", "Level1Model1", 1, 1);

        Level1Model2 level1Model2 = new Level1Model2();
        ModelHolder level1Model2Holder = createTestClassesAndInstances("level1model2", "Level1Model2", 1, 1);

        Level1Model3 level1Model3 = new Level1Model3();
        ModelHolder level1Model3Holder = createTestClassesAndInstances("level1model3", "Level1Model3", 1, 1);

        Level2Model1 level2Model1 = new Level2Model1();
        ModelHolder level2Model1Holder = createTestClassesAndInstances("level2model1", "Level2Model1", 1, 1);

        Level2Model2 level2Model2 = new Level2Model2();
        ModelHolder level2Model2Holder = createTestClassesAndInstances("level2model2", "Level2Model2", 1, 1);

        Level3Model1 level3Model1 = new Level3Model1();
        ModelHolder level3Model1Holder = createTestClassesAndInstances("level3model1", "Level3Model1", 1, 2);

        //Map<EObject, List<EObject>> root_to_level1model1_map = ImmutableMap.of(rootModelO1, ImmutableList.of(level1Model1O1));
        TransformationTraceTest root_to_level1model1 = TransformationTraceTest.builder()
                .modelName(TEST_1)
                .name("root_to_level1model1")
                .source(ImmutableList.of(rootModel))
                .sourceResourceSet(ImmutableMap.of(rootModel, rootModelHolder.getResourceSet()))
                .sourceURIS(ImmutableMap.of(rootModel, rootModelHolder.getUri()))
                .target(level1Model1)
                .targetResourceSet(level1Model1Holder.getResourceSet())
                .targetURI(level1Model1Holder.getUri())
                .trace(ImmutableMap.of(rootModelHolder.getFirstObject(), ImmutableList.of(level1Model1Holder.getFirstObject())))
                .build();

        TransformationTraceTest root_to_level1model2 = TransformationTraceTest.builder()
                .modelName(TEST_1)
                .name("root_to_level1model2")
                .source(ImmutableList.of(rootModel))
                .sourceResourceSet(ImmutableMap.of(rootModel, rootModelHolder.getResourceSet()))
                .sourceURIS(ImmutableMap.of(rootModel, rootModelHolder.getUri()))
                .target(level1Model2)
                .targetResourceSet(level1Model2Holder.getResourceSet())
                .targetURI(level1Model2Holder.getUri())
                .trace(ImmutableMap.of(rootModelHolder.getFirstObject(), ImmutableList.of(level1Model2Holder.getFirstObject())))
                .build();

        TransformationTraceTest root_to_level1model3 = TransformationTraceTest.builder()
                .modelName(TEST_1)
                .name("root_to_level1model3")
                .source(ImmutableList.of(rootModel))
                .sourceResourceSet(ImmutableMap.of(rootModel, rootModelHolder.getResourceSet()))
                .sourceURIS(ImmutableMap.of(rootModel, rootModelHolder.getUri()))
                .target(level1Model3)
                .targetResourceSet(level1Model3Holder.getResourceSet())
                .targetURI(level1Model3Holder.getUri())
                .trace(ImmutableMap.of(rootModelHolder.getFirstObject(), ImmutableList.of(level1Model3Holder.getFirstObject())))
                .build();

        TransformationTraceTest root_to_level2model1 = TransformationTraceTest.builder()
                .modelName(TEST_1)
                .name("root_to_level2model1")
                .source(ImmutableList.of(rootModel))
                .sourceResourceSet(ImmutableMap.of(rootModel, rootModelHolder.getResourceSet()))
                .sourceURIS(ImmutableMap.of(rootModel, rootModelHolder.getUri()))
                .target(level2Model1)
                .targetResourceSet(level2Model1Holder.getResourceSet())
                .targetURI(level2Model1Holder.getUri())
                .trace(ImmutableMap.of(rootModelHolder.getFirstObject(), ImmutableList.of(level2Model1Holder.getFirstObject())))
                .build();

        TransformationTraceTest level1_to_level2model2 = TransformationTraceTest.builder()
                .modelName(TEST_1)
                .name("level1_to_level2model2")
                .source(ImmutableList.of(level1Model1, level1Model2))
                .sourceResourceSet(ImmutableMap.of(level1Model1, level1Model1Holder.getResourceSet(), level1Model2, level1Model2Holder.getResourceSet()))
                .sourceURIS(ImmutableMap.of(level1Model1, level1Model1Holder.getUri(), level1Model2, level1Model2Holder.getUri()))
                .target(level2Model2)
                .targetResourceSet(level2Model2Holder.getResourceSet())
                .targetURI(level2Model2Holder.getUri())
                .trace(ImmutableMap.of(level1Model2Holder.getFirstObject(), ImmutableList.of(level2Model2Holder.getFirstObject())))
                .build();

        TransformationTraceTest level2_to_level3model1 = TransformationTraceTest.builder()
                .modelName(TEST_1)
                .name("level2_to_level3model1")
                .source(ImmutableList.of(level2Model1, level2Model2))
                .sourceResourceSet(ImmutableMap.of(level2Model1, level2Model1Holder.getResourceSet(), level2Model2, level2Model2Holder.getResourceSet()))
                .sourceURIS(ImmutableMap.of(level2Model1, level2Model1Holder.getUri(), level2Model2, level2Model2Holder.getUri()))
                .target(level3Model1)
                .targetResourceSet(level3Model1Holder.getResourceSet())
                .targetURI(level3Model1Holder.getUri())
                .trace(ImmutableMap.of(
                        level2Model2Holder.getFirstObject(), ImmutableList.of(level3Model1Holder.getObjectByIndex(1)),
                        level2Model1Holder.getFirstObject(), ImmutableList.of(level3Model1Holder.getObjectByIndex(2))
                ))
                .build();



        transformationTraceService.add(root_to_level1model1);
        transformationTraceService.add(root_to_level1model2);
        transformationTraceService.add(root_to_level1model3);
        transformationTraceService.add(root_to_level2model1);
        transformationTraceService.add(level1_to_level2model2);
        transformationTraceService.add(level2_to_level3model1);

        assertEquals(rootModelHolder.getFirstObject(), transformationTraceService.getRootAscendantOfInstance(TEST_1, level3Model1Holder.getFirstObject()));
        assertEquals(null, transformationTraceService.getAscendantOfInstanceByModelType(TEST_1, Level1Model1.class, level3Model1Holder.getFirstObject()));
        assertEquals(level1Model2Holder.getFirstObject(), transformationTraceService.getAscendantOfInstanceByModelType(TEST_1, Level1Model2.class, level3Model1Holder.getObjectByIndex(1)));
        assertEquals(level2Model1Holder.getFirstObject(), transformationTraceService.getAscendantOfInstanceByModelType(TEST_1, Level2Model1.class, level3Model1Holder.getObjectByIndex(2)));
        assertEquals(
                ImmutableList.of(level2_to_level3model1, level1_to_level2model2, root_to_level1model2),
                transformationTraceService.getTransformationTraceAscendantsByInstance(TEST_1, level3Model1Holder.getObjectByIndex(1))
        );

        assertEquals(ImmutableMap.builder()
                        .put(root_to_level1model1, ImmutableList.of(level1Model1Holder.getFirstObject()))
                        .put(root_to_level1model2, ImmutableList.of(level1Model2Holder.getFirstObject()))
                        .put(root_to_level1model3, ImmutableList.of(level1Model3Holder.getFirstObject()))
                        .put(root_to_level2model1, ImmutableList.of(level2Model1Holder.getFirstObject()))
                        .put(level1_to_level2model2, ImmutableList.of(level2Model2Holder.getFirstObject()))
                        .put(level2_to_level3model1, ImmutableList.of(level3Model1Holder.getObjectByIndex(2), level3Model1Holder.getObjectByIndex(1)))
                        .build(),
                transformationTraceService.getAllDescendantOfInstance(TEST_1, rootModelHolder.getFirstObject())
        );


        assertEquals(ImmutableList.of(level3Model1Holder.getObjectByIndex(2), level3Model1Holder.getObjectByIndex(1)), transformationTraceService.getDescendantOfInstanceByModelType(TEST_1, Level3Model1.class, rootModelHolder.getFirstObject()));
    }


    public ResourceSet createTestResourceSet(String modelTypeName, String... className) {
        final EcorePackage ecore = EcorePackage.eINSTANCE;

        final EPackage ePackage = newEPackageBuilder()
                .withName(modelTypeName)
                .withNsPrefix(modelTypeName)
                .withEClassifiers(Arrays.stream(className).map(c -> newEClassBuilder().withName(c).build()).collect(Collectors.toList()))
                .withNsURI(HTTP_TEST + modelTypeName)
                .build();



        final ResourceSet resourceSet = new ResourceSetImpl();
        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new EcoreResourceFactoryImpl());
        final Resource resource = resourceSet.createResource(URI.createURI(HTTP_TEST + modelTypeName));
        resource.getContents().add(ePackage);

        // Register packages
        resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);
        return resourceSet;
    }


    @RequiredArgsConstructor
    @Getter
    public class ModelHolder {
        @NonNull
        ResourceSet resourceSet;
        @NonNull
        Resource packageResource;
        @NonNull
        Resource dataResource;
        @NonNull
        EPackage epackage;
        @NonNull
        List<EClass> classes;
        @NonNull
        List<EObject> objects;
        @NonNull
        URI uri;

        public EObject getObjectByIndex(int i) {
            return objects.get(i - 1);
        }

        public EObject getFirstObject() {
            return objects.get(0);
        }
    }

    public ModelHolder createTestClassesAndInstances(String modelTypeName, String modelTypePrefix, int classNumber,  int instanceNumByClass) {
        final EcorePackage ecore = EcorePackage.eINSTANCE;

        final EPackage ePackage = newEPackageBuilder()
                .withName(modelTypeName)
                .withNsPrefix(modelTypeName)
                ///.withEClassifiers(Arrays.stream(className).map(c -> newEClassBuilder().withName(c).build()).collect(Collectors.toList()))
                .withNsURI(HTTP_TEST + modelTypeName)
                .build();

        final ResourceSet resourceSet = new ResourceSetImpl();

        resourceSet.getResourceFactoryRegistry().getExtensionToFactoryMap().put("*", new EcoreResourceFactoryImpl());
        final Resource resource = resourceSet.createResource(URI.createURI(HTTP_TEST + modelTypeName));

        resource.getContents().add(ePackage);

        // Register packages
        resourceSet.getPackageRegistry().put(ePackage.getNsURI(), ePackage);

        URI uri = URI.createURI("uri:" + modelTypeName);
        Resource dataResource = resourceSet.createResource(uri);


        //EClassImpl rootModelC1 = (EClassImpl) ePackage.getEClassifier(modelTypePrefix + "C1");
        //rootModelC1.getEAllAttributes().add(newEClassBuilder().withName("name").withT)

        List<EClass> classes = ECollections.newBasicEList();
        List<EObject> instances = ECollections.newBasicEList();

        int cnt = 1;
        for (int j=1; j<=classNumber; j++) {
            final EClass clazz = newEClassBuilder()
                    .withName(modelTypePrefix + "C" + Integer.toString(j))
                    .withEStructuralFeatures(
                            newEAttributeBuilder()
                                    .withName("name")
                                    .withEType(ecore.getEString()))
                    .build();

            classes.add(clazz);
            ePackage.getEClassifiers().add(clazz);
            for (int i = 1; i <= instanceNumByClass; i++) {
                EObject m = ePackage.getEFactoryInstance().create(clazz);
                m.eSet(clazz.getEStructuralFeature("name"), modelTypePrefix + "O" + Integer.toString(cnt));
                dataResource.getContents().add(m);
                instances.add(m);
                cnt++;
            }
        }

        return new ModelHolder(resourceSet, resource, dataResource, ePackage, classes, instances, uri);
    }


}