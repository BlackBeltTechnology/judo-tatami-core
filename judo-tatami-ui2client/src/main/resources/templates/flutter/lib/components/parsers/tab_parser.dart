import 'package:dynamic_widget/dynamic_widget.dart';
import 'package:dynamic_widget/dynamic_widget/icons_helper.dart';
import 'package:flutter/material.dart';

class TabParser extends WidgetParser {
  @override
  Widget parse(Map<String, dynamic> map, BuildContext buildContext,
      ClickListener listener) {
    return Tab(
      icon: Icon(getIconGuessFavorMaterial(name: map['icon'])),
      text: map.containsKey('label') ? map['label'] : 'Tab',
    );
  }

  @override
  String get widgetName => "Tab";
}
