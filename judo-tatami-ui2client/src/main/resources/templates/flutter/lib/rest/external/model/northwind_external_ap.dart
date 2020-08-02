part of openapi.api;

// northwind_ExternalAP
class NorthwindExternalAP {
  /* Array of CategoryInfo instances */
  List<NorthwindServicesCategoryInfo> allCategories = [];
  /* Array of ProductInfo instances */
  List<NorthwindServicesProductInfo> allProducts = [];
  NorthwindExternalAP();

  @override
  String toString() {
    return 'NorthwindExternalAP[allCategories=$allCategories, allProducts=$allProducts, ]';
  }

  fromJson(Map<String, dynamic> json) {
    if (json == null) return;

    {
      final _jsonData = json[r'allCategories'];
      allCategories = (_jsonData == null)
          ? null
          : NorthwindServicesCategoryInfo.listFromJson(_jsonData);
    } // _jsonFieldName
    {
      final _jsonData = json[r'allProducts'];
      allProducts = (_jsonData == null)
          ? null
          : NorthwindServicesProductInfo.listFromJson(_jsonData);
    } // _jsonFieldName
  }

  NorthwindExternalAP.fromJson(Map<String, dynamic> json) {
    fromJson(json); // allows child classes to call
  }

  Map<String, dynamic> toJson() {
    final json = <String, dynamic>{};
    if (allCategories != null) {
      json[r'allCategories'] = LocalApiClient.serialize(allCategories);
    }
    if (allProducts != null) {
      json[r'allProducts'] = LocalApiClient.serialize(allProducts);
    }
    return json;
  }

  static List<NorthwindExternalAP> listFromJson(List<dynamic> json) {
    return json == null
        ? <NorthwindExternalAP>[]
        : json.map((value) => NorthwindExternalAP.fromJson(value)).toList();
  }

  static Map<String, NorthwindExternalAP> mapFromJson(
      Map<String, dynamic> json) {
    final map = <String, NorthwindExternalAP>{};
    if (json != null && json.isNotEmpty) {
      json.forEach((String key, dynamic value) =>
          map[key] = NorthwindExternalAP.fromJson(value));
    }
    return map;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) {
      return true;
    }

    if (other is NorthwindExternalAP && runtimeType == other.runtimeType) {
      return const ListEquality().equals(allCategories, other.allCategories) &&
          const ListEquality().equals(allProducts, other.allProducts);
    }

    return false;
  }

  @override
  int get hashCode {
    var hashCode = runtimeType.hashCode;

    hashCode = hashCode ^ const ListEquality().hash(allCategories);

    hashCode = hashCode ^ const ListEquality().hash(allProducts);

    return hashCode;
  }

  NorthwindExternalAP copyWith({
    List<NorthwindServicesCategoryInfo> allCategories,
    List<NorthwindServicesProductInfo> allProducts,
  }) {
    NorthwindExternalAP copy = NorthwindExternalAP();
    {
      var newVal;
      final v = allCategories ?? this.allCategories;
      newVal = <NorthwindServicesCategoryInfo>[]
        ..addAll((v ?? []).map((y) => y.copyWith()).toList());
      copy.allCategories = newVal;
    }
    {
      var newVal;
      final v = allProducts ?? this.allProducts;
      newVal = <NorthwindServicesProductInfo>[]
        ..addAll((v ?? []).map((y) => y.copyWith()).toList());
      copy.allProducts = newVal;
    }
    return copy;
  }
}
