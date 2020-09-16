package hu.blackbelt.judo.tatami.psm2asm;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.buildAsmModel;
import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.SaveArguments.asmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.calculatePsmValidationScriptURI;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.validatePsm;
import static hu.blackbelt.judo.meta.psm.accesspoint.util.builder.AccesspointBuilders.*;
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
import static hu.blackbelt.judo.meta.psm.service.util.builder.ServiceBuilders.*;
import static hu.blackbelt.judo.meta.psm.type.util.builder.TypeBuilders.newCardinalityBuilder;
import static hu.blackbelt.judo.meta.psm.type.util.builder.TypeBuilders.newStringTypeBuilder;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.calculatePsm2AsmTransformationScriptURI;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.executePsm2AsmTransformation;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

import java.io.File;
import java.util.Arrays;
import java.util.Optional;

import hu.blackbelt.judo.meta.asm.runtime.AsmUtils;
import hu.blackbelt.judo.meta.psm.accesspoint.ActorType;
import hu.blackbelt.judo.meta.psm.service.*;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EOperation;
import org.hamcrest.core.IsEqual;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.psm.data.AssociationEnd;
import hu.blackbelt.judo.meta.psm.data.Attribute;
import hu.blackbelt.judo.meta.psm.data.BoundOperation;
import hu.blackbelt.judo.meta.psm.data.EntityType;
import hu.blackbelt.judo.meta.psm.data.util.builder.DataBuilders;
import hu.blackbelt.judo.meta.psm.derived.ExpressionDialect;
import hu.blackbelt.judo.meta.psm.derived.StaticNavigation;
import hu.blackbelt.judo.meta.psm.namespace.Model;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.psm.type.StringType;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class AccessPointTest {

    public static final String MODEL_NAME = "Test";
    public static final String TARGET_TEST_CLASSES = "target/test-classes";

    Log slf4jlog;
    PsmModel psmModel;
    AsmModel asmModel;

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
    }

    private void transform(final String testName) throws Exception {
        psmModel.savePsmModel(PsmModel.SaveArguments.psmSaveArgumentsBuilder()
                .file(new File(TARGET_TEST_CLASSES, getClass().getName() + "-" + testName + "-psm.model"))
                .build());

        validatePsm(new Slf4jLog(log), psmModel, calculatePsmValidationScriptURI());

        executePsm2AsmTransformation(psmModel, asmModel);

        asmModel.saveAsmModel(asmSaveArgumentsBuilder()
                .file(new File(TARGET_TEST_CLASSES, getClass().getName() + "-" + testName + "-asm.model"))
                .build());
    }

    @Test
    void testGetPrincipalOperations() throws Exception {
        final ActorType actor1 = newActorTypeBuilder()
                .withName("Actor1")
                .withRealm("realm1")
                .build();
        final UnmappedTransferObjectType ap1 = newUnmappedTransferObjectTypeBuilder()
                .withName("AccessPoint1")
                .withActorType(actor1)
                .build();
        useUnmappedTransferObjectType(ap1)
                .withOperations(newUnboundOperationBuilder()
                        .withName("getPrincipal1")
                        .withOutput(newParameterBuilder()
                                .withName("output")
                                .withCardinality(newCardinalityBuilder()
                                        .withLower(0)
                                        .withUpper(1)
                                        .build())
                                .withType(ap1)
                                .build())
                        .withBehaviour(newTransferOperationBehaviourBuilder()
                                .withBehaviourType(TransferOperationBehaviourType.GET_PRINCIPAL)
                                .withOwner(ap1)
                                .build())
                        .build())
                .build();

        final ActorType actor2 = newActorTypeBuilder()
                .withName("Actor2")
                .withRealm("realm2")
                .build();
        final UnmappedTransferObjectType ap2 = newUnmappedTransferObjectTypeBuilder()
                .withName("AccessPoint2")
                .withActorType(actor2)
                .withRelations(newTransferObjectRelationBuilder()
                        .withName("ap1")
                        .withTarget(ap1)
                        .withEmbedded(true)
                        .withCardinality(newCardinalityBuilder()
                                .withLower(0)
                                .withUpper(1)
                                .build())
                        .build())
                .build();
        useUnmappedTransferObjectType(ap2)
                .withOperations(newUnboundOperationBuilder()
                        .withName("getPrincipal2")
                        .withOutput(newParameterBuilder()
                                .withName("output")
                                .withCardinality(newCardinalityBuilder()
                                        .withLower(0)
                                        .withUpper(1)
                                        .build())
                                .withType(ap2)
                                .build())
                        .withBehaviour(newTransferOperationBehaviourBuilder()
                                .withBehaviourType(TransferOperationBehaviourType.GET_PRINCIPAL)
                                .withOwner(ap2)
                                .build())
                        .build())
                .build();

        final Model model = newModelBuilder()
                .withName("Model")
                .withElements(Arrays.asList(actor1, actor2, ap1, ap2))
                .build();

        psmModel.addContent(model);

        transform("testGetPrincipalOperations");

        final AsmUtils asmUtils = new AsmUtils(asmModel.getResourceSet());
        final Optional<EOperation> getPrincipal1 = asmUtils.all(EOperation.class)
                .filter(o -> "getPrincipal1".equals(o.getName()))
                .findAny();
        final Optional<EOperation> getPrincipal2 = asmUtils.all(EOperation.class)
                .filter(o -> "getPrincipal2".equals(o.getName()))
                .findAny();

        assertThat(getPrincipal1.isPresent(), equalTo(Boolean.TRUE));
        assertThat(getPrincipal2.isPresent(), equalTo(Boolean.TRUE));

        // ensure that getPrincipal operations are exposed by their own access points only
        assertThat(AsmUtils.getExtensionAnnotationListByName(getPrincipal1.get(), "exposedBy").stream()
                .allMatch(a -> "Model.AccessPoint1".equals(a.getDetails().get("value"))), equalTo(Boolean.TRUE));
        assertThat(AsmUtils.getExtensionAnnotationListByName(getPrincipal1.get(), "exposedBy").stream()
                .anyMatch(a -> "Model.AccessPoint1".equals(a.getDetails().get("value"))), equalTo(Boolean.TRUE));
        assertThat(AsmUtils.getExtensionAnnotationListByName(getPrincipal2.get(), "exposedBy").stream()
                .allMatch(a -> "Model.AccessPoint2".equals(a.getDetails().get("value"))), equalTo(Boolean.TRUE));
        assertThat(AsmUtils.getExtensionAnnotationListByName(getPrincipal2.get(), "exposedBy").stream()
                .anyMatch(a -> "Model.AccessPoint2".equals(a.getDetails().get("value"))), equalTo(Boolean.TRUE));
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

        final UnmappedTransferObjectType accessPoint1 = newUnmappedTransferObjectTypeBuilder()
                .withName("AP1")
                .withRelations(newTransferObjectRelationBuilder()
                        .withName("messenger")
                        .withTarget(messageDTO)
                        .withCardinality(newCardinalityBuilder().build())
                        .build())
                .build();

        final ActorType actor1 = newActorTypeBuilder()
                .withName("Actor1")
                .withTransferObjectType(accessPoint1)
                .build();

        final TransferObjectRelation allMessagesGraph = newTransferObjectRelationBuilder()
                .withName("allMessages")
                .withTarget(messageDTO)
                .withCardinality(newCardinalityBuilder().withUpper(-1).build())
                .withBinding(allMessages)
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

        final TransferObjectRelation allUsersGraph = newTransferObjectRelationBuilder()
                .withName("allUsers")
                .withTarget(userDTO)
                .withCardinality(newCardinalityBuilder().withUpper(-1).build())
                .withBinding(allUsers)
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

        final UnmappedTransferObjectType accessPoint2 = newUnmappedTransferObjectTypeBuilder()
                .withName("AP2")
                .withRelations(allMessagesGraph)
                .build();

        final ActorType actor2 = newActorTypeBuilder()
                .withName("Actor2")
                .withTransferObjectType(accessPoint2)
                .build();

        final UnmappedTransferObjectType accessPoint3 = newUnmappedTransferObjectTypeBuilder()
                .withName("AP3")
                .withRelations(allUsersGraph)
                .build();

        final ActorType actor3 = newActorTypeBuilder()
                .withName("Actor3")
                .withTransferObjectType(accessPoint3)
                .build();

        final Model model = newModelBuilder()
                .withName("Model")
                .withElements(Arrays.asList(string, message, user, messageDTO, userDTO, emailDTO, allMessages, allUsers, accessPoint1, accessPoint2, accessPoint3, actor1, actor2, actor3))
                .build();

        psmModel.addContent(model);

        transform("testExposedServicesAndGraphs");
    }
}
