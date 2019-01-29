package hu.blackbelt.judo.tatami.psm2measure;

import hu.blackbelt.judo.meta.psm.PsmModelInfo;
import hu.blackbelt.judo.tatami.core.AbstractModelInfoTracker;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true)
public class PsmAbstractModelInfoServiceTracker extends AbstractModelInfoTracker<PsmModelInfo> {

    @Reference
    Psm2MeasureTransformation psm2MeasureTransformation;

    @Activate
    protected void activate(BundleContext contextPar) {
        openTracker(contextPar);
    }

    @Deactivate
    protected void deactivate() {
        closeTracker();
    }


    @Override
    public void install(PsmModelInfo instance) {
        psm2MeasureTransformation.install(instance);
    }

    @Override
    public void uninstall(PsmModelInfo instance) {
        psm2MeasureTransformation.uninstall(instance);
    }

    @Override
    public Class<PsmModelInfo> getModelInfoClass() {
        return PsmModelInfo.class;
    }


}
