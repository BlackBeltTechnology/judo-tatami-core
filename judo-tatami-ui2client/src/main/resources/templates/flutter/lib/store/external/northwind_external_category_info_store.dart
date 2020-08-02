import 'package:judo/repositories/external/northwind_external_category_info_repository.dart';
import 'package:judo/store/external/northwind_external_product_info_store.dart';
import 'package:mobx/mobx.dart';

part 'northwind_external_category_info_store.g.dart';

class NorthwindExternalCategoryInfoStore
    extends _NorthwindExternalCategoryInfoStore
    with _$NorthwindExternalCategoryInfoStore {
  NorthwindExternalCategoryInfoStore() : super();

  NorthwindExternalCategoryInfoStore.clone(
      NorthwindExternalCategoryInfoStore categoryInfoStore)
      : super.clone(categoryInfoStore);
}

abstract class _NorthwindExternalCategoryInfoStore
    with Store, NorthwindExternalCategoryInfoRepository {
  _NorthwindExternalCategoryInfoStore();

  _NorthwindExternalCategoryInfoStore.clone(
      NorthwindExternalCategoryInfoStore categoryInfoStore) {
    identifier = categoryInfoStore.identifier;
    categoryName = categoryInfoStore.categoryName;
    products = ObservableList.of(categoryInfoStore.products);
  }

  String identifier;

  @observable
  String categoryName;

  @observable
  var products = ObservableList<NorthwindExternalProductInfoStore>();
}
