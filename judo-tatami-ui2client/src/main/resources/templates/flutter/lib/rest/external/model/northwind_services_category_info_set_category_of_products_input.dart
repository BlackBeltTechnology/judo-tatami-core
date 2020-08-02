part of openapi.api;

// northwind_services_CategoryInfo__setCategoryOfProducts__input
class NorthwindServicesCategoryInfoSetCategoryOfProductsInput {
  /* ID of instance */
  String identifier;
  
  NorthwindExternalAPSetCategoryOfAllProductsInputCategory category;
  NorthwindServicesCategoryInfoSetCategoryOfProductsInput();

  @override
  String toString() {
    return 'NorthwindServicesCategoryInfoSetCategoryOfProductsInput[identifier=$identifier, category=$category, ]';
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

  NorthwindServicesCategoryInfoSetCategoryOfProductsInput.fromJson(Map<String, dynamic> json) {
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
  static List<NorthwindServicesCategoryInfoSetCategoryOfProductsInput> listFromJson(List<dynamic> json) {
    return json == null ? <NorthwindServicesCategoryInfoSetCategoryOfProductsInput>[] : json.map((value) => NorthwindServicesCategoryInfoSetCategoryOfProductsInput.fromJson(value)).toList();
  }

  static Map<String, NorthwindServicesCategoryInfoSetCategoryOfProductsInput> mapFromJson(Map<String, dynamic> json) {
    final map = <String, NorthwindServicesCategoryInfoSetCategoryOfProductsInput>{};
    if (json != null && json.isNotEmpty) {
      json.forEach((String key, dynamic value) => map[key] = NorthwindServicesCategoryInfoSetCategoryOfProductsInput.fromJson(value));
    }
    return map;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) {
      return true;
    }

    if (other is NorthwindServicesCategoryInfoSetCategoryOfProductsInput && runtimeType == other.runtimeType) {
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

  NorthwindServicesCategoryInfoSetCategoryOfProductsInput copyWith({
       String identifier,
       NorthwindExternalAPSetCategoryOfAllProductsInputCategory category,
    }) {
    NorthwindServicesCategoryInfoSetCategoryOfProductsInput copy = NorthwindServicesCategoryInfoSetCategoryOfProductsInput();
        copy.identifier = identifier ?? this.identifier;
        copy.category = category ?? this.category?.copyWith();
    return copy;
  }
}

