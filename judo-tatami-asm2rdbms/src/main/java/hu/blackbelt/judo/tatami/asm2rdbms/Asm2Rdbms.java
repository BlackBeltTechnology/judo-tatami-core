package hu.blackbelt.judo.tatami.asm2rdbms;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.exceptions.ScriptExecutionException;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmUtils;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.eclipse.emf.ecore.resource.URIHandler;

import java.io.File;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Map;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter.programParameterBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.excel.ExcelModelContext.excelModelContextBuilder;
import static hu.blackbelt.judo.tatami.core.TransformationTraceUtil.*;

public class Asm2Rdbms {

    public static final String ASM_2_RDBMS_URI_POSTFIX = "asm2rdbms";

    public static final String DIALECT_HSQLDB = "hsqldb";
    public static final String DIALECT_POSTGGRESSQL = "postgressql";
    public static final String DIALECT_ORACLE = "oracle";

    static Map<String, String> dialectTypeFileNames = ImmutableMap.of(
            DIALECT_HSQLDB, "RDBMS_Data_Types_Hsqldb.xlsx",
            DIALECT_POSTGGRESSQL, "RDBMS_Data_Types_Postgres.xlsx",
            DIALECT_ORACLE, "RDBMS_Data_Types_Oracle.xlsx"
    );

    public static Asm2RdbmsTransformationTrace executeAsm2RdbmsTransformation(ResourceSet resourceSet, AsmModel asmModel, RdbmsModel rdbmsModel, Log log,
                                                                              java.net.URI scriptUri, java.net.URI excelModelUri, String dialect) throws Exception {

        // If resource was not created for target model before
        Resource rdbmsResource = rdbmsModel.getResourceSet().getResource(rdbmsModel.getUri(), false);
        if (rdbmsResource == null) {
            rdbmsResource = resourceSet.createResource(rdbmsModel.getUri());
        }

        // Execution context
        ExecutionContext executionContext = executionContextBuilder()
                .log(log)
                .resourceSet(resourceSet)
                .modelContexts(ImmutableList.of(
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("ASM")
                                .resource(asmModel.getResourceSet().getResource(asmModel.getUri(), false))
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("RDBMS")
                                .resource(rdbmsResource)
                                .build(),
                        excelModelContextBuilder()
                                .name("TYPEMAPPING")
                                .excel(excelModelUri.resolve(dialectTypeFileNames.get(dialect).replace(" ", "%20")).toString())
                                .excelConfiguration(excelModelUri.resolve("typemapping.xml").toString())
                                .build(),
                        excelModelContextBuilder()
                                .name("RULEMAPPING")
                                .excel(excelModelUri.resolve("RDBMS_Table_Mapping_Rules.xlsx".replace(" ", "%20")).toString())
                                .excelConfiguration(excelModelUri.resolve("rulemapping.xml").toString())
                                .build(),
                        excelModelContextBuilder()
                                .name("NAMEMAPPING")
                                .excel(excelModelUri.resolve("RDBMS_Sql_Name_Mapping.xlsx".replace(" ", "%20")).toString())
                                .excelConfiguration(excelModelUri.resolve("namemapping.xml").toString())
                                .build()
                        )
                )
                .injectContexts(ImmutableMap.of(
                        "AbbreviateUtils", new AbbreviateUtils(),
                        "asmUtils", new AsmUtils(asmModel.getResourceSet())
                ))
                .build();

        // run the model / metadata loading
        executionContext.load();


        EtlExecutionContext nameMappingExecutionContext = etlExecutionContextBuilder()
                .source(scriptUri.resolve("excelToNameMapping.etl"))
                .build();

        EtlExecutionContext typeMappingExecutionContext = etlExecutionContextBuilder()
                .source(scriptUri.resolve("excelToTypeMapping.etl"))
                .build();


        EtlExecutionContext rulesExecutionContext = etlExecutionContextBuilder()
                .source(scriptUri.resolve("excelToRules.etl"))
                .build();

        EtlExecutionContext asm2rdbmsExecutionContext = etlExecutionContextBuilder()
                .source(scriptUri.resolve("asmToRdbms.etl"))
                .parameters(ImmutableList.of(
                        programParameterBuilder().name("modelVersion").value(asmModel.getVersion()).build(),
                        programParameterBuilder().name("dialect").value(dialect).build(),
                        programParameterBuilder().name("extendedMetadataURI")
                                .value("http://blackbelt.hu/judo/meta/ExtendedMetadata").build()
                ))
                .build();

        // Transformation script
        executionContext.executeProgram(nameMappingExecutionContext);
        executionContext.executeProgram(typeMappingExecutionContext);
        executionContext.executeProgram(rulesExecutionContext);
        executionContext.executeProgram(asm2rdbmsExecutionContext);

        executionContext.commit();
        executionContext.close();

        List<EObject> traceModel = getTransformationTrace(ASM_2_RDBMS_URI_POSTFIX, asm2rdbmsExecutionContext);

        return Asm2RdbmsTransformationTrace.asm2RdbmsTransformationTraceBuilder()
                .asmModel(asmModel)
                .rdbmsModel(rdbmsModel)
                .trace(resolveAsm2RdbmsTrace(traceModel, asmModel, rdbmsModel)).build();
    }


    public static ResourceSet createAsm2RdbmsTraceResourceSet() {
        return createTraceResourceSet(ASM_2_RDBMS_URI_POSTFIX);
    }

    public static ResourceSet createAsm2RdbmsTraceResourceSet(URIHandler uriHandler) {
        return createTraceResourceSet(ASM_2_RDBMS_URI_POSTFIX, uriHandler);
    }

    public static Asm2RdbmsTransformationTrace loadAsm2RdbmsTrace(URI uri, URIHandler uriHandler, AsmModel asmModel, RdbmsModel rdbmsModel) throws IOException {
        ResourceSet traceLoadedResourceSet = createAsm2RdbmsTraceResourceSet(uriHandler);
        Resource traceResoureLoaded = traceLoadedResourceSet.createResource(uri);
        traceResoureLoaded.load(ImmutableMap.of());

        Asm2RdbmsTransformationTrace trace = Asm2RdbmsTransformationTrace.asm2RdbmsTransformationTraceBuilder()
                .asmModel(asmModel)
                .rdbmsModel(rdbmsModel)
                .trace(resolveAsm2RdbmsTrace(traceResoureLoaded, asmModel, rdbmsModel)).build();

        return trace;
    }

    public static Map<EObject, List<EObject>> resolveAsm2RdbmsTrace(Resource traceResource, AsmModel asmModel, RdbmsModel rdbmsModel) {
        return resolveAsm2RdbmsTrace(traceResource.getContents(), asmModel, rdbmsModel);
    }

    public static Map<EObject, List<EObject>> resolveAsm2RdbmsTrace(List<EObject> trace, AsmModel asmModel, RdbmsModel rdbmsModel) {
        return resolveTransformationTrace(trace,
                ImmutableList.of(asmModel.getResourceSet(), rdbmsModel.getResourceSet()));
    }

    public static List<EObject> getAsm2RdbmsTrace(Map<EObject, List<EObject>> trace) throws ScriptExecutionException {
        return getTransformationTrace(ASM_2_RDBMS_URI_POSTFIX, trace);
    }
}
