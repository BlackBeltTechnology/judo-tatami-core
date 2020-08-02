library openapi.api;

import 'dart:async';
import 'dart:convert';
import 'package:dio/dio.dart';
import 'package:openapi_dart_common/openapi.dart';
import 'package:collection/collection.dart';


part 'api_client.dart';

part 'api/all_categories_api.dart';
part 'api/all_products_api.dart';
part 'api/default_api.dart';

part 'model/northwind_external_ap.dart';
part 'model/northwind_external_ap_extended.dart';
part 'model/northwind_external_ap_set_category_of_all_products_input.dart';
part 'model/northwind_external_ap_set_category_of_all_products_input_category.dart';
part 'model/northwind_identifier.dart';
part 'model/northwind_services_category_info.dart';
part 'model/northwind_services_category_info_extended.dart';
part 'model/northwind_services_category_info_extended_products.dart';
part 'model/northwind_services_category_info_set_category_of_products_input.dart';
part 'model/northwind_services_product_info.dart';
part 'model/northwind_services_product_info_extended.dart';
part 'model/northwind_services_product_info_extended_category.dart';


