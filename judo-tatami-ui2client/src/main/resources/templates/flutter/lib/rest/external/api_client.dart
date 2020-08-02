part of openapi.api;

class LocalApiClient {
  static final _regList = RegExp(r'^List<(.*)>$');
  static final _regMap = RegExp(r'^Map<String,(.*)>$');

  static dynamic serialize(Object value) {
    try {
      if (value == null) {
        return null;
      } else if (value is List) {
        return value.map((v) => serialize(v)).toList();
      } else if (value is Map) {
        return Map.fromIterables(
            value.keys, value.values.map((v) => serialize(v)));
      } else if (value is String) {
        return value;
      } else if (value is bool) {
        return value;
      } else if (value is num) {
        return value;
      } else if (value is DateTime) {
        return value.toUtc().toIso8601String();
      }
      if (value is NorthwindExternalAP) {
        return value.toJson();
      }
      if (value is NorthwindExternalAPExtended) {
        return value.toJson();
      }
      if (value is NorthwindExternalAPSetCategoryOfAllProductsInput) {
        return value.toJson();
      }
      if (value is NorthwindExternalAPSetCategoryOfAllProductsInputCategory) {
        return value.toJson();
      }
      if (value is NorthwindIdentifier) {
        return value.toJson();
      }
      if (value is NorthwindServicesCategoryInfo) {
        return value.toJson();
      }
      if (value is NorthwindServicesCategoryInfoExtended) {
        return value.toJson();
      }
      if (value is NorthwindServicesCategoryInfoExtendedProducts) {
        return value.toJson();
      }
      if (value is NorthwindServicesCategoryInfoSetCategoryOfProductsInput) {
        return value.toJson();
      }
      if (value is NorthwindServicesProductInfo) {
        return value.toJson();
      }
      if (value is NorthwindServicesProductInfoExtended) {
        return value.toJson();
      }
      if (value is NorthwindServicesProductInfoExtendedCategory) {
        return value.toJson();
      }
      return value.toString();
    } on Exception catch (e, stack) {
      throw ApiException.withInner(
          500, 'Exception during deserialization.', e, stack);
    }
  }

  static dynamic deserializeFromString(String json, String targetType) {
    if (json == null) {
      // HTTP Code 204
      return null;
    }

    // Remove all spaces.  Necessary for reg expressions as well.
    targetType = targetType.replaceAll(' ', '');

    if (targetType == 'String') return json;

    var decodedJson = jsonDecode(json);
    return deserialize(decodedJson, targetType);
  }

  static dynamic deserialize(dynamic value, String targetType) {
    if (value == null) return null; // 204
    try {
      switch (targetType) {
        case 'String':
          return '$value';
        case 'int':
          return value is int ? value : int.parse('$value');
        case 'bool':
          return value is bool ? value : '$value'.toLowerCase() == 'true';
        case 'double':
          return value is double ? value : double.parse('$value');
        case 'NorthwindExternalAP':
          return NorthwindExternalAP.fromJson(value);
        case 'NorthwindExternalAPExtended':
          return NorthwindExternalAPExtended.fromJson(value);
        case 'NorthwindExternalAPSetCategoryOfAllProductsInput':
          return NorthwindExternalAPSetCategoryOfAllProductsInput.fromJson(
              value);
        case 'NorthwindExternalAPSetCategoryOfAllProductsInputCategory':
          return NorthwindExternalAPSetCategoryOfAllProductsInputCategory
              .fromJson(value);
        case 'NorthwindIdentifier':
          return NorthwindIdentifier.fromJson(value);
        case 'NorthwindServicesCategoryInfo':
          return NorthwindServicesCategoryInfo.fromJson(value);
        case 'NorthwindServicesCategoryInfoExtended':
          return NorthwindServicesCategoryInfoExtended.fromJson(value);
        case 'NorthwindServicesCategoryInfoExtendedProducts':
          return NorthwindServicesCategoryInfoExtendedProducts.fromJson(value);
        case 'NorthwindServicesCategoryInfoSetCategoryOfProductsInput':
          return NorthwindServicesCategoryInfoSetCategoryOfProductsInput
              .fromJson(value);
        case 'NorthwindServicesProductInfo':
          return NorthwindServicesProductInfo.fromJson(value);
        case 'NorthwindServicesProductInfoExtended':
          return NorthwindServicesProductInfoExtended.fromJson(value);
        case 'NorthwindServicesProductInfoExtendedCategory':
          return NorthwindServicesProductInfoExtendedCategory.fromJson(value);
        default:
          {
            Match match;
            if (value is List &&
                (match = _regList.firstMatch(targetType)) != null) {
              var newTargetType = match[1];
              return value.map((v) => deserialize(v, newTargetType)).toList();
            } else if (value is Map &&
                (match = _regMap.firstMatch(targetType)) != null) {
              var newTargetType = match[1];
              return Map.fromIterables(value.keys,
                  value.values.map((v) => deserialize(v, newTargetType)));
            }
          }
      }
    } on Exception catch (e, stack) {
      throw ApiException.withInner(
          500, 'Exception during deserialization.', e, stack);
    }
    throw ApiException(
        500, 'Could not find a suitable class for deserialization');
  }

  /// Format the given parameter object into string.
  static String parameterToString(dynamic value) {
    if (value == null) {
      return '';
    } else if (value is DateTime) {
      return value.toUtc().toIso8601String();
    } else if (value is String) {
      return value.toString();
    }

    return jsonEncode(value);
  }
}
