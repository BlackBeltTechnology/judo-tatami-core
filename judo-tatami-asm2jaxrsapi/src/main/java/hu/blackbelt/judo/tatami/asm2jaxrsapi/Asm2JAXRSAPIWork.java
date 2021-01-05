package hu.blackbelt.judo.tatami.asm2jaxrsapi;

import static com.google.common.base.Preconditions.checkState;
import static hu.blackbelt.judo.tatami.asm2jaxrsapi.Asm2JAXRSAPI.executeAsm2JAXRSAPIGeneration;

import java.io.File;
import java.io.InputStream;
import java.net.URI;
import java.util.Optional;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.tatami.core.workflow.work.AbstractTransformationWork;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Asm2JAXRSAPIWork extends AbstractTransformationWork {
	
	public static final String JAXRSAPI_OUTPUT = "asm2JAXRSAPI:output";

	final URI transformationScriptRoot;

	public Asm2JAXRSAPIWork(TransformationContext transformationContext, URI transformationScriptRoot) {
		super(transformationContext);
		this.transformationScriptRoot = transformationScriptRoot;
	}

	public Asm2JAXRSAPIWork(TransformationContext transformationContext) {
		this(transformationContext, Asm2JAXRSAPI.calculateAsm2JaxrsapiTemplateScriptURI());
	}

	@Override
	public void execute() throws Exception {

		Optional<AsmModel> asmModel = getTransformationContext().getByClass(AsmModel.class);
		asmModel.orElseThrow(() -> new IllegalArgumentException("ASM Model does not found in transformation context"));

		File temporaryDirectory = File.createTempFile(Asm2JAXRSAPI.class.getName(), asmModel.get().getName());
		if (temporaryDirectory.exists()) {
			temporaryDirectory.delete();
		}
		temporaryDirectory.deleteOnExit();
		temporaryDirectory.mkdir();

		InputStream asm2JAXRSBundle = executeAsm2JAXRSAPIGeneration(asmModel.get(),
				getTransformationContext().getByClass(Log.class).orElseGet(() -> new Slf4jLog(log)),
				transformationScriptRoot,
				temporaryDirectory,
				metricsCollector);
		
		checkState(asm2JAXRSBundle != null, "No InputStream created");

		getTransformationContext().put(JAXRSAPI_OUTPUT, asm2JAXRSBundle);

	}

}
