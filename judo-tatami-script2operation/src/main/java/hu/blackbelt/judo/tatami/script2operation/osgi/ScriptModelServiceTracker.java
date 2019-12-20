package hu.blackbelt.judo.tatami.script2operation.osgi;

import hu.blackbelt.judo.meta.script.runtime.ScriptModel;
import hu.blackbelt.judo.tatami.script2operation.Script2OperationService;
import hu.blackbelt.judo.tatami.core.AbstractModelTracker;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.BundleException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.*;

@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Slf4j
public class ScriptModelServiceTracker extends AbstractModelTracker<ScriptModel> {

    @Reference
    Script2OperationService script2OperationService;

    @Activate
    protected void activate(ComponentContext contextPar) {
        componentContext = contextPar;
        openTracker(contextPar.getBundleContext());
    }

    @Deactivate
    protected void deactivate() {
        closeTracker();
    }

    private ComponentContext componentContext;

    @Override
    public void install(ScriptModel scriptModel) {
        try {
            script2OperationService.install(scriptModel, componentContext.getBundleContext());
        } catch (Exception e) {
            log.error("Could not register Operation Bundle: " + scriptModel.getName(), e);
        }
    }

    @Override
    public void uninstall(ScriptModel scriptModel) {
        try {
            script2OperationService.uninstall(scriptModel);
        } catch (BundleException e) {
            log.error("Could not unregister Operation Bundle: " + scriptModel.getName(), e);
        }
    }

    @Override
    public Class<ScriptModel> getModelClass() {
        return ScriptModel.class;
    }
}
