package hu.blackbelt.judo.tatami.itest;

import org.ops4j.pax.exam.Option;

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