part of judo.components;

class JudoContainer extends StatelessWidget {
  JudoContainer({
    this.child,
    @required this.col,
    this.row = 1,
    this.padding,
    this.color,
  });

  final Widget child;
  final EdgeInsets padding;
  final int col;
  final int row;
  final Color color;

  @override
  Widget build(BuildContext context) {
    return Expanded(
      flex: col,
      child: Container(
        color: color,
        constraints: BoxConstraints(
          maxHeight: row * kJudoHeight,
        ),
        padding: padding,
        child: child,
      ),
    );
  }
}