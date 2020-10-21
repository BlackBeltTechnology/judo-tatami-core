package hu.blackbelt.judo.tatami.esm2ui;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.ui.Application;
import hu.blackbelt.judo.meta.ui.Container;
import hu.blackbelt.judo.meta.ui.NavigationController;
import hu.blackbelt.judo.meta.ui.Tab;
import hu.blackbelt.judo.meta.ui.TabController;
import hu.blackbelt.judo.meta.ui.VisualElement;
import hu.blackbelt.judo.meta.ui.runtime.UiModel;
import hu.blackbelt.judo.meta.ui.runtime.UiUtils;
import hu.blackbelt.model.northwind.esm.NorthwindEsmModel;
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

import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.SaveArguments.esmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.buildEsmModel;
import static hu.blackbelt.judo.meta.ui.runtime.UiEpsilonValidator.calculateUiValidationScriptURI;
import static hu.blackbelt.judo.meta.ui.runtime.UiEpsilonValidator.validateUi;
import static hu.blackbelt.judo.meta.ui.runtime.UiModel.SaveArguments.uiSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.ui.runtime.UiModel.buildUiModel;
import static hu.blackbelt.judo.tatami.esm2ui.Esm2Ui.calculateEsm2UiTransformationScriptURI;
import static hu.blackbelt.judo.tatami.esm2ui.Esm2Ui.executeEsm2UiTransformation;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class Northwind2UiApplicationTest {

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
        slf4jlog = new Slf4jLog(log);
        esmModel = buildEsmModel().uri(URI.createURI(TEST_SOURCE_MODEL_NAME)).name(TEST).build();
        uiModel = buildUiModel().name(TEST).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        final String traceFileName = testName + "-esm2ui.model";
        esm2UiTransformationTrace.save(new File(TARGET_TEST_CLASSES, traceFileName));
        uiModel.saveUiModel(uiSaveArgumentsBuilder().file(new File(TARGET_TEST_CLASSES, testName + "-ui.model")));
        esmModel.saveEsmModel(esmSaveArgumentsBuilder().file(new File(TARGET_TEST_CLASSES, testName + "-esm.model")));
    }

    private void transform() throws Exception {
    	log.info(esmModel.getDiagnosticsAsString());
    	assertTrue(esmModel.isValid());
		// Make transformation which returns the trace with the serialized URI's
        esm2UiTransformationTrace = executeEsm2UiTransformation(esmModel, "default", 12, uiModel, new Slf4jLog(log),
                calculateEsm2UiTransformationScriptURI());

        log.info(uiModel.getDiagnosticsAsString());
        assertTrue(uiModel.isValid());
        validateUi(new Slf4jLog(log), uiModel, calculateUiValidationScriptURI());
    }

    
    @Test
    void testCreateApplication() throws Exception {
        testName = "Northwind";

        esmModel = NorthwindEsmModel.fullDemo();

        transform();

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

        final Optional<NavigationController> navigationController = allUi(NavigationController.class)
                .findAny();
        assertTrue(navigationController.isPresent());

        assertTrue(navigationController.get() == application.get().getNavigationController());
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
          int sumCol = UiUtils.getSumOfCols((Container) element);
          int maxRow = UiUtils.getMaxOfRows((Container) element);
          sb.append(maxRow + " rows in " + element.getRow());
          if (maxRow <= element.getRow()) sb.append(" ***FITS*** //////// ");
          else sb.append(" ===WRONG=== //////// ");
          sb.append(sumCol + " cols in " + element.getCol());
          if (sumCol <= element.getCol()) sb.append(" ***FITS*** ");
          else sb.append(" ===WRONG=== ");
        }
        else if(UiUtils.isVerticalFlex(element)) {
          sb.append("|||VERTICAL||| ");
          int sumRow = UiUtils.getSumOfRows((Container) element);
          int maxCol = UiUtils.getMaxOfCols((Container) element);

          sb.append(sumRow + " rows in " + element.getRow());
          if (sumRow <= element.getRow()) sb.append(" ***FITS*** //////// ");
          else sb.append(" ===WRONG=== //////// ");
          
          sb.append(maxCol + " cols in " + element.getCol());
          if (maxCol <= element.getCol()) sb.append(" ***FITS*** ");
          else sb.append(" ===WRONG=== ");
        } else {
          sb.append(" (" + element.getRow() + "*" + element.getCol() + ")");
        }
        
        if (element instanceof Container) {
          sb.append("\n");
          for (VisualElement c : ((Container) element).getChildren()) sb.append(print(c,level+1,false));
        } else if (element instanceof TabController) {
        	sb.append("\n");
            for (Tab c : ((TabController) element).getTabs()) sb.append(print(c.getElement(),level+1,true));
        } else {
          sb.append("\n");
        }
        return sb.toString();
    }

}
