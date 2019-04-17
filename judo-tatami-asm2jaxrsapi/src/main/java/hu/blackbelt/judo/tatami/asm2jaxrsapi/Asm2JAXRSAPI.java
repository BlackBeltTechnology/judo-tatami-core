package hu.blackbelt.judo.tatami.asm2jaxrsapi;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.contexts.EglExecutionContext;
import hu.blackbelt.judo.framework.compiler.api.CompilerUtil;
import hu.blackbelt.judo.framework.compiler.api.FullyQualifiedName;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.ops4j.pax.tinybundles.core.TinyBundle;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.FrameworkUtil;
import org.osgi.framework.wiring.BundleWiring;

import javax.tools.JavaFileObject;
import javax.ws.rs.core.Application;
import java.io.*;
import java.util.Set;
import java.util.stream.Collectors;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EglExecutionContext.eglExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter.programParameterBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;
import static hu.blackbelt.judo.framework.compiler.api.CompilerContext.compilerContextBuilder;
import static org.ops4j.pax.tinybundles.core.TinyBundles.bundle;

@Slf4j
public class Asm2JAXRSAPI {

    public static InputStream executeAsm2JAXRSAPIGeneration(ResourceSet resourceSet, AsmModel asmModel, Log log,
                                                            File scriptDir, File sourceCodeOutputDir) throws Exception {

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
        // JaxRS classloader is used in OSGi
        Bundle bundle = FrameworkUtil.getBundle(Application.class);
        ClassLoader classLoader = Asm2JAXRSAPI.class.getClassLoader();
        BundleContext bundleContext = null;
        if (bundle != null) {
            bundleContext = bundle.getBundleContext();
            classLoader = bundle.adapt(BundleWiring.class).getClassLoader();
        }

        Iterable<JavaFileObject> compiled = CompilerUtil.compile(compilerContextBuilder()
                .classLoader(classLoader)
                .bundleContext(bundleContext)
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
                                "javax.ws.rs.core;version=\"[2.0,3)\""
                )
                .set("Service-Component", Joiner.on(",").join(scrXmlFiles));
        return bundle.build();
    }

}
