package hu.blackbelt.judo.tatami.esm2ui;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.accesspoint.Access;
import hu.blackbelt.judo.meta.esm.accesspoint.ActorType;
import hu.blackbelt.judo.meta.esm.namespace.Model;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.esm.runtime.EsmUtils;
import hu.blackbelt.judo.meta.esm.structure.DataMember;
import hu.blackbelt.judo.meta.esm.structure.EntityType;
import hu.blackbelt.judo.meta.esm.structure.MemberType;
import hu.blackbelt.judo.meta.esm.structure.TransferObjectType;
import hu.blackbelt.judo.meta.esm.type.StringType;
import hu.blackbelt.judo.meta.esm.ui.*;
import hu.blackbelt.judo.meta.ui.Application;
import hu.blackbelt.judo.meta.ui.Tab;
import hu.blackbelt.judo.meta.ui.TabController;
import hu.blackbelt.judo.meta.ui.VisualElement;
import hu.blackbelt.judo.meta.ui.runtime.UiModel;
import hu.blackbelt.judo.meta.ui.runtime.UiUtils;
import hu.blackbelt.model.northwind.esm.NorthwindEsmModel;
import lombok.extern.slf4j.Slf4j;

import org.eclipse.emf.common.notify.Notifier;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.Writer;
import java.util.*;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static hu.blackbelt.judo.meta.esm.accesspoint.util.builder.AccesspointBuilders.newAccessBuilder;
import static hu.blackbelt.judo.meta.esm.accesspoint.util.builder.AccesspointBuilders.newActorTypeBuilder;
import static hu.blackbelt.judo.meta.esm.namespace.util.builder.NamespaceBuilders.newModelBuilder;
import static hu.blackbelt.judo.meta.esm.runtime.EsmEpsilonValidator.calculateEsmValidationScriptURI;
import static hu.blackbelt.judo.meta.esm.runtime.EsmEpsilonValidator.validateEsm;
import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.buildEsmModel;

import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.SaveArguments.esmSaveArgumentsBuilder;

