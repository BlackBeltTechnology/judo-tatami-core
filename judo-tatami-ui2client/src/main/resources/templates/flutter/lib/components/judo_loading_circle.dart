part of judo.components;

class JudoLoadingProgress extends StatelessWidget implements IJudoComponent {
  @override
  Widget build(BuildContext context) {
    return Row(
      mainAxisSize: MainAxisSize.min,
      children: [
        JudoContainer(
          col: 1,
          padding: EdgeInsets.all(8.0),
          child: Center(
              child: SizedBox(
                width: 32,
                height: 32,
                child: Center(
                  child: CircularProgressIndicator(
                    backgroundColor: kPrimaryColor,
                    strokeWidth: 8,
                  ),
                ))
        )),
      ],
    );
  }

  @override
  int getColSize() {
    return 1;
  }
}
