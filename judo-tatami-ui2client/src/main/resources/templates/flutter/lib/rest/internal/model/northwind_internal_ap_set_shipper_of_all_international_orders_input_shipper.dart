part of openapi.api;

// northwind_InternalAP__setShipperOfAllInternationalOrders__input_shipper
class NorthwindInternalAPSetShipperOfAllInternationalOrdersInputShipper {
  /* ID of referenced instance */
  String identifier;
  NorthwindInternalAPSetShipperOfAllInternationalOrdersInputShipper();

  @override
  String toString() {
    return 'NorthwindInternalAPSetShipperOfAllInternationalOrdersInputShipper[identifier=$identifier, ]';
  }

  fromJson(Map<String, dynamic> json) {
    if (json == null) return;
  
    {
      final _jsonData = json[r'__identifier'];
      identifier = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName

  }

  NorthwindInternalAPSetShipperOfAllInternationalOrdersInputShipper.fromJson(Map<String, dynamic> json) {
    fromJson(json); // allows child classes to call
  }

  Map<String, dynamic> toJson() {

    final json = <String, dynamic>{};
    if (identifier != null) {
        json[r'__identifier'] = LocalApiClient.serialize(identifier);
    }
    return json;
  }
  static List<NorthwindInternalAPSetShipperOfAllInternationalOrdersInputShipper> listFromJson(List<dynamic> json) {
    return json == null ? <NorthwindInternalAPSetShipperOfAllInternationalOrdersInputShipper>[] : json.map((value) => NorthwindInternalAPSetShipperOfAllInternationalOrdersInputShipper.fromJson(value)).toList();
  }

  static Map<String, NorthwindInternalAPSetShipperOfAllInternationalOrdersInputShipper> mapFromJson(Map<String, dynamic> json) {
    final map = <String, NorthwindInternalAPSetShipperOfAllInternationalOrdersInputShipper>{};
    if (json != null && json.isNotEmpty) {
      json.forEach((String key, dynamic value) => map[key] = NorthwindInternalAPSetShipperOfAllInternationalOrdersInputShipper.fromJson(value));
    }
    return map;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) {
      return true;
    }

    if (other is NorthwindInternalAPSetShipperOfAllInternationalOrdersInputShipper && runtimeType == other.runtimeType) {
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

  NorthwindInternalAPSetShipperOfAllInternationalOrdersInputShipper copyWith({
       String identifier,
    }) {
    NorthwindInternalAPSetShipperOfAllInternationalOrdersInputShipper copy = NorthwindInternalAPSetShipperOfAllInternationalOrdersInputShipper();
        copy.identifier = identifier ?? this.identifier;
    return copy;
  }
}

