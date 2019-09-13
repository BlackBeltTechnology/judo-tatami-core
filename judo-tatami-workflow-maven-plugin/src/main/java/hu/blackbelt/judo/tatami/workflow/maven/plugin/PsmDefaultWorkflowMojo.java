package hu.blackbelt.judo.tatami.workflow.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
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
import lombok.Getter;

@Mojo(name = "psm-default-workflow", defaultPhase = LifecyclePhase.COMPILE)
public class PsmDefaultWorkflowMojo extends AbstractMojo {

	@Parameter(defaultValue = "${project}", readonly = true, required = true)
	private MavenProject project;

	@Component
	public RepositorySystem repoSystem;

	@Parameter(defaultValue = "${repositorySystemSession}", readonly = true, required = true)
	public RepositorySystemSession repoSession;
	@Parameter(defaultValue = "${project.remoteProjectRepositories}", readonly = true, required = true)
	public List<RemoteRepository> repositories;

	@Parameter(property = "psmModelDest")
	private String psmModelDest;

	@Parameter(property = "psm2asmModelScriptRoot")
	private String psm2asmModelScriptRoot;
	@Parameter(property = "psm2measureModelScriptRoot")
	private String psm2measureModelScriptRoot;
	@Parameter(property = "asm2openApiModelScriptRoot")
	private String asm2openApiModelScriptRoot;
	@Parameter(property = "asm2rdbmsModelScriptRoot")
	private String asm2rdbmsModelScriptRoot;
	@Parameter(property = "rdbms2liquibaseModelScriptRoot")
	private String rdbms2liquibaseModelScriptRoot;
	@Parameter(property = "asm2sdkModelScriptRoot")
	private String asm2sdkModelScriptRoot;
	@Parameter(property = "asm2jaxrsapiModelScriptRoot")
	private String asm2jaxrsapiModelScriptRoot;
	
	
	@Parameter(property = "destination")
	private File destination;
	@Parameter(property = "modelName")
	private String modelName;
	@Parameter(property = "dialect")
	private String dialect;

	@Parameter(property = "asm2rdbmsModelModelRoot")
	private String asm2rdbmsModelModelRoot;

	@Getter
	@Parameter(property = "transformationArtifacts", defaultValue =
			"hu.blackbelt.judo.tatami:judo-tatami-asm2rdbms:${judo-tatami-version}," +
			"hu.blackbelt.judo.tatami:judo-tatami-psm2asm:${judo-tatami-version}," +
			"hu.blackbelt.judo.tatami:judo-tatami-psm2measure:${judo-tatami-version}," +
			"hu.blackbelt.judo.tatami:judo-tatami-asm2openapi:${judo-tatami-version}," +
			"hu.blackbelt.judo.tatami:judo-tatami-rdbms2liquibase:${judo-tatami-version}," +
			"hu.blackbelt.judo.tatami:judo-tatami-asm2sdk:${judo-tatami-version}," +
			"hu.blackbelt.judo.tatami:judo-tatami-asm2jaxrsapi:${judo-tatami-version}")
	private List<String> transformationArtifacts;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {

		// -------------------------------------------------- //
		// Fetching transformation script roots from manifest //
		// -------------------------------------------------- //
		WorkflowHelper workflowHelper = new WorkflowHelper(repoSystem, repoSession, repositories, transformationArtifacts);
		try {
			workflowHelper.extract();
			psm2measureModelScriptRoot = (psm2measureModelScriptRoot == null)
					? workflowHelper.getPsm2measureModelScriptRoot()
					: psm2measureModelScriptRoot;
			psm2asmModelScriptRoot = (psm2asmModelScriptRoot == null) 
					? workflowHelper.getPsm2asmModelScriptRoot()
					: psm2asmModelScriptRoot;
			asm2rdbmsModelScriptRoot = (asm2rdbmsModelScriptRoot == null) 
					? workflowHelper.getAsm2rdbmsModelScriptRoot()
					: asm2rdbmsModelScriptRoot;
			asm2openApiModelScriptRoot = (asm2openApiModelScriptRoot == null)
					? workflowHelper.getAsm2openApiModelScriptRoot()
					: asm2openApiModelScriptRoot;
			rdbms2liquibaseModelScriptRoot = (rdbms2liquibaseModelScriptRoot == null)
					? workflowHelper.getRdbms2liquibaseModelScriptRoot()
					: rdbms2liquibaseModelScriptRoot;
			asm2rdbmsModelModelRoot = (asm2rdbmsModelModelRoot == null)
					? workflowHelper.getAsm2rdbmsExcelModelURI() 
					: asm2rdbmsModelModelRoot;
			asm2sdkModelScriptRoot = (asm2sdkModelScriptRoot == null)
					? workflowHelper.getAsm2sdkModelScriptRoot()
					: asm2sdkModelScriptRoot;
			asm2jaxrsapiModelScriptRoot = (asm2jaxrsapiModelScriptRoot == null)
					? workflowHelper.getAsm2jaxrsapiModelScriptRoot()
					: asm2jaxrsapiModelScriptRoot;
					
		} catch (IOException e) {
			throw new MojoFailureException("An error occurred during the extraction of the script roots.", e);
		}

		// -------------- //
		// Setup workflow //
		// -------------- //
		PsmDefaultWorkflow defaultWorkflow = new PsmDefaultWorkflow();
		try {
			defaultWorkflow.setUp(DefaultWorkflowSetupParameters
					.defaultWorkflowSetupParameters()
					.psmModelSourceURI(new File(psmModelDest).toURI())
					.psm2AsmModelTransformationScriptURI(new URI(psm2asmModelScriptRoot))
					.psm2MeasureModelTransformationScriptURI(new URI(psm2measureModelScriptRoot))
					.asm2RdbmsModelTransformationScriptURI(new URI(asm2rdbmsModelScriptRoot))
					.asm2OpenapiModelTransformationScriptURI(new URI(asm2openApiModelScriptRoot))
					.rdbms2LiquibaseModelTransformationScriptURI(new URI(rdbms2liquibaseModelScriptRoot))
					.modelName(modelName)
					.dialect(dialect)
					.asm2RdbmsModelTransformationModelURI(new URI(asm2rdbmsModelModelRoot))
					.asm2sdkModelTransformationScriptURI(new URI(asm2sdkModelScriptRoot))
					.asm2jaxrsapiModelTransformationScriptURI(new URI(asm2jaxrsapiModelScriptRoot)));
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
			DefaultWorkflowSave.saveModels(defaultWorkflow.getTransformationContext(), destination);
		} catch (AsmValidationException | IOException | MeasureValidationException | RdbmsValidationException
				| OpenapiValidationException | LiquibaseValidationException | IllegalStateException e) {
			throw new MojoFailureException("An error occurred during the saving phase of the workflow.", e);
		}
	}

}
