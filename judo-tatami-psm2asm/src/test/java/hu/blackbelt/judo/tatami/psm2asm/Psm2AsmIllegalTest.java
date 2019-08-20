package hu.blackbelt.judo.tatami.psm2asm;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.NameMappedURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.NioFilesystemnRelativePathURIHandlerImpl;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.resource.URIHandler;
import org.eclipse.epsilon.common.util.UriUtil;
import org.hamcrest.CoreMatchers;
import org.hamcrest.MatcherAssert;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.nio.file.FileSystems;

import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.validatePsm;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.executePsm2AsmTransformation;
import static org.hamcrest.CoreMatchers.containsString;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class Psm2AsmIllegalTest {

    public static final String ASM_NORTHWIND = "asm:northwind";
    public static final String PSM_NORTHWIND = "psm:northwind";
    public static final String URN_NORTHWIND_PSM_MODEL = "urn:northwind-illegal-psm.model";
    public static final String URN_NORTHWIND_ASM = "urn:northwind-illegal-asm.model";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";
    public static final String NORTHWIND = "northwind";

    URIHandler uriHandler;
    Log slf4jlog;
    PsmModel psmModel;
    AsmModel asmModel;

    @BeforeEach
    public void setUp() throws Exception {

        // Set our custom handler
        uriHandler = new NameMappedURIHandlerImpl(
                ImmutableList.of(new NioFilesystemnRelativePathURIHandlerImpl("urn",
                        FileSystems.getDefault(), TARGET_TEST_CLASSES)),
                ImmutableMap.of(
                        URI.createURI(PSM_NORTHWIND), URI.createURI(URN_NORTHWIND_PSM_MODEL),
                        URI.createURI(ASM_NORTHWIND), URI.createURI(URN_NORTHWIND_ASM))
        );

        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading PSM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        psmModel = PsmModel.loadPsmModel(PsmModel.LoadArguments.psmLoadArgumentsBuilder()
                .uri(URI.createURI(PSM_NORTHWIND))
                .uriHandler(uriHandler)
                .name(NORTHWIND)
                .build());

        // TODO: Check it is valid or not as metamodel?
        assertTrue(psmModel.isValid());

        // TODO: Check semantic check is Okay?
        validatePsm(new Slf4jLog(log), psmModel,
                UriUtil.resolve(".", PsmModel.class.getClassLoader().getResource("validations/psm.evl").toURI()));

        asmModel = AsmModel.buildAsmModel()
                .name(NORTHWIND)
                .uri(URI.createURI(ASM_NORTHWIND))
                .build();
    }

    @Test
    public void testPsm2AsmTransformation() throws Exception {
        // Make transformation which returns the trace with the serialized URI's
        Psm2AsmTransformationTrace psm2AsmTransformationTrace = executePsm2AsmTransformation(
                psmModel,
                asmModel,
                new Slf4jLog(log),
                new File("src/main/epsilon/transformations/asm").toURI());

        AsmModel.AsmValidationException exception = assertThrows(AsmModel.AsmValidationException.class, () -> {
            asmModel.saveAsmModel();
        });

        assertThat(exception.getMessage(), containsString("Invalid model"));
    }


}