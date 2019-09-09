package org.workflow.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;

import lombok.Getter;
import lombok.SneakyThrows;

public class WorkflowUtil {
	
	WorkflowMojo workflowMojo;
	
	// 1st level
	private HashMap<String, Attributes> manifestMap = new HashMap<>();
	
	// 2nd level
	private HashMap<String, String> pathMap = new HashMap<>();
	
	@Getter
	private String asmModelScriptRoot;
	@Getter
	private String measureModelScriptRoot;
	@Getter
	private String rdbmsModelScriptRoot;
	@Getter
	private String liquibaseModelScriptRoot;
	@Getter
	private String openApiModelScriptRoot;
	@Getter
	private String excelModelURI;

	public WorkflowUtil(WorkflowMojo workflowMojo)
	{
		this.workflowMojo = workflowMojo;
	}
	
	public void extract() throws IOException
	{	
		for(String s : workflowMojo.getTransformationArtifacts())
		{
			JarFile j = new JarFile(getArtifactFile(s).getAbsoluteFile());
			//extractJarFile(j, getArtifactFile(s).getAbsoluteFile());
			Manifest m = j.getManifest();
			j.close();
			String tempName = s.split(":")[1].split("-")[2];
			manifestMap.put(tempName, m.getMainAttributes());
			pathMap.put(tempName,getArtifactFile(s).getAbsolutePath());
			
		}
		
		asmModelScriptRoot = produceValidScriptRootPath("Psm2Asm");
		workflowMojo.getLog().info(asmModelScriptRoot);
//jar:file:///home/donat/.m2/repository/hu/blackbelt/judo/tatami/judo-tatami-psm2asm/1.0.0-SNAPSHOT/judo-tatami-psm2asm-1.0.0-SNAPSHOT.jar!/tatami/psm2asm/transformations/asm/


		measureModelScriptRoot = produceValidScriptRootPath("Psm2Measure");
		openApiModelScriptRoot = produceValidScriptRootPath("Asm2OpenApi");
		rdbmsModelScriptRoot = produceValidScriptRootPath("Asm2Rdbms");
		liquibaseModelScriptRoot = produceValidScriptRootPath("Rdbms2Liquibase");
		excelModelURI = "jar:file://" + pathMap.get("asm2rdbms") + "!/" + manifestMap.get("asm2rdbms").getValue("RdbmsExcelModelURI") + "/";
	}
	
	@SneakyThrows(ArtifactResolutionException.class)
    private File getArtifactFile(String uri) {
        Artifact artifact = new DefaultArtifact(uri);
        ArtifactRequest req = new ArtifactRequest().setRepositories(workflowMojo.repositories).setArtifact(artifact);
        ArtifactResult resolutionResult;
        resolutionResult = workflowMojo.repoSystem.resolveArtifact(workflowMojo.repoSession, req);
        return resolutionResult.getArtifact().getFile();
    }
	
	private String produceValidScriptRootPath(String name)
	{
		return "jar:file://" + pathMap.get(name.toLowerCase()) + "!/" + manifestMap.get(name.toLowerCase()).getValue(name + "Transformation-ScriptRoot") + "/";
	}
	
}
