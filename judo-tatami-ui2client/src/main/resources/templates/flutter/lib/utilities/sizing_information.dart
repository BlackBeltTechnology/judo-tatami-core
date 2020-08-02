import 'dart:ui';

import 'package:flutter/material.dart';
import 'package:judo/utilities/constants.dart';

class SizingInformation {
  static double margin;
  static double padding;
  static double maxCol;

  static Size displaySize(BuildContext context) {
    return MediaQuery.of(context).size;
  }

  static double displayHeight(BuildContext context) {
    return displaySize(context).height;
  }

  static double displayWidth(BuildContext context) {
    return displaySize(context).width;
  }
}
