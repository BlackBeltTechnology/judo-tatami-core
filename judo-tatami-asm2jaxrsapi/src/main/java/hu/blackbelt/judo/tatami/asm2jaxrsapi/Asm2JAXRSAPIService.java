package hu.blackbelt.judo.tatami.asm2jaxrsapi;

import com.google.common.collect.Maps;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.tatami.core.TrackInfo;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.ecore.resource.impl.ResourceSetImpl;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.io.File;
import java.util.Map;

import static hu.blackbelt.judo.tatami.asm2jaxrsapi.Asm2JAXRSAPI.executeAsm2JAXRSAPIGeneration;

@Component(immediate = true, service = Asm2JAXRSAPIService.class)
@Slf4j
public class Asm2JAXRSAPIService {

    public static final String OPENAPI_META_VERSION_RANGE = "OpenAPI-Meta-Version-Range";

    @Reference
    Asm2JAXRSAPIScriptResource asm2JAXRSAPIScriptResource;

    Map<AsmModel, ServiceRegistration<TrackInfo>> asm2openAPITrackInfoRegistration = Maps.newHashMap();

    public void install(AsmModel asmModel, BundleContext bundleContext) throws Exception {
        /*
        BundleURIHandler bundleURIHandler = new BundleURIHandler("urn", "",
                bundleContext.getBundle());

        ResourceSet openAPIResourceSet = createOpenAPIResourceSet(bundleURIHandler);
        registerOpenAPIMetamodel(openAPIResourceSet);

        OpenAPIModel openAPIModel = OpenAPIModel.buildOpenAPIModel()
                .name(asmModel.getName())
                .version(asmModel.getVersion())
                .uri(URI.createURI("urn:" + asmModel.getName() + ".openapi"))
                .checksum(asmModel.getChecksum())
                .resourceSet(openAPIResourceSet)
                .checksum(asmModel.getChecksum())
                .metaVersionRange(bundleContext.getBundle().getHeaders().get(OPENAPI_META_VERSION_RANGE))
                .build();
        */
        executeAsm2JAXRSAPIGeneration(new ResourceSetImpl(), asmModel, new Slf4jLog(log),
                new File(asm2JAXRSAPIScriptResource.getSctiptRoot().getAbsolutePath(), "asm2jaxrsapi/templates/asm2jaxrsapi"), new File(""));

    }

    public void uninstall(AsmModel asmModel) {
        if (asm2openAPITrackInfoRegistration.containsKey(asmModel)) {
            asm2openAPITrackInfoRegistration.get(asmModel).unregister();
        } else {
            log.error("ASM model is not installed: " + asmModel.toString());
        }
    }
}
