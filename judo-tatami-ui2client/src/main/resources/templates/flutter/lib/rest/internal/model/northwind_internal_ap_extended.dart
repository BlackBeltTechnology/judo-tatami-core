part of openapi.api;

// northwind_InternalAP__extended
class NorthwindInternalAPExtended {
  /* Array of ShipperInfo instances (for creation) */
  List<NorthwindServicesShipperInfoExtended> allShippers = [];
  /* Array of InternationalOrderInfo instances (for creation) */
  List<NorthwindServicesInternationalOrderInfoExtended> allInternationalOrders = [];
  NorthwindInternalAPExtended();

  @override
  String toString() {
    return 'NorthwindInternalAPExtended[allShippers=$allShippers, allInternationalOrders=$allInternationalOrders, ]';
  }

  fromJson(Map<String, dynamic> json) {
    if (json == null) return;
  
    {
      final _jsonData = json[r'allShippers'];
      allShippers = (_jsonData == null) ? null :
            NorthwindServicesShipperInfoExtended.listFromJson(_jsonData);
    } // _jsonFieldName
    {
      final _jsonData = json[r'allInternationalOrders'];
      allInternationalOrders = (_jsonData == null) ? null :
            NorthwindServicesInternationalOrderInfoExtended.listFromJson(_jsonData);
    } // _jsonFieldName

  }

  NorthwindInternalAPExtended.fromJson(Map<String, dynamic> json) {
    fromJson(json); // allows child classes to call
  }

  Map<String, dynamic> toJson() {

    final json = <String, dynamic>{};
    if (allShippers != null) {
        json[r'allShippers'] = LocalApiClient.serialize(allShippers);
    }
    if (allInternationalOrders != null) {
        json[r'allInternationalOrders'] = LocalApiClient.serialize(allInternationalOrders);
    }
    return json;
  }
  static List<NorthwindInternalAPExtended> listFromJson(List<dynamic> json) {
    return json == null ? <NorthwindInternalAPExtended>[] : json.map((value) => NorthwindInternalAPExtended.fromJson(value)).toList();
  }

  static Map<String, NorthwindInternalAPExtended> mapFromJson(Map<String, dynamic> json) {
    final map = <String, NorthwindInternalAPExtended>{};
    if (json != null && json.isNotEmpty) {
      json.forEach((String key, dynamic value) => map[key] = NorthwindInternalAPExtended.fromJson(value));
    }
    return map;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) {
      return true;
    }

    if (other is NorthwindInternalAPExtended && runtimeType == other.runtimeType) {
    return 
        const ListEquality().equals(allShippers, other.allShippers) &&
    
        const ListEquality().equals(allInternationalOrders, other.allInternationalOrders)    
    ;
    }

    return false;
  }

  @override
  int get hashCode {
    var hashCode = runtimeType.hashCode;

    
        hashCode = hashCode ^ const ListEquality().hash(allShippers);
    
        hashCode = hashCode ^ const ListEquality().hash(allInternationalOrders);
    

    return hashCode;
  }

  NorthwindInternalAPExtended copyWith({
       List<NorthwindServicesShipperInfoExtended> allShippers,
       List<NorthwindServicesInternationalOrderInfoExtended> allInternationalOrders,
    }) {
    NorthwindInternalAPExtended copy = NorthwindInternalAPExtended();
        {
        var newVal;
        final v = allShippers ?? this.allShippers;
          newVal = <NorthwindServicesShipperInfoExtended>        []..addAll((v ?? []).map((y) => y.copyWith()).toList())
;
        copy.allShippers = newVal;
        }
        {
        var newVal;
        final v = allInternationalOrders ?? this.allInternationalOrders;
          newVal = <NorthwindServicesInternationalOrderInfoExtended>        []..addAll((v ?? []).map((y) => y.copyWith()).toList())
;
        copy.allInternationalOrders = newVal;
        }
    return copy;
  }
}

