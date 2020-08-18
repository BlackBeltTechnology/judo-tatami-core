package hu.blackbelt.judo.tatami.esm2ui;

import static hu.blackbelt.judo.meta.esm.accesspoint.util.builder.AccesspointBuilders.newActorTypeBuilder;
import static hu.blackbelt.judo.meta.esm.namespace.util.builder.NamespaceBuilders.newModelBuilder;
import static hu.blackbelt.judo.meta.esm.namespace.util.builder.NamespaceBuilders.newPackageBuilder;
import static hu.blackbelt.judo.meta.esm.operation.util.builder.OperationBuilders.newInheritedOperationReferenceBuilder;
import static hu.blackbelt.judo.meta.esm.operation.util.builder.OperationBuilders.newOperationBuilder;
import static hu.blackbelt.judo.meta.esm.operation.util.builder.OperationBuilders.newParameterBuilder;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.newDataMemberBuilder;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.newEntityTypeBuilder;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.newGeneralizationBuilder;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.newMappingBuilder;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.newOneWayRelationMemberBuilder;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.newTransferObjectTypeBuilder;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.useEntityType;
import static hu.blackbelt.judo.meta.esm.structure.util.builder.StructureBuilders.useTransferObjectType;
import static hu.blackbelt.judo.meta.esm.type.util.builder.TypeBuilders.*;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newActionButtonBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newDataColumnBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newDataFieldBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newGroupBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newOperationFormBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newTabularReferenceFieldBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newTransferObjectFormBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newTransferObjectTableBuilder;
import static hu.blackbelt.judo.meta.esm.ui.util.builder.UiBuilders.newTransferObjectViewBuilder;

import java.util.Arrays;

