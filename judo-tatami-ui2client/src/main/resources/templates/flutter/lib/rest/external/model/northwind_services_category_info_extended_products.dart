part of openapi.api;

// northwind_services_CategoryInfo__extended_products
class NorthwindServicesCategoryInfoExtendedProducts {
  /* ID of Product instance */
  String identifier;
  NorthwindServicesCategoryInfoExtendedProducts();

  @override
  String toString() {
    return 'NorthwindServicesCategoryInfoExtendedProducts[identifier=$identifier, ]';
  }

  fromJson(Map<String, dynamic> json) {
    if (json == null) return;
  
    {
      final _jsonData = json[r'__identifier'];
      identifier = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName

  }

  NorthwindServicesCategoryInfoExtendedProducts.fromJson(Map<String, dynamic> json) {
    fromJson(json); // allows child classes to call
  }

  Map<String, dynamic> toJson() {

    final json = <String, dynamic>{};
    if (identifier != null) {
        json[r'__identifier'] = LocalApiClient.serialize(identifier);
    }
    return json;
  }
  static List<NorthwindServicesCategoryInfoExtendedProducts> listFromJson(List<dynamic> json) {
    return json == null ? <NorthwindServicesCategoryInfoExtendedProducts>[] : json.map((value) => NorthwindServicesCategoryInfoExtendedProducts.fromJson(value)).toList();
  }

  static Map<String, NorthwindServicesCategoryInfoExtendedProducts> mapFromJson(Map<String, dynamic> json) {
    final map = <String, NorthwindServicesCategoryInfoExtendedProducts>{};
    if (json != null && json.isNotEmpty) {
      json.forEach((String key, dynamic value) => map[key] = NorthwindServicesCategoryInfoExtendedProducts.fromJson(value));
    }
    return map;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) {
      return true;
    }

    if (other is NorthwindServicesCategoryInfoExtendedProducts && runtimeType == other.runtimeType) {
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

  NorthwindServicesCategoryInfoExtendedProducts copyWith({
       String identifier,
    }) {
    NorthwindServicesCategoryInfoExtendedProducts copy = NorthwindServicesCategoryInfoExtendedProducts();
        copy.identifier = identifier ?? this.identifier;
    return copy;
  }
}

