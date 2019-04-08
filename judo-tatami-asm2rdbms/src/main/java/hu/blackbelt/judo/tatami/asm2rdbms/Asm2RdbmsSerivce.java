package hu.blackbelt.judo.tatami.asm2rdbms;

import com.google.common.collect.Maps;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.osgi.BundleURIHandler;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.tatami.core.TrackInfo;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.File;
import java.util.Hashtable;
import java.util.Map;

import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModelLoader.createRdbmsResourceSet;
import static hu.blackbelt.judo.tatami.asm2rdbms.Asm2Rdbms.executeAsm2RdbmsTransformation;


@Component(immediate = true, service = Asm2RdbmsSerivce.class)
@Slf4j
public class Asm2RdbmsSerivce {

    public static final String RDBMS_META_VERSION_RANGE = "Rdbms-Meta-Version-Range";

    @Reference
    Asm2RdbmsScriptResource asm2RdbmsScriptResource;

    @Reference
    Asm2RdbmsModelResource asm2RdbmsModelResource;

    Map<AsmModel, ServiceRegistration<TrackInfo>> asm2rdbmsTrackInfoRegistration = Maps.newHashMap();

    public RdbmsModel install(AsmModel asmModel, BundleContext bundleContext, String dialect) throws Exception {
        BundleURIHandler bundleURIHandler = new BundleURIHandler("urn", "",
                bundleContext.getBundle());

        ResourceSet rdbmsResourceSet = createRdbmsResourceSet(bundleURIHandler);

        RdbmsModel rdbmsModel = RdbmsModel.buildRdbmsModel()
                .name(asmModel.getName())
                .version(asmModel.getVersion())
                .uri(URI.createURI("urn:" + asmModel.getName() + ".rdbms"))
                .checksum(asmModel.getChecksum())
                .resourceSet(rdbmsResourceSet)
                .checksum(asmModel.getChecksum())
                .metaVersionRange(bundleContext.getBundle().getHeaders().get(RDBMS_META_VERSION_RANGE))
                .build();

        Asm2RdbmsTrackInfo asm2RdbmsTrackInfo = executeAsm2RdbmsTransformation(rdbmsResourceSet, asmModel, rdbmsModel, new Slf4jLog(log),
                new File(asm2RdbmsScriptResource.getSctiptRoot().getAbsolutePath(), "asm2rdbms/transformations"),
                new File(asm2RdbmsModelResource.getModelRoot().getAbsolutePath(), "asm2rdbms-model"), dialect);

        asm2rdbmsTrackInfoRegistration.put(asmModel, bundleContext.registerService(TrackInfo.class, asm2RdbmsTrackInfo, new Hashtable<>()));

        return rdbmsModel;
    }

    public void uninstall(AsmModel asmModel) {
        if (asm2rdbmsTrackInfoRegistration.containsKey(asmModel)) {
            asm2rdbmsTrackInfoRegistration.get(asmModel).unregister();
        } else {
            log.error("ASM model is not installed: " + asmModel.toString());
        }
    }
}
