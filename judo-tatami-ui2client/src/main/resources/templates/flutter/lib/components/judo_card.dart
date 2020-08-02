import 'package:flutter/material.dart';
import 'package:judo/utilities/constants.dart';
import 'package:judo/utilities/sizing_information.dart';

class JudoCard extends StatelessWidget {
  JudoCard({this.child});

  final Widget child;

  @override
  Widget build(BuildContext context) {
    return Container(
      margin: EdgeInsets.all(SizingInformation.margin),
      padding: EdgeInsets.all(SizingInformation.padding),
      child: child,
      decoration: BoxDecoration(
        color: Colors.white,
        boxShadow: [
          BoxShadow(
            color: Colors.grey.withOpacity(0.5),
            spreadRadius: 3,
            blurRadius: 7,
            offset: Offset(1, 1), // changes position of shadow
          ),
        ],
      ),
    );
  }
}
