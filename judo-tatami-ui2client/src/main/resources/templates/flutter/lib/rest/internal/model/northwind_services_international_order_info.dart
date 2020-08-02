part of openapi.api;

// northwind_services_InternationalOrderInfo
class NorthwindServicesInternationalOrderInfo {
  
  double exciseTax;
  
  String customsDescription;
  /* ID of Order instance */
  String identifier;
  /* Array of Comment instances */
  List<NorthwindServicesComment> comments = [];
  /* Array of OrderItem instances */
  List<NorthwindServicesOrderItem> items = [];
  
  DateTime orderDate;
  
  String shipperName;
  
  double totalPrice;
  
  double totalWeight;
  NorthwindServicesInternationalOrderInfo();

  @override
  String toString() {
    return 'NorthwindServicesInternationalOrderInfo[exciseTax=$exciseTax, customsDescription=$customsDescription, identifier=$identifier, comments=$comments, items=$items, orderDate=$orderDate, shipperName=$shipperName, totalPrice=$totalPrice, totalWeight=$totalWeight, ]';
  }

  fromJson(Map<String, dynamic> json) {
    if (json == null) return;
  
    {
      final _jsonData = json[r'exciseTax'];
      exciseTax = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName
    {
      final _jsonData = json[r'customsDescription'];
      customsDescription = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName
    {
      final _jsonData = json[r'__identifier'];
      identifier = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName
    {
      final _jsonData = json[r'comments'];
      comments = (_jsonData == null) ? null :
            NorthwindServicesComment.listFromJson(_jsonData);
    } // _jsonFieldName
    {
      final _jsonData = json[r'items'];
      items = (_jsonData == null) ? null :
            NorthwindServicesOrderItem.listFromJson(_jsonData);
    } // _jsonFieldName
    {
      final _jsonData = json[r'orderDate'];
      orderDate = (_jsonData == null) ? null :
        DateTime.parse(_jsonData);
    } // _jsonFieldName
    {
      final _jsonData = json[r'shipperName'];
      shipperName = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName
    {
      final _jsonData = json[r'totalPrice'];
      totalPrice = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName
    {
      final _jsonData = json[r'totalWeight'];
      totalWeight = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName

  }

  NorthwindServicesInternationalOrderInfo.fromJson(Map<String, dynamic> json) {
    fromJson(json); // allows child classes to call
  }

  Map<String, dynamic> toJson() {

    final json = <String, dynamic>{};
    if (exciseTax != null) {
        json[r'exciseTax'] = LocalApiClient.serialize(exciseTax);
    }
    if (customsDescription != null) {
        json[r'customsDescription'] = LocalApiClient.serialize(customsDescription);
    }
    if (identifier != null) {
        json[r'__identifier'] = LocalApiClient.serialize(identifier);
    }
    if (comments != null) {
        json[r'comments'] = LocalApiClient.serialize(comments);
    }
    if (items != null) {
        json[r'items'] = LocalApiClient.serialize(items);
    }
    if (orderDate != null) {
      json[r'orderDate'] = orderDate.toUtc().toIso8601String();
    }
    if (shipperName != null) {
        json[r'shipperName'] = LocalApiClient.serialize(shipperName);
    }
    if (totalPrice != null) {
        json[r'totalPrice'] = LocalApiClient.serialize(totalPrice);
    }
    if (totalWeight != null) {
        json[r'totalWeight'] = LocalApiClient.serialize(totalWeight);
    }
    return json;
  }
  static List<NorthwindServicesInternationalOrderInfo> listFromJson(List<dynamic> json) {
    return json == null ? <NorthwindServicesInternationalOrderInfo>[] : json.map((value) => NorthwindServicesInternationalOrderInfo.fromJson(value)).toList();
  }

  static Map<String, NorthwindServicesInternationalOrderInfo> mapFromJson(Map<String, dynamic> json) {
    final map = <String, NorthwindServicesInternationalOrderInfo>{};
    if (json != null && json.isNotEmpty) {
      json.forEach((String key, dynamic value) => map[key] = NorthwindServicesInternationalOrderInfo.fromJson(value));
    }
    return map;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) {
      return true;
    }

    if (other is NorthwindServicesInternationalOrderInfo && runtimeType == other.runtimeType) {
    return 

     exciseTax == other.exciseTax &&
  

     customsDescription == other.customsDescription &&
  

     identifier == other.identifier &&
  
        const ListEquality().equals(comments, other.comments) &&
    
        const ListEquality().equals(items, other.items) &&
    
          orderDate == other.orderDate &&
    

     shipperName == other.shipperName &&
  

     totalPrice == other.totalPrice &&
  

     totalWeight == other.totalWeight
    ;
    }

    return false;
  }

  @override
  int get hashCode {
    var hashCode = runtimeType.hashCode;

    

    if (exciseTax != null) {
      hashCode = hashCode ^ exciseTax.hashCode;
    }


    if (customsDescription != null) {
      hashCode = hashCode ^ customsDescription.hashCode;
    }


    if (identifier != null) {
      hashCode = hashCode ^ identifier.hashCode;
    }

        hashCode = hashCode ^ const ListEquality().hash(comments);
    
        hashCode = hashCode ^ const ListEquality().hash(items);
    
            if (orderDate != null) {
              hashCode = hashCode ^ orderDate.hashCode;
            }
    

    if (shipperName != null) {
      hashCode = hashCode ^ shipperName.hashCode;
    }


    if (totalPrice != null) {
      hashCode = hashCode ^ totalPrice.hashCode;
    }


    if (totalWeight != null) {
      hashCode = hashCode ^ totalWeight.hashCode;
    }


    return hashCode;
  }

  NorthwindServicesInternationalOrderInfo copyWith({
       double exciseTax,
       String customsDescription,
       String identifier,
       List<NorthwindServicesComment> comments,
       List<NorthwindServicesOrderItem> items,
       DateTime orderDate,
       String shipperName,
       double totalPrice,
       double totalWeight,
    }) {
    NorthwindServicesInternationalOrderInfo copy = NorthwindServicesInternationalOrderInfo();
        copy.exciseTax = exciseTax ?? this.exciseTax;
        copy.customsDescription = customsDescription ?? this.customsDescription;
        copy.identifier = identifier ?? this.identifier;
        {
        var newVal;
        final v = comments ?? this.comments;
          newVal = <NorthwindServicesComment>        []..addAll((v ?? []).map((y) => y.copyWith()).toList())
;
        copy.comments = newVal;
        }
        {
        var newVal;
        final v = items ?? this.items;
          newVal = <NorthwindServicesOrderItem>        []..addAll((v ?? []).map((y) => y.copyWith()).toList())
;
        copy.items = newVal;
        }
        copy.orderDate = orderDate ?? this.orderDate;
        copy.shipperName = shipperName ?? this.shipperName;
        copy.totalPrice = totalPrice ?? this.totalPrice;
        copy.totalWeight = totalWeight ?? this.totalWeight;
    return copy;
  }
}

