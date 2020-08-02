import 'package:judo/store/internal/northwind_internal_order_item_store.dart';
import 'package:mobx/mobx.dart';
import 'northwind_internal_comment_store.dart';
import 'northwind_internal_shipper_info_store.dart';

part 'northwind_internal_international_order_info_store.g.dart';

class NorthwindInternalInternationalOrderInfoStore
    extends _NorthwindInternalInternationalOrderInfoStore
    with _$NorthwindInternalInternationalOrderInfoStore {
  NorthwindInternalInternationalOrderInfoStore() : super();

  NorthwindInternalInternationalOrderInfoStore.clone(
      NorthwindInternalInternationalOrderInfoStore orderInfoStore)
      : super.clone(orderInfoStore);
}

abstract class _NorthwindInternalInternationalOrderInfoStore with Store {
  _NorthwindInternalInternationalOrderInfoStore();

  _NorthwindInternalInternationalOrderInfoStore.clone(
      NorthwindInternalInternationalOrderInfoStore orderInfoStore) {
    identifier = orderInfoStore.identifier;
    customsDescription = orderInfoStore.customsDescription;
    exciseTax = orderInfoStore.exciseTax;
    orderDate = orderInfoStore.orderDate;
    shipper = orderInfoStore.shipper;
    totalPrice = orderInfoStore.totalPrice;
    totalWeight = orderInfoStore.totalWeight;
    items = ObservableList.of(orderInfoStore.items);
    shipper = NorthwindInternalShipperInfoStore.clone(orderInfoStore.shipper);
    comments = ObservableList.of(orderInfoStore.comments);
  }

  String identifier = '';

  @observable
  String customsDescription = '';

  @observable
  double exciseTax = 0;

  @observable
  DateTime orderDate = DateTime.now();

  @observable
  String shipperName = '';

  @observable
  double totalPrice = 0;

  @observable
  double totalWeight = 0;

  @observable
  var items = ObservableList<NorthwindInternalOrderItemStore>();

  @observable
  NorthwindInternalShipperInfoStore shipper;

  @observable
  var comments = ObservableList<NorthwindInternalCommentStore>();
}
