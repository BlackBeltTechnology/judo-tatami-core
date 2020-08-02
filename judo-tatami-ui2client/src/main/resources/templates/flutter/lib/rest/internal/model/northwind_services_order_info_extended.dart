part of openapi.api;

// northwind_services_OrderInfo__extended
class NorthwindServicesOrderInfoExtended {
  
  NorthwindServicesOrderInfoExtendedShipper shipper;
  /* Array of Comment instances (for creation) */
  List<NorthwindServicesCommentExtended> comments = [];
  
  double totalPrice;
  
  double totalWeight;
  
  String shipperName;
  /* ID of Order instance */
  String identifier;
  
  NorthwindServicesProductInfoExtendedCategory categories;
  /* Array of OrderItem instances (for creation) */
  List<NorthwindServicesOrderItemExtended> items = [];
  
  DateTime orderDate;
  NorthwindServicesOrderInfoExtended();

  @override
  String toString() {
    return 'NorthwindServicesOrderInfoExtended[shipper=$shipper, comments=$comments, totalPrice=$totalPrice, totalWeight=$totalWeight, shipperName=$shipperName, identifier=$identifier, categories=$categories, items=$items, orderDate=$orderDate, ]';
  }

  fromJson(Map<String, dynamic> json) {
    if (json == null) return;
  
    {
      final _jsonData = json[r'shipper'];
      shipper = (_jsonData == null) ? null :
        
        NorthwindServicesOrderInfoExtendedShipper.fromJson(_jsonData);
    } // _jsonFieldName
    {
      final _jsonData = json[r'comments'];
      comments = (_jsonData == null) ? null :
            NorthwindServicesCommentExtended.listFromJson(_jsonData);
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
      final _jsonData = json[r'categories'];
      categories = (_jsonData == null) ? null :
        
        NorthwindServicesProductInfoExtendedCategory.fromJson(_jsonData);
    } // _jsonFieldName
    {
      final _jsonData = json[r'items'];
      items = (_jsonData == null) ? null :
            NorthwindServicesOrderItemExtended.listFromJson(_jsonData);
    } // _jsonFieldName
    {
      final _jsonData = json[r'orderDate'];
      orderDate = (_jsonData == null) ? null :
        DateTime.parse(_jsonData);
    } // _jsonFieldName

  }

  NorthwindServicesOrderInfoExtended.fromJson(Map<String, dynamic> json) {
    fromJson(json); // allows child classes to call
  }

  Map<String, dynamic> toJson() {

    final json = <String, dynamic>{};
    if (shipper != null) {
        json[r'shipper'] = LocalApiClient.serialize(shipper);
    }
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
    if (categories != null) {
        json[r'categories'] = LocalApiClient.serialize(categories);
    }
    if (items != null) {
        json[r'items'] = LocalApiClient.serialize(items);
    }
    if (orderDate != null) {
      json[r'orderDate'] = orderDate.toUtc().toIso8601String();
    }
    return json;
  }
  static List<NorthwindServicesOrderInfoExtended> listFromJson(List<dynamic> json) {
    return json == null ? <NorthwindServicesOrderInfoExtended>[] : json.map((value) => NorthwindServicesOrderInfoExtended.fromJson(value)).toList();
  }

  static Map<String, NorthwindServicesOrderInfoExtended> mapFromJson(Map<String, dynamic> json) {
    final map = <String, NorthwindServicesOrderInfoExtended>{};
    if (json != null && json.isNotEmpty) {
      json.forEach((String key, dynamic value) => map[key] = NorthwindServicesOrderInfoExtended.fromJson(value));
    }
    return map;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) {
      return true;
    }

    if (other is NorthwindServicesOrderInfoExtended && runtimeType == other.runtimeType) {
    return 
          shipper == other.shipper &&
    
        const ListEquality().equals(comments, other.comments) &&
    

     totalPrice == other.totalPrice &&
  

     totalWeight == other.totalWeight &&
  

     shipperName == other.shipperName &&
  

     identifier == other.identifier &&
  
          categories == other.categories &&
    
        const ListEquality().equals(items, other.items) &&
    
          orderDate == other.orderDate    
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

            if (categories != null) {
              hashCode = hashCode ^ categories.hashCode;
            }
    
        hashCode = hashCode ^ const ListEquality().hash(items);
    
            if (orderDate != null) {
              hashCode = hashCode ^ orderDate.hashCode;
            }
    

    return hashCode;
  }

  NorthwindServicesOrderInfoExtended copyWith({
       NorthwindServicesOrderInfoExtendedShipper shipper,
       List<NorthwindServicesCommentExtended> comments,
       double totalPrice,
       double totalWeight,
       String shipperName,
       String identifier,
       NorthwindServicesProductInfoExtendedCategory categories,
       List<NorthwindServicesOrderItemExtended> items,
       DateTime orderDate,
    }) {
    NorthwindServicesOrderInfoExtended copy = NorthwindServicesOrderInfoExtended();
        copy.shipper = shipper ?? this.shipper?.copyWith();
        {
        var newVal;
        final v = comments ?? this.comments;
          newVal = <NorthwindServicesCommentExtended>        []..addAll((v ?? []).map((y) => y.copyWith()).toList())
;
        copy.comments = newVal;
        }
        copy.totalPrice = totalPrice ?? this.totalPrice;
        copy.totalWeight = totalWeight ?? this.totalWeight;
        copy.shipperName = shipperName ?? this.shipperName;
        copy.identifier = identifier ?? this.identifier;
        copy.categories = categories ?? this.categories?.copyWith();
        {
        var newVal;
        final v = items ?? this.items;
          newVal = <NorthwindServicesOrderItemExtended>        []..addAll((v ?? []).map((y) => y.copyWith()).toList())
;
        copy.items = newVal;
        }
        copy.orderDate = orderDate ?? this.orderDate;
    return copy;
  }
}

