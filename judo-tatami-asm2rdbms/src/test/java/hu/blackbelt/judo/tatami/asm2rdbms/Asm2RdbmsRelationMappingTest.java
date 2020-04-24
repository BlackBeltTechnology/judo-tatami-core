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
    private void testOneWayRelation(int lowerCardinality, int upperCardinality, boolean isContainment, boolean isSelf) {
        if(upperCardinality != -1 && lowerCardinality > upperCardinality)
            throw new IllegalArgumentException(String.format("Invalid cardinalities: %d - %d", lowerCardinality, upperCardinality));
        //TODO
    }

    private void testTwoWayRelation(int lowerCardinality1, int upperCardinality1, int lowerCardinality2, int upperCardinality2, boolean isSelf) {
        //TODO
    }

    @Test
    @DisplayName("Test OneWayRelation With Null To Infinite Cardinality")
    public void testOneWayRelationWithNullToInfiniteCardinality() {
        // create annotation
        EAnnotation eAnnotation = newEntityEAnnotation();

        // create class to
        final EClass oneWayRelationTo = newEClassBuilder()
                .withName("OneWayRelationTo")
                .withEAnnotations(eAnnotation)
                .build();


        // create annotation2
        EAnnotation eAnnotation2 = newEntityEAnnotation();

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

        // ASSERTION - check if tables exist
        assertEquals(3, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_FROM)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_FROM + " table not found"))
                .size());
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_TO)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_TO + " table not found"))
                .size());

        // SAVE - rdbmsJunctionTable
        RdbmsJunctionTable rdbmsJunctionTable = rdbmsUtils.getRdbmsJunctionTable(RDBMS_JUNCTION_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_JUNCTION_TABLE_NAME + " junction table not found"));

        // SAVE - rdbmsForeignKeys
        EList<RdbmsForeignKey> rdbmsForeignKeys = rdbmsUtils.getRdbmsForeignKeys(RDBMS_JUNCTION_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_JUNCTION_TABLE_NAME + " junction table not found"));

        // SAVE - primaryKeys
        RdbmsIdentifierField primaryKeyFrom = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_FROM)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_FROM + " table not found"))
                .getPrimaryKey();
        RdbmsIdentifierField primaryKeyTo = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_TO)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_TO + " table not found"))
                .getPrimaryKey();

        // ASSERTION - check if correct keys are in junction table
        assertTrue(rdbmsForeignKeys.stream().anyMatch(o -> o.getReferenceKey().equals(primaryKeyFrom)));
        assertTrue(rdbmsForeignKeys.stream().anyMatch(o -> o.getReferenceKey().equals(primaryKeyTo)));

        // ASSERTION - check if field1 and field2 is the primary keys from "From" and "To"
        assertEquals(rdbmsJunctionTable.getField1().getReferenceKey(), primaryKeyTo);
        assertEquals(rdbmsJunctionTable.getField2().getReferenceKey(), primaryKeyFrom);

    }

    @Test
    @DisplayName("Test OneWayRelation With One To Infinite Cardinality")
    public void testOneWayRelationWithOneToInfiniteCardinality() {
        // create annotation
        EAnnotation eAnnotation = newEntityEAnnotation();

        // create class to
        final EClass oneWayRelationTo = newEClassBuilder()
                .withName("OneWayRelationTo")
                .withEAnnotations(eAnnotation)
                .build();


        // create annotation2
        EAnnotation eAnnotation2 = newEntityEAnnotation();

        // create class from
        final EClass oneWayRelationFrom = newEClassBuilder()
                .withName("OneWayRelationFrom")
                .withEAnnotations(eAnnotation2)
                .withEStructuralFeatures(newEReferenceBuilder()
                        .withName("oneWayReference")
                        .withEType(oneWayRelationTo)
                        .withLowerBound(1)
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

        executeTransformation("testOneWayRelationWithOneToInfiniteCardinality");

        final String RDBMS_TABLE_NAME_TO = "TestEpackage.OneWayRelationTo";
        final String RDBMS_TABLE_NAME_FROM = "TestEpackage.OneWayRelationFrom";
        final String ONE_WAY_REFERENCE = "oneWayReference";
        final String RDBMS_JUNCTION_TABLE_NAME = RDBMS_TABLE_NAME_FROM + "#" + ONE_WAY_REFERENCE + " to " + RDBMS_TABLE_NAME_TO;

        // ASSERTION - check if tables exist
        assertEquals(3, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_FROM)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_FROM + " table not found"))
                .size());
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_TO)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_TO + " table not found"))
                .size());

        // SAVE - rdbmsJunctionTable
        RdbmsJunctionTable rdbmsJunctionTable = rdbmsUtils.getRdbmsJunctionTable(RDBMS_JUNCTION_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_JUNCTION_TABLE_NAME + " junction table not found"));

        // SAVE - rdbmsForeignKeys
        EList<RdbmsForeignKey> rdbmsForeignKeys = rdbmsUtils.getRdbmsForeignKeys(RDBMS_JUNCTION_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_JUNCTION_TABLE_NAME + " junction table not found"));

        // SAVE - primaryKeys
        RdbmsIdentifierField primaryKeyFrom = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_FROM)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_FROM + " table not found"))
                .getPrimaryKey();
        RdbmsIdentifierField primaryKeyTo = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_TO)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_TO + " table not found"))
                .getPrimaryKey();

        // ASSERTION - check if correct keys are in junction table
        assertTrue(rdbmsForeignKeys.stream().anyMatch(o -> o.getReferenceKey().equals(primaryKeyFrom)));
        assertTrue(rdbmsForeignKeys.stream().anyMatch(o -> o.getReferenceKey().equals(primaryKeyTo)));

        // ASSERTION - check if field1 and field2 is the primary keys from "From" and "To"
        assertEquals(rdbmsJunctionTable.getField1().getReferenceKey(), primaryKeyTo);
        assertEquals(rdbmsJunctionTable.getField2().getReferenceKey(), primaryKeyFrom);

    }

    @Test
    @DisplayName("Test OneWayRelation With Null To One Cardinality")
    public void testOneWayRelationWithNullToOneCardinality() {
        // create annotation
        EAnnotation eAnnotation = newEntityEAnnotation();

        // create class to
        final EClass oneWayRelationTo = newEClassBuilder()
                .withName("OneWayRelationTo")
                .withEAnnotations(eAnnotation)
                .build();


        // create annotation2
        EAnnotation eAnnotation2 = newEntityEAnnotation();

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

        // ASSERTION - check if tables exist
        assertEquals(2, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_FROM)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_FROM + " not found"))
                .size());
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_TO)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_TO + " not found"))
                .size());

        // ASSERTION - check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_TO)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_TO + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME_FROM, ONE_WAY_REFERENCE)
                        .orElseThrow(() -> new RuntimeException(ONE_WAY_REFERENCE + " attribute not found"))
                        .getReferenceKey());
    }

    @Test
    @DisplayName("Test OneWayRelation With One To One Cardinality")
    public void testOneWayRelationWithOneToOneCardinality() {
        // create annotation
        EAnnotation eAnnotation = newEntityEAnnotation();

        // create class to
        final EClass oneWayRelationTo = newEClassBuilder()
                .withName("OneWayRelationTo")
                .withEAnnotations(eAnnotation)
                .build();


        // create annotation2
        EAnnotation eAnnotation2 = newEntityEAnnotation();

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

        // ASSERTION - check if tables exist
        assertEquals(2, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_FROM)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_FROM + " not found"))
                .size());
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_TO)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_TO + " not found"))
                .size());

        // ASSERTION - check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_TO)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_TO + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME_FROM, ONE_WAY_REFERENCE)
                        .orElseThrow(() -> new RuntimeException(ONE_WAY_REFERENCE + " attribute not found"))
                        .getReferenceKey());
    }

    @Test
    @DisplayName("Test OneWayContainment With Null To Infinite Cardinality")
    public void testOneWayContainmentWithNullToInfiniteCardinality() {
        // create annotation
        EAnnotation eAnnotation = newEntityEAnnotation();

        // create class to
        final EClass oneWayContainmentTo = newEClassBuilder()
                .withName("OneWayContainmentTo")
                .withEAnnotations(eAnnotation)
                .build();


        // create annotation2
        EAnnotation eAnnotation2 = newEntityEAnnotation();

        // create class from
        final EClass oneWayContainmentFrom = newEClassBuilder()
                .withName("OneWayContainmentFrom")
                .withEAnnotations(eAnnotation2)
                .withEStructuralFeatures(newEReferenceBuilder()
                        .withName("oneWayContainment")
                        .withEType(oneWayContainmentTo)
                        .withContainment(true)
                        .withLowerBound(0)
                        .withUpperBound(-1)
                        .build())
                .build();

        // create package
        final EPackage ePackage = newEPackageBuilder()
                .withName("TestEpackage")
                .withEClassifiers(ImmutableList.of(oneWayContainmentFrom, oneWayContainmentTo))
                .withNsPrefix("test")
                .withNsURI("http:///com.example.test.ecore")
                .build();

        // add content to asm model
        asmModel.addContent(ePackage);

        executeTransformation("testOneWayContainmentWithNullToInfiniteCardinality");

        final String RDBMS_TABLE_NAME_TO = "TestEpackage.OneWayContainmentTo";
        final String RDBMS_TABLE_NAME_FROM = "TestEpackage.OneWayContainmentFrom";
        // final String ONE_WAY_CONTAINMENT = "oneWayContainment";
        final String ONE_WAY_CONTAINMENT = "oneWayContainmentFromOneWayContainment";

        // ASSERTION - check if tables exist
        assertEquals(2, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_FROM)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_FROM + " not found"))
                .size());
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_TO)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_TO + " not found"))
                .size());

        // ASSERTION - check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_FROM)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_FROM + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME_TO, ONE_WAY_CONTAINMENT)
                        .orElseThrow(() -> new RuntimeException(ONE_WAY_CONTAINMENT + " attribute not found"))
                        .getReferenceKey());
    }

    @Test
    @DisplayName("Test OneWayContainment With One To Infinite Cardinality")
    public void testOneWayContainmentWithOneToInfiniteCardinality() {
        // create annotation
        EAnnotation eAnnotation = newEntityEAnnotation();

        // create class to
        final EClass oneWayContainmentTo = newEClassBuilder()
                .withName("OneWayContainmentTo")
                .withEAnnotations(eAnnotation)
                .build();


        // create annotation2
        EAnnotation eAnnotation2 = newEntityEAnnotation();

        // create class from
        final EClass oneWayContainmentFrom = newEClassBuilder()
                .withName("OneWayContainmentFrom")
                .withEAnnotations(eAnnotation2)
                .withEStructuralFeatures(newEReferenceBuilder()
                        .withName("oneWayContainment")
                        .withEType(oneWayContainmentTo)
                        .withContainment(true)
                        .withLowerBound(1)
                        .withUpperBound(-1)
                        .build())
                .build();

        // create package
        final EPackage ePackage = newEPackageBuilder()
                .withName("TestEpackage")
                .withEClassifiers(ImmutableList.of(oneWayContainmentFrom, oneWayContainmentTo))
                .withNsPrefix("test")
                .withNsURI("http:///com.example.test.ecore")
                .build();

        // add content to asm model
        asmModel.addContent(ePackage);

        executeTransformation("testOneWayContainmentWithOneToInfiniteCardinality");

        final String RDBMS_TABLE_NAME_TO = "TestEpackage.OneWayContainmentTo";
        final String RDBMS_TABLE_NAME_FROM = "TestEpackage.OneWayContainmentFrom";
        // final String ONE_WAY_CONTAINMENT = "oneWayContainment";
        final String ONE_WAY_CONTAINMENT = "oneWayContainmentFromOneWayContainment";

        // ASSERTION - check if tables exist
        assertEquals(2, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_FROM)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_FROM + " not found"))
                .size());
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_TO)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_TO + " not found"))
                .size());

        // ASSERTION - check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_FROM)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_FROM + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME_TO, ONE_WAY_CONTAINMENT)
                        .orElseThrow(() -> new RuntimeException(ONE_WAY_CONTAINMENT + " attribute not found"))
                        .getReferenceKey());
    }

    @Test
    @DisplayName("Test OneWayContainment With Null To One Cardinality")
    public void testOneWayContainmentWithNullToOneCardinality() {
        // create annotation
        EAnnotation eAnnotation = newEntityEAnnotation();

        // create class to
        final EClass oneWayContainmentTo = newEClassBuilder()
                .withName("OneWayContainmentTo")
                .withEAnnotations(eAnnotation)
                .build();


        // create annotation2
        EAnnotation eAnnotation2 = newEntityEAnnotation();

        // create class from
        final EClass oneWayContainmentFrom = newEClassBuilder()
                .withName("OneWayContainmentFrom")
                .withEAnnotations(eAnnotation2)
                .withEStructuralFeatures(newEReferenceBuilder()
                        .withName("oneWayContainment")
                        .withEType(oneWayContainmentTo)
                        .withContainment(true)
                        .withLowerBound(0)
                        .withUpperBound(1)
                        .build())
                .build();

        // create package
        final EPackage ePackage = newEPackageBuilder()
                .withName("TestEpackage")
                .withEClassifiers(ImmutableList.of(oneWayContainmentFrom, oneWayContainmentTo))
                .withNsPrefix("test")
                .withNsURI("http:///com.example.test.ecore")
                .build();

        // add content to asm model
        asmModel.addContent(ePackage);

        executeTransformation("testOneWayContainmentWithNullToOneCardinality");

        final String RDBMS_TABLE_NAME_TO = "TestEpackage.OneWayContainmentTo";
        final String RDBMS_TABLE_NAME_FROM = "TestEpackage.OneWayContainmentFrom";
        // final String ONE_WAY_CONTAINMENT = "oneWayContainment";
        final String ONE_WAY_CONTAINMENT = "oneWayContainmentFromOneWayContainment";

        // ASSERTION - check if tables exist
        assertEquals(2, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_FROM)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_FROM + " not found"))
                .size());
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_TO)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_TO + " not found"))
                .size());

        // ASSERTION - check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_FROM)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_FROM + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME_TO, ONE_WAY_CONTAINMENT)
                        .orElseThrow(() -> new RuntimeException(ONE_WAY_CONTAINMENT + " attribute not found"))
                        .getReferenceKey());
    }

    @Test
    @DisplayName("Test OneWayContainment With One To One Cardinality")
    public void testOneWayContainmentWithOneToOneCardinality() {
        // create annotation
        EAnnotation eAnnotation = newEntityEAnnotation();

        // create class to
        final EClass oneWayContainmentTo = newEClassBuilder()
                .withName("OneWayContainmentTo")
                .withEAnnotations(eAnnotation)
                .build();


        // create annotation2
        EAnnotation eAnnotation2 = newEntityEAnnotation();

        // create class from
        final EClass oneWayContainmentFrom = newEClassBuilder()
                .withName("OneWayContainmentFrom")
                .withEAnnotations(eAnnotation2)
                .withEStructuralFeatures(newEReferenceBuilder()
                        .withName("oneWayContainment")
                        .withEType(oneWayContainmentTo)
                        .withContainment(true)
                        .withLowerBound(1)
                        .withUpperBound(1)
                        .build())
                .build();

        // create package
        final EPackage ePackage = newEPackageBuilder()
                .withName("TestEpackage")
                .withEClassifiers(ImmutableList.of(oneWayContainmentFrom, oneWayContainmentTo))
                .withNsPrefix("test")
                .withNsURI("http:///com.example.test.ecore")
                .build();

        // add content to asm model
        asmModel.addContent(ePackage);

        executeTransformation("testOneWayContainmentWithOneToOneCardinality");

        final String RDBMS_TABLE_NAME_TO = "TestEpackage.OneWayContainmentTo";
        final String RDBMS_TABLE_NAME_FROM = "TestEpackage.OneWayContainmentFrom";
        // final String ONE_WAY_CONTAINMENT = "oneWayContainment";
        final String ONE_WAY_CONTAINMENT = "oneWayContainmentFromOneWayContainment";

        // ASSERTION - check if tables exist
        assertEquals(2, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_FROM)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_FROM + " not found"))
                .size());
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_TO)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_TO + " not found"))
                .size());

        // ASSERTION - check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_FROM)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_FROM + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME_TO, ONE_WAY_CONTAINMENT)
                        .orElseThrow(() -> new RuntimeException(ONE_WAY_CONTAINMENT + " attribute not found"))
                        .getReferenceKey());
    }

    @Test
    @DisplayName("Test TwoWayRelation With Null To Infinite Cardinalities")
    public void testTwoWayRelationWithNullToInfiniteCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        final EAnnotation eAnnotation2 = newEntityEAnnotation();

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
        final String RDBMS_JUNCTION_TABLE = RDBMS_TABLE_NAME_2 + "#" + TWO_WAY_REFERENCE1 + " to " + RDBMS_TABLE_NAME_1 + "#" + TWO_WAY_REFERENCE2;

        // ASSERTION - check if tables exist
        assertEquals(3, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_1)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                .size()); //+2 id and type

        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_2)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                .size());  //+2 id and type

        // SAVE - rdbmsJunctionTable
        RdbmsJunctionTable rdbmsJunctionTable =
                rdbmsUtils.getRdbmsJunctionTable(RDBMS_JUNCTION_TABLE)
                        .orElseThrow(() -> new RuntimeException(RDBMS_JUNCTION_TABLE + " junction table not found"));

        // SAVE - rdbmsForeignKeys
        EList<RdbmsForeignKey> rdbmsForeignKeys = rdbmsUtils.getRdbmsForeignKeys(RDBMS_JUNCTION_TABLE)
                .orElseThrow(() -> new RuntimeException(RDBMS_JUNCTION_TABLE + " junction table not found"));

        // SAVE - primaryKeys
        RdbmsIdentifierField primaryKey1 = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_1)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                .getPrimaryKey();
        RdbmsIdentifierField primaryKey2 = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_2)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                .getPrimaryKey();

        // ASSERTION - check if correct keys are in junction table
        assertTrue(rdbmsForeignKeys.stream().anyMatch(o -> o.getReferenceKey().equals(primaryKey1)));
        assertTrue(rdbmsForeignKeys.stream().anyMatch(o -> o.getReferenceKey().equals(primaryKey2)));

        // ASSERTION - check if field1 and field2 is the primary keys from "From" and "To"
        assertEquals(rdbmsJunctionTable.getField1().getReferenceKey(), primaryKey1);
        assertEquals(rdbmsJunctionTable.getField2().getReferenceKey(), primaryKey2);
    }

    @Test
    @DisplayName("Test TwoWayRelation With One To Infinite And Null To Infinite Cardinalities")
    public void testTwoWayRelationWithOneToInfiniteAndNullToInfiniteCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        final EAnnotation eAnnotation2 = newEntityEAnnotation();

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

        executeTransformation("testTwoWayRelationWithOneToInfiniteAndNullToInfiniteCardinalities");

        final String RDBMS_TABLE_NAME_1 = "TestEpackage.TwoWayRelation1";
        final String RDBMS_TABLE_NAME_2 = "TestEpackage.TwoWayRelation2";
        final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        final String TWO_WAY_REFERENCE2 = "twoWayReference2";
        final String RDBMS_JUNCTION_TABLE = RDBMS_TABLE_NAME_2 + "#" + TWO_WAY_REFERENCE1 + " to " + RDBMS_TABLE_NAME_1 + "#" + TWO_WAY_REFERENCE2;

        // ASSERTION - check if tables exist
        assertEquals(3, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_1)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                .size()); //+2 id and type

        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_2)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                .size());  //+2 id and type

        // SAVE - rdbmsJunctionTable
        RdbmsJunctionTable rdbmsJunctionTable =
                rdbmsUtils.getRdbmsJunctionTable(RDBMS_JUNCTION_TABLE)
                        .orElseThrow(() -> new RuntimeException(RDBMS_JUNCTION_TABLE + " junction table not found"));

        // SAVE - rdbmsForeignKeys
        EList<RdbmsForeignKey> rdbmsForeignKeys = rdbmsUtils.getRdbmsForeignKeys(RDBMS_JUNCTION_TABLE)
                .orElseThrow(() -> new RuntimeException(RDBMS_JUNCTION_TABLE + " junction table not found"));

        // SAVE - primaryKeys
        RdbmsIdentifierField primaryKey1 = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_1)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                .getPrimaryKey();
        RdbmsIdentifierField primaryKey2 = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_2)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                .getPrimaryKey();

        // ASSERTION - check if correct keys are in junction table
        assertTrue(rdbmsForeignKeys.stream().anyMatch(o -> o.getReferenceKey().equals(primaryKey1)));
        assertTrue(rdbmsForeignKeys.stream().anyMatch(o -> o.getReferenceKey().equals(primaryKey2)));

        // ASSERTION - check if field1 and field2 is the primary keys from "From" and "To"
        assertEquals(rdbmsJunctionTable.getField1().getReferenceKey(), primaryKey1);
        assertEquals(rdbmsJunctionTable.getField2().getReferenceKey(), primaryKey2);
    }

    @Test
    @DisplayName("Test TwoWayRelation With Null To Infinite And One To Infinite Cardinalities")
    public void testTwoWayRelationWithNullToInfiniteAndOneToInfiniteCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        final EAnnotation eAnnotation2 = newEntityEAnnotation();

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
                .withLowerBound(1)
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

        executeTransformation("testTwoWayRelationWithNullToInfiniteAndOneToInfiniteCardinalities");

        final String RDBMS_TABLE_NAME_1 = "TestEpackage.TwoWayRelation1";
        final String RDBMS_TABLE_NAME_2 = "TestEpackage.TwoWayRelation2";
        final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        final String TWO_WAY_REFERENCE2 = "twoWayReference2";
        final String RDBMS_JUNCTION_TABLE = RDBMS_TABLE_NAME_2 + "#" + TWO_WAY_REFERENCE1 + " to " + RDBMS_TABLE_NAME_1 + "#" + TWO_WAY_REFERENCE2;

        // ASSERTION - check if tables exist
        assertEquals(3, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_1)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                .size()); //+2 id and type

        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_2)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                .size());  //+2 id and type

        // SAVE - rdbmsJunctionTable
        RdbmsJunctionTable rdbmsJunctionTable =
                rdbmsUtils.getRdbmsJunctionTable(RDBMS_JUNCTION_TABLE)
                        .orElseThrow(() -> new RuntimeException(RDBMS_JUNCTION_TABLE + " junction table not found"));

        // SAVE - rdbmsForeignKeys
        EList<RdbmsForeignKey> rdbmsForeignKeys = rdbmsUtils.getRdbmsForeignKeys(RDBMS_JUNCTION_TABLE)
                .orElseThrow(() -> new RuntimeException(RDBMS_JUNCTION_TABLE + " junction table not found"));

        // SAVE - primaryKeys
        RdbmsIdentifierField primaryKey1 = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_1)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                .getPrimaryKey();
        RdbmsIdentifierField primaryKey2 = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_2)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                .getPrimaryKey();

        // ASSERTION - check if correct keys are in junction table
        assertTrue(rdbmsForeignKeys.stream().anyMatch(o -> o.getReferenceKey().equals(primaryKey1)));
        assertTrue(rdbmsForeignKeys.stream().anyMatch(o -> o.getReferenceKey().equals(primaryKey2)));

        // ASSERTION - check if field1 and field2 is the primary keys from "From" and "To"
        assertEquals(rdbmsJunctionTable.getField1().getReferenceKey(), primaryKey1);
        assertEquals(rdbmsJunctionTable.getField2().getReferenceKey(), primaryKey2);
    }

    @Test
    @DisplayName("Test TwoWayRelation With One To Infinite Cardinalities")
    public void testTwoWayRelationWithOneToInfiniteCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        final EAnnotation eAnnotation2 = newEntityEAnnotation();

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
                .withUpperBound(-1)
                .withEType(twoWayRelation1)
                .build();

        final EReference twoWayReference2 = newEReferenceBuilder()
                .withName("twoWayReference2")
                .withLowerBound(1)
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

        executeTransformation("testTwoWayRelationWithOneToInfiniteCardinalities");

        final String RDBMS_TABLE_NAME_1 = "TestEpackage.TwoWayRelation1";
        final String RDBMS_TABLE_NAME_2 = "TestEpackage.TwoWayRelation2";
        final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        final String TWO_WAY_REFERENCE2 = "twoWayReference2";
        final String RDBMS_JUNCTION_TABLE = RDBMS_TABLE_NAME_2 + "#" + TWO_WAY_REFERENCE1 + " to " + RDBMS_TABLE_NAME_1 + "#" + TWO_WAY_REFERENCE2;

        // ASSERTION - check if tables exist
        assertEquals(3, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_1)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                .size()); //+2 id and type

        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_2)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                .size());  //+2 id and type

        // SAVE - rdbmsJunctionTable
        RdbmsJunctionTable rdbmsJunctionTable =
                rdbmsUtils.getRdbmsJunctionTable(RDBMS_JUNCTION_TABLE)
                        .orElseThrow(() -> new RuntimeException(RDBMS_JUNCTION_TABLE + " junction table not found"));

        // SAVE - rdbmsForeignKeys
        EList<RdbmsForeignKey> rdbmsForeignKeys = rdbmsUtils.getRdbmsForeignKeys(RDBMS_JUNCTION_TABLE)
                .orElseThrow(() -> new RuntimeException(RDBMS_JUNCTION_TABLE + " junction table not found"));

        // SAVE - primaryKeys
        RdbmsIdentifierField primaryKey1 = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_1)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                .getPrimaryKey();
        RdbmsIdentifierField primaryKey2 = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_2)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                .getPrimaryKey();

        // ASSERTION - check if correct keys are in junction table
        assertTrue(rdbmsForeignKeys.stream().anyMatch(o -> o.getReferenceKey().equals(primaryKey1)));
        assertTrue(rdbmsForeignKeys.stream().anyMatch(o -> o.getReferenceKey().equals(primaryKey2)));

        // ASSERTION - check if field1 and field2 is the primary keys from "From" and "To"
        assertEquals(rdbmsJunctionTable.getField1().getReferenceKey(), primaryKey1);
        assertEquals(rdbmsJunctionTable.getField2().getReferenceKey(), primaryKey2);
    }

    @Test
    @DisplayName("Test TwoWayRelation With Null To Infinite And Null To One Cardinalities")
    public void testTwoWayRelationWithNullToInfiniteAndNullToOneCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        final EAnnotation eAnnotation2 = newEntityEAnnotation();

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

        executeTransformation("testTwoWayRelationWithNullToInfiniteAndNullToOneCardinalities");

        final String RDBMS_TABLE_NAME_1 = "TestEpackage.TwoWayRelation1";
        final String RDBMS_TABLE_NAME_2 = "TestEpackage.TwoWayRelation2";
        // final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        final String TWO_WAY_REFERENCE2 = "twoWayReference2";

        // ASSERTION - check if number of tables is correct
        assertEquals(2, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables found"))
                .size());

        // ASSERTION - check if number of fields is correct in each table
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_1)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                .size());
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_2)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                .size());

        // ASSERTION - check if foreign key is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_2)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME_1, TWO_WAY_REFERENCE2)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " field not found"))
                        .getReferenceKey());

    }

    @Test
    @DisplayName("Test TwoWayRelation With Null To Infinite And One To One Cardinalities")
    public void testTwoWayRelationWithNullToInfiniteAndOneToOneCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        final EAnnotation eAnnotation2 = newEntityEAnnotation();

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

        executeTransformation("testTwoWayRelationWithNullToInfiniteAndOneToOneCardinalities");

        final String RDBMS_TABLE_NAME_1 = "TestEpackage.TwoWayRelation1";
        final String RDBMS_TABLE_NAME_2 = "TestEpackage.TwoWayRelation2";
        // final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        final String TWO_WAY_REFERENCE2 = "twoWayReference2";

        // ASSERTION - check if number of tables is correct
        assertEquals(2, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables found"))
                .size());

        // ASSERTION - check if number of fields is correct in each table
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_1)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                .size());
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_2)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                .size());

        // ASSERTION - check if foreign key is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_2)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME_1, TWO_WAY_REFERENCE2)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " field not found"))
                        .getReferenceKey());

    }

    @Test
    @DisplayName("Test TwoWayRelation With One To Infinite And Null To One Cardinalities")
    public void testTwoWayRelationWithOneToInfiniteAndNullToOneCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        final EAnnotation eAnnotation2 = newEntityEAnnotation();

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
                .withUpperBound(-1)
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

        executeTransformation("testTwoWayRelationWithOneToInfiniteAndNullToOneCardinalities");

        final String RDBMS_TABLE_NAME_1 = "TestEpackage.TwoWayRelation1";
        final String RDBMS_TABLE_NAME_2 = "TestEpackage.TwoWayRelation2";
        // final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        final String TWO_WAY_REFERENCE2 = "twoWayReference2";

        // ASSERTION - check if number of tables is correct
        assertEquals(2, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables found"))
                .size());

        // ASSERTION - check if number of fields is correct in each table
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_1)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                .size());
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_2)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                .size());

        // ASSERTION - check if foreign key is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_2)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME_1, TWO_WAY_REFERENCE2)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " field not found"))
                        .getReferenceKey());

    }

    @Test
    @DisplayName("Test TwoWayRelation With One To Infinite And One To One Cardinalities")
    public void testTwoWayRelationWithOneToInfiniteAndOneToOneCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        final EAnnotation eAnnotation2 = newEntityEAnnotation();

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
                .withUpperBound(-1)
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

        executeTransformation("testTwoWayRelationWithOneToInfiniteAndOneToOneCardinalities");

        final String RDBMS_TABLE_NAME_1 = "TestEpackage.TwoWayRelation1";
        final String RDBMS_TABLE_NAME_2 = "TestEpackage.TwoWayRelation2";
        // final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        final String TWO_WAY_REFERENCE2 = "twoWayReference2";

        // ASSERTION - check if number of tables is correct
        assertEquals(2, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables found"))
                .size());

        // ASSERTION - check if number of fields is correct in each table
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_1)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                .size());
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_2)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                .size());

        // ASSERTION - check if foreign key is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_2)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME_1, TWO_WAY_REFERENCE2)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " field not found"))
                        .getReferenceKey());

    }

    @Test
    @DisplayName("Test TwoWayRelation With Null To One And Null To Infinite Cardinalities")
    public void testTwoWayRelationWithNullToOneAndNullToInfiniteCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        final EAnnotation eAnnotation2 = newEntityEAnnotation();

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

        executeTransformation("testTwoWayRelationWithNullToOneAndNullToInfiniteCardinalities");

        final String RDBMS_TABLE_NAME_1 = "TestEpackage.TwoWayRelation1";
        final String RDBMS_TABLE_NAME_2 = "TestEpackage.TwoWayRelation2";
        final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        // final String TWO_WAY_REFERENCE2 = "twoWayReference2";

        // ASSERTION - check if number of tables is correct
        assertEquals(2, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables found"))
                .size());

        // ASSERTION - check if number of fields is correct in each table
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_1)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                .size());
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_2)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                .size());

        // ASSERTION - check if foreign key is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_1)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME_2, TWO_WAY_REFERENCE1)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " field not found"))
                        .getReferenceKey());

    }

    @Test
    @DisplayName("Test TwoWayRelation With Null To One And One To Infinite Cardinalities")
    public void testTwoWayRelationWithNullToOneAndOneToInfiniteCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        final EAnnotation eAnnotation2 = newEntityEAnnotation();

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

        executeTransformation("testTwoWayRelationWithNullToOneAndOneToInfiniteCardinalities");

        final String RDBMS_TABLE_NAME_1 = "TestEpackage.TwoWayRelation1";
        final String RDBMS_TABLE_NAME_2 = "TestEpackage.TwoWayRelation2";
        final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        // final String TWO_WAY_REFERENCE2 = "twoWayReference2";

        // ASSERTION - check if number of tables is correct
        assertEquals(2, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables found"))
                .size());

        // ASSERTION - check if number of fields is correct in each table
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_1)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                .size());
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_2)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                .size());

        // ASSERTION - check if foreign key is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_1)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME_2, TWO_WAY_REFERENCE1)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " field not found"))
                        .getReferenceKey());

    }

    @Test
    @DisplayName("Test TwoWayRelation With Null To One Cardinalities")
    public void testTwoWayRelationWithNullToOneCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        final EAnnotation eAnnotation2 = newEntityEAnnotation();

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

        // ASSERTION - check if tables exist
        assertEquals(2, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_1)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                .size()); //+2 id and type

        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_2)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                .size());  //+2 id and type

        // ASSERTION - check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_1)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME_2, TWO_WAY_REFERENCE1)
                        .orElseThrow(() -> new RuntimeException(TWO_WAY_REFERENCE1 + " attribute not found"))
                        .getReferenceKey());
    }


    @Test
    @DisplayName("Test TwoWayRelation With Null To One And One To One Cardinalities")
    public void testTwoWayRelationWithNullToOneAndOnetoOneCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        final EAnnotation eAnnotation2 = newEntityEAnnotation();

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

        executeTransformation("testTwoWayRelationWithNullToOneAndOnetoOneCardinalities");

        final String RDBMS_TABLE_NAME_1 = "TestEpackage.TwoWayRelation1";
        final String RDBMS_TABLE_NAME_2 = "TestEpackage.TwoWayRelation2";
        // final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        final String TWO_WAY_REFERENCE2 = "twoWayReference2";

        // ASSERTION - check if tables exist
        assertEquals(2, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_1)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                .size()); //+2 id and type

        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_2)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                .size());  //+2 id and type

        // ASSERTION - check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_2)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME_1, TWO_WAY_REFERENCE2)
                        .orElseThrow(() -> new RuntimeException(TWO_WAY_REFERENCE2 + " attribute not found"))
                        .getReferenceKey());
    }

    @Test
    @DisplayName("Test TwoWayRelation With One To One And Null to Infinite Cardinalities")
    public void testTwoWayRelationWithOneToOneAndNulltoInfiniteCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        final EAnnotation eAnnotation2 = newEntityEAnnotation();

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

        executeTransformation("testTwoWayRelationWithOneToOneAndNulltoInfiniteCardinalities");

        final String RDBMS_TABLE_NAME_1 = "TestEpackage.TwoWayRelation1";
        final String RDBMS_TABLE_NAME_2 = "TestEpackage.TwoWayRelation2";
        final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        //final String TWO_WAY_REFERENCE2 = "twoWayReference2";

        // ASSERTION - check if tables exist
        assertEquals(2, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_1)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                .size()); //+2 id and type

        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_2)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                .size());  //+2 id and type

        // ASSERTION - check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_1)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME_2, TWO_WAY_REFERENCE1)
                        .orElseThrow(() -> new RuntimeException(TWO_WAY_REFERENCE1 + " attribute not found"))
                        .getReferenceKey());
    }

    @Test
    @DisplayName("Test TwoWayRelation With One To One And One to Infinite Cardinalities")
    public void testTwoWayRelationWithOneToOneAndOnetoInfiniteCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        final EAnnotation eAnnotation2 = newEntityEAnnotation();

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

        executeTransformation("testTwoWayRelationWithOneToOneAndOnetoInfiniteCardinalities");

        final String RDBMS_TABLE_NAME_1 = "TestEpackage.TwoWayRelation1";
        final String RDBMS_TABLE_NAME_2 = "TestEpackage.TwoWayRelation2";
        final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        //final String TWO_WAY_REFERENCE2 = "twoWayReference2";

        // ASSERTION - check if tables exist
        assertEquals(2, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_1)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                .size()); //+2 id and type

        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_2)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                .size());  //+2 id and type

        // ASSERTION - check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_1)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME_2, TWO_WAY_REFERENCE1)
                        .orElseThrow(() -> new RuntimeException(TWO_WAY_REFERENCE1 + " attribute not found"))
                        .getReferenceKey());
    }

    @Test
    @DisplayName("Test TwoWayRelation With One To One And Null to One Cardinalities")
    public void testTwoWayRelationWithOneToOneAndNulltoOneCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        final EAnnotation eAnnotation2 = newEntityEAnnotation();

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

        executeTransformation("testTwoWayRelationWithOneToOneAndNulltoOneCardinalities");

        final String RDBMS_TABLE_NAME_1 = "TestEpackage.TwoWayRelation1";
        final String RDBMS_TABLE_NAME_2 = "TestEpackage.TwoWayRelation2";
        final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        //final String TWO_WAY_REFERENCE2 = "twoWayReference2";

        // ASSERTION - check if tables exist
        assertEquals(2, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_1)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                .size()); //+2 id and type

        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_2)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                .size());  //+2 id and type

        // ASSERTION - check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_1)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME_2, TWO_WAY_REFERENCE1)
                        .orElseThrow(() -> new RuntimeException(TWO_WAY_REFERENCE1 + " attribute not found"))
                        .getReferenceKey());
    }

    @Test
    @DisplayName("Test TwoWayRelation With One To One Cardinalities")
    public void testTwoWayRelationWithOneToOneCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        final EAnnotation eAnnotation2 = newEntityEAnnotation();

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
        // final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        final String TWO_WAY_REFERENCE2 = "twoWayReference2";

        // ASSERTION - check if tables exist
        assertEquals(2, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_1)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                .size()); //+2 id and type

        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_2)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                .size());  //+2 id and type

        // ASSERTION - check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_2)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME_1, TWO_WAY_REFERENCE2)
                        .orElseThrow(() -> new RuntimeException(TWO_WAY_REFERENCE2 + " attribute not found"))
                        .getReferenceKey());
    }

    @Test
    @DisplayName("Test Self OneWayRelation With Null to One Cardinality")
    public void testSelfOneWayRelationWithNulltoOneCardinality() {
        //create eannotation
        final EAnnotation eAnnotation = newEntityEAnnotation();

        //create eclass
        final EClass self = newEClassBuilder()
                .withName("Self")
                .withEAnnotations(eAnnotation)
                .build();

        //create one way reference
        final EReference oneWayReference = newEReferenceBuilder()
                .withName("selfOnaWayReference")
                .withLowerBound(0)
                .withUpperBound(1)
                .withEType(self)
                .build();

        //add reference to self
        self.getEStructuralFeatures().add(oneWayReference);

        //create epackage
        final EPackage ePackage = newEPackage(self);

        //add content to model
        asmModel.addContent(ePackage);

        executeTransformation("testSelfOneWayRelationWithNulltoOneCardinality");

        final String RDBMS_TABLE_NAME = "TestEpackage.Self";
        final String ONE_WAY_REFERENCE = "selfOnaWayReference";

        // ASSERTION - check if tables exist
        assertEquals(1, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " not found"))
                .size());

        // ASSERTION - check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME, ONE_WAY_REFERENCE)
                        .orElseThrow(() -> new RuntimeException(ONE_WAY_REFERENCE + " attribute not found"))
                        .getReferenceKey());
    }

    @Test
    @DisplayName("Test Self OneWayRelation With One to One Cardinality")
    public void testSelfOneWayRelationWithOnetoOneCardinality() {
        //create eannotation
        final EAnnotation eAnnotation = newEntityEAnnotation();

        //create eclass
        final EClass self = newEClassBuilder()
                .withName("Self")
                .withEAnnotations(eAnnotation)
                .build();

        //create one way reference
        final EReference oneWayReference = newEReferenceBuilder()
                .withName("selfOnaWayReference")
                .withLowerBound(1)
                .withUpperBound(1)
                .withEType(self)
                .build();

        //add reference to self
        self.getEStructuralFeatures().add(oneWayReference);

        //create epackage
        final EPackage ePackage = newEPackage(self);

        //add content to model
        asmModel.addContent(ePackage);

        executeTransformation("testSelfOneWayRelationWithOnetoOneCardinality");

        final String RDBMS_TABLE_NAME = "TestEpackage.Self";
        final String ONE_WAY_REFERENCE = "selfOnaWayReference";

        // ASSERTION - check if tables exist
        assertEquals(1, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " not found"))
                .size());

        // ASSERTION - check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME, ONE_WAY_REFERENCE)
                        .orElseThrow(() -> new RuntimeException(ONE_WAY_REFERENCE + " attribute not found"))
                        .getReferenceKey());
    }

    @Test
    @DisplayName("Test Self OneWayRelation With Null to Infinite Cardinality")
    public void testSelfOneWayRelationWithNulltoInfiniteCardinality() {
        //create eannotation
        final EAnnotation eAnnotation = newEntityEAnnotation();

        //create eclass
        final EClass self = newEClassBuilder()
                .withName("Self")
                .withEAnnotations(eAnnotation)
                .build();

        //create one way reference
        final EReference oneWayReference = newEReferenceBuilder()
                .withName("selfOnaWayReference")
                .withLowerBound(0)
                .withUpperBound(-1)
                .withEType(self)
                .build();

        //add reference to self
        self.getEStructuralFeatures().add(oneWayReference);

        //create epackage
        final EPackage ePackage = newEPackage(self);

        //add content to model
        asmModel.addContent(ePackage);

        executeTransformation("testSelfOneWayRelationWithNulltoInfiniteCardinality");

        final String RDBMS_TABLE_NAME = "TestEpackage.Self";
        final String ONE_WAY_REFERENCE = "selfOnaWayReference";
        final String RDBMS_JUNCTION_TABLE_NAME = RDBMS_TABLE_NAME + "#" + ONE_WAY_REFERENCE + " to " + RDBMS_TABLE_NAME;

        // ASSERTION - check if tables exist
        assertEquals(2, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                .size());

        // SAVE - rdbmsJunctionTable
        RdbmsJunctionTable rdbmsJunctionTable = rdbmsUtils.getRdbmsJunctionTable(RDBMS_JUNCTION_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_JUNCTION_TABLE_NAME + " junction table not found"));

        // SAVE - rdbmsForeignKeys
        EList<RdbmsForeignKey> rdbmsForeignKeys = rdbmsUtils.getRdbmsForeignKeys(RDBMS_JUNCTION_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_JUNCTION_TABLE_NAME + " junction table not found"));

        // SAVE - primaryKeys
        RdbmsIdentifierField primaryKey = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                .getPrimaryKey();

        // ASSERTION - check if correct keys are in junction table
        assertTrue(rdbmsForeignKeys.stream().anyMatch(o -> o.getReferenceKey().equals(primaryKey)));

        // ASSERTION - check if field1 and field2 is the primary keys from "From" and "To"
        assertEquals(rdbmsJunctionTable.getField1().getReferenceKey(), primaryKey);
        assertEquals(rdbmsJunctionTable.getField2().getReferenceKey(), primaryKey);

        // TODO assert foreignkeysql name unique
    }

    @Test
    @DisplayName("Test Self OneWayRelation With One to Infinite Cardinality")
    public void testSelfOneWayRelationWithOnetoInfiniteCardinality() {
        //create eannotation
        final EAnnotation eAnnotation = newEntityEAnnotation();

        //create eclass
        final EClass self = newEClassBuilder()
                .withName("Self")
                .withEAnnotations(eAnnotation)
                .build();

        //create one way reference
        final EReference oneWayReference = newEReferenceBuilder()
                .withName("selfOnaWayReference")
                .withLowerBound(1)
                .withUpperBound(-1)
                .withEType(self)
                .build();

        //add reference to self
        self.getEStructuralFeatures().add(oneWayReference);

        //create epackage
        final EPackage ePackage = newEPackage(self);

        //add content to model
        asmModel.addContent(ePackage);

        executeTransformation("testSelfOneWayRelationWithOnetoInfiniteCardinality");

        final String RDBMS_TABLE_NAME = "TestEpackage.Self";
        final String ONE_WAY_REFERENCE = "selfOnaWayReference";
        final String RDBMS_JUNCTION_TABLE_NAME = RDBMS_TABLE_NAME + "#" + ONE_WAY_REFERENCE + " to " + RDBMS_TABLE_NAME;

        // ASSERTION - check if tables exist
        assertEquals(2, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                .size());

        // SAVE - rdbmsJunctionTable
        RdbmsJunctionTable rdbmsJunctionTable = rdbmsUtils.getRdbmsJunctionTable(RDBMS_JUNCTION_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_JUNCTION_TABLE_NAME + " junction table not found"));

        // SAVE - rdbmsForeignKeys
        EList<RdbmsForeignKey> rdbmsForeignKeys = rdbmsUtils.getRdbmsForeignKeys(RDBMS_JUNCTION_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_JUNCTION_TABLE_NAME + " junction table not found"));

        // SAVE - primaryKeys
        RdbmsIdentifierField primaryKey = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                .getPrimaryKey();

        // ASSERTION - check if correct keys are in junction table
        assertTrue(rdbmsForeignKeys.stream().anyMatch(o -> o.getReferenceKey().equals(primaryKey)));

        // ASSERTION - check if field1 and field2 is the primary keys from "From" and "To"
        assertEquals(rdbmsJunctionTable.getField1().getReferenceKey(), primaryKey);
        assertEquals(rdbmsJunctionTable.getField2().getReferenceKey(), primaryKey);

        // TODO assert foreignkeysql name unique
    }


    @Test
    @DisplayName("Test Self OneWayContainment With Null to One Cardinality")
    public void testSelfOneWayContainmentWithNulltoOneCardinality() {
        //create eannotation
        final EAnnotation eAnnotation = newEntityEAnnotation();

        //create eclass
        final EClass self = newEClassBuilder()
                .withName("Self")
                .withEAnnotations(eAnnotation)
                .build();

        //create one way reference
        final EReference oneWayReference = newEReferenceBuilder()
                .withName("selfOnaWayReference")
                .withLowerBound(0)
                .withUpperBound(1)
                .withContainment(true)
                .withEType(self)
                .build();

        //add reference to self
        self.getEStructuralFeatures().add(oneWayReference);

        //create epackage
        final EPackage ePackage = newEPackage(self);

        //add content to model
        asmModel.addContent(ePackage);

        executeTransformation("testSelfOneWayContainmentWithNulltoOneCardinality");

        final String RDBMS_TABLE_NAME = "TestEpackage.Self";
        final String ONE_WAY_REFERENCE = "selfSelfOnaWayReference";

        // ASSERTION - check if tables exist
        assertEquals(1, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " not found"))
                .size());

        // ASSERTION - check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME, ONE_WAY_REFERENCE)
                        .orElseThrow(() -> new RuntimeException(ONE_WAY_REFERENCE + " attribute not found"))
                        .getReferenceKey());
    }

    @Test
    @DisplayName("Test Self OneWayContainment With One to One Cardinality")
    public void testSelfOneWayContainmentWithOnetoOneCardinality() {
        //create eannotation
        final EAnnotation eAnnotation = newEntityEAnnotation();

        //create eclass
        final EClass self = newEClassBuilder()
                .withName("Self")
                .withEAnnotations(eAnnotation)
                .build();

        //create one way reference
        final EReference oneWayReference = newEReferenceBuilder()
                .withName("selfOnaWayReference")
                .withLowerBound(1)
                .withUpperBound(1)
                .withContainment(true)
                .withEType(self)
                .build();

        //add reference to self
        self.getEStructuralFeatures().add(oneWayReference);

        //create epackage
        final EPackage ePackage = newEPackage(self);

        //add content to model
        asmModel.addContent(ePackage);

        executeTransformation("testSelfOneWayContainmentWithOnetoOneCardinality");

        final String RDBMS_TABLE_NAME = "TestEpackage.Self";
        final String ONE_WAY_REFERENCE = "selfSelfOnaWayReference";

        // ASSERTION - check if tables exist
        assertEquals(1, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " not found"))
                .size());

        // ASSERTION - check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME, ONE_WAY_REFERENCE)
                        .orElseThrow(() -> new RuntimeException(ONE_WAY_REFERENCE + " attribute not found"))
                        .getReferenceKey());
    }

    @Test
    @DisplayName("Test Self OneWayContainment With Null to Infinite Cardinality")
    public void testSelfOneWayContainmentWithNulltoInfiniteCardinality() {
        //create eannotation
        final EAnnotation eAnnotation = newEntityEAnnotation();

        //create eclass
        final EClass self = newEClassBuilder()
                .withName("Self")
                .withEAnnotations(eAnnotation)
                .build();

        //create one way reference
        final EReference oneWayReference = newEReferenceBuilder()
                .withName("selfOnaWayReference")
                .withLowerBound(0)
                .withUpperBound(-1)
                .withContainment(true)
                .withEType(self)
                .build();

        //add reference to self
        self.getEStructuralFeatures().add(oneWayReference);

        //create epackage
        final EPackage ePackage = newEPackage(self);

        //add content to model
        asmModel.addContent(ePackage);

        executeTransformation("testSelfOneWayContainmentWithNulltoInfiniteCardinality");

        final String RDBMS_TABLE_NAME = "TestEpackage.Self";
        final String ONE_WAY_REFERENCE = "selfSelfOnaWayReference";

        // ASSERTION - check if tables exist
        assertEquals(1, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                .size());

        // ASSERTION - check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME, ONE_WAY_REFERENCE)
                        .orElseThrow(() -> new RuntimeException(ONE_WAY_REFERENCE + " attribute not found"))
                        .getReferenceKey());

        // TODO assert foreignkeysql name unique
    }

    @Test
    @DisplayName("Test Self OneWayContainment With One to Infinite Cardinality")
    public void testSelfOneWayContainmentWithOnetoInfiniteCardinality() {
        //create eannotation
        final EAnnotation eAnnotation = newEntityEAnnotation();

        //create eclass
        final EClass self = newEClassBuilder()
                .withName("Self")
                .withEAnnotations(eAnnotation)
                .build();

        //create one way reference
        final EReference oneWayReference = newEReferenceBuilder()
                .withName("selfOnaWayReference")
                .withLowerBound(1)
                .withUpperBound(-1)
                .withContainment(true)
                .withEType(self)
                .build();

        //add reference to self
        self.getEStructuralFeatures().add(oneWayReference);

        //create epackage
        final EPackage ePackage = newEPackage(self);

        //add content to model
        asmModel.addContent(ePackage);

        executeTransformation("testSelfOneWayContainmentWithOnetoInfiniteCardinality");

        final String RDBMS_TABLE_NAME = "TestEpackage.Self";
        final String ONE_WAY_REFERENCE = "selfSelfOnaWayReference";

        // ASSERTION - check if tables exist
        assertEquals(1, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                .size());

        // ASSERTION - check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME, ONE_WAY_REFERENCE)
                        .orElseThrow(() -> new RuntimeException(ONE_WAY_REFERENCE + " attribute not found"))
                        .getReferenceKey());

        // TODO assert foreignkeysql name unique
    }

    @Test
    @DisplayName("Test Self TwoWayRelation With Null To One Cardinalities")
    public void testSelfTwoWayRelationWithNullToOneCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        // create classes
        final EClass self = newEClassBuilder()
                .withName("Self")
                .withEAnnotations(eAnnotation)
                .build();

        // create references
        final EReference twoWayReference1 = newEReferenceBuilder()
                .withName("twoWayReference1")
                .withLowerBound(0)
                .withUpperBound(1)
                .withEType(self)
                .build();

        final EReference twoWayReference2 = newEReferenceBuilder()
                .withName("twoWayReference2")
                .withLowerBound(0)
                .withUpperBound(1)
                .withEType(self)
                .build();

        // add opposites
        twoWayReference1.setEOpposite(twoWayReference2);
        twoWayReference2.setEOpposite(twoWayReference1);

        // add references
        self.getEStructuralFeatures().add(twoWayReference2);
        self.getEStructuralFeatures().add(twoWayReference1);

        // create package
        final EPackage ePackage = newEPackage(self);

        // add content to asm model
        asmModel.addContent(ePackage);

        executeTransformation("testSelfTwoWayRelationWithNullToOneCardinalities");

        final String RDBMS_TABLE_NAME = "TestEpackage.Self";
        final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        // final String TWO_WAY_REFERENCE2 = "twoWayReference2";

        // ASSERTION - check if tables exist
        assertEquals(1, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                .size()); //+2 id and type

        // ASSERTION - check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME, TWO_WAY_REFERENCE1)
                        .orElseThrow(() -> new RuntimeException(TWO_WAY_REFERENCE1 + " attribute not found"))
                        .getReferenceKey());
    }

    @Test
    @DisplayName("Test Self TwoWayRelation With One To One Cardinalities")
    public void testSelfTwoWayRelationWithOneToOneCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        // create classes
        final EClass self = newEClassBuilder()
                .withName("Self")
                .withEAnnotations(eAnnotation)
                .build();

        // create references
        final EReference twoWayReference1 = newEReferenceBuilder()
                .withName("twoWayReference1")
                .withLowerBound(1)
                .withUpperBound(1)
                .withEType(self)
                .build();

        final EReference twoWayReference2 = newEReferenceBuilder()
                .withName("twoWayReference2")
                .withLowerBound(1)
                .withUpperBound(1)
                .withEType(self)
                .build();

        // add opposites
        twoWayReference1.setEOpposite(twoWayReference2);
        twoWayReference2.setEOpposite(twoWayReference1);

        // add references
        self.getEStructuralFeatures().add(twoWayReference2);
        self.getEStructuralFeatures().add(twoWayReference1);

        // create package
        final EPackage ePackage = newEPackage(self);

        // add content to asm model
        asmModel.addContent(ePackage);

        executeTransformation("testSelfTwoWayRelationWithOneToOneCardinalities");

        final String RDBMS_TABLE_NAME = "TestEpackage.Self";
        //final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        final String TWO_WAY_REFERENCE2 = "twoWayReference2";

        // ASSERTION - check if tables exist
        assertEquals(1, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                .size()); //+2 id and type

        // ASSERTION - check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME, TWO_WAY_REFERENCE2)
                        .orElseThrow(() -> new RuntimeException(TWO_WAY_REFERENCE2 + " attribute not found"))
                        .getReferenceKey());
    }

    @Test
    @DisplayName("Test Self TwoWayRelation With Null To One And One To One Cardinalities")
    public void testSelfTwoWayRelationWithNullToOneAndOneToOneCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        // create classes
        final EClass self = newEClassBuilder()
                .withName("Self")
                .withEAnnotations(eAnnotation)
                .build();

        // create references
        final EReference twoWayReference1 = newEReferenceBuilder()
                .withName("twoWayReference1")
                .withLowerBound(1)
                .withUpperBound(1)
                .withEType(self)
                .build();

        final EReference twoWayReference2 = newEReferenceBuilder()
                .withName("twoWayReference2")
                .withLowerBound(0)
                .withUpperBound(1)
                .withEType(self)
                .build();

        // add opposites
        twoWayReference1.setEOpposite(twoWayReference2);
        twoWayReference2.setEOpposite(twoWayReference1);

        // add references
        self.getEStructuralFeatures().add(twoWayReference2);
        self.getEStructuralFeatures().add(twoWayReference1);

        // create package
        final EPackage ePackage = newEPackage(self);

        // add content to asm model
        asmModel.addContent(ePackage);

        executeTransformation("testSelfTwoWayRelationWithOneToOneCardinalities");

        final String RDBMS_TABLE_NAME = "TestEpackage.Self";
        final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        // final String TWO_WAY_REFERENCE2 = "twoWayReference2";

        // ASSERTION - check if tables exist
        assertEquals(1, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                .size()); //+2 id and type

        // ASSERTION - check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME, TWO_WAY_REFERENCE1)
                        .orElseThrow(() -> new RuntimeException(TWO_WAY_REFERENCE1 + " attribute not found"))
                        .getReferenceKey());
    }

    @Test
    @DisplayName("Test Self TwoWayRelation With One To One And Null To One Cardinalities")
    public void testSelfTwoWayRelationWithOneToOneAndNullToOneCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        // create classes
        final EClass self = newEClassBuilder()
                .withName("Self")
                .withEAnnotations(eAnnotation)
                .build();

        // create references
        final EReference twoWayReference1 = newEReferenceBuilder()
                .withName("twoWayReference1")
                .withLowerBound(0)
                .withUpperBound(1)
                .withEType(self)
                .build();

        final EReference twoWayReference2 = newEReferenceBuilder()
                .withName("twoWayReference2")
                .withLowerBound(1)
                .withUpperBound(1)
                .withEType(self)
                .build();

        // add opposites
        twoWayReference1.setEOpposite(twoWayReference2);
        twoWayReference2.setEOpposite(twoWayReference1);

        // add references
        self.getEStructuralFeatures().add(twoWayReference2);
        self.getEStructuralFeatures().add(twoWayReference1);

        // create package
        final EPackage ePackage = newEPackage(self);

        // add content to asm model
        asmModel.addContent(ePackage);

        executeTransformation("testSelfTwoWayRelationWithOneToOneAndNullToOneCardinalities");

        final String RDBMS_TABLE_NAME = "TestEpackage.Self";
        // final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        final String TWO_WAY_REFERENCE2 = "twoWayReference2";

        // ASSERTION - check if tables exist
        assertEquals(1, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                .size()); //+2 id and type

        // ASSERTION - check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME, TWO_WAY_REFERENCE2)
                        .orElseThrow(() -> new RuntimeException(TWO_WAY_REFERENCE2 + " attribute not found"))
                        .getReferenceKey());
    }

    @Test
    @DisplayName("Test Self TwoWayRelation With Null To One And Null To Infinite Cardinalities")
    public void testSelfTwoWayRelationWithNullToOneAndNullToInfiniteCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        // create classes
        final EClass self = newEClassBuilder()
                .withName("Self")
                .withEAnnotations(eAnnotation)
                .build();

        // create references
        final EReference twoWayReference1 = newEReferenceBuilder()
                .withName("twoWayReference1")
                .withLowerBound(0)
                .withUpperBound(1)
                .withEType(self)
                .build();

        final EReference twoWayReference2 = newEReferenceBuilder()
                .withName("twoWayReference2")
                .withLowerBound(0)
                .withUpperBound(-1)
                .withEType(self)
                .build();

        // add opposites
        twoWayReference1.setEOpposite(twoWayReference2);
        twoWayReference2.setEOpposite(twoWayReference1);

        // add references
        self.getEStructuralFeatures().add(twoWayReference2);
        self.getEStructuralFeatures().add(twoWayReference1);

        // create package
        final EPackage ePackage = newEPackage(self);

        // add content to asm model
        asmModel.addContent(ePackage);

        executeTransformation("testSelfTwoWayRelationWithNullToOneAndNullToInfiniteCardinalities");

        final String RDBMS_TABLE_NAME = "TestEpackage.Self";
        final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        // final String TWO_WAY_REFERENCE2 = "twoWayReference2";

        // ASSERTION - check if tables exist
        assertEquals(1, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                .size()); //+2 id and type

        // ASSERTION - check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME, TWO_WAY_REFERENCE1)
                        .orElseThrow(() -> new RuntimeException(TWO_WAY_REFERENCE1 + " attribute not found"))
                        .getReferenceKey());

        // TODO assert foreignkeysql name unique
    }

    @Test
    @DisplayName("Test Self TwoWayRelation With Null To One And One To Infinite Cardinalities")
    public void testSelfTwoWayRelationWithNullToOneAndOneToInfiniteCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        // create classes
        final EClass self = newEClassBuilder()
                .withName("Self")
                .withEAnnotations(eAnnotation)
                .build();

        // create references
        final EReference twoWayReference1 = newEReferenceBuilder()
                .withName("twoWayReference1")
                .withLowerBound(0)
                .withUpperBound(1)
                .withEType(self)
                .build();

        final EReference twoWayReference2 = newEReferenceBuilder()
                .withName("twoWayReference2")
                .withLowerBound(1)
                .withUpperBound(-1)
                .withEType(self)
                .build();

        // add opposites
        twoWayReference1.setEOpposite(twoWayReference2);
        twoWayReference2.setEOpposite(twoWayReference1);

        // add references
        self.getEStructuralFeatures().add(twoWayReference2);
        self.getEStructuralFeatures().add(twoWayReference1);

        // create package
        final EPackage ePackage = newEPackage(self);

        // add content to asm model
        asmModel.addContent(ePackage);

        executeTransformation("testSelfTwoWayRelationWithNullToOneAndOneToInfiniteCardinalities");

        final String RDBMS_TABLE_NAME = "TestEpackage.Self";
        final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        // final String TWO_WAY_REFERENCE2 = "twoWayReference2";

        // ASSERTION - check if tables exist
        assertEquals(1, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                .size()); //+2 id and type

        // ASSERTION - check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME, TWO_WAY_REFERENCE1)
                        .orElseThrow(() -> new RuntimeException(TWO_WAY_REFERENCE1 + " attribute not found"))
                        .getReferenceKey());

        // TODO assert foreignkeysql name unique
    }

    @Test
    @DisplayName("Test Self TwoWayRelation With Null To One And Null To Infinite Cardinalities")
    public void testSelfTwoWayRelationWithOneToNullAndOneToInfiniteCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        // create classes
        final EClass self = newEClassBuilder()
                .withName("Self")
                .withEAnnotations(eAnnotation)
                .build();

        // create references
        final EReference twoWayReference1 = newEReferenceBuilder()
                .withName("twoWayReference1")
                .withLowerBound(1)
                .withUpperBound(1)
                .withEType(self)
                .build();

        final EReference twoWayReference2 = newEReferenceBuilder()
                .withName("twoWayReference2")
                .withLowerBound(0)
                .withUpperBound(-1)
                .withEType(self)
                .build();

        // add opposites
        twoWayReference1.setEOpposite(twoWayReference2);
        twoWayReference2.setEOpposite(twoWayReference1);

        // add references
        self.getEStructuralFeatures().add(twoWayReference2);
        self.getEStructuralFeatures().add(twoWayReference1);

        // create package
        final EPackage ePackage = newEPackage(self);

        // add content to asm model
        asmModel.addContent(ePackage);

        executeTransformation("testSelfTwoWayRelationWithOneToNullAndOneToInfiniteCardinalities");

        final String RDBMS_TABLE_NAME = "TestEpackage.Self";
        final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        // final String TWO_WAY_REFERENCE2 = "twoWayReference2";

        // ASSERTION - check if tables exist
        assertEquals(1, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                .size()); //+2 id and type

        // ASSERTION - check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME, TWO_WAY_REFERENCE1)
                        .orElseThrow(() -> new RuntimeException(TWO_WAY_REFERENCE1 + " attribute not found"))
                        .getReferenceKey());

        // TODO assert foreignkeysql name unique
    }

    @Test
    @DisplayName("Test Self TwoWayRelation With Null To One And One To Infinite Cardinalities")
    public void testSelfTwoWayRelationWithOneToOneAndOneToInfiniteCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        // create classes
        final EClass self = newEClassBuilder()
                .withName("Self")
                .withEAnnotations(eAnnotation)
                .build();

        // create references
        final EReference twoWayReference1 = newEReferenceBuilder()
                .withName("twoWayReference1")
                .withLowerBound(1)
                .withUpperBound(1)
                .withEType(self)
                .build();

        final EReference twoWayReference2 = newEReferenceBuilder()
                .withName("twoWayReference2")
                .withLowerBound(1)
                .withUpperBound(-1)
                .withEType(self)
                .build();

        // add opposites
        twoWayReference1.setEOpposite(twoWayReference2);
        twoWayReference2.setEOpposite(twoWayReference1);

        // add references
        self.getEStructuralFeatures().add(twoWayReference2);
        self.getEStructuralFeatures().add(twoWayReference1);

        // create package
        final EPackage ePackage = newEPackage(self);

        // add content to asm model
        asmModel.addContent(ePackage);

        executeTransformation("testSelfTwoWayRelationWithOneToOneAndOneToInfiniteCardinalities");

        final String RDBMS_TABLE_NAME = "TestEpackage.Self";
        final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        // final String TWO_WAY_REFERENCE2 = "twoWayReference2";

        // ASSERTION - check if tables exist
        assertEquals(1, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                .size()); //+2 id and type

        // ASSERTION - check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME, TWO_WAY_REFERENCE1)
                        .orElseThrow(() -> new RuntimeException(TWO_WAY_REFERENCE1 + " attribute not found"))
                        .getReferenceKey());

        // TODO assert foreignkeysql name unique
    }

    @Test
    @DisplayName("Test Self TwoWayRelation With Null To Infinite And Null To One Cardinalities")
    public void testSelfTwoWayRelationWithNullToInfiniteAndNullToOneCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        // create classes
        final EClass self = newEClassBuilder()
                .withName("Self")
                .withEAnnotations(eAnnotation)
                .build();

        // create references
        final EReference twoWayReference1 = newEReferenceBuilder()
                .withName("twoWayReference1")
                .withLowerBound(0)
                .withUpperBound(-1)
                .withEType(self)
                .build();

        final EReference twoWayReference2 = newEReferenceBuilder()
                .withName("twoWayReference2")
                .withLowerBound(0)
                .withUpperBound(1)
                .withEType(self)
                .build();

        // add opposites
        twoWayReference1.setEOpposite(twoWayReference2);
        twoWayReference2.setEOpposite(twoWayReference1);

        // add references
        self.getEStructuralFeatures().add(twoWayReference2);
        self.getEStructuralFeatures().add(twoWayReference1);

        // create package
        final EPackage ePackage = newEPackage(self);

        // add content to asm model
        asmModel.addContent(ePackage);

        executeTransformation("testSelfTwoWayRelationWithNullToInfiniteAndNullToOneCardinalities");

        final String RDBMS_TABLE_NAME = "TestEpackage.Self";
        // final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        final String TWO_WAY_REFERENCE2 = "twoWayReference2";

        // ASSERTION - check if tables exist
        assertEquals(1, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                .size()); //+2 id and type

        // ASSERTION - check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME, TWO_WAY_REFERENCE2)
                        .orElseThrow(() -> new RuntimeException(TWO_WAY_REFERENCE2 + " attribute not found"))
                        .getReferenceKey());

        // TODO assert foreignkeysql name unique
    }

    @Test
    @DisplayName("Test Self TwoWayRelation With Null To Infinite And One To One Cardinalities")
    public void testSelfTwoWayRelationWithNullToInfiniteAndOneToOneCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        // create classes
        final EClass self = newEClassBuilder()
                .withName("Self")
                .withEAnnotations(eAnnotation)
                .build();

        // create references
        final EReference twoWayReference1 = newEReferenceBuilder()
                .withName("twoWayReference1")
                .withLowerBound(0)
                .withUpperBound(-1)
                .withEType(self)
                .build();

        final EReference twoWayReference2 = newEReferenceBuilder()
                .withName("twoWayReference2")
                .withLowerBound(1)
                .withUpperBound(1)
                .withEType(self)
                .build();

        // add opposites
        twoWayReference1.setEOpposite(twoWayReference2);
        twoWayReference2.setEOpposite(twoWayReference1);

        // add references
        self.getEStructuralFeatures().add(twoWayReference2);
        self.getEStructuralFeatures().add(twoWayReference1);

        // create package
        final EPackage ePackage = newEPackage(self);

        // add content to asm model
        asmModel.addContent(ePackage);

        executeTransformation("testSelfTwoWayRelationWithNullToInfiniteAndOneToOneCardinalities");

        final String RDBMS_TABLE_NAME = "TestEpackage.Self";
        // final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        final String TWO_WAY_REFERENCE2 = "twoWayReference2";

        // ASSERTION - check if tables exist
        assertEquals(1, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                .size()); //+2 id and type

        // ASSERTION - check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME, TWO_WAY_REFERENCE2)
                        .orElseThrow(() -> new RuntimeException(TWO_WAY_REFERENCE2 + " attribute not found"))
                        .getReferenceKey());

        // TODO assert foreignkeysql name unique
    }

    @Test
    @DisplayName("Test Self TwoWayRelation With One To Infinite And Null To One Cardinalities")
    public void testSelfTwoWayRelationWithOneToInfiniteAndNullToOneCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        // create classes
        final EClass self = newEClassBuilder()
                .withName("Self")
                .withEAnnotations(eAnnotation)
                .build();

        // create references
        final EReference twoWayReference1 = newEReferenceBuilder()
                .withName("twoWayReference1")
                .withLowerBound(1)
                .withUpperBound(-1)
                .withEType(self)
                .build();

        final EReference twoWayReference2 = newEReferenceBuilder()
                .withName("twoWayReference2")
                .withLowerBound(0)
                .withUpperBound(1)
                .withEType(self)
                .build();

        // add opposites
        twoWayReference1.setEOpposite(twoWayReference2);
        twoWayReference2.setEOpposite(twoWayReference1);

        // add references
        self.getEStructuralFeatures().add(twoWayReference2);
        self.getEStructuralFeatures().add(twoWayReference1);

        // create package
        final EPackage ePackage = newEPackage(self);

        // add content to asm model
        asmModel.addContent(ePackage);

        executeTransformation("testSelfTwoWayRelationWithOneToInfiniteAndNullToOneCardinalities");

        final String RDBMS_TABLE_NAME = "TestEpackage.Self";
        // final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        final String TWO_WAY_REFERENCE2 = "twoWayReference2";

        // ASSERTION - check if tables exist
        assertEquals(1, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                .size()); //+2 id and type

        // ASSERTION - check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME, TWO_WAY_REFERENCE2)
                        .orElseThrow(() -> new RuntimeException(TWO_WAY_REFERENCE2 + " attribute not found"))
                        .getReferenceKey());

        // TODO assert foreignkeysql name unique
    }

    @Test
    @DisplayName("Test Self TwoWayRelation With One To Infinite And One To One Cardinalities")
    public void testSelfTwoWayRelationWithOneToInfiniteAndOneToOneCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        // create classes
        final EClass self = newEClassBuilder()
                .withName("Self")
                .withEAnnotations(eAnnotation)
                .build();

        // create references
        final EReference twoWayReference1 = newEReferenceBuilder()
                .withName("twoWayReference1")
                .withLowerBound(1)
                .withUpperBound(-1)
                .withEType(self)
                .build();

        final EReference twoWayReference2 = newEReferenceBuilder()
                .withName("twoWayReference2")
                .withLowerBound(1)
                .withUpperBound(1)
                .withEType(self)
                .build();

        // add opposites
        twoWayReference1.setEOpposite(twoWayReference2);
        twoWayReference2.setEOpposite(twoWayReference1);

        // add references
        self.getEStructuralFeatures().add(twoWayReference2);
        self.getEStructuralFeatures().add(twoWayReference1);

        // create package
        final EPackage ePackage = newEPackage(self);

        // add content to asm model
        asmModel.addContent(ePackage);

        executeTransformation("testSelfTwoWayRelationWithOneToInfiniteAndOneToOneCardinalities");

        final String RDBMS_TABLE_NAME = "TestEpackage.Self";
        // final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        final String TWO_WAY_REFERENCE2 = "twoWayReference2";

        // ASSERTION - check if tables exist
        assertEquals(1, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                .size()); //+2 id and type

        // ASSERTION - check if RdbmsForeignKey is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                        .getPrimaryKey(),
                rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME, TWO_WAY_REFERENCE2)
                        .orElseThrow(() -> new RuntimeException(TWO_WAY_REFERENCE2 + " attribute not found"))
                        .getReferenceKey());

        // TODO assert foreignkeysql name unique
    }

    @Test
    @DisplayName("Test Self TwoWayRelation With Null To Infinite Cardinalities")
    public void testSelfTwoWayRelationWithNullToInfiniteCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        // create classes
        final EClass self = newEClassBuilder()
                .withName("Self")
                .withEAnnotations(eAnnotation)
                .build();

        // create references
        final EReference twoWayReference1 = newEReferenceBuilder()
                .withName("twoWayReference1")
                .withLowerBound(0)
                .withUpperBound(-1)
                .withEType(self)
                .build();

        final EReference twoWayReference2 = newEReferenceBuilder()
                .withName("twoWayReference2")
                .withLowerBound(0)
                .withUpperBound(-1)
                .withEType(self)
                .build();

        // add opposites
        twoWayReference1.setEOpposite(twoWayReference2);
        twoWayReference2.setEOpposite(twoWayReference1);

        // add references
        self.getEStructuralFeatures().add(twoWayReference2);
        self.getEStructuralFeatures().add(twoWayReference1);

        // create package
        final EPackage ePackage = newEPackage(self);

        // add content to asm model
        asmModel.addContent(ePackage);

        executeTransformation("testSelfTwoWayRelationWithNullToInfiniteCardinalities");

        final String RDBMS_TABLE_NAME = "TestEpackage.Self";
        final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        final String TWO_WAY_REFERENCE2 = "twoWayReference2";
        final String RDBMS_JUNCTION_TABLE = RDBMS_TABLE_NAME + "#" + TWO_WAY_REFERENCE1 + " to " + RDBMS_TABLE_NAME + "#" + TWO_WAY_REFERENCE2;

        // ASSERTION - check if tables exist
        assertEquals(2, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                .size()); //+2 id and type

        // SAVE - rdbmsJunctionTable
        RdbmsJunctionTable rdbmsJunctionTable =
                rdbmsUtils.getRdbmsJunctionTable(RDBMS_JUNCTION_TABLE)
                        .orElseThrow(() -> new RuntimeException(RDBMS_JUNCTION_TABLE + " junction table not found"));

        // SAVE - rdbmsForeignKeys
        EList<RdbmsForeignKey> rdbmsForeignKeys = rdbmsUtils.getRdbmsForeignKeys(RDBMS_JUNCTION_TABLE)
                .orElseThrow(() -> new RuntimeException(RDBMS_JUNCTION_TABLE + " junction table not found"));

        // SAVE - primaryKeys
        RdbmsIdentifierField primaryKey = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                .getPrimaryKey();

        // ASSERTION - check if correct keys are in junction table
        assertTrue(rdbmsForeignKeys.stream().anyMatch(o -> o.getReferenceKey().equals(primaryKey)));

        // ASSERTION - check if field1 and field2 is the primary keys from "From" and "To"
        assertEquals(rdbmsJunctionTable.getField1().getReferenceKey(), primaryKey);
        assertEquals(rdbmsJunctionTable.getField2().getReferenceKey(), primaryKey);

    }

    @Test
    @DisplayName("Test Self TwoWayRelation With Null To Infinite And One To Infinite Cardinalities")
    public void testSelfTwoWayRelationWithNullToInfiniteAndOneToInfiniteCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        // create classes
        final EClass self = newEClassBuilder()
                .withName("Self")
                .withEAnnotations(eAnnotation)
                .build();

        // create references
        final EReference twoWayReference1 = newEReferenceBuilder()
                .withName("twoWayReference1")
                .withLowerBound(0)
                .withUpperBound(-1)
                .withEType(self)
                .build();

        final EReference twoWayReference2 = newEReferenceBuilder()
                .withName("twoWayReference2")
                .withLowerBound(1)
                .withUpperBound(-1)
                .withEType(self)
                .build();

        // add opposites
        twoWayReference1.setEOpposite(twoWayReference2);
        twoWayReference2.setEOpposite(twoWayReference1);

        // add references
        self.getEStructuralFeatures().add(twoWayReference2);
        self.getEStructuralFeatures().add(twoWayReference1);

        // create package
        final EPackage ePackage = newEPackage(self);

        // add content to asm model
        asmModel.addContent(ePackage);

        executeTransformation("testSelfTwoWayRelationWithNullToInfiniteAndOneToInfiniteCardinalities");

        final String RDBMS_TABLE_NAME = "TestEpackage.Self";
        final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        final String TWO_WAY_REFERENCE2 = "twoWayReference2";
        final String RDBMS_JUNCTION_TABLE = RDBMS_TABLE_NAME + "#" + TWO_WAY_REFERENCE1 + " to " + RDBMS_TABLE_NAME + "#" + TWO_WAY_REFERENCE2;

        // ASSERTION - check if tables exist
        assertEquals(2, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                .size()); //+2 id and type

        // SAVE - rdbmsJunctionTable
        RdbmsJunctionTable rdbmsJunctionTable =
                rdbmsUtils.getRdbmsJunctionTable(RDBMS_JUNCTION_TABLE)
                        .orElseThrow(() -> new RuntimeException(RDBMS_JUNCTION_TABLE + " junction table not found"));

        // SAVE - rdbmsForeignKeys
        EList<RdbmsForeignKey> rdbmsForeignKeys = rdbmsUtils.getRdbmsForeignKeys(RDBMS_JUNCTION_TABLE)
                .orElseThrow(() -> new RuntimeException(RDBMS_JUNCTION_TABLE + " junction table not found"));

        // SAVE - primaryKeys
        RdbmsIdentifierField primaryKey = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                .getPrimaryKey();

        // ASSERTION - check if correct keys are in junction table
        assertTrue(rdbmsForeignKeys.stream().anyMatch(o -> o.getReferenceKey().equals(primaryKey)));

        // ASSERTION - check if field1 and field2 is the primary keys from "From" and "To"
        assertEquals(rdbmsJunctionTable.getField1().getReferenceKey(), primaryKey);
        assertEquals(rdbmsJunctionTable.getField2().getReferenceKey(), primaryKey);

    }

    @Test
    @DisplayName("Test Self TwoWayRelation With One To Infinite And Null To Infinite Cardinalities")
    public void testSelfTwoWayRelationWithOneToInfiniteAndNullToInfiniteCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        // create classes
        final EClass self = newEClassBuilder()
                .withName("Self")
                .withEAnnotations(eAnnotation)
                .build();

        // create references
        final EReference twoWayReference1 = newEReferenceBuilder()
                .withName("twoWayReference1")
                .withLowerBound(1)
                .withUpperBound(-1)
                .withEType(self)
                .build();

        final EReference twoWayReference2 = newEReferenceBuilder()
                .withName("twoWayReference2")
                .withLowerBound(0)
                .withUpperBound(-1)
                .withEType(self)
                .build();

        // add opposites
        twoWayReference1.setEOpposite(twoWayReference2);
        twoWayReference2.setEOpposite(twoWayReference1);

        // add references
        self.getEStructuralFeatures().add(twoWayReference2);
        self.getEStructuralFeatures().add(twoWayReference1);

        // create package
        final EPackage ePackage = newEPackage(self);

        // add content to asm model
        asmModel.addContent(ePackage);

        executeTransformation("testSelfTwoWayRelationWithOneToInfiniteAndNullToInfiniteCardinalities");

        final String RDBMS_TABLE_NAME = "TestEpackage.Self";
        final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        final String TWO_WAY_REFERENCE2 = "twoWayReference2";
        final String RDBMS_JUNCTION_TABLE = RDBMS_TABLE_NAME + "#" + TWO_WAY_REFERENCE1 + " to " + RDBMS_TABLE_NAME + "#" + TWO_WAY_REFERENCE2;

        // ASSERTION - check if tables exist
        assertEquals(2, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                .size()); //+2 id and type

        // SAVE - rdbmsJunctionTable
        RdbmsJunctionTable rdbmsJunctionTable =
                rdbmsUtils.getRdbmsJunctionTable(RDBMS_JUNCTION_TABLE)
                        .orElseThrow(() -> new RuntimeException(RDBMS_JUNCTION_TABLE + " junction table not found"));

        // SAVE - rdbmsForeignKeys
        EList<RdbmsForeignKey> rdbmsForeignKeys = rdbmsUtils.getRdbmsForeignKeys(RDBMS_JUNCTION_TABLE)
                .orElseThrow(() -> new RuntimeException(RDBMS_JUNCTION_TABLE + " junction table not found"));

        // SAVE - primaryKeys
        RdbmsIdentifierField primaryKey = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                .getPrimaryKey();

        // ASSERTION - check if correct keys are in junction table
        assertTrue(rdbmsForeignKeys.stream().anyMatch(o -> o.getReferenceKey().equals(primaryKey)));

        // ASSERTION - check if field1 and field2 is the primary keys from "From" and "To"
        assertEquals(rdbmsJunctionTable.getField1().getReferenceKey(), primaryKey);
        assertEquals(rdbmsJunctionTable.getField2().getReferenceKey(), primaryKey);

    }

    @Test
    @DisplayName("Test Self TwoWayRelation With One To Infinite Cardinalities")
    public void testSelfTwoWayRelationWithOneToInfiniteCardinalities() {
        // create annotations
        final EAnnotation eAnnotation = newEntityEAnnotation();

        // create classes
        final EClass self = newEClassBuilder()
                .withName("Self")
                .withEAnnotations(eAnnotation)
                .build();

        // create references
        final EReference twoWayReference1 = newEReferenceBuilder()
                .withName("twoWayReference1")
                .withLowerBound(1)
                .withUpperBound(-1)
                .withEType(self)
                .build();

        final EReference twoWayReference2 = newEReferenceBuilder()
                .withName("twoWayReference2")
                .withLowerBound(1)
                .withUpperBound(-1)
                .withEType(self)
                .build();

        // add opposites
        twoWayReference1.setEOpposite(twoWayReference2);
        twoWayReference2.setEOpposite(twoWayReference1);

        // add references
        self.getEStructuralFeatures().add(twoWayReference2);
        self.getEStructuralFeatures().add(twoWayReference1);

        // create package
        final EPackage ePackage = newEPackage(self);

        // add content to asm model
        asmModel.addContent(ePackage);

        executeTransformation("testSelfTwoWayRelationWithOneToInfiniteCardinalities");

        final String RDBMS_TABLE_NAME = "TestEpackage.Self";
        final String TWO_WAY_REFERENCE1 = "twoWayReference1";
        final String TWO_WAY_REFERENCE2 = "twoWayReference2";
        final String RDBMS_JUNCTION_TABLE = RDBMS_TABLE_NAME + "#" + TWO_WAY_REFERENCE1 + " to " + RDBMS_TABLE_NAME + "#" + TWO_WAY_REFERENCE2;

        // ASSERTION - check if tables exist
        assertEquals(2, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - check for correct number of fields
        assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                .size()); //+2 id and type

        // SAVE - rdbmsJunctionTable
        RdbmsJunctionTable rdbmsJunctionTable =
                rdbmsUtils.getRdbmsJunctionTable(RDBMS_JUNCTION_TABLE)
                        .orElseThrow(() -> new RuntimeException(RDBMS_JUNCTION_TABLE + " junction table not found"));

        // SAVE - rdbmsForeignKeys
        EList<RdbmsForeignKey> rdbmsForeignKeys = rdbmsUtils.getRdbmsForeignKeys(RDBMS_JUNCTION_TABLE)
                .orElseThrow(() -> new RuntimeException(RDBMS_JUNCTION_TABLE + " junction table not found"));

        // SAVE - primaryKeys
        RdbmsIdentifierField primaryKey = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                .getPrimaryKey();

        // ASSERTION - check if correct keys are in junction table
        assertTrue(rdbmsForeignKeys.stream().anyMatch(o -> o.getReferenceKey().equals(primaryKey)));

        // ASSERTION - check if field1 and field2 is the primary keys from "From" and "To"
        assertEquals(rdbmsJunctionTable.getField1().getReferenceKey(), primaryKey);
        assertEquals(rdbmsJunctionTable.getField2().getReferenceKey(), primaryKey);

    }

}
