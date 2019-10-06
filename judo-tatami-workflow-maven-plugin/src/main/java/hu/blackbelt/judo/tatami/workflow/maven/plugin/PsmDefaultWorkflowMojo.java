package hu.blackbelt.judo.tatami.workflow.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.net.*;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import lombok.Builder;
import lombok.Getter;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel.AsmValidationException;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel.LiquibaseValidationException;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel.MeasureValidationException;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel.OpenapiValidationException;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel.PsmValidationException;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel.RdbmsValidationException;
import hu.blackbelt.judo.tatami.workflow.PsmDefaultWorkflow;
import hu.blackbelt.judo.tatami.workflow.DefaultWorkflowSave;
import hu.blackbelt.judo.tatami.workflow.DefaultWorkflowSetupParameters;

@Mojo(name = "psm-default-workflow",
		defaultPhase = LifecyclePhase.COMPILE,
		requiresDependencyResolution = ResolutionScope.COMPILE)
public class PsmDefaultWorkflowMojo extends AbstractMojo {

	public static final String PSM_2_ASM_TRANSFORMATION_SCRIPT_ROOT = "Psm2Asm-Transformation-ScriptRoot";
	public static final String PSM_2_MEASURE_TRANSFORMATION_SCRIPT_ROOT = "Psm2Measure-Transformation-ScriptRoot";
	public static final String ASM_2_OPEN_API_TRANSFORMATION_SCRIPT_ROOT = "Asm2Openapi-Transformation-ScriptRoot";
	public static final String ASM_2_RDBMS_TRANSFORMATION_SCRIPT_ROOT = "Asm2Rdbms-Transformation-ScriptRoot";
	public static final String ASM_2_RDBMS_TRANSFORMATION_MODEL_ROOT = "Asm2Rdbms-Transformation-ModelRoot";
	public static final String RDBMS_2_LIQUIBASE_TRANSFORMATION_SCRIPT_ROOT = "Rdbms2Liquibase-Transformation-ScriptRoot";
	public static final String ASM_2_SDK_TRANSFORMATION_SCRIPT_ROOT = "Asm2SDK-Transformation-ScriptRoot";
	public static final String ASM_2_JAXRSAPI_TRANSFORMATION_SCRIPT_ROOT = "Asm2Jaxrsapi-Transformation-ScriptRoot";

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject project;

	@Component
	public RepositorySystem repoSystem;

	@Parameter(defaultValue = "${repositorySystemSession}", readonly = true, required = true)
	public RepositorySystemSession repoSession;

	@Parameter(defaultValue = "${project.remoteProjectRepositories}", readonly = true, required = true)
	public List<RemoteRepository> repositories;

	@Parameter( defaultValue = "${plugin}", readonly = true )
	private PluginDescriptor pluginDescriptor;

	@Parameter(property = "psmModelDest")
	private File psmModelDest;

	@Parameter(property = "psm2asmTransformationScriptRoot")
	private File psm2asmTransformationScriptRoot;

	@Parameter(property = "psm2measureTransformationScriptRoot")
	private File psm2measureTransformationScriptRoot;

	@Parameter(property = "asm2openApiTransformationScriptRoot")
	private File asm2openApiTransformationScriptRoot;

	@Parameter(property = "asm2rdbmsTransformationScriptRoot")
	private File asm2rdbmsTransformationScriptRoot;

	@Parameter(property = "asm2rdbmsTransformationModelRoot")
	private File asm2rdbmsTransformationModelRoot;

	@Parameter(property = "rdbms2liquibaseTransformationScriptRoot")
	private File rdbms2liquibaseTransformationScriptRoot;

	@Parameter(property = "asm2sdkTransformationScriptRoot")
	private File asm2sdkTransformationScriptRoot;

	@Parameter(property = "asm2jaxrsapiTransformationScriptRoot")
	private File asm2jaxrsapiTransformationScriptRoot;
	
	
	@Parameter(property = "destination")
	private File destination;
	@Parameter(property = "modelName")
	private String modelName;
	
