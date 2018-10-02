package hu.blackbelt.judo.tatami.psm2asm;

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
import hu.blackbelt.judo.meta.psm.PsmMetaModel;
import hu.blackbelt.judo.meta.psm.PsmModelInfo;
import hu.blackbelt.judo.meta.psm.data.DataPackage;
import hu.blackbelt.judo.meta.psm.facade.FacadePackage;
import hu.blackbelt.judo.meta.psm.namespace.NamespacePackage;
import hu.blackbelt.judo.meta.psm.type.TypePackage;
import hu.blackbelt.judo.tatami.core.Slf4jLog;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;


@Component(immediate = true, service = Psm2AsmTransformation.class)
@Slf4j
public class Psm2AsmTransformation {

    public static final String HTTP_BLACKBELT_HU_JUDO_ASM_TYPES = "http://blackbelt.hu/judo/asm/types";
    public static final String HTTP_BLACKBELT_HU_JUDO_ASM_BASE = "http://blackbelt.hu/judo/asm/base";
    public static final String ASM_META_VERSION_RANGE = "Asm-Meta-Version-Range";
    public static final String HTTP_BLACKBELT_HU_JUDO_META_ASM = "http://blackbelt.hu/judo/meta/asm";
    public static final String HTTP_BLACKBELT_HU_JUDO_META_EXTENDED_METADATA = "http://blackbelt.hu/judo/meta/ExtendedMetadata";
    @Reference
    PsmMetaModel psmMetaModel;

    @Reference
    AsmResourceLoader asmResourceLoader;

    @Reference
    Psm2AsmResourceLoader psm2AsmResourceLoader;


    Map<String, ServiceRegistration<AsmModelInfo>> asmRegistrations = new ConcurrentHashMap<>();
    Map<String, PsmModelInfo> asmModels = new HashMap<>();

    ComponentContext componentContext;

    @Activate
    public void activate(ComponentContext componentContext) {
        this.componentContext = componentContext;
    }

    @Deactivate
    public void deativate() {
        asmRegistrations.values().forEach(e -> e.unregister());
    }

    public void install(PsmModelInfo psmModelInfo) {
        // Target model
        String key = componentContext.getBundleContext().getBundle().getBundleId() + "-" + psmModelInfo.getName();

        File targetAsmModelFile = componentContext.getBundleContext().getBundle().getDataFile(key + ".model");

        AsmModelInfo asmModelInfo = new AsmModelInfo(
                targetAsmModelFile,
                psmModelInfo.getName(),
                psmModelInfo.getVersion(),
                URI.createURI(targetAsmModelFile.getAbsolutePath()),
                psmModelInfo.getChecksum(),
                new VersionRange(componentContext.getBundleContext().getBundle().getHeaders().get(ASM_META_VERSION_RANGE)));

        List modelContexts = Lists.newArrayList();
        modelContexts.add(EmfModelContext.builder()
                .name("SRC")
                .aliases(ImmutableList.of("JUDOPSM"))
                .artifacts(ImmutableMap.of("model", psmModelInfo.getFile().getAbsolutePath()))
                .cached(true)
                .expand(true)
                .metaModelUris(ImmutableList.of(NamespacePackage.eNS_URI, FacadePackage.eNS_URI, DataPackage.eNS_URI, TypePackage.eNS_URI))
                .readOnLoad(true)
                .storeOnDisposal(false)
                .build());

        modelContexts.add(EmfModelContext.builder()
                .name("TYPES")
                .artifacts(ImmutableMap.of("model", asmResourceLoader.getTypesFile().getAbsolutePath()))
                .platformAlias(HTTP_BLACKBELT_HU_JUDO_ASM_TYPES)
                .cached(true)
                .expand(true)
                .metaModelUris(ImmutableList.of(EcorePackage.eNS_URI))
                .readOnLoad(true)
                .storeOnDisposal(false)
                .build());

        modelContexts.add(EmfModelContext.builder()
                .name("BASE")
                .artifacts(ImmutableMap.of("model", asmResourceLoader.getBaseFile().getAbsolutePath()))
                .platformAlias(HTTP_BLACKBELT_HU_JUDO_ASM_BASE)
                .cached(true)
                .expand(true)
                .metaModelUris(ImmutableList.of(EcorePackage.eNS_URI))
                .readOnLoad(true)
                .storeOnDisposal(false)
                .build());


        modelContexts.add(EmfModelContext.builder()
                .name("ASM")
                .artifacts(ImmutableMap.of("model", targetAsmModelFile.getAbsolutePath()))
                .platformAlias(HTTP_BLACKBELT_HU_JUDO_ASM_BASE)
                .cached(true)
                .expand(false)
                .metaModelUris(ImmutableList.of(EcorePackage.eNS_URI))
                .readOnLoad(false)
                .storeOnDisposal(true)
                .build());


        try (ExecutionContext executionContext = ExecutionContext.builder()
                .sourceDirectory(psm2AsmResourceLoader.getSctiptRoot())
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

            psmMetaModel.registerPsmMetamodel(executionContext.getResourceSet());
            executionContext.init();

            log.info("Registering model: " + asmModelInfo);
            ServiceRegistration<AsmModelInfo> modelServiceRegistration = componentContext.getBundleContext().registerService(AsmModelInfo.class, asmModelInfo, asmModelInfo.toDictionary());
            asmModels.put(key, psmModelInfo);
            asmRegistrations.put(key, modelServiceRegistration);

            EtlExecutionContext etlExecutionContext = EtlExecutionContext.etlExecutionContextBuilder()
                    .source("psm2asm/transformations/asm/psmToAsm.etl")
                    .parameters(ImmutableList.of(
                            ProgramParameter.builder().name("modelName").value(psmModelInfo.getName()).build(),
                            ProgramParameter.builder().name("nsURI").value(HTTP_BLACKBELT_HU_JUDO_META_ASM).build(),
                            ProgramParameter.builder().name("nsPrefix").value("asm").build(),
                            ProgramParameter.builder().name("extendedMetadataURI").value(HTTP_BLACKBELT_HU_JUDO_META_EXTENDED_METADATA).build()
                    ))
                    .build();
            executionContext.executeProgram(etlExecutionContext);

            executionContext.commit();
        } catch (Exception e) {
            log.error("Could not transform PSM model", e);
        }
    }

    public void uninstall(PsmModelInfo psmModelInfo) {
        String key = componentContext.getBundleContext().getBundle().getBundleId() + "-" + psmModelInfo.getName();
        if (!asmRegistrations.containsKey(key)) {
            log.error("Model is not registered: " + psmModelInfo.getName());
        } else {
            asmRegistrations.get(key).unregister();
            asmRegistrations.remove(key);
            asmModels.remove(key);
        }
    }
}
