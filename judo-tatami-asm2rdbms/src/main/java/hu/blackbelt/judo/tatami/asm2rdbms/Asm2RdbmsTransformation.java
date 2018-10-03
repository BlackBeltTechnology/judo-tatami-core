package hu.blackbelt.judo.tatami.asm2rdbms;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import hu.blackbelt.epsilon.runtime.execution.ArtifactResolver;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter;
import hu.blackbelt.epsilon.runtime.execution.model.emf.EmfModelContext;
import hu.blackbelt.epsilon.runtime.execution.model.excel.ExcelModelContext;
import hu.blackbelt.judo.meta.asm.AsmModelInfo;
import hu.blackbelt.judo.meta.asm.AsmResourceLoader;
import hu.blackbelt.judo.meta.rdbms.RdbmsMetaModel;
import hu.blackbelt.judo.meta.rdbms.RdbmsModelInfo;
import hu.blackbelt.judo.meta.rdbms.RdbmsPackage;
import hu.blackbelt.judo.tatami.core.Slf4jLog;
import hu.blackbelt.osgi.utils.osgi.api.BundleUtil;
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

import java.io.File;
import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component(immediate = true, service = Asm2RdbmsTransformation.class)
@Slf4j
public class Asm2RdbmsTransformation {

    public static final String RDBMS_META_VERSION_RANGE = "Rdbms-Meta-Version-Range";
    public static final String HTTP_BLACKBELT_HU_JUDO_META_EXTENDED_METADATA = "http://blackbelt.hu/judo/meta/ExtendedMetadata";
    public static final String HTTP_BLACKBELT_HU_JUDO_ASM_TYPES = "http://blackbelt.hu/judo/asm/types";
    public static final String HTTP_BLACKBELT_HU_JUDO_ASM_BASE = "http://blackbelt.hu/judo/asm/base";

    @Reference
    AsmResourceLoader asmResourceLoader;

    @Reference
    Asm2RdbmsResourceLoader asm2RdbmsResourceLoader;

    @Reference
    RdbmsMetaModel rdbmsMetaModel;


    Map<String, ServiceRegistration<RdbmsModelInfo>> rdbmsRegistrations = new ConcurrentHashMap<>();
    Map<String, RdbmsModelInfo> rdbmsModels = new HashMap<>();

    ComponentContext componentContext;

    @Activate
    public void activate(ComponentContext componentContext) {
        this.componentContext = componentContext;
    }

    @Deactivate
    public void deativate() {
        rdbmsRegistrations.forEach((k, v) -> { v.unregister(); rdbmsModels.get(k).getFile().delete(); });
    }

    @SneakyThrows(UnsupportedEncodingException.class)
    public void install(AsmModelInfo asmModelInfo) {
        // Target model
        String key = componentContext.getBundleContext().getBundle().getBundleId() + "-" + asmModelInfo.getName();

        File targetRdbmsModelFile = componentContext.getBundleContext().getBundle().getDataFile(key + ".model");
        if (targetRdbmsModelFile.exists()) {
            targetRdbmsModelFile.delete();
        }

        RdbmsModelInfo rdbmsModelInfo = new RdbmsModelInfo(
                targetRdbmsModelFile,
                asmModelInfo.getName(),
                asmModelInfo.getVersion(),
                URI.createURI(targetRdbmsModelFile.getAbsolutePath()),
                asmModelInfo.getChecksum(),
                new VersionRange(componentContext.getBundleContext().getBundle().getHeaders().get(RDBMS_META_VERSION_RANGE)));

        List modelContexts = Lists.newArrayList();

        modelContexts.add(EmfModelContext.builder()
                .name("ASM")
                .artifacts(ImmutableMap.of("model", asmModelInfo.getFile().getAbsolutePath()))
                .metaModelUris(ImmutableList.of(
                        EcorePackage.eNS_URI,
                        HTTP_BLACKBELT_HU_JUDO_ASM_BASE)
                )
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
                        "excelConfiguration", getModelPath() + "mapping.xml"))
                .spreadSheetPassword("")
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

            executionContext.init();

            EtlExecutionContext etlExecutionContext = EtlExecutionContext.etlExecutionContextBuilder()
                    .source("asm2rdbms/transformations/asmToRdbms.etl")
                    .parameters(ImmutableList.of(
                            ProgramParameter.builder().name("modelName").value(asmModelInfo.getName()).build(),
                            ProgramParameter.builder().name("asmVersion").value(asmModelInfo.getVersion().toString()).build(),
                            ProgramParameter.builder().name("extendedMetadataURI").value(HTTP_BLACKBELT_HU_JUDO_META_EXTENDED_METADATA).build()
                    ))
                    .build();
            executionContext.executeProgram(etlExecutionContext);

            executionContext.commit();

            log.info("Registering model: " + rdbmsModelInfo);
            ServiceRegistration<RdbmsModelInfo> modelServiceRegistration = componentContext.getBundleContext().registerService(RdbmsModelInfo.class, rdbmsModelInfo, rdbmsModelInfo.toDictionary());
            rdbmsModels.put(key, rdbmsModelInfo);
            rdbmsRegistrations.put(key, modelServiceRegistration);

        } catch (Exception e) {
            log.error("Could not transform ASM -> RDBMS model", e);
        }
    }

    public void uninstall(AsmModelInfo psmModelInfo) {
        String key = componentContext.getBundleContext().getBundle().getBundleId() + "-" + psmModelInfo.getName();
        if (!rdbmsRegistrations.containsKey(key)) {
            log.error("Model is not registered: " + psmModelInfo.getName());
        } else {
            rdbmsModels.get(key).getFile().delete();
            rdbmsRegistrations.get(key).unregister();
            rdbmsRegistrations.remove(key);
            rdbmsModels.remove(key);
        }
    }

    private String getModelPath() {
        return "file:" + asm2RdbmsResourceLoader.getSctiptRoot().getAbsolutePath() + File.separator + "asm2rdbms" + File.separator + "model" + File.separator;
    }
}
