import 'dart:html' as html;

import 'package:dio/dio.dart';
import 'package:flutter_appauth_web/flutter_appauth_web.dart';
import 'package:openapi_dart_common/openapi.dart';
import 'package:{{modelPackage application.name}}/{{path application.name}}/error/error_handler.dart';
import 'package:{{modelPackage application.name}}/{{path application.name}}/repository/package.dart';
import 'package:{{modelPackage application.name}}/{{path application.name}}/rest/lib/api.dart';
import 'package:{{modelPackage application.name}}/{{path application.name}}/utilities/package.dart';
import 'package:flutter_appauth_platform_interface/flutter_appauth_platform_interface.dart';

import '../injector/injector.dart';
import '../oauth.dart';
import './auth.dart';


class WebAuth implements Auth {
  static const AUTH_CODE_VERIFIER_KEY = "auth_code_verifier";
  static const AUTH_CODE_KEY = "auth_code";
  static const AUTH_ACCESS_TOKEN_KEY = "auth_access_token";
  static const AUTH_ACCESS_TOKEN_EXPIRE = "auth_access_token_expire";
  static const AUTH_REFRESH_TOKEN_KEY = "auth_refresh_token";
  static const AUTH_DESTINATION_URL = "auth_destination_url";
  static const GRANT_AUTHORIZATION = "authorization_code";
  static const GRANT_REFRESH = "refresh_token";
  static const APP_NAME_KEY = "judo_ng_app_name";
  static const APP_NAME = "{{modelName application.name}}{{packageName application.name}}";

  {{modelName application.name}}{{packageName application.name}}MetadataSecurityFor{{className application.name}} _securitySettings = {{modelName application.name}}{{packageName application.name}}MetadataSecurityFor{{className application.name}}();
  var _authInfo = AuthInfoStore();

  bool isAuthenticationRequired() {
    return _securitySettings.clientId != null;
  }

  bool isLoggedIn() {
    return _authInfo.loggedIn;
  }

  bool isAuthorized() {
    return isLoggedIn() && html.window.sessionStorage[APP_NAME_KEY] == APP_NAME;
  }

  bool isAccessTokenSet() {
    return html.window.sessionStorage[AUTH_ACCESS_TOKEN_KEY] != null && html.window.sessionStorage[AUTH_ACCESS_TOKEN_KEY].isNotEmpty;
  }

  Future<String> getAccessToken() async {
    if (!isAccessTokenSet()) {
      var authResponse = AppAuthWebPlugin.processLoginResult(
        html.window.location.href,
        html.window.sessionStorage[AUTH_CODE_VERIFIER_KEY],
      );

      if (authResponse != null) {
        html.window.sessionStorage[AUTH_CODE_KEY] = authResponse.authorizationCode;
        return await _requestToken(GRANT_AUTHORIZATION);
      }
      return null;
    }
    return html.window.sessionStorage[AUTH_ACCESS_TOKEN_KEY];
  }

  Future<void> init(String url) async {
    var _apiClient = locator<ApiClient>();

    try {
      await _loadClientMetaData();
      Info.serverDown = false;
    } on ApiException catch (error) {
      if (error.code == 500 && error.message.contains('Connection')) {
        Info.serverDown = true;
      }
    }

    var appNamePresent = html.window.sessionStorage[APP_NAME_KEY];
    if (appNamePresent != null && appNamePresent != APP_NAME) {
      logout();
      return;
    }

    if (isAuthenticationRequired()) {
      locator.unregister(instance: _apiClient);

      // Add the token refresh interceptor for the rest of the api calls
      _apiClient =
          ApiClient(basePath: url, apiClientDelegate: getDioDelegate());
      locator.registerSingleton<ApiClient>(_apiClient);

      if (!isNavigateToSettingsPage) {
        await login();
      }

      var token = await getAccessToken();
      _updateAuthInfo(token);
      await _addTokenToApiClient(token);
    }
    await _updatePrincipalAllowed();
  }

  logout() {
    getAuthInfo().setAuthenticated(false);
    html.window.sessionStorage.clear();
    locator<ApiClient>().setAuthentication(_securitySettings.name, null);
    html.window.location.assign(_securitySettings.logoutEndpoint + "?redirect_uri=${Uri.encodeQueryComponent(_calculateRedirectUri())}");
    _securitySettings = {{modelName application.name}}{{packageName application.name}}MetadataSecurityFor{{className application.name}}();
  }

  Future<void> login() async {
    if (html.window.sessionStorage[AUTH_CODE_VERIFIER_KEY] == null || html.window.sessionStorage[AUTH_CODE_VERIFIER_KEY].isEmpty) {
      var auth = AppAuthWebPlugin();
      var request = AuthorizationTokenRequest(
          _securitySettings.clientId,
          _calculateRedirectUri(),
          issuer: _securitySettings.issuer,
          scopes: [_securitySettings.defaultScopes]);
      await auth.authorizeAndExchangeCode(request);
    }
  }

