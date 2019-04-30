package hu.blackbelt.judo.tatami.itest;

import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.options.MavenArtifactUrlReference;

import static org.ops4j.pax.exam.CoreOptions.maven;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;

public class TatamiTestUtil {
    public static final String META_GROUPID = "hu.blackbelt.judo.meta";
    public static final String TATAMI_GROUPID = "hu.blackbelt.judo.tatami";
    public static final String FRAMEWORK_GROUPID = "hu.blackbelt.judo.framework";

    public static final String META_ASM = "judo-meta-asm";
    public static final String META_RDBMS_RUNTIME = "judo-meta-rdbms-runtime";
    public static final String META_PSM_RUNTIME = "judo-meta-psm-runtime";
    public static final String META_PSM_IMPORT_RANGE = "judo.meta.psm.import.range";
    public static final String META_PSM_JQL_RUNTIME = "judo-meta-psm-jql-runtime";
    public static final String META_PSM_JQL_EXTRACT_RUNTIME = "judo-meta-psm-jql-extract-runtime";
    public static final String META_MEASURE_RUNTIME = "judo-meta-measure-runtime";
    public static final String META_EXPRESSION_RUNTIME = "judo-meta-expression-runtime";
    public static final String META_LIQUIBASE_RUNTIME = "judo-meta-liquibase-runtime";
    public static final String META_OPENAPI_RUNTIME = "judo-meta-openapi-runtime";

    public static final String TATAMI_CORE = "judo-tatami-core";
    public static final String TATAMI_PSM2ASM = "judo-tatami-psm2asm";
    public static final String TATAMI_PSM2JQL = "judo-tatami-psm2jql";
    public static final String TATAMI_PSM2MEASURE = "judo-tatami-psm2measure";
    public static final String TATAMI_JQL2EXPRESSION = "judo-tatami-jql2expression";
    public static final String TATAMI_ASM2RDBMS = "judo-tatami-asm2rdbms";
    public static final String TATAMI_RDBMS2LIQUIBSE = "judo-tatami-rdbms2liquibase";
    public static final String TATAMI_ASM2OPENAPI = "judo-tatami-asm2openapi";
    public static final String TATAMI_ASM2JAXRSAPI = "judo-tatami-asm2jaxrsapi";


    public static final String FEATURE_EPSILON_RUNTIME = "epsilon-runtime";
    public static final String FEATURE_ECLIPSE_XTEXT = "eclipse-xtext";
    public static final String FEATURE_OSGI_UTILS = "osgi-utils";
    public static final String FEATURE_GOOGLE_GUAVA = "guava";
    public static final String FEATURE_CXF_JAXRS = "cxf-jaxrs";
    public static final String FEATURE_CXF_JACKSON = "cxf-jackson";
    public static final String FEATURE_SWAGGER_CORE = "cxf-rs-description-swagger2";
    public static final String FEATURE_ANTLR_3 = "antlr3";

    public static MavenArtifactUrlReference apacheCxf() {
        return maven().groupId("org.apache.cxf.karaf").artifactId("apache-cxf").versionAsInProject().classifier("features").type("xml");
    }


    public static MavenArtifactUrlReference blackbeltOsgiUtils() {
        return maven().groupId("hu.blackbelt.osgi.utils").artifactId("features").versionAsInProject().classifier("features").type("xml");
    }

    public static MavenArtifactUrlReference blackbeltGoogle() {
        return maven().groupId("hu.blackbelt.karaf.features").artifactId("google-features").versionAsInProject().classifier("features").type("xml");
    }

    public static MavenArtifactUrlReference blackbeltJavax() {
        return maven().groupId("hu.blackbelt.karaf.features").artifactId("javax-features").versionAsInProject().classifier("features").type("xml");
    }

    public static MavenArtifactUrlReference blackbeltBouncCastle() {
        return maven().groupId("hu.blackbelt.karaf.features").artifactId("bouncycastle-features").versionAsInProject().classifier("features").type("xml");
    }

    public static MavenArtifactUrlReference blackbeltApacheCommons() {
        return maven().groupId("hu.blackbelt.karaf.features").artifactId("apache-commons-features").versionAsInProject().classifier("features").type("xml");
    }

    public static MavenArtifactUrlReference blackbeltApacheHttpClient() {
        return maven().groupId("hu.blackbelt.karaf.features").artifactId("apache-httpclient-features").versionAsInProject().classifier("features").type("xml");
    }

    public static MavenArtifactUrlReference blackbeltApachePoi() {
        return maven().groupId("hu.blackbelt.karaf.features").artifactId("apache-poi-features").versionAsInProject().classifier("features").type("xml");
    }

