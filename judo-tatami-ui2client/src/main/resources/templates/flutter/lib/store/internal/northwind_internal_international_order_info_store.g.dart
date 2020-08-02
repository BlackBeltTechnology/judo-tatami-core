// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'northwind_internal_international_order_info_store.dart';

// **************************************************************************
// StoreGenerator
// **************************************************************************

// ignore_for_file: non_constant_identifier_names, unnecessary_brace_in_string_interps, unnecessary_lambdas, prefer_expression_function_bodies, lines_longer_than_80_chars, avoid_as, avoid_annotating_with_dynamic

mixin _$NorthwindInternalInternationalOrderInfoStore
    on _NorthwindInternalInternationalOrderInfoStore, Store {
  final _$customsDescriptionAtom = Atom(
      name: '_NorthwindInternalInternationalOrderInfoStore.customsDescription');

  @override
  String get customsDescription {
    _$customsDescriptionAtom.reportRead();
    return super.customsDescription;
  }

  @override
  set customsDescription(String value) {
    _$customsDescriptionAtom.reportWrite(value, super.customsDescription, () {
      super.customsDescription = value;
    });
  }

  final _$exciseTaxAtom =
      Atom(name: '_NorthwindInternalInternationalOrderInfoStore.exciseTax');

  @override
  double get exciseTax {
    _$exciseTaxAtom.reportRead();
    return super.exciseTax;
  }

  @override
  set exciseTax(double value) {
    _$exciseTaxAtom.reportWrite(value, super.exciseTax, () {
      super.exciseTax = value;
    });
  }

  final _$orderDateAtom =
      Atom(name: '_NorthwindInternalInternationalOrderInfoStore.orderDate');

  @override
  DateTime get orderDate {
    _$orderDateAtom.reportRead();
    return super.orderDate;
  }

  @override
  set orderDate(DateTime value) {
    _$orderDateAtom.reportWrite(value, super.orderDate, () {
      super.orderDate = value;
    });
  }

  final _$shipperNameAtom =
      Atom(name: '_NorthwindInternalInternationalOrderInfoStore.shipperName');

  @override
  String get shipperName {
    _$shipperNameAtom.reportRead();
    return super.shipperName;
  }

  @override
  set shipperName(String value) {
    _$shipperNameAtom.reportWrite(value, super.shipperName, () {
      super.shipperName = value;
    });
  }

  final _$totalPriceAtom =
      Atom(name: '_NorthwindInternalInternationalOrderInfoStore.totalPrice');

  @override
  double get totalPrice {
    _$totalPriceAtom.reportRead();
    return super.totalPrice;
  }

  @override
  set totalPrice(double value) {
    _$totalPriceAtom.reportWrite(value, super.totalPrice, () {
      super.totalPrice = value;
    });
  }

  final _$totalWeightAtom =
      Atom(name: '_NorthwindInternalInternationalOrderInfoStore.totalWeight');

  @override
  double get totalWeight {
    _$totalWeightAtom.reportRead();
    return super.totalWeight;
  }

  @override
  set totalWeight(double value) {
    _$totalWeightAtom.reportWrite(value, super.totalWeight, () {
      super.totalWeight = value;
    });
  }

  final _$itemsAtom =
      Atom(name: '_NorthwindInternalInternationalOrderInfoStore.items');

  @override
  ObservableList<NorthwindInternalOrderItemStore> get items {
    _$itemsAtom.reportRead();
    return super.items;
  }

  @override
  set items(ObservableList<NorthwindInternalOrderItemStore> value) {
    _$itemsAtom.reportWrite(value, super.items, () {
      super.items = value;
    });
  }

  final _$shipperAtom =
      Atom(name: '_NorthwindInternalInternationalOrderInfoStore.shipper');

  @override
  NorthwindInternalShipperInfoStore get shipper {
    _$shipperAtom.reportRead();
    return super.shipper;
  }

  @override
  set shipper(NorthwindInternalShipperInfoStore value) {
    _$shipperAtom.reportWrite(value, super.shipper, () {
      super.shipper = value;
    });
  }

  final _$commentsAtom =
      Atom(name: '_NorthwindInternalInternationalOrderInfoStore.comments');

  @override
  ObservableList<NorthwindInternalCommentStore> get comments {
    _$commentsAtom.reportRead();
    return super.comments;
  }

  @override
  set comments(ObservableList<NorthwindInternalCommentStore> value) {
    _$commentsAtom.reportWrite(value, super.comments, () {
      super.comments = value;
    });
  }

  @override
  String toString() {
    return '''
customsDescription: ${customsDescription},
exciseTax: ${exciseTax},
orderDate: ${orderDate},
shipperName: ${shipperName},
totalPrice: ${totalPrice},
totalWeight: ${totalWeight},
items: ${items},
shipper: ${shipper},
comments: ${comments}
    ''';
  }
}
