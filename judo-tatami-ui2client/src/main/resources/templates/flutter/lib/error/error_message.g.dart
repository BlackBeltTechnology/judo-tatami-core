// GENERATED CODE - DO NOT MODIFY BY HAND

part of 'error_message.dart';

// **************************************************************************
// StoreGenerator
// **************************************************************************

// ignore_for_file: non_constant_identifier_names, unnecessary_brace_in_string_interps, unnecessary_lambdas, prefer_expression_function_bodies, lines_longer_than_80_chars, avoid_as, avoid_annotating_with_dynamic

mixin _$ErrorMessage on _ErrorMessage, Store {
  final _$messageAtom = Atom(name: '_ErrorMessage.message');

  @override
  String get message {
    _$messageAtom.reportRead();
    return super.message;
  }

  @override
  set message(String value) {
    _$messageAtom.reportWrite(value, super.message, () {
      super.message = value;
    });
  }

  final _$_ErrorMessageActionController =
      ActionController(name: '_ErrorMessage');

  @override
  void setMessage(String newMessage) {
    final _$actionInfo = _$_ErrorMessageActionController.startAction(
        name: '_ErrorMessage.setMessage');
    try {
      return super.setMessage(newMessage);
    } finally {
      _$_ErrorMessageActionController.endAction(_$actionInfo);
    }
  }

  @override
  String toString() {
    return '''
message: ${message}
    ''';
  }
}
