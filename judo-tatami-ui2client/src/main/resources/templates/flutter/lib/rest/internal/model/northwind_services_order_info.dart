part of openapi.api;

// northwind_services_OrderInfo
class NorthwindServicesOrderInfo {
  /* Array of Comment instances */
  List<NorthwindServicesComment> comments = [];
  
  double totalPrice;
  
  double totalWeight;
  
  String shipperName;
  /* ID of Order instance */
  String identifier;
  /* Array of OrderItem instances */
  List<NorthwindServicesOrderItem> items = [];
  
  DateTime orderDate;
  NorthwindServicesOrderInfo();

  @override
  String toString() {
    return 'NorthwindServicesOrderInfo[comments=$comments, totalPrice=$totalPrice, totalWeight=$totalWeight, shipperName=$shipperName, identifier=$identifier, items=$items, orderDate=$orderDate, ]';
  }

  fromJson(Map<String, dynamic> json) {
    if (json == null) return;
  
    {
      final _jsonData = json[r'comments'];
      comments = (_jsonData == null) ? null :
            NorthwindServicesComment.listFromJson(_jsonData);
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
    {
      final _jsonData = json[r'shipperName'];
      shipperName = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName
    {
      final _jsonData = json[r'__identifier'];
      identifier = (_jsonData == null) ? null :
        _jsonData;
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

  }

  NorthwindServicesOrderInfo.fromJson(Map<String, dynamic> json) {
    fromJson(json); // allows child classes to call
  }

  Map<String, dynamic> toJson() {

    final json = <String, dynamic>{};
    if (comments != null) {
        json[r'comments'] = LocalApiClient.serialize(comments);
    }
    if (totalPrice != null) {
        json[r'totalPrice'] = LocalApiClient.serialize(totalPrice);
    }
    if (totalWeight != null) {
        json[r'totalWeight'] = LocalApiClient.serialize(totalWeight);
    }
    if (shipperName != null) {
        json[r'shipperName'] = LocalApiClient.serialize(shipperName);
    }
    if (identifier != null) {
        json[r'__identifier'] = LocalApiClient.serialize(identifier);
    }
    if (items != null) {
        json[r'items'] = LocalApiClient.serialize(items);
    }
    if (orderDate != null) {
      json[r'orderDate'] = orderDate.toUtc().toIso8601String();
    }
    return json;
  }
  static List<NorthwindServicesOrderInfo> listFromJson(List<dynamic> json) {
    return json == null ? <NorthwindServicesOrderInfo>[] : json.map((value) => NorthwindServicesOrderInfo.fromJson(value)).toList();
  }

  static Map<String, NorthwindServicesOrderInfo> mapFromJson(Map<String, dynamic> json) {
    final map = <String, NorthwindServicesOrderInfo>{};
    if (json != null && json.isNotEmpty) {
      json.forEach((String key, dynamic value) => map[key] = NorthwindServicesOrderInfo.fromJson(value));
    }
    return map;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) {
      return true;
    }

    if (other is NorthwindServicesOrderInfo && runtimeType == other.runtimeType) {
    return 
        const ListEquality().equals(comments, other.comments) &&
    

     totalPrice == other.totalPrice &&
  

     totalWeight == other.totalWeight &&
  

     shipperName == other.shipperName &&
  

     identifier == other.identifier &&
  
        const ListEquality().equals(items, other.items) &&
    
          orderDate == other.orderDate    
    ;
    }

    return false;
  }

  @override
  int get hashCode {
    var hashCode = runtimeType.hashCode;

    
        hashCode = hashCode ^ const ListEquality().hash(comments);
    

    if (totalPrice != null) {
      hashCode = hashCode ^ totalPrice.hashCode;
    }


    if (totalWeight != null) {
      hashCode = hashCode ^ totalWeight.hashCode;
    }


    if (shipperName != null) {
      hashCode = hashCode ^ shipperName.hashCode;
    }


    if (identifier != null) {
      hashCode = hashCode ^ identifier.hashCode;
    }

        hashCode = hashCode ^ const ListEquality().hash(items);
    
            if (orderDate != null) {
              hashCode = hashCode ^ orderDate.hashCode;
            }
    

    return hashCode;
  }

  NorthwindServicesOrderInfo copyWith({
       List<NorthwindServicesComment> comments,
       double totalPrice,
       double totalWeight,
       String shipperName,
       String identifier,
       List<NorthwindServicesOrderItem> items,
       DateTime orderDate,
    }) {
    NorthwindServicesOrderInfo copy = NorthwindServicesOrderInfo();
        {
        var newVal;
        final v = comments ?? this.comments;
          newVal = <NorthwindServicesComment>        []..addAll((v ?? []).map((y) => y.copyWith()).toList())
;
        copy.comments = newVal;
        }
        copy.totalPrice = totalPrice ?? this.totalPrice;
        copy.totalWeight = totalWeight ?? this.totalWeight;
        copy.shipperName = shipperName ?? this.shipperName;
        copy.identifier = identifier ?? this.identifier;
        {
        var newVal;
        final v = items ?? this.items;
          newVal = <NorthwindServicesOrderItem>        []..addAll((v ?? []).map((y) => y.copyWith()).toList())
;
        copy.items = newVal;
        }
        copy.orderDate = orderDate ?? this.orderDate;
    return copy;
  }
}

