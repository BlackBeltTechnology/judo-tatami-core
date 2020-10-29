part of judo.components;

class JudoTimeInput extends StatelessWidget {
  JudoTimeInput({
    this.key,
    @required this.col,
    this.label,
    this.icon,
    this.onChanged,
    @required this.initialDate,
    this.readOnly = false,
    this.disabled = false,
    this.use24HourFormat,
  });

  final Key key;
  final int col;
  final String label;
  final Icon icon;
  final Function onChanged;
  final TimeOfDay initialDate;
  final bool readOnly;
  final bool disabled;
  final bool use24HourFormat;

  @override
  Widget build(BuildContext context) {
    return JudoContainer(
      padding: EdgeInsets.symmetric(horizontal: 10),
      col: col,
      child: TextFormField(
        key: key,
        readOnly: disabled ? true : readOnly,
        enabled: disabled ? false : !readOnly,
        initialValue: initialDate != null ? initialDate.toString() : TimeOfDay.now().toString() ,
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
        onChanged: (value) => onChangedHandler(TimeOfDay.fromDateTime(DateTime.parse(value))),
      ),
    );
  }

  Widget iconDatePicker(BuildContext context) {
    var tempTime = this.initialDate ?? DateTime.now();
    return IconButton(
        icon: Icon(
          Icons.calendar_today,
          color: disabled ? kDisabledColor : null,
        ),
        onPressed: disabled ? null : () async {
          tempTime = await showTimePicker(
              context: context,
              initialTime: tempTime,
              initialEntryMode: TimePickerEntryMode.input,
              builder: use24HourFormat ? (BuildContext context, Widget child) {
                return MediaQuery(
                  data: MediaQuery.of(context).copyWith(alwaysUse24HourFormat: true),
                  child: child,
                );
              }
                  :
              null
          );
          onChangedHandler(tempTime);
        }
    );
  }

  void onChangedHandler(TimeOfDay value) {
    if (this.onChanged != null) {
      this.onChanged(value);
    }
  }
}
