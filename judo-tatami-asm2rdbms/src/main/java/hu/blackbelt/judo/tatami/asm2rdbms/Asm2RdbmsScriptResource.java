package hu.blackbelt.judo.tatami.asm2rdbms;

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

@Component(immediate = true, service = Asm2RdbmsScriptResource.class)
public class Asm2RdbmsScriptResource {

    Bundle bundle;
    File scriptRoot;

    @Activate
    public void activate(ComponentContext componentContext) throws IOException {
        bundle = componentContext.getBundleContext().getBundle();

        scriptRoot = BundleUtil.copyBundlePathToPersistentStorage(
                bundle, "asm2rdbms", "tatami/asm2rdbms", "*", true);
    }

    @Deactivate
    public void deactivate(ComponentContext componentContext) throws IOException {
        bundle = componentContext.getBundleContext().getBundle();

        if (Files.exists(Paths.get(scriptRoot.getAbsolutePath()))) {
            Files.walk(Paths.get(scriptRoot.getAbsolutePath()))
                    .map(Path::toFile)
                    .sorted(Comparator.reverseOrder())
                    .forEach(File::delete);
        }
    }


    public File getSctiptRoot() {
        return scriptRoot;
    }

}
