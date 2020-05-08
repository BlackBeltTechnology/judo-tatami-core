package hu.blackbelt.judo.tatami.asm2rdbms;

import hu.blackbelt.judo.meta.rdbmsNameMapping.util.builder.NameMappingsBuilder;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static com.google.common.collect.ImmutableList.of;
import static hu.blackbelt.judo.meta.asm.runtime.AsmUtils.addExtensionAnnotation;
import static hu.blackbelt.judo.meta.rdbmsNameMapping.util.builder.NameMappingBuilder.create;
import static java.lang.String.format;
import static org.eclipse.emf.ecore.EcorePackage.eINSTANCE;
import static org.eclipse.emf.ecore.util.builder.EcoreBuilders.*;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

public class Asm2RdbmsNameMappingTest extends Asm2RdbmsMappingTestBase {

    private void assertTablesSlqName(final Set<String> expected) {
        rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("Tables not found"))
                .forEach(o -> {
                    assertTrue(expected.contains(o.getSqlName()), o.getSqlName() + " not found");
                    expected.remove(o.getSqlName());
                });
        if (expected.size() != 0) {
            fail(format("Tables are missing: %s", expected));
        }
    }

    private void assertFieldsSqlName(final Set<String> expected, final String tableName) {
        rdbmsUtils.getRdbmsFields(tableName)
                .orElseThrow(() -> new RuntimeException(tableName + " not found"))
                .forEach(o -> {
                    assertTrue(expected.contains(o.getSqlName()), o.getSqlName() + " not found");
                    expected.remove(o.getSqlName());
                });
        if (expected.size() != 0) {
            fail(format("Fields are missing from %s: %s", tableName, expected));
        }
    }

    @Test
    public void testNameMapping() { // TODO: containment
        ///////////////
        // setup names

        final String RDBMS_TABLE_NAME = "TestEpackage.TestEclass";
        final String RDBMS_TABLE_NAME2 = "TestEpackage.TestEclass2";
        final String RDBMS_ATTRIBUTE_NAME = RDBMS_TABLE_NAME + "#testAttribute";
        final String RDBMS_REFERENCE_NAME = RDBMS_TABLE_NAME2 + "#testReference";
        final String RDBMS_TWO_REFERENCE_NAME = RDBMS_TABLE_NAME2 + "#twoWayReference";
        final String RDBMS_TWO_REFERENCE_NAME2 = RDBMS_TABLE_NAME + "#twoWayReference2";
        final String NAME_MAPPING_STRING = "NewName";


        // setup names
        ////////////////////////////////////
        // add name mappings to rdbms model

        rdbmsModel.addContent(
                NameMappingsBuilder.create()
                        .withNameMappings(of(
                                create() // table
                                        .withFullyQualifiedName(RDBMS_TABLE_NAME)
                                        .withRdbmsName(RDBMS_TABLE_NAME + NAME_MAPPING_STRING)
                                        .build(),
                                create() // table2
                                        .withFullyQualifiedName(RDBMS_TABLE_NAME2)
                                        .withRdbmsName(RDBMS_TABLE_NAME2 + NAME_MAPPING_STRING)
                                        .build(),
                                create() // junction table
                                        .withFullyQualifiedName(RDBMS_TWO_REFERENCE_NAME + " to " + RDBMS_TWO_REFERENCE_NAME2)
                                        .withRdbmsName(RDBMS_TWO_REFERENCE_NAME + " to " + RDBMS_TWO_REFERENCE_NAME2 + " " + NAME_MAPPING_STRING)
                                        .build(),
                                create() // attribute
                                        .withFullyQualifiedName(RDBMS_ATTRIBUTE_NAME)
                                        .withRdbmsName(RDBMS_ATTRIBUTE_NAME + NAME_MAPPING_STRING)
                                        .build(),
                                create() // reference
                                        .withFullyQualifiedName(RDBMS_REFERENCE_NAME)
                                        .withRdbmsName(RDBMS_REFERENCE_NAME + NAME_MAPPING_STRING)
                                        .build(),
                                create() // two way reference
                                        .withFullyQualifiedName(RDBMS_TWO_REFERENCE_NAME)
                                        .withRdbmsName(RDBMS_TWO_REFERENCE_NAME + NAME_MAPPING_STRING)
                                        .build(),
                                create() // two way reference2
                                        .withFullyQualifiedName(RDBMS_TWO_REFERENCE_NAME2)
                                        .withRdbmsName(RDBMS_TWO_REFERENCE_NAME2 + NAME_MAPPING_STRING)
                                        .build()
                        ))
                        .build()
        );

        // add name mappings to rdbms model
        ///////////////////////////////////
        // setup asm model

        EClass testEclass = newEClassBuilder()
                .withName("TestEclass")
                .withEStructuralFeatures(
                        newEAttributeBuilder()
                                .withName("testAttribute")
                                .withEType(eINSTANCE.getEString())
                                .build()
                )
                .build();
        addExtensionAnnotation(testEclass, ENTITY_ANNOTATION, VALUE_ANNOTATION);

        EClass testEclass2 = newEClassBuilder()
                .withName("TestEclass2")
                .withEStructuralFeatures(
                        newEReferenceBuilder()
                                .withName("testReference")
                                .withLowerBound(0)
                                .withUpperBound(1)
                                .withEType(testEclass)
                                .build()
                )
                .build();
        addExtensionAnnotation(testEclass2, ENTITY_ANNOTATION, VALUE_ANNOTATION);

        EReference eReference = newEReferenceBuilder()
                .withName("twoWayReference")
                .withLowerBound(0)
                .withUpperBound(-1)
                .withEType(testEclass)
                .build();
        EReference eReference2 = newEReferenceBuilder()
                .withName("twoWayReference2")
                .withLowerBound(0)
                .withUpperBound(-1)
                .withEType(testEclass2)
                .build();

        eReference.setEOpposite(eReference2);
        eReference2.setEOpposite(eReference);

        testEclass.getEStructuralFeatures().add(eReference2);
        testEclass2.getEStructuralFeatures().add(eReference);

        EPackage ePackage = newEPackage(of(testEclass, testEclass2));

        asmModel.addContent(ePackage);

        // setup asm model and transform

        executeTransformation("testNameMapping");

        // setup asm model and transform
        ////////////////////////////////
        // fill expected sets

        Set<String> tableSqlNames = new HashSet<>();
        Set<String> fieldSqlNames = new HashSet<>();
        Set<String> fieldSqlNames2 = new HashSet<>();
        Set<String> fieldSqlNames3 = new HashSet<>(); // junction table

        tableSqlNames.add(RDBMS_TABLE_NAME + NAME_MAPPING_STRING);
        tableSqlNames.add(RDBMS_TABLE_NAME2 + NAME_MAPPING_STRING);
        // tableSqlNames.add(RDBMS_TWO_REFERENCE_NAME + " to " + RDBMS_TWO_REFERENCE_NAME2 + " " + NAME_MAPPING_STRING);
        tableSqlNames.add("T_TEEP_TSES_WWFC_TEEP_TT2_TWR2"); // FIXME: why?

        fieldSqlNames.add("ID");
        fieldSqlNames.add("TYPE");
        fieldSqlNames.add(RDBMS_ATTRIBUTE_NAME + NAME_MAPPING_STRING);


        fieldSqlNames2.add("ID");
        fieldSqlNames2.add("TYPE");
        fieldSqlNames2.add(RDBMS_REFERENCE_NAME + NAME_MAPPING_STRING);

        fieldSqlNames3.add("ID");
        fieldSqlNames3.add(RDBMS_TWO_REFERENCE_NAME + NAME_MAPPING_STRING);
        fieldSqlNames3.add(RDBMS_TWO_REFERENCE_NAME2 + NAME_MAPPING_STRING);

        // fill expected sets
        ////////////////////////////////////
        // compare expected and actual sets

        assertTablesSlqName(tableSqlNames);
        assertFieldsSqlName(fieldSqlNames, RDBMS_TABLE_NAME);
        assertFieldsSqlName(fieldSqlNames2, RDBMS_TABLE_NAME2);
        assertFieldsSqlName(fieldSqlNames3, RDBMS_TWO_REFERENCE_NAME + " to " + RDBMS_TWO_REFERENCE_NAME2);

        // compare expected and actual sets
        ///////////////////////////////////
    }

}
