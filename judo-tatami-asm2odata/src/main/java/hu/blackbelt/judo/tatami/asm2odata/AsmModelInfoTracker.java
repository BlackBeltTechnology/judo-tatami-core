package hu.blackbelt.judo.tatami.asm2odata;

import hu.blackbelt.judo.meta.asm.AsmModelInfo;
import hu.blackbelt.judo.tatami.core.AbstractModelInfoTracker;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;


@Component(immediate = true)
public class AsmModelInfoTracker extends AbstractModelInfoTracker<AsmModelInfo> {

    @Reference
    Asm2OdataTransformation asm2OdataTransformation;

    @Activate
    protected void activate(BundleContext contextPar) {
        openTracker(contextPar);
    }

    @Deactivate
    protected void deactivate() {
        closeTracker();
    }

    @Override
    public void install(AsmModelInfo instance) {
        asm2OdataTransformation.install(instance);
    }

    @Override
    public void uninstall(AsmModelInfo instance) {
        asm2OdataTransformation.uninstall(instance);
    }

    @Override
    public Class<AsmModelInfo> getModelInfoClass() {
        return AsmModelInfo.class;
    }

}
