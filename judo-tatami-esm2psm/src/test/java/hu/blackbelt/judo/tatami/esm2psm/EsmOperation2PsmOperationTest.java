package hu.blackbelt.judo.tatami.esm2psm;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.accesspoint.AccessPoint;
import hu.blackbelt.judo.meta.esm.namespace.Model;
import hu.blackbelt.judo.meta.esm.operation.OperationModifier;
import hu.blackbelt.judo.meta.esm.operation.util.builder.OperationBuilder;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.esm.runtime.EsmUtils;
import hu.blackbelt.judo.meta.esm.structure.EntityType;
import hu.blackbelt.judo.meta.esm.structure.RelationMemberType;
import hu.blackbelt.judo.meta.esm.structure.TransferObjectType;
import hu.blackbelt.judo.meta.psm.PsmUtils;
import hu.blackbelt.judo.meta.psm.accesspoint.ExposedGraph;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.psm.service.BoundTransferOperation;
import hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType;
import hu.blackbelt.judo.meta.psm.service.OperationDeclaration;
import hu.blackbelt.judo.meta.psm.service.TransferObjectRelation;
import hu.blackbelt.judo.meta.psm.service.TransferOperation;
import hu.blackbelt.judo.meta.psm.service.TransferOperationBehaviourType;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static hu.blackbelt.judo.meta.esm.accesspoint.util.builder.AccesspointBuilders.newAccessPointBuilder;
import static hu.blackbelt.judo.meta.esm.accesspoint.util.builder.AccesspointBuilders.newExposedGraphBuilder;
import static hu.blackbelt.judo.meta.esm.namespace.util.builder.NamespaceBuilders.newModelBuilder;
import static hu.blackbelt.judo.meta.esm.operation.util.builder.OperationBuilders.newOperationBuilder;
import static hu.blackbelt.judo.meta.esm.operation.util.builder.OperationBuilders.newParameterBuilder;
import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.buildEsmModel;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.*;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.SaveArguments.psmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.buildPsmModel;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.calculateEsm2PsmTransformationScriptURI;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.executeEsm2PsmTransformation;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class EsmOperation2PsmOperationTest {

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

    }

    private static final String MODEL_NAME = "Model";
    private static final String ENTITY_TYPE_NAME = "E";
    private static final String MAPPED_TRANSFER_OBJECT_TYPE_NAME = "M";
    private static final String UNMAPPED_TRANSFER_OBJECT_TYPE_NAME = "U";

    private static final String BOUND_OPERATION_NAME = "boundOperation";
    private static final String STATIC_OPERATION_NAME = "staticOperation";
    private static final String BOUND_OPERATION_NAME_IN_MAPPED_TRANSFER_OBJECT_TYPE = "boundOperation2";
    private static final String STATIC_OPERATION_NAME_IN_MAPPED_TRANSFER_OBJECT_TYPE = "staticOperation2";
    private static final String STATIC_REFERENCING_OPERATION_NAME_IN_UNMAPPED_TRANSFER_OBJECT_TYPE = "staticOperation3";
    private static final String STATIC_OPERATION_NAME_IN_UNMAPPED_TRANSFER_OBJECT_TYPE = "staticOperation4";

    private static final String INPUT_PARAMETER_TYPE_NAME = "InputType";
    private static final String INPUT_PARAMETER_NAME = "input";

    private static final String OUTPUT_PARAMETER_TYPE_NAME = "OutputType";
    private static final String OUTPUT_PARAMETER_NAME = "output";

    private static final String FAULT1_TYPE_NAME = "Fault1Type";
    private static final String FAULT1_NAME = "fault1";

    private static final String FAULT2_TYPE_NAME = "Fault2Type";
    private static final String FAULT2_NAME = "fault2";

    private static final int INPUT_PARAMETER_CARDINALITY_LOWER = 2;
    private static final int INPUT_PARAMETER_CARDINALITY_UPPER = 5;

    private static final int OUTPUT_PARAMETER_CARDINALITY_LOWER = 10;
    private static final int OUTPUT_PARAMETER_CARDINALITY_UPPER = 15;

    private static final Predicate<OperationDeclaration> EXPECTED_INPUT_AND_OUTPUT_PARAMETERS = o -> o.getInput() != null && o.getOutput() != null &&
            INPUT_PARAMETER_NAME.equals(o.getInput().getName()) &&
            (MODEL_NAME + PsmUtils.NAMESPACE_SEPARATOR + INPUT_PARAMETER_TYPE_NAME).equals(PsmUtils.namespaceElementToString(o.getInput().getType())) &&
            o.getInput().getCardinality() != null && o.getInput().getCardinality().getLower() == INPUT_PARAMETER_CARDINALITY_LOWER && o.getInput().getCardinality().getUpper() == INPUT_PARAMETER_CARDINALITY_UPPER &&
            OUTPUT_PARAMETER_NAME.equals(o.getOutput().getName()) &&
            (MODEL_NAME + PsmUtils.NAMESPACE_SEPARATOR + OUTPUT_PARAMETER_TYPE_NAME).equals(PsmUtils.namespaceElementToString(o.getOutput().getType())) &&
            o.getOutput().getCardinality() != null && o.getOutput().getCardinality().getLower() == OUTPUT_PARAMETER_CARDINALITY_LOWER && o.getOutput().getCardinality().getUpper() == OUTPUT_PARAMETER_CARDINALITY_UPPER;

    private OperationBuilder parameterDecorator(final OperationBuilder operationBuilder, final TransferObjectType inputParameterType, final TransferObjectType outputParameterType, final TransferObjectType fault1Type, final TransferObjectType fault2Type) {
        return operationBuilder
                .withInput(newParameterBuilder()
                        .withName(INPUT_PARAMETER_NAME)
                        .withTarget(inputParameterType)
                        .withLower(INPUT_PARAMETER_CARDINALITY_LOWER)
                        .withUpper(INPUT_PARAMETER_CARDINALITY_UPPER)
                        .build())
                .withOutput(newParameterBuilder()
                        .withName(OUTPUT_PARAMETER_NAME)
                        .withTarget(outputParameterType)
                        .withLower(OUTPUT_PARAMETER_CARDINALITY_LOWER)
                        .withUpper(OUTPUT_PARAMETER_CARDINALITY_UPPER)
                        .build())
                .withFaults(Arrays.asList(
                        newParameterBuilder()
                                .withName(FAULT1_NAME)
                                .withTarget(fault1Type)
                                .build(),
                        newParameterBuilder()
                                .withName(FAULT2_NAME)
                                .withTarget(fault2Type)
                                .build()));
    }

    @Test
    void testCreateSimpleOperations() throws Exception {
        testName = "CreateBoundOperations";

        final String body = "return;";

        final TransferObjectType inputParameterType = newTransferObjectTypeBuilder()
                .withName(INPUT_PARAMETER_TYPE_NAME)
                .build();

        final TransferObjectType outputParameterType = newTransferObjectTypeBuilder()
                .withName(OUTPUT_PARAMETER_TYPE_NAME)
                .build();

        final TransferObjectType fault1Type = newTransferObjectTypeBuilder()
                .withName(FAULT1_TYPE_NAME)
                .build();

        final TransferObjectType fault2Type = newTransferObjectTypeBuilder()
                .withName(FAULT2_TYPE_NAME)
                .build();

        final EntityType entityType = newEntityTypeBuilder().withName(ENTITY_TYPE_NAME).withAbstract_(false)
                .withOperations(parameterDecorator(newOperationBuilder()
                                .withName(BOUND_OPERATION_NAME)
                                .withBody(body),
                        inputParameterType, outputParameterType, fault1Type, fault2Type).build())
                .withOperations(parameterDecorator(newOperationBuilder()
                                .withName(STATIC_OPERATION_NAME)
                                .withModifier(OperationModifier.STATIC)
                                .withBody(body),
                        inputParameterType, outputParameterType, fault1Type, fault2Type).build())
                .build();
        entityType.setMapping(newMappingBuilder().withTarget(entityType).build());

        final TransferObjectType mappedTransferObjectType = newTransferObjectTypeBuilder().withName(MAPPED_TRANSFER_OBJECT_TYPE_NAME)
                .withMapping(newMappingBuilder().withTarget(entityType).build())
                .withOperations(parameterDecorator(newOperationBuilder()
                                .withName(BOUND_OPERATION_NAME_IN_MAPPED_TRANSFER_OBJECT_TYPE)
                                .withBound(true)
                                .withBinding(BOUND_OPERATION_NAME),
                        inputParameterType, outputParameterType, fault1Type, fault2Type).build())
                .withOperations(parameterDecorator(newOperationBuilder()
                                .withName(STATIC_OPERATION_NAME_IN_MAPPED_TRANSFER_OBJECT_TYPE)
                                .withModifier(OperationModifier.STATIC)
                                .withBound(true)
                                .withBinding(MODEL_NAME + EsmUtils.NAMESPACE_SEPARATOR + ENTITY_TYPE_NAME + EsmUtils.OPERATION_SEPARATOR + STATIC_OPERATION_NAME),
                        inputParameterType, outputParameterType, fault1Type, fault2Type).build())
                .build();

        final TransferObjectType unmappedTransferObjectType = newTransferObjectTypeBuilder().withName(UNMAPPED_TRANSFER_OBJECT_TYPE_NAME)
                .withOperations(parameterDecorator(newOperationBuilder()
                                .withName(STATIC_REFERENCING_OPERATION_NAME_IN_UNMAPPED_TRANSFER_OBJECT_TYPE)
                                .withModifier(OperationModifier.STATIC)
                                .withBound(true)
                                .withBinding(MODEL_NAME + EsmUtils.NAMESPACE_SEPARATOR + ENTITY_TYPE_NAME + EsmUtils.OPERATION_SEPARATOR + STATIC_OPERATION_NAME),
                        inputParameterType, outputParameterType, fault1Type, fault2Type).build())
                .withOperations(parameterDecorator(newOperationBuilder()
                                .withName(STATIC_OPERATION_NAME_IN_UNMAPPED_TRANSFER_OBJECT_TYPE)
                                .withModifier(OperationModifier.STATIC)
                                .withBody(body),
                        inputParameterType, outputParameterType, fault1Type, fault2Type).build())
                .build();

        final Model model = newModelBuilder().withName(MODEL_NAME).build();
        model.getElements().addAll(Arrays.asList(entityType, mappedTransferObjectType, unmappedTransferObjectType, inputParameterType, outputParameterType, fault1Type, fault2Type));

        esmModel.addContent(model);

        transform();

        final Optional<hu.blackbelt.judo.meta.psm.namespace.Model> psmModel = allPsm(hu.blackbelt.judo.meta.psm.namespace.Model.class)
                .filter(m -> MODEL_NAME.equals(m.getName())).findAny();
        assertTrue(psmModel.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.data.EntityType> e = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class)
                .filter(et -> ENTITY_TYPE_NAME.equals(et.getName()))
                .findAny();
        assertTrue(e.isPresent());
        assertTrue(e.get().getOperations().stream().anyMatch(o -> BOUND_OPERATION_NAME.equals(o.getName()) && EXPECTED_INPUT_AND_OUTPUT_PARAMETERS.test(o)));

        final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> m = allPsm(hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class)
                .filter(t -> MAPPED_TRANSFER_OBJECT_TYPE_NAME.equals(t.getName()))
                .findAny();
        assertTrue(m.isPresent());
        assertTrue(m.get().getOperations().stream().anyMatch(o -> BOUND_OPERATION_NAME_IN_MAPPED_TRANSFER_OBJECT_TYPE.equals(o.getName()) && EXPECTED_INPUT_AND_OUTPUT_PARAMETERS.test(o)));
        assertTrue(m.get().getOperations().stream().anyMatch(o -> STATIC_OPERATION_NAME_IN_MAPPED_TRANSFER_OBJECT_TYPE.equals(o.getName()) && EXPECTED_INPUT_AND_OUTPUT_PARAMETERS.test(o)));

        final Optional<hu.blackbelt.judo.meta.psm.service.UnmappedTransferObjectType> u = allPsm(hu.blackbelt.judo.meta.psm.service.UnmappedTransferObjectType.class)
                .filter(t -> UNMAPPED_TRANSFER_OBJECT_TYPE_NAME.equals(t.getName()))
                .findAny();
        assertTrue(u.isPresent());
        assertTrue(u.get().getOperations().stream().anyMatch(o -> STATIC_OPERATION_NAME_IN_UNMAPPED_TRANSFER_OBJECT_TYPE.equals(o.getName()) && EXPECTED_INPUT_AND_OUTPUT_PARAMETERS.test(o)));
        assertTrue(u.get().getOperations().stream().anyMatch(o -> STATIC_REFERENCING_OPERATION_NAME_IN_UNMAPPED_TRANSFER_OBJECT_TYPE.equals(o.getName()) && EXPECTED_INPUT_AND_OUTPUT_PARAMETERS.test(o)));

        final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> detaultE = allPsm(hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class)
                .filter(t -> ENTITY_TYPE_NAME.equals(t.getName()))
                .findAny();
        assertTrue(detaultE.isPresent());
        assertTrue(detaultE.get().getOperations().stream().anyMatch(o -> BOUND_OPERATION_NAME.equals(o.getName()) && EXPECTED_INPUT_AND_OUTPUT_PARAMETERS.test(o)));
        assertTrue(detaultE.get().getOperations().stream().anyMatch(o -> STATIC_OPERATION_NAME.equals(o.getName()) && EXPECTED_INPUT_AND_OUTPUT_PARAMETERS.test(o)));
    }

    @Test
    void testGeneratingBehaviourOfTransferObjectRelations() throws Exception {
        testName = "testGeneratingBehaviourOfTransferObjectRelations";

        final String MODEL_NAME = "Model";
        final String ENTITY_TYPE_D_NAME = "D";
        final String ENTITY_TYPE_E_NAME = "E";
        final String ENTITY_TYPE_F_NAME = "F";
        final String RELATION_NAME_FROM_D_TO_E = "e";
        final String SINGLE_CONTAINMENT_RELATION_NAME = "singleContainment";
        final String MULTIPLE_CONTAINMENT_RELATION_NAME = "multipleContainment";
        final String SINGLE_REFERENCE_RELATION_NAME = "singleReference";
        final String MULTIPLE_REFERENCE_RELATION_NAME = "multipleReference";
        final String EXPOSED_GRAPH_NAME = "g";
        final String ACCESS_POINT_NAME = "AP";

        final int LOWER = 2;
        final int UPPER = 5;

        final String NAME_OF_GET_OPERATION = "_getGForAP";

        final String NAME_OF_GET_E_OPERATION = "_getE";

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

        final EntityType entityTypeD = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_D_NAME)
                .withRelations(newOneWayRelationMemberBuilder()
                        .withName(RELATION_NAME_FROM_D_TO_E)
                        .withTarget(entityTypeE)
                        .withLower(LOWER)
                        .withUpper(UPPER)
                        .withContainment(true)
                        .withAggregation(true)
                        .build())
                .build();
        entityTypeD.setMapping(newMappingBuilder().withTarget(entityTypeD).build());

        final AccessPoint accessPoint = newAccessPointBuilder()
                .withName(ACCESS_POINT_NAME)
                .withExposedGraphs(newExposedGraphBuilder()
                        .withName(EXPOSED_GRAPH_NAME)
                        .withTarget(entityTypeD)
                        .withGetterExpression(MODEL_NAME + EsmUtils.NAMESPACE_SEPARATOR + ENTITY_TYPE_D_NAME)
                        .withLower(0)
                        .withUpper(-1)
                        .withCreateable(false).withUpdateable(false).withDeleteable(false)
                        .build())
                .build();

        final Model model = newModelBuilder().withName(MODEL_NAME)
                .withElements(Arrays.asList(entityTypeD, entityTypeE, entityTypeF, accessPoint)).build();

        esmModel.addContent(model);

        transform();

        final Optional<hu.blackbelt.judo.meta.psm.accesspoint.AccessPoint> ap = allPsm(hu.blackbelt.judo.meta.psm.accesspoint.AccessPoint.class).findAny();
        assertTrue(ap.isPresent());

        final Optional<ExposedGraph> graph = ap.get().getExposedGraphs().stream().filter(g -> EXPOSED_GRAPH_NAME.equals(g.getName())).findAny();
        assertTrue(graph.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> defaultD = allPsm(MappedTransferObjectType.class)
                .filter(t -> ENTITY_TYPE_D_NAME.equals(t.getName()))
                .findAny();
        assertTrue(defaultD.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> defaultE = allPsm(MappedTransferObjectType.class)
                .filter(t -> ENTITY_TYPE_E_NAME.equals(t.getName()))
                .findAny();
        assertTrue(defaultE.isPresent());

        final Optional<TransferObjectRelation> dToE = defaultD.get().getRelations().stream().filter(r -> RELATION_NAME_FROM_D_TO_E.equals(r.getName())).findAny();
        assertTrue(dToE.isPresent());

        final Optional<TransferOperation> getG = defaultD.get().getOperations().stream().filter(o -> NAME_OF_GET_OPERATION.equals(o.getName())).findAny();
        assertTrue(getG.isPresent());
        assertNotNull(getG.get().getOutput());

        assertTrue(graph.get().getMappedTransferObjectType().getOperations().stream().anyMatch(o -> NAME_OF_GET_E_OPERATION.equals(o.getName()) && (o instanceof BoundTransferOperation) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.GET_RELATION && EcoreUtil.equals(o.getBehaviour().getOwner(), dToE.get()) &&
                o.getInput() == null && o.getOutput() != null && o.getFaults().isEmpty() &&
                o.getOutput().getCardinality().getLower() == LOWER && o.getOutput().getCardinality().getUpper() == UPPER &&
                EcoreUtil.equals(o.getOutput().getType(), defaultE.get())
        ));

        assertTrue(defaultD.get().getOperations().stream().anyMatch(o -> NAME_OF_GET_E_OPERATION.equals(o.getName())));

        log.debug("List of generated operations (D):{}", defaultD.get().getOperations().stream().map(o -> "\n - " + o.getName()).sorted().collect(Collectors.joining()));

        final ByteArrayOutputStream bos = new ByteArrayOutputStream();
        psmModel.savePsmModel(psmSaveArgumentsBuilder()
                .outputStream(bos)
                .build());
        log.info(bos.toString());

        assertEquals(2, defaultD.get().getOperations().size());
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
