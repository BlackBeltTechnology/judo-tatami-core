package hu.blackbelt.judo.tatami.esm2ui;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.namespace.Model;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.esm.runtime.EsmUtils;
import hu.blackbelt.judo.meta.esm.structure.DataMember;
import hu.blackbelt.judo.meta.esm.structure.EntityType;
import hu.blackbelt.judo.meta.esm.structure.MemberType;
import hu.blackbelt.judo.meta.esm.structure.OneWayRelationMember;
import hu.blackbelt.judo.meta.esm.structure.RelationFeature;
import hu.blackbelt.judo.meta.esm.structure.RelationKind;
import hu.blackbelt.judo.meta.esm.structure.TransferObjectType;
import hu.blackbelt.judo.meta.esm.type.StringType;
import hu.blackbelt.judo.meta.esm.ui.Component;
import hu.blackbelt.judo.meta.esm.ui.Container;
import hu.blackbelt.judo.meta.esm.ui.Layout;
import hu.blackbelt.judo.meta.esm.ui.TransferObjectForm;
import hu.blackbelt.judo.meta.esm.ui.TransferObjectTable;
import hu.blackbelt.judo.meta.esm.ui.TransferObjectView;
import hu.blackbelt.judo.meta.ui.Application;
import hu.blackbelt.judo.meta.ui.NavigationController;
import hu.blackbelt.judo.meta.ui.runtime.UiModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static hu.blackbelt.judo.meta.esm.accesspoint.util.builder.AccesspointBuilders.newActorTypeBuilder;
import static hu.blackbelt.judo.meta.esm.namespace.util.builder.NamespaceBuilders.newModelBuilder;
import static hu.blackbelt.judo.meta.esm.operation.util.builder.OperationBuilders.newOperationBuilder;
import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.buildEsmModel;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.*;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newStringTypeBuilder;

import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newTransferObjectTableBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newTransferObjectFormBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newTransferObjectViewBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newDataColumnBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newGroupBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newDataFieldBuilder;

import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.SaveArguments.esmSaveArgumentsBuilder;

