package hu.blackbelt.judo.tatami.esm2ui;

import static hu.blackbelt.judo.meta.esm.accesspoint.util.builder.AccesspointBuilders.newAccessBuilder;
import static hu.blackbelt.judo.meta.esm.accesspoint.util.builder.AccesspointBuilders.newActorTypeBuilder;
import static hu.blackbelt.judo.meta.esm.accesspoint.util.builder.AccesspointBuilders.newClaimBuilder;
import static hu.blackbelt.judo.meta.esm.accesspoint.util.builder.AccesspointBuilders.useActorType;
import static hu.blackbelt.judo.meta.esm.namespace.util.builder.NamespaceBuilders.newAnnotationBuilder;
import static hu.blackbelt.judo.meta.esm.namespace.util.builder.NamespaceBuilders.newModelBuilder;
import static hu.blackbelt.judo.meta.esm.runtime.EsmEpsilonValidator.calculateEsmValidationScriptURI;
import static hu.blackbelt.judo.meta.esm.runtime.EsmEpsilonValidator.validateEsm;
import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.buildEsmModel;
import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.SaveArguments.esmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.newDataMemberBuilder;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.newEntityTypeBuilder;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.newMappingBuilder;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.newTransferObjectTypeBuilder;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.useTransferObjectType;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newStringTypeBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newDataColumnBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newDataFieldBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newGroupBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newMenuItemAccessBuilder;
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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import hu.blackbelt.judo.meta.ui.data.RelationType;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.accesspoint.Access;
import hu.blackbelt.judo.meta.esm.accesspoint.ActorKind;
import hu.blackbelt.judo.meta.esm.accesspoint.ActorType;
import hu.blackbelt.judo.meta.esm.accesspoint.ClaimType;
import hu.blackbelt.judo.meta.esm.namespace.Annotation;
import hu.blackbelt.judo.meta.esm.namespace.Model;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.esm.runtime.EsmUtils;
import hu.blackbelt.judo.meta.esm.structure.DataMember;
import hu.blackbelt.judo.meta.esm.structure.EntityType;
import hu.blackbelt.judo.meta.esm.structure.MemberType;
import hu.blackbelt.judo.meta.esm.structure.TransferObjectType;
import hu.blackbelt.judo.meta.esm.type.StringType;
import hu.blackbelt.judo.meta.esm.ui.Component;
import hu.blackbelt.judo.meta.esm.ui.Layout;
import hu.blackbelt.judo.meta.esm.ui.MenuItemAccess;
import hu.blackbelt.judo.meta.esm.ui.TransferObjectForm;
import hu.blackbelt.judo.meta.esm.ui.TransferObjectTable;
import hu.blackbelt.judo.meta.esm.ui.TransferObjectView;
import hu.blackbelt.judo.meta.ui.Application;
import hu.blackbelt.judo.meta.ui.Flex;
import hu.blackbelt.judo.meta.ui.NavigationController;
import hu.blackbelt.judo.meta.ui.PageDefinition;
import hu.blackbelt.judo.meta.ui.data.ClassType;
import hu.blackbelt.judo.meta.ui.runtime.UiModel;
import lombok.extern.slf4j.Slf4j;

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
        log.info(esmModel.getDiagnosticsAsString());
        assertTrue(esmModel.isValid());
        validateEsm(new Slf4jLog(log), esmModel, calculateEsmValidationScriptURI());
        // Make transformation which returns the trace with the serialized URI's
        esm2UiTransformationTrace = executeEsm2UiTransformation(esmModel, "default", 12, false, uiModel, new Slf4jLog(log),
                calculateEsm2UiTransformationScriptURI());

        log.info(uiModel.getDiagnosticsAsString());
        assertTrue(uiModel.isValid());
        validateUi(new Slf4jLog(log), uiModel, calculateUiValidationScriptURI());
    }

    
    @Test
    void testCreateApplication() throws Exception {
        testName = "CreateApplication";

        final String MODEL_NAME = "Model";
        final String ACCESS_POINT_NAME = "AP";

        final TransferObjectType accessPoint = newTransferObjectTypeBuilder()
                .withName(ACCESS_POINT_NAME)
                .build();
        
        ActorType actor = newActorTypeBuilder()
                .withName("actor")
                .withPrincipal(accessPoint)
                .withRealm("sandbox")
                .build();
        useTransferObjectType(accessPoint).withActorType(actor).build();

        final Model model = newModelBuilder().withName(MODEL_NAME)
                .withElements(Arrays.asList(accessPoint, actor)).build();

        esmModel.addContent(model);

        transform();

        final Optional<Application> application = allUi(Application.class)
                .findAny();
        assertTrue(application.isPresent());

        final Optional<NavigationController> navigationController = allUi(NavigationController.class)
                .findAny();
        assertTrue(navigationController.isPresent());
        assertEquals(navigationController.get(), application.get().getNavigationController());
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

        ActorType actor = newActorTypeBuilder()
                .withName("actor")
                .withRealm("sandbox")
                .withKind(ActorKind.HUMAN)
                .build();

        // Exposed entity
        final String EXPOSED_ENTITY_TYPE_NAME = "ExposedEntity";

        DataMember attribute = newDataMemberBuilder()
                .withName("email")
                .withMemberType(MemberType.STORED)
                .withDataType(string)
                .withRequired(true)
                .withIdentifier(true)
                .build();
        attribute.setBinding(attribute);

        final EntityType exposedEntity = newEntityTypeBuilder()
                .withName(EXPOSED_ENTITY_TYPE_NAME)
                .withAttributes(attribute)
                .build();
        exposedEntity.setMapping(newMappingBuilder().withTarget(exposedEntity).build());
        
        DataMember attribute2 = newDataMemberBuilder()
                .withName("email")
                .withMemberType(MemberType.STORED)
                .withDataType(string)
                .build();
        attribute2.setBinding(attribute2);
        
        final EntityType exposedEntity2 = newEntityTypeBuilder()
                .withName(EXPOSED_ENTITY_TYPE_NAME + "2")
                .withAttributes(attribute2)
                .withTable(newTransferObjectTableBuilder().withName("table").withColumns(
                        newDataColumnBuilder().withName("col").withDataFeature(attribute2).build()
                        ).build())
                .withView(newTransferObjectViewBuilder().withName("view")
                        .withComponents(newDataFieldBuilder().withName("data").withDataFeature(attribute2).build())
                        .build())
                .withForm(newTransferObjectFormBuilder().withName("form")
                        .withComponents(newDataFieldBuilder().withName("data").withDataFeature(attribute2).build())
                        .build())
                .build();
        exposedEntity2.setMapping(newMappingBuilder().withTarget(exposedEntity2).build());

        final Annotation dashboardAnnotation = newAnnotationBuilder().withClassName("hu.blackbelt.judo.meta.esm.accesspoint.Access")
                .withName("dashboard").build();
        
        // Create multiple reference relation to mapped entity
        final String EXPOSED_GRAPH_MULTIPLE_NAME = "ExposedGraphMultiple";
        final Access exposedRelationMultiple = newAccessBuilder()
                .withName(EXPOSED_GRAPH_MULTIPLE_NAME)
                .withTarget(exposedEntity)
                .withGetterExpression(MODEL_NAME + EsmUtils.NAMESPACE_SEPARATOR + EXPOSED_ENTITY_TYPE_NAME)
                .withLower(0)
                .withUpper(-1)
                .withCreateable(true).withUpdateable(true).withDeleteable(true)
                .build();
        useActorType(actor).withAccesses(exposedRelationMultiple).build();

        // Create single reference relation to mapped entity
        final String EXPOSED_GRAPH_SINGLE_NAME = "ExposedGraphSingle";
        final Access exposedRelationSingle = newAccessBuilder()
                .withName(EXPOSED_GRAPH_SINGLE_NAME)
                .withTarget(exposedEntity)
                .withGetterExpression(MODEL_NAME + EsmUtils.NAMESPACE_SEPARATOR + EXPOSED_ENTITY_TYPE_NAME)
                .withLower(0)
                .withUpper(1)
                .withAppliedAnnotations(dashboardAnnotation)
                .withCreateable(true).withUpdateable(true).withDeleteable(true)
                .build();
        useActorType(actor).withAccesses(exposedRelationSingle).build();
        
        final Access exposedRelationForEntity2 = newAccessBuilder()
                .withName("entity2s")
                .withTarget(exposedEntity2)
                .withGetterExpression(MODEL_NAME + EsmUtils.NAMESPACE_SEPARATOR + EXPOSED_ENTITY_TYPE_NAME + "2")
                .withLower(0)
                .withUpper(1)
                .withAppliedAnnotations(dashboardAnnotation)
                .withCreateable(true).withUpdateable(false).withDeleteable(false)
                .withTargetDefinedCRUD(false)
                .build();
        useActorType(actor).withAccesses(exposedRelationForEntity2).build();
        
        // Add table representation for exposed relation
        final String EXPOSED_GRAPH_TABLE_NAME = "ExposedGraphTableName";

        final TransferObjectTable exposedEntityTable = newTransferObjectTableBuilder()
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

        final MenuItemAccess menu1 = newMenuItemAccessBuilder().withName("menu1").withAccess(exposedRelationSingle).build();
        final MenuItemAccess menu2 = newMenuItemAccessBuilder().withName("menu2").withAccess(exposedRelationMultiple).build();
        
        useActorType(actor).withMenuItems(menu1, menu2).build();
        
        ActorType actor2 = newActorTypeBuilder()
                .withName("actor2")
                .withRealm("sandbox")
                .withKind(ActorKind.HUMAN)
                .withClaims(newClaimBuilder().withAttribute(attribute).withClaimType(ClaimType.EMAIL).build())
                .build();
        useTransferObjectType(exposedEntity).withActorType(actor2).build();
        
        ActorType actor3 = newActorTypeBuilder()
                .withName("actor3")
                .withKind(ActorKind.HUMAN)
                .build();
        
        final Access exposedRelationMultiple2 = newAccessBuilder()
                .withName(EXPOSED_GRAPH_MULTIPLE_NAME)
                .withTarget(exposedEntity)
                .withGetterExpression(MODEL_NAME + EsmUtils.NAMESPACE_SEPARATOR + EXPOSED_ENTITY_TYPE_NAME)
                .withLower(0)
                .withUpper(-1)
                .withAppliedAnnotations(dashboardAnnotation)
                .withCreateable(true).withUpdateable(true).withDeleteable(true)
                .build();
        useActorType(actor3).withAccesses(exposedRelationMultiple2).build();
        
        final Model model = newModelBuilder().withName(MODEL_NAME)
                .withElements(Arrays.asList(string, exposedEntity, exposedEntity2, actor, actor2, actor3))
                .withAnnotations(dashboardAnnotation)
                .build();

        esmModel.addContent(model);
        
        transform();

        final Optional<Application> application = allUi(Application.class).filter(a -> a.getName().equals(EsmUtils.getNamespaceElementFQName(actor))).findAny();
        assertTrue(application.isPresent());
        final Optional<ClassType> uiActor = application.get().getDataElements().stream()
                .filter(d -> d instanceof  ClassType && d.getName().equals(actor.getFQName())).map(c -> (ClassType)c).findAny();
        assertTrue(uiActor.isPresent());
        final Optional<RelationType> uiRelation = uiActor.get().getRelations().stream()
                .filter(r -> r.getName().equals(EXPOSED_GRAPH_SINGLE_NAME)).findAny();
        assertTrue(uiRelation.isPresent());
        
        final Optional<ClassType> uiEntity = application.get().getDataElements().stream().filter(e -> e instanceof ClassType)
                .map(e -> (ClassType) e).filter(c -> c.getName().equals(EsmUtils.getNamespaceElementFQName(exposedEntity))).findAny();
        assertTrue(uiEntity.isPresent());
        
        final Optional<ClassType> uiEntityForCreateAndUpdate = application.get().getDataElements().stream().filter(e -> e instanceof ClassType)
                .map(e -> (ClassType) e).filter(c -> c.getName().equals(EsmUtils.getNamespaceElementFQName(exposedEntity2))).findAny();
        assertTrue(uiEntityForCreateAndUpdate.isPresent());
        assertTrue(uiEntityForCreateAndUpdate.get().isIsForCreateOrUpdateType());
        
        final Optional<PageDefinition> uiDashboard = application.get().getPages().stream()
                .filter(d -> d.getName().equals(EsmUtils.getNamespaceElementFQName(actor) + "#Dashboard") && d.getIsPageTypeDashboard()).findAny();
        assertTrue(uiDashboard.isPresent());
        assertEquals(uiRelation.get(), uiDashboard.get().getDataElement());
        assertTrue(uiDashboard.get().getContainers().stream().filter(c -> c.getLayoutType().isOriginal()).findFirst().isPresent());
        assertTrue(uiDashboard.get().getContainers().stream().filter(c -> c.getLayoutType().isOriginal()).findFirst().get().getChildren().stream().anyMatch(c -> c instanceof Flex && c.getName().equals(exposedEntity.getView().getName())));

        final Optional<NavigationController> navigationController = allUi(NavigationController.class)
                .findAny();
        assertTrue(navigationController.isPresent());
        assertEquals(navigationController.get(), application.get().getNavigationController());
        assertTrue(navigationController.get().getItems().stream()
                .anyMatch(item -> item.getName().equals(EsmUtils.getNamespaceElementFQName(actor) + "." + exposedRelationSingle.getName() + "#NavigationItem")
                && item.getTarget().getIsPageTypeView()));
        assertTrue(navigationController.get().getItems().stream()
                .anyMatch(item -> item.getName().equals(EsmUtils.getNamespaceElementFQName(actor) + "." + exposedRelationMultiple.getName() + "#NavigationItem")
                && item.getTarget().getIsPageTypeTable()));
        
        final Optional<Application> application2 = allUi(Application.class).filter(a -> a.getName().equals(EsmUtils.getNamespaceElementFQName(actor2))).findAny();
        assertTrue(application.isPresent());

        final Optional<ClassType> uiEntity2 = application2.get().getDataElements().stream().filter(e -> e instanceof ClassType)
                .map(e -> (ClassType) e).filter(c -> c.getName().equals(EsmUtils.getNamespaceElementFQName(exposedEntity))).findAny();
        assertTrue(uiEntity2.isPresent());
        
        final Optional<PageDefinition> uiDashboard2 = application2.get().getPages().stream()
                .filter(d -> d.getName().equals(EsmUtils.getNamespaceElementFQName(actor2) + "#Dashboard") && d.getIsPageTypeDashboard()).findAny();
        assertTrue(uiDashboard2.isPresent());
        assertNull(uiDashboard2.get().getDataElement());
        
                
        final Optional<Application> application3 = allUi(Application.class).filter(a -> a.getName().equals(EsmUtils.getNamespaceElementFQName(actor3))).findAny();
        assertTrue(application3.isPresent());
        final Optional<ClassType> uiActor3 = application3.get().getDataElements().stream()
                .filter(d -> d instanceof ClassType && d.getName().equals(actor3.getFQName())).map(c -> (ClassType)c).findAny();
        assertTrue(uiActor3.isPresent());
        final Optional<RelationType> uiRelation3 = uiActor3.get().getRelations().stream()
                .filter(r -> r.getName().equals(EXPOSED_GRAPH_MULTIPLE_NAME)).findAny();
        assertTrue(uiRelation3.isPresent());
        
        final Optional<PageDefinition> uiDashboard3 = application3.get().getPages().stream()
                .filter(d -> d.getName().equals(EsmUtils.getNamespaceElementFQName(actor3) + "#Dashboard") && d.getIsPageTypeDashboard()).findAny();
        assertTrue(uiDashboard3.isPresent());
        assertEquals(uiRelation3.get(), uiDashboard3.get().getDataElement());
       
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
