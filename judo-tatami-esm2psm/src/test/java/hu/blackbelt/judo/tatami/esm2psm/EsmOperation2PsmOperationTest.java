package hu.blackbelt.judo.tatami.esm2psm;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.accesspoint.ClaimType;
import hu.blackbelt.judo.meta.esm.namespace.Model;
import hu.blackbelt.judo.meta.esm.operation.OperationType;
import hu.blackbelt.judo.meta.esm.operation.util.builder.OperationBuilder;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.esm.runtime.EsmUtils;
import hu.blackbelt.judo.meta.esm.structure.DataMember;
import hu.blackbelt.judo.meta.esm.structure.EntityType;
import hu.blackbelt.judo.meta.esm.structure.MemberType;
import hu.blackbelt.judo.meta.esm.structure.RelationKind;
import hu.blackbelt.judo.meta.esm.structure.TransferObjectType;
import hu.blackbelt.judo.meta.esm.type.NumericType;
import hu.blackbelt.judo.meta.psm.PsmUtils;
import hu.blackbelt.judo.meta.psm.namespace.Package;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.psm.service.*;
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

import static hu.blackbelt.judo.meta.esm.accesspoint.util.builder.AccesspointBuilders.*;
import static hu.blackbelt.judo.meta.esm.namespace.util.builder.NamespaceBuilders.newModelBuilder;
import static hu.blackbelt.judo.meta.esm.operation.util.builder.OperationBuilders.newOperationBuilder;
import static hu.blackbelt.judo.meta.esm.operation.util.builder.OperationBuilders.newParameterBuilder;
import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.buildEsmModel;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.*;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newNumericTypeBuilder;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.calculatePsmValidationScriptURI;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.validatePsm;
import static hu.blackbelt.judo.meta.esm.runtime.EsmEpsilonValidator.validateEsm;
import static hu.blackbelt.judo.meta.esm.runtime.EsmEpsilonValidator.calculateEsmValidationScriptURI;
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
    	assertTrue(esmModel.isValid());
    	validateEsm(new Slf4jLog(log), esmModel, calculateEsmValidationScriptURI());
    	
        // Make transformation which returns the trace with the serialized URI's
        esm2PsmTransformationTrace = executeEsm2PsmTransformation(esmModel, psmModel, new Slf4jLog(log),
                calculateEsm2PsmTransformationScriptURI());

        assertTrue(psmModel.isValid());
        validatePsm(new Slf4jLog(log), psmModel, calculatePsmValidationScriptURI());
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
                .withFaults(newParameterBuilder()
                        .withName(FAULT1_NAME)
                        .withLower(1).withUpper(1)
                        .withTarget(fault1Type)
                        .build());
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
                                .withBinding("")
                                .withBody(body),
                        inputParameterType, outputParameterType, fault1Type, fault2Type).build())
                .withOperations(parameterDecorator(newOperationBuilder()
                                .withName(STATIC_OPERATION_NAME)
                                .withOperationType(OperationType.STATIC)
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
                        .withOperationType(OperationType.ABSTRACT)
                        .build())
                .withOperations(newOperationBuilder()
                        .withName(ABSTRACT_OPERATION_NAME_WITH_CUSTOMIMPLEMENTATION)
                        .withBinding("")
                        .withCustomImplementation(true)
                        .withOperationType(OperationType.ABSTRACT)
                        .build())
                .build();
        abstractEntityType.setMapping(newMappingBuilder().withTarget(abstractEntityType).build());

        final TransferObjectType mappedTransferObjectType = newTransferObjectTypeBuilder().withName(MAPPED_TRANSFER_OBJECT_TYPE_NAME)
                .withMapping(newMappingBuilder().withTarget(entityType).build())
                .withOperations(parameterDecorator(newOperationBuilder()
                                .withName(BOUND_OPERATION_NAME_IN_MAPPED_TRANSFER_OBJECT_TYPE)
                                .withOperationType(OperationType.MAPPED)
                                .withBinding(BOUND_OPERATION_NAME)
                                .withBody(""),
                        inputParameterType, outputParameterType, fault1Type, fault2Type).build())
                .build();
        
        final TransferObjectType unmappedTransferObjectType = newTransferObjectTypeBuilder().withName(UNMAPPED_TRANSFER_OBJECT_TYPE_NAME)
                .withOperations(parameterDecorator(newOperationBuilder()
                                .withName(STATIC_OPERATION_NAME_IN_UNMAPPED_TRANSFER_OBJECT_TYPE)
                                .withOperationType(OperationType.STATIC)
                                .withBinding("")
                                .withBody(body),
                        inputParameterType, outputParameterType, fault1Type, fault2Type).build())
                .build();

        final Model model = newModelBuilder().withName(MODEL_NAME).build();
        
        model.getElements().addAll(Arrays.asList(entityType, abstractEntityType, mappedTransferObjectType, unmappedTransferObjectType, inputParameterType, outputParameterType, fault1Type, fault2Type));

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
        
        final Optional<hu.blackbelt.judo.meta.psm.data.EntityType> a = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class)
                .filter(et -> ABSTRACT_ENTITY_TYPE_NAME.equals(et.getName()))
                .findAny();
        assertTrue(a.isPresent());
        assertTrue(a.get().getOperations().stream().anyMatch(o -> ABSTRACT_OPERATION_NAME_WITH_BODY.equals(o.getName()) && o.getImplementation() == null));
        assertTrue(a.get().getOperations().stream().anyMatch(o -> ABSTRACT_OPERATION_NAME_WITH_CUSTOMIMPLEMENTATION.equals(o.getName()) && o.getImplementation() == null));

        final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> m = allPsm(hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class)
                .filter(t -> MAPPED_TRANSFER_OBJECT_TYPE_NAME.equals(t.getName()))
                .findAny();
        assertTrue(m.isPresent());
        assertTrue(m.get().getOperations().stream().anyMatch(o -> BOUND_OPERATION_NAME_IN_MAPPED_TRANSFER_OBJECT_TYPE.equals(o.getName()) && EXPECTED_INPUT_AND_OUTPUT_PARAMETERS.test(o)));

        final Optional<hu.blackbelt.judo.meta.psm.service.UnmappedTransferObjectType> u = allPsm(hu.blackbelt.judo.meta.psm.service.UnmappedTransferObjectType.class)
                .filter(t -> UNMAPPED_TRANSFER_OBJECT_TYPE_NAME.equals(t.getName()))
                .findAny();
        assertTrue(u.isPresent());
        assertTrue(u.get().getOperations().stream().anyMatch(o -> STATIC_OPERATION_NAME_IN_UNMAPPED_TRANSFER_OBJECT_TYPE.equals(o.getName()) && EXPECTED_INPUT_AND_OUTPUT_PARAMETERS.test(o)));

        final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> detaultE = allPsm(hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class)
                .filter(t -> ENTITY_TYPE_NAME.equals(t.getName()) && !t.isOptional())
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

        final int LOWER = 2;
        final int UPPER = 5;

        final String NAME_OF_REFRESH_E_OPERATION = "_refreshInstance" + MODEL_NAME + "_" + ENTITY_TYPE_E_NAME;
        final String NAME_OF_REFRESH_E_OPERATION_ET = "_refreshInstanceFor" + MODEL_NAME + "_" + ENTITY_TYPE_E_NAME;
        final String NAME_OF_UPDATE_INSTANCE_E_OPERATION = "_updateInstance" + MODEL_NAME + "_" + ENTITY_TYPE_E_NAME;
        final String NAME_OF_UPDATE_INSTANCE_E_OPERATION_ET = "_updateInstanceFor" + MODEL_NAME + "_" + ENTITY_TYPE_E_NAME;
        final String NAME_OF_VALIDATE_UPDATE_E_OPERATION = "_validateUpdateInstance" + MODEL_NAME + "_" + ENTITY_TYPE_E_NAME;
        final String NAME_OF_VALIDATE_UPDATE_E_OPERATION_ET = "_validateUpdateInstanceFor" + MODEL_NAME + "_" + ENTITY_TYPE_E_NAME;
        final String NAME_OF_DELETE_INSTANCE_E_OPERATION = "_deleteInstance" + MODEL_NAME + "_" + ENTITY_TYPE_E_NAME;
        final String NAME_OF_DELETE_INSTANCE_E_OPERATION_ET = "_deleteInstanceFor" + MODEL_NAME + "_" + ENTITY_TYPE_E_NAME;
        final String NAME_OF_SET_REFERENCE_SINGLE_REFERENCE_OPERATION = "_setReferenceSingleReference";
        final String NAME_OF_SET_REFERENCE_SINGLE_REFERENCE_OPERATION_ET = "_setReferenceSingleReferenceFor" + MODEL_NAME + "_" + ENTITY_TYPE_E_NAME;
        final String NAME_OF_UNSET_REFERENCE_SINGLE_REFERENCE_OPERATION = "_unsetReferenceSingleReference";
        final String NAME_OF_UNSET_REFERENCE_SINGLE_REFERENCE_OPERATION_ET = "_unsetReferenceSingleReferenceFor" + MODEL_NAME + "_" + ENTITY_TYPE_E_NAME;
        final String NAME_OF_ADD_REFERENCE_SINGLE_REFERENCE_OPERATION = "_addReferenceSingleReference";
        final String NAME_OF_REMOVE_REFERENCE_SINGLE_REFERENCE_OPERATION = "_removeReferenceSingleReference";
        final String NAME_OF_SET_REFERENCE_MULTIPLE_REFERENCE_OPERATION = "_setReferenceMultipleReference";
        final String NAME_OF_SET_REFERENCE_MULTIPLE_REFERENCE_OPERATION_ET = "_setReferenceMultipleReferenceFor" + MODEL_NAME + "_" + ENTITY_TYPE_E_NAME;
        final String NAME_OF_UNSET_REFERENCE_MULTIPLE_REFERENCE_OPERATION = "_unsetReferenceMultipleReference";
        final String NAME_OF_ADD_REFERENCE_MULTIPLE_REFERENCE_OPERATION = "_addReferenceMultipleReference";
        final String NAME_OF_ADD_REFERENCE_MULTIPLE_REFERENCE_OPERATION_ET = "_addReferenceMultipleReferenceFor" + MODEL_NAME + "_" + ENTITY_TYPE_E_NAME;
        final String NAME_OF_REMOVE_REFERENCE_MULTIPLE_REFERENCE_OPERATION = "_removeReferenceMultipleReference";
        final String NAME_OF_REMOVE_REFERENCE_MULTIPLE_REFERENCE_OPERATION_ET = "_removeReferenceMultipleReferenceFor" + MODEL_NAME + "_" + ENTITY_TYPE_E_NAME;
        final String NAME_OF_SET_REFERENCE_SINGLE_CONTAINMENT_OPERATION = "_setReferenceSingleContainment";
        final String NAME_OF_UNSET_REFERENCE_SINGLE_CONTAINMENT_OPERATION = "_unsetReferenceSingleContainment";
        final String NAME_OF_ADD_REFERENCE_SINGLE_CONTAINMENT_OPERATION = "_addReferenceSingleContainment";
        final String NAME_OF_REMOVE_REFERENCE_SINGLE_CONTAINMENT_OPERATION = "_removeReferenceSingleContainment";
        final String NAME_OF_SET_REFERENCE_MULTIPLE_CONTAINMENT_OPERATION = "_setReferenceMultipleContainment";
        final String NAME_OF_UNSET_REFERENCE_MULTIPLE_CONTAINMENT_OPERATION = "_unsetReferenceMultipleContainment";
        final String NAME_OF_ADD_REFERENCE_MULTIPLE_CONTAINMENT_OPERATION = "_addReferenceMultipleContainment";
        final String NAME_OF_REMOVE_REFERENCE_MULTIPLE_CONTAINMENT_OPERATION = "_removeReferenceMultipleContainment";

        final String NAME_OF_LIST_E_OPERATION = "_listE";
        final String NAME_OF_LIST_E_OPERATION_ET = "_listEFor" + MODEL_NAME + "_" + ENTITY_TYPE_D_NAME;
        final String NAME_OF_CREATE_INSTANCE_E_OPERATION = "_createInstanceE";
        final String NAME_OF_CREATE_INSTANCE_E_OPERATION_ET = "_createInstanceEFor" + MODEL_NAME + "_" + ENTITY_TYPE_D_NAME;
        final String NAME_OF_VALIDATE_CREATE_E_OPERATION = "_validateCreateInstanceE";
        final String NAME_OF_VALIDATE_CREATE_E_OPERATION_ET = "_validateCreateInstanceEFor" + MODEL_NAME + "_" + ENTITY_TYPE_D_NAME;
        final String NAME_OF_REFRESH_D_OPERATION = "_refreshInstance" + MODEL_NAME + "_" + ENTITY_TYPE_D_NAME;
        final String NAME_OF_REFRESH_D_OPERATION_ET = "_refreshInstanceFor" + MODEL_NAME + "_" + ENTITY_TYPE_D_NAME;
        final String NAME_OF_UPDATE_INSTANCE_D_OPERATION = "_updateInstance" + MODEL_NAME + "_" + ENTITY_TYPE_D_NAME;
        final String NAME_OF_UPDATE_INSTANCE_D_OPERATION_ET = "_updateInstanceFor" + MODEL_NAME + "_" + ENTITY_TYPE_D_NAME;
        final String NAME_OF_VALIDATE_UPDATE_D_OPERATION = "_validateUpdateInstance" + MODEL_NAME + "_" + ENTITY_TYPE_D_NAME;
        final String NAME_OF_VALIDATE_UPDATE_D_OPERATION_ET = "_validateUpdateInstanceFor" + MODEL_NAME + "_" + ENTITY_TYPE_D_NAME;
        final String NAME_OF_DELETE_INSTANCE_D_OPERATION = "_deleteInstance" + MODEL_NAME + "_" + ENTITY_TYPE_D_NAME;
        final String NAME_OF_DELETE_INSTANCE_D_OPERATION_ET = "_deleteInstanceFor" + MODEL_NAME + "_" + ENTITY_TYPE_D_NAME;
        final String NAME_OF_SET_REFERENCE_E_OPERATION = "_setReferenceE";
        final String NAME_OF_SET_REFERENCE_E_OPERATION_ET = "_setReferenceEFor" + MODEL_NAME + "_" + ENTITY_TYPE_D_NAME;
        final String NAME_OF_UNSET_REFERENCE_E_OPERATION = "_unsetReferenceE";
        final String NAME_OF_ADD_REFERENCE_E_OPERATION = "_addReferenceE";
        final String NAME_OF_ADD_REFERENCE_E_OPERATION_ET = "_addReferenceEFor" + MODEL_NAME + "_" + ENTITY_TYPE_D_NAME;
        final String NAME_OF_REMOVE_REFERENCE_E_OPERATION = "_removeReferenceE";
        final String NAME_OF_REMOVE_REFERENCE_E_OPERATION_ET = "_removeReferenceEFor" + MODEL_NAME + "_" + ENTITY_TYPE_D_NAME;
        final String NAME_OF_GET_RANGE_E_OPERATION = "_getRangeReferenceE";
        final String NAME_OF_GET_TEMPLATE_D_OPERATION = "_getTemplateD";

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
                        .withRangeExpression(MODEL_NAME + EsmUtils.NAMESPACE_SEPARATOR + ENTITY_TYPE_E_NAME)
                        .build())
                .build();
        entityTypeD.setMapping(newMappingBuilder().withTarget(entityTypeD).build());

        NumericType numeric = newNumericTypeBuilder().withName("numeric").withPrecision(2).withScale(1).build();
        DataMember id = newDataMemberBuilder().withName("id").withDataType(numeric).withMemberType(MemberType.STORED).withRequired(true).withIdentifier(true).build();
    	id.setBinding(id);
    	entityTypeD.getAttributes().add(id);
    	
        hu.blackbelt.judo.meta.esm.accesspoint.ActorType actor = newActorTypeBuilder().withName("actor")
        		.withClaims(newClaimBuilder().withAttribute(id).withClaimType(ClaimType.USERNAME).build())
        		.withAnonymous(false)
        		.withRealm("sandbox")
        		.withPrincipal(entityTypeD)
        		.withManaged(false)
                .withAccesses(newAccessBuilder()
                        .withName("dList")
                        .withTarget(entityTypeD)
                        .withLower(0).withUpper(-1)
                        .withCreateable(true)
                        .withUpdateable(true)
                        .withDeleteable(true)
                        .withTargetDefinedCRUD(false)
                        .build())
                .build();
    	useTransferObjectType(entityTypeD).withActorType(actor).build();

        final Model model = newModelBuilder().withName(MODEL_NAME)
                .withElements(Arrays.asList(entityTypeD, entityTypeE, entityTypeF, numeric, actor)).build();

        esmModel.addContent(model);

        transform();

        final Optional<hu.blackbelt.judo.meta.psm.service.TransferObjectType> ap = allPsm(hu.blackbelt.judo.meta.psm.service.TransferObjectType.class)
                .filter(t -> t.isAccessPoint())
                .findAny();
        assertTrue(ap.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.data.EntityType> d = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class)
                .filter(t -> ENTITY_TYPE_D_NAME.equals(t.getName()))
                .findAny();
        assertTrue(d.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.data.EntityType> e = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class)
                .filter(t -> ENTITY_TYPE_E_NAME.equals(t.getName()))
                .findAny();
        assertTrue(e.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> defaultD = allPsm(MappedTransferObjectType.class)
                .filter(t -> ENTITY_TYPE_D_NAME.equals(t.getName()) && !t.isOptional())
                .findAny();
        assertTrue(defaultD.isPresent());

        log.debug("List of generated operations (D):{}", defaultD.get().getOperations().stream().map(o -> "\n - " + o.getName() + ": " + (o.getBehaviour() != null ? o.getBehaviour().getBehaviourType() : "-")).sorted().collect(Collectors.joining()));

        final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> defaultE = allPsm(MappedTransferObjectType.class)
                .filter(t -> ENTITY_TYPE_E_NAME.equals(t.getName()) && !t.isOptional())
                .findAny();
        assertTrue(defaultE.isPresent());
        
        final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> optionalD = allPsm(MappedTransferObjectType.class)
                .filter(t -> ENTITY_TYPE_D_NAME.equals(t.getName()) && t.isOptional())
                .findAny();
        assertTrue(defaultE.isPresent());

        final Optional<UnmappedTransferObjectType> getRangeInputType = allPsm(UnmappedTransferObjectType.class)
                .filter(t -> t.getName().equals("_GetRangeInputDE"))
                .findAny();
        assertTrue(getRangeInputType.isPresent());

        final Optional<TransferObjectRelation> getRangeInputOwner = getRangeInputType.get().getRelations().stream()
        		.filter(r -> r.getTarget().equals(optionalD.get())).findAny();
        assertTrue(getRangeInputOwner.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> defaultF = allPsm(MappedTransferObjectType.class)
                .filter(t -> ENTITY_TYPE_F_NAME.equals(t.getName()))
                .findAny();
        assertTrue(defaultF.isPresent());

        final Optional<TransferObjectRelation> dToE = defaultD.get().getRelations().stream().filter(r -> RELATION_NAME_FROM_D_TO_E.equals(r.getName())).findAny();
        assertTrue(dToE.isPresent());

        final Optional<TransferObjectRelation> defaultSingleContainment = defaultE.get().getRelations().stream().filter(r -> SINGLE_CONTAINMENT_RELATION_NAME.equals(r.getName())).findAny();
        final Optional<TransferObjectRelation> defaultMultipleContainment = defaultE.get().getRelations().stream().filter(r -> MULTIPLE_CONTAINMENT_RELATION_NAME.equals(r.getName())).findAny();
        final Optional<TransferObjectRelation> defaultSingleReference = defaultE.get().getRelations().stream().filter(r -> SINGLE_REFERENCE_RELATION_NAME.equals(r.getName())).findAny();
        final Optional<TransferObjectRelation> defaultMultipleReference = defaultE.get().getRelations().stream().filter(r -> MULTIPLE_REFERENCE_RELATION_NAME.equals(r.getName())).findAny();

        assertTrue(defaultSingleContainment.isPresent());
        assertTrue(defaultMultipleContainment.isPresent());
        assertTrue(defaultSingleReference.isPresent());
        assertTrue(defaultMultipleReference.isPresent());

        assertTrue(defaultD.get().getOperations().stream().anyMatch(o -> NAME_OF_LIST_E_OPERATION.equals(o.getName()) && (o instanceof BoundTransferOperation) &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInstanceRepresentation().getEntityType(), d.get()) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.LIST && EcoreUtil.equals(o.getBehaviour().getOwner(), dToE.get()) &&
                o.getInput() != null && o.getOutput() != null && o.getFaults().isEmpty() &&
                o.getInput().getCardinality().getLower() == 0 && o.getInput().getCardinality().getUpper() == 1 &&
                o.getInput().getType() != null && (o.getInput().getType() instanceof UnmappedTransferObjectType) &&
                o.getOutput().getCardinality().getLower() == LOWER && o.getOutput().getCardinality().getUpper() == UPPER &&
                EcoreUtil.equals(o.getOutput().getType(), defaultE.get()) &&
                NAME_OF_LIST_E_OPERATION_ET.equals(((BoundTransferOperation) o).getBinding().getName()) &&
                ((BoundTransferOperation) o).getBinding().getInput() != null && ((BoundTransferOperation) o).getBinding().getOutput() != null && ((BoundTransferOperation) o).getBinding().getFaults().isEmpty() &&
                ((BoundTransferOperation) o).getBinding().getOutput().getCardinality().getLower() == LOWER && o.getOutput().getCardinality().getUpper() == UPPER &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getOutput().getType(), defaultE.get())
        ));

        final Optional<TransferOperation> create2 = defaultD.get().getOperations().stream().filter(o -> NAME_OF_CREATE_INSTANCE_E_OPERATION.equals(o.getName()) && (o instanceof BoundTransferOperation) &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInstanceRepresentation().getEntityType(), d.get()) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.CREATE_INSTANCE && EcoreUtil.equals(o.getBehaviour().getOwner(), dToE.get()) &&
                o.getInput() != null && o.getOutput() != null && o.getFaults().isEmpty() &&
                o.getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getInput().getType(), defaultE.get()) &&
                o.getOutput().getCardinality().getLower() == 1 && o.getOutput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getOutput().getType(), defaultE.get()) &&
                NAME_OF_CREATE_INSTANCE_E_OPERATION_ET.equals(((BoundTransferOperation) o).getBinding().getName()) &&
                ((BoundTransferOperation) o).getBinding().getInput() != null && ((BoundTransferOperation) o).getBinding().getOutput() != null && ((BoundTransferOperation) o).getBinding().getFaults().isEmpty() &&
                ((BoundTransferOperation) o).getBinding().getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInput().getType(), defaultE.get()) &&
                ((BoundTransferOperation) o).getBinding().getOutput().getCardinality().getLower() == 1 && o.getOutput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getOutput().getType(), defaultE.get())
        ).findAny();

        assertTrue(create2.isPresent());

        assertTrue(defaultD.get().getOperations().stream().anyMatch(o -> NAME_OF_VALIDATE_CREATE_E_OPERATION.equals(o.getName()) && (o instanceof BoundTransferOperation) &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInstanceRepresentation().getEntityType(), d.get()) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.VALIDATE_CREATE && EcoreUtil.equals(o.getBehaviour().getOwner(), dToE.get()) &&
                o.getInput() != null && o.getOutput() != null && o.getFaults().isEmpty() &&
                o.getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getInput().getType(), defaultE.get()) &&
                o.getOutput().getCardinality().getLower() == 1 && o.getOutput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getOutput().getType(), defaultE.get()) &&
                NAME_OF_VALIDATE_CREATE_E_OPERATION_ET.equals(((BoundTransferOperation) o).getBinding().getName()) &&
                ((BoundTransferOperation) o).getBinding().getInput() != null && ((BoundTransferOperation) o).getBinding().getOutput() != null && ((BoundTransferOperation) o).getBinding().getFaults().isEmpty() &&
                ((BoundTransferOperation) o).getBinding().getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInput().getType(), defaultE.get()) &&
                ((BoundTransferOperation) o).getBinding().getOutput().getCardinality().getLower() == 1 && o.getOutput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getOutput().getType(), defaultE.get())
        ));

        assertTrue(defaultD.get().getOperations().stream().anyMatch(o -> NAME_OF_VALIDATE_CREATE_E_OPERATION.equals(o.getName()) && (o instanceof BoundTransferOperation) &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInstanceRepresentation().getEntityType(), d.get()) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.VALIDATE_CREATE && EcoreUtil.equals(o.getBehaviour().getOwner(), dToE.get()) &&
                o.getInput() != null && o.getOutput() != null && o.getFaults().isEmpty() &&
                o.getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getInput().getType(), defaultE.get()) &&
                o.getOutput().getCardinality().getLower() == 1 && o.getOutput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getOutput().getType(), defaultE.get()) &&
                NAME_OF_VALIDATE_CREATE_E_OPERATION_ET.equals(((BoundTransferOperation) o).getBinding().getName()) &&
                ((BoundTransferOperation) o).getBinding().getInput() != null && ((BoundTransferOperation) o).getBinding().getOutput() != null && ((BoundTransferOperation) o).getBinding().getFaults().isEmpty() &&
                ((BoundTransferOperation) o).getBinding().getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInput().getType(), defaultE.get()) &&
                ((BoundTransferOperation) o).getBinding().getOutput().getCardinality().getLower() == 1 && o.getOutput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getOutput().getType(), defaultE.get())
        ));

        assertTrue(defaultD.get().getOperations().stream().anyMatch(o -> NAME_OF_REFRESH_D_OPERATION.equals(o.getName()) && (o instanceof BoundTransferOperation) &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInstanceRepresentation().getEntityType(), d.get()) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.REFRESH && EcoreUtil.equals(o.getBehaviour().getOwner(), defaultD.get()) &&
                o.getInput() != null && o.getOutput() != null && o.getFaults().isEmpty() &&
                o.getInput().getCardinality().getLower() == 0 && o.getInput().getCardinality().getUpper() == 1 &&
                o.getOutput().getCardinality().getLower() == 1 && o.getOutput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getOutput().getType(), defaultD.get()) &&
                NAME_OF_REFRESH_D_OPERATION_ET.equals(((BoundTransferOperation) o).getBinding().getName()) &&
                ((BoundTransferOperation) o).getBinding().getInput() != null && ((BoundTransferOperation) o).getBinding().getOutput() != null && ((BoundTransferOperation) o).getBinding().getFaults().isEmpty() &&
                ((BoundTransferOperation) o).getBinding().getInput().getCardinality().getLower() == 0 && o.getInput().getCardinality().getUpper() == 1 &&
                ((BoundTransferOperation) o).getBinding().getOutput().getCardinality().getLower() == 1 && o.getOutput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getOutput().getType(), defaultD.get())
        ));

        assertTrue(defaultD.get().getOperations().stream().anyMatch(o -> NAME_OF_UPDATE_INSTANCE_D_OPERATION.equals(o.getName()) && (o instanceof BoundTransferOperation) &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInstanceRepresentation().getEntityType(), d.get()) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.UPDATE_INSTANCE && EcoreUtil.equals(o.getBehaviour().getOwner(), defaultD.get()) &&
                o.getInput() != null && o.getOutput() != null && o.getFaults().isEmpty() &&
                o.getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getInput().getType(), defaultD.get()) &&
                o.getOutput().getCardinality().getLower() == 1 && o.getOutput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getOutput().getType(), defaultD.get()) &&
                NAME_OF_UPDATE_INSTANCE_D_OPERATION_ET.equals(((BoundTransferOperation) o).getBinding().getName()) &&
                ((BoundTransferOperation) o).getBinding().getInput() != null && ((BoundTransferOperation) o).getBinding().getOutput() != null && ((BoundTransferOperation) o).getBinding().getFaults().isEmpty() &&
                ((BoundTransferOperation) o).getBinding().getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInput().getType(), defaultD.get()) &&
                ((BoundTransferOperation) o).getBinding().getOutput().getCardinality().getLower() == 1 && o.getOutput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getOutput().getType(), defaultD.get())
        ));

        assertTrue(defaultD.get().getOperations().stream().anyMatch(o -> NAME_OF_VALIDATE_UPDATE_D_OPERATION.equals(o.getName()) && (o instanceof BoundTransferOperation) &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInstanceRepresentation().getEntityType(), d.get()) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.VALIDATE_UPDATE && EcoreUtil.equals(o.getBehaviour().getOwner(), defaultD.get()) &&
                o.getInput() != null && o.getOutput() != null && o.getFaults().isEmpty() &&
                o.getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getInput().getType(), defaultD.get()) &&
                o.getOutput().getCardinality().getLower() == 1 && o.getOutput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getOutput().getType(), defaultD.get()) &&
                NAME_OF_VALIDATE_UPDATE_D_OPERATION_ET.equals(((BoundTransferOperation) o).getBinding().getName()) &&
                ((BoundTransferOperation) o).getBinding().getInput() != null && ((BoundTransferOperation) o).getBinding().getOutput() != null && ((BoundTransferOperation) o).getBinding().getFaults().isEmpty() &&
                ((BoundTransferOperation) o).getBinding().getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInput().getType(), defaultD.get()) &&
                ((BoundTransferOperation) o).getBinding().getOutput().getCardinality().getLower() == 1 && o.getOutput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getOutput().getType(), defaultD.get())
        ));

        assertTrue(defaultD.get().getOperations().stream().anyMatch(o -> NAME_OF_SET_REFERENCE_E_OPERATION.equals(o.getName()) && (o instanceof BoundTransferOperation) &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInstanceRepresentation().getEntityType(), d.get()) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.SET_REFERENCE && EcoreUtil.equals(o.getBehaviour().getOwner(), dToE.get()) &&
                o.getInput() != null && o.getOutput() == null && o.getFaults().isEmpty() &&
                o.getInput().getCardinality().getLower() == LOWER && o.getInput().getCardinality().getUpper() == UPPER &&
                EcoreUtil.equals(o.getInput().getType(), defaultE.get()) &&
                NAME_OF_SET_REFERENCE_E_OPERATION_ET.equals(((BoundTransferOperation) o).getBinding().getName()) &&
                ((BoundTransferOperation) o).getBinding().getInput() != null && ((BoundTransferOperation) o).getBinding().getOutput() == null && ((BoundTransferOperation) o).getBinding().getFaults().isEmpty() &&
                ((BoundTransferOperation) o).getBinding().getInput().getCardinality().getLower() == LOWER && o.getInput().getCardinality().getUpper() == UPPER &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInput().getType(), defaultE.get())
        ));

        assertFalse(defaultD.get().getOperations().stream().anyMatch(o -> NAME_OF_UNSET_REFERENCE_E_OPERATION.equals(o.getName())));

        assertTrue(defaultD.get().getOperations().stream().anyMatch(o -> NAME_OF_ADD_REFERENCE_E_OPERATION.equals(o.getName()) && (o instanceof BoundTransferOperation) &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInstanceRepresentation().getEntityType(), d.get()) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.ADD_REFERENCE && EcoreUtil.equals(o.getBehaviour().getOwner(), dToE.get()) &&
                o.getInput() != null && o.getOutput() == null && o.getFaults().isEmpty() &&
                o.getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == UPPER &&
                EcoreUtil.equals(o.getInput().getType(), defaultE.get()) &&
                NAME_OF_ADD_REFERENCE_E_OPERATION_ET.equals(((BoundTransferOperation) o).getBinding().getName()) &&
                ((BoundTransferOperation) o).getBinding().getInput() != null && ((BoundTransferOperation) o).getBinding().getOutput() == null && ((BoundTransferOperation) o).getBinding().getFaults().isEmpty() &&
                ((BoundTransferOperation) o).getBinding().getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == UPPER &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInput().getType(), defaultE.get())
        ));

        assertTrue(defaultD.get().getOperations().stream().anyMatch(o -> NAME_OF_REMOVE_REFERENCE_E_OPERATION.equals(o.getName()) && (o instanceof BoundTransferOperation) &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInstanceRepresentation().getEntityType(), d.get()) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.REMOVE_REFERENCE && EcoreUtil.equals(o.getBehaviour().getOwner(), dToE.get()) &&
                o.getInput() != null && o.getOutput() == null && o.getFaults().isEmpty() &&
                o.getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == -1 &&
                EcoreUtil.equals(o.getInput().getType(), defaultE.get()) &&
                NAME_OF_REMOVE_REFERENCE_E_OPERATION_ET.equals(((BoundTransferOperation) o).getBinding().getName()) &&
                ((BoundTransferOperation) o).getBinding().getInput() != null && ((BoundTransferOperation) o).getBinding().getOutput() == null && ((BoundTransferOperation) o).getBinding().getFaults().isEmpty() &&
                ((BoundTransferOperation) o).getBinding().getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == -1 &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInput().getType(), defaultE.get())
        ));

        assertTrue(defaultD.get().getOperations().stream().anyMatch(o -> NAME_OF_GET_RANGE_E_OPERATION.equals(o.getName()) && (o instanceof UnboundOperation) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.GET_RANGE && EcoreUtil.equals(o.getBehaviour().getOwner(), dToE.get()) &&
                o.getInput() != null && o.getOutput() != null && o.getFaults().isEmpty() &&
                o.getInput().getCardinality().getLower() == 0 && o.getInput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getInput().getType(), getRangeInputType.get()) &&
                o.getOutput().getCardinality().getLower() == 0 && o.getOutput().getCardinality().getUpper() == -1 &&
                EcoreUtil.equals(o.getOutput().getType(), defaultE.get())
        ));
        
        assertTrue(defaultD.get().getOperations().stream().anyMatch(o -> NAME_OF_GET_TEMPLATE_D_OPERATION.equals(o.getName()) && (o instanceof UnboundOperation) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.GET_TEMPLATE &&
                EcoreUtil.equals(o.getBehaviour().getOwner(), defaultD.get()) &&
                o.getInput() == null && o.getOutput() != null && o.getFaults().isEmpty() &&
                o.getOutput().getCardinality().getLower() == 0 && o.getOutput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getOutput().getType(), optionalD.get())
        ));

        assertEquals(10L, defaultD.get().getOperations().stream().filter(o -> o instanceof BoundTransferOperation).count());
        assertEquals(2L, defaultD.get().getOperations().stream().filter(o -> o instanceof UnboundOperation).count());

        assertTrue(defaultD.get().getOperations().stream().anyMatch(o -> NAME_OF_DELETE_INSTANCE_D_OPERATION.equals(o.getName()) && (o instanceof BoundTransferOperation) &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInstanceRepresentation().getEntityType(), d.get()) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.DELETE_INSTANCE && EcoreUtil.equals(o.getBehaviour().getOwner(), defaultD.get()) &&
                o.getInput() == null && o.getOutput() == null && o.getFaults().isEmpty() &&
                NAME_OF_DELETE_INSTANCE_D_OPERATION_ET.equals(((BoundTransferOperation) o).getBinding().getName()) &&
                ((BoundTransferOperation) o).getBinding().getInput() == null && ((BoundTransferOperation) o).getBinding().getOutput() == null && ((BoundTransferOperation) o).getBinding().getFaults().isEmpty()
        ));

        assertTrue(defaultE.get().getOperations().stream().anyMatch(o -> NAME_OF_REFRESH_E_OPERATION.equals(o.getName()) && (o instanceof BoundTransferOperation) &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInstanceRepresentation().getEntityType(), e.get()) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.REFRESH && EcoreUtil.equals(o.getBehaviour().getOwner(), defaultE.get()) &&
                o.getInput() != null && o.getOutput() != null && o.getFaults().isEmpty() &&
                o.getInput().getCardinality().getLower() == 0 && o.getInput().getCardinality().getUpper() == 1 &&
                o.getOutput().getCardinality().getLower() == 1 && o.getOutput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getOutput().getType(), defaultE.get()) &&
                NAME_OF_REFRESH_E_OPERATION_ET.equals(((BoundTransferOperation) o).getBinding().getName()) &&
                ((BoundTransferOperation) o).getBinding().getInput() != null && ((BoundTransferOperation) o).getBinding().getOutput() != null && ((BoundTransferOperation) o).getBinding().getFaults().isEmpty() &&
                ((BoundTransferOperation) o).getBinding().getInput().getCardinality().getLower() == 0 && o.getInput().getCardinality().getUpper() == 1 &&
                ((BoundTransferOperation) o).getBinding().getOutput().getCardinality().getLower() == 1 && o.getOutput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getOutput().getType(), defaultE.get())
        ));

        assertTrue(defaultE.get().getOperations().stream().anyMatch(o -> NAME_OF_UPDATE_INSTANCE_E_OPERATION.equals(o.getName()) && (o instanceof BoundTransferOperation) &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInstanceRepresentation().getEntityType(), e.get()) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.UPDATE_INSTANCE && EcoreUtil.equals(o.getBehaviour().getOwner(), defaultE.get()) &&
                o.getInput() != null && o.getOutput() != null && o.getFaults().isEmpty() &&
                o.getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getInput().getType(), defaultE.get()) &&
                o.getOutput().getCardinality().getLower() == 1 && o.getOutput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getOutput().getType(), defaultE.get()) &&
                NAME_OF_UPDATE_INSTANCE_E_OPERATION_ET.equals(((BoundTransferOperation) o).getBinding().getName()) &&
                ((BoundTransferOperation) o).getBinding().getInput() != null && ((BoundTransferOperation) o).getBinding().getOutput() != null && ((BoundTransferOperation) o).getBinding().getFaults().isEmpty() &&
                ((BoundTransferOperation) o).getBinding().getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInput().getType(), defaultE.get()) &&
                ((BoundTransferOperation) o).getBinding().getOutput().getCardinality().getLower() == 1 && o.getOutput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getOutput().getType(), defaultE.get())
        ));

        assertTrue(defaultE.get().getOperations().stream().anyMatch(o -> NAME_OF_VALIDATE_UPDATE_E_OPERATION.equals(o.getName()) && (o instanceof BoundTransferOperation) &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInstanceRepresentation().getEntityType(), e.get()) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.VALIDATE_UPDATE && EcoreUtil.equals(o.getBehaviour().getOwner(), defaultE.get()) &&
                o.getInput() != null && o.getOutput() != null && o.getFaults().isEmpty() &&
                o.getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getInput().getType(), defaultE.get()) &&
                o.getOutput().getCardinality().getLower() == 1 && o.getOutput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getOutput().getType(), defaultE.get()) &&
                NAME_OF_VALIDATE_UPDATE_E_OPERATION_ET.equals(((BoundTransferOperation) o).getBinding().getName()) &&
                ((BoundTransferOperation) o).getBinding().getInput() != null && ((BoundTransferOperation) o).getBinding().getOutput() != null && ((BoundTransferOperation) o).getBinding().getFaults().isEmpty() &&
                ((BoundTransferOperation) o).getBinding().getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInput().getType(), defaultE.get()) &&
                ((BoundTransferOperation) o).getBinding().getOutput().getCardinality().getLower() == 1 && o.getOutput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getOutput().getType(), defaultE.get())
        ));

        assertTrue(defaultE.get().getOperations().stream().anyMatch(o -> NAME_OF_DELETE_INSTANCE_E_OPERATION.equals(o.getName()) && (o instanceof BoundTransferOperation) &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInstanceRepresentation().getEntityType(), e.get()) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.DELETE_INSTANCE && EcoreUtil.equals(o.getBehaviour().getOwner(), defaultE.get()) &&
                o.getInput() == null && o.getOutput() == null && o.getFaults().isEmpty() &&
                NAME_OF_DELETE_INSTANCE_E_OPERATION_ET.equals(((BoundTransferOperation) o).getBinding().getName()) &&
                ((BoundTransferOperation) o).getBinding().getInput() == null && ((BoundTransferOperation) o).getBinding().getOutput() == null && ((BoundTransferOperation) o).getBinding().getFaults().isEmpty()
        ));

        assertTrue(defaultE.get().getOperations().stream().anyMatch(o -> NAME_OF_SET_REFERENCE_SINGLE_REFERENCE_OPERATION.equals(o.getName()) && (o instanceof BoundTransferOperation) &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInstanceRepresentation().getEntityType(), e.get()) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.SET_REFERENCE && EcoreUtil.equals(o.getBehaviour().getOwner(), defaultSingleReference.get()) &&
                o.getInput() != null && o.getOutput() == null && o.getFaults().isEmpty() &&
                o.getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(o.getInput().getType(), defaultF.get()) &&
                NAME_OF_SET_REFERENCE_SINGLE_REFERENCE_OPERATION_ET.equals(((BoundTransferOperation) o).getBinding().getName()) &&
                ((BoundTransferOperation) o).getBinding().getInput() != null && ((BoundTransferOperation) o).getBinding().getOutput() == null && ((BoundTransferOperation) o).getBinding().getFaults().isEmpty() &&
                ((BoundTransferOperation) o).getBinding().getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == 1 &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInput().getType(), defaultF.get())
        ));

        assertTrue(defaultE.get().getOperations().stream().anyMatch(o -> NAME_OF_UNSET_REFERENCE_SINGLE_REFERENCE_OPERATION.equals(o.getName()) && (o instanceof BoundTransferOperation) &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInstanceRepresentation().getEntityType(), e.get()) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.UNSET_REFERENCE && EcoreUtil.equals(o.getBehaviour().getOwner(), defaultSingleReference.get()) &&
                o.getInput() == null && o.getOutput() == null && o.getFaults().isEmpty() &&
                NAME_OF_UNSET_REFERENCE_SINGLE_REFERENCE_OPERATION_ET.equals(((BoundTransferOperation) o).getBinding().getName()) &&
                ((BoundTransferOperation) o).getBinding().getInput() == null && ((BoundTransferOperation) o).getBinding().getOutput() == null && ((BoundTransferOperation) o).getBinding().getFaults().isEmpty()
        ));

        assertFalse(defaultE.get().getOperations().stream().anyMatch(o -> NAME_OF_ADD_REFERENCE_SINGLE_REFERENCE_OPERATION.equals(o.getName())));
        assertFalse(defaultE.get().getOperations().stream().anyMatch(o -> NAME_OF_REMOVE_REFERENCE_SINGLE_REFERENCE_OPERATION.equals(o.getName())));

        assertTrue(defaultE.get().getOperations().stream().anyMatch(o -> NAME_OF_SET_REFERENCE_MULTIPLE_REFERENCE_OPERATION.equals(o.getName()) && (o instanceof BoundTransferOperation) &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInstanceRepresentation().getEntityType(), e.get()) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.SET_REFERENCE && EcoreUtil.equals(o.getBehaviour().getOwner(), defaultMultipleReference.get()) &&
                o.getInput() != null && o.getOutput() == null && o.getFaults().isEmpty() &&
                o.getInput().getCardinality().getLower() == 0 && o.getInput().getCardinality().getUpper() == -1 &&
                EcoreUtil.equals(o.getInput().getType(), defaultF.get()) &&
                NAME_OF_SET_REFERENCE_MULTIPLE_REFERENCE_OPERATION_ET.equals(((BoundTransferOperation) o).getBinding().getName()) &&
                ((BoundTransferOperation) o).getBinding().getInput() != null && ((BoundTransferOperation) o).getBinding().getOutput() == null && ((BoundTransferOperation) o).getBinding().getFaults().isEmpty() &&
                ((BoundTransferOperation) o).getBinding().getInput().getCardinality().getLower() == 0 && o.getInput().getCardinality().getUpper() == -1 &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInput().getType(), defaultF.get())
        ));

        assertFalse(defaultE.get().getOperations().stream().anyMatch(o -> NAME_OF_UNSET_REFERENCE_MULTIPLE_REFERENCE_OPERATION.equals(o.getName())));

        assertTrue(defaultE.get().getOperations().stream().anyMatch(o -> NAME_OF_ADD_REFERENCE_MULTIPLE_REFERENCE_OPERATION.equals(o.getName()) && (o instanceof BoundTransferOperation) &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInstanceRepresentation().getEntityType(), e.get()) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.ADD_REFERENCE && EcoreUtil.equals(o.getBehaviour().getOwner(), defaultMultipleReference.get()) &&
                o.getInput() != null && o.getOutput() == null && o.getFaults().isEmpty() &&
                o.getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == -1 &&
                EcoreUtil.equals(o.getInput().getType(), defaultF.get()) &&
                NAME_OF_ADD_REFERENCE_MULTIPLE_REFERENCE_OPERATION_ET.equals(((BoundTransferOperation) o).getBinding().getName()) &&
                ((BoundTransferOperation) o).getBinding().getInput() != null && ((BoundTransferOperation) o).getBinding().getOutput() == null && ((BoundTransferOperation) o).getBinding().getFaults().isEmpty() &&
                ((BoundTransferOperation) o).getBinding().getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == -1 &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInput().getType(), defaultF.get())
        ));

        assertTrue(defaultE.get().getOperations().stream().anyMatch(o -> NAME_OF_REMOVE_REFERENCE_MULTIPLE_REFERENCE_OPERATION.equals(o.getName()) && (o instanceof BoundTransferOperation) &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInstanceRepresentation().getEntityType(), e.get()) &&
                o.getBehaviour() != null && o.getBehaviour().getBehaviourType() == TransferOperationBehaviourType.REMOVE_REFERENCE && EcoreUtil.equals(o.getBehaviour().getOwner(), defaultMultipleReference.get()) &&
                o.getInput() != null && o.getOutput() == null && o.getFaults().isEmpty() &&
                o.getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == -1 &&
                EcoreUtil.equals(o.getInput().getType(), defaultF.get()) &&
                NAME_OF_REMOVE_REFERENCE_MULTIPLE_REFERENCE_OPERATION_ET.equals(((BoundTransferOperation) o).getBinding().getName()) &&
                ((BoundTransferOperation) o).getBinding().getInput() != null && ((BoundTransferOperation) o).getBinding().getOutput() == null && ((BoundTransferOperation) o).getBinding().getFaults().isEmpty() &&
                ((BoundTransferOperation) o).getBinding().getInput().getCardinality().getLower() == 1 && o.getInput().getCardinality().getUpper() == -1 &&
                EcoreUtil.equals(((BoundTransferOperation) o).getBinding().getInput().getType(), defaultF.get())
        ));

        assertFalse(defaultE.get().getOperations().stream().anyMatch(o -> NAME_OF_SET_REFERENCE_SINGLE_CONTAINMENT_OPERATION.equals(o.getName())));
        assertFalse(defaultE.get().getOperations().stream().anyMatch(o -> NAME_OF_UNSET_REFERENCE_SINGLE_CONTAINMENT_OPERATION.equals(o.getName())));
        assertFalse(defaultE.get().getOperations().stream().anyMatch(o -> NAME_OF_ADD_REFERENCE_SINGLE_CONTAINMENT_OPERATION.equals(o.getName())));
        assertFalse(defaultE.get().getOperations().stream().anyMatch(o -> NAME_OF_REMOVE_REFERENCE_SINGLE_CONTAINMENT_OPERATION.equals(o.getName())));

        assertFalse(defaultE.get().getOperations().stream().anyMatch(o -> NAME_OF_SET_REFERENCE_MULTIPLE_CONTAINMENT_OPERATION.equals(o.getName())));
        assertFalse(defaultE.get().getOperations().stream().anyMatch(o -> NAME_OF_UNSET_REFERENCE_MULTIPLE_CONTAINMENT_OPERATION.equals(o.getName())));
        assertFalse(defaultE.get().getOperations().stream().anyMatch(o -> NAME_OF_ADD_REFERENCE_MULTIPLE_CONTAINMENT_OPERATION.equals(o.getName())));
        assertFalse(defaultE.get().getOperations().stream().anyMatch(o -> NAME_OF_REMOVE_REFERENCE_MULTIPLE_CONTAINMENT_OPERATION.equals(o.getName())));
    }

    @Test
    void testInitializerFlag() throws Exception {
        testName = "TestInitializerFlag";

        // FIXME - replace test case (using ESM initializer flag instead of type and operation names)
        final TransferObjectType initializer1 = newTransferObjectTypeBuilder()
                .withName("Initializer1")
                .withOperations(newOperationBuilder()
                        .withName("run")
                        .withOperationType(OperationType.STATIC)
                        .withBody("// TODO")
                        .withBinding("")
                        .withInitializer(true)
                        .build())
                .build();

        final TransferObjectType initializer2 = newTransferObjectTypeBuilder()
                .withName("Initializer2")
                .withOperations(newOperationBuilder()
                        .withName("run")
                        .withOperationType(OperationType.STATIC)
                        .withBody("// TODO")
                        .withBinding("")
                        .withInitializer(true)
                        .build())
                .build();

        final TransferObjectType nonInitializer1 = newTransferObjectTypeBuilder()
                .withName("NonInitializer1")
                .withOperations(newOperationBuilder()
                        .withName("run")
                        .withOperationType(OperationType.STATIC)
                        .withBody("// TODO")
                        .withBinding("")
                        .build())
                .build();

        final TransferObjectType nonInitializer2 = newTransferObjectTypeBuilder()
                .withName("NonInitializer2")
                .withOperations(newOperationBuilder()
                        .withName("run")
                        .withOperationType(OperationType.STATIC)
                        .withBody("// TODO")
                        .withBinding("")
                        .build())
                .build();

        final Model model = newModelBuilder().withName(MODEL_NAME)
                .withElements(Arrays.asList(initializer1, initializer2, nonInitializer1, nonInitializer2))
                .build();

        esmModel.addContent(model);

        transform();

        final List<UnboundOperation> initializers = allPsm(UnboundOperation.class).filter(o -> o.isInitializer()).collect(Collectors.toList());

        assertEquals(2, initializers.size());
    }
    
    @Test
    void testInitializerFlagInheritance() throws Exception {
        testName = "TestInitializerFlagInheritance";

        final TransferObjectType parent = newTransferObjectTypeBuilder()
                .withName("parent")
                .withOperations(newOperationBuilder()
                        .withName("init")
                        .withOperationType(OperationType.STATIC)
                        .withBody("// TODO")
                        .withBinding("")
                        .withInitializer(true)
                        .build())
                .build();

        final TransferObjectType child = newTransferObjectTypeBuilder()
                .withName("child")
                .withGeneralizations(newGeneralizationBuilder().withTarget(parent).build())
                .build();
        final TransferObjectType grandChild = newTransferObjectTypeBuilder()
                .withName("grandChild")
                .withGeneralizations(newGeneralizationBuilder().withTarget(child).build())
                .build();

        final EntityType entityParent = newEntityTypeBuilder()
                .withName("entityParent")
                .withOperations(newOperationBuilder()
                        .withName("init")
                        .withOperationType(OperationType.STATIC)
                        .withBody("// TODO")
                        .withBinding("")
                        .withInitializer(true)
                        .build())
                .build();
        entityParent.setMappedEntity(entityParent);

        final EntityType entityChild = newEntityTypeBuilder()
                .withName("entityChild")
                .withGeneralizations(newGeneralizationBuilder().withTarget(entityParent).build())
                .build();
        entityChild.setMappedEntity(entityChild);
        final EntityType entityGrandChild = newEntityTypeBuilder()
                .withName("entityGrandChild")
                .withGeneralizations(newGeneralizationBuilder().withTarget(entityChild).build())
                .build();
        entityGrandChild.setMappedEntity(entityGrandChild);

        final Model model = newModelBuilder().withName(MODEL_NAME)
                .withElements(Arrays.asList(parent, child, grandChild, entityParent, entityChild, entityGrandChild))
                .build();

        esmModel.addContent(model);

        transform();

        Optional<MappedTransferObjectType> psmParent1 = allPsm(MappedTransferObjectType.class).filter(o -> o.getName().equals(entityParent.getName())).findAny();
        assertTrue(psmParent1.isPresent());
        Optional<TransferOperation> bound1 = psmParent1.get().getOperations().stream().filter(o -> o.getName().equals("init")).findAny();
        assertTrue(bound1.isPresent());
        assertTrue(bound1.get() instanceof UnboundOperation);
        assertTrue(((UnboundOperation) bound1.get()).isInitializer());
        
        Optional<MappedTransferObjectType> mapped1 = allPsm(MappedTransferObjectType.class).filter(o -> o.getName().equals(entityChild.getName())).findAny();
        assertTrue(mapped1.isPresent());
        Optional<TransferOperation> unbound1 = mapped1.get().getOperations().stream().filter(o -> o.getName().equals("init")).findAny();
        assertTrue(unbound1.isPresent());
        assertTrue(unbound1.get() instanceof UnboundOperation);
        assertFalse(((UnboundOperation) unbound1.get()).isInitializer());
        
        Optional<MappedTransferObjectType> mapped2 = allPsm(MappedTransferObjectType.class).filter(o -> o.getName().equals(entityGrandChild.getName())).findAny();
        assertTrue(mapped2.isPresent());
        Optional<TransferOperation> unbound2 = mapped2.get().getOperations().stream().filter(o -> o.getName().equals("init")).findAny();
        assertTrue(unbound2.isPresent());
        assertTrue(unbound2.get() instanceof UnboundOperation);
        assertFalse(((UnboundOperation) unbound2.get()).isInitializer());
        
        Optional<UnmappedTransferObjectType> psmParent2 = allPsm(UnmappedTransferObjectType.class).filter(o -> o.getName().equals(parent.getName())).findAny();
        assertTrue(psmParent2.isPresent());
        Optional<TransferOperation> bound2 = psmParent2.get().getOperations().stream().filter(o -> o.getName().equals("init")).findAny();
        assertTrue(bound2.isPresent());
        assertTrue(bound2.get() instanceof UnboundOperation);
        assertTrue(((UnboundOperation) bound2.get()).isInitializer());
        
        Optional<UnmappedTransferObjectType> unmapped1 = allPsm(UnmappedTransferObjectType.class).filter(o -> o.getName().equals(child.getName())).findAny();
        assertTrue(unmapped1.isPresent());
        Optional<TransferOperation> unbound3 = unmapped1.get().getOperations().stream().filter(o -> o.getName().equals("init")).findAny();
        assertTrue(unbound3.isPresent());
        assertTrue(unbound3.get() instanceof UnboundOperation);
        assertFalse(((UnboundOperation) unbound3.get()).isInitializer());
        
        Optional<UnmappedTransferObjectType> unmapped2 = allPsm(UnmappedTransferObjectType.class).filter(o -> o.getName().equals(grandChild.getName())).findAny();
        assertTrue(unmapped2.isPresent());
        Optional<TransferOperation> unbound4 = unmapped2.get().getOperations().stream().filter(o -> o.getName().equals("init")).findAny();
        assertTrue(unbound4.isPresent());
        assertTrue(unbound4.get() instanceof UnboundOperation);
        assertFalse(((UnboundOperation) unbound4.get()).isInitializer());
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
