part of judo.components;

class JudoTabParser extends WidgetParser {
  @override
  Widget parse(Map<String, dynamic> map, BuildContext buildContext, ClickListener listener) {
    List<Tab> tabList = map['tabs']
        .map<Tab>((e) => Tab(
              icon: e.containsKey('icon') ? Icon(getIconGuessFavorMaterial(name: e['icon'])) : null,
              text: e.containsKey('label') ? e['label'] : 'Tab',
            ))
        .toList();

    return JudoTab(
      col: map.containsKey("col") ? map["col"] : 12,
      tabContent: DynamicWidgetBuilder.buildWidgets(map['children'], buildContext, listener),
      tabs: tabList,
    );
  }

  @override
  String get widgetName => "JudoTab";
}
