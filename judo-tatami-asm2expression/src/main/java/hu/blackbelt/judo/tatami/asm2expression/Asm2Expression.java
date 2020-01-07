package hu.blackbelt.judo.tatami.asm2expression;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.expression.builder.jql.JqlExtractor;
import hu.blackbelt.judo.meta.expression.builder.jql.asm.AsmJqlExtractor;
import hu.blackbelt.judo.meta.expression.runtime.ExpressionModel;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.measure.support.MeasureModelResourceSupport;
import org.eclipse.emf.ecore.resource.ResourceSet;

public class Asm2Expression {

    public static void executeAsm2Expression(AsmModel asmModel, MeasureModel measureModel, ExpressionModel expressionModel) {
        ResourceSet measureResourceSet;
        if (measureModel == null) {
            measureResourceSet = MeasureModelResourceSupport.createMeasureResourceSet();
        } else  {
            measureResourceSet = measureModel.getResourceSet();
        }

        JqlExtractor jqlExtractor = new AsmJqlExtractor(asmModel.getResourceSet(), measureResourceSet, expressionModel.getResourceSet());
        jqlExtractor.extractExpressions();
    }

}


