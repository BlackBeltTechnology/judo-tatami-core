part of judo.components;

abstract class JudoTableDataInfo {
  List<DataColumn> getColumns(Function onAdd);
  Function getRow({Function navigateToEditPageAction, Function navigateToViewPageAction, Function navigateToCreatePageAction, Function removeAction, Function unsetAction, Function deleteAction});
}

class JudoTable extends StatelessWidget {
  JudoTable({
    @required this.col,
    this.row,
    @required this.dataInfo,
    @required this.rowList,
    this.navigateToEditPageAction,
    this.navigateToViewPageAction,
    this.navigateToCreatePageAction,
    this.removeAction,
    this.unsetAction,
    this.deleteAction,
    this.sortAscending = true,
    this.disabled = false,
    this.onAdd
  });

  final int col;
  final int row;
  final bool sortAscending;
  final bool disabled;
  final JudoTableDataInfo dataInfo;
  final List rowList;
  final Function navigateToEditPageAction;
  final Function navigateToViewPageAction;
  final Function navigateToCreatePageAction;
  final Function removeAction;
  final Function unsetAction;
  final Function deleteAction;
  final Function onAdd;

  @override
  Widget build(BuildContext context) {

    print('TABLE HEIGHT: ${row * kJudoHeight} ');

    return JudoContainer(
      col: col,
      row: row,
      child: SizedBox(
        height: row * kJudoHeight,
        child: SingleChildScrollView(
          child: rowList is ObservableList
              ? Observer(
            builder: (_) => DataTable(
              dataRowColor: disabled ? MaterialStateProperty.resolveWith((_) => kDisabledColor) : null,
              headingRowColor: disabled ? MaterialStateProperty.resolveWith((_) => kDisabledColor) : null,
              headingTextStyle: TextStyle(
                color: Color(kPrimaryColor.value),
                fontWeight: FontWeight.bold,
              ),
              onSelectAll: (b) {},
              sortAscending: sortAscending,
              columns: dataInfo.getColumns(onAdd),
              rows: dataRow(),
            ),
          )
              : DataTable(
            dataRowColor: disabled ? MaterialStateProperty.resolveWith((_) => kDisabledColor) : null,
            headingRowColor: disabled ? MaterialStateProperty.resolveWith((_) => kDisabledColor) : null,
            headingTextStyle: TextStyle(
              color: Color(kPrimaryColor.value),
              fontWeight: FontWeight.bold,
            ),
            onSelectAll: (b) {},
            sortAscending: sortAscending,
            columns: dataInfo.getColumns(onAdd),
            rows: dataRow(),
          ),
        ),
      )
    );
  }

  List<DataRow> dataRow() {
    List<DataRow> dataRowList = rowList.map<DataRow>(dataInfo.getRow(
        navigateToEditPageAction: disabled ? null : this.navigateToEditPageAction,
        navigateToCreatePageAction: disabled ? null : this.navigateToCreatePageAction,
        navigateToViewPageAction: disabled ? null : this.navigateToViewPageAction,
        deleteAction: disabled ? null : this.deleteAction,
        removeAction: disabled ? null : this.removeAction,
        unsetAction: disabled ? null : this.unsetAction
    )).toList();
    return dataRowList;
  }
}
