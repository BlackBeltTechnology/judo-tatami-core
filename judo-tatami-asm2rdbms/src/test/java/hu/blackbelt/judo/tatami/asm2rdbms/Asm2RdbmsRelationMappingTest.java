package hu.blackbelt.judo.tatami.asm2rdbms;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.judo.meta.rdbms.RdbmsForeignKey;
import hu.blackbelt.judo.meta.rdbms.RdbmsIdentifierField;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.eclipse.emf.ecore.util.builder.EcoreBuilders.*;
import static org.junit.jupiter.api.Assertions.*;

public class Asm2RdbmsRelationMappingTest extends Asm2RdbmsMappingTestBase {

    @Test
    @DisplayName("Test OneWayRelation With Null To One Cardinality")
    public void testOneWayRelationWithNullToOneCardinality() throws Exception {
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
                .orElseThrow(() -> new Exception("No tables were found")).size());

        assertTrue(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_TO).isPresent());
        assertTrue(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_FROM).isPresent());

        // check if RdbmsForeignKey exists
        assertTrue(rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME_FROM, ONE_WAY_REFERENCE, false).isPresent());
    }

    @Test
    @DisplayName("Test OneWayRelation With Null To Infinite Cardinality")
    public void testOneWayRelationWithNullToInfiniteCardinality()  throws Exception {
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

        // check if tables exist
        assertEquals(3, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new Exception("No tables were found")).size());

        assertTrue(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_TO).isPresent());
        assertTrue(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_FROM).isPresent());

        // check junction table
        assertEquals(1, rdbmsUtils.getRdbmsJunctionTables().
                orElseThrow(() -> new Exception("No junction tables were found")).size());

        assertTrue(rdbmsUtils.getRdbmsJunctionTable(RDBMS_TABLE_NAME_FROM + "#" + ONE_WAY_REFERENCE + " to " + RDBMS_TABLE_NAME_TO).isPresent());


        // check if RdbmsForeignKey not exists
        assertFalse(rdbmsUtils.getRdbmsForeignKey(RDBMS_TABLE_NAME_FROM, ONE_WAY_REFERENCE, false).isPresent());
    }

}
