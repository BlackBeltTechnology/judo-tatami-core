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
    private final String ID_ATTRIBUTE = "#_id";
    private final String TYPE_ATTRIBUTE = "#_type";

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
                .build();

        final EClass apple = newEClassBuilder()
                .withName("apple")
                .withESuperTypes(fruit)
                .withEAnnotations(newEntityEAnnotation())
                .build();

        final EPackage ePackage = newEPackage(ImmutableList.of(fruit, apple));

        asmModel.addContent(ePackage);

        executeTransformation("testBasicInheritance");

        final String RDBMS_TABLE_FRUIT = "TestEpackage.fruit";
        final String RDBMS_TABLE_APPLE = "TestEpackage.apple";

        // setup asm model and transform
        ////////////////////////////////
        // fill expected values

        Set<String> tables = new HashSet<>();
        Set<String> fields1 = new HashSet<>();
        Set<String> fields2 = new HashSet<>();
        Set<String> parents = new HashSet<>();

        tables.add(RDBMS_TABLE_FRUIT);
        tables.add(RDBMS_TABLE_APPLE);

        fields1.add(RDBMS_TABLE_FRUIT + ID_ATTRIBUTE);
        fields1.add(RDBMS_TABLE_FRUIT + TYPE_ATTRIBUTE);

        fields2.add(RDBMS_TABLE_APPLE + ID_ATTRIBUTE);
        fields2.add(RDBMS_TABLE_APPLE + TYPE_ATTRIBUTE);

        parents.add(RDBMS_TABLE_FRUIT);

        // fill expected values
        //////////////////////////////////////
        // compare expected and actual values

        assertTables(tables);
        assertFields(fields1, RDBMS_TABLE_FRUIT);
        assertFields(fields2, RDBMS_TABLE_APPLE);
        assertParents(parents, RDBMS_TABLE_APPLE);

        // compare expected and actual values
        /////////////////////////////////////
        // "validate" rdbms model

        // ASSERTION - check if apple's parent is valid
        assertEquals(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_FRUIT).get().getPrimaryKey(),
                rdbmsUtils.getRdbmsTable(RDBMS_TABLE_APPLE).get().getParents().get(0).getPrimaryKey());
    }

    @Test
    @DisplayName("Test Inheritance With Two Parents")
    public void testInheritanceWithTwoParents() {
        ///////////////////
        // setup asm model
        final EcorePackage ecore = EcorePackage.eINSTANCE;

        final EClass vegetable = newEClassBuilder()
                .withName("vegetable")
                .withEAnnotations(newEntityEAnnotation())
                .build();

        final EClass fruit = newEClassBuilder()
                .withName("fruit")
                .withEAnnotations(newEntityEAnnotation())
                .build();

        final EClass tomato = newEClassBuilder()
                .withName("tomato")
                .withEAnnotations(newEntityEAnnotation())
                .withESuperTypes(ImmutableList.of(vegetable, fruit))
                .build();

        final EPackage ePackage = newEPackage(ImmutableList.of(vegetable, fruit, tomato));

        asmModel.addContent(ePackage);

        executeTransformation("testInheritanceWithTwoParents");

        // setup asm model and transform
        ////////////////////////////////////////////////////////////
        // prepare table names and fill sets with required elements

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

        fields1.add(RDBMS_TABLE_VEGETABLE + ID_ATTRIBUTE);
        fields1.add(RDBMS_TABLE_VEGETABLE + TYPE_ATTRIBUTE);

        fields2.add(RDBMS_TABLE_FRUIT + ID_ATTRIBUTE);
        fields2.add(RDBMS_TABLE_FRUIT + TYPE_ATTRIBUTE);

        fields3.add(RDBMS_TABLE_TOMATO + ID_ATTRIBUTE);
        fields3.add(RDBMS_TABLE_TOMATO + TYPE_ATTRIBUTE);

        parents.add(RDBMS_TABLE_VEGETABLE);
        parents.add(RDBMS_TABLE_FRUIT);

        // prepare table names and fill sets with required elements
        ///////////////////////////////////////////////////////////
        // compare expected and actual elements

        assertTables(tables);
        assertFields(fields1, RDBMS_TABLE_VEGETABLE);
        assertFields(fields2, RDBMS_TABLE_FRUIT);
        assertFields(fields3, RDBMS_TABLE_TOMATO);
        assertParents(parents, RDBMS_TABLE_TOMATO);

        // compare expected and actual elements
        ///////////////////////////////////////
        // "validate" rdbms model

        final EList<RdbmsTable> rdbmsParents = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_TOMATO).get().getParents();
        final RdbmsIdentifierField primaryKey1 = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_VEGETABLE).get().getPrimaryKey();
        final RdbmsIdentifierField primaryKey2 = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_FRUIT).get().getPrimaryKey();
        assertTrue(rdbmsParents.stream().anyMatch(o -> o.getPrimaryKey().equals(primaryKey1)),
                RDBMS_TABLE_TOMATO + "'s parent's primary key is not valid: " + primaryKey1.getName());
        assertTrue(rdbmsParents.stream().anyMatch(o -> o.getPrimaryKey().equals(primaryKey2)),
                RDBMS_TABLE_TOMATO + "'s parent's primary key is not valid: " + primaryKey2.getName());
    }

    //FIXME: only the direct parent is present
    @Test
    @DisplayName("Test Indirect Inheritance")
    public void testIndirectInheritance() {
        ///////////////////
        // setup asm model
        final EcorePackage ecore = EcorePackage.eINSTANCE;

        final EClass vehicle = newEClassBuilder()
                .withName("vehicle")
                .withEAnnotations(newEntityEAnnotation())
                .build();

        final EClass car = newEClassBuilder()
                .withName("car")
                .withEAnnotations(newEntityEAnnotation())
                .withESuperTypes(vehicle)
                .build();

        final EClass electric_car = newEClassBuilder()
                .withName("electric_car")
                .withEAnnotations(newEntityEAnnotation())
                .withESuperTypes(car)
                .build();

        final EPackage ePackage = newEPackage(ImmutableList.of(vehicle, car, electric_car));

        asmModel.addContent(ePackage);

        executeTransformation("testIndirectInheritance");

        // setup asm model and transform
        ////////////////////////////////////////////////////////////
        // prepare table names and fill sets with required elements

        final String RDBMS_TABLE_VEHICLE = "TestEpackage.vehicle";
        final String RDBMS_TABLE_CAR = "TestEpackage.car";
        final String RDBMS_TABLE_ELECTRIC_CAR = "TestEpackage.electric_car";

        Set<String> tables = new HashSet<>();
        Set<String> fields1 = new HashSet<>(); //vehicle
        Set<String> fields2 = new HashSet<>(); //car
        Set<String> fields3 = new HashSet<>(); //electric car
        Set<String> parents1 = new HashSet<>(); //car's parents
        Set<String> parents2 = new HashSet<>(); //electric car's parents

        tables.add(RDBMS_TABLE_VEHICLE);
        tables.add(RDBMS_TABLE_CAR);
        tables.add(RDBMS_TABLE_ELECTRIC_CAR);

        fields1.add(RDBMS_TABLE_VEHICLE + ID_ATTRIBUTE);
        fields1.add(RDBMS_TABLE_VEHICLE + TYPE_ATTRIBUTE);

        fields2.add(RDBMS_TABLE_CAR + ID_ATTRIBUTE);
        fields2.add(RDBMS_TABLE_CAR + TYPE_ATTRIBUTE);

        fields3.add(RDBMS_TABLE_ELECTRIC_CAR + ID_ATTRIBUTE);
        fields3.add(RDBMS_TABLE_ELECTRIC_CAR + TYPE_ATTRIBUTE);

        parents1.add(RDBMS_TABLE_VEHICLE);
//        parents2.add(RDBMS_TABLE_VEHICLE);
        parents2.add(RDBMS_TABLE_CAR);

        // prepare table names and fill sets with required elements
        ///////////////////////////////////////////////////////////
        // compare expected and actual elements

        assertTables(tables);
        assertFields(fields1, RDBMS_TABLE_VEHICLE);
        assertFields(fields2, RDBMS_TABLE_CAR);
        assertFields(fields3, RDBMS_TABLE_ELECTRIC_CAR);
        assertParents(parents1, RDBMS_TABLE_CAR);
        assertParents(parents2, RDBMS_TABLE_ELECTRIC_CAR);

        // compare expected and actual elements
        ///////////////////////////////////////
        // "validate" rdbms model

        final EList<RdbmsTable> rdbmsParents2 = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_CAR).get().getParents();
        final EList<RdbmsTable> rdbmsParents3 = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_ELECTRIC_CAR).get().getParents();

        final RdbmsIdentifierField primaryKey1 = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_VEHICLE).get().getPrimaryKey();
        final RdbmsIdentifierField primaryKey2 = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_CAR).get().getPrimaryKey();

        assertTrue(rdbmsParents2.stream().anyMatch(o -> o.getPrimaryKey().equals(primaryKey1)),
                RDBMS_TABLE_CAR + "'s parent's primary key is not valid: " + primaryKey1.getName());
