part of judo.components;

abstract class JudoTableDataInfo {
  List<DataColumn> getColumns(Function onAdd);

  Function getRow(Function onTap);
}

class JudoTable extends StatelessWidget implements IJudoComponent {
  JudoTable({
    @required this.col,
    @required this.dataInfo,
    @required this.rowList,
    this.onTap,
    this.sortAscending = true,
    this.plusRow,
    this.onAdd
  });

  final int col;
  final bool sortAscending;
  final JudoTableDataInfo dataInfo;
  final List rowList;
  final Function onTap;
  final Function onAdd;
  final DataRow plusRow;

  @override
  int getColSize() {
    return this.col;
  }

  @override
  Widget build(BuildContext context) {
    return Flexible(
        flex: col,
        child: Container(
          height: rowList.length * kJudoHeight + kJudoHeight + (plusRow != null ? kJudoHeight : 0),
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
    List<DataRow> dataRowList = plusRow == null ? [] : [plusRow];
    dataRowList.addAll(rowList.map<DataRow>(dataInfo.getRow(onTap)).toList());
    return dataRowList;
  }
}
