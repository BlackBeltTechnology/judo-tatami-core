part of openapi.api;

// northwind_services_ProductInfo
class NorthwindServicesProductInfo {
  
  double unitPrice;
  
  double weight;
  /* ID of Product instance */
  String identifier;
  
  String productName;
  NorthwindServicesProductInfo();

  @override
  String toString() {
    return 'NorthwindServicesProductInfo[unitPrice=$unitPrice, weight=$weight, identifier=$identifier, productName=$productName, ]';
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
      final _jsonData = json[r'productName'];
      productName = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName

  }

  NorthwindServicesProductInfo.fromJson(Map<String, dynamic> json) {
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
    if (productName != null) {
        json[r'productName'] = LocalApiClient.serialize(productName);
    }
    return json;
  }
  static List<NorthwindServicesProductInfo> listFromJson(List<dynamic> json) {
    return json == null ? <NorthwindServicesProductInfo>[] : json.map((value) => NorthwindServicesProductInfo.fromJson(value)).toList();
  }

  static Map<String, NorthwindServicesProductInfo> mapFromJson(Map<String, dynamic> json) {
    final map = <String, NorthwindServicesProductInfo>{};
    if (json != null && json.isNotEmpty) {
      json.forEach((String key, dynamic value) => map[key] = NorthwindServicesProductInfo.fromJson(value));
    }
    return map;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) {
      return true;
    }

    if (other is NorthwindServicesProductInfo && runtimeType == other.runtimeType) {
    return 

     unitPrice == other.unitPrice &&
  

     weight == other.weight &&
  

     identifier == other.identifier &&
  

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


    if (productName != null) {
      hashCode = hashCode ^ productName.hashCode;
    }


    return hashCode;
  }

  NorthwindServicesProductInfo copyWith({
       double unitPrice,
       double weight,
       String identifier,
       String productName,
    }) {
    NorthwindServicesProductInfo copy = NorthwindServicesProductInfo();
        copy.unitPrice = unitPrice ?? this.unitPrice;
        copy.weight = weight ?? this.weight;
        copy.identifier = identifier ?? this.identifier;
        copy.productName = productName ?? this.productName;
    return copy;
  }
}

