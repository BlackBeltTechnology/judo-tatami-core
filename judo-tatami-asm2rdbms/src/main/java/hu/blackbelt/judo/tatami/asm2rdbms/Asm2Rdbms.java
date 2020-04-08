package hu.blackbelt.judo.tatami.asm2rdbms;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.Hashing;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmUtils;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.common.util.UriUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;
import java.util.Map;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter.programParameterBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.excel.ExcelModelContext.excelModelContextBuilder;
import static hu.blackbelt.judo.tatami.asm2rdbms.Asm2RdbmsTransformationTrace.resolveAsm2RdbmsTrace;
import static hu.blackbelt.judo.tatami.core.TransformationTraceUtil.*;

public class Asm2Rdbms {

    public static final String SCRIPT_ROOT_TATAMI_ASM_2_RDBMS = "tatami/asm2rdbms/transformations/";
    public static final String MODEL_ROOT_TATAMI_ASM_2_RDBMS = "tatami/asm2rdbms/model/";

    public static final String ASM_2_RDBMS_URI_POSTFIX = "asm2rdbms";

    public static final String DIALECT_HSQLDB = "hsqldb";
    public static final String DIALECT_POSTGRESQL = "postgresql";
    public static final String DIALECT_ORACLE = "oracle";

    static Map<String, String> dialectTypeFileNames = ImmutableMap.of(
            DIALECT_HSQLDB, "RDBMS_Data_Types_Hsqldb.xlsx",
            DIALECT_POSTGRESQL, "RDBMS_Data_Types_Postgres.xlsx",
            DIALECT_ORACLE, "RDBMS_Data_Types_Oracle.xlsx"
    );

    private static MD5Utils MD5_UTILS = new MD5Utils();

    public static Asm2RdbmsTransformationTrace executeAsm2RdbmsTransformation(AsmModel asmModel, RdbmsModel rdbmsModel, Log log,
                                                                              java.net.URI scriptUri, java.net.URI excelModelUri, String dialect) throws Exception {

        // Execution context
        ExecutionContext executionContext = executionContextBuilder()
                .log(log)
                .modelContexts(ImmutableList.of(
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("ASM")
                                .resource(asmModel.getResource())
                                .build(),
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("RDBMS")
                                .resource(rdbmsModel.getResource())
                                .build(),
                        excelModelContextBuilder()
                                .name("TYPEMAPPING")
                                .excel(UriUtil.resolve(dialectTypeFileNames.get(dialect), excelModelUri).toString())
                                .excelConfiguration(UriUtil.resolve("typemapping.xml", excelModelUri).toString())
                                .build(),
                        excelModelContextBuilder()
                                .name("RULEMAPPING")
                                .excel(UriUtil.resolve("RDBMS_Table_Mapping_Rules.xlsx", excelModelUri).toString())
                                .excelConfiguration(UriUtil.resolve("rulemapping.xml", excelModelUri).toString())
                                .build(),
                        excelModelContextBuilder()
                                .name("NAMEMAPPING")
                                .excel(UriUtil.resolve("RDBMS_Sql_Name_Mapping.xlsx", excelModelUri).toString())
                                .excelConfiguration(UriUtil.resolve("namemapping.xml", excelModelUri).toString())
                                .build()
                        )
                )
                .injectContexts(ImmutableMap.of(
                        "AbbreviateUtils", new AbbreviateUtils(),
                        "MD5Utils", MD5_UTILS,
                        "asmUtils", new AsmUtils(asmModel.getResourceSet())
                ))
                .build();

        // run the model / metadata loading
        executionContext.load();


        EtlExecutionContext nameMappingExecutionContext = etlExecutionContextBuilder()
                .source(UriUtil.resolve("excelToNameMapping.etl", scriptUri))
                .build();

        EtlExecutionContext typeMappingExecutionContext = etlExecutionContextBuilder()
                .source(UriUtil.resolve("excelToTypeMapping.etl", scriptUri))
                .build();


        EtlExecutionContext rulesExecutionContext = etlExecutionContextBuilder()
                .source(UriUtil.resolve("excelToRules.etl", scriptUri))
                .build();

        EtlExecutionContext asm2rdbmsExecutionContext = etlExecutionContextBuilder()
                .source(UriUtil.resolve("asmToRdbms.etl", scriptUri))
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

        List<EObject> traceModel = getTransformationTraceFromEtlExecutionContext(ASM_2_RDBMS_URI_POSTFIX, asm2rdbmsExecutionContext);

        return Asm2RdbmsTransformationTrace.asm2RdbmsTransformationTraceBuilder()
                .asmModel(asmModel)
                .rdbmsModel(rdbmsModel)
                .trace(resolveAsm2RdbmsTrace(traceModel, asmModel, rdbmsModel)).build();
    }

    public static URI calculateAsm2RdbmsTransformationScriptURI() throws URISyntaxException {
        return calculateURI(SCRIPT_ROOT_TATAMI_ASM_2_RDBMS);
    }

    public static URI calculateAsm2RdbmsModelURI() throws URISyntaxException {
        return calculateURI(MODEL_ROOT_TATAMI_ASM_2_RDBMS);
    }

    public static URI calculateURI(String path) throws URISyntaxException {
        URI psmRoot = Asm2Rdbms.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        if (psmRoot.toString().endsWith(".jar")) {
            psmRoot = new URI("jar:" + psmRoot.toString() + "!/" + path);
        } else if (psmRoot.toString().startsWith("jar:bundle:")) {
            psmRoot = new URI(psmRoot.toString().substring(4, psmRoot.toString().indexOf("!")) + path);
        } else {
            psmRoot = new URI(psmRoot.toString() + "/" + path);
        }
        return psmRoot;
    }

    public static class MD5Utils {

        public static String md5(final String string) {
            return Hashing.md5().hashString(string, Charset.forName("UTF-8")).toString();
        }
    }
}
