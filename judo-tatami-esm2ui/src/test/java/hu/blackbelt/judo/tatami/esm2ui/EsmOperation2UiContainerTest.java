package hu.blackbelt.judo.tatami.esm2ui;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.accesspoint.ActorType;
import hu.blackbelt.judo.meta.esm.namespace.Model;
import hu.blackbelt.judo.meta.esm.operation.OperationModifier;
import hu.blackbelt.judo.meta.esm.operation.util.builder.OperationBuilder;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.esm.runtime.EsmUtils;
import hu.blackbelt.judo.meta.esm.structure.EntityType;
import hu.blackbelt.judo.meta.esm.structure.MemberType;
import hu.blackbelt.judo.meta.esm.structure.RelationKind;
import hu.blackbelt.judo.meta.esm.structure.TransferObjectType;
import hu.blackbelt.judo.meta.ui.runtime.UiUtils;
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
import java.util.function.Predicate;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static hu.blackbelt.judo.meta.esm.accesspoint.util.builder.AccesspointBuilders.newActorTypeBuilder;
import static hu.blackbelt.judo.meta.esm.namespace.util.builder.NamespaceBuilders.newModelBuilder;
import static hu.blackbelt.judo.meta.esm.operation.util.builder.OperationBuilders.newOperationBuilder;
import static hu.blackbelt.judo.meta.esm.operation.util.builder.OperationBuilders.newParameterBuilder;
import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.buildEsmModel;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.*;
import static hu.blackbelt.judo.meta.ui.runtime.UiEpsilonValidator.calculateUiValidationScriptURI;
import static hu.blackbelt.judo.meta.ui.runtime.UiEpsilonValidator.validateUi;
import static hu.blackbelt.judo.meta.ui.runtime.UiModel.SaveArguments.uiSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.ui.runtime.UiModel.buildUiModel;
import static hu.blackbelt.judo.tatami.esm2ui.Esm2Ui.calculateEsm2UiTransformationScriptURI;
import static hu.blackbelt.judo.tatami.esm2ui.Esm2Ui.executeEsm2UiTransformation;
import static org.junit.jupiter.api.Assertions.*;