import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.*;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newStringTypeBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.*;
import static hu.blackbelt.judo.meta.ui.runtime.UiEpsilonValidator.calculateUiValidationScriptURI;
import static hu.blackbelt.judo.meta.ui.runtime.UiEpsilonValidator.validateUi;
import static hu.blackbelt.judo.meta.ui.runtime.UiModel.SaveArguments.uiSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.ui.runtime.UiModel.buildUiModel;
import static hu.blackbelt.judo.tatami.esm2ui.Esm2UiPreview.executeEsm2UiTransformation;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class Esm2UiPreviewTest {
	private final String TEST_SOURCE_MODEL_NAME = "urn:test.judo-meta-esm";
    private final String TEST = "test";
    private final String TARGET_TEST_CLASSES = "target/test-classes";

    Log slf4jlog;
    EsmModel esmModel;

    String testName;
    Map<EObject, List<EObject>> resolvedTrace;
    UiModel uiModel;

    @BeforeEach
    void setUp() throws Exception {
        slf4jlog = new Slf4jLog(log);
        esmModel = buildEsmModel().uri(URI.createURI(TEST_SOURCE_MODEL_NAME)).name(TEST).build();
        uiModel = buildUiModel().name(TEST).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        uiModel.saveUiModel(uiSaveArgumentsBuilder().file(new File(TARGET_TEST_CLASSES, testName + "-ui.model")));
        esmModel.saveEsmModel(esmSaveArgumentsBuilder().file(new File(TARGET_TEST_CLASSES, testName + "-esm.model")));
    }
    
    private void validateEsmTestModel() throws Exception {
    	log.info(esmModel.getDiagnosticsAsString());
    	assertTrue(esmModel.isValid());
    	validateEsm(new Slf4jLog(log), esmModel, calculateEsmValidationScriptURI());
    }
    
    private void validateUiTestModel() throws Exception {
    	log.info(uiModel.getDiagnosticsAsString());
        assertTrue(uiModel.isValid());
        validateUi(new Slf4jLog(log), uiModel, calculateUiValidationScriptURI());
    }

    @Test
    void testTransformViewForPreview() throws Exception {
        testName = "ViewForPreview";

        esmModel.addContent(SimpleOrderModel.createSimpleOrderModel());

        validateEsmTestModel();
    	String json = executeEsm2UiTransformation(esmModel, SimpleOrderModel.getViewForTest(), "default", 12, uiModel, new Slf4jLog(log));
    	saveJson(json, SimpleOrderModel.getViewForTest().getName());
    	savePrettyJson(json, SimpleOrderModel.getViewForTest().getName());
        validateUiTestModel();

        final Optional<Application> application = allUi(Application.class)
                .findAny();
        assertTrue(application.isPresent());

        application.get().getPages().stream()
        	.forEach(p -> 
        		p.getContainers().stream()
        			.forEach(c -> 
        				slf4jlog.debug("Printing UI model page " + testName + "::" + p.getName() + ": \n" + printElement(c))
        			)
        	);
    }
    
    @Test
    void testTransformFormForPreview() throws Exception {
        testName = "FormForPreview";

        esmModel.addContent(SimpleOrderModel.createSimpleOrderModel());

        validateEsmTestModel();
    	String json = executeEsm2UiTransformation(esmModel, SimpleOrderModel.getFormForTest(), "default", 12, uiModel, new Slf4jLog(log));
    	saveJson(json, SimpleOrderModel.getFormForTest().getName());
    	savePrettyJson(json, SimpleOrderModel.getFormForTest().getName());
        validateUiTestModel();

        final Optional<Application> application = allUi(Application.class)
                .findAny();
        assertTrue(application.isPresent());

        application.get().getPages().stream()
        	.forEach(p -> 
        		p.getContainers().stream()
        			.forEach(c -> 
        				slf4jlog.debug("Printing UI model page " + testName + "::" + p.getName() + ": \n" + printElement(c))
        			)
        	);
    }
    
    @Test
    void testTransformTableForPreview() throws Exception {
        testName = "TableForPreview";

        esmModel.addContent(SimpleOrderModel.createSimpleOrderModel());

        validateEsmTestModel();
    	String json = executeEsm2UiTransformation(esmModel, SimpleOrderModel.getTableForTest(), "default", 12, uiModel, new Slf4jLog(log));
    	saveJson(json, SimpleOrderModel.getTableForTest().getName());
    	savePrettyJson(json, SimpleOrderModel.getTableForTest().getName());
        validateUiTestModel();

        final Optional<Application> application = allUi(Application.class)
                .findAny();
        assertTrue(application.isPresent());

        application.get().getPages().stream()
        	.forEach(p -> 
        		p.getContainers().stream()
        			.forEach(c -> 
        				slf4jlog.debug("Printing UI model page " + testName + "::" + p.getName() + ": \n" + printElement(c))
        			)
        	);
    }
    
    @Test
    void testNorthwindViewPreview() throws Exception {
        testName = "NorthwindViewPreview";

        esmModel = NorthwindEsmModel.fullDemo();

        validateEsmTestModel();
        
        final ResourceSet resourceSet = esmModel.getResourceSet();
        final Iterable<Notifier> esmContents = resourceSet::getAllContents;

        Optional<TransferObjectView> view = StreamSupport.stream(esmContents.spliterator(), true)
        		.filter(e -> (TransferObjectView.class).isAssignableFrom(e.getClass()))
                .map(e -> (TransferObjectView) e).filter(v -> v.getName().equals("InternationalOrderInfoView")).findAny();
        
    	String json = executeEsm2UiTransformation(esmModel, view.get(), "default", 12, uiModel, new Slf4jLog(log));
    	saveJson(json, testName);
    	savePrettyJson(json, testName);
        validateUiTestModel();
    }
    
    @Test
    void testNorthwindFormPreview() throws Exception {
        testName = "NorthwindFormPreview";

        esmModel = NorthwindEsmModel.fullDemo();

        validateEsmTestModel();
        
        final ResourceSet resourceSet = esmModel.getResourceSet();
        final Iterable<Notifier> esmContents = resourceSet::getAllContents;

        Optional<TransferObjectForm> form = StreamSupport.stream(esmContents.spliterator(), true)
        		.filter(e -> (TransferObjectForm.class).isAssignableFrom(e.getClass()))
                .map(e -> (TransferObjectForm) e).filter(v -> v.getName().equals("InternationalOrderInfoForm")).findAny();
        
    	String json = executeEsm2UiTransformation(esmModel, form.get(), "default", 12, uiModel, new Slf4jLog(log));
    	saveJson(json, testName);
    	savePrettyJson(json, testName);
        validateUiTestModel();
    }
    
    @Test
    void testNorthwindTablePreview() throws Exception {
        testName = "NorthwindTablePreview";

        esmModel = NorthwindEsmModel.fullDemo();

        validateEsmTestModel();
        
        final ResourceSet resourceSet = esmModel.getResourceSet();
        final Iterable<Notifier> esmContents = resourceSet::getAllContents;
        
        Optional<TransferObjectTable> table = StreamSupport.stream(esmContents.spliterator(), true)
        		.filter(e -> (TransferObjectTable.class).isAssignableFrom(e.getClass()))
                .map(e -> (TransferObjectTable) e).filter(v -> v.getName().equals("InternationalOrderInfoTable")).findAny();
        
    	String json = executeEsm2UiTransformation(esmModel, table.get(), "default", 12, uiModel, new Slf4jLog(log));
    	saveJson(json, testName);
    	savePrettyJson(json, testName);
        validateUiTestModel();
    }

    @Test
    void testInstanceActionButtons() throws Exception {
        testName = "instanceActionButtons";

        StringType stringType = newStringTypeBuilder().withName("String").withMaxLength(256).build();

        DataMember orderCustomer = newDataMemberBuilder()
                .withName("customer")
                .withMemberType(MemberType.STORED)
                .withDataType(stringType)
                .withRequired(true)
                .build();
        orderCustomer.setBinding(orderCustomer);

        EntityType order = newEntityTypeBuilder()
                .withName("Order")
                .withAttributes(orderCustomer)
                .withCreateable(true)
                .withUpdateable(true)
                .withDeleteable(true)
                .build();
        order.setMapping(newMappingBuilder().withTarget(order).build());

        TransferObjectView view = newTransferObjectViewBuilder()
                .withName(order.getName() + "View")
                .withLabel(order.getName() + "View")
                .withLayout(Layout.HORIZONTAL)
                .withWrap(true)
                .withHorizontal(Horizontal.LEFT)
                .withVertical(Vertical.TOP)
                .withFrame(true)
                .withComponents(newDataFieldBuilder()
                        .withName(orderCustomer.getName())
                        .withLabel(orderCustomer.getName().toUpperCase())
                        .withIconName("text_fields")
                        .withDataFeature(orderCustomer)
                        .withBasis(2)
                        .build())
                .build();

        useTransferObjectType(order).withView(view).build();

        Access access = newAccessBuilder()
                .withName("orders")
                .withLower(0)
                .withUpper(-1)
                .withTarget(order)
                .withTargetDefinedCRUD(true)
                .build();

        ActorType actor = newActorTypeBuilder()
                .withName("actor")
                .withAccesses(access)
                .withManaged(false)
                .withMenuItems(newMenuItemAccessBuilder().withName("order").withAccess(access).build())
                .build();

        // Create model
        Model model = newModelBuilder()
                .withName("SimpleOrder")
                .withElements(stringType)
                .withElements(order)
                .withElements(actor)
                .build();
        esmModel.addContent(model);

        validateEsmTestModel();

        final ResourceSet resourceSet = esmModel.getResourceSet();
        final Iterable<Notifier> esmContents = resourceSet::getAllContents;

        String json = executeEsm2UiTransformation(esmModel, view, "default", 12, uiModel, new Slf4jLog(log));
        saveJson(json, testName);
        savePrettyJson(json, testName);
        validateUiTestModel();
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

    private static String printElement(VisualElement element) {
        StringBuilder sb = new StringBuilder();
        sb.append(print(element,0,false));
        return sb.toString();
    }
      
    private static String print(VisualElement element, int level, boolean isTab) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < level; ++i) sb.append("  ");
        if (isTab) sb.append("TAB: ");
        sb.append(element.getName());
        
        if(UiUtils.isHorizontalFlex(element)) {
          sb.append("---HORIZONTAL---");
          int sumCol = UiUtils.getSumOfCols((hu.blackbelt.judo.meta.ui.Container) element);
          int maxRow = UiUtils.getMaxOfRows((hu.blackbelt.judo.meta.ui.Container) element);
          sb.append(maxRow + " rows in " + element.getRow());
          if (maxRow <= element.getRow()) sb.append(" ***FITS*** //////// ");
          else sb.append(" ===WRONG=== //////// ");
          sb.append(sumCol + " cols in " + element.getCol());
          if (sumCol <= element.getCol()) sb.append(" ***FITS*** ");
          else sb.append(" ===WRONG=== ");
        }
        else if(UiUtils.isVerticalFlex(element)) {
          sb.append("|||VERTICAL||| ");
          int sumRow = UiUtils.getSumOfRows((hu.blackbelt.judo.meta.ui.Container) element);
          int maxCol = UiUtils.getMaxOfCols((hu.blackbelt.judo.meta.ui.Container) element);

          sb.append(sumRow + " rows in " + element.getRow());
          if (sumRow <= element.getRow()) sb.append(" ***FITS*** //////// ");
          else sb.append(" ===WRONG=== //////// ");
          
          sb.append(maxCol + " cols in " + element.getCol());
          if (maxCol <= element.getCol()) sb.append(" ***FITS*** ");
          else sb.append(" ===WRONG=== ");
        } else {
          sb.append(" (" + element.getRow() + "*" + element.getCol() + ")");
        }
        
        if (element instanceof hu.blackbelt.judo.meta.ui.Container) {
          sb.append("\n");
          for (VisualElement c : ((hu.blackbelt.judo.meta.ui.Container) element).getChildren()) sb.append(print(c,level+1,false));
        } else if (element instanceof TabController) {
        	sb.append("\n");
            for (Tab c : ((TabController) element).getTabs()) sb.append(print(c.getElement(),level+1,true));
        } else {
          sb.append("\n");
        }
        return sb.toString();
    }
    
    private void saveJson(String json, String name) {
    	 final File jsonFile = new File(TARGET_TEST_CLASSES, name + ".json");
         try (final Writer targetFileWriter = new FileWriter(jsonFile)) {
             targetFileWriter.append(json);
         } catch (IOException ex) {
             log.error("Unable to create JSON output", ex);
         }
    }
    
    private void savePrettyJson(String json, String name) {
    	
	   	 final File jsonFile = new File(TARGET_TEST_CLASSES, name + "Pretty" + ".json");
	     try (final Writer targetFileWriter = new FileWriter(jsonFile)) {
	   	 
	   	 	ObjectMapper mapper = new ObjectMapper();
			Object jsonObject = mapper.readValue(json, Object.class);
	    	String pretty = mapper.writerWithDefaultPrettyPrinter().writeValueAsString(jsonObject);
	        targetFileWriter.append(pretty);
	        log.info("JSON of {}:\n{}",name,pretty);
	        
	     } catch (IOException ex) {
	        log.error("Unable to create JSON output", ex);
	     }
    }
}
