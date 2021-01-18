package hu.blackbelt.judo.tatami.esm2ui;

import static hu.blackbelt.judo.meta.esm.accesspoint.util.builder.AccesspointBuilders.*;
import static hu.blackbelt.judo.meta.esm.namespace.util.builder.NamespaceBuilders.*;
import static hu.blackbelt.judo.meta.esm.operation.util.builder.OperationBuilders.*;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.*;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.*;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.*;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import hu.blackbelt.judo.meta.esm.accesspoint.ActorType;
import hu.blackbelt.judo.meta.esm.namespace.Model;
import hu.blackbelt.judo.meta.esm.namespace.Package;
import hu.blackbelt.judo.meta.esm.operation.Operation;
import hu.blackbelt.judo.meta.esm.operation.OperationType;
import hu.blackbelt.judo.meta.esm.runtime.EsmUtils;
import hu.blackbelt.judo.meta.esm.structure.DataFeature;
import hu.blackbelt.judo.meta.esm.structure.DataMember;
import hu.blackbelt.judo.meta.esm.structure.EntityType;
import hu.blackbelt.judo.meta.esm.structure.MemberType;
import hu.blackbelt.judo.meta.esm.structure.OneWayRelationMember;
import hu.blackbelt.judo.meta.esm.structure.RelationKind;
import hu.blackbelt.judo.meta.esm.structure.TransferObjectType;
import hu.blackbelt.judo.meta.esm.type.BooleanType;
import hu.blackbelt.judo.meta.esm.type.DateType;
import hu.blackbelt.judo.meta.esm.type.EnumerationType;
import hu.blackbelt.judo.meta.esm.type.NumericType;
import hu.blackbelt.judo.meta.esm.type.StringType;
import hu.blackbelt.judo.meta.esm.type.TimestampType;
import hu.blackbelt.judo.meta.esm.ui.Action;
import hu.blackbelt.judo.meta.esm.ui.DataField;
import hu.blackbelt.judo.meta.esm.ui.EnumWidget;
import hu.blackbelt.judo.meta.esm.ui.Group;
import hu.blackbelt.judo.meta.esm.ui.DataColumn;
import hu.blackbelt.judo.meta.esm.ui.Horizontal;
import hu.blackbelt.judo.meta.esm.ui.Layout;
import hu.blackbelt.judo.meta.esm.ui.OperationForm;
import hu.blackbelt.judo.meta.esm.ui.Stretch;
import hu.blackbelt.judo.meta.esm.ui.TabBar;
import hu.blackbelt.judo.meta.esm.ui.TabularReferenceField;
import hu.blackbelt.judo.meta.esm.ui.TransferObjectForm;
import hu.blackbelt.judo.meta.esm.ui.TransferObjectTable;
import hu.blackbelt.judo.meta.esm.ui.TransferObjectView;
import hu.blackbelt.judo.meta.esm.ui.Vertical;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleOrderModel {
	
	private static TransferObjectView viewForTest;
	private static TransferObjectForm formForTest;
	private static TransferObjectTable tableForTest;

	public static TransferObjectForm getFormForTest() {
		return formForTest;
	}

	public static TransferObjectTable getTableForTest() {
		return tableForTest;
	}

	public static TransferObjectView getViewForTest() {
		return viewForTest;
	}

	public static Model createSimpleOrderModel() {
		
		//Data types
        StringType stringType = newStringTypeBuilder().withName("String").withMaxLength(256).build();
        NumericType floatType = newNumericTypeBuilder().withName("Float").withScale(4).withPrecision(7).build();
        NumericType integerType = newNumericTypeBuilder().withName("Integer").withScale(0).withPrecision(9).build();
        DateType dateType = newDateTypeBuilder().withName("Date").build();
        BooleanType boolType = newBooleanTypeBuilder().withName("Boolean").build();
        TimestampType tsType = newTimestampTypeBuilder().withName("TimeStamp").build();
        EnumerationType enumType = newEnumerationTypeBuilder().withName("Country")
        		.withMembers(
        				newEnumerationMemberBuilder().withName("HU").withOrdinal(1).build(),
        				newEnumerationMemberBuilder().withName("DE").withOrdinal(2).build(),
        				newEnumerationMemberBuilder().withName("FR").withOrdinal(3).build()
        				)
        		.build();
        
        //Order
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
        
        DataMember orderReceived = newDataMemberBuilder()
        		.withName("received")
        		.withMemberType(MemberType.STORED)
        		.withDataType(tsType)
        		.withRequired(true)
        		.build();
        orderReceived.setBinding(orderReceived);
        
        EntityType order = newEntityTypeBuilder()
                .withName("Order")
                .withAttributes(orderDate,orderCustomer,orderReceived)
                .build();
        order.setMapping(newMappingBuilder().withTarget(order).build());

        //OrderItem
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
            	.withAttributes(orderItemQuantity,orderItemPrice,orderItemProduct)
                .build();
        orderItem.setMapping(newMappingBuilder().withTarget(orderItem).build());
        
        OneWayRelationMember orderItems = newOneWayRelationMemberBuilder()
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
        orderItems.setBinding(orderItems);
        useEntityType(order)
        	.withRelations(orderItems)
        	.build();
        
        //InternationalOrder
        DataMember internationalOrderDuty = newDataMemberBuilder()
        		.withName("duty")
        		.withMemberType(MemberType.STORED)
        		.withDataType(floatType)
        		.withRequired(true)
        		.build();
        internationalOrderDuty.setBinding(internationalOrderDuty);
        
        DataMember orderCountry = newDataMemberBuilder()
        		.withName("country")
        		.withMemberType(MemberType.STORED)
        		.withDataType(enumType)
        		.withRequired(true)
        		.build();
        orderCountry.setBinding(orderCountry);
        
        DataMember review = newDataMemberBuilder()
        		.withName("review")
        		.withMemberType(MemberType.STORED)
        		.withDataType(stringType)
        		.withRequired(false)
        		.build();
        review.setBinding(review);
        
        EntityType internationalOrder = newEntityTypeBuilder().withName("InternationalOrder")
        		.withGeneralizations(newGeneralizationBuilder().withTarget(order).build())
        		.withAttributes(internationalOrderDuty,orderCountry,review)
        		.build();
        internationalOrder.setMapping(newMappingBuilder().withTarget(internationalOrder).build());
        
        //Employee
        DataMember employeeName = newDataMemberBuilder()
        		.withName("name")
        		.withMemberType(MemberType.STORED)
        		.withDataType(stringType)
        		.withRequired(true)
        		.build();
        employeeName.setBinding(employeeName);
        
        DataMember employeeId = newDataMemberBuilder()
        		.withName("id")
        		.withMemberType(MemberType.STORED)
        		.withDataType(stringType)
        		.withRequired(true)
        		.build();
        employeeId.setBinding(employeeId);
        
        EntityType employee = newEntityTypeBuilder().withName("employee")
        		.withOperations(newOperationBuilder()
        				.withName("promote")
        				.withOperationType(OperationType.INSTANCE)
        				.withInherited(newInheritedOperationReferenceBuilder().build())
        				.withBinding("")
        				.build())
        		.withAttributes(employeeName,employeeId)
        		.build();
        employee.setMapping(newMappingBuilder().withTarget(employee).build());
        
        EntityType archiver = newEntityTypeBuilder().withName("Archiver")
        		.withGeneralizations(newGeneralizationBuilder().withTarget(employee).build())
        		.build();
        archiver.setMapping(newMappingBuilder().withTarget(archiver).build());
        
        EntityType deliverer = newEntityTypeBuilder().withName("Deliverer")
        		.withGeneralizations(newGeneralizationBuilder().withTarget(employee).build())
        		.build();
        deliverer.setMapping(newMappingBuilder().withTarget(deliverer).build());
        
        //ArchivedOrder
        DataMember archivedOrderArchivalDate = newDataMemberBuilder()
        		.withName("archivalDate")
        		.withMemberType(MemberType.STORED)
        		.withDataType(dateType)
        		.withRequired(true)
        		.build();
        archivedOrderArchivalDate.setBinding(archivedOrderArchivalDate);
        
        DataMember archivedOrderCustomer = newDataMemberBuilder()
        		.withName("customer")
        		.withMemberType(MemberType.STORED)
        		.withDataType(stringType)
        		.withRequired(true)
        		.build();
        archivedOrderCustomer.setBinding(archivedOrderCustomer);

        DataMember archivedOrderOrderDate = newDataMemberBuilder()
        		.withName("orderDate")
        		.withMemberType(MemberType.STORED)
        		.withDataType(dateType)
        		.withRequired(true)
        		.build();
        archivedOrderOrderDate.setBinding(archivedOrderOrderDate);
        
        EntityType archivedOrder = newEntityTypeBuilder().withName("ArchivedOrder")
        		.withAttributes(archivedOrderArchivalDate,archivedOrderCustomer,archivedOrderOrderDate)
        		.build();
        archivedOrder.setMapping(newMappingBuilder().withTarget(archivedOrder).build());
        
        //ArchivedOrderItem
        DataMember archivedOrderItemReview = newDataMemberBuilder()
        		.withName("review")
        		.withDataType(integerType)
        		.build();
        archivedOrderItemReview.setBinding(archivedOrderItemReview);
        
        EntityType archivedOrderItem = newEntityTypeBuilder()
                .withName("ArchivedOrderItem")
                .withGeneralizations(newGeneralizationBuilder().withTarget(orderItem).build())
                .withAttributes(archivedOrderItemReview)
                .build();
        archivedOrderItem.setMapping(newMappingBuilder().withTarget(archivedOrderItem).build());
        
        OneWayRelationMember archivedOrderItems = newOneWayRelationMemberBuilder()
        		.withName("items")
        		.withTarget(archivedOrderItem)
        		.withMemberType(MemberType.STORED)
        		.withRelationKind(RelationKind.COMPOSITION)
        		.withCreateable(false)
        		.withUpdateable(false)
        		.withDeleteable(false)
        		.withLower(0)
        		.withUpper(-1)
        		.build();
        archivedOrderItems.setBinding(archivedOrderItems);
        
        useEntityType(archivedOrder)
        	.withRelations(archivedOrderItems)
        	.build();

        //ReturnedItems
        DataMember returnedItemsIsDamaged = newDataMemberBuilder()
        		.withName("isDamaged")
        		.withDataType(boolType)
        		.build();
        
        DataMember returnedItemsComment = newDataMemberBuilder()
        		.withName("comment")
        		.withDataType(stringType)
        		.build();
        
        EntityType returnedItem = newEntityTypeBuilder().withName("ReturnedItem")
        	.withGeneralizations(newGeneralizationBuilder().withTarget(orderItem).build())
        	.withAttributes(returnedItemsIsDamaged,returnedItemsComment)
        	.build();
        
        returnedItem.setMapping(newMappingBuilder().withTarget(returnedItem).build());

        //Donated item
        DataMember donatedItemReceiver = newDataMemberBuilder()
        		.withName("receiver")
        		.withDataType(stringType)
        		.build();
        
        EntityType donatedItem = newEntityTypeBuilder().withName("DonatedItem")
        		.withAttributes(donatedItemReceiver)
            	.build();
        donatedItem.setMapping(newMappingBuilder().withTarget(donatedItem).build());
        
        //Complaint
        DataMember complaintIsAngry = newDataMemberBuilder()
        		.withName("isAngry")
        		.withMemberType(MemberType.TRANSIENT)
        		.withDataType(boolType)
        		.build();
        
        DataMember complaintProduct = newDataMemberBuilder()
        		.withName("product")
        		.withMemberType(MemberType.TRANSIENT)
        		.withDataType(stringType)
        		.withRequired(true)
        		.build();
        
        TransferObjectType complaint = newTransferObjectTypeBuilder().withName("Complaint")
        		.withAttributes(complaintIsAngry,complaintProduct)
        		.build();
        
        //DamagedProduct
        DataMember damagedProductProduct = newDataMemberBuilder()
        		.withName("product")
        		.withMemberType(MemberType.TRANSIENT)
        		.withDataType(stringType)
        		.withRequired(true)
        		.build();
        DataMember damagedProductComplaint = newDataMemberBuilder()
        		.withName("complaint")
        		.withMemberType(MemberType.TRANSIENT)
        		.withDataType(stringType)
        		.build();
        
        TransferObjectType damagedProduct = newTransferObjectTypeBuilder().withName("DamagedProduct")
        		.withAttributes(damagedProductProduct,damagedProductComplaint)
        		.build();
        
        //Archive operation - single mapped input and single mapped output
        Operation archiveOperation = newOperationBuilder().withName("archive")
        		.withOperationType(OperationType.INSTANCE)
        		.withInput(
        				newParameterBuilder().withName("input").withTarget(archiver)
        				.withLower(1).withUpper(1).build())
        		.withOutput(
        				newParameterBuilder().withName("output").withTarget(archivedOrder)
        				.withLower(1).withUpper(1).build())
        		.withBinding("")
        		.withInherited(newInheritedOperationReferenceBuilder().build())
        		.build();
        
        useEntityType(order).withOperations(archiveOperation).build();
        
        //Return damaged items operation - collection mapped input and collection mapped output
        Operation returnDamagedItemsOperation = newOperationBuilder().withName("returnDamagedItems")
        		.withOperationType(OperationType.INSTANCE)
        		.withInput(
        				newParameterBuilder().withName("input").withTarget(deliverer)
        				.withLower(0).withUpper(-1).build())
        		.withOutput(
        				newParameterBuilder().withName("output").withTarget(returnedItem)
        				.withLower(0).withUpper(-1).build())
        		.withBinding("")
        		.withInherited(newInheritedOperationReferenceBuilder().build())
        		.build();
        
        useEntityType(order).withOperations(returnDamagedItemsOperation).build();
        
        //Donate operation
        Operation donateOperation = newOperationBuilder().withName("donate")
        		.withOperationType(OperationType.INSTANCE)
        		.withOutput(
        				newParameterBuilder().withName("output").withTarget(donatedItem)
        				.withLower(0).withUpper(-1).build())
        		.withBinding("")
        		.withInherited(newInheritedOperationReferenceBuilder().build())
        		.build();
        
        useEntityType(returnedItem).withOperations(donateOperation).build();
         
        //Register return operation - single unmapped input, single mapped output
        Operation registerReturnOperation = newOperationBuilder().withName("registerReturn")
        		.withOperationType(OperationType.INSTANCE)
        		.withInput(
        				newParameterBuilder().withName("input").withTarget(complaint)
        				.withLower(0).withUpper(1).build())
        		.withOutput(
        				newParameterBuilder().withName("output").withTarget(returnedItem)
        				.withLower(1).withUpper(1).build())
        		.withBinding("")
        		.withInherited(newInheritedOperationReferenceBuilder().build())
        		.build();
        
        useEntityType(deliverer).withOperations(registerReturnOperation).build();
        
        //Review damaged products operation - no input, collection unmapped output
        Operation reviewDamagedProductsOperation = newOperationBuilder().withName("reviewDamagedProducts")
        		.withOperationType(OperationType.INSTANCE)
        		.withOutput(
        				newParameterBuilder().withName("output").withTarget(damagedProduct)
        				.withLower(0).withUpper(-1).build())
        		.withBinding("")
        		.withInherited(newInheritedOperationReferenceBuilder().build())
        		.build();
        
        useEntityType(archiver).withOperations(reviewDamagedProductsOperation).build();
        
        //Register complaints operations - collection unmapped input, no output
        Operation registerComplaintsOperation = newOperationBuilder().withName("registerComplaints")
        		.withOperationType(OperationType.INSTANCE)
        		.withInput(
        				newParameterBuilder().withName("input").withTarget(complaint)
        				.withLower(0).withUpper(-1).build())
        		.withBinding("")
        		.withInherited(newInheritedOperationReferenceBuilder().build())
        		.build();
        
        useEntityType(deliverer).withOperations(registerComplaintsOperation).build();
        
        // Access Point
        TransferObjectType application = newTransferObjectTypeBuilder()
                .withName("OrderApplication")
                .build();

        OneWayRelationMember applicationOrders = newOneWayRelationMemberBuilder()
        		.withName("orders")
        		.withTarget(order)
        		.withMemberType(MemberType.DERIVED)
        		.withRelationKind(RelationKind.AGGREGATION)
        		.withGetterExpression("SimpleOrder::Order")
        		.withCreateable(true)
        		.withUpdateable(true)
        		.withDeleteable(true)
        		.withLower(0)
        		.withUpper(-1)
        		.build();
        useTransferObjectType(application).withRelations(applicationOrders).build();
        
        OneWayRelationMember applicationIntOrders = newOneWayRelationMemberBuilder()
        		.withName("internationalOrders")
        		.withTarget(internationalOrder)
        		.withMemberType(MemberType.DERIVED)
        		.withRelationKind(RelationKind.AGGREGATION)
        		.withGetterExpression("SimpleOrder::InternationalOrder")
        		.withCreateable(true)
        		.withUpdateable(true)
        		.withDeleteable(true)
        		.withLower(0)
        		.withUpper(-1)
        		.build();
        useTransferObjectType(application).withRelations(applicationIntOrders).build();
        
        Package types = newPackageBuilder().withName("types").withElements(stringType,integerType,floatType,dateType,boolType,tsType,enumType).build();

		ActorType actor = newActorTypeBuilder()
				.withName("actor")
				.withPrincipal(application)
				.withRealm("sandbox")
				.withManaged(false)
				.build();
		useTransferObjectType(application).withActorType(actor).build();

		// Create model
		Model model = newModelBuilder()
				.withName("SimpleOrder")
				.withElements(types)
				.withElements(order, orderItem, internationalOrder,
						application, actor,
						employee, archiver,
						archivedOrder,
						archivedOrderItem,
						returnedItem, donatedItem,
						deliverer,
						complaint,damagedProduct)
				.build();

		addUiElementsToTransferObjects(model);
		
        //Order Form
        setFormForTransferObjectType(order);
            
        //Order Table
        setTableForTransferObjectType(order,true);        

        //Order View
        setViewForTransferObjectType(order);
            
        //International Order Form
        formForTest = internationalOrder.getForm();
            
        //International Order Table
        tableForTest = internationalOrder.getTable();    

        //International Order View
        viewForTest = internationalOrder.getView();
        DataField fieldForReview2 = (DataField) viewForTest.getComponents().stream()
        		.filter(c -> c instanceof DataField)
        		.filter(c -> ((DataField)c).getDataFeature().equals(review))
        		.findAny()
        		.get();
        fieldForReview2.setTextMultiLine(true);
		
        return model;
	}
	
	private static TransferObjectForm createFormForTransferObjectType(TransferObjectType transferObject) {
		
		TransferObjectForm form = newTransferObjectFormBuilder()
        		.withName(transferObject.getName() + "Form")
        		.withFrame(false)
        		.build();

		if (!transferObject.getAllAttributes().isEmpty()) {
			List<DataField> dataFields = new ArrayList<>();
			transferObject.getAllAttributes().stream().forEach(a -> {
				dataFields.add(newDataFieldBuilder().withName(a.getName()).withLabel(a.getName().toUpperCase())
						.withIconName(getIconName(a)).withDataFeature(a).withCol(3).build());
			});
			
			useTransferObjectForm(form).withComponents(dataFields).build();
		}

		if (!transferObject.getAllRelations().isEmpty()) {
			ArrayList<TabularReferenceField> tables = new ArrayList<>();
			transferObject.getAllRelations().stream().forEach(r -> {
				ArrayList<DataColumn> columns = new ArrayList<>();

				TabularReferenceField tabular = newTabularReferenceFieldBuilder().withName(r.getName())
						.withLabel(r.getName().toUpperCase()).withRelationFeature(r).withCol(12)
						.withTargetDefinedTabular(false).build();
				TransferObjectType target = (TransferObjectType) (r.getTarget());

				target.getAllAttributes().stream().forEach(a -> {
					columns.add(newDataColumnBuilder().withName(a.getName()).withLabel(a.getName().toUpperCase())
							.withVisible(true).withDataFeature(a).build());
				});
				tabular.getColumns().addAll(columns);
				tables.add(tabular);
			});
			
			useTransferObjectForm(form).withComponents(tables).build();
		}
        
        return form;
	}
	
	public static TransferObjectForm setFormForTransferObjectType(TransferObjectType transferObject) {
		TransferObjectForm form = createFormForTransferObjectType(transferObject);
		transferObject.setForm(form);
        return form;
	}

	private static String getIconName(DataFeature a) {
		if (a.getDataType() instanceof DateType) {
			return "calendar_today";
		} else if (a.getDataType() instanceof TimestampType) {
			return "schedule";
		} else if (a.getDataType() instanceof NumericType) {
			return "dialpad";
		} else if (a.getDataType() instanceof EnumerationType) {
			return "list";
		} else if (a.getDataType() instanceof BooleanType) {
			return "check_box";
		} else {
			return "text_fields";
		}
	}
	
	private static TransferObjectTable createTableForTransferObjectType(TransferObjectType transferObject, boolean masterDetail) {
        TransferObjectTable table = newTransferObjectTableBuilder()
        		.withMasterDetail(masterDetail)
				.withName(transferObject.getName())
				.withLabel(transferObject.getName().toUpperCase())
				.build();
		
		if (!transferObject.getAllAttributes().isEmpty()) {
			ArrayList<DataColumn> columns = new ArrayList<>();
			transferObject.getAllAttributes().stream().forEach(a -> {
				columns.add(newDataColumnBuilder().withName(a.getName()).withLabel(a.getName().toUpperCase())
						.withVisible(true).withDataFeature(a).build());
			});
			useTransferObjectTable(table).withColumns(columns);
		}
		return table;
	}
	
	public static TransferObjectTable setTableForTransferObjectType(TransferObjectType transferObject, boolean masterDetail) {
		TransferObjectTable table = createTableForTransferObjectType(transferObject, masterDetail);
		transferObject.setTable(table);
        return table;
	}
	
	private static TransferObjectView createViewForTransferObjectType(TransferObjectType transferObject) {
        TransferObjectView view = newTransferObjectViewBuilder()
				.withName(transferObject.getName() + "View")
				.withLabel(transferObject.getName() + "View")
				.withLayout(Layout.HORIZONTAL)
				.withHorizontal(Horizontal.LEFT)
				.withVertical(Vertical.TOP)
				.withFrame(false)
        		.build();
		
		
		if (!transferObject.getAllAttributes().isEmpty()) {
			ArrayList<DataField> dataFields = new ArrayList<>();
			transferObject.getAllAttributes().stream().forEach(a -> {
				dataFields.add(newDataFieldBuilder().withName(a.getName()).withLabel(a.getName().toUpperCase())
						.withIconName(getIconName(a)).withDataFeature(a).withCol(3).build());
			});
			useTransferObjectView(view).withComponents(dataFields).build();
		}
		if (!transferObject.getAllRelations().isEmpty()) {
			ArrayList<TabularReferenceField> tables = new ArrayList<>();
			transferObject.getAllRelations().stream().forEach(r -> {
				ArrayList<DataColumn> columns = new ArrayList<>();

				TabularReferenceField tabular = newTabularReferenceFieldBuilder().withName(r.getName())
						.withLabel(r.getName().toUpperCase()).withRelationFeature(r)
						.withCol(12).build();
				TransferObjectType target = (TransferObjectType) (r.getTarget());
				target.getAllAttributes().stream().forEach(a -> {
					columns.add(newDataColumnBuilder().withName(a.getName()).withLabel(a.getName().toUpperCase())
							.withVisible(true).withDataFeature(a).build());
				});
				tabular.getColumns().addAll(columns);
				tables.add(tabular);
			});
			useTransferObjectView(view).withComponents(tables).build();
		}
		if (!EsmUtils.getAllOperations(transferObject).isEmpty()) {
			ArrayList<OperationForm> operations = new ArrayList<>();
			EsmUtils.getAllOperations(transferObject).stream().forEach(o -> {
				operations.add(newOperationFormBuilder().withName(o.getName()).withLabel(o.getName().toUpperCase())
						.withCol(15).withStretch(Stretch.BOTH).withOperation(o.getName()).build());
			});
			useTransferObjectView(view).withComponents(operations).build();
		}
		return view;
	}
	
	public static TransferObjectView setViewForTransferObjectType(TransferObjectType transferObject) {
		TransferObjectView view = createViewForTransferObjectType(transferObject);
        transferObject.setView(view);
        return view;
	}
	
    public static void addUiElementsToTransferObjects(Model model) {
    	model.getElements().stream().filter(e -> e instanceof TransferObjectType).forEach(t -> {
    		setViewForTransferObjectType((TransferObjectType) t);
    		setFormForTransferObjectType((TransferObjectType) t);
    		setTableForTransferObjectType((TransferObjectType) t, false);
    	});
    }
}
