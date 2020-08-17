package hu.blackbelt.judo.tatami.ui2client.flutter;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.internal.lang3.StringUtils;
import hu.blackbelt.judo.meta.ui.Application;
import hu.blackbelt.judo.meta.ui.data.ClassType;
import hu.blackbelt.judo.meta.ui.data.RelationKind;
import hu.blackbelt.judo.meta.ui.data.RelationType;
import lombok.*;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

public class FlutterHelper {

    @SneakyThrows
    public static void registerSpEL(StandardEvaluationContext context) {
        context.registerFunction("fqPath", FlutterHelper.class.getDeclaredMethod("fqPath", new Class[] { String.class }));
        context.registerFunction("fqClass", FlutterHelper.class.getDeclaredMethod("fqClass", new Class[] { String.class }));
        context.registerFunction("fqVariable", FlutterHelper.class.getDeclaredMethod("fqVariable", new Class[] { String.class }));
        context.registerFunction("path", FlutterHelper.class.getDeclaredMethod("path", new Class[] { String.class }));
        context.registerFunction("className", FlutterHelper.class.getDeclaredMethod("className", new Class[] { String.class }));
        context.registerFunction("modelName", FlutterHelper.class.getDeclaredMethod("modelName", new Class[] { String.class }));
        context.registerFunction("modelPackage", FlutterHelper.class.getDeclaredMethod("modelPackage", new Class[] { String.class }));
        context.registerFunction("variable", FlutterHelper.class.getDeclaredMethod("variable", new Class[] { String.class }));
        context.registerFunction("operations", FlutterHelper.class.getDeclaredMethod("operations", new Class[] { Application.class }));
        context.registerFunction("isEmbedded", FlutterHelper.class.getDeclaredMethod("isEmbedded", new Class[] { RelationType.class }));
        context.registerFunction("cleanup", FlutterHelper.class.getDeclaredMethod("cleanup", new Class[] { String.class }));
    }

    public static void registerHandlebars(Handlebars handlebars) {
        handlebars.registerHelpers(FlutterHelper.class);
    }

    public static String fqPath(String fqName) {
        return fqName
                .replaceAll("::", "__")
                .replaceAll("#","_")
                .replaceAll("([a-z])([A-Z]+)", "$1_$2")
                .toLowerCase();
    }

    public static String fqClass(String fqName) {
        return stream(fqName.replaceAll("#", "::").split("::"))
                .map(s -> StringUtils.capitalize(s))
                .collect(Collectors.joining());
    }

    public static String fqVariable(String fqName) {
        return StringUtils.uncapitalize(fqClass(fqName));
    }

    public static String path(String fqName) {
        String fq = fqPath(fqName);
        if (fq.lastIndexOf("__") > -1) {
            return fq.substring(fq.lastIndexOf("__") + 2);
        } else {
            return fq;
        }

    }

    public static String className(String fqName) {
        String[] splitted = fqName.split("::");
        return fqClass(stream(splitted)
                .map(s -> StringUtils.capitalize(s))
                .skip(Math.max(0, splitted.length - 1))
                .collect(Collectors.joining()));
    }

    public static String modelName(String fqName) {
        String[] splitted = fqName.split("::");
        return fqClass(stream(splitted)
                .map(s -> StringUtils.capitalize(s))
                .findFirst().get());
    }

    public static String modelPackage(String fqName) {
        String[] splitted = fqName.split("::");
        return path(stream(splitted)
                .map(s -> StringUtils.capitalize(s))
                .findFirst().get());
    }

    public static String cleanup(String string) {
        return string.replaceAll("[\\n\\t ]", "");
    }

    public static String variable(String fqName) {
        return StringUtils.uncapitalize(className(fqName));
    }

    public static boolean isEmbedded(RelationType relationType) {
        return (!relationType.isIsReadOnly()) && (relationType.getRelationKind() != RelationKind.ASSOCIATION);
    }

    public static Collection<RelationTuple> operations(Application application) {
        return application.getDataElements().stream()
                .filter(t -> t instanceof ClassType)
                .flatMap(t -> ((ClassType) t).getRelations().stream())
                .flatMap(r -> ((ClassType) r.eContainer()).getAccessPointRelations().stream()
                        .map(r2 -> new RelationTuple(r2, r)))
                .collect(Collectors.toSet());
    }

    @Getter @Setter @AllArgsConstructor @EqualsAndHashCode @ToString
    public static class RelationTuple {
        RelationType accessRelation;
        RelationType relationType;
//        ClassType classType;
    }

}
