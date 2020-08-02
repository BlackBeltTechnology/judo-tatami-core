import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:judo/components/judo_button.dart';
import 'package:judo/components/judo_card.dart';
import 'package:judo/components/judo_container.dart';
import 'package:judo/components/judo_group.dart';
import 'package:judo/components/judo_radio.dart';
import 'package:judo/components/judo_tab.dart';
import 'package:judo/components/judo_title.dart';
import 'package:judo/utilities/constants.dart';
import 'package:judo/utilities/sizing_information.dart';

class HomePage extends StatefulWidget {
  @override
  _HomePageState createState() => _HomePageState();
}

class _HomePageState extends State<HomePage>
    with SingleTickerProviderStateMixin {
  int _exampleIdx = 1;

//  int _radioValue = 0;
//  TabController _tabController;

//  void initState() {
//    super.initState();
//    _tabController = TabController(
//      vsync: this,
//      length: 3,
//    );
//  }

//  void _handleRadioValueChange(int value) {
//    setState(() {
//      _radioValue = value;
//    });
//  }

  void switchExample(int idx) {
    setState(() {
      _exampleIdx = idx;
    });
  }

  void testFunction(Widget widget, int idx) {
    idx = idx != null ? idx : 0;
//
//    print("$idx $widget");
//    if ( widget is MultiChildRenderObjectWidget){
//      for
//    }
  }

  Widget exampleReturn(int idx) {
    switch (idx) {
      case 1:
//        print(Example1().build(context));
//        for (var el
//            in (Example1().build(context) as MultiChildRenderObjectWidget)
//                .children) {
//          print(((el as StatelessWidget).build(context) as StatelessWidget)
//              .build(context));
//          ;
//        }
        return Example1();
        break;
      case 2:
        return Example2();
        break;
      case 3:
        return Example3();
        break;
      case 4:
        return Example4();
        break;
      case 5:
        return Example5();
        break;
      case 6:
        return Example6();
        break;
    }
  }

  @override
  Widget build(BuildContext context) {
    return SafeArea(
      child: Scaffold(
        body: SingleChildScrollView(
          child: Column(
            mainAxisSize: MainAxisSize.min, // required for JudoGroup
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
                      child: Text('6'),
                      onPressed: () => switchExample(6),
                    ),
                  ],
                ),
              ),
              exampleReturn(_exampleIdx),
            ],
          ),
        ),
      ),
    );
  }
}

///////////////////////////////////////////////////////////////

class Example1 extends StatelessWidget {
  const Example1({
    Key key,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        JudoGroup(
          col: 12,
          card: true,
          judoDirection: JudoDirection.Horizontal,
          children: [
            JudoGroup(
              col: 2,
              card: true,
              judoDirection: JudoDirection.Vertical,
              children: [JudoExample(col: 2)],
            ),
            JudoGroup(
              col: 2,
              card: true,
              judoDirection: JudoDirection.Vertical,
              children: [JudoExample(col: 2)],
            ),
            JudoGroup(
              col: 2,
              card: true,
              judoDirection: JudoDirection.Vertical,
              children: [JudoExample(col: 2)],
            ),
            JudoGroup(
              col: 2,
              card: true,
              judoDirection: JudoDirection.Vertical,
              children: [JudoExample(col: 2)],
            ),
            JudoGroup(
              col: 2,
              card: true,
              judoDirection: JudoDirection.Vertical,
              children: [JudoExample(col: 2)],
            ),
            JudoGroup(
              col: 2,
              card: true,
              judoDirection: JudoDirection.Vertical,
              children: [JudoExample(col: 2)],
            ),
          ],
        ),
      ],
    );
  }
}

///////////////////////////////////////////////////////////////////////////////

