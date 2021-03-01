package hu.blackbelt.judo.tatami.workflow.maven.plugin;

import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.tatami.workflow.DefaultWorkflowSave;
import hu.blackbelt.judo.tatami.workflow.DefaultWorkflowSetupParameters;
import hu.blackbelt.judo.tatami.workflow.EsmDefaultWorkflow;
import org.apache.maven.artifact.DependencyResolutionRequiredException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugin.descriptor.PluginDescriptor;
import org.apache.maven.plugins.annotations.*;
import org.apache.maven.project.MavenProject;
import org.codehaus.plexus.classworlds.realm.ClassRealm;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.repository.RemoteRepository;

import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.google.common.base.Strings.isNullOrEmpty;

@Mojo(name = "esm-default-workflow",
		defaultPhase = LifecyclePhase.COMPILE,
		requiresDependencyResolution = ResolutionScope.COMPILE)
public class EsmDefaultWorkflowMojo extends AbstractMojo {

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

	@Parameter(property = "esmModelFile")
	private File esmModelFile;

	@Parameter(property = "destination")
	private File destination;

	@Parameter(property = "modelName")
	private String modelName;
	
	@Parameter(property = "dialectList")
	private List<String> dialectList;

	@Parameter(property = "runInParallel", defaultValue = "true")
	private Boolean runInParallel = true;

	@Parameter(property = "enableMetrics", defaultValue = "true")
	private Boolean enableMetrics = true;

	@Parameter(property = "ignoreEsm2Psm", defaultValue = "false")
	private Boolean ignoreEsm2Psm = false;

	@Parameter(property = "ignoreEsm2Ui", defaultValue = "false")
	private Boolean ignoreEsm2Ui = false;

	@Parameter(property = "ignorePsm2Asm", defaultValue = "false")
	private Boolean ignorePsm2Asm = false;

	@Parameter(property = "ignorePsm2Measure", defaultValue = "false")
	private Boolean ignorePsm2Measure = false;

	@Parameter(property = "ignoreAsm2Openapi", defaultValue = "false")
	private Boolean ignoreAsm2Openapi = false;

	@Parameter(property = "ignoreAsm2Rdbms", defaultValue = "false")
	private Boolean ignoreAsm2Rdbms = false;

	@Parameter(property = "ignoreAsm2Keycloak", defaultValue = "false")
	private Boolean ignoreAsm2Keycloak = false;

	@Parameter(property = "ignoreRdbms2Liquibase", defaultValue = "false")
	private Boolean ignoreRdbms2Liquibase = false;

	@Parameter(property = "ignoreAsm2sdk", defaultValue = "false")
	private Boolean ignoreAsm2sdk = false;

	@Parameter(property = "ignoreAsm2jaxrsapi", defaultValue = "false")
	private Boolean ignoreAsm2jaxrsapi = false;

	@Parameter(property = "ignoreAsm2Expression", defaultValue = "false")
	private Boolean ignoreAsm2Expression = false;

	@Parameter(property = "ignoreAsm2Script", defaultValue = "false")
	private Boolean ignoreAsm2Script = false;

	@Parameter(property = "ignoreScript2Operation", defaultValue = "false")
	private Boolean ignoreScript2Operation = false;

	@Parameter(property = "esmGeneratorClassName")
	private String esmGeneratorClassName;

	@Parameter(property = "esmGeneratorMethodName")
	private String esmGeneratorMethodName;

	@Parameter(property = "validateModels", defaultValue = "false")
	private Boolean validateModels = false;

	Set<URL> classPathUrls = new HashSet<>();

	private void setContextClassLoader() throws DependencyResolutionRequiredException, MalformedURLException {
		// Project dependencies
		for (Object mavenCompilePath : project.getCompileClasspathElements()) {
			String currentPathProcessed = (String) mavenCompilePath;
			classPathUrls.add(new File(currentPathProcessed).toURI().toURL());
		}

		// Plugin dependencies
		final ClassRealm classRealm = pluginDescriptor.getClassRealm();
		for (URL url: classRealm.getURLs()) {
			classPathUrls.add(url);
		}

		URL[] urlsForClassLoader = classPathUrls.toArray(new URL[classPathUrls.size()]);
		getLog().debug("Set urls for URLClassLoader: " + Arrays.asList(urlsForClassLoader));

		// need to define parent classloader which knows all dependencies of the plugin
		ClassLoader classLoader = new URLClassLoader(urlsForClassLoader, EsmDefaultWorkflowMojo.class.getClassLoader());
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

		EsmDefaultWorkflow defaultWorkflow;
		try {
			DefaultWorkflowSetupParameters.DefaultWorkflowSetupParametersBuilder parameters =
					DefaultWorkflowSetupParameters
					.defaultWorkflowSetupParameters()
					.runInParallel(runInParallel)
					.enableMetrics(enableMetrics)
					.ignoreEsm2Psm(ignoreEsm2Psm)
					.ignoreEsm2Ui(ignoreEsm2Ui)
					.ignorePsm2Asm(ignorePsm2Asm)
					.ignoreAsm2jaxrsapi(ignoreAsm2jaxrsapi)
					.ignoreAsm2Openapi(ignoreAsm2Openapi)
					.ignoreAsm2Rdbms(ignoreAsm2Rdbms)
					.ignoreAsm2Keycloak(ignoreAsm2Keycloak)
					.ignoreAsm2sdk(ignoreAsm2sdk)
					.ignoreAsm2Expression(ignoreAsm2Expression)
					.ignoreAsm2Script(ignoreAsm2Script)
					.ignorePsm2Measure(ignorePsm2Measure)
					.ignoreScript2Operation(ignoreScript2Operation)
					.ignoreRdbms2Liquibase(ignoreRdbms2Liquibase)
					.validateModels(validateModels)
					.modelName(modelName)
					.dialectList(dialectList);
			//DefaultWorkflowSetupParameters.addTransformerCalculatedUris(parameters);

			if (!isNullOrEmpty(esmGeneratorClassName) && !isNullOrEmpty(esmGeneratorMethodName)) {
				Class generatorClass = Thread.currentThread().getContextClassLoader().loadClass(esmGeneratorClassName);
				Method generatorMethod = generatorClass.getMethod(esmGeneratorMethodName);
				EsmModel esmModel = (EsmModel) generatorMethod.invoke(generatorClass.newInstance());
				parameters.esmModel(esmModel);
			} else {
				parameters.esmModelSourceURI(esmModelFile.toURI());
			}
			defaultWorkflow = new EsmDefaultWorkflow(parameters);
		} catch (ClassNotFoundException | NoSuchMethodException | InstantiationException | IllegalAccessException | InvocationTargetException e) {
			throw new MojoFailureException("An error occurred during the setup phase of the workflow.", e);
		}

		// ------------ //
		// Run workflow //
		// ------------ //
		try {
			defaultWorkflow.startDefaultWorkflow();
		} catch (IllegalStateException e) {
			try {
				DefaultWorkflowSave.saveModels(defaultWorkflow.getTransformationContext(), destination, dialectList);
			} catch (Exception e2) {
			}
			throw new MojoFailureException("An error occurred during the execution phase of the workflow.", e);
		}

		// ------------------ //
		// Save models/traces //
		// ------------------ //
		destination.mkdirs();
		try {
			DefaultWorkflowSave.saveModels(defaultWorkflow.getTransformationContext(), destination, dialectList);
		} catch (Exception e) {
			throw new MojoFailureException("An error occurred during the saving phase of the workflow.", e);
		}
	}

}
