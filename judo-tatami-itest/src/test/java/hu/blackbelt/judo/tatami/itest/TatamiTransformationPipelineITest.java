package hu.blackbelt.judo.tatami.itest;

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
import hu.blackbelt.osgi.utils.osgi.api.BundleTrackerManager;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.ConfigurationManager;
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
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static hu.blackbelt.judo.framework.KarafTestUtil.judoKarafRuntimeRepo;
import static hu.blackbelt.judo.framework.KarafTestUtil.karafConfig;
import static hu.blackbelt.judo.tatami.itest.TatamiTestUtil.metaBundles;
import static hu.blackbelt.judo.tatami.itest.TatamiTestUtil.tatamiBundles;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.CoreOptions.provision;
import static org.ops4j.pax.exam.OptionUtils.combine;
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
    public static final String FEATURE_OSGI_UTILS = "osgi-utils";
    public static final String FEATURE_EPSILON_RUNTIME = "epsilon-runtime";
    public static final String FEATURE_ECLIPSE_XTEXT = "eclipse-xtext";
    public static final String FEATURE_SWAGGER_CORE = "cxf-rs-description-swagger2";
    public static final String FEATURE_TINYBUNDLES = "tinybundles";


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

        return combine(combine(karafConfig(this.getClass()), combine(metaBundles(), tatamiBundles())),

                features(judoKarafRuntimeRepo() , FEATURE_SCR, FEATURE_OSGI_UTILS, FEATURE_EPSILON_RUNTIME,
                        FEATURE_ECLIPSE_XTEXT, FEATURE_SWAGGER_CORE, FEATURE_TINYBUNDLES, "cxf-jaxrs", "cxf-jackson"),

                mavenBundle()
                        .groupId("hu.blackbelt.cxf")
                        .artifactId("cxf-jaxrs-application-manager")
                        .version("0.4.0").start(),

                mavenBundle()
                        .groupId(FRAMEWORK_GROUPID)
                        .artifactId(FRAMEWORK_COMPILER_API)
                        .versionAsInProject().start(),

                provision(
                        testModelBundle()
                )
        );

    }

    public static String getVersion(String key) {
        ConfigurationManager cm = new ConfigurationManager();
        String version = cm.getProperty(key, "1.0.0");
        return version;
    }


    public InputStream testModelBundle() throws FileNotFoundException {
        return bundle()
                .add( "model/northwind.judo-meta-psm",
                        new FileInputStream(new File(testTargetDir().getAbsolutePath(), "northwind-judopsm.model")))
                .set( Constants.BUNDLE_MANIFESTVERSION, "2")
                .set( Constants.BUNDLE_SYMBOLICNAME, "northwind-model" )
                //set( Constants.IMPORT_PACKAGE, "meta/psm;version=\"" + getVersion(META_PSM_IMPORT_RANGE) +"\"")
                .set( "Psm-Models", "file=model/northwind.judo-meta-psm;version=1.0.0;name=Northwind;checksum=notset;meta-version-range=\"" + getVersion(META_PSM_IMPORT_RANGE) + "\"")
                .build( withBnd());
    }

    @Test
    public void testMethod() throws InvalidSyntaxException {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "== RUNNING TEST METHOD");
        log.log(LOG_INFO, "==============================================");

        /*
        TransformationTraceService transformationTraceService = getOsgiService(bundleContext, TransformationTraceService.class, 30000);
        AsmModel asmModel = getOsgiService(bundleContext, AsmModel.class, 30000);
        RdbmsModel rdbmsModel = getOsgiService(bundleContext, RdbmsModel.class, 30000);
        */

        Collection<ServiceReference<TransformationTrace>> transformationTraces = bundleContext.getServiceReferences(TransformationTrace.class, null);

        assertThat(transformationTraces.stream().map(r -> bundleContext.getService(r).getTransformationTraceName()).collect(Collectors.toList()),
                containsInAnyOrder("asm2openapi", "asm2rdbms", "psm2measure", "psm2jqlextract", "psm2asm", "jqlextract2expression"));


        // Get Order entity
        Optional<EClass> orderClass = AsmUtils.asStream(asmModel.getResourceSet().getAllContents())
                .filter(e -> e instanceof EClass)
                .map(e -> (EClass) e)
                .filter(e -> AsmUtils.isEntity(e))
                .filter(e -> AsmUtils.getFQName(e).equals("northwind.entities.Order")).findFirst();

        List<EObject> orderRdbmsObjectList = transformationTraceService.getDescendantOfInstanceByModelType("Northwind", RdbmsModel.class, orderClass.get());

        assertThat(orderRdbmsObjectList, hasSize(1));
        assertThat(orderRdbmsObjectList.get(0), is(instanceOf(RdbmsTable.class)));
        assertThat(((RdbmsTable) orderRdbmsObjectList.get(0)).getSqlName(), equalTo("T_ENTTS_ORDER"));

        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "== STOPPING TEST METHOD");
        log.log(LOG_INFO, "==============================================");

    }

    public  File testTargetDir(){
        String relPath = getClass().getProtectionDomain().getCodeSource().getLocation().getFile();
        File targetDir = new File(relPath);
        if(!targetDir.exists()) {
            targetDir.mkdir();
        }
        return targetDir;
    }
}