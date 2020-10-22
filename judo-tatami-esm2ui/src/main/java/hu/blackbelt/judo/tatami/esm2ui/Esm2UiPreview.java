package hu.blackbelt.judo.tatami.esm2ui;

import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.esm.runtime.EsmUtils;
import hu.blackbelt.judo.meta.esm.ui.VisualElement;
import hu.blackbelt.judo.meta.ui.runtime.UiUtils;
import hu.blackbelt.judo.meta.ui.PageDefinition;
import hu.blackbelt.judo.meta.ui.runtime.UiModel;
import hu.blackbelt.judo.tatami.core.TransformationTraceUtil;
import lombok.extern.slf4j.Slf4j;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.eclipse.epsilon.common.util.UriUtil;
import org.emfjson.jackson.module.EMFModule;
import org.emfjson.jackson.resource.JsonResourceFactory;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Set;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;

@Slf4j
public class Esm2UiPreview {

    public static final String SCRIPT_ROOT_TATAMI_ESM_2_UI_PREVIEW = "tatami/esm2ui/transformations/preview/";

    /**
     * Execute ESM to UI model transformation to preview one visual element
     *
     * @param esmModel  The ESM model definition and loaded resources
     * @param previewElement The element of which the preview shall be generated
     * @param uiModel  The UI model definition transformed to
     * @return The JSON String representation of the Page Definition as a result of the transformation of the preview element
     * @throws Exception
     */
    public static String executeEsm2UiTransformation(EsmModel esmModel, VisualElement previewElement, String applicationType, Integer applicationColumns, UiModel uiModel) throws Exception {
    	return executeEsm2UiTransformation(esmModel, previewElement, applicationType, applicationColumns, uiModel, new Slf4jLog(log), calculateEsm2UiPreviewTransformationScriptURI());
    }

    /**
     * Execute ESM to UI model transformation to preview one visual element
     *
     * @param esmModel  The ESM model definition and loaded resources
     * @param previewElement The element of which the preview shall be generated
     * @param uiModel  The UI model definition transformed to
     * @param log       The log instance used in scripts
     * @return The JSON String representation of the Page Definition as a result of the transformation of the preview element
     * @throws Exception
     */
    public static String executeEsm2UiTransformation(EsmModel esmModel, VisualElement previewElement, String applicationType, Integer applicationColumns, UiModel uiModel, Log log) throws Exception {
    	return executeEsm2UiTransformation(esmModel, previewElement, applicationType, applicationColumns, uiModel, log, calculateEsm2UiPreviewTransformationScriptURI());
    }

    /**
     * Execute ESM to UI model transformation to preview one visual element
     *
     * @param esmModel  The ESM model definition and loaded resources
     * @param previewElement The element of which the preview shall be generated
     * @param uiModel  The UI model definition transformed to
     * @param scriptDir The physical filesystem directory where the script root is
     * @return The JSON String representation of the Page Definition as a result of the transformation of the preview element
     * @throws Exception
     */
    public static String executeEsm2UiTransformation(EsmModel esmModel, VisualElement previewElement, String applicationType, Integer applicationColumns, UiModel uiModel, URI scriptDir) throws Exception {
    	return executeEsm2UiTransformation(esmModel, previewElement, applicationType, applicationColumns, uiModel, new Slf4jLog(log), scriptDir);
    }

    /**
     * Execute ESM to UI model transformation to preview one visual element
     *
     * @param esmModel  The ESM model definition and loaded resources
     * @param uiModel  The UI model definition transformed to
     * @param log       The log instance used in scripts
     * @param scriptDir The physical filesystem directory where the script root is
     * @return The JSON String representation of the Page Definition as a result of the transformation of the preview element
     * @throws Exception
     */
    public static String executeEsm2UiTransformation(EsmModel esmModel, VisualElement previewElement, String applicationType, Integer applicationColumns, UiModel uiModel, Log log,
                                                                          URI scriptDir) throws Exception {

    	Set<PageDefinition> previewPage = new HashSet<>();
    	
    	// Execution context
        ExecutionContext executionContext = executionContextBuilder()
                .log(log)
                .modelContexts(ImmutableList.of(
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("ESM")
                                .resource(esmModel.getResource())
                                .validateModel(false)
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("UI")
                                .validateModel(false)
                                .resource(uiModel.getResource())
                                .build()))
                .injectContexts(ImmutableMap.<String, Object>builder()
                		.put("esmUtils", new EsmUtils())
                		.put("uiUtils", new UiUtils())
                		.put("applicationType", applicationType)
                		.put("applicationColumns", applicationColumns)
                		.put("previewElement", previewElement)
                		.put("previewPage", previewPage).build())
                .build();

        // run the model / metadata loadingexecutionContext.
        executionContext.load();

        EtlExecutionContext etlExecutionContext = etlExecutionContextBuilder()
                .source(UriUtil.resolve("esmToUiPreview.etl", scriptDir))
                .build();

        // Transformation script
        executionContext.executeProgram(etlExecutionContext);
        executionContext.commit();
        executionContext.close();

        if (previewPage.size() != 1) {
        	throw new IllegalStateException("One and only one page definition should be created for preview.");
        }
        
        return exportPageDefinitionToJson(previewPage.iterator().next());
    }

    public static URI calculateEsm2UiPreviewTransformationScriptURI() throws URISyntaxException {
        URI uiRoot = Esm2UiPreview.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        if (uiRoot.toString().endsWith(".jar")) {
            uiRoot = new URI("jar:" + uiRoot.toString() + "!/" + SCRIPT_ROOT_TATAMI_ESM_2_UI_PREVIEW);
        } else if (uiRoot.toString().startsWith("jar:bundle:")) {
            uiRoot = new URI(uiRoot.toString().substring(4, uiRoot.toString().indexOf("!")) + SCRIPT_ROOT_TATAMI_ESM_2_UI_PREVIEW);
        } else {
            uiRoot = new URI(uiRoot.toString() + "/" + SCRIPT_ROOT_TATAMI_ESM_2_UI_PREVIEW);
        }
        return uiRoot;
    }
    
    /**
     * Create a JSON String representation of a given Page Definition
     *
     * @param page  The Page Definition to be transformed to a JSON String
     * @return The JSON String representation of the Page Definition
     * @throws JsonProcessingException
     */
	public static String exportPageDefinitionToJson(hu.blackbelt.judo.meta.ui.PageDefinition page) throws JsonProcessingException {
		ResourceSet resourceSet = new ResourceSetImpl();
		resourceSet.getResourceFactoryRegistry()
						.getExtensionToFactoryMap()
						.put("json", new JsonResourceFactory());
		
		Resource resource = resourceSet.createResource
				  (org.eclipse.emf.common.util.URI.createFileURI("preview.json"));
		
		resource.getContents().add(page);

		ObjectMapper mapper = new ObjectMapper();
		EMFModule module = new EMFModule();
		module.setReferenceSerializer(new JsonSerializer<EObject>() {
			  @Override
			  public void serialize(EObject v, JsonGenerator g, SerializerProvider s)
			  throws IOException {
				  mapper.writeValue(g, v);
			  }
			});
		mapper.registerModule(module);
		
		return mapper.writeValueAsString(resource);
	}
}
