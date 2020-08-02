part of openapi.api;

// northwind_services_InternationalOrderInfo__extended
class NorthwindServicesInternationalOrderInfoExtended {
  
  double exciseTax;
  
  String customsDescription;
  /* ID of Order instance */
  String identifier;
  
  NorthwindServicesProductInfoExtendedCategory categories;
  /* Array of Comment instances (for creation) */
  List<NorthwindServicesCommentExtended> comments = [];
  /* Array of OrderItem instances (for creation) */
  List<NorthwindServicesOrderItemExtended> items = [];
  
  DateTime orderDate;
  
  NorthwindServicesOrderInfoExtendedShipper shipper;
  
  String shipperName;
  
  double totalPrice;
  
  double totalWeight;
  NorthwindServicesInternationalOrderInfoExtended();

  @override
  String toString() {
    return 'NorthwindServicesInternationalOrderInfoExtended[exciseTax=$exciseTax, customsDescription=$customsDescription, identifier=$identifier, categories=$categories, comments=$comments, items=$items, orderDate=$orderDate, shipper=$shipper, shipperName=$shipperName, totalPrice=$totalPrice, totalWeight=$totalWeight, ]';
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
      final _jsonData = json[r'categories'];
      categories = (_jsonData == null) ? null :
        
        NorthwindServicesProductInfoExtendedCategory.fromJson(_jsonData);
    } // _jsonFieldName
    {
      final _jsonData = json[r'comments'];
      comments = (_jsonData == null) ? null :
            NorthwindServicesCommentExtended.listFromJson(_jsonData);
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
    {
      final _jsonData = json[r'shipper'];
      shipper = (_jsonData == null) ? null :
        
        NorthwindServicesOrderInfoExtendedShipper.fromJson(_jsonData);
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

  NorthwindServicesInternationalOrderInfoExtended.fromJson(Map<String, dynamic> json) {
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
    if (categories != null) {
        json[r'categories'] = LocalApiClient.serialize(categories);
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
    if (shipper != null) {
        json[r'shipper'] = LocalApiClient.serialize(shipper);
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
  static List<NorthwindServicesInternationalOrderInfoExtended> listFromJson(List<dynamic> json) {
    return json == null ? <NorthwindServicesInternationalOrderInfoExtended>[] : json.map((value) => NorthwindServicesInternationalOrderInfoExtended.fromJson(value)).toList();
  }

  static Map<String, NorthwindServicesInternationalOrderInfoExtended> mapFromJson(Map<String, dynamic> json) {
    final map = <String, NorthwindServicesInternationalOrderInfoExtended>{};
    if (json != null && json.isNotEmpty) {
      json.forEach((String key, dynamic value) => map[key] = NorthwindServicesInternationalOrderInfoExtended.fromJson(value));
    }
    return map;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) {
      return true;
    }

    if (other is NorthwindServicesInternationalOrderInfoExtended && runtimeType == other.runtimeType) {
    return 

     exciseTax == other.exciseTax &&
  

     customsDescription == other.customsDescription &&
  

     identifier == other.identifier &&
  
          categories == other.categories &&
    
        const ListEquality().equals(comments, other.comments) &&
    
        const ListEquality().equals(items, other.items) &&
    
          orderDate == other.orderDate &&
    
          shipper == other.shipper &&
    

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

            if (categories != null) {
              hashCode = hashCode ^ categories.hashCode;
            }
    
        hashCode = hashCode ^ const ListEquality().hash(comments);
    
        hashCode = hashCode ^ const ListEquality().hash(items);
    
            if (orderDate != null) {
              hashCode = hashCode ^ orderDate.hashCode;
            }
    
            if (shipper != null) {
              hashCode = hashCode ^ shipper.hashCode;
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

  NorthwindServicesInternationalOrderInfoExtended copyWith({
       double exciseTax,
       String customsDescription,
       String identifier,
       NorthwindServicesProductInfoExtendedCategory categories,
       List<NorthwindServicesCommentExtended> comments,
       List<NorthwindServicesOrderItemExtended> items,
       DateTime orderDate,
       NorthwindServicesOrderInfoExtendedShipper shipper,
       String shipperName,
       double totalPrice,
       double totalWeight,
    }) {
    NorthwindServicesInternationalOrderInfoExtended copy = NorthwindServicesInternationalOrderInfoExtended();
        copy.exciseTax = exciseTax ?? this.exciseTax;
        copy.customsDescription = customsDescription ?? this.customsDescription;
        copy.identifier = identifier ?? this.identifier;
        copy.categories = categories ?? this.categories?.copyWith();
        {
        var newVal;
        final v = comments ?? this.comments;
          newVal = <NorthwindServicesCommentExtended>        []..addAll((v ?? []).map((y) => y.copyWith()).toList())
;
        copy.comments = newVal;
        }
        {
        var newVal;
        final v = items ?? this.items;
          newVal = <NorthwindServicesOrderItemExtended>        []..addAll((v ?? []).map((y) => y.copyWith()).toList())
;
        copy.items = newVal;
        }
        copy.orderDate = orderDate ?? this.orderDate;
        copy.shipper = shipper ?? this.shipper?.copyWith();
        copy.shipperName = shipperName ?? this.shipperName;
        copy.totalPrice = totalPrice ?? this.totalPrice;
        copy.totalWeight = totalWeight ?? this.totalWeight;
    return copy;
  }
}

