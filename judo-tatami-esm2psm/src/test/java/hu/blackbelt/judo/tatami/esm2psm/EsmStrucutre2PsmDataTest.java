package hu.blackbelt.judo.tatami.esm2psm;

import static hu.blackbelt.judo.meta.esm.expression.util.builder.ExpressionBuilders.newAttributeSelectorTypeBuilder;
import static hu.blackbelt.judo.meta.esm.expression.util.builder.ExpressionBuilders.newDataExpressionTypeBuilder;
import static hu.blackbelt.judo.meta.esm.expression.util.builder.ExpressionBuilders.newLogicalExpressionTypeBuilder;
import static hu.blackbelt.judo.meta.esm.expression.util.builder.ExpressionBuilders.newReferenceExpressionTypeBuilder;
import static hu.blackbelt.judo.meta.esm.expression.util.builder.ExpressionBuilders.newReferenceSelectorTypeBuilder;
import static hu.blackbelt.judo.meta.esm.namespace.util.builder.NamespaceBuilders.newModelBuilder;
import static hu.blackbelt.judo.meta.esm.namespace.util.builder.NamespaceBuilders.newPackageBuilder;
import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.buildEsmModel;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.newDataMemberBuilder;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.newEntityTypeBuilder;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.newGeneralizationBuilder;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.newOneWayRelationMemberBuilder;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.newTwoWayRelationMemberBuilder;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newStringTypeBuilder;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.buildPsmModel;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.SaveArguments.psmSaveArgumentsBuilder;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.calculateEsm2PsmTransformationScriptURI;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.executeEsm2PsmTransformation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.namespace.Model;
import hu.blackbelt.judo.meta.esm.namespace.Package;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.esm.structure.DataMember;
import hu.blackbelt.judo.meta.esm.structure.EntityType;
import hu.blackbelt.judo.meta.esm.structure.Generalization;
import hu.blackbelt.judo.meta.esm.structure.OneWayRelationMember;
import hu.blackbelt.judo.meta.esm.structure.TwoWayRelationMember;
import hu.blackbelt.judo.meta.esm.type.StringType;
import hu.blackbelt.judo.meta.psm.data.Attribute;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EsmStrucutre2PsmDataTest {

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
    void testCreateEntityType() throws Exception {
        testName = "CreateEntityType";
        
        EntityType entityType1 = newEntityTypeBuilder().withName("entityType1").withDefaultAccesspoint(false)
        		.withFilter(newLogicalExpressionTypeBuilder().build()).build();
        
        EntityType entityType2 = newEntityTypeBuilder().withName("entityType2").withDefaultAccesspoint(false)
        		.withFilter(newLogicalExpressionTypeBuilder().build()).build();
        Generalization generalisation2 = newGeneralizationBuilder().withTarget(entityType2).build();
        EntityType entityType3 = newEntityTypeBuilder().withName("entityType3").withDefaultAccesspoint(false)
        		.withGeneralizations(generalisation2).withFilter(newLogicalExpressionTypeBuilder().build()).build();
        Generalization generalization3 = newGeneralizationBuilder().withTarget(entityType3).build();
        EntityType entityType4 = newEntityTypeBuilder().withName("entityType4").withDefaultAccesspoint(false)
        		.withGeneralizations(generalization3).withFilter(newLogicalExpressionTypeBuilder().build()).build();
        
        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(ImmutableList.of(entityType1,entityType2,entityType3,entityType4))
                .build();

        esmModel.addContent(model);
        transform();

        final Set<hu.blackbelt.judo.meta.psm.data.EntityType> psmEntityTypes = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class).collect(Collectors.toSet());
        assertTrue(psmEntityTypes.size() == 4);
        assertTrue(psmEntityTypes.stream().allMatch(e -> !e.isAbstract()));
        
        final Set<String> psmEntityTypeNames = psmEntityTypes.stream().map(e -> e.getName()).collect(Collectors.toSet());
        final Set<String> esmEntityTypeNames = ImmutableSet.of(entityType1.getName(),entityType2.getName(),entityType3.getName(),entityType4.getName());
        assertThat(psmEntityTypeNames, IsEqual.equalTo(esmEntityTypeNames));
        
        final Optional<hu.blackbelt.judo.meta.psm.data.EntityType> psmEntityType4 = psmEntityTypes.stream().filter(e -> e.getName().equals(entityType4.getName())).findAny();
        assertTrue(psmEntityType4.isPresent());
        
        final Set<String> psmEntityType4SuperTypeNames = psmEntityType4.get().getSuperEntityTypes().stream().map(s -> s.getName()).collect(Collectors.toSet());
        final Set<String> esmEntityType4SuperTypeNames = ImmutableSet.of(entityType3.getName());
        assertThat(psmEntityType4SuperTypeNames, IsEqual.equalTo(esmEntityType4SuperTypeNames));
        
        final Optional<hu.blackbelt.judo.meta.psm.data.EntityType> psmEntityType3 = psmEntityTypes.stream().filter(e -> e.getName().equals(entityType3.getName())).findAny();
        assertTrue(psmEntityType3.isPresent());
        
        final Set<String> psmEntityType3SuperTypeNames = psmEntityType3.get().getSuperEntityTypes().stream().map(s -> s.getName()).collect(Collectors.toSet());
        final Set<String> esmEntityType3SuperTypeNames = ImmutableSet.of(entityType2.getName());
        assertThat(psmEntityType3SuperTypeNames, IsEqual.equalTo(esmEntityType3SuperTypeNames));
    }
    
    @Test
    void testCreateAttribute() throws Exception {
        testName = "CreateAttribute";
        
        StringType string = newStringTypeBuilder().withName("string").withMaxLength(256).build();
        DataMember attribute = newDataMemberBuilder().withName("attribute").withDataType(string)
        		.withGetterExpression(newDataExpressionTypeBuilder().build())
        		.withSetterExpression(newAttributeSelectorTypeBuilder().build())
        		.build();
        
        EntityType entityType = newEntityTypeBuilder().withName("entityType").withDefaultAccesspoint(false)
        		.withFilter(newLogicalExpressionTypeBuilder().build()).withAttributes(attribute).build();

        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(ImmutableList.of(entityType,string))
                .build();

        esmModel.addContent(model);
        transform();

        final hu.blackbelt.judo.meta.psm.data.EntityType psmEntityType = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class)
        		.filter(e -> e.getName().equals(entityType.getName())).findAny().get();

        assertTrue(psmEntityType.getAttributes().size() == 1);
        
        final Attribute psmAttribute = psmEntityType.getAttributes().get(0);
        assertTrue(psmAttribute.getName().equals(attribute.getName()));
        assertTrue(psmAttribute.isRequired() == attribute.isRequired());
        assertTrue(psmAttribute.isIdentifier() == attribute.isIdentifier());
        
        final hu.blackbelt.judo.meta.psm.type.StringType psmStringType = allPsm(hu.blackbelt.judo.meta.psm.type.StringType.class).findAny().get();
        assertTrue(psmAttribute.getDataType().equals(psmStringType));
        
    }
    
    @Test
    void testCreateContainment() throws Exception {
        testName = "CreateContainment";
        
        EntityType target = newEntityTypeBuilder().withName("target").withDefaultAccesspoint(false)
        		.withFilter(newLogicalExpressionTypeBuilder().build()).build();
        
        OneWayRelationMember containment = newOneWayRelationMemberBuilder().withName("containment").withContainment(true)
        		.withGetterExpression(newReferenceExpressionTypeBuilder().build())
        		.withSetterExpression(newReferenceSelectorTypeBuilder().build())
        		.withLower(1)
        		.withUpper(3)
        		.withTarget(target)
        		.build();
        
        EntityType entityType = newEntityTypeBuilder().withName("entityType").withDefaultAccesspoint(false)
        		.withFilter(newLogicalExpressionTypeBuilder().build()).withRelations(containment).build();

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

        assertTrue(psmEntityType.getRelations().size() == 1);
        
        final hu.blackbelt.judo.meta.psm.data.Relation psmContainment = psmEntityType.getRelations().get(0);
        
        assertTrue(psmContainment.getName().equals(containment.getName()));
        assertTrue(psmContainment.getCardinality().getLower() == containment.getLower());
        assertTrue(psmContainment.getCardinality().getUpper() == containment.getUpper());
        assertTrue(psmContainment.getTarget().equals(psmTarget));
        
    }
    
    @Test
    void testCreateAssociationEndWithoutPartner() throws Exception {
        testName = "CreateAssociationEndWithoutPartner";
        
        EntityType target = newEntityTypeBuilder().withName("target").withDefaultAccesspoint(false)
        		.withFilter(newLogicalExpressionTypeBuilder().build()).build();
        
        OneWayRelationMember associationEnd = newOneWayRelationMemberBuilder().withName("associationEnd").withContainment(false)
        		.withGetterExpression(newReferenceExpressionTypeBuilder().build())
        		.withSetterExpression(newReferenceSelectorTypeBuilder().build())
        		.withLower(1)
        		.withUpper(3)
        		.withTarget(target)
        		.build();
        
        EntityType entityType = newEntityTypeBuilder().withName("entityType").withDefaultAccesspoint(false)
        		.withFilter(newLogicalExpressionTypeBuilder().build()).withRelations(associationEnd).build();

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

        assertTrue(psmEntityType.getRelations().size() == 1);
        
        final hu.blackbelt.judo.meta.psm.data.Relation psmAssociationEnd = psmEntityType.getRelations().get(0);
        
        assertTrue(psmAssociationEnd.getName().equals(associationEnd.getName()));
        assertTrue(psmAssociationEnd.getCardinality().getLower() == associationEnd.getLower());
        assertTrue(psmAssociationEnd.getCardinality().getUpper() == associationEnd.getUpper());
        assertTrue(psmAssociationEnd.getTarget().equals(psmTarget));
    }
    
    @Test
    void testCreateAssociationEndWithPartner() throws Exception {
        testName = "CreateAssociationEndWithPartner";
        
        EntityType target = newEntityTypeBuilder().withName("target").withDefaultAccesspoint(false)
        		.withFilter(newLogicalExpressionTypeBuilder().build()).build();
        EntityType entityType = newEntityTypeBuilder().withName("entityType").withDefaultAccesspoint(false)
        		.withFilter(newLogicalExpressionTypeBuilder().build()).build();
        
        TwoWayRelationMember associationEnd1 = newTwoWayRelationMemberBuilder().withName("associationEnd1")
        		.withLower(1)
        		.withUpper(1)
        		.withTarget(target)
        		.build();
        
        TwoWayRelationMember associationEnd2 = newTwoWayRelationMemberBuilder().withName("associationEnd2")
        		.withLower(1)
        		.withUpper(1)
        		.withTarget(entityType)
        		.build();
        
        entityType.getRelations().add(associationEnd1);
        target.getRelations().add(associationEnd2);
        associationEnd1.setPartner(associationEnd2);
        associationEnd2.setPartner(associationEnd1);

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

        assertTrue(psmEntityType.getRelations().size() == 1);
        assertTrue(target.getRelations().size() == 1);
        
        final hu.blackbelt.judo.meta.psm.data.Relation psmAssociationEnd1 = psmEntityType.getRelations().get(0);
        assertTrue(psmAssociationEnd1.getName().equals(associationEnd1.getName()));
        assertTrue(psmAssociationEnd1.getCardinality().getLower() == associationEnd1.getLower());
        assertTrue(psmAssociationEnd1.getCardinality().getUpper() == associationEnd1.getUpper());
        assertTrue(psmAssociationEnd1.getTarget().equals(psmTarget));
        
        final hu.blackbelt.judo.meta.psm.data.Relation psmAssociationEnd2 = psmTarget.getRelations().get(0);
        assertTrue(psmAssociationEnd2.getName().equals(associationEnd2.getName()));
        assertTrue(psmAssociationEnd2.getCardinality().getLower() == associationEnd2.getLower());
        assertTrue(psmAssociationEnd2.getCardinality().getUpper() == associationEnd2.getUpper());
        assertTrue(psmAssociationEnd2.getTarget().equals(psmEntityType));
    }
    
    @Test
    void testCreatePackage() throws Exception {
        testName = "CreateModel";
        
        Package p1 = newPackageBuilder().withName("package1").build();

        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(p1)
                .build();

        esmModel.addContent(model);
        transform();

        final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmPackage = allPsm(hu.blackbelt.judo.meta.psm.namespace.Package.class)
                .findAny();
        
        assertTrue(psmPackage.isPresent());
        assertThat(psmPackage.get().getName(), IsEqual.equalTo(p1.getName()));
        assertThat(psmPackage.get().getNamespace().getName(), IsEqual.equalTo(model.getName()));
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
