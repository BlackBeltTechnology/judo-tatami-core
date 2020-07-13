package hu.blackbelt.judo.tatami.itest;

import org.ops4j.pax.exam.options.MavenArtifactUrlReference;

import static org.ops4j.pax.exam.CoreOptions.maven;

public class TatamiTestUtil {
    public static final String FEATURE_CXF_JAXRS = "cxf-jaxrs";
    public static final String FEATURE_CXF_JACKSON = "cxf-jackson";
    public static final String FEATURE_SWAGGER_CORE = "cxf-rs-description-swagger2";

    public static MavenArtifactUrlReference apacheCxf() {
        return maven().groupId("org.apache.cxf.karaf").artifactId("apache-cxf").versionAsInProject().classifier("features").type("xml");
    }

    public static MavenArtifactUrlReference blackbeltOsgiUtils() {
        return maven().groupId("hu.blackbelt.osgi.utils").artifactId("features").versionAsInProject().classifier("features").type("xml");
    }

    public static MavenArtifactUrlReference blackbeltGoogle() {
        return maven().groupId("hu.blackbelt.karaf.features").artifactId("google-features").versionAsInProject().classifier("features").type("xml");
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

    public static MavenArtifactUrlReference blackbeltOpenapiGenerator() {
        return maven().groupId("hu.blackbelt.karaf.features").artifactId("openapi-generator-features").versionAsInProject().classifier("features").type("xml");
    }

    public static MavenArtifactUrlReference blackbeltTatami() {
        return maven().groupId("hu.blackbelt.judo.tatami").artifactId("judo-tatami-karaf-features").versionAsInProject().classifier("features").type("xml");
    }
}