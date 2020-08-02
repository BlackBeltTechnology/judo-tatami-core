import 'package:judo/repositories/internal/northwind_internal_category_info_repository.dart';
import 'package:judo/repositories/internal/northwind_internal_international_order_info_repository.dart';
import 'package:judo/repositories/internal/northwind_internal_order_item_repository.dart';
import 'package:judo/repositories/internal/northwind_internal_product_info_repository.dart';
import 'package:judo/repositories/internal/northwind_internal_shipper_info_repository.dart';
import 'package:judo/store/internal/northwind_internal_order_item_store.dart';
import 'package:judo/store/internal/northwind_internal_shipper_info_store.dart';
import 'package:mobx/mobx.dart';

import 'northwind_internal_category_info_store.dart';
import 'northwind_internal_comment_store.dart';
import 'northwind_internal_international_order_info_store.dart';
import 'northwind_internal_product_info_store.dart';

part 'northwind_internal_ap_store.g.dart';

class NorthwindInternalApStore extends _NorthwindInternalApStore
    with _$NorthwindInternalApStore {}

// TODO: change 'current' to 'edited'
// TODO:
abstract class _NorthwindInternalApStore with Store {
  //
  //
  // INTERNAL Repos
  var _northwindInternalCategoryInfoRepository =
      NorthwindInternalCategoryInfoRepository();

  var _northwindInternalProductInfoRepository =
      NorthwindInternalProductInfoRepository();

  var _northwindInternalShipperInfoRepository =
      NorthwindInternalShipperInfoRepository();

  var _northwindInternalOrderItemRepository =
      NorthwindInternalOrderItemRepository();

  var _northwindInternalInternationalOrderInfoRepository =
      NorthwindInternalInternationalOrderInfoRepository();

  //
  //
  // INTERNAL Lists

  @observable
  var northwindInternalCategoryInfoStoreList =
      ObservableList<NorthwindInternalCategoryInfoStore>();

  @observable
  var northwindInternalProductInfoStoreList =
      ObservableList<NorthwindInternalProductInfoStore>();

  @observable
  var northwindInternalShipperInfoStoreList =
      ObservableList<NorthwindInternalShipperInfoStore>();

  @observable
  var northwindInternalOrderItemStoreList =
      ObservableList<NorthwindInternalOrderItemStore>();

  @observable
  var northwindInternalInternationalOrderInfoStoreList =
      ObservableList<NorthwindInternalInternationalOrderInfoStore>();

  //
  //
  // CATEGORY OBSERVABLES
  @observable
  NorthwindInternalCategoryInfoStore currentCategory;

  @observable
  var editCategory = NorthwindInternalCategoryInfoStore();

  //
  //
  // PRODUCT OBSERVABLES
  @observable
  NorthwindInternalProductInfoStore currentProduct;

  @observable
  var editProduct = NorthwindInternalProductInfoStore();

  //
  //
  // SHIPPING OBSERVABLES
  @observable
  NorthwindInternalShipperInfoStore currentShipper;

  @observable
  var editShipper = NorthwindInternalShipperInfoStore();

  //
  //
  // ORDERITEM OBSERVABLES
  @observable
  NorthwindInternalOrderItemStore currentOrderItem;

  @observable
  var editOrderItem = NorthwindInternalOrderItemStore();

  //
  //
  // INTERNATIONALORDERINFO OBSERVABLES
  @observable
  NorthwindInternalInternationalOrderInfoStore currentOrderInfo;

  @observable
  var editOrderInfo = NorthwindInternalInternationalOrderInfoStore();

  //
  //
  // CATEGORY ACTIONS

  @action
  Future getCategories() async {
    await _northwindInternalCategoryInfoRepository
        .getAll(northwindInternalCategoryInfoStoreList);
  }

  //
  //
  // PRODUCT ACTIONS

  @action
  Future getProducts() async {
    await _northwindInternalProductInfoRepository
        .getAll(northwindInternalProductInfoStoreList);
  }

  //
  //
  // SHIPPER ACTIONS

  @action
  Future createShipper() async {
    currentShipper = NorthwindInternalShipperInfoStore.clone(editShipper);

    await _northwindInternalShipperInfoRepository.createShipper(currentShipper);

    northwindInternalShipperInfoStoreList.add(currentShipper);
  }

  @action
  Future getShippers() async {
    await _northwindInternalShipperInfoRepository
        .getAll(northwindInternalShipperInfoStoreList);
  }

  //
  //
  // INTERNATIONALORDERINFO ACTIONS

  @action
  Future createOrder() async {
    currentOrderInfo =
        NorthwindInternalInternationalOrderInfoStore.clone(editOrderInfo);

    await _northwindInternalInternationalOrderInfoRepository
        .createOrder(currentOrderInfo);

    northwindInternalInternationalOrderInfoStoreList.add(currentOrderInfo);
  }

  @action
  Future updateOrder() async {
    currentOrderInfo =
        NorthwindInternalInternationalOrderInfoStore.clone(editOrderInfo);

    await _northwindInternalInternationalOrderInfoRepository
        .updateOrder(currentOrderInfo);
  }

  @action
  void selectOrder(NorthwindInternalInternationalOrderInfoStore order) {
    currentOrderInfo = order;
    editOrderInfo =
        NorthwindInternalInternationalOrderInfoStore.clone(currentOrderInfo);
  }

  @action
  Future changeShipper(NorthwindInternalShipperInfoStore newShipper) async {
    await _northwindInternalInternationalOrderInfoRepository.changeShipper(
        currentOrderInfo, newShipper);
    editOrderInfo.shipper = currentShipper;
  }

  //
  //
  //ORDERITEM ACTIONS

  @action
  Future addOrderItem() async {
    NorthwindInternalOrderItemStore newOrderItem = editOrderInfo.items
        .firstWhere(
            (element) =>
                element.product.identifier == currentProduct.identifier,
            orElse: () => null);

    if (newOrderItem != null) {
      newOrderItem.quantity++;
      return;
    }

    newOrderItem = NorthwindInternalOrderItemStore();
    newOrderItem.quantity = editOrderItem.quantity;
    newOrderItem.discount = editOrderItem.discount;
    newOrderItem.unitPrice = editOrderItem.unitPrice;
    newOrderItem.product = currentProduct;

    await _northwindInternalOrderItemRepository.createItem(
        currentOrderInfo, newOrderItem);

    currentOrderInfo.items.add(newOrderItem);
  }

  @action
  void createOrderItem() {
    NorthwindInternalOrderItemStore newOrderItem = editOrderInfo.items
        .firstWhere(
            (element) =>
                element.product.identifier == currentProduct.identifier,
            orElse: () => null);

    if (newOrderItem != null) {
      newOrderItem.quantity++;
      return;
    }

    newOrderItem = NorthwindInternalOrderItemStore();
    newOrderItem.quantity = editOrderItem.quantity;
    newOrderItem.discount = editOrderItem.discount;
    newOrderItem.unitPrice = editOrderItem.unitPrice;
    newOrderItem.product = currentProduct;

    editOrderInfo.items.add(newOrderItem);
  }

  @action
  Future deleteOrderItem(
      NorthwindInternalOrderItemStore deletedOrderItem) async {
    await _northwindInternalOrderItemRepository.removeItem(
        currentOrderInfo, deletedOrderItem);
    editOrderInfo.items.remove(deletedOrderItem);
  }
}
