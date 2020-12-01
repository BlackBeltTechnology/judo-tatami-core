package hu.blackbelt.judo.tatami.esm2psm;

import com.google.common.collect.ImmutableSet;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.esm.accesspoint.ActorType;
import hu.blackbelt.judo.meta.esm.accesspoint.util.builder.AccessBuilder;
import hu.blackbelt.judo.meta.esm.accesspoint.util.builder.ActorTypeBuilder;
import hu.blackbelt.judo.meta.esm.namespace.Model;
import hu.blackbelt.judo.meta.esm.namespace.util.builder.ModelBuilder;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.esm.structure.EntityType;
import hu.blackbelt.judo.meta.esm.structure.TransferObjectType;
import hu.blackbelt.judo.meta.esm.structure.TwoWayRelationMember;
import hu.blackbelt.judo.meta.esm.structure.util.builder.EntityTypeBuilder;
import hu.blackbelt.judo.meta.esm.structure.util.builder.MappingBuilder;
import hu.blackbelt.judo.meta.esm.structure.util.builder.TransferObjectTypeBuilder;
import hu.blackbelt.judo.meta.esm.structure.util.builder.TwoWayRelationMemberBuilder;
import hu.blackbelt.judo.meta.psm.data.BoundOperation;
import hu.blackbelt.judo.meta.psm.namespace.NamedElement;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static hu.blackbelt.judo.meta.esm.accesspoint.ActorKind.SYSTEM;
import static hu.blackbelt.judo.meta.esm.runtime.EsmEpsilonValidator.calculateEsmValidationScriptURI;
import static hu.blackbelt.judo.meta.esm.runtime.EsmEpsilonValidator.validateEsm;
import static hu.blackbelt.judo.meta.esm.runtime.EsmModel.buildEsmModel;
import static hu.blackbelt.judo.meta.esm.structure.MemberType.STORED;
import static hu.blackbelt.judo.meta.esm.structure.RelationKind.ASSOCIATION;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.calculatePsmValidationScriptURI;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.validatePsm;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.SaveArguments.psmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.buildPsmModel;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.calculateEsm2PsmTransformationScriptURI;
import static hu.blackbelt.judo.tatami.esm2psm.Esm2Psm.executeEsm2PsmTransformation;
import static java.util.Arrays.asList;
import static org.hamcrest.CoreMatchers.equalTo;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
public class Esm2PsmTwoWayRelationOperationTest {
    private final String TEST_SOURCE_MODEL_NAME = "urn:test.judo-meta-esm";
    private final String MODEL_NAME = "M";
    private final String TARGET_TEST_CLASSES = "target/test-classes";

    Log slf4jlog;
    EsmModel esmModel;

    Map<EObject, List<EObject>> resolvedTrace;
    PsmModel psmModel;
    Esm2PsmTransformationTrace esm2PsmTransformationTrace;

    @BeforeEach
    void setUp() throws Exception {
        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading ESM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        esmModel = buildEsmModel().uri(URI.createURI(TEST_SOURCE_MODEL_NAME)).name(MODEL_NAME).build();

        // Create empty PSM model
        psmModel = buildPsmModel().name(MODEL_NAME).build();
    }

    @AfterEach
    void tearDown() throws Exception {
        final String traceFileName = MODEL_NAME + "-esm2psm.model";

        // Saving trace map
        esm2PsmTransformationTrace.save(new File(TARGET_TEST_CLASSES, traceFileName));

        // Loading trace map
        Esm2PsmTransformationTrace esm2PsmTransformationTraceLoaded = Esm2PsmTransformationTrace
                .fromModelsAndTrace(MODEL_NAME, esmModel, psmModel, new File(TARGET_TEST_CLASSES, traceFileName));

        // Resolve serialized URI's as EObject map
        resolvedTrace = esm2PsmTransformationTraceLoaded.getTransformationTrace();

        // Printing trace
        for (EObject e: resolvedTrace.keySet()) {
            for (EObject t: resolvedTrace.get(e)) {
                log.trace(e.toString() + " -> " + t.toString());
            }
        }

        psmModel.savePsmModel(psmSaveArgumentsBuilder().file(new File(TARGET_TEST_CLASSES, MODEL_NAME + "-psm.model")));
    }

