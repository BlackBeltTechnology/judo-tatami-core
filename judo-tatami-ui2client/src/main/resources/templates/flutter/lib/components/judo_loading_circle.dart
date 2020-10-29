part of judo.components;

class JudoLoadingProgress extends StatelessWidget {

  @override
  Widget build(BuildContext context) {
    return JudoContainer(
      col: 1,
      row: 1,
      child: Center(
        child: SizedBox(
            width: 32,
            height: 32,
            child: CircularProgressIndicator(
              backgroundColor: kPrimaryColor,
              strokeWidth: 8,
            )
        ),
      ),
    );
  }
}
