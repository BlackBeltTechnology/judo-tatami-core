part of judo.components;

class JudoInputText extends StatelessWidget {
  JudoInputText({
    this.key,
    @required this.col,
    this.label,
    this.icon,
    this.onChanged,
    this.initialValue,
    this.readOnly = false,
    this.disabled = false,
  });

  final Key key;
  final int col;
  final String label;
  final Icon icon;
  final Function onChanged;
  final String initialValue;
  final bool readOnly;
  final bool disabled;

  @override
  Widget build(BuildContext context) {
    return JudoContainer(
      color: disabled ? kDisabledColor : null,
      padding: EdgeInsets.symmetric(horizontal: 10),
      col: col,
      child: TextFormField(
        key: key,
        readOnly: disabled ? true : readOnly,
        enabled: disabled ? false : !readOnly,
        initialValue: initialValue,
        decoration: disabled ?
        InputDecoration(
          labelText: label,
          prefixIcon: icon,
        )
            : readOnly ?
        InputDecoration(
            labelText: label,
            prefixIcon: icon,
            border: InputBorder.none,
            focusedBorder: InputBorder.none,
            enabledBorder: InputBorder.none,
            errorBorder: InputBorder.none,
            disabledBorder: InputBorder.none)
            :
        InputDecoration(
            labelText: label,
            prefixIcon: icon),
        onChanged: onChanged,
      ),
    );
  }
}
