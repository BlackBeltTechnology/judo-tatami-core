package hu.blackbelt.judo.tatami.asm2rdbms;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.File;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.SaveArguments.asmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.buildAsmModel;
import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel.SaveArguments.rdbmsSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel.buildRdbmsModel;
import static hu.blackbelt.judo.meta.rdbmsDataTypes.support.RdbmsDataTypesModelResourceSupport.registerRdbmsDataTypesMetamodel;
import static hu.blackbelt.judo.meta.rdbmsNameMapping.support.RdbmsNameMappingModelResourceSupport.registerRdbmsNameMappingMetamodel;
import static hu.blackbelt.judo.meta.rdbmsRules.support.RdbmsTableMappingRulesModelResourceSupport.registerRdbmsTableMappingRulesMetamodel;
import static hu.blackbelt.judo.tatami.asm2rdbms.Asm2Rdbms.*;
import static org.eclipse.emf.ecore.util.builder.EcoreBuilders.*;

@Slf4j
public class Asm2RdbmsTypeMappingTest {
    private static String ASM_MODEL_NAME = "TestAsmModel";
    private static String RDBMS_MODEL_NAME = "TestRdbmsModel";

    private static String TARGET_TEST_CLASSES = "target/test-classes";

    Slf4jLog logger;

    private AsmModel asmModel;
    private RdbmsModel rdbmsModel;

    @BeforeEach
    public void setUp() {
        logger = new Slf4jLog(log);

        asmModel = buildAsmModel().name(ASM_MODEL_NAME).build();
        rdbmsModel = buildRdbmsModel().name(RDBMS_MODEL_NAME).build();

        registerRdbmsNameMappingMetamodel(rdbmsModel.getResourceSet());
        registerRdbmsDataTypesMetamodel(rdbmsModel.getResourceSet());
        registerRdbmsTableMappingRulesMetamodel(rdbmsModel.getResourceSet());
    }

    private void executeTransformation(String testName) throws Exception {
        Asm2RdbmsTransformationTrace asm2RdbmsTransformationTrace = executeAsm2RdbmsTransformation(asmModel, rdbmsModel, new Slf4jLog(log),
                calculateAsm2RdbmsTransformationScriptURI(),
                calculateAsm2RdbmsModelURI(), "hsqldb");

        asmModel = asm2RdbmsTransformationTrace.getAsmModel();
        rdbmsModel = asm2RdbmsTransformationTrace.getRdbmsModel();

        asmModel.saveAsmModel(asmSaveArgumentsBuilder()
                .file(new File(TARGET_TEST_CLASSES, "testTypeMapping" + "-" + testName + "-" + ASM_MODEL_NAME + ".model")));
        rdbmsModel.saveRdbmsModel(rdbmsSaveArgumentsBuilder()
                .file(new File(TARGET_TEST_CLASSES, "testTypeMapping" + "-" + testName + "-" + RDBMS_MODEL_NAME + ".model")));
        asm2RdbmsTransformationTrace.save(new File(TARGET_TEST_CLASSES, "testTypeMapping" + "-" + testName + "-" + "Asm2RdbmsTarnsformationTrace.model"));

    }

    @Test
    @DisplayName("Test Numeric Types")
    public void testNumericTypes() throws Exception {
        final EcorePackage ecore = EcorePackage.eINSTANCE;

        // create annotation
        EAnnotation eAnnotation = newEAnnotationBuilder()
                .withSource("http://blackbelt.hu/judo/meta/ExtendedMetadata/entity")
                .build();

        eAnnotation.getDetails().put("value", "true");

        // create class with numeric type attributes
        final EClass eClass = newEClassBuilder()
                .withName("eclass")
                .withEStructuralFeatures(
                        ImmutableList.of(
                                newEAttributeBuilder()
                                        .withName("bigDecimalAttr")
                                        .withEType(ecore.getEBigDecimal())
                                        .build(),
                                newEAttributeBuilder()
                                        .withName("bigInteger")
                                        .withEType(ecore.getEBigInteger())
                                        .build(),
                                newEAttributeBuilder()
                                        .withName("doubleAttr")
                                        .withEType(ecore.getEDouble())
                                        .build(),
                                newEAttributeBuilder()
                                        .withName("floatAttr")
                                        .withEType(ecore.getEFloat())
                                        .build(),
                                newEAttributeBuilder()
                                        .withName("intAttr")
                                        .withEType(ecore.getEInt())
                                        .build(),
                                newEAttributeBuilder()
                                        .withName("longAttr")
                                        .withEType(ecore.getELong())
                                        .build(),
                                newEAttributeBuilder()
                                        .withName("shortAttr")
                                        .withEType(ecore.getEShort())
                                        .build(),
                                newEAttributeBuilder()
                                        .withName("byteAttr")
                                        .withEType(ecore.getEByte())
                                        .build()
                        )
                )
                .withEAnnotations(eAnnotation)
                .build();

        // add class to package
        final EPackage ePackage = newEPackageBuilder()
                .withName("TestPackage")
                .withNsPrefix("test")
                .withNsURI("http:///com.example.test.ecore")
                .withEClassifiers(eClass)
                .build();

        // add eth to asmModel
        asmModel.addContent(ePackage);

        // transform previously created asm model to rdbms model
        executeTransformation("testNumericTypes");

        //TODO ASSERTIONS

    }

