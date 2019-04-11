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
import hu.blackbelt.judo.tatami.core.TrackInfo;
import hu.blackbelt.judo.tatami.core.TrackInfoService;
import hu.blackbelt.osgi.utils.osgi.api.BundleTrackerManager;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.ConfigurationManager;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.LogLevelOption;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.options.MavenUrlReference;
import org.osgi.framework.BundleContext;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import javax.inject.Inject;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;
import static org.ops4j.pax.tinybundles.core.TinyBundles.bundle;
import static org.ops4j.pax.tinybundles.core.TinyBundles.withBnd;

@RunWith(PaxExam.class)
@Slf4j
public class TatamiTransformationPipelineITest {
    public static final String JUDO_META_GROUPID = "hu.blackbelt.judo.meta";
    public static final String TATAMI_GROUPID = "hu.blackbelt.judo.tatami";
    public static final String TATAMI_VERSION = "judo.tatami.version";

    public static final String META_ASM = "judo-meta-asm";
    public static final String META_ASM_VERSION = "judo.meta.asm.version";
    public static final String META_RDBMS_RUNTIME = "judo-meta-rdbms-runtime";
    public static final String META_RDBMS_VERSION = "judo.meta.rdbms.version";
    public static final String META_PSM_RUNTIME = "judo-meta-psm-runtime";
    public static final String META_PSM_VERSION = "judo.meta.psm.version";
    public static final String META_PSM_IMPORT_RANGE = "judo.meta.psm.import.range";
    public static final String META_PSM_JQL_RUNTIME = "judo-meta-psm-jql-runtime";
    public static final String META_PSM_JQL_VERSION = "judo.meta.psm.jql.version";
    public static final String META_PSM_JQL_EXTRACT_RUNTIME = "judo-meta-psm-jql-extract-runtime";
    public static final String META_PSM_JQL_EXTRACT_VERSION = "judo.meta.psm.jql.extract.version";
    public static final String META_MEASURE_RUNTIME = "judo-meta-measure-runtime";
    public static final String META_MEASURE_VERSION = "judo.meta.measure.version";
    public static final String META_EXPRESSION_RUNTIME = "judo-meta-expression-runtime";
    public static final String META_EXPRESSION_VERSION = "judo.meta.expression.version";
    public static final String META_LIQUIBASE_RUNTIME = "judo-meta-liquibase-runtime";
    public static final String META_LIQUIBASE_VERSION = "judo.meta.liquibase.version";
    public static final String META_OPENAPI_RUNTIME = "judo-meta-openapi-runtime";
    public static final String META_OPENAPI_VERSION = "judo.meta.openapi.version";

    public static final String TATAMI_CORE = "judo-tatami-core";
    public static final String TATAMI_PSM2ASM = "judo-tatami-psm2asm";
    public static final String TATAMI_PSM2JQL = "judo-tatami-psm2jql";
    public static final String TATAMI_PSM2MEASURE = "judo-tatami-psm2measure";
    public static final String TATAMI_JQL2EXPRESSION = "judo-tatami-jql2expression";
    public static final String TATAMI_ASM2RDBMS = "judo-tatami-asm2rdbms";
    public static final String TATAMI_RDBMS2LIQUIBSE = "judo-tatami-rdbms2liquibase";
    public static final String TATAMI_ASM2OPENAPI = "judo-tatami-asm2openapi";

    public static final String ORG_APACHE_KARAF = "org.apache.karaf";
    public static final String APACHE_KARAF = "apache-karaf";
    public static final String ORG_APACHE_KARAF_FEATURES = "org.apache.karaf.features";
    public static final String STANDARD = "standard";
    public static final String FEATURES = "features";
    public static final String XML = "xml";
    public static final String ZIP = "zip";
    public static final String JUDO_KARAF = "hu.blackbelt.judo.karaf";
    public static final String JUDO_KARAF_RUNTIME_FEATURES = "judo-karaf-runtime-features";
    public static final String FEATURE_SCR = "scr";
    public static final String FEATURE_OSGI_UTILS = "osgi-utils";
    public static final String FEATURE_EPSILON_RUNTIME = "epsilon-runtime";
    public static final String FEATURE_ECLIPSE_XTEXT = "eclipse-xtext";
    public static final String FEATURE_SWAGGER_CORE = "cxf-rs-description-swagger2";
    public static final String TINYBUNDLES_GROUPID = "org.ops4j.pax.tinybundles";
    public static final String TINYBUNDLES = "tinybundles";
    public static final String TINYBUNDLES_VERSION = "3.0.0";
    public static final String BNDLIB_GROUPID = "biz.aQute.bnd";
    public static final String BNDLIB = "biz.aQute.bndlib";
    public static final String BNDLIB_VERSION = "3.5.0";
    public static final String PAX_EXAM_KARAF_VERSION_PROPERTY = "pax.exam.karaf.version";
    public static final String KARAF_VERSION = "4.1.2";
    public static final String JUDO_KARAF_RUNTIME_VERSION_PROPERTY = "judo.karaf.runtime.version";
    public static final String JUDO_KARAF_RUNTIME_VERSION = "1.0.0";


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
    BundleContext context;

