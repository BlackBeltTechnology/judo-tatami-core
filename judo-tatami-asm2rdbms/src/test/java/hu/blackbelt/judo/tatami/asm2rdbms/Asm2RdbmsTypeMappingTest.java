package hu.blackbelt.judo.tatami.asm2rdbms;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.judo.meta.rdbms.RdbmsField;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.ecore.*;
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
    private static final String TIMESTAMP = "TIMESTAMP";
    private static final String TIMESTAMP_WITH_TIMEZONE = "TIMESTAMP WITH TIMEZONE";

    /**
     * Asserts the fundamental properties of a RdbmsField
     *
     * @param rdbmsField        RdbmsField to check
     * @param expectedType      name of the expected type
     * @param expectedSize      -1 if undefined
     * @param expectedPrecision -1 if undefined
     * @param expectedScale     -1 if undefined
     */
    private void typeAsserter(final RdbmsField rdbmsField, final String expectedType,
                              final int expectedSize, final int expectedPrecision, final int expectedScale) {
        assertNotNull(rdbmsField);
        assertEquals(expectedType, rdbmsField.getRdbmsTypeName());
        assertEquals(expectedSize, rdbmsField.getSize());
        assertEquals(expectedPrecision, rdbmsField.getPrecision());
        assertEquals(expectedScale, rdbmsField.getScale());
    }

    /**
     * Creates EDataType with given instance type name
     *
     * @param instanceTypeName name of instance type
     * @return new EDataType with name created from instanceTypeName without dots
     */
    private EDataType customEDataTypeBuilder(final String instanceTypeName) {
        return newEDataTypeBuilder()
                .withName(instanceTypeName.replace(".", ""))
                .withInstanceTypeName(instanceTypeName)
                .build();
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

        // create custom numeric types
        final EDataType javalangByte = customEDataTypeBuilder("java.lang.Byte");
        final EDataType javalangShort = customEDataTypeBuilder("java.lang.Short");
        final EDataType javalangInteger = customEDataTypeBuilder("java.lang.Integer");
        final EDataType javalangLong = customEDataTypeBuilder("java.lang.Long");
        final EDataType javamathBigInteger = customEDataTypeBuilder("java.math.BigInteger");
        final EDataType javalangFloat = customEDataTypeBuilder("java.lang.Float");
        final EDataType javalangDouble = customEDataTypeBuilder("java.lang.Double");
        final EDataType javamathBigDecimal = customEDataTypeBuilder("java.math.BigDecimal");

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
                                        .build(),
                                newEAttributeBuilder()
                                        .withName("javalangByteAttr")
                                        .withEType(javalangByte)
                                        .build(),
                                newEAttributeBuilder()
                                        .withName("javalangShortAttr")
                                        .withEType(javalangShort)
                                        .build(),
                                newEAttributeBuilder()
                                        .withName("javalangIntegerAttr")
                                        .withEType(javalangInteger)
                                        .build(),
                                newEAttributeBuilder()
                                        .withName("javalangLongAttr")
                                        .withEType(javalangLong)
                                        .build(),
                                newEAttributeBuilder()
                                        .withName("javamathBigIntegerAttr")
                                        .withEType(javamathBigInteger)
                                        .build(),
                                newEAttributeBuilder()
                                        .withName("javalangFloatAttr")
                                        .withEType(javalangFloat)
                                        .build(),
                                newEAttributeBuilder()
                                        .withName("javalangDoubleAttr")
                                        .withEType(javalangDouble)
                                        .build(),
                                newEAttributeBuilder()
                                        .withName("javamathBigDecimalAttr")
                                        .withEType(javamathBigDecimal)
                                        .build()
                        )
                )
                .withEAnnotations(eAnnotation)
                .build();
        logger.debug("Create TestNumericTypesClass eclass");

        // add class and custom numeric types to package
        final EPackage ePackage = newEPackageBuilder()
                .withName("TestNumericTypesPackage")
                .withNsPrefix("test")
                .withNsURI("http:///com.example.test.ecore")
                .withEClassifiers(ImmutableList.of(
                        // add eclass
                        eClass,

                        // add custom types
                        javalangByte,
                        javalangShort,
                        javalangInteger,
                        javalangLong,
                        javamathBigInteger,
                        javalangFloat,
                        javalangDouble,
                        javamathBigDecimal
                ))
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

        assertEquals(18, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
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
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "javalangByteAttr", true)
                        .orElseThrow(() -> new RuntimeException("javalangByteAttr is missing")),
                INTEGER,
                -1,
                -1,
                -1);
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "javalangShortAttr", true)
                        .orElseThrow(() -> new RuntimeException("javalangShortAttr is missing")),
                INTEGER,
                -1,
                -1,
                -1);
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "javalangIntegerAttr", true)
                        .orElseThrow(() -> new RuntimeException("javalangIntegerAttr is missing")),
                INTEGER,
                -1,
                -1,
                -1);
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "javalangLongAttr", true)
                        .orElseThrow(() -> new RuntimeException("javalangLongAttr is missing")),
                BIGINT,
                -1,
                -1,
                -1);
