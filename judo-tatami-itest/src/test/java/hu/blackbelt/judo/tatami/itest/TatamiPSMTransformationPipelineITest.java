package hu.blackbelt.judo.tatami.itest;

import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.openapi.API;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel;
import hu.blackbelt.judo.meta.openapi.runtime.exporter.OpenAPIExporter;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.tatami.core.TransformationTrace;
import hu.blackbelt.judo.tatami.core.TransformationTraceService;
import hu.blackbelt.judo.tatami.core.TransformationTraceUtil;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.osgi.utils.osgi.api.BundleTrackerManager;
import org.eclipse.emf.common.util.EList;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;

import javax.inject.Inject;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;

import static hu.blackbelt.judo.framework.KarafTestUtil.*;
import static hu.blackbelt.judo.meta.asm.runtime.AsmModel.SaveArguments.asmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel.SaveArguments.liquibaseSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.measure.runtime.MeasureModel.SaveArguments.measureSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel.SaveArguments.openapiSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.openapi.runtime.exporter.OpenAPIExporter.convertModelToFile;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModel.SaveArguments.psmSaveArgumentsBuilder;
import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel.SaveArguments.rdbmsSaveArgumentsBuilder;
import static hu.blackbelt.judo.tatami.itest.TatamiTestUtil.*;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.systemProperty;
import static org.ops4j.pax.exam.OptionUtils.combine;
import static org.ops4j.pax.exam.cm.ConfigurationAdminOptions.newConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;
import static org.osgi.service.log.LogService.LOG_ERROR;
import static org.osgi.service.log.LogService.LOG_INFO;

public abstract class TatamiPSMTransformationPipelineITest {

    public static final String FRAMEWORK_GROUPID = "hu.blackbelt.judo.framework";

    public static final String FRAMEWORK_COMPILER_API = "compiler-api";

    public static final String BLACKBELT_CXF_GROUPID = "hu.blackbelt.cxf";
    public static final String JAXRS_APPLICATION_MANAGER = "cxf-jaxrs-application-manager";
    public static final String JAXRS_APPLICATION_MANAGER_VERSION = "0.5.0";
    public static final String FEATURE_JUDO_TATAMI_META_ASM = "judo-tatami-meta-asm";
    public static final String FEATURE_JUDO_TATAMI_META_ESM = "judo-tatami-meta-esm";
    public static final String FEATURE_JUDO_TATAMI_META_PSM = "judo-tatami-meta-psm";
    public static final String FEATURE_JUDO_TATAMI_META_JQL = "judo-tatami-meta-jql";
    public static final String FEATURE_JUDO_TATAMI_META_EXPRESSION = "judo-tatami-meta-expression";
    public static final String FEATURE_JUDO_TATAMI_CORE = "judo-tatami-core";
    public static final String FEATURE_JUDO_TATAMI_META_LIQUIBASE = "judo-tatami-meta-liquibase";
    public static final String FEATURE_JUDO_TATAMI_META_RDBMS = "judo-tatami-meta-rdbms";
    public static final String FEATURE_JUDO_TATAMI_META_OPENAPI = "judo-tatami-meta-openapi";
    public static final String FEATURE_JUDO_TATAMI_META_MEASURE = "judo-tatami-meta-measure";
    public static final String FEATURE_JUDO_TATAMI_ESM_2_PSM = "judo-tatami-esm2psm";
    public static final String FEATURE_JUDO_TATAMI_PSM_2_ASM = "judo-tatami-psm2asm";
    public static final String FEATURE_JUDO_TATAMI_PSM_2_MEASURE = "judo-tatami-psm2measure";
    public static final String FEATURE_JUDO_TATAMI_ASM_2_JAXRSAPI = "judo-tatami-asm2jaxrsapi";
    public static final String FEATURE_JUDO_TATAMI_ASM_2_OPENAPI = "judo-tatami-asm2openapi";
    public static final String FEATURE_JUDO_TATAMI_RDBMS_2_LIQUIBASE = "judo-tatami-rdbms2liquibase";
    public static final String FEATURE_JUDO_TATAMI_ASM_2_RDBMS = "judo-tatami-asm2rdbms";

    public static final String FEATURE_JUDO_TATAMI_WORKFLOW = "judo-tatami-workflow";

    @Inject
    LogService log;

    @Inject
    protected BundleTrackerManager bundleTrackerManager;

    @Inject
    protected PsmModel psmModel;

    @Inject
    protected AsmModel asmModel;

    @Inject
    protected RdbmsModel rdbmsModel;

    @Inject
    protected MeasureModel measureModel;

    @Inject
    protected LiquibaseModel liquibaseModel;

    @Inject
    protected OpenapiModel openAPIModel;

    @Inject
    TransformationTraceService transformationTraceService;

    @Inject
    BundleContext bundleContext;

    @Inject
    TransformationContext transformationContext;


