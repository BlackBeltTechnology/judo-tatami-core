package hu.blackbelt.judo.tatami.asm2rdbms;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.judo.meta.rdbms.RdbmsTable;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.eclipse.emf.ecore.util.builder.EcoreBuilders.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Asm2RdbmsInheritanceTest extends Asm2RdbmsMappingTestBase {

    @Test
    @DisplayName("Test Basic Inheritance")
    public void testBasicInheritance() {
        final EcorePackage ecore = EcorePackage.eINSTANCE;

        // create annotation
        EAnnotation eAnnotation = newEAnnotationBuilder()
                .withSource("http://blackbelt.hu/judo/meta/ExtendedMetadata/entity")
                .build();

        eAnnotation.getDetails().put("value", "true");

        // create annotation2
        EAnnotation eAnnotation2 = newEAnnotationBuilder()
                .withSource("http://blackbelt.hu/judo/meta/ExtendedMetadata/entity")
                .build();

        eAnnotation2.getDetails().put("value", "true");

        // create class fruit
        final EClass fruit = newEClassBuilder()
                .withName("fruit")
                .withEAnnotations(eAnnotation)
                .withEStructuralFeatures(
                        newEAttributeBuilder()
                                .withName("fruit_id")
                                .withEType(ecore.getEInt())
                                .build()
                )
                .build();

        // create class apple
        final EClass apple = newEClassBuilder()
                .withName("apple")
                .withESuperTypes(fruit)
                .withEStructuralFeatures(
                        newEAttributeBuilder()
                                .withName("apple_type")
                                .withEType(ecore.getEString())
                                .build()
                )
                .withEAnnotations(eAnnotation2)
                .build();

        // create package
        final EPackage ePackage = newEPackageBuilder()
                .withName("TestEpackage")
                .withEClassifiers(ImmutableList.of(fruit, apple))
                .withNsPrefix("test")
                .withNsURI("http:///com.example.test.ecore")
                .build();

        // add content to asm model
        asmModel.addContent(ePackage);

        executeTransformation("testBasicInheritance");

        final String RDBMS_TABLE_FRUIT = "TestEpackage.fruit";
        final String RDBMS_TABLE_APPLE = "TestEpackage.apple";
        final String RDBMS_FIELD_FRUIT_ID = "TestEpackage.fruit#fruit_id";
        final String RDBMS_FIELD_APPLE_TYPE = "TestEpackage.apple#apple_type";

        // ASSERTION - check if number of tables is correct
        assertEquals(2, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables found"))
                .size());

        // ASSERTION - check if number of fields is correct
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_FRUIT)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_FRUIT + " table not found"))
                .size());
        assertEquals(3, rdbmsUtils.getRdbmsFields(RDBMS_TABLE_APPLE)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_APPLE + " table not found"))
                .size());

        // ASSERTION - check if defined attributes are presented
        assertTrue(rdbmsUtils.getRdbmsField(RDBMS_TABLE_FRUIT, RDBMS_FIELD_FRUIT_ID).isPresent());
        assertTrue(rdbmsUtils.getRdbmsField(RDBMS_TABLE_APPLE, RDBMS_FIELD_APPLE_TYPE).isPresent());

        // SAVE - save 2 tables
        RdbmsTable rdbmsTable_fruit = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_FRUIT)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_FRUIT + " table not found"));
        RdbmsTable rdbmsTable_apple = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_APPLE)
                .orElseThrow(() -> new RuntimeException(RDBMS_TABLE_APPLE + " table not found"));

        // ASSERTION - check if apple table has only one parent
        assertEquals(1, rdbmsTable_apple.getParents().size());

        // ASSERTION - check if apple's parent is valid
        assertEquals(rdbmsTable_fruit.getPrimaryKey(),
                rdbmsTable_apple.getParents().get(0).getPrimaryKey());
    }

    @Disabled
    @Test
    @DisplayName("Test Inheritance With Two Parents")
    public void testInheritanceWithTwoParents() {
        final EcorePackage ecore = EcorePackage.eINSTANCE;

        // create annotation
        EAnnotation eAnnotation = newEAnnotationBuilder()
                .withSource("http://blackbelt.hu/judo/meta/ExtendedMetadata/entity")
                .build();

        eAnnotation.getDetails().put("value", "true");

        // create annotation1
        EAnnotation eAnnotation1 = newEAnnotationBuilder()
                .withSource("http://blackbelt.hu/judo/meta/ExtendedMetadata/entity")
                .build();

        eAnnotation.getDetails().put("value", "true");

        // create annotation2
        EAnnotation eAnnotation2 = newEAnnotationBuilder()
                .withSource("http://blackbelt.hu/judo/meta/ExtendedMetadata/entity")
                .build();

        eAnnotation.getDetails().put("value", "true");

        // create eclass vegetable
        EClass vegetable = newEClassBuilder()
                .withName("vegetables")
                .withEAnnotations(eAnnotation)
                .withEStructuralFeatures(
                        newEAttributeBuilder()
                                .withName("isVegetable")
                                .withEType(ecore.getEBoolean())
                                .build()
                )
                .build();

        // create eclass fruit
        EClass fruit = newEClassBuilder()
                .withName("fruits")
                .withEAnnotations(eAnnotation1)
                .withEStructuralFeatures(
                        newEAttributeBuilder()
                                .withName("isFruit")
                                .withEType(ecore.getEBoolean())
                                .build()
                )
                .build();

        // create eclass tomato
        EClass tomato = newEClassBuilder()
                .withName("tomato")
                .withEAnnotations(eAnnotation2)
                .withEStructuralFeatures(
                        newEAttributeBuilder()
                                .withName("tomato_type")
                                .withEType(ecore.getEString())
                                .build()
                )
                .withESuperTypes(ImmutableList.of(vegetable, fruit))
                .build();

        // create package
        final EPackage ePackage = newEPackageBuilder()
                .withName("TestEpackage")
                .withEClassifiers(ImmutableList.of(vegetable, fruit, tomato))
                .withNsPrefix("test")
                .withNsURI("http:///com.example.test.ecore")
                .build();

        // add content to asm model
        asmModel.addContent(ePackage);

        executeTransformation("testInheritanceWithTwoParents");

        // ASSERTION - check if correct number of tables were created
        assertEquals(3, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new RuntimeException("No tables found"))
                .size());

        //TODO: test fails

    }


}