    public static MavenArtifactUrlReference blackbeltEclipseEmf() {
        return maven().groupId("hu.blackbelt.karaf.features").artifactId("eclipse-emf-features").versionAsInProject().classifier("features").type("xml");
    }

    public static MavenArtifactUrlReference blackbeltEclipseEpsilon() {
        return maven().groupId("hu.blackbelt.karaf.features").artifactId("eclipse-epsilon-features").versionAsInProject().classifier("features").type("xml");
    }

    public static MavenArtifactUrlReference blackbeltEclipseXtext() {
        return maven().groupId("hu.blackbelt.karaf.features").artifactId("eclipse-xtext-features").versionAsInProject().classifier("features").type("xml");
    }

    public static MavenArtifactUrlReference blackbeltTinybundles() {
        return maven().groupId("hu.blackbelt.karaf.features").artifactId("tinybundles-features").versionAsInProject().classifier("features").type("xml");
    }

    public static MavenArtifactUrlReference blackbeltAntlr() {
        return maven().groupId("hu.blackbelt.karaf.features").artifactId("antlr-features").versionAsInProject().classifier("features").type("xml");
    }

    public static MavenArtifactUrlReference blackbeltEpsilonRuntime() {
        return maven().groupId("hu.blackbelt.epsilon").artifactId("features").versionAsInProject().classifier("features").type("xml");
    }

    /*
    public static MavenArtifactUrlReference judoKarafRuntimeRepo() {
        return maven()
                .groupId(JUDO_KARAF_GROUPID)
                .artifactId(JUDO_KARAF_RUNTIME_FEATURES)
                .versionAsInProject()
                .classifier(FEATURES)
                .type(XML);
    } */

    public static Option[] metaBundles() {
        return new Option[]{
                mavenBundle()
                        .groupId(META_GROUPID)
                        .artifactId(META_ASM)
                        .versionAsInProject().start(),

                mavenBundle()
                        .groupId(META_GROUPID)
                        .artifactId(META_RDBMS_RUNTIME)
                        .versionAsInProject().start(),

                mavenBundle()
                        .groupId(META_GROUPID)
                        .artifactId(META_PSM_RUNTIME)
                        .versionAsInProject().start(),

                mavenBundle()
                        .groupId(META_GROUPID)
                        .artifactId(META_PSM_JQL_RUNTIME)
                        .versionAsInProject().start(),

                mavenBundle()
                        .groupId(META_GROUPID)
                        .artifactId(META_PSM_JQL_EXTRACT_RUNTIME)
                        .versionAsInProject().start(),

                mavenBundle()
                        .groupId(META_GROUPID)
                        .artifactId(META_MEASURE_RUNTIME)
                        .versionAsInProject().start(),

                mavenBundle()
                        .groupId(META_GROUPID)
                        .artifactId(META_EXPRESSION_RUNTIME)
                        .versionAsInProject().start(),

                mavenBundle()
                        .groupId(META_GROUPID)
                        .artifactId(META_LIQUIBASE_RUNTIME)
                        .versionAsInProject().start(),

                mavenBundle()
                        .groupId(META_GROUPID)
                        .artifactId(META_OPENAPI_RUNTIME)
                        .versionAsInProject().start(),
        };
    }

    public static Option[] tatamiBundles() {
        return new Option[]{
                mavenBundle()
                        .groupId(TATAMI_GROUPID)
                        .artifactId(TATAMI_CORE)
                        .versionAsInProject().start(),

                mavenBundle()
                        .groupId(TATAMI_GROUPID)
                        .artifactId(TATAMI_PSM2ASM)
                        .versionAsInProject().start(),

                mavenBundle()
                        .groupId(TATAMI_GROUPID)
                        .artifactId(TATAMI_PSM2JQL)
                        .versionAsInProject().start(),

                mavenBundle()
                        .groupId(TATAMI_GROUPID)
                        .artifactId(TATAMI_PSM2MEASURE)
                        .versionAsInProject().start(),

                mavenBundle()
                        .groupId(TATAMI_GROUPID)
                        .artifactId(TATAMI_JQL2EXPRESSION)
                        .versionAsInProject().start(),

                mavenBundle()
                        .groupId(TATAMI_GROUPID)
                        .artifactId(TATAMI_ASM2OPENAPI)
                        .versionAsInProject().start(),

                mavenBundle()
                        .groupId(TATAMI_GROUPID)
                        .artifactId(TATAMI_ASM2JAXRSAPI)
                        .versionAsInProject().start(),

                mavenBundle()
                        .groupId(TATAMI_GROUPID)
                        .artifactId(TATAMI_ASM2RDBMS)
                        .versionAsInProject().start(),

                mavenBundle()
                        .groupId(TATAMI_GROUPID)
                        .artifactId(TATAMI_RDBMS2LIQUIBSE)
                        .versionAsInProject().start(),


        };
    }
}