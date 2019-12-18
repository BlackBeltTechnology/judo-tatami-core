package hu.blackelt.judo.tatami.asm2expression;

import hu.blackbelt.judo.meta.expression.runtime.ExpressionModel;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.tatami.core.workflow.work.AbstractTransformationWork;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import lombok.extern.slf4j.Slf4j;
import java.util.Optional;

import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;

import static hu.blackbelt.judo.meta.expression.runtime.ExpressionModel.buildExpressionModel;
import static hu.blackelt.judo.tatami.asm2expression.Asm2Expression.executeAsm2Expression;

import java.io.File;

@Slf4j
public class Asm2ExpressionWork extends AbstractTransformationWork {

	public Asm2ExpressionWork(TransformationContext transformationContext) {
		super(transformationContext);
	}

	@Override
	public void execute() throws Exception {

		Optional<AsmModel> asmModel = getTransformationContext().getByClass(AsmModel.class);
		asmModel.orElseThrow(() -> new IllegalArgumentException("ASM Model does not found in transformation context"));

		Optional<MeasureModel> measureModel = getTransformationContext().getByClass(MeasureModel.class);

		File temporaryDirectory = File.createTempFile(Asm2Expression.class.getName(), asmModel.get().getName());
		if (temporaryDirectory.exists()) {
			temporaryDirectory.delete();
		}
		temporaryDirectory.deleteOnExit();
		temporaryDirectory.mkdir();

		ExpressionModel
				expressionModel = getTransformationContext().getByClass(ExpressionModel.class)
				.orElseGet(() -> buildExpressionModel().name(asmModel.get().getName()).build());
		getTransformationContext().put(expressionModel);

		executeAsm2Expression(asmModel.get(), measureModel.orElse(null), expressionModel);
	}
}
