part of judo.components;

class JudoLabel extends StatelessWidget {

  JudoLabel({this.text, this.trailingIcon, this.leadingIcon});

  final String text;
  final Icon trailingIcon;
  final Icon leadingIcon;

  @override
  Widget build(BuildContext context) {
    return Row(
      children: [
        leadingIcon ?? Text(''),
        Text(
          text,
          style: TextStyle(
            fontWeight: FontWeight.bold,
            fontSize: 25,
          ),
        ),
        trailingIcon ?? Text(''),
      ],
    );
  }

}
