import 'package:flutter/material.dart';
import 'package:judo/components/judo_component.dart';
import 'judo_container.dart';

class JudoInputText extends StatelessWidget implements IJudoComponent {
  JudoInputText({
    @required this.col,
    this.label,
    this.icon,
    this.onChanged,
    this.initialValue,
  });

  final int col;
  final String label;
  final Icon icon;
  final Function onChanged;
  final String initialValue;

  @override
  int getColSize() {
    return this.col;
  }

  @override
  Widget build(BuildContext context) {
    return JudoContainer(
      padding: EdgeInsets.symmetric(horizontal: 10),
      col: col,
      child: TextFormField(
        initialValue: initialValue,
        decoration: InputDecoration(
          labelText: label,
          prefixIcon: icon,
        ),
        onChanged: onChanged,
      ),
    );
  }
}
