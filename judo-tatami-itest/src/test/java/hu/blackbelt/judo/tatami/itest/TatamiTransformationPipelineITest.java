package hu.blackbelt.judo.tatami.itest;

import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.*;

import java.io.File;
import java.net.URL;

import org.junit.Assert;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.ConfigurationManager;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;
import org.ops4j.pax.exam.options.MavenUrlReference;

@RunWith(PaxExam.class)
//@Slf4j
public class TatamiTransformationPipelineITest {

//    @Inject
//    protected Calculator calculator;

    @Configuration
    public Option[] config() {
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
                replaceConfigurationFile("etc/org.ops4j.pax.logging.cfg",
                        getConfigFile("/etc/org.ops4j.pax.logging.cfg")),
                configureConsole().ignoreLocalConsole(),
                //features(karafStandardRepo , "scr"),
                features(judoKarafRuntimeRepo , "scr", "osgi-utils", "epsilon-runtime", "eclipse-xtext"),

                mavenBundle()
                        .groupId("hu.blackbelt.judo.meta")
                        .artifactId("judo-meta-asm")
                        .version(getVersion("judo.meta.asm.version")).start(),

                mavenBundle()
                        .groupId("hu.blackbelt.judo.meta")
                        .artifactId("judo-meta-rdbms-runtime")
                        .version(getVersion("judo.meta.rdbms.version")).start(),

                mavenBundle()
                        .groupId("hu.blackbelt.judo.meta")
                        .artifactId("judo-meta-psm-runtime")
                        .version(getVersion("judo.meta.psm.version")).start(),

                mavenBundle()
                        .groupId("hu.blackbelt.judo.meta")
                        .artifactId("judo-meta-psm-jql-runtime")
                        .version(getVersion("judo.meta.psm.jql.version")).start(),

                /*
                mavenBundle()
                        .groupId("hu.blackbelt.judo.meta")
                        .artifactId("judo-meta-psm-jql-extract-runtime")
                        .version(getVersion("judo.meta.psm.jql.extract.version")).start(),
                */
                mavenBundle()
                        .groupId("hu.blackbelt.judo.meta")
                        .artifactId("judo-meta-measure-runtime")
                        .version(getVersion("judo.meta.measure.version")).start(),

                mavenBundle()
                        .groupId("hu.blackbelt.judo.meta")
                        .artifactId("judo-meta-expression-runtime")
                        .version(getVersion("judo.meta.expression.version")).start(),

                mavenBundle()
                        .groupId("hu.blackbelt.judo.meta")
                        .artifactId("judo-meta-liquibase-runtime")
                        .version(getVersion("judo.meta.liquibase.version")).start(),

                /*
                mavenBundle()
                        .groupId("hu.blackbelt.judo.meta")
                        .artifactId("judo-meta-openapi-runtime")
                        .version(getVersion("judo.meta.openapi.version")).start(),
                */
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
    /*
    @Test
    public void testAdd() {
        int result = calculator.add(1, 2);
        LOG.info("Result of add was {}", result);
        Assert.assertEquals(3, result);
    }
    */

}