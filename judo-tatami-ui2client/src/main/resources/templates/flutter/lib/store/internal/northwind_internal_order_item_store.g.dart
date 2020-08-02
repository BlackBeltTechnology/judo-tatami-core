// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'northwind_internal_order_item_store.dart';

// **************************************************************************
// StoreGenerator
// **************************************************************************

// ignore_for_file: non_constant_identifier_names, unnecessary_brace_in_string_interps, unnecessary_lambdas, prefer_expression_function_bodies, lines_longer_than_80_chars, avoid_as, avoid_annotating_with_dynamic

mixin _$NorthwindInternalOrderItemStore
    on _NorthwindInternalOrderItemStore, Store {
  final _$unitPriceAtom =
      Atom(name: '_NorthwindInternalOrderItemStore.unitPrice');

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

  final _$quantityAtom =
      Atom(name: '_NorthwindInternalOrderItemStore.quantity');

  @override
  int get quantity {
    _$quantityAtom.reportRead();
    return super.quantity;
  }

  @override
  set quantity(int value) {
    _$quantityAtom.reportWrite(value, super.quantity, () {
      super.quantity = value;
    });
  }

  final _$discountAtom =
      Atom(name: '_NorthwindInternalOrderItemStore.discount');

  @override
  double get discount {
    _$discountAtom.reportRead();
    return super.discount;
  }

  @override
  set discount(double value) {
    _$discountAtom.reportWrite(value, super.discount, () {
      super.discount = value;
    });
  }

  final _$productNameAtom =
      Atom(name: '_NorthwindInternalOrderItemStore.productName');

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

  final _$categoryNameAtom =
      Atom(name: '_NorthwindInternalOrderItemStore.categoryName');

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

  final _$priceAtom = Atom(name: '_NorthwindInternalOrderItemStore.price');

  @override
  double get price {
    _$priceAtom.reportRead();
    return super.price;
  }

  @override
  set price(double value) {
    _$priceAtom.reportWrite(value, super.price, () {
      super.price = value;
    });
  }

  final _$productAtom = Atom(name: '_NorthwindInternalOrderItemStore.product');

  @override
  NorthwindInternalProductInfoStore get product {
    _$productAtom.reportRead();
    return super.product;
  }

  @override
  set product(NorthwindInternalProductInfoStore value) {
    _$productAtom.reportWrite(value, super.product, () {
      super.product = value;
    });
  }

  @override
  String toString() {
    return '''
unitPrice: ${unitPrice},
quantity: ${quantity},
discount: ${discount},
productName: ${productName},
categoryName: ${categoryName},
price: ${price},
product: ${product}
    ''';
  }
}
