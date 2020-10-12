part of judo.components;

abstract class JudoTableDataInfo {
  List<DataColumn> getColumns(Function onAdd);

  Function getRow({Function navigateToEditPageAction, Function navigateToViewPageAction, Function navigateToCreatePageAction, Function removeAction, Function deleteAction});
}

class JudoTable extends StatelessWidget implements IJudoComponent {
  JudoTable(
      {@required this.col,
        @required this.dataInfo,
        @required this.rowList,
        this.navigateToEditPageAction,
        this.navigateToViewPageAction,
        this.navigateToCreatePageAction,
        this.removeAction,
        this.deleteAction,
        this.sortAscending = true,
        this.onAdd});

  final int col;
  final bool sortAscending;
  final JudoTableDataInfo dataInfo;
  final List rowList;
  final Function navigateToEditPageAction;
  final Function navigateToViewPageAction;
  final Function navigateToCreatePageAction;
  final Function removeAction;
  final Function deleteAction;
  final Function onAdd;

  @override
  int getColSize() {
    return this.col;
  }

  @override
  Widget build(BuildContext context) {
    return Flexible(
      flex: col,
      child: rowList is ObservableList
          ? Observer(
        builder: (_) => Container(
          height: rowList.length * kJudoHeight + kJudoHeight,
          child: Observer(
            builder: (_) => DataTable(
              onSelectAll: (b) {},
              sortAscending: sortAscending,
              columns: dataInfo.getColumns(onAdd),
              rows: dataRow(),
            ),
          ),
        ),
      )
          : Container(
        height: rowList.length * kJudoHeight + kJudoHeight,
        child: Observer(
          builder: (_) => DataTable(
            onSelectAll: (b) {},
            sortAscending: sortAscending,
            columns: dataInfo.getColumns(onAdd),
            rows: dataRow(),
          ),
        ),
      ),
    );
  }

  List<DataRow> dataRow() {
    List<DataRow> dataRowList = rowList.map<DataRow>(dataInfo.getRow(
      navigateToEditPageAction: this.navigateToEditPageAction,
      navigateToCreatePageAction: this.navigateToCreatePageAction,
      navigateToViewPageAction: this.navigateToViewPageAction,
      deleteAction: this.deleteAction,
      removeAction: this.removeAction
    )).toList();
    return dataRowList;
  }
}
