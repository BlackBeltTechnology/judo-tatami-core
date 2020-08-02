part of openapi.api;

// northwind_InternalAP__removeItemsFromAllInternationalOrders__input
class NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInput {
  /* ID of instance */
  String identifier;
  /* Referenced element(s) */
  List<NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInputItems> items = [];
  NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInput();

  @override
  String toString() {
    return 'NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInput[identifier=$identifier, items=$items, ]';
  }

  fromJson(Map<String, dynamic> json) {
    if (json == null) return;
  
    {
      final _jsonData = json[r'__identifier'];
      identifier = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName
    {
      final _jsonData = json[r'items'];
      items = (_jsonData == null) ? null :
            NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInputItems.listFromJson(_jsonData);
    } // _jsonFieldName

  }

  NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInput.fromJson(Map<String, dynamic> json) {
    fromJson(json); // allows child classes to call
  }

  Map<String, dynamic> toJson() {

    final json = <String, dynamic>{};
    if (identifier != null) {
        json[r'__identifier'] = LocalApiClient.serialize(identifier);
    }
    if (items != null) {
        json[r'items'] = LocalApiClient.serialize(items);
    }
    return json;
  }
  static List<NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInput> listFromJson(List<dynamic> json) {
    return json == null ? <NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInput>[] : json.map((value) => NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInput.fromJson(value)).toList();
  }

  static Map<String, NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInput> mapFromJson(Map<String, dynamic> json) {
    final map = <String, NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInput>{};
    if (json != null && json.isNotEmpty) {
      json.forEach((String key, dynamic value) => map[key] = NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInput.fromJson(value));
    }
    return map;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) {
      return true;
    }

    if (other is NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInput && runtimeType == other.runtimeType) {
    return 

     identifier == other.identifier &&
  
        const ListEquality().equals(items, other.items)    
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

        hashCode = hashCode ^ const ListEquality().hash(items);
    

    return hashCode;
  }

  NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInput copyWith({
       String identifier,
       List<NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInputItems> items,
    }) {
    NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInput copy = NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInput();
        copy.identifier = identifier ?? this.identifier;
        {
        var newVal;
        final v = items ?? this.items;
          newVal = <NorthwindInternalAPRemoveItemsFromAllInternationalOrdersInputItems>        []..addAll((v ?? []).map((y) => y.copyWith()).toList())
;
        copy.items = newVal;
        }
    return copy;
  }
}

