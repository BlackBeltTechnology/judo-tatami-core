part of openapi.api;

// northwind_services_OrderItem__extended
class NorthwindServicesOrderItemExtended {
  
  double unitPrice;
  
  NorthwindServicesCategoryInfoExtendedProducts product;
  
  int quantity;
  
  double price;
  
  double discount;
  /* ID of OrderDetail instance */
  String identifier;
  
  NorthwindServicesProductInfoExtendedCategory category;
  
  String categoryName;
  
  String productName;
  NorthwindServicesOrderItemExtended();

  @override
  String toString() {
    return 'NorthwindServicesOrderItemExtended[unitPrice=$unitPrice, product=$product, quantity=$quantity, price=$price, discount=$discount, identifier=$identifier, category=$category, categoryName=$categoryName, productName=$productName, ]';
  }

  fromJson(Map<String, dynamic> json) {
    if (json == null) return;
  
    {
      final _jsonData = json[r'unitPrice'];
      unitPrice = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName
    {
      final _jsonData = json[r'product'];
      product = (_jsonData == null) ? null :
        
        NorthwindServicesCategoryInfoExtendedProducts.fromJson(_jsonData);
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
      final _jsonData = json[r'category'];
      category = (_jsonData == null) ? null :
        
        NorthwindServicesProductInfoExtendedCategory.fromJson(_jsonData);
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

  NorthwindServicesOrderItemExtended.fromJson(Map<String, dynamic> json) {
    fromJson(json); // allows child classes to call
  }

  Map<String, dynamic> toJson() {

    final json = <String, dynamic>{};
    if (unitPrice != null) {
        json[r'unitPrice'] = LocalApiClient.serialize(unitPrice);
    }
    if (product != null) {
        json[r'product'] = LocalApiClient.serialize(product);
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
    if (category != null) {
        json[r'category'] = LocalApiClient.serialize(category);
    }
    if (categoryName != null) {
        json[r'categoryName'] = LocalApiClient.serialize(categoryName);
    }
    if (productName != null) {
        json[r'productName'] = LocalApiClient.serialize(productName);
    }
    return json;
  }
  static List<NorthwindServicesOrderItemExtended> listFromJson(List<dynamic> json) {
    return json == null ? <NorthwindServicesOrderItemExtended>[] : json.map((value) => NorthwindServicesOrderItemExtended.fromJson(value)).toList();
  }

  static Map<String, NorthwindServicesOrderItemExtended> mapFromJson(Map<String, dynamic> json) {
    final map = <String, NorthwindServicesOrderItemExtended>{};
    if (json != null && json.isNotEmpty) {
      json.forEach((String key, dynamic value) => map[key] = NorthwindServicesOrderItemExtended.fromJson(value));
    }
    return map;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) {
      return true;
    }

    if (other is NorthwindServicesOrderItemExtended && runtimeType == other.runtimeType) {
    return 

     unitPrice == other.unitPrice &&
  
          product == other.product &&
    

     quantity == other.quantity &&
  

     price == other.price &&
  

     discount == other.discount &&
  

     identifier == other.identifier &&
  
          category == other.category &&
    

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

            if (product != null) {
              hashCode = hashCode ^ product.hashCode;
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

            if (category != null) {
              hashCode = hashCode ^ category.hashCode;
            }
    

    if (categoryName != null) {
      hashCode = hashCode ^ categoryName.hashCode;
    }


    if (productName != null) {
      hashCode = hashCode ^ productName.hashCode;
    }


    return hashCode;
  }

  NorthwindServicesOrderItemExtended copyWith({
       double unitPrice,
       NorthwindServicesCategoryInfoExtendedProducts product,
       int quantity,
       double price,
       double discount,
       String identifier,
       NorthwindServicesProductInfoExtendedCategory category,
       String categoryName,
       String productName,
    }) {
    NorthwindServicesOrderItemExtended copy = NorthwindServicesOrderItemExtended();
        copy.unitPrice = unitPrice ?? this.unitPrice;
        copy.product = product ?? this.product?.copyWith();
        copy.quantity = quantity ?? this.quantity;
        copy.price = price ?? this.price;
        copy.discount = discount ?? this.discount;
        copy.identifier = identifier ?? this.identifier;
        copy.category = category ?? this.category?.copyWith();
        copy.categoryName = categoryName ?? this.categoryName;
        copy.productName = productName ?? this.productName;
    return copy;
  }
}

