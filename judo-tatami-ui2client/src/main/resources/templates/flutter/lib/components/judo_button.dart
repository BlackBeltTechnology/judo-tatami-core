part of judo.components;

class JudoButton extends StatelessWidget implements IJudoComponent {
  JudoButton({
    @required this.col,
    this.label,
    this.icon,
    this.onPressed,
    this.rounded = true,
    this.color = kPrimaryColor,
    this.disabledColor = Colors.black26,
    this.textColor = Colors.white,
    this.disabledTextColor = Colors.black26
  });

  final int col;
  final String label;
  final Function onPressed;
  final Icon icon;
  final bool rounded;
  final Color color;
  final Color disabledColor;
  final Color textColor;
  final Color disabledTextColor;

  @override
  int getColSize() {
    return this.col;
  }

  @override
  Widget build(BuildContext context) {
    return JudoContainer(
      padding: EdgeInsets.symmetric(horizontal: 10),
      col: col,
      child: icon != null
          ? RaisedButton.icon(
              shape: RoundedRectangleBorder(
                borderRadius: rounded ? BorderRadius.circular(16.0) : BorderRadius.zero
              ),
              icon: icon,
              label: label != null ? Text(label) : Text(''),
              onPressed: onPressed,
              color: color,
              disabledColor: disabledColor,
              textColor: textColor,
              disabledTextColor: disabledTextColor,
            )
          : RaisedButton(
              shape: RoundedRectangleBorder(
                  borderRadius: rounded ? BorderRadius.circular(16.0) : BorderRadius.zero
              ),
              child: label != null ? Text(label) : Text(''),
              onPressed: onPressed,
              color: color,
              disabledColor: disabledColor,
              textColor: textColor,
              disabledTextColor: disabledTextColor,
            ),
    );
  }
}
