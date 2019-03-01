package hu.blackbelt.judo.tatami.rdbms2liquibase;

import hu.blackbelt.judo.meta.rdbms.RdbmsModelInfo;
import hu.blackbelt.judo.tatami.core.AbstractModelInfoTracker;
import org.osgi.framework.BundleContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true)
public class RdbmsModelInfoTracker extends AbstractModelInfoTracker<RdbmsModelInfo> {

    @Reference
    Rdbms2liquibaseTransformation rdbms2liquibaseTransformation;

    @Activate
    protected void activate(BundleContext contextPar) {
        openTracker(contextPar);
    }

    @Deactivate
    protected void deactivate() {
        closeTracker();
    }

    @Override
    public void install(RdbmsModelInfo instance) {
        rdbms2liquibaseTransformation.install(instance);
    }

    @Override
    public void uninstall(RdbmsModelInfo instance) {
        rdbms2liquibaseTransformation.uninstall(instance);
    }

    @Override
    public Class<RdbmsModelInfo> getModelInfoClass() {
        return RdbmsModelInfo.class;
    }

}
