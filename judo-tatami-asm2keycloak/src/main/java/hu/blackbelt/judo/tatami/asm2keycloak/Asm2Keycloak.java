package hu.blackbelt.judo.tatami.asm2keycloak;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.hash.Hashing;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmUtils;
import hu.blackbelt.judo.meta.keycloak.runtime.KeycloakModel;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.epsilon.common.util.UriUtil;

import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.List;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext.etlExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter.programParameterBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;
import static hu.blackbelt.judo.tatami.asm2keycloak.Asm2KeycloakTransformationTrace.resolveAsm2KeycloakTrace;
import static hu.blackbelt.judo.tatami.core.TransformationTraceUtil.*;

@Slf4j
public class Asm2Keycloak {

    public static final String SCRIPT_ROOT_TATAMI_ASM_2_KEYCLOAK = "tatami/asm2keycloak/transformations/";

    public static final String ASM_2_KEYCLOAK_URI_POSTFIX = "asm2keycloak";

    public static Asm2KeycloakTransformationTrace executeAsm2KeycloakTransformation(AsmModel asmModel, KeycloakModel keycloakModel) throws Exception {
        return executeAsm2KeycloakTransformation(asmModel, keycloakModel, new Slf4jLog(log), calculateAsm2KeycloakTransformationScriptURI());

    }

    public static Asm2KeycloakTransformationTrace executeAsm2KeycloakTransformation(AsmModel asmModel, KeycloakModel keycloakModel, Log log) throws Exception {
        return executeAsm2KeycloakTransformation(asmModel, keycloakModel, log, calculateAsm2KeycloakTransformationScriptURI());
    }

    public static Asm2KeycloakTransformationTrace executeAsm2KeycloakTransformation(AsmModel asmModel, KeycloakModel keycloakModel, Log log,
                                                                              java.net.URI scriptUri) throws Exception {

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
                                .name("KEYCLOAK")
                                .resource(keycloakModel.getResource())
                                .build()
                        )
                )
                .injectContexts(ImmutableMap.of(
                        "asmUtils", new AsmUtils(asmModel.getResourceSet())
                ))
                .build();

        // run the model / metadata loading
        executionContext.load();

        EtlExecutionContext asm2keycloakExecutionContext = etlExecutionContextBuilder()
                .source(UriUtil.resolve("asmToKeycloak.etl", scriptUri))
                .parameters(ImmutableList.of(
                        programParameterBuilder().name("modelVersion").value(asmModel.getVersion()).build()
                ))
                .build();

        executionContext.executeProgram(asm2keycloakExecutionContext);

        executionContext.commit();
        executionContext.close();

        List<EObject> traceModel = getTransformationTraceFromEtlExecutionContext(ASM_2_KEYCLOAK_URI_POSTFIX, asm2keycloakExecutionContext);

        return Asm2KeycloakTransformationTrace.asm2KeycloakTransformationTraceBuilder()
                .asmModel(asmModel)
                .keycloakModel(keycloakModel)
                .trace(resolveAsm2KeycloakTrace(traceModel, asmModel, keycloakModel)).build();
    }

    public static URI calculateAsm2KeycloakTransformationScriptURI() {
        return calculateURI(SCRIPT_ROOT_TATAMI_ASM_2_KEYCLOAK);
    }

    @SneakyThrows(URISyntaxException.class)
    public static URI calculateURI(String path) {
        URI psmRoot = Asm2Keycloak.class.getProtectionDomain().getCodeSource().getLocation().toURI();
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
