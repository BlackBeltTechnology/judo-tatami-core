package hu.blackbelt.judo.tatami.asm2rdbms;

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
import hu.blackbelt.judo.meta.rdbms.RdbmsMetaModel;
import hu.blackbelt.judo.meta.rdbms.RdbmsModelInfo;
import hu.blackbelt.judo.meta.rdbms.RdbmsPackage;
import hu.blackbelt.judo.tatami.core.Slf4jLog;
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


@Component(immediate = true, service = Asm2RdbmsTransformation.class)
@Slf4j
public class Asm2RdbmsTransformation {

    public static final String RDBMS_META_VERSION_RANGE = "Rdbms-Meta-Version-Range";
    public static final String HTTP_BLACKBELT_HU_JUDO_META_EXTENDED_METADATA = "http://blackbelt.hu/judo/meta/ExtendedMetadata";

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
        rdbmsRegistrations.values().forEach(e -> e.unregister());
    }

    public void install(AsmModelInfo asmModelInfo) {
        // Target model
        String key = componentContext.getBundleContext().getBundle().getBundleId() + "-" + asmModelInfo.getName();

        File targetAsmModelFile = componentContext.getBundleContext().getBundle().getDataFile(key + ".model");

        RdbmsModelInfo rdbmsModelInfo = new RdbmsModelInfo(
                targetAsmModelFile,
                asmModelInfo.getName(),
                asmModelInfo.getVersion(),
                URI.createURI(targetAsmModelFile.getAbsolutePath()),
                asmModelInfo.getChecksum(),
                new VersionRange(componentContext.getBundleContext().getBundle().getHeaders().get(RDBMS_META_VERSION_RANGE)));

        List modelContexts = Lists.newArrayList();

        modelContexts.add(EmfModelContext.builder()
                .name("SRC")
                .aliases(ImmutableList.of("JUDOPSM"))
                .artifacts(ImmutableMap.of("model", asmModelInfo.getFile().getAbsolutePath()))
                .cached(true)
                .expand(true)
                .metaModelUris(ImmutableList.of(RdbmsPackage.eNS_URI))
                .readOnLoad(true)
                .storeOnDisposal(false)
                .build());

        /*
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
        */

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

            //psmMetaModel.registerPsmMetamodel(executionContext.getResourceSet());
            executionContext.init();

            log.info("Registering model: " + rdbmsModelInfo);
            ServiceRegistration<RdbmsModelInfo> modelServiceRegistration = componentContext.getBundleContext().registerService(RdbmsModelInfo.class, rdbmsModelInfo, rdbmsModelInfo.toDictionary());
            rdbmsModels.put(key, rdbmsModelInfo);
            rdbmsRegistrations.put(key, modelServiceRegistration);

            EtlExecutionContext etlExecutionContext = EtlExecutionContext.etlExecutionContextBuilder()
                    .source("asm2rdbms/transformations/asmToRdbms.etls")
                    .parameters(ImmutableList.of(
                            ProgramParameter.builder().name("modelName").value(asmModelInfo.getName()).build(),
                            ProgramParameter.builder().name("asmVersion").value(asmModelInfo.getVersion().toString()).build(),
                            ProgramParameter.builder().name("extendedMetadataURI").value(HTTP_BLACKBELT_HU_JUDO_META_EXTENDED_METADATA).build()
                    ))
                    .build();
            executionContext.executeProgram(etlExecutionContext);

            executionContext.commit();
        } catch (Exception e) {
            log.error("Could not transform PSM model", e);
        }
    }

    public void uninstall(AsmModelInfo psmModelInfo) {
        String key = componentContext.getBundleContext().getBundle().getBundleId() + "-" + psmModelInfo.getName();
        if (!rdbmsRegistrations.containsKey(key)) {
            log.error("Model is not registered: " + psmModelInfo.getName());
        } else {
            rdbmsRegistrations.get(key).unregister();
            rdbmsRegistrations.remove(key);
            rdbmsModels.remove(key);
        }
    }

    /*

                                    <configuration>
                                    <metaModels>
                                        <metaModel>mvn:hu.blackbelt.judo.meta:judo-meta-rdbms:ecore:rdbms:${judo-meta-rdbms.version}</metaModel>
                                        <metaModel>mvn:hu.blackbelt.judo.meta:judo-meta-asm:ecore:types:${judo-meta-asm.version}</metaModel>
                                        <metaModel>mvn:hu.blackbelt.judo.meta:judo-meta-asm:ecore:base:${judo-meta-asm.version}</metaModel>
                                    </metaModels>
                                    <excelModels>
                                        <excelModel>
                                            <name>TYPEMAPPING</name>
                                            <artifact>${basedir}/model/RDBMS Data Types Hsqldb.xlsx</artifact>
                                            <configurationArtifact>file:${basedir}/model/mapping.xml</configurationArtifact>
                                            <aliases>
                                                <alias>TYPEMAPPING</alias>
                                            </aliases>
                                        </excelModel>
                                    </excelModels>
                                    <models>
                                        <model>
                                            <artifact>mvn:${test.asm.model.groupId}:${test.asm.model.artifactId}:model:asm:${test.asm.model.version}</artifact>
                                            <name>ASM</name>
                                            <metaModelUris>
                                                <param>http://www.eclipse.org/emf/2002/Ecore</param>
                                                <param>http://blackbelt.hu/judo/asm/types</param>
                                            </metaModelUris>
                                        </model>

                                        <model>
                                            <artifact>${project.build.directory}/rdbmsHsqldb.model</artifact>
                                            <name>RDBMS</name>
                                            <readOnLoad>false</readOnLoad>
                                            <storeOnDisposal>true</storeOnDisposal>
                                            <metaModelUris>
                                                <param>http://blackbelt.hu/judo/meta/psm/rdbms</param>
                                            </metaModelUris>
                                        </model>
                                    </models>
                                    <eolPrograms>
                                        <Etl>
                                            <source>${basedir}/src/transformations/asmToRdbms.etl</source>
                                            <parameters>
                                                <parameter>
                                                    <name>asmVersion</name>
                                                    <value>${project.version}</value>
                                                </parameter>
                                                <parameter>
                                                    <name>extendedMetadataURI</name>
                                                    <value>http://blackbelt.hu/judo/meta/ExtendedMetadata</value>
                                                </parameter>
                                            </parameters>
                                        </Etl>
                                    </eolPrograms>
                                </configuration>


     */
}
