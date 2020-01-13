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
import hu.blackbelt.judo.meta.psm.service.UnboundOperation;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Disabled;
import org.junit.jupiter.api.Test;

import java.io.File;
import java.io.FileOutputStream;
import java.util.Iterator;
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
    public void testReferenceEmbedding() throws Exception {
        slf4jlog.info("testReferenceEmbedment~~~~~~~~~~~~~~~~~~~~");
        //ShipperInfo
        //TwoWayRelationMember shipperOrdersInShipper = newTwoWayRelationMemberBuilder().withName("shipperOrders").withUpper(-1).build();
        AssociationEnd shipperOrdersInShipper = newAssociationEndBuilder().withName("shipperOrders").withCardinality(newCardinalityBuilder().withUpper(-1).withLower(0).build()).build();
        EntityType shipperEntity = newEntityTypeBuilder().withName("Shipper").withRelations(ImmutableList.of(shipperOrdersInShipper)).build();

        MappedTransferObjectType shipperInfo = newMappedTransferObjectTypeBuilder().withName("ShipperInfo").withEntityType(shipperEntity)
                .withRelations(ImmutableList.of(

                )).build();

        //ProductInfo
        EntityType productEntity = newEntityTypeBuilder().withName("Product").withRelations(ImmutableList.of()).build();

        MappedTransferObjectType productInfo = newMappedTransferObjectTypeBuilder().withName("ProductInfo").withEntityType(productEntity)
                .withRelations(ImmutableList.of(
                        //orwm:category@esm
                )).build();

        //OrderItem (for product relation in intOrdInf)
        AssociationEnd productInOrderDetail = newAssociationEndBuilder().withName("productInOrderDetail").withTarget(productEntity).withCardinality(newCardinalityBuilder().withLower(1).withUpper(1).build()).build();
        EntityType orderDetail = newEntityTypeBuilder().withName("OrderDetail")
                .withRelations(ImmutableList.of(productInOrderDetail))
                .build();

        MappedTransferObjectType orderItem = newMappedTransferObjectTypeBuilder().withName("OrderItem").withEntityType(orderDetail)
                .withRelations(ImmutableList.of(
                        newTransferObjectRelationBuilder().withName("product").withEmbedded(false).withTarget(productInfo).withBinding(productInOrderDetail).withCardinality(newCardinalityBuilder().withLower(1).withUpper(1).build()).build()
                ))
                .build();

        //TODO: continue
        //AssociationEnd itemsInOrder = newAssociationEndBuilder().withName("items").withTarget(orderDetail)//.withContainment(true).withLower(1).withUpper(-1).build();
        //shipperInOrder + shipperOrdersInShipper (twr@esm -> 2assoc@psm)
        Containment orderDetailsInOrder = newContainmentBuilder().withName("orderDetails").withTarget(orderDetail).withCardinality(newCardinalityBuilder().withLower(1).withUpper(-1).build()).build();
        AssociationEnd shipperInOrder = newAssociationEndBuilder().withName("shipper").withTarget(shipperEntity).withCardinality(newCardinalityBuilder().withLower(0).withUpper(1).build()).build();
        shipperInOrder.setPartner(shipperOrdersInShipper);
        shipperOrdersInShipper.setPartner(shipperInOrder);
        EntityType orderEntity = newEntityTypeBuilder().withName("Order")
                .withRelations(ImmutableList.of(orderDetailsInOrder, shipperInOrder/*, itemsInOrder*/))
                .build();
        shipperOrdersInShipper.setTarget(orderEntity);
        //orderEntity.setMapping(newMappingBuilder().withTarget(orderEntity).build());
        //shipperOrdersInShipper.setTarget(orderEntity);

        MappedTransferObjectType orderInfo = newMappedTransferObjectTypeBuilder().withName("OrderInfo").withEntityType(orderEntity)
                .withRelations(ImmutableList.of(
                        //nonembedded => new embedded "shipper_" targeting ShipperReference (mto w/o members)
                        newTransferObjectRelationBuilder().withName("shipper").withTarget(shipperInfo).withBinding(shipperInOrder).withEmbedded(false).withCardinality(newCardinalityBuilder().withLower(0).withUpper(1).build()).build(),
                        //embedded => goto target
                        newTransferObjectRelationBuilder().withName("items").withTarget(orderItem).withBinding(orderDetailsInOrder).withEmbedded(true).withEmbeddedCreate(true).withCardinality(newCardinalityBuilder().withLower(1).withUpper(-1).build()).build()
                ))
                .build();

        EntityType internationalOrderEntity = newEntityTypeBuilder().withName("InternationalOrder")
                .withRelations(ImmutableList.of(

                ))
                .withSuperEntityTypes(orderEntity)
                .build();
        MappedTransferObjectType internationalOrderInfo = newMappedTransferObjectTypeBuilder().withName("InternationalOrderInfo")
                .withEntityType(internationalOrderEntity)
                .withRelations(ImmutableList.of(

                ))
                .build();
/*
        //"createOrder_" bound op with input parameter typed InternationalOrderInfo
        //TODO: change target to internationalOrderInfo when supported
        UnboundOperation createOrder = newUnboundOperationBuilder().withName("createOrder_")
                .withInput(newParameterBuilder().withName("input").withLower(1).withUpper(1)
                        .withTarget(orderInfo).build())
                .withOutput(newParameterBuilder().withName("output").withLower(1).withUpper(1)
                        .withTarget(orderInfo).build())
                .withCustomImplementation(true).withBody("body").withStateful(true)
                .build();
*/
        UnboundOperation createOrder = newUnboundOperationBuilder().withName("createOrder_")
                .withInput(newParameterBuilder().withName("input").withCardinality(newCardinalityBuilder().withLower(1).withUpper(1).build()).withType(orderInfo).build())
                .withOutput(newParameterBuilder().withName("output").withCardinality(newCardinalityBuilder().withLower(1).withUpper(1).build()).withType(orderInfo).build())
                .withImplementation(newOperationBodyBuilder().withBody("body").withCustomImplementation(true).withStateful(true).build())
                .build();

        Package entities = newPackageBuilder().withName("entities").withElements(ImmutableList.of(orderEntity, shipperEntity, orderDetail, productEntity)).build();
        Package services = newPackageBuilder().withName("services").withElements(ImmutableList.of(orderInfo, shipperInfo, orderItem, productInfo, createOrder)).build();

        //Package navigations = newPackageBuilder().withName("navigations").withElements(ImmutableList.of(productSelector)).build();

        //Model model = newModelBuilder().withName("testModel").withElements(ImmutableList.of(entities, services)).build();
        Model model = NamespaceBuilders.newModelBuilder().withName("testModel")
                .withPackages(ImmutableList.of(entities, services))
                //.withElements(accessPoint)
                .build();
        psmModel.addContent(model);
        executePsm2AsmTransformation(
                psmModel,
                asmModel,
                new Slf4jLog(log),
                calculatePsm2AsmTransformationScriptURI());
        asmModel.saveAsmModel(asmSaveArgumentsBuilder()
                .outputStream(new FileOutputStream(new File(TARGET_TEST_CLASSES, REFERENCE_ASM_MODEL))));
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
