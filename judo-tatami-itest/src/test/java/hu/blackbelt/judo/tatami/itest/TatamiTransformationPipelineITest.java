package hu.blackbelt.judo.tatami.itest;

import static org.ops4j.pax.exam.CoreOptions.*;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;
import static org.ops4j.pax.tinybundles.core.TinyBundles.*;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.net.URL;

import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.osgi.utils.osgi.api.BundleTrackerManager;
import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.ConfigurationManager;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.karaf.options.LogLevelOption;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.options.MavenUrlReference;
import org.osgi.framework.Constants;

import javax.inject.Inject;

@RunWith(PaxExam.class)
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


    @Inject
    protected BundleTrackerManager bundleTrackerManager;

    @Inject
    protected PsmModel psmModel;

    @Configuration
    public Option[] config() throws FileNotFoundException {
        MavenArtifactUrlReference karafUrl = maven()
                .groupId("org.apache.karaf")
                .artifactId("apache-karaf")
                .version(karafVersion())
                .type("zip");

        MavenUrlReference karafStandardRepo = maven()
                .groupId("org.apache.karaf.features")
                .artifactId("standard")
                .version(karafVersion())
                .classifier("features")
                .type("xml");

        MavenUrlReference judoKarafRuntimeRepo = maven()
                .groupId("hu.blackbelt.judo.karaf")
                .artifactId("judo-karaf-runtime-features")
                .version(judoKarafRuntimeVersion())
                .classifier("features")
                .type("xml");


        return new Option[] {
                // KarafDistributionOption.debugConfiguration("5005", true),
                karafDistributionConfiguration()
                        .frameworkUrl(karafUrl)
                        .unpackDirectory(new File("target", "exam"))
                        .useDeployFolder(false),
                keepRuntimeFolder(),
                logLevel(LogLevelOption.LogLevel.DEBUG),
                replaceConfigurationFile("etc/org.ops4j.pax.logging.cfg",
                        getConfigFile("/etc/org.ops4j.pax.logging.cfg")),
                configureConsole().ignoreLocalConsole(),
                //features(karafStandardRepo , "scr"),
                features(judoKarafRuntimeRepo , "scr", "osgi-utils", "epsilon-runtime", "eclipse-xtext"),
                mavenBundle()
                        .groupId("org.ops4j.pax.tinybundles")
                        .artifactId("tinybundles")
                        .version("3.0.0").start(),

                mavenBundle()
                        .groupId("biz.aQute.bnd")
                        .artifactId("biz.aQute.bndlib")
                        .version("3.5.0").start(),
                
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
        String karafVersion = cm.getProperty("pax.exam.karaf.version", "4.1.2");
        return karafVersion;
    }

    public static String judoKarafRuntimeVersion() {
        ConfigurationManager cm = new ConfigurationManager();
        String judoKarafRuntimeVersion = cm.getProperty("judo.karaf.runtime.version", "1.0.0");
        return judoKarafRuntimeVersion;
    }

    public static String getVersion(String key) {
        ConfigurationManager cm = new ConfigurationManager();
        String version = cm.getProperty(key, "1.0.0");
        return version;
    }


    public InputStream testModelBundle() throws FileNotFoundException {
        /*
            <instructions>
                <Export-Package>meta/asm;version=${project.version}</Export-Package>
                <Import-Package>
                    meta/psm;version="${judo-meta-psm.import-range}"
                </Import-Package>
                <Include-Resource>
                    {maven-resources},
                    model/northwind.judo-meta-psm=${basedir}/../model/model/northwind-judopsm.model,
                    model/RDBMS Sql Name Mapping.xlsx=${basedir}/../model/model/RDBMS Sql Name Mapping.xlsx
                </Include-Resource>
                <Psm-Models>
                    file=model/northwind.judo-meta-psm;version=${parsedVersion.majorVersion}.${parsedVersion.minorVersion}.${parsedVersion.incrementalVersion};name=Northwind;checksum=${northwind_md5};meta-version="${judo-meta-psm.import-range}"
                </Psm-Models>
            </instructions>
         */

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
    public void testMethod() {
        Assert.assertEquals(3, 3);
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