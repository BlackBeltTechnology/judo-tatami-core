package hu.blackbelt.judo.tatami.ui2client.flutter;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.internal.lang3.StringUtils;
import com.google.common.collect.Sets;
import hu.blackbelt.judo.meta.ui.*;
import hu.blackbelt.judo.meta.ui.data.*;
import lombok.SneakyThrows;
import lombok.extern.java.Log;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.ecore.xmi.XMIResource;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

import static java.util.Arrays.stream;

@Log
public class FlutterHelper {

    final static Set<String> RESERVED_WORDS =
            Sets.newHashSet(
                    "abstract",
                    "as",
                    "assert",
                    "async",
                    "await",
                    "break",
                    "case",
                    "catch",
                    "class",
                    "const",
                    "continue",
                    "covariant",
                    "default",
                    "deferred",
                    "do",
                    "dynamic",
                    "else",
                    "enum",
                    "export",
                    "extends",
                    "extension",
                    "external",
                    "factory",
                    "false",
                    "final",
                    "finally",
                    "for",
                    "Function",
                    "get",
                    "hide",
                    "if",
                    "implements",
                    "import",
                    "in",
                    "inout",
                    "interface",
                    "is",
                    "late",
                    "library",
                    "mixin",
                    "native",
                    "new",
                    "null",
                    "of",
                    "on",
                    "operator",
                    "out",
                    "part",
                    "patch",
                    "required",
                    "rethrow",
                    "return",
                    "set",
                    "show",
                    "source",
                    "static",
                    "super",
                    "switch",
                    "sync",
                    "this",
                    "throw",
                    "true",
                    "try",
                    "typedef",
                    "var",
                    "void",
                    "while",
                    "with",
                    "yield",
                    "String",
                    "bool",
                    "int",
                    "num",
                    "double",
                    "dynamic",
                    "List",
                    "Map",
                    "Object"
            );

