import 'package:flutter/material.dart';
import 'package:judo/components/judo_component.dart';
import 'package:judo/utilities/constants.dart';
import 'judo_container.dart';

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
    return Container(
      child: DataTable(
        sortAscending: true,
        columns: dataInfo.getColumns(),
        rows: dataRow(rowList),
      ),
    );
  }

  List<DataRow> dataRow(List list) {
    List<DataRow> dataRowList =
        list.map<DataRow>(dataInfo.getRow(onTap)).toList();

    return dataRowList;
  }
}
