package hu.blackbelt.judo.tatami.itest;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.asm.runtime.AsmUtils;
import hu.blackbelt.judo.meta.expression.runtime.ExpressionModel;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.openapi.runtime.OpenAPIModel;
import hu.blackbelt.judo.meta.psm.jql.extract.runtime.PsmJqlExtractModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.rdbms.RdbmsTable;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.tatami.core.TransformationTrace;
import hu.blackbelt.judo.tatami.core.TransformationTraceService;
import hu.blackbelt.judo.tatami.itest.restbeans.OrderInfo;
import hu.blackbelt.osgi.utils.osgi.api.BundleTrackerManager;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
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

import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static hu.blackbelt.judo.framework.KarafTestUtil.karafConfig;
import static hu.blackbelt.judo.framework.KarafTestUtil.karafStandardRepo;
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
import static org.osgi.service.log.LogService.LOG_INFO;

@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class TatamiTransformationPipelineITest {

    public static final String FRAMEWORK_GROUPID = "hu.blackbelt.judo.framework";

    public static final String META_PSM_IMPORT_RANGE = "judo.meta.psm.import.range";
    public static final String FRAMEWORK_COMPILER_API = "compiler-api";

    public static final String FEATURE_SCR = "scr";
    public static final String FEATURE_TINYBUNDLES = "tinybundles";


    private static final String BASE_URL = "http://localhost:8181/cxf/northwind/internalAP";
    public static final String BLACKBELT_CXF_GROUPID = "hu.blackbelt.cxf";
    public static final String JAXRS_APPLICATION_MANAGER = "cxf-jaxrs-application-manager";
    public static final String JAXRS_APPLICATION_MANAGER_VERSION = "0.4.0";
    public static final String FEATURE_JUDO_TATAMI_META_ASM = "judo-tatami-meta-asm";
    public static final String FEATURE_JUDO_TATAMI_META_PSM = "judo-tatami-meta-psm";
    public static final String FEATURE_JUDO_TATAMI_META_JQL = "judo-tatami-meta-jql";
    public static final String FEATURE_JUDO_TATAMI_META_JQL_EXTRACT = "judo-tatami-meta-jql-extract";
    public static final String FEATURE_JUDO_TATAMI_META_EXPRESSION = "judo-tatami-meta-expression";
    public static final String FEATURE_JUDO_TATAMI_CORE = "judo-tatami-core";
    public static final String FEATURE_JUDO_TATAMI_META_LIQUIBASE = "judo-tatami-meta-liquibase";
    public static final String FEATURE_JUDO_TATAMI_META_RDBMS = "judo-tatami-meta-rdbms";
    public static final String FEATURE_JUDO_TATAMI_META_OPENAPI = "judo-tatami-meta-openapi";
    public static final String FEATURE_JUDO_TATAMI_META_MEASURE = "judo-tatami-meta-measure";
    public static final String FEATURE_JUDO_TATAMI_PSM_2_ASM = "judo-tatami-psm2asm";
    public static final String FEATURE_JUDO_TATAMI_PSM_2_JQL = "judo-tatami-psm2jql";
    public static final String FEATURE_JUDO_TATAMI_PSM_2_MEASURE = "judo-tatami-psm2measure";
    public static final String FEATURE_JUDO_TATAMI_ASM_2_JAXRSAPI = "judo-tatami-asm2jaxrsapi";
    public static final String FEATURE_JUDO_TATAMI_ASM_2_OPENAPI = "judo-tatami-asm2openapi";
    public static final String FEATURE_JUDO_TATAMI_RDBMS_2_LIQUIBASE = "judo-tatami-rdbms2liquibase";
    public static final String FEATURE_JUDO_TATAMI_JQL_2_EXPRESSION = "judo-tatami-jql2expression";
    public static final String FEATURE_JUDO_TATAMI_ASM_2_RDBMS = "judo-tatami-asm2rdbms";

    @Inject
    LogService log;

    @Inject
    protected BundleTrackerManager bundleTrackerManager;

    @Inject
    protected PsmModel psmModel;

    @Inject
    protected AsmModel asmModel;

    @Inject
    protected PsmJqlExtractModel psmJqlExtractModel;

    @Inject
    protected RdbmsModel rdbmsModel;

    @Inject
    protected MeasureModel measureModel;

    @Inject
    protected LiquibaseModel liquibaseModel;

    @Inject
    protected ExpressionModel expressionModel;

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

                features(apacheCxf(), FEATURE_CXF_JAXRS),

                features(blackbeltTatami(), FEATURE_JUDO_TATAMI_META_ASM, FEATURE_JUDO_TATAMI_META_PSM, FEATURE_JUDO_TATAMI_META_JQL, FEATURE_JUDO_TATAMI_META_JQL_EXTRACT, FEATURE_JUDO_TATAMI_META_EXPRESSION,
                        FEATURE_JUDO_TATAMI_META_MEASURE, FEATURE_JUDO_TATAMI_META_OPENAPI, FEATURE_JUDO_TATAMI_META_RDBMS, FEATURE_JUDO_TATAMI_META_LIQUIBASE, FEATURE_JUDO_TATAMI_CORE,
                        FEATURE_JUDO_TATAMI_PSM_2_ASM, FEATURE_JUDO_TATAMI_PSM_2_JQL, FEATURE_JUDO_TATAMI_PSM_2_MEASURE, FEATURE_JUDO_TATAMI_ASM_2_JAXRSAPI, FEATURE_JUDO_TATAMI_ASM_2_OPENAPI,
                        FEATURE_JUDO_TATAMI_ASM_2_RDBMS, FEATURE_JUDO_TATAMI_JQL_2_EXPRESSION, FEATURE_JUDO_TATAMI_RDBMS_2_LIQUIBASE),


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

                provision(
                        testModelBundle()
                )
        );

    }

    public InputStream testModelBundle() throws FileNotFoundException {
        return bundle()
                .add( "model/northwind.judo-meta-psm",
                        new FileInputStream(new File(testTargetDir(getClass()).getAbsolutePath(), "northwind-judopsm.model")))
                .set( Constants.BUNDLE_MANIFESTVERSION, "2")
                .set( Constants.BUNDLE_SYMBOLICNAME, "northwind-model" )
                //set( Constants.IMPORT_PACKAGE, "meta/psm;version=\"" + getConfiguration(META_PSM_IMPORT_RANGE) +"\"")
                .set( "Psm-Models", "file=model/northwind.judo-meta-psm;version=1.0.0;name=Northwind;checksum=notset;meta-version-range=\"[1.0.0,2)\"")
                .build( withBnd());
    }

    @Test
    public void testTrace() throws InvalidSyntaxException {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "== RUNNING TEST TRACE METHOD");
        log.log(LOG_INFO, "==============================================");

        Collection<ServiceReference<TransformationTrace>> transformationTraces = bundleContext.getServiceReferences(TransformationTrace.class, null);

        assertThat(transformationTraces.stream().map(r -> bundleContext.getService(r).getTransformationTraceName()).collect(Collectors.toList()),
                containsInAnyOrder("asm2openapi", "asm2rdbms", "psm2measure", "psm2jqlextract", "psm2asm", "jqlextract2expression"));


        AsmUtils asmUtils = new AsmUtils(asmModel.getResourceSet());
        // Get Order entity
        Optional<EClass> orderClass = asmUtils.getClassByFQName("northwind.entities.Order");

        List<EObject> orderRdbmsObjectList = transformationTraceService.getDescendantOfInstanceByModelType("Northwind", RdbmsModel.class, orderClass.get());

        assertThat(orderRdbmsObjectList, hasSize(1));
        assertThat(orderRdbmsObjectList.get(0), is(instanceOf(RdbmsTable.class)));
        assertThat(((RdbmsTable) orderRdbmsObjectList.get(0)).getSqlName(), equalTo("T_ENTTS_ORDER"));

        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "== STOPPING TEST TRACE METHOD");
        log.log(LOG_INFO, "==============================================");

    }

    @Test
    public void testRest() throws Exception {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "== RUNNING TEST REST METHOD");
        log.log(LOG_INFO, "==============================================");

        waitWebPage(BASE_URL +"/?_wadl");

        WebTarget wt = ClientBuilder.newClient().register(new JacksonJaxbJsonProvider()).target(BASE_URL);

        assertBundleStarted(bundleContext, "Northwind-asm2jaxrsapi");

        OrderInfo orderInfo = wt.path("/getAllOrders").request("application/json").get(OrderInfo.class);
        assertNotNull(orderInfo);


        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "== STOPPING TEST REST METHOD");
        log.log(LOG_INFO, "==============================================");
    }


}