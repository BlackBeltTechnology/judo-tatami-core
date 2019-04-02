package hu.blackbelt.judo.tatami.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;

import java.util.Arrays;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.*;
import org.eclipse.emf.ecore.impl.EClassImpl;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.emf.ecore.xmi.impl.EcoreResourceFactoryImpl;
import org.junit.Test;

import java.util.stream.Collectors;

import static org.eclipse.emf.ecore.util.builder.EcoreBuilders.*;
import static org.junit.Assert.*;

public class TrackInfoServiceImplTest {


    public static final String HTTP_TEST = "http://test/";
    public static final String TEST_1 = "test1";

    TrackInfoServiceImpl trackInfoService;

    @Test
    public void testBuildGraph() {
        trackInfoService = new TrackInfoServiceImpl();


        RootModel rootModel = new RootModel();
        ResourceSet rootModelRs = createTestResourceSet("root", "RootC1");
        EPackage rootModelP = rootModelRs.getPackageRegistry().getEPackage(HTTP_TEST + "root");
        URI rootModelUri = URI.createURI("uri:root");
        Resource rootR = rootModelRs.createResource(rootModelUri);
        EClassImpl rootModelC1 = (EClassImpl) rootModelP.getEClassifier("RootC1");
        EObject  rootModelO1 = rootModelP.getEFactoryInstance().create(rootModelC1);
        rootR.getContents().add(rootModelO1);


        Level1Model1 level1Model1 = new Level1Model1();
        ResourceSet level1Model1Rs = createTestResourceSet("level1Model1", "Level1Model1C1");
        EPackage level1Model1P = level1Model1Rs.getPackageRegistry().getEPackage(HTTP_TEST + "level1Model1");
        URI level1Model1Uri = URI.createURI("uri:level1Model1");
        Resource level1Model1R = level1Model1Rs.createResource(level1Model1Uri);
        EClassImpl level1Model1C1 = (EClassImpl) level1Model1P.getEClassifier("Level1Model1C1");
        EObject  level1Model1O1 = level1Model1P.getEFactoryInstance().create(level1Model1C1);
        level1Model1R.getContents().add(level1Model1O1);

        Level1Model2 level1Model2 = new Level1Model2();
        ResourceSet level1Model2Rs = createTestResourceSet("level1Model2", "level1Model2C1");
        EPackage level1Model2P = level1Model2Rs.getPackageRegistry().getEPackage(HTTP_TEST + "level1Model2");
        URI level1Model2Uri = URI.createURI("uri:level1Model2");
        Resource level1Model2R = level1Model2Rs.createResource(level1Model2Uri);
        EClassImpl level1Model2C1 = (EClassImpl) level1Model2P.getEClassifier("level1Model2C1");
        EObject  level1Model2O1 = level1Model2P.getEFactoryInstance().create(level1Model2C1);
        level1Model2R.getContents().add(level1Model2O1);

        Level1Model3 level1Model3 = new Level1Model3();
        ResourceSet level1Model3Rs = createTestResourceSet("level1Model3", "level1Model3C1");
        EPackage level1Model3P = level1Model3Rs.getPackageRegistry().getEPackage(HTTP_TEST + "level1Model3");
        URI level1Model3Uri = URI.createURI("uri:level1Model3");
        Resource level1Model3R = level1Model3Rs.createResource(level1Model3Uri);
        EClassImpl level1Model3C1 = (EClassImpl) level1Model3P.getEClassifier("level1Model3C1");
        EObject  level1Model3O1 = level1Model3P.getEFactoryInstance().create(level1Model3C1);
        level1Model3R.getContents().add(level1Model3O1);

        Level2Model1 level2Model1 = new Level2Model1();
        ResourceSet level2Model1Rs = createTestResourceSet("level2Model1", "level2Model1C1");
        EPackage level2Model1P = level2Model1Rs.getPackageRegistry().getEPackage(HTTP_TEST + "level2Model1");
        URI level2Model1Uri = URI.createURI("uri:level2Model1");
        Resource level2Model1R = level2Model1Rs.createResource(level2Model1Uri);
        EClassImpl level2Model1C1 = (EClassImpl) level2Model1P.getEClassifier("level2Model1C1");
        EObject  level2Model1O1 = level2Model1P.getEFactoryInstance().create(level2Model1C1);
        level2Model1R.getContents().add(level2Model1O1);

        Level2Model2 level2Model2 = new Level2Model2();
        ResourceSet level2Model2Rs = createTestResourceSet("level2Model2", "level2Model2C1");
        EPackage level2Model2P = level2Model2Rs.getPackageRegistry().getEPackage(HTTP_TEST + "level2Model2");
        URI level2Model2Uri = URI.createURI("uri:level2Model2");
        Resource level2Model2R = level2Model2Rs.createResource(level2Model2Uri);
        EClassImpl level2Model2C1 = (EClassImpl) level2Model2P.getEClassifier("level2Model2C1");
        EObject  level2Model2O1 = level2Model2P.getEFactoryInstance().create(level2Model2C1);
        level2Model2R.getContents().add(level2Model2O1);


        Level3Model1 level3Model1 = new Level3Model1();
        ResourceSet level3Model1Rs = createTestResourceSet("level3Model1", "level3Model1C1");
        EPackage level3Model1P = level3Model1Rs.getPackageRegistry().getEPackage(HTTP_TEST + "level3Model1");
        URI level3Model1Uri = URI.createURI("uri:level3Model1");
        Resource level3Model1R = level3Model1Rs.createResource(level3Model1Uri);
        EClassImpl level3Model1C1 = (EClassImpl) level3Model1P.getEClassifier("level3Model1C1");
        EObject  level3Model1O1 = level3Model1P.getEFactoryInstance().create(level3Model1C1);
        level3Model1R.getContents().add(level3Model1O1);
        EObject  level3Model1O2 = level3Model1P.getEFactoryInstance().create(level3Model1C1);
        level3Model1R.getContents().add(level3Model1O2);

        //Map<EObject, List<EObject>> root_to_level1model1_map = ImmutableMap.of(rootModelO1, ImmutableList.of(level1Model1O1));
        TrackInfoTest root_to_level1model1 = TrackInfoTest.builder()
                .modelName(TEST_1)
                .name("root_to_level1model1")
                .source(ImmutableList.of(rootModel))
                .sourceResourceSet(ImmutableMap.of(rootModel, rootModelRs))
                .sourceURIS(ImmutableMap.of(rootModel, rootModelUri))
                .target(level1Model1)
                .targetResourceSet(level1Model1Rs)
                .targetURI(level1Model1Uri)
                .trace(ImmutableMap.of(rootModelO1, ImmutableList.of(level1Model1O1)))
                .build();

        TrackInfoTest root_to_level1model2 = TrackInfoTest.builder()
                .modelName(TEST_1)
                .name("root_to_level1model2")
                .source(ImmutableList.of(rootModel))
                .sourceResourceSet(ImmutableMap.of(rootModel, rootModelRs))
                .sourceURIS(ImmutableMap.of(rootModel, rootModelUri))
                .target(level1Model2)
                .targetResourceSet(level1Model2Rs)
                .targetURI(level1Model2Uri)
                .trace(ImmutableMap.of(rootModelO1, ImmutableList.of(level1Model2O1)))
                .build();

        TrackInfoTest root_to_level1model3 = TrackInfoTest.builder()
                .modelName(TEST_1)
                .name("root_to_level1model3")
                .source(ImmutableList.of(rootModel))
                .sourceResourceSet(ImmutableMap.of(rootModel, rootModelRs))
                .sourceURIS(ImmutableMap.of(rootModel, rootModelUri))
                .target(level1Model3)
                .targetResourceSet(level1Model3Rs)
                .targetURI(level1Model3Uri)
                .trace(ImmutableMap.of(rootModelO1, ImmutableList.of(level1Model3O1)))
                .build();

        TrackInfoTest root_to_level2model1 = TrackInfoTest.builder()
                .modelName(TEST_1)
                .name("root_to_level2model1")
                .source(ImmutableList.of(rootModel))
                .sourceResourceSet(ImmutableMap.of(rootModel, rootModelRs))
                .sourceURIS(ImmutableMap.of(rootModel, rootModelUri))
                .target(level2Model1)
                .targetResourceSet(level2Model1Rs)
                .targetURI(level2Model1Uri)
                .trace(ImmutableMap.of(rootModelO1, ImmutableList.of(level2Model1O1)))
                .build();

        TrackInfoTest level1_to_level2model2 = TrackInfoTest.builder()
                .modelName(TEST_1)
                .name("level1_to_level2model2")
                .source(ImmutableList.of(level1Model1, level1Model2))
                .sourceResourceSet(ImmutableMap.of(level1Model1, level1Model1Rs, level1Model2, level1Model2Rs))
                .sourceURIS(ImmutableMap.of(level1Model1, level1Model1Uri, level1Model2, level1Model2Uri))
                .target(level2Model2)
                .targetResourceSet(level2Model2Rs)
                .targetURI(level2Model2Uri)
                .trace(ImmutableMap.of(level1Model2O1, ImmutableList.of(level2Model2O1)))
                .build();

        TrackInfoTest level2_to_level3model1 = TrackInfoTest.builder()
                .modelName(TEST_1)
                .name("level2_to_level3model1")
                .source(ImmutableList.of(level2Model1, level2Model2))
                .sourceResourceSet(ImmutableMap.of(level2Model1, level1Model2Rs, level2Model2, level2Model2Rs))
                .sourceURIS(ImmutableMap.of(level2Model1, level2Model1Uri, level2Model2, level2Model2Uri))
                .target(level3Model1)
                .targetResourceSet(level3Model1Rs)
                .targetURI(level3Model1Uri)
                .trace(ImmutableMap.of(
                        level2Model2O1, ImmutableList.of(level3Model1O1),
                        level2Model1O1, ImmutableList.of(level3Model1O2)
                ))
                .build();



        trackInfoService.add(root_to_level1model1);
        trackInfoService.add(root_to_level1model2);
        trackInfoService.add(root_to_level1model3);
        trackInfoService.add(root_to_level2model1);
        trackInfoService.add(level1_to_level2model2);
        trackInfoService.add(level2_to_level3model1);

        assertEquals(rootModelO1, trackInfoService.getRootAscendantOfInstance(TEST_1, level3Model1O1));
        assertEquals(null, trackInfoService.getAscendantOfInstanceByModelType(TEST_1, Level1Model1.class, level3Model1O1));
        assertEquals(level1Model2O1, trackInfoService.getAscendantOfInstanceByModelType(TEST_1, Level1Model2.class, level3Model1O1));
        assertEquals(level2Model1O1, trackInfoService.getAscendantOfInstanceByModelType(TEST_1, Level2Model1.class, level3Model1O2));
        assertEquals(
                ImmutableList.of(level2_to_level3model1, level1_to_level2model2, root_to_level1model2),
                trackInfoService.getTrackInfoAscendantsByInstance(TEST_1, level3Model1O1)
        );

        assertEquals(ImmutableMap.builder()
                        .put(root_to_level1model1, ImmutableList.of(level1Model1O1))
                        .put(root_to_level1model2, ImmutableList.of(level1Model2O1))
                        .put(root_to_level1model3, ImmutableList.of(level1Model3O1))
                        .put(root_to_level2model1, ImmutableList.of(level2Model1O1))
                        .put(level1_to_level2model2, ImmutableList.of(level2Model2O1))
                        .put(level2_to_level3model1, ImmutableList.of(level3Model1O1))
                        .build(),
                trackInfoService.getAllDescendantOfInstance(TEST_1, rootModelO1)
        );


        assertEquals(ImmutableList.of(level3Model1O1), trackInfoService.getDescendantOfInstanceByModelType(TEST_1, Level3Model1.class, rootModelO1));
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

}