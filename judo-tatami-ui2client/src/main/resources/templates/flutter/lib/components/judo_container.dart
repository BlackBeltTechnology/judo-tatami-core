part of judo.components;

class JudoContainer extends StatelessWidget implements IJudoComponent {
  JudoContainer({
    this.child,
    @required this.col,
    this.padding,
  });

  final Widget child;
  final EdgeInsets padding;
  final int col;

  @override
  int getColSize() {
    return this.col;
  }

  @override
  Widget build(BuildContext context) {
    return Flexible(
      flex: col,
      child: Container(
        constraints: BoxConstraints(
          maxHeight: kJudoHeight,
        ),
        padding: padding,
        child: child,
      ),
    );
  }
}

class JudoExample extends StatelessWidget implements IJudoComponent {
  JudoExample({
    @required this.col,
  });

  final int col;

  @override
  int getColSize() {
    return this.col;
  }

  @override
  Widget build(BuildContext context) {
    return JudoInputText(
      col: col,
      label: 'col',
      icon: Icon(Icons.print),
    );
  }
}
