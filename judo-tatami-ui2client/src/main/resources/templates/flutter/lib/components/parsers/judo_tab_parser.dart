import 'package:dynamic_widget/dynamic_widget.dart';
import 'package:dynamic_widget/dynamic_widget/icons_helper.dart';
import 'package:flutter/material.dart';
import 'package:judo/components/judo_tab_rework.dart';

class JudoTabParser extends WidgetParser {
  @override
  Widget parse(Map<String, dynamic> map, BuildContext buildContext,
      ClickListener listener) {
    List<Tab> tabList = map['tabs']
        .map<Tab>((e) => Tab(
              icon: e.containsKey('icon')
                  ? Icon(getIconGuessFavorMaterial(name: e['icon']))
                  : null,
              text: e.containsKey('label') ? e['label'] : 'Tab',
            ))
        .toList();

    return JudoTab(
      col: map.containsKey("col") ? map["col"] : 12,
      tabContent: DynamicWidgetBuilder.buildWidgets(
          map['children'], buildContext, listener),
      tabs: tabList,
    );
  }

  @override
  String get widgetName => "JudoTab";
}
