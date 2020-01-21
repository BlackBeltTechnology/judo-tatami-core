package hu.blackbelt.judo.tatami.psm2asm;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.psm.data.AssociationEnd;
import hu.blackbelt.judo.meta.psm.data.Containment;
import hu.blackbelt.judo.meta.psm.data.EntityType;
import hu.blackbelt.judo.meta.psm.namespace.Model;
import hu.blackbelt.judo.meta.psm.namespace.Package;
import hu.blackbelt.judo.meta.psm.namespace.util.builder.NamespaceBuilders;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.psm.service.MappedTransferObjectType;
import hu.blackbelt.judo.meta.psm.service.TransferObjectRelation;
import hu.blackbelt.judo.meta.psm.service.UnboundOperation;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EReference;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
import java.util.Optional;
import java.util.stream.Stream;
import java.util.stream.StreamSupport;

import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.SaveArguments.asmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.buildAsmModel;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.calculatePsmValidationScriptURI;
import static hu.blackbelt.judo.meta.psm.PsmEpsilonValidator.validatePsm;
import static hu.blackbelt.judo.meta.psm.data.util.builder.DataBuilders.*;
import static hu.blackbelt.judo.meta.psm.namespace.util.builder.NamespaceBuilders.newPackageBuilder;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.buildPsmModel;
import static hu.blackbelt.judo.meta.psm.service.util.builder.ServiceBuilders.*;
import static hu.blackbelt.judo.meta.psm.type.util.builder.TypeBuilders.newCardinalityBuilder;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.calculatePsm2AsmTransformationScriptURI;
import static hu.blackbelt.judo.tatami.psm2asm.Psm2Asm.executePsm2AsmTransformation;
import static org.junit.jupiter.api.Assertions.assertTrue;

@Slf4j
public class Psm2AsmReferenceTest {


    public static final String MODEL_NAME = "referenceModel";
    public static final String REFERENCE_ASM_MODEL = "reference-asm.model";

    public static final String TARGET_TEST_CLASSES = "target/test-classes";

    public static final String GENERATED_REFERENCE_CLASS_POSTFIX = "__Reference";

    Log slf4jlog;
    PsmModel psmModel;
    AsmModel asmModel;

    @BeforeEach
    public void setUp() throws Exception {

        // Default logger
        slf4jlog = new Slf4jLog(log);

        // Loading PSM to isolated ResourceSet, because in Tatami
        // there is no new namespace registration made.
        psmModel = buildPsmModel()
                .name(MODEL_NAME)
                .build();

        // When model is invalid the loader have to throw exception. This checks that invalid model cannot valid -if
        // the loading check does not run caused by some reason
        assertTrue(psmModel.isValid());

        validatePsm(new Slf4jLog(log), psmModel,
                calculatePsmValidationScriptURI());

        // Create empty ASM model
        asmModel = buildAsmModel()
                .name(MODEL_NAME)
                .build();
    }

