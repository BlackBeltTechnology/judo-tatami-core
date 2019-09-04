package org.workflow.maven.plugin;

import java.io.File;
import java.io.IOException;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel.AsmValidationException;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel.LiquibaseValidationException;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel.MeasureValidationException;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel.OpenapiValidationException;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel.PsmValidationException;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel.RdbmsValidationException;
import hu.blackbelt.judo.tatami.workflow.DefaultWorkflow;
import hu.blackbelt.judo.tatami.workflow.DefaultWorkflowSetupParameters;

@Mojo(name = "defaultworkflow", defaultPhase = LifecyclePhase.COMPILE)
public class WorkflowMojo extends AbstractMojo {

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

	@Parameter(property = "defaultWorkflow.destination")
	private String destination;
	
	@Parameter(property = "modelName")
	private String modelName;
	
	@Parameter(property = "dialect")
	private String dialect;
	
	@Parameter(property = "excelModelURI")
	private String excelModelURI;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
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
			// TODO
		}

		defaultWorkflow.startDefaultWorkflow();
		try {
			defaultWorkflow.saveModels(new File(destination).toURI());
		} catch (IOException |
				 AsmValidationException |
				 MeasureValidationException |
				 RdbmsValidationException |
				 OpenapiValidationException |
				 LiquibaseValidationException e) {
			// TODO
		}
	}
}
