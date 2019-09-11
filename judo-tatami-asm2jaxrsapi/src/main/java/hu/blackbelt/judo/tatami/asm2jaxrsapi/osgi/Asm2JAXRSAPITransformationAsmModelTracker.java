package hu.blackbelt.judo.tatami.asm2jaxrsapi.osgi;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.tatami.core.AbstractModelTracker;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.BundleException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.*;


@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Slf4j
public class Asm2JAXRSAPITransformationAsmModelTracker extends AbstractModelTracker<AsmModel> {

    @Reference
    Asm2JAXRSAPITransformationService asm2JAXRSAPITransformationService;

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
            asm2JAXRSAPITransformationService.install(asmModel, componentContext.getBundleContext());
        } catch (Exception e) {
            log.error("Could not register JAX-RS Bundle: " + asmModel.getName(), e);
        }
    }

    @Override
    public void uninstall(AsmModel asmModel) {
        try {
            asm2JAXRSAPITransformationService.uninstall(asmModel);
        } catch (BundleException e) {
            log.error("Could not unregister JAX-RS Bundle: " + asmModel.getName(), e);
        }
    }

    @Override
    public Class<AsmModel> getModelClass() {
        return AsmModel.class;
    }
}
