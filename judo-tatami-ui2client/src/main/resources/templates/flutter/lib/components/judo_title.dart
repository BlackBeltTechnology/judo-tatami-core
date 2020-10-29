part of judo.components;

class JudoTitle extends StatelessWidget {
  JudoTitle({
    @required this.col,
    @required this.text,
  });

  final String text;
  final int col;

  @override
  Widget build(BuildContext context) {
    return JudoContainer(
      padding: EdgeInsets.symmetric(horizontal: 10, vertical: 5),
      col: col,
      child: Column(
        crossAxisAlignment: CrossAxisAlignment.start,
        mainAxisSize: MainAxisSize.min,
        children: [
          Text(
            text,
            style: TextStyle(
                fontWeight: FontWeight.w900,
                color: kSecondaryColor,
                fontSize: 24),
          ),
          SizedBox(
            height: 10,
            child: Divider(
              color: Colors.grey,
              thickness: 1,
            ),
          ),
        ],
      ),
    );
  }
}
