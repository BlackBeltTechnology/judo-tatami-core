import 'package:mobx/mobx.dart';

part 'northwind_internal_shipper_info_store.g.dart';

class NorthwindInternalShipperInfoStore
    extends _NorthwindInternalShipperInfoStore
    with _$NorthwindInternalShipperInfoStore {
  NorthwindInternalShipperInfoStore() : super();

  NorthwindInternalShipperInfoStore.clone(
      NorthwindInternalShipperInfoStore shipperInfoStore)
      : super.clone(shipperInfoStore);
}

abstract class _NorthwindInternalShipperInfoStore with Store {
  _NorthwindInternalShipperInfoStore();

  _NorthwindInternalShipperInfoStore.clone(
      NorthwindInternalShipperInfoStore shipperInfoStore) {
    identifier = shipperInfoStore.identifier;
    companyName = shipperInfoStore.companyName;
  }

  String identifier = '';

  @observable
  String companyName = '';
}
