part of judo.components;

class JudoColumn extends StatelessWidget {
  JudoColumn({
    this.children,
    this.col,
    this.row,
    this.mainAxisAlignment,
    this.crossAxisAlignment,
    this.mainAxisSize,
  });

  final List<Widget> children;
  final int col;
  final int row;
  final MainAxisAlignment mainAxisAlignment;
  final CrossAxisAlignment crossAxisAlignment;
  final MainAxisSize mainAxisSize;

  @override
  Widget build(BuildContext context) {
    return JudoContainer(
      col: col,
      row: row,
      child: Column(
        mainAxisAlignment: mainAxisAlignment ?? MainAxisAlignment.start,
        crossAxisAlignment: crossAxisAlignment ?? CrossAxisAlignment.center,
        mainAxisSize: mainAxisSize ?? MainAxisSize.min,
        children: children.map((e) => Row(
          children: [
            e
          ],
        )).toList(),
      ),
    );
  }
}