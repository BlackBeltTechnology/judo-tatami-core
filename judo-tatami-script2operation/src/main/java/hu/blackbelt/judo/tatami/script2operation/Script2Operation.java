package hu.blackbelt.judo.tatami.script2operation;

import com.google.common.collect.Maps;
import com.google.common.collect.Sets;
import hu.blackbelt.judo.framework.compiler.api.CompilerUtil;
import hu.blackbelt.judo.framework.compiler.api.FullyQualifiedName;
import hu.blackbelt.judo.framework.compiler.api.fileobject.JavaFileObjects;
import hu.blackbelt.judo.meta.script.runtime.ScriptModel;
import hu.blackbelt.judo.meta.script.support.ScriptModelResourceSupport;
import hu.blackbelt.judo.script.codegen.generator.Script2JavaGenerator;
import org.ops4j.pax.tinybundles.core.TinyBundle;
import org.osgi.framework.Constants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.tools.JavaFileObject;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.nio.charset.Charset;
import java.util.Collections;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

import static hu.blackbelt.judo.framework.compiler.api.CompilerContext.compilerContextBuilder;
import static hu.blackbelt.judo.meta.script.support.ScriptModelResourceSupport.scriptModelResourceSupportBuilder;
import static org.ops4j.pax.tinybundles.core.TinyBundles.bundle;


public class Script2Operation {

    static Logger log = LoggerFactory.getLogger(Script2Operation.class);

    public static InputStream executeScript2OperationGeneration(ScriptModel scriptModel) throws Exception {

        ScriptModelResourceSupport scriptModelResourceSupport =
                scriptModelResourceSupportBuilder().resourceSet(scriptModel.getResourceSet()).build();

        Script2JavaGenerator scriptGenerator = new Script2JavaGenerator();

        Map<String, String> sourceCodesByFqName = Maps.newHashMap();
        Map<String, String> scrXmlFilesByFqName = Maps.newHashMap();


        scriptModelResourceSupport.getStreamOfScriptBindingOperationBinding()
                .forEach(binding -> {
                    String packageName = scriptGenerator.generatePackageName(binding.getTypeName());
                    String unitName =  scriptGenerator.generateClassName(binding.getOperationName());
                    String sourceCode = String.valueOf(scriptGenerator.generate(binding.getScript(), binding));

                    String operationFQName = String.valueOf(binding.getTypeName()).replace("::", ".") + "#" + binding.getOperationName();

                    String modelName = scriptModel.getName();

                    sourceCodesByFqName.put(packageName + "." + unitName, sourceCode);
                    scrXmlFilesByFqName.put(packageName + "." + unitName,

                    "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
                        "<scr:component xmlns:scr=\"http://www.osgi.org/xmlns/scr/v1.3.0\" name=\"" + packageName + "." + unitName + "\" immediate=\"true\">\n" +
                        "    <implementation class=\"" + packageName + "." + unitName + "\"/>\n" +
                        "    <property name=\"judo.model.name\" value=\"" + modelName + "\"/>\n" +
                        "    <property name=\"operation.name\" value=\"" + operationFQName + "\"/>\n" +
                        "    <property name=\"script\" value=\"true\"/>\n" +
                        "    <service>\n" +
                        "        <provide interface=\"java.util.function.Function\"/>\n" +
                        "    </service>\n" +
                        "    <reference name=\"dao\" interface=\"hu.blackbelt.judo.dao.api.DAO\" field=\"dao\" target=\"(judo.model.name=" + modelName + ")\"/>\n" +
                        "    <reference name=\"dispatcher\" interface=\"hu.blackbelt.judo.dispatcher.api.Dispatcher\" field=\"dispatcher\" target=\"(judo.model.name=" + modelName + ")\"/>\n" +
                        "    <reference name=\"asmModel\" interface=\"hu.blackbelt.judo.meta.asm.runtime.AsmModel\" bind=\"setAsmModel\" target=\"(name=" + modelName + ")\"/>\n" +
                        "    <reference name=\"identifierProvider\" interface=\"hu.blackbelt.judo.dao.api.IdentifierProvider\" field=\"idProvider\"/>\n" +
                        "\n" +
                        "</scr:component>\n"
                    );
                });

        if (log.isDebugEnabled()) {
            sourceCodesByFqName.entrySet().forEach(e -> {
                log.debug(e.getKey() + "\n\n" + e.getValue());
            });
        }



        // Generating bundle
        return generateBundle(
                scriptModel.getName(),
                scriptModel.getVersion(),
                compile(sourceCodesByFqName),
                sourceCodesByFqName,
                scrXmlFilesByFqName
        );
    }

