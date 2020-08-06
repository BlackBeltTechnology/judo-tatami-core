import 'dart:async';
import 'package:judo/rest/external_ap/lib/api.dart';
import 'package:judo/store/internal_ap/northwind_internal_ap_category_info_store.dart';
import 'package:judo/utilities/constants.dart';
import 'package:openapi_dart_common/openapi.dart';

class NorthwindInternalCategoryInfoRepository {
  final ApiClient _apiClient =
      ApiClient(basePath: kBasePathUrl, apiClientDelegate: DioClientDelegate());

  Future createCategory(
      NorthwindInternalCategoryInfoStore categoryInfo) async {}

  Future removeCategory(
      NorthwindInternalCategoryInfoStore categoryInfo) async {}

  Future updateCategory(NorthwindInternalCategoryInfoStore oldCategoryInfo,
      NorthwindInternalCategoryInfoStore newCategoryInfo) async {}

  Future<NorthwindInternalCategoryInfoStore> getCategory() async {}

  Future getAllCategories(
      List<NorthwindInternalCategoryInfoStore> categoryInfoList) async {}
}
