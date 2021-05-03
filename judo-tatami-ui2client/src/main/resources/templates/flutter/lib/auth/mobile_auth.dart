import 'package:openapi_dart_common/openapi.dart';

import '../oauth.dart';
import './auth.dart';

class MobileAuth implements Auth {
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
    return null;
  }

  Future<void> init(String url) {

  }

  logout() {

  }

  Future<void> login() {

  }

  DioClientDelegate getDioDelegate() {
    return null;
  }

  AuthInfoStore getAuthInfo() {
    return null;
  }
}

Auth getAuth() => MobileAuth();
