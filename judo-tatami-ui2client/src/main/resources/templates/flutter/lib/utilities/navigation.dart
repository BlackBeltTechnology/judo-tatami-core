part of judo.utility;

class NavigationItem {
  const NavigationItem({@required
  this.route,
    this.leading,
    this.title,
  });

  final String route;
  final Widget leading;
  final Widget title;
}
