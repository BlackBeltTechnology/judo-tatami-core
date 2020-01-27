package hu.blackbelt.judo.tatami.asm2script;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.measure.support.MeasureModelResourceSupport;
import hu.blackbelt.judo.meta.script.builder.jcl.JclExtractor;
import hu.blackbelt.judo.meta.script.builder.jcl.asm.AsmJclExtractor;
import hu.blackbelt.judo.meta.script.runtime.ScriptModel;
import org.eclipse.emf.ecore.resource.ResourceSet;

public class Asm2Script {

    public static void executeAsm2Script(AsmModel asmModel, MeasureModel measureModel, ScriptModel scriptModel) {
        ResourceSet measureResourceSet;
        if (measureModel == null) {
            measureResourceSet = MeasureModelResourceSupport.createMeasureResourceSet();
        } else  {
            measureResourceSet = measureModel.getResourceSet();
        }

        JclExtractor jclExtractor = new AsmJclExtractor(asmModel.getResourceSet(), measureResourceSet, scriptModel.getResourceSet());
        jclExtractor.extractScripts();
    }

}


