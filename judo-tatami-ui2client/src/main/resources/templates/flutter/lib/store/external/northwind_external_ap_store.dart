import 'package:judo/repositories/external/northwind_external_category_info_repository.dart';
import 'package:judo/repositories/external/northwind_external_product_info_repository.dart';
import 'package:judo/store/external/northwind_external_product_info_store.dart';
import 'package:mobx/mobx.dart';

import 'northwind_external_category_info_store.dart';

part 'northwind_external_ap_store.g.dart';

class NorthwindExternalApStore extends _NorthwindExternalApStore
    with _$NorthwindExternalApStore {}

abstract class _NorthwindExternalApStore with Store {
  //
  //
  // EXTERNAL Repos
  var _northwindExternalCategoryInfoRepository =
      NorthwindExternalCategoryInfoRepository();

  var _northwindExternalProductInfoRepository =
      NorthwindExternalProductInfoRepository();

  //
  //
  // EXTERNAL Lists

  @observable
  var northwindExternalCategoryInfoStoreList =
      ObservableList<NorthwindExternalCategoryInfoStore>();

  @observable
  var northwindExternalProductInfoStoreList =
      ObservableList<NorthwindExternalProductInfoStore>();

  //
  //
  // CATEGORY OBSERVABLES
  @observable
  NorthwindExternalCategoryInfoStore currentCategory;

  @observable
  var editCategory = NorthwindExternalCategoryInfoStore();

  //
  //
  // PRODUCT OBSERVABLES
  @observable
  NorthwindExternalProductInfoStore currentProduct;

  @observable
  var editProduct = NorthwindExternalProductInfoStore();

  //
  //
  // CATEGORY ACTIONS

  @action
  Future createCategory() async {
    currentCategory = NorthwindExternalCategoryInfoStore.clone(editCategory);

    _northwindExternalCategoryInfoRepository.createCategory(currentCategory);

    northwindExternalCategoryInfoStoreList.add(currentCategory);
  }

  @action
  void removeCategory() {
    _northwindExternalCategoryInfoRepository.removeCategory(currentCategory);
    northwindExternalCategoryInfoStoreList.remove(currentCategory);
  }

  @action
  Future getCategories() async {
    await _northwindExternalCategoryInfoRepository
        .getAll(northwindExternalCategoryInfoStoreList);
  }

  //
  //
  // PRODUCT ACTIONS

  @action
  Future createProduct() async {
    currentProduct = NorthwindExternalProductInfoStore.clone(editProduct);

    _northwindExternalProductInfoRepository.createProduct(currentProduct);

    northwindExternalProductInfoStoreList.add(currentProduct);
  }

  // TODO: removeProduct() missing

  @action
  Future getProducts() async {
    await _northwindExternalProductInfoRepository
        .getAll(northwindExternalProductInfoStoreList);
  }
}
