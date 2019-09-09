package org.workflow.maven.plugin;

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
import hu.blackbelt.judo.tatami.workflow.DefaultWorkflow;
import hu.blackbelt.judo.tatami.workflow.DefaultWorkflowSetupParameters;
import lombok.Getter;

@Mojo(name = "defaultworkflow", defaultPhase = LifecyclePhase.COMPILE)
public class WorkflowMojo extends AbstractMojo {

    @Parameter( defaultValue = "${project}", readonly = true, required = true )
    private MavenProject project;
    
    @Component
    public RepositorySystem repoSystem;

    @Parameter(defaultValue = "${repositorySystemSession}", readonly = true, required = true)
    public RepositorySystemSession repoSession;

    @Parameter(defaultValue = "${project.remoteProjectRepositories}", readonly = true, required = true)
    public List<RemoteRepository> repositories;
   
	@Parameter(property = "psmModelDest")
	private String psmModelDest;

	@Parameter(property = "asmModelScriptRoot")
	private String asmModelScriptRoot;
	@Parameter(property = "measureModelScriptRoot")
	private String measureModelScriptRoot;
	@Parameter(property = "openApiModelScriptRoot")
	private String openApiModelScriptRoot;
	@Parameter(property = "rdbmsModelScriptRoot")
	private String rdbmsModelScriptRoot;
	@Parameter(property = "liquibaseModelScriptRoot")
	private String liquibaseModelScriptRoot;

	@Parameter(property = "destination")
	private File destination;
	
	@Parameter(property = "modelName")
	private String modelName;
	
	@Parameter(property = "dialect")
	private String dialect;
	
	@Parameter(property = "excelModelURI")
	private String excelModelURI;

	@Getter
	@Parameter(property = "transformationArtifacts", 
			defaultValue = "hu.blackbelt.judo.tatami:judo-tatami-asm2rdbms:${judo-tatami-version},"
					+ "hu.blackbelt.judo.tatami:judo-tatami-psm2asm:${judo-tatami-version},"
					+ "hu.blackbelt.judo.tatami:judo-tatami-psm2measure:${judo-tatami-version},"
					+ "hu.blackbelt.judo.tatami:judo-tatami-asm2openapi:${judo-tatami-version},"
					+ "hu.blackbelt.judo.tatami:judo-tatami-rdbms2liquibase:${judo-tatami-version}")
	private List<String> transformationArtifacts;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		//----------------------------------------------------//
		// Fetching transformation script roots from manifest //
		//----------------------------------------------------//
		WorkflowUtil workflowUtil = new WorkflowUtil(this);
		try {
			workflowUtil.extract();
			measureModelScriptRoot = workflowUtil.getMeasureModelScriptRoot();
			asmModelScriptRoot = workflowUtil.getAsmModelScriptRoot();
			rdbmsModelScriptRoot = workflowUtil.getRdbmsModelScriptRoot();
			openApiModelScriptRoot = workflowUtil.getOpenApiModelScriptRoot();
			liquibaseModelScriptRoot = workflowUtil.getLiquibaseModelScriptRoot();
			excelModelURI = workflowUtil.getExcelModelURI();
			
		} catch (IOException e1) {
			throw new MojoFailureException("An error occurred during the extraction of the script roots",e1);
		}
		
		//------------------//
		// Running workflow //
		//------------------//
		DefaultWorkflow defaultWorkflow = new DefaultWorkflow();
		try {
			defaultWorkflow.setUp(DefaultWorkflowSetupParameters.defaultWorkflowSetupParameters()
					.psmModeldest(new File(psmModelDest))
					.asmModelURI(new URI(asmModelScriptRoot))
					.measureModelURI(new URI(measureModelScriptRoot))
					.rdbmsModelURI(new URI(rdbmsModelScriptRoot))
					.openapiModelURI(new URI(openApiModelScriptRoot))
					.liquibaseModelURI(new URI(liquibaseModelScriptRoot))
					.modelName(modelName)
					.dialect(dialect)
					.excelModelUri(new URI(excelModelURI))
					.build());
		} catch (IOException | PsmValidationException | URISyntaxException e) {
			throw new MojoFailureException("An error occurred during the setup phase of the workflow.", e);
		}

		try {
			defaultWorkflow.startDefaultWorkflow();
		}
		catch(IllegalStateException e)
		{
			throw new MojoFailureException("An error occurred during the execution phase of the workflow.", e);
		}
		
	    destination.mkdirs();
		
		try {
			defaultWorkflow.saveModels(destination);
		} catch (AsmValidationException |
				 IOException |
				 MeasureValidationException |
				 RdbmsValidationException |
				 OpenapiValidationException |
				 LiquibaseValidationException |
				 IllegalStateException e) {
			throw new MojoFailureException("An error occurred during the saving phase of the workflow.", e);
		}
	}

}