    @Test
    @DisplayName("Test String-like Types")
    public void testStringTypes() throws Exception {
        final EcorePackage ecore = EcorePackage.eINSTANCE;

        // create annotation
        EAnnotation eAnnotation = newEAnnotationBuilder()
                .withSource("http://blackbelt.hu/judo/meta/ExtendedMetadata/entity")
                .build();

        eAnnotation.getDetails().put("value", "true");

        // create class with string-like type attributes
        final EClass eClass = newEClassBuilder()
                .withName("eclass")
                .withEStructuralFeatures(
                        ImmutableList.of(
                                //FIXME: there are no rules to transform char ...
//                                newEAttributeBuilder()
//                                        .withName("charAttr")
//                                        .withEType(ecore.getEChar())
//                                        .build(),
                                newEAttributeBuilder()
                                        .withName("stringAttr")
                                        .withEType(ecore.getEString())
                                        .build()
                        )
                )
                .withEAnnotations(eAnnotation)
                .build();

        // add class to package
        final EPackage ePackage = newEPackageBuilder()
                .withName("TestPackage")
                .withNsPrefix("test")
                .withNsURI("http:///com.example.test.ecore")
                .withEClassifiers(eClass)
                .build();

        // add eth to asmModel
        asmModel.addContent(ePackage);

        // transform previously created asm model to rdbms model
        executeTransformation("testStringTypes");

        //TODO ASSERTIONS

    }

    @Test
    @DisplayName("Test Date Types")
    public void testDateTypes() throws Exception {
        final EcorePackage ecore = EcorePackage.eINSTANCE;

        // create annotation
        EAnnotation eAnnotation = newEAnnotationBuilder()
                .withSource("http://blackbelt.hu/judo/meta/ExtendedMetadata/entity")
                .build();

        eAnnotation.getDetails().put("value", "true");

        // create class with date type attributes
        final EClass eClass = newEClassBuilder()
                .withName("eclass")
                .withEStructuralFeatures(
                        ImmutableList.of(
                                newEAttributeBuilder()
                                        .withName("dateAttr")
                                        .withEType(ecore.getEDate())
                                        .build()
                        )
                )
                .withEAnnotations(eAnnotation)
                .build();

        // add class to package
        final EPackage ePackage = newEPackageBuilder()
                .withName("TestPackage")
                .withNsPrefix("test")
                .withNsURI("http:///com.example.test.ecore")
                .withEClassifiers(eClass)
                .build();

        // add eth to asmModel
        asmModel.addContent(ePackage);

        // transform previously created asm model to rdbms model
        executeTransformation("testDateTypes");

        //TODO ASSERTIONS

    }

    @Test
    @DisplayName("Test Boolean Types")
    public void testBooleanTypes() throws Exception {
        final EcorePackage ecore = EcorePackage.eINSTANCE;

        // create annotation
        EAnnotation eAnnotation = newEAnnotationBuilder()
                .withSource("http://blackbelt.hu/judo/meta/ExtendedMetadata/entity")
                .build();

        eAnnotation.getDetails().put("value", "true");

        // create class with boolean type attributes
        final EClass eClass = newEClassBuilder()
                .withName("eclass")
                .withEStructuralFeatures(
                        ImmutableList.of(
                                newEAttributeBuilder()
                                        .withName("booleanAttr")
                                        .withEType(ecore.getEBoolean())
                                        .build()
                        )
                )
                .withEAnnotations(eAnnotation)
                .build();

        // add class to package
        final EPackage ePackage = newEPackageBuilder()
                .withName("TestPackage")
                .withNsPrefix("test")
                .withNsURI("http:///com.example.test.ecore")
                .withEClassifiers(eClass)
                .build();

        // add eth to asmModel
        asmModel.addContent(ePackage);

        // transform previously created asm model to rdbms model
        executeTransformation("testBooleanTypes");

        //TODO ASSERTIONS

    }


}
