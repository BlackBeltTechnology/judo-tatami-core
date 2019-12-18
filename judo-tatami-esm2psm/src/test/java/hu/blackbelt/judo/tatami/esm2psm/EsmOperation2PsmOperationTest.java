package hu.blackbelt.judo.tatami.esm2psm;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.namespace.Model;
import hu.blackbelt.judo.meta.esm.namespace.Package;
import hu.blackbelt.judo.meta.esm.operation.BoundOperation;
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
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static hu.blackbelt.judo.meta.esm.namespace.util.builder.NamespaceBuilders.newModelBuilder;
import static hu.blackbelt.judo.meta.esm.namespace.util.builder.NamespaceBuilders.newPackageBuilder;
import static hu.blackbelt.judo.meta.esm.operation.util.builder.OperationBuilders.newBoundOperationBuilder;
import static hu.blackbelt.judo.meta.esm.operation.util.builder.OperationBuilders.newParameterBuilder;
import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.buildEsmModel;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.*;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.SaveArguments.psmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.buildPsmModel;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.calculateEsm2PsmTransformationScriptURI;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.executeEsm2PsmTransformation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class EsmOperation2PsmOperationTest {

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
	void testCreateBoundOperation() throws Exception {
		testName = "CreateBoundOperation";

		EntityType entityType = newEntityTypeBuilder().withName("entityType").withAbstract_(false).build();
		entityType.setMapping(newMappingBuilder().withTarget(entityType).build());
		
		EntityType targetEntityType = newEntityTypeBuilder().withName("targetEntityType").withAbstract_(false).build();
		targetEntityType.setMapping(newMappingBuilder().withTarget(targetEntityType).build());
		
		TransferObjectType targetMappedTransferObjectType = newTransferObjectTypeBuilder()
				.withName("targetMappedTransferObjectType")
				.withMapping(newMappingBuilder().withTarget(targetEntityType).build()).build();

		// bound operations
		BoundOperation boundOperation = newBoundOperationBuilder().withName("boundOperation").withBody("body")
				.withAbstract_(false)
				.withInput(newParameterBuilder().withName("inputParameter").withLower(1).withUpper(1)
						.withTarget(targetMappedTransferObjectType).build())
				.withOutput(newParameterBuilder().withName("outputParameter").withLower(1).withUpper(1)
						.withTarget(targetMappedTransferObjectType).build())
				.withFaults(newParameterBuilder().withName("faultParameter").withLower(1).withUpper(1)
						.withTarget(targetMappedTransferObjectType).build())
				.withCustomImplementation(false).build();

		// MappedTransferObjectTypes
		TransferObjectType mappedTransferObjectType = newTransferObjectTypeBuilder()
				.withName("mappedTransferObjectType").withMapping(newMappingBuilder().withTarget(entityType).build())
				.withOperations(boundOperation).build();

		Package servicePackage = newPackageBuilder().withName("service")
				.withElements(ImmutableList.of(mappedTransferObjectType, targetMappedTransferObjectType)).build();
		Package entitiesPackage = newPackageBuilder().withName("entities")
				.withElements(ImmutableList.of(entityType, targetEntityType)).build();
		Model model = newModelBuilder().withName("Model")
				.withElements(ImmutableList.of(servicePackage, entitiesPackage)).build();

		esmModel.addContent(model);
		
		transform();

		final Optional<hu.blackbelt.judo.meta.psm.namespace.Model> psmModel = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Model.class).filter(m -> "Model".equals(m.getName())).findAny();
		assertTrue(psmModel.isPresent());
		final Optional<hu.blackbelt.judo.meta.psm.namespace.Package> psmServicePackage = allPsm(
				hu.blackbelt.judo.meta.psm.namespace.Package.class).filter(pkg -> "service".equals(pkg.getName()))
						.findAny();
		assertTrue(psmServicePackage.isPresent());

		// MappedTransferObjectType
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

		// --------- Bound Operation
		final Optional<hu.blackbelt.judo.meta.psm.service.BoundOperation> psmBoundOperation = allPsm(
				hu.blackbelt.judo.meta.psm.service.BoundOperation.class)
						.filter(boundOp -> "boundOperation".equals(boundOp.getName())).findAny();
		assertTrue(psmBoundOperation.isPresent());
		assertThat(psmBoundOperation.get().eContainer(), IsEqual.equalTo(psmMappedTransferObject.get()));
		assertTrue(psmBoundOperation.get().getInput().getCardinality().getLower() == 1
				&& psmBoundOperation.get().getInput().getCardinality().getUpper() == 1);
		assertTrue(psmBoundOperation.get().getOutput().getCardinality().getLower() == 1
				&& psmBoundOperation.get().getOutput().getCardinality().getUpper() == 1);

		// --------- Parameter
		final Optional<hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType> psmTargetMappedTransferObject = allPsm(
				hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType.class)
						.filter(mappedTOT -> "targetMappedTransferObjectType".equals(mappedTOT.getName())).findAny();
		assertTrue(psmTargetMappedTransferObject.isPresent());

		// input
		final Optional<hu.blackbelt.judo.meta.psm.service.Parameter> psmInputParameter = allPsm(
				hu.blackbelt.judo.meta.psm.service.Parameter.class)
						.filter(param -> "inputParameter".equals(param.getName())).findAny();
		assertTrue(psmInputParameter.isPresent());
		assertThat(psmBoundOperation.get().getInput(), IsEqual.equalTo(psmInputParameter.get()));
		assertTrue(psmInputParameter.get().getCardinality().getLower() == 1);
		assertTrue(psmInputParameter.get().getCardinality().getUpper() == 1);
		assertTrue(psmInputParameter.get().getType().equals(psmTargetMappedTransferObject.get()));

		// output
		final Optional<hu.blackbelt.judo.meta.psm.service.Parameter> psmOutputParameter = allPsm(
				hu.blackbelt.judo.meta.psm.service.Parameter.class)
						.filter(param -> "outputParameter".equals(param.getName())).findAny();
		assertTrue(psmOutputParameter.isPresent());
		assertThat(psmBoundOperation.get().getOutput(), IsEqual.equalTo(psmOutputParameter.get()));
		assertTrue(psmOutputParameter.get().getCardinality().getLower() == 1);
		assertTrue(psmOutputParameter.get().getCardinality().getUpper() == 1);
		assertTrue(psmOutputParameter.get().getType().equals(psmTargetMappedTransferObject.get()));

		// fault
		final Optional<hu.blackbelt.judo.meta.psm.service.Parameter> psmFaultParameter = allPsm(
				hu.blackbelt.judo.meta.psm.service.Parameter.class)
						.filter(param -> "faultParameter".equals(param.getName())).findAny();
		assertTrue(psmFaultParameter.isPresent());
		assertTrue(psmBoundOperation.get().getFaults().contains(psmFaultParameter.get()));
		assertTrue(psmFaultParameter.get().getCardinality().getLower() == 1);
		assertTrue(psmFaultParameter.get().getCardinality().getUpper() == 1);
		assertTrue(psmFaultParameter.get().getType().equals(psmTargetMappedTransferObject.get()));
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
