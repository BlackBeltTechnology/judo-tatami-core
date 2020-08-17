part of judo.components;

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
