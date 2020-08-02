import 'package:mobx/mobx.dart';
import 'northwind_internal_category_info_store.dart';

part 'northwind_internal_product_info_store.g.dart';

class NorthwindInternalProductInfoStore
    extends _NorthwindInternalProductInfoStore
    with _$NorthwindInternalProductInfoStore {
  NorthwindInternalProductInfoStore() : super();

  NorthwindInternalProductInfoStore.clone(
      NorthwindInternalProductInfoStore productInfoStore)
      : super.clone(productInfoStore);
}

abstract class _NorthwindInternalProductInfoStore with Store {
  _NorthwindInternalProductInfoStore();

  _NorthwindInternalProductInfoStore.clone(
      NorthwindInternalProductInfoStore productInfoStore) {
    identifier = productInfoStore.identifier;
    productName = productInfoStore.productName;
    unitPrice = productInfoStore.unitPrice;
    weight = productInfoStore.weight;
    category =
        NorthwindInternalCategoryInfoStore.clone(productInfoStore.category);
  }

  String identifier;

  @observable
  String productName;

  @observable
  double unitPrice;

  @observable
  double weight;

  @observable
  NorthwindInternalCategoryInfoStore category;
}
