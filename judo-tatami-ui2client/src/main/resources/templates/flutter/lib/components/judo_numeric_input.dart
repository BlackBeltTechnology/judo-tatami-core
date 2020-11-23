part of judo.components;

class JudoNumericInput extends StatefulWidget {

  JudoNumericInput({
    this.key,
    @required this.col,
    this.label,
    this.icon,
    this.onChanged,
    this.initialValue,
    this.readOnly = false,
    this.disabled = false,
    this.padding,
    this.stretch = false,
    this.alignment = Alignment.centerLeft,
  });

  final Key key;
  final int col;
  final String label;
  final Icon icon;
  final Function onChanged;
  final String initialValue;
  final bool readOnly;
  final bool disabled;
  final bool stretch;
  final Alignment alignment;
  final EdgeInsets padding;

  @override
  _JudoNumericInputState createState() => _JudoNumericInputState();
}

class _JudoNumericInputState extends State<JudoNumericInput> {
  TextEditingController _controller;
  RegExp _regExp = RegExp(r"^[+|\-]{0,1}\d*\.{0,1}\d*$");

  @override
  void initState() {
    super.initState();
    _controller = TextEditingController(text: widget.initialValue);
  }

  void dispose() {
    _controller.dispose();
    super.dispose();
  }

  @override
  Widget build(BuildContext context) {
    return JudoContainer(
      color: widget.disabled ? kDisabledColor : null,
      padding: widget.padding ?? EdgeInsets.symmetric(horizontal: 10),
      col: widget.col,
      stretch: widget.stretch,
      alignment: widget.alignment,
      child: TextField(
        key: widget.key,
        controller: _controller,
        readOnly: widget.disabled ? true : widget.readOnly,
        enabled: widget.disabled ? false : !widget.readOnly,
        keyboardType: TextInputType.number,
        inputFormatters: [
          TextInputFormatter.withFunction((oldValue, newValue) {
            var correctTextEditingValue = TextEditingValue();
            if(_regExp.hasMatch(newValue.text)){
              correctTextEditingValue = TextEditingValue().copyWith(text: newValue.text, composing: newValue.composing, selection: newValue.selection);
            } else {
              correctTextEditingValue = TextEditingValue().copyWith(text: oldValue.text, composing: oldValue.composing, selection: oldValue.selection);
            }
            return correctTextEditingValue;
          }),
        ],
        decoration: widget.disabled ?
        InputDecoration(
          labelText: widget.label,
          prefixIcon: widget.icon,
        )
            : widget.readOnly ?
        InputDecoration(
            labelText: widget.label,
            prefixIcon: widget.icon,
            border: InputBorder.none,
            focusedBorder: InputBorder.none,
            enabledBorder: InputBorder.none,
            errorBorder: InputBorder.none,
            disabledBorder: InputBorder.none)
            :
        InputDecoration(
            labelText: widget.label,
            prefixIcon: widget.icon,
        ),
        onChanged: (value) {
            return widget.onChanged(value);
        },
      ),
    );
  }
}
