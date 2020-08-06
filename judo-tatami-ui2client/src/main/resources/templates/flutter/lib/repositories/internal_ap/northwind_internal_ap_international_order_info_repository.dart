import 'package:judo/rest/internal_ap/lib/api.dart';
import 'package:judo/store/internal_ap/northwind_internal_ap_international_order_info_store.dart';
import 'package:judo/store/internal_ap/northwind_internal_ap_order_item_store.dart';
import 'package:judo/store/internal_ap/northwind_internal_ap_shipper_info_store.dart';
import 'package:judo/utilities/constants.dart';
import 'package:openapi_dart_common/openapi.dart';

class NorthwindInternalInternationalOrderInfoRepository {
  final ApiClient _apiClient =
      ApiClient(basePath: kBasePathUrl, apiClientDelegate: DioClientDelegate());

  Future createOrder(
      NorthwindInternalInternationalOrderInfoStore orderInfo) async {
    NorthwindServicesInternationalOrderInfoExtended
        internationalOrderInfoExtended =
        NorthwindServicesInternationalOrderInfoExtended();

    internationalOrderInfoExtended.items =
        orderInfo.items.map<NorthwindServicesOrderItemExtended>((e) {
      NorthwindServicesOrderItemExtended orderItemExtended =
          NorthwindServicesOrderItemExtended();

      orderItemExtended.unitPrice = e.unitPrice;

      orderItemExtended.category =
          NorthwindServicesProductInfoExtendedCategory();
      orderItemExtended.category.identifier = e.product.category.identifier;

      orderItemExtended.product =
          NorthwindServicesCategoryInfoExtendedProducts();
      orderItemExtended.product.identifier = e.product.identifier;

      orderItemExtended.quantity = e.quantity;
      orderItemExtended.discount = e.discount;

      return orderItemExtended;
    }).toList();

    internationalOrderInfoExtended.shipper =
        NorthwindServicesShipperInfoExtended();
    internationalOrderInfoExtended.shipper.identifier =
        orderInfo.shipper.identifier;

    internationalOrderInfoExtended.orderDate = orderInfo.orderDate;
    internationalOrderInfoExtended.customsDescription =
        orderInfo.customsDescription;
    internationalOrderInfoExtended.exciseTax = orderInfo.exciseTax;

    NorthwindServicesInternationalOrderInfo internationalOrderInfo =
        await DefaultApi(_apiClient)
            .northwindInternalAPCreateAllInternationalOrders(
                internationalOrderInfoExtended);

    orderInfo.identifier = internationalOrderInfo.identifier;
    orderInfo.totalPrice = internationalOrderInfo.totalPrice;
    orderInfo.totalWeight = internationalOrderInfo.totalWeight;
    orderInfo.shipperName = internationalOrderInfo.shipperName;
    orderInfo.customsDescription = internationalOrderInfo.customsDescription;
    orderInfo.orderDate = internationalOrderInfo.orderDate;
    orderInfo.exciseTax = internationalOrderInfo.exciseTax;
    orderInfo.items.forEach((e) {
      NorthwindServicesOrderItem northwindOrderItem =
          internationalOrderInfo.items.firstWhere((element) =>
              element.quantity == e.quantity &&
              element.productName == e.product.productName);
      e.identifier = northwindOrderItem.identifier;
      e.price = northwindOrderItem.price;
      e.categoryName = northwindOrderItem.categoryName;
      e.productName = northwindOrderItem.productName;
    });
  }

  ////////////////////////////////////////////////////////////////////////////

  Future updateOrder(
      NorthwindInternalInternationalOrderInfoStore orderInfo) async {
    NorthwindServicesInternationalOrderInfo
        northwindServicesInternationalOrderInfo =
        NorthwindServicesInternationalOrderInfo();

    northwindServicesInternationalOrderInfo.items =
        orderInfo.items.map<NorthwindServicesOrderItem>((e) {
      NorthwindServicesOrderItem northwindServicesOrderItem =
          NorthwindServicesOrderItem();

      northwindServicesOrderItem.identifier = e.identifier;
      northwindServicesOrderItem.price = e.price;
      northwindServicesOrderItem.categoryName = e.categoryName;
      northwindServicesOrderItem.productName = e.productName;
      northwindServicesOrderItem.unitPrice = e.unitPrice;
      northwindServicesOrderItem.quantity = e.quantity;
      northwindServicesOrderItem.discount = e.discount;

      return northwindServicesOrderItem;
    }).toList();

    northwindServicesInternationalOrderInfo.identifier = orderInfo.identifier;
    northwindServicesInternationalOrderInfo.orderDate = orderInfo.orderDate;
    northwindServicesInternationalOrderInfo.customsDescription =
        orderInfo.customsDescription;
    northwindServicesInternationalOrderInfo.exciseTax = orderInfo.exciseTax;

    northwindServicesInternationalOrderInfo = await DefaultApi(_apiClient)
        .northwindInternalAPUpdateAllInternationalOrders(
            northwindServicesInternationalOrderInfo);

    orderInfo.identifier = northwindServicesInternationalOrderInfo.identifier;
    orderInfo.orderDate = northwindServicesInternationalOrderInfo.orderDate;
    orderInfo.customsDescription =
        northwindServicesInternationalOrderInfo.customsDescription;
    orderInfo.exciseTax = northwindServicesInternationalOrderInfo.exciseTax;
    orderInfo.shipperName = northwindServicesInternationalOrderInfo.shipperName;
    orderInfo.totalPrice = northwindServicesInternationalOrderInfo.totalPrice;
    orderInfo.totalWeight = northwindServicesInternationalOrderInfo.totalWeight;
  }

  ////////////////////////////////////////////////////////////////////////////

  Future changeShipper(NorthwindInternalInternationalOrderInfoStore orderInfo,
      NorthwindInternalShipperInfoStore newShipper) async {
    NorthwindInternalAPSetShipperOfAllInternationalOrdersInput setShipper =
        NorthwindInternalAPSetShipperOfAllInternationalOrdersInput();
    setShipper.identifier = orderInfo.identifier;

    setShipper.shipper =
        NorthwindInternalAPSetShipperOfAllInternationalOrdersInputShipper();
    setShipper.shipper.identifier = newShipper.identifier;

    await DefaultApi(_apiClient)
        .northwindInternalAPSetShipperOfAllInternationalOrders(setShipper);
  }

////////////////////////////////////////////////////////////////////////////
}
