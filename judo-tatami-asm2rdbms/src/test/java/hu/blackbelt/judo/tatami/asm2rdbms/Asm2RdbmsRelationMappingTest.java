package hu.blackbelt.judo.tatami.asm2rdbms;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.judo.meta.rdbms.RdbmsForeignKey;
import hu.blackbelt.judo.meta.rdbms.RdbmsIdentifierField;
import hu.blackbelt.judo.meta.rdbms.RdbmsJunctionTable;
import org.eclipse.emf.common.util.EList;
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
     * @throws IllegalArgumentException if cardinality is not valid
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
     * @throws IllegalArgumentException if cardinality is not valid
     */
    private String parseCardinalities(int lowerCardinality1, int upperCardinality1, int lowerCardinality2, int upperCardinality2) {
        return parseCardinality(lowerCardinality1) + "To" + parseCardinality(upperCardinality1) +
                "And" +
                parseCardinality(lowerCardinality2) + "To" + parseCardinality(upperCardinality2);
    }

    //////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////

    /**
     * Calls {@link #testOneWayRelation(int, int, boolean, boolean)} with both boolean parameters with false value
     *
     * @param lowerCardinality lowerCardinality
     * @param upperCardinality upperCardinality
     * @throws IllegalArgumentException if cardinality is not valid
     */
    private void testOneWayRelation(int lowerCardinality, int upperCardinality) {
        testOneWayRelation(lowerCardinality, upperCardinality, false, false);
    }

    /**
     * Calls {@link #testOneWayRelation(int, int, boolean, boolean)} with isSelf parameter with false value
     *
     * @param lowerCardinality lowerCardinality
     * @param upperCardinality upperCardinality
     * @param isContainment    isContainment
     * @throws IllegalArgumentException if cardinality is not valid
     */
    private void testOneWayRelation(int lowerCardinality, int upperCardinality, boolean isContainment) {
        testOneWayRelation(lowerCardinality, upperCardinality, isContainment, false);
    }

    /**
     * Tests one way reference with given cardintaliy.
     * It can be a containment and/or can reference to itself.
     *
     * @param lowerCardinality lowerCardinality
     * @param upperCardinality upperCardinality
     * @param isContainment    isContainment
     * @param isSelf           isSelf
     * @throws IllegalArgumentException if cardinality is not valid
     */
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
        final String transformationName;
        if (!isContainment && !isSelf) {
            transformationName = "testOneWayRelationWith" + parseCardinalities(lowerCardinality, upperCardinality) + "Cardinality";
        } else if (isContainment && !isSelf) {
            transformationName = "testOneWayContainmentWith" + parseCardinalities(lowerCardinality, upperCardinality) + "Cardinality";
        } else if (!isContainment) {
            transformationName = "testOneWaySelfRelationWidth" + parseCardinalities(lowerCardinality, upperCardinality) + "Cardinality";
        } else {
            transformationName = "testOneWaySelfContainmentWith" + parseCardinalities(lowerCardinality, upperCardinality) + "Cardinality";
        }

        executeTransformation(transformationName);

        // SAVE - table and reference name
        final String RDBMS_TABLE_NAME_1 = "TestEpackage.OneWayRelation1";
        final String RDBMS_TABLE_NAME_2 = isSelf ? RDBMS_TABLE_NAME_1 : "TestEpackage.OneWayRelation2";
        final String ONE_WAY_REFERENCE = isContainment
                ? "oneWayRelation" + (!isSelf ? "2" : "1") + "OneWayReference"
                : "oneWayReference";

        // Calculate the possible variates of table- and field numbers
        int expectedTableNumber, expectedFieldNumber1, expectedFieldNumber2 = 0;
        if (upperCardinality == -1 && isContainment && isSelf) {
            expectedTableNumber = 1;
            expectedFieldNumber1 = 3;
        } else if (upperCardinality == -1 && isContainment) {
            expectedFieldNumber1 = 3;
            expectedTableNumber = expectedFieldNumber2 = 2;
        } else if (upperCardinality == -1 && isSelf) {
            expectedTableNumber = expectedFieldNumber1 = 2;
        } else if (upperCardinality != -1 && isContainment && isSelf) {
            expectedTableNumber = 1;
            expectedFieldNumber1 = 3;
        } else if (upperCardinality == -1) {
            expectedTableNumber = 3;
            expectedFieldNumber1 = expectedFieldNumber2 = 2;
        } else if (isContainment) {
            expectedFieldNumber1 = 3;
            expectedTableNumber = expectedFieldNumber2 = 2;
        } else if (isSelf) {
            expectedTableNumber = 1;
            expectedFieldNumber1 = 3;
        } else {
            expectedFieldNumber2 = 3;
            expectedTableNumber = expectedFieldNumber1 = 2;
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
            final String container = (!isContainment || isSelf) ? RDBMS_TABLE_NAME_1 : RDBMS_TABLE_NAME_2;
            final String contained = (!isContainment || isSelf) ? RDBMS_TABLE_NAME_2 : RDBMS_TABLE_NAME_1;
            // ASSERTION - foreign key contains to the correct primary key
            assertEquals(rdbmsUtils.getRdbmsTable(container)
                            .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                            .getPrimaryKey(),
                    rdbmsUtils.getRdbmsForeignKey(contained, ONE_WAY_REFERENCE)
                            .orElseThrow(() -> new RuntimeException(ONE_WAY_REFERENCE + " attribute not found"))
                            .getReferenceKey());
        }

    }

    //////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////
    //////////////////////////////////////////////////////////////////////////

    /**
     * Calls {@link #testTwoWayRelation(int, int, int, int, boolean)} where both end of the reference is the same
     * and isSelf is false
     *
     * @param lowerCardinality lowerCardinality
     * @param upperCardinality upperCardinality
     * @throws IllegalArgumentException if cardinality is not valid
     */
    private void testTwoWayRelation(int lowerCardinality, int upperCardinality) {
        testTwoWayRelation(lowerCardinality, upperCardinality, lowerCardinality, upperCardinality, false);
    }

    /**
     * Calls {@link #testTwoWayRelation(int, int, int, int, boolean)} where both end of the reference is the same.
     *
     * @param lowerCardinality lowerCardinality
     * @param upperCardinality upperCardinality
     * @param isSelf           isSelf
     * @throws IllegalArgumentException if cardinality is not valid
     */
    private void testTwoWayRelation(int lowerCardinality, int upperCardinality, boolean isSelf) {
        testTwoWayRelation(lowerCardinality, upperCardinality, lowerCardinality, upperCardinality, isSelf);
    }

    /**
     * Calls {@link #testTwoWayRelation(int, int, int, int, boolean)} where isSelf is false
     *
     * @param lowerCardinality1 lowerCardinality1
     * @param upperCardinality1 upperCardinality1
     * @param lowerCardinality2 lowerCardinality2
     * @param upperCardinality2 upperCardinality2
     * @throws IllegalArgumentException if cardinality is not valid
     */
    private void testTwoWayRelation(int lowerCardinality1, int upperCardinality1, int lowerCardinality2, int upperCardinality2) {
        testTwoWayRelation(lowerCardinality1, upperCardinality1, lowerCardinality2, upperCardinality2, false);
    }

    /**
     * Tests two way reference with given cardinalities.
     * It can reference to itself.
     *
     * @param lowerCardinality1 lowerCardinality1
     * @param upperCardinality1 upperCardinality1
     * @param lowerCardinality2 lowerCardinality2
     * @param upperCardinality2 upperCardinality2
     * @param isSelf            isSelf
     * @throws IllegalArgumentException if cardinality is not valid
     */
    private void testTwoWayRelation(int lowerCardinality1, int upperCardinality1, int lowerCardinality2, int upperCardinality2, boolean isSelf) {
        // Check parameters
        if (!((lowerCardinality1 == 0 && upperCardinality1 == 1) ||
                (lowerCardinality1 == 1 && upperCardinality1 == 1) ||
                (lowerCardinality1 == 0 && upperCardinality1 == -1) ||
                (lowerCardinality1 == 1 && upperCardinality1 == -1) ||
                (lowerCardinality2 == 0 && upperCardinality2 == 1) ||
                (lowerCardinality2 == 1 && upperCardinality2 == 1) ||
                (lowerCardinality2 == 0 && upperCardinality2 == -1) ||
                (lowerCardinality2 == 1 && upperCardinality2 == -1))) {
            throw new IllegalArgumentException(String.format("Invalid cardinalities: %d, %d, %d, %d", lowerCardinality1, upperCardinality1, lowerCardinality2, upperCardinality2));
        }

        // create eclass1
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
            // create eclass2
            final EClass twoWayRelation2 = newEClassBuilder()
                    .withName("TwoWayRelation2")
                    .withEAnnotations(newEntityEAnnotation())
                    .build();

            // setup relations
            twoWayReference2.setEType(twoWayRelation2);
            twoWayReference1.setEOpposite(twoWayReference2);
            twoWayRelation2.getEStructuralFeatures().add(twoWayReference1);
            ePackage = newEPackage(ImmutableList.of(twoWayRelation1, twoWayRelation2));
        } else {
            // setup relations
            twoWayReference2.setEType(twoWayRelation1);
            twoWayReference1.setEOpposite(twoWayReference2);
            twoWayRelation1.getEStructuralFeatures().add(twoWayReference1);
            ePackage = newEPackage(twoWayRelation1);
        }

        // Add content to asmModel
        asmModel.addContent(ePackage);

        final String transformationName = !isSelf
                ? "testTwoWayRelationWith" + parseCardinalities(lowerCardinality1, upperCardinality1, upperCardinality2, upperCardinality2) + "Cardinalities"
                : "testTwoWaySelfRelationWith" + parseCardinalities(lowerCardinality1, upperCardinality1, upperCardinality2, upperCardinality2) + "Cardinalities";
        executeTransformation(transformationName);

        final String RDBMS_TABLE_NAME_1 = "TestEpackage.TwoWayRelation1";
        final String RDBMS_TABLE_NAME_2 = isSelf ? RDBMS_TABLE_NAME_1 : "TestEpackage.TwoWayRelation2";
        final String TWO_WAY_REFERENCE;
        if (upperCardinality1 == -1 && upperCardinality2 == -1) {
            final String RDBMS_JUNCTION_TABLE = RDBMS_TABLE_NAME_2 + "#twoWayReference1 to " + RDBMS_TABLE_NAME_1 + "#twoWayReference2";

            // ASSERTION - check if tables exist
            assertEquals(isSelf ? 2 : 3, rdbmsUtils.getRdbmsTables()
                    .orElseThrow(() -> new RuntimeException("No tables were found"))
                    .size());

            // ASSERTION - check for correct number of fields
            assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_1)
                    .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                    .size()); //+2 id and type
            if (!isSelf) {
                assertEquals(2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_2)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                        .size());  //+2 id and type
            }

            // SAVE - rdbmsJunctionTable
            RdbmsJunctionTable rdbmsJunctionTable =
                    rdbmsUtils.getRdbmsJunctionTable(RDBMS_JUNCTION_TABLE)
                            .orElseThrow(() -> new RuntimeException(RDBMS_JUNCTION_TABLE + " junction table not found"));

            // SAVE - rdbmsForeignKeys
            EList<RdbmsForeignKey> rdbmsForeignKeys = rdbmsUtils.getRdbmsForeignKeys(RDBMS_JUNCTION_TABLE)
                    .orElseThrow(() -> new RuntimeException(RDBMS_JUNCTION_TABLE + " junction table not found"));

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

            TWO_WAY_REFERENCE = "twoWayReference" + (decider ? "2" : "1");

            // ASSERTION - check if number of tables is correct
            assertEquals(isSelf ? 1 : 2, rdbmsUtils.getRdbmsTables()
                    .orElseThrow(() -> new RuntimeException("No tables found"))
                    .size());


            // ASSERTION - check if number of fields is correct in each table
            assertEquals(decider || isSelf ? 3 : 2, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_1)
                    .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_1 + " table not found"))
                    .size());
            if (!isSelf) {
                assertEquals(decider ? 2 : 3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_NAME_2)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME_2 + " table not found"))
                        .size());
            }

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
        testOneWayRelation(0, -1);
    }

    @Test
    @DisplayName("Test OneWayRelation With One To Infinite Cardinality")
    public void testOneWayRelationWithOneToInfiniteCardinality() {
        testOneWayRelation(1, -1);
    }

    @Test
    @DisplayName("Test OneWayRelation With Null To One Cardinality")
    public void testOneWayRelationWithNullToOneCardinality() {
        testOneWayRelation(0, 1);
    }

    @Test
    @DisplayName("Test OneWayRelation With One To One Cardinality")
    public void testOneWayRelationWithOneToOneCardinality() {
        testOneWayRelation(1, 1);
    }

    @Test
    @DisplayName("Test OneWayContainment With Null To Infinite Cardinality")
    public void testOneWayContainmentWithNullToInfiniteCardinality() {
        testOneWayRelation(0, -1, true);
    }

    @Test
    @DisplayName("Test OneWayContainment With One To Infinite Cardinality")
    public void testOneWayContainmentWithOneToInfiniteCardinality() {
        testOneWayRelation(1, -1, true);
    }

    @Test
    @DisplayName("Test OneWayContainment With Null To One Cardinality")
    public void testOneWayContainmentWithNullToOneCardinality() {
        testOneWayRelation(0, 1, true);
    }

    @Test
    @DisplayName("Test OneWayContainment With One To One Cardinality")
    public void testOneWayContainmentWithOneToOneCardinality() {
        testOneWayRelation(1, 1, true);
    }

    @Test
    @DisplayName("Test TwoWayRelation With Null To Infinite Cardinalities")
    public void testTwoWayRelationWithNullToInfiniteCardinalities() {
        testTwoWayRelation(0, -1);
    }

    @Test
    @DisplayName("Test TwoWayRelation With One To Infinite And Null To Infinite Cardinalities")
    public void testTwoWayRelationWithOneToInfiniteAndNullToInfiniteCardinalities() {
        testTwoWayRelation(1, -1, 0, -1);
    }

    @Test
    @DisplayName("Test TwoWayRelation With Null To Infinite And One To Infinite Cardinalities")
    public void testTwoWayRelationWithNullToInfiniteAndOneToInfiniteCardinalities() {
        testTwoWayRelation(0, -1, 1, -1);
    }

    @Test
    @DisplayName("Test TwoWayRelation With One To Infinite Cardinalities")
    public void testTwoWayRelationWithOneToInfiniteCardinalities() {
        testTwoWayRelation(1, -1);
    }

    @Test
    @DisplayName("Test TwoWayRelation With Null To Infinite And Null To One Cardinalities")
    public void testTwoWayRelationWithNullToInfiniteAndNullToOneCardinalities() {
        testTwoWayRelation(0, -1, 0, 1);

    }

    @Test
    @DisplayName("Test TwoWayRelation With Null To Infinite And One To One Cardinalities")
    public void testTwoWayRelationWithNullToInfiniteAndOneToOneCardinalities() {
        testTwoWayRelation(0, -1, 1, 1);
    }

    @Test
    @DisplayName("Test TwoWayRelation With One To Infinite And Null To One Cardinalities")
    public void testTwoWayRelationWithOneToInfiniteAndNullToOneCardinalities() {
        testTwoWayRelation(1, -1, 0, 1);
    }

    @Test
    @DisplayName("Test TwoWayRelation With One To Infinite And One To One Cardinalities")
    public void testTwoWayRelationWithOneToInfiniteAndOneToOneCardinalities() {
        testTwoWayRelation(1, -1, 1, 1);
    }

    @Test
    @DisplayName("Test TwoWayRelation With Null To One And Null To Infinite Cardinalities")
    public void testTwoWayRelationWithNullToOneAndNullToInfiniteCardinalities() {
        testTwoWayRelation(0, 1, 0, -1);
    }

    @Test
    @DisplayName("Test TwoWayRelation With Null To One And One To Infinite Cardinalities")
    public void testTwoWayRelationWithNullToOneAndOneToInfiniteCardinalities() {
        testTwoWayRelation(1, 1, 1, -1);
    }

    @Test
    @DisplayName("Test TwoWayRelation With Null To One Cardinalities")
    public void testTwoWayRelationWithNullToOneCardinalities() {
        testTwoWayRelation(0, 1);
    }


    @Test
    @DisplayName("Test TwoWayRelation With Null To One And One To One Cardinalities")
    public void testTwoWayRelationWithNullToOneAndOnetoOneCardinalities() {
        testTwoWayRelation(0, 1, 1, 1);
    }

    @Test
    @DisplayName("Test TwoWayRelation With One To One And Null to Infinite Cardinalities")
    public void testTwoWayRelationWithOneToOneAndNulltoInfiniteCardinalities() {
        testTwoWayRelation(1, 1, 0, -1);
    }

    @Test
    @DisplayName("Test TwoWayRelation With One To One And One to Infinite Cardinalities")
    public void testTwoWayRelationWithOneToOneAndOnetoInfiniteCardinalities() {
        testTwoWayRelation(1, 1, 1, -1);
    }

    @Test
    @DisplayName("Test TwoWayRelation With One To One And Null to One Cardinalities")
    public void testTwoWayRelationWithOneToOneAndNulltoOneCardinalities() {
        testTwoWayRelation(1, 1, 0, 1);
    }

    @Test
    @DisplayName("Test TwoWayRelation With One To One Cardinalities")
    public void testTwoWayRelationWithOneToOneCardinalities() {
        testTwoWayRelation(1, 1);
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
        testTwoWayRelation(0, 1, true);
    }

    @Test
    @DisplayName("Test Self TwoWayRelation With One To One Cardinalities")
    public void testSelfTwoWayRelationWithOneToOneCardinalities() {
        testTwoWayRelation(1, 1, true);
    }

    @Test
    @DisplayName("Test Self TwoWayRelation With One To One And Null To One Cardinalities")
    public void testSelfTwoWayRelationWithOneToOneAndNullToOneCardinalities() {
        testTwoWayRelation(1, 1, 0, 1, true);
    }

    @Test
    @DisplayName("Test Self TwoWayRelation With Null To One And One To One Cardinalities")
    public void testSelfTwoWayRelationWithNullToOneAndOneToOneCardinalities() {
        testTwoWayRelation(0, 1, 1, 1, true);
    }

    @Test
    @DisplayName("Test Self TwoWayRelation With Null To One And Null To Infinite Cardinalities")
    public void testSelfTwoWayRelationWithNullToOneAndNullToInfiniteCardinalities() {
        testTwoWayRelation(0, 1, 0, -1, true);
    }

    @Test
    @DisplayName("Test Self TwoWayRelation With Null To One And One To Infinite Cardinalities")
    public void testSelfTwoWayRelationWithNullToOneAndOneToInfiniteCardinalities() {
        testTwoWayRelation(0, 1, 1, -1, true);
    }

    @Test
    @DisplayName("Test Self TwoWayRelation With One To One And Null To Infinite Cardinalities")
    public void testSelfTwoWayRelationWithOneToOneAndNullToInfiniteCardinalities() {
        testTwoWayRelation(1, 1, 0, -1, true);
    }

    @Test
    @DisplayName("Test Self TwoWayRelation With One To One And One To Infinite Cardinalities")
    public void testSelfTwoWayRelationWithOneToOneAndOneToInfiniteCardinalities() {
        testTwoWayRelation(1, 1, 1, -1, true);
    }

    @Test
    @DisplayName("Test Self TwoWayRelation With Null To Infinite And Null To One Cardinalities")
    public void testSelfTwoWayRelationWithNullToInfiniteAndNullToOneCardinalities() {
        testTwoWayRelation(0, -1, 0, 1, true);
    }

    @Test
    @DisplayName("Test Self TwoWayRelation With Null To Infinite And One To One Cardinalities")
    public void testSelfTwoWayRelationWithNullToInfiniteAndOneToOneCardinalities() {
        testTwoWayRelation(0, -1, 1, 1, true);
    }

    @Test
    @DisplayName("Test Self TwoWayRelation With One To Infinite And Null To One Cardinalities")
    public void testSelfTwoWayRelationWithOneToInfiniteAndNullToOneCardinalities() {
        testTwoWayRelation(1, -1, 0, 1, true);
    }

    @Test
    @DisplayName("Test Self TwoWayRelation With One To Infinite And One To One Cardinalities")
    public void testSelfTwoWayRelationWithOneToInfiniteAndOneToOneCardinalities() {
        testTwoWayRelation(1, -1, 1, 1, true);
    }

    @Test
    @DisplayName("Test Self TwoWayRelation With Null To Infinite Cardinalities")
    public void testSelfTwoWayRelationWithNullToInfiniteCardinalities() {
        testTwoWayRelation(0, -1, true);
    }

    @Test
    @DisplayName("Test Self TwoWayRelation With Null To Infinite And One To Infinite Cardinalities")
    public void testSelfTwoWayRelationWithNullToInfiniteAndOneToInfiniteCardinalities() {
        testTwoWayRelation(0, -1, 1, -1, true);
    }

    @Test
    @DisplayName("Test Self TwoWayRelation With One To Infinite And Null To Infinite Cardinalities")
    public void testSelfTwoWayRelationWithOneToInfiniteAndNullToInfiniteCardinalities() {
        testTwoWayRelation(1, -1, 0, -1, true);
    }

    @Test
    @DisplayName("Test Self TwoWayRelation With One To Infinite Cardinalities")
    public void testSelfTwoWayRelationWithOneToInfiniteCardinalities() {
        testTwoWayRelation(1, -1, true);
    }

}
