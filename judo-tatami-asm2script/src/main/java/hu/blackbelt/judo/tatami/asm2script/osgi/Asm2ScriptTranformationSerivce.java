package hu.blackbelt.judo.tatami.asm2script.osgi;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.epsilon.runtime.execution.impl.StringBuilderLogger;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.expression.runtime.ExpressionModel;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.osgi.service.component.annotations.Component;

import static hu.blackbelt.judo.tatami.asm2script.Asm2Script.executeAsm2Expression;

@Component(immediate = true, service = Asm2ScriptTranformationSerivce.class)
@Slf4j

public class Asm2ScriptTranformationSerivce {

    public ExpressionModel install(AsmModel asmModel, MeasureModel measureModel) {
        ExpressionModel expressionModel = ExpressionModel.buildExpressionModel()
                .name(asmModel.getName())
                .version(asmModel.getVersion())
                .uri(URI.createURI("expression:" + asmModel.getName() + ".model"))
                .checksum(asmModel.getChecksum())
                .tags(asmModel.getTags())
                .build();

        Log logger = new StringBuilderLogger(Slf4jLog.determinateLogLevel(log));

        try {

            executeAsm2Expression(asmModel, measureModel, expressionModel);

            log.info("\u001B[33m {}\u001B[0m", logger.getBuffer());
        } catch (Exception e) {
            log.info("\u001B[31m {}\u001B[0m", logger.getBuffer());
            throw e;
        }
        return expressionModel;
    }
}
