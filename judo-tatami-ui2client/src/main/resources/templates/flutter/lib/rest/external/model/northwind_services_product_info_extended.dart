part of openapi.api;

// northwind_services_ProductInfo__extended
class NorthwindServicesProductInfoExtended {
  
  double unitPrice;
  
  double weight;
  /* ID of Product instance */
  String identifier;
  
  NorthwindServicesProductInfoExtendedCategory category;
  
  String productName;
  NorthwindServicesProductInfoExtended();

  @override
  String toString() {
    return 'NorthwindServicesProductInfoExtended[unitPrice=$unitPrice, weight=$weight, identifier=$identifier, category=$category, productName=$productName, ]';
  }

  fromJson(Map<String, dynamic> json) {
    if (json == null) return;
  
    {
      final _jsonData = json[r'unitPrice'];
      unitPrice = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName
    {
      final _jsonData = json[r'weight'];
      weight = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName
    {
      final _jsonData = json[r'__identifier'];
      identifier = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName
    {
      final _jsonData = json[r'category'];
      category = (_jsonData == null) ? null :
        
        NorthwindServicesProductInfoExtendedCategory.fromJson(_jsonData);
    } // _jsonFieldName
    {
      final _jsonData = json[r'productName'];
      productName = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName

  }

  NorthwindServicesProductInfoExtended.fromJson(Map<String, dynamic> json) {
    fromJson(json); // allows child classes to call
  }

  Map<String, dynamic> toJson() {

    final json = <String, dynamic>{};
    if (unitPrice != null) {
        json[r'unitPrice'] = LocalApiClient.serialize(unitPrice);
    }
    if (weight != null) {
        json[r'weight'] = LocalApiClient.serialize(weight);
    }
    if (identifier != null) {
        json[r'__identifier'] = LocalApiClient.serialize(identifier);
    }
    if (category != null) {
        json[r'category'] = LocalApiClient.serialize(category);
    }
    if (productName != null) {
        json[r'productName'] = LocalApiClient.serialize(productName);
    }
    return json;
  }
  static List<NorthwindServicesProductInfoExtended> listFromJson(List<dynamic> json) {
    return json == null ? <NorthwindServicesProductInfoExtended>[] : json.map((value) => NorthwindServicesProductInfoExtended.fromJson(value)).toList();
  }

  static Map<String, NorthwindServicesProductInfoExtended> mapFromJson(Map<String, dynamic> json) {
    final map = <String, NorthwindServicesProductInfoExtended>{};
    if (json != null && json.isNotEmpty) {
      json.forEach((String key, dynamic value) => map[key] = NorthwindServicesProductInfoExtended.fromJson(value));
    }
    return map;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) {
      return true;
    }

    if (other is NorthwindServicesProductInfoExtended && runtimeType == other.runtimeType) {
    return 

     unitPrice == other.unitPrice &&
  

     weight == other.weight &&
  

     identifier == other.identifier &&
  
          category == other.category &&
    

     productName == other.productName
    ;
    }

    return false;
  }

  @override
  int get hashCode {
    var hashCode = runtimeType.hashCode;

    

    if (unitPrice != null) {
      hashCode = hashCode ^ unitPrice.hashCode;
    }


    if (weight != null) {
      hashCode = hashCode ^ weight.hashCode;
    }


    if (identifier != null) {
      hashCode = hashCode ^ identifier.hashCode;
    }

            if (category != null) {
              hashCode = hashCode ^ category.hashCode;
            }
    

    if (productName != null) {
      hashCode = hashCode ^ productName.hashCode;
    }


    return hashCode;
  }

  NorthwindServicesProductInfoExtended copyWith({
       double unitPrice,
       double weight,
       String identifier,
       NorthwindServicesProductInfoExtendedCategory category,
       String productName,
    }) {
    NorthwindServicesProductInfoExtended copy = NorthwindServicesProductInfoExtended();
        copy.unitPrice = unitPrice ?? this.unitPrice;
        copy.weight = weight ?? this.weight;
        copy.identifier = identifier ?? this.identifier;
        copy.category = category ?? this.category?.copyWith();
        copy.productName = productName ?? this.productName;
    return copy;
  }
}

