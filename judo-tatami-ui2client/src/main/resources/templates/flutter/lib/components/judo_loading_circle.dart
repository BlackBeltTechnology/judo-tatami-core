part of judo.components;

class JudoLoadingProgress extends StatelessWidget implements IJudoComponent {
  @override
  Widget build(BuildContext context) {
    return Expanded(child: Center(
        child: Padding(
          padding: EdgeInsets.all(8.0),
          child: SizedBox(
              width: 32,
              height: 32,
              child: Center(
                child: CircularProgressIndicator(
                  backgroundColor: kPrimaryColor,
                  strokeWidth: 8,
                ),
              )),
        )
    ));
  }

  @override
  int getColSize() {
    return 1;
  }
}
