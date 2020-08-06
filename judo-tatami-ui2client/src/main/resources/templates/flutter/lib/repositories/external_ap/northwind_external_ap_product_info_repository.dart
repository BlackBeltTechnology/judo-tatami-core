import 'dart:async';

import 'package:judo/rest/external_ap/lib/api.dart';
import 'package:judo/store/external_ap/northwind_external_ap_category_info_store.dart';
import 'package:judo/store/external_ap/northwind_external_ap_product_info_store.dart';
import 'package:judo/utilities/constants.dart';
import 'package:openapi_dart_common/openapi.dart';

class NorthwindExternalProductInfoRepository {
  final ApiClient _apiClient =
      ApiClient(basePath: kBasePathUrl, apiClientDelegate: DioClientDelegate());

  Future createProduct(NorthwindExternalProductInfoStore productInfo) async {
    var northwindServicesProductInfoExtended =
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
    productInfo.weight = northwindServicesProductInfo.weight;
    productInfo.unitPrice = northwindServicesProductInfo.unitPrice;
    productInfo.productName = northwindServicesProductInfo.productName;
  }

  Future removeProduct(NorthwindExternalProductInfoStore productInfo) async {
    var northwindIdentifier = NorthwindIdentifier();
    northwindIdentifier.identifier = productInfo.identifier;

    await DefaultApi(_apiClient)
        .northwindExternalAPDeleteAllProducts(northwindIdentifier);
  }

  Future updateProduct(NorthwindExternalProductInfoStore oldProductInfo,
      NorthwindExternalProductInfoStore newProductInfo) async {}

  Future<NorthwindExternalCategoryInfoStore> getProduct() async {}

  Future getAllProducts(
      List<NorthwindExternalProductInfoStore> productInfoList) async {
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
