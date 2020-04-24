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

import static org.eclipse.emf.ecore.util.builder.EcoreBuilders.newEClassBuilder;
import static org.eclipse.emf.ecore.util.builder.EcoreBuilders.newEReferenceBuilder;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Asm2RdbmsRelationMappingTest extends Asm2RdbmsMappingTestBase {

    /**
     * Converts -1, 0 or 1 cardinality into human readable word
     *
     * @param cardinality -1, 0, 1
     * @return Infinite, Null or One
     * @throws IllegalArgumentException if cardinality other then -1, 0 or 1
     */
    private String parseCardinality(int cardinality) {
        switch (cardinality) {
            case -1:
                return "Infinite";
            case 0:
                return "Null";
            case 1:
                return "One";
            default:
                throw new IllegalArgumentException("unexpected cardinality");
        }
    }

    /**
     * Concats 2 "readable" cardinalities for testing purposes
     *
     * @param lowerCardinality lowerCardinality
     * @param upperCardinality upperCardinality
     * @return 2 "readable" cardinalities with "To" between them
     */
    private String parseCardinalities(int lowerCardinality, int upperCardinality) {
        return parseCardinality(lowerCardinality) + "To" + parseCardinality(upperCardinality);
    }


    /**
     * Concats 4 "readable" cardinalities for testing purposes
     *
     * @param lowerCardinality1 lowerCardinality1
     * @param upperCardinality1 upperCardinality1
     * @param lowerCardinality2 lowerCardinality2
     * @param upperCardinality2 upperCardinality2
     * @return 4 "readable" cardinalities with "To" and "And" between them
     */
    private String parseCardinalities(int lowerCardinality1, int upperCardinality1, int lowerCardinality2, int upperCardinality2) {
        return parseCardinality(lowerCardinality1) + "To" + parseCardinality(upperCardinality1) +
                "And" +
                parseCardinality(lowerCardinality2) + "To" + parseCardinality(upperCardinality2);
    }

    //////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////

    private void testOneWayRelation(int lowerCardinality, int upperCardinality, boolean isContainment, boolean isSelf) {
        // TODO assert foreignkeysql name unique (self, oneway)

        // Check parameters
        if (!((lowerCardinality == 0 && upperCardinality == 1) ||
                (lowerCardinality == 1 && upperCardinality == 1) ||
                (lowerCardinality == 0 && upperCardinality == -1) ||
                (lowerCardinality == 1 && upperCardinality == -1)))
            throw new IllegalArgumentException(String.format("Invalid cardinalities: %d, %d", lowerCardinality, upperCardinality));

        // Create eclass1
        final EClass oneWayRelation1 = newEClassBuilder()
                .withName("OneWayRelation1")
                .withEAnnotations(newEntityEAnnotation())
                .build();

        // Create relation/reference
        final EReference oneWayReference = newEReferenceBuilder()
                .withName("oneWayReference")
                .withLowerBound(lowerCardinality)
                .withUpperBound(upperCardinality)
                .withContainment(isContainment)
                .withEType(oneWayRelation1)
                .build();

        // Create epackage
        final EPackage ePackage;
        if (!isSelf) {
            final EClass oneWayRelation2 = newEClassBuilder()
                    .withName("OneWayRelation2")
                    .withEAnnotations(newEntityEAnnotation())
                    .withEStructuralFeatures(oneWayReference)
                    .build();

            ePackage = newEPackage(ImmutableList.of(oneWayRelation2, oneWayRelation1));
        } else {
            oneWayRelation1.getEStructuralFeatures().add(oneWayReference);
            ePackage = newEPackage(oneWayRelation1);
        }

        // Add content to asmModel
        asmModel.addContent(ePackage);

        // "Calculate" name for saved file during test
        String transformationName = "testOneWay";
        if (!isContainment && !isSelf) {
            transformationName += "RelationWith" + parseCardinalities(lowerCardinality, upperCardinality) + "Cardinality";
        } else if (isContainment && !isSelf) {
            transformationName += "ContainmentWith" + parseCardinalities(lowerCardinality, upperCardinality) + "Cardinality";
        } else if (!isContainment) {
            transformationName += "SelfRelationWidth" + parseCardinalities(lowerCardinality, upperCardinality) + "Cardinality";
        } else {
            transformationName += "SelfContainmentWith" + parseCardinalities(lowerCardinality, upperCardinality) + "Cardinality";
        }

        executeTransformation(transformationName);

        // SAVE - table and reference name
        final String RDBMS_TABLE_NAME_1 = "TestEpackage.OneWayRelation1";
        final String RDBMS_TABLE_NAME_2 = isSelf ? RDBMS_TABLE_NAME_1 : "TestEpackage.OneWayRelation2";
        String ONE_WAY_REFERENCE;
        if (isContainment) {
            if (!isSelf) {
                ONE_WAY_REFERENCE = "oneWayRelation2OneWayReference";
            } else {
                ONE_WAY_REFERENCE = "oneWayRelation1OneWayReference";
            }
        } else {
            ONE_WAY_REFERENCE = "oneWayReference";
        }

        // Calculate the possible variates of table- and field numbers
        int expectedTableNumber, expectedFieldNumber1, expectedFieldNumber2 = 0;
        if (upperCardinality == -1 && isContainment && isSelf) {
            expectedTableNumber = 1;
            expectedFieldNumber1 = 3;
        } else if (upperCardinality == -1 && isContainment) {
            expectedTableNumber = 2;
            expectedFieldNumber1 = 3;
            expectedFieldNumber2 = 2;
        } else if (upperCardinality == -1 && isSelf) {
            expectedTableNumber = 2;
            expectedFieldNumber1 = 2;
        } else if (upperCardinality != -1 && isContainment && isSelf) {
            expectedTableNumber = 1;
            expectedFieldNumber1 = 3;
        } else if (upperCardinality == -1) {
            expectedTableNumber = 3;
            expectedFieldNumber1 = 2;
            expectedFieldNumber2 = 2;
        } else if (isContainment) {
            expectedTableNumber = 2;
            expectedFieldNumber1 = 3;
            expectedFieldNumber2 = 2;
        } else if (isSelf) {
            expectedTableNumber = 1;
            expectedFieldNumber1 = 3;
        } else {
            expectedTableNumber = 2;
            expectedFieldNumber1 = 2;
            expectedFieldNumber2 = 3;
        }

        // ASSERTION - correct number of tables
        assertEquals(expectedTableNumber, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables were found"))
                .size());

        // ASSERTION - correct number of fields
        assertEquals(expectedFieldNumber1, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_1)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                .size());
        if (!isSelf) {
            assertEquals(expectedFieldNumber2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_2)
                    .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                    .size());
        }


        if (upperCardinality == -1 && !isContainment) {
            // SAVE - junction table name
            final String RDBMS_JUNCTION_TABLE_NAME = RDBMS_TABLE_NAME_2 + "#" + ONE_WAY_REFERENCE + " to " + RDBMS_TABLE_NAME_1;

            // SAVE - junction table
            RdbmsJunctionTable rdbmsJunctionTable = rdbmsUtils.getRdbmsJunctionTable(RDBMS_JUNCTION_TABLE_NAME)
                    .orElseThrow(() -> new RuntimeException(RDBMS_JUNCTION_TABLE_NAME + " junction table not found"));

            // SAVE - foreign keys
            EList<RdbmsForeignKey> rdbmsForeignKeys = rdbmsUtils.getRdbmsForeignKeys(RDBMS_JUNCTION_TABLE_NAME)
                    .orElseThrow(() -> new RuntimeException(RDBMS_JUNCTION_TABLE_NAME + " junction table not found"));

            // SAVE - primary key 1
            RdbmsIdentifierField primaryKey1 = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_1)
                    .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                    .getPrimaryKey();

            // ASSERTION - primary key 1 can be found in junction table
            assertTrue(rdbmsForeignKeys.stream().anyMatch(o -> o.getReferenceKey().equals(primaryKey1)));

            // ASSERTION - field1 contains the correct primary key
            assertEquals(rdbmsJunctionTable.getField1().getReferenceKey(), primaryKey1);

            if (isSelf) {
                // ASSERTION - field2 contains the correct primary key
                assertEquals(rdbmsJunctionTable.getField2().getReferenceKey(), primaryKey1);
            } else {
                // SAVE - primary key 2
                RdbmsIdentifierField primaryKey2 = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_2)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                        .getPrimaryKey();

                // ASSERTION - primary key 2 can be found in junction table
                assertTrue(rdbmsForeignKeys.stream().anyMatch(o -> o.getReferenceKey().equals(primaryKey2)));

                // ASSERTION - field2 contains the correct primary key
                assertEquals(rdbmsJunctionTable.getField2().getReferenceKey(), primaryKey2);
            }

        } else {
            if (!isContainment || isSelf) {
                // ASSERTION - foreign key contains to the correct primary key
                assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_1)
                                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                                .getPrimaryKey(),
                        rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME_2, ONE_WAY_REFERENCE)
                                .orElseThrow(() -> new RuntimeException(ONE_WAY_REFERENCE + " attribute not found"))
                                .getReferenceKey());
            } else {
                // ASSERTION - foreign key contains to the correct primary key
                assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_2)
                                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                                .getPrimaryKey(),
                        rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME_1, ONE_WAY_REFERENCE)
                                .orElseThrow(() -> new RuntimeException(ONE_WAY_REFERENCE + " attribute not found"))
                                .getReferenceKey());
            }
        }

    }

    //////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////

    private void testTwoWayRelation(int lowerCardinality, int upperCardinality, boolean isSelf) {
        // Check parameters
        if (!((lowerCardinality == 0 && upperCardinality == 1) ||
                (lowerCardinality == 1 && upperCardinality == 1) ||
                (lowerCardinality == 0 && upperCardinality == -1) ||
                (lowerCardinality == 1 && upperCardinality == -1)))
            throw new IllegalArgumentException(String.format("Invalid cardinalities: %d, %d", lowerCardinality, upperCardinality));

        testTwoWayRelation(lowerCardinality, upperCardinality, lowerCardinality, upperCardinality, isSelf);
    }

    private void testTwoWayRelation(int lowerCardinality1, int upperCardinality1, int lowerCardinality2, int upperCardinality2, boolean isSelf) {
        // Check parameters
        if (!((lowerCardinality1 == 0 && upperCardinality1 == 1) ||
                (lowerCardinality1 == 1 && upperCardinality1 == 1) ||
                (lowerCardinality1 == 0 && upperCardinality1 == -1) ||
                (lowerCardinality1 == 1 && upperCardinality1 == -1)) &&
                !((lowerCardinality2 == 0 && upperCardinality2 == 1) ||
                        (lowerCardinality2 == 1 && upperCardinality2 == 1) ||
                        (lowerCardinality2 == 0 && upperCardinality2 == -1) ||
                        (lowerCardinality2 == 1 && upperCardinality2 == -1)))
            throw new IllegalArgumentException(String.format("Invalid cardinalities: %d, %d", lowerCardinality1, upperCardinality1));

        final EClass twoWayRelation1 = newEClassBuilder()
                .withName("TwoWayRelation1")
                .withEAnnotations(newEntityEAnnotation())
                .build();

        // create references
        final EReference twoWayReference1 = newEReferenceBuilder()
                .withName("twoWayReference1")
                .withLowerBound(lowerCardinality1)
                .withUpperBound(upperCardinality1)
                .withEType(twoWayRelation1)
                .build();

        final EReference twoWayReference2 = newEReferenceBuilder()
                .withName("twoWayReference2")
                .withLowerBound(lowerCardinality2)
                .withUpperBound(upperCardinality2)
                .build();

        // add opposites
        twoWayReference2.setEOpposite(twoWayReference1);

        // add references
        twoWayRelation1.getEStructuralFeatures().add(twoWayReference2);

        final EPackage ePackage;
        if (!isSelf) {
            final EClass twoWayRelation2 = newEClassBuilder()
                    .withName("TwoWayRelation2")
                    .withEAnnotations(newEntityEAnnotation())
                    .build();
            twoWayReference2.setEType(twoWayRelation2);
            twoWayReference1.setEOpposite(twoWayReference2);
            twoWayRelation2.getEStructuralFeatures().add(twoWayReference1);
            ePackage = newEPackage(ImmutableList.of(twoWayRelation1, twoWayRelation2));
        } else {
            twoWayReference2.setEType(twoWayRelation1);
            twoWayReference1.setEOpposite(twoWayReference2);
            twoWayRelation1.getEStructuralFeatures().add(twoWayReference1);
            ePackage = newEPackage(ImmutableList.of(twoWayRelation1));
        }

        asmModel.addContent(ePackage);

        String transformationName = "testTwoWay";
        if (!isSelf) {
            transformationName += "RelationWith" + parseCardinalities(lowerCardinality1, upperCardinality1, upperCardinality2, upperCardinality2) + "Cardinalities";
        } else {
            transformationName += "SelfRelationWith" + parseCardinalities(lowerCardinality1, upperCardinality1, upperCardinality2, upperCardinality2) + "Cardinalities";
        }

        executeTransformation(transformationName);

        final String RDBMS_TABLE_NAME_1;
        final String RDBMS_TABLE_NAME_2;
        final String TWO_WAY_REFERENCE;
        if (upperCardinality1 == -1 && upperCardinality2 == -1) {
            RDBMS_TABLE_NAME_1 = "TestEpackage.TwoWayRelation1";
            RDBMS_TABLE_NAME_2 = "TestEpackage.TwoWayRelation2";
            final String RDBMS_JUNCTION_TABLE = RDBMS_TABLE_NAME_2 + "#twoWayReference1 to " + RDBMS_TABLE_NAME_1 + "#twoWayReference2";

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
        } else {
            final boolean decider;
            if (upperCardinality1 == -1 || upperCardinality2 == -1) {
                decider = upperCardinality1 == -1;
            } else {
                if (lowerCardinality1 == lowerCardinality2 && lowerCardinality1 == 1) {
                    decider = true;
                } else if (lowerCardinality1 == lowerCardinality2) {
                    decider = false;
                } else {
                    decider = lowerCardinality1 != 1;
                }
            }
            RDBMS_TABLE_NAME_1 = "TestEpackage.TwoWayRelation1";
            RDBMS_TABLE_NAME_2 = "TestEpackage.TwoWayRelation2";
            TWO_WAY_REFERENCE = "twoWayReference" + (decider ? "2" : "1");

            // ASSERTION - check if number of tables is correct
            assertEquals(2, rdbmsUtils.getRdbmsTables()
                    .orElseThrow(() -> new RuntimeException("No tables found"))
                    .size());


            // ASSERTION - check if number of fields is correct in each table
            assertEquals(decider ? 3 : 2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_1)
                    .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                    .size());
            assertEquals(decider ? 2 : 3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_2)
                    .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                    .size());

            final String contained = decider ? RDBMS_TABLE_NAME_2 : RDBMS_TABLE_NAME_1;
            final String container = decider ? RDBMS_TABLE_NAME_1 : RDBMS_TABLE_NAME_2;
            // ASSERTION - check if foreign key is valid
            assertEquals(rdbmsUtils.getRdbmsTable(contained)
                            .orElseThrow(() -> new RuntimeException(contained + " table not found"))
                            .getPrimaryKey(),
                    rdbmsUtils.getRdbmsForeignKey(container, TWO_WAY_REFERENCE)
                            .orElseThrow(() -> new RuntimeException(TWO_WAY_REFERENCE + " field not found"))
                            .getReferenceKey());
        }

    }

    //////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////

    @Test
    @DisplayName("Test OneWayRelation With Null To Infinite Cardinality")
    public void testOneWayRelationWithNullToInfiniteCardinality() {
        testOneWayRelation(0, -1, false, false);
    }

    @Test
    @DisplayName("Test OneWayRelation With One To Infinite Cardinality")
    public void testOneWayRelationWithOneToInfiniteCardinality() {
        testOneWayRelation(1, -1, false, false);
    }

    @Test
    @DisplayName("Test OneWayRelation With Null To One Cardinality")
    public void testOneWayRelationWithNullToOneCardinality() {
        testOneWayRelation(0, 1, false, false);
    }

    @Test
    @DisplayName("Test OneWayRelation With One To One Cardinality")
    public void testOneWayRelationWithOneToOneCardinality() {
        testOneWayRelation(1, 1, false, false);
    }

    @Test
    @DisplayName("Test OneWayContainment With Null To Infinite Cardinality")
    public void testOneWayContainmentWithNullToInfiniteCardinality() {
        testOneWayRelation(0, -1, true, false);
    }

    @Test
    @DisplayName("Test OneWayContainment With One To Infinite Cardinality")
    public void testOneWayContainmentWithOneToInfiniteCardinality() {
        testOneWayRelation(1, -1, true, false);
    }

    @Test
    @DisplayName("Test OneWayContainment With Null To One Cardinality")
    public void testOneWayContainmentWithNullToOneCardinality() {
        testOneWayRelation(0, 1, true, false);
    }

    @Test
    @DisplayName("Test OneWayContainment With One To One Cardinality")
    public void testOneWayContainmentWithOneToOneCardinality() {
        testOneWayRelation(1, 1, true, false);
    }

    @Test
    @DisplayName("Test TwoWayRelation With Null To Infinite Cardinalities")
    public void testTwoWayRelationWithNullToInfiniteCardinalities() {
        testTwoWayRelation(0, -1, false);
    }

    @Test
    @DisplayName("Test TwoWayRelation With One To Infinite And Null To Infinite Cardinalities")
    public void testTwoWayRelationWithOneToInfiniteAndNullToInfiniteCardinalities() {
        testTwoWayRelation(1, -1, 0, -1, false);
    }

    @Test
    @DisplayName("Test TwoWayRelation With Null To Infinite And One To Infinite Cardinalities")
    public void testTwoWayRelationWithNullToInfiniteAndOneToInfiniteCardinalities() {
        testTwoWayRelation(0, -1, 1, -1, false);
    }

    @Test
    @DisplayName("Test TwoWayRelation With One To Infinite Cardinalities")
    public void testTwoWayRelationWithOneToInfiniteCardinalities() {
        testTwoWayRelation(1, -1, false);
    }

    @Test
    @DisplayName("Test TwoWayRelation With Null To Infinite And Null To One Cardinalities")
    public void testTwoWayRelationWithNullToInfiniteAndNullToOneCardinalities() {
        testTwoWayRelation(0, -1, 0, 1, false);

    }

    @Test
    @DisplayName("Test TwoWayRelation With Null To Infinite And One To One Cardinalities")
    public void testTwoWayRelationWithNullToInfiniteAndOneToOneCardinalities() {
        testTwoWayRelation(0, -1, 1, 1, false);
    }

    @Test
    @DisplayName("Test TwoWayRelation With One To Infinite And Null To One Cardinalities")
    public void testTwoWayRelationWithOneToInfiniteAndNullToOneCardinalities() {
        testTwoWayRelation(1, -1, 0, 1, false);
    }

    @Test
    @DisplayName("Test TwoWayRelation With One To Infinite And One To One Cardinalities")
    public void testTwoWayRelationWithOneToInfiniteAndOneToOneCardinalities() {
        testTwoWayRelation(1, -1, 1, 1, false);
    }

    @Test
    @DisplayName("Test TwoWayRelation With Null To One And Null To Infinite Cardinalities")
    public void testTwoWayRelationWithNullToOneAndNullToInfiniteCardinalities() {
        testTwoWayRelation(0, 1, 0, -1, false);
    }

    @Test
    @DisplayName("Test TwoWayRelation With Null To One And One To Infinite Cardinalities")
    public void testTwoWayRelationWithNullToOneAndOneToInfiniteCardinalities() {
        testTwoWayRelation(1, 1, 1, -1, false);
    }

    @Test
    @DisplayName("Test TwoWayRelation With Null To One Cardinalities")
    public void testTwoWayRelationWithNullToOneCardinalities() {
        testTwoWayRelation(0, 1, false);
    }


    @Test
    @DisplayName("Test TwoWayRelation With Null To One And One To One Cardinalities")
    public void testTwoWayRelationWithNullToOneAndOnetoOneCardinalities() {
        testTwoWayRelation(0, 1, 1, 1, false);
    }

    @Test
    @DisplayName("Test TwoWayRelation With One To One And Null to Infinite Cardinalities")
    public void testTwoWayRelationWithOneToOneAndNulltoInfiniteCardinalities() {
        testTwoWayRelation(1, 1, 0, -1, false);
    }

    @Test
    @DisplayName("Test TwoWayRelation With One To One And One to Infinite Cardinalities")
    public void testTwoWayRelationWithOneToOneAndOnetoInfiniteCardinalities() {
        testTwoWayRelation(1, 1, 1, -1, false);
    }

    @Test
    @DisplayName("Test TwoWayRelation With One To One And Null to One Cardinalities")
    public void testTwoWayRelationWithOneToOneAndNulltoOneCardinalities() {
        testTwoWayRelation(1, 1, 0, 1, false);
    }

    @Test
    @DisplayName("Test TwoWayRelation With One To One Cardinalities")
    public void testTwoWayRelationWithOneToOneCardinalities() {
        testTwoWayRelation(1, 1, false);
    }

    @Test
    @DisplayName("Test Self OneWayRelation With Null to One Cardinality")
    public void testSelfOneWayRelationWithNulltoOneCardinality() {
        testOneWayRelation(0, 1, false, true);
    }

    @Test
    @DisplayName("Test Self OneWayRelation With One to One Cardinality")
    public void testSelfOneWayRelationWithOnetoOneCardinality() {
        testOneWayRelation(1, 1, false, true);
    }

    @Test
    @DisplayName("Test Self OneWayRelation With Null to Infinite Cardinality")
    public void testSelfOneWayRelationWithNulltoInfiniteCardinality() {
        testOneWayRelation(0, -1, false, true);
    }

    @Test
    @DisplayName("Test Self OneWayRelation With One to Infinite Cardinality")
    public void testSelfOneWayRelationWithOnetoInfiniteCardinality() {
        testOneWayRelation(1, -1, false, true);
    }

    @Test
    @DisplayName("Test Self OneWayContainment With Null to One Cardinality")
    public void testSelfOneWayContainmentWithNulltoOneCardinality() {
        testOneWayRelation(0, 1, true, true);
    }

    @Test
    @DisplayName("Test Self OneWayContainment With One to One Cardinality")
    public void testSelfOneWayContainmentWithOnetoOneCardinality() {
        testOneWayRelation(1, 1, true, true);
    }

    @Test
    @DisplayName("Test Self OneWayContainment With Null to Infinite Cardinality")
    public void testSelfOneWayContainmentWithNulltoInfiniteCardinality() {
        testOneWayRelation(0, -1, true, true);
    }

    @Test
    @DisplayName("Test Self OneWayContainment With One to Infinite Cardinality")
    public void testSelfOneWayContainmentWithOnetoInfiniteCardinality() {
        testOneWayRelation(1, -1, true, true);
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
