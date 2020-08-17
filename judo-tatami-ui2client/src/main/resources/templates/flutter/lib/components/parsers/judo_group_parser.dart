part of judo.components;

class JudoRowParser extends WidgetParser {
  @override
  Widget parse(Map<String, dynamic> map, BuildContext buildContext,
      ClickListener listener) {
    return Expanded(
      flex: map.containsKey("col") ? map["col"] : 12,
      child: Row(
        crossAxisAlignment: map.containsKey('crossAxisAlignment')
            ? parseCrossAxisAlignment(map['crossAxisAlignment'])
            : CrossAxisAlignment.center,
        mainAxisAlignment: map.containsKey('mainAxisAlignment')
            ? parseMainAxisAlignment(map['mainAxisAlignment'])
            : MainAxisAlignment.start,
        mainAxisSize: map.containsKey('mainAxisSize')
            ? parseMainAxisSize(map['mainAxisSize'])
            : MainAxisSize.min,
        textBaseline: map.containsKey('textBaseline')
            ? parseTextBaseline(map['textBaseline'])
            : null,
        textDirection: map.containsKey('textDirection')
            ? parseTextDirection(map['textDirection'])
            : null,
        verticalDirection: map.containsKey('verticalDirection')
            ? parseVerticalDirection(map['verticalDirection'])
            : VerticalDirection.down,
        children: DynamicWidgetBuilder.buildWidgets(
            map['children'], buildContext, listener),
      ),
    );
  }

  @override
  String get widgetName => "JudoRow";
}

class JudoColumnParser extends WidgetParser {
  @override
  Widget parse(Map<String, dynamic> map, BuildContext buildContext,
      ClickListener listener) {
    return Expanded(
      flex: map.containsKey("col") ? map["col"] : 12,
      child: Row(
        children: [
          Expanded(
            child: Column(
              crossAxisAlignment: map.containsKey('crossAxisAlignment')
                  ? parseCrossAxisAlignment(map['crossAxisAlignment'])
                  : CrossAxisAlignment.center,
              mainAxisAlignment: map.containsKey('mainAxisAlignment')
                  ? parseMainAxisAlignment(map['mainAxisAlignment'])
                  : MainAxisAlignment.start,
              mainAxisSize: map.containsKey('mainAxisSize')
                  ? parseMainAxisSize(map['mainAxisSize'])
                  : MainAxisSize.min,
              textBaseline: map.containsKey('textBaseline')
                  ? parseTextBaseline(map['textBaseline'])
                  : null,
              textDirection: map.containsKey('textDirection')
                  ? parseTextDirection(map['textDirection'])
                  : null,
              verticalDirection: map.containsKey('verticalDirection')
                  ? parseVerticalDirection(map['verticalDirection'])
                  : VerticalDirection.down,
              children: DynamicWidgetBuilder.buildWidgets(
                  map['children'], buildContext, listener),
            ),
          ),
        ],
      ),
    );
  }

  @override
  String get widgetName => "JudoColumn";
}
