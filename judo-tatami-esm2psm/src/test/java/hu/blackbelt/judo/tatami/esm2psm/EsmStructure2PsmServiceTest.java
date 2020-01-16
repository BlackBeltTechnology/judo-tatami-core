package hu.blackbelt.judo.tatami.esm2psm;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.namespace.Model;
import hu.blackbelt.judo.meta.esm.namespace.Package;
import hu.blackbelt.judo.meta.esm.operation.BoundOperation;
import hu.blackbelt.judo.meta.esm.operation.UnboundOperation;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.esm.structure.*;
import hu.blackbelt.judo.meta.esm.type.StringType;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType;
import hu.blackbelt.judo.meta.psm.service.TransferAttribute;
import hu.blackbelt.judo.meta.psm.service.TransferObjectRelation;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
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

import static hu.blackbelt.judo.meta.esm.namespace.util.builder.NamespaceBuilders.newModelBuilder;
import static hu.blackbelt.judo.meta.esm.namespace.util.builder.NamespaceBuilders.newPackageBuilder;
import static hu.blackbelt.judo.meta.esm.operation.util.builder.OperationBuilders.*;
import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.buildEsmModel;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.*;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newStringTypeBuilder;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.SaveArguments.psmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.buildPsmModel;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.calculateEsm2PsmTransformationScriptURI;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.executeEsm2PsmTransformation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class EsmStructure2PsmServiceTest {

	private final String TEST_SOURCE_MODEL_NAME = "urn:test.judo-meta-esm";
	private final String TEST = "test";
	private final String TARGET_TEST_CLASSES = "target/test-classes";

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
		esmModel = buildEsmModel().uri(URI.createURI(TEST_SOURCE_MODEL_NAME)).name(TEST).build();

		// Create empty PSM model
		psmModel = buildPsmModel().name(TEST).build();
	}

	@AfterEach
	void tearDown() throws Exception {
		final String traceFileName = testName + "-esm2psm.model";

		// Saving trace map
		esm2PsmTransformationTrace.save(new File(TARGET_TEST_CLASSES, traceFileName));

		// Loading trace map
		Esm2PsmTransformationTrace esm2PsmTransformationTraceLoaded = Esm2PsmTransformationTrace
				.fromModelsAndTrace(TEST, esmModel, psmModel, new File(TARGET_TEST_CLASSES, traceFileName));

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
		esm2PsmTransformationTrace = executeEsm2PsmTransformation(esmModel, psmModel, new Slf4jLog(log),
				calculateEsm2PsmTransformationScriptURI());
	}
	
	@Test
	void testCreateUpdateOperationForTransferObjectRelation() throws Exception {
		testName = "CreateUpdateOperationForTransferObjectRelation";
		
		EntityType order = newEntityTypeBuilder().withName("Order").build();
		order.setMapping(newMappingBuilder().withTarget(order).build());
		
		EntityType orderItem = newEntityTypeBuilder().withName("OrderItem").build();
		orderItem.setMapping(newMappingBuilder().withTarget(orderItem).build());
		
		OneWayRelationMember items = newOneWayRelationMemberBuilder().withName("items")
				.withUpdateable(true)
				.withRelationMemberType(RelationMemberType.RELATION).withTarget(orderItem).build();
		
		EntityType product = newEntityTypeBuilder().withName("Product").build();
		product.setMapping(newMappingBuilder().withTarget(product).build());
		
		OneWayRelationMember productOfItem = newOneWayRelationMemberBuilder().withName("product")
				.withTarget(product).build();
		orderItem.getRelations().add(productOfItem);
				
		order.getRelations().add(items);
		
		Model model = newModelBuilder().withName("Model")
				.withElements(ImmutableList.of(order, orderItem, product)).build();

		esmModel.addContent(model);
		
		transform();
		
		final Optional<hu.blackbelt.judo.meta.psm.service.BoundOperationWithRelation> psmSetOp = allPsm(
				hu.blackbelt.judo.meta.psm.service.BoundOperationWithRelation.class)
				.filter(o -> o.getName().equalsIgnoreCase("_set" + productOfItem.getName() + "Of" + items.getName()))
				.findAny();
		
		final Optional<hu.blackbelt.judo.meta.psm.service.TransferObjectRelation> psmProductsTransferObjectRelation = allPsm(
				hu.blackbelt.judo.meta.psm.service.TransferObjectRelation.class)
				.filter(r -> r.getName().equals(productOfItem.getName()))
				.findAny();
		assertTrue(psmProductsTransferObjectRelation.isPresent());
		
		final Optional<hu.blackbelt.judo.meta.psm.service.TransferObjectRelation> psmItemsTransferObjectRelation = allPsm(
				hu.blackbelt.judo.meta.psm.service.TransferObjectRelation.class)
				.filter(r -> r.getName().equals(items.getName()))
				.findAny();
		assertTrue(psmItemsTransferObjectRelation.isPresent());
		
		assertTrue(psmSetOp.isPresent());
		assertThat(psmSetOp.get().getInput().getName(), IsEqual.equalTo("input"));
		assertEquals(psmSetOp.get().getInput().getCardinality().getLower(),1);
		assertEquals(psmSetOp.get().getInput().getCardinality().getUpper(),1);
		assertNull(psmSetOp.get().getOutput());
		assertThat(psmSetOp.get().getRelation(), IsEqual.equalTo(psmProductsTransferObjectRelation.get()));
		
		
		final Optional<hu.blackbelt.judo.meta.psm.service.BoundOperationWithRelation> psmUnsetOp = allPsm(
				hu.blackbelt.judo.meta.psm.service.BoundOperationWithRelation.class)
				.filter(o -> o.getName().equalsIgnoreCase("_unset" + productOfItem.getName() + "Of" + items.getName()))
				.findAny();
		
		assertTrue(psmUnsetOp.isPresent());
		assertThat(psmUnsetOp.get().getInput().getName(), IsEqual.equalTo("input"));
		assertEquals(psmUnsetOp.get().getInput().getCardinality().getLower(),1);
		assertEquals(psmUnsetOp.get().getInput().getCardinality().getUpper(),1);
		assertNull(psmUnsetOp.get().getOutput());
		assertThat(psmUnsetOp.get().getRelation(), IsEqual.equalTo(psmProductsTransferObjectRelation.get()));
		
		assertThat(psmItemsTransferObjectRelation.get().getSet().get(0), IsEqual.equalTo(psmSetOp.get()));
	}

	@Test
	void testCreateUnmappedTransferObjectWithRelationAndDataMembers() throws Exception {
		testName = "CreateUnmappedTransferObjectWithRelationAndDataMembers";

		// attributes
		StringType stringType = newStringTypeBuilder().withName("stringType").withMaxLength(255).build();
		DataMember dataMemberBasic = newDataMemberBuilder().withName("transferAttributeBasic").withRequired(false)
				.withIdentifier(false).withDataType(stringType).build();

		// relation targets
		EntityType targetEntityType = newEntityTypeBuilder().withName("targetEntityType").withAbstract_(false).build();
		targetEntityType.setMapping(newMappingBuilder().withTarget(targetEntityType).build());

		TransferObjectType targetUnmappedTransferObjectType = newTransferObjectTypeBuilder()
				.withName("targetUnmappedTransferObjectType").build();

		TransferObjectType targetMappedTransferObjectType = newTransferObjectTypeBuilder()
				.withName("targetMappedTransferObjectType")
				.withMapping(newMappingBuilder().withTarget(targetEntityType).build()).build();

		// relations
		OneWayRelationMember oneWayRelationContainmentTargetingUnmapped = newOneWayRelationMemberBuilder()
				.withName("transferRelationAggregationTargetingUnmapped").withAggregation(true).withContainment(false)
				.withLower(1).withUpper(1).withRelationMemberType(RelationMemberType.RELATION)
				.withTarget(targetUnmappedTransferObjectType).build();

		OneWayRelationMember oneWayRelationContainmentTargetingMapped = newOneWayRelationMemberBuilder()
				.withName("transferRelationContainmentTargetingMapped").withContainment(false).withAggregation(true)
				.withLower(1).withUpper(1).withRelationMemberType(RelationMemberType.RELATION)
				.withTarget(targetMappedTransferObjectType).build();

		OneWayRelationMember oneWayRelationBasicTargetingEntity = newOneWayRelationMemberBuilder()
				.withName("transferRelationBasicTargetingEntity").withContainment(false).withAggregation(false)
				.withLower(1).withUpper(1).withRelationMemberType(RelationMemberType.RELATION)
				.withTarget(targetEntityType).build();

		// Unmapped TransferObjectType
		TransferObjectType unmappedTransferObjectType = newTransferObjectTypeBuilder()
				.withName("unmappedTransferObjectType")
				.withRelations(ImmutableList.of(oneWayRelationBasicTargetingEntity,
						oneWayRelationContainmentTargetingUnmapped, oneWayRelationContainmentTargetingMapped))
				.withAttributes(ImmutableList.of(dataMemberBasic)).build();

		Package servicesPkg = newPackageBuilder().withName("service").withElements(ImmutableList
				.of(targetMappedTransferObjectType, unmappedTransferObjectType, targetUnmappedTransferObjectType))
				.build();
		Package entitiesPkg = newPackageBuilder().withName("entities").withElements(ImmutableList.of(targetEntityType))
				.build();
		Package typesPkg = newPackageBuilder().withName("types").withElements(ImmutableList.of(stringType)).build();
		Model model = newModelBuilder().withName("Model")
				.withElements(ImmutableList.of(servicesPkg, entitiesPkg, typesPkg)).build();

		esmModel.addContent(model);

		transform();

		// Namespaces
		final Optional<hu.blackbelt.judo.meta.psm.namespace.Model> psmModel = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Model.class).findAny();
		assertTrue(psmModel.isPresent());

		final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmServicePackage = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Package.class)
						.filter(pack -> servicesPkg.getName().equals(pack.getName())).findAny();
		assertTrue(psmServicePackage.isPresent());

		final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmEntitiesPackage = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Package.class)
						.filter(pack -> entitiesPkg.getName().equals(pack.getName())).findAny();
		assertTrue(psmEntitiesPackage.isPresent());

		final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmTypesPackage = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Package.class)
						.filter(pack -> typesPkg.getName().equals(pack.getName())).findAny();
		assertTrue(psmTypesPackage.isPresent());

		// targets
		final Optional<hu.blackbelt.judo.meta.psm.data.EntityType> psmTargetEntityType = allPsm(
				hu.blackbelt.judo.meta.psm.data.EntityType.class)
						.filter(entityType -> targetEntityType.getName().equals(entityType.getName())).findAny();
		assertTrue(psmTargetEntityType.isPresent());

		final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmDefaultTransferObjectOfTargetEntityType = allPsm(
				hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class)
						.filter(mappedTOT -> (targetEntityType.getName()).equals(mappedTOT.getName()))
						.findAny();
		assertTrue(psmDefaultTransferObjectOfTargetEntityType.isPresent());

		final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmTargetMappedTransferObject = allPsm(
				hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class)
						.filter(mappedTOT -> targetMappedTransferObjectType.getName().equals(mappedTOT.getName()))
						.findAny();
		assertTrue(psmTargetMappedTransferObject.isPresent());

		final Optional<hu.blackbelt.judo.meta.psm.service.UnmappedTransferObjectType> psmTargetUnmappedTransferObject = allPsm(
				hu.blackbelt.judo.meta.psm.service.UnmappedTransferObjectType.class)
						.filter(unmappedTOT -> targetUnmappedTransferObjectType.getName().equals(unmappedTOT.getName()))
						.findAny();
		assertTrue(psmTargetUnmappedTransferObject.isPresent());

		// UnmappedTransferObject
		final Optional<hu.blackbelt.judo.meta.psm.service.UnmappedTransferObjectType> psmUnmappedTransferObject = allPsm(
				hu.blackbelt.judo.meta.psm.service.UnmappedTransferObjectType.class)
						.filter(unmappedTOT -> unmappedTransferObjectType.getName().equals(unmappedTOT.getName()))
						.findAny();
		assertTrue(psmUnmappedTransferObject.isPresent());
		assertThat(psmUnmappedTransferObject.get().getNamespace().getName(),
				IsEqual.equalTo(psmServicePackage.get().getName()));

		// UnmappedTransferObject / TransferAttributes
		final Optional<TransferAttribute> psmTransferAttributeBasic = allPsm(TransferAttribute.class)
				.filter(transferAttr -> dataMemberBasic.getName().equals(transferAttr.getName())).findAny();
		assertTrue(psmTransferAttributeBasic.isPresent());
		assertTrue(psmUnmappedTransferObject.get().getAttributes().contains(psmTransferAttributeBasic.get()));

		// UnmappedTransferObject / TransferRelations
		final Optional<TransferObjectRelation> psmTransferObjectRelationEmbeddedTargetingUnmapped = allPsm(
				TransferObjectRelation.class)
						.filter(transferRel -> oneWayRelationContainmentTargetingUnmapped.getName()
								.equals(transferRel.getName()))
						.findAny();
		assertTrue(psmTransferObjectRelationEmbeddedTargetingUnmapped.isPresent());
		assertTrue(psmTransferObjectRelationEmbeddedTargetingUnmapped.get().getTarget()
				.equals(psmTargetUnmappedTransferObject.get()));
		assertTrue(psmTransferObjectRelationEmbeddedTargetingUnmapped.get().isEmbedded());
		assertTrue(psmUnmappedTransferObject.get().getRelations()
				.contains(psmTransferObjectRelationEmbeddedTargetingUnmapped.get()));

		final Optional<TransferObjectRelation> psmTransferObjectRelationEmbeddedTargetingMapped = allPsm(
				TransferObjectRelation.class).filter(
						transferRel -> oneWayRelationContainmentTargetingMapped.getName().equals(transferRel.getName()))
						.findAny();
		assertTrue(psmTransferObjectRelationEmbeddedTargetingMapped.isPresent());
		assertTrue(psmTransferObjectRelationEmbeddedTargetingMapped.get().getTarget()
				.equals(psmTargetMappedTransferObject.get()));
		assertTrue(psmTransferObjectRelationEmbeddedTargetingMapped.get().isEmbedded());
		assertTrue(psmUnmappedTransferObject.get().getRelations()
				.contains(psmTransferObjectRelationEmbeddedTargetingMapped.get()));

		final Optional<TransferObjectRelation> psmTransferObjectRelationBasicTargetingEntity = allPsm(
				TransferObjectRelation.class).filter(
						transferRel -> oneWayRelationBasicTargetingEntity.getName().equals(transferRel.getName()))
						.findAny();
		assertTrue(psmTransferObjectRelationBasicTargetingEntity.isPresent());
		assertTrue(psmTransferObjectRelationBasicTargetingEntity.get().getTarget()
				.equals(psmDefaultTransferObjectOfTargetEntityType.get()));
		assertFalse(psmTransferObjectRelationBasicTargetingEntity.get().isEmbedded());
		assertTrue(psmUnmappedTransferObject.get().getRelations()
				.contains(psmTransferObjectRelationBasicTargetingEntity.get()));
	}

	@Test
	void testCreateDefaultTransferObjectWithRelationMembers() throws Exception {
		testName = "CreateDefaultTransferObjectWithRelationMembers";

		// relations
		EntityType targetEntityType = newEntityTypeBuilder().withName("targetEntityType").withAbstract_(false).build();
		targetEntityType.setMapping(newMappingBuilder().withTarget(targetEntityType).build());

		OneWayRelationMember oneWayRelationContainment = newOneWayRelationMemberBuilder()
				.withName("transferRelationContainment").withContainment(true).withAggregation(true).withLower(1)
				.withUpper(1).withRelationMemberType(RelationMemberType.RELATION).withTarget(targetEntityType).build();
		oneWayRelationContainment.setBinding(oneWayRelationContainment);

		OneWayRelationMember oneWayRelationBasic = newOneWayRelationMemberBuilder().withName("transferRelationBasic")
				.withContainment(false).withAggregation(false).withLower(1).withUpper(1)
				.withRelationMemberType(RelationMemberType.RELATION).withTarget(targetEntityType).build();
		oneWayRelationBasic.setBinding(oneWayRelationBasic);

		OneWayRelationMember oneWayRelationWithProperty = newOneWayRelationMemberBuilder()
				.withName("transferRelationWithBinding").withContainment(false).withAggregation(false).withLower(1)
				.withUpper(1).withRelationMemberType(RelationMemberType.PROPERTY)
				.withGetterExpression("self.transferRelationWithBinding.target").withTarget(targetEntityType).build();
		oneWayRelationWithProperty.setBinding(oneWayRelationWithProperty);

		OneWayRelationMember oneWayRelationWithPropertyWithDefault = newOneWayRelationMemberBuilder()
				.withName("transferRelationWithBindingWithDefault").withContainment(false).withLower(1)
				.withUpper(1).withRelationMemberType(RelationMemberType.PROPERTY).withGetterExpression("expr")
				.withTarget(targetEntityType).withDefaultExpression("expr").build();
		oneWayRelationWithPropertyWithDefault.setBinding(oneWayRelationWithPropertyWithDefault);

		OneWayRelationMember oneWayRelationWithPropertyWithRange = newOneWayRelationMemberBuilder()
				.withName("transferRelationWithBindingWithRange").withContainment(false).withLower(1)
				.withUpper(1).withRelationMemberType(RelationMemberType.PROPERTY).withGetterExpression("expr")
				.withTarget(targetEntityType).withRangeExpression("expr").build();
		oneWayRelationWithPropertyWithRange.setBinding(oneWayRelationWithPropertyWithRange);

		OneWayRelationMember oneWayRelationWithPropertyWithDefaultAndRange = newOneWayRelationMemberBuilder()
				.withName("transferRelationWithBindingWithDefaultAndRange").withContainment(false)
				.withLower(1).withUpper(1).withRelationMemberType(RelationMemberType.PROPERTY)
				.withGetterExpression("expr").withTarget(targetEntityType).withDefaultExpression("expr")
				.withRangeExpression("expr").build();
		oneWayRelationWithPropertyWithDefaultAndRange
				.setBinding(oneWayRelationWithPropertyWithDefaultAndRange);

		EntityType entityType = newEntityTypeBuilder().withName("entityType")
				.withRelations(ImmutableList.of(oneWayRelationContainment, oneWayRelationBasic,
						oneWayRelationWithProperty, oneWayRelationWithPropertyWithDefault,
						oneWayRelationWithPropertyWithRange,
						oneWayRelationWithPropertyWithDefaultAndRange))
				.build();
		entityType.setMapping(newMappingBuilder().withTarget(entityType).build());

		Package servicePackage = newPackageBuilder().withName("service")
				.withElements(ImmutableList.of(entityType)).build();
		Package entitiesPackage = newPackageBuilder().withName("entities")
				.withElements(ImmutableList.of(targetEntityType)).build();
		Model model = newModelBuilder().withName("Model")
				.withElements(ImmutableList.of(servicePackage, entitiesPackage)).build();

		esmModel.addContent(model);

		transform();

		// Namespaces
		final Optional<hu.blackbelt.judo.meta.psm.namespace.Model> psmModel = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Model.class).filter(m -> model.getName().equals(m.getName()))
						.findAny();
		assertTrue(psmModel.isPresent());
		final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmServicePackage = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Package.class)
						.filter(pkg -> servicePackage.getName().equals(pkg.getName())).findAny();
		assertTrue(psmServicePackage.isPresent());
		final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmEntitiesPackage = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Package.class)
						.filter(pkg -> entitiesPackage.getName().equals(pkg.getName())).findAny();
		assertTrue(psmEntitiesPackage.isPresent());

		final Optional<hu.blackbelt.judo.meta.psm.data.EntityType> psmEntityType = allPsm(
				hu.blackbelt.judo.meta.psm.data.EntityType.class)
						.filter(entity -> entityType.getName().equals(entity.getName())).findAny();
		assertTrue(psmEntityType.isPresent());
		final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmDefaultTransferObject = allPsm(
				hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class)
						.filter(mappedTOT -> (entityType.getName()).equals(mappedTOT.getName()))
						.findAny();
		assertTrue(psmDefaultTransferObject.isPresent());

		assertThat(psmDefaultTransferObject.get().getName(), IsEqual.equalTo(entityType.getName()));
		assertThat(psmDefaultTransferObject.get().getNamespace().getName(),
				IsEqual.equalTo(servicePackage.getName()));

		final Optional<hu.blackbelt.judo.meta.psm.data.EntityType> psmTargetEntityType = allPsm(
				hu.blackbelt.judo.meta.psm.data.EntityType.class)
						.filter(entity -> targetEntityType.getName().equals(entity.getName())).findAny();
		assertTrue(psmTargetEntityType.isPresent());

		final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmTargetMappedTransferObjectType = allPsm(
				hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class)
						.filter(mappedTOT -> (targetEntityType.getName()).equals(mappedTOT.getName()))
						.findAny();
		assertTrue(psmTargetMappedTransferObjectType.isPresent());

		// relations
		// embedded
		final Optional<TransferObjectRelation> psmTransferObjectRelationEmbedded = allPsm(TransferObjectRelation.class)
				.filter(transferObjectRel -> oneWayRelationContainment.getName().equals(transferObjectRel.getName()))
				.findAny();
		assertTrue(psmTransferObjectRelationEmbedded.isPresent());

		assertTrue(psmDefaultTransferObject.get().getRelations().contains(psmTransferObjectRelationEmbedded.get()));
		assertThat(psmTransferObjectRelationEmbedded.get().getTarget(),
				IsEqual.equalTo(psmTargetMappedTransferObjectType.get()));
		assertTrue(psmTransferObjectRelationEmbedded.get().isEmbedded());
		assertTrue(psmTransferObjectRelationEmbedded.get().getCardinality().getLower() == 1
				&& psmTransferObjectRelationEmbedded.get().getCardinality().getUpper() == 1);

		// property
		final Optional<TransferObjectRelation> psmTransferObjectRelation = allPsm(TransferObjectRelation.class)
				.filter(transferObjectRel -> oneWayRelationBasic.getName().equals(transferObjectRel.getName()))
				.findAny();
		assertTrue(psmTransferObjectRelation.isPresent());

		assertTrue(psmDefaultTransferObject.get().getRelations().contains(psmTransferObjectRelation.get()));
		assertThat(psmTransferObjectRelation.get().getTarget(),
				IsEqual.equalTo(psmTargetMappedTransferObjectType.get()));
		assertTrue(psmTransferObjectRelation.get().getCardinality().getLower() == 1
				&& psmTransferObjectRelation.get().getCardinality().getUpper() == 1);

		final Optional<TransferObjectRelation> psmTransferObjectRelationWithProperty = allPsm(
				TransferObjectRelation.class).filter(
						transferObjectRel -> oneWayRelationWithProperty.getName().equals(transferObjectRel.getName()))
						.findAny();
		assertTrue(psmTransferObjectRelationWithProperty.isPresent());

		assertTrue(psmDefaultTransferObject.get().getRelations().contains(psmTransferObjectRelationWithProperty.get()));
		assertThat(psmTransferObjectRelationWithProperty.get().getTarget(),
				IsEqual.equalTo(psmTargetMappedTransferObjectType.get()));
		assertTrue(psmTransferObjectRelationWithProperty.get().getCardinality().getLower() == 1
				&& psmTransferObjectRelationWithProperty.get().getCardinality().getUpper() == 1);

		final Optional<TransferObjectRelation> psmTransferObjectRelationWithPropertyWithSetterWithDefaultAndRange = allPsm(
				TransferObjectRelation.class)
						.filter(transferObjectRel -> oneWayRelationWithPropertyWithDefaultAndRange.getName()
								.equals(transferObjectRel.getName()))
						.findAny();
		assertTrue(psmTransferObjectRelationWithPropertyWithSetterWithDefaultAndRange.isPresent());

		assertTrue(psmDefaultTransferObject.get().getRelations()
				.contains(psmTransferObjectRelationWithPropertyWithSetterWithDefaultAndRange.get()));
		assertThat(psmTransferObjectRelationWithPropertyWithSetterWithDefaultAndRange.get().getTarget(),
				IsEqual.equalTo(psmTargetMappedTransferObjectType.get()));
		assertTrue(psmTransferObjectRelationWithPropertyWithSetterWithDefaultAndRange.get().getCardinality()
				.getLower() == 1
				&& psmTransferObjectRelationWithPropertyWithSetterWithDefaultAndRange.get().getCardinality()
						.getUpper() == 1);
	}

	
	@Test
	void testCreateDefaultTransferObjectWithDataMembers() throws Exception {
		testName = "CreateDefaultTransferObjectWithDataMembers";

		// attributes
		StringType stringType = newStringTypeBuilder().withName("stringType").withMaxLength(255).build();

		DataMember dataMemberBasic = newDataMemberBuilder().withName("transferAttributeBasic").withRequired(false)
				.withIdentifier(false).withDataType(stringType)
				.withDataMemberType(DataMemberType.ATTRIBUTE)
				.build();
		dataMemberBasic.setBinding(dataMemberBasic);

		DataMember dataMemberWithProperty = newDataMemberBuilder().withName("transferAttributeWithBinding")
				.withRequired(false).withDataMemberType(DataMemberType.PROPERTY).withIdentifier(false)
				.withDataType(stringType).withGetterExpression("expr")
				.build();
		dataMemberWithProperty.setBinding(dataMemberWithProperty);

		EntityType entityType = newEntityTypeBuilder().withName("entityType").withAbstract_(false)
				.withAttributes(ImmutableList.of(dataMemberBasic, dataMemberWithProperty)).build();
		entityType.setMapping(newMappingBuilder().withTarget(entityType).build());

		Package entitiesPackage = newPackageBuilder().withName("entities").withElements(ImmutableList.of(entityType))
				.build();
		Package typesPackage = newPackageBuilder().withName("types").withElements(ImmutableList.of(stringType)).build();
		Model model = newModelBuilder().withName("Model")
				.withElements(ImmutableList.of(entitiesPackage, typesPackage)).build();

		esmModel.addContent(model);
		
		transform();

		// Namespaces
		final Optional<hu.blackbelt.judo.meta.psm.namespace.Model> psmModel = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Model.class).filter(m -> model.getName().equals(m.getName()))
						.findAny();
		assertTrue(psmModel.isPresent());

		final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmEntitiesPackage = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Package.class)
						.filter(pkg -> entitiesPackage.getName().equals(pkg.getName())).findAny();
		assertTrue(psmEntitiesPackage.isPresent());

		final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmTypesPackage = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Package.class)
						.filter(pkg -> typesPackage.getName().equals(pkg.getName())).findAny();
		assertTrue(psmTypesPackage.isPresent());
		
		// entityType
		final Optional<hu.blackbelt.judo.meta.psm.data.EntityType> psmEntityType = allPsm(
				hu.blackbelt.judo.meta.psm.data.EntityType.class)
						.filter(entity -> entityType.getName().equals(entity.getName())).findAny();
		assertTrue(psmEntityType.isPresent());
		
		// defaultTransferObject of entityType
		final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmDefaultTransferObject = allPsm(
				hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class)
						.filter(mappedTOT -> (entityType.getName()).equals(mappedTOT.getName()))
						.findAny();
		assertTrue(psmDefaultTransferObject.isPresent());

		assertThat(psmDefaultTransferObject.get().getName(), IsEqual.equalTo(entityType.getName()));
		assertThat(psmDefaultTransferObject.get().getNamespace().getName(),
				IsEqual.equalTo(entitiesPackage.getName()));

		// attributes
		final Optional<TransferAttribute> transferAttributeBasic = allPsm(TransferAttribute.class)
				.filter(transferAttr -> dataMemberBasic.getName().equals(transferAttr.getName())).findAny();
		assertTrue(transferAttributeBasic.isPresent());
		assertTrue(psmDefaultTransferObject.get().getAttributes().contains(transferAttributeBasic.get()));

		// with property
		final Optional<TransferAttribute> transferAttributeAttributeWithProperty = allPsm(TransferAttribute.class)
				.filter(transferAttr -> dataMemberWithProperty.getName().equals(transferAttr.getName())).findAny();
		assertTrue(transferAttributeAttributeWithProperty.isPresent());
		assertTrue(psmDefaultTransferObject.get().getAttributes().contains(transferAttributeAttributeWithProperty.get()));
	}

	@Test
	void testCreateMappedTransferObjectWithRelationMembers() throws Exception {
		testName = "CreateMappedTransferObjectWithRelationMembers";

		EntityType entityType = newEntityTypeBuilder().withName("entityType").withAbstract_(false).build();
		entityType.setMapping(newMappingBuilder().withTarget(entityType).build());

		// relation targets
		EntityType targetEntityType = newEntityTypeBuilder().withName("targetEntityType").withAbstract_(false).build();
		targetEntityType.setMapping(newMappingBuilder().withTarget(targetEntityType).build());

		TransferObjectType targetMappedTransferObjectType = newTransferObjectTypeBuilder()
				.withName("targetMappedTransferObjectType")
				.withMapping(newMappingBuilder().withTarget(entityType).withFilter("expr").build()).build();

		OneWayRelationMember oneWayRelationBasic = newOneWayRelationMemberBuilder().withName("transferRelationBasic")
				.withContainment(false).withAggregation(false).withLower(1).withUpper(1)
				.withRelationMemberType(RelationMemberType.RELATION)
				.withTarget(targetEntityType)
				.build();

		OneWayRelationMember oneWayRelationBasicTargetingMapped = newOneWayRelationMemberBuilder()
				.withName("transferRelationBasicTargetingMapped").withContainment(false).withLower(1).withUpper(1)
				.withTarget(targetMappedTransferObjectType)
				.build();

		OneWayRelationMember oneWayRelationWithProperty = newOneWayRelationMemberBuilder()
				.withName("transferRelationWithBinding").withContainment(false).withLower(1).withUpper(1)
				.withRelationMemberType(RelationMemberType.PROPERTY)
				.withGetterExpression("self.oneWayRelationWithProperty.target").withTarget(targetEntityType)
				.build();

		OneWayRelationMember oneWayRelationWithPropertyWithDefault = newOneWayRelationMemberBuilder()
				.withName("transferRelationWithBindingWithSetterWithDefault").withContainment(false).withLower(1)
				.withUpper(1).withRelationMemberType(RelationMemberType.PROPERTY).withGetterExpression("expr")
				.withTarget(targetEntityType).withDefaultExpression("expr").build();

		OneWayRelationMember oneWayRelationWithPropertyWithRange = newOneWayRelationMemberBuilder()
				.withName("transferRelationWithBindingWithSetterWithRange").withContainment(false).withLower(1)
				.withUpper(1).withRelationMemberType(RelationMemberType.PROPERTY).withGetterExpression("expr")
				.withTarget(targetEntityType)
				.withRangeExpression("expr").build();

		OneWayRelationMember oneWayRelationWithPropertyWithDefaultAndRange = newOneWayRelationMemberBuilder()
				.withName("transferRelationWithBindingWithSetterWithDefaultAndRange").withContainment(false)
				.withLower(1).withUpper(1).withRelationMemberType(RelationMemberType.PROPERTY)
				.withGetterExpression("expr").withTarget(targetEntityType).withDefaultExpression("expr")
				.withRangeExpression("expr").build();

		OneWayRelationMember oneWayRelationWithPropertyWithDefaultAndRangeTargetingMapped = newOneWayRelationMemberBuilder()
				.withName("transferRelationWithBindingWithSetterWithDefaultAndRangeTargetingMapped")
				.withContainment(false).withLower(1).withUpper(1).withRelationMemberType(RelationMemberType.PROPERTY)
				.withGetterExpression("expr").withTarget(targetMappedTransferObjectType).withDefaultExpression("expr")
				.withRangeExpression("expr").build();

		// MappedTransferObjectTypes
		TransferObjectType mappedTransferObjectType = newTransferObjectTypeBuilder()
				.withName("mappedTransferObjectType").withMapping(newMappingBuilder().withTarget(entityType).build())
				.withRelations(ImmutableList.of(oneWayRelationBasicTargetingMapped,
						oneWayRelationWithPropertyWithDefaultAndRangeTargetingMapped, oneWayRelationBasic,
						oneWayRelationWithProperty,
						oneWayRelationWithPropertyWithDefaultAndRange,
						oneWayRelationWithPropertyWithDefault, oneWayRelationWithPropertyWithRange))
				.build();
		Package servicePackage = newPackageBuilder().withName("service")
				.withElements(ImmutableList.of(mappedTransferObjectType, targetMappedTransferObjectType)).build();
		Package entitiesPackage = newPackageBuilder().withName("entities")
				.withElements(ImmutableList.of(entityType, targetEntityType)).build();
		Model model = newModelBuilder().withName("Model")
				.withElements(ImmutableList.of(servicePackage, entitiesPackage)).build();

		esmModel.addContent(model);

		transform();

		// Namespaces
		final Optional<hu.blackbelt.judo.meta.psm.namespace.Model> psmModel = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Model.class).filter(m -> model.getName().equals(m.getName()))
						.findAny();
		assertTrue(psmModel.isPresent());

		final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmServicePackage = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Package.class)
						.filter(pkg -> servicePackage.getName().equals(pkg.getName())).findAny();
		assertTrue(psmServicePackage.isPresent());

		final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmEntitiesPackage = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Package.class)
						.filter(pkg -> entitiesPackage.getName().equals(pkg.getName())).findAny();
		assertTrue(psmEntitiesPackage.isPresent());

		final Optional<hu.blackbelt.judo.meta.psm.data.EntityType> psmEntityType = allPsm(
				hu.blackbelt.judo.meta.psm.data.EntityType.class)
						.filter(entity -> entityType.getName().equals(entity.getName())).findAny();
		assertTrue(psmEntityType.isPresent());
		final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmMappedTransferObject = allPsm(
				hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class)
						.filter(mappedTOT -> mappedTransferObjectType.getName().equals(mappedTOT.getName())).findAny();
		assertTrue(psmMappedTransferObject.isPresent());

		assertThat(psmMappedTransferObject.get().getName(), IsEqual.equalTo(mappedTransferObjectType.getName()));
		assertThat(psmMappedTransferObject.get().getNamespace().getName(), IsEqual.equalTo(servicePackage.getName()));

		// relations
		final Optional<hu.blackbelt.judo.meta.psm.data.EntityType> psmTargetEntityType = allPsm(
				hu.blackbelt.judo.meta.psm.data.EntityType.class)
						.filter(entity -> targetEntityType.getName().equals(entity.getName())).findAny();
		assertTrue(psmTargetEntityType.isPresent());

		final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmTargetDefaultMappedTransferObjectType = allPsm(
				hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class)
						.filter(mappedTOT -> (targetEntityType.getName()).equals(mappedTOT.getName()))
						.findAny();
		assertTrue(psmTargetDefaultMappedTransferObjectType.isPresent());
		

		final Optional<TransferObjectRelation> psmTransferObjectRelation = allPsm(TransferObjectRelation.class)
				.filter(transferObjectRel -> oneWayRelationBasic.getName().equals(transferObjectRel.getName()))
				.findAny();
		assertTrue(psmTransferObjectRelation.isPresent());

		assertTrue(psmMappedTransferObject.get().getRelations().contains(psmTransferObjectRelation.get()));
		assertThat(psmTransferObjectRelation.get().getTarget(),
				IsEqual.equalTo(psmTargetDefaultMappedTransferObjectType.get()));
		assertTrue(psmTransferObjectRelation.get().getCardinality().getLower() == 1
				&& psmTransferObjectRelation.get().getCardinality().getUpper() == 1);

		final Optional<TransferObjectRelation> psmTransferObjectRelationWithProperty = allPsm(
				TransferObjectRelation.class).filter(
						transferObjectRel -> oneWayRelationWithProperty.getName().equals(transferObjectRel.getName()))
						.findAny();
		assertTrue(psmTransferObjectRelationWithProperty.isPresent());

		assertTrue(psmMappedTransferObject.get().getRelations().contains(psmTransferObjectRelationWithProperty.get()));
		assertThat(psmTransferObjectRelationWithProperty.get().getTarget(),
				IsEqual.equalTo(psmTargetDefaultMappedTransferObjectType.get()));
		assertTrue(psmTransferObjectRelationWithProperty.get().getCardinality().getLower() == 1
				&& psmTransferObjectRelationWithProperty.get().getCardinality().getUpper() == 1);

		final Optional<TransferObjectRelation> psmTransferObjectRelationWithPropertyWithSetterWithDefaultAndRange = allPsm(
				TransferObjectRelation.class)
						.filter(transferObjectRel -> oneWayRelationWithPropertyWithDefaultAndRange.getName()
								.equals(transferObjectRel.getName()))
						.findAny();
		assertTrue(psmTransferObjectRelationWithPropertyWithSetterWithDefaultAndRange.isPresent());

		assertTrue(psmMappedTransferObject.get().getRelations()
				.contains(psmTransferObjectRelationWithPropertyWithSetterWithDefaultAndRange.get()));
		assertThat(psmTransferObjectRelationWithPropertyWithSetterWithDefaultAndRange.get().getTarget(),
				IsEqual.equalTo(psmTargetDefaultMappedTransferObjectType.get()));
		assertTrue(psmTransferObjectRelationWithPropertyWithSetterWithDefaultAndRange.get().getCardinality()
				.getLower() == 1
				&& psmTransferObjectRelationWithPropertyWithSetterWithDefaultAndRange.get().getCardinality()
						.getUpper() == 1);

		// target: mapped
		final Optional<TransferObjectRelation> psmTransferObjectRelationTargetingMapped = allPsm(
				TransferObjectRelation.class)
						.filter(transferObjectRel -> oneWayRelationBasicTargetingMapped.getName()
								.equals(transferObjectRel.getName()))
						.findAny();
		assertTrue(psmTransferObjectRelationTargetingMapped.isPresent());

		final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmTargetMappedTransferObjectType = allPsm(
				hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class)
						.filter(mappedTOT -> targetMappedTransferObjectType.getName().equals(mappedTOT.getName()))
						.findAny();
		assertTrue(psmMappedTransferObject.isPresent());

		assertTrue(
				psmMappedTransferObject.get().getRelations().contains(psmTransferObjectRelationTargetingMapped.get()));
		assertThat(psmTransferObjectRelationTargetingMapped.get().getTarget(),
				IsEqual.equalTo(psmTargetMappedTransferObjectType.get()));
		assertTrue(psmTransferObjectRelation.get().getCardinality().getLower() == 1
				&& psmTransferObjectRelationTargetingMapped.get().getCardinality().getUpper() == 1);

		final Optional<TransferObjectRelation> psmTransferObjectWithPropertyWithSetterWithDefaultAndRangeTargetingMappedTargetingMapped = allPsm(
				TransferObjectRelation.class).filter(
						transferObjectRel -> oneWayRelationWithPropertyWithDefaultAndRangeTargetingMapped
								.getName().equals(transferObjectRel.getName()))
						.findAny();
		assertTrue(
				psmTransferObjectWithPropertyWithSetterWithDefaultAndRangeTargetingMappedTargetingMapped.isPresent());

		assertThat(psmTransferObjectWithPropertyWithSetterWithDefaultAndRangeTargetingMappedTargetingMapped.get()
				.getTarget(), IsEqual.equalTo(psmTargetMappedTransferObjectType.get()));
		assertTrue(psmTransferObjectWithPropertyWithSetterWithDefaultAndRangeTargetingMappedTargetingMapped.get()
				.getCardinality().getLower() == 1
				&& psmTransferObjectWithPropertyWithSetterWithDefaultAndRangeTargetingMappedTargetingMapped.get()
						.getCardinality().getUpper() == 1);
	}

	@Test
	void testCreateMappedTransferObjectWithDataMembers() throws Exception {
		testName = "CreateMappedTransferObjectWithDataMembers";

		EntityType entityType = newEntityTypeBuilder().withName("entityType").withAbstract_(false).build();
		entityType.setMapping(newMappingBuilder().withTarget(entityType).build());

		// attributes
		StringType stringType = newStringTypeBuilder().withName("stringType").withMaxLength(255).build();

		DataMember dataMemberBasic = newDataMemberBuilder().withName("transferAttributeBasic").withRequired(false)
				.withIdentifier(false).withDataType(stringType)
				.build();
		// property
		DataMember dataMemberWithProperty = newDataMemberBuilder().withName("transferAttributeWithBinding")
				.withRequired(false).withDataMemberType(DataMemberType.PROPERTY).withIdentifier(false)
				.withDataType(stringType).withGetterExpression("expr")
				.build();

		DataMember dataMemberWithPropertyAndDefault = newDataMemberBuilder()
				.withName("transferAttributeWithBindingAndSetterAndDefault").withRequired(false)
				.withDataMemberType(DataMemberType.PROPERTY).withIdentifier(false).withDataType(stringType)
				.withGetterExpression("expr").withDefaultExpression("expr").build();

		TransferObjectType mappedTransferObjectType = newTransferObjectTypeBuilder()
				.withName("mappedTransferObjectType").withMapping(newMappingBuilder().withTarget(entityType).build())
				.withAttributes(ImmutableList.of(dataMemberBasic, dataMemberWithProperty,
						dataMemberWithPropertyAndDefault))
				.build();

		Package servicePackage = newPackageBuilder().withName("service")
				.withElements(ImmutableList.of(mappedTransferObjectType)).build();
		Package entitiesPackage = newPackageBuilder().withName("entities").withElements(ImmutableList.of(entityType))
				.build();
		Package typesPackage = newPackageBuilder().withName("types").withElements(ImmutableList.of(stringType)).build();
		Model model = newModelBuilder().withName("Model")
				.withElements(ImmutableList.of(servicePackage, entitiesPackage, typesPackage)).build();

		esmModel.addContent(model);
		
		transform();

		// Model
		final Optional<hu.blackbelt.judo.meta.psm.namespace.Model> psmModel = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Model.class).filter(m -> model.getName().equals(m.getName()))
						.findAny();
		assertTrue(psmModel.isPresent());
		// service package
		final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmServicePackage = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Package.class)
						.filter(pkg -> servicePackage.getName().equals(pkg.getName())).findAny();
		assertTrue(psmServicePackage.isPresent());
		// entities package
		final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmEntitiesPackage = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Package.class)
						.filter(pkg -> entitiesPackage.getName().equals(pkg.getName())).findAny();
		assertTrue(psmEntitiesPackage.isPresent());
		// types package
		final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmTypesPackage = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Package.class)
						.filter(pkg -> typesPackage.getName().equals(pkg.getName())).findAny();
		assertTrue(psmTypesPackage.isPresent());
		// entityType
		final Optional<hu.blackbelt.judo.meta.psm.data.EntityType> psmEntityType = allPsm(
				hu.blackbelt.judo.meta.psm.data.EntityType.class)
						.filter(entity -> entityType.getName().equals(entity.getName())).findAny();
		assertTrue(psmEntityType.isPresent());
		// entityType
		final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmMappedTransferObject = allPsm(
				hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class)
						.filter(mappedTOT -> (mappedTransferObjectType.getName()).equals(mappedTOT.getName()))
						.findAny();
		assertTrue(psmMappedTransferObject.isPresent());

		assertThat(psmMappedTransferObject.get().getName(), IsEqual.equalTo(mappedTransferObjectType.getName()));
		assertThat(psmMappedTransferObject.get().getNamespace().getName(), IsEqual.equalTo(servicePackage.getName()));

		// attributes
		final Optional<TransferAttribute> transferAttributeBasic = allPsm(TransferAttribute.class)
				.filter(transferAttr -> dataMemberBasic.getName().equals(transferAttr.getName())).findAny();
		assertTrue(transferAttributeBasic.isPresent());
		assertTrue(psmMappedTransferObject.get().getAttributes().contains(transferAttributeBasic.get()));

		// with property
		final Optional<TransferAttribute> transferAttributeAttributeWithProperty = allPsm(TransferAttribute.class)
				.filter(transferAttr -> dataMemberWithProperty.getName().equals(transferAttr.getName())).findAny();
		assertTrue(transferAttributeAttributeWithProperty.isPresent());
		assertTrue(
				psmMappedTransferObject.get().getAttributes().contains(transferAttributeAttributeWithProperty.get()));

		final Optional<TransferAttribute> transferAttributeWithPropertyAndSetterAndDefault = allPsm(
				TransferAttribute.class)
						.filter(transferAttr -> dataMemberWithPropertyAndDefault.getName()
								.equals(transferAttr.getName()))
						.findAny();
		assertTrue(transferAttributeWithPropertyAndSetterAndDefault.isPresent());
		assertTrue(psmMappedTransferObject.get().getAttributes()
				.contains(transferAttributeWithPropertyAndSetterAndDefault.get()));
	}

	@Test
	void testDefaultTransferObjectInheritance() throws Exception {
		testName = "DefaultTransferObjectInheritance";
		
		EntityType grandparentEntityType = newEntityTypeBuilder().withName("grandparentEntityType").withAbstract_(false)
				.build();
		grandparentEntityType.setMapping(newMappingBuilder().withTarget(grandparentEntityType).build());

		EntityType parentEntityType = newEntityTypeBuilder().withName("parentEntityType").withAbstract_(false)
				.withGeneralizations(newGeneralizationBuilder().withTarget(grandparentEntityType).build()).build();
		parentEntityType.setMapping(newMappingBuilder().withTarget(parentEntityType).build());

		EntityType childEntityType = newEntityTypeBuilder().withName("childEntityType").withAbstract_(false)
				.withGeneralizations(newGeneralizationBuilder().withTarget(parentEntityType).build()).build();
		childEntityType.setMapping(newMappingBuilder().withTarget(childEntityType).build());

		Package entitiesPackage = newPackageBuilder().withName("entities")
				.withElements(ImmutableList.of(grandparentEntityType, parentEntityType, childEntityType)).build();
		Model model = newModelBuilder().withName("Model").withElements(ImmutableList.of(entitiesPackage)).build();

		esmModel.addContent(model);

		transform();

		final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmDefaultGrandparentTransferObject = allPsm(
				hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class)
						.filter(mappedTOT -> (grandparentEntityType.getName()).equals(mappedTOT.getName()))
						.findAny();
		assertTrue(psmDefaultGrandparentTransferObject.isPresent());
		assertThat(psmDefaultGrandparentTransferObject.get().getName(),
				IsEqual.equalTo(grandparentEntityType.getName()));

		final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmDefaultParentTransferObject = allPsm(
				hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class)
						.filter(mappedTOT -> (parentEntityType.getName()).equals(mappedTOT.getName()))
						.findAny();
		assertTrue(psmDefaultParentTransferObject.isPresent());
		assertThat(psmDefaultParentTransferObject.get().getName(), IsEqual.equalTo(parentEntityType.getName()));

		final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmDefaultChildTransferObject = allPsm(
				hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class)
						.filter(mappedTOT -> (childEntityType.getName()).equals(mappedTOT.getName()))
						.findAny();
		assertTrue(psmDefaultChildTransferObject.isPresent());
		assertThat(psmDefaultChildTransferObject.get().getName(), IsEqual.equalTo(childEntityType.getName()));

		assertTrue(psmDefaultParentTransferObject.get().getSuperTransferObjectTypes()
				.contains(psmDefaultGrandparentTransferObject.get()));
		assertTrue(psmDefaultChildTransferObject.get().getSuperTransferObjectTypes()
				.contains(psmDefaultParentTransferObject.get()));
		assertTrue(psmDefaultChildTransferObject.get().getAllSuperTransferObjectTypes()
				.contains(psmDefaultGrandparentTransferObject.get()));
	}

	@Test
	void testTransferObjectInheritance() throws Exception {
		testName = "TransferObjectInheritance";
		EntityType entityType = newEntityTypeBuilder().withName("entityType").withAbstract_(false).build();
		entityType.setMapping(newMappingBuilder().withTarget(entityType).build());

		StringType stringType = newStringTypeBuilder().withName("stringType").withMaxLength(255).build();

		// grandparent
		DataMember dataMemberOfGrandparent = newDataMemberBuilder().withName("inheritedAttributeFromGrandparent")
				.withRequired(false).withIdentifier(false).withDataType(stringType)

				.build();

		TransferObjectType grandparentMappedTransferObjectType = newTransferObjectTypeBuilder()
				.withName("grandparentMappedTransferObjectType")
				.withMapping(newMappingBuilder().withTarget(entityType).build()).withAttributes(dataMemberOfGrandparent)
				.build();

		// parent
		DataMember dataMemberOfParent = newDataMemberBuilder().withName("inheritedAttributeFromParent")
				.withRequired(false).withIdentifier(false).withDataType(stringType)

				.build();

		TransferObjectType parentMappedTransferObjectType = newTransferObjectTypeBuilder()
				.withName("parentMappedTransferObjectType")
				.withMapping(newMappingBuilder().withTarget(entityType).build()).withAttributes(dataMemberOfParent)
				.withGeneralizations(newGeneralizationBuilder().withTarget(grandparentMappedTransferObjectType).build())
				.build();

		// child
		TransferObjectType childMappedTransferObjectType = newTransferObjectTypeBuilder()
				.withName("childMappedTransferObjectType")
				.withMapping(newMappingBuilder().withTarget(entityType).build())
				.withGeneralizations(newGeneralizationBuilder().withTarget(parentMappedTransferObjectType).build())
				.build();
		TransferObjectType secondChildMappedTransferObjectType = newTransferObjectTypeBuilder()
				.withName("secondChildMappedTransferObjectType")
				.withMapping(newMappingBuilder().withTarget(entityType).build())
				.withGeneralizations(newGeneralizationBuilder().withTarget(parentMappedTransferObjectType).build())
				.build();

		Package servicePackage = newPackageBuilder().withName("service")
				.withElements(ImmutableList.of(parentMappedTransferObjectType, grandparentMappedTransferObjectType,
						childMappedTransferObjectType, secondChildMappedTransferObjectType))
				.build();
		Package entitiesPackage = newPackageBuilder().withName("entities").withElements(ImmutableList.of(entityType))
				.build();
		Package typesPackage = newPackageBuilder().withName("types").withElements(ImmutableList.of(stringType)).build();
		Model model = newModelBuilder().withName("Model")
				.withElements(ImmutableList.of(servicePackage, entitiesPackage, typesPackage)).build();

		esmModel.addContent(model);
		transform();

		// Namespaces
		final Optional<hu.blackbelt.judo.meta.psm.namespace.Model> psmModel = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Model.class).filter(m -> model.getName().equals(m.getName()))
						.findAny();
		assertTrue(psmModel.isPresent());

		final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmServicePackage = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Package.class)
						.filter(pkg -> servicePackage.getName().equals(pkg.getName())).findAny();
		assertTrue(psmServicePackage.isPresent());

		final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmEntitiesPackage = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Package.class)
						.filter(pkg -> entitiesPackage.getName().equals(pkg.getName())).findAny();
		assertTrue(psmEntitiesPackage.isPresent());

		final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmTypesPackage = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Package.class)
						.filter(pkg -> typesPackage.getName().equals(pkg.getName())).findAny();
		assertTrue(psmTypesPackage.isPresent());

		final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmGrandparentMappedTransferObjectType = allPsm(
				hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class)
						.filter(mappedTOT -> grandparentMappedTransferObjectType.getName().equals(mappedTOT.getName()))
						.findAny();
		assertTrue(psmGrandparentMappedTransferObjectType.isPresent());
		final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmParentMappedTransferObjectType = allPsm(
				hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class)
						.filter(mappedTOT -> parentMappedTransferObjectType.getName().equals(mappedTOT.getName()))
						.findAny();
		assertTrue(psmParentMappedTransferObjectType.isPresent());
		final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmChildMappedTransferObjectType = allPsm(
				hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class)
						.filter(mappedTOT -> childMappedTransferObjectType.getName().equals(mappedTOT.getName()))
						.findAny();
		assertTrue(psmChildMappedTransferObjectType.isPresent());
		final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmSecondChildMappedTransferObjectType = allPsm(
				hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class)
						.filter(mappedTOT -> secondChildMappedTransferObjectType.getName().equals(mappedTOT.getName()))
						.findAny();
		assertTrue(psmSecondChildMappedTransferObjectType.isPresent());

		assertTrue(psmParentMappedTransferObjectType.get().getInheritedTransferAttributeNames()
				.contains("inheritedAttributeFromGrandparent"));

		assertTrue(psmChildMappedTransferObjectType.get().getInheritedTransferAttributeNames()
				.contains("inheritedAttributeFromGrandparent"));
		assertTrue(psmChildMappedTransferObjectType.get().getInheritedTransferAttributeNames()
				.contains("inheritedAttributeFromParent"));

		assertTrue(psmSecondChildMappedTransferObjectType.get().getInheritedTransferAttributeNames()
				.contains("inheritedAttributeFromGrandparent"));
		assertTrue(psmSecondChildMappedTransferObjectType.get().getInheritedTransferAttributeNames()
				.contains("inheritedAttributeFromParent"));
	}

	@Test
	void testCreateTransferRelationsFromTwoWayEndpoints() throws Exception {
		testName = "CreateTransferRelationsFromTwoWayEndpoints";

		TwoWayRelationMember twoWayRelationMemberA = newTwoWayRelationMemberBuilder().withName("twoWayRelationMemberA")
				.withLower(1).withUpper(1)
				.build();

		TwoWayRelationMember twoWayRelationMemberB = newTwoWayRelationMemberBuilder().withName("twoWayRelationMemberB")
				.withLower(1).withUpper(1)
				.build();

		twoWayRelationMemberA.setPartner(twoWayRelationMemberB);
		twoWayRelationMemberB.setPartner(twoWayRelationMemberA);

		EntityType entityTypeA = newEntityTypeBuilder().withName("EntityTypeA")
				.withRelations(ImmutableList.of(twoWayRelationMemberA)).build();
		entityTypeA.setMapping(newMappingBuilder().withTarget(entityTypeA).build());

		EntityType entityTypeB = newEntityTypeBuilder().withName("EntityTypeB")
				.withRelations(ImmutableList.of(twoWayRelationMemberB)).build();
		entityTypeB.setMapping(newMappingBuilder().withTarget(entityTypeB).build());

		twoWayRelationMemberA.setTarget(entityTypeB);
		twoWayRelationMemberB.setTarget(entityTypeA);

		Package entitiesPackage = newPackageBuilder().withName("entities")
				.withElements(ImmutableList.of(entityTypeA, entityTypeB)).build();
		Model model = newModelBuilder().withName("Model").withElements(ImmutableList.of(entitiesPackage)).build();

		esmModel.addContent(model);
		transform();

		final Optional<hu.blackbelt.judo.meta.psm.namespace.Model> psmModel = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Model.class).filter(m -> model.getName().equals(m.getName()))
						.findAny();
		assertTrue(psmModel.isPresent());
		final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmEntitiesPackage = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Package.class)
						.filter(pkg -> entitiesPackage.getName().equals(pkg.getName())).findAny();
		assertTrue(psmEntitiesPackage.isPresent());

		final Optional<hu.blackbelt.judo.meta.psm.data.EntityType> psmEntityTypeA = allPsm(
				hu.blackbelt.judo.meta.psm.data.EntityType.class)
						.filter(entity -> entityTypeA.getName().equals(entity.getName())).findAny();
		assertTrue(psmEntityTypeA.isPresent());
		final Optional<hu.blackbelt.judo.meta.psm.data.EntityType> psmEntityTypeB = allPsm(
				hu.blackbelt.judo.meta.psm.data.EntityType.class)
						.filter(entity -> entityTypeB.getName().equals(entity.getName())).findAny();
		assertTrue(psmEntityTypeB.isPresent());

		final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmDefaultTransferObjectA = allPsm(
				hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class)
						.filter(mappedTOT -> (entityTypeA.getName()).equals(mappedTOT.getName()))
						.findAny();
		assertTrue(psmDefaultTransferObjectA.isPresent());
		final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmDefaultTransferObjectB = allPsm(
				hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class)
						.filter(mappedTOT -> (entityTypeB.getName()).equals(mappedTOT.getName()))
						.findAny();
		assertTrue(psmDefaultTransferObjectB.isPresent());

		final Optional<TransferObjectRelation> transferObjectRelationA = allPsm(TransferObjectRelation.class)
				.filter(transferRel -> twoWayRelationMemberA.getName().equals(transferRel.getName())).findAny();
		assertTrue(transferObjectRelationA.isPresent());
		assertTrue(psmDefaultTransferObjectA.get().getRelations().contains(transferObjectRelationA.get()));

		final Optional<TransferObjectRelation> transferObjectRelationB = allPsm(TransferObjectRelation.class)
				.filter(transferRel -> twoWayRelationMemberB.getName().equals(transferRel.getName())).findAny();
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
