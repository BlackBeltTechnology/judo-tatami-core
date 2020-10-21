package hu.blackbelt.judo.tatami.esm2ui;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.ui.Application;
import hu.blackbelt.judo.meta.ui.Tab;
import hu.blackbelt.judo.meta.ui.TabController;
import hu.blackbelt.judo.meta.ui.VisualElement;
import hu.blackbelt.judo.meta.ui.runtime.UiModel;
import hu.blackbelt.judo.meta.ui.runtime.UiUtils;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static hu.blackbelt.judo.meta.esm.runtime.EsmEpsilonValidator.calculateEsmValidationScriptURI;
import static hu.blackbelt.judo.meta.esm.runtime.EsmEpsilonValidator.validateEsm;
import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.buildEsmModel;

import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.SaveArguments.esmSaveArgumentsBuilder;

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

    private void transformViewForPreview() throws Exception {
    	validateEsmTestModel();
    	log.info(executeEsm2UiTransformation(esmModel, SimpleOrderModel.getViewForTest(), "default", 12, uiModel, new Slf4jLog(log)));
        validateUiTestModel();
    }
    
    private void transformFormForPreview() throws Exception {
    	validateEsmTestModel();
    	log.info(executeEsm2UiTransformation(esmModel, SimpleOrderModel.getFormForTest(), "default", 12, uiModel, new Slf4jLog(log)));
        validateUiTestModel();
    }
    
    private void transformTableForPreview() throws Exception {
    	validateEsmTestModel();
    	log.info(executeEsm2UiTransformation(esmModel, SimpleOrderModel.getTableForTest(), "default", 12, uiModel, new Slf4jLog(log)));
        validateUiTestModel();
    }
    
    @Test
    void testTransformViewForPreview() throws Exception {
        testName = "ViewForPreview";

        esmModel.addContent(SimpleOrderModel.createSimpleOrderModel());

        transformViewForPreview();

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

        transformFormForPreview();

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

        transformTableForPreview();

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
}
