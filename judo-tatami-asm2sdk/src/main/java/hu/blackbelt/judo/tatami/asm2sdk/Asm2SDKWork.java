package hu.blackbelt.judo.tatami.asm2sdk;

import hu.blackbelt.judo.tatami.core.workflow.work.AbstractTransformationWork;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import lombok.extern.slf4j.Slf4j;
import java.net.URI;
import java.util.Optional;

import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import static hu.blackbelt.judo.tatami.asm2sdk.Asm2SDK.executeAsm2SDKGeneration;

import java.io.File;
import java.io.InputStream;

@Slf4j
public class Asm2SDKWork extends AbstractTransformationWork {

	public static final String OUTPUT = "asm2SDK:output";

	final URI transformationScriptRoot;

	public Asm2SDKWork(TransformationContext transformationContext, URI transformationScriptRoot) {
		super(transformationContext);
		this.transformationScriptRoot = transformationScriptRoot;
	}

	@Override
	public void execute() throws Exception {

		Optional<AsmModel> asmModel = getTransformationContext().getByClass(AsmModel.class);
		asmModel.orElseThrow(() -> new IllegalArgumentException("ASM Model does not found in transformation context"));

		File temporaryDirectory = File.createTempFile(Asm2SDK.class.getName(), asmModel.get().getName());
		if (temporaryDirectory.exists()) {
			temporaryDirectory.delete();
		}
		temporaryDirectory.deleteOnExit();
		temporaryDirectory.mkdir();

		InputStream asm2SDKBundle = executeAsm2SDKGeneration(asmModel.get(),
				getTransformationContext().getByClass(Log.class).orElseGet(() -> new Slf4jLog(log)),
				transformationScriptRoot, temporaryDirectory);

		getTransformationContext().put(OUTPUT, asm2SDKBundle);

	}
}
