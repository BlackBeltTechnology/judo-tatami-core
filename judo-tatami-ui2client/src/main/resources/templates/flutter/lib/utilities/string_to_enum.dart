part of judo.utility;

extension EnumParser on String {
  T toEnum<T>(List<T> values) {
    return values.firstWhere(
            (e) => e.toString().toLowerCase().split(".").last == '$this'.toLowerCase(),
        orElse: () => null); //return null if not found
  }
}