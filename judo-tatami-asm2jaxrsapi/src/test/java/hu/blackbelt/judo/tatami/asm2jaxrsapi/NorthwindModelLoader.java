package hu.blackbelt.judo.tatami.asm2jaxrsapi;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.model.northwind.Demo;
import lombok.Getter;
import lombok.extern.slf4j.Slf4j;

import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.calculatePsm2AsmTransformationScriptURI;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.executePsm2AsmTransformation;

@Slf4j
public class NorthwindModelLoader {

    @Getter
    private AsmModel asmModel;

    private NorthwindModelLoader(final String modelName) throws Exception {
        // Default logger
        final Log slf4jlog = new Slf4jLog(log);

        final PsmModel psmModel = new Demo().fullDemo();

        // Create empty RDBMS model
        asmModel = AsmModel.buildAsmModel()
                .name(modelName)
                .build();

        executePsm2AsmTransformation(psmModel, asmModel, slf4jlog, calculatePsm2AsmTransformationScriptURI());
    }

    public static NorthwindModelLoader createNorthwindModelLoader(final String modelName) throws Exception {
        return new NorthwindModelLoader(modelName);
    }
}
