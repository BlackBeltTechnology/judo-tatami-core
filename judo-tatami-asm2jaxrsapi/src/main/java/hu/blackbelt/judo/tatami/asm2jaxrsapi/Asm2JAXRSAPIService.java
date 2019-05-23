package hu.blackbelt.judo.tatami.asm2jaxrsapi;

import com.google.common.collect.Maps;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;
import java.util.Map;

import static hu.blackbelt.judo.tatami.asm2jaxrsapi.Asm2JAXRSAPI.executeAsm2JAXRSAPIGeneration;

@Component(immediate = true, service = Asm2JAXRSAPIService.class)
@Slf4j
public class Asm2JAXRSAPIService {

    File tempDir;

    @Reference
    Asm2JAXRSAPIScriptResource asm2JAXRSAPIScriptResource;

    Map<AsmModel, Bundle> asm2jaxrsAPIBundles = Maps.newHashMap();

    @Activate
    public void activate(BundleContext bundleContext) {
        tempDir = bundleContext.getBundle().getDataFile("generated");
        tempDir.mkdirs();
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

    public void install(AsmModel asmModel, BundleContext bundleContext) throws Exception {
        asm2jaxrsAPIBundles.put(asmModel,
                bundleContext.installBundle("asm2jaxrsapi",
                        executeAsm2JAXRSAPIGeneration(new ResourceSetImpl(), asmModel, new Slf4jLog(log),
                                new File(asm2JAXRSAPIScriptResource.getSctiptRoot().getAbsolutePath(), "asm2jaxrsapi/templates"),
                                new File(tempDir.getAbsolutePath(), asmModel.getName()))));

        asm2jaxrsAPIBundles.get(asmModel).start();
    }

    public void uninstall(AsmModel asmModel) throws BundleException {
        if (asm2jaxrsAPIBundles.containsKey(asmModel)) {
            asm2jaxrsAPIBundles.get(asmModel).uninstall();
        } else {
            log.error("ASM model is not installed: " + asmModel.toString());
        }
    }
}
