part of openapi.api;

class DefaultApi {
  final DefaultApiDelegate apiDelegate;
  DefaultApi(ApiClient apiClient)
      : assert(apiClient != null),
        apiDelegate = DefaultApiDelegate(apiClient);

  ///
  ///
  ///
  Future<NorthwindServicesCategoryInfo> northwindExternalAPCreateAllCategories(
      NorthwindServicesCategoryInfoExtended input,
      {Options options}) async {
    final response = await apiDelegate.northwindExternalAPCreateAllCategories(
      input,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindExternalAPCreateAllCategories_decode(response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future<NorthwindServicesProductInfo> northwindExternalAPCreateAllProducts(
      NorthwindServicesProductInfoExtended input,
      {Options options}) async {
    final response = await apiDelegate.northwindExternalAPCreateAllProducts(
      input,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindExternalAPCreateAllProducts_decode(response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future northwindExternalAPDeleteAllCategories(NorthwindIdentifier input,
      {Options options}) async {
    final response = await apiDelegate.northwindExternalAPDeleteAllCategories(
      input,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindExternalAPDeleteAllCategories_decode(response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future northwindExternalAPDeleteAllProducts(NorthwindIdentifier input,
      {Options options}) async {
    final response = await apiDelegate.northwindExternalAPDeleteAllProducts(
      input,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindExternalAPDeleteAllProducts_decode(response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future<List<NorthwindServicesCategoryInfo>>
      northwindExternalAPGetAllCategories({Options options}) async {
    final response = await apiDelegate.northwindExternalAPGetAllCategories(
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindExternalAPGetAllCategories_decode(response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future<List<NorthwindServicesProductInfo>> northwindExternalAPGetAllProducts(
      {Options options}) async {
    final response = await apiDelegate.northwindExternalAPGetAllProducts(
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindExternalAPGetAllProducts_decode(response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future northwindExternalAPSetCategoryOfAllProducts(
      NorthwindExternalAPSetCategoryOfAllProductsInput input,
      {Options options}) async {
    final response =
        await apiDelegate.northwindExternalAPSetCategoryOfAllProducts(
      input,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindExternalAPSetCategoryOfAllProducts_decode(response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future<NorthwindServicesCategoryInfo> northwindExternalAPUpdateAllCategories(
      NorthwindServicesCategoryInfo input,
      {Options options}) async {
    final response = await apiDelegate.northwindExternalAPUpdateAllCategories(
      input,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindExternalAPUpdateAllCategories_decode(response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future<NorthwindServicesProductInfo> northwindExternalAPUpdateAllProducts(
      NorthwindServicesProductInfo input,
      {Options options}) async {
    final response = await apiDelegate.northwindExternalAPUpdateAllProducts(
      input,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindExternalAPUpdateAllProducts_decode(response);
    }
  }

  ///
  ///
  ///
}

class DefaultApiDelegate {
  final ApiClient apiClient;

  DefaultApiDelegate(this.apiClient) : assert(apiClient != null);

  Future<ApiResponse> northwindExternalAPCreateAllCategories(
      NorthwindServicesCategoryInfoExtended input,
      {Options options}) async {
    Object postBody = input;

    // verify required params are set
    if (input == null) {
      throw ApiException(400, 'Missing required param: input');
    }

    // create path and map variables
    final __path = '/ExternalAP/allCategories/create';

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

  Future<NorthwindServicesCategoryInfo>
      northwindExternalAPCreateAllCategories_decode(
          ApiResponse response) async {
    if (response.body != null) {
      return LocalApiClient.deserializeFromString(
              await decodeBodyBytes(response), 'NorthwindServicesCategoryInfo')
          as NorthwindServicesCategoryInfo;
    }

    return null;
  }

  Future<ApiResponse> northwindExternalAPCreateAllProducts(
      NorthwindServicesProductInfoExtended input,
      {Options options}) async {
    Object postBody = input;

    // verify required params are set
    if (input == null) {
      throw ApiException(400, 'Missing required param: input');
    }

    // create path and map variables
    final __path = '/ExternalAP/allProducts/create';

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

  Future<NorthwindServicesProductInfo>
      northwindExternalAPCreateAllProducts_decode(ApiResponse response) async {
    if (response.body != null) {
      return LocalApiClient.deserializeFromString(
              await decodeBodyBytes(response), 'NorthwindServicesProductInfo')
          as NorthwindServicesProductInfo;
    }

    return null;
  }

  Future<ApiResponse> northwindExternalAPDeleteAllCategories(
      NorthwindIdentifier input,
      {Options options}) async {
    Object postBody = input;

    // verify required params are set
    if (input == null) {
      throw ApiException(400, 'Missing required param: input');
    }

    // create path and map variables
    final __path = '/ExternalAP/allCategories/delete';

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

  Future northwindExternalAPDeleteAllCategories_decode(
      ApiResponse response) async {
    if (response.body != null) {}

    return;
  }

  Future<ApiResponse> northwindExternalAPDeleteAllProducts(
      NorthwindIdentifier input,
      {Options options}) async {
    Object postBody = input;

    // verify required params are set
    if (input == null) {
      throw ApiException(400, 'Missing required param: input');
    }

    // create path and map variables
    final __path = '/ExternalAP/allProducts/delete';

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

  Future northwindExternalAPDeleteAllProducts_decode(
      ApiResponse response) async {
    if (response.body != null) {}

    return;
  }

  Future<ApiResponse> northwindExternalAPGetAllCategories(
      {Options options}) async {
    Object postBody;

    // verify required params are set

    // create path and map variables
    final __path = '/ExternalAP/allCategories/get';

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

  Future<List<NorthwindServicesCategoryInfo>>
      northwindExternalAPGetAllCategories_decode(ApiResponse response) async {
    if (response.body != null) {
      return (LocalApiClient.deserializeFromString(
              await decodeBodyBytes(response),
              'List<NorthwindServicesCategoryInfo>') as List)
          .map((item) => item as NorthwindServicesCategoryInfo)
          .toList();
    }

    return null;
  }

  Future<ApiResponse> northwindExternalAPGetAllProducts(
      {Options options}) async {
    Object postBody;

    // verify required params are set

    // create path and map variables
    final __path = '/ExternalAP/allProducts/get';

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

  Future<List<NorthwindServicesProductInfo>>
      northwindExternalAPGetAllProducts_decode(ApiResponse response) async {
    if (response.body != null) {
      return (LocalApiClient.deserializeFromString(
              await decodeBodyBytes(response),
              'List<NorthwindServicesProductInfo>') as List)
          .map((item) => item as NorthwindServicesProductInfo)
          .toList();
    }

    return null;
  }

  Future<ApiResponse> northwindExternalAPSetCategoryOfAllProducts(
      NorthwindExternalAPSetCategoryOfAllProductsInput input,
      {Options options}) async {
    Object postBody = input;

    // verify required params are set
    if (input == null) {
      throw ApiException(400, 'Missing required param: input');
    }

    // create path and map variables
    final __path = '/ExternalAP/allProducts/category/set';

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

  Future northwindExternalAPSetCategoryOfAllProducts_decode(
      ApiResponse response) async {
    if (response.body != null) {}

    return;
  }

  Future<ApiResponse> northwindExternalAPUpdateAllCategories(
      NorthwindServicesCategoryInfo input,
      {Options options}) async {
    Object postBody = input;

    // verify required params are set
    if (input == null) {
      throw ApiException(400, 'Missing required param: input');
    }

    // create path and map variables
    final __path = '/ExternalAP/allCategories/update';

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

  Future<NorthwindServicesCategoryInfo>
      northwindExternalAPUpdateAllCategories_decode(
          ApiResponse response) async {
    if (response.body != null) {
      return LocalApiClient.deserializeFromString(
              await decodeBodyBytes(response), 'NorthwindServicesCategoryInfo')
          as NorthwindServicesCategoryInfo;
    }

    return null;
  }

  Future<ApiResponse> northwindExternalAPUpdateAllProducts(
      NorthwindServicesProductInfo input,
      {Options options}) async {
    Object postBody = input;

    // verify required params are set
    if (input == null) {
      throw ApiException(400, 'Missing required param: input');
    }

    // create path and map variables
    final __path = '/ExternalAP/allProducts/update';

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

  Future<NorthwindServicesProductInfo>
      northwindExternalAPUpdateAllProducts_decode(ApiResponse response) async {
    if (response.body != null) {
      return LocalApiClient.deserializeFromString(
              await decodeBodyBytes(response), 'NorthwindServicesProductInfo')
          as NorthwindServicesProductInfo;
    }

    return null;
  }
}
