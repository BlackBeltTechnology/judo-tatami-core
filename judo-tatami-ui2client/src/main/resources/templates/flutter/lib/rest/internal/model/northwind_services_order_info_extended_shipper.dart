part of openapi.api;

// northwind_services_OrderInfo__extended_shipper
class NorthwindServicesOrderInfoExtendedShipper {
  /* ID of Shipper instance */
  String identifier;
  NorthwindServicesOrderInfoExtendedShipper();

  @override
  String toString() {
    return 'NorthwindServicesOrderInfoExtendedShipper[identifier=$identifier, ]';
  }

  fromJson(Map<String, dynamic> json) {
    if (json == null) return;
  
    {
      final _jsonData = json[r'__identifier'];
      identifier = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName

  }

  NorthwindServicesOrderInfoExtendedShipper.fromJson(Map<String, dynamic> json) {
    fromJson(json); // allows child classes to call
  }

  Map<String, dynamic> toJson() {

    final json = <String, dynamic>{};
    if (identifier != null) {
        json[r'__identifier'] = LocalApiClient.serialize(identifier);
    }
    return json;
  }
  static List<NorthwindServicesOrderInfoExtendedShipper> listFromJson(List<dynamic> json) {
    return json == null ? <NorthwindServicesOrderInfoExtendedShipper>[] : json.map((value) => NorthwindServicesOrderInfoExtendedShipper.fromJson(value)).toList();
  }

  static Map<String, NorthwindServicesOrderInfoExtendedShipper> mapFromJson(Map<String, dynamic> json) {
    final map = <String, NorthwindServicesOrderInfoExtendedShipper>{};
    if (json != null && json.isNotEmpty) {
      json.forEach((String key, dynamic value) => map[key] = NorthwindServicesOrderInfoExtendedShipper.fromJson(value));
    }
    return map;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) {
      return true;
    }

    if (other is NorthwindServicesOrderInfoExtendedShipper && runtimeType == other.runtimeType) {
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

  NorthwindServicesOrderInfoExtendedShipper copyWith({
       String identifier,
    }) {
    NorthwindServicesOrderInfoExtendedShipper copy = NorthwindServicesOrderInfoExtendedShipper();
        copy.identifier = identifier ?? this.identifier;
    return copy;
  }
}

