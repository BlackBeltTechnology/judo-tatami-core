package hu.blackbelt.judo.tatami.asm2jaxrsapi;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.contexts.EglExecutionContext;
import hu.blackbelt.judo.framework.compiler.api.StaticCompiler;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.io.File;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EglExecutionContext.eglExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter.programParameterBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;

public class Asm2JAXRSAPI {

    public static void executeAsm2JAXRSAPIGeneration(ResourceSet resourceSet, AsmModel asmModel, Log log,
                                                     File scriptDir, File outputDir) throws Exception {

        // Execution context
        ExecutionContext executionContext = executionContextBuilder()
                .log(log)
                .resourceSet(resourceSet)
                .modelContexts(ImmutableList.of(
                        wrappedEmfModelContextBuilder()
                                .log(log)
                                .name("ASM")
                                .resource(asmModel.getResourceSet().getResource(asmModel.getUri(), false))
                                .build()
                        )
                )
                .sourceDirectory(scriptDir)
                .build();

        // run the model / metadata loading
        executionContext.load();

        EglExecutionContext eglExecutionContext = eglExecutionContextBuilder()
                .source("main.egl")
                .outputRoot(outputDir.getAbsolutePath())
                .parameters(ImmutableList.of(
                        programParameterBuilder().name("modelVersion").value(asmModel.getVersion()).build(),
                        programParameterBuilder().name("extendedMetadataURI")
                                .value("http://blackbelt.hu/judo/meta/ExtendedMetadata").build()
                ))
                .build();

        // Transformation script
        executionContext.executeProgram(eglExecutionContext);

        for (String src : (Set<String>)executionContext.getContext().get("outputJavaClasses")) {
            log.info(src);
        }

        compile(outputDir, (Set<String>)executionContext.getContext().get("outputJavaClasses"));
        executionContext.commit();
        executionContext.close();
    }

    /*
    private static Set<Class> compileJanino(File sourceDir, Set<String> sourceFiles) throws Exception {
        ClassLoader parentClassLoader = Asm2JAXRSAPI.class.getClassLoader();
        JavaSourceClassLoader genSrcClassLoader = new JavaSourceClassLoader(parentClassLoader, new File[]{sourceDir}, "UTF-8");

        Set<Class> compiled = new HashSet<>();
        for (String fileName : sourceFiles) {
            compiled.add(genSrcClassLoader.loadClass(fileName.substring(0, fileName.length() - 5).replaceAll("\\/", ".")));
        }
        return compiled;
    }
    */
    private static List<File> compile(File sourceDir, Set<String> sourceCodeFiles) throws Exception {
        Set<Class> compiled = new HashSet<>();

        // JavaCompiler jacaCompiler = new EclipseCompiler();
        // JavaCompiler.CompilationTask compile = javac.getTask(out, fileManager, dianosticListener, options, classes, compilationUnits);

        List<File> sourceFiles = new ArrayList<>();
        for (String fileName : sourceCodeFiles) {
            //compiled.add(genSrcClassLoader.loadClass(fileName.substring(0, fileName.length() - 5).replaceAll("\\/", ".")));
            sourceFiles.add(new File(sourceDir.getAbsolutePath(), fileName));
        }

        return StaticCompiler.compile(null, sourceFiles, sourceDir, true, false);
    }
}