    @SneakyThrows
    public static void registerSpEL(StandardEvaluationContext context) {
        context.registerFunction("fqPath", FlutterHelper.class.getDeclaredMethod("fqPath", new Class[]{String.class}));
        context.registerFunction("fqClass", FlutterHelper.class.getDeclaredMethod("fqClass", new Class[]{String.class}));
        context.registerFunction("fqVariable", FlutterHelper.class.getDeclaredMethod("fqVariable", new Class[]{String.class}));
        context.registerFunction("fqClassWithoutModel", FlutterHelper.class.getDeclaredMethod("fqClassWithoutModel", new Class[]{String.class}));
        context.registerFunction("path", FlutterHelper.class.getDeclaredMethod("path", new Class[]{String.class}));
        context.registerFunction("openApiDataType", FlutterHelper.class.getDeclaredMethod("openApiDataType", new Class[]{String.class}));
        context.registerFunction("className", FlutterHelper.class.getDeclaredMethod("className", new Class[]{String.class}));
        context.registerFunction("baseUrl", FlutterHelper.class.getDeclaredMethod("baseUrl", new Class[]{String.class}));
        context.registerFunction("modelName", FlutterHelper.class.getDeclaredMethod("modelName", new Class[]{String.class}));
        context.registerFunction("modelNameVariable", FlutterHelper.class.getDeclaredMethod("modelNameVariable", new Class[]{String.class}));
        context.registerFunction("modelPackage", FlutterHelper.class.getDeclaredMethod("modelPackage", new Class[]{String.class}));
        context.registerFunction("packageName", FlutterHelper.class.getDeclaredMethod("packageName", new Class[]{String.class}));
        context.registerFunction("variable", FlutterHelper.class.getDeclaredMethod("variable", new Class[]{String.class}));
        context.registerFunction("cleanup", FlutterHelper.class.getDeclaredMethod("cleanup", new Class[]{String.class}));
        context.registerFunction("getType", FlutterHelper.class.getDeclaredMethod("getType", new Class[]{VisualElement.class}));
        context.registerFunction("getWidgetTemplate", FlutterHelper.class.getDeclaredMethod("getWidgetTemplate", new Class[]{VisualElement.class}));
        context.registerFunction("mainAxisAlignment", FlutterHelper.class.getDeclaredMethod("mainAxisAlignment", new Class[]{Flex.class}));
        context.registerFunction("crossAxisAlignment", FlutterHelper.class.getDeclaredMethod("crossAxisAlignment", new Class[]{Flex.class}));
        context.registerFunction("mainAxisSize", FlutterHelper.class.getDeclaredMethod("mainAxisSize", new Class[]{Flex.class}));
        context.registerFunction("dartType", FlutterHelper.class.getDeclaredMethod("dartType", new Class[]{DataType.class}));
        context.registerFunction("filterDataType", FlutterHelper.class.getDeclaredMethod("filterDataType", new Class[]{DataType.class}));
        context.registerFunction("dataTypeToOperationType", FlutterHelper.class.getDeclaredMethod("dataTypeToOperationType", new Class[]{DataType.class}));
        context.registerFunction("getDistinctDataTypes", FlutterHelper.class.getDeclaredMethod("getDistinctDataTypes", new Class[]{EList.class}));
        context.registerFunction("isTransientAttribute", FlutterHelper.class.getDeclaredMethod("isTransientAttribute", new Class[]{AttributeType.class}));
        context.registerFunction("multiplyCol", FlutterHelper.class.getDeclaredMethod("multiplyCol", new Class[]{Double.class}));
        context.registerFunction("validatableFlagNeed", FlutterHelper.class.getDeclaredMethod("validatableFlagNeed", new Class[]{RelationType.class}));
        context.registerFunction("isEnumType", FlutterHelper.class.getDeclaredMethod("isEnumType", new Class[]{DataType.class}));
        context.registerFunction("isTimestampType", FlutterHelper.class.getDeclaredMethod("isTimestampType", new Class[]{DataType.class}));
        context.registerFunction("isBooleanDataType", FlutterHelper.class.getDeclaredMethod("isBooleanDataType", new Class[]{DataType.class}));
        context.registerFunction("isDateType", FlutterHelper.class.getDeclaredMethod("isDateType", new Class[]{DataType.class}));
        context.registerFunction("isStringType", FlutterHelper.class.getDeclaredMethod("isStringType", new Class[]{DataType.class}));
        context.registerFunction("isBooleanType", FlutterHelper.class.getDeclaredMethod("isBooleanType", new Class[]{DataType.class}));
        context.registerFunction("isNumericType", FlutterHelper.class.getDeclaredMethod("isNumericType", new Class[]{DataType.class}));
        context.registerFunction("isInputWidgetMapNeed", FlutterHelper.class.getDeclaredMethod("isInputWidgetMapNeed", new Class[]{PageDefinition.class}));
        context.registerFunction("isValidateHere", FlutterHelper.class.getDeclaredMethod("isValidateHere", new Class[]{PageDefinition.class}));
        context.registerFunction("isSingleRelationDashboardPage", FlutterHelper.class.getDeclaredMethod("isSingleRelationDashboardPage", new Class[]{PageDefinition.class}));
        context.registerFunction("isCollectionRelationDashboardPage", FlutterHelper.class.getDeclaredMethod("isCollectionRelationDashboardPage", new Class[]{PageDefinition.class}));
        context.registerFunction("isAccessTablePage", FlutterHelper.class.getDeclaredMethod("isAccessTablePage", new Class[]{PageDefinition.class}));
        context.registerFunction("isRefreshViewTypePage", FlutterHelper.class.getDeclaredMethod("isRefreshViewTypePage", new Class[]{PageDefinition.class}));
        context.registerFunction("isRefreshTableTypePage", FlutterHelper.class.getDeclaredMethod("isRefreshTableTypePage", new Class[]{PageDefinition.class}));
        context.registerFunction("isViewTypePage", FlutterHelper.class.getDeclaredMethod("isViewTypePage", new Class[]{PageDefinition.class}));
        context.registerFunction("isCreateTypePage", FlutterHelper.class.getDeclaredMethod("isCreateTypePage", new Class[]{PageDefinition.class}));
        context.registerFunction("getInputWidgets", FlutterHelper.class.getDeclaredMethod("getInputWidgets", new Class[]{Container.class}));
        context.registerFunction("getPagesByRelation", FlutterHelper.class.getDeclaredMethod("getPagesByRelation", new Class[]{EList.class, DataElement.class}));
        context.registerFunction("safe", FlutterHelper.class.getDeclaredMethod("safe", new Class[]{String.class, String.class}));
        context.registerFunction("dart", FlutterHelper.class.getDeclaredMethod("dart", new Class[]{String.class}));
        context.registerFunction("isObserverButton", FlutterHelper.class.getDeclaredMethod("isObserverButton", new Class[]{VisualElement.class, DataElement.class, Action.class}));
        context.registerFunction("isFilterOperationLike", FlutterHelper.class.getDeclaredMethod("isFilterOperationLike", new Class[]{EnumerationMember.class, String.class}));
        context.registerFunction("labelName", FlutterHelper.class.getDeclaredMethod("labelName", new Class[]{String.class}));
        context.registerFunction("l10nLabelName", FlutterHelper.class.getDeclaredMethod("l10nLabelName", new Class[]{String.class}));
        context.registerFunction("getAttributeTypeNamesFromWidgets", FlutterHelper.class.getDeclaredMethod("getAttributeTypeNamesFromWidgets", new Class[]{Container.class}));
        context.registerFunction("isEmptyList", FlutterHelper.class.getDeclaredMethod("isEmptyList", new Class[]{List.class}));

        //page store naming
        context.registerFunction("storeClassPath", FlutterHelper.class.getDeclaredMethod("storeClassPath", new Class[]{String.class, String.class}));
        context.registerFunction("storeFolderPath", FlutterHelper.class.getDeclaredMethod("storeFolderPath", new Class[]{String.class}));
        context.registerFunction("storeClassFileName", FlutterHelper.class.getDeclaredMethod("storeClassFileName", new Class[]{String.class}));
        context.registerFunction("storeClassName", FlutterHelper.class.getDeclaredMethod("storeClassName", new Class[]{String.class}));
        context.registerFunction("storeClassRelativePath", FlutterHelper.class.getDeclaredMethod("storeClassRelativePath", new Class[]{String.class}));

        //repository naming
        context.registerFunction("repositoryFolderPath", FlutterHelper.class.getDeclaredMethod("repositoryFolderPath", new Class[]{String.class}));
        context.registerFunction("repositoryClassPath", FlutterHelper.class.getDeclaredMethod("repositoryClassPath", new Class[]{String.class, String.class}));
        context.registerFunction("repositoryRelationPath", FlutterHelper.class.getDeclaredMethod("repositoryRelationPath", new Class[]{String.class, String.class, String.class}));
        context.registerFunction("repositoryClassRelativePath", FlutterHelper.class.getDeclaredMethod("repositoryClassRelativePath", new Class[]{String.class}));
        context.registerFunction("repositoryRelationRelativePath", FlutterHelper.class.getDeclaredMethod("repositoryRelationRelativePath", new Class[]{String.class, String.class}));
        context.registerFunction("repositoryClassName", FlutterHelper.class.getDeclaredMethod("repositoryClassName", new Class[]{String.class}));
        context.registerFunction("repositoryRelationName", FlutterHelper.class.getDeclaredMethod("repositoryRelationName", new Class[]{String.class, String.class}));
    }

