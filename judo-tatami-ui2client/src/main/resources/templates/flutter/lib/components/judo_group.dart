import 'package:flutter/material.dart';
import 'package:judo/components/judo_card.dart';
import 'package:judo/components/judo_component.dart';
import 'package:judo/utilities/sizing_information.dart';

class JudoGroup extends StatelessWidget implements IJudoComponent {
  JudoGroup({
    @required this.card,
    @required this.col,
    @required this.judoDirection,
    @required this.children,
    this.mainAlignment = MainAxisAlignment.center,
    this.crossAlignment = CrossAxisAlignment.center,
  });

  final List<Widget> children;
  final bool card;
  final int col;
  final JudoDirection judoDirection;
  final MainAxisAlignment mainAlignment;
  final CrossAxisAlignment crossAlignment;

  int getRowCounter() {
    int counter = 0;
    for (var el in children) {
      if (el is JudoGroup) {
        if (el.judoDirection == JudoDirection.Horizontal) {
          counter++;
        }
        counter += el.getRowCounter();
      } else if (el is JudoRow) {
        counter++;
      }
    }
    return counter;
  }

  @override
  int getColSize() {
    return col;
  }

  @override
  Widget build(BuildContext context) {
    List<Widget> tempList = new List<Widget>();
    List<Widget> resultChildren = new List<Widget>();
    int counter = 0;
    bool customChildren = false;

    try {
      for (int i = 0; i < children.length; i++) {
        counter += (children[i] as IJudoComponent).getColSize();
      }
    } on Exception catch (e) {
      print(e);
    }

    try {
      if (judoDirection == JudoDirection.Horizontal &&
          counter > SizingInformation.maxCol) {
        counter = 0;
        customChildren = true;

        for (int i = 0; i < children.length; i++) {
          int currentCol = children.cast<IJudoComponent>()[i].getColSize();
          counter += currentCol;

          if (counter <= SizingInformation.maxCol) {
            tempList.add(children[i]);
          } else {
            resultChildren.add(JudoGroup(
              mainAlignment: mainAlignment,
              crossAlignment: crossAlignment,
              judoDirection: JudoDirection.Horizontal,
              card: false,
              col: counter - currentCol,
              children: tempList,
            ));
            tempList = new List<Widget>();
            counter = currentCol;
            tempList.add(children[i]);
          }
        }
        if (tempList.length != 0) {
          resultChildren.add(JudoRow(
            card: false,
            col: counter,
            children: tempList,
          ));
        }
      }
    } on Exception catch (e) {
      print(e);
    }

    if (customChildren) {
      return JudoColumn(
        mainAlignment: mainAlignment,
        crossAlignment: crossAlignment,
        col: col,
        card: card,
        children: resultChildren,
      );
    } else {
      resultChildren = children;
      switch (judoDirection) {
        case JudoDirection.Horizontal:
          return JudoRow(
            mainAlignment: mainAlignment,
            crossAlignment: crossAlignment,
            col: col,
            card: card,
            children: resultChildren,
          );
        case JudoDirection.Vertical:
          return JudoColumn(
            mainAlignment: mainAlignment,
            crossAlignment: crossAlignment,
            col: col,
            card: card,
            children: resultChildren,
          );
      }
    }
    return JudoColumn(
      children: [Text('Something went wrong in JudoGroup component.')],
      card: true,
      col: 12,
    );
  }
}

enum JudoDirection {
  Vertical, // column
  Horizontal, // row
}

class JudoColumn extends StatelessWidget {
  JudoColumn({
    this.children,
    this.col,
    this.card,
    this.mainAlignment = MainAxisAlignment.center,
    this.crossAlignment = CrossAxisAlignment.center,
  });

  final List<Widget> children;
  final int col;
  final bool card;

  final MainAxisAlignment mainAlignment;
  final CrossAxisAlignment crossAlignment;

  @override
  Widget build(BuildContext context) {
    return Flexible(
      flex: col,
      child: Row(
        children: [
          Flexible(
            child: card
                ? JudoCard(
                    child: Column(
                      mainAxisAlignment: mainAlignment,
                      crossAxisAlignment: crossAlignment,
                      mainAxisSize: MainAxisSize.min,
                      children: children,
                    ),
                  )
                : Column(
                    mainAxisAlignment: mainAlignment,
                    crossAxisAlignment: crossAlignment,
                    mainAxisSize: MainAxisSize.min,
                    children: children,
                  ),
          ),
        ],
      ),
    );
  }
}

class JudoRow extends StatelessWidget {
  JudoRow({
    this.children,
    this.col,
    this.card,
    this.mainAlignment = MainAxisAlignment.center,
    this.crossAlignment = CrossAxisAlignment.center,
  });

  final List<Widget> children;
  final int col;
  final bool card;

  final MainAxisAlignment mainAlignment;
  final CrossAxisAlignment crossAlignment;

  @override
  Widget build(BuildContext context) {
    return Flexible(
      flex: col,
      child: card
          ? JudoCard(
              child: Row(
                mainAxisAlignment: mainAlignment,
                crossAxisAlignment: crossAlignment,
                children: children,
              ),
            )
          : Row(
              mainAxisAlignment: mainAlignment,
              crossAxisAlignment: crossAlignment,
              children: children,
            ),
    );
  }
}