	@Parameter(property = "dialectList")
	private List<String> dialectList;

	@Parameter(property = "ignorePsm2Asm", defaultValue = "false")
	private Boolean ignorePsm2Asm = false;

	@Parameter(property = "ignorePsm2Measure", defaultValue = "false")
	private Boolean ignorePsm2Measure = false;

	@Parameter(property = "ignoreAsm2Openapi", defaultValue = "false")
	private Boolean ignoreAsm2Openapi = false;

	@Parameter(property = "ignoreAsm2Rdbms", defaultValue = "false")
	private Boolean ignoreAsm2Rdbms = false;

	@Parameter(property = "ignoreRdbms2Liquibase", defaultValue = "false")
	private Boolean ignoreRdbms2Liquibase = false;

	@Parameter(property = "ignoreAsm2sdk", defaultValue = "false")
	private Boolean ignoreAsm2sdk = false;

	@Parameter(property = "ignoreAsm2jaxrsapi", defaultValue = "false")
	private Boolean ignoreAsm2jaxrsapi = false;


	@Getter
	@Parameter(property = "tagsForSearch", defaultValue =
			PSM_2_ASM_TRANSFORMATION_SCRIPT_ROOT + "," +
			PSM_2_MEASURE_TRANSFORMATION_SCRIPT_ROOT + "," +
			ASM_2_OPEN_API_TRANSFORMATION_SCRIPT_ROOT + "," +
			ASM_2_RDBMS_TRANSFORMATION_SCRIPT_ROOT + "," +
			ASM_2_RDBMS_TRANSFORMATION_MODEL_ROOT + "," +
			RDBMS_2_LIQUIBASE_TRANSFORMATION_SCRIPT_ROOT + "," +
			ASM_2_SDK_TRANSFORMATION_SCRIPT_ROOT + "," +
			ASM_2_JAXRSAPI_TRANSFORMATION_SCRIPT_ROOT)
	private List<String> tagsForSearch;

	List<URL> compileClasspathFiles = new ArrayList<>();

	private void setContextClassLoader() throws DependencyResolutionRequiredException, MalformedURLException {
		for (Object mavenCompilePath : project.getCompileClasspathElements()) {
			String currentPathProcessed = (String) mavenCompilePath;
			compileClasspathFiles.add(new File(currentPathProcessed).toURI().toURL());
		}

		URL[] urlsForClassLoader = compileClasspathFiles.toArray(new URL[compileClasspathFiles.size()]);
		getLog().info("Set urls for URLClassLoader: " + Arrays.asList(urlsForClassLoader));

		// need to define parent classloader which knows all dependencies of the plugin
		ClassLoader classLoader = new URLClassLoader(urlsForClassLoader, PsmDefaultWorkflowMojo.class.getClassLoader());
		Thread.currentThread().setContextClassLoader(classLoader);
	}

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		// -------------------------------------------------- //
		// Fetching transformation script roots from manifest //
		// -------------------------------------------------- //

		// Needed for to access project's dependencies.
		// Info: http://blog.chalda.cz/2018/02/17/Maven-plugin-and-fight-with-classloading.html
		try {
			setContextClassLoader();
		} catch (Exception e) {
			throw new MojoExecutionException("Failed to set classloader", e);
		}

		WorkflowHelper workflowHelper = new WorkflowHelper(getLog(), compileClasspathFiles, tagsForSearch);

