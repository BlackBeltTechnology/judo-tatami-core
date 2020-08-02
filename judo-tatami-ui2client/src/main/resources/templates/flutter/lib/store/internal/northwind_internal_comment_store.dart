import 'package:mobx/mobx.dart';

part 'northwind_internal_comment_store.g.dart';

class NorthwindInternalCommentStore extends _NorthwindInternalCommentStore
    with _$NorthwindInternalCommentStore {
  NorthwindInternalCommentStore() : super();

  NorthwindInternalCommentStore.clone(
      NorthwindInternalCommentStore commentStore)
      : super.clone(commentStore);
}

abstract class _NorthwindInternalCommentStore with Store {
  _NorthwindInternalCommentStore();

  _NorthwindInternalCommentStore.clone(
      NorthwindInternalCommentStore commentStore) {
    identifier = commentStore.identifier;
    note = commentStore.note;
    author = commentStore.author;
    timestamp = commentStore.timestamp;
  }

  String identifier;

  @observable
  String note;

  @observable
  String author;

  @observable
  DateTime timestamp;
}
