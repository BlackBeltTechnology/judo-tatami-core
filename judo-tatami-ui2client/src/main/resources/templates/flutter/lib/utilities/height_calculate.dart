import 'dart:convert';

import 'package:dartz/dartz.dart';
import 'package:judo/utilities/constants.dart';
import 'package:judo/utilities/sizing_information.dart';

class HeightCalculate {
  List<JudoDynamicWidgetInfo> widgetInfoList = [];
  List<JudoDynamicWidgetInfo> tabContentWidgets = [];

  Tuple2 results(String json) {
    dynamic map = jsonDecode(json);

    wrapping(map);

    getAllWidgetInfo(map);

    double height = heightCalc();

    return Tuple2(jsonEncode(map), height);
  }

  Map<String, dynamic> getNewRow(List list, int colSize) {
    colSize =
        colSize > SizingInformation.maxCol ? SizingInformation.maxCol : colSize;
    return ({
      'type': 'Expanded',
      'child': {'type': 'Row', 'col': colSize, 'children': list}
    });
  }

  void wrapping(dynamic map) {
    if (map['type'] == 'JudoRow') {
      int count = 0;
      map['children'].forEach((x) => count += x['col']);

      if (count > SizingInformation.maxCol) {
        List elementList = [];
        List rowList = [];

        int colSizeCounter = map['children'][0]['col'];

        for (int i = 0; i < map['children'].length; i++) {
          int nextCol = i != map['children'].length - 1
              ? map['children'][i + 1]['col']
              : 0;
          colSizeCounter += nextCol;

          elementList.add(map['children'][i]);

          if (colSizeCounter > SizingInformation.maxCol) {
            var newRow = getNewRow(elementList, colSizeCounter - nextCol);
            rowList.add(newRow);
            colSizeCounter = nextCol;
            elementList = [];
          }
        }

        if (elementList.length != 0) {
          var newRow = getNewRow(elementList, colSizeCounter);
          rowList.add(newRow);
        }

        map.addAll({
          'children': [
            {
              'type': 'Expanded',
              'child': {
                'type': 'Column',
                'col': colSizeCounter > SizingInformation.maxCol
                    ? SizingInformation.maxCol
                    : colSizeCounter,
                'children': rowList
              }
            }
          ]
        });
      }
    }

    if (map.containsKey('children')) {
      for (var element in map['children']) {
        if (element.containsKey('children') || element.containsKey('child')) {
          wrapping(element);
        }
      }
    } else if (map.containsKey('child')) {
      wrapping(map['child']);
    } else {
      return;
    }
  }

  void getAllWidgetInfo(dynamic map,
      {int depth = 0,
      JudoDynamicWidgetInfo parent,
      List<JudoDynamicWidgetInfo> children}) {
    String type = map['type'];
    int height = 1;

    if (map.containsKey('children')) {
      List<JudoDynamicWidgetInfo> newChildren = [];
      JudoDynamicWidgetInfo judoDynamicWidget =
          new JudoDynamicWidgetInfo(type, depth, parent, height);
      widgetInfoList.add(judoDynamicWidget);
      depth++;

      if (children != null) {
        children.add(judoDynamicWidget);
      }

      for (var element in map['children']) {
        getAllWidgetInfo(element,
            depth: depth, parent: judoDynamicWidget, children: newChildren);
        judoDynamicWidget._children = newChildren;
      }
    } else if (map.containsKey('child')) {
      List<JudoDynamicWidgetInfo> newChildren = [];
      JudoDynamicWidgetInfo judoDynamicWidget =
          new JudoDynamicWidgetInfo(type, depth, parent, height);
      widgetInfoList.add(judoDynamicWidget);
      depth++;

      if (children != null) {
        children.add(judoDynamicWidget);
      }

      getAllWidgetInfo(map['child'],
          depth: depth, parent: judoDynamicWidget, children: newChildren);
      judoDynamicWidget._children = newChildren;
    } else {
      JudoDynamicWidgetInfo judoDynamicWidget =
          new JudoDynamicWidgetInfo(type, depth, parent, height); // parent
      widgetInfoList.add(judoDynamicWidget);
      depth++;

      if (children != null) {
        children.add(judoDynamicWidget);
      }
    }
  }

  double heightCalc() {
    var newWidgetInfoList = widgetInfoList
        .where((e) =>
            e._typeName == 'Column' ||
            e._typeName == 'JudoColumn' ||
            e._typeName == 'JudoTab')
        .toList();

    newWidgetInfoList.sort((a, b) => b._depth.compareTo(a._depth));

    for (var e in newWidgetInfoList) {
      getHeight(e);
    }

    newWidgetInfoList.sort((a, b) => b._height.compareTo(a._height));

    return newWidgetInfoList.first._height * kJudoHeight;
  }

  void getHeight(JudoDynamicWidgetInfo widgetInfo) {
    int resultHeight = 0;

    if (widgetInfo._typeName == 'Row' || widgetInfo._typeName == 'JudoRow') {
      widgetInfo._children.sort((a, b) => b._height.compareTo(a._height));
      resultHeight += widgetInfo._children.first._height;
    } else if (widgetInfo._typeName == 'JudoTab') {
      widgetInfo._children.sort((a, b) => b._height.compareTo(a._height));
      resultHeight += widgetInfo._children.first._height + 1;
    } else {
      for (var el in widgetInfo._children) {
        resultHeight += el._height;
      }
    }
    widgetInfo._height = resultHeight;

    if (widgetInfo._parent == null) {
      return;
    }

    if (widgetInfo._parent._typeName == 'Column' ||
        widgetInfo._parent._typeName == 'JudoColumn' ||
        widgetInfo._parent._typeName == 'JudoTab') {
      return;
    }

    getHeight(widgetInfo._parent);
  }
}

class JudoDynamicWidgetInfo {
  int _id;
  JudoDynamicWidgetInfo _parent;
  String _typeName;
  int _depth;
  List<JudoDynamicWidgetInfo> _children = [];
  int _height;

  static int objectCounter = 0;

  JudoDynamicWidgetInfo(
      this._typeName, this._depth, this._parent, this._height) {
    _id = objectCounter;
    objectCounter++;
  }

  @override
  int get hashCode =>
      _id.hashCode ^
      _parent.hashCode ^
      _typeName.hashCode ^
      _depth.hashCode ^
      _children.hashCode ^
      _height.hashCode;

  @override
  bool operator ==(Object other) {
    return other is JudoDynamicWidgetInfo && _id == other._id;
  }

  @override
  String toString() {
    return '{id: $_id, type: $_typeName, depth: $_depth, height: $_height children: ' +
        _children.length.toString() +
        ', parent_id: ${_parent != null ? _parent._id : 'null'}}';
  }
}
