part of judo.utility;

class AppRouteObserver extends RouteObserver<PageRoute> {
  factory AppRouteObserver() => _instance;

  AppRouteObserver._private();

  static final AppRouteObserver _instance = AppRouteObserver._private();
}
