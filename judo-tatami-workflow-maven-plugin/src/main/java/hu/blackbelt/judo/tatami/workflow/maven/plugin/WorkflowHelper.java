package hu.blackbelt.judo.tatami.workflow.maven.plugin;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

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
	private String psm2asmModelScriptRoot;
	@Getter
	private String psm2measureModelScriptRoot;
	@Getter
	private String asm2rdbmsModelScriptRoot;
	@Getter
	private String rdbms2liquibaseModelScriptRoot;
	@Getter
	private String asm2openApiModelScriptRoot;
	@Getter
	private String asm2rdbmsExcelModelURI;
	@Getter
	private String asm2sdkModelScriptRoot;
	@Getter
	private String asm2jaxrsapiModelScriptRoot;
	
	private RepositorySystem repoSystem;
	private RepositorySystemSession repoSession;
	private List<RemoteRepository> repositories;
	private List<String> transformationArtifacts;

	public WorkflowHelper(RepositorySystem repoSystem, RepositorySystemSession repoSession, List<RemoteRepository> repositories, List<String> transformationArtifacts) {
		this.repoSystem = repoSystem;
		this.repoSession = repoSession;
		this.repositories = repositories;
		this.transformationArtifacts = transformationArtifacts;
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

		psm2asmModelScriptRoot = produceValidationScriptRootPath("Psm2Asm");
		psm2measureModelScriptRoot = produceValidationScriptRootPath("Psm2Measure");
		asm2openApiModelScriptRoot = produceValidationScriptRootPath("Asm2OpenApi");
		asm2rdbmsModelScriptRoot = produceValidationScriptRootPath("Asm2Rdbms");
		rdbms2liquibaseModelScriptRoot = produceValidationScriptRootPath("Rdbms2Liquibase");
		asm2rdbmsExcelModelURI = "jar:file://" + pathMap.get("asm2rdbms") + "!/"
				+ manifestMap.get("asm2rdbms").getValue("Asm2Rdbms-Transformation-ModelRoot") + "/";
		asm2sdkModelScriptRoot = produceValidationScriptRootPath("Asm2SDK");
		asm2jaxrsapiModelScriptRoot = produceValidationScriptRootPath("Asm2Jaxrsapi");
	}

	private String produceValidationScriptRootPath(String name) {
		return "jar:file://" + pathMap.get(name.toLowerCase()) + "!/"
				+ manifestMap.get(name.toLowerCase()).getValue(name + "-Transformation-ScriptRoot") + "/";
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