import static hu.blackbelt.judo.meta.ui.runtime.UiEpsilonValidator.calculateUiValidationScriptURI;
import static hu.blackbelt.judo.meta.ui.runtime.UiEpsilonValidator.validateUi;
import static hu.blackbelt.judo.meta.ui.runtime.UiModel.SaveArguments.uiSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.ui.runtime.UiModel.buildUiModel;
import static hu.blackbelt.judo.tatami.esm2ui.Esm2Ui.calculateEsm2UiTransformationScriptURI;
import static hu.blackbelt.judo.tatami.esm2ui.Esm2Ui.executeEsm2UiTransformation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class EsmAccesspoint2UiApplicationTest {

    private final String TEST_SOURCE_MODEL_NAME = "urn:test.judo-meta-esm";
    private final String TEST = "test";
    private final String TARGET_TEST_CLASSES = "target/test-classes";

    Log slf4jlog;
    EsmModel esmModel;

    String testName;
    Map<EObject, List<EObject>> resolvedTrace;
    UiModel uiModel;
    Esm2UiTransformationTrace esm2UiTransformationTrace;

    @BeforeEach
    void setUp() throws Exception {
        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading ESM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        esmModel = buildEsmModel().uri(URI.createURI(TEST_SOURCE_MODEL_NAME)).name(TEST).build();

        // Create empty UI model
        uiModel = buildUiModel().name(TEST).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        final String traceFileName = testName + "-esm2ui.model";

        // Saving trace map
        esm2UiTransformationTrace.save(new File(TARGET_TEST_CLASSES, traceFileName));

        // Loading trace map
        Esm2UiTransformationTrace esm2UiTransformationTraceLoaded = Esm2UiTransformationTrace
                .fromModelsAndTrace(TEST, esmModel, uiModel, new File(TARGET_TEST_CLASSES, traceFileName));

        // Resolve serialized URI's as EObject map
        resolvedTrace = esm2UiTransformationTraceLoaded.getTransformationTrace();

        // Printing trace
        for (EObject e : resolvedTrace.keySet()) {
            for (EObject t : resolvedTrace.get(e)) {
                log.trace(e.toString() + " -> " + t.toString());
            }
        }

        uiModel.saveUiModel(uiSaveArgumentsBuilder().file(new File(TARGET_TEST_CLASSES, testName + "-ui.model")));
        esmModel.saveEsmModel(esmSaveArgumentsBuilder().file(new File(TARGET_TEST_CLASSES, testName + "-esm.model")));

    }

    private void transform() throws Exception {
        // Make transformation which returns the trace with the serialized URI's
        esm2UiTransformationTrace = executeEsm2UiTransformation(esmModel, "default", 12, uiModel, new Slf4jLog(log),
                calculateEsm2UiTransformationScriptURI());

        assertTrue(uiModel.isValid());
        validateUi(new Slf4jLog(log), uiModel, calculateUiValidationScriptURI());
    }

    
    @Test
    void testCreateApplication() throws Exception {
        testName = "CreateApplication";

        final String MODEL_NAME = "Model";
        final String TRANSFER_OBJECT_TYPE_NAME = "T";
        final String ACCESS_POINT_NAME = "AP";

        final TransferObjectType accessPoint = newTransferObjectTypeBuilder()
                .withName(ACCESS_POINT_NAME)
                .build();
        
        accessPoint.setActorType(newActorTypeBuilder().build());

        final Model model = newModelBuilder().withName(MODEL_NAME)
                .withElements(Arrays.asList(accessPoint)).build();

        esmModel.addContent(model);

        transform();

        final Optional<Application> application = allUi(Application.class)
                .findAny();
        assertTrue(application.isPresent());

        final Optional<NavigationController> navigationController = allUi(NavigationController.class)
                .findAny();
        assertTrue(navigationController.isPresent());

        assertTrue(navigationController.get() == application.get().getNavigationController());
    }

    
    private Component createContainerTree(String prefix, DataMember dataMember) {
    	return newGroupBuilder()
    			.withLayout(Layout.VERTICAL)
    			.withName(prefix + "_grp1")
    			.withComponents(newGroupBuilder()
    					.withLayout(Layout.VERTICAL)
    					.withName(prefix + "_grp2")
    					.withComponents(
    							newDataFieldBuilder()
    								.withLabel("Label - " + dataMember.getName())
    								.withDataFeature(dataMember).build())
    					.build())
    			.build();
    }
    

    @Test
    void testCreateEntryPageFromExposedGraph() throws Exception {
        testName = "CreateEntryPageFromExposedGraph";

        // Create String DataType
        StringType string = newStringTypeBuilder().withName("string").withMaxLength(256).build();
        
        // Create Access Point
        final String MODEL_NAME = "Model";
//        final String TRANSFER_OBJECT_TYPE_NAME = "T";
        final String ACCESS_POINT_NAME = "AccessPoint";

        final TransferObjectType accessPoint = newTransferObjectTypeBuilder()
                .withName(ACCESS_POINT_NAME)
                .build();
        
        accessPoint.setActorType(newActorTypeBuilder().build());
        
        // Exposed entity
        final String EXPOSED_ENTITY_TYPE_NAME = "ExposedEntity";

        DataMember attribute = newDataMemberBuilder()
        		.withName("attribute")
        		.withMemberType(MemberType.STORED)
        		.withDataType(string)
                .build();
        attribute.setBinding(attribute);

        final EntityType exposedEntity = newEntityTypeBuilder()
                .withName(EXPOSED_ENTITY_TYPE_NAME)
                .withAttributes(attribute)
                .build();
        exposedEntity.setMapping(newMappingBuilder().withTarget(exposedEntity).build());

        // Create multiple reference relation to mapped entity
        final String EXPOSED_GRAPH_MULTIPLE_NAME = "ExposedGraphMultiple";
        final RelationFeature exposedRelationMultiple = newOneWayRelationMemberBuilder()
                .withName(EXPOSED_GRAPH_MULTIPLE_NAME)
                .withTarget(exposedEntity)
                .withGetterExpression(MODEL_NAME + EsmUtils.NAMESPACE_SEPARATOR + EXPOSED_ENTITY_TYPE_NAME)
                .withLower(0)
                .withUpper(-1)
                .withCreateable(true).withUpdateable(true).withDeleteable(true)
                .build();
        accessPoint.getRelations().add(exposedRelationMultiple);

        // Create single reference relation to mapped entity
        final String EXPOSED_GRAPH_SINGLE_NAME = "ExposedGraphSingle";
        final RelationFeature exposedRelationSingle = newOneWayRelationMemberBuilder()
                .withName(EXPOSED_GRAPH_SINGLE_NAME)
                .withTarget(exposedEntity)
                .withGetterExpression(MODEL_NAME + EsmUtils.NAMESPACE_SEPARATOR + EXPOSED_ENTITY_TYPE_NAME)
                .withLower(0)
                .withUpper(1)
                .withCreateable(true).withUpdateable(true).withDeleteable(true)
                .build();
        accessPoint.getRelations().add(exposedRelationSingle);

        
        // Add table representation for exposed relation
        final String EXPOSED_GRAPH_TABLE_NAME = "ExposedGraphTableName";

        final TransferObjectTable exposedEntityTable = newTransferObjectTableBuilder()
        		.withMaxVisibleElements(5)
        		.withMasterDetail(true)
        		.withName(EXPOSED_GRAPH_TABLE_NAME)
        		.withColumns(newDataColumnBuilder()
        				.withDataFeature(attribute)
        				.build())
        		.build();
        exposedEntity.setTable(exposedEntityTable);

        // Add view representation for exposed relation
        final String EXPOSED_GRAPH_VIEW_NAME = "ExposedGraphViewName";
        final TransferObjectView exposedEntityView = newTransferObjectViewBuilder()
        		.withName(EXPOSED_GRAPH_VIEW_NAME)
        		.withComponents(createContainerTree("group_view", attribute))
        		.build();
        exposedEntity.setView(exposedEntityView);

        // Add form representation for exposed relation
        final String EXPOSED_GRAPH_FORM_NAME = "ExposedGraphFormName";
        final TransferObjectForm exposedEntityForm = newTransferObjectFormBuilder()
        		.withName(EXPOSED_GRAPH_FORM_NAME)
        		.withComponents(createContainerTree("group_form", attribute))
        		.build();
        exposedEntity.setForm(exposedEntityForm);

        
        final Model model = newModelBuilder().withName(MODEL_NAME)
                .withElements(Arrays.asList(string, exposedEntity, accessPoint)).build();

        esmModel.addContent(model);
        
        transform();

        final Optional<Application> application = allUi(Application.class)
                .findAny();
        assertTrue(application.isPresent());

        final Optional<NavigationController> navigationController = allUi(NavigationController.class)
                .findAny();
        assertTrue(navigationController.isPresent());

        assertTrue(navigationController.get() == application.get().getNavigationController());
    }

    
/*
    @Test
    void testCreateExposedService() throws Exception {
        testName = "CreateExposedService";

        final String MODEL_NAME = "Model";
        final String TRANSFER_OBJECT_TYPE_NAME = "T";
        final String SERVICE_GROUP_NAME = "ServiceGroup";
        final String OPERATION_NAME = "unboundOperation";
        final String ACCESS_POINT_NAME = "AP";

        final TransferObjectType unmappedTransferObjectType = newTransferObjectTypeBuilder()
                .withName(TRANSFER_OBJECT_TYPE_NAME)
                .withOperations(newOperationBuilder().withName(OPERATION_NAME)
                        .withCustomImplementation(true)
                        .withModifier(OperationModifier.STATIC)
                        .withBinding("")
                        .build())
                .build();

        final TransferObjectType accessPoint = newTransferObjectTypeBuilder()
                .withName(ACCESS_POINT_NAME)
                .withRelations(newOneWayRelationMemberBuilder()
                        .withName(SERVICE_GROUP_NAME)
                        .withMemberType(MemberType.DERIVED)
                        .withTarget(unmappedTransferObjectType)
                        .withGetterExpression("Model::T")
                        .build())
                .build();
        
        accessPoint.setActorType(newActorTypeBuilder().build());

        final Model model = newModelBuilder().withName(MODEL_NAME)
                .withElements(Arrays.asList(unmappedTransferObjectType, accessPoint)).build();

        esmModel.addContent(model);

        transform();
    }
*/
    
/*
    @Test
    void testCreateExposedGraph() throws Exception {
        testName = "CreateExposedGraph";

        final String MODEL_NAME = "Model";
        final String ENTITY_TYPE_NAME = "E";
        final String EXPOSED_GRAPH_NAME = "g";
        final String ACCESS_POINT_NAME = "AP";

        final EntityType entityType = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME)
                .build();
        entityType.setMapping(newMappingBuilder().withTarget(entityType).build());

        final OneWayRelationMember eg = newOneWayRelationMemberBuilder()
                .withName(EXPOSED_GRAPH_NAME)
                .withTarget(entityType)
                .withMemberType(MemberType.DERIVED)
                .withGetterExpression(MODEL_NAME + EsmUtils.NAMESPACE_SEPARATOR + ENTITY_TYPE_NAME)
                .withLower(0)
                .withUpper(-1)
                .build();
        
        final TransferObjectType accessPoint = newTransferObjectTypeBuilder()
                .withName(ACCESS_POINT_NAME)
                .withRelations(eg)
                .build();
        
        accessPoint.setActorType(newActorTypeBuilder().build());
        
        log.debug("container is ap: " + ((TransferObjectType)eg.eContainer()).isAccesspoint());
        log.debug("target is mapped: " + eg.getTarget().isMapped());
        log.debug("getter: " + !eg.getGetterExpression().trim().equals(""));
        
        
        final Model model = newModelBuilder().withName(MODEL_NAME)
                .withElements(Arrays.asList(entityType, accessPoint)).build();

        esmModel.addContent(model);

        transform();
    }    
*/


/*
    @Test
    void testGeneratingBehaviourOfExposedGraphs() throws Exception {
        testName = "testGeneratingBehaviourOfExposedGraphs";

        final String MODEL_NAME = "Model";
        final String ENTITY_TYPE_E_NAME = "E";
        final String ENTITY_TYPE_F_NAME = "F";
        final String SINGLE_CONTAINMENT_RELATION_NAME = "singleContainment";
        final String MULTIPLE_CONTAINMENT_RELATION_NAME = "multipleContainment";
        final String SINGLE_REFERENCE_RELATION_NAME = "singleReference";
        final String MULTIPLE_REFERENCE_RELATION_NAME = "multipleReference";
        final String EXPOSED_GRAPH_NAME = "g";
        final String ACCESS_POINT_NAME = "AP";

        final int LOWER = 2;
        final int UPPER = 5;

        final String NAME_OF_GET_OPERATION = "_getG";
        final String NAME_OF_CREATE_OPERATION = "_createG";
        final String NAME_OF_UPDATE_OPERATION = "_updateG";
        final String NAME_OF_DELETE_OPERATION = "_deleteG";

        final String NAME_OF_UNSET_SINGLE_CONTAINMENT_OPERATION = "_unsetSingleContainmentOfG";

        final String NAME_OF_SET_SINGLE_REFERENCE_OPERATION = "_setSingleReferenceOfG";
        final String NAME_OF_UNSET_SINGLE_REFERENCE_OPERATION = "_unsetSingleReferenceOfG";

        final String NAME_OF_REMOVE_ALL_MULTIPLE_CONTAINMENT_OPERATION = "_removeMultipleContainmentFromG";

        final String NAME_OF_SET_MULTIPLE_REFERENCE_OPERATION = "_setMultipleReferenceOfG";
        final String NAME_OF_ADD_ALL_MULTIPLE_REFERENCE_OPERATION = "_addMultipleReferenceToG";
        final String NAME_OF_REMOVE_ALL_MULTIPLE_REFERENCE_OPERATION = "_removeMultipleReferenceFromG";

//        final String NAME_OF_GET_RANGE_OF_SINGLE_REFERENCE_TO_CREATE = "_getRangeOfSingleReferenceToCreateG";
//        final String NAME_OF_GET_RANGE_OF_MULTIPLE_REFERENCE_TO_CREATE = "_getRangeOfMultipleReferenceToCreateG";
//        final String NAME_OF_GET_RANGE_OF_SINGLE_REFERENCE_TO_UPDATE = "_getRangeOfSingleReferenceToUpdateG";
//        final String NAME_OF_GET_RANGE_OF_MULTIPLE_REFERENCE_TO_UPDATE = "_getRangeOfMultipleReferenceToUpdateG";

        final EntityType entityTypeF = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_F_NAME)
                .build();
        entityTypeF.setMapping(newMappingBuilder().withTarget(entityTypeF).build());

        final EntityType entityTypeE = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_E_NAME)
                .withRelations(newOneWayRelationMemberBuilder()
                        .withName(SINGLE_CONTAINMENT_RELATION_NAME)
                        .withMemberType(MemberType.STORED)
                        .withTarget(entityTypeF)
                        .withRelationKind(RelationKind.COMPOSITION)
                        .withCreateable(true).withUpdateable(true).withDeleteable(true)
                        .withLower(0).withUpper(1)
                        .withRangeExpression(MODEL_NAME + EsmUtils.NAMESPACE_SEPARATOR + ENTITY_TYPE_F_NAME)
                        .build())
                .withRelations(newOneWayRelationMemberBuilder()
                        .withName(SINGLE_REFERENCE_RELATION_NAME)
                        .withMemberType(MemberType.STORED)
                        .withTarget(entityTypeF)
                        .withRelationKind(RelationKind.ASSOCIATION)
                        .withLower(0).withUpper(1)
                        .withRangeExpression(MODEL_NAME + EsmUtils.NAMESPACE_SEPARATOR + ENTITY_TYPE_F_NAME)
                        .build())
                .withRelations(newOneWayRelationMemberBuilder()
                        .withName(MULTIPLE_CONTAINMENT_RELATION_NAME)
                        .withMemberType(MemberType.STORED)
                        .withTarget(entityTypeF)
                        .withRelationKind(RelationKind.COMPOSITION)
                        .withCreateable(true).withUpdateable(true).withDeleteable(true)
                        .withLower(0).withUpper(-1)
                        .withRangeExpression(MODEL_NAME + EsmUtils.NAMESPACE_SEPARATOR + ENTITY_TYPE_F_NAME)
                        .build())
                .withRelations(newOneWayRelationMemberBuilder()
                        .withName(MULTIPLE_REFERENCE_RELATION_NAME)
                        .withMemberType(MemberType.STORED)
                        .withTarget(entityTypeF)
                        .withRelationKind(RelationKind.ASSOCIATION)
                        .withLower(0).withUpper(-1)
                        .withRangeExpression(MODEL_NAME + EsmUtils.NAMESPACE_SEPARATOR + ENTITY_TYPE_F_NAME)
                        .build())
                .build();
        entityTypeE.setMapping(newMappingBuilder().withTarget(entityTypeE).build());

        
        final TransferObjectTable entityTypeETable = newTransferObjectTableBuilder()
        		.withMaxVisibleElements(5)
        		.withMasterDetail(true)
        		.withColumns(newDataColumnBuilder().build())
        		.build();
        entityTypeE.setTable(entityTypeETable);
        
        final TransferObjectType accessPoint = newTransferObjectTypeBuilder()
                .withName(ACCESS_POINT_NAME)
                .withRelations(newOneWayRelationMemberBuilder()
                        .withName(EXPOSED_GRAPH_NAME)
                        .withTarget(entityTypeE)
                        .withGetterExpression(MODEL_NAME + EsmUtils.NAMESPACE_SEPARATOR + ENTITY_TYPE_E_NAME)
                        .withLower(LOWER)
                        .withUpper(UPPER)
                        .withCreateable(true).withUpdateable(true).withDeleteable(true)
                        .build())
                .build();
        
        accessPoint.setActorType(newActorTypeBuilder().build());

        final Model model = newModelBuilder().withName(MODEL_NAME)
                .withElements(Arrays.asList(entityTypeE, entityTypeF, accessPoint)).build();

        esmModel.addContent(model);

        transform();
    }
    
    */
    
    static <T> Stream<T> asStream(Iterator<T> sourceIterator, boolean parallel) {
        Iterable<T> iterable = () -> sourceIterator;
        return StreamSupport.stream(iterable.spliterator(), parallel);
    }

    <T> Stream<T> allUi() {
        return asStream((Iterator<T>) uiModel.getResourceSet().getAllContents(), false);
    }

    private <T> Stream<T> allUi(final Class<T> clazz) {
        return allUi().filter(e -> clazz.isAssignableFrom(e.getClass())).map(e -> (T) e);
    }

}
