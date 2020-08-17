part of judo.components;

abstract class JudoTableDataInfo {
  List<DataColumn> getColumns();

  Function getRow(Function onTap);
}

class JudoTable extends StatelessWidget implements IJudoComponent {
  JudoTable({
    @required this.col,
    @required this.dataInfo,
    @required this.rowList,
    this.onTap,
    this.sortAscending = true,
  });

  final int col;
  final bool sortAscending;
  final JudoTableDataInfo dataInfo;
  final List rowList;
  final Function onTap;

  @override
  int getColSize() {
    return this.col;
  }

  @override
  Widget build(BuildContext context) {
    return Observer(
      builder: (_) => Flexible(
        flex: col,
        child: Container(
          height: rowList.length * kJudoHeight + kJudoHeight,
          child: DataTable(
            onSelectAll: (b) {},
            sortAscending: sortAscending,
            columns: dataInfo.getColumns(),
            rows: dataRow(),
          ),
        ),
      ),
    );
  }

  List<DataRow> dataRow() {
    List<DataRow> dataRowList =
        rowList.map<DataRow>(dataInfo.getRow(onTap)).toList();

    return dataRowList;
  }
}
