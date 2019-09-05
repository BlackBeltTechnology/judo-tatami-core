package org.workflow.maven.plugin;

import java.io.File;
import java.io.IOException;
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
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel.AsmValidationException;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel.LiquibaseValidationException;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel.MeasureValidationException;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel.OpenapiValidationException;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel.PsmValidationException;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel.RdbmsValidationException;
import hu.blackbelt.judo.tatami.workflow.DefaultWorkflow;
import hu.blackbelt.judo.tatami.workflow.DefaultWorkflowSetupParameters;
import lombok.SneakyThrows;

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

	@Parameter(property = "asmModelPath")
	private String asmModelPath;
	@Parameter(property = "measureModelPath")
	private String measureModelPath;
	@Parameter(property = "openApiModelPath")
	private String openApiModelPath;
	@Parameter(property = "rdbmsModelPath")
	private String rdbmsModelPath;
	@Parameter(property = "liquibaseModelPath")
	private String liquibaseModelPath;

	@Parameter(property = "destination")
	private File destination;
	
	@Parameter(property = "modelName")
	private String modelName;
	
	@Parameter(property = "dialect")
	private String dialect;
	
	@Parameter(property = "excelModelURI")
	private String excelModelURI;

	@Parameter(property = "transformationArtifacts", 
			defaultValue = "hu.blackbelt.judo.tatami:judo-tatami-asm2rdbms:${judo-tatami-version},"
					+ "hu.blackbelt.judo.tatami:judo-tatami-esm2psm:${judo-tatami-version}")
	List<String> trasformationArtifacts;
	
	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		
		
		for(String s : trasformationArtifacts) {
			getLog().warn(s);
			getLog().info(getArtifactFile(s).getAbsolutePath());
		}
			
		WorkflowUtil workflowUtil = new WorkflowUtil();
		DefaultWorkflow defaultWorkflow = new DefaultWorkflow();
		try {
			defaultWorkflow.setUp(DefaultWorkflowSetupParameters.defaultWorkflowSetupParameters()
					.psmModeldest(new File(psmModelDest))
					.asmModelURI(new File(asmModelPath).toURI())
					.measureModelURI(new File(measureModelPath).toURI())
					.rdbmsModelURI(new File(rdbmsModelPath).toURI())
					.openapiModelURI(new File(openApiModelPath).toURI())
					.liquibaseModelURI(new File(liquibaseModelPath).toURI())
					.modelName(modelName)
					.dialect(dialect)
					.excelModelUri(excelModelURI)
					.build());
		} catch (IOException | PsmValidationException e) {
			// log.error("An error occurred during the setup phase of the workflow. \n" + e.toString());
			throw new MojoFailureException("An error occurred during the setup phase of the workflow.", e);
		}

		try {
			defaultWorkflow.startDefaultWorkflow();
		}
		catch(IllegalStateException e)
		{
			//log.error("An error occurred during the execution phase of the workflow. \n" + e.toString());
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
			//log.error("An error occurred during the saving of the models. \n" + e.toString());
			throw new MojoFailureException("An error occurred during the saving phase of the workflow.", e);
		}
	}
	
	
    @SneakyThrows(ArtifactResolutionException.class)
    public File getArtifactFile(String uri) {
        Artifact artifact = new DefaultArtifact(uri);
        ArtifactRequest req = new ArtifactRequest().setRepositories(this.repositories).setArtifact(artifact);
        ArtifactResult resolutionResult;
        resolutionResult = this.repoSystem.resolveArtifact(this.repoSession, req);
        return resolutionResult.getArtifact().getFile();
    }

}
