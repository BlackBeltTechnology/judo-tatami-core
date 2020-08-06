import 'dart:async';

import 'package:judo/rest/external_ap/lib/api.dart';
import 'package:judo/store/internal_ap/northwind_internal_ap_category_info_store.dart';
import 'package:judo/store/internal_ap/northwind_internal_ap_product_info_store.dart';
import 'package:judo/utilities/constants.dart';
import 'package:openapi_dart_common/openapi.dart';

class NorthwindInternalProductInfoRepository {
  final ApiClient _apiClient =
      ApiClient(basePath: kBasePathUrl, apiClientDelegate: DioClientDelegate());

  Future createProduct(NorthwindInternalProductInfoStore productInfo) async {}

  Future removeProduct(NorthwindInternalProductInfoStore productInfo) async {}

  Future updateProduct(NorthwindInternalProductInfoStore oldProductInfo,
      NorthwindInternalProductInfoStore newProductInfo) async {}

  Future<NorthwindInternalCategoryInfoStore> getProduct() async {}

  Future getAllProducts(
      List<NorthwindInternalProductInfoStore> productInfoList) async {}
}
