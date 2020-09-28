package hu.blackbelt.judo.tatami.ui2client;

import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newStringTypeBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newTransferObjectFormBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newTransferObjectTableBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newTransferObjectViewBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newTabularReferenceFieldBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newDataColumnBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newDataFieldBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newActionButtonBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newGroupBuilder;

import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newNumericTypeBuilder;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.newDateTypeBuilder;
import static hu.blackbelt.judo.meta.esm.accesspoint.util.builder.AccesspointBuilders.newActorTypeBuilder;
import static hu.blackbelt.judo.meta.esm.namespace.util.builder.NamespaceBuilders.newModelBuilder;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.*;

import java.util.Arrays;


import hu.blackbelt.judo.meta.esm.accesspoint.ActorType;
import hu.blackbelt.judo.meta.esm.namespace.Model;
import hu.blackbelt.judo.meta.esm.structure.DataMember;
import hu.blackbelt.judo.meta.esm.structure.EntityType;
import hu.blackbelt.judo.meta.esm.structure.MemberType;
import hu.blackbelt.judo.meta.esm.structure.OneWayRelationMember;
import hu.blackbelt.judo.meta.esm.structure.RelationKind;
import hu.blackbelt.judo.meta.esm.structure.TransferObjectType;
import hu.blackbelt.judo.meta.esm.type.DateType;
import hu.blackbelt.judo.meta.esm.type.NumericType;
import hu.blackbelt.judo.meta.esm.type.StringType;
import hu.blackbelt.judo.meta.esm.ui.Action;
import hu.blackbelt.judo.meta.esm.ui.Horizontal;
import hu.blackbelt.judo.meta.esm.ui.Layout;
import hu.blackbelt.judo.meta.esm.ui.TransferObjectForm;
import hu.blackbelt.judo.meta.esm.ui.TransferObjectTable;
import hu.blackbelt.judo.meta.esm.ui.TransferObjectView;
import hu.blackbelt.judo.meta.esm.ui.Vertical;

public class SimpleOrderModel {


