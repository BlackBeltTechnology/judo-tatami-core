package hu.blackbelt.judo.tatami.esm2psm;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.accesspoint.AccessPoint;
import hu.blackbelt.judo.meta.esm.namespace.Model;
import hu.blackbelt.judo.meta.esm.operation.OperationModifier;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.esm.runtime.EsmUtils;
import hu.blackbelt.judo.meta.esm.structure.EntityType;
import hu.blackbelt.judo.meta.esm.structure.RelationMemberType;
import hu.blackbelt.judo.meta.esm.structure.TransferObjectType;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType;
import hu.blackbelt.judo.meta.psm.service.TransferObjectRelation;
import hu.blackbelt.judo.meta.psm.service.TransferOperation;
import hu.blackbelt.judo.meta.psm.service.TransferOperationBehaviourType;
import hu.blackbelt.judo.meta.psm.service.UnboundOperation;
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

import static hu.blackbelt.judo.meta.esm.accesspoint.util.builder.AccesspointBuilders.newAccessPointBuilder;
import static hu.blackbelt.judo.meta.esm.accesspoint.util.builder.AccesspointBuilders.newExposedGraphBuilder;
import static hu.blackbelt.judo.meta.esm.namespace.util.builder.NamespaceBuilders.newModelBuilder;
import static hu.blackbelt.judo.meta.esm.operation.util.builder.OperationBuilders.newOperationBuilder;
import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.buildEsmModel;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.*;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.calculatePsmValidationScriptURI;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.validatePsm;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.SaveArguments.psmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.buildPsmModel;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.calculateEsm2PsmTransformationScriptURI;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.executeEsm2PsmTransformation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class EsmAccesspoint2PsmAccesspointTest {

    private final String TEST_SOURCE_MODEL_NAME = "urn:test.judo-meta-esm";
    private final String TEST = "test";
    private final String TARGET_TEST_CLASSES = "target/test-classes";

    Log slf4jlog;
    EsmModel esmModel;

    String testName;
    Map<EObject, List<EObject>> resolvedTrace;
    PsmModel psmModel;
    Esm2PsmTransformationTrace esm2PsmTransformationTrace;

    @BeforeEach
    void setUp() throws Exception {
        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading ESM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        esmModel = buildEsmModel().uri(URI.createURI(TEST_SOURCE_MODEL_NAME)).name(TEST).build();

        // Create empty PSM model
        psmModel = buildPsmModel().name(TEST).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        final String traceFileName = testName + "-esm2psm.model";

        // Saving trace map
        esm2PsmTransformationTrace.save(new File(TARGET_TEST_CLASSES, traceFileName));

        // Loading trace map
        Esm2PsmTransformationTrace esm2PsmTransformationTraceLoaded = Esm2PsmTransformationTrace
                .fromModelsAndTrace(TEST, esmModel, psmModel, new File(TARGET_TEST_CLASSES, traceFileName));

        // Resolve serialized URI's as EObject map
        resolvedTrace = esm2PsmTransformationTraceLoaded.getTransformationTrace();

        // Printing trace
        for (EObject e : resolvedTrace.keySet()) {
            for (EObject t : resolvedTrace.get(e)) {
                log.trace(e.toString() + " -> " + t.toString());
            }
        }

        psmModel.savePsmModel(psmSaveArgumentsBuilder().file(new File(TARGET_TEST_CLASSES, testName + "-psm.model")));
    }

    private void transform() throws Exception {
        // Make transformation which returns the trace with the serialized URI's
        esm2PsmTransformationTrace = executeEsm2PsmTransformation(esmModel, psmModel, new Slf4jLog(log),
                calculateEsm2PsmTransformationScriptURI());

        assertTrue(psmModel.isValid());
        validatePsm(new Slf4jLog(log), psmModel, calculatePsmValidationScriptURI());
    }

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

        final AccessPoint accessPoint = newAccessPointBuilder()
                .withName(ACCESS_POINT_NAME)
                .withExposedGraphs(newExposedGraphBuilder()
                        .withName(SERVICE_GROUP_NAME)
                        .withTarget(unmappedTransferObjectType)
                        .withGetterExpression("")
                        .build())
                .build();

        final Model model = newModelBuilder().withName(MODEL_NAME)
                .withElements(Arrays.asList(unmappedTransferObjectType, accessPoint)).build();

        esmModel.addContent(model);

        transform();

        final Optional<hu.blackbelt.judo.meta.psm.service.TransferObjectType> ap = allPsm(hu.blackbelt.judo.meta.psm.service.TransferObjectType.class)
                .filter(t -> t.isAccessPoint())
                .findAny();
        assertTrue(ap.isPresent());

        assertTrue(ap.get().getRelations().stream().anyMatch(s -> SERVICE_GROUP_NAME.equals(s.getName())));
    }

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

        final AccessPoint accessPoint = newAccessPointBuilder()
                .withName(ACCESS_POINT_NAME)
                .withExposedGraphs(newExposedGraphBuilder()
                        .withName(EXPOSED_GRAPH_NAME)
                        .withTarget(entityType)
                        .withGetterExpression(MODEL_NAME + EsmUtils.NAMESPACE_SEPARATOR + ENTITY_TYPE_NAME)
                        .withLower(0)
                        .withUpper(-1)
                        .build())
                .build();

        final Model model = newModelBuilder().withName(MODEL_NAME)
                .withElements(Arrays.asList(entityType, accessPoint)).build();

        esmModel.addContent(model);

        transform();

        final Optional<hu.blackbelt.judo.meta.psm.service.TransferObjectType> ap = allPsm(hu.blackbelt.judo.meta.psm.service.TransferObjectType.class)
                .filter(t -> t.isAccessPoint())
                .findAny();
        assertTrue(ap.isPresent());

        assertTrue(ap.get().getRelations().stream()
                .filter(r -> r.isExposedGraph())
                .anyMatch(g -> EXPOSED_GRAPH_NAME.equals(g.getName())));
    }

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
                        .withRelationMemberType(RelationMemberType.RELATION)
                        .withTarget(entityTypeF)
                        .withContainment(true)
                        .withCreateable(true).withUpdateable(true).withDeleteable(true)
                        .withLower(0).withUpper(1)
                        .withRangeExpression(MODEL_NAME + EsmUtils.NAMESPACE_SEPARATOR + ENTITY_TYPE_F_NAME)
                        .build())
                .withRelations(newOneWayRelationMemberBuilder()
                        .withName(SINGLE_REFERENCE_RELATION_NAME)
                        .withRelationMemberType(RelationMemberType.RELATION)
                        .withTarget(entityTypeF)
                        .withContainment(false)
                        .withLower(0).withUpper(1)
                        .withRangeExpression(MODEL_NAME + EsmUtils.NAMESPACE_SEPARATOR + ENTITY_TYPE_F_NAME)
                        .build())
                .withRelations(newOneWayRelationMemberBuilder()
                        .withName(MULTIPLE_CONTAINMENT_RELATION_NAME)
                        .withRelationMemberType(RelationMemberType.RELATION)
                        .withTarget(entityTypeF)
                        .withContainment(true)
                        .withCreateable(true).withUpdateable(true).withDeleteable(true)
                        .withLower(0).withUpper(-1)
                        .withRangeExpression(MODEL_NAME + EsmUtils.NAMESPACE_SEPARATOR + ENTITY_TYPE_F_NAME)
                        .build())
                .withRelations(newOneWayRelationMemberBuilder()
                        .withName(MULTIPLE_REFERENCE_RELATION_NAME)
                        .withRelationMemberType(RelationMemberType.RELATION)
                        .withTarget(entityTypeF)
                        .withContainment(false)
                        .withLower(0).withUpper(-1)
                        .withRangeExpression(MODEL_NAME + EsmUtils.NAMESPACE_SEPARATOR + ENTITY_TYPE_F_NAME)
                        .build())
                .build();
        entityTypeE.setMapping(newMappingBuilder().withTarget(entityTypeE).build());

        final AccessPoint accessPoint = newAccessPointBuilder()
                .withName(ACCESS_POINT_NAME)
                .withExposedGraphs(newExposedGraphBuilder()
                        .withName(EXPOSED_GRAPH_NAME)
                        .withTarget(entityTypeE)
                        .withGetterExpression(MODEL_NAME + EsmUtils.NAMESPACE_SEPARATOR + ENTITY_TYPE_E_NAME)
                        .withLower(LOWER)
                        .withUpper(UPPER)
                        .withCreateable(true).withUpdateable(true).withDeleteable(true)
                        .build())
                .build();

        final Model model = newModelBuilder().withName(MODEL_NAME)
                .withElements(Arrays.asList(entityTypeE, entityTypeF, accessPoint)).build();

        esmModel.addContent(model);

        transform();

        final Optional<hu.blackbelt.judo.meta.psm.service.TransferObjectType> ap = allPsm(hu.blackbelt.judo.meta.psm.service.TransferObjectType.class)
                .filter(t -> t.isAccessPoint())
                .findAny();
        assertTrue(ap.isPresent());

        final Optional<TransferObjectRelation> graph = ap.get().getRelations().stream()
                .filter(r -> r.isExposedGraph())
                .filter(g -> EXPOSED_GRAPH_NAME.equals(g.getName())).findAny();
        assertTrue(graph.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> defaultE = allPsm(MappedTransferObjectType.class)
                .filter(t -> ENTITY_TYPE_E_NAME.equals(t.getName()))
                .findAny();
        assertTrue(defaultE.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> defaultF = allPsm(MappedTransferObjectType.class)
                .filter(t -> ENTITY_TYPE_F_NAME.equals(t.getName()))
                .findAny();
        assertTrue(defaultF.isPresent());

        final Optional<TransferObjectRelation> defaultSingleContainment = defaultE.get().getRelations().stream().filter(r -> SINGLE_CONTAINMENT_RELATION_NAME.equals(r.getName())).findAny();
        final Optional<TransferObjectRelation> defaultMultipleContainment = defaultE.get().getRelations().stream().filter(r -> MULTIPLE_CONTAINMENT_RELATION_NAME.equals(r.getName())).findAny();
        final Optional<TransferObjectRelation> defaultSingleReference = defaultE.get().getRelations().stream().filter(r -> SINGLE_REFERENCE_RELATION_NAME.equals(r.getName())).findAny();
        final Optional<TransferObjectRelation> defaultMultipleReference = defaultE.get().getRelations().stream().filter(r -> MULTIPLE_REFERENCE_RELATION_NAME.equals(r.getName())).findAny();

        assertTrue(defaultSingleContainment.isPresent());
        assertTrue(defaultMultipleContainment.isPresent());
        assertTrue(defaultSingleReference.isPresent());
        assertTrue(defaultMultipleReference.isPresent());

        log.debug("List of generated operations:{}", graph.get().getTarget().getOperations().stream().map(o -> "\n - " + o.getName()).sorted().collect(Collectors.joining()));

        assertTrue(ap.get().getOperations().stream().anyMatch(o -> NAME_OF_GET_OPERATION.equals(o.getName()) && (o instanceof UnboundOperation) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.GET && EcoreUtil.equals(o.getBehaviour().getOwner(), graph.get()) &&
                o.getInput() == null && o.getOutput() != null && o.getFaults().isEmpty() &&
                o.getOutput().getCardinality().getLower() == LOWER && o.getOutput().getCardinality().getUpper() == UPPER &&
                EcoreUtil.equals(o.getOutput().getType(), defaultE.get())
        ));

        final Optional<TransferOperation> create = ap.get().getOperations().stream().filter(o -> NAME_OF_CREATE_OPERATION.equals(o.getName()) && (o instanceof UnboundOperation) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.CREATE && EcoreUtil.equals(o.getBehaviour().getOwner(), graph.get()) &&
                o.getInput() != null && o.getOutput() != null && o.getFaults().isEmpty() &&
                o.getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getInput().getType(), defaultE.get()) &&
                o.getOutput().getCardinality().getLower() == 1 && o.getOutput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getOutput().getType(), defaultE.get())
        ).findAny();

        assertTrue(create.isPresent());

        final Optional<TransferOperation> update = ap.get().getOperations().stream().filter(o -> NAME_OF_UPDATE_OPERATION.equals(o.getName()) && (o instanceof UnboundOperation) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.UPDATE && EcoreUtil.equals(o.getBehaviour().getOwner(), graph.get()) &&
                o.getInput() != null && o.getOutput() != null && o.getFaults().isEmpty() &&
                o.getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getInput().getType(), defaultE.get()) &&
                o.getOutput().getCardinality().getLower() == 1 && o.getOutput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getOutput().getType(), defaultE.get())
        ).findAny();

        assertTrue(update.isPresent());

        assertTrue(ap.get().getOperations().stream().anyMatch(o -> NAME_OF_DELETE_OPERATION.equals(o.getName()) && (o instanceof UnboundOperation) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.DELETE && EcoreUtil.equals(o.getBehaviour().getOwner(), graph.get()) &&
                o.getInput() != null && o.getOutput() == null && o.getFaults().isEmpty() &&
                o.getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getInput().getType(), defaultE.get())
        ));

        assertTrue(ap.get().getOperations().stream().anyMatch(o -> NAME_OF_DELETE_OPERATION.equals(o.getName()) && (o instanceof UnboundOperation) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.DELETE && EcoreUtil.equals(o.getBehaviour().getOwner(), graph.get()) &&
                o.getInput() != null && o.getOutput() == null && o.getFaults().isEmpty() &&
                o.getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getInput().getType(), defaultE.get())
        ));

        assertTrue(ap.get().getOperations().stream().anyMatch(o -> NAME_OF_UNSET_SINGLE_CONTAINMENT_OPERATION.equals(o.getName()) && (o instanceof UnboundOperation) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.UNSET_RELATION && EcoreUtil.equals(o.getBehaviour().getOwner(), graph.get()) && EcoreUtil.equals(o.getBehaviour().getRelation(), defaultSingleContainment.get()) &&
                o.getInput() != null && o.getOutput() == null && o.getFaults().isEmpty() &&
                o.getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getInput().getType(), defaultE.get())
        ));

        assertTrue(ap.get().getOperations().stream().anyMatch(o -> NAME_OF_SET_SINGLE_REFERENCE_OPERATION.equals(o.getName()) && (o instanceof UnboundOperation) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.SET_RELATION && EcoreUtil.equals(o.getBehaviour().getOwner(), graph.get()) && EcoreUtil.equals(o.getBehaviour().getRelation(), defaultSingleReference.get()) &&
                o.getInput() != null && o.getOutput() == null && o.getFaults().isEmpty() &&
                o.getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getInput().getType(), defaultE.get())
        ));

        assertTrue(ap.get().getOperations().stream().anyMatch(o -> NAME_OF_UNSET_SINGLE_REFERENCE_OPERATION.equals(o.getName()) && (o instanceof UnboundOperation) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.UNSET_RELATION && EcoreUtil.equals(o.getBehaviour().getOwner(), graph.get()) && EcoreUtil.equals(o.getBehaviour().getRelation(), defaultSingleReference.get()) &&
                o.getInput() != null && o.getOutput() == null && o.getFaults().isEmpty() &&
                o.getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getInput().getType(), defaultE.get())
        ));

        assertTrue(ap.get().getOperations().stream().anyMatch(o -> NAME_OF_REMOVE_ALL_MULTIPLE_CONTAINMENT_OPERATION.equals(o.getName()) && (o instanceof UnboundOperation) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.REMOVE_ALL_FROM_RELATION && EcoreUtil.equals(o.getBehaviour().getOwner(), graph.get()) && EcoreUtil.equals(o.getBehaviour().getRelation(), defaultMultipleContainment.get()) &&
                o.getInput() != null && o.getOutput() == null && o.getFaults().isEmpty() &&
                o.getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getInput().getType(), defaultE.get())
        ));

        assertTrue(ap.get().getOperations().stream().anyMatch(o -> NAME_OF_SET_MULTIPLE_REFERENCE_OPERATION.equals(o.getName()) && (o instanceof UnboundOperation) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.SET_RELATION && EcoreUtil.equals(o.getBehaviour().getOwner(), graph.get()) && EcoreUtil.equals(o.getBehaviour().getRelation(), defaultMultipleReference.get()) &&
                o.getInput() != null && o.getOutput() == null && o.getFaults().isEmpty() &&
                o.getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getInput().getType(), defaultE.get())
        ));

        assertTrue(ap.get().getOperations().stream().anyMatch(o -> NAME_OF_ADD_ALL_MULTIPLE_REFERENCE_OPERATION.equals(o.getName()) && (o instanceof UnboundOperation) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.ADD_ALL_TO_RELATION && EcoreUtil.equals(o.getBehaviour().getOwner(), graph.get()) && EcoreUtil.equals(o.getBehaviour().getRelation(), defaultMultipleReference.get()) &&
                o.getInput() != null && o.getOutput() == null && o.getFaults().isEmpty() &&
                o.getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getInput().getType(), defaultE.get())
        ));

        assertTrue(ap.get().getOperations().stream().anyMatch(o -> NAME_OF_REMOVE_ALL_MULTIPLE_REFERENCE_OPERATION.equals(o.getName()) && (o instanceof UnboundOperation) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.REMOVE_ALL_FROM_RELATION && EcoreUtil.equals(o.getBehaviour().getOwner(), graph.get()) && EcoreUtil.equals(o.getBehaviour().getRelation(), defaultMultipleReference.get()) &&
                o.getInput() != null && o.getOutput() == null && o.getFaults().isEmpty() &&
                o.getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getInput().getType(), defaultE.get())
        ));