    private void transform() throws Exception {
        assertTrue(esmModel.isValid());
        validateEsm(new Slf4jLog(log), esmModel, calculateEsmValidationScriptURI());

        // Make transformation which returns the trace with the serialized URI's
        esm2PsmTransformationTrace = executeEsm2PsmTransformation(
                esmModel, psmModel, new Slf4jLog(log), calculateEsm2PsmTransformationScriptURI());

        assertTrue(psmModel.isValid());
        validatePsm(new Slf4jLog(log), psmModel, calculatePsmValidationScriptURI());
    }

    private void createEsmModelAndTransform(int requiredCardinality) {
        final EntityType entityA = EntityTypeBuilder.create().withName("A").build();
        entityA.setMapping(MappingBuilder.create().withTarget(entityA).build());

        final EntityType entityB = EntityTypeBuilder.create().withName("B").build();
        entityB.setMapping(MappingBuilder.create().withTarget(entityB).build());

        final TwoWayRelationMember entityBRelation = TwoWayRelationMemberBuilder.create()
                .withName("b")
                .withTarget(entityB)
                .withLower(requiredCardinality)
                .withUpper(requiredCardinality)
                .withMemberType(STORED)
                .withRelationKind(ASSOCIATION)
                .build();

        final TwoWayRelationMember entityARelation = TwoWayRelationMemberBuilder.create()
                .withName("a")
                .withTarget(entityA)
                .withLower(0)
                .withUpper(1)
                .withMemberType(STORED)
                .withRelationKind(ASSOCIATION)
                .build();

        entityBRelation.setPartner(entityARelation);
        entityARelation.setPartner(entityBRelation);

        entityA.getRelations().add(entityBRelation);
        entityB.getRelations().add(entityARelation);

        final ActorType actorType = ActorTypeBuilder.create()
                .withName("Actor")
                .withAccesses(asList(
                        AccessBuilder.create()
                                .withTarget(entityA)
                                .withLower(0)
                                .withUpper(-1)
                                .withName("accessToA")
                                .withCreateable(true)
                                .withUpdateable(true)
                                .build(),
                        AccessBuilder.create()
                                .withTarget(entityB)
                                .withLower(0)
                                .withUpper(-1)
                                .withName("accessToB")
                                .withCreateable(true)
                                .withUpdateable(true)
                                .build()))
                .withKind(SYSTEM)
                .build();

        final TransferObjectType accessPoint = TransferObjectTypeBuilder.create()
                .withName("AccessPoint")
                .withActorType(actorType)
                .build();

        final Model model = ModelBuilder.create()
                .withName(MODEL_NAME)
                .withElements(asList(entityA, entityB, actorType, accessPoint))
                .build();

        esmModel.addContent(model);

        try {
            transform();
        } catch (Exception e) {
            fail(e);
        }
    }

    @Test
    @Disabled // TODO JNG-2009
    public void testTwoWaySingleToSingleRequired() {
        createEsmModelAndTransform(1);
        checkOperations();
    }

    @Test
    @Disabled // TODO JNG-2009
    public void testTwoWaySingleToCollectionRequired() {
        createEsmModelAndTransform(5);
        checkOperations();
    }

    private void checkOperations() {
        final Set<String> operationNames = allPsm(BoundOperation.class)
                .filter(e -> e.getName().matches("^_((un)?set|add|remove)Reference[AB]ForM_[AB]$"))
                .map(NamedElement::getName)
                .collect(Collectors.toSet());

        assertThat(operationNames, equalTo(ImmutableSet.of("_setReferenceBForM_A")));
    }

    private static <T> Stream<T> asStream(Iterator<T> sourceIterator, boolean parallel) {
        Iterable<T> iterable = () -> sourceIterator;
        return StreamSupport.stream(iterable.spliterator(), parallel);
    }

    private <T> Stream<T> allPsm() {
        return asStream((Iterator<T>) psmModel.getResourceSet().getAllContents(), false);
    }

    private <T> Stream<T> allPsm(final Class<T> clazz) {
        return allPsm().filter(e -> clazz.isAssignableFrom(e.getClass())).map(e -> (T) e);
    }
}
