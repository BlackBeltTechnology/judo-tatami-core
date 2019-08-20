package hu.blackbelt.judo.tatami.asm2openapi.osgi;

import com.google.common.collect.Maps;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.execution.impl.StringBuilderLogger;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel;
import hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPITransformationTrace;
import hu.blackbelt.judo.tatami.core.TransformationTrace;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;

import java.util.Hashtable;
import java.util.Map;

import static hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel.buildOpenapiModel;
import static hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPI.executeAsm2OpenAPITransformation;

@Component(immediate = true, service = Asm2OpenAPITransformationService.class)
@Slf4j
public class Asm2OpenAPITransformationService {

    Map<AsmModel, ServiceRegistration<TransformationTrace>> asm2openAPITransformationTraceRegistration = Maps.newHashMap();

    BundleContext bundleContext;

    @Activate
    public void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }


    public OpenapiModel install(AsmModel asmModel) throws Exception {
        OpenapiModel openapiModel = buildOpenapiModel()
                .name(asmModel.getName())
                .version(asmModel.getVersion())
                .uri(URI.createURI("openapi:" + asmModel.getName() + ".model"))
                .checksum(asmModel.getChecksum())
                .tags(asmModel.getTags())
                .build();

        Log logger = new StringBuilderLogger(Slf4jLog.determinateLogLevel(log));
        try {
            java.net.URI scriptUri =
                    bundleContext.getBundle()
                            .getEntry("/tatami/asm2openapi/transformations/openapi/asmToOpenAPI.etl")
                            .toURI()
                            .resolve(".");

            Asm2OpenAPITransformationTrace asm2OpenAPITransformationTrace = executeAsm2OpenAPITransformation(
                    asmModel,
                    openapiModel,
                    logger,
                    scriptUri);

            asm2openAPITransformationTraceRegistration.put(asmModel, bundleContext.registerService(
                            TransformationTrace.class, asm2OpenAPITransformationTrace, new Hashtable<>()));

            log.info("\u001B[33m {}\u001B[0m", logger.getBuffer());
        } catch (Exception e) {
            log.info("\u001B[31m {}\u001B[0m", logger.getBuffer());
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
