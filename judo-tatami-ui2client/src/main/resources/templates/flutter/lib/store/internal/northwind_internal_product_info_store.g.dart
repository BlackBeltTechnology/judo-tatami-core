// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'northwind_internal_product_info_store.dart';

// **************************************************************************
// StoreGenerator
// **************************************************************************

// ignore_for_file: non_constant_identifier_names, unnecessary_brace_in_string_interps, unnecessary_lambdas, prefer_expression_function_bodies, lines_longer_than_80_chars, avoid_as, avoid_annotating_with_dynamic

mixin _$NorthwindInternalProductInfoStore
    on _NorthwindInternalProductInfoStore, Store {
  final _$productNameAtom =
      Atom(name: '_NorthwindInternalProductInfoStore.productName');

  @override
  String get productName {
    _$productNameAtom.reportRead();
    return super.productName;
  }

  @override
  set productName(String value) {
    _$productNameAtom.reportWrite(value, super.productName, () {
      super.productName = value;
    });
  }

  final _$unitPriceAtom =
      Atom(name: '_NorthwindInternalProductInfoStore.unitPrice');

  @override
  double get unitPrice {
    _$unitPriceAtom.reportRead();
    return super.unitPrice;
  }

  @override
  set unitPrice(double value) {
    _$unitPriceAtom.reportWrite(value, super.unitPrice, () {
      super.unitPrice = value;
    });
  }

  final _$weightAtom = Atom(name: '_NorthwindInternalProductInfoStore.weight');

  @override
  double get weight {
    _$weightAtom.reportRead();
    return super.weight;
  }

  @override
  set weight(double value) {
    _$weightAtom.reportWrite(value, super.weight, () {
      super.weight = value;
    });
  }

  final _$categoryAtom =
      Atom(name: '_NorthwindInternalProductInfoStore.category');

  @override
  NorthwindInternalCategoryInfoStore get category {
    _$categoryAtom.reportRead();
    return super.category;
  }

  @override
  set category(NorthwindInternalCategoryInfoStore value) {
    _$categoryAtom.reportWrite(value, super.category, () {
      super.category = value;
    });
  }

  @override
  String toString() {
    return '''
productName: ${productName},
unitPrice: ${unitPrice},
weight: ${weight},
category: ${category}
    ''';
  }
}
