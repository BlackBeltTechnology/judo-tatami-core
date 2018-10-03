package hu.blackbelt.judo.tatami.asm2odata;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import hu.blackbelt.epsilon.runtime.execution.ArtifactResolver;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.contexts.ProgramParameter;
import hu.blackbelt.epsilon.runtime.execution.model.emf.EmfModelContext;
import hu.blackbelt.judo.meta.asm.AsmModelInfo;
import hu.blackbelt.judo.meta.asm.AsmResourceLoader;
import hu.blackbelt.judo.meta.odata.OdataMetaModel;
import hu.blackbelt.judo.meta.odata.OdataModelInfo;
import hu.blackbelt.judo.meta.odata.edm.EdmPackage;
import hu.blackbelt.judo.meta.odata.edmx.EdmxPackage;
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


@Component(immediate = true, service = Asm2OdataTransformation.class)
@Slf4j
public class Asm2OdataTransformation {

    public static final String ODATA_META_VERSION_RANGE = "Odata-Meta-Version-Range";
    public static final String HTTP_BLACKBELT_HU_JUDO_META_EXTENDED_METADATA = "http://blackbelt.hu/judo/meta/ExtendedMetadata";
    public static final String HTTP_BLACKBELT_HU_JUDO_ASM_BASE = "http://blackbelt.hu/judo/asm/base";

    @Reference
    AsmResourceLoader asmResourceLoader;

    @Reference
    Asm2ODataResourceLoader asm2ODataResourceLoader;

    @Reference
    OdataMetaModel odataMetaModel;


    Map<String, ServiceRegistration<OdataModelInfo>> odataRegistrations = new ConcurrentHashMap<>();
    Map<String, OdataModelInfo> odataModels = new HashMap<>();

    ComponentContext componentContext;

    @Activate
    public void activate(ComponentContext componentContext) {
        this.componentContext = componentContext;
    }

    @Deactivate
    public void deativate() {
        odataRegistrations.forEach((k, v) -> { v.unregister(); odataModels.get(k).getFile().delete(); });
    }

    public void install(AsmModelInfo asmModelInfo) {
        // Target model
        String key = componentContext.getBundleContext().getBundle().getBundleId() + "-" + asmModelInfo.getName();

        File targetOdataModelFile = componentContext.getBundleContext().getBundle().getDataFile(key + ".model");
        if (targetOdataModelFile.exists()) {
            targetOdataModelFile.delete();
        }

        OdataModelInfo odataModelInfo = new OdataModelInfo(
                targetOdataModelFile,
                asmModelInfo.getName(),
                asmModelInfo.getVersion(),
                URI.createURI(targetOdataModelFile.getAbsolutePath()),
                asmModelInfo.getChecksum(),
                new VersionRange(componentContext.getBundleContext().getBundle().getHeaders().get(ODATA_META_VERSION_RANGE)));

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
                .name("ODATA")
                .artifacts(ImmutableMap.of("model", targetOdataModelFile.getAbsolutePath()))
                .readOnLoad(false)
                .storeOnDisposal(true)
                .metaModelUris(ImmutableList.of(EdmPackage.eNS_URI, EdmxPackage.eNS_URI))
                .build());


        try (ExecutionContext executionContext = ExecutionContext.builder()
                .sourceDirectory(asm2ODataResourceLoader.getSctiptRoot())
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

            odataMetaModel.registerOdataMetamodel(executionContext.getResourceSet());
            executionContext.addMetaModel(asmResourceLoader.getBaseFile().getAbsolutePath());
            executionContext.addMetaModel(asmResourceLoader.getTypesFile().getAbsolutePath());

            executionContext.init();

            EtlExecutionContext etlExecutionContext = EtlExecutionContext.etlExecutionContextBuilder()
                    .source("asm2odata/transformations/odata/asmToOData.etl")
                    .parameters(ImmutableList.of(
                            ProgramParameter.builder().name("domainModelName").value(asmModelInfo.getName() + "Domain").build(),
                            ProgramParameter.builder().name("asmVersion").value(asmModelInfo.getVersion().toString()).build(),
                            ProgramParameter.builder().name("extendedMetadataURI").value(HTTP_BLACKBELT_HU_JUDO_META_EXTENDED_METADATA).build()
                    ))
                    .build();
            executionContext.executeProgram(etlExecutionContext);

            executionContext.commit();

            log.info("Registering model: " + odataModelInfo);
            ServiceRegistration<OdataModelInfo> modelServiceRegistration = componentContext.getBundleContext().registerService(OdataModelInfo.class, odataModelInfo, odataModelInfo.toDictionary());
            odataModels.put(key, odataModelInfo);
            odataRegistrations.put(key, modelServiceRegistration);

        } catch (Exception e) {
            log.error("Could not transform ASM -> ODATA model", e);
        }
    }

    public void uninstall(AsmModelInfo psmModelInfo) {
        String key = componentContext.getBundleContext().getBundle().getBundleId() + "-" + psmModelInfo.getName();
        if (!odataRegistrations.containsKey(key)) {
            log.error("Model is not registered: " + psmModelInfo.getName());
        } else {
            odataModels.get(key).getFile().delete();
            odataRegistrations.get(key).unregister();
            odataRegistrations.remove(key);
            odataModels.remove(key);
        }
    }

    private String getModelPath() {
        return "file:" + asm2ODataResourceLoader.getSctiptRoot().getAbsolutePath() + File.separator + "asm2odata" + File.separator + "model" + File.separator;
    }
}
