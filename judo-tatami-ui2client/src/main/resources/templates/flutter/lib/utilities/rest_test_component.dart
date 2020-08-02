import 'dart:convert';

import 'package:flutter/material.dart';
import 'package:http/http.dart';
import 'package:judo/components/judo_button.dart';
import 'package:judo/components/judo_input_text.dart';
import 'package:judo/components/judo_title.dart';
import 'package:judo/rest/external/api.dart';
import 'package:judo/utilities/constants.dart';
import 'package:openapi_dart_common/openapi.dart';

class HttpRequestTesting extends StatefulWidget {
  @override
  _HttpRequestTestingState createState() => _HttpRequestTestingState();
}

class _HttpRequestTestingState extends State<HttpRequestTesting> {
  String textString = '';
  String categoryName = '';
  String productName = '';
  double productPrice = 0;
  List<Category> categoryList = [];
  List<Product> productList = [];
  Category dropdownValue;
  final ApiClient _apiClient = ApiClient(
      basePath: "http://localhost:8181/api/northwind",
      apiClientDelegate: DioClientDelegate());

  void createCategoryTwo() async {
    NorthwindServicesCategoryInfoExtended _category =
        NorthwindServicesCategoryInfoExtended();
    _category.categoryName = this.categoryName;
    print((await DefaultApi(_apiClient)
            .northwindExternalAPCreateAllCategories(_category))
        .toString());
  }

  void createCategory() async {
    Map<String, String> requestHeaders = {
      'Content-Type': 'application/json',
    };

    Map<String, String> requestBody = {
      'categoryName': categoryName,
    };

    Response response = await post(
      'http://localhost:8181/api/northwind/ExternalAP/allCategories/create',
      headers: requestHeaders,
      body: jsonEncode(requestBody),
    );

    if (response.statusCode == 200) {
      dynamic responseBody = jsonDecode(response.body);
      setState(() {
        textString = 'post, code 200, ok';
        categoryList.add(Category(
            responseBody['__identifier'], responseBody['categoryName']));
      });
      print(categoryList);
    } else {
      setState(() {
        textString = 'something went wrong';
      });
      print(response.statusCode);
      print(response.body);
    }
  }

  void getAllCategoryTwo() async {
    print((await DefaultApi(_apiClient).northwindExternalAPGetAllCategories())
        .toString());
  }

  void getAllCategory() async {
    Response response = await get(
        'http://localhost:8181/api/northwind/ExternalAP/allCategories/get');

    if (response.statusCode == 200) {
      dynamic responseBody = jsonDecode(response.body);
      List<Category> tempList = [];
      for (var element in responseBody) {
        tempList
            .add(Category(element['__identifier'], element['categoryName']));
      }
      setState(() {
        for (Category element in tempList) {
          if (!categoryList.contains(element)) {
            categoryList.add(element);
          }
        }
        textString = 'get, code 200, ok';
      });
      print(response.body);
    } else {
      print(response.statusCode);
      print(response.body);
    }
  }

  Future<Category> getProductCategory(String productId) async {
    Map<String, String> requestHeaders = {
      '__identifier': productId,
    };

    Response response = await get(
        'http://localhost:8181/api/northwind/services/ProductInfo/category/get',
        headers: requestHeaders);

    if (response.statusCode == 200) {
      print(response.body);
      dynamic responseBody = jsonDecode(response.body);
      return Category(responseBody['__identifier'], responseBody['_name']);
    } else {
      print(response.statusCode);
      print(response.body);
      return null;
    }
  }

  void getAllProduct() async {
    Response response = await get(
        'http://localhost:8181/api/northwind/ExternalAP/allProducts/get');

    if (response.statusCode == 200) {
      dynamic responseBody = jsonDecode(response.body);
      List<Product> tempList = [];
      for (var element in responseBody) {
        tempList.add(Product(
            element['__identifier'],
            element['productName'],
            element['unitPrice'],
            await getProductCategory(element['__identifier'])));
      }
      setState(() {
        for (Product element in tempList) {
          if (!productList.contains(element)) {
            productList.add(element);
          }
        }
        textString = 'get, code 200, ok';
      });
      print(response.body);
    } else {
      print(response.statusCode);
      print(response.body);
    }
  }