    @Inject
    TrackInfoService trackInfoService;


    @Configuration
    public Option[] config() throws FileNotFoundException {
        MavenArtifactUrlReference karafUrl = maven()
                .groupId(ORG_APACHE_KARAF)
                .artifactId(APACHE_KARAF)
                .version(karafVersion())
                .type(ZIP);

        MavenUrlReference karafStandardRepo = maven()
                .groupId(ORG_APACHE_KARAF_FEATURES)
                .artifactId(STANDARD)
                .version(karafVersion())
                .classifier(FEATURES)
                .type(XML);

        MavenUrlReference judoKarafRuntimeRepo = maven()
                .groupId(JUDO_KARAF)
                .artifactId(JUDO_KARAF_RUNTIME_FEATURES)
                .version(judoKarafRuntimeVersion())
                .classifier(FEATURES)
                .type(XML);


        return new Option[] {
                // KarafDistributionOption.debugConfiguration("5005", true),
                karafDistributionConfiguration()
                        .frameworkUrl(karafUrl)
                        .unpackDirectory(new File("target", "exam"))
                        .useDeployFolder(false),
                keepRuntimeFolder(),
                cleanCaches(true),
                logLevel(LogLevelOption.LogLevel.INFO),
                // Debug
                //vmOption("-Xrunjdwp:transport=dt_socket,server=y,suspend=y,address=5005"),
                //systemTimeout(30000),
                //debugConfiguration("5005", true),
                vmOption("-Dfile.encoding=UTF-8"),
                replaceConfigurationFile("etc/org.ops4j.pax.logging.cfg",
                        getConfigFile("/etc/org.ops4j.pax.logging.cfg")),

                configureConsole().ignoreLocalConsole(),

                //features(karafStandardRepo , "scr"),
                features(judoKarafRuntimeRepo , FEATURE_SCR, FEATURE_OSGI_UTILS, FEATURE_EPSILON_RUNTIME, FEATURE_ECLIPSE_XTEXT, FEATURE_SWAGGER_CORE),

                mavenBundle("org.apache.servicemix.bundles", "org.apache.servicemix.bundles.hamcrest", "1.3_1"),

                mavenBundle()
                        .groupId(TINYBUNDLES_GROUPID)
                        .artifactId(TINYBUNDLES)
                        .version(TINYBUNDLES_VERSION).start(),

                mavenBundle()
                        .groupId(BNDLIB_GROUPID)
                        .artifactId(BNDLIB)
                        .version(BNDLIB_VERSION).start(),
                
                mavenBundle()
                        .groupId(JUDO_META_GROUPID)
                        .artifactId(META_ASM)
                        .version(getVersion(META_ASM_VERSION)).start(),

                mavenBundle()
                        .groupId(JUDO_META_GROUPID)
                        .artifactId(META_RDBMS_RUNTIME)
                        .version(getVersion(META_RDBMS_VERSION)).start(),

                mavenBundle()
                        .groupId(JUDO_META_GROUPID)
                        .artifactId(META_PSM_RUNTIME)
                        .version(getVersion(META_PSM_VERSION)).start(),

                mavenBundle()
                        .groupId(JUDO_META_GROUPID)
                        .artifactId(META_PSM_JQL_RUNTIME)
                        .version(getVersion(META_PSM_JQL_VERSION)).start(),

                mavenBundle()
                        .groupId(JUDO_META_GROUPID)
                        .artifactId(META_PSM_JQL_EXTRACT_RUNTIME)
                        .version(getVersion(META_PSM_JQL_EXTRACT_VERSION)).start(),

                mavenBundle()
                        .groupId(JUDO_META_GROUPID)
                        .artifactId(META_MEASURE_RUNTIME)
                        .version(getVersion(META_MEASURE_VERSION)).start(),

                mavenBundle()
                        .groupId(JUDO_META_GROUPID)
                        .artifactId(META_EXPRESSION_RUNTIME)
                        .version(getVersion(META_EXPRESSION_VERSION)).start(),

                mavenBundle()
                        .groupId(JUDO_META_GROUPID)
                        .artifactId(META_LIQUIBASE_RUNTIME)
                        .version(getVersion(META_LIQUIBASE_VERSION)).start(),

                mavenBundle()
                        .groupId(JUDO_META_GROUPID)
                        .artifactId(META_OPENAPI_RUNTIME)
                        .version(getVersion(META_OPENAPI_VERSION)).start(),


                mavenBundle()
                        .groupId(TATAMI_GROUPID)
                        .artifactId(TATAMI_CORE)
                        .version(getVersion(TATAMI_VERSION)).start(),

                mavenBundle()
                        .groupId(TATAMI_GROUPID)
                        .artifactId(TATAMI_PSM2ASM)
                        .version(getVersion(TATAMI_VERSION)).start(),

                mavenBundle()
                        .groupId(TATAMI_GROUPID)
                        .artifactId(TATAMI_PSM2JQL)
                        .version(getVersion(TATAMI_VERSION)).start(),

                mavenBundle()
                        .groupId(TATAMI_GROUPID)
                        .artifactId(TATAMI_PSM2MEASURE)
                        .version(getVersion(TATAMI_VERSION)).start(),

                mavenBundle()
                        .groupId(TATAMI_GROUPID)
                        .artifactId(TATAMI_JQL2EXPRESSION)
                        .version(getVersion(TATAMI_VERSION)).start(),

                mavenBundle()
                        .groupId(TATAMI_GROUPID)
                        .artifactId(TATAMI_ASM2OPENAPI)
                        .version(getVersion(TATAMI_VERSION)).start(),

                mavenBundle()
                        .groupId(TATAMI_GROUPID)
                        .artifactId(TATAMI_ASM2RDBMS)
                        .version(getVersion(TATAMI_VERSION)).start(),

                mavenBundle()
                        .groupId(TATAMI_GROUPID)
                        .artifactId(TATAMI_RDBMS2LIQUIBSE)
                        .version(getVersion(TATAMI_VERSION)).start(),

                provision(
                        testModelBundle()
                )

        };
    }

