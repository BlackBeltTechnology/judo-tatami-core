import 'package:dynamic_widget/dynamic_widget.dart';
import 'package:dynamic_widget/dynamic_widget/icons_helper.dart';
import 'package:flutter/material.dart';
import 'package:judo/components/judo_button.dart';

class JudoButtonParser extends WidgetParser {
  @override
  Widget parse(Map<String, dynamic> map, BuildContext buildContext,
      ClickListener listener) {
    String clickEvent =
        map.containsKey('click_event') ? map['click_event'] : '';

    return JudoButton(
      col: map.containsKey('col') ? map['col'] : 2,
      icon: Icon(getIconGuessFavorMaterial(name: map['icon'])),
      label: map.containsKey('label') ? map['label'] : '',
      onPressed: () {
        listener.onClicked(clickEvent);
      },
    );
  }

  @override
  String get widgetName => 'JudoButton';
}
