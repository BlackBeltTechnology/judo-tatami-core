package hu.blackbelt.judo.tatami.asm2expression;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.expression.runtime.ExpressionModel;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.tatami.core.workflow.work.AbstractTransformationWork;
import hu.blackbelt.judo.tatami.core.workflow.work.AbstractTransformationWorkConfiguration;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.util.Optional;

import static com.google.common.base.Preconditions.checkArgument;
import static hu.blackbelt.judo.meta.expression.adapters.asm.ExpressionEpsilonValidatorOnAsm.validateExpressionOnAsm;
import static hu.blackbelt.judo.meta.expression.runtime.ExpressionEpsilonValidator.calculateExpressionValidationScriptURI;
import static hu.blackbelt.judo.meta.expression.runtime.ExpressionModel.SaveArguments.expressionSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.expression.runtime.ExpressionModel.buildExpressionModel;
import static hu.blackbelt.judo.tatami.asm2expression.Asm2Expression.executeAsm2Expression;

@Slf4j
public class ValidateExpressionOnAsmWork extends AbstractTransformationWork {

	public ValidateExpressionOnAsmWork(TransformationContext transformationContext) {
		this(AbstractTransformationWorkConfiguration.builder()
				.transformationContext(transformationContext)
				.build());
	}

	public ValidateExpressionOnAsmWork(AbstractTransformationWorkConfiguration configuration) {
		super(configuration);
	}

	@Override
	public void execute() throws Exception {

		Optional<AsmModel> asmModel = getTransformationContext().getByClass(AsmModel.class);
		checkArgument(asmModel.isPresent(), "ASM Model does not found in transformation context");

		Optional<MeasureModel> measureModel = getTransformationContext().getByClass(MeasureModel.class);

		ExpressionModel expressionModel = getTransformationContext().getByClass(ExpressionModel.class)
				.orElseGet(() -> buildExpressionModel().name(asmModel.get().getName()).build());
		getTransformationContext().put(expressionModel);

		executeAsm2Expression(asmModel.get(), measureModel.orElse(null), expressionModel);

		if (configuration.isSaveOutputModel()) {
			final File outputFile = new File(configuration.getTargetDirectory(), expressionModel.getName() + "-expression-" + expressionModel.getVersion() + ".model");
			if (configuration.isDeleteOutputModelOnExit()) {
				outputFile.deleteOnExit();
			}

			expressionModel.saveExpressionModel(expressionSaveArgumentsBuilder()
					.file(outputFile)
					.build());
		}

		if (configuration.isValidate()) {
			validateExpressionOnAsm(getLog(), asmModel.get(), measureModel.get(), expressionModel, calculateExpressionValidationScriptURI());
		}
	}
}