    public static void registerHandlebars(Handlebars handlebars) {
        handlebars.registerHelpers(FlutterHelper.class);
    }

    public static boolean isBooleanDataType (DataType dataType) {
        return dataType instanceof BooleanType;
    }
    public static boolean isTransientAttribute (AttributeType attributeType) {
        return MemberType.TRANSIENT == attributeType.getMemberType();
    }

    public static String mainAxisSize(Flex flex) {
        return flex.getMainAxisSize().getLiteral().toLowerCase();
    }

    public static String crossAxisAlignment(Flex flex) {
        switch (flex.getCrossAxisAlignment()) {
            case CENTER:
                return "center";
            case END:
                return "end";
            case BASELINE:
                return "baseline";
            case STRETCH:
                return "stretch";
            case START:
                return "start";
            default:
                return null;
        }

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

    public static String getType(VisualElement visualElementType) {
        return visualElementType.eClass().getInstanceClass().getSimpleName();
    }

    public static String getWidgetTemplate(VisualElement visualElementType) {
        String componentsLocation = "templates/flutter/lib/ui/pages/widgets/";
        return componentsLocation + visualElementType.eClass().getInstanceClass().getSimpleName().toLowerCase() + ".dart.hbs";
    }

    @Deprecated
    public static String fqPath(String fqName) {
        return fqName
                .replaceAll("\\.", "__")
                .replaceAll("::", "__")
                .replaceAll("#", "__")
                .replaceAll("/", "__")
                .replaceAll("([a-z])([A-Z]+)", "$1_$2")
                .toLowerCase();
    }

    @Deprecated
    public static String fqClass(String fqName) {
        return stream(fqName.replaceAll("#", "::")
                .replaceAll("\\.", "::")
                .replaceAll("/", "::")
                .replaceAll("_", "::")
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

    /**
     * Splits a given fully qualified name at the namespace separator "::" and removes the first and last element of the list (i.e. model name and type name).
     *
     * @param name fully qualified name with "::" as namespace separators
     * @return the list of the names of the packages which contain a given type
     */
    public static List<String> getPackageNameTokens(String name) {
        List<String> nameTokens = stream(name
                .split("::"))
                .collect(Collectors.toList());
        if (nameTokens.size() >= 2) {
            nameTokens.remove(0);
            nameTokens.remove(nameTokens.size() - 1);
        }
        return nameTokens;
    }

    public static String packageName(String packageName) {
        List<String> nameTokens = stream(packageName.replaceAll("#", "::")
                .replaceAll("\\.", "::")
                .replaceAll("/", "::")
                .split("::"))
                .collect(Collectors.toList());
        if (nameTokens.size() > 2) {
            nameTokens.remove(0);
            nameTokens.remove(nameTokens.size() - 1);
            return nameTokens.stream()
                    .map(s -> StringUtils.capitalize(s))
                    .collect(Collectors.joining());
        }
        return null;
    }

    @Deprecated
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

    public static String openApiDataType(String fqDataTypeName){
        if (fqDataTypeName == null) {
            return null;
        }
        String[] splitted = fqDataTypeName.split("\\.");
        return stream(splitted)
                .skip(splitted.length - 1)
                .findFirst()
                .get();
    }

    public static String className(String fqName) {
        if (fqName == null) {
            return null;
        }
        String[] splitted = fqName.split("::");
        return fqClass(stream(splitted)
                .map(s -> StringUtils.capitalize(s))
                .skip(Math.max(0, splitted.length - 1))
                .collect(Collectors.joining()));
    }

    public static String baseUrl(String fqName) {
        String[] splitted = fqName.split("::");
        return stream(splitted).findFirst().get();
    }

    public static String modelName(String fqName) {
        String[] splitted = fqName.split("::");
        return fqClass(stream(splitted)
                .map(s -> StringUtils.capitalize(s))
                .findFirst().get());
    }

    public static String modelNameVariable(String fqName) {
        String[] splitted = fqName.split("::");
        return StringUtils.uncapitalize(fqClass(stream(splitted)
                .map(s -> StringUtils.capitalize(s))
                .findFirst().get()));
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
        return reserved(StringUtils.uncapitalize(className(fqName)));
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
        } else if (dataType instanceof TimestampType) {
            return "DateTime";
        } else if (dataType instanceof StringType) {
            return "String";
        } else if (dataType instanceof EnumerationType) {
            return className(dataType.getName());
        } else {
            return "String";
        }
    }

    public static String filterDataType(DataType dataType) {
        if (dataType instanceof NumericType) {
            return "numeric";
        } else if (dataType instanceof BooleanType) {
            return "boolean";
        } else if (dataType instanceof DateType) {
            return "date";
        } else if (dataType instanceof TimestampType) {
            return "dateTime";
        } else if (dataType instanceof StringType) {
            return "string";
        } else if (dataType instanceof EnumerationType) {
            return "enumeration";
        } else {
            return "string";
        }
    }

    public static String dataTypeToOperationType(DataType dataType){
        if (dataType instanceof NumericType) {
            return "NumericOperation";
        } else if (dataType instanceof BooleanType) {
            return "BooleanOperation";
        } else if (dataType instanceof DateType) {
            return "NumericOperation";
        } else if (dataType instanceof TimestampType) {
            return "NumericOperation";
        } else if (dataType instanceof StringType) {
            return "StringOperation";
        } else if (dataType instanceof EnumerationType) {
            return "EnumerationOperation";
        } else {
            return "StringOperation";
        }
    }

    public static List<DataType> getDistinctDataTypes(EList<DataType> dataTypeList) {
        List<DataType> filteredList = dataTypeList.stream().filter(s -> !className(s.getName()).matches("^(String|Numeric|Boolean|Enumeration)(Operation)$")).collect(Collectors.toList());
        Collection<DataType> resultList = filteredList.stream().collect(Collectors.toMap(FlutterHelper::filterDataType, Function.identity(),(dataType1, dataType2) -> dataType1)).values();
        return new ArrayList<>(resultList);
    }

    public static boolean validatableFlagNeed (RelationType relationType) {
        return relationType.getIsRelationKindComposition() || relationType.getIsRelationKindAggregation() || relationType.getTarget().getRelations()
                .stream()
                .anyMatch(
                        relationTypeElement -> relationTypeElement.getIsRelationKindAggregation() || relationTypeElement.getIsRelationKindComposition()
                );
    }

    public static boolean isEnumType (DataType type) {
        return type instanceof EnumerationType;
    }

    public static boolean isTimestampType (DataType type) {
        return type instanceof TimestampType;
    }

    public static boolean isDateType (DataType type) {
        return type instanceof DateType;
    }

    public static boolean isBooleanType (DataType type) {
        return type instanceof BooleanType;
    }

    public static boolean isStringType (DataType type) {
        return type instanceof StringType;
    }

    public static boolean isNumericType (DataType type)  {
        return type instanceof NumericType;
    }

    public static boolean isInputWidgetMapNeed (PageDefinition page) {
        return page.getIsPageTypeUpdate() || page.getIsPageTypeCreate() || page.getIsPageTypeCustom() || page.getIsPageTypeOperationInput() ;
    }

    public static String multiplyCol(Double col) {
        return String.valueOf(Math.round(col * 100));
    }

    public static List<VisualElement> getInputWidgets(Container container) {
        List<VisualElement> inputList = new ArrayList<VisualElement>();

        getInputWidgetsFromContainers(container, inputList);

        return inputList;
    }


    public static void getInputWidgetsFromContainers(Container container, List<VisualElement> inputList) {
        List<VisualElement> children = container.getChildren();

        for (VisualElement element : children ) {
            if (element instanceof Container ) {
                getInputWidgetsFromContainers((Container) element, inputList);
            } else if (element instanceof Input) {
                inputList.add(element);
            }
        }
    }

    public static boolean isValidateHere(PageDefinition page){
        return !page.getIsPageTypeUpdate() && !page.getIsPageTypeView() && !page.getIsPageTypeOperationOutput();
    }

    public static boolean isSingleRelationDashboardPage(PageDefinition page){
        if (page.getRelationType() == null) return false;
        return page.getIsPageTypeDashboard() && !page.getRelationType().isIsCollection();
    }

    public static boolean isCollectionRelationDashboardPage(PageDefinition page){
        if (page.getRelationType() == null) return false;
        return page.getIsPageTypeDashboard() && page.getRelationType().isIsCollection();
    }

    public static boolean isAccessTablePage(PageDefinition page){
        if (page.getRelationType() == null) return false;
        return page.getIsPageTypeDashboard() || (page.getIsPageTypeTable() && page.getRelationType().isIsAccess());
    }

    public static boolean isViewTypePage(PageDefinition page) {
        return page.getIsPageTypeView() || page.getIsPageTypeOperationOutput() || isSingleRelationDashboardPage(page);
    }

    public static boolean isRefreshViewTypePage(PageDefinition page) {
        return page.getIsPageTypeView() || isSingleRelationDashboardPage(page);
    }

    public static boolean isRefreshTableTypePage(PageDefinition page) {
        return page.getIsPageTypeTable() || isCollectionRelationDashboardPage(page);
    }

    public static boolean isCreateTypePage(PageDefinition page) {
        return page.getIsPageTypeCreate() || page.getIsPageTypeOperationInput();
    }

    public static List<PageDefinition> getPagesByRelation(EList<PageDefinition> pages, DataElement relation){
        List<PageDefinition> filteredList = pages.stream().filter(e -> e.getDataElement() != null ).collect(Collectors.toList());
        return filteredList.stream()
                .filter(e -> (((XMIResource) e.getDataElement().eResource()).getID(e.getDataElement())
                        .equals(((XMIResource) relation.eResource()).getID(relation))))
                .collect(Collectors.toList());
    }

    public static String safe(String input, String defaultValue) {
        return input == null || "".equals(input.trim()) ? defaultValue : input;
    }

    public static String reserved(String input) {
        if (RESERVED_WORDS.contains(input)) {
            return input + "_";
        } else {
            return input;
        }
    }

    public static String dart(String input) {
        return input
//                .replaceAll("[^\\\\u(\\p{XDigit}{4})]", "_")
                .replaceAll("[^\\.A-Za-z0-9_]", "_").toLowerCase();
    }

    public static boolean isObserverButton(VisualElement visualElement, DataElement relationType, Action action){
        if (visualElement == null || relationType == null || action == null ) {
            return false;
        }

        if (relationType instanceof OperationParameterType){
            return (visualElement.getEnabledBy() != null) || (!((OperationParameterType) relationType).isIsCollection() && action.getIsCreateAction()) || action.getIsUnsetAction();
        } else if (relationType instanceof RelationType){
            return (visualElement.getEnabledBy() != null) || (!((RelationType) relationType).isIsCollection() && action.getIsCreateAction()) || action.getIsUnsetAction();
        } else {
            return false;
        }

    }

    public static String labelName(String fqName) {
        if (fqName == null) {
            return null;
        }
        return fqName.replace("::", " ");
    }

    public static String l10nLabelName(String label) {
        String[] array = label.split("");
        byte[] bytes = label.getBytes(StandardCharsets.UTF_8);

        for(int i = 0; i < array.length; i++) {
            if(array[i].matches("[^A-Za-z]")){
                array[i] = String.valueOf(Byte.toUnsignedInt(bytes[i]));
            }
        }
        return "_" + String.join("", array); // replace need, because minus bytes
    }

    public static boolean isFilterOperationLike(EnumerationMember operator, String enumName) {
        return variable(operator.getName()).equals("like") && className(enumName).equals("StringOperation");
    }

    public static List<String> getAttributeTypeNamesFromWidgets(Container container) {

        List<String> inputList = new ArrayList<>();

        getAttributeTypeNames(container, inputList);

        return inputList;
    }

    public static void getAttributeTypeNames(Container container, List<String> inputList) {
        List<VisualElement> children = container.getChildren();

        for (VisualElement element : children ) {
            if(element.getEnabledBy() != null){
                if(!inputList.contains(element.getEnabledBy().getName())) {
                    inputList.add((element).getEnabledBy().getName());
                }
            }
            if (element instanceof Container ) {
                getAttributeTypeNames((Container) element, inputList);
            } else if (element instanceof Input ) {
                if(!inputList.contains(((Input) element).getAttributeType().getName())) {
                    inputList.add(((Input) element).getAttributeType().getName());
                }
            } else if (element instanceof Formatted) {
                if(!inputList.contains(((Formatted) element).getAttributeType().getName())) {
                    inputList.add(((Formatted) element).getAttributeType().getName());
                }
            } else if (element instanceof Button) {
                if(((Button) element).getAction().getConfirmationCondition() != null) {
                    if(!inputList.contains(((Button) element).getAction().getConfirmationCondition().getName())) {
                        inputList.add(((Button) element).getAction().getConfirmationCondition().getName());
                    }
                }
            }
        }
    }

    public static boolean isEmptyList(List list){
        return list.isEmpty();
    }

    //store naming

    /**
     * Calculates the relative path of the "store" folder. Uses  {@link #path(String)} to calculate actor name.
     * @param actorName name of the actor of the application
     * @return relative path of "store" folder, e.g. lib/actor/store/
     */
    public static String storeFolderPath(String actorName) {
        return "lib/"
            .concat(path(actorName))
            .concat("/store/");
    }

    /**
     * Calculates the relative path of a Store class. Uses {@link #storeFolderPath(String)} to calculate relative path of "store" folder
     * and {@link #storeClassRelativePath(String)} to calculate folder structure based on the package structure and the file name of the Store class.
     * @param fqName the fully qualified name of a type with "::" as namespace separators
     * @param actorName name of the actor of the application
     * @return relative path of a Store class, e.g. lib/actor/store/packageName1/packageName2/../typename__store.dart
     */
    public static String storeClassPath(String fqName, String actorName) {
        return storeFolderPath(actorName).concat(storeClassRelativePath(fqName));
    }

    /**
     * Calculates folder structure based on the package structure using {@link #getPackageNameTokens(String)}
     * and the file name of the Store class using {@link #storeClassFileName(String)}.
     *
     * @param fqName the fully qualified name of a type with "::" as namespace separators
     * @return relative path of a Store class, e.g. packageName1/packageName2/../typename__store.dart
     */
    public static String storeClassRelativePath(String fqName) {
        List<String> packageNameTokens = getPackageNameTokens(fqName);
        if (packageNameTokens.isEmpty()) {
            return storeClassFileName(fqName);
        }
        return packageNameTokens.stream()
                .collect(Collectors.joining("/"))
                .concat("/" + storeClassFileName(fqName));
    }

    /**
     * Calculates the Store class file name. Uses {@link #className(String)} to remove model name and package names from the fully qualified name.
     * @param fqName the fully qualified name of a type with "::" as namespace separators
     * @return the file name of a Store class, e.g. typename__store.dart
     */
    public static String storeClassFileName(String fqName) {
        return className(fqName).toLowerCase() + "__store.dart";
    }

    /**
     * Calculates the Store class name. Uses {@link #fqClassWithoutModel(String)} to remove model name from the fully qualified name and capitalizes package names.
     * @param fqName the fully qualified name of a type with "::" as namespace separators
     * @return the class name of a Store class, e.g. Package1Package2..TypeNameStore
     */
    public static String storeClassName(String fqName) {
        return fqClassWithoutModel(fqName) + "Store";
    }

    //repository naming

    /**
     * Calculates the relative path of the "repository" folder. Uses  {@link #path(String)} to calculate actor name.
     * @param actorName name of the actor of the application
     * @return relative path of "repository" folder, e.g. lib/actor/repository/
     */
    public static String repositoryFolderPath(String actorName) {
        return "lib/"
                .concat(path(actorName))
                .concat("/repository/");
    }

    /**
     * Calculates the relative path of a Repository class. Uses {@link #repositoryFolderPath(String)} to calculate relative path of "repository" folder
     * and {@link #repositoryClassRelativePath(String)} to calculate folder structure based on the package structure and the file name of the Repository class.
     * @param fqName the fully qualified name of a type with "::" as namespace separators
     * @param actorName name of the actor of the application
     * @return relative path of a Repository class, e.g. lib/actor/repository/packageName1/packageName2/../typename/repository.dart
     */
    public static String repositoryClassPath(String fqName, String actorName) {
        return repositoryFolderPath(actorName).concat(repositoryClassRelativePath(fqName));
    }

    /**
     * Calculates the relative path of a Repository class. Uses {@link #repositoryFolderPath(String)} to calculate relative path of "repository" folder
     * and {@link #repositoryRelationRelativePath(String,String)} to calculate folder structure based on the package structure and the file name of the Repository class.
     * @param relationName the name of the relation
     * @param actorName name of the actor of the application
     * @return relative path of a Repository class, e.g. lib/actor/repository/packageName1/packageName2/../typename/relationName__repository.dart
     */
    public static String repositoryRelationPath(String relationName, String ownerName, String actorName) {
        return repositoryFolderPath(actorName).concat(repositoryRelationRelativePath(relationName, ownerName));
    }

    /**
     * Calculates folder structure based on the package structure using {@link #getRepositoryRelativePath(String)}
     * for the path and file name of Repository classes of types.
     *
     * @param fqName the fully qualified name of a type with "::" as namespace separators
     * @return relative path of a Repository class, e.g. packageName1/packageName2/../typename/repository.dart
     */
    public static String repositoryClassRelativePath(String fqName) {
        return getRepositoryRelativePath(fqName).concat("/repository.dart");
    }

    /**
     * Calculates folder structure based on the package structure using {@link #getRepositoryRelativePath(String)}.
     * for the path and file name of Repository classes of relations.
     *
     * @param relationName name of the realtion
     * @param ownerName the fully qualified name of owner of the relation with "::" as namespace separators
     * @return relative path of a Repository class, e.g. packageName1/packageName2/../typename/relationname__repository.dart
     */
    public static String repositoryRelationRelativePath(String relationName, String ownerName) {
        return getRepositoryRelativePath(ownerName).concat("/" + relationName.toLowerCase().concat("__repository.dart"));
    }

    /**
     * Calculates folder structure based on the package structure using {@link #getPackageNameTokens(String)}
     * for the path of a repository for a type.
     *
     * @param fqName the fully qualified name of a type with "::" as namespace separators
     * @return relative path of a Repository class folder, e.g. packageName1/packageName2/../typename
     */
    private static String getRepositoryRelativePath(String fqName) {
        List<String> packageNameTokens = getPackageNameTokens(fqName);
        String path;
        if (packageNameTokens.isEmpty()) {
            path = className(fqName).toLowerCase();
        } else {
            path = packageNameTokens.stream()
                .collect(Collectors.joining("/"))
                .concat(className(fqName).toLowerCase());
        }
        return path;
    }

    /**
     * Calculates the Repository class name. Uses {@link #fqClassWithoutModel(String)} to remove model name from the fully qualified name and capitalizes package names.
     * @param fqName the fully qualified name of a type with "::" as namespace separators
     * @return the class name of a Repository class, e.g. Package1Package2..TypeNameRepository
     */
    public static String repositoryClassName(String fqName) {
        return fqClassWithoutModel(fqName).concat("Repository");
    }

    /**
     * Calculates the Repository class name for relations. Uses {@link #fqClassWithoutModel(String)} to remove model name from the fully qualified name of the owner name and capitalizes package names, then adds the relation name calpitalzed.
     * @param relationName the name of a the relation
     * @param ownerName the fully qualified name of the relation's owner with "::" as namespace separators
     * @return the class name of a Repository class, e.g. Package1Package2..TypeNameRelationNameRepository
     */
    public static String repositoryRelationName(String relationName, String ownerName) {
        return repositoryClassName(ownerName).concat(StringUtils.capitalize(relationName)).concat("Repository");
    }

}
