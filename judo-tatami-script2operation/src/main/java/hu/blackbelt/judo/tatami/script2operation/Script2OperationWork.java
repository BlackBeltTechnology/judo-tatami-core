package hu.blackbelt.judo.tatami.script2operation;

import hu.blackbelt.judo.meta.script.runtime.ScriptModel;
import hu.blackbelt.judo.tatami.core.workflow.work.AbstractTransformationWork;
import lombok.extern.slf4j.Slf4j;
import java.util.Optional;

import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;

import static com.google.common.base.Preconditions.checkState;
import static hu.blackbelt.judo.tatami.script2operation.Script2Operation.executeScript2OperationGeneration;

import java.io.File;
import java.io.InputStream;

@Slf4j
public class Script2OperationWork extends AbstractTransformationWork {

	public static final String OPERATION_OUTPUT = "script2Operation:output";


	public Script2OperationWork(TransformationContext transformationContext) {
		super(transformationContext);
	}

	@Override
	public void execute() throws Exception {

		Optional<ScriptModel> asmModel = getTransformationContext().getByClass(ScriptModel.class);
		asmModel.orElseThrow(() -> new IllegalArgumentException("Script Model does not found in transformation context"));

		InputStream script2OperationBundle = executeScript2OperationGeneration(asmModel.get());

		checkState(script2OperationBundle != null, "No InputStream created");

		getTransformationContext().put(OPERATION_OUTPUT, script2OperationBundle);

	}
}