    private static Iterable<JavaFileObject> compile(Map<String, String> sourceCodeByFqName) throws Exception {
        if (sourceCodeByFqName.isEmpty()) {
            return Collections.emptyList();
        }

        Iterable<JavaFileObject> compiled = CompilerUtil.compile(compilerContextBuilder()
                .sameClassLoaderAs(Script2Operation.class)
                .compilationUnits(
                        sourceCodeByFqName.entrySet().stream()
                                .map(
                                        e -> JavaFileObjects.forSourceLines(e.getKey(), e.getValue()))
                                .collect(Collectors.toList())
                )
                .build());
        return compiled;
    }

    private static Iterable<File> fileNamesToFile(File sourceDir, Set<String> sourceCodeFiles) throws Exception {
        return  sourceCodeFiles.stream().map(fn -> new File(sourceDir.getAbsolutePath(), fn)).collect(Collectors.toList());
    }

    private static InputStream generateBundle(String modelName, String version, Iterable<JavaFileObject> compiled,
                                              Map<String, String> sourceCodeByFqName, Map<String, String> scrXmlFilesByFqName) throws FileNotFoundException {
        TinyBundle bundle = bundle();

        Set<String> exportedPackages = Sets.newHashSet();

        compiled.forEach(javaFileObject -> {
            FullyQualifiedName fullyQualifiedName = (FullyQualifiedName) javaFileObject;
            try {
                bundle.add(fullyQualifiedName.getFullyQualifiedName().replace('.', '/') + ".class",
                        javaFileObject.openInputStream());

                String packageName = fullyQualifiedName.getFullyQualifiedName()
                        .substring(0 , fullyQualifiedName.getFullyQualifiedName().lastIndexOf("."));

                exportedPackages.add(packageName);
            } catch (IOException e) {
                throw new RuntimeException("File not found: "+ javaFileObject.getName(), e);
            }
        });
        sourceCodeByFqName.entrySet().forEach(s -> {
            bundle.add(s.getKey().replace(".", "/") + ".java",
                    new ByteArrayInputStream(s.getValue().getBytes(Charset.forName("UTF-8"))));
        });

        scrXmlFilesByFqName.entrySet().forEach(s -> {
            bundle.add("OSGI-INF/" + s.getKey() + ".xml",
                    new ByteArrayInputStream(s.getValue().getBytes(Charset.forName("UTF-8"))));
        });

        bundle.set( Constants.BUNDLE_MANIFESTVERSION, "2")
                .set( Constants.BUNDLE_SYMBOLICNAME, modelName + "-script2operation" )
                .set( Constants.BUNDLE_VERSION, version )
                .set( Constants.REQUIRE_CAPABILITY,
                        "osgi.extender;filter:=\"(&(osgi.extender=osgi.component)(version>=1.3.0)(!(version>=2.0.0)))\"")
                .set( Constants.IMPORT_PACKAGE,
                        "org.osgi.framework;version=\"[1.8,2.0)\"," +
                        "hu.blackbelt.judo.dao.api;version=\"[1.0,2.0)\"," +
                        "hu.blackbelt.judo.dispatcher.api;version=\"[1.0,2.0)\"," +
                        "hu.blackbelt.judo.meta.asm.runtime;version=\"[1.0,2.0)\"," + 
                        "hu.blackbelt.judo.operation.utils;version=\"[1.0,2.0)\"," +
                        "org.eclipse.emf.ecore," +
                        "org.eclipse.emf.common," +
                        "org.eclipse.emf.common.util," +
                        "org.slf4j;version=\"1.7.2\""
                );

        if (exportedPackages.size() > 0) {
                bundle.set( Constants.EXPORT_PACKAGE, exportedPackages.stream().collect(Collectors.joining(",")));

        }

        if (scrXmlFilesByFqName.size() > 0) {
                bundle.set("Service-Component",
                    scrXmlFilesByFqName.keySet().stream()
                            .map(n -> "OSGI-INF/" + n + ".xml")
                            .collect(Collectors.joining(",")));
        }
        return bundle.build();
    }

    private static InputStream getClassByteCode(Class clazz) {
        return clazz.getClassLoader().getResourceAsStream(getClassFileName(clazz));
    }

    private static String getClassFileName(Class clazz) {
        return clazz.getName().replace(".", "/") + ".class";
    }

}


