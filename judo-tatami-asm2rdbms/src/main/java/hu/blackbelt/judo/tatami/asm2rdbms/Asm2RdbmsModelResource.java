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

@Component(immediate = true, service = Asm2RdbmsModelResource.class)
public class Asm2RdbmsModelResource {

    Bundle bundle;
    File modelRoot;

    @Activate
    public void activate(ComponentContext componentContext) throws IOException {
        bundle = componentContext.getBundleContext().getBundle();

        modelRoot = BundleUtil.copyBundlePathToPersistentStorage(
                bundle, "asm2rdbms-model", "tatami/asm2rdbms/model", "*", true);
    }

    @Deactivate
    public void deactivate(ComponentContext componentContext) throws IOException {
        bundle = componentContext.getBundleContext().getBundle();

        Files.walk(Paths.get(modelRoot.getAbsolutePath()))
                .map(Path::toFile)
                .sorted(Comparator.reverseOrder())
                .forEach(File::delete);
    }


    public File getModelRoot() {
        return modelRoot;
    }

}