    @Configuration
    public Option[] config() throws FileNotFoundException {

        return combine(karafConfig(this.getClass()),

                systemProperty("org.ops4j.pax.exam.raw.extender.intern.Parser.DEFAULT_TIMEOUT").value("60000"),
                systemProperty("pax.exam.service.timeout").value("60000"),

                features(karafStandardRepo()),

                features(blackbeltBouncCastle()),

                features(blackbeltApacheCommons()),

                features(blackbeltApacheHttpClient()),

                features(blackbeltApachePoi()),

                features(blackbeltOsgiUtils()),

                features(blackbeltGoogle()),

                features(blackbeltTinybundles()),

                features(blackbeltEclipseEmf()),

                features(blackbeltAntlr()),

                features(blackbeltEclipseEpsilon()),

                features(blackbeltEpsilonRuntime()),

                features(blackbeltEclipseXtext()),

                features(apacheCxf(), FEATURE_SWAGGER_CORE, FEATURE_CXF_JACKSON, FEATURE_CXF_JAXRS),

                features(blackbeltTatami(), FEATURE_JUDO_TATAMI_META_ASM, FEATURE_JUDO_TATAMI_META_ESM, FEATURE_JUDO_TATAMI_META_PSM, FEATURE_JUDO_TATAMI_META_JQL,
                        FEATURE_JUDO_TATAMI_META_EXPRESSION,
                        FEATURE_JUDO_TATAMI_META_MEASURE, FEATURE_JUDO_TATAMI_META_OPENAPI, FEATURE_JUDO_TATAMI_META_RDBMS, FEATURE_JUDO_TATAMI_META_LIQUIBASE, FEATURE_JUDO_TATAMI_CORE,
                        FEATURE_JUDO_TATAMI_ESM_2_PSM, FEATURE_JUDO_TATAMI_PSM_2_ASM, FEATURE_JUDO_TATAMI_PSM_2_MEASURE, FEATURE_JUDO_TATAMI_ASM_2_JAXRSAPI, FEATURE_JUDO_TATAMI_ASM_2_OPENAPI,
                        FEATURE_JUDO_TATAMI_ASM_2_RDBMS, FEATURE_JUDO_TATAMI_RDBMS_2_LIQUIBASE, FEATURE_JUDO_TATAMI_WORKFLOW),

                /*
                newConfiguration("hu.blackbelt.judo.tatami.asm2jaxrsapi.osgi.Asm2JAXRSAPITransformationAsmModelTracker")
                        .put("placeholder", "true").asOption(),

                newConfiguration("hu.blackbelt.judo.tatami.asm2openapi.osgi.Asm2OpenAPITransformationAsmModelTracker")
                        .put("placeholder", "true").asOption(),

                newConfiguration("hu.blackbelt.judo.tatami.asm2rdbms.osgi.Asm2RdbmsTransformationAsmModelTracker")
                        .put("placeholder", "true").asOption(),

                newConfiguration("hu.blackbelt.judo.tatami.asm2sdk.osgi.AsmModelServiceTracker")
                        .put("placeholder", "true").asOption(),

                newConfiguration("hu.blackbelt.judo.tatami.esm2psm.osgi.Esm2PsmTransformationEsmModelTracker")
                        .put("placeholder", "true").asOption(),

                newConfiguration("hu.blackbelt.judo.tatami.psm2asm.osgi.Psm2AsmTransformationPsmModelTracker")
                        .put("placeholder", "true").asOption(),

                newConfiguration("hu.blackbelt.judo.tatami.psm2measure.osgi.Psm2MeasureTransformationPsmModelTracker")
                        .put("placeholder", "true").asOption(),

                newConfiguration("hu.blackbelt.judo.tatami.rdbms2liquibase.osgi.Rdbms2LiquibaseRdbmsModelTracker")
                        .put("placeholder", "true").asOption(),
                 */


                newConfiguration("hu.blackbelt.judo.tatami.esm2psm.osgi.Esm2PsmTransformationEsmModelTracker")
                        .put("placeholder", "true").asOption(),

                /*
                newConfiguration("hu.blackbelt.judo.tatami.workflow.osgi.PsmWorkflowDefaultPsmModelTracker")
                        .put("placeholder", "true")
                        .put("sqlDialect", "hsqldb")
                        .put("validateModels", "true")
                        .put("saveCompletedModels", "true")
                        .put("saveFailedModels", "true")
                        .put("outputDirectory", new File("target", "exam").getAbsolutePath())
                */
                replaceConfigurationFile("/etc/hu.blackbelt.judo.tatami.workflow.osgi.PsmWorkflowDefaultPsmModelTracker.cfg",
                        getConfigFile(getClass(), "/etc/hu.blackbelt.judo.tatami.workflow.osgi.PsmWorkflowDefaultPsmModelTracker.cfg")),

                editConfigurationFilePut("etc/org.ops4j.pax.web.cfg",
                        "org.osgi.service.http.port", "8181"),

                newConfiguration("hu.blackbelt.jaxrs.providers.JacksonProvider")
                        .put("JacksonProvider.SerializationFeature.INDENT_OUTPUT", "true").asOption(),


                /* Added for test purposes only */
                mavenBundle()
                        .groupId("com.fasterxml.jackson.module")
                        .artifactId("jackson-module-jaxb-annotations")
                        .versionAsInProject().start(),

                mavenBundle()
                        .groupId(BLACKBELT_CXF_GROUPID)
                        .artifactId(JAXRS_APPLICATION_MANAGER)
                        .version(JAXRS_APPLICATION_MANAGER_VERSION).start(),

                mavenBundle()
                        .groupId(FRAMEWORK_GROUPID)
                        .artifactId(FRAMEWORK_COMPILER_API)
                        .versionAsInProject().start(),
                getProvisonModelBundle()
        );

    }

    public abstract Option getProvisonModelBundle() throws FileNotFoundException;

    public abstract String getAppName();

}