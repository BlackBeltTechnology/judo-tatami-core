import 'package:openapi_dart_common/openapi.dart';

import '../oauth.dart';
import 'auth_stub.dart'
  // ignore: uri_does_not_exist
  if (dart.library.io) 'mobile_auth.dart'
  // ignore: uri_does_not_exist
  if (dart.library.html) 'web_auth.dart';

abstract class Auth {
  /**
   * If App is anonymous or not.
   */
  bool isAuthenticationRequired(){
    return false;
  }

  bool isLoggedIn() {
    return false;
  }

  /**
   * Only relevant in a web context where users can open multiple apps.
   */
  bool isAuthorized() {
    return false;
  }

  bool isAccessTokenSet() {
    return false;
  }

  Future<String> getAccessToken() async {
    return Future.delayed(
      Duration(seconds: 0),
          () => '',
    );
  }

  Future<void> init(String url) async {}

  logout() {}

  Future<void> login() async {}

  DioClientDelegate getDioDelegate() {
    return null;
  }

  AuthInfoStore getAuthInfo() {
    return null;
  }

  factory Auth() => getAuth();
}