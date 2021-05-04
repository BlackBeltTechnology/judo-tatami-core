import 'package:openapi_dart_common/openapi.dart';

import '../oauth.dart';
import './auth.dart';

class MobileAuth implements Auth {
  bool isAuthenticationRequired() {
    return false;
  }

  bool isAuthorized() {
    return false;
  }

  bool isLoggedIn() {
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
}

Auth getAuth() => MobileAuth();
