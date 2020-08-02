import 'package:flutter/material.dart';
import 'package:judo/components/judo_component.dart';
import 'package:judo/components/judo_container.dart';
import 'package:judo/utilities/constants.dart';

class JudoTitle extends StatelessWidget implements IJudoComponent {
  JudoTitle({
    @required this.col,
    @required this.text,
  });

  final String text;
  final int col;

  @override
  int getColSize() {
    return this.col;
  }

  @override
  Widget build(BuildContext context) {
    return JudoContainer(
      padding: EdgeInsets.symmetric(horizontal: 10),
      col: col,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(
            text,
            style: TextStyle(
                fontWeight: FontWeight.w900,
                color: kSecondaryColor,
                fontSize: 24),
          ),
          SizedBox(
            height: 10,
            child: Divider(
              color: Colors.grey,
              thickness: 1,
            ),
          ),
        ],
      ),
    );
  }
}
