package hu.blackbelt.judo.tatami.ui2client.flutter;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.internal.lang3.StringUtils;
import hu.blackbelt.judo.meta.ui.*;
import hu.blackbelt.judo.meta.ui.data.*;
import lombok.*;
import lombok.extern.java.Log;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.util.*;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Log
public class FlutterHelper {

    @SneakyThrows
    public static void registerSpEL(StandardEvaluationContext context) {
        context.registerFunction("fqPath", FlutterHelper.class.getDeclaredMethod("fqPath", new Class[]{String.class}));
        context.registerFunction("fqClass", FlutterHelper.class.getDeclaredMethod("fqClass", new Class[]{String.class}));
        context.registerFunction("fqVariable", FlutterHelper.class.getDeclaredMethod("fqVariable", new Class[]{String.class}));
        context.registerFunction("fqClassWithoutModel", FlutterHelper.class.getDeclaredMethod("fqClassWithoutModel", new Class[]{String.class}));
        context.registerFunction("path", FlutterHelper.class.getDeclaredMethod("path", new Class[]{String.class}));
        context.registerFunction("className", FlutterHelper.class.getDeclaredMethod("className", new Class[]{String.class}));
        context.registerFunction("modelName", FlutterHelper.class.getDeclaredMethod("modelName", new Class[]{String.class}));
        context.registerFunction("modelPackage", FlutterHelper.class.getDeclaredMethod("modelPackage", new Class[]{String.class}));
        context.registerFunction("variable", FlutterHelper.class.getDeclaredMethod("variable", new Class[]{String.class}));
        context.registerFunction("cleanup", FlutterHelper.class.getDeclaredMethod("cleanup", new Class[]{String.class}));
        context.registerFunction("getType", FlutterHelper.class.getDeclaredMethod("getType", new Class[]{VisualElement.class}));
        context.registerFunction("getWidgetTemplate", FlutterHelper.class.getDeclaredMethod("getWidgetTemplate", new Class[]{VisualElement.class}));
        context.registerFunction("isHorizontal", FlutterHelper.class.getDeclaredMethod("isHorizontal", new Class[]{Flex.class}));
        context.registerFunction("mainAxisAlignment", FlutterHelper.class.getDeclaredMethod("mainAxisAlignment", new Class[]{Flex.class}));
        context.registerFunction("crossAxisAlignment", FlutterHelper.class.getDeclaredMethod("crossAxisAlignment", new Class[]{Flex.class}));
        context.registerFunction("mainAxisSize", FlutterHelper.class.getDeclaredMethod("mainAxisSize", new Class[]{Flex.class}));
        context.registerFunction("isSaveButton", FlutterHelper.class.getDeclaredMethod("isSaveButton", new Class[]{Action.class}));
        context.registerFunction("isBackButton", FlutterHelper.class.getDeclaredMethod("isBackButton", new Class[]{Action.class}));
        context.registerFunction("isCreateButton", FlutterHelper.class.getDeclaredMethod("isCreateButton", new Class[]{Action.class}));
        context.registerFunction("isDeleteButton", FlutterHelper.class.getDeclaredMethod("isDeleteButton", new Class[]{Action.class}));
        context.registerFunction("isEditButton", FlutterHelper.class.getDeclaredMethod("isEditButton", new Class[]{Action.class}));
        context.registerFunction("dartType", FlutterHelper.class.getDeclaredMethod("dartType", new Class[]{DataType.class}));
        context.registerFunction("getTargetDataTypeClass", FlutterHelper.class.getDeclaredMethod("getTargetDataTypeClass", new Class[]{DataElement.class}));
        context.registerFunction("getTargetDataTypeVariable", FlutterHelper.class.getDeclaredMethod("getTargetDataTypeVariable", new Class[]{DataElement.class}));
        context.registerFunction("isTablePage", FlutterHelper.class.getDeclaredMethod("isTablePage", new Class[]{PageDefinition.class}));
        context.registerFunction("isViewPage", FlutterHelper.class.getDeclaredMethod("isViewPage", new Class[]{PageDefinition.class}));
        context.registerFunction("isCreatePage", FlutterHelper.class.getDeclaredMethod("isCreatePage", new Class[]{PageDefinition.class}));
        context.registerFunction("isEditPage", FlutterHelper.class.getDeclaredMethod("isEditPage", new Class[]{PageDefinition.class}));
        context.registerFunction("isDashboardPage", FlutterHelper.class.getDeclaredMethod("isDashboardPage", new Class[]{PageDefinition.class}));
        context.registerFunction("isInstanceAction", FlutterHelper.class.getDeclaredMethod("isInstanceAction", new Class[]{PageDefinition.class}));
    }

    public static void registerHandlebars(Handlebars handlebars) {
        handlebars.registerHelpers(FlutterHelper.class);
    }