  void createProduct() async {
    Map<String, String> requestHeaders = {
      'Content-Type': 'application/json',
      '__identifier': dropdownValue.id,
    };

    Map<String, dynamic> requestBody = {
      'productName': productName,
      'unitPrice': productPrice
    };

    Response response = await post(
      'http://localhost:8181/api/northwind/services/CategoryInfo/products/create',
      headers: requestHeaders,
      body: jsonEncode(requestBody),
    );

    print(dropdownValue.id);
    print(dropdownValue.name);
    print(productName);
    print(productPrice);

    if (response.statusCode == 200) {
      dynamic responseBody = jsonDecode(response.body);
      setState(() {
        textString = 'post, code 200, ok';
        productList.add(Product(
            responseBody['__identifier'],
            responseBody['productName'],
            responseBody['unitPrice'],
            dropdownValue));
      });
      print(productList);
    } else {
      setState(() {
        textString = 'something went wrong';
      });
      print(response.statusCode);
      print(response.body);
    }
  }

  @override
  Widget build(BuildContext context) {
    return Column(
      mainAxisSize: MainAxisSize.min,
      children: [
        Row(
          children: [
            Text(textString),
          ],
        ),
        SizedBox(
          height: 50,
        ),
        JudoTitle(col: 1, text: 'Create category'),
        Row(
          children: [
            JudoInputText(
              col: 1,
              label: 'Category name',
              onChanged: (value) => categoryName = value,
            ),
            JudoButton(
              col: 1,
              label: 'Create Category',
              onPressed: createCategoryTwo,
            ),
          ],
        ),
        SizedBox(
          height: 50,
        ),
        JudoTitle(col: 1, text: 'Get categories'),
        Row(
          children: [
            JudoButton(
              col: 1,
              label: 'Get categories',
              onPressed: getAllCategory,
            ),
            JudoButton(
              col: 1,
              label: 'Get categories 2',
              onPressed: getAllCategoryTwo,
            ),
            JudoButton(
              col: 1,
              label: 'Get all products',
              onPressed: getAllProduct,
            ),
          ],
        ),
        SizedBox(
          height: 50,
        ),
        JudoTitle(col: 1, text: 'Create product'),
        Row(
          children: [
            JudoInputText(
              col: 1,
              label: 'Product Name',
              onChanged: (value) => productName = value,
            ),
            JudoInputText(
              col: 1,
              label: 'Product price',
              onChanged: (value) => productPrice = double.parse(value),
            ),
            DropdownButton<Category>(
              hint: Text('select'),
              value: dropdownValue,
              icon: Icon(Icons.arrow_drop_down),
              elevation: 16,
              style: TextStyle(color: kPrimaryColor),
              underline: Container(
                height: 2,
                color: kSecondaryColor,
              ),
              onChanged: (Category newValue) {
                setState(() {
                  dropdownValue = newValue;
                });
              },
              items: categoryList
                  .map<DropdownMenuItem<Category>>((Category value) {
                return DropdownMenuItem<Category>(
                  value: value,
                  child: Text(value.name),
                );
              }).toList(),
            ),
            JudoButton(
              col: 1,
              label: 'Create Product',
              onPressed: createProduct,
            ),
          ],
        ),
      ],
    );
  }
}

class Category {
  String _id;
  String _name;

  Category(this._id, this._name);

  String get name => _name;

  String get id => _id;

  @override
  bool operator ==(Object other) => other is Category && _id == other._id;

  @override
  int get hashCode => _id.hashCode;

  @override
  String toString() {
    return 'Category{_id: $_id, _name: $_name}';
  }
}

class Product {
  String _id;
  String _name;
  double _price;
  Category _category;

  Product(this._id, this._name, this._price, this._category);

  Category get category => _category;

  double get price => _price;

  String get name => _name;

  String get id => _id;

  @override
  String toString() {
    return 'Product{_id: $_id, _name: $_name, _category: $_category}';
  }
}
