part of openapi.api;

// northwind_InternalAP__setShipperOfAllInternationalOrders__input
class NorthwindInternalAPSetShipperOfAllInternationalOrdersInput {
  
  NorthwindInternalAPSetShipperOfAllInternationalOrdersInputShipper shipper;
  /* ID of instance */
  String identifier;
  NorthwindInternalAPSetShipperOfAllInternationalOrdersInput();

  @override
  String toString() {
    return 'NorthwindInternalAPSetShipperOfAllInternationalOrdersInput[shipper=$shipper, identifier=$identifier, ]';
  }

  fromJson(Map<String, dynamic> json) {
    if (json == null) return;
  
    {
      final _jsonData = json[r'shipper'];
      shipper = (_jsonData == null) ? null :
        
        NorthwindInternalAPSetShipperOfAllInternationalOrdersInputShipper.fromJson(_jsonData);
    } // _jsonFieldName
    {
      final _jsonData = json[r'__identifier'];
      identifier = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName

  }

  NorthwindInternalAPSetShipperOfAllInternationalOrdersInput.fromJson(Map<String, dynamic> json) {
    fromJson(json); // allows child classes to call
  }

  Map<String, dynamic> toJson() {

    final json = <String, dynamic>{};
    if (shipper != null) {
        json[r'shipper'] = LocalApiClient.serialize(shipper);
    }
    if (identifier != null) {
        json[r'__identifier'] = LocalApiClient.serialize(identifier);
    }
    return json;
  }
  static List<NorthwindInternalAPSetShipperOfAllInternationalOrdersInput> listFromJson(List<dynamic> json) {
    return json == null ? <NorthwindInternalAPSetShipperOfAllInternationalOrdersInput>[] : json.map((value) => NorthwindInternalAPSetShipperOfAllInternationalOrdersInput.fromJson(value)).toList();
  }

  static Map<String, NorthwindInternalAPSetShipperOfAllInternationalOrdersInput> mapFromJson(Map<String, dynamic> json) {
    final map = <String, NorthwindInternalAPSetShipperOfAllInternationalOrdersInput>{};
    if (json != null && json.isNotEmpty) {
      json.forEach((String key, dynamic value) => map[key] = NorthwindInternalAPSetShipperOfAllInternationalOrdersInput.fromJson(value));
    }
    return map;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) {
      return true;
    }

    if (other is NorthwindInternalAPSetShipperOfAllInternationalOrdersInput && runtimeType == other.runtimeType) {
    return 
          shipper == other.shipper &&
    

     identifier == other.identifier
    ;
    }

    return false;
  }

  @override
  int get hashCode {
    var hashCode = runtimeType.hashCode;

    
            if (shipper != null) {
              hashCode = hashCode ^ shipper.hashCode;
            }
    

    if (identifier != null) {
      hashCode = hashCode ^ identifier.hashCode;
    }


    return hashCode;
  }

  NorthwindInternalAPSetShipperOfAllInternationalOrdersInput copyWith({
       NorthwindInternalAPSetShipperOfAllInternationalOrdersInputShipper shipper,
       String identifier,
    }) {
    NorthwindInternalAPSetShipperOfAllInternationalOrdersInput copy = NorthwindInternalAPSetShipperOfAllInternationalOrdersInput();
        copy.shipper = shipper ?? this.shipper?.copyWith();
        copy.identifier = identifier ?? this.identifier;
    return copy;
  }
}

