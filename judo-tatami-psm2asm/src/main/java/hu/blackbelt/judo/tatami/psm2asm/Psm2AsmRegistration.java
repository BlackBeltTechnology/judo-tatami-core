package hu.blackbelt.judo.tatami.psm2asm;

import hu.blackbelt.judo.meta.psm.PsmMetaModel;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

@Component(immediate = true)
public class Psm2AsmRegistration {

    @Reference
    PsmMetaModel psmMetaModel;

    @Activate
    public void activate(ComponentContext componentContext) {

    }
}
