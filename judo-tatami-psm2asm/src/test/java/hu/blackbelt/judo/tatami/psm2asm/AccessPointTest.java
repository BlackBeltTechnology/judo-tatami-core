package hu.blackbelt.judo.tatami.psm2asm;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.buildAsmModel;
import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.SaveArguments.asmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.calculatePsmValidationScriptURI;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.validatePsm;
import static hu.blackbelt.judo.meta.psm.accesspoint.util.builder.AccesspointBuilders.newAccessPointBuilder;
import static hu.blackbelt.judo.meta.psm.accesspoint.util.builder.AccesspointBuilders.newExposedGraphBuilder;
import static hu.blackbelt.judo.meta.psm.accesspoint.util.builder.AccesspointBuilders.newExposedServiceBuilder;
import static hu.blackbelt.judo.meta.psm.data.util.builder.DataBuilders.newAssociationEndBuilder;
import static hu.blackbelt.judo.meta.psm.data.util.builder.DataBuilders.newAttributeBuilder;
import static hu.blackbelt.judo.meta.psm.data.util.builder.DataBuilders.newBoundOperationBuilder;
import static hu.blackbelt.judo.meta.psm.data.util.builder.DataBuilders.newEntityTypeBuilder;
import static hu.blackbelt.judo.meta.psm.data.util.builder.DataBuilders.newOperationBodyBuilder;
import static hu.blackbelt.judo.meta.psm.data.util.builder.DataBuilders.useAssociationEnd;
import static hu.blackbelt.judo.meta.psm.data.util.builder.DataBuilders.useBoundOperation;
import static hu.blackbelt.judo.meta.psm.derived.util.builder.DerivedBuilders.newReferenceExpressionTypeBuilder;
import static hu.blackbelt.judo.meta.psm.derived.util.builder.DerivedBuilders.newStaticNavigationBuilder;
import static hu.blackbelt.judo.meta.psm.namespace.util.builder.NamespaceBuilders.newModelBuilder;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.buildPsmModel;
import static hu.blackbelt.judo.meta.psm.service.util.builder.ServiceBuilders.newBoundTransferOperationBuilder;
import static hu.blackbelt.judo.meta.psm.service.util.builder.ServiceBuilders.newMappedTransferObjectTypeBuilder;
import static hu.blackbelt.judo.meta.psm.service.util.builder.ServiceBuilders.newParameterBuilder;
import static hu.blackbelt.judo.meta.psm.service.util.builder.ServiceBuilders.newTransferAttributeBuilder;
import static hu.blackbelt.judo.meta.psm.service.util.builder.ServiceBuilders.newTransferObjectRelationBuilder;
import static hu.blackbelt.judo.meta.psm.service.util.builder.ServiceBuilders.newTransferOperationBehaviourBuilder;
import static hu.blackbelt.judo.meta.psm.service.util.builder.ServiceBuilders.newUnboundOperationBuilder;
import static hu.blackbelt.judo.meta.psm.service.util.builder.ServiceBuilders.newUnmappedTransferObjectTypeBuilder;
import static hu.blackbelt.judo.meta.psm.service.util.builder.ServiceBuilders.useMappedTransferObjectType;
import static hu.blackbelt.judo.meta.psm.type.util.builder.TypeBuilders.newCardinalityBuilder;
import static hu.blackbelt.judo.meta.psm.type.util.builder.TypeBuilders.newStringTypeBuilder;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.calculatePsm2AsmTransformationScriptURI;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.executePsm2AsmTransformation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

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
import hu.blackbelt.judo.meta.psm.accesspoint.AccessPoint;
import hu.blackbelt.judo.meta.psm.accesspoint.ExposedGraph;
import hu.blackbelt.judo.meta.psm.accesspoint.ExposedService;
import hu.blackbelt.judo.meta.psm.data.AssociationEnd;
import hu.blackbelt.judo.meta.psm.data.Attribute;
import hu.blackbelt.judo.meta.psm.data.BoundOperation;
import hu.blackbelt.judo.meta.psm.data.EntityType;
import hu.blackbelt.judo.meta.psm.data.util.builder.DataBuilders;
import hu.blackbelt.judo.meta.psm.derived.ExpressionDialect;
import hu.blackbelt.judo.meta.psm.derived.StaticNavigation;
import hu.blackbelt.judo.meta.psm.namespace.Model;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType;
import hu.blackbelt.judo.meta.psm.service.TransferObjectRelation;
import hu.blackbelt.judo.meta.psm.service.TransferOperationBehaviourType;
import hu.blackbelt.judo.meta.psm.service.UnmappedTransferObjectType;
import hu.blackbelt.judo.meta.psm.type.StringType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccessPointTest {

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
    void testExposedServicesAndGraphs() throws Exception {
        final StringType string = newStringTypeBuilder()
                .withName("String")
                .withMaxLength(255)
                .build();

        final BoundOperation delete = newBoundOperationBuilder()
                .withName("delete")
                .withImplementation(newOperationBodyBuilder()
                        .withBody("delete __this")
                        .build())
                .build();
        final BoundOperation getAuthor = newBoundOperationBuilder()
                .withName("_getAuthor")
                .build();
        final Attribute messageBody = newAttributeBuilder()
                .withName("body")
                .withDataType(string)
                .withRequired(true)
                .build();
        final AssociationEnd authorOfMessage = DataBuilders.newAssociationEndBuilder()
                .withName("author")
                .withCardinality(newCardinalityBuilder().build())
                .build();
        final EntityType message = newEntityTypeBuilder()
                .withName("Message")
                .withAttributes(messageBody)
                .withRelations(authorOfMessage)
                .withOperations(delete)
                .withOperations(getAuthor)
                .build();

        final BoundOperation getMessages = newBoundOperationBuilder()
                .withName("_getMessages")
                .build();
        final Attribute userName = newAttributeBuilder()
                .withName("name")
                .withDataType(string)
                .withRequired(true)
                .build();
        final Attribute userEmail = newAttributeBuilder()
                .withName("email")
                .withDataType(string)
                .withRequired(false)
                .build();
        final AssociationEnd messagesOfUser = newAssociationEndBuilder()
                .withName("messages")
                .withCardinality(newCardinalityBuilder().withUpper(-1).build())
                .withPartner(authorOfMessage)
                .withTarget(message)
                .build();
        final EntityType user = newEntityTypeBuilder()
                .withName("User")
                .withAttributes(userName)
                .withAttributes(userEmail)
                .withRelations(messagesOfUser)
                .withOperations(getMessages)
                .build();
        useAssociationEnd(authorOfMessage)
                .withTarget(user)
                .withPartner(messagesOfUser)
                .build();

        final UnmappedTransferObjectType emailDTO = newUnmappedTransferObjectTypeBuilder()
                .withName("EmailDTO")
                .withAttributes(newTransferAttributeBuilder()
                        .withName("email")
                        .withDataType(string)
                        .withRequired(true)
                        .build())
                .build();

        final MappedTransferObjectType messageDTO = newMappedTransferObjectTypeBuilder().build();
        final MappedTransferObjectType userDTO = newMappedTransferObjectTypeBuilder().build();
        useBoundOperation(delete)
                .withInstanceRepresentation(messageDTO)
                .build();
        final TransferObjectRelation authorOfMessageDTO = newTransferObjectRelationBuilder()
                .withName("author")
                .withEmbedded(true)
                .withTarget(userDTO)
                .withCardinality(newCardinalityBuilder().build())
                .withBinding(authorOfMessage)
                .build();
        useMappedTransferObjectType(messageDTO)
                .withName("MessageDTO")
                .withEntityType(message)
                .withAttributes(newTransferAttributeBuilder()
                        .withName("body")
                        .withDataType(string)
                        .withRequired(true)
                        .withBinding(messageBody)
                        .build())
                .withRelations(authorOfMessageDTO)
                .withOperations(newBoundTransferOperationBuilder()
                        .withName("delete")
                        .withBinding(delete)
                        .build())
                .withOperations(newUnboundOperationBuilder()
                        .withName("echo")
                        .withInput(newParameterBuilder()
                                .withName("input")
                                .withCardinality(newCardinalityBuilder().withLower(1).build())
                                .withType(messageDTO)
                                .build())
                        .withOutput(newParameterBuilder()
                                .withName("output")
                                .withCardinality(newCardinalityBuilder().withLower(1).build())
                                .withType(messageDTO)
                                .build())
                        .withImplementation(newOperationBodyBuilder()
                                .withBody("return input;")
                                .build())
                        .build())
                .withOperations(newUnboundOperationBuilder()
                        .withName("getAllMessagesOf")
                        .withInput(newParameterBuilder()
                                .withName("input")
                                .withCardinality(newCardinalityBuilder().withLower(1).build())
                                .withType(userDTO)
                                .build())
                        .withOutput(newParameterBuilder()
                                .withName("output")
                                .withCardinality(newCardinalityBuilder().withUpper(-1).build())
                                .withType(messageDTO)
                                .build())
                        .withImplementation(newOperationBodyBuilder()
                                .withBody("return Model::MessageDTO!filter(m | m.author == input);")
                                .build())
                        .build())
                .build();
        useBoundOperation(getMessages)
                .withInstanceRepresentation(userDTO)
                .withOutput(newParameterBuilder()
                        .withName("output")
                        .withType(messageDTO)
                        .withCardinality(newCardinalityBuilder().withUpper(-1).build())
                        .build())
                .build();
        final TransferObjectRelation messagesOfUserDTO = newTransferObjectRelationBuilder()
                .withName("messages")
                .withTarget(messageDTO)
                .withCardinality(newCardinalityBuilder().withUpper(-1).build())
                .withBinding(messagesOfUser)
                .build();
        useMappedTransferObjectType(userDTO)
                .withName("UserDTO")
                .withEntityType(user)
                .withAttributes(newTransferAttributeBuilder()
                        .withName("name")
                        .withDataType(string)
                        .withRequired(true)
                        .withBinding(userName)
                        .build())
                .withRelations(messagesOfUserDTO)
                .withOperations(newUnboundOperationBuilder()
                        .withName("checkEmailIsUnique")
                        .withInput(newParameterBuilder()
                                .withName("input")
                                .withType(emailDTO)
                                .withCardinality(newCardinalityBuilder().withLower(1).build())
                                .build())
                        .withImplementation(newOperationBodyBuilder()
                                .withCustomImplementation(true)
                                .build())
                        .build())
                .withOperations(newBoundTransferOperationBuilder()
                        .withName("_getMessages")
                        .withBinding(getMessages)
                        .withOutput(newParameterBuilder()
                                .withName("output")
                                .withType(messageDTO)
                                .withCardinality(newCardinalityBuilder().withUpper(-1).build())
                                .build())
                        .withBehaviour(newTransferOperationBehaviourBuilder()
                                .withBehaviourType(TransferOperationBehaviourType.GET_RELATION)
                                .withOwner(messagesOfUserDTO)
                                .build())
                        .build())
                .build();
        useBoundOperation(getAuthor)
                .withInstanceRepresentation(messageDTO)
                .withOutput(newParameterBuilder()
                        .withName("output")
                        .withType(userDTO)
                        .withCardinality(newCardinalityBuilder().build())
                        .build())
                .build();
        useMappedTransferObjectType(messageDTO)
                .withOperations(newBoundTransferOperationBuilder()
                        .withName("_getAuthor")
                        .withBinding(getAuthor)
                        .withOutput(newParameterBuilder()
                                .withName("output")
                                .withType(userDTO)
                                .withCardinality(newCardinalityBuilder().build())
                                .build())
                        .withBehaviour(newTransferOperationBehaviourBuilder()
                                .withBehaviourType(TransferOperationBehaviourType.GET_RELATION)
                                .withOwner(authorOfMessageDTO)
                                .build())
                        .build())
                .build();

        final StaticNavigation allMessages = newStaticNavigationBuilder()
                .withName("AllMessages")
                .withTarget(message)
                .withGetterExpression(newReferenceExpressionTypeBuilder()
                        .withDialect(ExpressionDialect.JQL)
                        .withExpression("Model::Message")
                        .build())
                .withCardinality(newCardinalityBuilder().withUpper(-1).build())
                .build();

        final StaticNavigation allUsers = newStaticNavigationBuilder()
                .withName("AllUsers")
                .withTarget(user)
                .withGetterExpression(newReferenceExpressionTypeBuilder()
                        .withDialect(ExpressionDialect.JQL)
                        .withExpression("Model::User")
                        .build())
                .withCardinality(newCardinalityBuilder().withUpper(-1).build())
                .build();

        final AccessPoint accessPoint1 = newAccessPointBuilder()
                .withName("AP1")
                .withExposedServices(newExposedServiceBuilder()
                        .withName("messenger")
                        .withOperationGroup(messageDTO)
                        .build())
                .build();

        final ExposedGraph allMessagesGraph = newExposedGraphBuilder()
                .withName("allMessages")
                .withMappedTransferObjectType(messageDTO)
                .withCardinality(newCardinalityBuilder().withUpper(-1).build())
                .withSelector(allMessages)
                .build();
        useMappedTransferObjectType(messageDTO)
                .withOperations(newUnboundOperationBuilder()
                        .withName("_getAllMessages")
                        .withOutput(newParameterBuilder()
                                .withName("output")
                                .withType(messageDTO)
                                .withCardinality(newCardinalityBuilder().withUpper(-1).build())
                                .build())
                        .withBehaviour(newTransferOperationBehaviourBuilder()
                                .withBehaviourType(TransferOperationBehaviourType.GET)
                                .withOwner(allMessagesGraph)
                                .build())
                        .build())
                .build();

        final ExposedGraph allUsersGraph = newExposedGraphBuilder()
                .withName("allUsers")
                .withMappedTransferObjectType(userDTO)
                .withCardinality(newCardinalityBuilder().withUpper(-1).build())
                .withSelector(allUsers)
                .build();
        useMappedTransferObjectType(userDTO)
                .withOperations(newUnboundOperationBuilder()
                        .withName("_getAllUsers")
                        .withOutput(newParameterBuilder()
                                .withName("output")
                                .withType(userDTO)
                                .withCardinality(newCardinalityBuilder().withUpper(-1).build())
                                .build())
                        .withBehaviour(newTransferOperationBehaviourBuilder()
                                .withBehaviourType(TransferOperationBehaviourType.GET)
                                .withOwner(allUsersGraph)
                                .build())
                        .build())
                .build();

        final AccessPoint accessPoint2 = newAccessPointBuilder()
                .withName("AP2")
                .withExposedGraphs(allMessagesGraph)
                .build();

        final AccessPoint accessPoint3 = newAccessPointBuilder()
                .withName("AP3")
                .withExposedGraphs(allUsersGraph)
                .build();

        final Model model = newModelBuilder()
                .withName("Model")
                .withElements(Arrays.asList(string, message, user, messageDTO, userDTO, emailDTO, allMessages, allUsers, accessPoint1, accessPoint2, accessPoint3))
                .build();

        psmModel.addContent(model);

        transform("testExposedServicesAndGraphs");
    }
    
    @Test
    void testAccessPoint() throws Exception {
        
		UnmappedTransferObjectType opGroup = newUnmappedTransferObjectTypeBuilder().withName("operationGroup").build();
		ExposedService eService = newExposedServiceBuilder().withName("eService").withOperationGroup(opGroup).build();
		
		EntityType entity = newEntityTypeBuilder().withName("entity").build();
		MappedTransferObjectType mappedObject = newMappedTransferObjectTypeBuilder().withName("mappedObject").withEntityType(entity).build();
		StaticNavigation selector = newStaticNavigationBuilder().withName("selector").withTarget(entity)
					.withGetterExpression(newReferenceExpressionTypeBuilder().withExpression("M::entity").build())
					.withCardinality(newCardinalityBuilder().withLower(0).withUpper(-1).build())
					.build();
		ExposedGraph eGraph = newExposedGraphBuilder().withMappedTransferObjectType(mappedObject)
					.withName("eGraph")
					.withSelector(selector)
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
		
		AccessPoint accessPoint = newAccessPointBuilder().withName("accessPoint")
				.withExposedServices(eService)
				.withExposedGraphs(ImmutableList.of(eGraph
						//eGraph2
						))
				.build();

		Model model = newModelBuilder().withName("M").withElements(ImmutableList.of(accessPoint, opGroup, entity, mappedObject, selector
				//, selector2
				)).build();

        psmModel.addContent(model);

        transform("testAccessPoint");
        
        final Optional<EClass> asmAP = asmUtils.all(EClass.class).filter(c -> c.getName().equals(accessPoint.getName())).findAny();
        assertTrue(asmAP.isPresent());
        assertTrue(asmAP.get().isAbstract());
        assertTrue(asmAP.get().isInterface());
        assertThat(asmAP.get().getEAnnotation(ACCESSPOINT_SOURCE), IsNull.notNullValue());
        final EAnnotation apAnnotation = asmAP.get().getEAnnotation(ACCESSPOINT_SOURCE);
        assertTrue(apAnnotation.getDetails().containsKey("value"));
        assertTrue(apAnnotation.getDetails().get("value").equals("true"));
        assertTrue(apAnnotation.getEModelElement().equals(asmAP.get()));
        
        final Optional<EReference> asmES = asmUtils.all(EReference.class).filter(r -> r.getName().equals(eService.getName())).findAny();
        assertTrue(asmES.isPresent());
        assertThat(asmES.get().getLowerBound(), IsEqual.equalTo(1));
        assertThat(asmES.get().getUpperBound(), IsEqual.equalTo(1));
        assertTrue(asmES.get().isDerived());
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
