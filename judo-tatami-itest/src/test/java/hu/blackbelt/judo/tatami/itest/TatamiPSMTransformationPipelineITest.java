package hu.blackbelt.judo.tatami.itest;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.google.common.collect.ImmutableMap;
import edu.uoc.som.openapi.API;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmUtils;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.openapi.runtime.OpenAPIModel;
import hu.blackbelt.judo.meta.openapi.runtime.exporter.OpenAPIExporter;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.rdbms.RdbmsIdentifierField;
import hu.blackbelt.judo.meta.rdbms.RdbmsTable;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.tatami.core.Dispatcher;
import hu.blackbelt.judo.tatami.core.TransformationTrace;
import hu.blackbelt.judo.tatami.core.TransformationTraceService;
import hu.blackbelt.osgi.utils.osgi.api.BundleTrackerManager;
import io.swagger.models.Swagger;
import io.swagger.util.Json;
import io.swagger.util.Yaml;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import sdk.demo.service.OrderInfo;

import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static hu.blackbelt.judo.framework.KarafTestUtil.karafConfig;
import static hu.blackbelt.judo.framework.KarafTestUtil.karafStandardRepo;
import static hu.blackbelt.judo.meta.asm.runtime.AsmModelLoader.getAsmDefaultSaveOptions;
import static hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModelLoader.getLiquibaseDefaultSaveOptions;
import static hu.blackbelt.judo.meta.measure.runtime.MeasureModelLoader.getMeasureModelDefaultSaveOptions;
import static hu.blackbelt.judo.meta.openapi.runtime.OpenAPIModelLoader.getOpenAPIModelDefaultSaveOptions;
import static hu.blackbelt.judo.meta.psm.runtime.PsmModelLoader.getPsmDefaultSaveOptions;
import static hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModelLoader.getRdbmsModelDefaultSaveOptions;
import static hu.blackbelt.judo.tatami.itest.TatamiTestUtil.*;
import static hu.blackbelt.judo.tatami.itest.TestUtility.*;
import static junit.framework.TestCase.assertNotNull;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.provision;
import static org.ops4j.pax.exam.OptionUtils.combine;
import static org.ops4j.pax.exam.cm.ConfigurationAdminOptions.newConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFilePut;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.ops4j.pax.tinybundles.core.TinyBundles.bundle;
import static org.ops4j.pax.tinybundles.core.TinyBundles.withBnd;
import static org.osgi.service.log.LogService.LOG_ERROR;
import static org.osgi.service.log.LogService.LOG_INFO;

