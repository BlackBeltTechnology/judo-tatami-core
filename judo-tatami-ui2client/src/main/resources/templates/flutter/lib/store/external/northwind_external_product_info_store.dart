import 'package:mobx/mobx.dart';
import 'northwind_external_category_info_store.dart';

part 'northwind_external_product_info_store.g.dart';

class NorthwindExternalProductInfoStore
    extends _NorthwindExternalProductInfoStore
    with _$NorthwindExternalProductInfoStore {
  NorthwindExternalProductInfoStore() : super();

  NorthwindExternalProductInfoStore.clone(
      NorthwindExternalProductInfoStore productInfoStore)
      : super.clone(productInfoStore);
}

abstract class _NorthwindExternalProductInfoStore with Store {
  _NorthwindExternalProductInfoStore();

  _NorthwindExternalProductInfoStore.clone(
      NorthwindExternalProductInfoStore productInfoStore) {
    identifier = productInfoStore.identifier;
    productName = productInfoStore.productName;
    unitPrice = productInfoStore.unitPrice;
    weight = productInfoStore.weight;
    category =
        NorthwindExternalCategoryInfoStore.clone(productInfoStore.category);
  }

  String identifier;

  @observable
  String productName;

  @observable
  double unitPrice;

  @observable
  double weight;

  @observable
  NorthwindExternalCategoryInfoStore category;
}