//        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "javamathBigIntegerAttr", true)
//                        .orElseThrow(() -> new RuntimeException("javamathBigIntegerAttr is missing")),
//                DECIMAL,
//                -1,
//                18,
//                0);
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "javalangFloatAttr", true)
                        .orElseThrow(() -> new RuntimeException("javalangFloatAttr is missing")),
                FLOAT,
                -1,
                -1,
                -1);
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "javalangDoubleAttr", true)
                        .orElseThrow(() -> new RuntimeException("javalangDoubleAttr is missing")),
                DOUBLE,
                -1,
                -1,
                -1);
//        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "javamathBigDecimalAttr", true)
//                        .orElseThrow(() -> new RuntimeException("javamathBigDecimalAttr is missing")),
//                DECIMAL,
//                -1,
//                64,
//                20);

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

        // create custom string-like type
        final EDataType javalangString = customEDataTypeBuilder("java.lang.String");

        // create class with string-like type attributes
        final EClass eClass = newEClassBuilder()
                .withName("TestStringlikeTypesClass")
                .withEStructuralFeatures(
                        ImmutableList.of(
                                newEAttributeBuilder()
                                        .withName("stringAttr")
                                        .withEType(ecore.getEString())
                                        .build(),
                                newEAttributeBuilder()
                                        .withName("javalangStringAttr")
                                        .withEType(javalangString)
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
                .withEClassifiers(ImmutableList.of(
                        // add eclass
                        eClass,

                        // add custom string-like type
                        javalangString
                ))
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
        assertEquals(4, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException("There are no fields in the given table"))
                .size()); //+2 type and id


        // check field types based on typemapping table
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "stringAttr", true)
                        .orElseThrow(() -> new RuntimeException("stringAttr is missing")),
                VARCHAR,
                255,
                -1,
                -1);
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "javalangStringAttr", true)
                        .orElseThrow(() -> new RuntimeException("javalangStringAttr is missing")),
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

        // create custom date types
        final EDataType javautilDate = customEDataTypeBuilder("java.util.Date");
        final EDataType javasqlDate = customEDataTypeBuilder("java.sql.Date");
        final EDataType javatimeLocalDate = customEDataTypeBuilder("java.time.LocalDate");
        final EDataType orgjodatimeLocalDate = customEDataTypeBuilder("org.joda.time.LocalDate");
        final EDataType javasqlTimestamp = customEDataTypeBuilder("java.sql.Timestamp");
        final EDataType javatimeLocalDateTime = customEDataTypeBuilder("java.time.LocalDateTime");
        final EDataType javatimeOffsetDateTime = customEDataTypeBuilder("java.time.OffsetDateTime");
        final EDataType javatimeZonedDateTime = customEDataTypeBuilder("java.time.ZonedDateTime");
        final EDataType orgjodatimeDateTime = customEDataTypeBuilder("org.joda.time.DateTime");
        final EDataType orgjodatimeLocalDateTime = customEDataTypeBuilder("org.joda.time.LocalDateTime");
        final EDataType orgjodatimeMutableDateTime = customEDataTypeBuilder("org.joda.time.MutableDateTime");

        // create class with date type attributes
        final EClass eClass = newEClassBuilder()
                .withName("TestDateTypesClass")
                .withEStructuralFeatures(
                        ImmutableList.of(
                                newEAttributeBuilder()
                                        .withName("dateAttr")
                                        .withEType(ecore.getEDate())
                                        .build(),
                                newEAttributeBuilder()
                                        .withName("javautilDateAttr")
                                        .withEType(javautilDate)
                                        .build(),
                                newEAttributeBuilder()
                                        .withName("javasqlDateAttr")
                                        .withEType(javasqlDate)
                                        .build(),
                                newEAttributeBuilder()
                                        .withName("javatimeLocalDateAttr")
                                        .withEType(javatimeLocalDate)
                                        .build(),
                                newEAttributeBuilder()
                                        .withName("orgjodatimeLocalDateAttr")
                                        .withEType(orgjodatimeLocalDate)
                                        .build(),
                                newEAttributeBuilder()
                                        .withName("javasqlTimestampAttr")
                                        .withEType(javasqlTimestamp)
                                        .build(),
                                newEAttributeBuilder()
                                        .withName("javatimeLocalDateTimeAttr")
                                        .withEType(javatimeLocalDateTime)
                                        .build(),
                                newEAttributeBuilder()
                                        .withName("javatimeOffsetDateTimeAttr")
                                        .withEType(javatimeOffsetDateTime)
                                        .build(),
                                newEAttributeBuilder()
                                        .withName("javatimeZonedDateTimeAttr")
                                        .withEType(javatimeZonedDateTime)
                                        .build(),
                                newEAttributeBuilder()
                                        .withName("orgjodatimeDateTimeAttr")
                                        .withEType(orgjodatimeDateTime)
                                        .build(),
                                newEAttributeBuilder()
                                        .withName("orgjodatimeLocalDateTimeAttr")
                                        .withEType(orgjodatimeLocalDateTime)
                                        .build(),
                                newEAttributeBuilder()
                                        .withName("orgjodatimeMutableDateTimeAttr")
                                        .withEType(orgjodatimeMutableDateTime)
                                        .build()
                        )
                )
                .withEAnnotations(eAnnotation)
                .build();
        logger.debug("Create TestDateTypesClass eclass");


        // add class and custom types to package
        final EPackage ePackage = newEPackageBuilder()
                .withName("TestDateTypesPackage")
                .withNsPrefix("test")
                .withNsURI("http:///com.example.test.ecore")
                .withEClassifiers(ImmutableList.of(
                        // eclass
                        eClass,

                        // custom types
                        javautilDate,
                        javautilDate,
                        javasqlDate,
                        javatimeLocalDate,
                        orgjodatimeLocalDate,
                        javasqlTimestamp,
                        javatimeLocalDateTime,
                        javatimeOffsetDateTime,
                        javatimeZonedDateTime,
                        orgjodatimeDateTime,
                        orgjodatimeLocalDateTime,
                        orgjodatimeMutableDateTime
                ))
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
        assertEquals(14, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException("There are no fields in the given table"))
                .size()); //+2 type and id

        // check field types based on typemapping table
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "dateAttr", true)
                        .orElseThrow(() -> new RuntimeException("dateAttr is missing")),
                DATE,
                -1,
                -1,
                -1);
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "javautilDateAttr", true)
                        .orElseThrow(() -> new RuntimeException("javautilDateAttr is missing")),
                DATE,
                -1,
                -1,
                -1);
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "javasqlDateAttr", true)
                        .orElseThrow(() -> new RuntimeException("javasqlDateAttr is missing")),
                DATE,
                -1,
                -1,
                -1);
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "javatimeLocalDateAttr", true)
                        .orElseThrow(() -> new RuntimeException("javatimeLocalDateAttr is missing")),
                DATE,
                -1,
                -1,
                -1);
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "orgjodatimeLocalDateAttr", true)
                        .orElseThrow(() -> new RuntimeException("orgjodatimeLocalDateAttr is missing")),
                DATE,
                -1,
                -1,
                -1);
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "javasqlTimestampAttr", true)
                        .orElseThrow(() -> new RuntimeException("javasqlTimestampAttr is missing")),
                TIMESTAMP,
                -1,
                -1,
                -1);
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "javatimeLocalDateTimeAttr", true)
                        .orElseThrow(() -> new RuntimeException("javatimeLocalDateTimeAttr is missing")),
                TIMESTAMP,
                -1,
                -1,
                -1);
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "javatimeOffsetDateTimeAttr", true)
                        .orElseThrow(() -> new RuntimeException("javatimeOffsetDateTimeAttr is missing")),
                TIMESTAMP_WITH_TIMEZONE,
                -1,
                -1,
                -1);
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "javatimeZonedDateTimeAttr", true)
                        .orElseThrow(() -> new RuntimeException("javatimeZonedDateTimeAttr is missing")),
                TIMESTAMP_WITH_TIMEZONE,
                -1,
                -1,
                -1);
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "orgjodatimeDateTimeAttr", true)
                        .orElseThrow(() -> new RuntimeException("orgjodatimeDateTimeAttr is missing")),
                TIMESTAMP_WITH_TIMEZONE,
                -1,
                -1,
                -1);
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "orgjodatimeLocalDateTimeAttr", true)
                        .orElseThrow(() -> new RuntimeException("orgjodatimeLocalDateTimeAttr is missing")),
                TIMESTAMP,
                -1,
                -1,
                -1);
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "orgjodatimeMutableDateTimeAttr", true)
                        .orElseThrow(() -> new RuntimeException("orgjodatimeMutableDateTimeAttr is missing")),
                TIMESTAMP_WITH_TIMEZONE,
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

        // create custom type
        final EDataType javalangBoolean = customEDataTypeBuilder("java.lang.Boolean");

        // create class with boolean type attributes
        final EClass eClass = newEClassBuilder()
                .withName("TestBooleanTypesClass")
                .withEStructuralFeatures(
                        ImmutableList.of(
                                newEAttributeBuilder()
                                        .withName("booleanAttr")
                                        .withEType(ecore.getEBoolean())
                                        .build(),
                                newEAttributeBuilder()
                                        .withName("javalangBooleanAttr")
                                        .withEType(javalangBoolean)
                                        .build()
                        )
                )
                .withEAnnotations(eAnnotation)
                .build();
        logger.debug("Create TestBooleanTypesClass eclass");

        // add class and custom booolean type to package
        final EPackage ePackage = newEPackageBuilder()
                .withName("TestBooleanTypesPackage")
                .withNsPrefix("test")
                .withNsURI("http:///com.example.test.ecore")
                .withEClassifiers(ImmutableList.of(
                        // add eclass
                        eClass,

                        // add custom type
                        javalangBoolean
                ))
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
        assertEquals(4, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException("There no fields in the given table"))
                .size()); //+2 type and id

        // check field types based on typemapping table
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "booleanAttr", true)
                        .orElseThrow(() -> new RuntimeException("booleanAttr is missing")),
                BOOLEAN,
                -1,
                -1,
                -1);
        typeAsserter(rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, "javalangBooleanAttr", true)
                        .orElseThrow(() -> new RuntimeException("javalangBooleanAttr is missing")),
                BOOLEAN,
                -1,
                -1,
                -1);
    }

}