@Category(PSMTestSuite.class)
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class TatamiPSMTransformationPipelineITest {

    public static final String FRAMEWORK_GROUPID = "hu.blackbelt.judo.framework";

    public static final String META_PSM_IMPORT_RANGE = "judo.meta.psm.import.range";
    public static final String FRAMEWORK_COMPILER_API = "compiler-api";

    public static final String FEATURE_SCR = "scr";
    public static final String FEATURE_TINYBUNDLES = "tinybundles";


    public static final String BLACKBELT_CXF_GROUPID = "hu.blackbelt.cxf";
    public static final String JAXRS_APPLICATION_MANAGER = "cxf-jaxrs-application-manager";
    public static final String JAXRS_APPLICATION_MANAGER_VERSION = "0.4.0";
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


    public static final String BASE_URL = "http://localhost:8181/cxf/demo/internalAP";
    public static final String DEMO_ENTITIES_ORDER = "demo.entities.Order";
    public static final String DEMO = "demo";
    public static final String DEMO_SERVICE_GET_ALL_ORDERS = "/demo/service/getAllOrders";

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
    protected OpenAPIModel openAPIModel;

    @Inject
    TransformationTraceService transformationTraceService;

    @Inject
    BundleContext bundleContext;


    @Configuration
    public Option[] config() throws FileNotFoundException {

        return combine(karafConfig(this.getClass()),

                features(karafStandardRepo()),

                features(blackbeltJavax()),

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

                features(apacheCxf(), FEATURE_SWAGGER_CORE, FEATURE_CXF_JACKSON, FEATURE_CXF_JAXRS),

                features(blackbeltTatami(), FEATURE_JUDO_TATAMI_META_ASM, FEATURE_JUDO_TATAMI_META_ESM, FEATURE_JUDO_TATAMI_META_PSM, FEATURE_JUDO_TATAMI_META_JQL,
                        FEATURE_JUDO_TATAMI_META_EXPRESSION,
                        FEATURE_JUDO_TATAMI_META_MEASURE, FEATURE_JUDO_TATAMI_META_OPENAPI, FEATURE_JUDO_TATAMI_META_RDBMS, FEATURE_JUDO_TATAMI_META_LIQUIBASE, FEATURE_JUDO_TATAMI_CORE,
                        FEATURE_JUDO_TATAMI_ESM_2_PSM, FEATURE_JUDO_TATAMI_PSM_2_ASM, FEATURE_JUDO_TATAMI_PSM_2_MEASURE, FEATURE_JUDO_TATAMI_ASM_2_JAXRSAPI, FEATURE_JUDO_TATAMI_ASM_2_OPENAPI,
                        FEATURE_JUDO_TATAMI_ASM_2_RDBMS, FEATURE_JUDO_TATAMI_RDBMS_2_LIQUIBASE),

                newConfiguration("hu.blackbelt.jaxrs.providers.JacksonProvider")
                        .put("JacksonProvider.SerializationFeature.INDENT_OUTPUT", "true").asOption(),

                editConfigurationFilePut("etc/org.ops4j.pax.web.cfg",
                        "org.osgi.service.http.port", "8181"),

                /* Added for test purposes only */
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

    public Option getProvisonModelBundle() throws FileNotFoundException {
        return provision(
                testPsmModelBundle()
        );
    }

    public InputStream testPsmModelBundle() throws FileNotFoundException {
        return bundle()
                .add( "model/" + DEMO + ".judo-meta-psm",
                        new FileInputStream(new File(testTargetDir(getClass()).getAbsolutePath(),  "northwind-judopsm.model")))
                .set( Constants.BUNDLE_MANIFESTVERSION, "2")
                .set( Constants.BUNDLE_SYMBOLICNAME,  DEMO + "-model" )
                //set( Constants.IMPORT_PACKAGE, "meta/psm;version=\"" + getConfiguration(META_PSM_IMPORT_RANGE) +"\"")
                .set( "Psm-Models", "file=model/" + DEMO + ".judo-meta-psm;version=1.0.0;name=" + DEMO + ";checksum=notset;meta-version-range=\"[1.0.0,2)\"")
                .build( withBnd());
    }


    @Test
    public void saveModels() throws InvalidSyntaxException, IOException {

        asmModel.getResourceSet().getResource(asmModel.getUri(), false)
                .save(new FileOutputStream(new File("itest-" + DEMO + "-asm.model")), getAsmDefaultSaveOptions());

        psmModel.getResourceSet().getResource(psmModel.getUri(), false)
                .save(new FileOutputStream(new File("itest-" + DEMO + "-psm.model")), getPsmDefaultSaveOptions());

        rdbmsModel.getResourceSet().getResource(rdbmsModel.getUri(), false)
                .save(new FileOutputStream(new File("itest-" + DEMO + "-rdbms.model")), getRdbmsModelDefaultSaveOptions());

        measureModel.getResourceSet().getResource(measureModel.getUri(), false)
                .save(new FileOutputStream(new File("itest-" + DEMO + "-measure.model")), getMeasureModelDefaultSaveOptions());

        liquibaseModel.getResourceSet().getResource(liquibaseModel.getUri(), false)
                .save(new FileOutputStream(new File("itest-" + DEMO + "-liquibase.model")), getLiquibaseDefaultSaveOptions());

        openAPIModel.getResourceSet().getResource(openAPIModel.getUri(), false)
                .save(new FileOutputStream(new File("itest-" + DEMO + "-openapi.model")), getOpenAPIModelDefaultSaveOptions());

        final Swagger swagger = OpenAPIExporter.convertModelToOpenAPI((API) openAPIModel.getResourceSet().getResource(openAPIModel.getUri(), false).getContents().get(0));
        try (final Writer targetFileWriter = new FileWriter(new File("itest-" + DEMO + "-openapi.json"))) {
            final String json = Json.pretty().writeValueAsString(swagger);
            targetFileWriter.append(json);
        } catch (IOException ex) {
            log.log(LOG_ERROR, "Unable to create JSON output", ex);
        }
        try (final Writer targetFileWriter = new FileWriter(new File("itest-" + DEMO + "-openapi.yaml"))) {
            final String yaml = Yaml.pretty().writeValueAsString(swagger);
            targetFileWriter.append(yaml);
        } catch (IOException ex) {
            log.log(LOG_ERROR, "Unable to create YAML output", ex);
        }
    }


    @Test
    public void testTrace() throws InvalidSyntaxException {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "== RUNNING TEST TRACE METHOD");
        log.log(LOG_INFO, "==============================================");

        Collection<ServiceReference<TransformationTrace>> transformationTraces = bundleContext.getServiceReferences(TransformationTrace.class, null);

        assertThat(transformationTraces.stream().map(r -> bundleContext.getService(r).getTransformationTraceName()).collect(Collectors.toList()),
                containsInAnyOrder( "asm2openapi", "asm2rdbms", "psm2measure", "psm2asm"));


        AsmUtils asmUtils = new AsmUtils(asmModel.getResourceSet());
        // Get Order entity
        Optional<EClass> orderClass = asmUtils.getClassByFQName(DEMO_ENTITIES_ORDER);

        List<EObject> orderRdbmsObjectList = transformationTraceService.getDescendantOfInstanceByModelType(DEMO, RdbmsModel.class, orderClass.get());

        assertThat(orderRdbmsObjectList, hasSize(2));
        assertThat(orderRdbmsObjectList, hasItems(instanceOf(RdbmsTable.class), instanceOf(RdbmsIdentifierField.class) ));
        assertThat(orderRdbmsObjectList.stream()
                .filter(RdbmsTable.class::isInstance)
                .map(RdbmsTable.class::cast)
                .findFirst().get().getSqlName(), equalTo("T_ENTTS_ORDER"));

        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "== STOPPING TEST TRACE METHOD");
        log.log(LOG_INFO, "==============================================");

    }

    @Test
    public void testRest() throws Exception {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "== RUNNING TEST REST METHOD");
        log.log(LOG_INFO, "==============================================");

        Dispatcher dispatcher = new Dispatcher() {
            @Override
            public Map<String, Object> callOperation(String target, String operation, Map<String, Object> payload) {
                log.log(LOG_INFO, "Dispatcher called - " + target + " " + operation + " Payload: " + payload.toString());
                return ImmutableMap.<String, Object>of();
            }
        };
        bundleContext.registerService(Dispatcher.class, dispatcher, null);

        waitWebPage(BASE_URL +"/?_wadl");

        WebTarget wt = ClientBuilder.newClient().register(new JacksonJaxbJsonProvider()).target(BASE_URL);

        assertBundleStarted(bundleContext,  DEMO + "-asm2jaxrsapi");

        Response response = null;
        try {
            response = wt.path(DEMO_SERVICE_GET_ALL_ORDERS)
                    .request("application/json")
                    .get();
                    //.post(null, OrderInfo.class);
        } catch (Exception e) {
            log.log(LOG_ERROR, "EXCEPTION: ", e);
        }
        assertNotNull(response);

        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "== STOPPING TEST REST METHOD");
        log.log(LOG_INFO, "==============================================");
    }


}