import 'package:judo/repositories/internal/northwind_internal_category_info_repository.dart';
import 'package:mobx/mobx.dart';
import 'northwind_internal_product_info_store.dart';

part 'northwind_internal_category_info_store.g.dart';

class NorthwindInternalCategoryInfoStore
    extends _NorthwindInternalCategoryInfoStore
    with _$NorthwindInternalCategoryInfoStore {
  NorthwindInternalCategoryInfoStore() : super();

  NorthwindInternalCategoryInfoStore.clone(
      NorthwindInternalCategoryInfoStore categoryInfoStore)
      : super.clone(categoryInfoStore);
}

abstract class _NorthwindInternalCategoryInfoStore
    with Store, NorthwindInternalCategoryInfoRepository {
  _NorthwindInternalCategoryInfoStore();

  _NorthwindInternalCategoryInfoStore.clone(
      NorthwindInternalCategoryInfoStore categoryInfoStore) {
    identifier = categoryInfoStore.identifier;
    categoryName = categoryInfoStore.categoryName;
    products = ObservableList.of(categoryInfoStore.products);
  }

  String identifier;

  @observable
  String categoryName;

  @observable
  var products = ObservableList<NorthwindInternalProductInfoStore>();
}
