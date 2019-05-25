package hu.blackbelt.judo.tatami.asm2openapi;

import com.google.common.collect.Maps;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.execution.impl.StringBuilderLogger;
import hu.blackbelt.epsilon.runtime.osgi.BundleURIHandler;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.openapi.runtime.OpenAPIModel;
import hu.blackbelt.judo.tatami.core.TransformationTrace;
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

import static hu.blackbelt.judo.meta.openapi.runtime.OpenAPIModelLoader.createOpenAPIResourceSet;
import static hu.blackbelt.judo.meta.openapi.runtime.OpenAPIModelLoader.registerOpenAPIMetamodel;
import static hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPI.executeAsm2OpenAPITransformation;

@Component(immediate = true, service = Asm2OpenAPIService.class)
@Slf4j
public class Asm2OpenAPIService {

    public static final String OPENAPI_META_VERSION_RANGE = "OpenAPI-Meta-Version-Range";

    @Reference
    Asm2OpenAPIScriptResource asm2OpenAPIScriptResource;

    Map<AsmModel, ServiceRegistration<TransformationTrace>> asm2openAPITransformationTraceRegistration = Maps.newHashMap();

    public OpenAPIModel install(AsmModel asmModel, BundleContext bundleContext) throws Exception {
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

        Log logger = new StringBuilderLogger(Slf4jLog.determinateLogLevel(log));
        try {
            Asm2OpenAPITransformationTrace asm2OpenAPITransformationTrace = executeAsm2OpenAPITransformation(openAPIResourceSet, asmModel, openAPIModel, logger,
                    new File(asm2OpenAPIScriptResource.getSctiptRoot().getAbsolutePath(), "asm2openapi/transformations/openapi"));

            asm2openAPITransformationTraceRegistration.put(asmModel, bundleContext.registerService(TransformationTrace.class, asm2OpenAPITransformationTrace, new Hashtable<>()));
            log.info(logger.getBuffer());
        } catch (Exception e) {
            log.error(logger.getBuffer());
            throw e;
        }
        return openAPIModel;
    }

    public void uninstall(AsmModel asmModel) {
        if (asm2openAPITransformationTraceRegistration.containsKey(asmModel)) {
            asm2openAPITransformationTraceRegistration.get(asmModel).unregister();
        } else {
            log.error("ASM model is not installed: " + asmModel.toString());
        }
    }
}
