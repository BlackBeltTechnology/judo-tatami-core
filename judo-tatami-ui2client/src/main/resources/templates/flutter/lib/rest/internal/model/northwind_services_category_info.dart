part of openapi.api;

// northwind_services_CategoryInfo
class NorthwindServicesCategoryInfo {
  /* ID of Category instance */
  String identifier;
  
  String categoryName;
  NorthwindServicesCategoryInfo();

  @override
  String toString() {
    return 'NorthwindServicesCategoryInfo[identifier=$identifier, categoryName=$categoryName, ]';
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

  }

  NorthwindServicesCategoryInfo.fromJson(Map<String, dynamic> json) {
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
    return json;
  }
  static List<NorthwindServicesCategoryInfo> listFromJson(List<dynamic> json) {
    return json == null ? <NorthwindServicesCategoryInfo>[] : json.map((value) => NorthwindServicesCategoryInfo.fromJson(value)).toList();
  }

  static Map<String, NorthwindServicesCategoryInfo> mapFromJson(Map<String, dynamic> json) {
    final map = <String, NorthwindServicesCategoryInfo>{};
    if (json != null && json.isNotEmpty) {
      json.forEach((String key, dynamic value) => map[key] = NorthwindServicesCategoryInfo.fromJson(value));
    }
    return map;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) {
      return true;
    }

    if (other is NorthwindServicesCategoryInfo && runtimeType == other.runtimeType) {
    return 

     identifier == other.identifier &&
  

     categoryName == other.categoryName
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


    return hashCode;
  }

  NorthwindServicesCategoryInfo copyWith({
       String identifier,
       String categoryName,
    }) {
    NorthwindServicesCategoryInfo copy = NorthwindServicesCategoryInfo();
        copy.identifier = identifier ?? this.identifier;
        copy.categoryName = categoryName ?? this.categoryName;
    return copy;
  }
}

