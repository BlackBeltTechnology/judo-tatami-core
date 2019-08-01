package hu.blackbelt.judo.tatami.asm2openapi;

import com.google.common.collect.Maps;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.execution.impl.StringBuilderLogger;
import hu.blackbelt.epsilon.runtime.osgi.BundleURIHandler;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel;
import hu.blackbelt.judo.tatami.core.TransformationTrace;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.util.Hashtable;
import java.util.Map;
import java.util.Optional;

import static hu.blackbelt.judo.meta.openapi.support.OpenapiModelResourceSupport.openapiModelResourceSupportBuilder;
import static hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPI.executeAsm2OpenAPITransformation;

@Component(immediate = true, service = Asm2OpenAPIService.class)
@Slf4j
public class Asm2OpenAPIService {

    public static final String OPENAPI_META_VERSION_RANGE = "OpenAPI-Meta-Version-Range";

    Map<AsmModel, ServiceRegistration<TransformationTrace>> asm2openAPITransformationTraceRegistration = Maps.newHashMap();

    BundleContext scriptBundleContext;

    @Activate
    public void activate(BundleContext bundleContext) {
        scriptBundleContext = bundleContext;
    }


    public OpenapiModel install(AsmModel asmModel, BundleContext bundleContext) throws Exception {
        BundleURIHandler bundleURIHandler = new BundleURIHandler("urn", "",
                bundleContext.getBundle());

        OpenapiModel openapiModel = OpenapiModel.buildOpenapiModel()
                .name(asmModel.getName())
                .version(asmModel.getVersion())
                .uri(URI.createURI("urn:" + asmModel.getName() + ".openapi"))
                .checksum(asmModel.getChecksum())
                .openapiModelResourceSupport(
                        openapiModelResourceSupportBuilder()
                                .uriHandler(Optional.of(bundleURIHandler))
                                .build())
                .metaVersionRange(bundleContext.getBundle().getHeaders().get(OPENAPI_META_VERSION_RANGE)).build();



        Log logger = new StringBuilderLogger(Slf4jLog.determinateLogLevel(log));
        try {
            java.net.URI scriptUri =
                    scriptBundleContext.getBundle()
                            .getEntry("/tatami/asm2openapi/transformations/openapi/asmToOpenAPI.etl")
                            .toURI()
                            .resolve(".");


            Asm2OpenAPITransformationTrace asm2OpenAPITransformationTrace = executeAsm2OpenAPITransformation(openapiModel.getResourceSet(), asmModel, openapiModel, logger,
                    scriptUri);

            asm2openAPITransformationTraceRegistration.put(asmModel, bundleContext.registerService(TransformationTrace.class, asm2OpenAPITransformationTrace, new Hashtable<>()));
            log.info(logger.getBuffer());
        } catch (Exception e) {
            log.error(logger.getBuffer());
            throw e;
        }
        return openapiModel;
    }

    public void uninstall(AsmModel asmModel) {
        if (asm2openAPITransformationTraceRegistration.containsKey(asmModel)) {
            asm2openAPITransformationTraceRegistration.get(asmModel).unregister();
        } else {
            log.error("ASM model is not installed: " + asmModel.toString());
        }
    }
}
