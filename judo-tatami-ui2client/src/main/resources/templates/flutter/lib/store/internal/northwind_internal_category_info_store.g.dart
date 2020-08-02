// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'northwind_internal_category_info_store.dart';

// **************************************************************************
// StoreGenerator
// **************************************************************************

// ignore_for_file: non_constant_identifier_names, unnecessary_brace_in_string_interps, unnecessary_lambdas, prefer_expression_function_bodies, lines_longer_than_80_chars, avoid_as, avoid_annotating_with_dynamic

mixin _$NorthwindInternalCategoryInfoStore
    on _NorthwindInternalCategoryInfoStore, Store {
  final _$categoryNameAtom =
      Atom(name: '_NorthwindInternalCategoryInfoStore.categoryName');

  @override
  String get categoryName {
    _$categoryNameAtom.reportRead();
    return super.categoryName;
  }

  @override
  set categoryName(String value) {
    _$categoryNameAtom.reportWrite(value, super.categoryName, () {
      super.categoryName = value;
    });
  }

  final _$productsAtom =
      Atom(name: '_NorthwindInternalCategoryInfoStore.products');

  @override
  ObservableList<NorthwindInternalProductInfoStore> get products {
    _$productsAtom.reportRead();
    return super.products;
  }

  @override
  set products(ObservableList<NorthwindInternalProductInfoStore> value) {
    _$productsAtom.reportWrite(value, super.products, () {
      super.products = value;
    });
  }

  @override
  String toString() {
    return '''
categoryName: ${categoryName},
products: ${products}
    ''';
  }
}
