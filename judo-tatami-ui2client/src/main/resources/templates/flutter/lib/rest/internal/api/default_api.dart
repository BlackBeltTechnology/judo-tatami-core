part of openapi.api;

class DefaultApi {
  final DefaultApiDelegate apiDelegate;
  DefaultApi(ApiClient apiClient)
      : assert(apiClient != null),
        apiDelegate = DefaultApiDelegate(apiClient);

  ///
  ///
  ///
  Future<NorthwindServicesInternationalOrderInfo>
      northwindInternalAPCreateAllInternationalOrders(
          NorthwindServicesInternationalOrderInfoExtended input,
          {Options options}) async {
    final response =
        await apiDelegate.northwindInternalAPCreateAllInternationalOrders(
      input,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindInternalAPCreateAllInternationalOrders_decode(response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future<NorthwindServicesShipperInfo> northwindInternalAPCreateAllShippers(
      NorthwindServicesShipperInfoExtended input,
      {Options options}) async {
    final response = await apiDelegate.northwindInternalAPCreateAllShippers(
      input,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindInternalAPCreateAllShippers_decode(response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future northwindInternalAPDeleteAllInternationalOrders(
      NorthwindIdentifier input,
      {Options options}) async {
    final response =
        await apiDelegate.northwindInternalAPDeleteAllInternationalOrders(
      input,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindInternalAPDeleteAllInternationalOrders_decode(response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future northwindInternalAPDeleteAllShippers(NorthwindIdentifier input,
      {Options options}) async {
    final response = await apiDelegate.northwindInternalAPDeleteAllShippers(
      input,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindInternalAPDeleteAllShippers_decode(response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future<List<NorthwindServicesInternationalOrderInfo>>
      northwindInternalAPGetAllInternationalOrders({Options options}) async {
    final response =
        await apiDelegate.northwindInternalAPGetAllInternationalOrders(
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindInternalAPGetAllInternationalOrders_decode(response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future<List<NorthwindServicesShipperInfo>> northwindInternalAPGetAllShippers(
      {Options options}) async {
    final response = await apiDelegate.northwindInternalAPGetAllShippers(
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindInternalAPGetAllShippers_decode(response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future northwindInternalAPRemoveItemsFromAllInternationalOrders(
      NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInput input,
      {Options options}) async {
    final response = await apiDelegate
        .northwindInternalAPRemoveItemsFromAllInternationalOrders(
      input,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindInternalAPRemoveItemsFromAllInternationalOrders_decode(
              response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future northwindInternalAPSetShipperOfAllInternationalOrders(
      NorthwindInternalAPSetShipperOfAllInternationalOrdersInput input,
      {Options options}) async {
    final response =
        await apiDelegate.northwindInternalAPSetShipperOfAllInternationalOrders(
      input,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindInternalAPSetShipperOfAllInternationalOrders_decode(
              response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future northwindInternalAPUnsetShipperOfAllInternationalOrders(
      NorthwindIdentifier input,
      {Options options}) async {
    final response = await apiDelegate
        .northwindInternalAPUnsetShipperOfAllInternationalOrders(
      input,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindInternalAPUnsetShipperOfAllInternationalOrders_decode(
              response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future<NorthwindServicesInternationalOrderInfo>
      northwindInternalAPUpdateAllInternationalOrders(
          NorthwindServicesInternationalOrderInfo input,
          {Options options}) async {
    final response =
        await apiDelegate.northwindInternalAPUpdateAllInternationalOrders(
      input,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindInternalAPUpdateAllInternationalOrders_decode(response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future<NorthwindServicesShipperInfo> northwindInternalAPUpdateAllShippers(
      NorthwindServicesShipperInfo input,
      {Options options}) async {
    final response = await apiDelegate.northwindInternalAPUpdateAllShippers(
      input,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindInternalAPUpdateAllShippers_decode(response);
    }
  }

  ///
  ///
  ///
}

class DefaultApiDelegate {
  final ApiClient apiClient;

  DefaultApiDelegate(this.apiClient) : assert(apiClient != null);

  Future<ApiResponse> northwindInternalAPCreateAllInternationalOrders(
      NorthwindServicesInternationalOrderInfoExtended input,
      {Options options}) async {
    Object postBody = input;

    // verify required params are set
    if (input == null) {
      throw ApiException(400, 'Missing required param: input');
    }

    // create path and map variables
    final __path = '/InternalAP/allInternationalOrders/create';

    // query params
    final queryParams = <QueryParam>[];
    final headerParams = <String, String>{}
      ..addAll(options?.headers?.cast<String, String>() ?? {});
    if (headerParams['Accept'] == null) {
      // we only want to accept this format as we can parse it
      headerParams['Accept'] = 'application/json';
    }

    final authNames = <String>[];
    final opt = options ?? Options();

    final contentTypes = [];

    if (contentTypes.isNotEmpty && headerParams['Content-Type'] == null) {
      headerParams['Content-Type'] = contentTypes[0];
    }
    if (postBody != null) {
      postBody = LocalApiClient.serialize(postBody);
    }

    opt.headers = headerParams;
    opt.method = 'POST';

    return await apiClient.invokeAPI(
        __path, queryParams, postBody, authNames, opt);
  }

  Future<NorthwindServicesInternationalOrderInfo>
      northwindInternalAPCreateAllInternationalOrders_decode(
          ApiResponse response) async {
    if (response.body != null) {
      return LocalApiClient.deserializeFromString(
              await decodeBodyBytes(response),
              'NorthwindServicesInternationalOrderInfo')
          as NorthwindServicesInternationalOrderInfo;
    }

    return null;
  }

  Future<ApiResponse> northwindInternalAPCreateAllShippers(
      NorthwindServicesShipperInfoExtended input,
      {Options options}) async {
    Object postBody = input;

    // verify required params are set
    if (input == null) {
      throw ApiException(400, 'Missing required param: input');
    }

    // create path and map variables
    final __path = '/InternalAP/allShippers/create';

    // query params
    final queryParams = <QueryParam>[];
    final headerParams = <String, String>{}
      ..addAll(options?.headers?.cast<String, String>() ?? {});
    if (headerParams['Accept'] == null) {
      // we only want to accept this format as we can parse it
      headerParams['Accept'] = 'application/json';
    }

    final authNames = <String>[];
    final opt = options ?? Options();

    final contentTypes = [];

    if (contentTypes.isNotEmpty && headerParams['Content-Type'] == null) {
      headerParams['Content-Type'] = contentTypes[0];
    }
    if (postBody != null) {
      postBody = LocalApiClient.serialize(postBody);
    }

    opt.headers = headerParams;
    opt.method = 'POST';

    return await apiClient.invokeAPI(
        __path, queryParams, postBody, authNames, opt);
  }

  Future<NorthwindServicesShipperInfo>
      northwindInternalAPCreateAllShippers_decode(ApiResponse response) async {
    if (response.body != null) {
      return LocalApiClient.deserializeFromString(
              await decodeBodyBytes(response), 'NorthwindServicesShipperInfo')
          as NorthwindServicesShipperInfo;
    }

    return null;
  }

  Future<ApiResponse> northwindInternalAPDeleteAllInternationalOrders(
      NorthwindIdentifier input,
      {Options options}) async {
    Object postBody = input;

    // verify required params are set
    if (input == null) {
      throw ApiException(400, 'Missing required param: input');
    }

    // create path and map variables
    final __path = '/InternalAP/allInternationalOrders/delete';

    // query params
    final queryParams = <QueryParam>[];
    final headerParams = <String, String>{}
      ..addAll(options?.headers?.cast<String, String>() ?? {});
    if (headerParams['Accept'] == null) {
      // we only want to accept this format as we can parse it
      headerParams['Accept'] = 'application/json';
    }

    final authNames = <String>[];
    final opt = options ?? Options();

    final contentTypes = [];

    if (contentTypes.isNotEmpty && headerParams['Content-Type'] == null) {
      headerParams['Content-Type'] = contentTypes[0];
    }
    if (postBody != null) {
      postBody = LocalApiClient.serialize(postBody);
    }

    opt.headers = headerParams;
    opt.method = 'POST';

    return await apiClient.invokeAPI(
        __path, queryParams, postBody, authNames, opt);
  }

  Future northwindInternalAPDeleteAllInternationalOrders_decode(
      ApiResponse response) async {
    if (response.body != null) {}

    return;
  }

  Future<ApiResponse> northwindInternalAPDeleteAllShippers(
      NorthwindIdentifier input,
      {Options options}) async {
    Object postBody = input;

    // verify required params are set
    if (input == null) {
      throw ApiException(400, 'Missing required param: input');
    }

    // create path and map variables
    final __path = '/InternalAP/allShippers/delete';

    // query params
    final queryParams = <QueryParam>[];
    final headerParams = <String, String>{}
      ..addAll(options?.headers?.cast<String, String>() ?? {});
    if (headerParams['Accept'] == null) {
      // we only want to accept this format as we can parse it
      headerParams['Accept'] = 'application/json';
    }

    final authNames = <String>[];
    final opt = options ?? Options();

    final contentTypes = [];

    if (contentTypes.isNotEmpty && headerParams['Content-Type'] == null) {
      headerParams['Content-Type'] = contentTypes[0];
    }
    if (postBody != null) {
      postBody = LocalApiClient.serialize(postBody);
    }

    opt.headers = headerParams;
    opt.method = 'POST';

    return await apiClient.invokeAPI(
        __path, queryParams, postBody, authNames, opt);
  }

  Future northwindInternalAPDeleteAllShippers_decode(
      ApiResponse response) async {
    if (response.body != null) {}

    return;
  }

  Future<ApiResponse> northwindInternalAPGetAllInternationalOrders(
      {Options options}) async {
    Object postBody;

    // verify required params are set

    // create path and map variables
    final __path = '/InternalAP/allInternationalOrders/get';

    // query params
    final queryParams = <QueryParam>[];
    final headerParams = <String, String>{}
      ..addAll(options?.headers?.cast<String, String>() ?? {});
    if (headerParams['Accept'] == null) {
      // we only want to accept this format as we can parse it
      headerParams['Accept'] = 'application/json';
    }

    final authNames = <String>[];
    final opt = options ?? Options();

    final contentTypes = [];

    if (contentTypes.isNotEmpty && headerParams['Content-Type'] == null) {
      headerParams['Content-Type'] = contentTypes[0];
    }
    if (postBody != null) {
      postBody = LocalApiClient.serialize(postBody);
    }

    opt.headers = headerParams;
    opt.method = 'GET';

    return await apiClient.invokeAPI(
        __path, queryParams, postBody, authNames, opt);
  }

  Future<List<NorthwindServicesInternationalOrderInfo>>
      northwindInternalAPGetAllInternationalOrders_decode(
          ApiResponse response) async {
    if (response.body != null) {
      return (LocalApiClient.deserializeFromString(
              await decodeBodyBytes(response),
              'List<NorthwindServicesInternationalOrderInfo>') as List)
          .map((item) => item as NorthwindServicesInternationalOrderInfo)
          .toList();
    }

    return null;
  }

  Future<ApiResponse> northwindInternalAPGetAllShippers(
      {Options options}) async {
    Object postBody;

    // verify required params are set

    // create path and map variables
    final __path = '/InternalAP/allShippers/get';

    // query params
    final queryParams = <QueryParam>[];
    final headerParams = <String, String>{}
      ..addAll(options?.headers?.cast<String, String>() ?? {});
    if (headerParams['Accept'] == null) {
      // we only want to accept this format as we can parse it
      headerParams['Accept'] = 'application/json';
    }

    final authNames = <String>[];
    final opt = options ?? Options();

    final contentTypes = [];

    if (contentTypes.isNotEmpty && headerParams['Content-Type'] == null) {
      headerParams['Content-Type'] = contentTypes[0];
    }
    if (postBody != null) {
      postBody = LocalApiClient.serialize(postBody);
    }

    opt.headers = headerParams;
    opt.method = 'GET';

    return await apiClient.invokeAPI(
        __path, queryParams, postBody, authNames, opt);
  }

  Future<List<NorthwindServicesShipperInfo>>
      northwindInternalAPGetAllShippers_decode(ApiResponse response) async {
    if (response.body != null) {
      return (LocalApiClient.deserializeFromString(
              await decodeBodyBytes(response),
              'List<NorthwindServicesShipperInfo>') as List)
          .map((item) => item as NorthwindServicesShipperInfo)
          .toList();
    }

    return null;
  }

  Future<ApiResponse> northwindInternalAPRemoveItemsFromAllInternationalOrders(
      NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInput input,
      {Options options}) async {
    Object postBody = input;

    // verify required params are set
    if (input == null) {
      throw ApiException(400, 'Missing required param: input');
    }

    // create path and map variables
    final __path = '/InternalAP/allInternationalOrders/items/remove';

    // query params
    final queryParams = <QueryParam>[];
    final headerParams = <String, String>{}
      ..addAll(options?.headers?.cast<String, String>() ?? {});
    if (headerParams['Accept'] == null) {
      // we only want to accept this format as we can parse it
      headerParams['Accept'] = 'application/json';
    }

    final authNames = <String>[];
    final opt = options ?? Options();

    final contentTypes = [];

    if (contentTypes.isNotEmpty && headerParams['Content-Type'] == null) {
      headerParams['Content-Type'] = contentTypes[0];
    }
    if (postBody != null) {
      postBody = LocalApiClient.serialize(postBody);
    }

    opt.headers = headerParams;
    opt.method = 'POST';

    return await apiClient.invokeAPI(
        __path, queryParams, postBody, authNames, opt);
  }

  Future northwindInternalAPRemoveItemsFromAllInternationalOrders_decode(
      ApiResponse response) async {
    if (response.body != null) {}

    return;
  }

  Future<ApiResponse> northwindInternalAPSetShipperOfAllInternationalOrders(
      NorthwindInternalAPSetShipperOfAllInternationalOrdersInput input,
      {Options options}) async {
    Object postBody = input;

    // verify required params are set
    if (input == null) {
      throw ApiException(400, 'Missing required param: input');
    }

    // create path and map variables
    final __path = '/InternalAP/allInternationalOrders/shipper/set';

    // query params
    final queryParams = <QueryParam>[];
    final headerParams = <String, String>{}
      ..addAll(options?.headers?.cast<String, String>() ?? {});
    if (headerParams['Accept'] == null) {
      // we only want to accept this format as we can parse it
      headerParams['Accept'] = 'application/json';
    }

    final authNames = <String>[];
    final opt = options ?? Options();

    final contentTypes = [];

    if (contentTypes.isNotEmpty && headerParams['Content-Type'] == null) {
      headerParams['Content-Type'] = contentTypes[0];
    }
    if (postBody != null) {
      postBody = LocalApiClient.serialize(postBody);
    }

    opt.headers = headerParams;
    opt.method = 'POST';

    return await apiClient.invokeAPI(
        __path, queryParams, postBody, authNames, opt);
  }

  Future northwindInternalAPSetShipperOfAllInternationalOrders_decode(
      ApiResponse response) async {
    if (response.body != null) {}

    return;
  }

  Future<ApiResponse> northwindInternalAPUnsetShipperOfAllInternationalOrders(
      NorthwindIdentifier input,
      {Options options}) async {
    Object postBody = input;

    // verify required params are set
    if (input == null) {
      throw ApiException(400, 'Missing required param: input');
    }

    // create path and map variables
    final __path = '/InternalAP/allInternationalOrders/shipper/unset';

    // query params
    final queryParams = <QueryParam>[];
    final headerParams = <String, String>{}
      ..addAll(options?.headers?.cast<String, String>() ?? {});
    if (headerParams['Accept'] == null) {
      // we only want to accept this format as we can parse it
      headerParams['Accept'] = 'application/json';
    }

    final authNames = <String>[];
    final opt = options ?? Options();

    final contentTypes = [];

    if (contentTypes.isNotEmpty && headerParams['Content-Type'] == null) {
      headerParams['Content-Type'] = contentTypes[0];
    }
    if (postBody != null) {
      postBody = LocalApiClient.serialize(postBody);
    }

    opt.headers = headerParams;
    opt.method = 'POST';

    return await apiClient.invokeAPI(
        __path, queryParams, postBody, authNames, opt);
  }

  Future northwindInternalAPUnsetShipperOfAllInternationalOrders_decode(
      ApiResponse response) async {
    if (response.body != null) {}

    return;
  }

  Future<ApiResponse> northwindInternalAPUpdateAllInternationalOrders(
      NorthwindServicesInternationalOrderInfo input,
      {Options options}) async {
    Object postBody = input;

    // verify required params are set
    if (input == null) {
      throw ApiException(400, 'Missing required param: input');
    }

    // create path and map variables
    final __path = '/InternalAP/allInternationalOrders/update';

    // query params
    final queryParams = <QueryParam>[];
    final headerParams = <String, String>{}
      ..addAll(options?.headers?.cast<String, String>() ?? {});
    if (headerParams['Accept'] == null) {
      // we only want to accept this format as we can parse it
      headerParams['Accept'] = 'application/json';
    }

    final authNames = <String>[];
    final opt = options ?? Options();

    final contentTypes = [];

    if (contentTypes.isNotEmpty && headerParams['Content-Type'] == null) {
      headerParams['Content-Type'] = contentTypes[0];
    }
    if (postBody != null) {
      postBody = LocalApiClient.serialize(postBody);
    }

    opt.headers = headerParams;
    opt.method = 'POST';

    return await apiClient.invokeAPI(
        __path, queryParams, postBody, authNames, opt);
  }

  Future<NorthwindServicesInternationalOrderInfo>
      northwindInternalAPUpdateAllInternationalOrders_decode(
          ApiResponse response) async {
    if (response.body != null) {
      return LocalApiClient.deserializeFromString(
              await decodeBodyBytes(response),
              'NorthwindServicesInternationalOrderInfo')
          as NorthwindServicesInternationalOrderInfo;
    }

    return null;
  }

  Future<ApiResponse> northwindInternalAPUpdateAllShippers(
      NorthwindServicesShipperInfo input,
      {Options options}) async {
    Object postBody = input;

    // verify required params are set
    if (input == null) {
      throw ApiException(400, 'Missing required param: input');
    }

    // create path and map variables
    final __path = '/InternalAP/allShippers/update';

    // query params
    final queryParams = <QueryParam>[];
    final headerParams = <String, String>{}
      ..addAll(options?.headers?.cast<String, String>() ?? {});
    if (headerParams['Accept'] == null) {
      // we only want to accept this format as we can parse it
      headerParams['Accept'] = 'application/json';
    }

    final authNames = <String>[];
    final opt = options ?? Options();

    final contentTypes = [];

    if (contentTypes.isNotEmpty && headerParams['Content-Type'] == null) {
      headerParams['Content-Type'] = contentTypes[0];
    }
    if (postBody != null) {
      postBody = LocalApiClient.serialize(postBody);
    }

    opt.headers = headerParams;
    opt.method = 'POST';

    return await apiClient.invokeAPI(
        __path, queryParams, postBody, authNames, opt);
  }

  Future<NorthwindServicesShipperInfo>
      northwindInternalAPUpdateAllShippers_decode(ApiResponse response) async {
    if (response.body != null) {
      return LocalApiClient.deserializeFromString(
              await decodeBodyBytes(response), 'NorthwindServicesShipperInfo')
          as NorthwindServicesShipperInfo;
    }

    return null;
  }
}
