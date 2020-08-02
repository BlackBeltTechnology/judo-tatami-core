// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'northwind_internal_comment_store.dart';

// **************************************************************************
// StoreGenerator
// **************************************************************************

// ignore_for_file: non_constant_identifier_names, unnecessary_brace_in_string_interps, unnecessary_lambdas, prefer_expression_function_bodies, lines_longer_than_80_chars, avoid_as, avoid_annotating_with_dynamic

mixin _$NorthwindInternalCommentStore on _NorthwindInternalCommentStore, Store {
  final _$noteAtom = Atom(name: '_NorthwindInternalCommentStore.note');

  @override
  String get note {
    _$noteAtom.reportRead();
    return super.note;
  }

  @override
  set note(String value) {
    _$noteAtom.reportWrite(value, super.note, () {
      super.note = value;
    });
  }

  final _$authorAtom = Atom(name: '_NorthwindInternalCommentStore.author');

  @override
  String get author {
    _$authorAtom.reportRead();
    return super.author;
  }

  @override
  set author(String value) {
    _$authorAtom.reportWrite(value, super.author, () {
      super.author = value;
    });
  }

  final _$timestampAtom =
      Atom(name: '_NorthwindInternalCommentStore.timestamp');

  @override
  DateTime get timestamp {
    _$timestampAtom.reportRead();
    return super.timestamp;
  }

  @override
  set timestamp(DateTime value) {
    _$timestampAtom.reportWrite(value, super.timestamp, () {
      super.timestamp = value;
    });
  }

  @override
  String toString() {
    return '''
note: ${note},
author: ${author},
timestamp: ${timestamp}
    ''';
  }
}
