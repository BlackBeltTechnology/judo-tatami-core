import 'dart:async';
import 'package:judo/rest/external/api.dart';
import 'package:judo/store/external/northwind_external_category_info_store.dart';
import 'package:judo/utilities/constants.dart';
import 'package:openapi_dart_common/openapi.dart';

class NorthwindExternalCategoryInfoRepository {
  final ApiClient _apiClient =
      ApiClient(basePath: kBasePathUrl, apiClientDelegate: DioClientDelegate());

  Future createCategory(NorthwindExternalCategoryInfoStore categoryInfo) async {
    NorthwindServicesCategoryInfoExtended categoryExtended =
        NorthwindServicesCategoryInfoExtended();

    categoryExtended.categoryName = categoryInfo.categoryName;

    NorthwindServicesCategoryInfo servicesCategoryInfo =
        await DefaultApi(_apiClient)
            .northwindExternalAPCreateAllCategories(categoryExtended);

    categoryInfo.identifier = servicesCategoryInfo.identifier;
  }

  Future removeCategory(NorthwindExternalCategoryInfoStore categoryInfo) async {
    NorthwindIdentifier northwindIdentifier = NorthwindIdentifier();
    northwindIdentifier.identifier = categoryInfo.identifier;
    await DefaultApi(_apiClient)
        .northwindExternalAPDeleteAllCategories(northwindIdentifier);
  }

  Future update(NorthwindExternalCategoryInfoStore oldCategoryInfo,
      NorthwindExternalCategoryInfoStore newCategoryInfo) async {}

  Future<NorthwindExternalCategoryInfoStore> get() async {}

  Future getAll(
      List<NorthwindExternalCategoryInfoStore> categoryInfoList) async {
    List<NorthwindServicesCategoryInfo> list =
        await DefaultApi(_apiClient).northwindExternalAPGetAllCategories();

    List<NorthwindExternalCategoryInfoStore> tempCategoryList = [];

    list.forEach((element) {
      if (!categoryInfoList
          .any((category) => category.identifier == element.identifier)) {
        NorthwindExternalCategoryInfoStore newCategory =
            NorthwindExternalCategoryInfoStore();
        newCategory.identifier = element.identifier;
        newCategory.categoryName = element.categoryName;
        tempCategoryList.add(newCategory);
      }
    });

    categoryInfoList.addAll(tempCategoryList);
  }
}
