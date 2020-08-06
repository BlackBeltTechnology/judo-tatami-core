import 'package:dynamic_widget/dynamic_widget.dart';
import 'package:dynamic_widget/dynamic_widget/icons_helper.dart';
import 'package:dynamic_widget/dynamic_widget/utils.dart';
import 'package:flutter/material.dart';
import 'package:flutter/widgets.dart';
import 'package:judo/components/judo_input_text.dart';
import 'package:judo/utilities/constants.dart';

class JudoInputTextWidgetParser extends WidgetParser {
  @override
  Widget parse(Map<String, dynamic> map, BuildContext buildContext,
      ClickListener listener) {
    int col = map['col'];
    String label = map['label'];
    String icon = map['icon'];

    var judoInputTextWidget = JudoInputText(
      col: col,
      label: label,
      icon: Icon(getIconGuessFavorMaterial(name: icon)),
    );

    return judoInputTextWidget;
  }

  @override
  String get widgetName => "JudoInputText";
}
