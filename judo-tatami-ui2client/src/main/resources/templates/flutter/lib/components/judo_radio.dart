import 'package:flutter/material.dart';
import 'package:judo/components/judo_component.dart';
import 'package:judo/components/judo_container.dart';
import 'package:judo/utilities/constants.dart';

class JudoRadio extends StatelessWidget implements IJudoComponent {
  JudoRadio({
    this.label,
    this.value,
    this.groupValue,
    this.onChanged,
    this.colSize,
  });

  final String label;
  final int value;
  final int groupValue;
  final Function onChanged;
  final int colSize;

  @override
  int getColSize() {
    return this.colSize;
  }

  @override
  Widget build(BuildContext context) {
    return JudoContainer(
      col: colSize,
      child: Row(
        children: [
          Radio(
            activeColor: kPrimaryColor,
            value: value,
            groupValue: groupValue,
            onChanged: onChanged,
          ),
          Text(
            label,
            maxLines: 2,
            overflow: TextOverflow.ellipsis,
          ),
        ],
      ),
    );
  }
}
