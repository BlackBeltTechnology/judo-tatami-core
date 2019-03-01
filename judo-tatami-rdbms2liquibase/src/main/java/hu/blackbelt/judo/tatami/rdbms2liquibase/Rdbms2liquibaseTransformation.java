package hu.blackbelt.judo.tatami.rdbms2liquibase;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import hu.blackbelt.epsilon.runtime.execution.ArtifactResolver;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.ModelContext;
import hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter;
import hu.blackbelt.epsilon.runtime.execution.model.emf.EmfModelContext;
import hu.blackbelt.epsilon.runtime.execution.model.excel.ExcelModelContext;
import hu.blackbelt.judo.meta.rdbms.RdbmsMetaModel;
import hu.blackbelt.judo.meta.rdbms.RdbmsModelInfo;
import hu.blackbelt.judo.meta.rdbms.RdbmsPackage;
import hu.blackbelt.judo.tatami.core.Slf4jLog;
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


@Component(immediate = true, service = Rdbms2liquibaseTransformation.class)
@Slf4j
public class Rdbms2liquibaseTransformation {

    public static final String RDBMS_META_VERSION_RANGE = "Rdbms-Meta-Version-Range";

    @Reference
    Rdbms2liquibaseResourceLoader rdbms2liquibaseResourceLoader;

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
    public void install(RdbmsModelInfo rdbmsModelInfo) {

    	/*
    	// Target model
        String key = componentContext.getBundleContext().getBundle().getBundleId() + "-" + rdbmsModelInfo.getName();

        File targetRdbmsModelFile = componentContext.getBundleContext().getBundle().getDataFile(key + ".model");
        if (targetRdbmsModelFile.exists()) {
            targetRdbmsModelFile.delete();
        }

        LiquibaseModel rdbmsModelInfo = new RdbmsModelInfo(
                targetRdbmsModelFile,
                rdbmsModelInfo.getName(),
                rdbmsModelInfo.getVersion(),
                URI.createURI(targetRdbmsModelFile.getAbsolutePath()),
                rdbmsModelInfo.getChecksum(),
                new VersionRange(componentContext.getBundleContext().getBundle().getHeaders().get(RDBMS_META_VERSION_RANGE)));

        List<ModelContext> modelContexts = Lists.newArrayList();

        modelContexts.add(EmfModelContext.builder()
                .name("ASM")
                .artifacts(ImmutableMap.of("model", rdbmsModelInfo.getFile().getAbsolutePath()))
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
                .sourceDirectory(rdbms2liquibaseResourceLoader.getSctiptRoot())
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
                            ProgramParameter.builder().name("modelName").value(rdbmsModelInfo.getName()).build(),
                            ProgramParameter.builder().name("asmVersion").value(rdbmsModelInfo.getVersion().toString()).build(),
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
        */
    }

    public void uninstall(RdbmsModelInfo rdbmsModelInfo) {
        /*
    	String key = componentContext.getBundleContext().getBundle().getBundleId() + "-" + rdbmsModelInfo.getName();
        if (!rdbmsRegistrations.containsKey(key)) {
            log.error("Model is not registered: " + rdbmsModelInfo.getName());
        } else {
            rdbmsModels.get(key).getFile().delete();
            rdbmsRegistrations.get(key).unregister();
            rdbmsRegistrations.remove(key);
            rdbmsModels.remove(key);
        }
        */
    }


    private String getModelPath() {
        return "file:" + rdbms2liquibaseResourceLoader.getSctiptRoot().getAbsolutePath() + File.separator + "rdbms2liquibase" + File.separator + "model" + File.separator;
    }
}
