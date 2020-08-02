import 'dart:async';

import 'package:judo/rest/external/api.dart';
import 'package:judo/store/external/northwind_external_category_info_store.dart';
import 'package:judo/store/external/northwind_external_product_info_store.dart';
import 'package:judo/utilities/constants.dart';
import 'package:openapi_dart_common/openapi.dart';

class NorthwindExternalProductInfoRepository {
  final ApiClient _apiClient =
      ApiClient(basePath: kBasePathUrl, apiClientDelegate: DioClientDelegate());

  Future createProduct(NorthwindExternalProductInfoStore productInfo) async {
    NorthwindServicesProductInfoExtended northwindServicesProductInfoExtended =
        NorthwindServicesProductInfoExtended();

    northwindServicesProductInfoExtended.weight = productInfo.weight;
    northwindServicesProductInfoExtended.unitPrice = productInfo.unitPrice;
    northwindServicesProductInfoExtended.productName = productInfo.productName;

    NorthwindServicesProductInfo northwindServicesProductInfo =
        await AllProductsApi(_apiClient)
            .northwindServicesCategoryInfoCreateProducts(
                productInfo.category.identifier,
                northwindServicesProductInfoExtended);

    productInfo.identifier = northwindServicesProductInfo.identifier;
  }

  Future removeProduct(NorthwindExternalProductInfoStore productInfo) async {
    NorthwindIdentifier northwindIdentifier = NorthwindIdentifier();
    northwindIdentifier.identifier = productInfo.identifier;
    await DefaultApi(_apiClient)
        .northwindExternalAPDeleteAllProducts(northwindIdentifier);
  }

  Future update(NorthwindExternalCategoryInfoStore oldCategoryInfo,
      NorthwindExternalCategoryInfoStore newCategoryInfo) async {}

  Future<NorthwindExternalCategoryInfoStore> get() async {}

  Future getAll(List<NorthwindExternalProductInfoStore> productInfoList) async {
    List<NorthwindServicesProductInfo> list =
        await DefaultApi(_apiClient).northwindExternalAPGetAllProducts();

    List<NorthwindExternalProductInfoStore> tempProductList = [];

    list.forEach((element) async {
      if (!productInfoList
          .any((product) => product.identifier == element.identifier)) {
        NorthwindExternalProductInfoStore newProduct =
            NorthwindExternalProductInfoStore();
        newProduct.identifier = element.identifier;
        newProduct.productName = element.productName;
        newProduct.unitPrice = element.unitPrice;
        newProduct.weight = element.weight;
        tempProductList.add(newProduct);

        NorthwindServicesCategoryInfo northwindServicesCategoryInfo =
            await AllCategoriesApi(_apiClient)
                .northwindServicesProductInfoGetCategory(element.identifier);

        // TODO: check category exists
        newProduct.category = NorthwindExternalCategoryInfoStore();
        newProduct.category.identifier =
            northwindServicesCategoryInfo.identifier;

        newProduct.category.categoryName =
            northwindServicesCategoryInfo.categoryName;
      }
    });

    productInfoList.addAll(tempProductList);
  }
}
