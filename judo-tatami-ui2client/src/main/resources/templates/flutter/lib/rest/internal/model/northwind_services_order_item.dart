part of openapi.api;

// northwind_services_OrderItem
class NorthwindServicesOrderItem {
  
  double unitPrice;
  
  int quantity;
  
  double price;
  
  double discount;
  /* ID of OrderDetail instance */
  String identifier;
  
  String categoryName;
  
  String productName;
  NorthwindServicesOrderItem();

  @override
  String toString() {
    return 'NorthwindServicesOrderItem[unitPrice=$unitPrice, quantity=$quantity, price=$price, discount=$discount, identifier=$identifier, categoryName=$categoryName, productName=$productName, ]';
  }

  fromJson(Map<String, dynamic> json) {
    if (json == null) return;
  
    {
      final _jsonData = json[r'unitPrice'];
      unitPrice = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName
    {
      final _jsonData = json[r'quantity'];
      quantity = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName
    {
      final _jsonData = json[r'price'];
      price = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName
    {
      final _jsonData = json[r'discount'];
      discount = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName
    {
      final _jsonData = json[r'__identifier'];
      identifier = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName
    {
      final _jsonData = json[r'categoryName'];
      categoryName = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName
    {
      final _jsonData = json[r'productName'];
      productName = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName

  }

  NorthwindServicesOrderItem.fromJson(Map<String, dynamic> json) {
    fromJson(json); // allows child classes to call
  }

  Map<String, dynamic> toJson() {

    final json = <String, dynamic>{};
    if (unitPrice != null) {
        json[r'unitPrice'] = LocalApiClient.serialize(unitPrice);
    }
    if (quantity != null) {
        json[r'quantity'] = LocalApiClient.serialize(quantity);
    }
    if (price != null) {
        json[r'price'] = LocalApiClient.serialize(price);
    }
    if (discount != null) {
        json[r'discount'] = LocalApiClient.serialize(discount);
    }
    if (identifier != null) {
        json[r'__identifier'] = LocalApiClient.serialize(identifier);
    }
    if (categoryName != null) {
        json[r'categoryName'] = LocalApiClient.serialize(categoryName);
    }
    if (productName != null) {
        json[r'productName'] = LocalApiClient.serialize(productName);
    }
    return json;
  }
  static List<NorthwindServicesOrderItem> listFromJson(List<dynamic> json) {
    return json == null ? <NorthwindServicesOrderItem>[] : json.map((value) => NorthwindServicesOrderItem.fromJson(value)).toList();
  }

  static Map<String, NorthwindServicesOrderItem> mapFromJson(Map<String, dynamic> json) {
    final map = <String, NorthwindServicesOrderItem>{};
    if (json != null && json.isNotEmpty) {
      json.forEach((String key, dynamic value) => map[key] = NorthwindServicesOrderItem.fromJson(value));
    }
    return map;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) {
      return true;
    }

    if (other is NorthwindServicesOrderItem && runtimeType == other.runtimeType) {
    return 

     unitPrice == other.unitPrice &&
  

     quantity == other.quantity &&
  

     price == other.price &&
  

     discount == other.discount &&
  

     identifier == other.identifier &&
  

     categoryName == other.categoryName &&
  

     productName == other.productName
    ;
    }

    return false;
  }

  @override
  int get hashCode {
    var hashCode = runtimeType.hashCode;

    

    if (unitPrice != null) {
      hashCode = hashCode ^ unitPrice.hashCode;
    }


    if (quantity != null) {
      hashCode = hashCode ^ quantity.hashCode;
    }


    if (price != null) {
      hashCode = hashCode ^ price.hashCode;
    }


    if (discount != null) {
      hashCode = hashCode ^ discount.hashCode;
    }


    if (identifier != null) {
      hashCode = hashCode ^ identifier.hashCode;
    }


    if (categoryName != null) {
      hashCode = hashCode ^ categoryName.hashCode;
    }


    if (productName != null) {
      hashCode = hashCode ^ productName.hashCode;
    }


    return hashCode;
  }

  NorthwindServicesOrderItem copyWith({
       double unitPrice,
       int quantity,
       double price,
       double discount,
       String identifier,
       String categoryName,
       String productName,
    }) {
    NorthwindServicesOrderItem copy = NorthwindServicesOrderItem();
        copy.unitPrice = unitPrice ?? this.unitPrice;
        copy.quantity = quantity ?? this.quantity;
        copy.price = price ?? this.price;
        copy.discount = discount ?? this.discount;
        copy.identifier = identifier ?? this.identifier;
        copy.categoryName = categoryName ?? this.categoryName;
        copy.productName = productName ?? this.productName;
    return copy;
  }
}

