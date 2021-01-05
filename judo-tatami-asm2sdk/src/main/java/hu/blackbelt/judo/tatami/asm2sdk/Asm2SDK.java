package hu.blackbelt.judo.tatami.asm2sdk;

import com.google.common.base.Joiner;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.contexts.EglExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import hu.blackbelt.judo.framework.compiler.api.CompilerUtil;
import hu.blackbelt.judo.framework.compiler.api.FullyQualifiedName;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmUtils;
import hu.blackbelt.judo.tatami.core.CachingInputStream;
import hu.blackbelt.judo.tatami.core.workflow.work.MetricsCollector;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.epsilon.common.util.UriUtil;
import org.ops4j.pax.tinybundles.core.TinyBundle;
import org.osgi.framework.Constants;
import javax.tools.JavaFileObject;
import java.io.*;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import static hu.blackbelt.epsilon.runtime.execution.ExecutionContext.executionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.EglExecutionContext.eglExecutionContextBuilder;
import static hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter.programParameterBuilder;
import static hu.blackbelt.epsilon.runtime.execution.model.emf.WrappedEmfModelContext.wrappedEmfModelContextBuilder;
import static hu.blackbelt.judo.framework.compiler.api.CompilerContext.compilerContextBuilder;
import static org.ops4j.pax.tinybundles.core.TinyBundles.bundle;

@Slf4j
public class Asm2SDK {

    public static final String SCRIPT_ROOT_TATAMI_ASM_2_SDK = "tatami/asm2sdk/templates/";

    public static Asm2SDKBundleStreams executeAsm2SDKGeneration(AsmModel asmModel, Log log,
                                                       File sourceCodeOutputDir) throws Exception {
        return executeAsm2SDKGeneration(asmModel, log, calculateAsm2SDKTemplateScriptURI(), sourceCodeOutputDir);
    }

    public static Asm2SDKBundleStreams executeAsm2SDKGeneration(AsmModel asmModel, File sourceCodeOutputDir) throws Exception {
        return executeAsm2SDKGeneration(asmModel, new Slf4jLog(log), calculateAsm2SDKTemplateScriptURI(), sourceCodeOutputDir);
    }

    public static Asm2SDKBundleStreams executeAsm2SDKGeneration(AsmModel asmModel, Log log,
                                                                URI scriptUri, File sourceCodeOutputDir) throws Exception {
        return executeAsm2SDKGeneration(asmModel, log, scriptUri, sourceCodeOutputDir, null);
    }

    public static Asm2SDKBundleStreams executeAsm2SDKGeneration(AsmModel asmModel, Log log,
                                                                URI scriptUri, File sourceCodeOutputDir, MetricsCollector metricsCollector) throws Exception {

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
                .source(UriUtil.resolve("main.egl", scriptUri))
                .outputRoot(sourceCodeOutputDir.getAbsolutePath())
                .parameters(ImmutableList.of(
                        programParameterBuilder().name("modelVersion").value(asmModel.getVersion()).build(),
                        programParameterBuilder().name("extendedMetadataURI")
                                .value("http://blackbelt.hu/judo/meta/ExtendedMetadata").build()
                ))
                .build();

        // Transformation script
        executionContext.executeProgram(eglExecutionContext);

        Set<String> sdkJavaFileNames = ((Set<String>) executionContext.getContext().get("sdkJavaClasses"))
                .stream().map(s -> s.replaceAll("//", "/")).collect(Collectors.toSet());
        Set<String> internalJavaFileNames = ((Set<String>) executionContext.getContext().get("internalJavaClasses"))
                .stream().map(s -> s.replaceAll("//", "/")).collect(Collectors.toSet());
        Set<String> internalXmlFileNames = (Set<String>)executionContext.getContext().get("internalScrXmls");

        executionContext.commit();
        executionContext.close();

        Set<String> allJavaFiles = new HashSet<>();
        allJavaFiles.addAll(sdkJavaFileNames);
        allJavaFiles.addAll(internalJavaFileNames);

        Iterable<JavaFileObject> compiled;
        final Long compilerStartTs = System.nanoTime();
        boolean compilerFailed = false;
        try {
            if (metricsCollector != null) {
                metricsCollector.invokedTransformation("SDK-compile");
            }

            compiled = compile(sourceCodeOutputDir, allJavaFiles);
        } catch (Exception ex) {
            compilerFailed = true;
            throw ex;
        } finally {
            if (metricsCollector != null) {
                metricsCollector.stoppedTransformation("SDK-compile", System.nanoTime() - compilerStartTs, compilerFailed);
            }
        }

        final Long packagingStartTs = System.nanoTime();
        boolean packagingFailed = false;
        try {
            if (metricsCollector != null) {
                metricsCollector.invokedTransformation("SDK-package");
            }

            // Generating bundle
            return generateBundlesAsStream(
                    asmModel.getName(),
                    asmModel.getVersion(),
                    sourceCodeOutputDir,
                    sdkJavaFileNames,
                    internalJavaFileNames,
                    internalXmlFileNames,
                    compiled);
        } catch (Exception ex) {
            packagingFailed = true;
            throw ex;
        } finally {
            if (metricsCollector != null) {
                metricsCollector.stoppedTransformation("SDK-package", System.nanoTime() - packagingStartTs, packagingFailed);
            }
        }
    }

