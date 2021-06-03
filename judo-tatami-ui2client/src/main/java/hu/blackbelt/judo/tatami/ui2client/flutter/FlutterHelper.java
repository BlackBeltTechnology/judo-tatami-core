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
        context.registerFunction("storeFolderPath", FlutterHelper.class.getDeclaredMethod("storeFolderPath", new Class[]{String.class}));
        context.registerFunction("storeClassRelativePath", FlutterHelper.class.getDeclaredMethod("storeClassRelativePath", new Class[]{String.class}));
        context.registerFunction("storeClassPath", FlutterHelper.class.getDeclaredMethod("storeClassPath", new Class[]{String.class, String.class}));
        context.registerFunction("storeClassName", FlutterHelper.class.getDeclaredMethod("storeClassName", new Class[]{String.class}));

        //repository naming
        context.registerFunction("repositoryFolderPath", FlutterHelper.class.getDeclaredMethod("repositoryFolderPath", new Class[]{String.class}));
        context.registerFunction("repositoryClassPath", FlutterHelper.class.getDeclaredMethod("repositoryClassPath", new Class[]{String.class, String.class}));
        context.registerFunction("repositoryRelationPath", FlutterHelper.class.getDeclaredMethod("repositoryRelationPath", new Class[]{String.class, String.class, String.class}));
        context.registerFunction("repositoryClassRelativePath", FlutterHelper.class.getDeclaredMethod("repositoryClassRelativePath", new Class[]{String.class}));
        context.registerFunction("repositoryRelationRelativePath", FlutterHelper.class.getDeclaredMethod("repositoryRelationRelativePath", new Class[]{String.class, String.class}));
        context.registerFunction("repositoryClassName", FlutterHelper.class.getDeclaredMethod("repositoryClassName", new Class[]{String.class}));
        context.registerFunction("repositoryStoreMapperClassName", FlutterHelper.class.getDeclaredMethod("repositoryStoreMapperClassName", new Class[]{String.class}));
        context.registerFunction("repositoryRelationName", FlutterHelper.class.getDeclaredMethod("repositoryRelationName", new Class[]{String.class, String.class}));

        //page store naming
        context.registerFunction("pagesFolderPath", FlutterHelper.class.getDeclaredMethod("pagesFolderPath", new Class[]{String.class}));
        context.registerFunction("pagesUtilitiesPath", FlutterHelper.class.getDeclaredMethod("pagesUtilitiesPath", new Class[]{String.class}));
        context.registerFunction("pageStorePath", FlutterHelper.class.getDeclaredMethod("pageStorePath", new Class[]{String.class, String.class}));
        context.registerFunction("pageBodyPath", FlutterHelper.class.getDeclaredMethod("pageBodyPath", new Class[]{String.class, String.class, String.class}));
        context.registerFunction("pageStorePackagePath", FlutterHelper.class.getDeclaredMethod("pageStorePackagePath", new Class[]{String.class, String.class}));
        context.registerFunction("pageStorePackageRelativePath", FlutterHelper.class.getDeclaredMethod("pageStorePackageRelativePath", new Class[]{String.class}));
        context.registerFunction("pageLibraryName", FlutterHelper.class.getDeclaredMethod("pageLibraryName", new Class[]{String.class, String.class}));
        context.registerFunction("tablePath", FlutterHelper.class.getDeclaredMethod("tablePath", new Class[]{String.class, String.class, String.class, String.class}));
        context.registerFunction("dialogPath", FlutterHelper.class.getDeclaredMethod("dialogPath", new Class[]{String.class, String.class, String.class}));
        context.registerFunction("dialogTablePath", FlutterHelper.class.getDeclaredMethod("dialogTablePath", new Class[]{String.class, String.class, String.class}));
        context.registerFunction("dialogTableFileName", FlutterHelper.class.getDeclaredMethod("dialogTableFileName", new Class[]{String.class}));
        context.registerFunction("dialogFileName", FlutterHelper.class.getDeclaredMethod("dialogFileName", new Class[]{String.class}));
        context.registerFunction("pageBodyFileName", FlutterHelper.class.getDeclaredMethod("pageBodyFileName", new Class[]{String.class}));
        context.registerFunction("tableFileName", FlutterHelper.class.getDeclaredMethod("tableFileName", new Class[]{String.class, String.class}));
        context.registerFunction("pageStoreClassName", FlutterHelper.class.getDeclaredMethod("pageStoreClassName", new Class[]{String.class}));
        context.registerFunction("pageBodyClassName", FlutterHelper.class.getDeclaredMethod("pageBodyClassName", new Class[]{String.class, String.class}));
        context.registerFunction("pageClassName", FlutterHelper.class.getDeclaredMethod("pageClassName", new Class[]{String.class}));
        context.registerFunction("pageClassVariableName", FlutterHelper.class.getDeclaredMethod("pageClassVariableName", new Class[]{String.class}));
        context.registerFunction("pageStateClassName", FlutterHelper.class.getDeclaredMethod("pageStateClassName", new Class[]{String.class}));
        context.registerFunction("pageArgumentsClassName", FlutterHelper.class.getDeclaredMethod("pageArgumentsClassName", new Class[]{String.class}));
        context.registerFunction("tableClassName", FlutterHelper.class.getDeclaredMethod("tableClassName", new Class[]{String.class, String.class, String.class}));
        context.registerFunction("dialogClassName", FlutterHelper.class.getDeclaredMethod("dialogClassName", new Class[]{PageDefinition.class, String.class}));
        context.registerFunction("dialogTableClassName", FlutterHelper.class.getDeclaredMethod("dialogTableClassName", new Class[]{PageDefinition.class, String.class}));
        context.registerFunction("dialogStoreClassName", FlutterHelper.class.getDeclaredMethod("dialogStoreClassName", new Class[]{PageDefinition.class, String.class}));

        //utilities
        context.registerFunction("utilitiesFolderPath", FlutterHelper.class.getDeclaredMethod("utilitiesFolderPath", new Class[]{String.class}));
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

    /**
     * Calculates class name based on an ESM fq name including feature name separated with "." as feature separator.
     * Removes '_' from esm named element names
     *
     * @param fqName the fully qualified name of a type with "::" as namespace separators and "." as feature separator, names match the pattern "[a-zA-Z0-9_]+"
     * @return camel case class name including feature name, e.g. Model::Package_a::Package_b::TypeName.feature -> PackageAPackageBTypeNameFeature
     */
    private static String getClassName(String fqName) {
        String[] splitAtFeatureSeparator = fqName.split("\\.");
        String[] splitAtNsSeparator = splitAtFeatureSeparator[0].split("::");
        return stream(splitAtNsSeparator)
                .skip(Math.min(1, splitAtNsSeparator.length - 1)) //skip model name if necessary
                .map(s -> StringUtils.capitalize(stream(s.split("_")).map(t -> StringUtils.capitalize(t)).collect(Collectors.joining())))
                .collect(Collectors.joining())
                .concat(splitAtFeatureSeparator.length > 1 ?
                                StringUtils.capitalize(stream(splitAtFeatureSeparator[1].split("_")).map(t -> StringUtils.capitalize(t)).collect(Collectors.joining())) :
                                ""
                        );
    }

    /**
     * Calculates file name based on the ESM fq name without feature name, skipping model and package names.
     *
     * @param fqName the fully qualified name of a type with "::" as namespace separators, names match the pattern "[a-zA-Z0-9_]+"
     * @return file name based on fq name, e.g. Model::PackageOne::PackageTwo::TypeName -> type_name
     */
    private static String getFileName(String fqName) {
        String[] splitted = fqName.split("::");
        return stream(splitted)
                .skip(Math.max(0, splitted.length - 1))
                .collect(Collectors.joining())
                .replaceAll("([a-z])([A-Z]+)", "$1_$2")
                .toLowerCase();
    }

    /**
     * Calculates relative path of a type. Path is relative to the model.
     *
     * @param fqName the fully qualified name of a type with "::" as namespace separators, names match the pattern "[a-zA-Z0-9_]+"
     * @return the packages of a type joined by "/", e.g. package1/package2/.../packagen/ if the type is enclosed by packages and empty string if it's not
     */
    private static String getPackagePath(String fqName) {
        List<String> packageNameTokens = getPackageNameTokens(fqName);
        if (packageNameTokens.isEmpty()) {
            return "";
        }
        return packageNameTokens.stream()
                .map(t -> getFileName(t))
                .collect(Collectors.joining("/"))
                .concat("/");
    }

    /**
     * Calculates relative path of a type, based on {@link #getPackagePath(String)} and concatenates type name.
     *
     * @param fqName the fully qualified name of a type with "::" as namespace separators, names match the pattern "[a-zA-Z0-9_]+"
     * @return the relative path of a type including the type name, e.g. package1/package2/.../packagen/type_name/ or typeName/ if there are no packages
     */
    private static String getTypeNamePath(String fqName) {
        return getPackagePath(fqName).concat(getFileName(fqName)).concat("/");
    }

    /**
     * Calculates relative path of a type including the type name, based on {@link #getTypeNamePath(String)} and concatenates the given feature name.
     *
     * @param fqName the fully qualified name of a type with "::" as namespace separators
     * @return the relative path of a type including the type name and the feature name, e.g. package1/package2/.../packagen/fqName/featureName/ or fqName/featureName/ if there are no packages
     */
    private static String getFeaturePath(String fqName) {
        String[] splitAtFeatureSeparator = fqName.split("\\.");
        return getTypeNamePath(splitAtFeatureSeparator[0])
                .concat(splitAtFeatureSeparator.length > 1 ? getFileName(splitAtFeatureSeparator[1]).concat("/") : "");
    }

    /**
     * Calculates relative path of a page, which includes fully qualified type name, relation or operation name and page type.
     *
     * @param pageName fq name of page, which consists of fq type name, feature name (operation or relation) and page type, e.g. Model::Package::Type.feature#PageType or Model::Package::Type#Dashboard
     * @return type name path or feature path and page type concatenated, e.g package/type/feature/pagetype/ or empty string if page name does not fit the above convention
     */
    private static String getPageTypePath(String pageName) {
        String[] pageNameTokens = pageName.split("#");
        if (pageNameTokens.length == 2) {
            return getFeaturePath(pageNameTokens[0]).concat(pageNameTokens[1].toLowerCase()).concat("/");
        }
        return "";
    }

    //store naming

    /**
     * Calculates the relative path of the "store" folder. Uses  {@link #path(String)} to calculate actor name.
     *
     * @param actorName name of the actor of the application
     * @return relative path of "store" folder, e.g. lib/actor/store/
     */
    public static String storeFolderPath(String actorName) {
        return "lib/"
            .concat(getFileName(actorName))
            .concat("/store/");
    }

    /**
     * Calculates folder structure based on the package structure using {@link #getPackagePath(String)} and concatenates Store class name.
     * Path is relative to store folder.
     *
     * @param fqName the fully qualified name of a type with "::" as namespace separators
     * @return relative path of a Store class, e.g. package_name1/package_name2/../type_name__store.dart
     */
    public static String storeClassRelativePath(String fqName) {
        return getPackagePath(fqName).concat(getFileName(fqName) + "__store.dart");
    }

    /**
     * Calculates the relative path of a Store class. Uses {@link #storeFolderPath(String)} to calculate relative path of "store" folder
     * and {@link #storeClassRelativePath(String)} to calculate folder structure based on the package structure and the file name of the Store class.
     *
     * @param actorName name of the actor of the application
     * @param fqName the fully qualified name of a type with "::" as namespace separators
     * @return relative path of a Store class, e.g. lib/actor/store/package_name1/package_name2/../type_name__store.dart
     */
    public static String storeClassPath(String actorName, String fqName) {
        return storeFolderPath(actorName).concat(storeClassRelativePath(fqName));
    }

    /**
     * Calculates the Store class name. Uses {@link #fqClassWithoutModel(String)} to remove model name from the fully qualified name and capitalizes package names.
     *
     * @param fqName the fully qualified name of a type with "::" as namespace separators
     * @return the class name of a Store class, e.g. Package1Package2..TypeNameStore
     */
    public static String storeClassName(String fqName) {
        return getClassName(fqName) + "Store";
    }

    //repository naming

    /**
     * Calculates the relative path of the "repository" folder. Uses  {@link #path(String)} to calculate actor name.
     *
     * @param actorName name of the actor of the application
     * @return relative path of "repository" folder, e.g. lib/actor/repository/
     */
    public static String repositoryFolderPath(String actorName) {
        return "lib/"
                .concat(getFileName(actorName))
                .concat("/repository/");
    }

    /**
     * Calculates folder structure based on the package structure and type name using {@link #getTypeNamePath(String)}
     * for the path. Path is relative to repository folder.
     *
     * @param fqName the fully qualified name of a type with "::" as namespace separators
     * @return relative path of a Repository class, e.g. package_name1/package_name2/../type_name/repository.dart
     */
    public static String repositoryClassRelativePath(String fqName) {
        return getTypeNamePath(fqName).concat("repository.dart");
    }

    /**
     * Calculates the path of a Repository class. Uses {@link #repositoryFolderPath(String)} to calculate relative path of "repository" folder
     * and {@link #repositoryClassRelativePath(String)} to calculate folder structure and file name.
     *
     * @param actorName name of the actor of the application
     * @param fqName the fully qualified name of a type with "::" as namespace separators
     * @return path of a Repository class, e.g. lib/actor/repository/package_name1/package_name2/../type_name/repository.dart
     */
    public static String repositoryClassPath(String actorName, String fqName) {
        return repositoryFolderPath(actorName).concat(repositoryClassRelativePath(fqName));
    }

    /**
     * Calculates folder structure based on the package structure and type name using {@link #getTypeNamePath(String)}
     * and concatenates file name based on relation name.
     *
     * @param relationName name of the relation
     * @param ownerName the fully qualified name of owner of the relation with "::" as namespace separators
     * @return relative path of a Repository class, e.g. package_name1/package_name2/../type_name/relation_name__repository.dart
     */
    public static String repositoryRelationRelativePath(String ownerName, String relationName) {
        return getTypeNamePath(ownerName).concat(getFileName(relationName).concat("__repository.dart"));
    }

    /**
     * Calculates the relative path of a Repository class. Uses {@link #repositoryFolderPath(String)} and {@link #repositoryRelationRelativePath(String, String)}.
     *
     * @param actorName name of the actor of the application
     * @param ownerName the fq name of the owner of the relation
     * @param relationName name of the relation
     * @return path of a Repository class, e.g. lib/actor/repository/package_name1/package_name2/../type_name/relation_name__repository.dart
     */
    public static String repositoryRelationPath(String actorName, String ownerName, String relationName) {
        return repositoryFolderPath(actorName).concat(repositoryRelationRelativePath(ownerName, relationName));
    }

    /**
     * Calculates the Repository class name. Uses {@link #fqClassWithoutModel(String)} to remove model name from the fully qualified name and capitalizes package names.
     *
     * @param fqName the fully qualified name of a type with "::" as namespace separators
     * @return the class name of a Repository class, e.g. Package1Package2..TypeNameRepository
     */
    public static String repositoryClassName(String fqName) {
        return getClassName(fqName).concat("Repository");
    }

    /**
     * Calculates the RepositoryStoreMapper class name. Uses {@link #repositoryClassName(String)} to calculate Repository class name and concatenates "StoreMapper" to it.
     *
     * @param fqName the fully qualified name of a type with "::" as namespace separators
     * @return the class name of a RepositoryStoreMapper class, e.g. Package1Package2..TypeNameRepositoryStoreMapper
     */
    public static String repositoryStoreMapperClassName(String fqName) {
        return repositoryClassName(fqName).concat("StoreMapper");
    }

    /**
     * Calculates the Repository class name for relations.
     * Uses {@link #fqClassWithoutModel(String)} to remove model name from the fully qualified name of the owner name and capitalizes package names, then adds the relation name capitalized.
     *
     * @param ownerName the fully qualified name of the relation's owner with "::" as namespace separators
     * @param relationName the name of a the relation
     * @return the class name of a Repository class, e.g. Package1Package2..TypeNameRelationNameRepository
     */
    public static String repositoryRelationName(String ownerName, String relationName) {
        return getClassName(ownerName).concat(StringUtils.capitalize(relationName)).concat("Repository");
    }

    //page store naming

    private static String getPageClassName(String pageName) {
        String[] pageNameTokens = pageName.split("#");
        if (pageNameTokens.length == 2) {
            return getClassName(pageNameTokens[0])
                    .concat(StringUtils.capitalize(pageNameTokens[1].toLowerCase()));
        }
        return "";
    }

    /**
     * Calculates the relative path of the "pages" folder. Uses {@link #path(String)} to calculate actor name.
     *
     * @param actorName name of the actor of the application
     * @return relative path of "pages" folder, e.g. lib/actor/ui/pages/
     */
    public static String pagesFolderPath(String actorName) {
        return "lib/"
                .concat(getFileName(actorName))
                .concat("/ui/pages/");
    }

    /**
     * Calculates the relative path of the "pages/utilities" folder. Uses {@link #pagesFolderPath(String)} to calculate "pages" path.
     *
     * @param actorName name of the actor of the application
     * @return relative path of "pages" folder, e.g. lib/actor/ui/pages/utilities/
     */
    public static String pagesUtilitiesPath(String actorName) {
        return pagesFolderPath(actorName).concat("utilities/");
    }

    /**
     * Calculates relative path of page stores based on {@link #getPageTypePath(String)} and concatenates "page.dart". Path is relative to the "pages" folder.
     *
     * @param pageName fq name of page, which consists of fq type name, feature name (operation or relation) and page type, e.g. Model::Package::Type.feature#PageType or Model::Package::Type#Dashboard
     * @return relative path of a page store, e.g. lib/actor/ui/pages/package_name1/package_name2/../type_name/relation_name/pageType/page.dart
     */
    public static String pageStoreRelativePath(String pageName) {
        return getPageTypePath(pageName).concat("page.dart");
    }

    /**
     * Calculates relative path of page folder package based on {@link #getPageTypePath(String)} and concatenates "package.dart". Path is relative to the "pages" folder.
     *
     * @param pageName fq name of page, which consists of fq type name, feature name (operation or relation) and page type, e.g. Model::Package::Type.feature#PageType or Model::Package::Type#Dashboard
     * @return relative path of a package collecting page stores, bodies, dialogs, etc., e.g. lib/actor/ui/pages/package_name1/package_name2/../type_name/relation_name/pageType/package.dart
     */
    public static String pageStorePackageRelativePath(String pageName) {
        return getPageTypePath(pageName).concat("package.dart");
    }

    /**
     * Calculates path of page store by concatenating {@link #pagesFolderPath(String)} and {@link #pageStoreRelativePath(String)}
     *
     * @param actorName name of the actor of the application
     * @param pageName fq name of page, which consists of fq type name, feature name (operation or relation) and page type, e.g. Model::Package::Type.feature#PageType or Model::Package::Type#Dashboard
     * @return e.g. lib/actor/ui/pages/package_name1/package_name2/../type_name/relation_name/pageType/store.dart
     */
    public static String pageStorePath(String actorName, String pageName) {
        return pagesFolderPath(actorName).concat(pageStoreRelativePath(pageName));
    }

    /**
     * Calculates path of page packages by concatenating {@link #pagesFolderPath(String)} and {@link #pageStorePackageRelativePath(String)}.
     *
     * @param actorName name of the actor of the application
     * @param pageName fq name of page, which consists of fq type name, feature name (operation or relation) and page type, e.g. Model::Package::Type.feature#PageType or Model::Package::Type#Dashboard
     * @return e.g. package_name1/package_name2/.../type_name/relation_name/pageType/package.dart
     */
    public static String pageStorePackagePath(String actorName, String pageName) {
        return pagesFolderPath(actorName).concat(pageStorePackageRelativePath(pageName));
    }

    /**
     * Calculates the page store class name.
     *
     * @param pageName fq name of page, which consists of fq type name, feature name (operation or relation) and page type, e.g. Model::Package::Type.feature#PageType or Model::Package::Type#Dashboard
     * @return e.g. PackageNameTypeNameRelationNamePageTypePageStore
     */
    public static String pageStoreClassName(String pageName) {
        return getPageClassName(pageName).concat("PageStore");
    }

    /**
     * Calculates the page class name.
     *
     * @param pageName fq name of page, which consists of fq type name, feature name (operation or relation) and page type, e.g. Model::Package::Type.feature#PageType or Model::Package::Type#Dashboard
     * @return e.g. PackageNameTypeNameRelationNamePageTypePage
     */
    public static String pageClassName(String pageName) {
        return getPageClassName(pageName).concat("Page");
    }

    /**
     * Calculates variable name for the page class name.
     *
     * @param pageName fq name of page, which consists of fq type name, feature name (operation or relation) and page type, e.g. Model::Package::Type.feature#PageType or Model::Package::Type#Dashboard
     * @return e.g. packageNameTypeNameRelationNamePageTypePage
     */
    public static String pageClassVariableName(String pageName) {
        String className = getPageClassName(pageName).concat("Page");
        return className.substring(0, 1).toLowerCase() + className.substring(1);
    }

    /**
     * Calculates the page state class name.
     *
     * @param pageName fq name of page, which consists of fq type name, feature name (operation or relation) and page type, e.g. Model::Package::Type.feature#PageType or Model::Package::Type#Dashboard
     * @return PackageNameTypeNameRelationNamePageTypePageState
     */
    public static String pageStateClassName(String pageName) {
        return getPageClassName(pageName).concat("PageState");
    }

    /**
     * Calculates the page arguments class name.
     *
     * @param pageName fq name of page, which consists of fq type name, feature name (operation or relation) and page type, e.g. Model::Package::Type.feature#PageType or Model::Package::Type#Dashboard
     * @return e.g. PackageNameTypeNameRelationNamePageTypePageArguments
     */
    public static String pageArgumentsClassName(String pageName) {
        return getPageClassName(pageName).concat("PageArguments");
    }

    /**
     * Calculates library name of page packages which include page stores, bodies, dialogs, tables, etc of a page.
     *
     * @param actorName name of the actor of the application
     * @param pageName fq name of page, which consists of fq type name, feature name (operation or relation) and page type, e.g. Model::Package::Type.feature#PageType or Model::Package::Type#Dashboard
     * @return library name of a package collecting page stores, bodies, dialogs, etc., e.g. package_name1.package_name2....type_name.relation_name.pagetype
     */
    public static String pageLibraryName(String actorName, String pageName) {
        String lib = getPageTypePath(pageName).replaceAll("/",".");
        return getFileName(actorName).concat(".ui.pages.").concat(lib.substring(0, lib.length() - 1));
    }

    /**
     * Calculates relative path of page body based on {@link #getPageTypePath(String)} and {@link #pageBodyFileName(String)}
     *
     * @param pageName fq name of page, which consists of fq type name, feature name (operation or relation) and page type, e.g. Model::Package::Type.feature#PageType or Model::Package::Type#Dashboard
     * @param layoutTypeName the name of the layout: mobile, tablet, desktop
     * @return e.g. package_name1/package_name2/../type_name/relation_name/pageType/layoutName/body.dart
     */
    public static String pageBodyRelativePath(String pageName, String layoutTypeName) {
        return getPageTypePath(pageName).concat(pageBodyFileName(layoutTypeName));
    }

    /**
     * Calculates page body name based on the layout type.
     *
     * @param layoutTypeName the name of the layout: mobile, tablet, desktop
     * @return e.g. layoutName/body.dart
     */
    public static String pageBodyFileName(String layoutTypeName) {
        return layoutTypeName.toLowerCase().concat("/body.dart");
    }

    /**
     * Calculates path of page body by concatenating {@link #pagesFolderPath(String)} and {@link #pageBodyRelativePath(String, String)}
     *
     * @param actorName name of the actor of the application
     * @param pageName fq name of page, which consists of fq type name, feature name (operation or relation) and page type, e.g. Model::Package::Type.feature#PageType or Model::Package::Type#Dashboard
     * @param layoutTypeName the name of the layout: mobile, tablet, desktop
     * @return e.g. lib/actor/ui/pages/package_name1/package_name2/../type_name/relation_name/pageType/layoutTypeName/body.dart
     */
    public static String pageBodyPath(String actorName, String pageName, String layoutTypeName) {
        return pagesFolderPath(actorName).concat(pageBodyRelativePath(pageName, layoutTypeName));
    }

    /**
     * Calculates the page body class name.
     *
     * @param pageName fq name of page, which consists of fq type name, feature name (operation or relation) and page type, e.g. Model::Package::Type.feature#PageType or Model::Package::Type#Dashboard
     * @param layoutTypeName the name of the layout: mobile, tablet, desktop
     * @return e.g. PackageNameTypeNameRelationNamePageTypeLayoutTypePage
     */
    public static String pageBodyClassName(String pageName, String layoutTypeName) {
        return getPageClassName(pageName).concat(StringUtils.capitalize(layoutTypeName.toLowerCase())).concat("Page");
    }

    /**
     * Calculates relative path of a data table based on {@link #getPageTypePath(String)} and {@link #tableFileName(String, String)}.
     *
     * @param pageName fq name of page, which consists of fq type name, feature name (operation or relation) and page type, e.g. Model::Package::Type.feature#PageType or Model::Package::Type#Dashboard
     * @param layoutTypeName the name of the layout: mobile, tablet, desktop
     * @param relationName name of the data element of the table, i.e. the name of the relation
     * @return e.g. package_name1/package_name2/../type_name/relation_name/pageType/layoutTypeName/relation_name_table.dart
     */
    public static String tableRelativePath(String pageName, String layoutTypeName, String relationName) {
        return getPageTypePath(pageName).concat(tableFileName(layoutTypeName, relationName));
    }

    /**
     * Calculates table file name based on the layout type and the name of its data element.
     *
     * @param layoutTypeName the name of the layout: mobile, tablet, desktop
     * @param relationName name of the data element of the table, i.e. the name of the relation
     * @return e.g. layoutTypeName/tableName_table.dart
     */
    public static String tableFileName(String layoutTypeName, String relationName) {
        return layoutTypeName.toLowerCase().concat("/").concat(getFileName(relationName).concat("__table.dart"));
    }

    /**
     * Calculates path of a table data info by concatenating {@link #pagesFolderPath(String)} and {@link #tableRelativePath(String, String, String)}
     *
     * @param actorName name of the actor of the application
     * @param pageName fq name of page, which consists of fq type name, feature name (operation or relation) and page type, e.g. Model::Package::Type.feature#PageType or Model::Package::Type#Dashboard
     * @param layoutTypeName the name of the layout: mobile, tablet, desktop
     * @param relationName name of the data element of the table, i.e. the name of the relation
     * @return e.g. lib/actor/ui/pages/package_name1/package_name2/../type_name/relation_name/pageType/layoutTypeName/relation_name__table.dart
     */
    public static String tablePath(String actorName, String pageName, String layoutTypeName, String relationName) {
        return pagesFolderPath(actorName).concat(tableRelativePath(pageName, layoutTypeName, relationName));
    }

    /**
     * Calculates table data info class name.
     *
     * @param pageName fq name of page, which consists of fq type name, feature name (operation or relation) and page type, e.g. Model::Package::Type.feature#PageType or Model::Package::Type#Dashboard
     * @param layoutTypeName the name of the layout: mobile, tablet, desktop
     * @param relationName name of the dataElement of the table
     * @return e.g. PackageNameTypeNameRelationNamePageTypeDataElementDataInfo
     */
    public static String tableClassName(String pageName, String layoutTypeName, String relationName) {
        return getPageClassName(pageName).concat(StringUtils.capitalize(layoutTypeName.toLowerCase()))
                .concat(getClassName(relationName)).concat("DataInfo");
    }

    /**
     * Calculates relative path of a dialog based on {@link #getPageTypePath(String)} and {@link #dialogFileName(String)}
     *
     * @param pageName fq name of page, which consists of fq type name, feature name (operation or relation) and page type, e.g. Model::Package::Type.feature#PageType or Model::Package::Type#Dashboard
     * @param relationName the name of a the relation
     * @return e.g. package_name1/package_name2/../type_name/relation_name/pageType/dialogs/relation_name.dart
     */
    public static String dialogRelativePath(String pageName, String relationName) {
        return getPageTypePath(pageName).concat(dialogFileName(relationName));
    }

    /**
     * Calculates dialog name based on the layout type
     *
     * @param relationName the name of a the relation
     * @return e.g. dialogs/relation_name.dart
     */
    public static String dialogFileName(String relationName) {
        return "dialogs/".concat(getFileName(relationName)).concat(".dart");
    }

    /**
     * Calculates path of a dialog by concatenating {@link #pagesFolderPath(String)} and {@link #dialogRelativePath(String, String)}
     *
     * @param actorName name of the actor of the application
     * @param pageName fq name of page, which consists of fq type name, feature name (operation or relation) and page type, e.g. Model::Package::Type.feature#PageType or Model::Package::Type#Dashboard
     * @param relationName the name of a the relation
     * @return e.g. lib/actor/ui/pages/package_name1/package_name2/../type_name/relation_name/pageType/dialogs/relation_name.dart
     */
    public static String dialogPath(String actorName, String pageName, String relationName) {
        return pagesFolderPath(actorName).concat(dialogRelativePath(pageName, relationName));
    }

    /**
     * Calculates dialog class name.
     *
     * @param page page definition with a name, which consists of fq type name, feature name (operation or relation) and page type, e.g. Model::Package::Type.feature#PageType or Model::Package::Type#Dashboard
     * @param relationName the name of a the relation
     * @return e.g. PackageNameTypeNameRelationNameTableDialog if page type is table and PackageNameTypeNameRelationNamePageTypeRelationNameDialog, if page type is not table
     */
    public static String dialogClassName(PageDefinition page, String relationName) {
        if (page.getIsPageTypeTable()) {
            return getPageClassName(page.getName()).concat("Dialog");
        }
        return getPageClassName(page.getName()).concat(getClassName(relationName)).concat("Dialog");
    }

    /**
     * Calculates dialog store class name.
     *
     * @param page page definition with a name, which consists of fq type name, feature name (operation or relation) and page type, e.g. Model::Package::Type.feature#PageType or Model::Package::Type#Dashboard
     * @param relationName the name of a the relation
     * @return e.g. PackageNameTypeNameRelationNameTableDialogStore if page type is table and PackageNameTypeNameRelationNamePageTypeRelationNameDialogStore, if page type is not table
     */
    public static String dialogStoreClassName(PageDefinition page, String relationName) {
        if (page.getIsPageTypeTable()) {
            return getPageClassName(page.getName()).concat("DialogStore");
        }
        return getPageClassName(page.getName()).concat(getClassName(relationName)).concat("DialogStore");
    }

    /**
     * Calculates relative path of a dialog table based on {@link #getPageTypePath(String)} and {@link #dialogTableFileName(String)}
     *
     * @param pageName fq name of page, which consists of fq type name, feature name (operation or relation) and page type, e.g. Model::Package::Type.feature#PageType or Model::Package::Type#Dashboard
     * @param relationName the name of a the relation
     * @return e.g. package_name1/package_name2/../type_name/relation_name/pageType/dialogs/relation_name__table.dart
     */
    public static String dialogTableRelativePath(String pageName, String relationName) {
        return getPageTypePath(pageName).concat(dialogTableFileName(relationName));
    }

    /**
     * Calculates dialog table name.
     *
     * @param relationName the name of a the relation
     * @return e.g. dialogs/relation_name__table.dart
     */
    public static String dialogTableFileName(String relationName) {
        return "dialogs/".concat(getFileName(relationName)).concat("__table.dart");
    }

    /**
     * Calculates dialog table name.
     *
     * @param page page definition with a name, which consists of fq type name, feature name (operation or relation) and page type, e.g. Model::Package::Type.feature#PageType or Model::Package::Type#Dashboard
     * @param relationName the name of a the relation
     * @return e.g. PackageNameTypeNameRelationNameTableDialogTable if page type is table and PackageNameTypeNameRelationNamePageTypeRelationNameDialogTable, if page type is not table
     */
    public static String dialogTableClassName(PageDefinition page, String relationName) {
        if (page.getIsPageTypeTable()) {
            return getPageClassName(page.getName()).concat("DialogTable");
        }
        return getPageClassName(page.getName()).concat(getClassName(relationName)).concat("DialogTable");
    }

    /**
     * Calculates path of a dialog table by concatenating {@link #pagesFolderPath(String)} and {@link #dialogTableRelativePath(String, String)}
     *
     * @param actorName name of the actor of the application
     * @param pageName fq name of page, which consists of fq type name, feature name (operation or relation) and page type, e.g. Model::Package::Type.feature#PageType or Model::Package::Type#Dashboard
     * @param relationName the name of a the relation
     * @return e.g. lib/actor/ui/pages/package_name1/package_name2/../type_name/relation_name/pagetype/dialogs/relation_name__table.dart
     */
    public static String dialogTablePath(String actorName, String pageName, String relationName) {
        return pagesFolderPath(actorName).concat(dialogTableRelativePath(pageName, relationName));
    }

    // ui utilities

    /**
     * Calculates the relative path of the "ui/utilities" folder.
     *
     * @param actorName name of the actor of the application
     * @return relative path of "pages" folder, e.g. lib/actor/ui/utilities/
     */
    public static String utilitiesFolderPath(String actorName) {
        return "lib/"
                .concat(getFileName(actorName))
                .concat("/ui/utilities/");
    }
}
