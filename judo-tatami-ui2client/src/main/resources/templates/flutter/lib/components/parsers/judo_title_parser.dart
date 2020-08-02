import 'package:dynamic_widget/dynamic_widget.dart';
import 'package:dynamic_widget/dynamic_widget/icons_helper.dart';
import 'package:flutter/material.dart';
import 'package:judo/components/judo_button.dart';
import 'package:judo/components/judo_title.dart';

class JudoTitleParser extends WidgetParser {
  @override
  Widget parse(Map<String, dynamic> map, BuildContext buildContext,
      ClickListener listener) {
    return JudoTitle(
      col: map.containsKey('col') ? map['col'] : 4,
      text: map.containsKey('text') ? map['text'] : '',
    );
  }

  @override
  String get widgetName => 'JudoTitle';
}
