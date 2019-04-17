package hu.blackbelt.judo.tatami.asm2jaxrsapi;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.tatami.core.AbstractModelInfoTracker;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.BundleException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;


@Component(immediate = true)
@Slf4j
public class AsmModelServiceTracker extends AbstractModelInfoTracker<AsmModel> {

    @Reference
    Asm2JAXRSAPIService asm2JAXRSAPIService;

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
    public void install(AsmModel asmModel) {
        try {
            asm2JAXRSAPIService.install(asmModel, componentContext.getBundleContext());
        } catch (Exception e) {
            log.error("Could not register JAX-RS Bundle: " + asmModel.getName(), e);
        }
    }

    @Override
    public void uninstall(AsmModel asmModel) {
        try {
            asm2JAXRSAPIService.uninstall(asmModel);
        } catch (BundleException e) {
            log.error("Could not unregister JAX-RS Bundle: " + asmModel.getName(), e);
        }
    }

    @Override
    public Class<AsmModel> getModelClass() {
        return AsmModel.class;
    }
}
