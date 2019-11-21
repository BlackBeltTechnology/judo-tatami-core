package hu.blackbelt.judo.tatami.esm2psm;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.expression.ExpressionDialect;
import hu.blackbelt.judo.meta.esm.namespace.Model;
import hu.blackbelt.judo.meta.esm.namespace.Package;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.esm.structure.*;
import hu.blackbelt.judo.meta.esm.type.StringType;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.psm.service.TransferAttribute;
import hu.blackbelt.judo.meta.psm.service.TransferObjectRelation;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.lang.Class;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static hu.blackbelt.judo.meta.esm.expression.util.builder.ExpressionBuilders.*;
import static hu.blackbelt.judo.meta.esm.namespace.util.builder.NamespaceBuilders.newModelBuilder;
import static hu.blackbelt.judo.meta.esm.namespace.util.builder.NamespaceBuilders.newPackageBuilder;
import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.buildEsmModel;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.*;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newStringTypeBuilder;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.SaveArguments.psmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.buildPsmModel;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.calculateEsm2PsmTransformationScriptURI;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.executeEsm2PsmTransformation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class EsmStructure2PsmServiceTest {

    private final String TEST_SOURCE_MODEL_NAME = "urn:test.judo-meta-esm";
    private final String TEST = "test";
    private final String TARGET_TEST_CLASSES = "target/test-classes";

    private final String DEFAULT_TRANSFEROBJECTTYPES_PACKAGENAME = "_default_transferobjecttypes";

    private final String DEFAULT_TRANSFEROBJECTTYPE_PREFIX = "_";
    private final String DEFAULT_TRANSFEROBJECTTYPE_SUFFIX = "_defaulttransferobjecttype";


    Log slf4jlog;
    private static final Logger logger = LoggerFactory.getLogger(EsmStructure2PsmServiceTest.class);
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
    void testCreateUnmappedTransferObjectWithRelationAndDataMembers() throws Exception {
        testName = "CreateUnmappedTransferObjectWithRelationAndDataMembers";

        //attributes
        StringType stringType = newStringTypeBuilder().withName("stringType").withMaxLength(255).build();
        DataMember dataMemberBasic = newDataMemberBuilder().withName("transferAttributeBasic")
                .withRequired(false).withProperty(false).withIdentifier(false)
                .withDataType(stringType)
                .withGetterExpression(newDataExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withSetterExpression(newAttributeSelectorTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withDefaultExpression(newDataExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .build();

        //relation targets
        EntityType targetEntityType = newEntityTypeBuilder().withName("targetEntityType")
                .withAbstract_(false)
                .build();
        targetEntityType.setMapping(newMappingBuilder().withTarget(targetEntityType).withFilter(newLogicalExpressionTypeBuilder().withDialect(ExpressionDialect.JQL)).build());

        TransferObjectType targetUnmappedTransferObjectType = newTransferObjectTypeBuilder().withName("targetUnmappedTransferObjectType")
                .build();

        TransferObjectType targetMappedTransferObjectType = newTransferObjectTypeBuilder().withName("targetMappedTransferObjectType")
                .withMapping(newMappingBuilder().withTarget(targetEntityType).withFilter(newLogicalExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build()).build())
                .build();

        //relations
        OneWayRelationMember oneWayRelationContainmentTargetingUnmapped = newOneWayRelationMemberBuilder().withName("transferRelationContainmentTargetingUnmapped")
                .withContainment(true).withLower(1).withUpper(1).withProperty(false)
                .withTarget(targetUnmappedTransferObjectType)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .build();

        OneWayRelationMember oneWayRelationContainmentTargetingMapped = newOneWayRelationMemberBuilder().withName("transferRelationContainmentTargetingMapped")
                .withContainment(true).withLower(1).withUpper(1).withProperty(false)
                .withTarget(targetMappedTransferObjectType)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .build();

        OneWayRelationMember oneWayRelationBasicTargetingEntity = newOneWayRelationMemberBuilder().withName("transferRelationBasicTargetingEntity")
                .withContainment(false).withLower(1).withUpper(1).withProperty(false)
                .withTarget(targetEntityType)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .build();

        //Unmapped TransferObjectType
        TransferObjectType unmappedTransferObjectType = newTransferObjectTypeBuilder().withName("unmappedTransferObjectType")
                .withRelations(ImmutableList.of(oneWayRelationBasicTargetingEntity, oneWayRelationContainmentTargetingUnmapped, oneWayRelationContainmentTargetingMapped))
                .withAttributes(ImmutableList.of(dataMemberBasic))
                .build();

        Package servicesPkg = newPackageBuilder().withName("service").withElements(ImmutableList.of(targetMappedTransferObjectType, unmappedTransferObjectType, targetUnmappedTransferObjectType)).build();
        Package entitiesPkg = newPackageBuilder().withName("entities").withElements(ImmutableList.of(targetEntityType)).build();
        Package typesPkg = newPackageBuilder().withName("types").withElements(ImmutableList.of(stringType)).build();
        Model model = newModelBuilder().withName("Model").withElements(ImmutableList.of(servicesPkg, entitiesPkg, typesPkg)).build();

        esmModel.addContent(model);

        //ESM ->
        transform();
        //-> PSM

        //Namespaces
        final Optional<hu.blackbelt.judo.meta.psm.namespace.Model> psmModel = allPsm(hu.blackbelt.judo.meta.psm.namespace.Model.class).findAny();
        assertTrue(psmModel.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmServicePackage = allPsm(hu.blackbelt.judo.meta.psm.namespace.Package.class).filter(pack -> servicesPkg.getName().equals(pack.getName())).findAny();
        assertTrue(psmServicePackage.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmEntitiesPackage = allPsm(hu.blackbelt.judo.meta.psm.namespace.Package.class).filter(pack -> entitiesPkg.getName().equals(pack.getName())).findAny();
        assertTrue(psmEntitiesPackage.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmTypesPackage = allPsm(hu.blackbelt.judo.meta.psm.namespace.Package.class).filter(pack -> typesPkg.getName().equals(pack.getName())).findAny();
        assertTrue(psmTypesPackage.isPresent());

        //targets
        final Optional<hu.blackbelt.judo.meta.psm.data.EntityType> psmTargetEntityType = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class).filter(entityType -> targetEntityType.getName().equals(entityType.getName())).findAny();
        assertTrue(psmTargetEntityType.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmDefaultTransferObjectOfTargetEntityType = allPsm(hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class).filter(mappedTOT -> (DEFAULT_TRANSFEROBJECTTYPE_PREFIX + targetEntityType.getName() + DEFAULT_TRANSFEROBJECTTYPE_SUFFIX).equals(mappedTOT.getName())).findAny();
        assertTrue(psmDefaultTransferObjectOfTargetEntityType.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmTargetMappedTransferObject = allPsm(hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class).filter(mappedTOT -> targetMappedTransferObjectType.getName().equals(mappedTOT.getName())).findAny();
        assertTrue(psmTargetMappedTransferObject.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.service.UnmappedTransferObjectType> psmTargetUnmappedTransferObject = allPsm(hu.blackbelt.judo.meta.psm.service.UnmappedTransferObjectType.class).filter(unmappedTOT -> targetUnmappedTransferObjectType.getName().equals(unmappedTOT.getName())).findAny();
        assertTrue(psmTargetUnmappedTransferObject.isPresent());

        //UnmappedTransferObject
        final Optional<hu.blackbelt.judo.meta.psm.service.UnmappedTransferObjectType> psmUnmappedTransferObject = allPsm(hu.blackbelt.judo.meta.psm.service.UnmappedTransferObjectType.class).filter(unmappedTOT -> unmappedTransferObjectType.getName().equals(unmappedTOT.getName())).findAny();
        assertTrue(psmUnmappedTransferObject.isPresent());
        assertThat(psmUnmappedTransferObject.get().getNamespace().getName(), IsEqual.equalTo(psmServicePackage.get().getName()));

        //UnmappedTransferObject / TransferAttributes
        final Optional<TransferAttribute> psmTransferAttributeBasic = allPsm(TransferAttribute.class).filter(transferAttr -> dataMemberBasic.getName().equals(transferAttr.getName())).findAny();
        assertTrue(psmTransferAttributeBasic.isPresent());
        assertTrue(psmUnmappedTransferObject.get().getAttributes().contains(psmTransferAttributeBasic.get()));

        //UnmappedTransferObject / TransferRelations
        final Optional<TransferObjectRelation> psmTransferObjectRelationEmbeddedTargetingUnmapped = allPsm(TransferObjectRelation.class).filter(transferRel -> oneWayRelationContainmentTargetingUnmapped.getName().equals(transferRel.getName())).findAny();
        assertTrue(psmTransferObjectRelationEmbeddedTargetingUnmapped.isPresent());
        assertTrue(psmTransferObjectRelationEmbeddedTargetingUnmapped.get().getTarget().equals(psmTargetUnmappedTransferObject.get()));
        assertTrue(psmTransferObjectRelationEmbeddedTargetingUnmapped.get().isEmbedded());
        assertTrue(psmUnmappedTransferObject.get().getRelations().contains(psmTransferObjectRelationEmbeddedTargetingUnmapped.get()));

        final Optional<TransferObjectRelation> psmTransferObjectRelationEmbeddedTargetingMapped = allPsm(TransferObjectRelation.class).filter(transferRel -> oneWayRelationContainmentTargetingMapped.getName().equals(transferRel.getName())).findAny();
        assertTrue(psmTransferObjectRelationEmbeddedTargetingMapped.isPresent());
        assertTrue(psmTransferObjectRelationEmbeddedTargetingMapped.get().getTarget().equals(psmTargetMappedTransferObject.get()));
        assertTrue(psmTransferObjectRelationEmbeddedTargetingMapped.get().isEmbedded());
        assertTrue(psmUnmappedTransferObject.get().getRelations().contains(psmTransferObjectRelationEmbeddedTargetingMapped.get()));

        final Optional<TransferObjectRelation> psmTransferObjectRelationBasicTargetingEntity = allPsm(TransferObjectRelation.class).filter(transferRel -> oneWayRelationBasicTargetingEntity.getName().equals(transferRel.getName())).findAny();
        assertTrue(psmTransferObjectRelationBasicTargetingEntity.isPresent());
        assertTrue(psmTransferObjectRelationBasicTargetingEntity.get().getTarget().equals(psmDefaultTransferObjectOfTargetEntityType.get()));
        assertFalse(psmTransferObjectRelationBasicTargetingEntity.get().isEmbedded());
        assertTrue(psmUnmappedTransferObject.get().getRelations().contains(psmTransferObjectRelationBasicTargetingEntity.get()));
    }

    @Test
    void testCreateDefaultTransferObjectWithRelationMembers() throws Exception {
        testName = "CreateDefaultTransferObjectWithRelationMembers";

        EntityType entityType = newEntityTypeBuilder().withName("entityType")
                .withAbstract_(false)
                .build();
        entityType.setMapping(newMappingBuilder().withTarget(entityType).withFilter(newLogicalExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL)).build());

        //relations
        EntityType targetEntityType = newEntityTypeBuilder().withName("targetEntityType")
                .withAbstract_(false)
                .build();
        targetEntityType.setMapping(newMappingBuilder().withTarget(targetEntityType).withFilter(newLogicalExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL)).build());

        //...containment
        OneWayRelationMember oneWayRelationContainment = newOneWayRelationMemberBuilder().withName("transferRelationContainment")
                .withContainment(true).withLower(1).withUpper(1).withProperty(false)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withTarget(targetEntityType)
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .build();
        //...!property
        OneWayRelationMember oneWayRelationBasic = newOneWayRelationMemberBuilder().withName("transferRelationBasic")
                .withContainment(false).withLower(1).withUpper(1).withProperty(false)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withTarget(targetEntityType)
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .build();

        //...property
        OneWayRelationMember oneWayRelationWithProperty = newOneWayRelationMemberBuilder().withName("transferRelationWithBinding")
                .withContainment(false).withLower(1).withUpper(1).withProperty(true)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("self.transferRelationWithBinding.target").withDialect(ExpressionDialect.JQL).build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withTarget(targetEntityType)
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .build();

        OneWayRelationMember oneWayRelationWithPropertyWithSetter = newOneWayRelationMemberBuilder().withName("transferRelationWithBindingWithSetter")
                .withContainment(false).withLower(1).withUpper(1).withProperty(true)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("self.transferRelationWithBindingWithSetter.target").withDialect(ExpressionDialect.JQL).build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withExpression("self.transferRelationWithBindingWithSetter.target").withDialect(ExpressionDialect.JQL).build())
                .withTarget(targetEntityType)
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .build();

        OneWayRelationMember oneWayRelationWithPropertyWithSetterWithDefault = newOneWayRelationMemberBuilder().withName("transferRelationWithBindingWithSetterWithDefault")
                .withContainment(false).withLower(1).withUpper(1).withProperty(true)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .withTarget(targetEntityType)
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .build();

        OneWayRelationMember oneWayRelationWithPropertyWithSetterWithRange = newOneWayRelationMemberBuilder().withName("transferRelationWithBindingWithSetterWithRange")
                .withContainment(false).withLower(1).withUpper(1).withProperty(true)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .withTarget(targetEntityType)
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .build();

        OneWayRelationMember oneWayRelationWithPropertyWithSetterWithDefaultAndRange = newOneWayRelationMemberBuilder().withName("transferRelationWithBindingWithSetterWithDefaultAndRange")
                .withContainment(false).withLower(1).withUpper(1).withProperty(true)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .withTarget(targetEntityType)
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .build();

        EntityType entityWithMapping = newEntityTypeBuilder().withName("entityWithMapping")
                .withRelations(ImmutableList.of(
                        oneWayRelationContainment, oneWayRelationBasic, oneWayRelationWithProperty,
                        oneWayRelationWithPropertyWithSetter, oneWayRelationWithPropertyWithSetterWithDefault, oneWayRelationWithPropertyWithSetterWithRange, oneWayRelationWithPropertyWithSetterWithDefaultAndRange))
                .build();
        entityWithMapping.setMapping(newMappingBuilder().withTarget(entityWithMapping).withFilter(newLogicalExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL)).build());

        Package servicePackage = newPackageBuilder().withName("service").withElements(ImmutableList.of(entityWithMapping)).build();
        Package entitiesPackage = newPackageBuilder().withName("entities").withElements(ImmutableList.of(entityType, targetEntityType)).build();
        Model model = newModelBuilder().withName("Model").withElements(ImmutableList.of(servicePackage, entitiesPackage)).build();

        esmModel.addContent(model);

        // ESM -->
        transform();
        // --> PSM

        //Namespaces
        final Optional<hu.blackbelt.judo.meta.psm.namespace.Model> psmModel = allPsm(hu.blackbelt.judo.meta.psm.namespace.Model.class).filter(m -> model.getName().equals(m.getName())).findAny();
        assertTrue(psmModel.isPresent());
        final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmServicePackage = allPsm(hu.blackbelt.judo.meta.psm.namespace.Package.class).filter(pkg -> servicePackage.getName().equals(pkg.getName())).findAny();
        assertTrue(psmServicePackage.isPresent());
        final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmEntitiesPackage = allPsm(hu.blackbelt.judo.meta.psm.namespace.Package.class).filter(pkg -> entitiesPackage.getName().equals(pkg.getName())).findAny();
        assertTrue(psmEntitiesPackage.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.data.EntityType> psmEntityType = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class).filter(entity -> entityType.getName().equals(entity.getName())).findAny();
        assertTrue(psmEntityType.isPresent());
        final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmDefaultTransferObject = allPsm(hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class).filter(mappedTOT -> (DEFAULT_TRANSFEROBJECTTYPE_PREFIX + entityWithMapping.getName() + DEFAULT_TRANSFEROBJECTTYPE_SUFFIX).equals(mappedTOT.getName())).findAny();
        assertTrue(psmDefaultTransferObject.isPresent());

        assertThat(psmDefaultTransferObject.get().getName(), IsEqual.equalTo(DEFAULT_TRANSFEROBJECTTYPE_PREFIX + entityWithMapping.getName() + DEFAULT_TRANSFEROBJECTTYPE_SUFFIX));
        assertThat(psmDefaultTransferObject.get().getNamespace().getName(), IsEqual.equalTo(DEFAULT_TRANSFEROBJECTTYPES_PACKAGENAME)); //here

        final Optional<hu.blackbelt.judo.meta.psm.data.EntityType> psmTargetEntityType = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class).filter(entity -> targetEntityType.getName().equals(entity.getName())).findAny();
        assertTrue(psmTargetEntityType.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmTargetMappedTransferObjectType = allPsm(hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class).filter(mappedTOT -> (DEFAULT_TRANSFEROBJECTTYPE_PREFIX + targetEntityType.getName() + DEFAULT_TRANSFEROBJECTTYPE_SUFFIX).equals(mappedTOT.getName())).findAny();
        assertTrue(psmTargetMappedTransferObjectType.isPresent());

        //relations
        //embedded
        final Optional<TransferObjectRelation> psmTransferObjectRelationEmbedded = allPsm(TransferObjectRelation.class).filter(transferObjectRel -> oneWayRelationContainment.getName().equals(transferObjectRel.getName())).findAny();
        assertTrue(psmTransferObjectRelationEmbedded.isPresent());

        assertTrue(psmDefaultTransferObject.get().getRelations().contains(psmTransferObjectRelationEmbedded.get()));
        assertThat(psmTransferObjectRelationEmbedded.get().getTarget(), IsEqual.equalTo(psmTargetMappedTransferObjectType.get()));
        assertTrue(psmTransferObjectRelationEmbedded.get().isEmbedded());
        assertTrue(psmTransferObjectRelationEmbedded.get().getCardinality().getLower() == 1 && psmTransferObjectRelationEmbedded.get().getCardinality().getUpper() == 1);

        //property
        final Optional<TransferObjectRelation> psmTransferObjectRelation = allPsm(TransferObjectRelation.class).filter(transferObjectRel -> oneWayRelationBasic.getName().equals(transferObjectRel.getName())).findAny();
        assertTrue(psmTransferObjectRelation.isPresent());

        assertTrue(psmDefaultTransferObject.get().getRelations().contains(psmTransferObjectRelation.get()));
        assertThat(psmTransferObjectRelation.get().getTarget(), IsEqual.equalTo(psmTargetMappedTransferObjectType.get()));
        assertTrue(psmTransferObjectRelation.get().getCardinality().getLower() == 1 && psmTransferObjectRelation.get().getCardinality().getUpper() == 1);

        final Optional<TransferObjectRelation> psmTransferObjectRelationWithProperty = allPsm(TransferObjectRelation.class).filter(transferObjectRel -> oneWayRelationWithProperty.getName().equals(transferObjectRel.getName())).findAny();
        assertTrue(psmTransferObjectRelationWithProperty.isPresent());

        assertTrue(psmDefaultTransferObject.get().getRelations().contains(psmTransferObjectRelationWithProperty.get()));
        assertThat(psmTransferObjectRelationWithProperty.get().getTarget(), IsEqual.equalTo(psmTargetMappedTransferObjectType.get()));
        assertTrue(psmTransferObjectRelationWithProperty.get().getCardinality().getLower() == 1 && psmTransferObjectRelationWithProperty.get().getCardinality().getUpper() == 1);

        final Optional<TransferObjectRelation> psmTransferObjectRelationWithPropertyWithSetter = allPsm(TransferObjectRelation.class).filter(transferObjectRel -> oneWayRelationWithPropertyWithSetter.getName().equals(transferObjectRel.getName())).findAny();
        assertTrue(psmTransferObjectRelationWithPropertyWithSetter.isPresent());

        assertTrue(psmDefaultTransferObject.get().getRelations().contains(psmTransferObjectRelationWithPropertyWithSetter.get()));
        assertThat(psmTransferObjectRelationWithPropertyWithSetter.get().getTarget(), IsEqual.equalTo(psmTargetMappedTransferObjectType.get()));
        assertTrue(psmTransferObjectRelationWithPropertyWithSetter.get().getCardinality().getLower() == 1 && psmTransferObjectRelationWithPropertyWithSetter.get().getCardinality().getUpper() == 1);

        final Optional<TransferObjectRelation> psmTransferObjectRelationWithPropertyWithSetterWithDefaultAndRange = allPsm(TransferObjectRelation.class).filter(transferObjectRel -> oneWayRelationWithPropertyWithSetterWithDefaultAndRange.getName().equals(transferObjectRel.getName())).findAny();
        assertTrue(psmTransferObjectRelationWithPropertyWithSetterWithDefaultAndRange.isPresent());

        assertTrue(psmDefaultTransferObject.get().getRelations().contains(psmTransferObjectRelationWithPropertyWithSetterWithDefaultAndRange.get()));
        assertThat(psmTransferObjectRelationWithPropertyWithSetterWithDefaultAndRange.get().getTarget(), IsEqual.equalTo(psmTargetMappedTransferObjectType.get()));
        assertTrue(psmTransferObjectRelationWithPropertyWithSetterWithDefaultAndRange.get().getCardinality().getLower() == 1 && psmTransferObjectRelationWithPropertyWithSetterWithDefaultAndRange.get().getCardinality().getUpper() == 1);
    }

    @Test
    void testCreateDefaultTransferObjectWithDataMembers() throws Exception {
        testName = "CreateDefaultTransferObjectWithDataMembers";

        EntityType entityType = newEntityTypeBuilder().withName("entityType")
                .withAbstract_(false)
                .build();
        entityType.setMapping(newMappingBuilder().withTarget(entityType).withFilter(newLogicalExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL)).build());

        //attributes
        StringType stringType = newStringTypeBuilder().withName("stringType").withMaxLength(255).build();

        //...!property
        DataMember dataMemberBasic = newDataMemberBuilder().withName("transferAttributeBasic")
                .withRequired(false).withProperty(false).withIdentifier(false)
                .withDataType(stringType)
                .withGetterExpression(newDataExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withSetterExpression(newAttributeSelectorTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withDefaultExpression(newDataExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .build();

        //...property
        DataMember dataMemberWithProperty = newDataMemberBuilder().withName("transferAttributeWithBinding")
                .withRequired(false).withProperty(true).withIdentifier(false)
                .withDataType(stringType)
                .withGetterExpression(newDataExpressionTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .withSetterExpression(newAttributeSelectorTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withDefaultExpression(newDataExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .build();

        DataMember dataMemberWithPropertyAndSetter = newDataMemberBuilder().withName("transferAttributeWithBindingAndSetter")
                .withRequired(false).withProperty(true).withIdentifier(false)
                .withDataType(stringType)
                .withGetterExpression(newDataExpressionTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .withSetterExpression(newAttributeSelectorTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .withDefaultExpression(newDataExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .build();

        DataMember dataMemberWithPropertyAndSetterAndDefault = newDataMemberBuilder().withName("transferAttributeWithBindingAndSetterAndDefault")
                .withRequired(false).withProperty(true).withIdentifier(false)
                .withDataType(stringType)
                .withGetterExpression(newDataExpressionTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .withSetterExpression(newAttributeSelectorTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .withDefaultExpression(newDataExpressionTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .build();

        EntityType entityWithMapping = newEntityTypeBuilder().withName("entityWithMapping")
                .withMapping(newMappingBuilder().withTarget(entityType).withFilter(newLogicalExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build()).build())
                .withAttributes(ImmutableList.of(dataMemberBasic, dataMemberWithProperty, dataMemberWithPropertyAndSetter, dataMemberWithPropertyAndSetterAndDefault))
                .build();

        Package servicePackage = newPackageBuilder().withName("service").withElements(ImmutableList.of(entityWithMapping)).build();
        Package entitiesPackage = newPackageBuilder().withName("entities").withElements(ImmutableList.of(entityType)).build();
        Package typesPackage = newPackageBuilder().withName("types").withElements(ImmutableList.of(stringType)).build();
        Model model = newModelBuilder().withName("Model").withElements(ImmutableList.of(servicePackage, entitiesPackage, typesPackage)).build();

        esmModel.addContent(model);
        // ESM -->
        transform();
        // --> PSM

        //Namespaces
        final Optional<hu.blackbelt.judo.meta.psm.namespace.Model> psmModel = allPsm(hu.blackbelt.judo.meta.psm.namespace.Model.class).filter(m -> model.getName().equals(m.getName())).findAny();
        assertTrue(psmModel.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmServicePackage = allPsm(hu.blackbelt.judo.meta.psm.namespace.Package.class).filter(pkg -> servicePackage.getName().equals(pkg.getName())).findAny();
        assertTrue(psmServicePackage.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmEntitiesPackage = allPsm(hu.blackbelt.judo.meta.psm.namespace.Package.class).filter(pkg -> entitiesPackage.getName().equals(pkg.getName())).findAny();
        assertTrue(psmEntitiesPackage.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmTypesPackage = allPsm(hu.blackbelt.judo.meta.psm.namespace.Package.class).filter(pkg -> typesPackage.getName().equals(pkg.getName())).findAny();
        assertTrue(psmTypesPackage.isPresent());
        //entityType
        final Optional<hu.blackbelt.judo.meta.psm.data.EntityType> psmEntityType = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class).filter(entity -> entityType.getName().equals(entity.getName())).findAny();
        assertTrue(psmEntityType.isPresent());
        //defaultTransferObject of entityType
        final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmDefaultTransferObject = allPsm(hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class).filter(mappedTOT -> (DEFAULT_TRANSFEROBJECTTYPE_PREFIX + entityWithMapping.getName() + DEFAULT_TRANSFEROBJECTTYPE_SUFFIX).equals(mappedTOT.getName())).findAny();
        assertTrue(psmDefaultTransferObject.isPresent());

        assertThat(psmDefaultTransferObject.get().getName(), IsEqual.equalTo(DEFAULT_TRANSFEROBJECTTYPE_PREFIX + entityWithMapping.getName() + DEFAULT_TRANSFEROBJECTTYPE_SUFFIX));
        assertThat(psmDefaultTransferObject.get().getNamespace().getName(), IsEqual.equalTo(DEFAULT_TRANSFEROBJECTTYPES_PACKAGENAME));

        //attributes
        final Optional<TransferAttribute> transferAttributeBasic = allPsm(TransferAttribute.class).filter(transferAttr -> dataMemberBasic.getName().equals(transferAttr.getName())).findAny();
        assertTrue(transferAttributeBasic.isPresent());
        assertTrue(psmDefaultTransferObject.get().getAttributes().contains(transferAttributeBasic.get()));

        //with property
        final Optional<TransferAttribute> transferAttributeAttributeWithProperty = allPsm(TransferAttribute.class).filter(transferAttr -> dataMemberWithProperty.getName().equals(transferAttr.getName())).findAny();
        assertTrue(transferAttributeAttributeWithProperty.isPresent());
        assertTrue(psmDefaultTransferObject.get().getAttributes().contains(transferAttributeAttributeWithProperty.get()));

        final Optional<TransferAttribute> transferAttributeWithPropertyAndSetter = allPsm(TransferAttribute.class).filter(transferAttr -> dataMemberWithPropertyAndSetter.getName().equals(transferAttr.getName())).findAny();
        assertTrue(transferAttributeWithPropertyAndSetter.isPresent());
        assertTrue(psmDefaultTransferObject.get().getAttributes().contains(transferAttributeWithPropertyAndSetter.get()));

        final Optional<TransferAttribute> transferAttributeWithPropertyAndSetterAndDefault = allPsm(TransferAttribute.class).filter(transferAttr -> dataMemberWithPropertyAndSetterAndDefault.getName().equals(transferAttr.getName())).findAny();
        assertTrue(transferAttributeWithPropertyAndSetterAndDefault.isPresent());
        assertTrue(psmDefaultTransferObject.get().getAttributes().contains(transferAttributeWithPropertyAndSetterAndDefault.get()));
    }

    @Test
    void testCreateMappedTransferObjectWithRelationMembers() throws Exception {
        testName = "CreateMappedTransferObjectWithRelationMembers";

        EntityType entityType = newEntityTypeBuilder().withName("entityType")
                .withAbstract_(false)
                .build();
        entityType.setMapping(newMappingBuilder().withTarget(entityType).withFilter(newLogicalExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL)).build());

        //relation targets
        EntityType targetEntityType = newEntityTypeBuilder().withName("targetEntityType")
                .withAbstract_(false)
                .build();
        targetEntityType.setMapping(newMappingBuilder().withTarget(targetEntityType).withFilter(newLogicalExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL)).build());

        TransferObjectType targetMappedTransferObjectType = newTransferObjectTypeBuilder().withName("targetMappedTransferObjectType")
                .withMapping(newMappingBuilder().withTarget(entityType).withFilter(newLogicalExpressionTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build()).build())
                .build();

        //!property
        OneWayRelationMember oneWayRelationBasic = newOneWayRelationMemberBuilder().withName("transferRelationBasic")
                .withContainment(false).withLower(1).withUpper(1).withProperty(false)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withTarget(targetEntityType)
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .build();

        OneWayRelationMember oneWayRelationBasicTargetingMapped = newOneWayRelationMemberBuilder().withName("transferRelationBasicTargetingMapped")
                .withContainment(false).withLower(1).withUpper(1).withProperty(false)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withTarget(targetMappedTransferObjectType)
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .build();

        //property
        OneWayRelationMember oneWayRelationWithProperty = newOneWayRelationMemberBuilder().withName("transferRelationWithBinding")
                .withContainment(false).withLower(1).withUpper(1).withProperty(true)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("self.oneWayRelationWithProperty.target").withDialect(ExpressionDialect.JQL).build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withTarget(targetEntityType)
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .build();

        OneWayRelationMember oneWayRelationWithPropertyWithSetter = newOneWayRelationMemberBuilder().withName("transferRelationWithBindingWithSetter")
                .withContainment(false).withLower(1).withUpper(1).withProperty(true)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("self.oneWayRelationWithPropertyWithSetter.target").withDialect(ExpressionDialect.JQL).build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withExpression("self.oneWayRelationWithPropertyWithSetter.target").withDialect(ExpressionDialect.JQL).build())
                .withTarget(targetEntityType)
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .build();

        OneWayRelationMember oneWayRelationWithPropertyWithSetterWithDefault = newOneWayRelationMemberBuilder().withName("transferRelationWithBindingWithSetterWithDefault")
                .withContainment(false).withLower(1).withUpper(1).withProperty(true)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .withTarget(targetEntityType)
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .build();

        OneWayRelationMember oneWayRelationWithPropertyWithSetterWithRange = newOneWayRelationMemberBuilder().withName("transferRelationWithBindingWithSetterWithRange")
                .withContainment(false).withLower(1).withUpper(1).withProperty(true)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .withTarget(targetEntityType)
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .build();

        OneWayRelationMember oneWayRelationWithPropertyWithSetterWithDefaultAndRange = newOneWayRelationMemberBuilder().withName("transferRelationWithBindingWithSetterWithDefaultAndRange")
                .withContainment(false).withLower(1).withUpper(1).withProperty(true)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .withTarget(targetEntityType)
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .build();

        OneWayRelationMember oneWayRelationWithPropertyWithSetterWithDefaultAndRangeTargetingMapped = newOneWayRelationMemberBuilder().withName("transferRelationWithBindingWithSetterWithDefaultAndRangeTargetingMapped")
                .withContainment(false).withLower(1).withUpper(1).withProperty(true)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .withTarget(targetMappedTransferObjectType)
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .build();

        //MappedTransferObjectTypes
        TransferObjectType mappedTransferObjectType = newTransferObjectTypeBuilder().withName("mappedTransferObjectType")
                .withMapping(newMappingBuilder().withTarget(entityType).withFilter(newLogicalExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build()).build())
                .withRelations(ImmutableList.of(
                        oneWayRelationBasicTargetingMapped, oneWayRelationWithPropertyWithSetterWithDefaultAndRangeTargetingMapped,
                        oneWayRelationBasic, oneWayRelationWithProperty,
                        oneWayRelationWithPropertyWithSetter, oneWayRelationWithPropertyWithSetterWithDefaultAndRange, oneWayRelationWithPropertyWithSetterWithDefault, oneWayRelationWithPropertyWithSetterWithRange))
                .build();
        Package servicePackage = newPackageBuilder().withName("service").withElements(ImmutableList.of(mappedTransferObjectType, targetMappedTransferObjectType)).build();
        Package entitiesPackage = newPackageBuilder().withName("entities").withElements(ImmutableList.of(entityType, targetEntityType)).build();
        Model model = newModelBuilder().withName("Model").withElements(ImmutableList.of(servicePackage, entitiesPackage)).build();

        esmModel.addContent(model);
        // ESM -->
        transform();
        // --> PSM

        //Namespaces
        final Optional<hu.blackbelt.judo.meta.psm.namespace.Model> psmModel = allPsm(hu.blackbelt.judo.meta.psm.namespace.Model.class).filter(m -> model.getName().equals(m.getName())).findAny();
        assertTrue(psmModel.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmServicePackage = allPsm(hu.blackbelt.judo.meta.psm.namespace.Package.class).filter(pkg -> servicePackage.getName().equals(pkg.getName())).findAny();
        assertTrue(psmServicePackage.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmEntitiesPackage = allPsm(hu.blackbelt.judo.meta.psm.namespace.Package.class).filter(pkg -> entitiesPackage.getName().equals(pkg.getName())).findAny();
        assertTrue(psmEntitiesPackage.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.data.EntityType> psmEntityType = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class).filter(entity -> entityType.getName().equals(entity.getName())).findAny();
        assertTrue(psmEntityType.isPresent());
        final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmDefaultTransferObject = allPsm(hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class).filter(mappedTOT -> mappedTransferObjectType.getName().equals(mappedTOT.getName())).findAny();
        assertTrue(psmDefaultTransferObject.isPresent());

        assertThat(psmDefaultTransferObject.get().getName(), IsEqual.equalTo(mappedTransferObjectType.getName()));
        assertThat(psmDefaultTransferObject.get().getNamespace().getName(), IsEqual.equalTo(servicePackage.getName()));

        //relations
        final Optional<hu.blackbelt.judo.meta.psm.data.EntityType> psmTargetEntityType = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class).filter(entity -> targetEntityType.getName().equals(entity.getName())).findAny();
        assertTrue(psmTargetEntityType.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmTargetDefaultMappedTransferObjectType = allPsm(hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class).filter(mappedTOT -> (DEFAULT_TRANSFEROBJECTTYPE_PREFIX + targetEntityType.getName() + DEFAULT_TRANSFEROBJECTTYPE_SUFFIX).equals(mappedTOT.getName())).findAny();
        assertTrue(psmTargetDefaultMappedTransferObjectType.isPresent());

        final Optional<TransferObjectRelation> psmTransferObjectRelation = allPsm(TransferObjectRelation.class).filter(transferObjectRel -> oneWayRelationBasic.getName().equals(transferObjectRel.getName())).findAny();
        assertTrue(psmTransferObjectRelation.isPresent());

        assertTrue(psmDefaultTransferObject.get().getRelations().contains(psmTransferObjectRelation.get()));
        assertThat(psmTransferObjectRelation.get().getTarget(), IsEqual.equalTo(psmTargetDefaultMappedTransferObjectType.get()));
        assertTrue(psmTransferObjectRelation.get().getCardinality().getLower() == 1 && psmTransferObjectRelation.get().getCardinality().getUpper() == 1);

        final Optional<TransferObjectRelation> psmTransferObjectRelationWithProperty = allPsm(TransferObjectRelation.class).filter(transferObjectRel -> oneWayRelationWithProperty.getName().equals(transferObjectRel.getName())).findAny();
        assertTrue(psmTransferObjectRelationWithProperty.isPresent());

        assertTrue(psmDefaultTransferObject.get().getRelations().contains(psmTransferObjectRelationWithProperty.get()));
        assertThat(psmTransferObjectRelationWithProperty.get().getTarget(), IsEqual.equalTo(psmTargetDefaultMappedTransferObjectType.get()));
        assertTrue(psmTransferObjectRelationWithProperty.get().getCardinality().getLower() == 1 && psmTransferObjectRelationWithProperty.get().getCardinality().getUpper() == 1);

        final Optional<TransferObjectRelation> psmTransferObjectRelationWithPropertyWithSetter = allPsm(TransferObjectRelation.class).filter(transferObjectRel -> oneWayRelationWithPropertyWithSetter.getName().equals(transferObjectRel.getName())).findAny();
        assertTrue(psmTransferObjectRelationWithPropertyWithSetter.isPresent());

        assertTrue(psmDefaultTransferObject.get().getRelations().contains(psmTransferObjectRelationWithPropertyWithSetter.get()));
        assertThat(psmTransferObjectRelationWithPropertyWithSetter.get().getTarget(), IsEqual.equalTo(psmTargetDefaultMappedTransferObjectType.get()));
        assertTrue(psmTransferObjectRelationWithPropertyWithSetter.get().getCardinality().getLower() == 1 && psmTransferObjectRelationWithPropertyWithSetter.get().getCardinality().getUpper() == 1);

        final Optional<TransferObjectRelation> psmTransferObjectRelationWithPropertyWithSetterWithDefaultAndRange = allPsm(TransferObjectRelation.class).filter(transferObjectRel -> oneWayRelationWithPropertyWithSetterWithDefaultAndRange.getName().equals(transferObjectRel.getName())).findAny();
        assertTrue(psmTransferObjectRelationWithPropertyWithSetterWithDefaultAndRange.isPresent());

        assertTrue(psmDefaultTransferObject.get().getRelations().contains(psmTransferObjectRelationWithPropertyWithSetterWithDefaultAndRange.get()));
        assertThat(psmTransferObjectRelationWithPropertyWithSetterWithDefaultAndRange.get().getTarget(), IsEqual.equalTo(psmTargetDefaultMappedTransferObjectType.get()));
        assertTrue(psmTransferObjectRelationWithPropertyWithSetterWithDefaultAndRange.get().getCardinality().getLower() == 1 && psmTransferObjectRelationWithPropertyWithSetterWithDefaultAndRange.get().getCardinality().getUpper() == 1);

        //target: mapped
        final Optional<TransferObjectRelation> psmTransferObjectRelationTargetingMapped = allPsm(TransferObjectRelation.class).filter(transferObjectRel -> oneWayRelationBasicTargetingMapped.getName().equals(transferObjectRel.getName())).findAny();
        assertTrue(psmTransferObjectRelationTargetingMapped.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmTargetMappedTransferObjectType = allPsm(hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class).filter(mappedTOT -> targetMappedTransferObjectType.getName().equals(mappedTOT.getName())).findAny();
        assertTrue(psmDefaultTransferObject.isPresent());

        assertTrue(psmDefaultTransferObject.get().getRelations().contains(psmTransferObjectRelationTargetingMapped.get()));
        assertThat(psmTransferObjectRelationTargetingMapped.get().getTarget(), IsEqual.equalTo(psmTargetMappedTransferObjectType.get()));
        assertTrue(psmTransferObjectRelation.get().getCardinality().getLower() == 1 && psmTransferObjectRelationTargetingMapped.get().getCardinality().getUpper() == 1);

        final Optional<TransferObjectRelation> psmTransferObjectWithPropertyWithSetterWithDefaultAndRangeTargetingMappedTargetingMapped = allPsm(TransferObjectRelation.class).filter(transferObjectRel -> oneWayRelationWithPropertyWithSetterWithDefaultAndRangeTargetingMapped.getName().equals(transferObjectRel.getName())).findAny();
        assertTrue(psmTransferObjectWithPropertyWithSetterWithDefaultAndRangeTargetingMappedTargetingMapped.isPresent());

        assertThat(psmTransferObjectWithPropertyWithSetterWithDefaultAndRangeTargetingMappedTargetingMapped.get().getTarget(), IsEqual.equalTo(psmTargetMappedTransferObjectType.get()));
        assertTrue(psmTransferObjectWithPropertyWithSetterWithDefaultAndRangeTargetingMappedTargetingMapped.get().getCardinality().getLower() == 1
                && psmTransferObjectWithPropertyWithSetterWithDefaultAndRangeTargetingMappedTargetingMapped.get().getCardinality().getUpper() == 1);
    }


    @Test
    void testCreateMappedTransferObjectWithDataMembers() throws Exception {
        testName = "CreateMappedTransferObjectWithDataMembers";

        EntityType entityType = newEntityTypeBuilder().withName("entityType")
                .withAbstract_(false)
                .build();
        entityType.setMapping(newMappingBuilder().withTarget(entityType).withFilter(newLogicalExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL)).build());

        //attributes
        StringType stringType = newStringTypeBuilder().withName("stringType").withMaxLength(255).build();
        //!property
        DataMember dataMemberBasic = newDataMemberBuilder().withName("transferAttributeBasic")
                .withRequired(false).withProperty(false).withIdentifier(false)
                .withDataType(stringType)
                .withGetterExpression(newDataExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withSetterExpression(newAttributeSelectorTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withDefaultExpression(newDataExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .build();
        //property
        DataMember dataMemberWithProperty = newDataMemberBuilder().withName("transferAttributeWithBinding")
                .withRequired(false).withProperty(true).withIdentifier(false)
                .withDataType(stringType)
                .withGetterExpression(newDataExpressionTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .withSetterExpression(newAttributeSelectorTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withDefaultExpression(newDataExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .build();

        DataMember dataMemberWithPropertyAndSetter = newDataMemberBuilder().withName("transferAttributeWithBindingAndSetter")
                .withRequired(false).withProperty(true).withIdentifier(false)
                .withDataType(stringType)
                .withGetterExpression(newDataExpressionTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .withSetterExpression(newAttributeSelectorTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .withDefaultExpression(newDataExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .build();

        DataMember dataMemberWithPropertyAndSetterAndDefault = newDataMemberBuilder().withName("transferAttributeWithBindingAndSetterAndDefault")
                .withRequired(false).withProperty(true).withIdentifier(false)
                .withDataType(stringType)
                .withGetterExpression(newDataExpressionTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .withSetterExpression(newAttributeSelectorTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .withDefaultExpression(newDataExpressionTypeBuilder().withExpression("expr").withDialect(ExpressionDialect.JQL).build())
                .build();

        TransferObjectType mappedTransferObjectType = newTransferObjectTypeBuilder().withName("mappedTransferObjectType")
                .withMapping(newMappingBuilder().withTarget(entityType).withFilter(newLogicalExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build()).build())
                .withAttributes(ImmutableList.of(dataMemberBasic, dataMemberWithProperty, dataMemberWithPropertyAndSetter, dataMemberWithPropertyAndSetterAndDefault))
                .build();

        Package servicePackage = newPackageBuilder().withName("service").withElements(ImmutableList.of(mappedTransferObjectType)).build();
        Package entitiesPackage = newPackageBuilder().withName("entities").withElements(ImmutableList.of(entityType)).build();
        Package typesPackage = newPackageBuilder().withName("types").withElements(ImmutableList.of(stringType)).build();
        Model model = newModelBuilder().withName("Model").withElements(ImmutableList.of(servicePackage, entitiesPackage, typesPackage)).build();

        esmModel.addContent(model);
        // ESM -->
        transform();
        // --> PSM

        //Model
        final Optional<hu.blackbelt.judo.meta.psm.namespace.Model> psmModel = allPsm(hu.blackbelt.judo.meta.psm.namespace.Model.class).filter(m -> model.getName().equals(m.getName())).findAny();
        assertTrue(psmModel.isPresent());
        //service package
        final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmServicePackage = allPsm(hu.blackbelt.judo.meta.psm.namespace.Package.class).filter(pkg -> servicePackage.getName().equals(pkg.getName())).findAny();
        assertTrue(psmServicePackage.isPresent());
        //entities package
        final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmEntitiesPackage = allPsm(hu.blackbelt.judo.meta.psm.namespace.Package.class).filter(pkg -> entitiesPackage.getName().equals(pkg.getName())).findAny();
        assertTrue(psmEntitiesPackage.isPresent());
        //types package
        final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmTypesPackage = allPsm(hu.blackbelt.judo.meta.psm.namespace.Package.class).filter(pkg -> typesPackage.getName().equals(pkg.getName())).findAny();
        assertTrue(psmTypesPackage.isPresent());
        //entityType
        final Optional<hu.blackbelt.judo.meta.psm.data.EntityType> psmEntityType = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class).filter(entity -> entityType.getName().equals(entity.getName())).findAny();
        assertTrue(psmEntityType.isPresent());
        //entityType
        final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmMappedTransferObject = allPsm(hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class)
                .filter(mappedTOT -> (mappedTransferObjectType.getName()).equals(mappedTOT.getName())).findAny();
        assertTrue(psmMappedTransferObject.isPresent());

        assertThat(psmMappedTransferObject.get().getName(), IsEqual.equalTo(mappedTransferObjectType.getName()));
        assertThat(psmMappedTransferObject.get().getNamespace().getName(), IsEqual.equalTo(servicePackage.getName()));

        //attributes
        final Optional<TransferAttribute> transferAttributeBasic = allPsm(TransferAttribute.class).filter(transferAttr -> dataMemberBasic.getName().equals(transferAttr.getName())).findAny();
        assertTrue(transferAttributeBasic.isPresent());
        assertTrue(psmMappedTransferObject.get().getAttributes().contains(transferAttributeBasic.get()));

        //with property
        final Optional<TransferAttribute> transferAttributeAttributeWithProperty = allPsm(TransferAttribute.class).filter(transferAttr -> dataMemberWithProperty.getName().equals(transferAttr.getName())).findAny();
        assertTrue(transferAttributeAttributeWithProperty.isPresent());
        assertTrue(psmMappedTransferObject.get().getAttributes().contains(transferAttributeAttributeWithProperty.get()));

        final Optional<TransferAttribute> transferAttributeWithPropertyAndSetter = allPsm(TransferAttribute.class).filter(transferAttr -> dataMemberWithPropertyAndSetter.getName().equals(transferAttr.getName())).findAny();
        assertTrue(transferAttributeWithPropertyAndSetter.isPresent());
        assertTrue(psmMappedTransferObject.get().getAttributes().contains(transferAttributeWithPropertyAndSetter.get()));

        final Optional<TransferAttribute> transferAttributeWithPropertyAndSetterAndDefault = allPsm(TransferAttribute.class).filter(transferAttr -> dataMemberWithPropertyAndSetterAndDefault.getName().equals(transferAttr.getName())).findAny();
        assertTrue(transferAttributeWithPropertyAndSetterAndDefault.isPresent());
        assertTrue(psmMappedTransferObject.get().getAttributes().contains(transferAttributeWithPropertyAndSetterAndDefault.get()));
    }

    @Test
    void testDefaultTransferObjectInheritance() throws Exception {
        testName = "DefaultTransferObjectInheritance";
        EntityType grandparentEntityType = newEntityTypeBuilder().withName("grandparentEntityType")
                .withAbstract_(false)
                .build();
        grandparentEntityType.setMapping(newMappingBuilder().withTarget(grandparentEntityType).withFilter(newLogicalExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL)).build());

        EntityType parentEntityType = newEntityTypeBuilder().withName("parentEntityType")
                .withAbstract_(false)
                .withGeneralizations(newGeneralizationBuilder().withTarget(grandparentEntityType).build())
                .build();
        parentEntityType.setMapping(newMappingBuilder().withTarget(parentEntityType).withFilter(newLogicalExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL)).build());

        EntityType childEntityType = newEntityTypeBuilder().withName("childEntityType")
                .withAbstract_(false)
                .withGeneralizations(newGeneralizationBuilder().withTarget(parentEntityType).build())
                .build();
        childEntityType.setMapping(newMappingBuilder().withTarget(childEntityType).withFilter(newLogicalExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL)).build());

        Package entitiesPackage = newPackageBuilder().withName("entities").withElements(ImmutableList.of(grandparentEntityType, parentEntityType, childEntityType)).build();
        Model model = newModelBuilder().withName("Model").withElements(ImmutableList.of(entitiesPackage)).build();

        esmModel.addContent(model);

        transform();

        final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmDefaultGrandparentTransferObject = allPsm(hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class).filter(mappedTOT -> (DEFAULT_TRANSFEROBJECTTYPE_PREFIX + grandparentEntityType.getName() + DEFAULT_TRANSFEROBJECTTYPE_SUFFIX).equals(mappedTOT.getName())).findAny();
        assertTrue(psmDefaultGrandparentTransferObject.isPresent());
        assertThat(psmDefaultGrandparentTransferObject.get().getName(), IsEqual.equalTo(DEFAULT_TRANSFEROBJECTTYPE_PREFIX + grandparentEntityType.getName() + DEFAULT_TRANSFEROBJECTTYPE_SUFFIX));

        final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmDefaultParentTransferObject = allPsm(hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class).filter(mappedTOT -> (DEFAULT_TRANSFEROBJECTTYPE_PREFIX + parentEntityType.getName() + DEFAULT_TRANSFEROBJECTTYPE_SUFFIX).equals(mappedTOT.getName())).findAny();
        assertTrue(psmDefaultParentTransferObject.isPresent());
        assertThat(psmDefaultParentTransferObject.get().getName(), IsEqual.equalTo(DEFAULT_TRANSFEROBJECTTYPE_PREFIX + parentEntityType.getName() + DEFAULT_TRANSFEROBJECTTYPE_SUFFIX));

        final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmDefaultChildTransferObject = allPsm(hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class).filter(mappedTOT -> (DEFAULT_TRANSFEROBJECTTYPE_PREFIX + childEntityType.getName() + DEFAULT_TRANSFEROBJECTTYPE_SUFFIX).equals(mappedTOT.getName())).findAny();
        assertTrue(psmDefaultChildTransferObject.isPresent());
        assertThat(psmDefaultChildTransferObject.get().getName(), IsEqual.equalTo(DEFAULT_TRANSFEROBJECTTYPE_PREFIX + childEntityType.getName() + DEFAULT_TRANSFEROBJECTTYPE_SUFFIX));

        assertTrue(psmDefaultParentTransferObject.get().getSuperTransferObjectTypes().contains(psmDefaultGrandparentTransferObject.get()));
        assertTrue(psmDefaultChildTransferObject.get().getSuperTransferObjectTypes().contains(psmDefaultParentTransferObject.get()));
        assertTrue(psmDefaultChildTransferObject.get().getAllSuperTransferObjectTypes().contains(psmDefaultGrandparentTransferObject.get()));
    }

    @Test
    void testTransferObjectInheritance() throws Exception {
        testName = "TransferObjectInheritance";
        EntityType entityType = newEntityTypeBuilder().withName("entityType")
                .withAbstract_(false)
                .build();
        entityType.setMapping(newMappingBuilder().withTarget(entityType).withFilter(newLogicalExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL)).build());

        StringType stringType = newStringTypeBuilder().withName("stringType").withMaxLength(255).build();

        //grandparent
        DataMember dataMemberOfGrandparent = newDataMemberBuilder().withName("inheritedAttributeFromGrandparent")
                .withRequired(false).withProperty(false).withIdentifier(false)
                .withDataType(stringType)
                .withGetterExpression(newDataExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withSetterExpression(newAttributeSelectorTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withDefaultExpression(newDataExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .build();

        TransferObjectType grandparentMappedTransferObjectType = newTransferObjectTypeBuilder().withName("grandparentMappedTransferObjectType")
                .withMapping(newMappingBuilder().withTarget(entityType).withFilter(newLogicalExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build()).build())
                .withAttributes(dataMemberOfGrandparent)
                .build();

        //parent
        DataMember dataMemberOfParent = newDataMemberBuilder().withName("inheritedAttributeFromParent")
                .withRequired(false).withProperty(false).withIdentifier(false)
                .withDataType(stringType)
                .withGetterExpression(newDataExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withSetterExpression(newAttributeSelectorTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withDefaultExpression(newDataExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .build();

        TransferObjectType parentMappedTransferObjectType = newTransferObjectTypeBuilder().withName("parentMappedTransferObjectType")
                .withMapping(newMappingBuilder().withTarget(entityType).withFilter(newLogicalExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build()).build())
                .withAttributes(dataMemberOfParent)
                .withGeneralizations(newGeneralizationBuilder().withTarget(grandparentMappedTransferObjectType).build())
                .build();

        //child
        TransferObjectType childMappedTransferObjectType = newTransferObjectTypeBuilder().withName("childMappedTransferObjectType")
                .withMapping(newMappingBuilder().withTarget(entityType).withFilter(newLogicalExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build()).build())
                .withGeneralizations(newGeneralizationBuilder().withTarget(parentMappedTransferObjectType).build())
                .build();
        TransferObjectType secondChildMappedTransferObjectType = newTransferObjectTypeBuilder().withName("secondChildMappedTransferObjectType")
                .withMapping(newMappingBuilder().withTarget(entityType).withFilter(newLogicalExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build()).build())
                .withGeneralizations(newGeneralizationBuilder().withTarget(parentMappedTransferObjectType).build())
                .build();

        Package servicePackage = newPackageBuilder().withName("service").withElements(ImmutableList.of(parentMappedTransferObjectType, grandparentMappedTransferObjectType, childMappedTransferObjectType, secondChildMappedTransferObjectType)).build();
        Package entitiesPackage = newPackageBuilder().withName("entities").withElements(ImmutableList.of(entityType)).build();
        Package typesPackage = newPackageBuilder().withName("types").withElements(ImmutableList.of(stringType)).build();
        Model model = newModelBuilder().withName("Model").withElements(ImmutableList.of(servicePackage, entitiesPackage, typesPackage)).build();

        esmModel.addContent(model);
        // ESM -->
        transform();
        // --> PSM

        //Namespaces
        final Optional<hu.blackbelt.judo.meta.psm.namespace.Model> psmModel = allPsm(hu.blackbelt.judo.meta.psm.namespace.Model.class).filter(m -> model.getName().equals(m.getName())).findAny();
        assertTrue(psmModel.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmServicePackage = allPsm(hu.blackbelt.judo.meta.psm.namespace.Package.class).filter(pkg -> servicePackage.getName().equals(pkg.getName())).findAny();
        assertTrue(psmServicePackage.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmEntitiesPackage = allPsm(hu.blackbelt.judo.meta.psm.namespace.Package.class).filter(pkg -> entitiesPackage.getName().equals(pkg.getName())).findAny();
        assertTrue(psmEntitiesPackage.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmTypesPackage = allPsm(hu.blackbelt.judo.meta.psm.namespace.Package.class).filter(pkg -> typesPackage.getName().equals(pkg.getName())).findAny();
        assertTrue(psmTypesPackage.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmGrandparentMappedTransferObjectType = allPsm(hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class).filter(mappedTOT -> grandparentMappedTransferObjectType.getName().equals(mappedTOT.getName())).findAny();
        assertTrue(psmGrandparentMappedTransferObjectType.isPresent());
        final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmParentMappedTransferObjectType = allPsm(hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class).filter(mappedTOT -> parentMappedTransferObjectType.getName().equals(mappedTOT.getName())).findAny();
        assertTrue(psmParentMappedTransferObjectType.isPresent());
        final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmChildMappedTransferObjectType = allPsm(hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class).filter(mappedTOT -> childMappedTransferObjectType.getName().equals(mappedTOT.getName())).findAny();
        assertTrue(psmChildMappedTransferObjectType.isPresent());
        final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmSecondChildMappedTransferObjectType = allPsm(hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class).filter(mappedTOT -> secondChildMappedTransferObjectType.getName().equals(mappedTOT.getName())).findAny();
        assertTrue(psmSecondChildMappedTransferObjectType.isPresent());

        assertTrue(psmParentMappedTransferObjectType.get().getInheritedTransferAttributeNames().contains("inheritedAttributeFromGrandparent"));

        assertTrue(psmChildMappedTransferObjectType.get().getInheritedTransferAttributeNames().contains("inheritedAttributeFromGrandparent"));
        assertTrue(psmChildMappedTransferObjectType.get().getInheritedTransferAttributeNames().contains("inheritedAttributeFromParent"));

        assertTrue(psmSecondChildMappedTransferObjectType.get().getInheritedTransferAttributeNames().contains("inheritedAttributeFromGrandparent"));
        assertTrue(psmSecondChildMappedTransferObjectType.get().getInheritedTransferAttributeNames().contains("inheritedAttributeFromParent"));
    }

    @Test
    void testCreateTransferRelationsFromTwoWayEndpoints() throws Exception {
        testName = "CreateTransferRelationsFromTwoWayEndpoints";

        TwoWayRelationMember twoWayRelationMemberA = newTwoWayRelationMemberBuilder().withName("twoWayRelationMemberA")
                .withLower(1).withUpper(1)
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .build();

        TwoWayRelationMember twoWayRelationMemberB = newTwoWayRelationMemberBuilder().withName("twoWayRelationMemberB")
                .withLower(1).withUpper(1)
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .build();

        twoWayRelationMemberA.setPartner(twoWayRelationMemberB);
        twoWayRelationMemberB.setPartner(twoWayRelationMemberA);

        EntityType entityTypeA = newEntityTypeBuilder().withName("EntityTypeA").withRelations(ImmutableList.of(
                twoWayRelationMemberA
        )).build();
        entityTypeA.setMapping(newMappingBuilder().withTarget(entityTypeA).withFilter(newLogicalExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL)).build());

        EntityType entityTypeB = newEntityTypeBuilder().withName("EntityTypeB").withRelations(ImmutableList.of(
                twoWayRelationMemberB
        )).build();
        entityTypeB.setMapping(newMappingBuilder().withTarget(entityTypeB).withFilter(newLogicalExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL)).build());

        twoWayRelationMemberA.setTarget(entityTypeB);
        twoWayRelationMemberB.setTarget(entityTypeA);

        Package entitiesPackage = newPackageBuilder().withName("entities").withElements(ImmutableList.of(entityTypeA, entityTypeB)).build();
        Model model = newModelBuilder().withName("Model").withElements(ImmutableList.of(entitiesPackage)).build();

        esmModel.addContent(model);
        // ESM -->
        transform();
        // --> PSM

        final Optional<hu.blackbelt.judo.meta.psm.namespace.Model> psmModel = allPsm(hu.blackbelt.judo.meta.psm.namespace.Model.class).filter(m -> model.getName().equals(m.getName())).findAny();
        assertTrue(psmModel.isPresent());
        final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmEntitiesPackage = allPsm(hu.blackbelt.judo.meta.psm.namespace.Package.class).filter(pkg -> entitiesPackage.getName().equals(pkg.getName())).findAny();
        assertTrue(psmEntitiesPackage.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.data.EntityType> psmEntityTypeA = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class).filter(entity -> entityTypeA.getName().equals(entity.getName())).findAny();
        assertTrue(psmEntityTypeA.isPresent());
        final Optional<hu.blackbelt.judo.meta.psm.data.EntityType> psmEntityTypeB = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class).filter(entity -> entityTypeB.getName().equals(entity.getName())).findAny();
        assertTrue(psmEntityTypeB.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmDefaultTransferObjectA = allPsm(hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class).filter(mappedTOT -> (DEFAULT_TRANSFEROBJECTTYPE_PREFIX + entityTypeA.getName() + DEFAULT_TRANSFEROBJECTTYPE_SUFFIX).equals(mappedTOT.getName())).findAny();
        assertTrue(psmDefaultTransferObjectA.isPresent());
        final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmDefaultTransferObjectB = allPsm(hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class).filter(mappedTOT -> (DEFAULT_TRANSFEROBJECTTYPE_PREFIX + entityTypeB.getName() + DEFAULT_TRANSFEROBJECTTYPE_SUFFIX).equals(mappedTOT.getName())).findAny();
        assertTrue(psmDefaultTransferObjectB.isPresent());

        final Optional<TransferObjectRelation> transferObjectRelationA = allPsm(TransferObjectRelation.class).filter(transferRel -> twoWayRelationMemberA.getName().equals(transferRel.getName())).findAny();
        assertTrue(transferObjectRelationA.isPresent());
        assertTrue(psmDefaultTransferObjectA.get().getRelations().contains(transferObjectRelationA.get()));

        final Optional<TransferObjectRelation> transferObjectRelationB = allPsm(TransferObjectRelation.class).filter(transferRel -> twoWayRelationMemberB.getName().equals(transferRel.getName())).findAny();
        assertTrue(transferObjectRelationB.isPresent());
        assertTrue(psmDefaultTransferObjectB.get().getRelations().contains(transferObjectRelationB.get()));
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


