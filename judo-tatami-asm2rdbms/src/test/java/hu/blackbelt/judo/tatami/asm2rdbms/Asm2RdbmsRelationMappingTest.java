package hu.blackbelt.judo.tatami.asm2rdbms;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.judo.meta.rdbms.RdbmsForeignKey;
import hu.blackbelt.judo.meta.rdbms.RdbmsIdentifierField;
import hu.blackbelt.judo.meta.rdbms.RdbmsJunctionTable;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.eclipse.emf.ecore.util.builder.EcoreBuilders.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Asm2RdbmsRelationMappingTest extends Asm2RdbmsMappingTestBase {
    //////////////////////////////////////////////////
    /////////////// ONE WAY RELATIONS ////////////////

    @Test
    @DisplayName("Test OneWayRelation With Null To One Cardinality")
    public void testOneWayRelationWithNullToOneCardinality() {
        // create annotation
        EAnnotation eAnnotation = newEAnnotationBuilder()
                .withSource("http://blackbelt.hu/judo/meta/ExtendedMetadata/entity")
                .build();

        eAnnotation.getDetails().put("value", "true");

        // create class to
        final EClass oneWayRelationTo = newEClassBuilder()
                .withName("OneWayRelationTo")
                .withEAnnotations(eAnnotation)
                .build();


        // create annotation2
        EAnnotation eAnnotation2 = newEAnnotationBuilder()
                .withSource("http://blackbelt.hu/judo/meta/ExtendedMetadata/entity")
                .build();

        eAnnotation2.getDetails().put("value", "true");

        // create class from
        final EClass oneWayRelationFrom = newEClassBuilder()
                .withName("OneWayRelationFrom")
                .withEAnnotations(eAnnotation2)
                .withEStructuralFeatures(newEReferenceBuilder()
                        .withName("oneWayReference")
                        .withEType(oneWayRelationTo)
                        .withLowerBound(0)
                        .withUpperBound(1)
                        .build())
                .build();

        // create package
        final EPackage ePackage = newEPackageBuilder()
                .withName("TestEpackage")
                .withEClassifiers(ImmutableList.of(oneWayRelationFrom, oneWayRelationTo))
                .withNsPrefix("test")
                .withNsURI("http:///com.example.test.ecore")
                .build();

        // add content to asm model
        asmModel.addContent(ePackage);

        executeTransformation("testOneWayRelationWithNullToOneCardinality");

        final String RDBMS_TABLE_NAME_TO = "TestEpackage.OneWayRelationTo";
        final String RDBMS_TABLE_NAME_FROM = "TestEpackage.OneWayRelationFrom";
        final String ONE_WAY_REFERENCE = "oneWayReference";

        // check if tables exist
        assertEquals(2, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found")).size());

        // check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_TO)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_TO + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME_FROM, ONE_WAY_REFERENCE, false)
                        .orElseThrow(() -> new RuntimeException(ONE_WAY_REFERENCE + " attribute not found"))
                        .getReferenceKey());
    }

    @Test
    @DisplayName("Test OneWayRelation With Null To Infinite Cardinality")
    public void testOneWayRelationWithNullToInfiniteCardinality() {
        // create annotation
        EAnnotation eAnnotation = newEAnnotationBuilder()
                .withSource("http://blackbelt.hu/judo/meta/ExtendedMetadata/entity")
                .build();

        eAnnotation.getDetails().put("value", "true");

        // create class to
        final EClass oneWayRelationTo = newEClassBuilder()
                .withName("OneWayRelationTo")
                .withEAnnotations(eAnnotation)
                .build();


        // create annotation2
        EAnnotation eAnnotation2 = newEAnnotationBuilder()
                .withSource("http://blackbelt.hu/judo/meta/ExtendedMetadata/entity")
                .build();

        eAnnotation2.getDetails().put("value", "true");

        // create class from
        final EClass oneWayRelationFrom = newEClassBuilder()
                .withName("OneWayRelationFrom")
                .withEAnnotations(eAnnotation2)
                .withEStructuralFeatures(newEReferenceBuilder()
                        .withName("oneWayReference")
                        .withEType(oneWayRelationTo)
                        .withLowerBound(0)
                        .withUpperBound(-1)
                        .build())
                .build();

        // create package
        final EPackage ePackage = newEPackageBuilder()
                .withName("TestEpackage")
                .withEClassifiers(ImmutableList.of(oneWayRelationFrom, oneWayRelationTo))
                .withNsPrefix("test")
                .withNsURI("http:///com.example.test.ecore")
                .build();

        // add content to asm model
        asmModel.addContent(ePackage);

        executeTransformation("testOneWayRelationWithNullToInfiniteCardinality");

        final String RDBMS_TABLE_NAME_TO = "TestEpackage.OneWayRelationTo";
        final String RDBMS_TABLE_NAME_FROM = "TestEpackage.OneWayRelationFrom";
        final String ONE_WAY_REFERENCE = "oneWayReference";
        final String RDBMS_JUNCTION_TABLE_NAME = RDBMS_TABLE_NAME_FROM + "#" + ONE_WAY_REFERENCE + " to " + RDBMS_TABLE_NAME_TO;

        // check if tables exist
        assertEquals(3, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found")).size());

        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_FROM)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_FROM + " table not found"))
                .size());
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_TO)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_TO + " table not found"))
                .size());

        // save rdbmsJunctionTable
        RdbmsJunctionTable rdbmsJunctionTable = rdbmsUtils.getRdbmsJunctionTable(RDBMS_JUNCTION_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_JUNCTION_TABLE_NAME + " junction table not found"));

        // save rdbmsForeignKeys
        EList<RdbmsForeignKey> rdbmsForeignKeys = rdbmsUtils.getRdbmsForeignKeys(RDBMS_JUNCTION_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_JUNCTION_TABLE_NAME + " junction table not found"));

        // save primaryKeys
        RdbmsIdentifierField primaryKeyFrom = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_FROM)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_FROM + " table not found"))
                .getPrimaryKey();
        RdbmsIdentifierField primaryKeyTo = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_TO)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_TO + " table not found"))
                .getPrimaryKey();

        // assertions
        assertTrue(rdbmsForeignKeys.stream().anyMatch(o -> o.getReferenceKey().equals(primaryKeyFrom)));
        assertTrue(rdbmsForeignKeys.stream().anyMatch(o -> o.getReferenceKey().equals(primaryKeyTo)));

        assertEquals(rdbmsJunctionTable.getField1().getReferenceKey(), primaryKeyTo);
        assertEquals(rdbmsJunctionTable.getField2().getReferenceKey(), primaryKeyFrom);

    }

    @Test
    @DisplayName("Test OneWayRelation With One To One Cardinality")
    public void testOneWayRelationWithOneToOneCardinality() { //This test might be unnecessary though
        // create annotation
        EAnnotation eAnnotation = newEAnnotationBuilder()
                .withSource("http://blackbelt.hu/judo/meta/ExtendedMetadata/entity")
                .build();

        eAnnotation.getDetails().put("value", "true");

        // create class to
        final EClass oneWayRelationTo = newEClassBuilder()
                .withName("OneWayRelationTo")
                .withEAnnotations(eAnnotation)
                .build();


        // create annotation2
        EAnnotation eAnnotation2 = newEAnnotationBuilder()
                .withSource("http://blackbelt.hu/judo/meta/ExtendedMetadata/entity")
                .build();

        eAnnotation2.getDetails().put("value", "true");

        // create class from
        final EClass oneWayRelationFrom = newEClassBuilder()
                .withName("OneWayRelationFrom")
                .withEAnnotations(eAnnotation2)
                .withEStructuralFeatures(newEReferenceBuilder()
                        .withName("oneWayReference")
                        .withEType(oneWayRelationTo)
                        .withLowerBound(1)
                        .withUpperBound(1)
                        .build())
                .build();

        // create package
        final EPackage ePackage = newEPackageBuilder()
                .withName("TestEpackage")
                .withEClassifiers(ImmutableList.of(oneWayRelationFrom, oneWayRelationTo))
                .withNsPrefix("test")
                .withNsURI("http:///com.example.test.ecore")
                .build();

        // add content to asm model
        asmModel.addContent(ePackage);

        executeTransformation("testOneWayRelationWithOneToOneCardinality");

        final String RDBMS_TABLE_NAME_TO = "TestEpackage.OneWayRelationTo";
        final String RDBMS_TABLE_NAME_FROM = "TestEpackage.OneWayRelationFrom";
        final String ONE_WAY_REFERENCE = "oneWayReference";

        // check if tables exist
        assertEquals(2, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found")).size());

        // check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_TO)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_TO + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME_FROM, ONE_WAY_REFERENCE, false)
                        .orElseThrow(() -> new RuntimeException(ONE_WAY_REFERENCE + " attribute not found"))
                        .getReferenceKey());
    }

    //////////////////////////////////////////////////
    /////////////// TWO WAY RELATIONS ////////////////

    @Test
    @DisplayName("Test TwoWayRelation With Null To One Cardinalities")
    public void testTwoWayRelationWithNullToOneCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEAnnotationBuilder()
                .withSource("http://blackbelt.hu/judo/meta/ExtendedMetadata/entity")
                .build();

        eAnnotation.getDetails().put("value", "true");

        final EAnnotation eAnnotation2 = newEAnnotationBuilder()
                .withSource("http://blackbelt.hu/judo/meta/ExtendedMetadata/entity")
                .build();

        eAnnotation2.getDetails().put("value", "true");

        // create classes
        final EClass twoWayRelation1 = newEClassBuilder()
                .withName("TwoWayRelation1")
                .withEAnnotations(eAnnotation)
                .build();

        final EClass twoWayRelation2 = newEClassBuilder()
                .withName("TwoWayRelation2")
                .withEAnnotations(eAnnotation2)
                .build();

        // create references
        final EReference twoWayReference1 = newEReferenceBuilder()
                .withName("twoWayReference1")
                .withLowerBound(0)
                .withUpperBound(1)
                .withEType(twoWayRelation1)
                .build();

        final EReference twoWayReference2 = newEReferenceBuilder()
                .withName("twoWayReference2")
                .withLowerBound(0)
                .withUpperBound(1)
                .withEType(twoWayRelation2)
                .build();

        // add opposites
        twoWayReference1.setEOpposite(twoWayReference2);
        twoWayReference2.setEOpposite(twoWayReference1);

        // add references
        twoWayRelation1.getEStructuralFeatures().add(twoWayReference2);
        twoWayRelation2.getEStructuralFeatures().add(twoWayReference1);

        // create package
        final EPackage ePackage = newEPackageBuilder()
                .withName("TestEpackage")
                .withEClassifiers(ImmutableList.of(twoWayRelation1, twoWayRelation2))
                .withNsPrefix("test")
                .withNsURI("http:///com.example.test.ecore")
                .build();

        // add content to asm model
        asmModel.addContent(ePackage);

        executeTransformation("testTwoWayRelationWithNullToOneCardinalities");

        final String RDBMS_TABLE_NAME_1 = "TestEpackage.TwoWayRelation1";
        final String RDBMS_TABLE_NAME_2 = "TestEpackage.TwoWayRelation2";
        final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        //final String TWO_WAY_REFERENCE2 = "twoWayReference2";

        // check if tables exist
        assertEquals(2, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_1)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                .size()); //+2 id and type

        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_2)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                .size());  //+2 id and type

        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_1)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME_2, TWO_WAY_REFERENCE1, false)
                        .orElseThrow(() -> new RuntimeException(TWO_WAY_REFERENCE1 + " attribute not found"))
                        .getReferenceKey());
    }
    @Test
    @DisplayName("Test TwoWayRelation With One To One Cardinalities")
    public void testTwoWayRelationWithOneToOneCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEAnnotationBuilder()
                .withSource("http://blackbelt.hu/judo/meta/ExtendedMetadata/entity")
                .build();

        eAnnotation.getDetails().put("value", "true");

        final EAnnotation eAnnotation2 = newEAnnotationBuilder()
                .withSource("http://blackbelt.hu/judo/meta/ExtendedMetadata/entity")
                .build();

        eAnnotation2.getDetails().put("value", "true");

        // create classes
        final EClass twoWayRelation1 = newEClassBuilder()
                .withName("TwoWayRelation1")
                .withEAnnotations(eAnnotation)
                .build();

        final EClass twoWayRelation2 = newEClassBuilder()
                .withName("TwoWayRelation2")
                .withEAnnotations(eAnnotation2)
                .build();

        // create references
        final EReference twoWayReference1 = newEReferenceBuilder()
                .withName("twoWayReference1")
                .withLowerBound(1)
                .withUpperBound(1)
                .withEType(twoWayRelation1)
                .build();

        final EReference twoWayReference2 = newEReferenceBuilder()
                .withName("twoWayReference2")
                .withLowerBound(1)
                .withUpperBound(1)
                .withEType(twoWayRelation2)
                .build();

        // add opposites
        twoWayReference1.setEOpposite(twoWayReference2);
        twoWayReference2.setEOpposite(twoWayReference1);

        // add references
        twoWayRelation1.getEStructuralFeatures().add(twoWayReference2);
        twoWayRelation2.getEStructuralFeatures().add(twoWayReference1);

        // create package
        final EPackage ePackage = newEPackageBuilder()
                .withName("TestEpackage")
                .withEClassifiers(ImmutableList.of(twoWayRelation1, twoWayRelation2))
                .withNsPrefix("test")
                .withNsURI("http:///com.example.test.ecore")
                .build();

        // add content to asm model
        asmModel.addContent(ePackage);

        executeTransformation("testTwoWayRelationWithOneToOneCardinalities");

        final String RDBMS_TABLE_NAME_1 = "TestEpackage.TwoWayRelation1";
        final String RDBMS_TABLE_NAME_2 = "TestEpackage.TwoWayRelation2";
        final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        //final String TWO_WAY_REFERENCE2 = "twoWayReference2";

        // check if tables exist
        assertEquals(2, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

//        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_1)
//                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
//                .size()); //+2 id and type
//
//        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_2)
//                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
//                .size());  //+2 id and type

        //TODO: fix transformation rule
//        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_1)
//                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
//                        .getPrimaryKey(),
//                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME_2, TWO_WAY_REFERENCE1, false)
//                        .orElseThrow(() -> new RuntimeException(TWO_WAY_REFERENCE1 + " attribute not found"))
//                        .getReferenceKey());
    }

    @Test
    @DisplayName("Test TwoWayRelation With Null To Infinite Cardinalities")
    public void testTwoWayRelationWithNullToInfiniteCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEAnnotationBuilder()
                .withSource("http://blackbelt.hu/judo/meta/ExtendedMetadata/entity")
                .build();

        eAnnotation.getDetails().put("value", "true");

        final EAnnotation eAnnotation2 = newEAnnotationBuilder()
                .withSource("http://blackbelt.hu/judo/meta/ExtendedMetadata/entity")
                .build();

        eAnnotation2.getDetails().put("value", "true");

        // create classes
        final EClass twoWayRelation1 = newEClassBuilder()
                .withName("TwoWayRelation1")
                .withEAnnotations(eAnnotation)
                .build();

        final EClass twoWayRelation2 = newEClassBuilder()
                .withName("TwoWayRelation2")
                .withEAnnotations(eAnnotation2)
                .build();

        // create references
        final EReference twoWayReference1 = newEReferenceBuilder()
                .withName("twoWayReference1")
                .withLowerBound(0)
                .withUpperBound(-1)
                .withEType(twoWayRelation1)
                .build();

        final EReference twoWayReference2 = newEReferenceBuilder()
                .withName("twoWayReference2")
                .withLowerBound(0)
                .withUpperBound(-1)
                .withEType(twoWayRelation2)
                .build();

        // add opposites
        twoWayReference1.setEOpposite(twoWayReference2);
        twoWayReference2.setEOpposite(twoWayReference1);

        // add references
        twoWayRelation1.getEStructuralFeatures().add(twoWayReference2);
        twoWayRelation2.getEStructuralFeatures().add(twoWayReference1);

        // create package
        final EPackage ePackage = newEPackageBuilder()
                .withName("TestEpackage")
                .withEClassifiers(ImmutableList.of(twoWayRelation1, twoWayRelation2))
                .withNsPrefix("test")
                .withNsURI("http:///com.example.test.ecore")
                .build();

        // add content to asm model
        asmModel.addContent(ePackage);

        executeTransformation("testTwoWayRelationWithNullToInfiniteCardinalities");

        final String RDBMS_TABLE_NAME_1 = "TestEpackage.TwoWayRelation1";
        final String RDBMS_TABLE_NAME_2 = "TestEpackage.TwoWayRelation2";
        final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        final String TWO_WAY_REFERENCE2 = "twoWayReference2";

        // check if tables exist
        assertEquals(3, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_1)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                .size()); //+2 id and type

        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_2)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                .size());  //+2 id and type

        // save rdbmsJunctionTable
        RdbmsJunctionTable rdbmsJunctionTable =
                rdbmsUtils.getRdbmsJunctionTable(RDBMS_TABLE_NAME_2 + "#" + TWO_WAY_REFERENCE1 + " to " + RDBMS_TABLE_NAME_1 + "#" + TWO_WAY_REFERENCE2)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + "#" + TWO_WAY_REFERENCE1 + " to " + RDBMS_TABLE_NAME_1 + "#" + TWO_WAY_REFERENCE2 +
                                " junction table not found"));

        // save rdbmsForeignKeys
        EList<RdbmsForeignKey> rdbmsForeignKeys = rdbmsUtils.getRdbmsForeignKeys(RDBMS_TABLE_NAME_2 + "#" + TWO_WAY_REFERENCE1 + " to " + RDBMS_TABLE_NAME_1 + "#" + TWO_WAY_REFERENCE2)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + "#" + TWO_WAY_REFERENCE1 + " to " + RDBMS_TABLE_NAME_1 + "#" + TWO_WAY_REFERENCE2 +
                        " junction table not found"));

        // save primaryKeys
        RdbmsIdentifierField primaryKey1 = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_1)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                .getPrimaryKey();
        RdbmsIdentifierField primaryKey2 = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_2)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                .getPrimaryKey();

        // assertions
        assertTrue(rdbmsForeignKeys.stream().anyMatch(o -> o.getReferenceKey().equals(primaryKey1)));
        assertTrue(rdbmsForeignKeys.stream().anyMatch(o -> o.getReferenceKey().equals(primaryKey2)));

        assertEquals(rdbmsJunctionTable.getField1().getReferenceKey(), primaryKey1);
        assertEquals(rdbmsJunctionTable.getField2().getReferenceKey(), primaryKey2);
    }

}
