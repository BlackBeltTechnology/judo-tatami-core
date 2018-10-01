package hu.blackbelt.judo.tatami.psm2asm;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Lists;
import hu.blackbelt.epsilon.runtime.execution.ArtifactResolver;
import hu.blackbelt.epsilon.runtime.execution.ExecutionContext;
import hu.blackbelt.epsilon.runtime.execution.ModelContext;
import hu.blackbelt.epsilon.runtime.execution.model.emf.EmfModelContext;
import hu.blackbelt.judo.meta.asm.AsmResourceLoader;
import hu.blackbelt.judo.meta.psm.PsmMetaModel;
import hu.blackbelt.judo.meta.psm.PsmModel;
import hu.blackbelt.judo.meta.psm.data.DataPackage;
import hu.blackbelt.judo.meta.psm.facade.FacadePackage;
import hu.blackbelt.judo.meta.psm.namespace.Namespace;
import hu.blackbelt.judo.meta.psm.namespace.NamespacePackage;
import hu.blackbelt.judo.meta.psm.type.TypePackage;
import org.eclipse.emf.common.util.URI;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;

import java.util.List;


@Component(immediate = true, service = Psm2AsmTransformation.class)
public class Psm2AsmTransformation {

    @Reference
    PsmMetaModel  psmMetaModel;

    @Reference
    AsmResourceLoader asmResourceLoader;

    @Reference
    Psm2AsmResourceLoader  psm2AsmResourceLoader;

    public void execute(PsmModel psmModel) {
        /*
        ExecutionContext executionContext



        if (models != null) {
            modelContexts.addAll(models.stream().map(m -> m.toModelContext()).collect(Collectors.toList()));
        }
        if (xmlModels != null) {
            modelContexts.addAll(xmlModels.stream().map(m -> m.toModelContext()).collect(Collectors.toList()));
        }
        if (plainXmlModels != null) {
            modelContexts.addAll(plainXmlModels.stream().map(m -> m.toModelContext()).collect(Collectors.toList()));
        }
        if (excelModels != null) {
            modelContexts.addAll(excelModels.stream().map(m -> m.toModelContext()).collect(Collectors.toList()));
        } */

        List modelContexts = Lists.newArrayList();
        modelContexts.add(EmfModelContext.builder()
                .name("SRC")
                .aliases(ImmutableList.of("JUDOPSM"))
                .artifacts(ImmutableMap.of("model", psmModel.getFile().getAbsolutePath()))
                .cached(true)
                .expand(true)
                .metaModelUris(ImmutableList.of(NamespacePackage.eNS_URI, FacadePackage.eNS_URI, DataPackage.eNS_URI, TypePackage.eNS_URI))
                .readOnLoad(true)
                .storeOnDisposal(false)
                .build());


        try (ExecutionContext executionContext = ExecutionContext.builder()
                .sourceDirectory(psm2AsmResourceLoader.getSctiptRoot())
                .modelContexts(modelContexts)
                .artifactResolver(new ArtifactResolver() {
                    public URI getArtifactAsEclipseURI(String s) {
                        return URI.createURI(s);
                    }
                })
                .profile(false)
                .log(new Psm2AsmLog())
                .build()) {
            psmMetaModel.registerPsmMetamodel(executionContext.getResourceSet());
            executionContext.init();
        } catch (Exception e) {

        }
    }


        /*
        try (ExecutionContext executionContext = ExecutionContext.builder()
                .metaModels(psmMetaModel.getFactory())
                .modelContexts(modelContexts)
                .artifactResolver(MavenArtifactResolver.builder()
                        .repoSession(repoSession)
                        .repositories(repositories)
                        .repoSystem(repoSystem)
                        .log(log)
                        .build())
                .profile(profile)
                .sourceDirectory(sourceDirectory)
                .log(log)
                .build()) {

            executionContext.init();
            eolPrograms.stream().forEach(p -> { executionContext.executeProgram(p.toExecutionContext()); });
            executionContext.commit();
        } catch (Exception e) {
            throw new MojoExecutionException("Execution error: " + e.toString(), e);
        }
    }
*/

/*
                                <configuration>
                                    <metaModels>
                                        <metaModel>
                                            mvn:hu.blackbelt.judo.meta:judo-meta-psm:model:psm:${judo-meta-psm.version}
                                        </metaModel>
                                    </metaModels>

                                    <models>
                                        <model>
                                            <artifact>
                                                mvn:${test.psm.model.groupId}:${test.psm.model.artifactId}:model:psm:${test.psm.model.version}
                                            </artifact>
                                            <name>SRC</name>
                                            <aliases>
                                                <alias>JUDOPSM</alias>
                                            </aliases>
                                            <metaModelUris>
                                                <param>http://blackbelt.hu/judo/meta/psm/namespace</param>
                                                <param>http://blackbelt.hu/judo/meta/psm/type</param>
                                                <param>http://blackbelt.hu/judo/meta/psm/data</param>
                                                <param>http://blackbelt.hu/judo/meta/psm/facade</param>
                                            </metaModelUris>
                                            <expand>true</expand>
                                        </model>
                                        <model>
                                            <artifact>mvn:hu.blackbelt.judo.meta:judo-meta-asm:ecore:types:${judo-meta-asm.version}</artifact>
                                            <name>TYPES</name>
                                            <metaModelUris>
                                                <param>http://www.eclipse.org/emf/2002/Ecore</param>
                                            </metaModelUris>
                                            <platformAlias>http://blackbelt.hu/judo/asm/types</platformAlias>
                                        </model>
                                        <model>
                                            <artifact>mvn:hu.blackbelt.judo.meta:judo-meta-asm:ecore:base:${judo-meta-asm.version}</artifact>
                                            <name>BASE</name>
                                            <metaModelUris>
                                                <param>http://www.eclipse.org/emf/2002/Ecore</param>
                                            </metaModelUris>
                                            <platformAlias>http://blackbelt.hu/judo/asm/base</platformAlias>
                                        </model>
                                        <model>
                                            <artifact>${project.build.directory}/asm.model</artifact>
                                            <name>ASM</name>
                                            <readOnLoad>false</readOnLoad>
                                            <storeOnDisposal>true</storeOnDisposal>
                                            <metaModelUris>
                                                <param>http://www.eclipse.org/emf/2002/Ecore</param>
                                            </metaModelUris>
                                        </model>
                                    </models>
                                    <eolPrograms>
                                        <Etl>
                                            <source>${basedir}/src/transformations/asm/psmToAsm.etl</source>
                                            <parameters>
                                                <parameter>
                                                    <name>modelName</name>
                                                    <value>${test.psm.model.artifactId}</value>
                                                </parameter>
                                                <parameter>
                                                    <name>nsURI</name>
                                                    <value>http://blackbelt.hu/judo/meta/asm</value>
                                                </parameter>
                                                <parameter>
                                                    <name>nsPrefix</name>
                                                    <value>rt</value>
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