	public static Model createSimpleOrderModel() {

		// Data types
		StringType stringType = newStringTypeBuilder().withName("string").withMaxLength(256).build();
		NumericType floatType = newNumericTypeBuilder().withName("float").withScale(4).withPrecision(7).build();
		NumericType integerType = newNumericTypeBuilder().withName("integer").withScale(0).withPrecision(9).build();
		DateType dateType = newDateTypeBuilder().withName("date").build();


		// Order
		DataMember orderCustomer = newDataMemberBuilder()
				.withName("customer")
				.withMemberType(MemberType.STORED)
				.withDataType(stringType)
				.withRequired(true)
				.build();
		orderCustomer.setBinding(orderCustomer);

		DataMember orderDate = newDataMemberBuilder()
				.withName("orderDate")
				.withMemberType(MemberType.STORED)
				.withDataType(dateType)
				.withRequired(true)
				.build();
		orderDate.setBinding(orderDate);

		EntityType order = newEntityTypeBuilder()
				.withName("Order")
				.withAttributes(orderDate)
				.withAttributes(orderCustomer)
				.build();
		order.setMapping(newMappingBuilder().withTarget(order).build());


		// Order Item
		DataMember orderItemQuantity = newDataMemberBuilder()
				.withName("quantity")
				.withMemberType(MemberType.STORED)
				.withDataType(integerType)
				.withRequired(true)
				.build();
		orderItemQuantity.setBinding(orderItemQuantity);

		DataMember orderItemPrice = newDataMemberBuilder()
				.withName("price")
				.withMemberType(MemberType.STORED)
				.withDataType(floatType)
				.withRequired(true)
				.build();
		orderItemPrice.setBinding(orderItemPrice);

		DataMember orderItemProduct = newDataMemberBuilder()
				.withName("product")
				.withMemberType(MemberType.STORED)
				.withDataType(stringType)
				.withRequired(true)
				.build();
		orderItemProduct.setBinding(orderItemProduct);

		EntityType orderItem = newEntityTypeBuilder()
				.withName("OrderItem")
				.withAttributes(orderItemQuantity)
				.withAttributes(orderItemPrice)
				.withAttributes(orderItemProduct)
				.build();
		orderItem.setMapping(newMappingBuilder().withTarget(orderItem).build());

		OneWayRelationMember orderOrderItems = newOneWayRelationMemberBuilder()
				.withName("items")
				.withTarget(orderItem)
				.withMemberType(MemberType.STORED)
				.withRelationKind(RelationKind.COMPOSITION)
				.withCreateable(true)
				.withUpdateable(true)
				.withDeleteable(true)
				.withLower(0)
				.withUpper(-1)
				.build();
		orderOrderItems.setBinding(orderOrderItems);
		useEntityType(order)
				.withRelations(orderOrderItems)
				.build();


		// Access Point
		TransferObjectType application = newTransferObjectTypeBuilder()
				.withName("OrderApplication")
				.build();

		OneWayRelationMember applicationOrders = newOneWayRelationMemberBuilder()
				.withName("orders")
				.withTarget(order)
				.withMemberType(MemberType.DERIVED)
				.withRelationKind(RelationKind.ASSOCIATION)
				.withGetterExpression("SimpleOrder::Order")
				.withCreateable(true)
				.withUpdateable(true)
				.withDeleteable(true)
				.withLower(0)
				.withUpper(-1)
				.build();

		useTransferObjectType(application)
				.withRelations(applicationOrders)
				.build();

		// Order Form
		TransferObjectForm orderForm = newTransferObjectFormBuilder()
				.withName("OrderForm")
				.withComponents(Arrays.asList(
						newGroupBuilder()
								.withName("Content")
								.withLabel("Order details")
								.withLayout(Layout.HORIZONTAL)
								.withWrap(true)
								.withHorizontal(Horizontal.LEFT)
								.withVertical(Vertical.TOP)
								.withFrame(true)
								.withComponents(Arrays.asList(
										newDataFieldBuilder()
												.withName("customer")
												.withLabel("Customer")
												.withIconName("text_fields")
												.withDataFeature(orderCustomer)
												.build(),

										newDataFieldBuilder()
												.withName("orderDate")
												.withLabel("Order Date")
												.withIconName("calendar_today")
												.withDataFeature(orderDate)
												.build(),

										newTabularReferenceFieldBuilder()
												.withName("orderItems")
												.withLabel("Items")
												.withMaxVisibleElements(5)
												.withRelationFeature(orderOrderItems)
												.withTargetDefinedTabular(false)
												.withColumns(Arrays.asList(
														newDataColumnBuilder()
																.withName("product")
																.withLabel("Product")
																.withVisible(true)
																.withDataFeature(orderItemProduct)
																.build(),
														newDataColumnBuilder()
																.withName("quantity")
																.withLabel("Quantity")
																.withVisible(true)
																.withDataFeature(orderItemQuantity)
																.build(),
														newDataColumnBuilder()
																.withName("price")
																.withLabel("Price")
																.withVisible(true)
																.withDataFeature(orderItemPrice)
																.build()
												))
												.build()
								))
								.build(),

						newGroupBuilder()
								.withName("Buttons")
								.withLabel("Order details")
								.withLayout(Layout.HORIZONTAL)
								.withWrap(true)
								.withHorizontal(Horizontal.LEFT)
								.withVertical(Vertical.TOP)
								.withFrame(true)
								.withComponents(Arrays.asList(
										newActionButtonBuilder()
												.withName("cancel")
												.withLabel("Cancel")
												.withAction(Action.CANCEL)
												.build(),
										newActionButtonBuilder()
												.withName("ok")
												.withLabel("Ok")
												.withAction(Action.SUBMIT)
												.build()
								))
								.build()
				))
				.build();
		order.setForm(orderForm);


		// Order table
		TransferObjectTable orderTable = newTransferObjectTableBuilder()
				.withMasterDetail(true)
				.withName("OrderTable")
				.withLabel("Order")
				.withMaxVisibleElements(5)
				.withColumns(Arrays.asList(
						newDataColumnBuilder()
								.withName("product")
								.withLabel("Product")
								.withVisible(true)
								.withDataFeature(orderItemProduct)
								.build(),
						newDataColumnBuilder()
								.withName("quantity")
								.withLabel("Quantity")
								.withVisible(true)
								.withDataFeature(orderItemQuantity)
								.build(),
						newDataColumnBuilder()
								.withName("price")
								.withLabel("Price")
								.withVisible(true)
								.withDataFeature(orderItemPrice)
								.build()
				))
				.build();
		order.setTable(orderTable);


		// Order View
		TransferObjectView orderView = newTransferObjectViewBuilder()
				.withName("OrderView")
				.withLabel("Order")
				.withComponents(Arrays.asList(
						newDataFieldBuilder()
								.withName("customer")
								.withLabel("Customer")
								.withIconName("text_fields")
								.withDataFeature(orderCustomer)
								.build(),

						newDataFieldBuilder()
								.withName("orderDate")
								.withLabel("Order Date")
								.withIconName("calendar_today")
								.withDataFeature(orderDate)
								.build(),

						newTabularReferenceFieldBuilder()
								.withName("orderItems")
								.withLabel("Items")
								.withMaxVisibleElements(5)
								.withTargetDefinedTabular(false)
								.withRelationFeature(orderOrderItems)
								.withColumns(Arrays.asList(
										newDataColumnBuilder()
												.withName("product")
												.withLabel("Product")
												.withVisible(true)
												.withDataFeature(orderItemProduct)
												.build(),
										newDataColumnBuilder()
												.withName("quantity")
												.withLabel("Quantity")
												.withVisible(true)
												.withDataFeature(orderItemQuantity)
												.build(),
										newDataColumnBuilder()
												.withName("price")
												.withLabel("Price")
												.withVisible(true)
												.withDataFeature(orderItemPrice)
												.build()
								))
								.build()
				))
				.build();
		order.setView(orderView);


		// Order Item View
		TransferObjectView orderItemView = newTransferObjectViewBuilder()
				.withName("OrderItemView")
				.withLabel("Order Item")
				.withComponents(Arrays.asList(
						newDataFieldBuilder()
								.withName("product")
								.withLabel("Product")
								.withDataFeature(orderItemProduct)
								.build(),
						newDataFieldBuilder()
								.withName("quantity")
								.withLabel("Quantity")
								.withDataFeature(orderItemQuantity)
								.build(),
						newDataFieldBuilder()
								.withName("price")
								.withLabel("Price")
								.withDataFeature(orderItemPrice)
								.build()
				))
				.build();
		orderItem.setView(orderItemView);

		// Application View
		TransferObjectView applicationView = newTransferObjectViewBuilder()
				.withName("Dashboard")
				.withLabel("Dashboard")
				.withComponents(Arrays.asList(
						newTabularReferenceFieldBuilder()
								.withName("All Order")
								.withLabel("orders")
								.withMaxVisibleElements(5)
								.withRelationFeature(applicationOrders)
								.withTargetDefinedTabular(false)
								.withColumns(Arrays.asList(
										newDataColumnBuilder()
												.withName("customer")
												.withLabel("Customer")
												.withIconName("text_fields")
												.withDataFeature(orderCustomer)
												.build(),
										newDataColumnBuilder()
												.withName("orderDate")
												.withLabel("Order Date")
												.withIconName("calendar_today")
												.withDataFeature(orderDate)
												.build()
								))
								.build()
				))
				.build();
		application.setView(applicationView);

		ActorType actor = newActorTypeBuilder()
				.withName("actor")
				.withPrincipal(application)
				.withRealm("sandbox")
				.build();
		useTransferObjectType(application).withActorType(actor).build();


		// Create model
		Model model = newModelBuilder()
				.withName("SimpleOrder")
				.withElements(Arrays.asList(
						stringType, integerType, floatType, dateType,
						order, orderItem,
						application, actor
				))
				.withDemoAccessPoint(false)
				.build();

		return model;
	}


}