import hu.blackbelt.judo.meta.esm.namespace.Model;
import hu.blackbelt.judo.meta.esm.namespace.Package;
import hu.blackbelt.judo.meta.esm.operation.Operation;
import hu.blackbelt.judo.meta.esm.operation.OperationType;
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
import hu.blackbelt.judo.meta.esm.ui.Horizontal;
import hu.blackbelt.judo.meta.esm.ui.Layout;
import hu.blackbelt.judo.meta.esm.ui.TransferObjectForm;
import hu.blackbelt.judo.meta.esm.ui.TransferObjectTable;
import hu.blackbelt.judo.meta.esm.ui.TransferObjectView;
import hu.blackbelt.judo.meta.esm.ui.Vertical;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class SimpleOrderModel {

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
        
        EntityType internationalOrder = newEntityTypeBuilder().withName("InternationalOrder")
        		.withGeneralizations(newGeneralizationBuilder().withTarget(order).build())
        		.withAttributes(internationalOrderDuty,orderCountry)
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
        		.withOperations(newOperationBuilder().withName("promote").withOperationType(OperationType.INSTANCE).withBinding("").build())
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
                .withActorType(newActorTypeBuilder().withRealm("public").build())
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
        useTransferObjectType(application).withRelations(applicationOrders).build();
        
        OneWayRelationMember applicationIntOrders = newOneWayRelationMemberBuilder()
        		.withName("internationalOrders")
        		.withTarget(internationalOrder)
        		.withMemberType(MemberType.DERIVED)
        		.withRelationKind(RelationKind.ASSOCIATION)
        		.withGetterExpression("SimpleOrder::InternationalOrder")
        		.withCreateable(true)
        		.withUpdateable(true)
        		.withDeleteable(true)
        		.withLower(0)
        		.withUpper(-1)
        		.build();
        useTransferObjectType(application).withRelations(applicationIntOrders).build();
        
        //Order Form
        TransferObjectForm orderForm = newTransferObjectFormBuilder()
            		.withName("orderForm")
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
                    					
                    				newDataFieldBuilder()
                    					.withName("received")
                    					.withLabel("Received")
                    					.withIconName("schedule")
                    					.withDataFeature(orderReceived)
                    					.build(),
                    					
                    				newTabularReferenceFieldBuilder()
                    					.withName("orderItems")
                    					.withLabel("Items")
                    					.withMaxVisibleElements(5)
                    					.withRelationFeature(orderItems)
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
            
            //Order Table
            TransferObjectTable orderTable = newTransferObjectTableBuilder()
            		.withMasterDetail(true)
    				.withName("OrderTable")
    				.withLabel("Order")
    				.withMaxVisibleElements(5)
    				.withColumns(Arrays.asList(
        				newDataColumnBuilder()
    						.withName("orderDate")
    						.withLabel("Date")
    						.withVisible(true)
    						.withDataFeature(orderDate)
    						.build(),
    					newDataColumnBuilder()
    						.withName("customer")
    						.withLabel("Customer")
    						.withVisible(true)
    						.withDataFeature(orderCustomer)
    						.build(),
    					newDataColumnBuilder()
        					.withName("received")
        					.withLabel("Received")
        					.withVisible(true)
        					.withDataFeature(orderReceived)
        					.build()
    				))
    				.build();
            order.setTable(orderTable);        

            //Order View
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
            				
            				newDataFieldBuilder()
            					.withName("received")
            					.withLabel("Received")
            					.withIconName("schedule")
            					.withDataFeature(orderReceived)
            					.build(),

            				newTabularReferenceFieldBuilder()
            					.withName("orderItems")
            					.withLabel("Items")
            					.withMaxVisibleElements(5)
            					.withRelationFeature(orderItems)
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
            					.build(),
            				newOperationFormBuilder()
            					.withName("archive")
            					.withLabel("archive")
            					.withOperation("archive")
            					.build(),
            				newOperationFormBuilder()
            					.withName("returnDamagedItems")
            					.withLabel("returnDamagedItems")
            					.withOperation("returnDamagedItems")
            					.build()
    				))
            		.build();
            order.setView(orderView);
            
            //Order Item View
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
    	            						.build()))
            		.build();
            orderItem.setView(orderItemView);
            
            //Order Item Form
            TransferObjectForm orderItemForm = newTransferObjectFormBuilder()
                		.withName("OrderItemForm")
                		.withLabel("OrderItemForm")
                		.withComponents(Arrays.asList(
            				newGroupBuilder()
                        		.withName("Content")
                        		.withLabel("Deliverer")
                        		.withLayout(Layout.HORIZONTAL)
                        		.withWrap(true)
                        		.withHorizontal(Horizontal.LEFT)
                        		.withVertical(Vertical.TOP)
                        		.withFrame(true)
                        		.withComponents(Arrays.asList(
                        				newDataFieldBuilder()
                        					.withName("quantity")
                        					.withLabel("Quantity")
                        					.withIconName("dialpad")
                        					.withDataFeature(orderItemQuantity)
                        					.build(),
                        				newDataFieldBuilder()
                							.withName("price")
                							.withLabel("Price")
                							.withIconName("dialpad")
                							.withDataFeature(orderItemPrice)
                        					.build(),
                        				newDataFieldBuilder()
                							.withName("product")
                							.withLabel("Product")
                							.withIconName("text_fields")
                							.withDataFeature(orderItemProduct)
                        					.build()))
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
            orderItem.setForm(orderItemForm);
            
            //Order Item Table
            TransferObjectTable orderItemTable = newTransferObjectTableBuilder()
            		.withMasterDetail(true)
    				.withName("OrderItemTable")
    				.withLabel("OrderItemTable")
    				.withMaxVisibleElements(5)
    				.withColumns(Arrays.asList(
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
    						.build(),
    					newDataColumnBuilder()
    						.withName("product")
    						.withLabel("Product")
    						.withVisible(true)
    						.withDataFeature(orderItemProduct)
    						.build()
    				))
    				.build();
            orderItem.setTable(orderItemTable);

        //International Order View
        TransferObjectView intOrderView = newTransferObjectViewBuilder()
				.withName("internationalOrderView")
				.withLabel("International Order")
        		.withComponents(Arrays.asList(
	                				newDataFieldBuilder()
	            						.withName("orderDate")
	            						.withLabel("Order Date")
	            						.withDataFeature(orderDate)
	            						.build(),
	        						newDataFieldBuilder()
	            						.withName("customer")
	            						.withLabel("Customer")
	            						.withDataFeature(orderCustomer)
	            						.build(),
	        						newDataFieldBuilder()
	            						.withName("tax")
	            						.withLabel("Tax")
	            						.withDataFeature(internationalOrderDuty)
	            						.build(),
	            					newDataFieldBuilder()
	            						.withName("country")
	            						.withLabel("Country")
	            						.withDataFeature(orderCountry)
	            						.build(),
	            					newDataFieldBuilder()
                    					.withName("received")
                    					.withLabel("Received")
                    					.withDataFeature(orderReceived)
                    					.build(),
	            						
	            					newTabularReferenceFieldBuilder()
                    					.withName("items")
                    					.withLabel("Items")
                    					.withMaxVisibleElements(5)
                    					.withRelationFeature(orderItems)
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
                    					.build(),
                    			   newOperationFormBuilder()
                    					.withName("archive")
                    					.withLabel("archive")
                    					.withOperation("archive")
                    					.build(),
                    				newOperationFormBuilder()
                    					.withName("returnDamagedItems")
                    					.withLabel("returnDamagedItems")
                    					.withOperation("returnDamagedItems")
                    					.build()
				))
        		.build();
        internationalOrder.setView(intOrderView);
        
        //International Order Form
        TransferObjectForm internationalOrderForm = newTransferObjectFormBuilder()
        		.withName("InternationalOrderForm")
        		.withLabel("InternationalOrderForm")
        		.withComponents(Arrays.asList(
    				newGroupBuilder()
                		.withName("Content")
                		.withLayout(Layout.HORIZONTAL)
                		.withWrap(true)
                		.withHorizontal(Horizontal.LEFT)
                		.withVertical(Vertical.TOP)
                		.withFrame(true)
                		.withComponents(Arrays.asList(
                				newDataFieldBuilder()
                					.withName("duty")
                					.withLabel("Duty")
                					.withIconName("dialpad")
                					.withDataFeature(internationalOrderDuty)
                					.build(),
                				newDataFieldBuilder()
        							.withName("orderDate")
        							.withLabel("Order Date")
        							.withIconName("calendar_today")
        							.withDataFeature(orderDate)
                					.build(),
                				newDataFieldBuilder()
        							.withName("customer")
        							.withLabel("Customer")
        							.withIconName("text_fields")
        							.withDataFeature(orderCustomer)
                					.build(),
                				newDataFieldBuilder()
        							.withName("country")
        							.withLabel("Country")
        							.withIconName("list")
        							.withDataFeature(orderCountry)
                					.build(),
                				
                				newDataFieldBuilder()
                					.withName("received")
                					.withLabel("Received")
                					.withIconName("schedule")
                					.withDataFeature(orderReceived)
                					.build(),
                				
                				newTabularReferenceFieldBuilder()
                					.withName("items")
                					.withLabel("Items")
                					.withMaxVisibleElements(5)
                					.withRelationFeature(orderItems)
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
                					.build()))
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
        internationalOrder.setForm(internationalOrderForm);
        
        //International Order Table
        TransferObjectTable internationalOrderTable = newTransferObjectTableBuilder()
        		.withMasterDetail(true)
				.withName("OrderTable")
				.withLabel("Order")
				.withMaxVisibleElements(5)
				.withColumns(Arrays.asList(
    				newDataColumnBuilder()
						.withName("orderDate")
						.withLabel("Date")
						.withVisible(true)
						.withDataFeature(orderDate)
						.build(),
					newDataColumnBuilder()
						.withName("customer")
						.withLabel("Customer")
						.withVisible(true)
						.withDataFeature(orderCustomer)
						.build(),
					newDataColumnBuilder()
						.withName("duty")
						.withLabel("Duty")
						.withVisible(true)
						.withDataFeature(internationalOrderDuty)
						.build(),
						
					newDataColumnBuilder()
						.withName("country")
						.withLabel("Country")
						.withVisible(true)
						.withDataFeature(orderCountry)
    					.build(),
    				
    				newDataColumnBuilder()
    					.withName("received")
    					.withLabel("Received")
    					.withVisible(true)
    					.withDataFeature(orderReceived)
    					.build()
				))
				.build();
        internationalOrder.setTable(internationalOrderTable);
        
        //ArchiverForm
        TransferObjectForm archiverForm = newTransferObjectFormBuilder()
         		.withName("ArchiverForm")
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
                 					.withName("archiverName")
                 					.withLabel("Name")
                 					.withIconName("text_fields")
                 					.withDataFeature(employeeName)
                 					.build(),
                 				newDataFieldBuilder()
         							.withName("archiverID")
         							.withLabel("ID")
         							.withIconName("text_fields")
         							.withDataFeature(employeeId)
                 					.build()))
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
         archiver.setForm(archiverForm);
         
         //ArchiverTable
         TransferObjectTable archiverTable = newTransferObjectTableBuilder()
         		.withMasterDetail(true)
 				.withName("ArchiverTable")
 				.withLabel("Order")
 				.withMaxVisibleElements(5)
 				.withColumns(Arrays.asList(
     				newDataColumnBuilder()
 						.withName("archiverName")
 						.withLabel("Name")
 						.withVisible(true)
 						.withDataFeature(employeeName)
 						.build(),
 					newDataColumnBuilder()
 						.withName("archiverId")
 						.withLabel("ID")
 						.withVisible(true)
 						.withDataFeature(employeeId)
 						.build()
 				))
 				.build();
         archiver.setTable(archiverTable);
         
         //ArchiverView
         TransferObjectView archiverView = newTransferObjectViewBuilder()
 				.withName("ArchiverView")
 				.withLabel("Archiver")
         		.withComponents(Arrays.asList(
         				newDataFieldBuilder()
         					.withName("archiverName")
         					.withLabel("Name")
         					.withIconName("text_fields")
         					.withDataFeature(employeeName)
         					.build(),
         				newDataFieldBuilder()
 							.withName("archiverId")
 							.withLabel("ID")
 							.withIconName("text_fields")
 							.withDataFeature(employeeId)
         					.build(),
         				newOperationFormBuilder()
        					.withName("reviewDamagedProducts")
        					.withLabel("reviewDamagedProducts")
        					.withOperation("reviewDamagedProducts")
        					.build()
 				))
         		.build();
         archiver.setView(archiverView);
         
         //Archived Order Form
         TransferObjectForm archivedOrderForm = newTransferObjectFormBuilder()
          		.withName("ArchivedOrderForm")
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
                  					.withName("archivalDate")
                  					.withLabel("Archival Date")
                  					.withIconName("calendar_today")
                  					.withDataFeature(archivedOrderArchivalDate)
                  					.build(),
                  				newDataFieldBuilder()
          							.withName("orderDate")
          							.withLabel("Order Date")
          							.withIconName("calendar_today")
          							.withDataFeature(archivedOrderOrderDate)
                  					.build(),
                  				newDataFieldBuilder()
          							.withName("customer")
          							.withLabel("Customer")
          							.withIconName("text_fields")
          							.withDataFeature(archivedOrderCustomer)
                  					.build(),
                  				newTabularReferenceFieldBuilder()
                					.withName("items")
                					.withLabel("Items")
                					.withMaxVisibleElements(5)
                					.withRelationFeature(archivedOrderItems)
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
                    						.build(),
                    					newDataColumnBuilder()
                    						.withName("review")
                    						.withLabel("Review")
                    						.withVisible(true)
                    						.withDataFeature(archivedOrderItemReview)
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
         archivedOrder.setForm(archivedOrderForm);
         
         //Archived Order View
         TransferObjectView archivedOrderView = newTransferObjectViewBuilder()
 				.withName("ArchivedOrderView")
 				.withLabel("Archived Order")
         		.withComponents(Arrays.asList(
         				newDataFieldBuilder()
         					.withName("customer")
         					.withLabel("Customer")
         					.withIconName("text_fields")
         					.withDataFeature(archivedOrderCustomer)
         					.build(),

         				newDataFieldBuilder()
 							.withName("orderDate")
 							.withLabel("Order Date")
 							.withIconName("calendar_today")
 							.withDataFeature(archivedOrderOrderDate)
         					.build(),
         					
         				newDataFieldBuilder()
 							.withName("archivalDate")
 							.withLabel("Archival Date")
 							.withIconName("calendar_today")
 							.withDataFeature(archivedOrderArchivalDate)
         					.build(),

         				newTabularReferenceFieldBuilder()
         					.withName("orderItems")
         					.withLabel("Items")
         					.withMaxVisibleElements(5)
         					.withRelationFeature(archivedOrderItems)
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
             						.build(),
             					newDataColumnBuilder()
             						.withName("review")
             						.withLabel("Review")
             						.withVisible(true)
             						.withDataFeature(archivedOrderItemReview)
             						.build()
         					))
         					.build()
 				))
         		.build();
         archivedOrder.setView(archivedOrderView);
         
         //Archived Order Table
         TransferObjectTable archivedOrderTable = newTransferObjectTableBuilder()
         		.withMasterDetail(true)
 				.withName("ArchivedOrderTable")
 				.withLabel("Archived Order")
 				.withMaxVisibleElements(5)
 				.withColumns(Arrays.asList(
     				newDataColumnBuilder()
 						.withName("archivalDate")
 						.withLabel("archivalDate")
 						.withVisible(true)
 						.withDataFeature(archivedOrderArchivalDate)
 						.build(),
 					newDataColumnBuilder()
 						.withName("orderDate")
 						.withLabel("orderDate")
 						.withVisible(true)
 						.withDataFeature(archivedOrderOrderDate)
 						.build(),
 					newDataColumnBuilder()
 						.withName("customer")
 						.withLabel("customer")
 						.withVisible(true)
 						.withDataFeature(archivedOrderCustomer)
 						.build()))
 				.build();
         archivedOrder.setTable(archivedOrderTable);
         
         //Archived Order Item View
         TransferObjectView archivedOrderItemView = newTransferObjectViewBuilder()
 				.withName("ArchivedOrderItemView")
 				.withLabel("Archived Order Item")
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
 	            						.build(),
 	            				    newDataFieldBuilder()
 	            						.withName("review")
 	            						.withLabel("review")
 	            						.withDataFeature(archivedOrderItemReview)
 	            						.build()))
         		.build();
         archivedOrderItem.setView(archivedOrderItemView);
         
         //Archived Order Item Form
         TransferObjectForm archivedOrderItemForm = newTransferObjectFormBuilder()
             		.withName("ArchivedOrderItemForm")
             		.withLabel("ArchivedOrderItemForm")
             		.withComponents(Arrays.asList(
         				newGroupBuilder()
                     		.withName("Content")
                     		.withLabel("Deliverer")
                     		.withLayout(Layout.HORIZONTAL)
                     		.withWrap(true)
                     		.withHorizontal(Horizontal.LEFT)
                     		.withVertical(Vertical.TOP)
                     		.withFrame(true)
                     		.withComponents(Arrays.asList(
                     				newDataFieldBuilder()
                     					.withName("quantity")
                     					.withLabel("Quantity")
                     					.withIconName("dialpad")
                     					.withDataFeature(orderItemQuantity)
                     					.build(),
                     				newDataFieldBuilder()
             							.withName("price")
             							.withLabel("Price")
             							.withIconName("dialpad")
             							.withDataFeature(orderItemPrice)
                     					.build(),
                     				newDataFieldBuilder()
             							.withName("product")
             							.withLabel("Product")
             							.withIconName("text_fields")
             							.withDataFeature(orderItemProduct)
                     					.build(),
                     			    newDataFieldBuilder()
 	            						.withName("review")
 	            						.withLabel("review")
 	            						.withIconName("dialpad")
 	            						.withDataFeature(archivedOrderItemReview)
 	            						.build()))
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
         archivedOrderItem.setForm(archivedOrderItemForm);
         
         //Archived Order item Table
         TransferObjectTable archivedOrderItemTable = newTransferObjectTableBuilder()
         		.withMasterDetail(true)
 				.withName("ArchivedOrderItemTable")
 				.withLabel("Archived Order Item")
 				.withMaxVisibleElements(5)
				.withColumns(Arrays.asList(
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
						.build(),
					newDataColumnBuilder()
						.withName("product")
						.withLabel("Product")
						.withVisible(true)
						.withDataFeature(orderItemProduct)
						.build(),
				    newDataColumnBuilder()
						.withName("review")
						.withLabel("Review")
						.withVisible(true)
						.withDataFeature(archivedOrderItemReview)
						.build()
				))
				.build();
         archivedOrderItem.setTable(archivedOrderItemTable);
         
         //Returned Item Table
         TransferObjectTable returnedItemTable = newTransferObjectTableBuilder()
         		.withMasterDetail(true)
 				.withName("ReturedItemTable")
 				.withLabel("ReturnedItemTable")
 				.withMaxVisibleElements(5)
 				.withColumns(Arrays.asList(
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
 						.build(),
 					newDataColumnBuilder()
 						.withName("product")
 						.withLabel("Product")
 						.withVisible(true)
 						.withDataFeature(orderItemProduct)
 						.build(),
 				    newDataColumnBuilder()
 						.withName("isDamaged")
 						.withLabel("Damaged")
 						.withVisible(true)
 						.withDataFeature(returnedItemsIsDamaged)
 						.build(),
 					newDataColumnBuilder()
 						.withName("comment")
 						.withLabel("Comment")
 						.withVisible(true)
 						.withDataFeature(returnedItemsComment)
 						.build()
 				))
 				.build();
         returnedItem.setTable(returnedItemTable);
         
         //Returned Item View
         TransferObjectView returnedItemView = newTransferObjectViewBuilder()
 				.withName("ReturnedItemView")
 				.withLabel("Returned Item")
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
 	            						.build(),
 	            				    newDataFieldBuilder()
 	            						.withName("isDamaged")
 	            						.withLabel("Damaged")
 	            						.withDataFeature(returnedItemsIsDamaged)
 	            						.build(),
 	            				    newDataFieldBuilder()
 	            						.withName("comment")
 	            						.withLabel("Comment")
 	            						.withDataFeature(returnedItemsComment)
 	            						.build(),
 	            					newOperationFormBuilder()
 	               						.withName("donate")
 	               						.withLabel("donate")
 	               						.withOperation("donate")
 	               						.build()))
         		.build();
         returnedItem.setView(returnedItemView);
         
         //Returned Item Form
         TransferObjectForm returnedItemForm = newTransferObjectFormBuilder()
             		.withName("ReturnedItemForm")
             		.withLabel("ReturnedItemForm")
             		.withComponents(Arrays.asList(
         				newGroupBuilder()
                     		.withName("Content")
                     		.withLabel("Deliverer")
                     		.withLayout(Layout.HORIZONTAL)
                     		.withWrap(true)
                     		.withHorizontal(Horizontal.LEFT)
                     		.withVertical(Vertical.TOP)
                     		.withFrame(true)
                     		.withComponents(Arrays.asList(
                     				newDataFieldBuilder()
                     					.withName("quantity")
                     					.withLabel("Quantity")
                     					.withIconName("dialpad")
                     					.withDataFeature(orderItemQuantity)
                     					.build(),
                     				newDataFieldBuilder()
             							.withName("price")
             							.withLabel("Price")
             							.withIconName("dialpad")
             							.withDataFeature(orderItemPrice)
                     					.build(),
                     				newDataFieldBuilder()
             							.withName("product")
             							.withLabel("Product")
             							.withIconName("text_fields")
             							.withDataFeature(orderItemProduct)
                     					.build(),
                     			    newDataFieldBuilder()
 	            						.withName("isDamaged")
 	            						.withLabel("Damaged")
 	            						.withIconName("check_box")
 	            						.withDataFeature(returnedItemsIsDamaged)
 	            						.build(),
 	            					newDataFieldBuilder()
             							.withName("comment")
             							.withLabel("Comment")
             							.withIconName("text_fields")
             							.withDataFeature(returnedItemsComment)
                     					.build()))
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
         returnedItem.setForm(returnedItemForm);
         
         //Deliverer Form
         TransferObjectForm delivererForm = newTransferObjectFormBuilder()
             		.withName("DelivererForm")
             		.withComponents(Arrays.asList(
         				newGroupBuilder()
                     		.withName("Content")
                     		.withLabel("Deliverer")
                     		.withLayout(Layout.HORIZONTAL)
                     		.withWrap(true)
                     		.withHorizontal(Horizontal.LEFT)
                     		.withVertical(Vertical.TOP)
                     		.withFrame(true)
                     		.withComponents(Arrays.asList(
                     				newDataFieldBuilder()
                     					.withName("name")
                     					.withLabel("Name")
                     					.withIconName("text_fields")
                     					.withDataFeature(employeeName)
                     					.build(),
                     				newDataFieldBuilder()
             							.withName("id")
             							.withLabel("ID")
             							.withIconName("text_fields")
             							.withDataFeature(employeeId)
                     					.build()))
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
             deliverer.setForm(delivererForm); 
        
        //Deliverer Table
        TransferObjectTable delivererTable = newTransferObjectTableBuilder()
        		.withMasterDetail(true)
				.withName("DelivererTable")
				.withLabel("Deliverer")
				.withMaxVisibleElements(5)
				.withColumns(Arrays.asList(
    				newDataColumnBuilder()
						.withName("name")
						.withLabel("Name")
						.withVisible(true)
						.withDataFeature(employeeName)
						.build(),
					newDataColumnBuilder()
						.withName("id")
						.withLabel("ID")
						.withVisible(true)
						.withDataFeature(employeeId)
						.build()))
				.build();
        deliverer.setTable(delivererTable);
        
        //Deliverer View
        TransferObjectView delivererView = newTransferObjectViewBuilder()
				.withName("DelivererView")
				.withLabel("Deliverer")
        		.withComponents(Arrays.asList(
	                				newDataFieldBuilder()
	            						.withName("name")
	            						.withLabel("Name")
	            						.withDataFeature(employeeName)
	            						.build(),
	        						newDataFieldBuilder()
	            						.withName("id")
	            						.withLabel("ID")
	            						.withDataFeature(employeeId)
	            						.build()
	            						
        				
        				))
        		.build();
        deliverer.setView(delivererView);
         
        //Complaint Form
        TransferObjectForm complaintForm = newTransferObjectFormBuilder()
            		.withName("ComplaintForm")
            		.withComponents(Arrays.asList(
        				newGroupBuilder()
                    		.withName("Content")
                    		.withLabel("Complaint")
                    		.withLayout(Layout.HORIZONTAL)
                    		.withWrap(true)
                    		.withHorizontal(Horizontal.LEFT)
                    		.withVertical(Vertical.TOP)
                    		.withFrame(true)
                    		.withComponents(Arrays.asList(
                    				newDataFieldBuilder()
                    					.withName("isAngry")
                    					.withLabel("Angry")
                    					.withIconName("check_box")
                    					.withDataFeature(complaintIsAngry)
                    					.build(),
                    				newDataFieldBuilder()
            							.withName("product")
            							.withLabel("Product")
            							.withIconName("text_fields")
            							.withDataFeature(complaintProduct)
                    					.build()))
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
            complaint.setForm(complaintForm); 
       
       //Complaint Table
       TransferObjectTable complaintTable = newTransferObjectTableBuilder()
       		.withMasterDetail(true)
				.withName("ComplaintTable")
				.withLabel("Complaint")
				.withMaxVisibleElements(5)
				.withColumns(Arrays.asList(
   				newDataColumnBuilder()
						.withName("isAngry")
						.withLabel("Angry")
						.withVisible(true)
						.withDataFeature(complaintIsAngry)
						.build(),
					newDataColumnBuilder()
						.withName("product")
						.withLabel("Product")
						.withVisible(true)
						.withDataFeature(complaintProduct)
						.build()))
				.build();
       complaint.setTable(complaintTable);
       
       //Complaint View
       TransferObjectView complaintView = newTransferObjectViewBuilder()
				.withName("ComplaintView")
				.withLabel("Complaint")
       		.withComponents(Arrays.asList(
				newDataFieldBuilder()
					.withName("isAngry")
					.withLabel("Angry")
					.withDataFeature(complaintIsAngry)
					.build(),
				newDataFieldBuilder()
					.withName("product")
					.withLabel("Product")
					.withDataFeature(complaintProduct)
					.build()))
       		.build();
       complaint.setView(complaintView);
         
       //Damaged product Form
       TransferObjectForm damagedProductForm = newTransferObjectFormBuilder()
           		.withName("DamagedForm")
           		.withComponents(Arrays.asList(
       				newGroupBuilder()
                   		.withName("Content")
                   		.withLabel("Complaint")
                   		.withLayout(Layout.HORIZONTAL)
                   		.withWrap(true)
                   		.withHorizontal(Horizontal.LEFT)
                   		.withVertical(Vertical.TOP)
                   		.withFrame(true)
                   		.withComponents(Arrays.asList(
                   				newDataFieldBuilder()
                   					.withName("complaint")
                   					.withLabel("Complaint")
                   					.withIconName("text_fields")
                   					.withDataFeature(damagedProductComplaint)
                   					.build(),
                   				newDataFieldBuilder()
           							.withName("product")
           							.withLabel("Product")
           							.withIconName("text_fields")
           							.withDataFeature(damagedProductProduct)
                   					.build()))
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
           damagedProduct.setForm(damagedProductForm); 
      
      //Damaged product Table
      TransferObjectTable damagedProductTable = newTransferObjectTableBuilder()
      		.withMasterDetail(true)
				.withName("ComplaintTable")
				.withLabel("Complaint")
				.withMaxVisibleElements(5)
				.withColumns(Arrays.asList(
  				newDataColumnBuilder()
						.withName("complaint")
						.withLabel("Complaint")
						.withVisible(true)
						.withDataFeature(damagedProductComplaint)
						.build(),
					newDataColumnBuilder()
						.withName("product")
						.withLabel("Product")
						.withVisible(true)
						.withDataFeature(damagedProductProduct)
						.build()))
				.build();
      damagedProduct.setTable(damagedProductTable);
      
      //Damaged Product View
      TransferObjectView damagedProductView = newTransferObjectViewBuilder()
				.withName("ComplaintView")
				.withLabel("Complaint")
      		.withComponents(Arrays.asList(
				newDataFieldBuilder()
					.withName("complaint")
					.withLabel("Complaint")
					.withDataFeature(damagedProductComplaint)
					.build(),
				newDataFieldBuilder()
					.withName("product")
					.withLabel("Product")
					.withDataFeature(damagedProductProduct)
					.build()))
      		.build();
      damagedProduct.setView(damagedProductView);
      
    //Donated item Table
      TransferObjectTable donatedItemTable = newTransferObjectTableBuilder()
      		.withMasterDetail(true)
				.withName("donatedItemTable")
				.withLabel("Donated Items")
				.withMaxVisibleElements(5)
				.withColumns(Arrays.asList(
  				newDataColumnBuilder()
						.withName("receiver")
						.withLabel("Receiver")
						.withVisible(true)
						.withDataFeature(donatedItemReceiver)
						.build()
				))
				.build();
      donatedItem.setTable(donatedItemTable);        

      //Donated item View
      TransferObjectView donatedItemView = newTransferObjectViewBuilder()
				.withName("donatedItemView")
				.withLabel("Donated Item")
      		.withComponents(Arrays.asList(
      				newDataFieldBuilder()
      					.withName("receiver")
      					.withLabel("Receiver")
      					.withIconName("text_fields")
      					.withDataFeature(donatedItemReceiver)
      					.build()))
      		.build();
      donatedItem.setView(donatedItemView);
        
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
        
        Package types = newPackageBuilder().withName("types").withElements(stringType,integerType,floatType,dateType,boolType,tsType,enumType).build();
        
        // Create model
		Model model = newModelBuilder()
				.withName("SimpleOrder")
				.withElements(types)
				.withElements(order, orderItem, internationalOrder,
						application,
						employee, archiver,
						archivedOrder,
						archivedOrderItem,
						returnedItem, donatedItem,
						deliverer,
						complaint,damagedProduct)
				.withDemoAccessPoint(false)
				.build();

        return model;
	}
}
