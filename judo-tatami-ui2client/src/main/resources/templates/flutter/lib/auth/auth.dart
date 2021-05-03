import 'package:openapi_dart_common/openapi.dart';

import '../oauth.dart';
import 'auth_stub.dart'
  // ignore: uri_does_not_exist
  if (dart.library.io) 'mobile_auth.dart'
  // ignore: uri_does_not_exist
  if (dart.library.html) 'web_auth.dart';

abstract class Auth {
  bool isAuthenticationRequired() {
    return 1 == 1;
  }

  bool isLoggedIn() {
    return 1 == 1;
  }

  bool isAccessTokenSet() {
    return 1 == 1;
  }

  Future<String> getAccessToken() {
    // processLogin()...
    return null;
  }

  Future<void> init(String url) {

  }

  logout() {

  }

  Future<void> login() {
    // loginAuthorizeAndExchangeCode...
  }

  DioClientDelegate getDioDelegate() {
    return null;
  }

  AuthInfoStore getAuthInfo() {
    return null;
  }

  factory Auth() => getAuth();
}
