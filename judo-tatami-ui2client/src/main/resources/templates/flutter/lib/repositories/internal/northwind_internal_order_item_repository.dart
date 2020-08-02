import 'package:judo/rest/internal/api.dart';
import 'package:judo/store/internal/northwind_internal_international_order_info_store.dart';
import 'package:judo/store/internal/northwind_internal_order_item_store.dart';
import 'package:judo/utilities/constants.dart';
import 'package:openapi_dart_common/openapi.dart';

class NorthwindInternalOrderItemRepository {
  final ApiClient _apiClient =
      ApiClient(basePath: kBasePathUrl, apiClientDelegate: DioClientDelegate());

  Future removeItem(NorthwindInternalInternationalOrderInfoStore orderInfo,
      NorthwindInternalOrderItemStore deletedOrderItem) async {
    NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInput
        internationalOrdersInput =
        NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInput();

    internationalOrdersInput.identifier = orderInfo.identifier;

    internationalOrdersInput.items = List<
        NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInputItems>();

    NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInputItems
        ordersInputItem =
        NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInputItems();
    ordersInputItem.identifier = deletedOrderItem.identifier;
    internationalOrdersInput.items.add(ordersInputItem);

    await DefaultApi(_apiClient)
        .northwindInternalAPRemoveItemsFromAllInternationalOrders(
            internationalOrdersInput);
  }

  ////////////////////////////////////////////////////////////////////////////

  Future createItem(NorthwindInternalInternationalOrderInfoStore orderInfo,
      NorthwindInternalOrderItemStore newOrderItem) async {
    NorthwindServicesOrderItemExtended northwindServicesOrderItemExtended =
        NorthwindServicesOrderItemExtended();
    northwindServicesOrderItemExtended.unitPrice = newOrderItem.unitPrice;

    northwindServicesOrderItemExtended.category =
        NorthwindServicesProductInfoExtendedCategory();
    northwindServicesOrderItemExtended.category.identifier =
        newOrderItem.product.category.identifier;

    northwindServicesOrderItemExtended.product =
        NorthwindServicesCategoryInfoExtendedProducts();
    northwindServicesOrderItemExtended.product.identifier =
        newOrderItem.product.identifier;

    northwindServicesOrderItemExtended.quantity = newOrderItem.quantity;
    northwindServicesOrderItemExtended.discount = newOrderItem.discount;

    NorthwindServicesOrderItem northwindServicesOrderItem =
        await AllInternationalOrdersApi(_apiClient)
            .northwindServicesOrderInfoCreateItems(
                orderInfo.identifier, northwindServicesOrderItemExtended);

    newOrderItem.identifier = northwindServicesOrderItem.identifier;
    newOrderItem.price = northwindServicesOrderItem.price;
    newOrderItem.categoryName = northwindServicesOrderItem.categoryName;
    newOrderItem.productName = northwindServicesOrderItem.productName;
  }
}
