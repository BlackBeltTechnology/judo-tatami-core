package hu.blackbelt.judo.tatami.asm2rdbms;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import hu.blackbelt.epsilon.runtime.execution.ArtifactResolver;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.ModelContext;
import hu.blackbelt.epsilon.runtime.execution.contexts.EolExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter;
import hu.blackbelt.epsilon.runtime.execution.model.emf.EmfModelContext;
import hu.blackbelt.epsilon.runtime.execution.model.excel.ExcelModelContext;
import hu.blackbelt.judo.meta.asm.AsmModelInfo;
import hu.blackbelt.judo.meta.asm.AsmResourceLoader;
import hu.blackbelt.judo.meta.rdbms.RdbmsMetaModel;
import hu.blackbelt.judo.meta.rdbms.RdbmsModelInfo;
import hu.blackbelt.judo.meta.rdbms.RdbmsPackage;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EcorePackage;
import org.osgi.framework.ServiceRegistration;
import org.osgi.framework.VersionRange;
import org.osgi.service.component.ComponentContext;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;
import org.osgi.service.component.annotations.Reference;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

import static hu.blackbelt.judo.tatami.asm2rdbms.Asm2RdbmsTransformation.DIALECT_HSQLDB;
import static hu.blackbelt.judo.tatami.asm2rdbms.Asm2RdbmsTransformation.DIALECT_ORACLE;
import static hu.blackbelt.judo.tatami.asm2rdbms.Asm2RdbmsTransformation.DIALECT_POSTGGRESSQL;


@ObjectClassDefinition(name = "ASM2RDMS Transformation", description = "Judo Tatami ASM2RDMS Transformation")
@interface Asm2RdbmsTransformationServiceConfiguration {
    @AttributeDefinition(name = "dialects", description = "SQL Dialecst to transforms")
    String[] dialects() default {DIALECT_HSQLDB, DIALECT_POSTGGRESSQL, DIALECT_ORACLE} ;
}
@Component(immediate = true, service = Asm2RdbmsTransformation.class)
@Designate(ocd = Asm2RdbmsTransformationServiceConfiguration.class)
@Slf4j
public class Asm2RdbmsTransformation {

    public static final String RDBMS_META_VERSION_RANGE = "Rdbms-Meta-Version-Range";
    public static final String HTTP_BLACKBELT_HU_JUDO_META_EXTENDED_METADATA = "http://blackbelt.hu/judo/meta/ExtendedMetadata";
    public static final String HTTP_BLACKBELT_HU_JUDO_ASM_TYPES = "http://blackbelt.hu/judo/asm/types";
    public static final String HTTP_BLACKBELT_HU_JUDO_ASM_BASE = "http://blackbelt.hu/judo/asm/base";

    public static final String DIALECT_HSQLDB = "hsqldb";
    public static final String DIALECT_POSTGGRESSQL = "postgressql";
    public static final String DIALECT_ORACLE = "oracle";

    Map<String, String> dialectTypeFileNames = ImmutableMap.of(
        DIALECT_HSQLDB, "RDBMS Data Types Hsqldb.xlsx",
        DIALECT_POSTGGRESSQL, "RDBMS Data Types Postgres.xlsx",
        DIALECT_ORACLE, "RDBMS Data Types Oracle.xlsx"
    );

    @Reference
    AsmResourceLoader asmResourceLoader;

    @Reference
    Asm2RdbmsResourceLoader asm2RdbmsResourceLoader;

    @Reference
    RdbmsMetaModel rdbmsMetaModel;
    
    Map<String, ServiceRegistration<RdbmsModelInfo>> rdbmsRegistrations = new ConcurrentHashMap<>();
    Map<String, RdbmsModelInfo> rdbmsModels = new HashMap<>();

    ComponentContext componentContext;
    Asm2RdbmsTransformationServiceConfiguration configuration;

    @Activate
    public void activate(ComponentContext componentContext, Asm2RdbmsTransformationServiceConfiguration configuration) {
        this.componentContext = componentContext;
        this.configuration = configuration;
    }