		PsmDefaultWorkflow defaultWorkflow;
		try {
			workflowHelper.extract();
			URI psm2measureModelScriptRootResolved = (psm2measureTransformationScriptRoot == null)
					? workflowHelper.getUrlPathForTag(PSM_2_MEASURE_TRANSFORMATION_SCRIPT_ROOT).toURI()
					: psm2measureTransformationScriptRoot.toURI();

			URI psm2asmModelScriptRootResolved = (psm2asmTransformationScriptRoot == null)
					? workflowHelper.getUrlPathForTag(PSM_2_ASM_TRANSFORMATION_SCRIPT_ROOT).toURI()
					: psm2asmTransformationScriptRoot.toURI();

			URI asm2rdbmsModelScriptRootResolved = (asm2rdbmsTransformationScriptRoot == null)
					? workflowHelper.getUrlPathForTag(ASM_2_RDBMS_TRANSFORMATION_SCRIPT_ROOT).toURI()
					: asm2rdbmsTransformationScriptRoot.toURI();

			URI asm2rdbmsModelModelRootResolved = (asm2rdbmsTransformationModelRoot == null)
					? workflowHelper.getUrlPathForTag(ASM_2_RDBMS_TRANSFORMATION_MODEL_ROOT).toURI()
					: asm2rdbmsTransformationModelRoot.toURI();

			URI asm2openApiModelScriptRootResolved = (asm2openApiTransformationScriptRoot == null)
					? workflowHelper.getUrlPathForTag(ASM_2_OPEN_API_TRANSFORMATION_SCRIPT_ROOT).toURI()
					: asm2openApiTransformationScriptRoot.toURI();

			URI rdbms2liquibaseModelScriptRootResolved = (rdbms2liquibaseTransformationScriptRoot == null)
					? workflowHelper.getUrlPathForTag(RDBMS_2_LIQUIBASE_TRANSFORMATION_SCRIPT_ROOT).toURI()
					: rdbms2liquibaseTransformationScriptRoot.toURI();

			URI asm2sdkModelScriptRootResolved = (asm2sdkTransformationScriptRoot == null)
					? workflowHelper.getUrlPathForTag(ASM_2_SDK_TRANSFORMATION_SCRIPT_ROOT).toURI()
					: asm2sdkTransformationScriptRoot.toURI();

			URI asm2jaxrsapiModelScriptRootResolved = (asm2jaxrsapiTransformationScriptRoot == null)
					? workflowHelper.getUrlPathForTag(ASM_2_JAXRSAPI_TRANSFORMATION_SCRIPT_ROOT).toURI()
					: asm2jaxrsapiTransformationScriptRoot.toURI();

			defaultWorkflow = new PsmDefaultWorkflow(DefaultWorkflowSetupParameters
					.defaultWorkflowSetupParameters()
					.psmModelSourceURI(psmModelDest.toURI())
					.psm2AsmModelTransformationScriptURI(psm2asmModelScriptRootResolved)
					.psm2MeasureModelTransformationScriptURI(psm2measureModelScriptRootResolved)
					.asm2RdbmsModelTransformationScriptURI(asm2rdbmsModelScriptRootResolved)
					.asm2OpenapiModelTransformationScriptURI(asm2openApiModelScriptRootResolved)
					.rdbms2LiquibaseModelTransformationScriptURI(rdbms2liquibaseModelScriptRootResolved)
					.modelName(modelName)
					.dialectList(dialectList)
					.asm2RdbmsModelTransformationModelURI(asm2rdbmsModelModelRootResolved)
					.asm2sdkModelTransformationScriptURI(asm2sdkModelScriptRootResolved)
					.asm2jaxrsapiModelTransformationScriptURI(asm2jaxrsapiModelScriptRootResolved));

		} catch (IOException | PsmValidationException | URISyntaxException e) {
			throw new MojoFailureException("An error occurred during the setup phase of the workflow.", e);
		}

		// ------------ //
		// Run workflow //
		// ------------ //
		try {
			defaultWorkflow.startDefaultWorkflow();
		} catch (IllegalStateException e) {
			throw new MojoFailureException("An error occurred during the execution phase of the workflow.", e);
		}

		// ------------------ //
		// Save models/traces //
		// ------------------ //
		destination.mkdirs();
		try {
			DefaultWorkflowSave.saveModels(defaultWorkflow.getTransformationContext(), destination, dialectList);
		} catch (AsmValidationException | IOException | MeasureValidationException | RdbmsValidationException
				| OpenapiValidationException | LiquibaseValidationException | IllegalStateException e) {
			throw new MojoFailureException("An error occurred during the saving phase of the workflow.", e);
		}
	}

}
