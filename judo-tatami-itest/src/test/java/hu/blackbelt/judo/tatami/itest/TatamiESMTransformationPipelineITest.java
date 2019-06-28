package hu.blackbelt.judo.tatami.itest;

import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import org.osgi.framework.InvalidSyntaxException;

import javax.inject.Inject;
import java.io.*;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModelLoader.getAsmDefaultSaveOptions;

public abstract class TatamiESMTransformationPipelineITest extends TatamiPSMTransformationPipelineITest {

    @Inject
    protected EsmModel esmModel;

    public void saveModels() throws InvalidSyntaxException, IOException {
        super.saveModels();

        esmModel.getResourceSet().getResource(esmModel.getUri(), false)
                .save(new FileOutputStream(new File("itest-" + getAppName() + "-esm.model")), getAsmDefaultSaveOptions());
    }
}