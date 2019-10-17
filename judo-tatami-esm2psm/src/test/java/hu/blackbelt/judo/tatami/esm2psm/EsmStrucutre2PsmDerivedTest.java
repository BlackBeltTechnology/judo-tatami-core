package hu.blackbelt.judo.tatami.esm2psm;

import static hu.blackbelt.judo.meta.esm.expression.util.builder.ExpressionBuilders.*;
import static hu.blackbelt.judo.meta.esm.namespace.util.builder.NamespaceBuilders.newModelBuilder;
import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.buildEsmModel;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.*;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newStringTypeBuilder;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.buildPsmModel;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.SaveArguments.psmSaveArgumentsBuilder;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.calculateEsm2PsmTransformationScriptURI;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.executeEsm2PsmTransformation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.expression.ExpressionDialect;
import hu.blackbelt.judo.meta.esm.namespace.Model;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.esm.structure.DataMember;
import hu.blackbelt.judo.meta.esm.structure.EntityType;
import hu.blackbelt.judo.meta.esm.structure.OneWayRelationMember;
import hu.blackbelt.judo.meta.esm.structure.StaticData;
import hu.blackbelt.judo.meta.esm.structure.StaticNavigation;
import hu.blackbelt.judo.meta.esm.type.StringType;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EsmStrucutre2PsmDerivedTest {

    private final String TEST_SOURCE_MODEL_NAME = "urn:test.judo-meta-esm";
    private final String TEST = "test";
    private final String TARGET_TEST_CLASSES = "target/test-classes";

    Log slf4jlog;
    EsmModel esmModel;

    String testName;
    Map<EObject, List<EObject>> resolvedTrace;
    PsmModel psmModel;
    Esm2PsmTransformationTrace esm2PsmTransformationTrace;

    @BeforeEach
    void setUp() throws Exception {
        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading ESM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        esmModel = buildEsmModel()
                .uri(URI.createURI(TEST_SOURCE_MODEL_NAME))
                .name(TEST)
                .build();

        // Create empty PSM model
        psmModel = buildPsmModel()
                .name(TEST)
                .build();
    }

    @AfterEach
    void tearDown() throws Exception {
        final String traceFileName = testName + "-esm2psm.model";

        // Saving trace map
        esm2PsmTransformationTrace.save(new File(TARGET_TEST_CLASSES, traceFileName));

        // Loading trace map
        Esm2PsmTransformationTrace esm2PsmTransformationTraceLoaded = Esm2PsmTransformationTrace.fromModelsAndTrace(
                TEST, esmModel, psmModel, new File(TARGET_TEST_CLASSES, traceFileName));

        // Resolve serialized URI's as EObject map
        resolvedTrace = esm2PsmTransformationTraceLoaded.getTransformationTrace();

        // Printing trace
        for (EObject e : resolvedTrace.keySet()) {
            for (EObject t : resolvedTrace.get(e)) {
                log.trace(e.toString() + " -> " + t.toString());
            }
        }

        psmModel.savePsmModel(psmSaveArgumentsBuilder().file(new File(TARGET_TEST_CLASSES, testName + "-psm.model")));
    }

    private void transform() throws Exception {
        // Make transformation which returns the trace with the serialized URI's
        esm2PsmTransformationTrace = executeEsm2PsmTransformation(
                esmModel,
                psmModel,
                new Slf4jLog(log),
                calculateEsm2PsmTransformationScriptURI());
    }

    @Test
    void testCreateDataProperty() throws Exception {
        testName = "CreateDataProperty";

        StringType string = newStringTypeBuilder().withName("str").withMaxLength(256).build();
        
        DataMember member = newDataMemberBuilder().withName("dataProperty")
        		.withDataType(string)
        		.withGetterExpression(newDataExpressionTypeBuilder().withExpression("exp").withDialect(ExpressionDialect.XML).build())
        		.withSetterExpression(newAttributeSelectorTypeBuilder().withExpression("").withDialect(ExpressionDialect.XML).build())
        		.withDefaultExpression(newDataExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).build())
        		.withProperty(true)
        		.build();
                
        EntityType entityType = newEntityTypeBuilder().withName("entityType")
        		.withAttributes(member).build();
        
        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(ImmutableList.of(entityType,string))
                .build();

        esmModel.addContent(model);
        transform();

        final hu.blackbelt.judo.meta.psm.data.EntityType psmEntityType = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class).findAny().get();
        final List<hu.blackbelt.judo.meta.psm.derived.DataProperty> psmDataProperties = psmEntityType.getAllDataProperties();
        
        assertTrue(psmDataProperties.size() == 1);
        assertThat(psmDataProperties.get(0).getName(), IsEqual.equalTo(member.getName()));
        
        final hu.blackbelt.judo.meta.psm.type.StringType psmStringType = allPsm(hu.blackbelt.judo.meta.psm.type.StringType.class).findAny().get();
        assertTrue(psmDataProperties.get(0).getDataType().equals(psmStringType));
        
        assertThat(psmDataProperties.get(0).getGetterExpression().getExpression(), IsEqual.equalTo(member.getGetterExpression().getExpression()));
        assertNull(psmDataProperties.get(0).getSetterExpression());
        
        assertThat(psmDataProperties.get(0).getGetterExpression().getDialect(), IsEqual.equalTo(hu.blackbelt.judo.meta.psm.derived.ExpressionDialect.XML));
        assertNull(psmDataProperties.get(0).getSetterExpression());
        
    }
    
    @Test
    void testCreateStaticData() throws Exception {
        testName = "CreateStaticData";
        
        StringType string = newStringTypeBuilder().withName("str").withMaxLength(256).build();
        
        StaticData staticData = newStaticDataBuilder().withName("StaticData")
        		.withDataType(string)
        		.withGetterExpression(newDataExpressionTypeBuilder().withExpression("exp").build())
        		.withSetterExpression(newAttributeSelectorTypeBuilder().withExpression("exp").build())
        		.build();
        
        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(ImmutableList.of(string,staticData))
                .build();

        esmModel.addContent(model);
        transform();

        final Optional<hu.blackbelt.judo.meta.psm.derived.StaticData> psmStaticData = allPsm(hu.blackbelt.judo.meta.psm.derived.StaticData.class).findAny();
        
        assertTrue(psmStaticData.isPresent());
        assertThat(psmStaticData.get().getName(), IsEqual.equalTo(staticData.getName()));
        assertThat(psmStaticData.get().getGetterExpression().getExpression(), IsEqual.equalTo(staticData.getGetterExpression().getExpression()));
        assertThat(psmStaticData.get().getSetterExpression().getExpression(), IsEqual.equalTo(staticData.getSetterExpression().getExpression()));
        
        final hu.blackbelt.judo.meta.psm.type.StringType psmStringType = allPsm(hu.blackbelt.judo.meta.psm.type.StringType.class).findAny().get();
        assertTrue(psmStaticData.get().getDataType().equals(psmStringType));
    }
    
    @Test
    void testCreateNavigationProperty() throws Exception {
        testName = "CreateNavigationProperty";
        
        EntityType target = newEntityTypeBuilder().withName("target").build();
        
        OneWayRelationMember navigationProperty = newOneWayRelationMemberBuilder().withName("navigationProperty").withContainment(false)
        		.withProperty(true)
        		.withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("exp").build())
        		.withSetterExpression(newReferenceSelectorTypeBuilder().withExpression("exp").build())
        		.withDefaultExpression(newReferenceExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).build())
        		.withRangeExpression(newReferenceExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).build())
        		.withLower(1)
        		.withUpper(3)
        		.withTarget(target)
        		.build();
        
        EntityType entityType = newEntityTypeBuilder().withName("entityType")
        		.withRelations(navigationProperty).build();

        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(ImmutableList.of(entityType,target))
                .build();

        esmModel.addContent(model);
        transform();
        
        final hu.blackbelt.judo.meta.psm.data.EntityType psmEntityType = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class)
        		.filter(e -> e.getName().equals(entityType.getName())).findAny().get();
        final hu.blackbelt.judo.meta.psm.data.EntityType psmTarget = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class)
        		.filter(e -> e.getName().equals(target.getName())).findAny().get();

        assertTrue(psmEntityType.getNavigationProperties().size() == 1);
        
        final hu.blackbelt.judo.meta.psm.derived.NavigationProperty psmNavigationProperty = psmEntityType.getNavigationProperties().get(0);
        
        assertTrue(psmNavigationProperty.getName().equals(navigationProperty.getName()));
        assertTrue(psmNavigationProperty.getCardinality().getLower() == navigationProperty.getLower());
        assertTrue(psmNavigationProperty.getCardinality().getUpper() == navigationProperty.getUpper());
        assertTrue(psmNavigationProperty.getTarget().equals(psmTarget));
        assertThat(psmNavigationProperty.getGetterExpression().getExpression(), IsEqual.equalTo(navigationProperty.getGetterExpression().getExpression()));
        assertThat(psmNavigationProperty.getSetterExpression().getExpression(), IsEqual.equalTo(navigationProperty.getSetterExpression().getExpression()));
    }
    
    @Test
    void testCreateStaticNavigation() throws Exception {
        testName = "CreateStaticNavigation";
        
        EntityType target = newEntityTypeBuilder().withName("target").build();
        
        StaticNavigation staticNavigation = newStaticNavigationBuilder().withName("staticNavigation")
        		.withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("exp").build())
        		.withSetterExpression(newReferenceSelectorTypeBuilder().withExpression("").build())
        		.withTarget(target)
        		.build();
        
        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(ImmutableList.of(staticNavigation,target))
                .build();

        esmModel.addContent(model);
        transform();

        final Optional<hu.blackbelt.judo.meta.psm.derived.StaticNavigation> psmStaticNavigation = allPsm(hu.blackbelt.judo.meta.psm.derived.StaticNavigation.class).findAny();
        final hu.blackbelt.judo.meta.psm.data.EntityType psmEntityType = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class).findAny().get();
        
        assertTrue(psmStaticNavigation.isPresent());
        assertThat(psmStaticNavigation.get().getName(), IsEqual.equalTo(staticNavigation.getName()));
        assertThat(psmStaticNavigation.get().getGetterExpression().getExpression(), IsEqual.equalTo(staticNavigation.getGetterExpression().getExpression()));
        assertNull(psmStaticNavigation.get().getSetterExpression());
        assertThat(psmStaticNavigation.get().getTarget(), IsEqual.equalTo(psmEntityType));
    }

    static <T> Stream<T> asStream(Iterator<T> sourceIterator, boolean parallel) {
        Iterable<T> iterable = () -> sourceIterator;
        return StreamSupport.stream(iterable.spliterator(), parallel);
    }

    <T> Stream<T> allPsm() {
        return asStream((Iterator<T>) psmModel.getResourceSet().getAllContents(), false);
    }

    private <T> Stream<T> allPsm(final Class<T> clazz) {
        return allPsm().filter(e -> clazz.isAssignableFrom(e.getClass())).map(e -> (T) e);
    }
}
