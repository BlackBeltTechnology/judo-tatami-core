part of judo.components;

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
