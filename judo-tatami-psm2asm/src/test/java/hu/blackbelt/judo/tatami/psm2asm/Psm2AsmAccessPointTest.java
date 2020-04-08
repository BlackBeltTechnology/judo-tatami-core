package hu.blackbelt.judo.tatami.psm2asm;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.buildAsmModel;
import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.SaveArguments.asmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.calculatePsmValidationScriptURI;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.validatePsm;
import static hu.blackbelt.judo.meta.psm.data.util.builder.DataBuilders.newEntityTypeBuilder;
import static hu.blackbelt.judo.meta.psm.derived.util.builder.DerivedBuilders.newReferenceExpressionTypeBuilder;
import static hu.blackbelt.judo.meta.psm.derived.util.builder.DerivedBuilders.newStaticNavigationBuilder;
import static hu.blackbelt.judo.meta.psm.namespace.util.builder.NamespaceBuilders.newModelBuilder;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.buildPsmModel;
import static hu.blackbelt.judo.meta.psm.service.util.builder.ServiceBuilders.*;
import static hu.blackbelt.judo.meta.psm.type.util.builder.TypeBuilders.newCardinalityBuilder;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.calculatePsm2AsmTransformationScriptURI;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.executePsm2AsmTransformation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Optional;

import hu.blackbelt.judo.meta.psm.accesspoint.ActorType;
import hu.blackbelt.judo.meta.psm.accesspoint.util.builder.AccesspointBuilders;
import hu.blackbelt.judo.meta.psm.service.TransferObjectRelation;
import org.eclipse.emf.ecore.EAnnotation;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.hamcrest.core.IsEqual;
import org.hamcrest.core.IsNull;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.google.common.collect.ImmutableList;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmUtils;
import hu.blackbelt.judo.meta.psm.data.EntityType;
import hu.blackbelt.judo.meta.psm.derived.StaticNavigation;
import hu.blackbelt.judo.meta.psm.namespace.Model;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType;
import hu.blackbelt.judo.meta.psm.service.UnmappedTransferObjectType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class Psm2AsmAccessPointTest {

    public static final String MODEL_NAME = "Test";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";

    public static final String ACCESSPOINT_SOURCE = AsmUtils.getAnnotationUri("accessPoint");
    public static final String EXPOSED_SERVICE_SOURCE = AsmUtils.getAnnotationUri("exposedService");
    public static final String EXPOSED_GRAPH_SOURCE = AsmUtils.getAnnotationUri("exposedGraph");
    public static final String EXPRESSION_SOURCE = AsmUtils.getAnnotationUri("expression");

    Log slf4jlog;
    PsmModel psmModel;
    AsmModel asmModel;
    AsmUtils asmUtils;

    @BeforeEach
    public void setUp() {
        // Default logger
        slf4jlog = new Slf4jLog(log);
        // Loading PSM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        psmModel = buildPsmModel()
                .name(MODEL_NAME)
                .build();
        // Create empty ASM model
        asmModel = buildAsmModel()
                .name(MODEL_NAME)
                .build();
        asmUtils = new AsmUtils(asmModel.getResourceSet());
    }

    private void transform(final String testName) throws Exception {
        psmModel.savePsmModel(PsmModel.SaveArguments.psmSaveArgumentsBuilder()
                .file(new File(TARGET_TEST_CLASSES, getClass().getName() + "-" + testName + "-psm.model"))
                .build());

        assertTrue(psmModel.isValid());
        validatePsm(new Slf4jLog(log), psmModel, calculatePsmValidationScriptURI());

        executePsm2AsmTransformation(psmModel, asmModel, new Slf4jLog(log), calculatePsm2AsmTransformationScriptURI());

        assertTrue(asmModel.isValid());
        asmModel.saveAsmModel(asmSaveArgumentsBuilder()
                .file(new File(TARGET_TEST_CLASSES, getClass().getName() + "-" + testName + "-asm.model"))
                .build());
    }

    @Test
    void testAccessPoint() throws Exception {

        UnmappedTransferObjectType opGroup = newUnmappedTransferObjectTypeBuilder().withName("operationGroup").build();
        TransferObjectRelation eService = newTransferObjectRelationBuilder().withName("eService")
                .withCardinality(newCardinalityBuilder().build())
                .withTarget(opGroup).build();

        EntityType entity = newEntityTypeBuilder().withName("entity").build();
        MappedTransferObjectType mappedObject = newMappedTransferObjectTypeBuilder().withName("mappedObject").withEntityType(entity).build();
        StaticNavigation selector = newStaticNavigationBuilder().withName("selector").withTarget(entity)
                .withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("M::entity").build())
                .withCardinality(newCardinalityBuilder().withLower(0).withUpper(-1).build())
                .build();
        TransferObjectRelation eGraph = newTransferObjectRelationBuilder().withTarget(mappedObject)
                .withName("eGraph")
                .withBinding(selector)
                .withCardinality(newCardinalityBuilder().withLower(0).withUpper(-1).build())
                .build();

//		StaticNavigation selector2 = newStaticNavigationBuilder().withName("selector2").withTarget(entity)
//				.withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("M::entity").build())
//				.withSetterExpression(newReferenceSelectorTypeBuilder().withExpression("M::entity").build())
//				.withCardinality(newCardinalityBuilder().withLower(0).withUpper(-1).build())
//				.build();
//		ExposedGraph eGraph2 = newExposedGraphBuilder().withMappedTransferObjectType(mappedObject)
//				.withName("eGraph2")
//				.withSelector(selector)
//				.withCardinality(newCardinalityBuilder().withLower(0).withUpper(-1).build())
//				.build();

        UnmappedTransferObjectType accessPoint = newUnmappedTransferObjectTypeBuilder().withName("accessPoint")
                .withRelations(eService)
                .withRelations(ImmutableList.of(eGraph
                        //eGraph2
                ))
                .build();

        ActorType actor = AccesspointBuilders.newActorTypeBuilder()
                .withName("Actor")
                .withTransferObjectType(accessPoint)
                .build();

        Model model = newModelBuilder().withName("M").withElements(ImmutableList.of(actor, accessPoint, opGroup, entity, mappedObject, selector
                //, selector2
        )).build();

        psmModel.addContent(model);

        transform("testAccessPoint");

        final Optional<EClass> asmAP = asmUtils.all(EClass.class).filter(c -> c.getName().equals(accessPoint.getName())).findAny();
        assertTrue(asmAP.isPresent());
        assertThat(asmAP.get().getEAnnotation(ACCESSPOINT_SOURCE), IsNull.notNullValue());
        final EAnnotation apAnnotation = asmAP.get().getEAnnotation(ACCESSPOINT_SOURCE);
        assertTrue(apAnnotation.getDetails().containsKey("value"));
        assertTrue(apAnnotation.getDetails().get("value").equals("true"));
        assertTrue(apAnnotation.getEModelElement().equals(asmAP.get()));

        final Optional<EReference> asmES = asmUtils.all(EReference.class).filter(r -> r.getName().equals(eService.getName())).findAny();
        assertTrue(asmES.isPresent());
        // don't care
        //assertThat(asmES.get().getLowerBound(), IsEqual.equalTo(1));
        //assertThat(asmES.get().getUpperBound(), IsEqual.equalTo(1));
        //assertTrue(asmES.get().isDerived());
        assertTrue(asmES.get().getEContainingClass().equals(asmAP.get()));
        final Optional<EClass> asmOpGroup = asmUtils.all(EClass.class).filter(c -> c.getName().equals(opGroup.getName())).findAny();
        assertTrue(asmOpGroup.isPresent());
        assertTrue(asmES.get().getEType().equals(asmOpGroup.get()));
        assertThat(asmES.get().getEAnnotation(EXPOSED_SERVICE_SOURCE), IsNull.notNullValue());
        final EAnnotation esAnnotation = asmES.get().getEAnnotation(EXPOSED_SERVICE_SOURCE);
        assertTrue(esAnnotation.getDetails().containsKey("value"));
        assertTrue(esAnnotation.getDetails().get("value").equals("true"));
        assertTrue(esAnnotation.getEModelElement().equals(asmES.get()));

        final Optional<EReference> asmEG = asmUtils.all(EReference.class).filter(r -> r.getName().equals(eGraph.getName())).findAny();
        assertTrue(asmEG.isPresent());
        assertThat(asmEG.get().getLowerBound(), IsEqual.equalTo(eGraph.getCardinality().getLower()));
        assertThat(asmEG.get().getUpperBound(), IsEqual.equalTo(eGraph.getCardinality().getUpper()));
        assertTrue(asmEG.get().isDerived());
        assertTrue(asmEG.get().getEContainingClass().equals(asmAP.get()));

        final Optional<EClass> asmMappedObject = asmUtils.all(EClass.class).filter(c -> c.getName().equals(mappedObject.getName())).findAny();
        assertTrue(asmMappedObject.isPresent());
        assertTrue(asmEG.get().getEType().equals(asmMappedObject.get()));
        assertThat(asmEG.get().getEAnnotation(EXPOSED_GRAPH_SOURCE), IsNull.notNullValue());
        assertThat(asmEG.get().getEAnnotation(EXPRESSION_SOURCE), IsNull.notNullValue());
        final EAnnotation egAnnotation = asmEG.get().getEAnnotation(EXPOSED_GRAPH_SOURCE);
        assertTrue(egAnnotation.getDetails().containsKey("value"));
        assertTrue(egAnnotation.getDetails().get("value").equals("true"));
        assertTrue(egAnnotation.getEModelElement().equals(asmEG.get()));
        final EAnnotation egExprAnnotation = asmEG.get().getEAnnotation(EXPRESSION_SOURCE);
        assertTrue(egExprAnnotation.getDetails().containsKey("getter"));
        assertTrue(egExprAnnotation.getDetails().containsKey("getter.dialect"));
        assertTrue(egExprAnnotation.getDetails().get("getter").equals(selector.getGetterExpression().getExpression()));
        assertTrue(egExprAnnotation.getDetails().get("getter.dialect").equals(selector.getGetterExpression().getDialect().toString()));
        assertFalse(egExprAnnotation.getDetails().containsKey("setter"));
        assertFalse(egExprAnnotation.getDetails().containsKey("setter.dialect"));
        assertTrue(egExprAnnotation.getEModelElement().equals(asmEG.get()));

//        final Optional<EReference> asmEG2 = asmUtils.all(EReference.class).filter(r -> r.getName().equals(eGraph2.getName())).findAny();
//        assertTrue(asmEG2.isPresent());
//        assertThat(asmEG2.get().getEAnnotation(EXPRESSION_SOURCE), IsNull.notNullValue());
//        final EAnnotation egExprAnnotation2 = asmEG2.get().getEAnnotation(EXPRESSION_SOURCE);
//        assertTrue(egExprAnnotation2.getDetails().containsKey("setter"));
//        assertTrue(egExprAnnotation2.getDetails().containsKey("setter.dialect"));
//        assertTrue(egExprAnnotation2.getDetails().get("setter").equals(selector2.getSetterExpression().getExpression()));
//        assertTrue(egExprAnnotation2.getDetails().get("setter.dialect").equals(selector2.getSetterExpression().getDialect().toString()));
    }
}
