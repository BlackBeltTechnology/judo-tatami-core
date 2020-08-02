part of openapi.api;

// northwind_ExternalAP__setCategoryOfAllProducts__input
class NorthwindExternalAPSetCategoryOfAllProductsInput {
  /* ID of instance */
  String identifier;
  
  NorthwindExternalAPSetCategoryOfAllProductsInputCategory category;
  NorthwindExternalAPSetCategoryOfAllProductsInput();

  @override
  String toString() {
    return 'NorthwindExternalAPSetCategoryOfAllProductsInput[identifier=$identifier, category=$category, ]';
  }

  fromJson(Map<String, dynamic> json) {
    if (json == null) return;
  
    {
      final _jsonData = json[r'__identifier'];
      identifier = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName
    {
      final _jsonData = json[r'category'];
      category = (_jsonData == null) ? null :
        
        NorthwindExternalAPSetCategoryOfAllProductsInputCategory.fromJson(_jsonData);
    } // _jsonFieldName

  }

  NorthwindExternalAPSetCategoryOfAllProductsInput.fromJson(Map<String, dynamic> json) {
    fromJson(json); // allows child classes to call
  }

  Map<String, dynamic> toJson() {

    final json = <String, dynamic>{};
    if (identifier != null) {
        json[r'__identifier'] = LocalApiClient.serialize(identifier);
    }
    if (category != null) {
        json[r'category'] = LocalApiClient.serialize(category);
    }
    return json;
  }
  static List<NorthwindExternalAPSetCategoryOfAllProductsInput> listFromJson(List<dynamic> json) {
    return json == null ? <NorthwindExternalAPSetCategoryOfAllProductsInput>[] : json.map((value) => NorthwindExternalAPSetCategoryOfAllProductsInput.fromJson(value)).toList();
  }

  static Map<String, NorthwindExternalAPSetCategoryOfAllProductsInput> mapFromJson(Map<String, dynamic> json) {
    final map = <String, NorthwindExternalAPSetCategoryOfAllProductsInput>{};
    if (json != null && json.isNotEmpty) {
      json.forEach((String key, dynamic value) => map[key] = NorthwindExternalAPSetCategoryOfAllProductsInput.fromJson(value));
    }
    return map;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) {
      return true;
    }

    if (other is NorthwindExternalAPSetCategoryOfAllProductsInput && runtimeType == other.runtimeType) {
    return 

     identifier == other.identifier &&
  
          category == other.category    
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

            if (category != null) {
              hashCode = hashCode ^ category.hashCode;
            }
    

    return hashCode;
  }

  NorthwindExternalAPSetCategoryOfAllProductsInput copyWith({
       String identifier,
       NorthwindExternalAPSetCategoryOfAllProductsInputCategory category,
    }) {
    NorthwindExternalAPSetCategoryOfAllProductsInput copy = NorthwindExternalAPSetCategoryOfAllProductsInput();
        copy.identifier = identifier ?? this.identifier;
        copy.category = category ?? this.category?.copyWith();
    return copy;
  }
}

