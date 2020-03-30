package hu.blackbelt.judo.tatami.asm2rdbms;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.judo.meta.rdbms.RdbmsField;
import hu.blackbelt.judo.meta.rdbms.RdbmsTable;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.eclipse.emf.ecore.util.builder.EcoreBuilders.*;
import static org.junit.jupiter.api.Assertions.*;

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
        logger.info("Create TestNumericTypesClass eclass");

        // add class to package
        final EPackage ePackage = newEPackageBuilder()
                .withName("TestNumericTypesPackage")
                .withNsPrefix("test")
                .withNsURI("http:///com.example.test.ecore")
                .withEClassifiers(eClass)
                .build();
        logger.info("Create TestNumericTypesPackage EPackage");

        // add everything to asmModel
        asmModel.addContent(ePackage);
        logger.info("Add TestNumericTypesPackage to asmModel");

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
        logger.info("Create TestStringlikeTypesClass eclass");

        // add class to package
        final EPackage ePackage = newEPackageBuilder()
                .withName("TestStringlikeTypesPackage")
                .withNsPrefix("test")
                .withNsURI("http:///com.example.test.ecore")
                .withEClassifiers(eClass)
                .build();
        logger.info("Create TestStringlikeTypesPackage EPackage");

        // add eth to asmModel
        asmModel.addContent(ePackage);
        logger.info("Add TestStringlikeTypesPackage to asmModel");

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
        logger.info("Create TestDateTypesClass eclass");


        // add class to package
        final EPackage ePackage = newEPackageBuilder()
                .withName("TestDateTypesPackage")
                .withNsPrefix("test")
                .withNsURI("http:///com.example.test.ecore")
                .withEClassifiers(eClass)
                .build();
        logger.info("Create TestDateTypesPackage EPackage");

        // add eth to asmModel
        asmModel.addContent(ePackage);
        logger.info("Add TestDateTypesPackage to asmModel");

        // transform previously created asm model to rdbms model
        executeTransformation("testDateTypes");

        //check eclass (tables)
        Object[] rdbmsTables = rdbmsModelResourceSupport.getStreamOfRdbmsRdbmsTable().toArray();
        assertEquals(1, rdbmsTables.length);
        assertTrue(rdbmsTables[0] instanceof RdbmsTable);
        assertEquals("TestDateTypesPackage.TestDateTypesClass", ((RdbmsTable) rdbmsTables[0]).getName());

        //check eattributes (fields)
        EList<RdbmsField> rdbmsFields = ((RdbmsTable) rdbmsTables[0]).getFields();
        assertEquals(3, rdbmsFields.size()); //+2 type and id
        final String fqname = "TestDateTypesPackage.TestDateTypesClass#";
        assertEquals(DATE,
                rdbmsFields.stream()
                        .filter(rdbmsField -> (fqname + "dateAttr").equals(rdbmsField.getName()))
                        .findAny().get().getRdbmsTypeName());

        //check primary key
        assertNotNull(((RdbmsTable) rdbmsTables[0]).getPrimaryKey());
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
        logger.info("Create TestBooleanTypesClass eclass");

        // add class to package
        final EPackage ePackage = newEPackageBuilder()
                .withName("TestBooleanTypesPackage")
                .withNsPrefix("test")
                .withNsURI("http:///com.example.test.ecore")
                .withEClassifiers(eClass)
                .build();
        logger.info("Create TestBooleanTypesPackage EPackage");

        // add eth to asmModel
        asmModel.addContent(ePackage);
        logger.info("Add TestBooleanTypesPackage to asmModel");

        // transform previously created asm model to rdbms model
        executeTransformation("testBooleanTypes");

        Object[] rdbmsTables = rdbmsModelResourceSupport.getStreamOfRdbmsRdbmsTable().toArray();
        assertEquals(1, rdbmsTables.length);
        assertTrue(rdbmsTables[0] instanceof RdbmsTable);
        assertEquals("TestBooleanTypesPackage.TestBooleanTypesClass", ((RdbmsTable) rdbmsTables[0]).getName());

        //check eattributes (fields)
        EList<RdbmsField> rdbmsFields = ((RdbmsTable) rdbmsTables[0]).getFields();
        assertEquals(3, rdbmsFields.size()); //+2 type and id
        final String fqname = "TestBooleanTypesPackage.TestBooleanTypesClass#";
        assertEquals(BOOLEAN,
                rdbmsFields.stream()
                        .filter(rdbmsField -> (fqname + "booleanAttr").equals(rdbmsField.getName()))
                        .findAny().get().getRdbmsTypeName());

        //check primary key
        assertNotNull(((RdbmsTable) rdbmsTables[0]).getPrimaryKey());
    }

}
