import 'package:mobx/mobx.dart';

part 'table_helper_store.g.dart';

class TableHelperStore extends _TableHelperStore with _$TableHelperStore {
  TableHelperStore(ObservableList selected, Function getId) : super(selected, getId);
}

abstract class _TableHelperStore with Store {

    _TableHelperStore(this.selected, this.getId);

    Function getId;

    @observable
    ObservableList<String> selected;

    @observable
    String singleSelected;

    @computed
    bool get selectButtonEnabled => selected.isNotEmpty || singleSelected != null;

    @action
    void selectRow(dynamic value) {
      String valueId = getId(value);
      if (selected.contains(valueId)) {
        selected.remove(valueId);
      } else {
        selected.add(valueId);
      }
    }

    @action
    void singleSelectRow(dynamic value) {
      if (getId(value) == singleSelected) {
        singleSelected = null;
      } else {
        singleSelected = getId(value);
      }
    }

}