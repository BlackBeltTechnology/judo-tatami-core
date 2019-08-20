package hu.blackbelt.judo.tatami.psm2asm.osgi;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.tatami.psm2asm.Psm2AsmTransformationTrace;
import lombok.extern.slf4j.Slf4j;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Reference;

import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;

import static hu.blackbelt.judo.tatami.psm2asm.Psm2AsmTransformationTrace.fromModelsAndTrace;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2AsmTransformationTrace.resolvePsm2AsmTrace;

/**
 * This component contains a configuration of a trace model. It will filter and wait for coresponding
 * {@link hu.blackbelt.judo.meta.psm.runtime.PsmModel} and {@link hu.blackbelt.judo.meta.asm.runtime.AsmModel}
 * All of the model required to be able to load and register trace model.
 * When bundle arrives with a trace model it have to wait and match the {@link hu.blackbelt.judo.meta.psm.runtime.PsmModel}
 * and {@link hu.blackbelt.judo.meta.asm.runtime.AsmModel}
 */
//@Component(immediate = true, configurationPolicy = ConfigurationPolicy.REQUIRE)
@Slf4j
public class Psm2AsmTransformationTraceRegistration {


    public static final String MODEL_NAME = "modelName";
    public static final String TRACE_MODEL_STREAM = "traceModelStream";

    @Reference
    AsmModel asmModel;

    @Reference
    PsmModel psmModel;

    @Activate
    public void activate(ComponentContext componentContext) {
        String modelName = (String) componentContext.getProperties().get(MODEL_NAME);
        InputStream traceModelInputStream = (InputStream) componentContext.getProperties().get(TRACE_MODEL_STREAM);

        try {

            Psm2AsmTransformationTrace trace =
                    fromModelsAndTrace(modelName, psmModel, asmModel, traceModelInputStream);

        } catch (IOException e) {
            log.error("Error loading trace model", e);
        }
    }

    public static Dictionary toServiceParameters(String modelName, InputStream traceModel) {
        Dictionary<String, Object> parameters = new Hashtable<>();
        parameters.put(Psm2AsmTransformationTraceRegistration.MODEL_NAME, modelName);
        parameters.put(Psm2AsmTransformationTraceRegistration.TRACE_MODEL_STREAM, traceModel);
        parameters.put("psmModel.target", "(" + PsmModel.NAME + "=" + modelName + ")");
        parameters.put("asmModel.target", "(" + AsmModel.NAME + "=" + modelName + ")");
        return parameters;
    }
}