class Example2 extends StatelessWidget {
  const Example2({
    Key key,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        JudoGroup(
          col: 12,
          card: true,
          judoDirection: JudoDirection.Horizontal,
          children: [
            JudoGroup(
              col: 9,
              card: false,
              judoDirection: JudoDirection.Vertical,
              children: [
                JudoGroup(
                  col: 9,
                  card: false,
                  judoDirection: JudoDirection.Horizontal,
                  children: [
                    JudoGroup(
                      col: 6,
                      card: false,
                      judoDirection: JudoDirection.Vertical,
                      children: [
                        JudoGroup(
                          col: 6,
                          card: true,
                          judoDirection: JudoDirection.Vertical,
                          children: [
                            JudoExample(col: 2),
                            JudoExample(col: 2),
                            JudoExample(col: 2),
                            JudoExample(col: 2),
                          ],
                        ),
                        JudoGroup(
                          col: 6,
                          card: true,
                          judoDirection: JudoDirection.Vertical,
                          children: [
                            JudoExample(col: 2),
                            JudoExample(col: 2),
                            JudoExample(col: 2),
                            JudoExample(col: 2),
                          ],
                        ),
                      ],
                    ),
                    JudoGroup(
                      col: 3,
                      card: true,
                      judoDirection: JudoDirection.Vertical,
                      children: [
                        JudoExample(col: 2),
                        JudoExample(col: 2),
                        JudoExample(col: 2),
                        JudoExample(col: 2),
                        JudoExample(col: 2),
                        JudoExample(col: 2),
                        JudoExample(col: 2),
                        JudoExample(col: 2),
                      ],
                    ),
                  ],
                ),
              ],
            ),
            JudoGroup(
              col: 3,
              card: false,
              judoDirection: JudoDirection.Vertical,
              children: [
                JudoGroup(
                  col: 3,
                  card: true,
                  judoDirection: JudoDirection.Vertical,
                  children: [JudoExample(col: 3), JudoExample(col: 3)],
                ),
                JudoGroup(
                  col: 3,
                  card: true,
                  judoDirection: JudoDirection.Vertical,
                  children: [JudoExample(col: 3), JudoExample(col: 3)],
                ),
              ],
            ),
          ],
        ),
      ],
    );
  }
}

////////////////////////////////////////////////////////////////////////////////

class Example3 extends StatefulWidget {
  @override
  _Example3State createState() => _Example3State();
}

class _Example3State extends State<Example3> {
  int _radioValue1 = 0;

  void _handleRadioValueChange1(int value) {
    setState(() {
      _radioValue1 = value;
    });
  }

  int _radioValue2 = 3;

  void _handleRadioValueChange2(int value) {
    setState(() {
      _radioValue2 = value;
    });
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        JudoGroup(
          col: 12,
          card: true,
          judoDirection: JudoDirection.Horizontal,
          children: [
            JudoGroup(
              col: 6,
              card: true,
              judoDirection: JudoDirection.Vertical,
              children: [
                JudoRadio(
                  label: 'Radio-1',
                  colSize: 2,
                  value: 0,
                  groupValue: _radioValue1,
                  onChanged: _handleRadioValueChange1,
                ),
                JudoRadio(
                  label: 'Radio-2',
                  colSize: 2,
                  value: 1,
                  groupValue: _radioValue1,
                  onChanged: _handleRadioValueChange1,
                ),
                JudoRadio(
                  label: 'Radio-3',
                  colSize: 2,
                  value: 2,
                  groupValue: _radioValue1,
                  onChanged: _handleRadioValueChange1,
                ),
              ],
            ),
            JudoGroup(
              col: 6,
              card: true,
              judoDirection: JudoDirection.Horizontal,
              children: [
                JudoRadio(
                  label: 'Radio-1',
                  colSize: 2,
                  value: 3,
                  groupValue: _radioValue2,
                  onChanged: _handleRadioValueChange2,
                ),
                JudoRadio(
                  label: 'Radio-2',
                  colSize: 2,
                  value: 4,
                  groupValue: _radioValue2,
                  onChanged: _handleRadioValueChange2,
                ),
                JudoRadio(
                  label: 'Radio-3',
                  colSize: 2,
                  value: 5,
                  groupValue: _radioValue2,
                  onChanged: _handleRadioValueChange2,
                ),
              ],
            ),
          ],
        ),
      ],
    );
  }
}

