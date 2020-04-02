package hu.blackbelt.judo.tatami.asm2rdbms;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.judo.meta.rdbms.RdbmsTable;
import org.eclipse.emf.common.util.EMap;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EPackage;
import org.eclipse.emf.ecore.EcorePackage;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.util.Arrays;
import java.util.Map;

import static java.util.Arrays.stream;
import static org.eclipse.emf.ecore.util.builder.EcoreBuilders.*;
import static org.junit.jupiter.api.Assertions.*;

@SuppressWarnings("OptionalGetWithoutIsPresent")
public class Asm2RdbmsRelationMappingTest extends Asm2RdbmsMappingTestBase {

    @Test
    @Disabled
    public void testOneWayRelation() throws Exception {
        final EcorePackage ecore = EcorePackage.eINSTANCE;

        // create annotation
        EAnnotation eAnnotation = newEAnnotationBuilder()
                .withSource("http://blackbelt.hu/judo/meta/ExtendedMetadata/entity")
                .build();

        eAnnotation.getDetails().put("value", "true");

        // create class with numeric type attributes
        final EClass oneWayRelationTo = newEClassBuilder()
                .withName("OneWayRelationTo")
                .withEAnnotations(eAnnotation)
                .build();


        // create annotation2
        EAnnotation eAnnotation2 = newEAnnotationBuilder()
                .withSource("http://blackbelt.hu/judo/meta/ExtendedMetadata/entity")
                .build();

        eAnnotation2.getDetails().put("value", "true");
        final EClass oneWayRelationFrom = newEClassBuilder()
                .withName("OneWayRelationFrom")
                .withEAnnotations(eAnnotation2)
                .withEStructuralFeatures(newEReferenceBuilder()
                        .withName("oneWayReference")
                        .withEType(oneWayRelationTo)
                        .build())
                .build();

        final EPackage ePackage = newEPackageBuilder()
                .withName("TestEpackage")
                .withEClassifiers(ImmutableList.of(oneWayRelationFrom, oneWayRelationTo))
                .withNsPrefix("test")
                .withNsURI("http:///com.example.test.ecore")
                .build();

        asmModel.addContent(ePackage);

        executeTransformation("testOneWayRelation");

        assertEquals(2, rdbmsUtils.getRdbmsTables()
                .orElseThrow(() -> new Exception("There are now tables created"))
                .size());

        final String RDBMS_TABLE_NAME_TO = "TestEpackage.OneWayRelationTo";
        final String RDBMS_TABLE_NAME_FROM = "TestEpackage.OneWayRelationFrom";
        assertTrue(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_TO).isPresent());
        assertTrue(rdbmsUtils.getRdbmsTable(RDBMS_TABLE_NAME_FROM).isPresent());

        assertTrue(rdbmsUtils.getRdbmsFieldWithUUID(RDBMS_TABLE_NAME_FROM, "oneWayReference").isPresent());


    }

}
