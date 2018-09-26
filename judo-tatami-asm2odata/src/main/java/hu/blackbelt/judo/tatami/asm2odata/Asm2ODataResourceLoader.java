package hu.blackbelt.judo.tatami.asm2odata;

import hu.blackbelt.osgi.utils.osgi.api.BundleUtil;
import org.osgi.framework.Bundle;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.io.File;
import java.io.IOException;

@Component(immediate = true, service = Asm2ODataResourceLoader.class)
public class Asm2ODataResourceLoader {

    Bundle bundle;
    File scriptRoot;

    @Activate
    public void activate(ComponentContext componentContext) throws IOException {
        bundle = componentContext.getBundleContext().getBundle();

        scriptRoot = BundleUtil.copyBundlePathToPersistentStorage(
                bundle, "asm2odata", "tatami/asm2odata", "*", true);
    }

    public File getSctiptRoot() {
        return scriptRoot;
    }

}
