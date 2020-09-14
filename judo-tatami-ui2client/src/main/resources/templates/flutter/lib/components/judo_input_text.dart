part of judo.components;

class JudoInputText extends StatelessWidget implements IJudoComponent {
  JudoInputText({
    this.key,
    @required this.col,
    this.label,
    this.icon,
    this.onChanged,
    this.initialValue,
    this.readOnly = false,
  });

  final Key key;
  final int col;
  final String label;
  final Icon icon;
  final Function onChanged;
  final String initialValue;
  final bool readOnly;

  @override
  int getColSize() {
    return this.col;
  }

  @override
  Widget build(BuildContext context) {
    return JudoContainer(
      padding: EdgeInsets.symmetric(horizontal: 10),
      col: col,
      child: TextFormField(
        key: key,
        readOnly: readOnly,
        enabled: !readOnly,
        initialValue: initialValue,
        decoration: readOnly ?
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
