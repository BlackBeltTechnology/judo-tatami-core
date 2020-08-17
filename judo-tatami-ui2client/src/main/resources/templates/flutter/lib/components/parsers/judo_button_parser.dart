part of judo.components;

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
