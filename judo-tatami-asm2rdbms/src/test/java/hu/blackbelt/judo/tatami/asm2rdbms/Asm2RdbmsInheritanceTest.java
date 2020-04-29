package hu.blackbelt.judo.tatami.asm2rdbms;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.judo.meta.rdbms.RdbmsIdentifierField;
import hu.blackbelt.judo.meta.rdbms.RdbmsTable;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.util.HashSet;
import java.util.Set;

import static java.lang.String.format;
import static org.eclipse.emf.ecore.util.builder.EcoreBuilders.newEAttributeBuilder;
import static org.eclipse.emf.ecore.util.builder.EcoreBuilders.newEClassBuilder;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class Asm2RdbmsInheritanceTest extends Asm2RdbmsMappingTestBase {
    private void assertParents(Set<String> expected, String tableName) {
        rdbmsUtils.getRdbmsTable(tableName)
                .orElseThrow(() -> new RuntimeException(tableName + " not found"))
                .getParents().forEach(o -> {
            assertTrue(expected.contains(o.getName()), o.getName() + " not found");
            expected.remove(o.getName());
        });
        if (expected.size() != 0) {
            fail(format("Parents are missing from %s: %s", tableName, expected));
        }
    }

    @Test
    @DisplayName("Test Basic Inheritance")
    public void testBasicInheritance() {
        ///////////////////
        // setup asm model
        final EcorePackage ecore = EcorePackage.eINSTANCE;

        final EClass fruit = newEClassBuilder()
                .withName("fruit")
                .withEAnnotations(newEntityEAnnotation())
                .withEStructuralFeatures(
                        newEAttributeBuilder()
                                .withName("fruit_id")
                                .withEType(ecore.getEInt())
                                .build()
                )
                .build();

        final EClass apple = newEClassBuilder()
                .withName("apple")
                .withESuperTypes(fruit)
                .withEStructuralFeatures(
                        newEAttributeBuilder()
                                .withName("apple_type")
                                .withEType(ecore.getEString())
                                .build()
                )
                .withEAnnotations(newEntityEAnnotation())
                .build();

        final EPackage ePackage = newEPackage(ImmutableList.of(fruit, apple));

        asmModel.addContent(ePackage);

        executeTransformation("testBasicInheritance");

        final String RDBMS_TABLE_FRUIT = "TestEpackage.fruit";
        final String RDBMS_TABLE_APPLE = "TestEpackage.apple";
        final String RDBMS_FIELD_FRUIT_ID = "TestEpackage.fruit#fruit_id";
        final String RDBMS_FIELD_APPLE_TYPE = "TestEpackage.apple#apple_type";

        // setup asm model and transform
        ////////////////////////
        // fill expected values

        Set<String> tables = new HashSet<>();
        Set<String> fields1 = new HashSet<>();
        Set<String> fields2 = new HashSet<>();
        Set<String> parents = new HashSet<>();

        tables.add(RDBMS_TABLE_FRUIT);
        tables.add(RDBMS_TABLE_APPLE);
        fields1.add(RDBMS_TABLE_FRUIT + "#_id");
        fields1.add(RDBMS_TABLE_FRUIT + "#_type");
        fields1.add(RDBMS_FIELD_FRUIT_ID);
        fields2.add(RDBMS_TABLE_APPLE + "#_id");
        fields2.add(RDBMS_TABLE_APPLE + "#_type");
        fields2.add(RDBMS_FIELD_APPLE_TYPE);
        parents.add(RDBMS_TABLE_FRUIT);

        // fill expected values
        ////////////////////////////////////////////////////////////
        // compare expected and actual values

        assertTables(tables);
        assertFields(fields1, RDBMS_TABLE_FRUIT);
        assertFields(fields2, RDBMS_TABLE_APPLE);
        assertParents(parents, RDBMS_TABLE_APPLE);

        // compare expected and actual values
        ////////////////////////////////////////////////////////////
        // "validate" rdbms model

        // ASSERTION - check if apple's parent is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_FRUIT).get().getPrimaryKey(),
                rdbmsUtils.getRdbmsTable(RDBMS_TABLE_APPLE).get().getParents().get(0).getPrimaryKey());
    }

    @Disabled
    @Test
    @DisplayName("Test Inheritance With Two Parents")
    public void testInheritanceWithTwoParents() {
        final EcorePackage ecore = EcorePackage.eINSTANCE;

        final EClass vegetable = newEClassBuilder()
                .withName("vegetable")
                .withEAnnotations(newEntityEAnnotation())
                .withEStructuralFeatures(
                        newEAttributeBuilder()
                                .withName("isVegetable")
                                .withEType(ecore.getEBoolean())
                                .build()
                )
                .build();

        final EClass fruit = newEClassBuilder()
                .withName("fruit")
                .withEAnnotations(newEntityEAnnotation())
                .withEStructuralFeatures(
                        newEAttributeBuilder()
                                .withName("isFruit")
                                .withEType(ecore.getEBoolean())
                                .build()
                )
                .build();

        final EClass tomato = newEClassBuilder()
                .withName("tomato")
                .withEAnnotations(newEntityEAnnotation())
                .withEStructuralFeatures(
                        newEAttributeBuilder()
                                .withName("tomato_type")
                                .withEType(ecore.getEString())
                                .build()
                )
                .withESuperTypes(ImmutableList.of(vegetable, fruit))
                .build();

        final EPackage ePackage = newEPackage(ImmutableList.of(vegetable, fruit, tomato));

        asmModel.addContent(ePackage);

        executeTransformation("testInheritanceWithTwoParents");

        final String RDBMS_TABLE_VEGETABLE = "TestEpackage.vegetable";
        final String RDBMS_TABLE_FRUIT = "TestEpackage.fruit";
        final String RDBMS_TABLE_TOMATO = "TestEpackage.tomato";

        Set<String> tables = new HashSet<>();
        Set<String> fields1 = new HashSet<>(); //vegetable
        Set<String> fields2 = new HashSet<>(); //fruit
        Set<String> fields3 = new HashSet<>(); //apple
        Set<String> parents = new HashSet<>();

        tables.add(RDBMS_TABLE_VEGETABLE);
        tables.add(RDBMS_TABLE_FRUIT);
        tables.add(RDBMS_TABLE_TOMATO);

        fields1.add(RDBMS_TABLE_VEGETABLE + "#_id");
        fields1.add(RDBMS_TABLE_VEGETABLE + "#_type");
        fields1.add(RDBMS_TABLE_VEGETABLE + "#isVegetable");

        fields2.add(RDBMS_TABLE_FRUIT + "#_id");
        fields2.add(RDBMS_TABLE_FRUIT + "#_type");
        fields2.add(RDBMS_TABLE_FRUIT + "#isFruit");

        fields3.add(RDBMS_TABLE_TOMATO + "#_id");
        fields3.add(RDBMS_TABLE_TOMATO + "#_type");
        fields3.add(RDBMS_TABLE_TOMATO + "#tomato_type");
        //fields3.add(RDBMS_TABLE_TOMATO + "#isFruit");
        //fields3.add(RDBMS_TABLE_TOMATO + "#isVegetable");

        parents.add(RDBMS_TABLE_VEGETABLE);
        parents.add(RDBMS_TABLE_FRUIT);

        assertTables(tables);
        assertFields(fields1, RDBMS_TABLE_VEGETABLE);
        assertFields(fields2, RDBMS_TABLE_FRUIT);
        assertFields(fields3, RDBMS_TABLE_TOMATO);
        assertParents(parents, RDBMS_TABLE_TOMATO);

        EList<RdbmsTable> rdbmsParents = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_TOMATO).get().getParents();
        RdbmsIdentifierField primaryKey1 = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_VEGETABLE).get().getPrimaryKey();
        RdbmsIdentifierField primaryKey2 = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_FRUIT).get().getPrimaryKey();
        assertTrue(rdbmsParents.stream().anyMatch(o -> o.getPrimaryKey().equals(primaryKey1)), "Parents primary key is not valid");
        assertTrue(rdbmsParents.stream().anyMatch(o -> o.getPrimaryKey().equals(primaryKey2)), "Parents primary key is not valid");
    }
}
