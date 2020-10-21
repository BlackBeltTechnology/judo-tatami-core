part of judo.components;

class JudoComboBox<T> extends StatefulWidget {

  JudoComboBox({
    @required this.col,
    @required this.items,
    @required this.onChanged,
    @required this.value,
    @required this.dropdownMenuShow,
    this.onTap,
  });

  final int col;
  T value;
  final List items;
  final Function onChanged;
  ///  EXAMPLE:
  ///  (T value) {
  ///    return DropdownMenuItem<T>(
  ///      value: value,
  ///      child: Text(value.name),
  ///    );
  ///  }
  final Function dropdownMenuShow;

  final Function onTap;

  @override
  _JudoComboBoxState<T> createState() => _JudoComboBoxState<T>();
}

class _JudoComboBoxState<T> extends State<JudoComboBox<T>> {

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [JudoContainer(
        col: widget.col,
        padding: EdgeInsets.symmetric(horizontal: 10),
        child: Observer(
          builder: (_) => DropdownButton<T>(
            onTap: widget.onTap,
            hint: Text('Select'),
            value: widget.value,
            icon: Icon(Icons.arrow_drop_down),
            elevation: 16,
            style: TextStyle(color: kPrimaryColor),
            underline: Container(
              height: 2,
              color: kSecondaryColor,
            ),
            onChanged: widget.onChanged,
            items: widget.items
                .map<
                DropdownMenuItem<T>>(widget.dropdownMenuShow).toList(),
          ),
        ),
      ),]
    );
  }



}