//        assertTrue(rdbmsParents3.stream().anyMatch(o -> o.getPrimaryKey().equals(primaryKey1)),
//                RDBMS_TABLE_ELECTRIC_CAR + "'s parent's primary key is not valid: " + primaryKey1.getName());

        assertTrue(rdbmsParents3.stream().anyMatch(o -> o.getPrimaryKey().equals(primaryKey2)),
                RDBMS_TABLE_ELECTRIC_CAR + "'s parent's primary key is not valid: " + primaryKey1.getName());
    }

    //FIXME: only the direct parent is present
    @Test
    @DisplayName("Test Diamond Inheritance")
    public void testDiamondInheritance() {
        ///////////////////
        // setup asm model
        final EcorePackage ecore = EcorePackage.eINSTANCE;

        final EClass A = newEClassBuilder()
                .withName("A")
                .withEAnnotations(newEntityEAnnotation())
                .build();
        final EClass B = newEClassBuilder()
                .withName("B")
                .withEAnnotations(newEntityEAnnotation())
                .withESuperTypes(A)
                .build();
        final EClass BB = newEClassBuilder()
                .withName("BB")
                .withEAnnotations(newEntityEAnnotation())
                .withESuperTypes(A)
                .build();
        final EClass C = newEClassBuilder()
                .withName("C")
                .withEAnnotations(newEntityEAnnotation())
                .withESuperTypes(ImmutableList.of(B, BB))
                .build();

        final EPackage ePackage = newEPackage(ImmutableList.of(A, B, BB, C));

        asmModel.addContent(ePackage);

        executeTransformation("testDiamondInheritance");

        // setup asm model and transform
        ////////////////////////////////////////////////////////////
        // prepare table names and fill sets with required elements

        Set<String> tables = new HashSet<>();
        Set<String> fields1 = new HashSet<>(); //A
        Set<String> fields2 = new HashSet<>(); //B
        Set<String> fields22 = new HashSet<>(); //BB
        Set<String> fields3 = new HashSet<>(); //C
        Set<String> parents2 = new HashSet<>(); //B's parents
        Set<String> parents22 = new HashSet<>(); //BB's parents
        Set<String> parents3 = new HashSet<>(); //C's parents

        final String RDBMS_TABLE_A = "TestEpackage.A";
        final String RDBMS_TABLE_B = "TestEpackage.B";
        final String RDBMS_TABLE_BB = "TestEpackage.BB";
        final String RDBMS_TABLE_C = "TestEpackage.C";

        tables.add(RDBMS_TABLE_A);
        tables.add(RDBMS_TABLE_B);
        tables.add(RDBMS_TABLE_BB);
        tables.add(RDBMS_TABLE_C);

        fields1.add(RDBMS_TABLE_A + ID_ATTRIBUTE);
        fields1.add(RDBMS_TABLE_A + TYPE_ATTRIBUTE);

        fields2.add(RDBMS_TABLE_B + ID_ATTRIBUTE);
        fields2.add(RDBMS_TABLE_B + TYPE_ATTRIBUTE);
        parents2.add(RDBMS_TABLE_A);

        fields22.add(RDBMS_TABLE_BB + ID_ATTRIBUTE);
        fields22.add(RDBMS_TABLE_BB + TYPE_ATTRIBUTE);
        parents22.add(RDBMS_TABLE_A);

        fields3.add(RDBMS_TABLE_C + ID_ATTRIBUTE);
        fields3.add(RDBMS_TABLE_C + TYPE_ATTRIBUTE);
//        parents3.add(RDBMS_TABLE_A);
        parents3.add(RDBMS_TABLE_B);
        parents3.add(RDBMS_TABLE_BB);

        // prepare table names and fill sets with required elements
        ///////////////////////////////////////////////////////////
        // compare expected and actual elements

        assertTables(tables);
        assertFields(fields1, RDBMS_TABLE_A);
        assertFields(fields2, RDBMS_TABLE_B);
        assertFields(fields22, RDBMS_TABLE_BB);
        assertFields(fields3, RDBMS_TABLE_C);
        assertParents(parents2, RDBMS_TABLE_B);
        assertParents(parents22, RDBMS_TABLE_BB);
        assertParents(parents3, RDBMS_TABLE_C);

        // compare expected and actual elements
        ///////////////////////////////////////
        // "validate" rdbms model

        final EList<RdbmsTable> rdbmsParents2 = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_B).get().getParents();
        final EList<RdbmsTable> rdbmsParents22 = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_BB).get().getParents();
        final EList<RdbmsTable> rdbmsParents3 = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_C).get().getParents();

        final RdbmsIdentifierField primaryKey1 = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_A).get().getPrimaryKey();
        final RdbmsIdentifierField primaryKey2 = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_B).get().getPrimaryKey();
        final RdbmsIdentifierField primaryKey22 = rdbmsUtils.getRdbmsTable(RDBMS_TABLE_B).get().getPrimaryKey();

        assertTrue(rdbmsParents2.stream().anyMatch(o -> o.getPrimaryKey().equals(primaryKey1)),
                RDBMS_TABLE_B + "'s parent's primary key is not valid: " + primaryKey1.getName());

        assertTrue(rdbmsParents22.stream().anyMatch(o -> o.getPrimaryKey().equals(primaryKey1)),
                RDBMS_TABLE_BB + "'s parent's primary key is not valid: " + primaryKey1.getName());

//        assertTrue(rdbmsParents3.stream().anyMatch(o -> o.getPrimaryKey().equals(primaryKey1)),
//                RDBMS_TABLE_C + "'s parent's primary key is not valid: " + primaryKey1.getName());
        assertTrue(rdbmsParents3.stream().anyMatch(o -> o.getPrimaryKey().equals(primaryKey2)),
                RDBMS_TABLE_C + "'s parent's primary key is not valid: " + primaryKey2.getName());
        assertTrue(rdbmsParents3.stream().anyMatch(o -> o.getPrimaryKey().equals(primaryKey22)),
                RDBMS_TABLE_C + "'s parent's primary key is not valid: " + primaryKey22.getName());

    }


}
