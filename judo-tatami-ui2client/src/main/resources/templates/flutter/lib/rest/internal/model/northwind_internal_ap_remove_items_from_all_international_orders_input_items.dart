part of openapi.api;

// northwind_InternalAP__removeItemsFromAllInternationalOrders__input_items
class NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInputItems {
  /* ID of referenced instance */
  String identifier;
  NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInputItems();

  @override
  String toString() {
    return 'NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInputItems[identifier=$identifier, ]';
  }

  fromJson(Map<String, dynamic> json) {
    if (json == null) return;
  
    {
      final _jsonData = json[r'__identifier'];
      identifier = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName

  }

  NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInputItems.fromJson(Map<String, dynamic> json) {
    fromJson(json); // allows child classes to call
  }

  Map<String, dynamic> toJson() {

    final json = <String, dynamic>{};
    if (identifier != null) {
        json[r'__identifier'] = LocalApiClient.serialize(identifier);
    }
    return json;
  }
  static List<NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInputItems> listFromJson(List<dynamic> json) {
    return json == null ? <NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInputItems>[] : json.map((value) => NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInputItems.fromJson(value)).toList();
  }

  static Map<String, NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInputItems> mapFromJson(Map<String, dynamic> json) {
    final map = <String, NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInputItems>{};
    if (json != null && json.isNotEmpty) {
      json.forEach((String key, dynamic value) => map[key] = NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInputItems.fromJson(value));
    }
    return map;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) {
      return true;
    }

    if (other is NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInputItems && runtimeType == other.runtimeType) {
    return 

     identifier == other.identifier
    ;
    }

    return false;
  }

  @override
  int get hashCode {
    var hashCode = runtimeType.hashCode;

    

    if (identifier != null) {
      hashCode = hashCode ^ identifier.hashCode;
    }


    return hashCode;
  }

  NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInputItems copyWith({
       String identifier,
    }) {
    NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInputItems copy = NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInputItems();
        copy.identifier = identifier ?? this.identifier;
    return copy;
  }
}

