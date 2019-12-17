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
import hu.blackbelt.judo.meta.psm.derived.NavigationProperty;
import hu.blackbelt.judo.meta.psm.namespace.Namespace;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

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
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

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
                .withDefaultExpression(newDataExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withProperty(true)
                .build();

        EntityType entityType = newEntityTypeBuilder().withName("entityType")
                .withAttributes(member).build();

        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(ImmutableList.of(entityType, string))
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
    void testCreateDataPropertyForTransferAttributeBinding() throws Exception {
        testName = "CreateDataPropertyForTransferAttributeBinding";

        StringType string = newStringTypeBuilder().withName("str").withMaxLength(256).build();

        DataMember member = newDataMemberBuilder().withName("member")
                .withDataType(string)
                .withProperty(true)
                .withGetterExpression(newDataExpressionTypeBuilder().withExpression("self.member").build())
                .withSetterExpression(newAttributeSelectorTypeBuilder().withExpression("self.member").build())
                .withDefaultExpression(newDataExpressionTypeBuilder().withExpression("").build())
                .build();

        EntityType target = newEntityTypeBuilder().withName("entityType")
                .build();

        TransferObjectType container = newTransferObjectTypeBuilder().withName("container")
                .withMapping(newMappingBuilder().withTarget(target).withFilter(newLogicalExpressionTypeBuilder()
                        .withExpression("").build()))
                .withAttributes(member).build();

        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(ImmutableList.of(string, container, target))
                .build();

        esmModel.addContent(model);
        transform();

        final hu.blackbelt.judo.meta.psm.data.EntityType psmTarget = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class).findAny().get();
        final List<hu.blackbelt.judo.meta.psm.derived.DataProperty> psmDataProperties = psmTarget.getAllDataProperties();

        assertThat(psmDataProperties.size(), IsEqual.equalTo(1));

        String psmName = "_" + member.getName() + "_TestModel_container";
        assertThat(psmDataProperties.get(0).getName(), IsEqual.equalTo(psmName));
    }

    @Test
    void testCreateDataPropertyForTransferAttributeDefault() throws Exception {
        testName = "CreateDataPropertyForTransferAttributeDefault";

        StringType string = newStringTypeBuilder().withName("str").withMaxLength(256).build();

        DataMember member1 = newDataMemberBuilder().withName("member1")
                .withDataType(string)
                .withProperty(false)
                .withGetterExpression(newDataExpressionTypeBuilder().withExpression("self.member").build())
                .withSetterExpression(newAttributeSelectorTypeBuilder().withExpression("").build())
                .withDefaultExpression(newDataExpressionTypeBuilder().withExpression("self.member").build())
                .build();

        DataMember member2 = newDataMemberBuilder().withName("member2")
                .withDataType(string)
                .withProperty(true)
                .withGetterExpression(newDataExpressionTypeBuilder().withExpression("self.member").build())
                .withSetterExpression(newAttributeSelectorTypeBuilder().withExpression("self.member").build())
                .withDefaultExpression(newDataExpressionTypeBuilder().withExpression("self.member").build())
                .build();

        EntityType target = newEntityTypeBuilder().withName("entityType")
                .build();

        TransferObjectType container = newTransferObjectTypeBuilder().withName("container")
                .withMapping(newMappingBuilder().withTarget(target).withFilter(newLogicalExpressionTypeBuilder()
                        .withExpression("").build()))
                .withAttributes(ImmutableList.of(member1, member2)).build();

        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(ImmutableList.of(string, container, target))
                .build();

        esmModel.addContent(model);
        transform();

        final hu.blackbelt.judo.meta.psm.data.EntityType psmTarget = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class).findAny().get();
        final List<hu.blackbelt.judo.meta.psm.derived.DataProperty> psmDataProperties = psmTarget.getAllDataProperties();
        
        assertTrue(psmDataProperties.size() == 1);
        
        String psmName1 = "_" + member1.getName() + "_default_TestModel_container";
        String psmName2 = "_" + member2.getName() + "_default_TestModel_container";
        String psmBindingName = "_" + member2.getName() + "_TestModel_container";

        final Namespace namespaceOfPsmEntityType = (Namespace) psmTarget.eContainer();
        final Optional<hu.blackbelt.judo.meta.psm.derived.StaticData> psmDataProperty1 = namespaceOfPsmEntityType.getElements().stream()
                .filter(e -> psmName1.equals(e.getName()))
                .map(e -> (hu.blackbelt.judo.meta.psm.derived.StaticData) e)
                .findAny();
        final Optional<hu.blackbelt.judo.meta.psm.derived.StaticData> psmDataProperty2 = namespaceOfPsmEntityType.getElements().stream()
                .filter(e -> psmName2.equals(e.getName()))
                .map(e -> (hu.blackbelt.judo.meta.psm.derived.StaticData) e)
                .findAny();
        final Optional<hu.blackbelt.judo.meta.psm.derived.DataProperty> psmDataPropertyBinding = psmDataProperties.stream().filter(p -> p.getName().equals(psmBindingName)).findAny();

        assertTrue(psmDataProperty1.isPresent());
        assertThat(psmDataProperty1.get().getGetterExpression().getExpression(), IsEqual.equalTo(member1.getDefaultExpression().getExpression()));
        assertNull(psmDataProperty1.get().getSetterExpression());

        assertTrue(psmDataProperty2.isPresent());
        assertThat(psmDataProperty2.get().getGetterExpression().getExpression(), IsEqual.equalTo(member2.getDefaultExpression().getExpression()));
        assertNull(psmDataProperty2.get().getSetterExpression());

        assertTrue(psmDataPropertyBinding.isPresent());
        assertThat(psmDataPropertyBinding.get().getGetterExpression().getExpression(), IsEqual.equalTo(member2.getGetterExpression().getExpression()));
        assertThat(psmDataPropertyBinding.get().getGetterExpression().getExpression(), IsEqual.equalTo(member2.getSetterExpression().getExpression()));
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
                .withElements(ImmutableList.of(string, staticData))
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
    
   /* @Test
    void testCreateStaticDataForTransferAttributeBinding() throws Exception {
        testName = "CreateStaticDataForTransferAttributeBinding";

        StringType string = newStringTypeBuilder().withName("str").withMaxLength(256).build();
        
        DataMember member = newDataMemberBuilder().withName("member")
        		.withDataType(string)
        		.withProperty(true)
        		.withGetterExpression(newDataExpressionTypeBuilder().withExpression("self.member").build())
        		.withSetterExpression(newAttributeSelectorTypeBuilder().withExpression("self.member").build())
        		.withDefaultExpression(newDataExpressionTypeBuilder().withExpression("").build())
        		.build();
        
        TransferObjectType container = newTransferObjectTypeBuilder().withName("container")
        		.withAttributes(member).build();
        
        Package pkg = newPackageBuilder().withName("pkg").withElements(ImmutableList.of(container,string)).build();
                
        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(pkg)
                .build();

        esmModel.addContent(model);
        transform();
        
        final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmPkg = allPsm(hu.blackbelt.judo.meta.psm.namespace.Package.class).findAny();
        final Optional<hu.blackbelt.judo.meta.psm.derived.StaticData> psmStaticData = allPsm(hu.blackbelt.judo.meta.psm.derived.StaticData.class).findAny();
        assertTrue(psmPkg.isPresent());
        assertTrue(psmStaticData.isPresent());
        
        final Optional<hu.blackbelt.judo.meta.psm.derived.StaticData> psmStaticDataInPsmPkg = psmPkg.get().getElements().stream()
        		.filter(e -> e instanceof hu.blackbelt.judo.meta.psm.derived.StaticData).map(e -> (hu.blackbelt.judo.meta.psm.derived.StaticData)e).findAny(); 
        
        assertThat(psmStaticDataInPsmPkg.get(), IsEqual.equalTo(psmStaticData.get()));
        
        String psmName = "_" + member.getName() + "_container";
        assertThat(psmStaticData.get().getName(), IsEqual.equalTo(psmName));
    }*/

    @Test
    void testCreateStaticDataForTransferAttributeDefault() throws Exception {
        testName = "CreateStaticDataForTransferAttributeDefault";

        StringType string = newStringTypeBuilder().withName("str").withMaxLength(256).build();

        DataMember member = newDataMemberBuilder().withName("member")
                .withDataType(string)
                .withProperty(false)
                .withGetterExpression(newDataExpressionTypeBuilder().withExpression("self.member").build())
                .withSetterExpression(newAttributeSelectorTypeBuilder().withExpression("self.member").build())
                .withDefaultExpression(newDataExpressionTypeBuilder().withExpression("self.member").build())
                .build();

        TransferObjectType container = newTransferObjectTypeBuilder().withName("container")
                .withAttributes(member).build();

        Package pkg = newPackageBuilder().withName("pkg").withElements(ImmutableList.of(container, string)).build();

        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(pkg)
                .build();

        esmModel.addContent(model);
        transform();

        final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmPkg = allPsm(hu.blackbelt.judo.meta.psm.namespace.Package.class).findAny();
        assertTrue(psmPkg.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.derived.StaticData> psmStaticData = allPsm(hu.blackbelt.judo.meta.psm.derived.StaticData.class).findAny();
        assertTrue(psmStaticData.isPresent());

        final Optional<hu.blackbelt.judo.meta.psm.derived.StaticData> psmStaticDataInPsmPkg = psmPkg.get().getElements().stream()
                .filter(e -> e instanceof hu.blackbelt.judo.meta.psm.derived.StaticData).map(e -> (hu.blackbelt.judo.meta.psm.derived.StaticData) e).findAny();
        assertThat(psmStaticDataInPsmPkg.get(), IsEqual.equalTo(psmStaticData.get()));

        String psmName = "_" + member.getName() + "_default_container";
        assertThat(psmStaticData.get().getName(), IsEqual.equalTo(psmName));

        assertThat(psmStaticData.get().getGetterExpression().getExpression(), IsEqual.equalTo(member.getDefaultExpression().getExpression()));
        assertNull(psmStaticData.get().getSetterExpression());
    }

    @Test
    void testCreateNavigationProperty() throws Exception {
        testName = "CreateNavigationProperty";

        EntityType target = newEntityTypeBuilder().withName("target").build();

        OneWayRelationMember navigationProperty = newOneWayRelationMemberBuilder().withName("navigationProperty").withContainment(false)
                .withProperty(true)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("exp").build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withExpression("exp").build())
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withExpression("").withDialect(ExpressionDialect.JQL).build())
                .withLower(1)
                .withUpper(3)
                .withTarget(target)
                .build();

        EntityType entityType = newEntityTypeBuilder().withName("entityType")
                .withRelations(navigationProperty).build();

        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(ImmutableList.of(entityType, target))
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
    void testCreateNavigationPropertyForTransferObjectRelationBinding() throws Exception {
        testName = "CreateNavigationPropertyForTransferObjectRelationBinding";

        EntityType navigationTarget = newEntityTypeBuilder().withName("target").build();

        OneWayRelationMember navigationProperty = newOneWayRelationMemberBuilder().withName("navigationProperty")
                .withProperty(true)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("exp").build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withExpression("exp").build())
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withExpression("").build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withExpression("").build())
                .withLower(1)
                .withUpper(3)
                .withTarget(navigationTarget)
                .build();

        EntityType target = newEntityTypeBuilder().withName("entityType")
                .build();

        TransferObjectType container = newTransferObjectTypeBuilder().withName("container")
                .withMapping(newMappingBuilder().withTarget(target).withFilter(newLogicalExpressionTypeBuilder().withExpression("").build()))
                .withRelations(navigationProperty).build();

        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(ImmutableList.of(navigationTarget, target, container))
                .build();

        esmModel.addContent(model);
        transform();

        final hu.blackbelt.judo.meta.psm.data.EntityType psmTarget = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class)
                .filter(e -> e.getName().equals(target.getName())).findAny().get();
        final hu.blackbelt.judo.meta.psm.data.EntityType psmNavigationTarget = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class)
                .filter(e -> e.getName().equals(navigationTarget.getName())).findAny().get();

        assertTrue(psmTarget.getNavigationProperties().size() == 1);

        final hu.blackbelt.judo.meta.psm.derived.NavigationProperty psmNavigationProperty = psmTarget.getNavigationProperties().get(0);

        String psmName = "_" + navigationProperty.getName() + "_TestModel_container";
        assertThat(psmNavigationProperty.getName(), IsEqual.equalTo(psmName));

        assertThat(psmNavigationProperty.getTarget(), IsEqual.equalTo(psmNavigationTarget));
    }

    @Test
    void testCreateNavigationPropertyForTransferObjectRelationDefault() throws Exception {
        testName = "CreateNavigationPropertyForTransferObjectRelationDefault";

        EntityType navigationTarget = newEntityTypeBuilder().withName("target").build();

        OneWayRelationMember navigationProperty1 = newOneWayRelationMemberBuilder().withName("navigationProperty1")
                .withProperty(true)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("self.navigationProperty.target").build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withExpression("self.navigationProperty.target").build())
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withExpression("self.navigationProperty.target").build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withExpression("").build())
                .withLower(1)
                .withUpper(3)
                .withTarget(navigationTarget)
                .build();

        OneWayRelationMember navigationProperty2 = newOneWayRelationMemberBuilder().withName("navigationProperty2")
                .withProperty(false)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("self.navigationProperty.target").build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withExpression("").build())
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withExpression("self.navigationProperty.target").build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withExpression("").build())
                .withLower(1)
                .withUpper(3)
                .withTarget(navigationTarget)
                .build();

        EntityType target = newEntityTypeBuilder().withName("entityType")
                .build();

        TransferObjectType container = newTransferObjectTypeBuilder().withName("container")
                .withMapping(newMappingBuilder().withTarget(target).withFilter(newLogicalExpressionTypeBuilder().withExpression("").build()))
                .withRelations(ImmutableList.of(navigationProperty1, navigationProperty2)).build();

        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(ImmutableList.of(navigationTarget, target, container))
                .build();

        esmModel.addContent(model);
        transform();

        final hu.blackbelt.judo.meta.psm.data.EntityType psmTarget = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class)
                .filter(e -> e.getName().equals(target.getName())).findAny().get();
        final hu.blackbelt.judo.meta.psm.data.EntityType psmNavigationTarget = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class)
                .filter(e -> e.getName().equals(navigationTarget.getName())).findAny().get();
        final List<hu.blackbelt.judo.meta.psm.derived.NavigationProperty> psmNavigationProperties = psmTarget.getAllNavigationProperties();

        assertTrue(psmNavigationProperties.size() == 1);

        String psmName1 = "_" + navigationProperty1.getName() + "_default_TestModel_container";
        String psmName2 = "_" + navigationProperty2.getName() + "_default_TestModel_container";
        String psmBindingName = "_" + navigationProperty1.getName() + "_TestModel_container";

        final Namespace namespaceOfPsmEntityType = (Namespace) psmTarget.eContainer();
        final Optional<hu.blackbelt.judo.meta.psm.derived.StaticNavigation> psmNavigationPropertyAsDefault1 = namespaceOfPsmEntityType.getElements().stream()
                .filter(e -> psmName1.equals(e.getName()))
                .map(e -> (hu.blackbelt.judo.meta.psm.derived.StaticNavigation) e)
                .findAny();
        final Optional<hu.blackbelt.judo.meta.psm.derived.StaticNavigation> psmNavigationPropertyAsDefault2 = namespaceOfPsmEntityType.getElements().stream()
                .filter(e -> psmName2.equals(e.getName()))
                .map(e -> (hu.blackbelt.judo.meta.psm.derived.StaticNavigation) e)
                .findAny();
        final Optional<hu.blackbelt.judo.meta.psm.derived.NavigationProperty> psmNavigationPropertyBinding = psmNavigationProperties.stream().filter(p -> p.getName().equals(psmBindingName)).findAny();

        assertTrue(psmNavigationPropertyAsDefault1.isPresent());
        assertThat(psmNavigationPropertyAsDefault1.get().getGetterExpression().getExpression(), IsEqual.equalTo(navigationProperty1.getDefaultExpression().getExpression()));
        assertNull(psmNavigationPropertyAsDefault1.get().getSetterExpression());
        assertThat(psmNavigationPropertyAsDefault1.get().getTarget(), IsEqual.equalTo(psmNavigationTarget));

        assertTrue(psmNavigationPropertyAsDefault2.isPresent());
        assertThat(psmNavigationPropertyAsDefault2.get().getGetterExpression().getExpression(), IsEqual.equalTo(navigationProperty2.getDefaultExpression().getExpression()));
        assertNull(psmNavigationPropertyAsDefault2.get().getSetterExpression());
        assertThat(psmNavigationPropertyAsDefault2.get().getTarget(), IsEqual.equalTo(psmNavigationTarget));

        assertTrue(psmNavigationPropertyBinding.isPresent());
        assertThat(psmNavigationPropertyBinding.get().getGetterExpression().getExpression(), IsEqual.equalTo(navigationProperty1.getGetterExpression().getExpression()));
        assertThat(psmNavigationPropertyBinding.get().getGetterExpression().getExpression(), IsEqual.equalTo(navigationProperty1.getSetterExpression().getExpression()));
        assertThat(psmNavigationPropertyBinding.get().getTarget(), IsEqual.equalTo(psmNavigationTarget));
    }

    @Test
    void testCreateNavigationPropertyForTransferObjectRelationRange() throws Exception {
        testName = "CreateNavigationPropertyForTransferObjectRelationRange";

        EntityType navigationTarget = newEntityTypeBuilder().withName("target").build();

        OneWayRelationMember navigationProperty1 = newOneWayRelationMemberBuilder().withName("navigationProperty1")
                .withProperty(true)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("self.navigationProperty.target").build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withExpression("self.navigationProperty.target").build())
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withExpression("").build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withExpression("self.navigationProperty.target").build())
                .withLower(1)
                .withUpper(3)
                .withTarget(navigationTarget)
                .build();

        OneWayRelationMember navigationProperty2 = newOneWayRelationMemberBuilder().withName("navigationProperty2")
                .withProperty(false)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("self.navigationProperty.target").build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withExpression("").build())
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withExpression("").build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withExpression("self.navigationProperty.target").build())
                .withLower(1)
                .withUpper(3)
                .withTarget(navigationTarget)
                .build();

        EntityType target = newEntityTypeBuilder().withName("entityType")
                .build();

        TransferObjectType container = newTransferObjectTypeBuilder().withName("container")
                .withMapping(newMappingBuilder().withTarget(target).withFilter(newLogicalExpressionTypeBuilder().withExpression("").build()))
                .withRelations(ImmutableList.of(navigationProperty1, navigationProperty2)).build();

        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(ImmutableList.of(navigationTarget, target, container))
                .build();

        esmModel.addContent(model);
        transform();

        final hu.blackbelt.judo.meta.psm.data.EntityType psmTarget = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class)
                .filter(e -> e.getName().equals(target.getName())).findAny().get();
        final List<hu.blackbelt.judo.meta.psm.derived.NavigationProperty> psmNavigationProperties = psmTarget.getAllNavigationProperties();
        final hu.blackbelt.judo.meta.psm.data.EntityType psmNavigationTarget = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class)
                .filter(e -> e.getName().equals(navigationTarget.getName())).findAny().get();

        assertTrue(psmNavigationProperties.size() == 3);

        String psmName1 = "_" + navigationProperty1.getName() + "_range_TestModel_container";
        String psmName2 = "_" + navigationProperty2.getName() + "_range_TestModel_container";
        String psmBindingName = "_" + navigationProperty1.getName() + "_TestModel_container";

        final Optional<hu.blackbelt.judo.meta.psm.derived.NavigationProperty> psmNavigationPropertyAsRange1 = psmNavigationProperties.stream().filter(p -> p.getName().equals(psmName1)).findAny();
        final Optional<hu.blackbelt.judo.meta.psm.derived.NavigationProperty> psmNavigationPropertyAsRange2 = psmNavigationProperties.stream().filter(p -> p.getName().equals(psmName2)).findAny();
        final Optional<hu.blackbelt.judo.meta.psm.derived.NavigationProperty> psmNavigationPropertyBinding = psmNavigationProperties.stream().filter(p -> p.getName().equals(psmBindingName)).findAny();

        assertTrue(psmNavigationPropertyAsRange1.isPresent());
        assertThat(psmNavigationPropertyAsRange1.get().getGetterExpression().getExpression(), IsEqual.equalTo(navigationProperty1.getRangeExpression().getExpression()));
        assertNull(psmNavigationPropertyAsRange1.get().getSetterExpression());
        assertThat(psmNavigationPropertyAsRange1.get().getTarget(), IsEqual.equalTo(psmNavigationTarget));
        assertTrue(psmNavigationPropertyAsRange1.get().getCardinality().getLower() == 0);
        assertTrue(psmNavigationPropertyAsRange1.get().getCardinality().getUpper() == -1);

        assertTrue(psmNavigationPropertyAsRange2.isPresent());
        assertThat(psmNavigationPropertyAsRange2.get().getGetterExpression().getExpression(), IsEqual.equalTo(navigationProperty2.getRangeExpression().getExpression()));
        assertNull(psmNavigationPropertyAsRange2.get().getSetterExpression());
        assertThat(psmNavigationPropertyAsRange2.get().getTarget(), IsEqual.equalTo(psmNavigationTarget));
        assertTrue(psmNavigationPropertyAsRange2.get().getCardinality().getLower() == 0);
        assertTrue(psmNavigationPropertyAsRange2.get().getCardinality().getUpper() == -1);

        assertTrue(psmNavigationPropertyBinding.isPresent());
        assertThat(psmNavigationPropertyBinding.get().getGetterExpression().getExpression(), IsEqual.equalTo(navigationProperty1.getGetterExpression().getExpression()));
        assertThat(psmNavigationPropertyBinding.get().getGetterExpression().getExpression(), IsEqual.equalTo(navigationProperty1.getSetterExpression().getExpression()));
        assertThat(psmNavigationPropertyBinding.get().getTarget(), IsEqual.equalTo(psmNavigationTarget));
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
                .withElements(ImmutableList.of(staticNavigation, target))
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
    
   /* @Test
    void testCreateStaticNavigationForTransferObjectRelationBinding() throws Exception {
        testName = "CreateStaticNavigationForTransferObjectRelationBinding";
        
        EntityType navigationTarget = newEntityTypeBuilder().withName("target").build();
        
        OneWayRelationMember navigationProperty = newOneWayRelationMemberBuilder().withName("navigationProperty")
        		.withProperty(true)
        		.withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("exp").build())
        		.withSetterExpression(newReferenceSelectorTypeBuilder().withExpression("exp").build())
        		.withDefaultExpression(newReferenceExpressionTypeBuilder().build())
        		.withRangeExpression(newReferenceExpressionTypeBuilder().build())
        		.withLower(1)
        		.withUpper(3)
        		.withTarget(navigationTarget)
        		.build();
        
        TransferObjectType container = newTransferObjectTypeBuilder().withName("container")
        		.withRelations(navigationProperty).build();

        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(ImmutableList.of(navigationTarget,container))
                .build();

        esmModel.addContent(model);
        transform();
        
        final Optional<hu.blackbelt.judo.meta.psm.derived.StaticNavigation> psmStaticNavigationAsBinding = allPsm(hu.blackbelt.judo.meta.psm.derived.StaticNavigation.class).findAny();
        final hu.blackbelt.judo.meta.psm.data.EntityType psmNavigationTarget = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class)
        		.filter(e -> e.getName().equals(navigationTarget.getName())).findAny().get();
        final hu.blackbelt.judo.meta.psm.namespace.Model psmModel = allPsm(hu.blackbelt.judo.meta.psm.namespace.Model.class).findAny().get();
        
        assertTrue(psmStaticNavigationAsBinding.isPresent());
        
        String psmName = "_" + navigationProperty.getName() + "_container";
        assertThat(psmStaticNavigationAsBinding.get().getName(), IsEqual.equalTo(psmName));
        
        assertThat(psmStaticNavigationAsBinding.get().eContainer(), IsEqual.equalTo(psmModel));
        assertThat(psmStaticNavigationAsBinding.get().getTarget(), IsEqual.equalTo(psmNavigationTarget));
    }*/

    @Test
    void testCreateStaticNavigationForTransferObjectRelationDefault() throws Exception {
        testName = "CreateStaticNavigationForTransferObjectRelationDefault";

        EntityType navigationTarget = newEntityTypeBuilder().withName("target").build();

        OneWayRelationMember navigationProperty = newOneWayRelationMemberBuilder().withName("navigationProperty")
                .withProperty(false)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("self.navigationProperty.target").build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withExpression("self.navigationProperty.target").build())
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withExpression("self.navigationProperty.target").build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withExpression("").build())
                .withLower(1)
                .withUpper(3)
                .withTarget(navigationTarget)
                .build();

        TransferObjectType container = newTransferObjectTypeBuilder().withName("container")
                .withRelations(navigationProperty).build();

        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(ImmutableList.of(navigationTarget, container))
                .build();

        esmModel.addContent(model);
        transform();

        final Optional<hu.blackbelt.judo.meta.psm.derived.StaticNavigation> psmStaticNavigationAsDefault = allPsm(hu.blackbelt.judo.meta.psm.derived.StaticNavigation.class).findAny();
        final hu.blackbelt.judo.meta.psm.data.EntityType psmNavigationTarget = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class)
                .filter(e -> e.getName().equals(navigationTarget.getName())).findAny().get();
        final hu.blackbelt.judo.meta.psm.namespace.Model psmModel = allPsm(hu.blackbelt.judo.meta.psm.namespace.Model.class).findAny().get();

        assertTrue(psmStaticNavigationAsDefault.isPresent());

        String psmName = "_" + navigationProperty.getName() + "_default_container";
        assertThat(psmStaticNavigationAsDefault.get().getName(), IsEqual.equalTo(psmName));

        assertThat(psmStaticNavigationAsDefault.get().eContainer(), IsEqual.equalTo(psmModel));
        assertThat(psmStaticNavigationAsDefault.get().getTarget(), IsEqual.equalTo(psmNavigationTarget));

        assertThat(psmStaticNavigationAsDefault.get().getGetterExpression().getExpression(), IsEqual.equalTo(navigationProperty.getDefaultExpression().getExpression()));
        assertNull(psmStaticNavigationAsDefault.get().getSetterExpression());
    }

    @Test
    void testCreateStaticNavigationForTransferObjectRelationRange() throws Exception {
        testName = "CreateStaticNavigationForTransferObjectRelationRange";

        EntityType navigationTarget = newEntityTypeBuilder().withName("target").build();

        OneWayRelationMember navigationProperty = newOneWayRelationMemberBuilder().withName("navigationProperty")
                .withProperty(false)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("self.navigationProperty.target").build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withExpression("self.navigationProperty.target").build())
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withExpression("").build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withExpression("self.navigationProperty.target").build())
                .withLower(1)
                .withUpper(3)
                .withTarget(navigationTarget)
                .build();

        TransferObjectType container = newTransferObjectTypeBuilder().withName("container")
                .withRelations(navigationProperty).build();

        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(ImmutableList.of(navigationTarget, container))
                .build();

        esmModel.addContent(model);
        transform();

        final Optional<hu.blackbelt.judo.meta.psm.derived.StaticNavigation> psmStaticNavigationAsRange = allPsm(hu.blackbelt.judo.meta.psm.derived.StaticNavigation.class).findAny();
        final hu.blackbelt.judo.meta.psm.data.EntityType psmNavigationTarget = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class)
                .filter(e -> e.getName().equals(navigationTarget.getName())).findAny().get();
        final hu.blackbelt.judo.meta.psm.namespace.Model psmModel = allPsm(hu.blackbelt.judo.meta.psm.namespace.Model.class).findAny().get();

        assertTrue(psmStaticNavigationAsRange.isPresent());

        String psmName = "_" + navigationProperty.getName() + "_range_container";
        assertThat(psmStaticNavigationAsRange.get().getName(), IsEqual.equalTo(psmName));

        assertThat(psmStaticNavigationAsRange.get().eContainer(), IsEqual.equalTo(psmModel));
        assertThat(psmStaticNavigationAsRange.get().getTarget(), IsEqual.equalTo(psmNavigationTarget));

        assertThat(psmStaticNavigationAsRange.get().getGetterExpression().getExpression(), IsEqual.equalTo(navigationProperty.getRangeExpression().getExpression()));
        assertNull(psmStaticNavigationAsRange.get().getSetterExpression());

        assertTrue(psmStaticNavigationAsRange.get().getCardinality().getLower() == 0);
        assertTrue(psmStaticNavigationAsRange.get().getCardinality().getUpper() == -1);
    }

    @Test
    void testCreateDataPropertyForEntityTypeDefaultTransferObjectTypeTransferAttributeDefaultAttribute() throws Exception {
        testName = "CreateDataPropertyForEntityTypeDefaultTransferObjectTypeTransferAttributeDefault";

        StringType string = newStringTypeBuilder().withName("string").withMaxLength(256).build();
        DataMember attribute = newDataMemberBuilder().withName("attribute").withDataType(string)
                .withGetterExpression(newDataExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("").build())
                .withSetterExpression(newAttributeSelectorTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("").build())
                .withDefaultExpression(newDataExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("defaultExpression").build())
                .build();

        EntityType entityType = newEntityTypeBuilder().withName("entityType").withAttributes(attribute).build();
        entityType.setMapping(newMappingBuilder()
                .withTarget(entityType)
                .withFilter(newLogicalExpressionTypeBuilder()
                        .withDialect(ExpressionDialect.JQL)
                        .withExpression("")
                        .build())
                .build());

        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(ImmutableList.of(entityType, string))
                .build();

        esmModel.addContent(model);
        transform();

        final hu.blackbelt.judo.meta.psm.data.EntityType psmEntityType = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class)
                .filter(e -> e.getName().equals(entityType.getName())).findAny().get();
        assertTrue(psmEntityType.getAttributes().size() == 1);

        String psmDataPropertyName = "_" + attribute.getName() + "_default_TestModel_entityType";

        final Namespace namespaceOfPsmEntityType = (Namespace) psmEntityType.eContainer();
        final Optional<hu.blackbelt.judo.meta.psm.derived.StaticData> psmDataProperty = namespaceOfPsmEntityType.getElements().stream()
                .filter(e -> psmDataPropertyName.equals(e.getName()))
                .map(e -> (hu.blackbelt.judo.meta.psm.derived.StaticData) e)
                .findAny();

        assertTrue(psmDataProperty.isPresent());
        assertTrue(psmDataProperty.get().getName().equals(psmDataPropertyName));
        assertThat(psmDataProperty.get().getGetterExpression().getExpression(), IsEqual.equalTo(attribute.getDefaultExpression().getExpression()));
        assertNull(psmDataProperty.get().getSetterExpression());
    }

    @Test
    void testCreateDataPropertyForEntityTypeDefaultTransferObjectTypeTransferAttributeDefaultDataProperty() throws Exception {
        testName = "CreateDataPropertyForEntityTypeDefaultTransferObjectTypeTransferAttributeDefault";

        StringType string = newStringTypeBuilder().withName("string").withMaxLength(256).build();
        DataMember dataProperty = newDataMemberBuilder().withName("attribute").withDataType(string).withProperty(true)
                .withGetterExpression(newDataExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("getterExpression").build())
                .withSetterExpression(newAttributeSelectorTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("").build())
                .withDefaultExpression(newDataExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("defaultExpression").build())
                .build();

        EntityType entityType = newEntityTypeBuilder().withName("entityType").withAttributes(dataProperty).build();
        entityType.setMapping(newMappingBuilder()
                .withTarget(entityType)
                .withFilter(newLogicalExpressionTypeBuilder()
                        .withDialect(ExpressionDialect.JQL)
                        .withExpression("")
                        .build())
                .build());

        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(ImmutableList.of(entityType, string))
                .build();

        esmModel.addContent(model);
        transform();

        final hu.blackbelt.judo.meta.psm.data.EntityType psmEntityType = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class)
                .filter(e -> e.getName().equals(entityType.getName())).findAny().get();
        assertTrue(psmEntityType.getDataProperties().size() == 1);

        String psmDataPropertyName = "_" + dataProperty.getName() + "_default_TestModel_entityType";

        final Namespace namespaceOfPsmEntityType = (Namespace) psmEntityType.eContainer();
        final Optional<hu.blackbelt.judo.meta.psm.derived.StaticData> psmDataProperty = namespaceOfPsmEntityType.getElements().stream()
                .filter(e -> psmDataPropertyName.equals(e.getName()))
                .map(e -> (hu.blackbelt.judo.meta.psm.derived.StaticData) e)
                .findAny();

        assertTrue(psmDataProperty.isPresent());
        assertThat(psmDataProperty.get().getGetterExpression().getExpression(), IsEqual.equalTo(dataProperty.getDefaultExpression().getExpression()));
        assertNull(psmDataProperty.get().getSetterExpression());
    }

    @Test
    void testCreateNavigationPropertyFromOneWayRelationMemberForEntityTypeDefaultTransferObjectTypeTransferObjectRelationDefaultContainment() throws Exception {
        testName = "CreateNavigationPropertyFromOneWayRelationMemberForEntityTypeDefaultTransferObjectTypeTransferObjectRelationDefault";

        EntityType target = newEntityTypeBuilder().withName("target").build();
        target.setMapping(newMappingBuilder()
                .withTarget(target)
                .withFilter(newLogicalExpressionTypeBuilder()
                        .withDialect(ExpressionDialect.JQL)
                        .withExpression("")
                        .build())
                .build());

        OneWayRelationMember containment = newOneWayRelationMemberBuilder().withName("containment").withContainment(true)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("").build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("").build())
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("defaultExpression").build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("").build())
                .withLower(1)
                .withUpper(3)
                .withTarget(target)
                .build();

        EntityType entityType = newEntityTypeBuilder().withName("entityType").withRelations(containment).build();
        entityType.setMapping(newMappingBuilder()
                .withTarget(entityType)
                .withFilter(newLogicalExpressionTypeBuilder()
                        .withDialect(ExpressionDialect.JQL)
                        .withExpression("")
                        .build())
                .build());

        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(ImmutableList.of(entityType, target))
                .build();

        esmModel.addContent(model);
        transform();

        final hu.blackbelt.judo.meta.psm.data.EntityType psmEntityType = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class)
                .filter(e -> e.getName().equals(entityType.getName())).findAny().get();

        assertTrue(psmEntityType.getRelations().size() == 1);

        String psmNavigationPropertyName = "_" + containment.getName() + "_default_TestModel_entityType";

        final Namespace namespaceOfPsmEntityType = (Namespace) psmEntityType.eContainer();
        final Optional<hu.blackbelt.judo.meta.psm.derived.StaticNavigation> psmNavigationProperty = namespaceOfPsmEntityType.getElements().stream()
                .filter(e -> psmNavigationPropertyName.equals(e.getName()))
                .map(e -> (hu.blackbelt.judo.meta.psm.derived.StaticNavigation) e)
                .findAny();

        assertTrue(psmNavigationProperty.isPresent());
        assertTrue(psmNavigationProperty.get().getName().equals(psmNavigationPropertyName));
        assertThat(psmNavigationProperty.get().getGetterExpression().getExpression(), IsEqual.equalTo(containment.getDefaultExpression().getExpression()));
        assertNull(psmNavigationProperty.get().getSetterExpression());
    }

    @Test
    void testCreateNavigationPropertyFromOneWayRelationMemberForEntityTypeDefaultTransferObjectTypeTransferObjectRelationDefaultAssociationEndWithoutPartner() throws Exception {
        testName = "CreateNavigationPropertyFromOneWayRelationMemberForEntityTypeDefaultTransferObjectTypeTransferObjectRelationDefault";

        EntityType target = newEntityTypeBuilder().withName("target").build();
        target.setMapping(newMappingBuilder()
                .withTarget(target)
                .withFilter(newLogicalExpressionTypeBuilder()
                        .withDialect(ExpressionDialect.JQL)
                        .withExpression("")
                        .build())
                .build());

        OneWayRelationMember associationEnd = newOneWayRelationMemberBuilder().withName("associationEnd").withContainment(false)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("").build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("").build())
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("defaultExpression").build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("").build())
                .withReverseCascadeDelete(true)
                .withLower(1)
                .withUpper(3)
                .withTarget(target)
                .build();

        EntityType entityType = newEntityTypeBuilder().withName("entityType").withRelations(associationEnd).build();
        entityType.setMapping(newMappingBuilder()
                .withTarget(entityType)
                .withFilter(newLogicalExpressionTypeBuilder()
                        .withDialect(ExpressionDialect.JQL)
                        .withExpression("")
                        .build())
                .build());

        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(ImmutableList.of(entityType, target))
                .build();

        esmModel.addContent(model);
        transform();

        final hu.blackbelt.judo.meta.psm.data.EntityType psmEntityType = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class)
                .filter(e -> e.getName().equals(entityType.getName())).findAny().get();

        assertTrue(psmEntityType.getRelations().size() == 1);

        String psmNavigationPropertyName = "_" + associationEnd.getName() + "_default_TestModel_entityType";

        final Namespace namespaceOfPsmEntityType = (Namespace) psmEntityType.eContainer();
        final Optional<hu.blackbelt.judo.meta.psm.derived.StaticNavigation> psmNavigationProperty = namespaceOfPsmEntityType.getElements().stream()
                .filter(e -> psmNavigationPropertyName.equals(e.getName()))
                .map(e -> (hu.blackbelt.judo.meta.psm.derived.StaticNavigation) e)
                .findAny();

        assertTrue(psmNavigationProperty.isPresent());
        assertTrue(psmNavigationProperty.get().getName().equals(psmNavigationPropertyName));
        assertThat(psmNavigationProperty.get().getGetterExpression().getExpression(), IsEqual.equalTo(associationEnd.getDefaultExpression().getExpression()));
        assertNull(psmNavigationProperty.get().getSetterExpression());
    }

    @Test
    void testCreateNavigationPropertyFromOneWayRelationMemberForEntityTypeDefaultTransferObjectTypeTransferObjectRelationDefaultNavigationProperty() throws Exception {
        testName = "CreateNavigationPropertyFromOneWayRelationMemberForEntityTypeDefaultTransferObjectTypeTransferObjectRelationDefault";

        EntityType target = newEntityTypeBuilder().withName("target").build();
        target.setMapping(newMappingBuilder()
                .withTarget(target)
                .withFilter(newLogicalExpressionTypeBuilder()
                        .withDialect(ExpressionDialect.JQL)
                        .withExpression("")
                        .build())
                .build());

        OneWayRelationMember associationEnd = newOneWayRelationMemberBuilder().withName("associationEnd").withContainment(false).withProperty(true)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("getterExpression").build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("").build())
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("defaultExpression").build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("").build())
                .withLower(1)
                .withUpper(3)
                .withTarget(target)
                .build();

        EntityType entityType = newEntityTypeBuilder().withName("entityType").withRelations(associationEnd).build();
        entityType.setMapping(newMappingBuilder()
                .withTarget(entityType)
                .withFilter(newLogicalExpressionTypeBuilder()
                        .withDialect(ExpressionDialect.JQL)
                        .withExpression("")
                        .build())
                .build());

        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(ImmutableList.of(entityType, target))
                .build();

        esmModel.addContent(model);
        transform();

        final hu.blackbelt.judo.meta.psm.data.EntityType psmEntityType = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class)
                .filter(e -> e.getName().equals(entityType.getName())).findAny().get();

        assertTrue(psmEntityType.getNavigationProperties().size() == 1);

        String psmNavigationPropertyName = "_" + associationEnd.getName() + "_default_TestModel_entityType";

        final Namespace namespaceOfPsmEntityType = (Namespace) psmEntityType.eContainer();
        final Optional<hu.blackbelt.judo.meta.psm.derived.StaticNavigation> psmNavigationProperty = namespaceOfPsmEntityType.getElements().stream()
                .filter(e -> psmNavigationPropertyName.equals(e.getName()))
                .map(e -> (hu.blackbelt.judo.meta.psm.derived.StaticNavigation) e)
                .findAny();

        assertTrue(psmNavigationProperty.isPresent());
        assertThat(psmNavigationProperty.get().getGetterExpression().getExpression(), IsEqual.equalTo(associationEnd.getDefaultExpression().getExpression()));
        assertNull(psmNavigationProperty.get().getSetterExpression());
    }

    @Test
    void testCreateNavigationPropertyFromTwoWayRelationMemberForEntityTypeDefaultTransferObjectTypeTransferObjectRelationDefaultAssociationEndWithPartner() throws Exception {
        testName = "CreateNavigationPropertyFromTwoWayRelationMemberForEntityTypeDefaultTransferObjectTypeTransferObjectRelationDefault";

        EntityType target = newEntityTypeBuilder().withName("target").build();
        target.setMapping(newMappingBuilder()
                .withTarget(target)
                .withFilter(newLogicalExpressionTypeBuilder()
                        .withDialect(ExpressionDialect.JQL)
                        .withExpression("")
                        .build())
                .build());
        EntityType entityType = newEntityTypeBuilder().withName("entityType").build();
        entityType.setMapping(newMappingBuilder()
                .withTarget(entityType)
                .withFilter(newLogicalExpressionTypeBuilder()
                        .withDialect(ExpressionDialect.JQL)
                        .withExpression("")
                        .build())
                .build());

        TwoWayRelationMember associationEnd1 = newTwoWayRelationMemberBuilder().withName("associationEnd1")
                .withLower(1)
                .withUpper(1)
                .withTarget(target)
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("defaultExpression").build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("").build())
                .build();

        TwoWayRelationMember associationEnd2 = newTwoWayRelationMemberBuilder().withName("associationEnd2")
                .withLower(1)
                .withUpper(1)
                .withTarget(entityType)
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("defaultExpression").build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("").build())
                .build();

        entityType.getRelations().add(associationEnd1);
        target.getRelations().add(associationEnd2);
        associationEnd1.setPartner(associationEnd2);
        associationEnd2.setPartner(associationEnd1);

        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(ImmutableList.of(entityType, target))
                .build();

        esmModel.addContent(model);
        transform();

        final hu.blackbelt.judo.meta.psm.data.EntityType psmEntityType = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class)
                .filter(e -> e.getName().equals(entityType.getName())).findAny().get();
        final hu.blackbelt.judo.meta.psm.data.EntityType psmTarget = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class)
                .filter(e -> e.getName().equals(target.getName())).findAny().get();
        assertTrue(psmEntityType.getRelations().size() == 1);
        assertTrue(psmTarget.getRelations().size() == 1);

        String psmNavigationPropertyName1 = "_" + associationEnd1.getName() + "_default_TestModel_entityType";
        String psmNavigationPropertyName2 = "_" + associationEnd2.getName() + "_default_TestModel_target";

        final Namespace namespaceOfPsmEntityType = (Namespace) psmEntityType.eContainer();
        final Namespace namespaceOfPsmTarget = (Namespace) psmTarget.eContainer();
        final Optional<hu.blackbelt.judo.meta.psm.derived.StaticNavigation> psmNavigationProperty1 = namespaceOfPsmEntityType.getElements().stream()
                .filter(e -> psmNavigationPropertyName1.equals(e.getName()))
                .map(e -> (hu.blackbelt.judo.meta.psm.derived.StaticNavigation) e)
                .findAny();
        final Optional<hu.blackbelt.judo.meta.psm.derived.StaticNavigation> psmNavigationProperty2 = namespaceOfPsmTarget.getElements().stream()
                .filter(e -> psmNavigationPropertyName2.equals(e.getName()))
                .map(e -> (hu.blackbelt.judo.meta.psm.derived.StaticNavigation) e)
                .findAny();

        assertTrue(psmNavigationProperty1.isPresent());
        assertTrue(psmNavigationProperty1.get().getName().equals(psmNavigationPropertyName1));
        assertThat(psmNavigationProperty1.get().getGetterExpression().getExpression(), IsEqual.equalTo(associationEnd1.getDefaultExpression().getExpression()));
        assertNull(psmNavigationProperty1.get().getSetterExpression());

        assertTrue(psmNavigationProperty2.isPresent());
        assertTrue(psmNavigationProperty2.get().getName().equals(psmNavigationPropertyName2));
        assertThat(psmNavigationProperty2.get().getGetterExpression().getExpression(), IsEqual.equalTo(associationEnd2.getDefaultExpression().getExpression()));
        assertNull(psmNavigationProperty2.get().getSetterExpression());
    }

    @Test
    void testCreateNavigationPropertyFromOneWayRelationMemberForEntityTypeDefaultTransferObjectTypeTransferObjectRelationRangeContainment() throws Exception {
        testName = "CreateNavigationPropertyFromOneWayRelationMemberForEntityTypeDefaultTransferObjectTypeTransferObjectRelationRange";

        EntityType target = newEntityTypeBuilder().withName("target").build();
        target.setMapping(newMappingBuilder()
                .withTarget(target)
                .withFilter(newLogicalExpressionTypeBuilder()
                        .withDialect(ExpressionDialect.JQL)
                        .withExpression("")
                        .build())
                .build());

        OneWayRelationMember containment = newOneWayRelationMemberBuilder().withName("containment").withContainment(true)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("").build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("").build())
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("").build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("rangeExpression").build())
                .withLower(1)
                .withUpper(3)
                .withTarget(target)
                .build();

        EntityType entityType = newEntityTypeBuilder().withName("entityType").withRelations(containment).build();
        entityType.setMapping(newMappingBuilder()
                .withTarget(entityType)
                .withFilter(newLogicalExpressionTypeBuilder()
                        .withDialect(ExpressionDialect.JQL)
                        .withExpression("")
                        .build())
                .build());

        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(ImmutableList.of(entityType, target))
                .build();

        esmModel.addContent(model);
        transform();

        final hu.blackbelt.judo.meta.psm.data.EntityType psmEntityType = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class)
                .filter(e -> e.getName().equals(entityType.getName())).findAny().get();

        assertTrue(psmEntityType.getRelations().size() == 1);
        assertTrue(psmEntityType.getNavigationProperties().size() == 1);

        final NavigationProperty psmNavigationProperty = psmEntityType.getNavigationProperties().get(0);

        String psmNavigationPropertyName = "_" + containment.getName() + "_range_TestModel_entityType";

        assertTrue(psmNavigationProperty.getName().equals(psmNavigationPropertyName));
        assertThat(psmNavigationProperty.getGetterExpression().getExpression(), IsEqual.equalTo(containment.getRangeExpression().getExpression()));
        assertNull(psmNavigationProperty.getSetterExpression());
    }

    @Test
    void testCreateNavigationPropertyFromOneWayRelationMemberForEntityTypeDefaultTransferObjectTypeTransferObjectRelationRangeAssociationEndWithoutPartner() throws Exception {
        testName = "CreateNavigationPropertyFromOneWayRelationMemberForEntityTypeDefaultTransferObjectTypeTransferObjectRelationRange";

        EntityType target = newEntityTypeBuilder().withName("target").build();
        target.setMapping(newMappingBuilder()
                .withTarget(target)
                .withFilter(newLogicalExpressionTypeBuilder()
                        .withDialect(ExpressionDialect.JQL)
                        .withExpression("")
                        .build())
                .build());

        OneWayRelationMember associationEnd = newOneWayRelationMemberBuilder().withName("associationEnd").withContainment(false)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("").build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("").build())
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("").build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("rangeExpression").build())
                .withReverseCascadeDelete(true)
                .withLower(1)
                .withUpper(3)
                .withTarget(target)
                .build();

        EntityType entityType = newEntityTypeBuilder().withName("entityType").withRelations(associationEnd).build();
        entityType.setMapping(newMappingBuilder()
                .withTarget(entityType)
                .withFilter(newLogicalExpressionTypeBuilder()
                        .withDialect(ExpressionDialect.JQL)
                        .withExpression("")
                        .build())
                .build());

        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(ImmutableList.of(entityType, target))
                .build();

        esmModel.addContent(model);
        transform();

        final hu.blackbelt.judo.meta.psm.data.EntityType psmEntityType = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class)
                .filter(e -> e.getName().equals(entityType.getName())).findAny().get();

        assertTrue(psmEntityType.getRelations().size() == 1);
        assertTrue(psmEntityType.getNavigationProperties().size() == 1);

        final NavigationProperty psmNavigationProperty = psmEntityType.getNavigationProperties().get(0);

        String psmNavigationPropertyName = "_" + associationEnd.getName() + "_range_TestModel_entityType";

        assertTrue(psmNavigationProperty.getName().equals(psmNavigationPropertyName));
        assertThat(psmNavigationProperty.getGetterExpression().getExpression(), IsEqual.equalTo(associationEnd.getRangeExpression().getExpression()));
        assertNull(psmNavigationProperty.getSetterExpression());
    }

    @Test
    void testCreateNavigationPropertyFromOneWayRelationMemberForEntityTypeDefaultTransferObjectTypeTransferObjectRelationRangeNavigationProperty() throws Exception {
        testName = "CreateNavigationPropertyFromOneWayRelationMemberForEntityTypeDefaultTransferObjectTypeTransferObjectRelationRange";

        EntityType target = newEntityTypeBuilder().withName("target").build();
        target.setMapping(newMappingBuilder()
                .withTarget(target)
                .withFilter(newLogicalExpressionTypeBuilder()
                        .withDialect(ExpressionDialect.JQL)
                        .withExpression("")
                        .build())
                .build());

        OneWayRelationMember associationEnd = newOneWayRelationMemberBuilder().withName("associationEnd").withContainment(false).withProperty(true)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("getterExpresssion").build())
                .withSetterExpression(newReferenceSelectorTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("").build())
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("").build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("rangeExpression").build())
                .withReverseCascadeDelete(true)
                .withLower(1)
                .withUpper(3)
                .withTarget(target)
                .build();

        EntityType entityType = newEntityTypeBuilder().withName("entityType").withRelations(associationEnd).build();
        entityType.setMapping(newMappingBuilder()
                .withTarget(entityType)
                .withFilter(newLogicalExpressionTypeBuilder()
                        .withDialect(ExpressionDialect.JQL)
                        .withExpression("")
                        .build())
                .build());

        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(ImmutableList.of(entityType, target))
                .build();

        esmModel.addContent(model);
        transform();

        final hu.blackbelt.judo.meta.psm.data.EntityType psmEntityType = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class)
                .filter(e -> e.getName().equals(entityType.getName())).findAny().get();

        assertTrue(psmEntityType.getNavigationProperties().size() == 2);

        final Optional<NavigationProperty> psmNavigationProperty = psmEntityType.getNavigationProperties().stream()
                .filter(p -> p.getName().equals("_" + associationEnd.getName() + "_range_TestModel_entityType")).findAny();

        assertTrue(psmNavigationProperty.isPresent());
        assertThat(psmNavigationProperty.get().getGetterExpression().getExpression(), IsEqual.equalTo(associationEnd.getRangeExpression().getExpression()));
        assertNull(psmNavigationProperty.get().getSetterExpression());
    }

    @Test
    void testCreateNavigationPropertyFromTwoWayRelationMemberForEntityTypeDefaultTransferObjectTypeTransferObjectRelationRangeAssociationEndWithPartner() throws Exception {
        testName = "CreateNavigationPropertyFromTwoWayRelationMemberForEntityTypeDefaultTransferObjectTypeTransferObjectRelationRange";

        EntityType target = newEntityTypeBuilder().withName("target").build();
        target.setMapping(newMappingBuilder()
                .withTarget(target)
                .withFilter(newLogicalExpressionTypeBuilder()
                        .withDialect(ExpressionDialect.JQL)
                        .withExpression("")
                        .build())
                .build());
        EntityType entityType = newEntityTypeBuilder().withName("entityType").build();
        entityType.setMapping(newMappingBuilder()
                .withTarget(entityType)
                .withFilter(newLogicalExpressionTypeBuilder()
                        .withDialect(ExpressionDialect.JQL)
                        .withExpression("")
                        .build())
                .build());

        TwoWayRelationMember associationEnd1 = newTwoWayRelationMemberBuilder().withName("associationEnd1")
                .withLower(1)
                .withUpper(1)
                .withTarget(target)
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("").build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("rangeExpression").build())
                .build();

        TwoWayRelationMember associationEnd2 = newTwoWayRelationMemberBuilder().withName("associationEnd2")
                .withLower(1)
                .withUpper(1)
                .withTarget(entityType)
                .withDefaultExpression(newReferenceExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("").build())
                .withRangeExpression(newReferenceExpressionTypeBuilder().withDialect(ExpressionDialect.JQL).withExpression("rangeExpression").build())
                .build();

        entityType.getRelations().add(associationEnd1);
        target.getRelations().add(associationEnd2);
        associationEnd1.setPartner(associationEnd2);
        associationEnd2.setPartner(associationEnd1);

        final Model model = newModelBuilder()
                .withName("TestModel")
                .withElements(ImmutableList.of(entityType, target))
                .build();

        esmModel.addContent(model);
        transform();

        final hu.blackbelt.judo.meta.psm.data.EntityType psmEntityType = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class)
                .filter(e -> e.getName().equals(entityType.getName())).findAny().get();
        final hu.blackbelt.judo.meta.psm.data.EntityType psmTarget = allPsm(hu.blackbelt.judo.meta.psm.data.EntityType.class)
                .filter(e -> e.getName().equals(target.getName())).findAny().get();
        assertTrue(psmEntityType.getRelations().size() == 1);
        assertTrue(psmTarget.getRelations().size() == 1);
        assertTrue(psmEntityType.getNavigationProperties().size() == 1);
        assertTrue(psmTarget.getNavigationProperties().size() == 1);

        final NavigationProperty psmNavigationProperty1 = psmEntityType.getNavigationProperties().get(0);
        final NavigationProperty psmNavigationProperty2 = psmTarget.getNavigationProperties().get(0);

        String psmNavigationPropertyName1 = "_" + associationEnd1.getName() + "_range_TestModel_entityType";

        assertTrue(psmNavigationProperty1.getName().equals(psmNavigationPropertyName1));
        assertThat(psmNavigationProperty1.getGetterExpression().getExpression(), IsEqual.equalTo(associationEnd1.getRangeExpression().getExpression()));
        assertNull(psmNavigationProperty1.getSetterExpression());

        String psmNavigationPropertyName2 = "_" + associationEnd2.getName() + "_range_TestModel_target";

        assertTrue(psmNavigationProperty2.getName().equals(psmNavigationPropertyName2));
        assertThat(psmNavigationProperty2.getGetterExpression().getExpression(), IsEqual.equalTo(associationEnd2.getRangeExpression().getExpression()));
        assertNull(psmNavigationProperty2.getSetterExpression());
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
