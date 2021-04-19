package hu.blackbelt.judo.tatami.asm2rdbms;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.Hashing;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmUtils;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
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
import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel.LoadArguments.rdbmsLoadArgumentsBuilder;
import static hu.blackbelt.judo.tatami.asm2rdbms.Asm2RdbmsTransformationTrace.resolveAsm2RdbmsTrace;
import static hu.blackbelt.judo.tatami.core.TransformationTraceUtil.*;

@Slf4j
public class Asm2Rdbms {

    public static final String SCRIPT_ROOT_TATAMI_ASM_2_RDBMS = "tatami/asm2rdbms/transformations/";
    public static final String MODEL_ROOT_TATAMI_ASM_2_RDBMS = "tatami/asm2rdbms/model/";

    public static final String ASM_2_RDBMS_URI_POSTFIX = "asm2rdbms";

    private static MD5Utils MD5_UTILS = new MD5Utils();


    public static Asm2RdbmsTransformationTrace executeAsm2RdbmsTransformation(AsmModel asmModel, RdbmsModel rdbmsModel, String dialect) throws Exception {
        return executeAsm2RdbmsTransformation(asmModel, rdbmsModel, new Slf4jLog(log), calculateAsm2RdbmsTransformationScriptURI(), calculateAsm2RdbmsModelURI(), dialect);
    }

    public static Asm2RdbmsTransformationTrace executeAsm2RdbmsTransformation(AsmModel asmModel, RdbmsModel rdbmsModel, Log log,  String dialect) throws Exception {
        return executeAsm2RdbmsTransformation(asmModel, rdbmsModel, log, calculateAsm2RdbmsTransformationScriptURI(), calculateAsm2RdbmsModelURI(), dialect);
    }

    public static Asm2RdbmsTransformationTrace executeAsm2RdbmsTransformation(AsmModel asmModel, RdbmsModel rdbmsModel, Log log,
                                                                              java.net.URI scriptUri, java.net.URI excelModelUri, String dialect) throws Exception {

        RdbmsModel mappingModel = RdbmsModel.loadRdbmsModel(
                rdbmsLoadArgumentsBuilder()
                        .validateModel(false)
                        .name("mapping-" + dialect)
                        .uri(org.eclipse.emf.common.util.URI.createURI("mem:mapping-" + dialect + "-rdbms"))
                        .inputStream(UriUtil.resolve("mapping-" + dialect + "-rdbms.model", excelModelUri)
                                .toURL()
                                .openStream()));
        rdbmsModel.getResource().getContents().addAll(mappingModel.getResource().getContents());

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
        executionContext.executeProgram(asm2rdbmsExecutionContext);

        executionContext.commit();
        executionContext.close();

        List<EObject> traceModel = getTransformationTraceFromEtlExecutionContext(ASM_2_RDBMS_URI_POSTFIX, asm2rdbmsExecutionContext);

        return Asm2RdbmsTransformationTrace.asm2RdbmsTransformationTraceBuilder()
                .asmModel(asmModel)
                .rdbmsModel(rdbmsModel)
                .trace(resolveAsm2RdbmsTrace(traceModel, asmModel, rdbmsModel)).build();
    }

    public static URI calculateAsm2RdbmsTransformationScriptURI() {
        return calculateURI(SCRIPT_ROOT_TATAMI_ASM_2_RDBMS);
    }

    public static URI calculateAsm2RdbmsModelURI(){
        return calculateURI(MODEL_ROOT_TATAMI_ASM_2_RDBMS);
    }

    @SneakyThrows(URISyntaxException.class)
    public static URI calculateURI(String path) {
        URI root = Asm2Rdbms.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        if (root.toString().endsWith(".jar")) {
            root = new URI("jar:" + root.toString() + "!/" + path);
        } else if (root.toString().startsWith("jar:bundle:")) {
            root = new URI(root.toString().substring(4, root.toString().indexOf("!")) + path);
        } else {
            root = new URI(root.toString() + "/" + path);
        }
        return root;
    }

    public static class MD5Utils {

        public static String md5(final String string) {
            return Hashing.md5().hashString(string, Charset.forName("UTF-8")).toString();
        }
    }
}