	private static Iterable<JavaFileObject> compile(File sourceDir, Set<String> sourceCodeFiles) throws Exception {
        if (sourceCodeFiles.isEmpty()) {
            return Collections.emptyList();
        }

        Iterable<JavaFileObject> compiled = CompilerUtil.compile(compilerContextBuilder()
                .sameClassLoaderAs(Asm2SDK.class)
                .compilationFiles(fileNamesToFile(sourceDir, sourceCodeFiles))
                .build());
        return compiled;
    }

    private static Iterable<File> fileNamesToFile(File sourceDir, Set<String> sourceCodeFiles) throws Exception {
        return  sourceCodeFiles.stream().map(fn -> new File(sourceDir.getAbsolutePath(), fn)).collect(Collectors.toList());
    }

    private static Asm2SDKBundleStreams generateBundlesAsStream(String name, String version, File sourceCodeOutputDir,
			Set<String> sdkJavaFileNames, Set<String> internalJavaFileNames, Set<String> internalXmlFileNames, Iterable<JavaFileObject> compiled) throws Exception {
        TinyBundle sdkBundle = bundle();
        TinyBundle internalBundle = bundle();
        
        Set<String> sdkExportedPackages = Sets.newHashSet();
        Set<String> internalExportedPackages = Sets.newHashSet();
    	
        compiled.forEach(c -> {
            FullyQualifiedName fullyQualifiedName = (FullyQualifiedName) c;
            try {
            	if (fullyQualifiedName.getFullyQualifiedName().startsWith("internal")) {
            		internalBundle.add(getPathInBundle(fullyQualifiedName), c.openInputStream());
            		internalExportedPackages.add(getPackageName(fullyQualifiedName));
            	} else {
            		sdkBundle.add(getPathInBundle(fullyQualifiedName), c.openInputStream());
            		sdkExportedPackages.add(getPackageName(fullyQualifiedName));
            	}
            } catch (IOException e) {
                throw new RuntimeException("File not found: "+ c.getName(), e);
            }
        });
        
        addSourceFiles(sourceCodeOutputDir, sdkJavaFileNames, sdkBundle);
        addSourceFiles(sourceCodeOutputDir, internalJavaFileNames, internalBundle);
        
        addCommonBundleHeaders(sdkBundle, version);
        addCommonBundleHeaders(internalBundle, version);
        
        sdkBundle.set( Constants.BUNDLE_SYMBOLICNAME, name + "-asm2sdk-sdk" )
    	.set(Constants.IMPORT_PACKAGE,
    			"org.osgi.framework;version=\"[1.8,2.0)\"," +    					
				"hu.blackbelt.structured.map.proxy;version=\"[1.0,2.0)\""
    			);

    	internalBundle.set(Constants.BUNDLE_SYMBOLICNAME, name + "-asm2sdk-internal" )
        .set(Constants.IMPORT_PACKAGE,
              "org.osgi.framework;version=\"[1.8,2.0)\"," +
              "hu.blackbelt.judo.dao.api;version=\"[1.0,2.0)\"," +
              "hu.blackbelt.judo.dispatcher.api;version=\"[1.0,2.0)\"," +
              "hu.blackbelt.judo.meta.asm.runtime;version=\"[1.0,2.0)\"," +
              "hu.blackbelt.structured.map.proxy;version=\"[1.0,2.0)\"," +
              "org.eclipse.emf.ecore," +
              "org.eclipse.emf.common," +
              "org.eclipse.emf.common.util," +
              "org.slf4j;version=\"1.7.2\", " +
              Joiner.on(",").join(sdkExportedPackages));

        addExportedPackages(sdkBundle, sdkExportedPackages);
        addExportedPackages(internalBundle, internalExportedPackages);

        addXmlDesciptors(internalBundle, sourceCodeOutputDir, internalXmlFileNames);
        
        return new Asm2SDKBundleStreams(new CachingInputStream(sdkBundle.build()), new CachingInputStream(internalBundle.build()));
	}

