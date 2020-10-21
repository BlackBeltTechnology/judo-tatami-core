part of judo.components;

class JudoDateInput extends StatelessWidget implements IJudoComponent {
  JudoDateInput({
    this.key,
    @required this.col,
    this.label,
    this.icon,
    this.onChanged,
    @required this.initialDate,
    this.readOnly = false,
    this.disabled = false,
    this.firstDate,
    this.lastDate,
  });

  final Key key;
  final int col;
  final String label;
  final Icon icon;
  final Function onChanged;
  final DateTime initialDate;
  final bool readOnly;
  final bool disabled;
  final DateTime firstDate;
  final DateTime lastDate;

  final DateFormat formatter = DateFormat('yyyy-MM-dd');

  @override
  int getColSize() {
    return this.col;
  }

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
          initialValue: formatter.format(initialDate ?? DateTime.now()),
          decoration: disabled ?
          InputDecoration(
            labelText: label,
            prefixIcon: icon,
            suffixIcon: iconDatePicker(context),
          )
              : readOnly ?
          InputDecoration(
              labelText: label,
              prefixIcon: icon,
              border: InputBorder.none,
              focusedBorder: InputBorder.none,
              enabledBorder: InputBorder.none,
              errorBorder: InputBorder.none,
              disabledBorder: InputBorder.none,

          )
              :
          InputDecoration(
              labelText: label,
              prefixIcon: icon,
              suffixIcon: iconDatePicker(context),
          ),
          onChanged: (value) => onChangedHandler(DateTime.parse(value)),
        ),
    );
  }

  Widget iconDatePicker(BuildContext context) {
    var tempDateTime = this.initialDate ?? DateTime.now();
    return IconButton(
        icon: Icon(
            Icons.calendar_today,
            color: disabled ? kDisabledColor : null,
        ),
        onPressed: disabled ? null : () async {
          tempDateTime = await showDatePicker(
            context: context,
            initialDate: tempDateTime,
            firstDate: this.firstDate ?? DateTime(1900),
            lastDate: this.lastDate ?? DateTime(2100),
          );
          onChangedHandler(tempDateTime);
        }
    );

  }

  void onChangedHandler(DateTime value) {
    if (this.onChanged != null) {
      this.onChanged(value);
    }
  }
}
