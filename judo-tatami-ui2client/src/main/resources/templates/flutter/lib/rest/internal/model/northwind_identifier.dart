part of openapi.api;

// northwind__identifier
class NorthwindIdentifier {
  /* ID of instance */
  String identifier;
  NorthwindIdentifier();

  @override
  String toString() {
    return 'NorthwindIdentifier[identifier=$identifier, ]';
  }

  fromJson(Map<String, dynamic> json) {
    if (json == null) return;
  
    {
      final _jsonData = json[r'__identifier'];
      identifier = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName

  }

  NorthwindIdentifier.fromJson(Map<String, dynamic> json) {
    fromJson(json); // allows child classes to call
  }

  Map<String, dynamic> toJson() {

    final json = <String, dynamic>{};
    if (identifier != null) {
        json[r'__identifier'] = LocalApiClient.serialize(identifier);
    }
    return json;
  }
  static List<NorthwindIdentifier> listFromJson(List<dynamic> json) {
    return json == null ? <NorthwindIdentifier>[] : json.map((value) => NorthwindIdentifier.fromJson(value)).toList();
  }

  static Map<String, NorthwindIdentifier> mapFromJson(Map<String, dynamic> json) {
    final map = <String, NorthwindIdentifier>{};
    if (json != null && json.isNotEmpty) {
      json.forEach((String key, dynamic value) => map[key] = NorthwindIdentifier.fromJson(value));
    }
    return map;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) {
      return true;
    }

    if (other is NorthwindIdentifier && runtimeType == other.runtimeType) {
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

  NorthwindIdentifier copyWith({
       String identifier,
    }) {
    NorthwindIdentifier copy = NorthwindIdentifier();
        copy.identifier = identifier ?? this.identifier;
    return copy;
  }
}

