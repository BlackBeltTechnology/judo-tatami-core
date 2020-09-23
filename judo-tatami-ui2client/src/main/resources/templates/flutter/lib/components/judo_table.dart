part of judo.components;

abstract class JudoTableDataInfo {
  List<DataColumn> getColumns(Function onAdd);

  Function getRow(Function onTap);
}

class JudoTable extends StatelessWidget implements IJudoComponent {
  JudoTable(
      {@required this.col,
        @required this.dataInfo,
        @required this.rowList,
        this.onTap,
        this.sortAscending = true,
        this.onAdd});

  final int col;
  final bool sortAscending;
  final JudoTableDataInfo dataInfo;
  final List rowList;
  final Function onTap;
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
    List<DataRow> dataRowList = rowList.map<DataRow>(dataInfo.getRow(onTap)).toList();
    return dataRowList;
  }
}