@Slf4j
public class EsmOperation2UiContainerTest {

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
    }

    private void transform() throws Exception {
        // Make transformation which returns the trace with the serialized URI's
        esm2UiTransformationTrace = executeEsm2UiTransformation(esmModel, "default", 12, uiModel, new Slf4jLog(log),
                calculateEsm2UiTransformationScriptURI());

        assertTrue(uiModel.isValid());
        validateUi(new Slf4jLog(log), uiModel, calculateUiValidationScriptURI());
    }

    private static final String MODEL_NAME = "Model";
    private static final String ENTITY_TYPE_NAME = "E";
    private static final String ABSTRACT_ENTITY_TYPE_NAME = "A";
    private static final String MAPPED_TRANSFER_OBJECT_TYPE_NAME = "M";
    private static final String UNMAPPED_TRANSFER_OBJECT_TYPE_NAME = "U";

    private static final String BOUND_OPERATION_NAME = "boundOperation";
    private static final String ABSTRACT_OPERATION_NAME_WITH_BODY = "abstractOperation1";
    private static final String ABSTRACT_OPERATION_NAME_WITH_CUSTOMIMPLEMENTATION = "abstractOperation2";
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

    /*
    private static final Predicate<OperationDeclaration> EXPECTED_INPUT_AND_OUTPUT_PARAMETERS = o -> o.getInput() != null && o.getOutput() != null &&
            INPUT_PARAMETER_NAME.equals(o.getInput().getName()) &&
            (MODEL_NAME + UiUtils.NAMESPACE_SEPARATOR + INPUT_PARAMETER_TYPE_NAME).equals(UiUtils.namespaceElementToString(o.getInput().getType())) &&
            o.getInput().getCardinality() != null && o.getInput().getCardinality().getLower() == INPUT_PARAMETER_CARDINALITY_LOWER && o.getInput().getCardinality().getUpper() == INPUT_PARAMETER_CARDINALITY_UPPER &&
            OUTPUT_PARAMETER_NAME.equals(o.getOutput().getName()) &&
            (MODEL_NAME + UiUtils.NAMESPACE_SEPARATOR + OUTPUT_PARAMETER_TYPE_NAME).equals(UiUtils.namespaceElementToString(o.getOutput().getType())) &&
            o.getOutput().getCardinality() != null && o.getOutput().getCardinality().getLower() == OUTPUT_PARAMETER_CARDINALITY_LOWER && o.getOutput().getCardinality().getUpper() == OUTPUT_PARAMETER_CARDINALITY_UPPER;
	*/

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
                                .withLower(1).withUpper(1)
                                .withTarget(fault1Type)
                                .build(),
                        newParameterBuilder()
                                .withName(FAULT2_NAME)
                                .withLower(1).withUpper(1)
                                .withTarget(fault2Type)
                                .build()));
    }

    /*
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
                                .withBinding("")
                                .withBody(body),
                        inputParameterType, outputParameterType, fault1Type, fault2Type).build())
                .withOperations(parameterDecorator(newOperationBuilder()
                                .withName(STATIC_OPERATION_NAME)
                                .withModifier(OperationModifier.STATIC)
                                .withBinding("")
                                .withBody(body),
                        inputParameterType, outputParameterType, fault1Type, fault2Type).build())
                .build();
        entityType.setMapping(newMappingBuilder().withTarget(entityType).build());

        final EntityType abstractEntityType = newEntityTypeBuilder().withName(ABSTRACT_ENTITY_TYPE_NAME).withAbstract_(true)
                .withOperations(newOperationBuilder()
                        .withName(ABSTRACT_OPERATION_NAME_WITH_BODY)
                        .withBinding("")
                        .withBody(body)
                        .withModifier(OperationModifier.ABSTRACT)
                        .build())
                .withOperations(newOperationBuilder()
                        .withName(ABSTRACT_OPERATION_NAME_WITH_CUSTOMIMPLEMENTATION)
                        .withBinding("")
                        .withCustomImplementation(true)
                        .withModifier(OperationModifier.ABSTRACT)
                        .build())
                .build();
        abstractEntityType.setMapping(newMappingBuilder().withTarget(abstractEntityType).build());

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
                                .withBinding("")
                                .withBound(true)
                                .withBinding(MODEL_NAME + EsmUtils.NAMESPACE_SEPARATOR + ENTITY_TYPE_NAME + EsmUtils.OPERATION_SEPARATOR + STATIC_OPERATION_NAME),
                        inputParameterType, outputParameterType, fault1Type, fault2Type).build())
                .build();

        final TransferObjectType unmappedTransferObjectType = newTransferObjectTypeBuilder().withName(UNMAPPED_TRANSFER_OBJECT_TYPE_NAME)
                .withOperations(parameterDecorator(newOperationBuilder()
                                .withName(STATIC_REFERENCING_OPERATION_NAME_IN_UNMAPPED_TRANSFER_OBJECT_TYPE)
                                .withModifier(OperationModifier.STATIC)
                                .withBinding("")
                                .withBound(true)
                                .withBinding(MODEL_NAME + EsmUtils.NAMESPACE_SEPARATOR + ENTITY_TYPE_NAME + EsmUtils.OPERATION_SEPARATOR + STATIC_OPERATION_NAME),
                        inputParameterType, outputParameterType, fault1Type, fault2Type).build())
                .withOperations(parameterDecorator(newOperationBuilder()
                                .withName(STATIC_OPERATION_NAME_IN_UNMAPPED_TRANSFER_OBJECT_TYPE)
                                .withModifier(OperationModifier.STATIC)
                                .withBinding("")
                                .withBody(body),
                        inputParameterType, outputParameterType, fault1Type, fault2Type).build())
                .build();

        final Model model = newModelBuilder().withName(MODEL_NAME).build();
        model.getElements().addAll(Arrays.asList(entityType, abstractEntityType, mappedTransferObjectType, unmappedTransferObjectType, inputParameterType, outputParameterType, fault1Type, fault2Type));

        esmModel.addContent(model);

        transform();
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

        final int LOWER = 2;
        final int UPPER = 5;

        final String NAME_OF_GET_OPERATION = "_getG";

        final String NAME_OF_GET_E_OPERATION = "_getE";
        final String NAME_OF_CREATE_E_OPERATION = "_createE";
        final String NAME_OF_UPDATE_E_OPERATION = "_updateE";
        final String NAME_OF_DELETE_E_OPERATION = "_deleteE";

        final String NAME_OF_UNSET_SINGLE_CONTAINMENT_OPERATION = "_unsetSingleContainmentOfE";

        final String NAME_OF_SET_SINGLE_REFERENCE_OPERATION = "_setSingleReferenceOfE";
        final String NAME_OF_UNSET_SINGLE_REFERENCE_OPERATION = "_unsetSingleReferenceOfE";

        final String NAME_OF_REMOVE_ALL_MULTIPLE_CONTAINMENT_OPERATION = "_removeMultipleContainmentFromE";

        final String NAME_OF_SET_MULTIPLE_REFERENCE_OPERATION = "_setMultipleReferenceOfE";
        final String NAME_OF_ADD_ALL_MULTIPLE_REFERENCE_OPERATION = "_addMultipleReferenceToE";
        final String NAME_OF_REMOVE_ALL_MULTIPLE_REFERENCE_OPERATION = "_removeMultipleReferenceFromE";

//        final String NAME_OF_GET_RANGE_OF_SINGLE_REFERENCE_TO_CREATE = "_getRangeOfSingleReferenceToCreateE";
//        final String NAME_OF_GET_RANGE_OF_MULTIPLE_REFERENCE_TO_CREATE = "_getRangeOfMultipleReferenceToCreateE";
//        final String NAME_OF_GET_RANGE_OF_SINGLE_REFERENCE_TO_UPDATE = "_getRangeOfSingleReferenceToUpdateE";
//        final String NAME_OF_GET_RANGE_OF_MULTIPLE_REFERENCE_TO_UPDATE = "_getRangeOfMultipleReferenceToUpdateE";

        final String NAME_OF_GET_E_OPERATION_ET = "_getEFor" + MODEL_NAME + "_" + ENTITY_TYPE_D_NAME;
        final String NAME_OF_CREATE_E_OPERATION_ET = "_createEFor" + MODEL_NAME + "_" + ENTITY_TYPE_D_NAME;
        final String NAME_OF_UPDATE_E_OPERATION_ET = "_updateEFor" + MODEL_NAME + "_" + ENTITY_TYPE_D_NAME;
        final String NAME_OF_DELETE_E_OPERATION_ET = "_deleteEFor" + MODEL_NAME + "_" + ENTITY_TYPE_D_NAME;

        final String NAME_OF_UNSET_SINGLE_CONTAINMENT_OPERATION_ET = "_unsetSingleContainmentOfEFor" + MODEL_NAME + "_" + ENTITY_TYPE_D_NAME;

        final String NAME_OF_SET_SINGLE_REFERENCE_OPERATION_ET = "_setSingleReferenceOfEFor" + MODEL_NAME + "_" + ENTITY_TYPE_D_NAME;
        final String NAME_OF_UNSET_SINGLE_REFERENCE_OPERATION_ET = "_unsetSingleReferenceOfEFor" + MODEL_NAME + "_" + ENTITY_TYPE_D_NAME;

        final String NAME_OF_REMOVE_ALL_MULTIPLE_CONTAINMENT_OPERATION_ET = "_removeMultipleContainmentFromEFor" + MODEL_NAME + "_" + ENTITY_TYPE_D_NAME;

        final String NAME_OF_SET_MULTIPLE_REFERENCE_OPERATION_ET = "_setMultipleReferenceOfEFor" + MODEL_NAME + "_" + ENTITY_TYPE_D_NAME;
        final String NAME_OF_ADD_ALL_MULTIPLE_REFERENCE_OPERATION_ET = "_addMultipleReferenceToEFor" + MODEL_NAME + "_" + ENTITY_TYPE_D_NAME;
        final String NAME_OF_REMOVE_ALL_MULTIPLE_REFERENCE_OPERATION_ET = "_removeMultipleReferenceFromEFor" + MODEL_NAME + "_" + ENTITY_TYPE_D_NAME;

//        final String NAME_OF_GET_RANGE_OF_SINGLE_REFERENCE_TO_CREATE_ET = "_getRangeOfSingleReferenceToCreateEFor" + MODEL_NAME + "_" + ENTITY_TYPE_D_NAME;
//        final String NAME_OF_GET_RANGE_OF_MULTIPLE_REFERENCE_TO_CREATE_ET = "_getRangeOfMultipleReferenceToCreateEFor" + MODEL_NAME + "_" + ENTITY_TYPE_D_NAME;
//        final String NAME_OF_GET_RANGE_OF_SINGLE_REFERENCE_TO_UPDATE_ET = "_getRangeOfSingleReferenceToUpdateEFor" + MODEL_NAME + "_" + ENTITY_TYPE_D_NAME;
//        final String NAME_OF_GET_RANGE_OF_MULTIPLE_REFERENCE_TO_UPDATE_ET = "_getRangeOfMultipleReferenceToUpdateEFor" + MODEL_NAME + "_" + ENTITY_TYPE_D_NAME;

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

        final EntityType entityTypeD = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_D_NAME)
                .withRelations(newOneWayRelationMemberBuilder()
                        .withName(RELATION_NAME_FROM_D_TO_E)
                        .withTarget(entityTypeE)
                        .withLower(LOWER)
                        .withUpper(UPPER)
                        .withRelationKind(RelationKind.AGGREGATION)
                        .withCreateable(true).withUpdateable(true).withDeleteable(true)
                        .build())
                .build();
        entityTypeD.setMapping(newMappingBuilder().withTarget(entityTypeD).build());

        final ActorType actorType = newActorTypeBuilder().build();
        entityTypeD.setActorType(actorType);

        final Model model = newModelBuilder().withName(MODEL_NAME)
                .withElements(Arrays.asList(entityTypeD, entityTypeE, entityTypeF)).build();

        esmModel.addContent(model);

        transform();
    }

    @Test
    void testInitializerFlag() throws Exception {
        testName = "TestInitializerFlag";

        // FIXME - replace test case (using ESM initializer flag instead of type and operation names)
        final TransferObjectType initializer1 = newTransferObjectTypeBuilder()
                .withName("Initializer1")
                .withOperations(newOperationBuilder()
                        .withName("run")
                        .withModifier(OperationModifier.STATIC)
                        .withBody("// TODO")
                        .withBinding("")
                        .withInitializer(true)
                        .build())
                .build();

        final TransferObjectType initializer2 = newTransferObjectTypeBuilder()
                .withName("Initializer2")
                .withOperations(newOperationBuilder()
                        .withName("run")
                        .withModifier(OperationModifier.STATIC)
                        .withBody("// TODO")
                        .withBinding("")
                        .withInitializer(true)
                        .build())
                .build();

        final TransferObjectType nonInitializer1 = newTransferObjectTypeBuilder()
                .withName("NonInitializer1")
                .withOperations(newOperationBuilder()
                        .withName("run")
                        .withModifier(OperationModifier.STATIC)
                        .withBody("// TODO")
                        .withBinding("")
                        .build())
                .build();

        final TransferObjectType nonInitializer2 = newTransferObjectTypeBuilder()
                .withName("NonInitializer2")
                .withOperations(newOperationBuilder()
                        .withName("run")
                        .withModifier(OperationModifier.STATIC)
                        .withBody("// TODO")
                        .withBinding("")
                        .build())
                .build();

        final Model model = newModelBuilder().withName(MODEL_NAME)
                .withElements(Arrays.asList(initializer1, initializer2, nonInitializer1, nonInitializer2))
                .build();

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
