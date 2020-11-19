part of judo.components;

class JudoButton extends StatelessWidget {
  JudoButton({
    @required this.col,
    this.label,
    this.icon,
    this.onPressed,
    this.rounded = true,
    this.color = kPrimaryColor,
    this.disabled = false,
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
  final bool disabled;
  final Color disabledColor;
  final Color textColor;
  final Color disabledTextColor;

  @override
  Widget build(BuildContext context) {
    return JudoContainer(
      padding: EdgeInsets.symmetric(horizontal: 10),
      col: col,
      child: icon != null
          ? Align(
            alignment: Alignment.centerLeft,
            child: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                Flexible(
                  fit: FlexFit.loose,
                  child: RaisedButton.icon(
        shape: RoundedRectangleBorder(
                    borderRadius: rounded ? BorderRadius.circular(16.0) : BorderRadius.zero
        ),
        icon: icon,
        label: label != null ? Text(label) : Text(''),
        onPressed: disabled ? null : onPressed,
        color: color,
        disabledColor: disabledColor,
        textColor: textColor,
        disabledTextColor: disabledTextColor,
      ),
                ),
              ],
            ),
          )
          : Align(
            alignment: Alignment.centerLeft,
            child: Row(
              mainAxisSize: MainAxisSize.min,
              children: [
                Flexible(
                  fit: FlexFit.loose,
                  child: RaisedButton(
        shape: RoundedRectangleBorder(
                    borderRadius: rounded ? BorderRadius.circular(16.0) : BorderRadius.zero
        ),
        child: label != null ? Text(label) : Text(''),
        onPressed: disabled ? null : onPressed,
        color: color,
        disabledColor: disabledColor,
        textColor: textColor,
        disabledTextColor: disabledTextColor,
      ),
                ),
              ],
            ),
          ),
    );
  }
}
