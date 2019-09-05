package org.workflow.maven.plugin;

import java.io.IOException;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.HashMap;
import java.util.Map;
import java.util.jar.Attributes;
import java.util.jar.JarFile;
import java.util.jar.Manifest;

import org.apache.maven.model.Dependency;

public class WorkflowUtil {
	
	public static void get()
	{
		List<DependencyNode> allNodes = new ArrayList<DependencyNode>();
		DependencyManagement dependencyManagement = project.getDependencyManagement();
		List<Dependency> dependencies = dependencyManagement.getDependencies();

		getLog().info("found the following managed dependencies:");
		for (Dependency dependency : dependencies) {
		    getLog().info(dependency.toString());
		    String groupId = dependency.getGroupId();
		    String artifactId = dependency.getArtifactId();
		    String version = dependency.getVersion();
		    String scope = dependency.getScope();
		    String type = dependency.getType();
		    Artifact artifact = artifactFactory.createArtifact(groupId, artifactId, version, scope, type);
		    MavenProject buildFromRepository = mavenProjectBuilder.buildFromRepository(artifact, remoteRepositories, localRepository);
		    allNodes.addAll(getDependencies(buildFromRepository));
		}
	}
	
	public static void extract() throws IOException
	{
		URLClassLoader classLoader = (URLClassLoader) ClassLoader.getSystemClassLoader();
		Manifest manifest;
		
	    for(URL url: classLoader.getURLs()) {
			manifest = new JarFile(url.getFile()).getManifest();
	    }
	    
	    HashMap<String, Attributes> entries = new HashMap(manifest.getEntries());
	    
	    /*for(Map.Entry<String, Attributes> e : entries.entrySet())
	    {
	    	System.out.println();
	    }*/
	     
	    //Dependency dependency = new Dependency();
		
	}
}
