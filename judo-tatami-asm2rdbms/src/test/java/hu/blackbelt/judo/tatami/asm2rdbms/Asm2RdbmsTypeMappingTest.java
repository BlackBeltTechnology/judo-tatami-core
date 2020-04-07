package hu.blackbelt.judo.tatami.asm2rdbms;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.judo.meta.rdbms.RdbmsField;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.eclipse.emf.ecore.util.builder.EcoreBuilders.*;
import static org.junit.jupiter.api.Assertions.*;

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

    /**
     * Asserts the fundamental properties of a RdbmsField
     *
     * @param rdbmsField        RdbmsField to check
     * @param expectedType      name of the expected type
     * @param expectedSize      -1 if undefined
     * @param expectedPrecision -1 if undefined
     * @param expectedScale     -1 if undefined
     */
    protected void typeAsserter(final RdbmsField rdbmsField, final String expectedType,
                                final int expectedSize, final int expectedPrecision, final int expectedScale) {
        assertNotNull(rdbmsField);
        assertEquals(expectedType, rdbmsField.getRdbmsTypeName());
        assertEquals(expectedSize, rdbmsField.getSize());
        assertEquals(expectedPrecision, rdbmsField.getPrecision());
        assertEquals(expectedScale, rdbmsField.getScale());
    }

    @Test
    @DisplayName("Test Numeric Types")
    public void testNumericTypes() {
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
        assertEquals(1, rdbmsUtils.getRdbmsTables().orElseThrow(() -> new RuntimeException("There are no tables created")).size());
        assertTrue(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME).isPresent());

        // check attributes -> fields
        assertEquals(1, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("There are no tables created")).size());

        assertEquals(10, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException("There is no table with given name or there are no fields in the given table"))
                .size()); //+2 type and id

        // check field types based on typemapping table
//        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "bigDecimalAttr", true)
//                        .orElseThrow(() -> new RuntimeException("intAttr is missing")),
//                DECIMAL,
//                -1,
//                64,
//                20);
//        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "bigInteger", true)
//                        .orElseThrow(() -> new RuntimeException("intAttr is missing")),
//                DECIMAL,
//                -1,
//                18,
//                0);
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "doubleAttr", true)
                        .orElseThrow(() -> new RuntimeException("doubleAttr is missing")),
                DOUBLE,
                -1,
                -1,
                -1);
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "longAttr", true)
                        .orElseThrow(() -> new RuntimeException("longAttr is missing")),
                BIGINT,
                -1,
                -1,
                -1);
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "floatAttr", true)
                        .orElseThrow(() -> new RuntimeException("floatAttr is missing")),
                FLOAT,
                -1,
                -1,
                -1);
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "intAttr", true)
                        .orElseThrow(() -> new RuntimeException("intAttr is missing")),
                INTEGER,
                -1,
                -1,
                -1);
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "shortAttr", true)
                        .orElseThrow(() -> new RuntimeException("shortAttr is missing")),
                INTEGER,
                -1,
                -1,
                -1);
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "byteAttr", true)
                        .orElseThrow(() -> new RuntimeException("byteAttr is missing")),
                INTEGER,
                -1,
                -1,
                -1);

    }

    @Test
    @DisplayName("Test String-like Types")
    public void testStringlikeTypes() {
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
                .orElseThrow(() -> new RuntimeException("There are no tables created")).size());
        assertTrue(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME).isPresent());

        // check attributes -> fields
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException("There are no fields in the given table"))
                .size()); //+2 type and id


        // check field types based on typemapping table
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "stringAttr", true)
                        .orElseThrow(() -> new RuntimeException("stringAttr is missing")),
                VARCHAR,
                255,
                -1,
                -1);
    }

    @Test
    @DisplayName("Test Date Types")
    public void testDateTypes() {
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
                .orElseThrow(() -> new RuntimeException("There are no tables created")).size());
        assertTrue(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME).isPresent());

        // check attributes -> fields
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException("There are no fields in the given table"))
                .size()); //+2 type and id

        // check field types based on typemapping table
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "dateAttr", true)
                        .orElseThrow(() -> new RuntimeException("dateAttr is missing")),
                DATE,
                -1,
                -1,
                -1);
    }

    @Test
    @DisplayName("Test Boolean Types")
    public void testBooleanTypes() {
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
                .orElseThrow(() -> new RuntimeException("There are no tables created")).size());
        assertTrue(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME).isPresent());

        // check attributes -> fields
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException("There no fields in the given table"))
                .size()); //+2 type and id

        // check field types based on typemapping table
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "booleanAttr", true)
                        .orElseThrow(() -> new RuntimeException("booleanAttr is missing")),
                BOOLEAN,
                -1,
                -1,
                -1);
    }

    @Test
    @Disabled
    @DisplayName("Test Custom Types")
    public void testCustomTypes() {
        //TODO
    }

}