  DioClientDelegate getDioDelegate() {
    var dioDelegate = DioClientDelegate();
    dioDelegate.client.interceptors.add(InterceptorsWrapper(onRequest: (options) async {
      var token = await _refreshToken();
      if (token != null) {
        options.headers.remove('Authorization');
        options.headers.putIfAbsent('Authorization', () => 'Bearer $token');
      }
    }));
    return dioDelegate;
  }

  AuthInfoStore getAuthInfo() {
    return _authInfo;
  }

  String _calculateRedirectUri() {
    var callbackUrl = html.window.location.href; //protocol  + "//" + html.window.location.host;
    var uri = Uri.parse(callbackUrl);
    var strippedUri = Uri(scheme: uri.scheme, userInfo: uri.userInfo, host: uri.host, port: uri.port, path: uri.path);
    return strippedUri.toString();
  }

  _loadClientMetaData() async {
    var apiClient = locator<ApiClient>();
    try {
      try {
        await DefaultApi(apiClient).{{modelNameVariable application.name}}{{packageName application.name}}{{className application.name}}Metadata();
      } on ArgumentError catch (error) {
        print("Could not get metadata: " + error.message);
        // Very nice way to determinate REALM
        var realm = error.message.toString().substring(26);
        apiClient.setAuthentication(realm, NoopAuth());
      }

      {{modelName application.name}}{{packageName application.name}}MetadataFor{{className application.name}} meta = await DefaultApi(apiClient).{{modelNameVariable application.name}}{{packageName application.name}}{{className application.name}}Metadata();
      if (meta.security.isNotEmpty) {
        _securitySettings = meta.security.first;
      }
      print("Security settings: $_securitySettings");

    } on ApiException catch (error) {
      print(error);
      throw error;
    }
  }

  Future<String> _requestToken(String grantType) async {
    // print("authResponse: ${authResponse.authorizationCode}");
    var tokenRequest = TokenRequest(
        _securitySettings.clientId,
        _calculateRedirectUri(),
        scopes: [_securitySettings.defaultScopes],
        grantType: grantType,
        authorizationCode: html.window.sessionStorage[AUTH_CODE_KEY],
        codeVerifier: html.window.sessionStorage[AUTH_CODE_VERIFIER_KEY],
        refreshToken: grantType == GRANT_REFRESH ? html.window.sessionStorage[AUTH_REFRESH_TOKEN_KEY]: null,
        issuer: _securitySettings.issuer);

    var token = await AppAuthWebPlugin.requestToken(tokenRequest).catchError((onError) {
      if (onError is ArgumentError && onError.message.toString().contains('token_failed')) {
        _authInfo.setAuthenticated(false);
        logout();
        login();
      } else {
        ErrorHandler.navigateToErrorPage(onError);
      }
    });
    if (token != null) {
      html.window.sessionStorage[AUTH_ACCESS_TOKEN_KEY] = token.accessToken;
      html.window.sessionStorage[AUTH_ACCESS_TOKEN_EXPIRE] = token.accessTokenExpirationDateTime.toString();
      html.window.sessionStorage[AUTH_REFRESH_TOKEN_KEY] = token.refreshToken;
      
      if (html.window.sessionStorage[APP_NAME_KEY] == null) {
        html.window.sessionStorage[APP_NAME_KEY] = APP_NAME;
      }

      _updateAuthInfo(token.accessToken);

      // print('Access token: ${token.accessToken}');
      // print('Refresh token: ${token.refreshToken}');
      // print(token.accessTokenExpirationDateTime);

      return token.accessToken;
    }
    return null;
  }

  Future<String> _refreshToken() async {
    var tokenExpiration = DateTime.parse(html.window.sessionStorage[AUTH_ACCESS_TOKEN_EXPIRE]);
    var expireIn = tokenExpiration.difference(DateTime.now());

    // print("Token expires in ${expireIn}");

    if (expireIn.inMinutes < 1) {
      var token = await _requestToken(GRANT_REFRESH);
      await _addTokenToApiClient(token);
      return token;
    }
    return null;
  }

  _updateAuthInfo(String token) {
    if (token != null) {
      _authInfo.setAuthenticated(true);
      var parsed = parseJwt(token);
      print("Access Token: " + parsed.toString());
      _authInfo.setUserName(parsed['preferred_username']);
      _authInfo.setEmail(parsed['email']);
    } else {
      _authInfo.setAuthenticated(false);
      _authInfo.setUserName(null);
      _authInfo.setEmail(null);
    }
  }

  _addTokenToApiClient(String token) {
    if (token != null) {
      locator<ApiClient>().setAuthentication(_securitySettings.name, OAuth(accessToken: token));
    }
  }

  Future<void> _updatePrincipalAllowed() async {
    {{#if application.principal}}
    {{fqClass application.name}}Repository _actorRepository = locator<{{fqClass application.name}}Repository>();

    try {
      var principal = await _actorRepository.getPrincipal();
      _authInfo.setIsPrincipalAllowed(principal.email.isNotEmpty);
    } catch (error) {
      _authInfo.setIsPrincipalAllowed(false);
    }
    {{else}}
    _authInfo.setIsPrincipalAllowed(true);
    {{/if}}
  }
}

Auth getAuth() => WebAuth();
