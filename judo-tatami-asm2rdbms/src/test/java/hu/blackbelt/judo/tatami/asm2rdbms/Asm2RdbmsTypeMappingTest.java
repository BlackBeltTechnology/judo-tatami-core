package hu.blackbelt.judo.tatami.asm2rdbms;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.eclipse.emf.ecore.util.builder.EcoreBuilders.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

@SuppressWarnings("OptionalGetWithoutIsPresent")
@Slf4j
public class Asm2RdbmsTypeMappingTest extends Asm2RdbmsMappingTestBase {
    private static final String INTEGER = "INTEGER";
    private static final String BIGINT = "BIGINT";
    private static final String DECIMAL = "DECIMAL";
    private static final String FLOAT = "FLOAT";
    private static final String DOUBLE = "DOUBLE";
    private static final String VARCHAR = "VARCHAR";
    private static final String BOOLEAN = "BOOLEAN";
    private static final String DATE = "DATE";


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
                                // built in types
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
        logger.debug("Create TestNumericTypesClass eclass");

        // add class to package
        final EPackage ePackage = newEPackageBuilder()
                .withName("TestNumericTypesPackage")
                .withNsPrefix("test")
                .withNsURI("http:///com.example.test.ecore")
                .withEClassifiers(eClass)
                .build();
        logger.debug("Create TestNumericTypesPackage EPackage");

        // add everything to asmModel
        asmModel.addContent(ePackage);
        logger.debug("Add TestNumericTypesPackage to asmModel");

        // transform previously created asm model to rdbms model
        executeTransformation("testNumericTypes");

        // check eclass -> tables
        final String RDBMS_TABLE_NAME = "TestNumericTypesPackage.TestNumericTypesClass";
        assertEquals(1, rdbmsUtils.getRdbmsTables().orElseThrow(() -> new Exception("There are no tables created")).size());
        assertTrue(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME).isPresent());

        // check attributes -> fields
        assertEquals(1, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new Exception("There are no tables created")).size());

        assertEquals(10, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new Exception("There is no table with given name or there are no fields in the given table"))
                .size()); //+2 type and id

        // check field types based on typemapping table
//        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "bigDecimalAttr", true).get(),
//                DECIMAL,
//                -1,
//                64,
//                20);
//        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME , "bigInteger", true).get(),
//                DECIMAL,
//                -1,
//                18,
//                0);
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "doubleAttr", true).get(),
                DOUBLE,
                -1,
                -1,
                -1);
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "longAttr", true).get(),
                BIGINT,
                -1,
                -1,
                -1);
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "floatAttr", true).get(),
                FLOAT,
                -1,
                -1,
                -1);
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "intAttr", true).get(),
                INTEGER,
                -1,
                -1,
                -1);
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "shortAttr", true).get(),
                INTEGER,
                -1,
                -1,
                -1);
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "byteAttr", true).get(),
                INTEGER,
                -1,
                -1,
                -1);

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
                                newEAttributeBuilder()
                                        .withName("stringAttr")
                                        .withEType(ecore.getEString())
                                        .build()
                        )
                )
                .withEAnnotations(eAnnotation)
                .build();
        logger.debug("Create TestStringlikeTypesClass eclass");

        // add class to package
        final EPackage ePackage = newEPackageBuilder()
                .withName("TestStringlikeTypesPackage")
                .withNsPrefix("test")
                .withNsURI("http:///com.example.test.ecore")
                .withEClassifiers(eClass)
                .build();
        logger.debug("Create TestStringlikeTypesPackage EPackage");

        // add eth to asmModel
        asmModel.addContent(ePackage);
        logger.debug("Add TestStringlikeTypesPackage to asmModel");

        // transform previously created asm model to rdbms model
        executeTransformation("testStringlikeTypes");

        // check eclass -> tables
        final String RDBMS_TABLE_NAME = "TestStringlikeTypesPackage.TestStringlikeTypesClass";
        assertEquals(1, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new Exception("There are no tables created")).size());
        assertTrue(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME).isPresent());

        // check attributes -> fields
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new Exception("There are no fields in the given table"))
                .size()); //+2 type and id


        // check field types based on typemapping table
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "stringAttr", true).get(),
                VARCHAR,
                255,
                -1,
                -1);
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
        logger.debug("Create TestDateTypesClass eclass");


        // add class to package
        final EPackage ePackage = newEPackageBuilder()
                .withName("TestDateTypesPackage")
                .withNsPrefix("test")
                .withNsURI("http:///com.example.test.ecore")
                .withEClassifiers(eClass)
                .build();
        logger.debug("Create TestDateTypesPackage EPackage");

        // add eth to asmModel
        asmModel.addContent(ePackage);
        logger.debug("Add TestDateTypesPackage to asmModel");

        // transform previously created asm model to rdbms model
        executeTransformation("testDateTypes");

        // check eclass -> tables
        final String RDBMS_TABLE_NAME = "TestDateTypesPackage.TestDateTypesClass";
        assertEquals(1, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new Exception("There are no tables created")).size());
        assertTrue(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME).isPresent());

        // check attributes -> fields
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new Exception("There are no fields in the given table"))
                .size()); //+2 type and id

        // check field types based on typemapping table
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "dateAttr", true).get(),
                DATE,
                -1,
                -1,
                -1);
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
        logger.debug("Create TestBooleanTypesClass eclass");

        // add class to package
        final EPackage ePackage = newEPackageBuilder()
                .withName("TestBooleanTypesPackage")
                .withNsPrefix("test")
                .withNsURI("http:///com.example.test.ecore")
                .withEClassifiers(eClass)
                .build();
        logger.debug("Create TestBooleanTypesPackage EPackage");

        // add eth to asmModel
        asmModel.addContent(ePackage);
        logger.debug("Add TestBooleanTypesPackage to asmModel");

        // transform previously created asm model to rdbms model
        executeTransformation("testBooleanTypes");

        // check eclass -> tables
        final String RDBMS_TABLE_NAME = "TestBooleanTypesPackage.TestBooleanTypesClass";
        assertEquals(1, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new Exception("There are no tables created")).size());
        assertTrue(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME).isPresent());

        // check attributes -> fields
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new Exception("There no fields in the given table"))
                .size()); //+2 type and id

        // check field types based on typemapping table
        assertTrue(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "booleanAttr", true).isPresent());
        assertEquals(BOOLEAN, rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME , "booleanAttr", true)
                .get().getRdbmsTypeName());
    }

    @Test
    @Disabled
    @DisplayName("Test Custom Types")
    public void testCustomTypes() {
        //TODO
    }

}
