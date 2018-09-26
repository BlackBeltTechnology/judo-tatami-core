package hu.blackbelt.judo.tatami.asm2rdbms;

import hu.blackbelt.osgi.utils.osgi.api.BundleUtil;
import org.osgi.framework.Bundle;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.io.File;
import java.io.IOException;

@Component(immediate = true, service = Asm2RdbmsResourceLoader.class)
public class Asm2RdbmsResourceLoader {

    Bundle bundle;
    File scriptRoot;

    @Activate
    public void activate(ComponentContext componentContext) throws IOException {
        bundle = componentContext.getBundleContext().getBundle();

        scriptRoot = BundleUtil.copyBundlePathToPersistentStorage(
                bundle, "asm2rdbms", "tatami/asm2rdbms", "*", true);
    }

    public File getSctiptRoot() {
        return scriptRoot;
    }

}
