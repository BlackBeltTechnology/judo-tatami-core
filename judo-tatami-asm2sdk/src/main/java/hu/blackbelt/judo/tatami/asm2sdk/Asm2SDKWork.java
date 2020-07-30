package hu.blackbelt.judo.tatami.asm2sdk;

import hu.blackbelt.judo.tatami.core.workflow.work.AbstractTransformationWork;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import lombok.extern.slf4j.Slf4j;
import java.net.URI;
import java.util.Collection;
import java.util.Optional;

import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;

import static com.google.common.base.Preconditions.checkState;
import static hu.blackbelt.judo.tatami.asm2sdk.Asm2SDK.executeAsm2SDKGeneration;

import java.io.File;
import java.io.InputStream;

@Slf4j
public class Asm2SDKWork extends AbstractTransformationWork {

	public static final String SDK_OUTPUT = "asm2SDK:output";
	public static final String SDK_OUTPUT_INTERNAL = "asm2SDK:output-internal";

	final URI transformationScriptRoot;

	public Asm2SDKWork(TransformationContext transformationContext, URI transformationScriptRoot) {
		super(transformationContext);
		this.transformationScriptRoot = transformationScriptRoot;
	}

	public Asm2SDKWork(TransformationContext transformationContext) {
		this(transformationContext, Asm2SDK.calculateAsm2SDKTemplateScriptURI());
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

		Asm2SDKBundleStreams bundleStreams = executeAsm2SDKGeneration(asmModel.get(),
				getTransformationContext().getByClass(Log.class).orElseGet(() -> new Slf4jLog(log)),
				transformationScriptRoot,
				temporaryDirectory);

		checkState(bundleStreams != null, "No InputStream created");
		getTransformationContext().put(SDK_OUTPUT, bundleStreams.getSdkBundleStream());
		getTransformationContext().put(SDK_OUTPUT_INTERNAL, bundleStreams.getInternalBundleStream());
	}
}
