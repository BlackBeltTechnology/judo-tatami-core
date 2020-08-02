part of openapi.api;

// northwind_services_OrderInfo__setProductOfItems__input
class NorthwindServicesOrderInfoSetProductOfItemsInput {
  
  NorthwindInternalAPSetShipperOfAllInternationalOrdersInputShipper product;
  /* ID of instance */
  String identifier;
  NorthwindServicesOrderInfoSetProductOfItemsInput();

  @override
  String toString() {
    return 'NorthwindServicesOrderInfoSetProductOfItemsInput[product=$product, identifier=$identifier, ]';
  }

  fromJson(Map<String, dynamic> json) {
    if (json == null) return;
  
    {
      final _jsonData = json[r'product'];
      product = (_jsonData == null) ? null :
        
        NorthwindInternalAPSetShipperOfAllInternationalOrdersInputShipper.fromJson(_jsonData);
    } // _jsonFieldName
    {
      final _jsonData = json[r'__identifier'];
      identifier = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName

  }

  NorthwindServicesOrderInfoSetProductOfItemsInput.fromJson(Map<String, dynamic> json) {
    fromJson(json); // allows child classes to call
  }

  Map<String, dynamic> toJson() {

    final json = <String, dynamic>{};
    if (product != null) {
        json[r'product'] = LocalApiClient.serialize(product);
    }
    if (identifier != null) {
        json[r'__identifier'] = LocalApiClient.serialize(identifier);
    }
    return json;
  }
  static List<NorthwindServicesOrderInfoSetProductOfItemsInput> listFromJson(List<dynamic> json) {
    return json == null ? <NorthwindServicesOrderInfoSetProductOfItemsInput>[] : json.map((value) => NorthwindServicesOrderInfoSetProductOfItemsInput.fromJson(value)).toList();
  }

  static Map<String, NorthwindServicesOrderInfoSetProductOfItemsInput> mapFromJson(Map<String, dynamic> json) {
    final map = <String, NorthwindServicesOrderInfoSetProductOfItemsInput>{};
    if (json != null && json.isNotEmpty) {
      json.forEach((String key, dynamic value) => map[key] = NorthwindServicesOrderInfoSetProductOfItemsInput.fromJson(value));
    }
    return map;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) {
      return true;
    }

    if (other is NorthwindServicesOrderInfoSetProductOfItemsInput && runtimeType == other.runtimeType) {
    return 
          product == other.product &&
    

     identifier == other.identifier
    ;
    }

    return false;
  }

  @override
  int get hashCode {
    var hashCode = runtimeType.hashCode;

    
            if (product != null) {
              hashCode = hashCode ^ product.hashCode;
            }
    

    if (identifier != null) {
      hashCode = hashCode ^ identifier.hashCode;
    }


    return hashCode;
  }

  NorthwindServicesOrderInfoSetProductOfItemsInput copyWith({
       NorthwindInternalAPSetShipperOfAllInternationalOrdersInputShipper product,
       String identifier,
    }) {
    NorthwindServicesOrderInfoSetProductOfItemsInput copy = NorthwindServicesOrderInfoSetProductOfItemsInput();
        copy.product = product ?? this.product?.copyWith();
        copy.identifier = identifier ?? this.identifier;
    return copy;
  }
}

