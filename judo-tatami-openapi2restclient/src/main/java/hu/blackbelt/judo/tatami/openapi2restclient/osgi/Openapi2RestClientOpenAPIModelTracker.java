package hu.blackbelt.judo.tatami.openapi2restclient.osgi;

import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel;
import hu.blackbelt.judo.tatami.core.AbstractModelTracker;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.BundleException;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.*;


@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Slf4j
public class Openapi2RestClientOpenAPIModelTracker extends AbstractModelTracker<OpenapiModel> {

    @Reference
    Openapi2RestClientService openapi2RestClientService;

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
    public void install(OpenapiModel openapiModel) {
        try {
            openapi2RestClientService.install(openapiModel, componentContext.getBundleContext());
        } catch (Exception e) {
            log.error("Could not register OpenAPI Bundle: " + openapiModel.getName(), e);
        }
    }

    @Override
    public void uninstall(OpenapiModel openapiModel) {
        try {
            openapi2RestClientService.uninstall(openapiModel);
        } catch (BundleException e) {
            log.error("Could not unregister OpenAPI Bundle: " + openapiModel.getName(), e);
        }
    }

    @Override
    public Class<OpenapiModel> getModelClass() {
        return OpenapiModel.class;
    }
}
