package hu.blackbelt.judo.tatami.workflow.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.maven.execution.MavenSession;
import org.apache.maven.plugins.annotations.Component;
import org.apache.maven.plugins.annotations.Parameter;
import org.eclipse.aether.RepositorySystem;
import org.eclipse.aether.RepositorySystemSession;
import org.eclipse.aether.artifact.Artifact;
import org.eclipse.aether.artifact.DefaultArtifact;
import org.eclipse.aether.repository.RemoteRepository;
import org.eclipse.aether.resolution.ArtifactRequest;
import org.eclipse.aether.resolution.ArtifactResolutionException;
import org.eclipse.aether.resolution.ArtifactResult;

import lombok.Getter;
import lombok.SneakyThrows;

public class WorkflowHelper {
	private HashMap<String, Attributes> manifestMap = new HashMap<>();
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
	
	private RepositorySystem repoSystem;
	private RepositorySystemSession repoSession;
	private List<RemoteRepository> repositories;
	private List<String> transformationArtifacts;

	public WorkflowHelper(PsmDefaultWorkflowMojo workflowMojo) {
		repoSystem = workflowMojo.repoSystem;
		repoSession = workflowMojo.repoSession;
		repositories = workflowMojo.repositories;
		transformationArtifacts = workflowMojo.getTransformationArtifacts();
	}
	
	public WorkflowHelper(MavenSession s)
	{
		
	}

	public void extract() throws IOException {
		for (String artifactString : transformationArtifacts) {
			JarFile j = new JarFile(getArtifactFile(artifactString).getAbsoluteFile());
			Manifest m = j.getManifest();
			j.close();
			String tempName = artifactString.split(":")[1].split("-")[2];
			manifestMap.put(tempName, m.getMainAttributes());
			pathMap.put(tempName, getArtifactFile(artifactString).getAbsolutePath());
		}

		asmModelScriptRoot = produceValidScriptRootPath("Psm2Asm");
		measureModelScriptRoot = produceValidScriptRootPath("Psm2Measure");
		openApiModelScriptRoot = produceValidScriptRootPath("Asm2OpenApi");
		rdbmsModelScriptRoot = produceValidScriptRootPath("Asm2Rdbms");
		liquibaseModelScriptRoot = produceValidScriptRootPath("Rdbms2Liquibase");
		excelModelURI = "jar:file://" + pathMap.get("asm2rdbms") + "!/"
				+ manifestMap.get("asm2rdbms").getValue("RdbmsExcelModelURI") + "/";
	}

	private String produceValidScriptRootPath(String name) {
		return "jar:file://" + pathMap.get(name.toLowerCase()) + "!/"
				+ manifestMap.get(name.toLowerCase()).getValue(name + "-Transformation-ModelRoot") + "/";
	}

	@SneakyThrows(ArtifactResolutionException.class)
	private File getArtifactFile(String uri) {
		Artifact artifact = new DefaultArtifact(uri);
		ArtifactRequest req = new ArtifactRequest().setRepositories(repositories).setArtifact(artifact);
		ArtifactResult resolutionResult;
		resolutionResult = repoSystem.resolveArtifact(repoSession, req);
		return resolutionResult.getArtifact().getFile();
	}
}
