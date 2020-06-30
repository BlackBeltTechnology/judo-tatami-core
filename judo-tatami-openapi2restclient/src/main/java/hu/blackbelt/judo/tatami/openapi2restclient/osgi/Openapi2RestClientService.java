package hu.blackbelt.judo.tatami.openapi2restclient.osgi;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.execution.impl.StringBuilderLogger;
import hu.blackbelt.judo.meta.openapi.API;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel;
import hu.blackbelt.judo.tatami.openapi2restclient.Openapi2RestClient;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.ConfigurationPolicy;
import org.osgi.service.component.annotations.Deactivate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.Map;

@Component(immediate = true, service = Openapi2RestClientService.class, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Slf4j
public class Openapi2RestClientService {

    ComponentContext componentContext;

    File tempDir;

    @Activate
    public void activate(ComponentContext componentContext) {
        tempDir = componentContext.getBundleContext().getBundle().getDataFile("generated");
        tempDir.mkdirs();
        this.componentContext = componentContext;
    }

    @Deactivate
    public void deactivate(BundleContext bundleContext) throws IOException {
        if (Files.exists(Paths.get(tempDir.getAbsolutePath()))) {
            Files.walk(Paths.get(tempDir.getAbsolutePath()))
                    .map(Path::toFile)
                    .sorted(Comparator.reverseOrder())
                    .forEach(File::delete);
        }
    }


    public void install(OpenapiModel openapiModel, BundleContext bundleContext) throws Exception {
        Log logger = new StringBuilderLogger(Slf4jLog.determinateLogLevel(log));
        try {
            String outputDir = (String) componentContext.getProperties().get("outputDirectory");
            String languages = (String) componentContext.getProperties().get("languages");

            if (languages == null || outputDir == null) {
                return;
            }

            for (String languageNoTrim : languages.split(",")) {
                String language = languageNoTrim.trim();
                Map<String, Object> options = new HashMap<>();
                for (Enumeration<String> e = componentContext.getProperties().keys(); e.hasMoreElements();) {
                    String propName = e.nextElement();
                    if (propName.startsWith(language + ".")) {
                        options.put(propName.substring((language + ".").length()), componentContext.getProperties().get(propName));
                    }

                }
                Map<API, Map<String, File>>  generated = Openapi2RestClient.executeOpenaapi2RestClientGeneration(openapiModel, language, new File(outputDir), options);
            }
            log.info("\u001B[33m {}\u001B[0m", logger.getBuffer());
        } catch (Exception e) {
            log.info("\u001B[31m {}\u001B[0m", logger.getBuffer());
            throw e;
        }
    }

    public void uninstall(OpenapiModel openapiModel) throws BundleException {
    }
}
