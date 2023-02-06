package hu.blackbelt.judo.tatami.core;

public class AnsiColor {

    public static String yellow(String str) {
        return colorize(str, "33m");
    }

    public static String red(String str) {
        return colorize(str, "31m");
    }

    public static String colorize(String str, String color) {
        if (System.getProperty("disableJudoAnsiColors") == null) {
            return "\u001B[" + color + "{}\u001B[0m";
        } else {
            return str;
        }
    }

}
