part of openapi.api;

// northwind_services_ShipperInfo
class NorthwindServicesShipperInfo {
  
  String companyName;
  /* ID of Shipper instance */
  String identifier;
  NorthwindServicesShipperInfo();

  @override
  String toString() {
    return 'NorthwindServicesShipperInfo[companyName=$companyName, identifier=$identifier, ]';
  }

  fromJson(Map<String, dynamic> json) {
    if (json == null) return;
  
    {
      final _jsonData = json[r'companyName'];
      companyName = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName
    {
      final _jsonData = json[r'__identifier'];
      identifier = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName

  }

  NorthwindServicesShipperInfo.fromJson(Map<String, dynamic> json) {
    fromJson(json); // allows child classes to call
  }

  Map<String, dynamic> toJson() {

    final json = <String, dynamic>{};
    if (companyName != null) {
        json[r'companyName'] = LocalApiClient.serialize(companyName);
    }
    if (identifier != null) {
        json[r'__identifier'] = LocalApiClient.serialize(identifier);
    }
    return json;
  }
  static List<NorthwindServicesShipperInfo> listFromJson(List<dynamic> json) {
    return json == null ? <NorthwindServicesShipperInfo>[] : json.map((value) => NorthwindServicesShipperInfo.fromJson(value)).toList();
  }

  static Map<String, NorthwindServicesShipperInfo> mapFromJson(Map<String, dynamic> json) {
    final map = <String, NorthwindServicesShipperInfo>{};
    if (json != null && json.isNotEmpty) {
      json.forEach((String key, dynamic value) => map[key] = NorthwindServicesShipperInfo.fromJson(value));
    }
    return map;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) {
      return true;
    }

    if (other is NorthwindServicesShipperInfo && runtimeType == other.runtimeType) {
    return 

     companyName == other.companyName &&
  

     identifier == other.identifier
    ;
    }

    return false;
  }

  @override
  int get hashCode {
    var hashCode = runtimeType.hashCode;

    

    if (companyName != null) {
      hashCode = hashCode ^ companyName.hashCode;
    }


    if (identifier != null) {
      hashCode = hashCode ^ identifier.hashCode;
    }


    return hashCode;
  }

  NorthwindServicesShipperInfo copyWith({
       String companyName,
       String identifier,
    }) {
    NorthwindServicesShipperInfo copy = NorthwindServicesShipperInfo();
        copy.companyName = companyName ?? this.companyName;
        copy.identifier = identifier ?? this.identifier;
    return copy;
  }
}

