part of openapi.api;

class AllInternationalOrdersApi {
  final AllInternationalOrdersApiDelegate apiDelegate;
  AllInternationalOrdersApi(ApiClient apiClient)
      : assert(apiClient != null),
        apiDelegate = AllInternationalOrdersApiDelegate(apiClient);

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
  Future northwindServicesInternationalOrderInfoDeleteAllCancelled(
      {Options options}) async {
    final response = await apiDelegate
        .northwindServicesInternationalOrderInfoDeleteAllCancelled(
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindServicesInternationalOrderInfoDeleteAllCancelled_decode(
              response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future northwindServicesInternationalOrderInfoShipAll(
      {Options options}) async {
    final response =
        await apiDelegate.northwindServicesInternationalOrderInfoShipAll(
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindServicesInternationalOrderInfoShipAll_decode(response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future<NorthwindServicesOrderInfo> northwindServicesOrderInfoChangeShipment(
      String identifier, NorthwindServicesShipmentChange input,
      {Options options}) async {
    final response = await apiDelegate.northwindServicesOrderInfoChangeShipment(
      identifier,
      input,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindServicesOrderInfoChangeShipment_decode(response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future<NorthwindServicesOrderItem> northwindServicesOrderInfoCreateItems(
      String identifier, NorthwindServicesOrderItemExtended input,
      {Options options}) async {
    final response = await apiDelegate.northwindServicesOrderInfoCreateItems(
      identifier,
      input,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindServicesOrderInfoCreateItems_decode(response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future northwindServicesOrderInfoDeleteItems(
      String identifier, NorthwindIdentifier input,
      {Options options}) async {
    final response = await apiDelegate.northwindServicesOrderInfoDeleteItems(
      identifier,
      input,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindServicesOrderInfoDeleteItems_decode(response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future<List<NorthwindServicesCategoryInfo>>
      northwindServicesOrderInfoGetCategories(String identifier,
          {Options options}) async {
    final response = await apiDelegate.northwindServicesOrderInfoGetCategories(
      identifier,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindServicesOrderInfoGetCategories_decode(response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future<List<NorthwindServicesOrderItem>> northwindServicesOrderInfoGetItems(
      String identifier,
      {Options options}) async {
    final response = await apiDelegate.northwindServicesOrderInfoGetItems(
      identifier,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindServicesOrderInfoGetItems_decode(response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future<NorthwindServicesShipperInfo> northwindServicesOrderInfoGetShipper(
      String identifier,
      {Options options}) async {
    final response = await apiDelegate.northwindServicesOrderInfoGetShipper(
      identifier,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindServicesOrderInfoGetShipper_decode(response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future northwindServicesOrderInfoSetProductOfItems(
      String identifier, NorthwindServicesOrderInfoSetProductOfItemsInput input,
      {Options options}) async {
    final response =
        await apiDelegate.northwindServicesOrderInfoSetProductOfItems(
      identifier,
      input,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindServicesOrderInfoSetProductOfItems_decode(response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future<NorthwindServicesOrderItem> northwindServicesOrderInfoUpdateItems(
      String identifier, NorthwindServicesOrderItem input,
      {Options options}) async {
    final response = await apiDelegate.northwindServicesOrderInfoUpdateItems(
      identifier,
      input,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindServicesOrderInfoUpdateItems_decode(response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future<NorthwindServicesCategoryInfo> northwindServicesOrderItemGetCategory(
      String identifier,
      {Options options}) async {
    final response = await apiDelegate.northwindServicesOrderItemGetCategory(
      identifier,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindServicesOrderItemGetCategory_decode(response);
    }
  }

  ///
  ///
  ///
  ///
  ///
  ///
  Future<NorthwindServicesProductInfo> northwindServicesOrderItemGetProduct(
      String identifier,
      {Options options}) async {
    final response = await apiDelegate.northwindServicesOrderItemGetProduct(
      identifier,
      options: options,
    );

    if (response.statusCode >= 400) {
      throw ApiException(response.statusCode, await decodeBodyBytes(response));
    } else {
      return await apiDelegate
          .northwindServicesOrderItemGetProduct_decode(response);
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

class AllInternationalOrdersApiDelegate {
  final ApiClient apiClient;

  AllInternationalOrdersApiDelegate(this.apiClient) : assert(apiClient != null);

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

  Future<ApiResponse> northwindServicesInternationalOrderInfoDeleteAllCancelled(
      {Options options}) async {
    Object postBody;

    // verify required params are set

    // create path and map variables
    final __path = '/services/InternationalOrderInfo/deleteAllCancelled';

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

  Future northwindServicesInternationalOrderInfoDeleteAllCancelled_decode(
      ApiResponse response) async {
    if (response.body != null) {}

    return;
  }

  Future<ApiResponse> northwindServicesInternationalOrderInfoShipAll(
      {Options options}) async {
    Object postBody;

    // verify required params are set

    // create path and map variables
    final __path = '/services/InternationalOrderInfo/shipAll';

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

  Future northwindServicesInternationalOrderInfoShipAll_decode(
      ApiResponse response) async {
    if (response.body != null) {}

    return;
  }

  Future<ApiResponse> northwindServicesOrderInfoChangeShipment(
      String identifier, NorthwindServicesShipmentChange input,
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
    final __path = '/services/OrderInfo/changeShipment';

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

  Future<NorthwindServicesOrderInfo>
      northwindServicesOrderInfoChangeShipment_decode(
          ApiResponse response) async {
    if (response.body != null) {
      return LocalApiClient.deserializeFromString(
              await decodeBodyBytes(response), 'NorthwindServicesOrderInfo')
          as NorthwindServicesOrderInfo;
    }

    return null;
  }

  Future<ApiResponse> northwindServicesOrderInfoCreateItems(
      String identifier, NorthwindServicesOrderItemExtended input,
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
    final __path = '/services/OrderInfo/items/create';

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

  Future<NorthwindServicesOrderItem>
      northwindServicesOrderInfoCreateItems_decode(ApiResponse response) async {
    if (response.body != null) {
      return LocalApiClient.deserializeFromString(
              await decodeBodyBytes(response), 'NorthwindServicesOrderItem')
          as NorthwindServicesOrderItem;
    }

    return null;
  }

  Future<ApiResponse> northwindServicesOrderInfoDeleteItems(
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
    final __path = '/services/OrderInfo/items/delete';

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

  Future northwindServicesOrderInfoDeleteItems_decode(
      ApiResponse response) async {
    if (response.body != null) {}

    return;
  }

  Future<ApiResponse> northwindServicesOrderInfoGetCategories(String identifier,
      {Options options}) async {
    Object postBody;

    // verify required params are set
    if (identifier == null) {
      throw ApiException(400, 'Missing required param: identifier');
    }

    // create path and map variables
    final __path = '/services/OrderInfo/categories/get';

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

  Future<List<NorthwindServicesCategoryInfo>>
      northwindServicesOrderInfoGetCategories_decode(
          ApiResponse response) async {
    if (response.body != null) {
      return (LocalApiClient.deserializeFromString(
              await decodeBodyBytes(response),
              'List<NorthwindServicesCategoryInfo>') as List)
          .map((item) => item as NorthwindServicesCategoryInfo)
          .toList();
    }

    return null;
  }

  Future<ApiResponse> northwindServicesOrderInfoGetItems(String identifier,
      {Options options}) async {
    Object postBody;

    // verify required params are set
    if (identifier == null) {
      throw ApiException(400, 'Missing required param: identifier');
    }

    // create path and map variables
    final __path = '/services/OrderInfo/items/get';

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

  Future<List<NorthwindServicesOrderItem>>
      northwindServicesOrderInfoGetItems_decode(ApiResponse response) async {
    if (response.body != null) {
      return (LocalApiClient.deserializeFromString(
              await decodeBodyBytes(response),
              'List<NorthwindServicesOrderItem>') as List)
          .map((item) => item as NorthwindServicesOrderItem)
          .toList();
    }

    return null;
  }

  Future<ApiResponse> northwindServicesOrderInfoGetShipper(String identifier,
      {Options options}) async {
    Object postBody;

    // verify required params are set
    if (identifier == null) {
      throw ApiException(400, 'Missing required param: identifier');
    }

    // create path and map variables
    final __path = '/services/OrderInfo/shipper/get';

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

  Future<NorthwindServicesShipperInfo>
      northwindServicesOrderInfoGetShipper_decode(ApiResponse response) async {
    if (response.body != null) {
      return LocalApiClient.deserializeFromString(
              await decodeBodyBytes(response), 'NorthwindServicesShipperInfo')
          as NorthwindServicesShipperInfo;
    }

    return null;
  }

  Future<ApiResponse> northwindServicesOrderInfoSetProductOfItems(
      String identifier, NorthwindServicesOrderInfoSetProductOfItemsInput input,
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
    final __path = '/services/OrderInfo/items/product/set';

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

  Future northwindServicesOrderInfoSetProductOfItems_decode(
      ApiResponse response) async {
    if (response.body != null) {}

    return;
  }

  Future<ApiResponse> northwindServicesOrderInfoUpdateItems(
      String identifier, NorthwindServicesOrderItem input,
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
    final __path = '/services/OrderInfo/items/update';

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

  Future<NorthwindServicesOrderItem>
      northwindServicesOrderInfoUpdateItems_decode(ApiResponse response) async {
    if (response.body != null) {
      return LocalApiClient.deserializeFromString(
              await decodeBodyBytes(response), 'NorthwindServicesOrderItem')
          as NorthwindServicesOrderItem;
    }

    return null;
  }

  Future<ApiResponse> northwindServicesOrderItemGetCategory(String identifier,
      {Options options}) async {
    Object postBody;

    // verify required params are set
    if (identifier == null) {
      throw ApiException(400, 'Missing required param: identifier');
    }

    // create path and map variables
    final __path = '/services/OrderItem/category/get';

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
      northwindServicesOrderItemGetCategory_decode(ApiResponse response) async {
    if (response.body != null) {
      return LocalApiClient.deserializeFromString(
              await decodeBodyBytes(response), 'NorthwindServicesCategoryInfo')
          as NorthwindServicesCategoryInfo;
    }

    return null;
  }

  Future<ApiResponse> northwindServicesOrderItemGetProduct(String identifier,
      {Options options}) async {
    Object postBody;

    // verify required params are set
    if (identifier == null) {
      throw ApiException(400, 'Missing required param: identifier');
    }

    // create path and map variables
    final __path = '/services/OrderItem/product/get';

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

  Future<NorthwindServicesProductInfo>
      northwindServicesOrderItemGetProduct_decode(ApiResponse response) async {
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
