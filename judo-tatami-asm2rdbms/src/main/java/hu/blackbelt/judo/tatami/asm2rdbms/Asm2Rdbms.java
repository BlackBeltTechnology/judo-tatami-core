package hu.blackbelt.judo.tatami.asm2rdbms;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.exceptions.ScriptExecutionException;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.io.File;
import java.util.List;
import java.util.Map;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter.programParameterBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.excel.ExcelModelContext.excelModelContextBuilder;
import static hu.blackbelt.judo.tatami.core.TransformationTraceUtil.*;

public class Asm2Rdbms {

    public static final String ASM_2_RDBMS_URI_POSTFIX = "jqlextract2expression";

    public static final String DIALECT_HSQLDB = "hsqldb";
    public static final String DIALECT_POSTGGRESSQL = "postgressql";
    public static final String DIALECT_ORACLE = "oracle";

    static Map<String, String> dialectTypeFileNames = ImmutableMap.of(
            DIALECT_HSQLDB, "RDBMS Data Types Hsqldb.xlsx",
            DIALECT_POSTGGRESSQL, "RDBMS Data Types Postgres.xlsx",
            DIALECT_ORACLE, "RDBMS Data Types Oracle.xlsx"
    );

    public static Asm2RdbmsTrackInfo executeAsm2RdbmsTransformation(ResourceSet resourceSet, AsmModel asmModel, RdbmsModel rdbmsModel, Log log,
                                                      File scriptDir, File excelModelDir, String dialect) throws Exception {

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
                                .excel(new File(excelModelDir, dialectTypeFileNames.get(dialect)).getAbsolutePath())
                                .excelConfiguration(new File(excelModelDir, "typemapping.xml").getAbsolutePath())
                                .build(),
                        excelModelContextBuilder()
                                .name("RULEMAPPING")
                                .excel(new File(excelModelDir, "RDBMS Table Mapping Rules.xlsx").getAbsolutePath())
                                .excelConfiguration(new File(excelModelDir, "rulemapping.xml").getAbsolutePath())
                                .build(),
                        excelModelContextBuilder()
                                .name("NAMEMAPPING")
                                .excel(new File(excelModelDir, "RDBMS Sql Name Mapping.xlsx").getAbsolutePath())
                                .excelConfiguration(new File(excelModelDir, "namemapping.xml").getAbsolutePath())
                                .build()
                        )
                )
                .injectContexts(ImmutableMap.of(
                        "AbbreviateUtils", new AbbreviateUtils()
                ))
                .sourceDirectory(scriptDir)
                .build();

        // run the model / metadata loading
        executionContext.load();

        // Prepare transformation execution context
        EtlExecutionContext etlExecutionContext = etlExecutionContextBuilder()
                .source("asmToRdbms.etl")
                .parameters(ImmutableList.of(
                        programParameterBuilder().name("modelVersion").value(asmModel.getVersion()).build(),
                        programParameterBuilder().name("dialect").value(dialect).build(),
                        programParameterBuilder().name("extendedMetadataURI")
                                .value("http://blackbelt.hu/judo/meta/ExtendedMetadata").build()
                ))
                .build();

        // Transformation script
        executionContext.executeProgram(etlExecutionContext);

        executionContext.commit();
        executionContext.close();

        List<EObject> traceModel = getTransformationTrace(ASM_2_RDBMS_URI_POSTFIX, etlExecutionContext);

        return Asm2RdbmsTrackInfo.asm2RdbmsTrackInfoBuilder()
                .asmModel(asmModel)
                .rdbmsModel(rdbmsModel)
                .trace(resolveAsm2RdbmsTrace(traceModel, asmModel, rdbmsModel)).build();
    }


    public static ResourceSet createAsm2RdbmsTraceResourceSet() {
        return createTraceResourceSet(ASM_2_RDBMS_URI_POSTFIX);
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
