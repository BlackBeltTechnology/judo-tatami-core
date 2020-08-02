import 'package:dynamic_widget/dynamic_widget.dart';
import 'package:flutter/material.dart';

class FlexibleWidgetParser extends WidgetParser {
  @override
  Widget parse(Map<String, dynamic> map, BuildContext buildContext,
      ClickListener listener) {
    return Flexible(
      child: DynamicWidgetBuilder.buildFromMap(
          map["child"], buildContext, listener),
      flex: map.containsKey("flex") ? map["flex"] : 1,
      fit: map.containsKey("fit") ? parseFit(map["fit"]) : FlexFit.loose,
    );
  }

  @override
  String get widgetName => "Flexible";
}

FlexFit parseFit(String fitString) {
  switch (fitString) {
    case 'loose':
      return FlexFit.loose;
    case 'tight':
      return FlexFit.tight;
  }
  return FlexFit.loose;
}
