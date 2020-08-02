// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'northwind_internal_shipper_info_store.dart';

// **************************************************************************
// StoreGenerator
// **************************************************************************

// ignore_for_file: non_constant_identifier_names, unnecessary_brace_in_string_interps, unnecessary_lambdas, prefer_expression_function_bodies, lines_longer_than_80_chars, avoid_as, avoid_annotating_with_dynamic

mixin _$NorthwindInternalShipperInfoStore
    on _NorthwindInternalShipperInfoStore, Store {
  final _$companyNameAtom =
      Atom(name: '_NorthwindInternalShipperInfoStore.companyName');

  @override
  String get companyName {
    _$companyNameAtom.reportRead();
    return super.companyName;
  }

  @override
  set companyName(String value) {
    _$companyNameAtom.reportWrite(value, super.companyName, () {
      super.companyName = value;
    });
  }

  @override
  String toString() {
    return '''
companyName: ${companyName}
    ''';
  }
}
