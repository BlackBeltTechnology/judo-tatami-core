part of openapi.api;

// northwind_services_Comment__extended
class NorthwindServicesCommentExtended {
  
  String note;
  
  String author;
  
  DateTime timestamp;
  NorthwindServicesCommentExtended();

  @override
  String toString() {
    return 'NorthwindServicesCommentExtended[note=$note, author=$author, timestamp=$timestamp, ]';
  }

  fromJson(Map<String, dynamic> json) {
    if (json == null) return;
  
    {
      final _jsonData = json[r'note'];
      note = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName
    {
      final _jsonData = json[r'author'];
      author = (_jsonData == null) ? null :
        _jsonData;
    } // _jsonFieldName
    {
      final _jsonData = json[r'timestamp'];
      timestamp = (_jsonData == null) ? null :
        DateTime.parse(_jsonData);
    } // _jsonFieldName

  }

  NorthwindServicesCommentExtended.fromJson(Map<String, dynamic> json) {
    fromJson(json); // allows child classes to call
  }

  Map<String, dynamic> toJson() {

    final json = <String, dynamic>{};
    if (note != null) {
        json[r'note'] = LocalApiClient.serialize(note);
    }
    if (author != null) {
        json[r'author'] = LocalApiClient.serialize(author);
    }
    if (timestamp != null) {
      json[r'timestamp'] = timestamp.toUtc().toIso8601String();
    }
    return json;
  }
  static List<NorthwindServicesCommentExtended> listFromJson(List<dynamic> json) {
    return json == null ? <NorthwindServicesCommentExtended>[] : json.map((value) => NorthwindServicesCommentExtended.fromJson(value)).toList();
  }

  static Map<String, NorthwindServicesCommentExtended> mapFromJson(Map<String, dynamic> json) {
    final map = <String, NorthwindServicesCommentExtended>{};
    if (json != null && json.isNotEmpty) {
      json.forEach((String key, dynamic value) => map[key] = NorthwindServicesCommentExtended.fromJson(value));
    }
    return map;
  }

  @override
  bool operator ==(Object other) {
    if (identical(this, other)) {
      return true;
    }

    if (other is NorthwindServicesCommentExtended && runtimeType == other.runtimeType) {
    return 

     note == other.note &&
  

     author == other.author &&
  
          timestamp == other.timestamp    
    ;
    }

    return false;
  }

  @override
  int get hashCode {
    var hashCode = runtimeType.hashCode;

    

    if (note != null) {
      hashCode = hashCode ^ note.hashCode;
    }


    if (author != null) {
      hashCode = hashCode ^ author.hashCode;
    }

            if (timestamp != null) {
              hashCode = hashCode ^ timestamp.hashCode;
            }
    

    return hashCode;
  }

  NorthwindServicesCommentExtended copyWith({
       String note,
       String author,
       DateTime timestamp,
    }) {
    NorthwindServicesCommentExtended copy = NorthwindServicesCommentExtended();
        copy.note = note ?? this.note;
        copy.author = author ?? this.author;
        copy.timestamp = timestamp ?? this.timestamp;
    return copy;
  }
}

