part of openapi.api;

// northwind_services_CategoryInfo__extended
class NorthwindServicesCategoryInfoExtended {
  /* ID of Category instance */
  String identifier;
  
  String categoryName;
  
  NorthwindServicesCategoryInfoExtendedProducts products;
  NorthwindServicesCategoryInfoExtended();

  @override
  String toString() {
    return 'NorthwindServicesCategoryInfoExtended[identifier=$identifier, categoryName=$categoryName, products=$products, ]';
  }

  fromJson(Map<String, dynamic> json) {
    if (json == null) return;
  
    {
      final _jsonData = json[r'__identifier'];
      identifier = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName
    {
      final _jsonData = json[r'categoryName'];
      categoryName = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName
    {
      final _jsonData = json[r'products'];
      products = (_jsonData == null) ? null :
        
        NorthwindServicesCategoryInfoExtendedProducts.fromJson(_jsonData);
    } // _jsonFieldName

  }

  NorthwindServicesCategoryInfoExtended.fromJson(Map<String, dynamic> json) {
    fromJson(json); // allows child classes to call
  }

  Map<String, dynamic> toJson() {

    final json = <String, dynamic>{};
    if (identifier != null) {
        json[r'__identifier'] = LocalApiClient.serialize(identifier);
    }
    if (categoryName != null) {
        json[r'categoryName'] = LocalApiClient.serialize(categoryName);
    }
    if (products != null) {
        json[r'products'] = LocalApiClient.serialize(products);
    }
    return json;
  }
  static List<NorthwindServicesCategoryInfoExtended> listFromJson(List<dynamic> json) {
    return json == null ? <NorthwindServicesCategoryInfoExtended>[] : json.map((value) => NorthwindServicesCategoryInfoExtended.fromJson(value)).toList();
  }

  static Map<String, NorthwindServicesCategoryInfoExtended> mapFromJson(Map<String, dynamic> json) {
    final map = <String, NorthwindServicesCategoryInfoExtended>{};
    if (json != null && json.isNotEmpty) {
      json.forEach((String key, dynamic value) => map[key] = NorthwindServicesCategoryInfoExtended.fromJson(value));
    }
    return map;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) {
      return true;
    }

    if (other is NorthwindServicesCategoryInfoExtended && runtimeType == other.runtimeType) {
    return 

     identifier == other.identifier &&
  

     categoryName == other.categoryName &&
  
          products == other.products    
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


    if (categoryName != null) {
      hashCode = hashCode ^ categoryName.hashCode;
    }

            if (products != null) {
              hashCode = hashCode ^ products.hashCode;
            }
    

    return hashCode;
  }

  NorthwindServicesCategoryInfoExtended copyWith({
       String identifier,
       String categoryName,
       NorthwindServicesCategoryInfoExtendedProducts products,
    }) {
    NorthwindServicesCategoryInfoExtended copy = NorthwindServicesCategoryInfoExtended();
        copy.identifier = identifier ?? this.identifier;
        copy.categoryName = categoryName ?? this.categoryName;
        copy.products = products ?? this.products?.copyWith();
    return copy;
  }
}

