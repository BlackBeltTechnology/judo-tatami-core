import 'package:flutter/material.dart';
import 'package:judo/components/judo_component.dart';
import 'package:judo/utilities/constants.dart';
import 'judo_container.dart';

class JudoButton extends StatelessWidget implements IJudoComponent {
  JudoButton({
    @required this.col,
    this.label,
    this.icon,
    this.onPressed,
  });

  final int col;
  final String label;
  final Function onPressed;
  final Icon icon;

  @override
  int getColSize() {
    return this.col;
  }

  @override
  Widget build(BuildContext context) {
    return JudoContainer(
      padding: EdgeInsets.symmetric(horizontal: 10),
      col: col,
      child: icon != null
          ? RaisedButton.icon(
              icon: icon,
              label: label != null ? Text(label) : Text(''),
              onPressed: onPressed,
              color: kPrimaryColor,
              disabledColor: kPrimaryColor,
              textColor: Colors.black,
              disabledTextColor: Colors.black,
            )
          : RaisedButton(
              child: label != null ? Text(label) : Text(''),
              onPressed: onPressed,
              color: kPrimaryColor,
              disabledColor: kPrimaryColor,
              textColor: Colors.black,
              disabledTextColor: Colors.black,
            ),
    );
  }
}
