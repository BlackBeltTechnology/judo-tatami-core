package hu.blackbelt.judo.tatami.ui2client.flutter;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Options;
import com.github.jknack.handlebars.internal.lang3.StringUtils;
import lombok.SneakyThrows;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.Arrays;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class FlutterHelper {

    @SneakyThrows
    public static void registerSpEL(StandardEvaluationContext context) {
        context.registerFunction("dartFileName", FlutterHelper.class.getDeclaredMethod("dartFileName", new Class[] { String.class }));
        context.registerFunction("dartClassName", FlutterHelper.class.getDeclaredMethod("dartClassName", new Class[] { String.class }));
        context.registerFunction("dartVariableName", FlutterHelper.class.getDeclaredMethod("dartVariableName", new Class[] { String.class }));

    }

    public static void registerHandlebars(Handlebars handlebars) {
        handlebars.registerHelpers(FlutterHelper.class);
    }

    public static String dartFileName(String fqName) {
        return fqName
                .replaceAll("::", "__")
                .replaceAll("#","__")
                .replaceAll("([a-z])([A-Z]+)", "$1_$2")
                .toLowerCase();
    }

    public static String dartClassName(String fqName) {
        return stream(fqName.replaceAll("#", "::").split("::"))
                .map(s -> StringUtils.capitalize(s))
                .collect(Collectors.joining());
    }

    public static String dartVariableName(String fqName) {
        return StringUtils.uncapitalize(stream(fqName.replaceAll("#", "::").split("::"))
                .map(s -> StringUtils.capitalize(s))
                .collect(Collectors.joining()));
    }

}