//        assertTrue(ap.get().getOperations().stream().anyMatch(o -> NAME_OF_GET_RANGE_OF_SINGLE_REFERENCE_TO_CREATE.equals(o.getName()) && (o instanceof UnboundOperation) &&
//                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.GET_RANGE_OF_RELATION && EcoreUtil.equals(o.getBehaviour().getOwner(), create.get().getInput()) && EcoreUtil.equals(o.getBehaviour().getRelation(), defaultSingleReference.get()) &&
//                o.getInput() != null && o.getOutput() != null && o.getFaults().isEmpty() &&
//                o.getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
//                EcoreUtil.equals(o.getInput().getType(), defaultE.get()) &&
//                o.getOutput().getCardinality().getLower() == 0 && o.getOutput().getCardinality().getUpper() == -1 &&
//                EcoreUtil.equals(o.getOutput().getType(), defaultF.get())
//        ));
//
//        assertTrue(ap.get().getOperations().stream().anyMatch(o -> NAME_OF_GET_RANGE_OF_SINGLE_REFERENCE_TO_UPDATE.equals(o.getName()) && (o instanceof UnboundOperation) &&
//                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.GET_RANGE_OF_RELATION && EcoreUtil.equals(o.getBehaviour().getOwner(), update.get().getInput()) && EcoreUtil.equals(o.getBehaviour().getRelation(), defaultSingleReference.get()) &&
//                o.getInput() != null && o.getOutput() != null && o.getFaults().isEmpty() &&
//                o.getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
//                EcoreUtil.equals(o.getInput().getType(), defaultE.get()) &&
//                o.getOutput().getCardinality().getLower() == 0 && o.getOutput().getCardinality().getUpper() == -1 &&
//                EcoreUtil.equals(o.getOutput().getType(), defaultF.get())
//        ));
//
//        assertTrue(ap.get().getOperations().stream().anyMatch(o -> NAME_OF_GET_RANGE_OF_MULTIPLE_REFERENCE_TO_CREATE.equals(o.getName()) && (o instanceof UnboundOperation) &&
//                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.GET_RANGE_OF_RELATION && EcoreUtil.equals(o.getBehaviour().getOwner(), create.get().getInput()) && EcoreUtil.equals(o.getBehaviour().getRelation(), defaultMultipleReference.get()) &&
//                o.getInput() != null && o.getOutput() != null && o.getFaults().isEmpty() &&
//                o.getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
//                EcoreUtil.equals(o.getInput().getType(), defaultE.get()) &&
//                o.getOutput().getCardinality().getLower() == 0 && o.getOutput().getCardinality().getUpper() == -1 &&
//                EcoreUtil.equals(o.getOutput().getType(), defaultF.get())
//        ));
//
//        assertTrue(ap.get().getOperations().stream().anyMatch(o -> NAME_OF_GET_RANGE_OF_MULTIPLE_REFERENCE_TO_UPDATE.equals(o.getName()) && (o instanceof UnboundOperation) &&
//                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.GET_RANGE_OF_RELATION && EcoreUtil.equals(o.getBehaviour().getOwner(), update.get().getInput()) && EcoreUtil.equals(o.getBehaviour().getRelation(), defaultMultipleReference.get()) &&
//                o.getInput() != null && o.getOutput() != null && o.getFaults().isEmpty() &&
//                o.getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
//                EcoreUtil.equals(o.getInput().getType(), defaultE.get()) &&
//                o.getOutput().getCardinality().getLower() == 0 && o.getOutput().getCardinality().getUpper() == -1 &&
//                EcoreUtil.equals(o.getOutput().getType(), defaultF.get())
//        ));

        assertEquals(11L, ap.get().getOperations().stream().filter(o -> o instanceof UnboundOperation).count());
    }

    static <T> Stream<T> asStream(Iterator<T> sourceIterator, boolean parallel) {
        Iterable<T> iterable = () -> sourceIterator;
        return StreamSupport.stream(iterable.spliterator(), parallel);
    }

    <T> Stream<T> allPsm() {
        return asStream((Iterator<T>) psmModel.getResourceSet().getAllContents(), false);
    }

    private <T> Stream<T> allPsm(final Class<T> clazz) {
        return allPsm().filter(e -> clazz.isAssignableFrom(e.getClass())).map(e -> (T) e);
    }
}
