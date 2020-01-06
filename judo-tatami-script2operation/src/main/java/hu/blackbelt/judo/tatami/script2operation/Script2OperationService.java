package hu.blackbelt.judo.tatami.script2operation;

import com.google.common.collect.Maps;
import hu.blackbelt.judo.meta.script.runtime.ScriptModel;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.io.File;
import java.util.Map;

import static hu.blackbelt.judo.tatami.script2operation.Script2Operation.executeScript2OperationGeneration;

@Component(immediate = true, service = Script2OperationService.class)
@Slf4j
public class Script2OperationService {

    Map<ScriptModel, Bundle> script2OperationBundles = Maps.newHashMap();

    BundleContext bundleContext;

    @Activate
    public void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }


    public void install(ScriptModel scriptModel, BundleContext bundleContext) throws Exception {
        try {
            script2OperationBundles.put(scriptModel,
                    bundleContext.installBundle(this.getClass().getName(),
                            executeScript2OperationGeneration(scriptModel, new File(""))));
        } catch (Exception e) {
            throw e;
        }
        script2OperationBundles.get(scriptModel).start();
    }

    public void uninstall(ScriptModel scriptModel) throws BundleException {
        if (script2OperationBundles.containsKey(scriptModel)) {
            script2OperationBundles.get(scriptModel).uninstall();
        } else {
            log.error("Script model is not installed: " + scriptModel.toString());
        }
    }
}
