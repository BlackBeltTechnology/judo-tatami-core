package hu.blackbelt.judo.tatami.esm2ui;

import static hu.blackbelt.judo.meta.esm.accesspoint.util.builder.AccesspointBuilders.newAccessBuilder;
import static hu.blackbelt.judo.meta.esm.accesspoint.util.builder.AccesspointBuilders.newActorTypeBuilder;
import static hu.blackbelt.judo.meta.esm.accesspoint.util.builder.AccesspointBuilders.useActorType;
import static hu.blackbelt.judo.meta.esm.namespace.util.builder.NamespaceBuilders.newModelBuilder;
import static hu.blackbelt.judo.meta.esm.operation.util.builder.OperationBuilders.newInheritedOperationReferenceBuilder;
import static hu.blackbelt.judo.meta.esm.operation.util.builder.OperationBuilders.newOperationBuilder;
import static hu.blackbelt.judo.meta.esm.operation.util.builder.OperationBuilders.newParameterBuilder;
import static hu.blackbelt.judo.meta.esm.runtime.EsmEpsilonValidator.calculateEsmValidationScriptURI;
import static hu.blackbelt.judo.meta.esm.runtime.EsmEpsilonValidator.validateEsm;
import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.buildEsmModel;
import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.SaveArguments.esmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.newDataMemberBuilder;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.newEntityTypeBuilder;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.newGeneralizationBuilder;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.newOneWayRelationMemberBuilder;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.useEntityType;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newNumericTypeBuilder;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newStringTypeBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newDataColumnBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newDataFieldBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newGroupBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newOperationFormBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newTransferObjectFormBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newTransferObjectTableBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newTransferObjectViewBuilder;
import static hu.blackbelt.judo.meta.ui.runtime.UiEpsilonValidator.calculateUiValidationScriptURI;
import static hu.blackbelt.judo.meta.ui.runtime.UiEpsilonValidator.validateUi;
import static hu.blackbelt.judo.meta.ui.runtime.UiModel.buildUiModel;
import static hu.blackbelt.judo.meta.ui.runtime.UiModel.SaveArguments.uiSaveArgumentsBuilder;
import static hu.blackbelt.judo.tatami.esm2ui.Esm2Ui.calculateEsm2UiTransformationScriptURI;
import static hu.blackbelt.judo.tatami.esm2ui.Esm2Ui.executeEsm2UiTransformation;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
import hu.blackbelt.judo.meta.esm.type.NumericType;
import hu.blackbelt.judo.meta.esm.type.StringType;
import hu.blackbelt.judo.meta.ui.AddAction;
import hu.blackbelt.judo.meta.ui.Application;
import hu.blackbelt.judo.meta.ui.BackAction;
import hu.blackbelt.judo.meta.ui.Button;
import hu.blackbelt.judo.meta.ui.CallOperationAction;
import hu.blackbelt.judo.meta.ui.CreateAction;
import hu.blackbelt.judo.meta.ui.DeleteAction;
import hu.blackbelt.judo.meta.ui.EditAction;
import hu.blackbelt.judo.meta.ui.Flex;
import hu.blackbelt.judo.meta.ui.PageContainer;
import hu.blackbelt.judo.meta.ui.PageDefinition;
import hu.blackbelt.judo.meta.ui.PageType;
import hu.blackbelt.judo.meta.ui.RemoveAction;
import hu.blackbelt.judo.meta.ui.SaveAction;
import hu.blackbelt.judo.meta.ui.SetAction;
import hu.blackbelt.judo.meta.ui.Table;
import hu.blackbelt.judo.meta.ui.UnsetAction;
import hu.blackbelt.judo.meta.ui.ViewAction;
import hu.blackbelt.judo.meta.ui.VisualElement;
import hu.blackbelt.judo.meta.ui.data.ClassType;
import hu.blackbelt.judo.meta.ui.data.OperationType;
import hu.blackbelt.judo.meta.ui.data.RelationType;
import hu.blackbelt.judo.meta.ui.runtime.UiModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Esm2UiOperationsTest {
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
    void testCalculatePageActions() throws Exception {
        testName = "calculatePageActions";
        
        final String MODEL_NAME = "Model";
        final String ENTITY_TYPE_NAME_1 = "E1";
        final String ENTITY_TYPE_NAME_2 = "E2";
        final String ENTITY_TYPE_NAME_3 = "E3";
        final String ACTOR_TYPE_NAME = "Actor";
        
        ActorType actor = newActorTypeBuilder()
                .withName(ACTOR_TYPE_NAME)
                .withRealm("sandbox")
                .build();
        
        NumericType numeric = newNumericTypeBuilder().withName("numeric")
        		.withPrecision(2).withScale(1).build();
        DataMember id1 = newDataMemberBuilder().withName("id").withDataType(numeric)
        		.withMemberType(MemberType.STORED).build();
        DataMember id2 = newDataMemberBuilder().withName("id").withDataType(numeric)
        		.withMemberType(MemberType.STORED).build();
        
        final EntityType e1 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_1)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withAttributes(id1)
                .build();
        useEntityType(e1).withMappedEntity(e1).build();
        
        final EntityType e2 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_2)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .build();
        useEntityType(e2).withMappedEntity(e2).build();
        
        OneWayRelationMember relation = newOneWayRelationMemberBuilder().withName("e2s").withTargetDefinedCRUD(false)
        		.withCreateable(false).withUpdateable(false).withDeleteable(false).withTarget(e2).withUpper(-1)
        		.withRelationKind(RelationKind.ASSOCIATION).withMemberType(MemberType.STORED).build();
        
        final EntityType e3 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_3)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withAttributes(id2)
                .build();
        useEntityType(e3).withMappedEntity(e3).build();
        
        OneWayRelationMember relation2 = newOneWayRelationMemberBuilder().withName("e3s").withTargetDefinedCRUD(false)
        		.withCreateable(true).withUpdateable(true).withDeleteable(true).withTarget(e3).withUpper(-1)
        		.withRelationKind(RelationKind.ASSOCIATION).withMemberType(MemberType.STORED).build();
        OneWayRelationMember relation3 = newOneWayRelationMemberBuilder().withName("e3s_single").withTargetDefinedCRUD(false)
        		.withCreateable(true).withUpdateable(true).withDeleteable(true).withTarget(e3).withUpper(1)
        		.withRelationKind(RelationKind.ASSOCIATION).withMemberType(MemberType.STORED).build();
        
        OneWayRelationMember relation4 = newOneWayRelationMemberBuilder().withName("e3sContainment").withTargetDefinedCRUD(false)
        		.withCreateable(true).withUpdateable(true).withDeleteable(true).withTarget(e3).withUpper(-1)
        		.withRelationKind(RelationKind.COMPOSITION).withMemberType(MemberType.STORED).build();
        OneWayRelationMember relation5 = newOneWayRelationMemberBuilder().withName("e3s_singleContainment").withTargetDefinedCRUD(false)
        		.withCreateable(true).withUpdateable(true).withDeleteable(true).withTarget(e3).withUpper(1)
        		.withRelationKind(RelationKind.COMPOSITION).withMemberType(MemberType.STORED).build();
        
        useEntityType(e1).withRelations(relation, relation2, relation3, relation4, relation5).build();
        
        Access access1 = newAccessBuilder().withName("e1")
        		.withTarget(e1)
        		.withLower(0)
        		.withUpper(-1)
        		.withCreateable(true)
        		.withUpdateable(true)
        		.withDeleteable(true)
        		.withTargetDefinedCRUD(false)
        		.build();

        useActorType(actor).withAccesses(access1).build();
        
        final Model model = newModelBuilder().withName(MODEL_NAME)
                .withElements(actor, e1, e2, e3, numeric).build();

        SimpleOrderModel.addUiElementsToTransferObjects(model);
        
        esmModel.addContent(model);
        
        transform();

        final Optional<Application> application = allUi(Application.class).filter(a -> a.getName().equals(actor.getFQName()))
                .findAny();
        assertTrue(application.isPresent());
        final Optional<RelationType> uiRelation2 = allUi(RelationType.class).filter(r -> r.getName().equals(relation2.getName())).findAny();
        assertTrue(uiRelation2.isPresent());
        final Optional<RelationType> uiRelation3 = allUi(RelationType.class).filter(r -> r.getName().equals(relation3.getName())).findAny();
        assertTrue(uiRelation3.isPresent());
        
        final Optional<PageDefinition> e1ViewPage = application.get().getPages().stream()
        		.filter(p -> p.getPageType().equals(PageType.VIEW) && p.getDataElement().getName().equals(access1.getName())).findAny();
        assertTrue(e1ViewPage.isPresent());
        
        final Optional<PageContainer> defaultContainer1 = e1ViewPage.get().getContainers().stream().filter(c -> c.getName().equals("default")).findAny();
        assertTrue(defaultContainer1.isPresent());
        final Optional<VisualElement> instanceActionsContainerE1View = defaultContainer1.get().getChildren().stream().filter(c -> c instanceof Flex && c.getName().equals("instanceActions")).findAny();
        assertTrue(instanceActionsContainerE1View.isPresent());
        assertEquals(3, ((Flex)instanceActionsContainerE1View.get()).getChildren().size());
        assertTrue(((Flex)instanceActionsContainerE1View.get()).getChildren().stream().anyMatch(b -> b instanceof Button && ((Button)b).getAction() != null && ((Button)b).getAction() instanceof BackAction));
        assertTrue(((Flex)instanceActionsContainerE1View.get()).getChildren().stream().anyMatch(b -> b instanceof Button && ((Button)b).getAction() != null && ((Button)b).getAction() instanceof DeleteAction));
        assertTrue(((Flex)instanceActionsContainerE1View.get()).getChildren().stream().anyMatch(b -> b instanceof Button && ((Button)b).getAction() != null && ((Button)b).getAction() instanceof EditAction));
        
        final Optional<VisualElement> viewContainerE1View = defaultContainer1.get().getChildren().stream()
        		.filter(c -> c instanceof Flex && c.getName().equals(e1.getName() + "View")).findAny();
        assertTrue(viewContainerE1View.isPresent());
        final Optional<VisualElement> uiTable1 = ((Flex)viewContainerE1View.get()).getChildren().stream()
        		.filter(c -> c instanceof Flex && c.getName().equals(relation4.getName())).findAny();
        assertTrue(uiTable1.isPresent());
        final Optional<VisualElement> instanceActionsUiTable1 = ((Flex)uiTable1.get()).getChildren().stream().filter(c -> c instanceof Flex && c.getName().equals("instanceActions")).findAny();
        assertFalse(instanceActionsUiTable1.isPresent());
        final Optional<VisualElement> uiTable1Table = ((Flex)uiTable1.get()).getChildren().stream().filter(c -> c instanceof Table).findAny();
        assertTrue(uiTable1Table.isPresent());
        assertEquals(1, ((Table)uiTable1Table.get()).getRowActions().size());
        assertTrue(((Table)uiTable1Table.get()).getRowActions().stream().anyMatch(a -> a instanceof ViewAction));
        
        final Optional<VisualElement> uiTable2 = ((Flex)viewContainerE1View.get()).getChildren().stream()
        		.filter(c -> c instanceof Flex && c.getName().equals(relation5.getName())).findAny();
        assertTrue(uiTable2.isPresent());
        final Optional<VisualElement> instanceActionsUiTable2 = ((Flex)uiTable2.get()).getChildren().stream().filter(c -> c instanceof Flex && c.getName().equals("instanceActions")).findAny();
        assertFalse(instanceActionsUiTable2.isPresent());
        final Optional<VisualElement> uiTable2Table = ((Flex)uiTable2.get()).getChildren().stream().filter(c -> c instanceof Table).findAny();
        assertTrue(uiTable2Table.isPresent());
        assertEquals(1, ((Table)uiTable2Table.get()).getRowActions().size());
        assertTrue(((Table)uiTable2Table.get()).getRowActions().stream().anyMatch(a -> a instanceof ViewAction));
        
        final Optional<PageDefinition> e1CreatePage = application.get().getPages().stream()
        		.filter(p -> p.getPageType().equals(PageType.CREATE) && p.getDataElement().getName().equals(access1.getName())).findAny();
        assertTrue(e1CreatePage.isPresent());
        final Optional<PageContainer> defaultContainer2 = e1CreatePage.get().getContainers().stream().filter(c -> c.getName().equals("default")).findAny();
        assertTrue(defaultContainer2.isPresent());
        final Optional<VisualElement> instanceActionsContainerE1Create = defaultContainer2.get().getChildren().stream().filter(c -> c instanceof Flex && c.getName().equals("instanceActions")).findAny();
        assertTrue(instanceActionsContainerE1Create.isPresent());
        assertEquals(2, ((Flex)instanceActionsContainerE1Create.get()).getChildren().size());
        assertTrue(((Flex)instanceActionsContainerE1Create.get()).getChildren().stream().anyMatch(b -> b instanceof Button && ((Button)b).getAction() != null && ((Button)b).getAction() instanceof BackAction));
        assertTrue(((Flex)instanceActionsContainerE1Create.get()).getChildren().stream().anyMatch(b -> b instanceof Button && ((Button)b).getAction() != null && ((Button)b).getAction() instanceof SaveAction));
        
        final Optional<VisualElement> viewContainerE1Create = defaultContainer2.get().getChildren().stream()
        		.filter(c -> c instanceof Flex && c.getName().equals(e1.getName() + "Form")).findAny();
        assertTrue(viewContainerE1Create.isPresent());
        final Optional<VisualElement> uiTable3 = ((Flex)viewContainerE1Create.get()).getChildren().stream()
        		.filter(c -> c instanceof Flex && c.getName().equals(relation4.getName())).findAny();
        assertTrue(uiTable3.isPresent());
        final Optional<VisualElement> instanceActionsUiTable3 = ((Flex)uiTable3.get()).getChildren().stream().filter(c -> c instanceof Flex && c.getName().equals("instanceActions")).findAny();
        assertTrue(instanceActionsUiTable3.isPresent());
        assertEquals(1, ((Flex)instanceActionsUiTable3.get()).getChildren().size());
        assertTrue(((Flex)instanceActionsUiTable3.get()).getChildren().stream().anyMatch(b -> b instanceof Button && ((Button)b).getAction() != null && ((Button)b).getAction() instanceof CreateAction));
        final Optional<VisualElement> uiTable3Table = ((Flex)uiTable3.get()).getChildren().stream().filter(c -> c instanceof Table).findAny();
        assertTrue(uiTable3Table.isPresent());
        assertEquals(3, ((Table)uiTable3Table.get()).getRowActions().size());
        assertTrue(((Table)uiTable3Table.get()).getRowActions().stream().anyMatch(a -> a instanceof ViewAction));
        assertTrue(((Table)uiTable3Table.get()).getRowActions().stream().anyMatch(a -> a instanceof DeleteAction));
        assertTrue(((Table)uiTable3Table.get()).getRowActions().stream().anyMatch(a -> a instanceof EditAction));
        
        final Optional<VisualElement> uiTable4 = ((Flex)viewContainerE1Create.get()).getChildren().stream()
        		.filter(c -> c instanceof Flex && c.getName().equals(relation5.getName())).findAny();
        assertTrue(uiTable4.isPresent());
        final Optional<VisualElement> instanceActionsUiTable4 = ((Flex)uiTable4.get()).getChildren().stream().filter(c -> c instanceof Flex && c.getName().equals("instanceActions")).findAny();
        assertTrue(instanceActionsUiTable4.isPresent());
        assertEquals(1, ((Flex)instanceActionsUiTable4.get()).getChildren().size());
        assertTrue(((Flex)instanceActionsUiTable4.get()).getChildren().stream().anyMatch(b -> b instanceof Button && ((Button)b).getAction() != null && ((Button)b).getAction() instanceof CreateAction));
        final Optional<VisualElement> uiTable4Table = ((Flex)uiTable4.get()).getChildren().stream().filter(c -> c instanceof Table).findAny();
        assertTrue(uiTable4Table.isPresent());
        assertEquals(3, ((Table)uiTable4Table.get()).getRowActions().size());
        assertTrue(((Table)uiTable4Table.get()).getRowActions().stream().anyMatch(a -> a instanceof ViewAction));
        assertTrue(((Table)uiTable4Table.get()).getRowActions().stream().anyMatch(a -> a instanceof DeleteAction));
        assertTrue(((Table)uiTable4Table.get()).getRowActions().stream().anyMatch(a -> a instanceof EditAction));
        
        final Optional<PageDefinition> e1EditPage = application.get().getPages().stream()
        		.filter(p -> p.getPageType().equals(PageType.UPDATE) && p.getDataElement().getName().equals(access1.getName())).findAny();
        assertTrue(e1EditPage.isPresent());
        final Optional<PageContainer> defaultContainer3 = e1EditPage.get().getContainers().stream().filter(c -> c.getName().equals("default")).findAny();
        assertTrue(defaultContainer3.isPresent());
        final Optional<VisualElement> instanceActionsContainerE1Update = defaultContainer3.get().getChildren().stream().filter(c -> c instanceof Flex && c.getName().equals("instanceActions")).findAny();
        assertTrue(instanceActionsContainerE1Update.isPresent());
        assertEquals(2, ((Flex)instanceActionsContainerE1Update.get()).getChildren().size());
        assertTrue(((Flex)instanceActionsContainerE1Update.get()).getChildren().stream().anyMatch(b -> b instanceof Button && ((Button)b).getAction() != null && ((Button)b).getAction() instanceof BackAction));
        assertTrue(((Flex)instanceActionsContainerE1Update.get()).getChildren().stream().anyMatch(b -> b instanceof Button && ((Button)b).getAction() != null && ((Button)b).getAction() instanceof SaveAction));
        
        final Optional<PageDefinition> e1TablePage = application.get().getPages().stream()
        		.filter(p -> p.getPageType().equals(PageType.TABLE) && p.getDataElement().getName().equals(access1.getName())).findAny();
        assertTrue(e1TablePage.isPresent());
        final Optional<PageContainer> defaultContainer4 = e1TablePage.get().getContainers().stream().filter(c -> c.getName().equals("default")).findAny();
        assertTrue(defaultContainer4.isPresent());
        final Optional<VisualElement> instanceActionsContainerE1Table = defaultContainer4.get().getChildren().stream().filter(c -> c instanceof Flex && c.getName().equals("instanceActions")).findAny();
        assertTrue(instanceActionsContainerE1Table.isPresent());
        assertEquals(1, ((Flex)instanceActionsContainerE1Table.get()).getChildren().size());
        assertTrue(((Flex)instanceActionsContainerE1Table.get()).getChildren().stream().anyMatch(b -> b instanceof Button && ((Button)b).getAction() != null && ((Button)b).getAction() instanceof CreateAction));
        
        final Optional<Table> uiTable5 = allUi(Table.class)
        		.filter(a -> a.getName().equals(e3.getName()) && a.getPageDefinition().getIsPageTypeTable() && a.getRelationType().equals(uiRelation2.get()))
                .findAny();
        assertTrue(uiTable5.isPresent());
        assertEquals(4, ((Table)uiTable5.get()).getRowActions().size());
        assertTrue(((Table)uiTable5.get()).getRowActions().stream().anyMatch(a -> a instanceof ViewAction));
        assertTrue(((Table)uiTable5.get()).getRowActions().stream().anyMatch(a -> a instanceof DeleteAction));
        assertTrue(((Table)uiTable5.get()).getRowActions().stream().anyMatch(a -> a instanceof EditAction));
        assertTrue(((Table)uiTable5.get()).getRowActions().stream().anyMatch(a -> a instanceof RemoveAction));
        
        final Optional<PageDefinition> e2TablePage = application.get().getPages().stream()
        		.filter(p -> p.getPageType().equals(PageType.TABLE) && p.getDataElement().getName().equals(relation.getName())).findAny();
        assertFalse(e2TablePage.isPresent());
        
        final Optional<PageDefinition> e3TablePage = application.get().getPages().stream()
        		.filter(p -> p.getPageType().equals(PageType.TABLE) && p.getDataElement().getName().equals(relation2.getName())).findAny();
        assertTrue(e3TablePage.isPresent());
        final Optional<PageContainer> defaultContainer5 = e3TablePage.get().getContainers().stream().filter(c -> c.getName().equals("default")).findAny();
        assertTrue(defaultContainer5.isPresent());
        final Optional<VisualElement> instanceActionsContainerE3Table = defaultContainer5.get().getChildren().stream().filter(c -> c instanceof Flex && c.getName().equals("instanceActions")).findAny();
        assertTrue(instanceActionsContainerE3Table.isPresent());
        assertEquals(4, ((Flex)instanceActionsContainerE3Table.get()).getChildren().size());
        assertTrue(((Flex)instanceActionsContainerE3Table.get()).getChildren().stream()
        		.anyMatch(b -> b instanceof Button && ((Button)b).getAction() != null && ((Button)b).getAction() instanceof BackAction));
        assertTrue(((Flex)instanceActionsContainerE3Table.get()).getChildren().stream()
        		.anyMatch(b -> b instanceof Button && ((Button)b).getAction() != null && ((Button)b).getAction() instanceof CreateAction));
        assertTrue(((Flex)instanceActionsContainerE3Table.get()).getChildren().stream()
        		.anyMatch(b -> b instanceof Button && ((Button)b).getAction() != null && ((Button)b).getAction() instanceof SetAction));
        assertTrue(((Flex)instanceActionsContainerE3Table.get()).getChildren().stream()
        		.anyMatch(b -> b instanceof Button && ((Button)b).getAction() != null && ((Button)b).getAction() instanceof AddAction));
        
        final Optional<Table> uiTable6 = allUi(Table.class)
        		.filter(a -> a.getName().equals(e3.getName()) && a.getPageDefinition().getIsPageTypeTable() && a.getRelationType().equals(uiRelation3.get()))
                .findAny();
        assertTrue(uiTable6.isPresent());
        assertEquals(4, ((Table)uiTable6.get()).getRowActions().size());
        assertTrue(((Table)uiTable6.get()).getRowActions().stream().anyMatch(a -> a instanceof ViewAction));
        assertTrue(((Table)uiTable6.get()).getRowActions().stream().anyMatch(a -> a instanceof DeleteAction));
        assertTrue(((Table)uiTable6.get()).getRowActions().stream().anyMatch(a -> a instanceof EditAction));
        assertTrue(((Table)uiTable6.get()).getRowActions().stream().anyMatch(a -> a instanceof UnsetAction));
        
        final Optional<PageDefinition> e3TablePageSingle = application.get().getPages().stream()
        		.filter(p -> p.getPageType().equals(PageType.TABLE) && p.getDataElement().getName().equals(relation3.getName())).findAny();
        assertTrue(e3TablePageSingle.isPresent());
        final Optional<PageContainer> defaultContainer6 = e3TablePageSingle.get().getContainers().stream().filter(c -> c.getName().equals("default")).findAny();
        assertTrue(defaultContainer6.isPresent());
        final Optional<VisualElement> instanceActionsContainerE3TableSingle = defaultContainer6.get().getChildren().stream()
        		.filter(c -> c instanceof Flex && c.getName().equals("instanceActions")).findAny();
        assertTrue(instanceActionsContainerE3TableSingle.isPresent());
        assertEquals(4, ((Flex)instanceActionsContainerE3TableSingle.get()).getChildren().size());
        assertTrue(((Flex)instanceActionsContainerE3TableSingle.get()).getChildren().stream()
        		.anyMatch(b -> b instanceof Button && ((Button)b).getAction() != null && ((Button)b).getAction() instanceof BackAction));
        assertTrue(((Flex)instanceActionsContainerE3TableSingle.get()).getChildren().stream()
        		.anyMatch(b -> b instanceof Button && ((Button)b).getAction() != null && ((Button)b).getAction() instanceof CreateAction));
        assertTrue(((Flex)instanceActionsContainerE3TableSingle.get()).getChildren().stream()
        		.anyMatch(b -> b instanceof Button && ((Button)b).getAction() != null && ((Button)b).getAction() instanceof SetAction));
        assertTrue(((Flex)instanceActionsContainerE3TableSingle.get()).getChildren().stream()
        		.anyMatch(b -> b instanceof Button && ((Button)b).getAction() != null && ((Button)b).getAction() instanceof UnsetAction));
        
    }
    
    @Test
    void testCallOperationActions() throws Exception {
        testName = "CallOperationActions";
        
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
        
        final Optional<OperationType> uiE2Op1 = uiE2.get().getOperations().stream()
        			.filter(c -> c.getName().equals(operation1.getName())).findAny();
        assertTrue(uiE2Op1.isPresent());
        final Optional<OperationType> uiE2Op2 = uiE2.get().getOperations().stream()
    			.filter(c -> c.getName().equals(operation2.getName())).findAny();
        assertTrue(uiE2Op2.isPresent());
	    final Optional<OperationType> uiE3Op1 = uiE3.get().getOperations().stream()
				.filter(c -> c.getName().equals(operation1.getName())).findAny();
	    assertTrue(uiE3Op1.isPresent());
		final Optional<OperationType> uiE3Op2 = uiE3.get().getOperations().stream()
				.filter(c -> c.getName().equals(operation2.getName())).findAny();
		assertTrue(uiE3Op2.isPresent());
		final Optional<OperationType> uiE3Op3 = uiE3.get().getOperations().stream()
				.filter(c -> c.getName().equals(operation3.getName())).findAny();
		assertTrue(uiE3Op3.isPresent());
        
        final Optional<PageDefinition> e2op1InputPage = application.get().getPages().stream()
        		.filter(c -> c.getPageType().equals(PageType.OPERATION_INPUT) && c.getName().endsWith(e2.getName()) && c.getName().contains(operation1.getName())).findAny();
        assertTrue(e2op1InputPage.isPresent());
        
        final Optional<PageDefinition> e2op2OutputPage = application.get().getPages().stream()
        		.filter(c -> c.getPageType().equals(PageType.OPERATION_OUTPUT) && c.getName().endsWith(e2.getName()) && c.getName().contains(operation2.getName())).findAny();
        assertTrue(e2op2OutputPage.isPresent());
        
        final Optional<PageDefinition> e3op1InputPage = application.get().getPages().stream()
        		.filter(c -> c.getPageType().equals(PageType.OPERATION_INPUT) && c.getName().endsWith(e3.getName()) && c.getName().contains(operation1.getName())).findAny();
        assertTrue(e3op1InputPage.isPresent());
        
        final Optional<PageDefinition> e3op2OutputPage = application.get().getPages().stream()
        		.filter(c -> c.getPageType().equals(PageType.OPERATION_OUTPUT) && c.getName().endsWith(e3.getName()) && c.getName().contains(operation2.getName())).findAny();
        assertTrue(e3op2OutputPage.isPresent());
        
        final Optional<PageDefinition> e3op3InputPage = application.get().getPages().stream()
        		.filter(c -> c.getPageType().equals(PageType.OPERATION_INPUT) && c.getName().endsWith(e3.getName()) && c.getName().contains(operation3.getName())).findAny();
        assertTrue(e3op3InputPage.isPresent());
        
        final Optional<PageDefinition> e3op3OutputPage = application.get().getPages().stream()
        		.filter(c -> c.getPageType().equals(PageType.OPERATION_OUTPUT) && c.getName().endsWith(e3.getName()) && c.getName().contains(operation3.getName())).findAny();
        assertTrue(e3op3OutputPage.isPresent());
        
        final Optional<CallOperationAction> callOp1 = allUi(CallOperationAction.class).filter(o -> o.getOperation().equals(uiE2Op1.get()))
                .findAny();
        assertTrue(callOp1.isPresent());
        assertEquals(e2op1InputPage.get(), callOp1.get().getInputParameterPage());
        assertEquals(uiE2Op1.get(), ((Button)callOp1.get().eContainer()).getDataElement());
        
        final Optional<CallOperationAction> callOp2 = allUi(CallOperationAction.class).filter(o -> o.getOperation().equals(uiE2Op2.get()))
                .findAny();
        assertTrue(callOp2.isPresent());
        assertEquals(e2op2OutputPage.get(), callOp2.get().getOutputParameterPage());
        assertEquals(uiE2Op2.get(), ((Button)callOp2.get().eContainer()).getDataElement());
        
        final Optional<CallOperationAction> callOp3 = allUi(CallOperationAction.class).filter(o -> o.getOperation().equals(uiE3Op1.get()))
                .findAny();
        assertTrue(callOp3.isPresent());
        assertEquals(e3op1InputPage.get(), callOp3.get().getInputParameterPage());
        assertEquals(uiE3Op1.get(), ((Button)callOp3.get().eContainer()).getDataElement());
        
        final Optional<CallOperationAction> callOp4 = allUi(CallOperationAction.class).filter(o -> o.getOperation().equals(uiE3Op2.get()))
                .findAny();
        assertTrue(callOp4.isPresent());
        assertEquals(e3op2OutputPage.get(), callOp4.get().getOutputParameterPage());
        assertEquals(uiE3Op2.get(), ((Button)callOp4.get().eContainer()).getDataElement());
        
        final Optional<CallOperationAction> callOp5 = allUi(CallOperationAction.class).filter(o -> o.getOperation().equals(uiE3Op3.get()))
                .findAny();
        assertTrue(callOp5.isPresent());
        assertEquals(e3op3OutputPage.get(), callOp5.get().getOutputParameterPage());
        assertEquals(e3op3InputPage.get(), callOp5.get().getInputParameterPage());
        assertEquals(uiE3Op3.get(), ((Button)callOp5.get().eContainer()).getDataElement());
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