    @Test
    public void testCreateEmbeddedTransferRelationInTransferObjectsOfOperationInputType() throws Exception {
        slf4jlog.info("testCreateEmbeddedTransferRelationInTransferObjectsOfOperationInputType");
        AssociationEnd shipperOrdersInShipper = newAssociationEndBuilder().withName("shipperOrders").withCardinality(newCardinalityBuilder().withUpper(-1).withLower(0).build()).build();
        EntityType shipperEntity = newEntityTypeBuilder().withName("Shipper").withRelations(ImmutableList.of(shipperOrdersInShipper)).build();
        MappedTransferObjectType shipperInfo = newMappedTransferObjectTypeBuilder().withName("ShipperInfo").withEntityType(shipperEntity)
                .withRelations(ImmutableList.of(
                )).build();

        EntityType productEntity = newEntityTypeBuilder().withName("Product").withRelations(ImmutableList.of()).build();

        MappedTransferObjectType productInfo = newMappedTransferObjectTypeBuilder().withName("ProductInfo").withEntityType(productEntity)
                .withRelations(ImmutableList.of(
                )).build();

        AssociationEnd productInOrderDetail = newAssociationEndBuilder().withName("productInOrderDetail").withTarget(productEntity).withCardinality(newCardinalityBuilder().withLower(1).withUpper(1).build()).build();
        EntityType orderDetail = newEntityTypeBuilder().withName("OrderDetail")
                .withRelations(ImmutableList.of(productInOrderDetail))
                .build();

        //mutual embedding
        TransferObjectRelation orderItemEmbeddingOrderInfo = newTransferObjectRelationBuilder().withName("embeddingOrder").withEmbedded(true).withCardinality(newCardinalityBuilder().withLower(1).withUpper(1).build()).build();
        MappedTransferObjectType orderItem = newMappedTransferObjectTypeBuilder().withName("OrderItem").withEntityType(orderDetail)
                .withRelations(ImmutableList.of(
                        newTransferObjectRelationBuilder().withName("product").withEmbedded(false).withTarget(productInfo).withBinding(productInOrderDetail).withCardinality(newCardinalityBuilder().withLower(1).withUpper(1).build()).build()
                        , orderItemEmbeddingOrderInfo
                        //mutual embedding
                ))
                .build();

        Containment orderDetailsInOrder = newContainmentBuilder().withName("orderDetails").withTarget(orderDetail).withCardinality(newCardinalityBuilder().withLower(1).withUpper(-1).build()).build();
        AssociationEnd shipperInOrder = newAssociationEndBuilder().withName("shipper").withTarget(shipperEntity).withCardinality(newCardinalityBuilder().withLower(0).withUpper(1).build()).build();
        shipperInOrder.setPartner(shipperOrdersInShipper);
        shipperOrdersInShipper.setPartner(shipperInOrder);
        EntityType orderEntity = newEntityTypeBuilder().withName("Order")
                .withRelations(ImmutableList.of(orderDetailsInOrder, shipperInOrder))
                .build();
        shipperOrdersInShipper.setTarget(orderEntity);

        MappedTransferObjectType orderInfo = newMappedTransferObjectTypeBuilder().withName("OrderInfo").withEntityType(orderEntity)
                .withRelations(ImmutableList.of(
                        newTransferObjectRelationBuilder().withName("shipper").withTarget(shipperInfo).withBinding(shipperInOrder).withEmbedded(false).withCardinality(newCardinalityBuilder().withLower(0).withUpper(1).build()).build(),
                        newTransferObjectRelationBuilder().withName("items").withTarget(orderItem).withBinding(orderDetailsInOrder).withEmbedded(true).withEmbeddedCreate(true).withCardinality(newCardinalityBuilder().withLower(1).withUpper(-1).build()).build()
                ))
                .build();

        //mutual embedding
        orderItemEmbeddingOrderInfo.setTarget(orderInfo);

        UnboundOperation createOrder = newUnboundOperationBuilder().withName("createOrder_")
                .withInput(newParameterBuilder().withName("input").withCardinality(newCardinalityBuilder().withLower(1).withUpper(1).build()).withType(orderInfo).build())
                .withOutput(newParameterBuilder().withName("output").withCardinality(newCardinalityBuilder().withLower(1).withUpper(1).build()).withType(orderInfo).build())
                .withImplementation(newOperationBodyBuilder().withBody("body").withCustomImplementation(true).withStateful(true).build())
                .build();

        Package entities = newPackageBuilder().withName("entities").withElements(ImmutableList.of(orderEntity, shipperEntity, orderDetail, productEntity)).build();
        Package services = newPackageBuilder().withName("services").withElements(ImmutableList.of(orderInfo, shipperInfo, orderItem, productInfo, createOrder)).build();

        Model model = NamespaceBuilders.newModelBuilder().withName("testModel")
                .withPackages(ImmutableList.of(entities, services))
                .build();
        psmModel.addContent(model);
        executePsm2AsmTransformation(
                psmModel,
                asmModel,
                new Slf4jLog(log),
                calculatePsm2AsmTransformationScriptURI());
        asmModel.saveAsmModel(asmSaveArgumentsBuilder()
                .outputStream(new FileOutputStream(new File(TARGET_TEST_CLASSES, REFERENCE_ASM_MODEL))));

        final Optional<EClass> asmOrderInfo = allAsm(EClass.class).filter(eclazz -> "OrderInfo".equals(eclazz.getName())).findAny();
        assertTrue(asmOrderInfo.isPresent());
        final Optional<EClass> asmOrderItem = allAsm(EClass.class).filter(eclazz -> "OrderItem".equals(eclazz.getName())).findAny();
        assertTrue(asmOrderItem.isPresent());
        final Optional<EClass> shipperReferenceClass = allAsm(EClass.class).filter(eclazz -> (shipperEntity.getName() + GENERATED_REFERENCE_CLASS_POSTFIX).equals(eclazz.getName())).findAny();
        assertTrue(shipperReferenceClass.isPresent());
        final Optional<EClass> productReferenceClass = allAsm(EClass.class).filter(eclazz -> (productEntity.getName() + GENERATED_REFERENCE_CLASS_POSTFIX).equals(eclazz.getName())).findAny();
        assertTrue(productReferenceClass.isPresent());
        final Optional<EReference> embeddedProductReference = allAsm(EReference.class).filter(reference -> "_product".equals(reference.getName())).findAny();
        assertTrue(embeddedProductReference.isPresent());
        final Optional<EReference> embeddedShipperReference = allAsm(EReference.class).filter(reference -> "_shipper".equals(reference.getName())).findAny();
        assertTrue(embeddedShipperReference.isPresent());


        assertTrue(embeddedShipperReference.get().isContainment());
        assertTrue(embeddedShipperReference.get().getEReferenceType().equals(shipperReferenceClass.get()));
        assertTrue(asmOrderInfo.get().getEAllReferences().contains(embeddedShipperReference.get()));

        assertTrue(embeddedProductReference.get().isContainment());
        assertTrue(embeddedProductReference.get().getEReferenceType().equals(productReferenceClass.get()));
        assertTrue(asmOrderItem.get().getEAllReferences().contains(embeddedProductReference.get()));
    }

    static <T> Stream<T> asStream(Iterator<T> sourceIterator, boolean parallel) {
        Iterable<T> iterable = () -> sourceIterator;
        return StreamSupport.stream(iterable.spliterator(), parallel);
    }

    <T> Stream<T> allAsm() {
        return asStream((Iterator<T>) asmModel.getResourceSet().getAllContents(), false);
    }

    private <T> Stream<T> allAsm(final Class<T> clazz) {
        return allAsm().filter(e -> clazz.isAssignableFrom(e.getClass())).map(e -> (T) e);
    }

}
