part of openapi.api;

// northwind_services_ShipmentChange
class NorthwindServicesShipmentChange {
  
  String shipperName;
  
  DateTime orderDate;
  NorthwindServicesShipmentChange();

  @override
  String toString() {
    return 'NorthwindServicesShipmentChange[shipperName=$shipperName, orderDate=$orderDate, ]';
  }

  fromJson(Map<String, dynamic> json) {
    if (json == null) return;
  
    {
      final _jsonData = json[r'shipperName'];
      shipperName = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName
    {
      final _jsonData = json[r'orderDate'];
      orderDate = (_jsonData == null) ? null :
        DateTime.parse(_jsonData);
    } // _jsonFieldName

  }

  NorthwindServicesShipmentChange.fromJson(Map<String, dynamic> json) {
    fromJson(json); // allows child classes to call
  }

  Map<String, dynamic> toJson() {

    final json = <String, dynamic>{};
    if (shipperName != null) {
        json[r'shipperName'] = LocalApiClient.serialize(shipperName);
    }
    if (orderDate != null) {
      json[r'orderDate'] = orderDate.toUtc().toIso8601String();
    }
    return json;
  }
  static List<NorthwindServicesShipmentChange> listFromJson(List<dynamic> json) {
    return json == null ? <NorthwindServicesShipmentChange>[] : json.map((value) => NorthwindServicesShipmentChange.fromJson(value)).toList();
  }

  static Map<String, NorthwindServicesShipmentChange> mapFromJson(Map<String, dynamic> json) {
    final map = <String, NorthwindServicesShipmentChange>{};
    if (json != null && json.isNotEmpty) {
      json.forEach((String key, dynamic value) => map[key] = NorthwindServicesShipmentChange.fromJson(value));
    }
    return map;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) {
      return true;
    }

    if (other is NorthwindServicesShipmentChange && runtimeType == other.runtimeType) {
    return 

     shipperName == other.shipperName &&
  
          orderDate == other.orderDate    
    ;
    }

    return false;
  }

  @override
  int get hashCode {
    var hashCode = runtimeType.hashCode;

    

    if (shipperName != null) {
      hashCode = hashCode ^ shipperName.hashCode;
    }

            if (orderDate != null) {
              hashCode = hashCode ^ orderDate.hashCode;
            }
    

    return hashCode;
  }

  NorthwindServicesShipmentChange copyWith({
       String shipperName,
       DateTime orderDate,
    }) {
    NorthwindServicesShipmentChange copy = NorthwindServicesShipmentChange();
        copy.shipperName = shipperName ?? this.shipperName;
        copy.orderDate = orderDate ?? this.orderDate;
    return copy;
  }
}

