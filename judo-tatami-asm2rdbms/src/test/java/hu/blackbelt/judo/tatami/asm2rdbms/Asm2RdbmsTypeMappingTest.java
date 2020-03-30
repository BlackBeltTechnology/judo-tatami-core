package hu.blackbelt.judo.tatami.asm2rdbms;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.rdbms.RdbmsField;
import hu.blackbelt.judo.meta.rdbms.RdbmsTable;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.meta.rdbms.support.RdbmsModelResourceSupport;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@Slf4j
public class Asm2RdbmsTypeMappingTest {
    private static final String ASM_MODEL_NAME = "TestAsmModel";
    private static final String RDBMS_MODEL_NAME = "TestRdbmsModel";

    private static final String TARGET_TEST_CLASSES = "target/test-classes";

    private static final String INTEGER = "INTEGER";
    private static final String BIGINT = "BIGINT";
    private static final String DECIMAL = "DECIMAL";
    private static final String FLOAT = "FLOAT";
    private static final String DOUBLE = "DOUBLE";
    private static final String VARCHAR = "VARCHAR";
    private static final String BOOLEAN = "BOOLEAN";

    Slf4jLog logger;

    private AsmModel asmModel;
    private RdbmsModel rdbmsModel;

    private RdbmsModelResourceSupport rdbmsModelResourceSupport;

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

        rdbmsModelResourceSupport = RdbmsModelResourceSupport.rdbmsModelResourceSupportBuilder()
                .resourceSet(rdbmsModel.getResourceSet())
                .uri(rdbmsModel.getUri())
                .build();

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
                .withName("TestNumericTypesClass")
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
                .withName("TestNumericTypesPackage")
                .withNsPrefix("test")
                .withNsURI("http:///com.example.test.ecore")
                .withEClassifiers(eClass)
                .build();

        // add everything to asmModel
        asmModel.addContent(ePackage);

        // transform previously created asm model to rdbms model
        executeTransformation("testNumericTypes");

        //check eclass (tables)
        Object[] rdbmsTables = rdbmsModelResourceSupport.getStreamOfRdbmsRdbmsTable().toArray();
        assertEquals(1, rdbmsTables.length);
        assertTrue(rdbmsTables[0] instanceof RdbmsTable);
        assertEquals("TestNumericTypesPackage.TestNumericTypesClass", ((RdbmsTable) rdbmsTables[0]).getName());

        //check eattributes (fields)
        EList<RdbmsField> rdbmsFields = ((RdbmsTable) rdbmsTables[0]).getFields();
        assertEquals(10, rdbmsFields.size()); //+2 _type and id
        final String fqname = "TestNumericTypesPackage.TestNumericTypesClass#";
        assertEquals(DECIMAL,
                rdbmsFields.stream()
                        .filter(rdbmsField -> (fqname + "bigDecimalAttr").equals(rdbmsField.getName()))
                        .findAny().get().getRdbmsTypeName());
        assertEquals(DECIMAL,
                rdbmsFields.stream()
                        .filter(rdbmsField -> (fqname + "bigInteger").equals(rdbmsField.getName()))
                        .findAny().get().getRdbmsTypeName());
        assertEquals(DOUBLE,
                rdbmsFields.stream()
                        .filter(rdbmsField -> (fqname + "doubleAttr").equals(rdbmsField.getName()))
                        .findAny().get().getRdbmsTypeName());
        assertEquals(BIGINT,
                rdbmsFields.stream()
                        .filter(rdbmsField -> (fqname + "longAttr").equals(rdbmsField.getName()))
                        .findAny().get().getRdbmsTypeName());
        assertEquals(FLOAT,
                rdbmsFields.stream()
                        .filter(rdbmsField -> (fqname + "floatAttr").equals(rdbmsField.getName()))
                        .findAny().get().getRdbmsTypeName());
        assertEquals(INTEGER,
                rdbmsFields.stream()
                        .filter(rdbmsField -> (fqname + "intAttr").equals(rdbmsField.getName()))
                        .findAny().get().getRdbmsTypeName());
        assertEquals(INTEGER,
                rdbmsFields.stream()
                        .filter(rdbmsField -> (fqname + "shortAttr").equals(rdbmsField.getName()))
                        .findAny().get().getRdbmsTypeName());
        assertEquals(INTEGER,
                rdbmsFields.stream()
                        .filter(rdbmsField -> (fqname + "byteAttr").equals(rdbmsField.getName()))
                        .findAny().get().getRdbmsTypeName());

        //check primary key
        assertNotNull(((RdbmsTable) rdbmsTables[0]).getPrimaryKey());
    }

    @Test
    @DisplayName("Test String-like Types")
    public void testStringlikeTypes() throws Exception {
        final EcorePackage ecore = EcorePackage.eINSTANCE;

        // create annotation
        EAnnotation eAnnotation = newEAnnotationBuilder()
                .withSource("http://blackbelt.hu/judo/meta/ExtendedMetadata/entity")
                .build();

        eAnnotation.getDetails().put("value", "true");

        // create class with string-like type attributes
        final EClass eClass = newEClassBuilder()
                .withName("TestStringlikeTypesClass")
                .withEStructuralFeatures(
                        ImmutableList.of(
                                //there are no rules to transform char
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
                .withName("TestStringlikeTypesPackage")
                .withNsPrefix("test")
                .withNsURI("http:///com.example.test.ecore")
                .withEClassifiers(eClass)
                .build();

        // add eth to asmModel
        asmModel.addContent(ePackage);

        // transform previously created asm model to rdbms model
        executeTransformation("testStringlikeTypes");

        //check eclass (tables)
        Object[] rdbmsTables = rdbmsModelResourceSupport.getStreamOfRdbmsRdbmsTable().toArray();
        assertEquals(1, rdbmsTables.length);
        assertTrue(rdbmsTables[0] instanceof RdbmsTable);
        assertEquals("TestStringlikeTypesPackage.TestStringlikeTypesClass", ((RdbmsTable) rdbmsTables[0]).getName());

        //check eattributes (fields)
        EList<RdbmsField> rdbmsFields = ((RdbmsTable) rdbmsTables[0]).getFields();
        assertEquals(3, rdbmsFields.size()); //+2 type and id
        final String fqname = "TestStringlikeTypesPackage.TestStringlikeTypesClass#";
//        assertEquals(VARCHAR,
//                rdbmsFields.stream()
//                        .filter(rdbmsField -> (fqname + "charAttr").equals(rdbmsField.getName()))
//                        .findAny().get().getRdbmsTypeName());
        assertEquals(VARCHAR,
                rdbmsFields.stream()
                        .filter(rdbmsField -> (fqname + "stringAttr").equals(rdbmsField.getName()))
                        .findAny().get().getRdbmsTypeName());

        //check primary key
        assertNotNull(((RdbmsTable) rdbmsTables[0]).getPrimaryKey());
    }

    @Test
    @Disabled
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
                .withName("TestDateTypesClass")
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
                .withName("TestDateTypesPackage")
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
    @Disabled
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
                .withName("TestBooleanTypesClass")
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
                .withName("TestBooleanTypesPackage")
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
