import 'package:mobx/mobx.dart';

part 'filter_store.g.dart';

class FilterStore extends _FilterStore with _$FilterStore {
  String label;
  String filterOperation;
  String attributeName;

  FilterStore({this.label, this.filterOperation, this.attributeName}) : super(label, filterOperation, attributeName);
}

abstract class _FilterStore with Store {
  _FilterStore(this.label, this.filterOperation, this.attributeName);

  String label;

  String filterOperation;

  String attributeName;

  @observable
  dynamic filterValue;

  @observable
  bool filterEnabled = false;

  @action
  void setFilterValue(dynamic newValue) {
    filterValue = newValue;
  }

  @action
  void changeFilterEnabled(){
    filterEnabled ? filterEnabled = false : filterEnabled = true;
  }
}