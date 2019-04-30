package hu.blackbelt.judo.tatami.esm2psm;

import hu.blackbelt.osgi.utils.osgi.api.BundleUtil;
import org.osgi.framework.Bundle;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Comparator;

@Component(immediate = true, service = Esm2PsmScriptResource.class)
public class Esm2PsmScriptResource {

    Bundle bundle;
    File scriptRoot;

    @Activate
    public void activate(ComponentContext componentContext) throws IOException {
        bundle = componentContext.getBundleContext().getBundle();

        scriptRoot = BundleUtil.copyBundlePathToPersistentStorage(
                bundle, "esm2psm", "tatami/esm2psm", "*", true);
    }

    @Deactivate
    public void deactivate(ComponentContext componentContext) throws IOException {
        bundle = componentContext.getBundleContext().getBundle();

        Files.walk(Paths.get(scriptRoot.getAbsolutePath()))
                .map(Path::toFile)
                .sorted(Comparator.reverseOrder())
                .forEach(File::delete);
    }


    public File getScriptRoot() {
        return scriptRoot;
    }

}
