part of openapi.api;

// northwind_ExternalAP__setCategoryOfAllProducts__input_category
class NorthwindExternalAPSetCategoryOfAllProductsInputCategory {
  /* ID of referenced instance */
  String identifier;
  NorthwindExternalAPSetCategoryOfAllProductsInputCategory();

  @override
  String toString() {
    return 'NorthwindExternalAPSetCategoryOfAllProductsInputCategory[identifier=$identifier, ]';
  }

  fromJson(Map<String, dynamic> json) {
    if (json == null) return;
  
    {
      final _jsonData = json[r'__identifier'];
      identifier = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName

  }

  NorthwindExternalAPSetCategoryOfAllProductsInputCategory.fromJson(Map<String, dynamic> json) {
    fromJson(json); // allows child classes to call
  }

  Map<String, dynamic> toJson() {

    final json = <String, dynamic>{};
    if (identifier != null) {
        json[r'__identifier'] = LocalApiClient.serialize(identifier);
    }
    return json;
  }
  static List<NorthwindExternalAPSetCategoryOfAllProductsInputCategory> listFromJson(List<dynamic> json) {
    return json == null ? <NorthwindExternalAPSetCategoryOfAllProductsInputCategory>[] : json.map((value) => NorthwindExternalAPSetCategoryOfAllProductsInputCategory.fromJson(value)).toList();
  }

  static Map<String, NorthwindExternalAPSetCategoryOfAllProductsInputCategory> mapFromJson(Map<String, dynamic> json) {
    final map = <String, NorthwindExternalAPSetCategoryOfAllProductsInputCategory>{};
    if (json != null && json.isNotEmpty) {
      json.forEach((String key, dynamic value) => map[key] = NorthwindExternalAPSetCategoryOfAllProductsInputCategory.fromJson(value));
    }
    return map;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) {
      return true;
    }

    if (other is NorthwindExternalAPSetCategoryOfAllProductsInputCategory && runtimeType == other.runtimeType) {
    return 

     identifier == other.identifier
    ;
    }

    return false;
  }

  @override
  int get hashCode {
    var hashCode = runtimeType.hashCode;

    

    if (identifier != null) {
      hashCode = hashCode ^ identifier.hashCode;
    }


    return hashCode;
  }

  NorthwindExternalAPSetCategoryOfAllProductsInputCategory copyWith({
       String identifier,
    }) {
    NorthwindExternalAPSetCategoryOfAllProductsInputCategory copy = NorthwindExternalAPSetCategoryOfAllProductsInputCategory();
        copy.identifier = identifier ?? this.identifier;
    return copy;
  }
}

