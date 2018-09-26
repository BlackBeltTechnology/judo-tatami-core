package hu.blackbelt.judo.tatami.psm2asm;

import hu.blackbelt.osgi.utils.osgi.api.BundleUtil;
import org.osgi.framework.Bundle;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.io.File;
import java.io.IOException;

@Component(immediate = true, service = Psm2AsmResourceLoader.class)
public class Psm2AsmResourceLoader {

    Bundle bundle;
    File scriptRoot;

    @Activate
    public void activate(ComponentContext componentContext) throws IOException {
        bundle = componentContext.getBundleContext().getBundle();

        scriptRoot = BundleUtil.copyBundlePathToPersistentStorage(
                bundle, "psm2asm", "tatami/psm2asm", "*", true);
    }

    public File getSctiptRoot() {
        return scriptRoot;
    }

}
