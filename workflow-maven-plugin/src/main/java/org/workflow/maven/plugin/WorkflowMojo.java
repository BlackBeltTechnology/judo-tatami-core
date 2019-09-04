package org.workflow.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.net.URISyntaxException;
import java.net.URL;

import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel.AsmValidationException;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel.LiquibaseValidationException;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel.MeasureValidationException;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel.OpenapiValidationException;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel.PsmValidationException;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel.RdbmsValidationException;
import hu.blackbelt.judo.tatami.workflow.DefaultWorkflow;

@Mojo(name = "defaultworkflow")
public class WorkflowMojo extends AbstractMojo {

	@Parameter(property = "defaultworkflow.psmModelDest")
	private File psmModelDest;

	@Parameter(property = "defaultWorkflow.asmModelUrl")
	private URL asmModelUrl;
	@Parameter(property = "defaultWorkflow.measureModelUrl")
	private URL measureModelUrl;
	@Parameter(property = "defaultWorkflow.openApiModelUrl")
	private URL openApiModelUrl;
	@Parameter(property = "defaultWorkflow.rdbmsModelUrl")
	private URL rdbmsModelUrl;
	@Parameter(property = "defaultWorkflow.liquibaseModelUrl")
	private URL liquibaseModelUrl;

	@Parameter(property = "defaultWorkflow.destination")
	private URL destination;

	@Override
	public void execute() throws MojoExecutionException, MojoFailureException {
		DefaultWorkflow defaultWorkflow = new DefaultWorkflow();
		try {
			defaultWorkflow.setUp(psmModelDest, asmModelUrl.toURI(), openApiModelUrl.toURI(), measureModelUrl.toURI(),
					rdbmsModelUrl.toURI(), liquibaseModelUrl.toURI());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (PsmValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

		defaultWorkflow.startDefaultWorkflow();
		try {
			defaultWorkflow.saveModels(destination.toURI());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (AsmValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (MeasureValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (RdbmsValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (OpenapiValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (LiquibaseValidationException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (URISyntaxException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}
