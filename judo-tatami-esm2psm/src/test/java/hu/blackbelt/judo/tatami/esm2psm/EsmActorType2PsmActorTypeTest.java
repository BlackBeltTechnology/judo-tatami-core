package hu.blackbelt.judo.tatami.esm2psm;

import static hu.blackbelt.judo.meta.esm.accesspoint.util.builder.AccesspointBuilders.newActorTypeBuilder;
import static hu.blackbelt.judo.meta.esm.namespace.util.builder.NamespaceBuilders.newModelBuilder;
import static hu.blackbelt.judo.meta.esm.operation.util.builder.OperationBuilders.newOperationBuilder;
import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.buildEsmModel;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.newEntityTypeBuilder;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.newTransferObjectTypeBuilder;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.calculatePsmValidationScriptURI;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.validatePsm;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.buildPsmModel;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.SaveArguments.psmSaveArgumentsBuilder;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.calculateEsm2PsmTransformationScriptURI;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.executeEsm2PsmTransformation;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.accesspoint.ActorType;
import hu.blackbelt.judo.meta.esm.accesspoint.Realm;
import hu.blackbelt.judo.meta.esm.namespace.Model;
import hu.blackbelt.judo.meta.esm.operation.OperationModifier;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.esm.structure.EntityType;
import hu.blackbelt.judo.meta.esm.structure.TransferObjectType;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class EsmActorType2PsmActorTypeTest {

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

        assertTrue(psmModel.isValid());
        validatePsm(new Slf4jLog(log), psmModel, calculatePsmValidationScriptURI());
    }

    @Test
    void testCreateActorTypePublicRealm() throws Exception {
        testName = "CreateActorType";

        final String MODEL_NAME = "Model";
        final String TRANSFER_OBJECT_TYPE_NAME = "T";
        final String ENTITY_TYPE_NAME = "E";
        final String OPERATION_NAME = "unboundOperation";

        final TransferObjectType unmappedTransferObjectType = newTransferObjectTypeBuilder()
                .withName(TRANSFER_OBJECT_TYPE_NAME)
                .withOperations(newOperationBuilder().withName(OPERATION_NAME)
                        .withCustomImplementation(true)
                        .withModifier(OperationModifier.STATIC)
                        .withBinding("")
                        .build())
                .build();
        final ActorType actorType = newActorTypeBuilder().build();
        unmappedTransferObjectType.setActorType(actorType);
        
        final EntityType entityType = newEntityTypeBuilder()
                .withName(ENTITY_TYPE_NAME)
                .build();

        final ActorType actorType2 = newActorTypeBuilder().withCustomRealm("realm").withRealm(Realm.CUSTOM).build();
        
        entityType.setActorType(actorType2);
        
        final Model model = newModelBuilder().withName(MODEL_NAME)
                .withElements(Arrays.asList(unmappedTransferObjectType, entityType)).build();

        esmModel.addContent(model);

        transform();

        final Optional<hu.blackbelt.judo.meta.psm.service.TransferObjectType> ap = allPsm(hu.blackbelt.judo.meta.psm.service.TransferObjectType.class)
                .filter(t -> t.isAccessPoint() && t.getName().equals(TRANSFER_OBJECT_TYPE_NAME))
                .findAny();
        assertTrue(ap.isPresent());
        assertNotNull(ap.get().getActorType());
        assertTrue(ap.get().getActorType().getTransferObjectType().equals(ap.get())
        		&& ap.get().getActorType().getName().equals(ap.get().getName())
        		&& ap.get().getActorType().getNamespace().getName().equals("_actortypes")
        		&& ap.get().getNamespace().getPackages().contains(ap.get().getActorType().getNamespace()));
        
        final Optional<hu.blackbelt.judo.meta.psm.service.TransferObjectType> ap2 = allPsm(hu.blackbelt.judo.meta.psm.service.TransferObjectType.class)
                .filter(t -> t.isAccessPoint() && t.getName().equals(ENTITY_TYPE_NAME))
                .findAny();
        assertTrue(ap.isPresent());
        assertNotNull(ap.get().getActorType());
        assertTrue(ap.get().getActorType().getTransferObjectType().equals(ap.get())
        		&& ap.get().getActorType().getName().equals(ap.get().getName())
        		&& ap.get().getActorType().getNamespace().getName().equals("_actortypes")
        		&& ap.get().getNamespace().getPackages().contains(ap.get().getActorType().getNamespace()));
        
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
