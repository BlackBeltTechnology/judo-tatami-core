part of judo.components;

class JudoRadioGroupParser extends WidgetParser {
  int _idx = 0;

  @override
  Widget parse(Map<String, dynamic> map, BuildContext buildContext, ClickListener listener) {
    List<JudoRadio> radioList = map['children']
        .map<JudoRadio>((e) => JudoRadio(
              label: e.containsKey('label') ? e['label'] : '',
              value: e.containsKey('value') ? e['value'] : _idx++,
              colSize: e.containsKey('col') ? e['col'] : 1,
            ))
        .toList();

    return JudoRadioGroup(
      col: map.containsKey('col') ? map['col'] : 12,
      children: radioList,
    );
  }

  @override
  String get widgetName => 'JudoRadioGroup';
}
