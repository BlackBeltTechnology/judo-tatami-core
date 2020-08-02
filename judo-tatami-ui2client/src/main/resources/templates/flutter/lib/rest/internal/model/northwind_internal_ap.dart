part of openapi.api;

// northwind_InternalAP
class NorthwindInternalAP {
  /* Array of ShipperInfo instances */
  List<NorthwindServicesShipperInfo> allShippers = [];
  /* Array of InternationalOrderInfo instances */
  List<NorthwindServicesInternationalOrderInfo> allInternationalOrders = [];
  NorthwindInternalAP();

  @override
  String toString() {
    return 'NorthwindInternalAP[allShippers=$allShippers, allInternationalOrders=$allInternationalOrders, ]';
  }

  fromJson(Map<String, dynamic> json) {
    if (json == null) return;
  
    {
      final _jsonData = json[r'allShippers'];
      allShippers = (_jsonData == null) ? null :
            NorthwindServicesShipperInfo.listFromJson(_jsonData);
    } // _jsonFieldName
    {
      final _jsonData = json[r'allInternationalOrders'];
      allInternationalOrders = (_jsonData == null) ? null :
            NorthwindServicesInternationalOrderInfo.listFromJson(_jsonData);
    } // _jsonFieldName

  }

  NorthwindInternalAP.fromJson(Map<String, dynamic> json) {
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
  static List<NorthwindInternalAP> listFromJson(List<dynamic> json) {
    return json == null ? <NorthwindInternalAP>[] : json.map((value) => NorthwindInternalAP.fromJson(value)).toList();
  }

  static Map<String, NorthwindInternalAP> mapFromJson(Map<String, dynamic> json) {
    final map = <String, NorthwindInternalAP>{};
    if (json != null && json.isNotEmpty) {
      json.forEach((String key, dynamic value) => map[key] = NorthwindInternalAP.fromJson(value));
    }
    return map;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) {
      return true;
    }

    if (other is NorthwindInternalAP && runtimeType == other.runtimeType) {
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

  NorthwindInternalAP copyWith({
       List<NorthwindServicesShipperInfo> allShippers,
       List<NorthwindServicesInternationalOrderInfo> allInternationalOrders,
    }) {
    NorthwindInternalAP copy = NorthwindInternalAP();
        {
        var newVal;
        final v = allShippers ?? this.allShippers;
          newVal = <NorthwindServicesShipperInfo>        []..addAll((v ?? []).map((y) => y.copyWith()).toList())
;
        copy.allShippers = newVal;
        }
        {
        var newVal;
        final v = allInternationalOrders ?? this.allInternationalOrders;
          newVal = <NorthwindServicesInternationalOrderInfo>        []..addAll((v ?? []).map((y) => y.copyWith()).toList())
;
        copy.allInternationalOrders = newVal;
        }
    return copy;
  }
}