    public static boolean isInstanceAction (PageDefinition pageDefinition) {
        return pageDefinition.getInstanceActions()!= null && !pageDefinition.getInstanceActions().isEmpty();
    }

    public static boolean isDashboardPage(PageDefinition pageDefinition) {
        return pageDefinition.getPageType().equals(PageType.DASHBOARD);
    }

    public static boolean isEditPage(PageDefinition pageDefinition) {
        return pageDefinition.getPageType().equals(PageType.UPDATE);
    }

    public static boolean isCreatePage(PageDefinition pageDefinition) {
        return pageDefinition.getPageType().equals(PageType.CREATE);
    }

    public static boolean isTablePage(PageDefinition pageDefinition) {
        return pageDefinition.getPageType().equals(PageType.TABLE);
    }

    public static boolean isViewPage(PageDefinition pageDefinition) {
        return pageDefinition.getPageType().equals(PageType.VIEW);
    }

    private static String getTargetDataTypeName(DataElement dataElement) {
        if (dataElement instanceof RelationType) {
            return ((RelationType) dataElement).getTarget().getName();
        }
        if (dataElement instanceof OperationParameterType) {
            return ((OperationParameterType) dataElement).getTarget().getName();
        }
        return null;
    }

    public static String getTargetDataTypeClass(DataElement dataElement) {
        String name = getTargetDataTypeName(dataElement);
        return name != null ? fqClass(name) : null;
    }

    public static String getTargetDataTypeVariable(DataElement dataElement) {
        String name = getTargetDataTypeName(dataElement);
        return name != null ? fqVariable(name) : null;
    }

    public static boolean isSaveButton(Action action) {
        return action != null && SaveAction.class.equals(action.eClass().getInstanceClass());
    }

    public static boolean isBackButton(Action action) {
        return action != null && BackAction.class.equals(action.eClass().getInstanceClass());
    }

    public static boolean isEditButton(Action action) {
        return action != null && EditAction.class.equals(action.eClass().getInstanceClass());
    }

    public static boolean isDeleteButton(Action action) {
        return action != null && DeleteAction.class.equals(action.eClass().getInstanceClass());
    }

    public static boolean isCreateButton(Action action) {
        return action != null && CreateAction.class.equals(action.eClass().getInstanceClass());
    }

    public static String mainAxisSize(Flex flex) {
        return flex.getMainAxisSize().getLiteral().toLowerCase();
    }

    public static String crossAxisAlignment(Flex flex) {
        return flex.getCrossAxisAlignment().getLiteral().toLowerCase();
    }

    public static String mainAxisAlignment(Flex flex) {
        switch (flex.getMainAxisAlignment()) {
            case CENTER:
                return "center";
            case END:
                return "end";
            case SPACEAROUND:
                return "spaceAround";
            case SPACEBETWEEN:
                return "spaceBetween";
            case SPACEEVENLY:
                return "spaceEvenly";
            case START:
                return "start";
            default:
                return null;
        }
    }

    public static boolean isHorizontal(Flex flex) {
        return Axis.HORIZONTAL.equals(flex.getDirection());
    }

    public static String getType(VisualElement visualElementType) {
        return visualElementType.eClass().getInstanceClass().getSimpleName();
    }

    public static String getWidgetTemplate(VisualElement visualElementType) {
        String componentsLocation = "templates/flutter/lib/ui/pages/widgets/";
        return componentsLocation + visualElementType.eClass().getInstanceClass().getSimpleName().toLowerCase() + ".dart.hbs";
    }

    public static String fqPath(String fqName) {
        return fqName
                .replaceAll("\\.", "__")
                .replaceAll("::", "__")
                .replaceAll("#", "__")
                .replaceAll("/", "__")
                .replaceAll("([a-z])([A-Z]+)", "$1_$2")
                .toLowerCase();
    }

    public static String fqClass(String fqName) {
        return stream(fqName.replaceAll("#", "::")
                .replaceAll("\\.", "::")
                .replaceAll("/", "::")
                .split("::"))
                .map(s -> StringUtils.capitalize(s))
                .collect(Collectors.joining());
    }

    public static String fqClassWithoutModel(String fqName) {
        return stream(fqName.replaceAll("#", "::")
                .replaceAll("\\.", "::")
                .replaceAll("/", "::")
                .split("::"))
                .skip(1)
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

    public static String dartType(DataType dataType) {
        if (dataType instanceof NumericType) {
            NumericType numericType = (NumericType) dataType;
            if (numericType.getScale() > 0) {
                return "double";
            } else {
                return "int";
            }
        } else if (dataType instanceof BooleanType) {
            return "bool";
        } else if (dataType instanceof DateType) {
            return "DateTime";
        } else if (dataType instanceof StringType) {
            return "String";
        } else {
            return "String";
        }
    }

}
