part of openapi.api;

// northwind_services_ProductInfo__extended_category
class NorthwindServicesProductInfoExtendedCategory {
  /* ID of Category instance */
  String identifier;
  NorthwindServicesProductInfoExtendedCategory();

  @override
  String toString() {
    return 'NorthwindServicesProductInfoExtendedCategory[identifier=$identifier, ]';
  }

  fromJson(Map<String, dynamic> json) {
    if (json == null) return;
  
    {
      final _jsonData = json[r'__identifier'];
      identifier = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName

  }

  NorthwindServicesProductInfoExtendedCategory.fromJson(Map<String, dynamic> json) {
    fromJson(json); // allows child classes to call
  }

  Map<String, dynamic> toJson() {

    final json = <String, dynamic>{};
    if (identifier != null) {
        json[r'__identifier'] = LocalApiClient.serialize(identifier);
    }
    return json;
  }
  static List<NorthwindServicesProductInfoExtendedCategory> listFromJson(List<dynamic> json) {
    return json == null ? <NorthwindServicesProductInfoExtendedCategory>[] : json.map((value) => NorthwindServicesProductInfoExtendedCategory.fromJson(value)).toList();
  }

  static Map<String, NorthwindServicesProductInfoExtendedCategory> mapFromJson(Map<String, dynamic> json) {
    final map = <String, NorthwindServicesProductInfoExtendedCategory>{};
    if (json != null && json.isNotEmpty) {
      json.forEach((String key, dynamic value) => map[key] = NorthwindServicesProductInfoExtendedCategory.fromJson(value));
    }
    return map;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) {
      return true;
    }

    if (other is NorthwindServicesProductInfoExtendedCategory && runtimeType == other.runtimeType) {
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

  NorthwindServicesProductInfoExtendedCategory copyWith({
       String identifier,
    }) {
    NorthwindServicesProductInfoExtendedCategory copy = NorthwindServicesProductInfoExtendedCategory();
        copy.identifier = identifier ?? this.identifier;
    return copy;
  }
}

