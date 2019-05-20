package hu.blackbelt.judo.tatami.asm2sdk;

import com.google.common.collect.Maps;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.File;
import java.util.Map;

@Component(immediate = true, service = Asm2SDKService.class)
@Slf4j
public class Asm2SDKService {

    @Reference
    Asm2SDKScriptResource asm2SDKScriptResource;

    Map<AsmModel, Bundle> asm2jaxrsAPIBundles = Maps.newHashMap();

    public void install(AsmModel asmModel, BundleContext bundleContext) throws Exception {
        asm2jaxrsAPIBundles.put(asmModel,
                bundleContext.installBundle("asm2jaxrsapi",
                        Asm2SDK.executeAsm2SDKGeneration(new ResourceSetImpl(), asmModel, new Slf4jLog(log),
                                new File(asm2SDKScriptResource.getSctiptRoot().getAbsolutePath(), "asm2sdk/templates"), new File(""))));

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
