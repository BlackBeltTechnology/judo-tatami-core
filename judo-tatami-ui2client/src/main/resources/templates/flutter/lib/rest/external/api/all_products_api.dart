part of openapi.api;

class AllProductsApi {
  final AllProductsApiDelegate apiDelegate;
  AllProductsApi(ApiClient apiClient)
      : assert(apiClient != null),
        apiDelegate = AllProductsApiDelegate(apiClient);

  ///
  ///
  ///
  Future<NorthwindServicesProductInfo>
      northwindServicesCategoryInfoCreateProducts(
          String identifier, NorthwindServicesProductInfoExtended input,
          {Options options}) async {
    final response =
        await apiDelegate.northwindServicesCategoryInfoCreateProducts(
      identifier,
      input,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindServicesCategoryInfoCreateProducts_decode(response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future northwindServicesCategoryInfoDeleteProducts(
      String identifier, NorthwindIdentifier input,
      {Options options}) async {
    final response =
        await apiDelegate.northwindServicesCategoryInfoDeleteProducts(
      identifier,
      input,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindServicesCategoryInfoDeleteProducts_decode(response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future<List<NorthwindServicesProductInfo>>
      northwindServicesCategoryInfoGetProducts(String identifier,
          {Options options}) async {
    final response = await apiDelegate.northwindServicesCategoryInfoGetProducts(
      identifier,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindServicesCategoryInfoGetProducts_decode(response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future northwindServicesCategoryInfoSetCategoryOfProducts(String identifier,
      NorthwindServicesCategoryInfoSetCategoryOfProductsInput input,
      {Options options}) async {
    final response =
        await apiDelegate.northwindServicesCategoryInfoSetCategoryOfProducts(
      identifier,
      input,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindServicesCategoryInfoSetCategoryOfProducts_decode(response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future<NorthwindServicesProductInfo>
      northwindServicesCategoryInfoUpdateProducts(
          String identifier, NorthwindServicesProductInfo input,
          {Options options}) async {
    final response =
        await apiDelegate.northwindServicesCategoryInfoUpdateProducts(
      identifier,
      input,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindServicesCategoryInfoUpdateProducts_decode(response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future<NorthwindServicesCategoryInfo> northwindServicesProductInfoGetCategory(
      String identifier,
      {Options options}) async {
    final response = await apiDelegate.northwindServicesProductInfoGetCategory(
      identifier,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindServicesProductInfoGetCategory_decode(response);
    }
  }

  ///
  ///
  ///
}

class AllProductsApiDelegate {
  final ApiClient apiClient;

  AllProductsApiDelegate(this.apiClient) : assert(apiClient != null);

  Future<ApiResponse> northwindServicesCategoryInfoCreateProducts(
      String identifier, NorthwindServicesProductInfoExtended input,
      {Options options}) async {
    Object postBody = input;

    // verify required params are set
    if (identifier == null) {
      throw ApiException(400, 'Missing required param: identifier');
    }
    if (input == null) {
      throw ApiException(400, 'Missing required param: input');
    }

    // create path and map variables
    final __path = '/services/CategoryInfo/products/create';

    // query params
    final queryParams = <QueryParam>[];
    final headerParams = <String, String>{}
      ..addAll(options?.headers?.cast<String, String>() ?? {});
    if (headerParams['Accept'] == null) {
      // we only want to accept this format as we can parse it
      headerParams['Accept'] = 'application/json';
    }

    headerParams['__identifier'] = identifier;

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
      northwindServicesCategoryInfoCreateProducts_decode(
          ApiResponse response) async {
    if (response.body != null) {
      return LocalApiClient.deserializeFromString(
              await decodeBodyBytes(response), 'NorthwindServicesProductInfo')
          as NorthwindServicesProductInfo;
    }

    return null;
  }

  Future<ApiResponse> northwindServicesCategoryInfoDeleteProducts(
      String identifier, NorthwindIdentifier input,
      {Options options}) async {
    Object postBody = input;

    // verify required params are set
    if (identifier == null) {
      throw ApiException(400, 'Missing required param: identifier');
    }
    if (input == null) {
      throw ApiException(400, 'Missing required param: input');
    }

    // create path and map variables
    final __path = '/services/CategoryInfo/products/delete';

    // query params
    final queryParams = <QueryParam>[];
    final headerParams = <String, String>{}
      ..addAll(options?.headers?.cast<String, String>() ?? {});
    if (headerParams['Accept'] == null) {
      // we only want to accept this format as we can parse it
      headerParams['Accept'] = 'application/json';
    }

    headerParams['__identifier'] = identifier;

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

  Future northwindServicesCategoryInfoDeleteProducts_decode(
      ApiResponse response) async {
    if (response.body != null) {}

    return;
  }

  Future<ApiResponse> northwindServicesCategoryInfoGetProducts(
      String identifier,
      {Options options}) async {
    Object postBody;

    // verify required params are set
    if (identifier == null) {
      throw ApiException(400, 'Missing required param: identifier');
    }

    // create path and map variables
    final __path = '/services/CategoryInfo/products/get';

    // query params
    final queryParams = <QueryParam>[];
    final headerParams = <String, String>{}
      ..addAll(options?.headers?.cast<String, String>() ?? {});
    if (headerParams['Accept'] == null) {
      // we only want to accept this format as we can parse it
      headerParams['Accept'] = 'application/json';
    }

    headerParams['__identifier'] = identifier;

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
      northwindServicesCategoryInfoGetProducts_decode(
          ApiResponse response) async {
    if (response.body != null) {
      return (LocalApiClient.deserializeFromString(
              await decodeBodyBytes(response),
              'List<NorthwindServicesProductInfo>') as List)
          .map((item) => item as NorthwindServicesProductInfo)
          .toList();
    }

    return null;
  }

  Future<ApiResponse> northwindServicesCategoryInfoSetCategoryOfProducts(
      String identifier,
      NorthwindServicesCategoryInfoSetCategoryOfProductsInput input,
      {Options options}) async {
    Object postBody = input;

    // verify required params are set
    if (identifier == null) {
      throw ApiException(400, 'Missing required param: identifier');
    }
    if (input == null) {
      throw ApiException(400, 'Missing required param: input');
    }

    // create path and map variables
    final __path = '/services/CategoryInfo/products/category/set';

    // query params
    final queryParams = <QueryParam>[];
    final headerParams = <String, String>{}
      ..addAll(options?.headers?.cast<String, String>() ?? {});
    if (headerParams['Accept'] == null) {
      // we only want to accept this format as we can parse it
      headerParams['Accept'] = 'application/json';
    }

    headerParams['__identifier'] = identifier;

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

  Future northwindServicesCategoryInfoSetCategoryOfProducts_decode(
      ApiResponse response) async {
    if (response.body != null) {}

    return;
  }

  Future<ApiResponse> northwindServicesCategoryInfoUpdateProducts(
      String identifier, NorthwindServicesProductInfo input,
      {Options options}) async {
    Object postBody = input;

    // verify required params are set
    if (identifier == null) {
      throw ApiException(400, 'Missing required param: identifier');
    }
    if (input == null) {
      throw ApiException(400, 'Missing required param: input');
    }

    // create path and map variables
    final __path = '/services/CategoryInfo/products/update';

    // query params
    final queryParams = <QueryParam>[];
    final headerParams = <String, String>{}
      ..addAll(options?.headers?.cast<String, String>() ?? {});
    if (headerParams['Accept'] == null) {
      // we only want to accept this format as we can parse it
      headerParams['Accept'] = 'application/json';
    }

    headerParams['__identifier'] = identifier;

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
      northwindServicesCategoryInfoUpdateProducts_decode(
          ApiResponse response) async {
    if (response.body != null) {
      return LocalApiClient.deserializeFromString(
              await decodeBodyBytes(response), 'NorthwindServicesProductInfo')
          as NorthwindServicesProductInfo;
    }

    return null;
  }

  Future<ApiResponse> northwindServicesProductInfoGetCategory(String identifier,
      {Options options}) async {
    Object postBody;

    // verify required params are set
    if (identifier == null) {
      throw ApiException(400, 'Missing required param: identifier');
    }

    // create path and map variables
    final __path = '/services/ProductInfo/category/get';

    // query params
    final queryParams = <QueryParam>[];
    final headerParams = <String, String>{}
      ..addAll(options?.headers?.cast<String, String>() ?? {});
    if (headerParams['Accept'] == null) {
      // we only want to accept this format as we can parse it
      headerParams['Accept'] = 'application/json';
    }

    headerParams['__identifier'] = identifier;

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

  Future<NorthwindServicesCategoryInfo>
      northwindServicesProductInfoGetCategory_decode(
          ApiResponse response) async {
    if (response.body != null) {
      return LocalApiClient.deserializeFromString(
              await decodeBodyBytes(response), 'NorthwindServicesCategoryInfo')
          as NorthwindServicesCategoryInfo;
    }

    return null;
  }
}
