import 'package:mobx/mobx.dart';

import 'northwind_internal_product_info_store.dart';

part 'northwind_internal_order_item_store.g.dart';

class NorthwindInternalOrderItemStore extends _NorthwindInternalOrderItemStore
    with _$NorthwindInternalOrderItemStore {
  NorthwindInternalOrderItemStore() : super();

  NorthwindInternalOrderItemStore.clone(
      NorthwindInternalOrderItemStore orderItemStore)
      : super.clone(orderItemStore);
}

abstract class _NorthwindInternalOrderItemStore with Store {
  _NorthwindInternalOrderItemStore();

  _NorthwindInternalOrderItemStore.clone(
      NorthwindInternalOrderItemStore orderItemStore) {
    identifier = orderItemStore.identifier;
    unitPrice = orderItemStore.unitPrice;
    quantity = orderItemStore.quantity;
    discount = orderItemStore.discount;
    productName = orderItemStore.productName;
    categoryName = orderItemStore.categoryName;
    price = orderItemStore.price;
    product = NorthwindInternalProductInfoStore.clone(orderItemStore.product);
  }

  String identifier;

  @observable
  double unitPrice = 0;

  @observable
  int quantity = 1;

  @observable
  double discount = 0;

  @observable
  String productName = '';

  @observable
  String categoryName = '';

  @observable
  double price = 0;

  @observable
  NorthwindInternalProductInfoStore product;
}