////////////////////////////////////////////////////////////////////////////////

class Example4 extends StatefulWidget {
  @override
  _Example4State createState() => _Example4State();
}

class _Example4State extends State<Example4>
    with SingleTickerProviderStateMixin {
  TabController _tabController;

  void initState() {
    super.initState();
    _tabController = TabController(
      vsync: this,
      length: 3,
    );
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        JudoGroup(
          col: 12,
          judoDirection: JudoDirection.Horizontal,
          card: true,
          children: [
            JudoGroup(
              card: true,
              col: 6,
              judoDirection: JudoDirection.Vertical,
              children: [
                JudoTab(
                  col: 6,
                  tabController: _tabController,
                  tabs: [
                    Tab(
                      icon: Icon(Icons.favorite),
                      text: 'Tab 1',
                    ),
                    Tab(
                      icon: Icon(Icons.favorite),
                      text: 'Tab 2',
                    ),
                    Tab(
                      icon: Icon(Icons.favorite),
                      text: 'Tab 3',
                    ),
                  ],
                  tabContent: [
                    JudoGroup(
                      card: false,
                      col: 6,
                      judoDirection: JudoDirection.Vertical,
                      children: [
                        JudoGroup(
                          card: false,
                          col: 6,
                          judoDirection: JudoDirection.Horizontal,
                          children: [
                            JudoExample(col: 2),
                            JudoExample(col: 2),
                            JudoExample(col: 2),
                          ],
                        ),
                        JudoGroup(
                          card: false,
                          col: 6,
                          judoDirection: JudoDirection.Horizontal,
                          children: [
                            JudoExample(col: 2),
                            JudoExample(col: 2),
                            JudoExample(col: 2),
                          ],
                        ),
                      ],
                    ),
                    JudoGroup(
                      card: false,
                      col: 6,
                      judoDirection: JudoDirection.Vertical,
                      children: [
                        JudoGroup(
                          card: false,
                          col: 6,
                          judoDirection: JudoDirection.Horizontal,
                          children: [
                            JudoExample(col: 2),
                            JudoExample(col: 2),
                            JudoExample(col: 2),
                          ],
                        ),
                      ],
                    ),
                    JudoGroup(
                      card: false,
                      col: 6,
                      judoDirection: JudoDirection.Vertical,
                      children: [
                        JudoGroup(
                          card: false,
                          col: 6,
                          judoDirection: JudoDirection.Horizontal,
                          children: [
                            JudoExample(col: 2),
                            JudoExample(col: 2),
                            JudoExample(col: 2),
                          ],
                        ),
                        JudoGroup(
                          card: false,
                          col: 6,
                          judoDirection: JudoDirection.Horizontal,
                          children: [
                            JudoExample(col: 2),
                            JudoExample(col: 2),
                            JudoExample(col: 2),
                          ],
                        ),
                        JudoGroup(
                          card: false,
                          col: 6,
                          judoDirection: JudoDirection.Horizontal,
                          children: [
                            JudoExample(col: 2),
                            JudoExample(col: 2),
                            JudoExample(col: 2),
                          ],
                        ),
                      ],
                    ),
                  ],
                ),
              ],
            ),
            JudoGroup(
              card: true,
              col: 6,
              judoDirection: JudoDirection.Vertical,
              children: [
                JudoGroup(
                  card: false,
                  col: 6,
                  judoDirection: JudoDirection.Horizontal,
                  children: [
                    JudoExample(col: 2),
                    JudoExample(col: 2),
                    JudoExample(col: 2),
                  ],
                ),
                JudoGroup(
                  card: false,
                  col: 6,
                  judoDirection: JudoDirection.Horizontal,
                  children: [
                    JudoExample(col: 2),
                    JudoExample(col: 2),
                    JudoExample(col: 2),
                  ],
                ),
                JudoGroup(
                  card: false,
                  col: 6,
                  judoDirection: JudoDirection.Horizontal,
                  children: [
                    JudoExample(col: 2),
                    JudoExample(col: 2),
                    JudoExample(col: 2),
                  ],
                ),
              ],
            )
          ],
        ),
      ],
    );
  }
}

