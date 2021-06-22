import 'package:mobx/mobx.dart';

part 'error_message.g.dart';

class ErrorMessage extends _ErrorMessage with _$ErrorMessage {}

abstract class _ErrorMessage with Store {
    @observable
    String message;

    @action
    void setMessage(String newMessage) {
      message = newMessage;
    }

    @action
    void clearMessage() {
        message = null;
    }

}