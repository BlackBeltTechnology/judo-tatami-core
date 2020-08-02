part of openapi.api;

// northwind_ExternalAP__extended
class NorthwindExternalAPExtended {
  /* Array of CategoryInfo instances (for creation) */
  List<NorthwindServicesCategoryInfoExtended> allCategories = [];
  /* Array of ProductInfo instances (for creation) */
  List<NorthwindServicesProductInfoExtended> allProducts = [];
  NorthwindExternalAPExtended();

  @override
  String toString() {
    return 'NorthwindExternalAPExtended[allCategories=$allCategories, allProducts=$allProducts, ]';
  }

  fromJson(Map<String, dynamic> json) {
    if (json == null) return;
  
    {
      final _jsonData = json[r'allCategories'];
      allCategories = (_jsonData == null) ? null :
            NorthwindServicesCategoryInfoExtended.listFromJson(_jsonData);
    } // _jsonFieldName
    {
      final _jsonData = json[r'allProducts'];
      allProducts = (_jsonData == null) ? null :
            NorthwindServicesProductInfoExtended.listFromJson(_jsonData);
    } // _jsonFieldName

  }

  NorthwindExternalAPExtended.fromJson(Map<String, dynamic> json) {
    fromJson(json); // allows child classes to call
  }

  Map<String, dynamic> toJson() {

    final json = <String, dynamic>{};
    if (allCategories != null) {
        json[r'allCategories'] = LocalApiClient.serialize(allCategories);
    }
    if (allProducts != null) {
        json[r'allProducts'] = LocalApiClient.serialize(allProducts);
    }
    return json;
  }
  static List<NorthwindExternalAPExtended> listFromJson(List<dynamic> json) {
    return json == null ? <NorthwindExternalAPExtended>[] : json.map((value) => NorthwindExternalAPExtended.fromJson(value)).toList();
  }

  static Map<String, NorthwindExternalAPExtended> mapFromJson(Map<String, dynamic> json) {
    final map = <String, NorthwindExternalAPExtended>{};
    if (json != null && json.isNotEmpty) {
      json.forEach((String key, dynamic value) => map[key] = NorthwindExternalAPExtended.fromJson(value));
    }
    return map;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) {
      return true;
    }

    if (other is NorthwindExternalAPExtended && runtimeType == other.runtimeType) {
    return 
        const ListEquality().equals(allCategories, other.allCategories) &&
    
        const ListEquality().equals(allProducts, other.allProducts)    
    ;
    }

    return false;
  }

  @override
  int get hashCode {
    var hashCode = runtimeType.hashCode;

    
        hashCode = hashCode ^ const ListEquality().hash(allCategories);
    
        hashCode = hashCode ^ const ListEquality().hash(allProducts);
    

    return hashCode;
  }

  NorthwindExternalAPExtended copyWith({
       List<NorthwindServicesCategoryInfoExtended> allCategories,
       List<NorthwindServicesProductInfoExtended> allProducts,
    }) {
    NorthwindExternalAPExtended copy = NorthwindExternalAPExtended();
        {
        var newVal;
        final v = allCategories ?? this.allCategories;
          newVal = <NorthwindServicesCategoryInfoExtended>        []..addAll((v ?? []).map((y) => y.copyWith()).toList())
;
        copy.allCategories = newVal;
        }
        {
        var newVal;
        final v = allProducts ?? this.allProducts;
          newVal = <NorthwindServicesProductInfoExtended>        []..addAll((v ?? []).map((y) => y.copyWith()).toList())
;
        copy.allProducts = newVal;
        }
    return copy;
  }
}