	private static void addXmlDesciptors(TinyBundle bundle, File sourceDir, Set<String> xmlFilenames) {
		xmlFilenames.forEach(s -> {
            try {
                bundle.add(s, new FileInputStream(new File(sourceDir.getAbsolutePath(), s)));
            } catch (FileNotFoundException e) {
                throw new RuntimeException("File not found: " + s, e);
            }
        });
    	
        if (xmlFilenames.size() > 0) {
        	bundle.set("Service-Component", Joiner.on(",").join(xmlFilenames));
        }
	}

	private static void addCommonBundleHeaders(TinyBundle bundle, String version) {
		bundle.set( Constants.BUNDLE_MANIFESTVERSION, "2")
    	.set( Constants.BUNDLE_VERSION, version )
    	.set( Constants.REQUIRE_CAPABILITY, "osgi.extender;filter:=\"(&(osgi.extender=osgi.component)(version>=1.3.0)(!(version>=2.0.0)))\"");
	}

	private static void addExportedPackages(TinyBundle bundle, Set<String> packageNames) {
		if (packageNames.size() > 0) {
            bundle.set(Constants.EXPORT_PACKAGE, packageNames.stream().collect(Collectors.joining(",")));
        }
	}

	private static String getPathInBundle(FullyQualifiedName fullyQualifiedName) {
		return fullyQualifiedName.getFullyQualifiedName().replace('.', '/') + ".class";
	}

	private static String getPackageName(FullyQualifiedName fullyQualifiedName) {
		String packageName = fullyQualifiedName.getFullyQualifiedName()
		        .substring(0 , fullyQualifiedName.getFullyQualifiedName().lastIndexOf("."));
		return packageName;
	}

	private static void addSourceFiles(File sourceCodeOutputDir, Set<String> javaFileNames, TinyBundle bundle) {
		javaFileNames.forEach(s -> {
            try {
                bundle.add(s, new FileInputStream(new File(sourceCodeOutputDir.getAbsolutePath(), s)));
            } catch (FileNotFoundException e) {
                throw new RuntimeException("File not found: " + s, e);
            }
        });
	}

    @SneakyThrows(URISyntaxException.class)
    public static URI calculateAsm2SDKTemplateScriptURI() {
        URI psmRoot = Asm2SDK.class.getProtectionDomain().getCodeSource().getLocation().toURI();
        if (psmRoot.toString().endsWith(".jar")) {
            psmRoot = new URI("jar:" + psmRoot.toString() + "!/" + SCRIPT_ROOT_TATAMI_ASM_2_SDK);
        } else if (psmRoot.toString().startsWith("jar:bundle:")) {
            psmRoot = new URI(psmRoot.toString().substring(4, psmRoot.toString().indexOf("!")) + SCRIPT_ROOT_TATAMI_ASM_2_SDK);
        } else {
            psmRoot = new URI(psmRoot.toString() + "/" + SCRIPT_ROOT_TATAMI_ASM_2_SDK);
        }
        return psmRoot;
    }

    private static InputStream getClassByteCode(Class clazz) {
        return clazz.getClassLoader().getResourceAsStream(getClassFileName(clazz));
    }

    private static String getClassFileName(Class clazz) {
        return clazz.getName().replace(".", "/") + ".class";
    }

}


