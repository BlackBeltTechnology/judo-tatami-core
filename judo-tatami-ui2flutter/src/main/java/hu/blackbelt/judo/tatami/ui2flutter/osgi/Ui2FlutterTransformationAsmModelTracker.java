package hu.blackbelt.judo.tatami.ui2flutter.osgi;

import hu.blackbelt.judo.meta.ui.runtime.UiModel;
import hu.blackbelt.judo.tatami.core.AbstractModelTracker;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.BundleException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.*;


@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Slf4j
public class Ui2FlutterTransformationAsmModelTracker extends AbstractModelTracker<UiModel> {

    @Reference
    Ui2FlutterTransformationService ui2FlutterTransformationService;

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
    public void install(UiModel uiModel) {
        try {
            ui2FlutterTransformationService.install(uiModel, componentContext.getBundleContext());
        } catch (Exception e) {
            log.error("Could not register UI Bundle: " + uiModel.getName(), e);
        }
    }

    @Override
    public void uninstall(UiModel uiModel) {
        try {
            ui2FlutterTransformationService.uninstall(uiModel);
        } catch (BundleException e) {
            log.error("Could not unregister UI Bundle: " + uiModel.getName(), e);
        }
    }

    @Override
    public Class<UiModel> getModelClass() {
        return UiModel.class;
    }
}
