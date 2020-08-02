// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'northwind_external_ap_store.dart';

// **************************************************************************
// StoreGenerator
// **************************************************************************

// ignore_for_file: non_constant_identifier_names, unnecessary_brace_in_string_interps, unnecessary_lambdas, prefer_expression_function_bodies, lines_longer_than_80_chars, avoid_as, avoid_annotating_with_dynamic

mixin _$NorthwindExternalApStore on _NorthwindExternalApStore, Store {
  final _$northwindExternalCategoryInfoStoreListAtom = Atom(
      name: '_NorthwindExternalApStore.northwindExternalCategoryInfoStoreList');

  @override
  ObservableList<NorthwindExternalCategoryInfoStore>
      get northwindExternalCategoryInfoStoreList {
    _$northwindExternalCategoryInfoStoreListAtom.reportRead();
    return super.northwindExternalCategoryInfoStoreList;
  }

  @override
  set northwindExternalCategoryInfoStoreList(
      ObservableList<NorthwindExternalCategoryInfoStore> value) {
    _$northwindExternalCategoryInfoStoreListAtom
        .reportWrite(value, super.northwindExternalCategoryInfoStoreList, () {
      super.northwindExternalCategoryInfoStoreList = value;
    });
  }

  final _$northwindExternalProductInfoStoreListAtom = Atom(
      name: '_NorthwindExternalApStore.northwindExternalProductInfoStoreList');

  @override
  ObservableList<NorthwindExternalProductInfoStore>
      get northwindExternalProductInfoStoreList {
    _$northwindExternalProductInfoStoreListAtom.reportRead();
    return super.northwindExternalProductInfoStoreList;
  }

  @override
  set northwindExternalProductInfoStoreList(
      ObservableList<NorthwindExternalProductInfoStore> value) {
    _$northwindExternalProductInfoStoreListAtom
        .reportWrite(value, super.northwindExternalProductInfoStoreList, () {
      super.northwindExternalProductInfoStoreList = value;
    });
  }

  final _$currentCategoryAtom =
      Atom(name: '_NorthwindExternalApStore.currentCategory');

  @override
  NorthwindExternalCategoryInfoStore get currentCategory {
    _$currentCategoryAtom.reportRead();
    return super.currentCategory;
  }

  @override
  set currentCategory(NorthwindExternalCategoryInfoStore value) {
    _$currentCategoryAtom.reportWrite(value, super.currentCategory, () {
      super.currentCategory = value;
    });
  }

  final _$editCategoryAtom =
      Atom(name: '_NorthwindExternalApStore.editCategory');

  @override
  NorthwindExternalCategoryInfoStore get editCategory {
    _$editCategoryAtom.reportRead();
    return super.editCategory;
  }

  @override
  set editCategory(NorthwindExternalCategoryInfoStore value) {
    _$editCategoryAtom.reportWrite(value, super.editCategory, () {
      super.editCategory = value;
    });
  }

  final _$currentProductAtom =
      Atom(name: '_NorthwindExternalApStore.currentProduct');

  @override
  NorthwindExternalProductInfoStore get currentProduct {
    _$currentProductAtom.reportRead();
    return super.currentProduct;
  }

  @override
  set currentProduct(NorthwindExternalProductInfoStore value) {
    _$currentProductAtom.reportWrite(value, super.currentProduct, () {
      super.currentProduct = value;
    });
  }

  final _$editProductAtom = Atom(name: '_NorthwindExternalApStore.editProduct');

  @override
  NorthwindExternalProductInfoStore get editProduct {
    _$editProductAtom.reportRead();
    return super.editProduct;
  }

  @override
  set editProduct(NorthwindExternalProductInfoStore value) {
    _$editProductAtom.reportWrite(value, super.editProduct, () {
      super.editProduct = value;
    });
  }

  final _$createCategoryAsyncAction =
      AsyncAction('_NorthwindExternalApStore.createCategory');

  @override
  Future<dynamic> createCategory() {
    return _$createCategoryAsyncAction.run(() => super.createCategory());
  }

  final _$getCategoriesAsyncAction =
      AsyncAction('_NorthwindExternalApStore.getCategories');

  @override
  Future<dynamic> getCategories() {
    return _$getCategoriesAsyncAction.run(() => super.getCategories());
  }

  final _$createProductAsyncAction =
      AsyncAction('_NorthwindExternalApStore.createProduct');

  @override
  Future<dynamic> createProduct() {
    return _$createProductAsyncAction.run(() => super.createProduct());
  }

  final _$getProductsAsyncAction =
      AsyncAction('_NorthwindExternalApStore.getProducts');

  @override
  Future<dynamic> getProducts() {
    return _$getProductsAsyncAction.run(() => super.getProducts());
  }

  final _$_NorthwindExternalApStoreActionController =
      ActionController(name: '_NorthwindExternalApStore');

  @override
  void removeCategory() {
    final _$actionInfo = _$_NorthwindExternalApStoreActionController
        .startAction(name: '_NorthwindExternalApStore.removeCategory');
    try {
      return super.removeCategory();
    } finally {
      _$_NorthwindExternalApStoreActionController.endAction(_$actionInfo);
    }
  }

  @override
  String toString() {
    return '''
northwindExternalCategoryInfoStoreList: ${northwindExternalCategoryInfoStoreList},
northwindExternalProductInfoStoreList: ${northwindExternalProductInfoStoreList},
currentCategory: ${currentCategory},
editCategory: ${editCategory},
currentProduct: ${currentProduct},
editProduct: ${editProduct}
    ''';
  }
}
