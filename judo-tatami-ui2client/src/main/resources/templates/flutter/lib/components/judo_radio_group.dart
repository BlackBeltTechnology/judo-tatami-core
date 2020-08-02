import 'package:flutter/material.dart';
import 'package:judo/components/judo_radio.dart';
import 'package:judo/utilities/constants.dart';

class JudoRadioGroup extends StatefulWidget {
  JudoRadioGroup({
    @required this.col,
    @required this.children,
  });

  final int col;
  final List<JudoRadio> children;

  @override
  _JudoRadioGroupState createState() => _JudoRadioGroupState();
}

class _JudoRadioGroupState extends State<JudoRadioGroup> {
  int _radioGroupValue;

  @override
  void initState() {
    super.initState();
    _radioGroupValue = -1;
  }

  @override
  Widget build(BuildContext context) {
    return Expanded(
      flex: widget.col,
      child: Row(
          mainAxisAlignment: MainAxisAlignment.center,
          children: widget.children
              .map<JudoRadio>((e) => JudoRadio(
                    label: e.label,
                    colSize: e.colSize,
                    value: e.value,
                    onChanged: (int value) {
                      setState(() {
                        _radioGroupValue = value;
                      });
                    },
                    groupValue: _radioGroupValue,
                  ))
              .toList()),
    );
  }
}
