package hu.blackbelt.judo.tatami.asm2rdbms;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.judo.meta.rdbmsNameMapping.util.builder.NameMappingsBuilder;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EReference;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static hu.blackbelt.judo.meta.asm.runtime.AsmUtils.addExtensionAnnotation;
import static hu.blackbelt.judo.meta.rdbmsNameMapping.util.builder.NameMappingBuilder.create;
import static org.eclipse.emf.ecore.EcorePackage.eINSTANCE;
import static org.eclipse.emf.ecore.util.builder.EcoreBuilders.*;
import static org.junit.jupiter.api.Assertions.assertEquals;

public class Asm2RdbmsNameMappingTest extends Asm2RdbmsMappingTestBase {

    @Test
    @DisplayName("Test Table And Attribute Name Mapping")
    public void testTableAndAttributeNameMapping() {
        final String RDBMS_TABLE_NAME = "TestEpackage.TestEclass";
        final String RDBMS_ATTRIBUTE_NAME = "TestEpackage.TestEclass#nameMappingAttribute";
        final String NAME_MAPPING_STRING = "NewName";

        // create name mappings
        rdbmsModel.addContent(
                NameMappingsBuilder.create()
                        .withNameMappings(ImmutableList.of(
                                create() // table
                                        .withFullyQualifiedName(RDBMS_TABLE_NAME)
                                        .withRdbmsName(RDBMS_TABLE_NAME + NAME_MAPPING_STRING)
                                        .build(),
                                create() // attribute
                                        .withFullyQualifiedName(RDBMS_ATTRIBUTE_NAME)
                                        .withRdbmsName(RDBMS_ATTRIBUTE_NAME + NAME_MAPPING_STRING)
                                        .build()
                        ))
                        .build()
        );

        EClass testEclass = newEClassBuilder()
                .withName("TestEclass")
                .withEStructuralFeatures(
                        newEAttributeBuilder()
                                .withName("nameMappingAttribute")
                                .withEType(eINSTANCE.getEString())
                                .build()
                )
                .build();
        addExtensionAnnotation(testEclass, ENTITY_ANNOTATION, VALUE_ANNOTATION);

        EPackage ePackage = newEPackage(testEclass);

        asmModel.addContent(ePackage);

        executeTransformation("testTableAndAttributeNameMapping");

        // ASSERTION - compare new sql name
        assertEquals(
                rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME)
                        .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_NAME + " table not found"))
                        .getSqlName(),
                RDBMS_TABLE_NAME + NAME_MAPPING_STRING
        );

        // ASSERTION - compare new sql name
        assertEquals(
                rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME, RDBMS_ATTRIBUTE_NAME)
                        .orElseThrow(() -> new RuntimeException(RDBMS_ATTRIBUTE_NAME + " table not found"))
                        .getSqlName(),
                RDBMS_ATTRIBUTE_NAME + NAME_MAPPING_STRING
        );
    }

    @Test
    @DisplayName("Test Reference Name Mapping")
    public void testReferenceNameMapping() {
        //final String RDBMS_TABLE_NAME = "TestEpackage.TestEclass";
        final String RDBMS_TABLE_NAME2 = "TestEpackage.TestEclass2";
        final String RDBMS_REFERENCE_NAME = "nameMappingReference";
        final String RDBMS_SELF_REFERENCE_NAME = "selfReference";
        //final String RDBMS_CONTAINMENT_NAME = "testEclass2NameMappingContainment";
        final String NAME_MAPPING_STRING = "NewName";

        // create mappings
        rdbmsModel.addContent(
                NameMappingsBuilder.create()
                        .withNameMappings(ImmutableList.of(
                                create() // reference
                                        .withFullyQualifiedName(RDBMS_TABLE_NAME2 + "#" + RDBMS_REFERENCE_NAME)
                                        .withRdbmsName(RDBMS_REFERENCE_NAME + NAME_MAPPING_STRING)
                                        .build(),
                                create() // self reference
                                        .withFullyQualifiedName(RDBMS_TABLE_NAME2 + "#" + RDBMS_SELF_REFERENCE_NAME)
                                        .withRdbmsName(RDBMS_SELF_REFERENCE_NAME + NAME_MAPPING_STRING)
                                        .build()
//                                create() // containment
//                                        .withFullyQualifiedName("TestEpackage.TestEclass") // FIXME
//                                        .withRdbmsName(RDBMS_CONTAINMENT_NAME + NAME_MAPPING_STRING)
//                                        .build()
                        ))
                        .build()
        );

        EClass testEclass = newEClassBuilder()
                .withName("TestEclass")
                .build();
        addExtensionAnnotation(testEclass, ENTITY_ANNOTATION, VALUE_ANNOTATION);

        EClass testEclass2 = newEClassBuilder()
                .withName("TestEclass2")
                .withEStructuralFeatures(ImmutableList.of(
                        newEReferenceBuilder()
                                .withName("nameMappingReference")
                                .withLowerBound(1)
                                .withUpperBound(1)
                                .withEType(testEclass)
                                .build()
//                        newEReferenceBuilder()
//                                .withName("nameMappingContainment")
//                                .withLowerBound(1)
//                                .withUpperBound(1)
//                                .withContainment(true)
//                                .withEType(testEclass)
//                                .build()
                ))
                .build();
        addExtensionAnnotation(testEclass2, ENTITY_ANNOTATION, VALUE_ANNOTATION);

        testEclass2.getEStructuralFeatures().add(
                newEReferenceBuilder()
                        .withName("selfReference")
                        .withLowerBound(1)
                        .withUpperBound(1)
                        .withEType(testEclass2)
                        .build()
        );

        EPackage ePackage = newEPackage(ImmutableList.of(testEclass, testEclass2));

        asmModel.addContent(ePackage);

        executeTransformation("testReferenceNameMapping");

        // ASSERTION - compare new sql name
        assertEquals(
                RDBMS_REFERENCE_NAME + NAME_MAPPING_STRING,
                rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME2, RDBMS_REFERENCE_NAME)
                        .orElseThrow(() -> new RuntimeException(RDBMS_REFERENCE_NAME + " field not found"))
                        .getSqlName()
        );

        // ASSERTION - compare new sql name
        assertEquals(
                RDBMS_SELF_REFERENCE_NAME + NAME_MAPPING_STRING,
                rdbmsUtils.getRdbmsField(RDBMS_TABLE_NAME2, RDBMS_SELF_REFERENCE_NAME)
                        .orElseThrow(() -> new RuntimeException(RDBMS_SELF_REFERENCE_NAME + " field not found"))
                        .getSqlName()
        );

    }

    @Test
    @DisplayName("Test Junction Table Name Mapping")
    public void testJunctionTableNameMapping() {

        final String JUNCTION_TABLE_NAME = "TestEpackage.TestEclass#TestEclass2Ref" +
                "_TestEpackage.TestEclass2#TestEclassRef";

        // create mapping
        rdbmsModel.addContent(
                NameMappingsBuilder.create()
                        .withNameMappings(ImmutableList.of(
                                create() // reference
                                        .withFullyQualifiedName(JUNCTION_TABLE_NAME)
                                        .withRdbmsName("NewSqlNameForJunctionTable")
                                        .build(),
                                create() // junct. field
                                        .withFullyQualifiedName("TestEpackage.TestEclass2#TestEclassRef")
                                        .withRdbmsName("TestRefNewName")
                                        .build(),
                                create() // junct. field
                                        .withFullyQualifiedName("TestEpackage.TestEclass#TestEclass2Ref")
                                        .withRdbmsName("TestRef2NewName")
                                        .build()
                        ))
                        .build()
        );

        EClass testEclass = newEClassBuilder()
                .withName("TestEclass")
                .build();
        addExtensionAnnotation(testEclass, ENTITY_ANNOTATION, VALUE_ANNOTATION);

        EClass testEclass2 = newEClassBuilder()
                .withName("TestEclass2")
                .build();
        addExtensionAnnotation(testEclass2, ENTITY_ANNOTATION, VALUE_ANNOTATION);

        EReference testEreference = newEReferenceBuilder()
                .withName("TestEclassRef")
                .withEType(testEclass)
                .withLowerBound(0)
                .withUpperBound(-1)
                .build();
        EReference testEreference2 = newEReferenceBuilder()
                .withName("TestEclass2Ref")
                .withEType(testEclass2)
                .withLowerBound(0)
                .withUpperBound(-1)
                .build();

        testEreference.setEOpposite(testEreference2);
        testEreference2.setEOpposite(testEreference);

        testEclass.getEStructuralFeatures().add(testEreference2);
        testEclass2.getEStructuralFeatures().add(testEreference);

        EPackage ePackage = newEPackage(ImmutableList.of(testEclass, testEclass2));

        asmModel.addContent(ePackage);

        executeTransformation("testJunctionTableNameMapping");

        // ASSERTION - compare new sql name
        assertEquals(
                "NewSqlNameForJunctionTable",
                rdbmsUtils.getRdbmsJunctionTables()
                        .orElseThrow(() -> new RuntimeException("Junction tables not found"))
                        .get(0).getSqlName()
        );

        // ASSERTION - compare new sql name
        assertEquals(
                "TestRef2NewName",
                rdbmsUtils.getRdbmsJunctionTables()
                        .orElseThrow(() -> new RuntimeException("Junction tables not found"))
                        .get(0).getField1().getSqlName()
        );

        // ASSERTION - compare new sql name
        assertEquals(
                "TestRefNewName",
                rdbmsUtils.getRdbmsJunctionTables()
                        .orElseThrow(() -> new RuntimeException("Junction tables not found"))
                        .get(0).getField2().getSqlName()
        );

    }

}