class Example5 extends StatelessWidget {
  const Example5({
    Key key,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        JudoGroup(
          col: 12,
          card: true,
          judoDirection: JudoDirection.Horizontal,
          children: [
            JudoGroup(
              col: 6,
              card: false,
              judoDirection: JudoDirection.Vertical,
              children: [
                JudoGroup(
                  col: 6,
                  card: false,
                  judoDirection: JudoDirection.Horizontal,
                  children: [
                    JudoGroup(
                      col: 6,
                      card: false,
                      judoDirection: JudoDirection.Vertical,
                      children: [
                        JudoGroup(
                          col: 3,
                          card: true,
                          judoDirection: JudoDirection.Vertical,
                          children: [
                            JudoTitle(col: 3, text: 'Title 1'),
                            JudoExample(col: 2),
                            JudoExample(col: 2),
                            JudoExample(col: 2),
                            JudoExample(col: 2),
                          ],
                        ),
                        JudoGroup(
                          col: 3,
                          card: true,
                          judoDirection: JudoDirection.Vertical,
                          children: [
                            JudoTitle(col: 3, text: 'Title 2'),
                            JudoExample(col: 2),
                            JudoExample(col: 2),
                            JudoExample(col: 2),
                            JudoExample(col: 2),
                          ],
                        ),
                      ],
                    ),
                  ],
                ),
              ],
            ),
            JudoGroup(
              col: 6,
              card: false,
              judoDirection: JudoDirection.Vertical,
              children: [
                JudoTitle(col: 6, text: 'Title 3'),
                JudoGroup(
                  col: 3,
                  card: true,
                  judoDirection: JudoDirection.Vertical,
                  children: [JudoExample(col: 3), JudoExample(col: 3)],
                ),
                JudoGroup(
                  col: 3,
                  card: true,
                  judoDirection: JudoDirection.Vertical,
                  children: [JudoExample(col: 3), JudoExample(col: 3)],
                ),
              ],
            ),
          ],
        ),
      ],
    );
  }
}

//////////////////////////////////////////////////////////////////////////

class Example6 extends StatelessWidget {
  const Example6({
    Key key,
  }) : super(key: key);

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        JudoGroup(
          col: 12,
          card: true,
          judoDirection: JudoDirection.Horizontal,
          children: [
            JudoGroup(
              col: 2,
              card: true,
              judoDirection: JudoDirection.Vertical,
              children: [
                JudoExample(col: 2),
                JudoButton(
                  col: 2,
                  label: 'Button-1',
                  icon: Icon(Icons.phone),
                  onPressed: null,
                ),
              ],
            ),
            JudoGroup(
              col: 2,
              card: true,
              judoDirection: JudoDirection.Vertical,
              children: [
                JudoExample(col: 2),
                JudoButton(
                  col: 2,
                  label: 'Button-2',
                  onPressed: null,
                ),
              ],
            ),
            JudoGroup(
              col: 2,
              card: true,
              judoDirection: JudoDirection.Vertical,
              children: [
                JudoExample(col: 2),
                JudoButton(
                  col: 2,
                  label: 'Button-1',
                  icon: Icon(Icons.phone),
                  onPressed: null,
                ),
              ],
            ),
            JudoGroup(
              col: 2,
              card: true,
              judoDirection: JudoDirection.Vertical,
              children: [
                JudoExample(col: 2),
                JudoButton(
                  col: 2,
                  label: 'Button-2',
                  onPressed: null,
                ),
              ],
            ),
            JudoGroup(
              col: 2,
              card: true,
              judoDirection: JudoDirection.Vertical,
              children: [JudoExample(col: 2)],
            ),
            JudoGroup(
              col: 2,
              card: true,
              judoDirection: JudoDirection.Vertical,
              children: [JudoExample(col: 2)],
            ),
          ],
        ),
      ],
    );
  }
}
