package hu.blackbelt.judo.tatami.esm2psm;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.accesspoint.AccessPoint;
import hu.blackbelt.judo.meta.esm.accesspoint.ExposedGraph;
import hu.blackbelt.judo.meta.esm.accesspoint.ExposedService;
import hu.blackbelt.judo.meta.esm.namespace.Model;
import hu.blackbelt.judo.meta.esm.namespace.Package;
import hu.blackbelt.judo.meta.esm.operation.BoundOperation;
import hu.blackbelt.judo.meta.esm.operation.UnboundOperation;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.esm.structure.EntityType;
import hu.blackbelt.judo.meta.esm.structure.TransferObjectType;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static hu.blackbelt.judo.meta.esm.accesspoint.util.builder.AccesspointBuilders.*;
import static hu.blackbelt.judo.meta.esm.namespace.util.builder.NamespaceBuilders.newModelBuilder;
import static hu.blackbelt.judo.meta.esm.namespace.util.builder.NamespaceBuilders.newPackageBuilder;
import static hu.blackbelt.judo.meta.esm.operation.util.builder.OperationBuilders.*;
import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.buildEsmModel;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.*;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.SaveArguments.psmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.buildPsmModel;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.calculateEsm2PsmTransformationScriptURI;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.executeEsm2PsmTransformation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class EsmAccesspoint2PsmAccesspointTest {

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
	void testCreateExposedService() throws Exception {
		testName = "CreateExposedService";
		
		EntityType entityType = newEntityTypeBuilder().withName("entityType").withAbstract_(false).build();
		entityType.setMapping(newMappingBuilder().withTarget(entityType).build());
		
		TransferObjectType mappedTransferObjectType = newTransferObjectTypeBuilder()
				.withName("mappedTransferObjectType").withMapping(newMappingBuilder().withTarget(entityType).build())
				.build();
		UnboundOperation unboundOperation = newUnboundOperationBuilder().withName("unboundOperation")
				.withCustomImplementation(true)
				.withInput(newParameterBuilder().withName("inputParameter").withTarget(mappedTransferObjectType)
						.withLower(1).withUpper(1).build())
				.withOutput(newParameterBuilder().withName("outputParameter").withTarget(mappedTransferObjectType)
						.withLower(1).withUpper(1).build())
				.build();
		ExposedService exposedService = newExposedServiceBuilder().withOperation(unboundOperation).build();
		AccessPoint accessPoint = newAccessPointBuilder().withExposedServices(exposedService).withName("accessPoint")
				.build();

		Package servicePackage = newPackageBuilder().withName("service")
				.withElements(ImmutableList.of(mappedTransferObjectType, unboundOperation)).build();
		Package entitiesPackage = newPackageBuilder().withName("entities").withElements(ImmutableList.of(entityType))
				.build();
		Model model = newModelBuilder().withName("Model")
				.withElements(ImmutableList.of(servicePackage, entitiesPackage, accessPoint)).build();

		esmModel.addContent(model);
		
		transform();

		final Optional<hu.blackbelt.judo.meta.psm.namespace.Model> psmModel = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Model.class).filter(m -> "Model".equals(m.getName())).findAny();
		assertTrue(psmModel.isPresent());
		final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmServicePackage = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Package.class).filter(pkg -> "service".equals(pkg.getName()))
						.findAny();
		assertTrue(psmServicePackage.isPresent());

		// I - MappedTransferObjectTypes
		final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmEntitiesPackage = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Package.class).filter(pkg -> "entities".equals(pkg.getName()))
						.findAny();
		assertTrue(psmEntitiesPackage.isPresent());
		final Optional<hu.blackbelt.judo.meta.psm.data.EntityType> psmEntityType = allPsm(
				hu.blackbelt.judo.meta.psm.data.EntityType.class)
						.filter(entity -> "entityType".equals(entity.getName())).findAny();
		assertTrue(psmEntityType.isPresent());
		final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmMappedTransferObject = allPsm(
				hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class)
						.filter(mappedTOT -> "mappedTransferObjectType".equals(mappedTOT.getName())).findAny();
		assertTrue(psmMappedTransferObject.isPresent());

		assertThat(psmMappedTransferObject.get().getName(), IsEqual.equalTo(mappedTransferObjectType.getName()));
		assertThat(psmMappedTransferObject.get().getNamespace().getName(),
				IsEqual.equalTo(psmServicePackage.get().getName()));

		final Optional<hu.blackbelt.judo.meta.psm.service.UnboundOperation> psmUnboundOperation = allPsm(
				hu.blackbelt.judo.meta.psm.service.UnboundOperation.class)
						.filter(unboundOp -> "unboundOperation".equals(unboundOp.getName())).findAny();
		assertTrue(psmUnboundOperation.isPresent());

		final Optional<hu.blackbelt.judo.meta.psm.accesspoint.ExposedService> psmExposedService = allPsm(
				hu.blackbelt.judo.meta.psm.accesspoint.ExposedService.class)
						.filter(exposedSrvc -> psmUnboundOperation.get().equals(exposedSrvc.getOperation())).findAny();
		assertTrue(psmExposedService.isPresent());

		final Optional<hu.blackbelt.judo.meta.psm.accesspoint.AccessPoint> psmAccessPoint = allPsm(
				hu.blackbelt.judo.meta.psm.accesspoint.AccessPoint.class)
						.filter(ap -> "accessPoint".equals(ap.getName())).findAny();
		assertTrue(psmAccessPoint.isPresent());

		assertTrue(psmAccessPoint.get().getExposedServices().contains(psmExposedService.get()));
	}

	@Test
	@Disabled
	public void testCreateExposedGraph() throws Exception {
		testName = "CreateExposedGraph";

		EntityType targetEntityType = newEntityTypeBuilder().withName("targetEntityType").withAbstract_(false).build();
		targetEntityType.setMapping(newMappingBuilder().withTarget(targetEntityType).build());
		
		TransferObjectType targetMappedTransferObjectType = newTransferObjectTypeBuilder()
				.withName("targetMappedTransferObjectType")
				.withMapping(newMappingBuilder().withTarget(targetEntityType).build()).build();

		BoundOperation boundOperation = newBoundOperationBuilder().withName("boundOperation").withBody("body")
				.withAbstract_(false)
				.withInput(newParameterBuilder().withName("inputParameter").withLower(1).withUpper(1)
						.withTarget(targetMappedTransferObjectType).build())
				.withOutput(newParameterBuilder().withName("outputParameter").withLower(1).withUpper(1)
						.withTarget(targetMappedTransferObjectType).build())
				.withCustomImplementation(false).build();

		EntityType entityType = newEntityTypeBuilder().withName("entityType").withAbstract_(false).build();
		entityType.setMapping(newMappingBuilder().withTarget(entityType).build());

		TransferObjectType mappedTransferObjectType = newTransferObjectTypeBuilder()
				.withName("mappedTransferObjectType").withMapping(newMappingBuilder().withTarget(entityType).build())
				.withOperations(boundOperation).build();

		ExposedGraph exposedGraph = newExposedGraphBuilder().withName("exposedGraph").withGetterExpression("exp")
				.withLower(1).withUpper(1).withTarget(mappedTransferObjectType)
				.withRepresentation(mappedTransferObjectType).build();
		
		ExposedGraph exposedGraph2 = newExposedGraphBuilder().withName("exposedGraph2").withGetterExpression("exp")
				.withLower(1).withUpper(1).withTarget(mappedTransferObjectType)
				.withRepresentation(mappedTransferObjectType)
				.withDeleteable(false)
				.withCreateable(false)
				.withUpdateable(false)
				.build();

		AccessPoint accessPoint = newAccessPointBuilder().withExposedGraphs(ImmutableList.of(exposedGraph,exposedGraph2)).withName("accessPoint")
				.build();

		Package servicePackage = newPackageBuilder().withName("service")
				.withElements(ImmutableList.of(mappedTransferObjectType, targetMappedTransferObjectType)).build();
		Package entitiesPackage = newPackageBuilder().withName("entities")
				.withElements(ImmutableList.of(entityType, targetEntityType)).build();
		Model model = newModelBuilder().withName("Model")
				.withElements(ImmutableList.of(servicePackage, entitiesPackage, accessPoint)).build();

		esmModel.addContent(model);

		transform();

		final Optional<hu.blackbelt.judo.meta.psm.namespace.Model> psmModel = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Model.class).filter(m -> "Model".equals(m.getName())).findAny();
		assertTrue(psmModel.isPresent());
		final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmServicePackage = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Package.class).filter(pkg -> "service".equals(pkg.getName()))
						.findAny();
		assertTrue(psmServicePackage.isPresent());

		final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmEntitiesPackage = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Package.class).filter(pkg -> "entities".equals(pkg.getName()))
						.findAny();
		assertTrue(psmEntitiesPackage.isPresent());
		final Optional<hu.blackbelt.judo.meta.psm.data.EntityType> psmEntityType = allPsm(
				hu.blackbelt.judo.meta.psm.data.EntityType.class)
						.filter(entity -> "entityType".equals(entity.getName())).findAny();
		assertTrue(psmEntityType.isPresent());
		final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmMappedTransferObject = allPsm(
				hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class)
						.filter(mappedTOT -> "mappedTransferObjectType".equals(mappedTOT.getName())).findAny();
		assertTrue(psmMappedTransferObject.isPresent());

		assertThat(psmMappedTransferObject.get().getName(), IsEqual.equalTo(mappedTransferObjectType.getName()));
		assertThat(psmMappedTransferObject.get().getNamespace().getName(),
				IsEqual.equalTo(psmServicePackage.get().getName()));

		final Optional<hu.blackbelt.judo.meta.psm.accesspoint.AccessPoint> psmAccessPoint = allPsm(
				hu.blackbelt.judo.meta.psm.accesspoint.AccessPoint.class)
						.filter(ap -> "accessPoint".equals(ap.getName())).findAny();
		assertTrue(psmAccessPoint.isPresent());

		final Optional<hu.blackbelt.judo.meta.psm.accesspoint.ExposedGraph> psmExposedGraph = allPsm(
				hu.blackbelt.judo.meta.psm.accesspoint.ExposedGraph.class).filter(ep -> exposedGraph.getName().equals(ep.getName())).findAny();
		assertTrue(psmExposedGraph.isPresent());

		assertTrue(psmAccessPoint.get().getExposedGraphs().contains(psmExposedGraph.get()));
		
		final Optional<hu.blackbelt.judo.meta.psm.accesspoint.ExposedGraph> psmExposedGraph2 = allPsm(
				hu.blackbelt.judo.meta.psm.accesspoint.ExposedGraph.class).filter(ep -> exposedGraph2.getName().equals(ep.getName())).findAny();
		assertTrue(psmExposedGraph2.isPresent());

		assertTrue(psmAccessPoint.get().getExposedGraphs().contains(psmExposedGraph2.get()));
		
		assertNull(psmExposedGraph2.get().getCreate());
		assertNull(psmExposedGraph2.get().getUpdate());
		assertNull(psmExposedGraph2.get().getDelete());
		
		//test get operation
		final Optional<hu.blackbelt.judo.meta.psm.service.UnboundOperation> psmGetOperation = allPsm(
				hu.blackbelt.judo.meta.psm.service.UnboundOperation.class).filter(op -> "get".equals(op.getName()))
						.findAny();
		assertTrue(psmGetOperation.isPresent());
		assertThat(psmExposedGraph.get().getGet(), IsEqual.equalTo(psmGetOperation.get()));
		assertNull(psmGetOperation.get().getInput());
		assertThat(psmGetOperation.get().getOutput().getName(), IsEqual.equalTo("output"));
		assertEquals(psmGetOperation.get().getOutput().getCardinality().getLower(),psmExposedGraph.get().getCardinality().getLower());
		assertEquals(psmGetOperation.get().getOutput().getCardinality().getUpper(),psmExposedGraph.get().getCardinality().getUpper());
		assertThat(psmGetOperation.get().getOutput().getType(), IsEqual.equalTo(psmExposedGraph.get().getMappedTransferObjectType()));
		
		//test create operation
		final Optional<hu.blackbelt.judo.meta.psm.service.UnboundOperation> psmCreateOperation = allPsm(
				hu.blackbelt.judo.meta.psm.service.UnboundOperation.class).filter(op -> "create".equals(op.getName()))
						.findAny();
		assertTrue(psmCreateOperation.isPresent());
		assertThat(psmExposedGraph.get().getCreate(), IsEqual.equalTo(psmCreateOperation.get()));
		
		assertThat(psmCreateOperation.get().getInput().getName(), IsEqual.equalTo("input"));
		assertEquals(psmCreateOperation.get().getInput().getCardinality().getLower(),1);
		assertEquals(psmCreateOperation.get().getInput().getCardinality().getUpper(),1);
		assertThat(psmCreateOperation.get().getInput().getType(), IsEqual.equalTo(psmExposedGraph.get().getMappedTransferObjectType()));
		
		assertThat(psmCreateOperation.get().getOutput().getName(), IsEqual.equalTo("output"));
		assertEquals(psmCreateOperation.get().getOutput().getCardinality().getLower(),1);
		assertEquals(psmCreateOperation.get().getOutput().getCardinality().getUpper(),1);
		assertThat(psmCreateOperation.get().getOutput().getType(), IsEqual.equalTo(psmExposedGraph.get().getMappedTransferObjectType()));
		
		//test update operation
		final Optional<hu.blackbelt.judo.meta.psm.service.BoundOperation> psmUpdateOperation = allPsm(
				hu.blackbelt.judo.meta.psm.service.BoundOperation.class).filter(op -> "update".equals(op.getName()))
						.findAny();
		assertTrue(psmUpdateOperation.isPresent());
		assertThat(psmExposedGraph.get().getUpdate(), IsEqual.equalTo(psmUpdateOperation.get()));
		
		assertThat(psmUpdateOperation.get().getInput().getName(), IsEqual.equalTo("input"));
		assertEquals(psmUpdateOperation.get().getInput().getCardinality().getLower(),1);
		assertEquals(psmUpdateOperation.get().getInput().getCardinality().getUpper(),1);
		assertThat(psmUpdateOperation.get().getInput().getType(), IsEqual.equalTo(psmExposedGraph.get().getMappedTransferObjectType()));
		
		assertThat(psmUpdateOperation.get().getOutput().getName(), IsEqual.equalTo("output"));
		assertEquals(psmUpdateOperation.get().getOutput().getCardinality().getLower(),1);
		assertEquals(psmUpdateOperation.get().getOutput().getCardinality().getUpper(),1);
		assertThat(psmUpdateOperation.get().getOutput().getType(), IsEqual.equalTo(psmExposedGraph.get().getMappedTransferObjectType()));
		
		//test delete operation
		final Optional<hu.blackbelt.judo.meta.psm.service.BoundOperation> psmDeleteOperation = allPsm(
				hu.blackbelt.judo.meta.psm.service.BoundOperation.class).filter(op -> "delete".equals(op.getName()))
						.findAny();
		assertTrue(psmDeleteOperation.isPresent());
		assertThat(psmExposedGraph.get().getDelete(), IsEqual.equalTo(psmDeleteOperation.get()));
		
		assertNull(psmDeleteOperation.get().getInput());
		assertNull(psmDeleteOperation.get().getOutput());
		
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
