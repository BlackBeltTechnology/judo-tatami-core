library openapi.api;

import 'dart:async';
import 'dart:convert';
import 'package:dio/dio.dart';
import 'package:openapi_dart_common/openapi.dart';
import 'package:collection/collection.dart';


part 'api_client.dart';

part 'api/all_international_orders_api.dart';
part 'api/default_api.dart';

part 'model/northwind_identifier.dart';
part 'model/northwind_internal_ap.dart';
part 'model/northwind_internal_ap_extended.dart';
part 'model/northwind_internal_ap_remove_items_from_all_international_orders_input.dart';
part 'model/northwind_internal_ap_remove_items_from_all_international_orders_input_items.dart';
part 'model/northwind_internal_ap_set_shipper_of_all_international_orders_input.dart';
part 'model/northwind_internal_ap_set_shipper_of_all_international_orders_input_shipper.dart';
part 'model/northwind_services_category_info.dart';
part 'model/northwind_services_category_info_extended.dart';
part 'model/northwind_services_category_info_extended_products.dart';
part 'model/northwind_services_category_info_set_category_of_products_input.dart';
part 'model/northwind_services_comment.dart';
part 'model/northwind_services_comment_extended.dart';
part 'model/northwind_services_international_order_info.dart';
part 'model/northwind_services_international_order_info_extended.dart';
part 'model/northwind_services_order_info.dart';
part 'model/northwind_services_order_info_extended.dart';
part 'model/northwind_services_order_info_extended_shipper.dart';
part 'model/northwind_services_order_info_set_product_of_items_input.dart';
part 'model/northwind_services_order_item.dart';
part 'model/northwind_services_order_item_extended.dart';
part 'model/northwind_services_product_info.dart';
part 'model/northwind_services_product_info_extended.dart';
part 'model/northwind_services_product_info_extended_category.dart';
part 'model/northwind_services_shipment_change.dart';
part 'model/northwind_services_shipment_change_extended.dart';
part 'model/northwind_services_shipper_info.dart';
part 'model/northwind_services_shipper_info_extended.dart';


