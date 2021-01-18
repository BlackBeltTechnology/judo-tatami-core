package hu.blackbelt.judo.tatami.esm2ui;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.accesspoint.Access;
import hu.blackbelt.judo.meta.esm.accesspoint.ActorType;
import hu.blackbelt.judo.meta.esm.measure.DurationType;
import hu.blackbelt.judo.meta.esm.namespace.Model;
import hu.blackbelt.judo.meta.esm.operation.Operation;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.esm.runtime.EsmUtils;
import hu.blackbelt.judo.meta.esm.structure.DataMember;
import hu.blackbelt.judo.meta.esm.structure.EntityType;
import hu.blackbelt.judo.meta.esm.structure.MemberType;
import hu.blackbelt.judo.meta.esm.structure.OneWayRelationMember;
import hu.blackbelt.judo.meta.esm.structure.RelationKind;
import hu.blackbelt.judo.meta.esm.structure.TransferObjectType;
import hu.blackbelt.judo.meta.esm.type.BooleanType;
import hu.blackbelt.judo.meta.esm.type.CustomType;
import hu.blackbelt.judo.meta.esm.type.DateType;
import hu.blackbelt.judo.meta.esm.type.EnumerationType;
import hu.blackbelt.judo.meta.esm.type.NumericType;
import hu.blackbelt.judo.meta.esm.type.PasswordType;
import hu.blackbelt.judo.meta.esm.type.StringType;
import hu.blackbelt.judo.meta.esm.type.TimestampType;
import hu.blackbelt.judo.meta.esm.type.XMLType;
import hu.blackbelt.judo.meta.ui.Application;
import hu.blackbelt.judo.meta.ui.data.ClassType;
import hu.blackbelt.judo.meta.ui.data.DataElement;
import hu.blackbelt.judo.meta.ui.data.DataType;
import hu.blackbelt.judo.meta.ui.data.OperationType;
import hu.blackbelt.judo.meta.ui.data.RelationBehaviourType;
import hu.blackbelt.judo.meta.ui.data.RelationType;
import hu.blackbelt.judo.meta.ui.runtime.UiModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static hu.blackbelt.judo.meta.esm.accesspoint.util.builder.AccesspointBuilders.newActorTypeBuilder;
import static hu.blackbelt.judo.meta.esm.accesspoint.util.builder.AccesspointBuilders.newAccessBuilder;
import static hu.blackbelt.judo.meta.esm.accesspoint.util.builder.AccesspointBuilders.useActorType;
import static hu.blackbelt.judo.meta.esm.namespace.util.builder.NamespaceBuilders.newModelBuilder;
import static hu.blackbelt.judo.meta.esm.operation.util.builder.OperationBuilders.*;
import static hu.blackbelt.judo.meta.esm.runtime.EsmEpsilonValidator.calculateEsmValidationScriptURI;
import static hu.blackbelt.judo.meta.esm.runtime.EsmEpsilonValidator.validateEsm;
import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.buildEsmModel;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.*;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newStringTypeBuilder;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newTimestampTypeBuilder;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newXMLTypeBuilder;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newBooleanTypeBuilder;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newCustomTypeBuilder;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newDateTypeBuilder;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newEnumerationMemberBuilder;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newEnumerationTypeBuilder;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newNumericTypeBuilder;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newPasswordTypeBuilder;
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
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class Esm2UiDataTest {
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
    	log.info(esmModel.getDiagnosticsAsString());
    	assertTrue(esmModel.isValid());
    	validateEsm(new Slf4jLog(log), esmModel, calculateEsmValidationScriptURI());
        // Make transformation which returns the trace with the serialized URI's
        esm2UiTransformationTrace = executeEsm2UiTransformation(esmModel, "default", 12, uiModel, new Slf4jLog(log),
                calculateEsm2UiTransformationScriptURI());

        log.info(uiModel.getDiagnosticsAsString());
        assertTrue(uiModel.isValid());
        validateUi(new Slf4jLog(log), uiModel, calculateUiValidationScriptURI());
    }

    @Test
    void testCreateRelationTypeForClass() throws Exception {
        testName = "CreateRelationTypeForClass";

        final String MODEL_NAME = "Model";
        final String TRANSFER_OBJECT_TYPE_NAME_1 = "T1";
        final String TRANSFER_OBJECT_TYPE_NAME_2 = "T2";
        final String TRANSFER_OBJECT_TYPE_NAME_3 = "T3";
        final String TRANSFER_OBJECT_TYPE_NAME_4 = "T4";
        final String ENTITY_TYPE_NAME_1 = "E1";
        final String ENTITY_TYPE_NAME_2 = "E2";
        final String ENTITY_TYPE_NAME_3 = "E3";
        final String ENTITY_TYPE_NAME_4 = "E4";
        final String ACTOR_TYPE_NAME = "Actor";
        
        ActorType actor = newActorTypeBuilder()
                .withName(ACTOR_TYPE_NAME)
                .withRealm("sandbox")
                .build();
        
        final EntityType e1 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_1)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .build();
        useEntityType(e1).withMappedEntity(e1).build();
        final EntityType e2 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_2)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withGeneralizations(newGeneralizationBuilder().withTarget(e1).build())
                .build();
        useEntityType(e2).withMappedEntity(e2).build();
        final EntityType e3 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_3)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withGeneralizations(newGeneralizationBuilder().withTarget(e2).build())
                .build();
        useEntityType(e3).withMappedEntity(e3).build();
        
        final EntityType e4 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_4)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .build();
        useEntityType(e4).withMappedEntity(e4).build();

        final TransferObjectType t1 = newTransferObjectTypeBuilder()
                .withName(TRANSFER_OBJECT_TYPE_NAME_1)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .build();
        useTransferObjectType(t1).withMappedEntity(e1).build();
        final TransferObjectType t2 = newTransferObjectTypeBuilder()
                .withName(TRANSFER_OBJECT_TYPE_NAME_2)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withGeneralizations(newGeneralizationBuilder().withTarget(t1).build())
                .build();
        useTransferObjectType(t2).withMappedEntity(e2).build();
        final TransferObjectType t3 = newTransferObjectTypeBuilder()
                .withName(TRANSFER_OBJECT_TYPE_NAME_3)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withGeneralizations(newGeneralizationBuilder().withTarget(t2).build())
                .build();
        useTransferObjectType(t3).withMappedEntity(e3).build();
        final TransferObjectType t4 = newTransferObjectTypeBuilder()
                .withName(TRANSFER_OBJECT_TYPE_NAME_4)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .build();
        useTransferObjectType(t4).withMappedEntity(e4).build();
        
        Access access1 = newAccessBuilder().withName("t1")
        		.withTarget(t1)
        		.withLower(0)
        		.withUpper(-1)
        		.withCreateable(true)
        		.withTargetDefinedCRUD(false)
        		.build();
        
        Access access2 = newAccessBuilder().withName("t3")
        		.withTarget(t3)
        		.withLower(0)
        		.withUpper(-1)
        		.withCreateable(false)
        		.withTargetDefinedCRUD(false)
        		.build();
        
        OneWayRelationMember relation1 = newOneWayRelationMemberBuilder()
        		.withName("transient")
        		.withLower(0)
        		.withUpper(-1)
        		.withTarget(t4)
        		.withMemberType(MemberType.TRANSIENT)
        		.withRelationKind(RelationKind.AGGREGATION)
        		.build();
        OneWayRelationMember relation2 = newOneWayRelationMemberBuilder()
        		.withName("binding")
        		.withLower(0)
        		.withUpper(-1)
        		.withTarget(e4)
        		.withMemberType(MemberType.STORED)
        		.withRelationKind(RelationKind.ASSOCIATION)
        		.build();
        OneWayRelationMember relation3 = newOneWayRelationMemberBuilder()
        		.withName("mapped")
        		.withLower(0)
        		.withUpper(-1)
        		.withTarget(t4)
        		.withMemberType(MemberType.MAPPED)
        		.withRelationKind(RelationKind.ASSOCIATION)
        		.withBinding(relation2)
        		.build();
        OneWayRelationMember relation4 = newOneWayRelationMemberBuilder()
        		.withName("derived")
        		.withLower(0)
        		.withUpper(1)
        		.withTarget(t4)
        		.withMemberType(MemberType.DERIVED)
        		.withRelationKind(RelationKind.AGGREGATION)
        		.withGetterExpression("self.mapped!head()")
        		.build();
        useEntityType(e1).withRelations(relation2).build();
        useTransferObjectType(t1).withRelations(relation1, relation3, relation4).build();

        useActorType(actor).withAccesses(access1, access2).build();
        
        final Model model = newModelBuilder().withName(MODEL_NAME)
                .withElements(actor, t1, t2, t3, t4, e1, e2, e3, e4).build();

        esmModel.addContent(model);

        transform();

        final Optional<Application> application = allUi(Application.class).filter(a -> a.getName().equals(actor.getFQName()))
                .findAny();
        assertTrue(application.isPresent());
        final Optional<ClassType> uiActor = application.get().getDataElements().stream().filter(e -> e instanceof ClassType)
        		.map(e -> (ClassType) e).filter(c -> c.isIsActor()).findAny();
        assertTrue(uiActor.isPresent());
        final Optional<ClassType> uiT1 = application.get().getDataElements().stream().filter(e -> e instanceof ClassType && e.getName().equals(t1.getFQName()))
        		.map(e -> (ClassType) e).findAny();
        assertTrue(uiT1.isPresent());
        final Optional<ClassType> uiT3 = application.get().getDataElements().stream().filter(e -> e instanceof ClassType && e.getName().equals(t3.getFQName()))
        		.map(e -> (ClassType) e).findAny();
        assertTrue(uiT3.isPresent());
        final Optional<ClassType> uiT4 = application.get().getDataElements().stream().filter(e -> e instanceof ClassType && e.getName().equals(t4.getFQName()))
        		.map(e -> (ClassType) e).findAny();
        assertTrue(uiT4.isPresent());
        
        Optional<RelationType> uiAccess1 = uiActor.get().getRelations().stream().filter(r -> r.isIsAccess() && r.getTarget().equals(uiT1.get())).findAny();
        assertTrue(uiAccess1.isPresent());
        assertTrue(uiAccess1.get().isIsCollection());
        assertTrue(uiAccess1.get().getName().equals("t1"));
        assertTrue(uiAccess1.get().getBehaviours().contains(RelationBehaviourType.CREATE));
        assertTrue(uiAccess1.get().getBehaviours().contains(RelationBehaviourType.VALIDATE_CREATE));
        assertTrue(uiAccess1.get().getBehaviours().contains(RelationBehaviourType.LIST));
        
        Optional<RelationType> uiAccess2 = uiActor.get().getRelations().stream().filter(r -> r.isIsAccess() && r.getTarget().equals(uiT3.get())).findAny();
        assertTrue(uiAccess2.isPresent());
        assertTrue(uiAccess2.get().isIsCollection());
        assertTrue(uiAccess2.get().getName().equals("t3"));
        assertFalse(uiAccess2.get().getBehaviours().contains(RelationBehaviourType.CREATE));
        assertFalse(uiAccess2.get().getBehaviours().contains(RelationBehaviourType.VALIDATE_CREATE));
        assertTrue(uiAccess2.get().getBehaviours().contains(RelationBehaviourType.LIST));
        
        Optional<RelationType> uiRelation1 = uiT1.get().getRelations().stream().filter(r -> r.getName().equals(relation1.getName())).findAny();
        assertTrue(uiRelation1.isPresent());
        assertTrue(uiRelation1.get().isIsCollection());
        assertTrue(uiRelation1.get().getTarget().equals(uiT4.get()));
        assertTrue(uiRelation1.get().getMemberType().equals(hu.blackbelt.judo.meta.ui.data.MemberType.TRANSIENT));
        assertTrue(uiRelation1.get().getRelationKind().equals(hu.blackbelt.judo.meta.ui.data.RelationKind.AGGREGATION));
        
        Optional<RelationType> uiRelation2 = uiT1.get().getRelations().stream().filter(r -> r.getName().equals(relation3.getName())).findAny();
        assertTrue(uiRelation2.isPresent());
        assertTrue(uiRelation2.get().isIsCollection());
        assertTrue(uiRelation2.get().getTarget().equals(uiT4.get()));
        assertTrue(uiRelation2.get().getMemberType().equals(hu.blackbelt.judo.meta.ui.data.MemberType.MAPPED));
        assertTrue(uiRelation2.get().getRelationKind().equals(hu.blackbelt.judo.meta.ui.data.RelationKind.ASSOCIATION));
        
        Optional<RelationType> uiRelation3 = uiT1.get().getRelations().stream().filter(r -> r.getName().equals(relation4.getName())).findAny();
        assertTrue(uiRelation3.isPresent());
        assertFalse(uiRelation3.get().isIsCollection());
        assertTrue(uiRelation3.get().getTarget().equals(uiT4.get()));
        assertTrue(uiRelation3.get().getMemberType().equals(hu.blackbelt.judo.meta.ui.data.MemberType.DERIVED));
        assertTrue(uiRelation3.get().getRelationKind().equals(hu.blackbelt.judo.meta.ui.data.RelationKind.AGGREGATION));
        
        assertTrue(uiT3.get().getRelations().stream().anyMatch(r -> r.getName().equals(uiRelation1.get().getName())));
        assertTrue(uiT3.get().getRelations().stream().anyMatch(r -> r.getName().equals(uiRelation2.get().getName())));
        assertTrue(uiT3.get().getRelations().stream().anyMatch(r -> r.getName().equals(uiRelation3.get().getName())));
    }
    
    @Test
    void testCreateClassTypeFromActorType() throws Exception {
        testName = "CreateClassTypeFromActorType";

        final String MODEL_NAME = "Model";
        final String TRANSFER_OBJECT_TYPE_NAME_1 = "T1";
        final String TRANSFER_OBJECT_TYPE_NAME_2 = "T2";
        final String TRANSFER_OBJECT_TYPE_NAME_3 = "T3";
        final String ENTITY_TYPE_NAME_1 = "E1";
        final String ENTITY_TYPE_NAME_2 = "E2";
        final String ENTITY_TYPE_NAME_3 = "E3";
        final String ACTOR_TYPE_NAME = "Actor";
        
        ActorType actor = newActorTypeBuilder()
                .withName(ACTOR_TYPE_NAME)
                .withRealm("sandbox")
                .build();
        
        final EntityType e1 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_1)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .build();
        useEntityType(e1).withMappedEntity(e1).build();
        final EntityType e2 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_2)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withGeneralizations(newGeneralizationBuilder().withTarget(e1).build())
                .build();
        useEntityType(e2).withMappedEntity(e2).build();
        final EntityType e3 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_3)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withGeneralizations(newGeneralizationBuilder().withTarget(e2).build())
                .build();
        useEntityType(e3).withMappedEntity(e3).build();

        final TransferObjectType t1 = newTransferObjectTypeBuilder()
                .withName(TRANSFER_OBJECT_TYPE_NAME_1)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .build();
        useTransferObjectType(t1).withMappedEntity(e1).build();
        final TransferObjectType t2 = newTransferObjectTypeBuilder()
                .withName(TRANSFER_OBJECT_TYPE_NAME_2)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withGeneralizations(newGeneralizationBuilder().withTarget(t1).build())
                .build();
        useTransferObjectType(t2).withMappedEntity(e2).build();
        final TransferObjectType t3 = newTransferObjectTypeBuilder()
                .withName(TRANSFER_OBJECT_TYPE_NAME_3)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withGeneralizations(newGeneralizationBuilder().withTarget(t2).build())
                .build();
        useTransferObjectType(t3).withMappedEntity(e3).build();
        
        Access access1 = newAccessBuilder().withName("t1")
        		.withTarget(t1)
        		.withLower(0)
        		.withUpper(-1)
        		.withCreateable(true)
        		.withTargetDefinedCRUD(false)
        		.build();
        
        Access access2 = newAccessBuilder().withName("t3")
        		.withTarget(t3)
        		.withLower(0)
        		.withUpper(-1)
        		.withCreateable(false)
        		.withTargetDefinedCRUD(false)
        		.build();

        useActorType(actor).withAccesses(access1, access2).build();
        
        final Model model = newModelBuilder().withName(MODEL_NAME)
                .withElements(actor, t1, t2, t3, e1, e2, e3).build();

        esmModel.addContent(model);

        transform();

        final Optional<Application> application = allUi(Application.class).filter(a -> a.getName().equals(actor.getFQName()))
                .findAny();
        assertTrue(application.isPresent());
        
        final Optional<ClassType> uiActor = application.get().getDataElements().stream().filter(e -> e instanceof ClassType)
        		.map(e -> (ClassType) e).filter(c -> c.isIsActor()).findAny();
        assertTrue(uiActor.isPresent());
        assertTrue(uiActor.get().getName().equals(EsmUtils.getNamespaceElementFQName(actor)));
        assertEquals(actor.getAccesses().size(), uiActor.get().getRelations().size());
    }
    
    @Test
    void testCreateOperationType() throws Exception {
        testName = "CreateOperationType";
        
        StringType str = newStringTypeBuilder().withName("string").withMaxLength(8).build();
        final String MODEL_NAME = "Model";
        final String ENTITY_TYPE_NAME_1 = "E1";
        final String ENTITY_TYPE_NAME_2 = "E2";
        final String ENTITY_TYPE_NAME_3 = "E3";
        final String ACTOR_TYPE_NAME = "Actor";
        
        ActorType actor = newActorTypeBuilder()
                .withName(ACTOR_TYPE_NAME)
                .withRealm("sandbox")
                .build();
        
        DataMember inputID = newDataMemberBuilder().withName("id").withDataType(str).build();
        final EntityType inputType = newEntityTypeBuilder()
                .withName("input")
                .withCreateable(true)
                .withUpdateable(true)
                .withDeleteable(true)
                .withAttributes(inputID)
                .withTable(
                		newTransferObjectTableBuilder()
                		.withName("inputTable")
                		.withColumns(
                				newDataColumnBuilder()
                				.withName("id")
                				.withLabel("id")
                				.withDataFeature(inputID).build()))
                .withForm(
                		newTransferObjectFormBuilder()
                		.withName("inputForm")
                		.withComponents(
                				newGroupBuilder()
                				.withName("g")
                				.withComponents(
                						newDataFieldBuilder()
                						.withName("id")
                						.withDataFeature(inputID).build()).build()))
                .build();
        useEntityType(inputType).withMappedEntity(inputType).build();
        
        DataMember outputID = newDataMemberBuilder().withName("id").withDataType(str).build();
        final EntityType outputType = newEntityTypeBuilder()
                .withName("output")
                .withCreateable(true)
                .withUpdateable(true)
                .withDeleteable(true)
                .withAttributes(outputID)
                .withTable(
                		newTransferObjectTableBuilder()
                		.withName("outputTable")
                		.withColumns(
                				newDataColumnBuilder()
                				.withName("id")
                				.withLabel("id")
                				.withDataFeature(outputID).build()))
                .withView(
                		newTransferObjectViewBuilder()
                		.withName("outputView")
                		.withComponents(
                				newGroupBuilder()
                				.withName("g")
                				.withComponents(
                						newDataFieldBuilder()
                						.withName("id")
                						.withDataFeature(outputID).build()).build()))
                .build();
        useEntityType(outputType).withMappedEntity(outputType).build();
        
        final EntityType e1 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_1)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .build();
        useEntityType(e1).withMappedEntity(e1).build();
        
        Operation operation1 = newOperationBuilder().withName("op1")
        		.withInput(newParameterBuilder().withName("input").withTarget(inputType).withUpper(-1).build())
        		.withBinding("op1")
        		.build();
        Operation operation2 = newOperationBuilder().withName("op2")
        		.withOutput(newParameterBuilder().withName("output").withTarget(outputType).withUpper(-1).build())
        		.withBinding("op2")
        		.build();
        Operation operation3 = newOperationBuilder().withName("op3")
        		.withInput(newParameterBuilder().withName("input").withTarget(inputType).withUpper(-1).build())
        		.withOutput(newParameterBuilder().withName("output").withTarget(outputType).withUpper(-1).build())
        		.withBinding("op3")
        		.build();
        
        useEntityType(e1).withOperations(operation1,operation2).build();
        
        final EntityType e2 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_2)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withGeneralizations(newGeneralizationBuilder().withTarget(e1).build())
                .build();
        useEntityType(e2).withMappedEntity(e2).build();
        final EntityType e3 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_3)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withGeneralizations(newGeneralizationBuilder().withTarget(e2).build())
                .build();
        useEntityType(e3).withMappedEntity(e3).build();
        useEntityType(e3).withOperations(operation3).build();
        
        Access access2 = newAccessBuilder().withName("t3")
        		.withTarget(e3)
        		.withLower(0)
        		.withUpper(-1)
        		.withCreateable(false)
        		.withTargetDefinedCRUD(false)
        		.build();

        useActorType(actor).withAccesses(access2).build();
        
        final Model model = newModelBuilder().withName(MODEL_NAME)
                .withElements(actor, e1, e2, e3, inputType, outputType, str).build();

        esmModel.addContent(model);

        transform();

        final Optional<Application> application = allUi(Application.class).filter(a -> a.getName().equals(actor.getFQName()))
                .findAny();
        assertTrue(application.isPresent());
        
        final Optional<ClassType> uiE3 = application.get().getDataElements().stream().filter(e -> e instanceof ClassType)
        		.map(e -> (ClassType) e).filter(c -> c.getName().equals(EsmUtils.getNamespaceElementFQName(e3))).findAny();
        assertTrue(uiE3.isPresent());
        assertEquals(3, uiE3.get().getOperations().size());
        
        final Optional<OperationType> uiOp1 = uiE3.get().getOperations().stream().filter(o -> o.getName().equals(operation1.getName())).findFirst();
        assertTrue(uiOp1.isPresent());
        assertNotNull(uiOp1.get().getInput());
        assertTrue(uiOp1.get().getInput().getTarget().getName().equals(EsmUtils.getNamespaceElementFQName(inputType)));
        assertNull(uiOp1.get().getOutput());
        
        final Optional<OperationType> uiOp2 = uiE3.get().getOperations().stream().filter(o -> o.getName().equals(operation2.getName())).findFirst();
        assertTrue(uiOp2.isPresent());
        assertNotNull(uiOp2.get().getOutput());
        assertTrue(uiOp2.get().getOutput().getTarget().getName().equals(EsmUtils.getNamespaceElementFQName(outputType)));
        assertNull(uiOp2.get().getInput());
        
        final Optional<OperationType> uiOp3 = uiE3.get().getOperations().stream().filter(o -> o.getName().equals(operation3.getName())).findFirst();
        assertTrue(uiOp3.isPresent());
        assertNotNull(uiOp3.get().getInput());
        assertTrue(uiOp3.get().getInput().getTarget().getName().equals(EsmUtils.getNamespaceElementFQName(inputType)));
        assertNotNull(uiOp3.get().getOutput());
        assertTrue(uiOp3.get().getOutput().getTarget().getName().equals(EsmUtils.getNamespaceElementFQName(outputType)));
    }
    
    @Test
    void testCreateOperationTypeFromAbstract() throws Exception {
        testName = "CreateOperationTypeFromAbstract";
        
        StringType str = newStringTypeBuilder().withName("string").withMaxLength(8).build();
        final String MODEL_NAME = "Model";
        final String ENTITY_TYPE_NAME_1 = "E1";
        final String ENTITY_TYPE_NAME_2 = "E2";
        final String ENTITY_TYPE_NAME_3 = "E3";
        final String ACTOR_TYPE_NAME = "Actor";
        
        ActorType actor = newActorTypeBuilder()
                .withName(ACTOR_TYPE_NAME)
                .withRealm("sandbox")
                .build();
        
        DataMember inputID = newDataMemberBuilder().withName("id").withDataType(str).build();
        final EntityType inputType = newEntityTypeBuilder()
                .withName("input")
                .withCreateable(true)
                .withUpdateable(true)
                .withDeleteable(true)
                .withAbstract_(true)
                .withAttributes(inputID)
                .withTable(
                		newTransferObjectTableBuilder()
                		.withName("inputTable")
                		.withColumns(
                				newDataColumnBuilder()
                				.withName("id")
                				.withLabel("id")
                				.withDataFeature(inputID).build()))
                .withForm(
                		newTransferObjectFormBuilder()
                		.withName("inputForm")
                		.withComponents(
                				newGroupBuilder()
                				.withName("g")
                				.withComponents(
                						newDataFieldBuilder()
                						.withName("id")
                						.withDataFeature(inputID).build()).build()))
                .build();
        useEntityType(inputType).withMappedEntity(inputType).build();
        
        DataMember outputID = newDataMemberBuilder().withName("id").withDataType(str).build();
        final EntityType outputType = newEntityTypeBuilder()
                .withName("output")
                .withCreateable(true)
                .withUpdateable(true)
                .withDeleteable(true)
                .withAbstract_(true)
                .withAttributes(outputID)
                .withTable(
                		newTransferObjectTableBuilder()
                		.withName("outputTable")
                		.withColumns(
                				newDataColumnBuilder()
                				.withName("id")
                				.withLabel("id")
                				.withDataFeature(outputID).build()))
                .withView(
                		newTransferObjectViewBuilder()
                		.withName("outputView")
                		.withComponents(
                				newGroupBuilder()
                				.withName("g")
                				.withComponents(
                						newDataFieldBuilder()
                						.withName("id")
                						.withDataFeature(outputID).build()).build()))
                .build();
        useEntityType(outputType).withMappedEntity(outputType).build();
        
        final EntityType e1 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_1)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withAbstract_(true)
                .build();
        useEntityType(e1).withMappedEntity(e1).build();
        
        Operation operation1 = newOperationBuilder().withName("op1")
        		.withInput(newParameterBuilder().withName("input").withTarget(inputType).withUpper(-1).build())
        		.withOperationType(hu.blackbelt.judo.meta.esm.operation.OperationType.ABSTRACT)
        		.withBinding("op1")
        		.build();
        Operation operation2 = newOperationBuilder().withName("op2")
        		.withOutput(newParameterBuilder().withName("output").withTarget(outputType).withUpper(-1).build())
        		.withOperationType(hu.blackbelt.judo.meta.esm.operation.OperationType.ABSTRACT)
        		.withBinding("op2")
        		.build();
        Operation operation3 = newOperationBuilder().withName("op3")
        		.withInput(newParameterBuilder().withName("input").withTarget(inputType).withUpper(-1).build())
        		.withOutput(newParameterBuilder().withName("output").withTarget(outputType).withUpper(-1).build())
        		.withBinding("op3")
        		.build();
        Operation operation1impl = newOperationBuilder().withName("op1")
        		.withInput(newParameterBuilder().withName("input").withTarget(inputType).withUpper(-1).build())
        		.withBinding("op1")
        		.build();
        Operation operation2impl = newOperationBuilder().withName("op2")
        		.withOutput(newParameterBuilder().withName("output").withTarget(outputType).withUpper(-1).build())
        		.withBinding("op2")
        		.build();
        
        useEntityType(e1).withOperations(operation1,operation2).build();
        
        final EntityType e2 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_2)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withAbstract_(true)
                .withGeneralizations(newGeneralizationBuilder().withTarget(e1).build())
                .build();
        useEntityType(e2).withMappedEntity(e2).build();
        final EntityType e3 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_3)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withGeneralizations(newGeneralizationBuilder().withTarget(e2).build())
                .build();
        useEntityType(e3).withMappedEntity(e3).build();
        useEntityType(e3).withOperations(operation3, operation1impl, operation2impl).build();
        
        Access access3 = newAccessBuilder().withName("e3")
        		.withTarget(e3)
        		.withLower(0)
        		.withUpper(-1)
        		.withCreateable(false)
        		.withTargetDefinedCRUD(false)
        		.build();
        Access access2 = newAccessBuilder().withName("e2")
        		.withTarget(e2)
        		.withLower(0)
        		.withUpper(-1)
        		.withCreateable(false)
        		.withTargetDefinedCRUD(false)
        		.build();

        useActorType(actor).withAccesses(access2, access3).build();
        
        final Model model = newModelBuilder().withName(MODEL_NAME)
                .withElements(actor, e1, e2, e3, inputType, outputType, str).build();

        esmModel.addContent(model);

        transform();

        final Optional<Application> application = allUi(Application.class).filter(a -> a.getName().equals(actor.getFQName()))
                .findAny();
        assertTrue(application.isPresent());
        
        final Optional<ClassType> uiE3 = application.get().getDataElements().stream().filter(e -> e instanceof ClassType)
        		.map(e -> (ClassType) e).filter(c -> c.getName().equals(EsmUtils.getNamespaceElementFQName(e3))).findAny();
        assertTrue(uiE3.isPresent());
        assertEquals(3, uiE3.get().getOperations().size());
        
        final Optional<OperationType> uiOp1 = uiE3.get().getOperations().stream().filter(o -> o.getName().equals(operation1.getName())).findFirst();
        assertTrue(uiOp1.isPresent());
        assertNotNull(uiOp1.get().getInput());
        assertTrue(uiOp1.get().getInput().getTarget().getName().equals(EsmUtils.getNamespaceElementFQName(inputType)));
        assertNull(uiOp1.get().getOutput());
        
        final Optional<OperationType> uiOp2 = uiE3.get().getOperations().stream().filter(o -> o.getName().equals(operation2.getName())).findFirst();
        assertTrue(uiOp2.isPresent());
        assertNotNull(uiOp2.get().getOutput());
        assertTrue(uiOp2.get().getOutput().getTarget().getName().equals(EsmUtils.getNamespaceElementFQName(outputType)));
        assertNull(uiOp2.get().getInput());
        
        final Optional<OperationType> uiOp3 = uiE3.get().getOperations().stream().filter(o -> o.getName().equals(operation3.getName())).findFirst();
        assertTrue(uiOp3.isPresent());
        assertNotNull(uiOp3.get().getInput());
        assertTrue(uiOp3.get().getInput().getTarget().getName().equals(EsmUtils.getNamespaceElementFQName(inputType)));
        assertNotNull(uiOp3.get().getOutput());
        assertTrue(uiOp3.get().getOutput().getTarget().getName().equals(EsmUtils.getNamespaceElementFQName(outputType)));
        
        final Optional<ClassType> uiE2 = application.get().getDataElements().stream().filter(e -> e instanceof ClassType)
        		.map(e -> (ClassType) e).filter(c -> c.getName().equals(EsmUtils.getNamespaceElementFQName(e2))).findAny();
        assertTrue(uiE2.isPresent());
        assertEquals(2, uiE2.get().getOperations().size());
        
        final Optional<OperationType> uiOp4 = uiE2.get().getOperations().stream().filter(o -> o.getName().equals(operation1.getName())).findFirst();
        assertTrue(uiOp4.isPresent());
        assertNotNull(uiOp4.get().getInput());
        assertTrue(uiOp4.get().getInput().getTarget().getName().equals(EsmUtils.getNamespaceElementFQName(inputType)));
        assertNull(uiOp4.get().getOutput());
        
        final Optional<OperationType> uiOp5 = uiE2.get().getOperations().stream().filter(o -> o.getName().equals(operation2.getName())).findFirst();
        assertTrue(uiOp5.isPresent());
        assertNotNull(uiOp5.get().getOutput());
        assertTrue(uiOp5.get().getOutput().getTarget().getName().equals(EsmUtils.getNamespaceElementFQName(outputType)));
        assertNull(uiOp5.get().getInput());
        
        final Optional<ClassType> uiE1 = application.get().getDataElements().stream().filter(e -> e instanceof ClassType)
        		.map(e -> (ClassType) e).filter(c -> c.getName().equals(EsmUtils.getNamespaceElementFQName(e1))).findAny();
        assertFalse(uiE1.isPresent());
    }
    
    @Test
    void testCreateClassTypeFromClassWithAttributes() throws Exception {
        testName = "CreateClassTypeFromClass";

        StringType str = newStringTypeBuilder().withName("string").withMaxLength(8).build();
        NumericType number = newNumericTypeBuilder().withName("number").withPrecision(2).withScale(1).build();
        final String MODEL_NAME = "Model";
        final String TRANSFER_OBJECT_TYPE_NAME_1 = "T1";
        final String TRANSFER_OBJECT_TYPE_NAME_2 = "T2";
        final String TRANSFER_OBJECT_TYPE_NAME_3 = "T3";
        final String ENTITY_TYPE_NAME_1 = "E1";
        final String ENTITY_TYPE_NAME_2 = "E2";
        final String ENTITY_TYPE_NAME_3 = "E3";
        final String ENTITY_TYPE_NAME_4 = "E4";
        final String ENTITY_TYPE_NAME_5 = "E5";
        final String ENTITY_TYPE_NAME_6 = "E6";
        final String ACTOR_TYPE_NAME = "Actor";
        
        ActorType actor = newActorTypeBuilder()
                .withName(ACTOR_TYPE_NAME)
                .withRealm("sandbox")
                .build();
        
        DataMember stored1 = newDataMemberBuilder().withName("stored").withDataType(str).withMemberType(MemberType.STORED).build();
        DataMember derived1 = newDataMemberBuilder().withName("derived").withDataType(number).withGetterExpression("1+1").withMemberType(MemberType.DERIVED).build();
        final EntityType e1 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_1)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withAttributes(stored1, derived1)
                .build();
        useEntityType(e1).withMappedEntity(e1)
        	.withAttributes(stored1, derived1).build();
        final EntityType e2 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_2)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withGeneralizations(newGeneralizationBuilder().withTarget(e1).build())
                .build();
        useEntityType(e2).withMappedEntity(e2).build();
        DataMember stored2 = newDataMemberBuilder().withName("stored2").withDataType(str).withMemberType(MemberType.STORED).build();
        DataMember derived2 = newDataMemberBuilder().withName("derived2").withDataType(number).withGetterExpression("1+1").withMemberType(MemberType.DERIVED).build();
        final EntityType e3 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_3)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withGeneralizations(newGeneralizationBuilder().withTarget(e2).build())
                .build();
        useEntityType(e3).withMappedEntity(e3)
        	.withAttributes(stored2, derived2).build();
        
        DataMember stored3 = newDataMemberBuilder().withName("stored").withDataType(str).withMemberType(MemberType.STORED).build();
        DataMember derived3 = newDataMemberBuilder().withName("derived").withDataType(number).withGetterExpression("1+1").withMemberType(MemberType.DERIVED).build();
        final EntityType e4 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_4)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .build();
        useEntityType(e4).withMappedEntity(e4)
        	.withAttributes(stored3, derived3).build();
        
        final EntityType e5 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_5)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .build();
        useEntityType(e5).withMappedEntity(e5).build();
        
        final EntityType e6 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_6)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withGeneralizations(newGeneralizationBuilder().withTarget(e5).build())
                .build();
        useEntityType(e6).withMappedEntity(e6).build();
        
        OneWayRelationMember relation = newOneWayRelationMemberBuilder()
        		.withName("e5s")
        		.withLower(0)
        		.withUpper(-1)
        		.withTarget(e5)
        		.withMemberType(MemberType.STORED)
        		.withRelationKind(RelationKind.ASSOCIATION)
        		.build();
        useEntityType(e4).withRelations(relation).build();

        DataMember stored1mapping = newDataMemberBuilder().withName("stored").withDataType(str).withMemberType(MemberType.MAPPED)
        		.withBinding(stored1).build();
        final TransferObjectType t1 = newTransferObjectTypeBuilder()
                .withName(TRANSFER_OBJECT_TYPE_NAME_1)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .build();
        useTransferObjectType(t1).withMappedEntity(e1)
        	.withAttributes(stored1mapping).build();
        final TransferObjectType t2 = newTransferObjectTypeBuilder()
                .withName(TRANSFER_OBJECT_TYPE_NAME_2)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withGeneralizations(newGeneralizationBuilder().withTarget(t1).build())
                .build();
        useTransferObjectType(t2).withMappedEntity(e2).build();
        DataMember stored2mapping = newDataMemberBuilder().withName("stored2").withDataType(str).withMemberType(MemberType.MAPPED)
        		.withBinding(stored2).build();
        DataMember derived2mapping = newDataMemberBuilder().withName("derived2").withDataType(number).withGetterExpression("1+1").withMemberType(MemberType.MAPPED)
        		.withBinding(derived2).build();
        DataMember transientMember = newDataMemberBuilder().withName("transient").withDataType(str).withMemberType(MemberType.TRANSIENT)
        		.withBinding(stored2).build();
        final TransferObjectType t3 = newTransferObjectTypeBuilder()
                .withName(TRANSFER_OBJECT_TYPE_NAME_3)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withGeneralizations(newGeneralizationBuilder().withTarget(t2).build())
                .build();
        useTransferObjectType(t3).withMappedEntity(e3)
        	.withAttributes(stored2mapping, derived2mapping, transientMember).build();
        
        Access access1 = newAccessBuilder().withName("t1")
        		.withTarget(t1)
        		.withLower(0)
        		.withUpper(-1)
        		.withCreateable(true)
        		.withTargetDefinedCRUD(false)
        		.build();
        
        Access access2 = newAccessBuilder().withName("t3")
        		.withTarget(t3)
        		.withLower(0)
        		.withUpper(-1)
        		.withCreateable(false)
        		.withTargetDefinedCRUD(false)
        		.build();
        
        Access access3 = newAccessBuilder().withName("e4")
        		.withTarget(e4)
        		.withLower(0)
        		.withUpper(-1)
        		.withCreateable(false)
        		.withTargetDefinedCRUD(false)
        		.build();

        useActorType(actor).withAccesses(access1, access2, access3).build();
        
        final Model model = newModelBuilder().withName(MODEL_NAME)
                .withElements(actor, t1, t2, t3, e1, e2, e3, e4, e5, e6, str, number).build();

        esmModel.addContent(model);

        transform();

        final Optional<Application> application = allUi(Application.class).filter(a -> a.getName().equals(actor.getFQName()))
                .findAny();
        assertTrue(application.isPresent());
        
        final Optional<ClassType> uiT1 = application.get().getDataElements().stream().filter(e -> e instanceof ClassType)
        		.map(e -> (ClassType) e).filter(c -> c.getName().equals(EsmUtils.getNamespaceElementFQName(t1))).findAny();
        assertTrue(uiT1.isPresent());
        assertTrue(uiT1.get().isIsMapped());
        assertEquals(1, uiT1.get().getAttributes().size());
        assertTrue(uiT1.get().getAttributes().get(0).getIsMemberTypeMapped());
        assertTrue(uiT1.get().getAttributes().get(0).getName().equals(stored1mapping.getName()));
        
        final Optional<ClassType> uiT2 = application.get().getDataElements().stream().filter(e -> e instanceof ClassType)
        		.map(e -> (ClassType) e).filter(c -> c.getName().equals(EsmUtils.getNamespaceElementFQName(t2))).findAny();
        assertFalse(uiT2.isPresent());
        
        final Optional<ClassType> uiT3 = application.get().getDataElements().stream().filter(e -> e instanceof ClassType)
        		.map(e -> (ClassType) e).filter(c -> c.getName().equals(EsmUtils.getNamespaceElementFQName(t3))).findAny();
        assertTrue(uiT3.isPresent());
        assertTrue(uiT3.get().isIsMapped());
        assertEquals(4, uiT3.get().getAttributes().size());
        assertTrue(uiT3.get().getAttributes().stream().anyMatch(a -> a.getName().equals(stored1mapping.getName()) && a.getIsMemberTypeMapped()));
        assertTrue(uiT3.get().getAttributes().stream().anyMatch(a -> a.getName().equals(stored2mapping.getName()) && a.getIsMemberTypeMapped()));
        assertTrue(uiT3.get().getAttributes().stream().anyMatch(a -> a.getName().equals(derived2mapping.getName()) && a.getIsMemberTypeMapped()));
        assertTrue(uiT3.get().getAttributes().stream().anyMatch(a -> a.getName().equals(transientMember.getName()) && a.getIsMemberTypeTransient()));
        
        final Optional<ClassType> uiE1 = application.get().getDataElements().stream().filter(e -> e instanceof ClassType)
        		.map(e -> (ClassType) e).filter(c -> c.getName().equals(EsmUtils.getNamespaceElementFQName(e1))).findAny();
        assertFalse(uiE1.isPresent());
        
        final Optional<ClassType> uiE2 = application.get().getDataElements().stream().filter(e -> e instanceof ClassType)
        		.map(e -> (ClassType) e).filter(c -> c.getName().equals(EsmUtils.getNamespaceElementFQName(e2))).findAny();
        assertFalse(uiE2.isPresent());
        
        final Optional<ClassType> uiE3 = application.get().getDataElements().stream().filter(e -> e instanceof ClassType)
        		.map(e -> (ClassType) e).filter(c -> c.getName().equals(EsmUtils.getNamespaceElementFQName(e3))).findAny();
        assertFalse(uiE3.isPresent());
        
        final Optional<ClassType> uiE4 = application.get().getDataElements().stream().filter(e -> e instanceof ClassType)
        		.map(e -> (ClassType) e).filter(c -> c.getName().equals(EsmUtils.getNamespaceElementFQName(e4))).findAny();
        assertTrue(uiE4.isPresent());
        assertTrue(uiE4.get().isIsMapped());
        assertEquals(2, uiE4.get().getAttributes().size());
        assertTrue(uiE4.get().getAttributes().stream().anyMatch(a -> a.getName().equals(stored3.getName()) && a.getIsMemberTypeStored()));
        assertTrue(uiE4.get().getAttributes().stream().anyMatch(a -> a.getName().equals(derived3.getName()) && a.getIsMemberTypeDerived()));
        
        final Optional<ClassType> uiE5 = application.get().getDataElements().stream().filter(e -> e instanceof ClassType)
        		.map(e -> (ClassType) e).filter(c -> c.getName().equals(EsmUtils.getNamespaceElementFQName(e5))).findAny();
        assertTrue(uiE5.isPresent());
        
        final Optional<ClassType> uiE6 = application.get().getDataElements().stream().filter(e -> e instanceof ClassType)
        		.map(e -> (ClassType) e).filter(c -> c.getName().equals(EsmUtils.getNamespaceElementFQName(e6))).findAny();
        assertFalse(uiE6.isPresent());
    }
    
    @Test
    void testCreateTypes() throws Exception {
        testName = "MapDataType";

        final String MODEL_NAME = "Model";
        final String ENTITY_TYPE_NAME_1 = "E1";
        final String ACTOR_TYPE_NAME_1 = "A1";
        final String ACTOR_TYPE_NAME_2 = "A2";
        //TODO JNG-2228
        XMLType xmlType = newXMLTypeBuilder().withName("xml").withXmlNamespace("namespace").withXmlElement("element").build();
        //TODO JNG-2229
        CustomType custom = newCustomTypeBuilder().withName("custom").build();
        
        PasswordType password = newPasswordTypeBuilder().withName("password").build();
        StringType string = newStringTypeBuilder().withName("string")
        		.withMaxLength(256)
        		.withRegExp(".*")
        		.build();
        NumericType numeric = newNumericTypeBuilder().withName("numeric")
        		.withPrecision(2)
        		.withScale(1)
        		.build();
        BooleanType booleanType = newBooleanTypeBuilder().withName("boolean").build();
        DateType dateType = newDateTypeBuilder().withName("date").build();
        TimestampType timestamp = newTimestampTypeBuilder().withName("timestamp").withBaseUnit(DurationType.HOUR)
        		.build();
        EnumerationType enumeration = newEnumerationTypeBuilder().withName("enum")
        		.withMembers(ImmutableList.of(
        				newEnumerationMemberBuilder().withName("m1").withOrdinal(1).build(),
        				newEnumerationMemberBuilder().withName("m2").withOrdinal(2).build()
        				))
        		.build();
//TODO JNG-2228
//        DataMember xmlAttribute = newDataMemberBuilder().withName("xml").withDataType(xmlType)
//        		.withMemberType(MemberType.STORED).build();
//TODO JNG-2229
//        DataMember customAttribute = newDataMemberBuilder().withName("custom").withDataType(custom)
//        		.withMemberType(MemberType.STORED).build();
        DataMember passwordAttribute = newDataMemberBuilder().withName("password").withDataType(password)
        		.withMemberType(MemberType.STORED).build();
        DataMember stringAttribute = newDataMemberBuilder().withName("string").withDataType(string)
        		.withMemberType(MemberType.STORED).build();
        DataMember numericAttribute = newDataMemberBuilder().withName("numeric").withDataType(numeric)
        		.withMemberType(MemberType.STORED).build();
        DataMember booleanAttribute = newDataMemberBuilder().withName("boolean").withDataType(booleanType)
        		.withMemberType(MemberType.STORED).build();
        DataMember dateAttribute = newDataMemberBuilder().withName("date").withDataType(dateType)
        		.withMemberType(MemberType.STORED).build();
        DataMember timestampAttribute = newDataMemberBuilder().withName("timestamp").withDataType(timestamp)
        		.withMemberType(MemberType.STORED).build();
        DataMember enumerationAttribute = newDataMemberBuilder().withName("enumeration").withDataType(enumeration)
        		.withMemberType(MemberType.STORED).build();
        
        ActorType actor = newActorTypeBuilder()
                .withName(ACTOR_TYPE_NAME_1)
                .withRealm("sandbox")
                .build();
        ActorType actor2 = newActorTypeBuilder()
                .withName(ACTOR_TYPE_NAME_2)
                .withRealm("sandbox")
                .build();
        
        final EntityType e1 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_1)
                .build();
        useEntityType(e1).withMappedEntity(e1).build();
        useEntityType(e1).withAttributes(passwordAttribute, stringAttribute, numericAttribute, booleanAttribute, dateAttribute, timestampAttribute, enumerationAttribute).build();
        
        Access access1 = newAccessBuilder().withName("e1")
        		.withTarget(e1)
        		.withLower(0)
        		.withUpper(-1)
        		.withCreateable(true).withUpdateable(true).withDeleteable(true)
        		.withTargetDefinedCRUD(false)
        		.build();
        useActorType(actor).withAccesses(access1).build();
        Access access2 = newAccessBuilder().withName("e1")
        		.withTarget(e1)
        		.withLower(0)
        		.withUpper(-1)
        		.withCreateable(true).withUpdateable(true).withDeleteable(true)
        		.withTargetDefinedCRUD(false)
        		.build();
        useActorType(actor2).withAccesses(access2).build();
        
        SimpleOrderModel.setFormForTransferObjectType(e1);
        
        final Model model = newModelBuilder().withName(MODEL_NAME)
                .withElements(actor, actor2, e1, xmlType, password, string, numeric, dateType, custom, booleanType, enumeration, timestamp).build();

        esmModel.addContent(model);

        transform();
        
        final Optional<Application> application = allUi(Application.class).filter(a -> a.getName().equals(actor.getFQName()))
                .findAny();
        assertTrue(application.isPresent());
        
        Optional<DataType> uiPassword = application.get().getDataTypes().stream().filter(e -> e instanceof hu.blackbelt.judo.meta.ui.data.PasswordType).findAny();
        assertTrue(uiPassword.isPresent());
        Optional<DataType> uiString = application.get().getDataTypes().stream().filter(e -> e instanceof hu.blackbelt.judo.meta.ui.data.StringType).findAny();
        assertTrue(uiString.isPresent());
        assertEquals(string.getMaxLength(), ((hu.blackbelt.judo.meta.ui.data.StringType)uiString.get()).getMaxLength());
        assertEquals(string.getRegExp(), ((hu.blackbelt.judo.meta.ui.data.StringType)uiString.get()).getRegExp());
        Optional<DataType> uiNumeric = application.get().getDataTypes().stream().filter(e -> e instanceof hu.blackbelt.judo.meta.ui.data.NumericType).findAny();
        assertTrue(uiNumeric.isPresent());
        assertEquals(numeric.getPrecision(), ((hu.blackbelt.judo.meta.ui.data.NumericType) uiNumeric.get()).getPrecision());
        assertEquals(numeric.getScale(), ((hu.blackbelt.judo.meta.ui.data.NumericType) uiNumeric.get()).getScale());
        Optional<DataType> uiBbooleanType = application.get().getDataTypes().stream().filter(e -> e instanceof hu.blackbelt.judo.meta.ui.data.BooleanType).findAny();
        assertTrue(uiBbooleanType.isPresent());
        Optional<DataType> uiDateType = application.get().getDataTypes().stream().filter(e -> e instanceof hu.blackbelt.judo.meta.ui.data.DateType).findAny();
        assertTrue(uiDateType.isPresent());
        Optional<DataType> uiTimestamp = application.get().getDataTypes().stream().filter(e -> e instanceof hu.blackbelt.judo.meta.ui.data.TimestampType).findAny();
        assertTrue(uiTimestamp.isPresent());
        Optional<DataType> uiEnumeration = application.get().getDataTypes().stream().filter(e -> e instanceof hu.blackbelt.judo.meta.ui.data.EnumerationType).findAny();
        assertTrue(uiEnumeration.isPresent());
        
        final Optional<Application> application2 = allUi(Application.class).filter(a -> a.getName().equals(actor2.getFQName()))
                .findAny();
        assertTrue(application2.isPresent());
        
        Optional<DataType> uiPassword2 = application2.get().getDataTypes().stream().filter(e -> e instanceof hu.blackbelt.judo.meta.ui.data.PasswordType).findAny();
        assertTrue(uiPassword2.isPresent());
        Optional<DataType> uiString2 = application2.get().getDataTypes().stream().filter(e -> e instanceof hu.blackbelt.judo.meta.ui.data.StringType).findAny();
        assertTrue(uiString2.isPresent());
        assertEquals(string.getMaxLength(), ((hu.blackbelt.judo.meta.ui.data.StringType)uiString2.get()).getMaxLength());
        assertEquals(string.getRegExp(), ((hu.blackbelt.judo.meta.ui.data.StringType)uiString2.get()).getRegExp());
        Optional<DataType> uiNumeric2 = application2.get().getDataTypes().stream().filter(e -> e instanceof hu.blackbelt.judo.meta.ui.data.NumericType).findAny();
        assertTrue(uiNumeric2.isPresent());
        assertEquals(numeric.getPrecision(), ((hu.blackbelt.judo.meta.ui.data.NumericType) uiNumeric2.get()).getPrecision());
        assertEquals(numeric.getScale(), ((hu.blackbelt.judo.meta.ui.data.NumericType) uiNumeric2.get()).getScale());
        Optional<DataType> uiBbooleanType2 = application2.get().getDataTypes().stream().filter(e -> e instanceof hu.blackbelt.judo.meta.ui.data.BooleanType).findAny();
        assertTrue(uiBbooleanType2.isPresent());
        Optional<DataType> uiDateType2 = application2.get().getDataTypes().stream().filter(e -> e instanceof hu.blackbelt.judo.meta.ui.data.DateType).findAny();
        assertTrue(uiDateType2.isPresent());
        Optional<DataType> uiTimestamp2 = application2.get().getDataTypes().stream().filter(e -> e instanceof hu.blackbelt.judo.meta.ui.data.TimestampType).findAny();
        assertTrue(uiTimestamp2.isPresent());
        Optional<DataType> uiEnumeration2 = application2.get().getDataTypes().stream().filter(e -> e instanceof hu.blackbelt.judo.meta.ui.data.EnumerationType).findAny();
        assertTrue(uiEnumeration2.isPresent());
    }
    
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
