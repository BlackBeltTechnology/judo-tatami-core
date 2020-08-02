import 'dart:async';

import 'package:judo/rest/external/api.dart';
import 'package:judo/store/internal/northwind_internal_category_info_store.dart';
import 'package:judo/store/internal/northwind_internal_product_info_store.dart';
import 'package:judo/utilities/constants.dart';
import 'package:openapi_dart_common/openapi.dart';

class NorthwindInternalProductInfoRepository {
  final ApiClient _apiClient =
      ApiClient(basePath: kBasePathUrl, apiClientDelegate: DioClientDelegate());

  Future getAll(
      List<NorthwindInternalProductInfoStore> productInfoList) async {}
}
