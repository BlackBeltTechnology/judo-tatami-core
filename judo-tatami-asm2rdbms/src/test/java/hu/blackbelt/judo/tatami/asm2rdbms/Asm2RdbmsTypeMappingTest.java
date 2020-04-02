package hu.blackbelt.judo.tatami.asm2rdbms;

import com.google.common.collect.ImmutableList;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.eclipse.emf.ecore.util.builder.EcoreBuilders.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

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
        assertEquals(1, rdbmsUtils.getRdbmsTables().orElseThrow(() -> new Exception("There are no tables created")).size());

        // check attributes -> fields
        final String RDBMS_TABLE_NAME = "TestNumericTypesPackage.TestNumericTypesClass";
        assertEquals(1, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new Exception("There are no tables created")).size());

        assertEquals(10, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new Exception("There is no table with given name or there are no fields in the given table"))
                .size()); //+2 type and id

        // check field types
        assertEquals(DECIMAL, rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "bigDecimalAttr")
                .orElseThrow(() -> new Exception("There's no attribute field with the given name: bigDecimalAttr")).getRdbmsTypeName());
        assertEquals(DECIMAL, rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "bigInteger")
                .orElseThrow(() -> new Exception("There's no attribute field with the given name: bigInteger")).getRdbmsTypeName());
        assertEquals(DOUBLE, rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "doubleAttr")
                .orElseThrow(() -> new Exception("There's no attribute field with the given name: doubleAttr")).getRdbmsTypeName());
        assertEquals(BIGINT, rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "longAttr")
                .orElseThrow(() -> new Exception("There's no attribute field with the given name: longAttr")).getRdbmsTypeName());
        assertEquals(FLOAT, rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "floatAttr")
                .orElseThrow(() -> new Exception("There's no attribute field with the given name: floatAttr")).getRdbmsTypeName());
        assertEquals(INTEGER, rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "intAttr")
                .orElseThrow(() -> new Exception("There's no attribute field with the given name: intAttr")).getRdbmsTypeName());
        assertEquals(INTEGER, rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "shortAttr")
                .orElseThrow(() -> new Exception("There's no attribute field with the given name: shortAttr")).getRdbmsTypeName());
        assertEquals(INTEGER, rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "byteAttr")
                .orElseThrow(() -> new Exception("There's no attribute field with the given name: byteAttr")).getRdbmsTypeName());
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
        assertEquals(1, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new Exception("There are no tables created")).size());

        // check attributes -> fields
        final String RDBMS_TABLE_NAME = "TestStringlikeTypesPackage.TestStringlikeTypesClass";
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new Exception("There is no table with given name or there are no fields in the given table"))
                .size()); //+2 type and id

        // check field types
        assertEquals(VARCHAR, rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "stringAttr")
                .orElseThrow(() -> new Exception("There's no attribute field with the given name: stringAttr")).getRdbmsTypeName());
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
        assertEquals(1, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new Exception("There are no tables created")).size());

        // check attributes -> fields
        final String RDBMS_TABLE_NAME = "TestDateTypesPackage.TestDateTypesClass";
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new Exception("There is no table with given name or there are no fields in the given table"))
                .size()); //+2 type and id

        // check field types
        assertEquals(DATE, rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "dateAttr")
                .orElseThrow(() -> new Exception("There's no attribute field with the given name: dateAttr")).getRdbmsTypeName());
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
        assertEquals(1, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new Exception("There are no tables created")).size());

        // check attributes -> fields
        final String RDBMS_TABLE_NAME = "TestBooleanTypesPackage.TestBooleanTypesClass";
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new Exception("There is no table with given name or there are no fields in the given table"))
                .size()); //+2 type and id

        // check field types
        assertEquals(BOOLEAN, rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "booleanAttr")
                .orElseThrow(() -> new Exception("There's no attribute field with the given name: booleanAttr")).getRdbmsTypeName());
    }

}
