import 'package:dynamic_widget/dynamic_widget.dart';
import 'package:flutter/material.dart';
import 'package:judo/components/parsers/judo_button_parser.dart';
import 'package:judo/components/parsers/judo_group_parser.dart';
import 'package:judo/components/parsers/judo_input_text_parser.dart';
import 'package:judo/components/parsers/judo_radio_parser.dart';
import 'package:judo/components/parsers/judo_tab_parser.dart';
import 'package:judo/components/parsers/judo_title_parser.dart';
import 'package:judo/components/parsers/tab_parser.dart';
import 'package:judo/screens/client_rest_mobx_testing.dart';
import 'package:judo/utilities/constants.dart';
import 'package:judo/utilities/example_jsons.dart';
import 'package:judo/utilities/height_calculate.dart';
import 'package:judo/utilities/rest_test_component.dart';
import 'package:judo/utilities/sizing_information.dart';

class DynamicHomePage extends StatefulWidget {
  DynamicHomePage();

  @override
  _DynamicHomePageState createState() => _DynamicHomePageState();
}

class _DynamicHomePageState extends State<DynamicHomePage> {
  int _exampleIdx = 1;
  double currentMaxCol;
  double pageHeight = 0;
  String json;

  void switchExample(int idx) {
    setState(() {
      _exampleIdx = idx;
      updatePageProperties();
    });
  }

  @override
  void initState() {
    super.initState();
    DynamicWidgetBuilder.addParser(JudoInputTextWidgetParser());
    DynamicWidgetBuilder.addParser(JudoRowParser());
    DynamicWidgetBuilder.addParser(JudoColumnParser());
    DynamicWidgetBuilder.addParser(JudoTabParser());
    DynamicWidgetBuilder.addParser(JudoRadioGroupParser());
    DynamicWidgetBuilder.addParser(JudoButtonParser());
    DynamicWidgetBuilder.addParser(JudoTitleParser());
    updatePageProperties();
  }

  void updatePageProperties() {
    setState(() {
      var results = HeightCalculate().results(getExampleJson(_exampleIdx));

      currentMaxCol = SizingInformation.maxCol;
      json = results.value1;
      pageHeight = results.value2;
    });
  }

  void deviceSwitch() {
    if (currentMaxCol != SizingInformation.maxCol) {
      updatePageProperties();
    }
  }

  @override
  Widget build(BuildContext context) {
    deviceSwitch();

    return SafeArea(
      child: Scaffold(
        body: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min,
            crossAxisAlignment: CrossAxisAlignment.stretch,
            children: [
              SingleChildScrollView(
                scrollDirection: Axis.horizontal,
                child: Row(
                  children: [
                    RaisedButton(
                      child: Text('1'),
                      onPressed: () => switchExample(1),
                    ),
                    RaisedButton(
                      child: Text('2'),
                      onPressed: () => switchExample(2),
                    ),
                    RaisedButton(
                      child: Text('3'),
                      onPressed: () => switchExample(3),
                    ),
                    RaisedButton(
                      child: Text('4'),
                      onPressed: () => switchExample(4),
                    ),
                    RaisedButton(
                      child: Text('5'),
                      onPressed: () => switchExample(5),
                    ),
                    RaisedButton(
                      child: Text('6 (local only)'),
                      onPressed: () => switchExample(6),
                    ),
                    RaisedButton(
                      child: Text('7 (local only)'),
                      onPressed: () => switchExample(7),
                    ),
                  ],
                ),
              ),
              switchWidget(),
            ],
          ),
        ),
      ),
    );
  }

  Future<Widget> _buildWidget(BuildContext context) async {
    return DynamicWidgetBuilder.build(
        json, context, new DefaultClickListener());
  }

  Widget switchWidget() {
    if (_exampleIdx == 6) {
      return HttpRequestTesting();
    } else if (_exampleIdx == 7) {
      return ClientRestMobxTest();
    } else {
      return FutureBuilder<Widget>(
        future: _buildWidget(context),
        builder: (BuildContext context, AsyncSnapshot<Widget> snapshot) {
          if (snapshot.hasError) {
            print(snapshot.error);
          }
          return snapshot.hasData
              ? SizedBox.fromSize(
                  size: Size(double.infinity, pageHeight),
                  child: Column(
                    children: [
                      snapshot.data,
                    ],
                  ),
                )
              : Container(
                  height: kJudoHeight,
                  child: Text("Loading..."),
                );
        },
      );
    }
  }
}

class DefaultClickListener implements ClickListener {
  @override
  void onClicked(String event) {
    print("Receive click event: " + event);
  }
}
