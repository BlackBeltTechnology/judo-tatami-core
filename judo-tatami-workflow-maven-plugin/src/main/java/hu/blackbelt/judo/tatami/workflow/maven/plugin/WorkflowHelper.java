package hu.blackbelt.judo.tatami.workflow.maven.plugin;

import java.io.IOException;
import java.net.JarURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;
import java.util.function.Function;
import java.util.jar.Attributes;
import java.util.jar.Manifest;
import java.util.stream.Collectors;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Data;
import org.apache.maven.plugin.logging.Log;

public class WorkflowHelper {
	private final  Map<String, UrlAndPath> urlAndPathForTag = new ConcurrentHashMap();
	private final Set<URL> jarsForSearch;
	private final List<String> tags;
	private final Log log;

	public WorkflowHelper(Log log, Set<URL> jarsForSearch, List<String> tags) {
		this.jarsForSearch = jarsForSearch;
		this.tags = tags;
		this.log = log;
	}

	public void extract() throws IOException {
		for (URL originalUrl : jarsForSearch.stream().filter(u -> u.toString().endsWith(".jar")).collect(Collectors.toList())) {
			log.debug("Workflow helper - Proceessing URL: " + originalUrl);

			URL url;
			if (originalUrl.toString().startsWith("jar:")) {
				url = originalUrl;
 			} else {
				url = new URL("jar:" + originalUrl.toString() + "!/");
			}
			JarURLConnection jarConnection = (JarURLConnection) url.openConnection();
			Manifest manifest = jarConnection.getManifest();
			Map<String, String> manifestEntries = manifest.getMainAttributes().entrySet().stream()
					.collect(Collectors.toMap(e -> e.getKey().toString(), e -> e.getValue().toString()));
//			for (Object key : manifestEntries.keySet()) {
//				log.info("    Key: " + key + " Val: " + manifestEntries.get(key.toString()));
//			}

			for (String tag : tags) {
				// log.info("   Search for tag: " + tag);
				if (manifestEntries.get(tag) != null) {
					log.debug("     - Tag " + tag + " found");
					urlAndPathForTag.put(tag, new UrlAndPath(url, (String) manifestEntries.get(tag)));
				}
			}
		}
	}

	public URL getUrlPathForTag(String tag) throws MalformedURLException {
		Preconditions.checkState(urlAndPathForTag.containsKey(tag), "No JAR file found with MANIFEST tag: " + tag + " in classpath.");
		return new URL(urlAndPathForTag.get(tag).getUrl() + urlAndPathForTag.get(tag).getPath() + "/");
	}

	@Data
	@AllArgsConstructor
	public static class UrlAndPath {
		URL url;
		String path;

		public String getFullURL() {
			return getUrl() + getPath() + "/";
		}
	}
}
