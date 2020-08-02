import 'dart:math';

import 'package:flutter/material.dart';
import 'package:judo/components/judo_component.dart';
import 'package:judo/components/judo_container.dart';
import 'package:judo/components/judo_group.dart';
import 'package:judo/utilities/constants.dart';

class JudoTab extends StatelessWidget implements IJudoComponent {
  JudoTab({
    @required this.col,
    @required this.tabController,
    @required this.tabs,
    @required this.tabContent,
  });

  final TabController tabController;
  final int col;
  final List<Tab> tabs;
  final List<JudoGroup> tabContent;

  @override
  int getColSize() {
    return this.col;
  }

  @override
  Widget build(BuildContext context) {
    List<int> heightList = [];
    for (var el in tabContent) {
      heightList.add(el.getRowCounter());
    }

    return JudoGroup(
      card: false,
      judoDirection: JudoDirection.Vertical,
      col: col,
      children: [
        JudoTabContainer(
          col: col,
          child: TabBar(
            controller: tabController,
            tabs: tabs,
            labelColor: kPrimaryColor,
            indicatorColor: kPrimaryColor,
          ),
        ),
        JudoTabViewContainer(
          height: heightList.reduce(max) * kJudoHeight,
          col: col,
          child: Expanded(
            child: TabBarView(
              controller: tabController,
              children: tabContent,
            ),
          ),
        ),
      ],
    );
  }
}

class JudoTabContainer extends StatelessWidget implements IJudoComponent {
  JudoTabContainer({
    @required this.col,
    @required this.child,
  });

  final int col;
  final Widget child;

  @override
  int getColSize() {
    return col;
  }

  @override
  Widget build(BuildContext context) {
    return Container(
      constraints: BoxConstraints(
        maxHeight: kJudoHeight,
      ),
      child: child,
    );
  }
}

class JudoTabViewContainer extends StatelessWidget implements IJudoComponent {
  JudoTabViewContainer({
    @required this.col,
    @required this.height,
    this.child,
  });

  final int col;
  final double height;
  final Widget child;

  @override
  int getColSize() {
    return col;
  }

  @override
  Widget build(BuildContext context) {
    return ConstrainedBox(
      constraints: BoxConstraints(
        maxHeight: height,
      ),
      child: child,
    );
  }
}
