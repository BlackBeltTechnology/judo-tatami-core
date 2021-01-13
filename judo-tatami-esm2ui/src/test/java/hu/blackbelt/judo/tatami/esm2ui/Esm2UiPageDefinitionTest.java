package hu.blackbelt.judo.tatami.esm2ui;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.accesspoint.Access;
import hu.blackbelt.judo.meta.esm.accesspoint.ActorType;
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
import hu.blackbelt.judo.meta.esm.type.StringType;
import hu.blackbelt.judo.meta.ui.Application;
import hu.blackbelt.judo.meta.ui.PageDefinition;
import hu.blackbelt.judo.meta.ui.PageType;
import hu.blackbelt.judo.meta.ui.data.ClassType;
import hu.blackbelt.judo.meta.ui.data.OperationParameterType;
import hu.blackbelt.judo.meta.ui.data.OperationType;
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

import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.*;

import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.SaveArguments.esmSaveArgumentsBuilder;

import static hu.blackbelt.judo.meta.ui.runtime.UiEpsilonValidator.calculateUiValidationScriptURI;
import static hu.blackbelt.judo.meta.ui.runtime.UiEpsilonValidator.validateUi;
import static hu.blackbelt.judo.meta.ui.runtime.UiModel.SaveArguments.uiSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.ui.runtime.UiModel.buildUiModel;
import static hu.blackbelt.judo.tatami.esm2ui.Esm2Ui.calculateEsm2UiTransformationScriptURI;
import static hu.blackbelt.judo.tatami.esm2ui.Esm2Ui.executeEsm2UiTransformation;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class Esm2UiPageDefinitionTest {
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
    void testCreateParameterPages() throws Exception {
        testName = "CreateParameterPages";
        
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
        
        Operation operation1 = newOperationBuilder().withName("op1")
        		.withInput(newParameterBuilder().withName("input").withTarget(inputType).withUpper(-1).build())
        		.withInherited(newInheritedOperationReferenceBuilder().build())
        		.withBinding("op1")
        		.build();
        Operation operation2 = newOperationBuilder().withName("op2")
        		.withOutput(newParameterBuilder().withName("output").withTarget(outputType).withUpper(-1).build())
        		.withInherited(newInheritedOperationReferenceBuilder().build())
        		.withBinding("op2")
        		.build();
        Operation operation3 = newOperationBuilder().withName("op3")
        		.withInput(newParameterBuilder().withName("input").withTarget(inputType).withUpper(-1).build())
        		.withOutput(newParameterBuilder().withName("output").withTarget(outputType).withUpper(-1).build())
        		.withInherited(newInheritedOperationReferenceBuilder().build())
        		.withBinding("op3")
        		.build();
        
        final EntityType e1 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_1)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withView(
                		newTransferObjectViewBuilder()
                		.withName("e1View")
                		.withComponents(
                				newGroupBuilder()
                				.withName("g")
                				.withComponents(
                						newOperationFormBuilder()
                						.withName("op1")
                						.withOperation("op1")
                						.build(),
                						newOperationFormBuilder()
                						.withName("op2")
                						.withOperation("op2")
                						.build())
                				.build()))
                .build();
        useEntityType(e1).withMappedEntity(e1).build();
        useEntityType(e1).withOperations(operation1,operation2).build();
        
        final EntityType e2 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_2)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withGeneralizations(newGeneralizationBuilder().withTarget(e1).build())
                .withView(
                		newTransferObjectViewBuilder()
                		.withName("e2View")
                		.withComponents(
                				newGroupBuilder()
                				.withName("g")
                				.withComponents(
                						newOperationFormBuilder()
                						.withName("op1")
                						.withOperation("op1")
                						.build(),
                						newOperationFormBuilder()
                						.withName("op2")
                						.withOperation("op2")
                						.build())
                				.build()))
                .build();
        useEntityType(e2).withMappedEntity(e2).build();
        final EntityType e3 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_3)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withGeneralizations(newGeneralizationBuilder().withTarget(e2).build())
                .withView(
                		newTransferObjectViewBuilder()
                		.withName("e3View")
                		.withComponents(
                				newGroupBuilder()
                				.withName("g")
                				.withComponents(
                						newOperationFormBuilder()
                						.withName("op1")
                						.withOperation("op1")
                						.build(),
                						newOperationFormBuilder()
                						.withName("op2")
                						.withOperation("op2")
                						.build(),
                						newOperationFormBuilder()
                						.withName("op3")
                						.withOperation("op3")
                						.build())
                				.build()))
                .build();
        useEntityType(e3).withMappedEntity(e3).build();
        useEntityType(e3).withOperations(operation3).build();
        
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
        final Optional<ClassType> uiE1 = application.get().getDataElements().stream().filter(e -> e instanceof ClassType)
        		.map(e -> (ClassType) e).filter(c -> c.getName().equals(EsmUtils.getNamespaceElementFQName(e1))).findAny();
        assertFalse(uiE1.isPresent());
        final Optional<ClassType> uiE2 = application.get().getDataElements().stream().filter(e -> e instanceof ClassType)
        		.map(e -> (ClassType) e).filter(c -> c.getName().equals(EsmUtils.getNamespaceElementFQName(e2))).findAny();
        assertTrue(uiE2.isPresent());
        final Optional<ClassType> uiE3 = application.get().getDataElements().stream().filter(e -> e instanceof ClassType)
        		.map(e -> (ClassType) e).filter(c -> c.getName().equals(EsmUtils.getNamespaceElementFQName(e3))).findAny();
        assertTrue(uiE3.isPresent());
        
        final Optional<PageDefinition> e2op1InputPage = application.get().getPages().stream()
        		.filter(c -> c.getPageType().equals(PageType.OPERATION_INPUT) && c.getName().endsWith(e2.getName()) && c.getName().contains(operation1.getName())).findAny();
        assertTrue(e2op1InputPage.isPresent());
        assertTrue(e2op1InputPage.get().getDataElement() instanceof OperationParameterType);
        assertTrue(e2op1InputPage.get().getDataElement().eContainer().eContainer().equals(uiE2.get()));
        
        final Optional<PageDefinition> e2op2OutputPage = application.get().getPages().stream()
        		.filter(c -> c.getPageType().equals(PageType.OPERATION_OUTPUT) && c.getName().endsWith(e2.getName()) && c.getName().contains(operation2.getName())).findAny();
        assertTrue(e2op2OutputPage.isPresent());
        assertTrue(e2op2OutputPage.get().getDataElement() instanceof OperationParameterType);
        assertTrue(e2op2OutputPage.get().getDataElement().eContainer().eContainer().equals(uiE2.get()));
        
        final Optional<PageDefinition> e3op1InputPage = application.get().getPages().stream()
        		.filter(c -> c.getPageType().equals(PageType.OPERATION_INPUT) && c.getName().endsWith(e3.getName()) && c.getName().contains(operation1.getName())).findAny();
        assertTrue(e3op1InputPage.isPresent());
        assertTrue(e3op1InputPage.get().getDataElement() instanceof OperationParameterType);
        assertTrue(e3op1InputPage.get().getDataElement().eContainer().eContainer().equals(uiE3.get()));
        
        final Optional<PageDefinition> e3op2OutputPage = application.get().getPages().stream()
        		.filter(c -> c.getPageType().equals(PageType.OPERATION_OUTPUT) && c.getName().endsWith(e3.getName()) && c.getName().contains(operation2.getName())).findAny();
        assertTrue(e3op2OutputPage.isPresent());
        assertTrue(e3op2OutputPage.get().getDataElement() instanceof OperationParameterType);
        assertTrue(e3op2OutputPage.get().getDataElement().eContainer().eContainer().equals(uiE3.get()));
        
        final Optional<PageDefinition> e3op3InputPage = application.get().getPages().stream()
        		.filter(c -> c.getPageType().equals(PageType.OPERATION_INPUT) && c.getName().endsWith(e3.getName()) && c.getName().contains(operation3.getName())).findAny();
        assertTrue(e3op3InputPage.isPresent());
        assertTrue(e3op3InputPage.get().getDataElement() instanceof OperationParameterType);
        assertTrue(e3op3InputPage.get().getDataElement().eContainer().eContainer().equals(uiE3.get()));
        
        final Optional<PageDefinition> e3op3OutputPage = application.get().getPages().stream()
        		.filter(c -> c.getPageType().equals(PageType.OPERATION_OUTPUT) && c.getName().endsWith(e3.getName()) && c.getName().contains(operation3.getName())).findAny();
        assertTrue(e3op3OutputPage.isPresent());
        assertTrue(e3op3OutputPage.get().getDataElement() instanceof OperationParameterType);
        assertTrue(e3op3OutputPage.get().getDataElement().eContainer().eContainer().equals(uiE3.get()));
    }
    
    @Test
    void testCreatePageDefinitionFromReferenceTypedElement() throws Exception {
        testName = "CreatePageDefinitionFromReferenceTypedElement";
        
        StringType str = newStringTypeBuilder().withName("string").withMaxLength(8).build();
        
        final String MODEL_NAME = "Model";
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
        
        final EntityType e1 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_1)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withAttributes(
                		newDataMemberBuilder().withName("id")
                			.withDataType(str)
                			.withMemberType(MemberType.STORED)
                			.build())
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
                .withAttributes(
                		newDataMemberBuilder().withName("id")
                			.withDataType(str)
                			.withMemberType(MemberType.STORED)
                			.build())
                .build();
        useEntityType(e3).withMappedEntity(e3).build();
        
        final EntityType e4 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_4)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withAttributes(
                		newDataMemberBuilder().withName("id")
                			.withDataType(str)
                			.withMemberType(MemberType.STORED)
                			.build())
                .build();
        useEntityType(e4).withMappedEntity(e4).build();
        
        final EntityType e5 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_5)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withAttributes(
                		newDataMemberBuilder().withName("id")
                			.withDataType(str)
                			.withMemberType(MemberType.STORED)
                			.build())
                .build();
        useEntityType(e5).withMappedEntity(e5).build();
        
        final EntityType e6 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_6)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withAttributes(
                		newDataMemberBuilder().withName("id")
                			.withDataType(str)
                			.withMemberType(MemberType.STORED)
                			.build())
                .build();
        useEntityType(e6).withMappedEntity(e6).build();

        Access access1 = newAccessBuilder().withName("e2")
        		.withTarget(e2)
        		.withLower(0)
        		.withUpper(-1)
        		.withCreateable(true)
        		.withTargetDefinedCRUD(false)
        		.build();
        
        OneWayRelationMember relation1 = newOneWayRelationMemberBuilder()
        		.withName("relation1")
        		.withLower(0)
        		.withUpper(-1)
        		.withTarget(e3)
        		.withMemberType(MemberType.STORED)
        		.withRelationKind(RelationKind.ASSOCIATION)
        		.withCreateable(true)
        		.build();
        OneWayRelationMember relation2 = newOneWayRelationMemberBuilder()
        		.withName("relation2")
        		.withLower(0)
        		.withUpper(-1)
        		.withTarget(e4)
        		.withMemberType(MemberType.STORED)
        		.withRelationKind(RelationKind.COMPOSITION)
        		.build();
        OneWayRelationMember relation3 = newOneWayRelationMemberBuilder()
        		.withName("relation3")
        		.withLower(0)
        		.withUpper(-1)
        		.withTarget(e5)
        		.withMemberType(MemberType.DERIVED)
        		.withRelationKind(RelationKind.ASSOCIATION)
        		.withDeleteable(true)
        		.withGetterExpression(MODEL_NAME + "::" + ENTITY_TYPE_NAME_5)
        		.build();
        OneWayRelationMember relation4 = newOneWayRelationMemberBuilder()
        		.withName("relation4")
        		.withLower(0)
        		.withUpper(1)
        		.withTarget(e6)
        		.withMemberType(MemberType.DERIVED)
        		.withRelationKind(RelationKind.AGGREGATION)
        		.withCreateable(true)
        		.withUpdateable(true)
        		.withGetterExpression(MODEL_NAME + "::" + ENTITY_TYPE_NAME_6)
        		.build();
        useEntityType(e1).withRelations(relation1, relation2, relation3).build();
        useEntityType(e2).withRelations(relation4).build();

        useActorType(actor).withAccesses(access1).build();
       
        final Model model = newModelBuilder().withName(MODEL_NAME)
                .withElements(actor, e1, e2, e3, e4, e5, e6, str).build();
        SimpleOrderModel.addUiElementsToTransferObjects(model);
        
        esmModel.addContent(model);

        transform();
        
        final Optional<Application> application = allUi(Application.class).filter(a -> a.getName().equals(actor.getFQName()))
                .findAny();
        assertTrue(application.isPresent());
        final Optional<ClassType> uiE1 = application.get().getDataElements().stream().filter(e -> e instanceof ClassType)
        		.map(e -> (ClassType) e).filter(c -> c.getName().equals(EsmUtils.getNamespaceElementFQName(e1))).findAny();
        assertFalse(uiE1.isPresent());
        final Optional<ClassType> uiE2 = application.get().getDataElements().stream().filter(e -> e instanceof ClassType)
        		.map(e -> (ClassType) e).filter(c -> c.getName().equals(EsmUtils.getNamespaceElementFQName(e2))).findAny();
        assertTrue(uiE2.isPresent());
        final Optional<ClassType> uiE3 = application.get().getDataElements().stream().filter(e -> e instanceof ClassType)
        		.map(e -> (ClassType) e).filter(c -> c.getName().equals(EsmUtils.getNamespaceElementFQName(e3))).findAny();
        assertTrue(uiE3.isPresent());
        final Optional<ClassType> uiE4 = application.get().getDataElements().stream().filter(e -> e instanceof ClassType)
        		.map(e -> (ClassType) e).filter(c -> c.getName().equals(EsmUtils.getNamespaceElementFQName(e4))).findAny();
        assertTrue(uiE4.isPresent());
        final Optional<ClassType> uiE5 = application.get().getDataElements().stream().filter(e -> e instanceof ClassType)
        		.map(e -> (ClassType) e).filter(c -> c.getName().equals(EsmUtils.getNamespaceElementFQName(e5))).findAny();
        assertTrue(uiE5.isPresent());
        final Optional<ClassType> uiE6 = application.get().getDataElements().stream().filter(e -> e instanceof ClassType)
        		.map(e -> (ClassType) e).filter(c -> c.getName().equals(EsmUtils.getNamespaceElementFQName(e6))).findAny();
        assertTrue(uiE6.isPresent());
        
        //exposed stored creatable association: view, create, table
        final Optional<PageDefinition> rel1Page = application.get().getPages().stream()
        		.filter(c -> c.getPageType().equals(PageType.VIEW) && c.getName().equals(EsmUtils.getNamespaceElementFQName(e2) + "." + relation1.getName() + "#View")).findAny();
        assertTrue(rel1Page.isPresent());
        assertTrue(uiE2.get().getRelations().contains(rel1Page.get().getDataElement()) && rel1Page.get().getDataElement().getName().equals(relation1.getName()));
        
        final Optional<PageDefinition> rel1PageCreate = application.get().getPages().stream()
        		.filter(c -> c.getPageType().equals(PageType.CREATE) && c.getName().equals(EsmUtils.getNamespaceElementFQName(e2) + "." + relation1.getName() + "#Create")).findAny();
        assertTrue(rel1PageCreate.isPresent());
        assertTrue(uiE2.get().getRelations().contains(rel1PageCreate.get().getDataElement()) && rel1PageCreate.get().getDataElement().getName().equals(relation1.getName()));
        
        final Optional<PageDefinition> rel1PageUpdate = application.get().getPages().stream()
        		.filter(c -> c.getPageType().equals(PageType.UPDATE) && c.getName().equals(EsmUtils.getNamespaceElementFQName(e2) + "." + relation1.getName() + "#Update")).findAny();
        assertFalse(rel1PageUpdate.isPresent());
        
        final Optional<PageDefinition> rel1PageTable = application.get().getPages().stream()
        		.filter(c -> c.getPageType().equals(PageType.TABLE) && c.getName().equals(EsmUtils.getNamespaceElementFQName(e2) + "." + relation1.getName() + "#Table")).findAny();
        assertTrue(rel1PageTable.isPresent());
        assertTrue(uiE2.get().getRelations().contains(rel1PageTable.get().getDataElement()) && rel1PageTable.get().getDataElement().getName().equals(relation1.getName()));
        
      //exposed stored composition: view
        final Optional<PageDefinition> rel2Page = application.get().getPages().stream()
        		.filter(c -> c.getPageType().equals(PageType.VIEW) && c.getName().equals(EsmUtils.getNamespaceElementFQName(e2) + "." + relation2.getName() + "#View")).findAny();
        assertTrue(rel2Page.isPresent());
        assertTrue(uiE2.get().getRelations().contains(rel2Page.get().getDataElement()) && rel2Page.get().getDataElement().getName().equals(relation2.getName()));
        
        final Optional<PageDefinition> rel2PageCreate = application.get().getPages().stream()
        		.filter(c -> c.getPageType().equals(PageType.CREATE) && c.getName().equals(EsmUtils.getNamespaceElementFQName(e2) + "." + relation2.getName() + "#Create")).findAny();
        assertFalse(rel2PageCreate.isPresent());
        
        final Optional<PageDefinition> rel2PageUpdate = application.get().getPages().stream()
        		.filter(c -> c.getPageType().equals(PageType.UPDATE) && c.getName().equals(EsmUtils.getNamespaceElementFQName(e2) + "." + relation2.getName() + "#Update")).findAny();
        assertFalse(rel2PageUpdate.isPresent());
        
        final Optional<PageDefinition> rel2PageTable = application.get().getPages().stream()
        		.filter(c -> c.getPageType().equals(PageType.TABLE) && c.getName().equals(EsmUtils.getNamespaceElementFQName(e2) + "." + relation2.getName() + "#Table")).findAny();
        assertFalse(rel2PageTable.isPresent());
        
        //exposed derived deletable aggregation: view
        final Optional<PageDefinition> rel3Page = application.get().getPages().stream()
        		.filter(c -> c.getPageType().equals(PageType.VIEW) && c.getName().equals(EsmUtils.getNamespaceElementFQName(e2) + "." + relation3.getName() + "#View")).findAny();
        assertTrue(rel3Page.isPresent());
        assertTrue(uiE2.get().getRelations().contains(rel3Page.get().getDataElement()) && rel3Page.get().getDataElement().getName().equals(relation3.getName()));
        
        final Optional<PageDefinition> rel3PageCreate = application.get().getPages().stream()
        		.filter(c -> c.getPageType().equals(PageType.CREATE) && c.getName().equals(EsmUtils.getNamespaceElementFQName(e2) + "." + relation3.getName() + "#Create")).findAny();
        assertFalse(rel3PageCreate.isPresent());
        
        final Optional<PageDefinition> rel3PageUpdate = application.get().getPages().stream()
        		.filter(c -> c.getPageType().equals(PageType.UPDATE) && c.getName().equals(EsmUtils.getNamespaceElementFQName(e2) + "." + relation3.getName() + "#Update")).findAny();
        assertFalse(rel3PageUpdate.isPresent());
        
        final Optional<PageDefinition> rel3PageTable = application.get().getPages().stream()
        		.filter(c -> c.getPageType().equals(PageType.TABLE) && c.getName().equals(EsmUtils.getNamespaceElementFQName(e2) + "." + relation3.getName() + "#Table")).findAny();
        assertTrue(rel3PageTable.isPresent());
        assertTrue(uiE2.get().getRelations().contains(rel3PageTable.get().getDataElement()) && rel3PageTable.get().getDataElement().getName().equals(relation3.getName()));
        
        //exposed derived updateable association: view, create, update, table
        final Optional<PageDefinition> rel4Page = application.get().getPages().stream()
        		.filter(c -> c.getPageType().equals(PageType.VIEW) && c.getName().equals(EsmUtils.getNamespaceElementFQName(e2) + "." + relation4.getName() + "#View")).findAny();
        assertTrue(rel4Page.isPresent());
        assertTrue(uiE2.get().getRelations().contains(rel4Page.get().getDataElement()) && rel4Page.get().getDataElement().getName().equals(relation4.getName()));
        
        final Optional<PageDefinition> rel4PageCreate = application.get().getPages().stream()
        		.filter(c -> c.getPageType().equals(PageType.CREATE) && c.getName().equals(EsmUtils.getNamespaceElementFQName(e2) + "." + relation4.getName() + "#Create")).findAny();
        assertFalse(rel4PageCreate.isPresent());
        
        final Optional<PageDefinition> rel4PageUpdate = application.get().getPages().stream()
        		.filter(c -> c.getPageType().equals(PageType.UPDATE) && c.getName().equals(EsmUtils.getNamespaceElementFQName(e2) + "." + relation4.getName() + "#Update")).findAny();
        assertFalse(rel4PageUpdate.isPresent());
        
        final Optional<PageDefinition> rel4PageTable = application.get().getPages().stream()
        		.filter(c -> c.getPageType().equals(PageType.TABLE) && c.getName().equals(EsmUtils.getNamespaceElementFQName(e2) + "." + relation4.getName() + "#Table")).findAny();
        assertFalse(rel4PageTable.isPresent());
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
