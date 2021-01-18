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
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newBooleanTypeBuilder;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newDateTypeBuilder;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newEnumerationMemberBuilder;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newEnumerationTypeBuilder;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newNumericTypeBuilder;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newPasswordTypeBuilder;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newStringTypeBuilder;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newTimestampTypeBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.*;
import static hu.blackbelt.judo.meta.ui.runtime.UiEpsilonValidator.calculateUiValidationScriptURI;
import static hu.blackbelt.judo.meta.ui.runtime.UiEpsilonValidator.validateUi;
import static hu.blackbelt.judo.meta.ui.runtime.UiModel.buildUiModel;
import static hu.blackbelt.judo.meta.ui.runtime.UiModel.SaveArguments.uiSaveArgumentsBuilder;
import static hu.blackbelt.judo.tatami.esm2ui.Esm2Ui.calculateEsm2UiTransformationScriptURI;
import static hu.blackbelt.judo.tatami.esm2ui.Esm2Ui.executeEsm2UiTransformation;
import static org.junit.jupiter.api.Assertions.assertEquals;
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

import com.google.common.collect.ImmutableList;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.accesspoint.Access;
import hu.blackbelt.judo.meta.esm.accesspoint.ActorType;
import hu.blackbelt.judo.meta.esm.measure.DurationType;
import hu.blackbelt.judo.meta.esm.namespace.Model;
import hu.blackbelt.judo.meta.esm.operation.Operation;
import hu.blackbelt.judo.meta.esm.operation.OperationType;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.esm.structure.DataMember;
import hu.blackbelt.judo.meta.esm.structure.EntityType;
import hu.blackbelt.judo.meta.esm.structure.MemberType;
import hu.blackbelt.judo.meta.esm.structure.OneWayRelationMember;
import hu.blackbelt.judo.meta.esm.structure.RelationKind;
import hu.blackbelt.judo.meta.esm.type.BooleanType;
import hu.blackbelt.judo.meta.esm.type.DateType;
import hu.blackbelt.judo.meta.esm.type.EnumerationType;
import hu.blackbelt.judo.meta.esm.type.NumericType;
import hu.blackbelt.judo.meta.esm.type.PasswordType;
import hu.blackbelt.judo.meta.esm.type.StringType;
import hu.blackbelt.judo.meta.esm.type.TimestampType;
import hu.blackbelt.judo.meta.esm.ui.Action;
import hu.blackbelt.judo.meta.esm.ui.DataField;
import hu.blackbelt.judo.meta.esm.ui.Divider;
import hu.blackbelt.judo.meta.esm.ui.EnumWidget;
import hu.blackbelt.judo.meta.esm.ui.Fit;
import hu.blackbelt.judo.meta.esm.ui.Group;
import hu.blackbelt.judo.meta.esm.ui.Horizontal;
import hu.blackbelt.judo.meta.esm.ui.Icon;
import hu.blackbelt.judo.meta.esm.ui.Layout;
import hu.blackbelt.judo.meta.esm.ui.OperationForm;
import hu.blackbelt.judo.meta.esm.ui.Placeholder;
import hu.blackbelt.judo.meta.esm.ui.Stretch;
import hu.blackbelt.judo.meta.esm.ui.TabularReferenceField;
import hu.blackbelt.judo.meta.esm.ui.TextField;
import hu.blackbelt.judo.meta.esm.ui.TextWidget;
import hu.blackbelt.judo.meta.esm.ui.TransferObjectForm;
import hu.blackbelt.judo.meta.esm.ui.TransferObjectView;
import hu.blackbelt.judo.meta.esm.ui.Vertical;
import hu.blackbelt.judo.meta.ui.Application;
import hu.blackbelt.judo.meta.ui.BackAction;
import hu.blackbelt.judo.meta.ui.Button;
import hu.blackbelt.judo.meta.ui.CallOperationAction;
import hu.blackbelt.judo.meta.ui.DateInput;
import hu.blackbelt.judo.meta.ui.DateTimeInput;
import hu.blackbelt.judo.meta.ui.EnumerationCombo;
import hu.blackbelt.judo.meta.ui.EnumerationRadio;
import hu.blackbelt.judo.meta.ui.Flex;
import hu.blackbelt.judo.meta.ui.Formatted;
import hu.blackbelt.judo.meta.ui.IconImage;
import hu.blackbelt.judo.meta.ui.Label;
import hu.blackbelt.judo.meta.ui.NavigationToPageAction;
import hu.blackbelt.judo.meta.ui.NumericInput;
import hu.blackbelt.judo.meta.ui.PageContainer;
import hu.blackbelt.judo.meta.ui.PageDefinition;
import hu.blackbelt.judo.meta.ui.PageType;
import hu.blackbelt.judo.meta.ui.PasswordInput;
import hu.blackbelt.judo.meta.ui.SaveAction;
import hu.blackbelt.judo.meta.ui.Spacer;
import hu.blackbelt.judo.meta.ui.Switch;
import hu.blackbelt.judo.meta.ui.Table;
import hu.blackbelt.judo.meta.ui.Text;
import hu.blackbelt.judo.meta.ui.TextArea;
import hu.blackbelt.judo.meta.ui.TextInput;
import hu.blackbelt.judo.meta.ui.VisualElement;
import hu.blackbelt.judo.meta.ui.data.ClassType;
import hu.blackbelt.judo.meta.ui.runtime.UiModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Esm2UiVisualElementTest {
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
    void testCreateButtonFromActionButton() throws Exception {
        testName = "createButtonFromActionButton";
        
        final String MODEL_NAME = "Model";
        final String ENTITY_TYPE_NAME_1 = "E1";
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
        
        Access access1 = newAccessBuilder().withName("e1")
        		.withTarget(e1)
        		.withLower(0)
        		.withUpper(-1)
        		.withCreateable(true)
        		.withTargetDefinedCRUD(false)
        		.build();

        useActorType(actor).withAccesses(access1).build();
        
        final Model model = newModelBuilder().withName(MODEL_NAME)
                .withElements(actor, e1).build();

        SimpleOrderModel.addUiElementsToTransferObjects(model);
        
        useTransferObjectView(e1.getView()).withComponents(
        		newGroupBuilder().withName("groupForActionButtons")
        			.withComponents(
        					newActionButtonBuilder().withName("action1").withLabel("CANCEL").withRow(4).withCol(2).withFit(Fit.LOOSE).withAction(Action.CANCEL).build(),
        					newActionButtonBuilder().withName("action2").withLabel("SUBMIT").withRow(1).withCol(3).withFit(Fit.TIGHT).withAction(Action.SUBMIT).build()
        					).build()
        		).build();
        
        esmModel.addContent(model);
        
        transform();

        final Optional<Application> application = allUi(Application.class).filter(a -> a.getName().equals(actor.getFQName()))
                .findAny();
        assertTrue(application.isPresent());
        
        final Optional<PageDefinition> e1ViewPage = application.get().getPages().stream().filter(p -> p.getPageType().equals(PageType.VIEW) && p.getDataElement().getName().equals(access1.getName())).findAny();
        assertTrue(e1ViewPage.isPresent());
        
        final Optional<Button> uiButton1 = allUi(Button.class).filter(b -> b.getPageDefinition().equals(e1ViewPage.get()) && b.getPageContainer().getName().equals("default") && b.getName().equals("action1")).findAny();
        assertTrue(uiButton1.isPresent());
        assertEquals(2d, uiButton1.get().getCol());
        assertEquals(4d, uiButton1.get().getRow());
        assertEquals("CANCEL", uiButton1.get().getLabel());
        assertTrue(uiButton1.get().getAction() instanceof BackAction);
        assertEquals(hu.blackbelt.judo.meta.ui.Fit.LOOSE, uiButton1.get().getFit());
        
        final Optional<Button> uiButton2 = allUi(Button.class).filter(b -> b.getPageDefinition().equals(e1ViewPage.get()) && b.getPageContainer().getName().equals("default") && b.getName().equals("action2")).findAny();
        assertTrue(uiButton2.isPresent());
        assertEquals(3d, uiButton2.get().getCol());
        assertEquals(1d, uiButton2.get().getRow());
        assertEquals("SUBMIT", uiButton2.get().getLabel());
        assertTrue(uiButton2.get().getAction() instanceof SaveAction);
        assertEquals(hu.blackbelt.judo.meta.ui.Fit.TIGHT, uiButton2.get().getFit());
    }
    
    @Test
    void testCreateFlexFromContainer() throws Exception {
        testName = "createFlexFromContainer";

        final String MODEL_NAME = "Model";
        final String ENTITY_TYPE_NAME_1 = "E1";
        final String ENTITY_TYPE_NAME_2 = "E2";
        final String ACTOR_TYPE_NAME = "Actor";
        
        PasswordType password = newPasswordTypeBuilder().withName("password").build();
        StringType string = newStringTypeBuilder().withName("string").withMaxLength(256)
        		.withRegExp(".*").build();
        NumericType numeric = newNumericTypeBuilder().withName("numeric")
        		.withPrecision(2).withScale(1).build();
        DataMember passwordAttribute = newDataMemberBuilder().withName("password").withDataType(password)
        		.withMemberType(MemberType.STORED).build();
        DataMember stringAttribute = newDataMemberBuilder().withName("name").withDataType(string)
        		.withMemberType(MemberType.STORED).build();
        DataMember numericAttribute = newDataMemberBuilder().withName("id").withDataType(numeric)
        		.withMemberType(MemberType.STORED).build();
        DataMember stringAttribute2 = newDataMemberBuilder().withName("name").withDataType(string)
        		.withMemberType(MemberType.STORED).build();
        
        ActorType actor = newActorTypeBuilder()
                .withName(ACTOR_TYPE_NAME)
                .withRealm("sandbox")
                .build();
        
        final EntityType e1 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_1)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withAttributes(numericAttribute, stringAttribute, passwordAttribute)
                .build();
        useEntityType(e1).withMappedEntity(e1).build();
        final EntityType e2 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_2)
                .withCreateable(true)
                .withUpdateable(true)
                .withDeleteable(true)
                .withAttributes(stringAttribute2)
                .build();
        useEntityType(e2).withMappedEntity(e2).build();
        
        OneWayRelationMember composition = newOneWayRelationMemberBuilder().withName("e2s").withMemberType(MemberType.STORED)
        		.withRelationKind(RelationKind.COMPOSITION).withTargetDefinedCRUD(true).withTarget(e2)
        		.withLower(0).withUpper(-1).build();
        useEntityType(e1).withRelations(composition).build();
        
        Access access1 = newAccessBuilder().withName("e1")
        		.withTarget(e1)
        		.withLower(0)
        		.withUpper(-1)
        		.withCreateable(true)
        		.withTargetDefinedCRUD(false)
        		.build();

        useActorType(actor).withAccesses(access1).build();
        
        final Model model = newModelBuilder().withName(MODEL_NAME)
                .withElements(actor, e1, numeric, password, string, e2).build();
        
		TabularReferenceField tabular = newTabularReferenceFieldBuilder().withName(composition.getName())
				.withLabel(composition.getName().toUpperCase()).withRelationFeature(composition).withCol(12)
				.withTargetDefinedTabular(false)
				.withColumns(newDataColumnBuilder().withName(stringAttribute2.getName()).withLabel(stringAttribute2.getName().toUpperCase())
					.withVisible(true).withDataFeature(stringAttribute2).build())
				.build();

		DataField dataField1 = newDataFieldBuilder().withName(stringAttribute.getName()).withLabel(stringAttribute.getName().toUpperCase())
					.withIconName(SimpleOrderModel.getIconName(stringAttribute)).withDataFeature(stringAttribute).withCol(3).build();
		DataField dataField2 = newDataFieldBuilder().withName(numericAttribute.getName()).withLabel(numericAttribute.getName().toUpperCase())
				.withIconName(SimpleOrderModel.getIconName(numericAttribute)).withDataFeature(numericAttribute).withCol(3).build();
		DataField dataField3 = newDataFieldBuilder().withName(passwordAttribute.getName()).withLabel(passwordAttribute.getName().toUpperCase())
				.withIconName(SimpleOrderModel.getIconName(passwordAttribute)).withDataFeature(passwordAttribute).withCol(3).build();
		
        Group group1 = newGroupBuilder().withName("group1").withLabel("G1").withCol(4).withRow(6)
        		.withFrame(true)
        		.withStretch(Stretch.BOTH)
        		.withFit(Fit.LOOSE)
        		.withLayout(Layout.HORIZONTAL)
        		.withHorizontal(Horizontal.CENTER)
        		.withVertical(Vertical.CENTER)
        		.withComponents(dataField1, dataField2)
        		.build();
        
        Group group2 = newGroupBuilder().withName("group2").withLabel("G2").withCol(8).withRow(2)
        		.withFrame(false)
        		.withStretch(Stretch.HORIZONTAL)
        		.withFit(Fit.TIGHT)
        		.withLayout(Layout.VERTICAL)
        		.withHorizontal(Horizontal.RIGHT)
        		.withComponents(dataField3, tabular)
        		.withVertical(Vertical.TOP)
        		.build();
        
        TransferObjectView view = newTransferObjectViewBuilder().withName("VIEW").withLabel(e1.getName())
        		.withComponents(group1).build();
        TransferObjectForm form = newTransferObjectFormBuilder().withName("FORM").withLabel(e1.getName())
        		.withComponents(group2).build();
        
        useEntityType(e1).withForm(form).withView(view).build();
        
        esmModel.addContent(model);
        
        transform();

        final Optional<Application> application = allUi(Application.class).filter(a -> a.getName().equals(actor.getFQName()))
                .findAny();
        assertTrue(application.isPresent());
        
        final Optional<PageDefinition> e1ViewPage = application.get().getPages().stream().filter(p -> p.getPageType().equals(PageType.VIEW) && p.getDataElement().getName().equals(access1.getName())).findAny();
        assertTrue(e1ViewPage.isPresent());
        
        final Optional<PageContainer> defaultContainer1 = e1ViewPage.get().getContainers().stream().filter(c -> c.getName().equals("default")).findAny();
        assertTrue(defaultContainer1.isPresent());
        final Optional<VisualElement> flexFromView = defaultContainer1.get().getChildren().stream().filter(c -> c instanceof Flex && c.getName().equals(view.getName())).findAny();
        assertTrue(flexFromView.isPresent());
        assertEquals(1, ((Flex) flexFromView.get()).getChildren().size());
        assertEquals(group1.getName(), ((Flex) flexFromView.get()).getChildren().get(0).getName());
        assertTrue(((Flex) flexFromView.get()).getChildren().get(0) instanceof Flex);
        Flex group1Flex = (Flex)((Flex) flexFromView.get()).getChildren().get(0);
        assertEquals(2, group1Flex.getChildren().size());
        assertEquals(4d, group1Flex.getCol());
        assertEquals(6d, group1Flex.getRow());
        assertEquals("G 1", group1Flex.getLabel());
        assertEquals(hu.blackbelt.judo.meta.ui.Fit.LOOSE, group1Flex.getFit());
        assertEquals(hu.blackbelt.judo.meta.ui.Axis.HORIZONTAL, group1Flex.getDirection());
        assertEquals(hu.blackbelt.judo.meta.ui.MainAxisAlignment.CENTER, group1Flex.getMainAxisAlignment());
        assertEquals(hu.blackbelt.judo.meta.ui.CrossAxisAlignment.CENTER, group1Flex.getCrossAxisAlignment());
        
        final Optional<PageDefinition> e1CreatePage = application.get().getPages().stream().filter(p -> p.getPageType().equals(PageType.CREATE) && p.getDataElement().getName().equals(access1.getName())).findAny();
        assertTrue(e1CreatePage.isPresent());
        
        final Optional<PageContainer> defaultContainer2 = e1CreatePage.get().getContainers().stream().filter(c -> c.getName().equals("default")).findAny();
        assertTrue(defaultContainer2.isPresent());
        final Optional<VisualElement> flexFromForm = defaultContainer2.get().getChildren().stream().filter(c -> c instanceof Flex && c.getName().equals(form.getName())).findAny();
        assertTrue(flexFromForm.isPresent());
        assertEquals(1, ((Flex) flexFromForm.get()).getChildren().size());
        assertEquals(group2.getName(), ((Flex) flexFromForm.get()).getChildren().get(0).getName());
        assertTrue(((Flex) flexFromForm.get()).getChildren().get(0) instanceof Flex);
        Flex group2Flex = (Flex)((Flex) flexFromForm.get()).getChildren().get(0);
        assertEquals(2, group2Flex.getChildren().size());
        assertEquals(8d, group2Flex.getCol());
        assertEquals(2d, group2Flex.getRow());
        assertEquals("G 2", group2Flex.getLabel());
        assertEquals(hu.blackbelt.judo.meta.ui.Fit.TIGHT, group2Flex.getFit());
        assertEquals(hu.blackbelt.judo.meta.ui.Axis.VERTICAL, group2Flex.getDirection());
        assertEquals(hu.blackbelt.judo.meta.ui.MainAxisAlignment.START, group2Flex.getMainAxisAlignment());
        assertEquals(hu.blackbelt.judo.meta.ui.CrossAxisAlignment.END, group2Flex.getCrossAxisAlignment());
    }
    
    @Test
    void testTransformTabularReferenceField() throws Exception {
        testName = "transformTabularReferenceField";
        
        /*
         * 1. if no columns are defined - placeholder is created
         * 2. if the relation represented by the tabular ref field is association - button is created, button action target is the table page of the relation target
         * 3. if the relation represented by the tabular ref field is composition / aggregation - table is created - target defined / not target defined
         */
        final String MODEL_NAME = "Model";
        final String ENTITY_TYPE_NAME_1 = "E1";
        final String ENTITY_TYPE_NAME_2 = "E2";
        final String ENTITY_TYPE_NAME_3 = "E3";
        final String ENTITY_TYPE_NAME_4 = "E4";
        final String ENTITY_TYPE_NAME_5 = "E5";
        final String ENTITY_TYPE_NAME_6 = "E6";
        final String ACTOR_TYPE_NAME = "Actor";
        
        StringType string = newStringTypeBuilder().withName("string").withMaxLength(256)
        		.withRegExp(".*").build();
        NumericType numeric = newNumericTypeBuilder().withName("numeric")
        		.withPrecision(2).withScale(1).build();
        
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
        
        DataMember idE4 = newDataMemberBuilder().withName("id").withDataType(numeric).withRequired(true).withIdentifier(true).build();
        DataMember nameE4 = newDataMemberBuilder().withName("name").withDataType(string).build();
        final EntityType e4 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_4)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withAttributes(idE4, nameE4)
                .build();
        useEntityType(e4).withMappedEntity(e4).build();
        final EntityType e5 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_5)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .build();
        useEntityType(e5).withMappedEntity(e5).build();
        
        DataMember idE6 = newDataMemberBuilder().withName("id").withDataType(numeric).withRequired(true).withIdentifier(true).build();
        DataMember nameE6 = newDataMemberBuilder().withName("name").withDataType(string).build();
        final EntityType e6 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_6)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withAttributes(idE6, nameE6)
                .build();
        useEntityType(e6).withMappedEntity(e6).build();
        
        ActorType actor = newActorTypeBuilder()
                .withName(ACTOR_TYPE_NAME)
                .withRealm("sandbox")
                .build();
        
        Access access1 = newAccessBuilder().withName("e1")
        		.withTarget(e1)
        		.withLower(0)
        		.withUpper(-1)
        		.withCreateable(true)
        		.withTargetDefinedCRUD(false)
        		.build();
        
        Access access2 = newAccessBuilder().withName("e2")
        		.withTarget(e2)
        		.withLower(0)
        		.withUpper(-1)
        		.withCreateable(false)
        		.withTargetDefinedCRUD(false)
        		.build();
        
        Access access3 = newAccessBuilder().withName("e3")
        		.withTarget(e3)
        		.withLower(0)
        		.withUpper(-1)
        		.withCreateable(false)
        		.withTargetDefinedCRUD(false)
        		.build();
        
        /*
         * 1. if no columns are defined - placeholder is created
         */
        OneWayRelationMember aggregationNotTargetDefinedNoColumns = newOneWayRelationMemberBuilder()
        		.withName("aggregationNotTargetDefinedNoColumns")
        		.withLower(0)
        		.withUpper(-1)
        		.withTarget(e5)
        		.withMemberType(MemberType.STORED)
        		.withRelationKind(RelationKind.AGGREGATION)
        		.build();
        OneWayRelationMember associationNotTargetDefinedNoColumns = newOneWayRelationMemberBuilder()
        		.withName("associationNotTargetDefinedNoColumns")
        		.withLower(0)
        		.withUpper(-1)
        		.withTarget(e5)
        		.withMemberType(MemberType.STORED)
        		.withRelationKind(RelationKind.ASSOCIATION)
        		.build();
        OneWayRelationMember aggregationTargetDefinedNoColumns = newOneWayRelationMemberBuilder()
        		.withName("aggregationTargetDefinedNoColumns")
        		.withLower(0)
        		.withUpper(-1)
        		.withTarget(e5)
        		.withMemberType(MemberType.STORED)
        		.withRelationKind(RelationKind.AGGREGATION)
        		.build();
        OneWayRelationMember associationTargetDefinedNoColumns = newOneWayRelationMemberBuilder()
        		.withName("associationTargetDefinedNoColumns")
        		.withLower(0)
        		.withUpper(-1)
        		.withTarget(e5)
        		.withMemberType(MemberType.STORED)
        		.withRelationKind(RelationKind.ASSOCIATION)
        		.build();
        
        useEntityType(e1).withRelations(associationNotTargetDefinedNoColumns, associationTargetDefinedNoColumns, aggregationNotTargetDefinedNoColumns, aggregationTargetDefinedNoColumns).build();
        
        TabularReferenceField tabular1 = newTabularReferenceFieldBuilder().withName(aggregationNotTargetDefinedNoColumns.getName())
				.withLabel(aggregationNotTargetDefinedNoColumns.getName().toUpperCase()).withRelationFeature(aggregationNotTargetDefinedNoColumns).withCol(12)
				.withTargetDefinedTabular(false)
				.build();
        TabularReferenceField tabular2 = newTabularReferenceFieldBuilder().withName(associationNotTargetDefinedNoColumns.getName())
				.withLabel(associationNotTargetDefinedNoColumns.getName().toUpperCase()).withRelationFeature(associationNotTargetDefinedNoColumns).withCol(12)
				.withTargetDefinedTabular(false)
				.build();
        TabularReferenceField tabular3 = newTabularReferenceFieldBuilder().withName(aggregationTargetDefinedNoColumns.getName())
				.withLabel(aggregationTargetDefinedNoColumns.getName().toUpperCase()).withRelationFeature(aggregationTargetDefinedNoColumns).withCol(12)
				.withTargetDefinedTabular(true)
				.build();
        TabularReferenceField tabular4 = newTabularReferenceFieldBuilder().withName(associationTargetDefinedNoColumns.getName())
				.withLabel(associationTargetDefinedNoColumns.getName().toUpperCase()).withRelationFeature(associationTargetDefinedNoColumns).withCol(12)
				.withTargetDefinedTabular(true)
				.build();
        
        useEntityType(e5).withTable(
        		newTransferObjectTableBuilder().withName(e5.getName() + "TABLE").build()
        		).build();
        
        useEntityType(e1).withView(
        		newTransferObjectViewBuilder().withName(e1.getName() + "VIEW")
        			.withComponents(tabular1, tabular2, tabular3, tabular4)
        		).build();
        
        /*
         * 2. if the relation represented by the tabular ref field is association - button is created, button action target is the table page of the relation target
         */
        OneWayRelationMember associationNotTargetDefined = newOneWayRelationMemberBuilder()
        		.withName("associationNotTargetDefined")
        		.withLower(0)
        		.withUpper(-1)
        		.withTarget(e6)
        		.withMemberType(MemberType.STORED)
        		.withRelationKind(RelationKind.ASSOCIATION)
        		.build();
        OneWayRelationMember associationTargetDefined = newOneWayRelationMemberBuilder()
        		.withName("associationTargetDefined")
        		.withLower(0)
        		.withUpper(-1)
        		.withTarget(e6)
        		.withMemberType(MemberType.STORED)
        		.withRelationKind(RelationKind.ASSOCIATION)
        		.build();
        
        useEntityType(e2).withRelations(associationNotTargetDefined, associationTargetDefined).build();

        TabularReferenceField tabular5 = newTabularReferenceFieldBuilder().withName(associationNotTargetDefined.getName())
				.withLabel(associationNotTargetDefined.getName().toUpperCase()).withRelationFeature(associationNotTargetDefined).withCol(12)
				.withTargetDefinedTabular(false)
				.withColumns(newDataColumnBuilder().withName(idE6.getName()).withDataFeature(idE6).build(),
    					newDataColumnBuilder().withName(nameE6.getName()).withDataFeature(nameE6).build())
				.build();
        TabularReferenceField tabular6 = newTabularReferenceFieldBuilder().withName(associationTargetDefined.getName())
				.withLabel(associationTargetDefined.getName().toUpperCase()).withRelationFeature(associationTargetDefined).withCol(12)
				.withTargetDefinedTabular(true)
				.build();
        
        useEntityType(e6).withTable(
        		newTransferObjectTableBuilder().withName(e6.getName() + "TABLE")
        			.withColumns(newDataColumnBuilder().withName(idE6.getName()).withDataFeature(idE6).build(),
        					newDataColumnBuilder().withName(nameE6.getName()).withDataFeature(nameE6).build())
        			.build()
        		).build();
        
        useEntityType(e2).withView(
        		newTransferObjectViewBuilder().withName(e2.getName() + "VIEW")
        			.withComponents(tabular5, tabular6)
        		).build();
        
        /*
         * 3. if the relation represented by the tabular ref field is composition / aggregation - table is created - target defined / not target defined
         */
        OneWayRelationMember compositionNotTargetDefined = newOneWayRelationMemberBuilder()
        		.withName("compositionNotTargetDefined")
        		.withLower(0)
        		.withUpper(-1)
        		.withTarget(e4)
        		.withMemberType(MemberType.STORED)
        		.withRelationKind(RelationKind.COMPOSITION)
        		.build();
        OneWayRelationMember compositionTargetDefined = newOneWayRelationMemberBuilder()
        		.withName("compositionTargetDefined")
        		.withLower(0)
        		.withUpper(-1)
        		.withTarget(e4)
        		.withMemberType(MemberType.STORED)
        		.withRelationKind(RelationKind.COMPOSITION)
        		.build();
        
        useEntityType(e3).withRelations(compositionNotTargetDefined, compositionTargetDefined).build();

        TabularReferenceField tabular7 = newTabularReferenceFieldBuilder().withName(compositionNotTargetDefined.getName())
				.withLabel(compositionNotTargetDefined.getName().toUpperCase()).withRelationFeature(compositionNotTargetDefined).withCol(12)
				.withTargetDefinedTabular(false)
				.withColumns(newDataColumnBuilder().withName(idE4.getName()).withDataFeature(idE4).build(),
    					newDataColumnBuilder().withName(nameE4.getName()).withDataFeature(nameE4).build())
				.build();
        TabularReferenceField tabular8 = newTabularReferenceFieldBuilder().withName(compositionTargetDefined.getName())
				.withLabel(compositionTargetDefined.getName().toUpperCase()).withRelationFeature(compositionTargetDefined).withCol(12)
				.withTargetDefinedTabular(true)
				.build();
        
        useEntityType(e4).withTable(
        		newTransferObjectTableBuilder().withName(e4.getName() + "TABLE")
        			.withColumns(newDataColumnBuilder().withName(idE4.getName()).withDataFeature(idE4).build(),
        					newDataColumnBuilder().withName(nameE4.getName()).withDataFeature(nameE4).build())
        			.build()
        		).build();
        
        useEntityType(e3).withView(
        		newTransferObjectViewBuilder().withName(e3.getName() + "VIEW")
        			.withComponents(tabular7, tabular8)
        		).build();

        useActorType(actor).withAccesses(access1, access2, access3).build();
        
        final Model model = newModelBuilder().withName(MODEL_NAME)
                .withElements(actor, e1, e2, e3, e4, e5, e6, string, numeric).build();

        esmModel.addContent(model);
        
        transform();
        
        final Optional<Application> application = allUi(Application.class).filter(a -> a.getName().equals(actor.getFQName())).findAny();
        assertTrue(application.isPresent());
        final Optional<PageDefinition> e1ViewPage = application.get().getPages().stream().filter(p -> p.getPageType().equals(PageType.VIEW) && p.getDataElement().getName().equals(access1.getName())).findAny();
        assertTrue(e1ViewPage.isPresent());
        final Optional<PageContainer> defaultContainer1 = e1ViewPage.get().getContainers().stream().filter(c -> c.getName().equals("default")).findAny();
        assertTrue(defaultContainer1.isPresent());
        final Optional<VisualElement> flexFromViewOpt1 = defaultContainer1.get().getChildren().stream().filter(c -> c instanceof Flex && c.getName().equals(e1.getView().getName())).findAny();
        assertTrue(flexFromViewOpt1.isPresent());
        Flex flexFromView1 = (Flex) flexFromViewOpt1.get();
        assertEquals(4, flexFromView1.getChildren().size());
        assertTrue(flexFromView1.getChildren().stream().allMatch(c -> c instanceof Spacer));

        final Optional<PageDefinition> e2ViewPage = application.get().getPages().stream().filter(p -> p.getPageType().equals(PageType.VIEW) && p.getDataElement().getName().equals(access2.getName())).findAny();
        assertTrue(e2ViewPage.isPresent());
        final Optional<PageContainer> defaultContainer2 = e2ViewPage.get().getContainers().stream().filter(c -> c.getName().equals("default")).findAny();
        assertTrue(defaultContainer2.isPresent());
        final Optional<VisualElement> flexFromViewOpt2 = defaultContainer2.get().getChildren().stream().filter(c -> c instanceof Flex && c.getName().equals(e2.getView().getName())).findAny();
        assertTrue(flexFromViewOpt2.isPresent());
        Flex flexFromView2 = (Flex) flexFromViewOpt2.get();
        assertEquals(2, flexFromView2.getChildren().size());
        assertTrue(flexFromView2.getChildren().stream().allMatch(c -> c instanceof Button));
        
        final Optional<VisualElement> button1opt = flexFromView2.getChildren().stream().filter(b -> b.getName().equals(tabular5.getName() + "#TabularReferenceButton")).findAny();
        assertTrue(button1opt.isPresent());
        Button button1 = (Button) button1opt.get();
        assertTrue(button1.getAction() instanceof NavigationToPageAction);
        assertTrue(((NavigationToPageAction)button1.getAction()).getTarget().getName().endsWith("TableForButton"));
        
        final Optional<VisualElement> button2opt = flexFromView2.getChildren().stream().filter(b -> b.getName().equals(tabular6.getName() + "#TabularReferenceButton")).findAny();
        assertTrue(button2opt.isPresent());
        Button button2 = (Button) button2opt.get();
        assertTrue(button2.getAction() instanceof NavigationToPageAction);
        assertTrue(((NavigationToPageAction)button2.getAction()).getTarget().getName().endsWith("Table"));
        
        final Optional<PageDefinition> e3ViewPage = application.get().getPages().stream().filter(p -> p.getPageType().equals(PageType.VIEW) && p.getDataElement().getName().equals(access3.getName())).findAny();
        assertTrue(e3ViewPage.isPresent());
        final Optional<PageContainer> defaultContainer3 = e3ViewPage.get().getContainers().stream().filter(c -> c.getName().equals("default")).findAny();
        assertTrue(defaultContainer3.isPresent());
        final Optional<VisualElement> flexFromViewOpt3 = defaultContainer3.get().getChildren().stream().filter(c -> c instanceof Flex && c.getName().equals(e3.getView().getName())).findAny();
        assertTrue(flexFromViewOpt3.isPresent());
        Flex flexFromView3 = (Flex) flexFromViewOpt3.get();
        assertEquals(2, flexFromView3.getChildren().size());
        assertTrue(flexFromView3.getChildren().stream().allMatch(c -> c instanceof Flex));
        
        Optional<VisualElement> uiTable1opt = flexFromView3.getChildren().stream().filter(c -> c.getName().equals(tabular7.getName())).findAny();
        Optional<VisualElement> uiTable2opt = flexFromView3.getChildren().stream().filter(c -> c.getName().equals(tabular8.getName())).findAny();
        assertTrue(uiTable1opt.isPresent());
        Flex uiTable1 = (Flex) uiTable1opt.get();
        assertTrue(uiTable2opt.isPresent());
        Flex uiTable2 = (Flex) uiTable2opt.get();
        
        assertTrue(uiTable1.getChildren().get(0) instanceof Label);
        assertTrue(uiTable1.getChildren().get(1) instanceof Table);
        assertTrue(((Table)uiTable1.getChildren().get(1)).getColumns().stream().anyMatch(c -> c instanceof Formatted && ((Formatted) c).getAttributeType().getName().equals(idE4.getName())));
        assertTrue(((Table)uiTable1.getChildren().get(1)).getColumns().stream().anyMatch(c -> c instanceof Formatted && ((Formatted) c).getAttributeType().getName().equals(nameE4.getName())));
        
        assertTrue(uiTable2.getChildren().get(0) instanceof Label);
        assertTrue(uiTable2.getChildren().get(1) instanceof Table);
        assertTrue(((Table)uiTable2.getChildren().get(1)).getColumns().stream().anyMatch(c -> c instanceof Formatted && ((Formatted) c).getAttributeType().getName().equals(idE4.getName())));
        assertTrue(((Table)uiTable2.getChildren().get(1)).getColumns().stream().anyMatch(c -> c instanceof Formatted && ((Formatted) c).getAttributeType().getName().equals(nameE4.getName())));
    }
    
    @Test 
    void testCreateVisualElements() throws Exception {
        testName = "createVisualElements";

        final String MODEL_NAME = "Model";
        final String ENTITY_TYPE_NAME_0 = "E0";
        final String ENTITY_TYPE_NAME_1 = "E1";
        final String ACTOR_TYPE_NAME = "Actor";
        
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
        
        DataMember passwordAttribute = newDataMemberBuilder().withName("password").withDataType(password)
        		.withMemberType(MemberType.STORED).build();
        DataMember stringAttribute1 = newDataMemberBuilder().withName("string1").withDataType(string)
        		.withMemberType(MemberType.STORED).build();
        DataMember stringAttribute2 = newDataMemberBuilder().withName("string2").withDataType(string)
        		.withMemberType(MemberType.STORED).build();
        DataMember stringAttribute3 = newDataMemberBuilder().withName("string3").withDataType(string)
        		.withMemberType(MemberType.STORED).build();
        DataMember stringAttribute4 = newDataMemberBuilder().withName("string4").withDataType(string)
        		.withMemberType(MemberType.STORED).build();
        DataMember numericAttribute = newDataMemberBuilder().withName("numeric").withDataType(numeric)
        		.withMemberType(MemberType.STORED).build();
        DataMember booleanAttribute = newDataMemberBuilder().withName("boolean").withDataType(booleanType)
        		.withMemberType(MemberType.STORED).build();
        DataMember dateAttribute = newDataMemberBuilder().withName("date").withDataType(dateType)
        		.withMemberType(MemberType.STORED).build();
        DataMember timestampAttribute = newDataMemberBuilder().withName("timestamp").withDataType(timestamp)
        		.withMemberType(MemberType.STORED).build();
        DataMember enumerationAttribute1 = newDataMemberBuilder().withName("enumeration1").withDataType(enumeration)
        		.withMemberType(MemberType.STORED).build();
        DataMember enumerationAttribute2 = newDataMemberBuilder().withName("enumeration2").withDataType(enumeration)
        		.withMemberType(MemberType.STORED).build();
        Operation operation = newOperationBuilder().withName("operation").withOperationType(OperationType.INSTANCE)
        		.withInherited(newInheritedOperationReferenceBuilder().build())
        		.withBinding("operation")
        		.build();
        
        ActorType actor = newActorTypeBuilder()
                .withName(ACTOR_TYPE_NAME)
                .withRealm("sandbox")
                .build();
        
        final EntityType e0 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_0)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withAttributes(dateAttribute, timestampAttribute, enumerationAttribute1, enumerationAttribute2)
                .withOperations(operation)
                .build();
        e0.setMappedEntity(e0);
        
        final EntityType e1 = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME_1)
                .withCreateable(false)
                .withUpdateable(false)
                .withDeleteable(false)
                .withAttributes(passwordAttribute, stringAttribute1, stringAttribute2, stringAttribute3, stringAttribute4, numericAttribute, booleanAttribute)
                .build();
        e1.setMappedEntity(e1);
        e1.addGeneralization(e0);
        
        Access access1 = newAccessBuilder().withName("e1")
        		.withTarget(e1)
        		.withLower(0)
        		.withUpper(-1)
        		.withCreateable(true)
        		.withTargetDefinedCRUD(false)
        		.build();

        useActorType(actor).withAccesses(access1).build();
        
        DataField dataFieldTextInput = newDataFieldBuilder().withName("TextInput").withLabel(stringAttribute1.getName().toUpperCase())
				.withIconName(SimpleOrderModel.getIconName(stringAttribute1)).withDataFeature(stringAttribute1).withTextWidget(TextWidget.INPUT)
				.withTextMultiLine(false).withCol(2).withRow(4)
				.withStretch(Stretch.NONE).withFit(Fit.LOOSE).build();
        DataField dataFieldText = newDataFieldBuilder().withName("Text").withLabel(stringAttribute2.getName().toUpperCase())
				.withIconName(SimpleOrderModel.getIconName(stringAttribute2)).withDataFeature(stringAttribute2).withTextWidget(TextWidget.TEXT)
				.withTextMultiLine(false).withCol(2).withRow(4)
				.withStretch(Stretch.HORIZONTAL).withFit(Fit.TIGHT).build();
        DataField dataFieldTextAreaInput = newDataFieldBuilder().withName("TextAreaInput").withLabel(stringAttribute3.getName().toUpperCase())
				.withIconName(SimpleOrderModel.getIconName(stringAttribute3)).withDataFeature(stringAttribute3).withTextWidget(TextWidget.INPUT)
				.withTextMultiLine(true).withCol(2).withRow(6)
				.withStretch(Stretch.HORIZONTAL).withFit(Fit.LOOSE).build();
        DataField dataFieldTextArea = newDataFieldBuilder().withName("TextArea").withLabel(stringAttribute4.getName().toUpperCase())
				.withIconName(SimpleOrderModel.getIconName(stringAttribute4)).withDataFeature(stringAttribute4).withTextWidget(TextWidget.TEXT)
				.withTextMultiLine(true).withCol(2).withRow(6)
				.withStretch(Stretch.VERTICAL).withFit(Fit.TIGHT).build();
        DataField dataFieldNumeric = newDataFieldBuilder().withName(numericAttribute.getName()).withLabel(numericAttribute.getName().toUpperCase())
				.withIconName(SimpleOrderModel.getIconName(numericAttribute)).withDataFeature(numericAttribute).withCol(3)
				.withStretch(Stretch.HORIZONTAL).withFit(Fit.LOOSE).build();
        DataField dataFieldSwitch = newDataFieldBuilder().withName(booleanAttribute.getName()).withLabel(booleanAttribute.getName().toUpperCase())
				.withIconName(SimpleOrderModel.getIconName(booleanAttribute)).withDataFeature(booleanAttribute).withCol(3)
				.withStretch(Stretch.BOTH).withFit(Fit.TIGHT).build();
        DataField dataFieldDate = newDataFieldBuilder().withName(dateAttribute.getName()).withLabel(dateAttribute.getName().toUpperCase())
				.withIconName(SimpleOrderModel.getIconName(dateAttribute)).withDataFeature(dateAttribute).withCol(3)
				.withStretch(Stretch.NONE).withFit(Fit.LOOSE).build();
        DataField dataFieldDateTime = newDataFieldBuilder().withName(timestampAttribute.getName()).withLabel(timestampAttribute.getName().toUpperCase())
				.withIconName(SimpleOrderModel.getIconName(timestampAttribute)).withDataFeature(timestampAttribute).withCol(3)
				.withStretch(Stretch.BOTH).withFit(Fit.LOOSE).build();
        DataField dataFieldRadio = newDataFieldBuilder().withName("radio").withLabel(enumerationAttribute1.getName().toUpperCase())
				.withIconName(SimpleOrderModel.getIconName(enumerationAttribute1)).withDataFeature(enumerationAttribute1).withEnumWidget(EnumWidget.RADIO).withCol(3)
				.withStretch(Stretch.HORIZONTAL).withFit(Fit.TIGHT).build();
        DataField dataFieldCombo = newDataFieldBuilder().withName("combo").withLabel(enumerationAttribute2.getName().toUpperCase())
				.withIconName(SimpleOrderModel.getIconName(enumerationAttribute2)).withDataFeature(enumerationAttribute2).withEnumWidget(EnumWidget.COMBO).withCol(3)
				.withStretch(Stretch.VERTICAL).withFit(Fit.LOOSE).build();
        DataField dataFieldPassword = newDataFieldBuilder().withName(passwordAttribute.getName()).withLabel(passwordAttribute.getName().toUpperCase())
 				.withIconName(SimpleOrderModel.getIconName(passwordAttribute)).withDataFeature(passwordAttribute).withCol(3)
 				.withStretch(Stretch.NONE).withFit(Fit.LOOSE).build();
        OperationForm operationForm = newOperationFormBuilder().withName("operation").withOperation("operation")
        		.withRow(1).withCol(2).withFit(Fit.LOOSE).withStretch(Stretch.NONE)
				.build();
        
        Divider divider = newDividerBuilder().withName("divider").withCol(4).withRow(2).withStretch(Stretch.HORIZONTAL).withFit(Fit.TIGHT).withLabel("label").build();
        Icon icon = newIconBuilder().withName("icon").withCol(2).withRow(2).withIconName("basket").withStretch(Stretch.NONE).withFit(Fit.LOOSE).build();
        Placeholder placeholder = newPlaceholderBuilder().withName("placeholder").withCol(4).withRow(2).withStretch(Stretch.BOTH).withFit(Fit.TIGHT).build();
        TextField textField = newTextFieldBuilder().withName("textField").withText("hello").withCol(3).withRow(2).withStretch(Stretch.HORIZONTAL).withFit(Fit.LOOSE).build();
        
        useEntityType(e1).withView(
        			newTransferObjectViewBuilder().withName("View")
        				.withComponents(dataFieldTextInput, dataFieldText, dataFieldTextAreaInput,
        						dataFieldTextArea, dataFieldNumeric, dataFieldSwitch, dataFieldDate,
        						dataFieldDateTime, dataFieldRadio, dataFieldCombo, dataFieldPassword,
        						divider, icon, placeholder, textField, operationForm)
        				.build()
        		).build();
        
        final Model model = newModelBuilder().withName(MODEL_NAME)
                .withElements(actor, e0, e1, password, string, numeric, booleanType, dateType, timestamp, enumeration).build();

        esmModel.addContent(model);
        
        transform();

        final Optional<Application> application = allUi(Application.class).filter(a -> a.getName().equals(actor.getFQName()))
                .findAny();
        assertTrue(application.isPresent());
        final Optional<ClassType> uiE1 = application.get().getDataElements().stream().filter(e -> e instanceof ClassType && e.getName().equals(e1.getFQName()))
        		.map(e -> (ClassType) e).findAny();
        assertTrue(uiE1.isPresent());
        
        final Optional<PageDefinition> e1ViewPage = application.get().getPages().stream().filter(p -> p.getPageType().equals(PageType.VIEW) && p.getDataElement().getName().equals(access1.getName())).findAny();
        assertTrue(e1ViewPage.isPresent());
        final Optional<PageContainer> defaultContainer = e1ViewPage.get().getContainers().stream().filter(c -> c.getName().equals("default")).findAny();
        assertTrue(defaultContainer.isPresent());
        final Optional<VisualElement> flexFromViewOpt = defaultContainer.get().getChildren().stream().filter(c -> c instanceof Flex && c.getName().equals(e1.getView().getName())).findAny();
        assertTrue(flexFromViewOpt.isPresent());
        Flex flexFromView = (Flex) flexFromViewOpt.get();
        
        final Optional<Formatted> uiText1 = flexFromView.getChildren().stream().filter(c -> c instanceof Formatted)
        		.map(f -> (Formatted) f).filter(f -> f.getName().equals(dataFieldText.getName())).findAny();
        assertTrue(uiText1.isPresent());
        assertEquals(2d, uiText1.get().getCol());
        assertEquals(4d, uiText1.get().getRow());
        assertEquals(model.getName() + "." + string.getName(), uiText1.get().getAttributeType().getDataType().getName());
        assertEquals(stringAttribute2.getName(), uiText1.get().getAttributeType().getName());
        assertEquals(uiE1.get(), uiText1.get().getAttributeType().eContainer());
        assertEquals(hu.blackbelt.judo.meta.ui.Fit.TIGHT, uiText1.get().getFit());
        assertEquals(hu.blackbelt.judo.meta.ui.Stretch.HORIZONTAL, uiText1.get().getStretch());
        
        final Optional<Formatted> uiText2 = flexFromView.getChildren().stream().filter(c -> c instanceof Formatted)
        		.map(f -> (Formatted) f).filter(f -> f.getName().equals(dataFieldTextArea.getName())).findAny();
        assertTrue(uiText2.isPresent());
        assertEquals(2d, uiText2.get().getCol());
        assertEquals(6d, uiText2.get().getRow());
        assertEquals(model.getName() + "." + string.getName(), uiText2.get().getAttributeType().getDataType().getName());
        assertEquals(stringAttribute4.getName(), uiText2.get().getAttributeType().getName());
        assertEquals(uiE1.get(), uiText2.get().getAttributeType().eContainer());
        assertEquals(hu.blackbelt.judo.meta.ui.Fit.TIGHT, uiText2.get().getFit());
        assertEquals(hu.blackbelt.judo.meta.ui.Stretch.VERTICAL, uiText2.get().getStretch());

        final Optional<TextInput> uiText3 = flexFromView.getChildren().stream().filter(c -> c instanceof TextInput)
        		.map(f -> (TextInput) f).filter(f -> f.getName().equals(dataFieldTextInput.getName())).findAny();
        assertTrue(uiText3.isPresent());
        assertEquals(2d, uiText3.get().getCol());
        assertEquals(4d, uiText3.get().getRow());
        assertEquals(model.getName() + "." + string.getName(), uiText3.get().getAttributeType().getDataType().getName());
        assertEquals(stringAttribute1.getName(), uiText3.get().getAttributeType().getName());
        assertEquals(uiE1.get(), uiText3.get().getAttributeType().eContainer());
        assertEquals(hu.blackbelt.judo.meta.ui.Fit.LOOSE, uiText3.get().getFit());
        assertEquals(hu.blackbelt.judo.meta.ui.Stretch.NONE, uiText3.get().getStretch());
        
        final Optional<TextArea> uiText4 = flexFromView.getChildren().stream().filter(c -> c instanceof TextArea)
        		.map(f -> (TextArea) f).filter(f -> f.getName().equals(dataFieldTextAreaInput.getName())).findAny();
        assertTrue(uiText4.isPresent());
        assertEquals(2d, uiText4.get().getCol());
        assertEquals(1d, uiText4.get().getRow());
        assertEquals(model.getName() + "." + string.getName(), uiText4.get().getAttributeType().getDataType().getName());
        assertEquals(stringAttribute3.getName(), uiText4.get().getAttributeType().getName());
        assertEquals(uiE1.get(), uiText4.get().getAttributeType().eContainer());
        assertEquals(hu.blackbelt.judo.meta.ui.Fit.LOOSE, uiText4.get().getFit());
        assertEquals(hu.blackbelt.judo.meta.ui.Stretch.HORIZONTAL, uiText4.get().getStretch());
        
        final Optional<NumericInput> uiNumeric = flexFromView.getChildren().stream().filter(c -> c instanceof NumericInput)
        		.map(f -> (NumericInput) f).filter(f -> f.getName().equals(dataFieldNumeric.getName())).findAny();
        assertTrue(uiNumeric.isPresent());
        assertEquals(3d, uiNumeric.get().getCol());
        assertEquals(1d, uiNumeric.get().getRow());
        assertEquals(model.getName() + "." + numeric.getName(), uiNumeric.get().getAttributeType().getDataType().getName());
        assertEquals(numericAttribute.getName(), uiNumeric.get().getAttributeType().getName());
        assertEquals(uiE1.get(), uiNumeric.get().getAttributeType().eContainer());
        assertEquals(hu.blackbelt.judo.meta.ui.Fit.LOOSE, uiNumeric.get().getFit());
        assertEquals(hu.blackbelt.judo.meta.ui.Stretch.HORIZONTAL, uiNumeric.get().getStretch());
        
        final Optional<Switch> uiSwitch = flexFromView.getChildren().stream().filter(c -> c instanceof Switch)
        		.map(f -> (Switch) f).filter(f -> f.getName().equals(dataFieldSwitch.getName())).findAny();
        assertTrue(uiSwitch.isPresent());
        assertEquals(3d, uiSwitch.get().getCol());
        assertEquals(1d, uiSwitch.get().getRow());
        assertEquals(model.getName() + "." + booleanType.getName(), uiSwitch.get().getAttributeType().getDataType().getName());
        assertEquals(booleanAttribute.getName(), uiSwitch.get().getAttributeType().getName());
        assertEquals(uiE1.get(), uiSwitch.get().getAttributeType().eContainer());
        assertEquals(hu.blackbelt.judo.meta.ui.Fit.TIGHT, uiSwitch.get().getFit());
        assertEquals(hu.blackbelt.judo.meta.ui.Stretch.BOTH, uiSwitch.get().getStretch());
        
        final Optional<DateInput> uiDateInput = flexFromView.getChildren().stream().filter(c -> c instanceof DateInput)
        		.map(f -> (DateInput) f).filter(f -> f.getName().equals(dataFieldDate.getName())).findAny();
        assertTrue(uiDateInput.isPresent());
        assertEquals(3d, uiDateInput.get().getCol());
        assertEquals(1d, uiDateInput.get().getRow());
        assertEquals(model.getName() + "." + dateType.getName(), uiDateInput.get().getAttributeType().getDataType().getName());
        assertEquals(dateAttribute.getName(), uiDateInput.get().getAttributeType().getName());
        assertEquals(uiE1.get(), uiDateInput.get().getAttributeType().eContainer());
        assertEquals(hu.blackbelt.judo.meta.ui.Fit.LOOSE, uiDateInput.get().getFit());
        assertEquals(hu.blackbelt.judo.meta.ui.Stretch.NONE, uiDateInput.get().getStretch());
        
        final Optional<DateTimeInput> uiDateTimeInput = flexFromView.getChildren().stream().filter(c -> c instanceof DateTimeInput)
        		.map(f -> (DateTimeInput) f).filter(f -> f.getName().equals(dataFieldDateTime.getName())).findAny();
        assertTrue(uiDateTimeInput.isPresent());
        assertEquals(3d, uiDateTimeInput.get().getCol());
        assertEquals(1d, uiDateTimeInput.get().getRow());
        assertEquals(model.getName() + "." + timestamp.getName(), uiDateTimeInput.get().getAttributeType().getDataType().getName());
        assertEquals(timestampAttribute.getName(), uiDateTimeInput.get().getAttributeType().getName());
        assertEquals(uiE1.get(), uiDateTimeInput.get().getAttributeType().eContainer());
        assertEquals(hu.blackbelt.judo.meta.ui.Fit.LOOSE, uiDateTimeInput.get().getFit());
        assertEquals(hu.blackbelt.judo.meta.ui.Stretch.BOTH, uiDateTimeInput.get().getStretch());
        
        final Optional<EnumerationRadio> uiEnumerationRadio = flexFromView.getChildren().stream().filter(c -> c instanceof EnumerationRadio)
        		.map(f -> (EnumerationRadio) f).filter(f -> f.getName().equals(dataFieldRadio.getName())).findAny();
        assertTrue(uiEnumerationRadio.isPresent());
        assertEquals(3d, uiEnumerationRadio.get().getCol());
        assertEquals(1d, uiEnumerationRadio.get().getRow());
        assertEquals(model.getName() + "." + enumeration.getName(), uiEnumerationRadio.get().getAttributeType().getDataType().getName());
        assertEquals(enumerationAttribute1.getName(), uiEnumerationRadio.get().getAttributeType().getName());
        assertEquals(uiE1.get(), uiEnumerationRadio.get().getAttributeType().eContainer());
        assertEquals(hu.blackbelt.judo.meta.ui.Fit.TIGHT, uiEnumerationRadio.get().getFit());
        assertEquals(hu.blackbelt.judo.meta.ui.Stretch.HORIZONTAL, uiEnumerationRadio.get().getStretch());
        
        final Optional<EnumerationCombo> uiEnumerationCombo = flexFromView.getChildren().stream().filter(c -> c instanceof EnumerationCombo)
        		.map(f -> (EnumerationCombo) f).filter(f -> f.getName().equals(dataFieldCombo.getName())).findAny();
        assertTrue(uiEnumerationCombo.isPresent());
        assertEquals(3d, uiEnumerationCombo.get().getCol());
        assertEquals(1d, uiEnumerationCombo.get().getRow());
        assertEquals(model.getName() + "." + enumeration.getName(), uiEnumerationCombo.get().getAttributeType().getDataType().getName());
        assertEquals(enumerationAttribute2.getName(), uiEnumerationCombo.get().getAttributeType().getName());
        assertEquals(uiE1.get(), uiEnumerationCombo.get().getAttributeType().eContainer());
        assertEquals(hu.blackbelt.judo.meta.ui.Fit.LOOSE, uiEnumerationCombo.get().getFit());
        assertEquals(hu.blackbelt.judo.meta.ui.Stretch.VERTICAL, uiEnumerationCombo.get().getStretch());
        
        final Optional<PasswordInput> uiPasswordInput = flexFromView.getChildren().stream().filter(c -> c instanceof PasswordInput)
        		.map(f -> (PasswordInput) f).filter(f -> f.getName().equals(dataFieldPassword.getName())).findAny();
        assertTrue(uiPasswordInput.isPresent());
        assertEquals(3d, uiPasswordInput.get().getCol());
        assertEquals(1d, uiPasswordInput.get().getRow());
        assertEquals(model.getName() + "." + password.getName(), uiPasswordInput.get().getAttributeType().getDataType().getName());
        assertEquals(passwordAttribute.getName(), uiPasswordInput.get().getAttributeType().getName());
        assertEquals(uiE1.get(), uiPasswordInput.get().getAttributeType().eContainer());
        assertEquals(hu.blackbelt.judo.meta.ui.Fit.LOOSE, uiPasswordInput.get().getFit());
        assertEquals(hu.blackbelt.judo.meta.ui.Stretch.NONE, uiPasswordInput.get().getStretch());
        
        final Optional<Button> uiOperationButton = flexFromView.getChildren().stream().filter(c -> c instanceof Button)
        		.map(f -> (Button) f).filter(f -> f.getName().equals(operationForm.getName())).findAny();
        assertTrue(uiOperationButton.isPresent());
        assertEquals(2d, uiOperationButton.get().getCol());
        assertEquals(1d, uiOperationButton.get().getRow());
        assertEquals("operation", uiOperationButton.get().getDataElement().getName());
        assertEquals(hu.blackbelt.judo.meta.ui.Fit.LOOSE, uiOperationButton.get().getFit());
        assertEquals(hu.blackbelt.judo.meta.ui.Stretch.NONE, uiOperationButton.get().getStretch());
        assertTrue(uiOperationButton.get().getAction() instanceof CallOperationAction);
        assertEquals("operation", ((CallOperationAction)uiOperationButton.get().getAction()).getOperation().getName());
        assertEquals(uiE1.get(), ((CallOperationAction)uiOperationButton.get().getAction()).getOperation().eContainer());
        assertEquals(((CallOperationAction)uiOperationButton.get().getAction()).getOperation(), uiOperationButton.get().getDataElement());
        
        final Optional<hu.blackbelt.judo.meta.ui.Divider> uiDivider = flexFromView.getChildren().stream().filter(c -> c instanceof hu.blackbelt.judo.meta.ui.Divider)
        		.map(f -> (hu.blackbelt.judo.meta.ui.Divider) f).filter(f -> f.getName().equals(divider.getName())).findAny();
        assertTrue(uiDivider.isPresent());
        assertEquals(4d, uiDivider.get().getCol());
        assertEquals(2d, uiDivider.get().getRow());
        assertEquals(hu.blackbelt.judo.meta.ui.Fit.TIGHT, uiDivider.get().getFit());
        assertEquals(hu.blackbelt.judo.meta.ui.Stretch.HORIZONTAL, uiDivider.get().getStretch());
        
        final Optional<IconImage> uiIconImage = flexFromView.getChildren().stream().filter(c -> c instanceof IconImage)
        		.map(f -> (IconImage) f).filter(f -> f.getName().equals(icon.getName())).findAny();
        assertTrue(uiIconImage.isPresent());
        assertEquals(icon.getIconName(), uiIconImage.get().getIcon().getName());
        assertEquals(2d, uiIconImage.get().getCol());
        assertEquals(2d, uiIconImage.get().getRow());
        assertEquals(hu.blackbelt.judo.meta.ui.Fit.LOOSE, uiIconImage.get().getFit());
        assertEquals(hu.blackbelt.judo.meta.ui.Stretch.NONE, uiIconImage.get().getStretch());
        
        final Optional<Spacer> uiSpacer = flexFromView.getChildren().stream().filter(c -> c instanceof Spacer)
        		.map(f -> (Spacer) f).filter(f -> f.getName().equals(placeholder.getName())).findAny();
        assertTrue(uiSpacer.isPresent());
        assertEquals(4d, uiSpacer.get().getCol());
        assertEquals(2d, uiSpacer.get().getRow());
        assertEquals(hu.blackbelt.judo.meta.ui.Fit.TIGHT, uiSpacer.get().getFit());
        assertEquals(hu.blackbelt.judo.meta.ui.Stretch.BOTH, uiSpacer.get().getStretch());
        
        final Optional<Text> uiText = flexFromView.getChildren().stream().filter(c -> c instanceof Text)
        		.map(f -> (Text) f).filter(f -> f.getName().equals(textField.getName())).findAny();
        assertTrue(uiText.isPresent());
        assertEquals(3d, uiText.get().getCol());
        assertEquals(2d, uiText.get().getRow());
        assertEquals(hu.blackbelt.judo.meta.ui.Fit.LOOSE, uiText.get().getFit());
        assertEquals(hu.blackbelt.judo.meta.ui.Stretch.HORIZONTAL, uiText.get().getStretch());
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
