package hu.blackbelt.judo.tatami.workflow.osgi;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPITransformationTrace;
import hu.blackbelt.judo.tatami.asm2rdbms.Asm2RdbmsTransformationTrace;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.esm2psm.Esm2PsmTransformationTrace;
import hu.blackbelt.judo.tatami.psm2asm.Psm2AsmTransformationTrace;
import hu.blackbelt.judo.tatami.psm2measure.Psm2MeasureTransformationTrace;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.BundleException;
import org.osgi.service.component.annotations.Component;

import java.io.IOException;
import java.io.InputStream;
import static hu.blackbelt.judo.tatami.core.ThrowingConsumer.throwingConsumerWrapper;

/**
 * This class manages the OSGi lifecycle of transformed models / bundles
 */
@Component(property = "implementation=default", service = TransformationContextRegistrationService.class)
@Slf4j
public class DefaultTransformationContextRegistrationService extends AbstractTransformationContextRegistrationService
        implements TransformationContextRegistrationService {

    public void registerEsmModel(EsmModel esmModel) {
        registerModel(esmModel, esmModel.toDictionary());
    }

    public void unregisterEsmModel(EsmModel esmModel) {
        unregisterModel(esmModel);
    }

    public void registerPsmModel(PsmModel psmModel) {
        registerModel(psmModel, psmModel.toDictionary());
    }

    public void unregisterPsmModel(PsmModel psmModel) {
        unregisterModel(psmModel);
    }


    public void registerAsmModel(AsmModel asmModel) {
        registerModel(asmModel, asmModel.toDictionary());
    }

    public void unregisterAsmModel(AsmModel asmModel) {
        unregisterModel(asmModel);
    }

    public void registerMeasureModel(MeasureModel measureModel) {
        registerModel(measureModel, measureModel.toDictionary());
    }

    public void unregisterMeasureModel(MeasureModel measureModel) {
        unregisterModel(measureModel);
    }


    public void registerRdbmsModel(RdbmsModel rdbmsModel) {
        registerModel(rdbmsModel, rdbmsModel.toDictionary());
    }

    public void unregisterRdbmsModel(RdbmsModel rdbmsModel) {
        unregisterModel(rdbmsModel);
    }

    public void registerLiquibaseModel(LiquibaseModel liquibaseModel) {
        registerModel(liquibaseModel, liquibaseModel.toDictionary());
    }

    public void unregisterLiquibaseModel(LiquibaseModel liquibaseModel) {
        unregisterModel(liquibaseModel);
    }

    public void registerOpenapiModel(OpenapiModel openapiModel) {
        registerModel(openapiModel, openapiModel.toDictionary());
    }

    public void unregisterOpenapiModel(OpenapiModel openapiModel) {
        unregisterModel(openapiModel);
    }

    public void registerJaxrsApi(InputStream jaxrsApiBundle) throws IOException, BundleException {
        registerInputStreamAsBundle(jaxrsApiBundle);
    }

    public void unregisterJaxrsApi(InputStream jaxrsApiBundle) throws BundleException {
        ungisterInputStream(jaxrsApiBundle);
    }

    public void registerSDK(InputStream sdkBundle) throws IOException, BundleException {
        registerInputStreamAsBundle(sdkBundle);
    }

    public void unregisterSDK(InputStream sdkBundle) throws BundleException {
        ungisterInputStream(sdkBundle);
    }

    public void registerTramsformationContext(TransformationContext transformationContext) {
        transformationContext.getByClass(EsmModel.class).ifPresent(m -> registerEsmModel(m));
        transformationContext.getByClass(PsmModel.class).ifPresent(m -> registerPsmModel(m));
        transformationContext.getByClass(AsmModel.class).ifPresent(m -> registerAsmModel(m));
        transformationContext.getByClass(MeasureModel.class).ifPresent(m -> registerMeasureModel(m));
        transformationContext.getByClass(RdbmsModel.class).ifPresent(m -> registerRdbmsModel(m));
        transformationContext.getByClass(OpenapiModel.class).ifPresent(m -> registerOpenapiModel(m));
        transformationContext.getByClass(LiquibaseModel.class).ifPresent(m -> registerLiquibaseModel(m));

        transformationContext.get(InputStream.class, "").ifPresent(throwingConsumerWrapper(m -> registerJaxrsApi(m)));
        transformationContext.get(InputStream.class, "").ifPresent(throwingConsumerWrapper(m -> registerSDK(m)));

        transformationContext.getByClass(Esm2PsmTransformationTrace.class).ifPresent(t -> registerTrace(t));
        transformationContext.getByClass(Psm2AsmTransformationTrace.class).ifPresent(t -> registerTrace(t));
        transformationContext.getByClass(Psm2MeasureTransformationTrace.class).ifPresent(t -> registerTrace(t));
        transformationContext.getByClass(Asm2RdbmsTransformationTrace.class).ifPresent(t -> registerTrace(t));
        transformationContext.getByClass(Asm2OpenAPITransformationTrace.class).ifPresent(t -> registerTrace(t));
    }

    public void unregisterTramsformationContext(TransformationContext transformationContext) {

        transformationContext.getByClass(EsmModel.class).ifPresent(m -> unregisterEsmModel(m));
        transformationContext.getByClass(PsmModel.class).ifPresent(m -> unregisterPsmModel(m));
        transformationContext.getByClass(AsmModel.class).ifPresent(m -> unregisterAsmModel(m));
        transformationContext.getByClass(MeasureModel.class).ifPresent(m -> unregisterMeasureModel(m));
        transformationContext.getByClass(RdbmsModel.class).ifPresent(m -> unregisterRdbmsModel(m));
        transformationContext.getByClass(OpenapiModel.class).ifPresent(m -> unregisterOpenapiModel(m));
        transformationContext.getByClass(LiquibaseModel.class).ifPresent(m -> unregisterLiquibaseModel(m));

        transformationContext.get(InputStream.class, "").ifPresent(throwingConsumerWrapper(m -> unregisterJaxrsApi(m)));
        transformationContext.get(InputStream.class, "").ifPresent(throwingConsumerWrapper(m -> unregisterSDK(m)));

        transformationContext.getByClass(Esm2PsmTransformationTrace.class).ifPresent(t -> unregisterTrace(t));
        transformationContext.getByClass(Psm2AsmTransformationTrace.class).ifPresent(t -> unregisterTrace(t));
        transformationContext.getByClass(Psm2MeasureTransformationTrace.class).ifPresent(t -> unregisterTrace(t));
        transformationContext.getByClass(Asm2RdbmsTransformationTrace.class).ifPresent(t -> unregisterTrace(t));
        transformationContext.getByClass(Asm2OpenAPITransformationTrace.class).ifPresent(t -> unregisterTrace(t));
    }
}
