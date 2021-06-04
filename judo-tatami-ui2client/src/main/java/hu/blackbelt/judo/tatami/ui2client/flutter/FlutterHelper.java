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
        context.registerFunction("uriPath", FlutterHelper.class.getDeclaredMethod("uriPath", new Class[]{String.class}));
        context.registerFunction("uriPathWithIdParam", FlutterHelper.class.getDeclaredMethod("uriPathWithIdParam", new Class[]{String.class}));
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
        context.registerFunction("getXMIID", FlutterHelper.class.getDeclaredMethod("getXMIID", new Class[]{VisualElement.class}));
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
        context.registerFunction("tableNavigateToViewHasIdParam", FlutterHelper.class.getDeclaredMethod("tableNavigateToViewHasIdParam", new Class[]{PageDefinition.class}));
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
        context.registerFunction("isBookmarkablePage", FlutterHelper.class.getDeclaredMethod("isBookmarkablePage", new Class[]{PageDefinition.class}));
        context.registerFunction("isAccessTablePage", FlutterHelper.class.getDeclaredMethod("isAccessTablePage", new Class[]{PageDefinition.class}));
        context.registerFunction("isAccessViewPage", FlutterHelper.class.getDeclaredMethod("isAccessViewPage", new Class[]{PageDefinition.class}));
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
        context.registerFunction("getAttributeTypeNamesFromColumnsAndAttributes", FlutterHelper.class.getDeclaredMethod("getAttributeTypeNamesFromColumnsAndAttributes", new Class[]{EList.class, EList.class}));
        context.registerFunction("getAttributeTypeNamesFromWidgets", FlutterHelper.class.getDeclaredMethod("getAttributeTypeNamesFromWidgets", new Class[]{Container.class}));
        context.registerFunction("isEmptyList", FlutterHelper.class.getDeclaredMethod("isEmptyList", new Class[]{List.class}));

        //page store naming
        context.registerFunction("storeFolderPath", FlutterHelper.class.getDeclaredMethod("storeFolderPath", new Class[]{ClassType.class}));
        context.registerFunction("storeClassRelativePath", FlutterHelper.class.getDeclaredMethod("storeClassRelativePath", new Class[]{ClassType.class}));
        context.registerFunction("storeClassPath", FlutterHelper.class.getDeclaredMethod("storeClassPath", new Class[]{ClassType.class, ClassType.class}));
        context.registerFunction("storeClassName", FlutterHelper.class.getDeclaredMethod("storeClassName", new Class[]{ClassType.class}));

        //repository naming
        context.registerFunction("repositoryFolderPath", FlutterHelper.class.getDeclaredMethod("repositoryFolderPath", new Class[]{ClassType.class}));
        context.registerFunction("repositoryClassPath", FlutterHelper.class.getDeclaredMethod("repositoryClassPath", new Class[]{ClassType.class, ClassType.class}));
        context.registerFunction("repositoryRelationPath", FlutterHelper.class.getDeclaredMethod("repositoryRelationPath", new Class[]{ClassType.class, RelationType.class}));
        context.registerFunction("repositoryClassRelativePath", FlutterHelper.class.getDeclaredMethod("repositoryClassRelativePath", new Class[]{ClassType.class}));
        context.registerFunction("repositoryRelationRelativePath", FlutterHelper.class.getDeclaredMethod("repositoryRelationRelativePath", new Class[]{RelationType.class}));
        context.registerFunction("repositoryClassName", FlutterHelper.class.getDeclaredMethod("repositoryClassName", new Class[]{ClassType.class}));
        context.registerFunction("repositoryStoreMapperClassName", FlutterHelper.class.getDeclaredMethod("repositoryStoreMapperClassName", new Class[]{ClassType.class}));
        context.registerFunction("repositoryRelationName", FlutterHelper.class.getDeclaredMethod("repositoryRelationName", new Class[]{RelationType.class}));

        //page store naming
        context.registerFunction("pagesFolderPath", FlutterHelper.class.getDeclaredMethod("pagesFolderPath", new Class[]{ClassType.class}));
        context.registerFunction("pageStorePath", FlutterHelper.class.getDeclaredMethod("pageStorePath", new Class[]{PageDefinition.class}));
        context.registerFunction("pageBodyPath", FlutterHelper.class.getDeclaredMethod("pageBodyPath", new Class[]{PageDefinition.class, String.class}));
        context.registerFunction("pageStorePackagePath", FlutterHelper.class.getDeclaredMethod("pageStorePackagePath", new Class[]{PageDefinition.class}));
        context.registerFunction("pageStorePackageRelativePath", FlutterHelper.class.getDeclaredMethod("pageStorePackageRelativePath", new Class[]{PageDefinition.class}));
        context.registerFunction("pageLibraryName", FlutterHelper.class.getDeclaredMethod("pageLibraryName", new Class[]{PageDefinition.class}));
        context.registerFunction("tablePath", FlutterHelper.class.getDeclaredMethod("tablePath", new Class[]{PageDefinition.class, String.class, String.class}));
        context.registerFunction("dialogPath", FlutterHelper.class.getDeclaredMethod("dialogPath", new Class[]{PageDefinition.class, String.class}));
        context.registerFunction("dialogTablePath", FlutterHelper.class.getDeclaredMethod("dialogTablePath", new Class[]{PageDefinition.class, String.class}));
        context.registerFunction("dialogTableFileName", FlutterHelper.class.getDeclaredMethod("dialogTableFileName", new Class[]{String.class}));
        context.registerFunction("dialogFileName", FlutterHelper.class.getDeclaredMethod("dialogFileName", new Class[]{String.class}));
        context.registerFunction("pageBodyFileName", FlutterHelper.class.getDeclaredMethod("pageBodyFileName", new Class[]{String.class}));
        context.registerFunction("tableFileName", FlutterHelper.class.getDeclaredMethod("tableFileName", new Class[]{String.class, String.class}));
        context.registerFunction("pageStoreClassName", FlutterHelper.class.getDeclaredMethod("pageStoreClassName", new Class[]{PageDefinition.class}));
        context.registerFunction("pageBodyClassName", FlutterHelper.class.getDeclaredMethod("pageBodyClassName", new Class[]{PageDefinition.class, String.class}));
        context.registerFunction("pageClassName", FlutterHelper.class.getDeclaredMethod("pageClassName", new Class[]{PageDefinition.class}));
        context.registerFunction("pageClassVariableName", FlutterHelper.class.getDeclaredMethod("pageClassVariableName", new Class[]{PageDefinition.class}));
        context.registerFunction("pageStateClassName", FlutterHelper.class.getDeclaredMethod("pageStateClassName", new Class[]{PageDefinition.class}));
        context.registerFunction("pageArgumentsClassName", FlutterHelper.class.getDeclaredMethod("pageArgumentsClassName", new Class[]{PageDefinition.class}));
        context.registerFunction("dialogClassName", FlutterHelper.class.getDeclaredMethod("dialogClassName", new Class[]{PageDefinition.class, String.class}));
        context.registerFunction("dialogTableClassName", FlutterHelper.class.getDeclaredMethod("dialogTableClassName", new Class[]{PageDefinition.class, String.class}));
        context.registerFunction("dialogStoreClassName", FlutterHelper.class.getDeclaredMethod("dialogStoreClassName", new Class[]{PageDefinition.class, String.class}));

        //utilities
        context.registerFunction("utilitiesFolderPath", FlutterHelper.class.getDeclaredMethod("utilitiesFolderPath", new Class[]{ClassType.class}));
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

    public static String getXMIID(VisualElement visualElementType) {
        return ((XMIResource) visualElementType.eResource()).getID(visualElementType);
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

    public static String uriPath(String fqName) {
        return stream(fqName.replaceAll("#", "::")
                .replaceAll("\\.", "::")
                .replaceAll("/", "::")
                .replaceAll("_", "::")
                .split("::"))
                .map(String::toLowerCase)
                .collect(Collectors.joining("-"));
    }

    public static String uriPathWithIdParam(String fqName) {
        return uriPath(fqName) + "/:id";
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
                .replaceAll("_", "::")
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

    @Deprecated
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

    @Deprecated
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

    public static boolean tableNavigateToViewHasIdParam(PageDefinition page) {
        return (page.getIsPageTypeTable() || page.getIsPageTypeDashboard()) && ((RelationType)page.getDataElement()).isIsAccess();
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

    public static boolean isBookmarkablePage(PageDefinition page){
        return page.getIsPageTypeDashboard() || isAccessTablePage(page) || isAccessViewPage(page);
    }

    public static boolean isAccessTablePage(PageDefinition page){
        if (page.getRelationType() == null) return false;
        return page.getIsPageTypeDashboard() || (page.getIsPageTypeTable() && page.getRelationType().isIsAccess());
    }

    public static boolean isAccessViewPage(PageDefinition page){
        if (page.getRelationType() == null) return false;
        return page.getIsPageTypeDashboard() || (page.getIsPageTypeView() && page.getRelationType().isIsAccess());
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

    public static List<String> getAttributeTypeNamesFromColumnsAndAttributes(EList<Filter> filterList, EList<AttributeType> attributeTypeList) {
        List<String> attributeNameList = filterList
                .stream()
                .map(filter -> filter.getAttributeType().getName())
                .collect(Collectors.toList());

        for (AttributeType element : attributeTypeList ) {
            if (element.isIsRequired()) {
                if (!attributeNameList.contains(element.getName())) {
                    attributeNameList.add(element.getName());
                }
            }
        }

        return attributeNameList;
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

    private static String getCamelCaseVersion(String token) {
        return StringUtils.capitalize(stream(token.split("_")).map(t -> StringUtils.capitalize(t)).collect(Collectors.joining()));
    }

    private static String getFileNameVersion(String token) {
        return token.replaceAll("([a-z])([A-Z]+)", "$1_$2").toLowerCase();
    }

    private static String getClassNameOfTokens(List<String> tokens) {
        return tokens.stream().map(t -> getCamelCaseVersion(t)).collect(Collectors.joining());
    }

    private static String getClassName(ClassType type) {
        return getClassNameOfTokens(type.getPackageNameTokens())
                .concat(getCamelCaseVersion(type.getSimpleName()));
    }

    private static String getClassName(RelationType relation) {
        return getClassNameOfTokens(relation.getOwnerPackageNameTokens())
                .concat(getCamelCaseVersion(relation.getOwnerSimpleName())).concat(getCamelCaseVersion(relation.getName()));
    }

    private static String getClassName(OperationType operation) {
        return getClassNameOfTokens(operation.getOwnerPackageNameTokens())
                .concat(getCamelCaseVersion(operation.getOwnerSimpleName())).concat(getCamelCaseVersion(operation.getName()));
    }

    private static String getPathOfTokens(List<String> tokens) {
        if(tokens.isEmpty()) {
            return "";
        } else {
            return tokens.stream().map(t -> getFileNameVersion(t)).collect(Collectors.joining("/")).concat("/");
        }
    }

    private static String getTypeNamePath(ClassType type) {
        List<String> tokens = type.getPackageNameTokens();
        tokens.add(type.getSimpleName());
        return getPathOfTokens(tokens);
    }

    private static String getTypeNamePath(RelationType relation) {
        List<String> tokens = relation.getOwnerPackageNameTokens();
        tokens.add(relation.getOwnerSimpleName());
        return getPathOfTokens(tokens);
    }

    public static String getPageTypePath(PageDefinition page) {
        List<String> tokens =  new ArrayList<>();
        if (page.getDataElement() != null && !(page.getPageType().equals(PageType.OPERATION_INPUT) || page.getPageType().equals(PageType.OPERATION_OUTPUT))) {
            RelationType dataElement = (RelationType) page.getDataElement();
            tokens.addAll(dataElement.getOwnerPackageNameTokens());
            tokens.add(dataElement.getOwnerSimpleName());
            tokens.add(dataElement.getName());
        } else if (page.getPageType().equals(PageType.OPERATION_INPUT) || page.getPageType().equals(PageType.OPERATION_OUTPUT)){
            OperationType dataElement = (OperationType) (page.getDataElement().eContainer());
            tokens.addAll(dataElement.getOwnerPackageNameTokens());
            tokens.add(dataElement.getOwnerSimpleName());
            tokens.add(dataElement.getName());
        } else {
            ClassType actor = ((Application)page.eContainer()).getActor();
            tokens.addAll(actor.getPackageNameTokens());
            tokens.add(actor.getSimpleName());
        }
        tokens.add(page.getPageType().toString().toLowerCase());
        return getPathOfTokens(tokens);
    }

    //store naming

    public static String storeFolderPath(ClassType actor) {
        return getPathOfTokens(new ArrayList<>(Arrays.asList("lib", actor.getSimpleName(), "store")));
    }

    public static String storeClassRelativePath(ClassType type) {
        return getPathOfTokens(type.getPackageNameTokens()).concat(getFileNameVersion(type.getSimpleName()).concat("__store.dart"));
    }

    public static String storeClassPath(ClassType actor, ClassType type) {
        return storeFolderPath(actor).concat(storeClassRelativePath(type));
    }

    public static String storeClassName(ClassType type) {
        return getClassName(type).concat("Store");
    }

    //repository naming

    public static String repositoryFolderPath(ClassType actor) {
        return getPathOfTokens(new ArrayList<>(Arrays.asList("lib", actor.getSimpleName(), "repository")));
    }

    public static String repositoryClassRelativePath(ClassType type) {
        return getTypeNamePath(type).concat("repository.dart");
    }

    public static String repositoryClassPath(ClassType actor, ClassType type) {
        return repositoryFolderPath(actor).concat(repositoryClassRelativePath(type));
    }

    public static String repositoryRelationRelativePath(RelationType relation) {
        return getTypeNamePath(relation)
                .concat(getFileNameVersion(relation.getName()).concat("__repository.dart"));
    }

    public static String repositoryRelationPath(ClassType actor, RelationType relation) {
        return repositoryFolderPath(actor).concat(repositoryRelationRelativePath(relation));
    }

    public static String repositoryClassName(ClassType type) {
        return getClassName(type).concat("Repository");
    }

    public static String repositoryStoreMapperClassName(ClassType type) {
        return repositoryClassName(type).concat("StoreMapper");
    }

    public static String repositoryRelationName(RelationType relation) {
        return getClassName(relation).concat("Repository");
    }

    //page store naming

    private static String getPageClassName(PageDefinition page) {
        if (page.getDataElement() != null && !(page.getPageType().equals(PageType.OPERATION_INPUT) || page.getPageType().equals(PageType.OPERATION_OUTPUT))) {
            RelationType dataElement = (RelationType) page.getDataElement();
            return getClassName(dataElement).concat(StringUtils.capitalize(page.getPageType().toString().toLowerCase()));
        } else if (page.getPageType().equals(PageType.OPERATION_INPUT) || page.getPageType().equals(PageType.OPERATION_OUTPUT)){
            OperationType dataElement = (OperationType) (page.getDataElement().eContainer());
            return getClassName(dataElement).concat(StringUtils.capitalize(page.getPageType().toString().toLowerCase()));
        }
        ClassType actor = ((Application)page.eContainer()).getActor();
        return getClassName(actor).concat(StringUtils.capitalize(page.getPageType().toString().toLowerCase()));
    }

    public static String pagesFolderPath(ClassType actor) {
        return getPathOfTokens(new ArrayList<>(Arrays.asList("lib", actor.getSimpleName(), "ui", "pages")));
    }

    public static String pageStoreRelativePath(PageDefinition page) {
        return getPageTypePath(page).concat("page.dart");
    }

    public static String pageStorePackageRelativePath(PageDefinition page) {
        return getPageTypePath(page).concat("package.dart");
    }

    public static String pageStorePath(PageDefinition page) {
        return pagesFolderPath(((Application)page.eContainer()).getActor()).concat(pageStoreRelativePath(page));
    }

    public static String pageStorePackagePath(PageDefinition page) {
        return pagesFolderPath(((Application)page.eContainer()).getActor()).concat(pageStorePackageRelativePath(page));
    }

    public static String pageStoreClassName(PageDefinition page) {
        return getPageClassName(page).concat("PageStore");
    }

    public static String pageClassName(PageDefinition page) {
        return getPageClassName(page).concat("Page");
    }

    public static String pageClassVariableName(PageDefinition page) {
        String className = getPageClassName(page).concat("Page");
        return className.substring(0, 1).toLowerCase() + className.substring(1);
    }

    public static String pageStateClassName(PageDefinition page) {
        return getPageClassName(page).concat("PageState");
    }

    public static String pageArgumentsClassName(PageDefinition page) {
        return getPageClassName(page).concat("PageArguments");
    }

    public static String pageLibraryName(PageDefinition page) {
        String lib = getPageTypePath(page).replaceAll("/",".");
        return getFileNameVersion(((Application)page.eContainer()).getActor().getSimpleName()).concat(".ui.pages.").concat(lib.substring(0, lib.length() - 1));
    }

    public static String pageBodyRelativePath(PageDefinition page, String layoutTypeName) {
        return getPageTypePath(page).concat(pageBodyFileName(layoutTypeName));
    }

    public static String pageBodyFileName(String layoutTypeName) {
        return layoutTypeName.toLowerCase().concat("/body.dart");
    }

    public static String pageBodyPath(PageDefinition page, String layoutTypeName) {
        return pagesFolderPath(((Application)page.eContainer()).getActor()).concat(pageBodyRelativePath(page, layoutTypeName));
    }

    public static String pageBodyClassName(PageDefinition page, String layoutTypeName) {
        return getPageClassName(page).concat(StringUtils.capitalize(layoutTypeName.toLowerCase())).concat("Page");
    }

    public static String tableRelativePath(PageDefinition page, String layoutTypeName, String relationName) {
        return getPageTypePath(page).concat(tableFileName(layoutTypeName, relationName));
    }

    public static String tableFileName(String layoutTypeName, String relationName) {
        return layoutTypeName.toLowerCase().concat("/").concat(getFileNameVersion(relationName).concat("__table.dart"));
    }

    public static String tablePath(PageDefinition page, String layoutTypeName, String relationName) {
        return pagesFolderPath(((Application)page.eContainer()).getActor()).concat(tableRelativePath(page, layoutTypeName, relationName));
    }

    public static String dialogRelativePath(PageDefinition page, String relationName) {
        return getPageTypePath(page).concat(dialogFileName(relationName));
    }

    public static String dialogFileName(String relationName) {
        return "dialogs/".concat(getFileNameVersion(relationName)).concat(".dart");
    }

    public static String dialogPath(PageDefinition page, String relationName) {
        return pagesFolderPath(((Application)page.eContainer()).getActor()).concat(dialogRelativePath(page, relationName));
    }

    public static String dialogClassName(PageDefinition page, String relationName) {
        if (page.getIsPageTypeTable()) {
            return getPageClassName(page).concat("Dialog");
        }
        return getPageClassName(page).concat(getCamelCaseVersion(relationName)).concat("Dialog");
    }

    public static String dialogStoreClassName(PageDefinition page, String relationName) {
        if (page.getIsPageTypeTable()) {
            return getPageClassName(page).concat("DialogStore");
        }
        return getPageClassName(page).concat(getCamelCaseVersion(relationName)).concat("DialogStore");
    }

    public static String dialogTableRelativePath(PageDefinition page, String relationName) {
        return getPageTypePath(page).concat(dialogTableFileName(relationName));
    }

    public static String dialogTableFileName(String relationName) {
        return "dialogs/".concat(getFileNameVersion(relationName)).concat("__table.dart");
    }

    public static String dialogTableClassName(PageDefinition page, String relationName) {
        if (page.getIsPageTypeTable()) {
            return getPageClassName(page).concat("DialogTable");
        }
        return getPageClassName(page).concat(getCamelCaseVersion(relationName)).concat("DialogTable");
    }

    public static String dialogTablePath(PageDefinition page, String relationName) {
        return pagesFolderPath(((Application)page.eContainer()).getActor()).concat(dialogTableRelativePath(page, relationName));
    }

    // ui utilities

    public static String utilitiesFolderPath(ClassType actor) {
        return getPathOfTokens(new ArrayList<>(Arrays.asList("lib", actor.getSimpleName(), "ui", "utilities")));
    }
}
