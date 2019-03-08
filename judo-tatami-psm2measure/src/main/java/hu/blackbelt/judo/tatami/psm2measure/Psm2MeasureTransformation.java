package hu.blackbelt.judo.tatami.psm2measure;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import hu.blackbelt.epsilon.runtime.execution.ArtifactResolver;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.ModelContext;
import hu.blackbelt.epsilon.runtime.execution.contexts.EtlExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.model.emf.EmfModelContext;
import hu.blackbelt.judo.meta.measure.MeasureModelInfo;
import hu.blackbelt.judo.meta.measure.MeasurePackage;
import hu.blackbelt.judo.meta.psm.PsmMetaModel;
import hu.blackbelt.judo.meta.psm.PsmModelInfo;
import hu.blackbelt.judo.meta.psm.data.DataPackage;
import hu.blackbelt.judo.meta.psm.namespace.NamespacePackage;
import hu.blackbelt.judo.meta.psm.type.TypePackage;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.common.util.URI;
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


@Component(immediate = true, service = Psm2MeasureTransformation.class)
@Slf4j
public class Psm2MeasureTransformation {

    public static final String MEASURE_META_VERSION_RANGE = "Measure-Meta-Version-Range";
    public static final String HTTP_BLACKBELT_HU_JUDO_META_MEAURE = "http://blackbelt.hu/judo/meta/measure";

    @Reference
    PsmMetaModel psmMetaModel;

    @Reference
    Psm2MeasureResourceLoader psm2MeasureResourceLoader;

    Map<String, ServiceRegistration<MeasureModelInfo>> measureRegistrations = new ConcurrentHashMap<>();
    Map<String, MeasureModelInfo> measureModels = new HashMap<>();

    ComponentContext componentContext;

    @Activate
    public void activate(ComponentContext componentContext) {
        this.componentContext = componentContext;
    }

    @Deactivate
    public void deativate() {
        measureRegistrations.forEach((k, v) -> { v.unregister(); measureModels.get(k).getFile().delete(); });
    }

    public void install(PsmModelInfo psmModelInfo) {
        // Target model
        String key = componentContext.getBundleContext().getBundle().getBundleId() + "-" + psmModelInfo.getName();

        File targetMeasureModelFile = componentContext.getBundleContext().getBundle().getDataFile(key + ".model");
        if (targetMeasureModelFile.exists()) {
            targetMeasureModelFile.delete();
        }

        MeasureModelInfo measureModelInfo = new MeasureModelInfo(
                targetMeasureModelFile,
                psmModelInfo.getName(),
                psmModelInfo.getVersion(),
                URI.createURI(targetMeasureModelFile.getAbsolutePath()),
                psmModelInfo.getChecksum(),
                new VersionRange(componentContext.getBundleContext().getBundle().getHeaders().get(MEASURE_META_VERSION_RANGE)));

        List<ModelContext> modelContexts = Lists.newArrayList();
        modelContexts.add(EmfModelContext.builder()
                .name("SRC")
                .aliases(ImmutableList.of("JUDOPSM"))
                .artifacts(ImmutableMap.of("model", psmModelInfo.getFile().getAbsolutePath()))
                .metaModelUris(ImmutableList.of(NamespacePackage.eNS_URI, DataPackage.eNS_URI, TypePackage.eNS_URI))
                .expand(true)
                .build());

        modelContexts.add(EmfModelContext.builder()
                .name("MEASURE")
                .artifacts(ImmutableMap.of("model", targetMeasureModelFile.getAbsolutePath()))
                .metaModelUris(ImmutableList.of(MeasurePackage.eNS_URI))
                .readOnLoad(false)
                .storeOnDisposal(true)
                .build());


        try (ExecutionContext executionContext = ExecutionContext.builder()
                .sourceDirectory(psm2MeasureResourceLoader.getSctiptRoot())
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

            EtlExecutionContext etlExecutionContext = EtlExecutionContext.etlExecutionContextBuilder()
                    .source("psm2measure/transformations/measure/psmToMeasure.etl")
                    .build();
            executionContext.executeProgram(etlExecutionContext);

            executionContext.commit();

            log.info("Registering model: " + measureModelInfo);
            ServiceRegistration<MeasureModelInfo> modelServiceRegistration = componentContext.getBundleContext().registerService(MeasureModelInfo.class, measureModelInfo, measureModelInfo.toDictionary());
            measureModels.put(key, measureModelInfo);
            measureRegistrations.put(key, modelServiceRegistration);
        } catch (Exception e) {
            log.error("Could not transform PSM -> measure model", e);
        }
    }

    public void uninstall(PsmModelInfo psmModelInfo) {
        String key = componentContext.getBundleContext().getBundle().getBundleId() + "-" + psmModelInfo.getName();
        if (!measureRegistrations.containsKey(key)) {
            log.error("Model is not registered: " + psmModelInfo.getName());
        } else {
            measureModels.get(key).getFile().delete();
            measureRegistrations.get(key).unregister();
            measureRegistrations.remove(key);
            measureModels.remove(key);
        }
    }
}