    @Deactivate
    public void deativate() {
        rdbmsRegistrations.forEach((k, v) -> { v.unregister(); rdbmsModels.get(k).getFile().delete(); });
    }
    @SneakyThrows(UnsupportedEncodingException.class)
    public void install(AsmModelInfo asmModelInfo) {
        for (String dialect : configuration.dialects()) {

            // Target model
            String key = componentContext.getBundleContext().getBundle().getBundleId() + "-" + dialect + "-" + asmModelInfo.getName();

            File targetRdbmsModelFile = componentContext.getBundleContext().getBundle().getDataFile(key + ".judo-meta-rdbms");
            File targetRdbmsTraceModelFile = componentContext.getBundleContext().getBundle().getDataFile(key + ".asm2rdbms-trace");
            File targetRdbmsTraceEcoreFile = new File(componentContext.getBundleContext().getBundle().getDataFile("asm2rdbms"), "model/RDBMS-ASM-Trace.ecore");

            if (targetRdbmsModelFile.exists()) {
                targetRdbmsModelFile.delete();
            }

            if (targetRdbmsTraceModelFile.exists()) {
                targetRdbmsTraceModelFile.delete();
            }

            RdbmsModelInfo rdbmsModelInfo = new RdbmsModelInfo(
                    targetRdbmsModelFile,
                    asmModelInfo.getName(),
                    asmModelInfo.getVersion(),
                    URI.createURI(targetRdbmsModelFile.getAbsolutePath()),
                    asmModelInfo.getChecksum(),
                    new VersionRange(componentContext.getBundleContext().getBundle().getHeaders().get(RDBMS_META_VERSION_RANGE)));

            List<ModelContext> modelContexts = Lists.newArrayList();

            modelContexts.add(EmfModelContext.builder()
                    .name("ASM")
                    .artifacts(ImmutableMap.of("model", asmModelInfo.getFile().getAbsolutePath()))
                    .metaModelUris(ImmutableList.of(
                            EcorePackage.eNS_URI,
                            HTTP_BLACKBELT_HU_JUDO_ASM_BASE)
                    )
                    .build());

            modelContexts.add(EmfModelContext.builder()
                    .name("TYPES")
                    .artifacts(ImmutableMap.of("model", asmResourceLoader.getTypesFile().getAbsolutePath()))
                    .platformAlias(HTTP_BLACKBELT_HU_JUDO_ASM_TYPES)
                    .metaModelUris(ImmutableList.of(EcorePackage.eNS_URI))
                    .build());

            modelContexts.add(EmfModelContext.builder()
                    .name("BASE")
                    .artifacts(ImmutableMap.of("model", asmResourceLoader.getBaseFile().getAbsolutePath()))
                    .platformAlias(HTTP_BLACKBELT_HU_JUDO_ASM_BASE)
                    .metaModelUris(ImmutableList.of(EcorePackage.eNS_URI))
                    .build());

            modelContexts.add(EmfModelContext.builder()
                    .name("RDBMS")
                    .artifacts(ImmutableMap.of("model", targetRdbmsModelFile.getAbsolutePath()))
                    .readOnLoad(false)
                    .storeOnDisposal(true)
                    .metaModelUris(ImmutableList.of(RdbmsPackage.eNS_URI))
                    .build());

            modelContexts.add(ExcelModelContext.builder()
                    .name("TYPEMAPPING")
                    .aliases(ImmutableList.of("TYPEMAPPING"))
                    .artifacts(ImmutableMap.of(
                            "excelSheet", getModelPath() + URLDecoder.decode("RDBMS Data Types Hsqldb.xlsx", "UTF-8"),
                            "excelConfiguration", getModelPath() + "typemapping.xml"))
                    .spreadSheetPassword("")
                    .build());

            modelContexts.add(ExcelModelContext.builder()
                    .name("RULEMAPPING")
                    .aliases(ImmutableList.of("RULEMAPPING"))
                    .artifacts(ImmutableMap.of(
                            "excelSheet", getModelPath() + URLDecoder.decode("RDBMS Table Mapping Rules.xlsx", "UTF-8"),
                            "excelConfiguration", getModelPath() + "rulemapping.xml"))
                    .spreadSheetPassword("")
                    .build());

            modelContexts.add(ExcelModelContext.builder()
                    .name("NAMEMAPPING")
                    .aliases(ImmutableList.of("NAMEMAPPING"))
                    .artifacts(ImmutableMap.of(
                            "excelSheet", getModelPath() + URLDecoder.decode(dialectTypeFileNames.get(dialect), "UTF-8"),
                            "excelConfiguration", getModelPath() + "namemapping.xml"))
                    .spreadSheetPassword("")
                    .build());


            modelContexts.add(EmfModelContext.builder()
                    .name("TRACE")
                    .artifacts(ImmutableMap.of("model", targetRdbmsTraceModelFile.getAbsolutePath()))
                    .readOnLoad(false)
                    .storeOnDisposal(true)
                    .metaModelUris(ImmutableList.of("http://blackbelt.hu/judo/meta/asm2rdbms/trace"))
                    .build());


            try (ExecutionContext executionContext = ExecutionContext.builder()
                    .sourceDirectory(asm2RdbmsResourceLoader.getSctiptRoot())
                    .modelContexts(modelContexts)
                    .metaModels(ImmutableList.of())
                    .artifactResolver(new ArtifactResolver() {
                        public URI getArtifactAsEclipseURI(String s) {
                            return URI.createURI(s);
                        }
                    })
                    .profile(false)
                    .log(new Slf4jLog())
                    .build()) {

                // asmResourceLoader.loadToReourceSet(executionContext.getResourceSet());
                rdbmsMetaModel.registerRdbmsMetamodel(executionContext.getResourceSet());
                executionContext.addMetaModel(asmResourceLoader.getBaseFile().getAbsolutePath());
                executionContext.addMetaModel(asmResourceLoader.getTypesFile().getAbsolutePath());
                executionContext.addMetaModel(targetRdbmsTraceEcoreFile.getAbsolutePath());

                executionContext.load();

                EtlExecutionContext asmToRdbms = EtlExecutionContext.etlExecutionContextBuilder()
                        .source("asm2rdbms/transformations/asmToRdbms.etl")
                        .parameters(ImmutableList.of(
                                ProgramParameter.builder().name("modelName").value(asmModelInfo.getName()).build(),
                                ProgramParameter.builder().name("modelVersion").value(asmModelInfo.getVersion().toString()).build(),
                                ProgramParameter.builder().name("asmVersion").value(asmModelInfo.getVersion().toString()).build(),
                                ProgramParameter.builder().name("dialect").value(dialect).build(),
                                ProgramParameter.builder().name("extendedMetadataURI").value(HTTP_BLACKBELT_HU_JUDO_META_EXTENDED_METADATA).build()
                        ))
                        .exportTransformationTrace("transformationTrace")
                        .build();
                executionContext.executeProgram(asmToRdbms);

                EolExecutionContext exportRdbmsTrace = EolExecutionContext.eolExecutionContextBuilder()
                        .source("asm2rdbms/transformations/exportRdbmsTrace.eol")
                        .build();
                executionContext.executeProgram(exportRdbmsTrace);

                executionContext.commit();

                log.info("Registering model: " + rdbmsModelInfo);
                ServiceRegistration<RdbmsModelInfo> modelServiceRegistration = componentContext.getBundleContext().registerService(RdbmsModelInfo.class, rdbmsModelInfo, rdbmsModelInfo.toDictionary());
                rdbmsModels.put(key, rdbmsModelInfo);
                rdbmsRegistrations.put(key, modelServiceRegistration);

            } catch (Exception e) {
                log.error("Could not transform ASM -> RDBMS model", e);
            }
        }

    }

    public void uninstall(AsmModelInfo asmModelInfo) {
        for (String dialect : configuration.dialects()) {

            String key = componentContext.getBundleContext().getBundle().getBundleId() + "-" + dialect + "-" + asmModelInfo.getName();

            if (!rdbmsRegistrations.containsKey(key)) {
                log.error("Model is not registered: " + asmModelInfo.getName() + " for dialect " + dialect);
            } else {
                rdbmsModels.get(key).getFile().delete();
                rdbmsRegistrations.get(key).unregister();
                rdbmsRegistrations.remove(key);
                rdbmsModels.remove(key);
            }
        }
    }

    private String getModelPath() {
        return "file:" + asm2RdbmsResourceLoader.getSctiptRoot().getAbsolutePath() + File.separator + "asm2rdbms" + File.separator + "model" + File.separator;
    }
}