    public static String karafVersion() {
        ConfigurationManager cm = new ConfigurationManager();
        String karafVersion = cm.getProperty(PAX_EXAM_KARAF_VERSION_PROPERTY, KARAF_VERSION);
        return karafVersion;
    }

    public static String judoKarafRuntimeVersion() {
        ConfigurationManager cm = new ConfigurationManager();
        String judoKarafRuntimeVersion = cm.getProperty(JUDO_KARAF_RUNTIME_VERSION_PROPERTY, JUDO_KARAF_RUNTIME_VERSION);
        return judoKarafRuntimeVersion;
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
        Collection<ServiceReference<TrackInfo>> trackInfos = context.getServiceReferences(TrackInfo.class, null);

        assertThat(trackInfos.stream().map(r -> context.getService(r).getTrackInfoName()).collect(Collectors.toList()),
                containsInAnyOrder("asm2openapi", "asm2rdbms", "psm2measure", "psm2jqlextract", "psm2asm", "jqlextract2expression"));


        // Get Order entity
        Optional<EClass> orderClass = AsmUtils.asStream(asmModel.getResourceSet().getAllContents())
                .filter(e -> e instanceof EClass)
                .map(e -> (EClass) e)
                .filter(e -> AsmUtils.isEntity(e))
                .filter(e -> AsmUtils.getFQName(e).equals("northwind.entities.Order")).findFirst();

        List<EObject> orderRdbmsObjectList = trackInfoService.getDescendantOfInstanceByModelType("Northwind", RdbmsModel.class, orderClass.get());

        assertThat(orderRdbmsObjectList, hasSize(1));
        assertThat(orderRdbmsObjectList.get(0), is(instanceOf(RdbmsTable.class)));
        assertThat(((RdbmsTable) orderRdbmsObjectList.get(0)).getSqlName(), equalTo("T_ENTTS_ORDER"));

    }

    public File getConfigFile(String path) {
        URL res = this.getClass().getResource(path);
        if (res == null) {
            throw new RuntimeException("Config resource " + path + " not found");
        }
        return new File(res.getFile());
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