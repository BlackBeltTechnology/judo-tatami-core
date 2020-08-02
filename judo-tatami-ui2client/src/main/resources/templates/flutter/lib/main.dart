import 'dart:io';

import 'package:flutter/material.dart';
import 'package:judo/screens/dynamic_home_page.dart';
import 'package:judo/screens/home_page.dart';
import 'package:judo/store/external/northwind_external_ap_store.dart';
import 'package:judo/store/internal/northwind_internal_ap_store.dart';
import 'package:judo/utilities/constants.dart';
import 'package:judo/utilities/example_jsons.dart';
import 'package:judo/utilities/sizing_information.dart';
import 'package:openapi_dart_common/openapi.dart';
import 'package:provider/provider.dart';

void main() {
  runApp(MyApp());
}

class MyApp extends StatelessWidget {
  @override
  Widget build(BuildContext context) => MultiProvider(
          providers: [
            Provider<NorthwindExternalApStore>(
                create: (_) => NorthwindExternalApStore()),
            Provider<NorthwindInternalApStore>(
                create: (_) => NorthwindInternalApStore()),
          ],
          child: MaterialApp(
            theme: ThemeData.light().copyWith(
              primaryColor: kPrimaryColor,
            ),
            home: SafeArea(
              child: LayoutBuilder(builder: (context, constraints) {
                if (constraints.maxWidth >= 980) {
                  SizingInformation.maxCol = 12;
                  SizingInformation.padding = kJudoCardPadding;
                  SizingInformation.margin = kJudoCardMargin;

                  return DynamicHomePage();
                } else if (constraints.maxWidth < 980 &&
                    constraints.maxWidth > 500) {
                  SizingInformation.maxCol = 8;
                  SizingInformation.padding = kJudoCardPadding / 2;
                  SizingInformation.margin = kJudoCardMargin / 2;

                  return DynamicHomePage();
                } else {
                  SizingInformation.maxCol = 4;
                  SizingInformation.padding = kJudoCardPadding / 4;
                  SizingInformation.margin = kJudoCardMargin / 4;

                  return DynamicHomePage();
                }
              }),
            ),
          ));
}
