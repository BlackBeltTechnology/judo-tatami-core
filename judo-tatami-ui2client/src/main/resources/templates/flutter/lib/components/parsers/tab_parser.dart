part of judo.components;

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
