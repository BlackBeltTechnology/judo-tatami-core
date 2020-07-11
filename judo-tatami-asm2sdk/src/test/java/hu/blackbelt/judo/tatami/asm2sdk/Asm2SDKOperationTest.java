package hu.blackbelt.judo.tatami.asm2sdk;

import com.google.common.io.ByteStreams;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmUtils;
import hu.blackbelt.judo.meta.psm.PsmTestModelBuilder;
import hu.blackbelt.judo.meta.psm.namespace.Model;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EOperation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.stream.Collectors;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.LoadArguments.asmLoadArgumentsBuilder;
import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.SaveArguments.asmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.calculatePsmValidationScriptURI;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.validatePsm;
import static hu.blackbelt.judo.meta.psm.PsmTestModelBuilder.Cardinality.cardinality;
import static hu.blackbelt.judo.meta.psm.data.util.builder.DataBuilders.newOperationBodyBuilder;
import static hu.blackbelt.judo.meta.psm.namespace.util.builder.NamespaceBuilders.newModelBuilder;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.buildPsmModel;
import static hu.blackbelt.judo.meta.psm.service.util.builder.ServiceBuilders.newUnboundOperationBuilder;
import static hu.blackbelt.judo.meta.psm.service.util.builder.ServiceBuilders.newUnmappedTransferObjectTypeBuilder;
import static hu.blackbelt.judo.meta.psm.service.util.builder.ServiceBuilders.*;
import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.buildAsmModel;
import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.loadAsmModel;
import static hu.blackbelt.judo.tatami.asm2sdk.Asm2SDK.calculateAsm2SDKTemplateScriptURI;
import static hu.blackbelt.judo.tatami.asm2sdk.Asm2SDK.executeAsm2SDKGeneration;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.calculatePsm2AsmTransformationScriptURI;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.executePsm2AsmTransformation;
import static org.junit.jupiter.api.Assertions.assertEquals;

@Slf4j
public class Asm2SDKOperationTest {

    public static final String MODEL_NAME = "Test";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";
    public static final String GENERATED_JAVA = "/tmp/judo-sdk/demo";

    private Log slf4jlog;
    private AsmModel asmModel;
	private PsmModel psmModel;

    @BeforeEach
    public void setUp() throws Exception {
        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading PSM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        psmModel = buildPsmModel()
                .name(MODEL_NAME)
                .build();
        
        // Create empty ASM model
        asmModel = buildAsmModel()
                .name(MODEL_NAME)
                .build();
        
        fillPsmModel();

    }

    @Test
    public void testExecuteAsm2SDKGeneration() throws Exception {
        try (OutputStream outputStream =
                     new FileOutputStream(new File(TARGET_TEST_CLASSES, MODEL_NAME + "-sdk.jar"))) {
            ByteStreams.copy(
                    executeAsm2SDKGeneration(asmModel, new Slf4jLog(log),
                            calculateAsm2SDKTemplateScriptURI(),
                            new File(GENERATED_JAVA)),
                    outputStream
            );
        }
    }
    
    private void transform(final String testName) throws Exception {
        psmModel.savePsmModel(PsmModel.SaveArguments.psmSaveArgumentsBuilder()
                .file(new File(TARGET_TEST_CLASSES, getClass().getName() + "-" + testName + "-psm.model"))
                .build());
        validatePsm(new Slf4jLog(log), psmModel, calculatePsmValidationScriptURI());
        executePsm2AsmTransformation(psmModel, asmModel, new Slf4jLog(log), calculatePsm2AsmTransformationScriptURI());
        asmModel.saveAsmModel(asmSaveArgumentsBuilder()
                .file(new File(TARGET_TEST_CLASSES, getClass().getName() + "-" + testName + "-asm.model"))
                .build());
    }

    private void fillPsmModel() throws Exception {
    	PsmTestModelBuilder modelBuilder = new PsmTestModelBuilder();
    	modelBuilder.addEntity("InputEntity").withAttribute("String", "name");
    	modelBuilder.addEntity("RelatedEntity").withAttribute("String", "name");
    	modelBuilder.addEntity("OutputEntity").withAttribute("String", "name");
    	modelBuilder.addEntity("Entity").withAttribute("String", "text")
    		.withAggregation("RelatedEntity", "related", cardinality(0, 1))
    		.withAggregation("RelatedEntity", "relateds", cardinality(0, -1));;
    	modelBuilder.addUnmappedTransferObject("Initializer");
    	
    	// unbound operation
    	modelBuilder.addUnboundOperation("Initializer", "unboundOperationVoid");
    	// unbound operation with single entity input
    	modelBuilder.addUnboundOperation("Initializer", "unboundOperationWithEntityInputVoid")
    		.withInput("InputEntity", "input", cardinality(0, 1));
    	modelBuilder.addUnboundOperation("Initializer", "unboundOperationWithEntityInputSingle")
		.withInput("InputEntity", "input", cardinality(0, 1))
		.withOutput("OutputEntity", cardinality(1,1));
    	modelBuilder.addUnboundOperation("Initializer", "unboundOperationWithEntityInputMulti")
		.withInput("InputEntity", "input", cardinality(0, 1))
		.withOutput("OutputEntity", cardinality(0, -1));
    	modelBuilder.addUnboundOperation("Initializer", "unboundOperationMulti")		
		.withOutput("OutputEntity", cardinality(0, -1));
    	modelBuilder.addUnboundOperation("Initializer", "unboundOperationSingle")		
		.withOutput("OutputEntity", cardinality(1, 1));
    	// unbound operation with multiple entity input
    	modelBuilder.addUnboundOperation("Initializer", "unboundOperationWithEntitiesInputVoid")
    		.withInput("InputEntity", "input", cardinality(0, -1));
    	
    	modelBuilder.addBoundOperation("Entity", "boundOperationVoid");
    	modelBuilder.addBoundOperation("Entity", "boundOperationWithEntityInputVoid")
    		.withInput("InputEntity", "input", cardinality(0, 1));
    	modelBuilder.addBoundOperation("Entity", "boundOperationWithEntityInputSingle")
		.withInput("InputEntity", "input", cardinality(0, 1))
		.withOutput("OutputEntity", cardinality(1,1));
    	modelBuilder.addBoundOperation("Entity", "boundOperationWithEntityInputMulti")
		.withInput("InputEntity", "input", cardinality(0, 1))
		.withOutput("OutputEntity", cardinality(0, -1));
    	modelBuilder.addBoundOperation("Entity", "boundOperationMulti")
		.withOutput("OutputEntity", cardinality(0, -1));
    	modelBuilder.addBoundOperation("Entity", "boundOperationSingle")
		.withOutput("OutputEntity", cardinality(0, 1));    	
    	modelBuilder.addBoundOperation("Entity", "boundOperationWithEntitiesInputVoid")
    		.withInput("InputEntity", "input", cardinality(0, -1));
    	
    	modelBuilder.addUnboundOperation("Initializer", "scriptOperation").withBody("var demo::entities::Entity e");
    	modelBuilder.addActorType("BoundVoidActor", "Entity");
    	modelBuilder.addActorType("InitializerActor", "Initializer");
        psmModel.addContent(modelBuilder.build());
        transform(MODEL_NAME);

    }

}
