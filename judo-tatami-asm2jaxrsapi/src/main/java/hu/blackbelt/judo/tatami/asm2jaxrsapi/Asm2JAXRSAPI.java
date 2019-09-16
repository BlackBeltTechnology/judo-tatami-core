package hu.blackbelt.judo.tatami.asm2jaxrsapi;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.contexts.EglExecutionContext;
import hu.blackbelt.judo.framework.compiler.api.CompilerUtil;
import hu.blackbelt.judo.framework.compiler.api.FullyQualifiedName;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmUtils;
import org.eclipse.epsilon.common.util.UriUtil;
import org.ops4j.pax.tinybundles.core.TinyBundle;
import org.osgi.framework.Constants;
import javax.tools.JavaFileObject;
import javax.ws.rs.core.Application;
import java.io.*;
import java.net.URI;
import java.util.Collections;
import java.util.Set;
import java.util.stream.Collectors;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EglExecutionContext.eglExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter.programParameterBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;
import static hu.blackbelt.judo.framework.compiler.api.CompilerContext.compilerContextBuilder;
import static org.ops4j.pax.tinybundles.core.TinyBundles.bundle;

public class Asm2JAXRSAPI {

    public static InputStream executeAsm2JAXRSAPIGeneration(AsmModel asmModel, Log log,
                                                            URI scriptDir, File sourceCodeOutputDir) throws Exception {

        // Execution context
        ExecutionContext executionContext = executionContextBuilder()
                .log(log)
                .modelContexts(ImmutableList.of(
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("ASM")
                                .resource(asmModel.getResourceSet().getResource(asmModel.getUri(), false))
                                .build()
                        )
                )
                .injectContexts(ImmutableMap.of("asmUtils", new AsmUtils((asmModel.getResourceSet()))))
                .build();

        // run the model / metadata loading
        executionContext.load();

        EglExecutionContext eglExecutionContext = eglExecutionContextBuilder()
                .source(UriUtil.resolve("main.egl", scriptDir))
                .outputRoot(sourceCodeOutputDir.getAbsolutePath())
                .parameters(ImmutableList.of(
                        programParameterBuilder().name("modelVersion").value(asmModel.getVersion()).build(),
                        programParameterBuilder().name("extendedMetadataURI")
                                .value("http://blackbelt.hu/judo/meta/ExtendedMetadata").build()
                ))
                .build();

        // Transformation script
        executionContext.executeProgram(eglExecutionContext);

        Set<String> javaFileNames = (Set<String>)executionContext.getContext().get("outputJavaClasses");
        Set<String> scrXmlFileNames = (Set<String>)executionContext.getContext().get("outputScrXmls");

        // compile(outputDir, (Set<String>)executionContext.getContext().get("outputJavaClasses"));
        executionContext.commit();
        executionContext.close();

        // Generating bundle
        return generateBundle(
                asmModel.getName(),
                asmModel.getVersion(),
                compile(sourceCodeOutputDir, javaFileNames),
                sourceCodeOutputDir,
                javaFileNames,
                scrXmlFileNames
        );
    }

    private static Iterable<JavaFileObject> compile(File sourceDir, Set<String> sourceCodeFiles) throws Exception {
        if (sourceCodeFiles.isEmpty()) {
            return Collections.emptyList();
        }

        // Force to import
        Application.class.getName();

        Iterable<JavaFileObject> compiled = CompilerUtil.compile(compilerContextBuilder()
                .sameClassLoaderAs(Asm2JAXRSAPI.class)
                .compilationFiles(fileNamesToFile(sourceDir, sourceCodeFiles))
                .build());
        return compiled;
    }

    private static Iterable<File> fileNamesToFile(File sourceDir, Set<String> sourceCodeFiles) throws Exception {
        return  sourceCodeFiles.stream().map(fn -> new File(sourceDir.getAbsolutePath(), fn)).collect(Collectors.toList());
    }

    private static InputStream generateBundle(String modelName, String version, Iterable<JavaFileObject> compiled, File sourceDir,
                                              Set<String> sourceCodeFiles, Set<String> scrXmlFiles) throws FileNotFoundException {
        TinyBundle bundle = bundle();
        compiled.forEach(c -> {
            FullyQualifiedName fullyQualifiedName = (FullyQualifiedName) c;
            try {
                bundle.add(fullyQualifiedName.getFullyQualifiedName().replace('.', '/') + ".class", c.openInputStream());
            } catch (IOException e) {
                throw new RuntimeException("File not found: "+ c.getName(), e);
            }
        });
        sourceCodeFiles.forEach(s -> {
            try {
                bundle.add(s, new FileInputStream(new File(sourceDir.getAbsolutePath(), s)));
            } catch (FileNotFoundException e) {
                throw new RuntimeException("File not found: " + s, e);
            }
        });

        scrXmlFiles.forEach(s -> {
            try {
                bundle.add(s, new FileInputStream(new File(sourceDir.getAbsolutePath(), s)));
            } catch (FileNotFoundException e) {
                throw new RuntimeException("File not found: " + s, e);
            }
        });

        bundle.set( Constants.BUNDLE_MANIFESTVERSION, "2")
                .set( Constants.BUNDLE_SYMBOLICNAME, modelName + "-asm2jaxrsapi" )
                .set( Constants.BUNDLE_VERSION, version )
                .set( Constants.REQUIRE_CAPABILITY,
                        "osgi.extender;filter:=\"(&(osgi.extender=osgi.component)(version>=1.3.0)(!(version>=2.0.0)))\"")
                .set( Constants.IMPORT_PACKAGE,
                        "org.osgi.framework;version=\"[1.8,2.0)\"," +
                                "javax.ws.rs;version=\"[2.0,3)\"," +
                                "javax.ws.rs.core;version=\"[2.0,3)\"," +
                                "hu.blackbelt.judo.tatami.core"

                )
                .set("Service-Component", Joiner.on(",").join(scrXmlFiles));
        return bundle.build();
    }

}
