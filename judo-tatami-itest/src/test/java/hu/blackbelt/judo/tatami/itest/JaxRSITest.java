package hu.blackbelt.judo.tatami.itest;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.judo.tatami.core.Dispatcher;
import hu.blackbelt.osgi.utils.osgi.api.BundleTrackerManager;
import org.junit.Before;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.jupiter.api.AfterEach;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Configuration;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.BundleContext;
import org.osgi.framework.ServiceReference;
import org.osgi.service.log.LogService;
import restdto.demo.services.InternationalOrderInfo;

import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileNotFoundException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

import static com.google.common.base.Preconditions.checkArgument;
import static hu.blackbelt.judo.framework.KarafTestUtil.karafConfig;
import static hu.blackbelt.judo.framework.KarafTestUtil.karafStandardRepo;
import static hu.blackbelt.judo.tatami.itest.TatamiTestUtil.*;
import static hu.blackbelt.judo.tatami.itest.TestUtility.assertBundleStarted;
import static hu.blackbelt.judo.tatami.itest.TestUtility.waitWebPage;
import static junit.framework.TestCase.assertNotNull;
import static junit.framework.TestCase.assertTrue;
import static org.ops4j.pax.exam.CoreOptions.mavenBundle;
import static org.ops4j.pax.exam.OptionUtils.combine;
import static org.ops4j.pax.exam.cm.ConfigurationAdminOptions.newConfiguration;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.editConfigurationFilePut;
import static org.ops4j.pax.exam.karaf.options.KarafDistributionOption.features;
import static org.osgi.service.log.LogService.LOG_ERROR;
import static org.osgi.service.log.LogService.LOG_INFO;
import static restdto.demo.services.CategoryInfo.categoryInfoBuilder;
import static restdto.demo.services.InternationalOrderInfo.internationalOrderInfoBuilder;
import static restdto.demo.services.OrderInfo.orderInfoBuilder;
import static restdto.demo.services.OrderItem.orderItemBuilder;
import static restdto.demo.services.ProductInfo.productInfoBuilder;

@Category(JaxRSTestSuite.class)
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class JaxRSITest {
    private static final String BASE_URL = "http://localhost:8181/cxf/demo";

    private static final String DEMO_SERVICE_GET_ALL_INTERNATIONAL_ORDERS = "/demo/services/InternationalOrderInfo/getAllInternationalOrders";
    private static final String DEMO_SERVICE_CREATE_INTERNATIONAL_ORDER = "/demo/services/InternationalOrderInfo/createInternationalOrder";

    private static final String DEMO_SERVICE_DELETE_ORDER_ITEM = "/demo/services/OrderInfo/deleteItem";

    public static final String FEATURE_JUDO_TATAMI_CORE = "judo-tatami-core";

    private Dispatcher dispatcher;
    private Response testResponse;

    @Inject
    LogService log;

    @Inject
    protected BundleTrackerManager bundleTrackerManager;

    @Inject
    BundleContext bundleContext;

    @Inject
    ObjectMapper objectMapper;

    @Configuration
    public Option[] config() throws FileNotFoundException {
        return combine(karafConfig(this.getClass()),

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

                features(blackbeltTatami(), FEATURE_JUDO_TATAMI_CORE),

                newConfiguration("hu.blackbelt.jaxrs.providers.JacksonProvider")
                        .put("JacksonProvider.SerializationFeature.INDENT_OUTPUT", "true")
                        .put("JacksonProvider.SerializationFeature.WRITE_DATES_AS_TIMESTAMPS", "false")
                        .asOption(),

                newConfiguration("hu.blackbelt.jaxrs.providers.ExtendedObjectMapperProvider")
                        .put("placeholder", "true")
                        .asOption(),

                editConfigurationFilePut("etc/org.ops4j.pax.web.cfg",
                        "org.osgi.service.http.port", "8181"),

                /* Added for test purposes only */
                mavenBundle()
                        .groupId("hu.blackbelt.cxf")
                        .artifactId("cxf-jaxrs-application-manager")
                        .versionAsInProject().start(),

                mavenBundle()
                        .groupId("hu.blackbelt.judo.framework")
                        .artifactId("compiler-api")
                        .versionAsInProject().start(),

                mavenBundle()
                        .groupId("hu.blackbelt.judo.tatami")
                        .artifactId("judo-tatami-asm2jaxrsapi")
                        .classifier("test-bundle")
                        .type("jar")
                        .versionAsInProject().start(),

                mavenBundle()
                        .groupId("org.json")
                        .artifactId("json")
                        .versionAsInProject().start(),

                mavenBundle()
                        .groupId("com.fasterxml.jackson.datatype")
                        .artifactId("jackson-datatype-jdk8")
                        .versionAsInProject().start(),

                mavenBundle()
                        .groupId("com.fasterxml.jackson.datatype")
                        .artifactId("jackson-datatype-jsr310")
                        .versionAsInProject().start(),

                mavenBundle()
                        .groupId("org.glassfish")
                        .artifactId("javax.json")
                        .versionAsInProject().start(),

                mavenBundle()
                        .groupId("com.fasterxml.jackson.datatype")
                        .artifactId("jackson-datatype-jsr353")
                        .versionAsInProject().start(),

                mavenBundle()
                        .groupId("com.fasterxml.jackson.module")
                        .artifactId("jackson-module-parameter-names")
                        .versionAsInProject().start(),

                mavenBundle()
                        .groupId("com.fasterxml.jackson.datatype")
                        .artifactId("jackson-datatype-guava")
                        .versionAsInProject().start()

        );
    }

    private Map<String, Object> fillTestMap() {
        //key: path to endpoint method, value: object to return from dispatcher
        Map<String, Object> testMap = new HashMap<>();

        //getAllInternationalOrders (input: none, output: collection<InternationalOrderInfo>), required (InternationalOrderInfo): orderDate
        testMap.put("demo.services.InternationalOrderInfo#getAllInternationalOrders", ImmutableList.of(
                internationalOrderInfoBuilder()
                        .shipperName("shipperNameInInternationalOrderInfo0")
                        .orderDate(ZonedDateTime.now())
                        .items(ImmutableList.of(
                                orderItemBuilder().productName("productName0").quantity(42).discount(2.71).product(
                                        productInfoBuilder().productName("productName").unitPrice(3.14).category(
                                                categoryInfoBuilder().categoryName("categoryNameInProductInfo").build()
                                        ).build()
                                ).category(
                                        categoryInfoBuilder().categoryName("categoryNameInOrderItem").build()
                                ).build(),
                                orderItemBuilder().productName("productName1").quantity(42).discount(2.71).product(
                                        productInfoBuilder().productName("productName").unitPrice(3.14).category(
                                                categoryInfoBuilder().categoryName("categoryNameInProductInfo").build()
                                        ).build()
                                ).category(
                                        categoryInfoBuilder().categoryName("categoryNameInOrderItem").build()
                                ).build()))
                        .build().toMap(),
                internationalOrderInfoBuilder().shipperName("shipperNameInInternationalOrderInfo1").orderDate(ZonedDateTime.now()).build().toMap()));

        //createOrder (input: OrderInfo, output: OrderInfo), required (OrderInfo): orderDate
        testMap.put("demo.services.InternationalOrderInfo#createInternationalOrder", orderInfoBuilder().shipperName("shipperNameInNewInternationalOrderInfo").orderDate(ZonedDateTime.now()).build().toMap());

        return testMap;
    }

    @Before
    public void init() throws Exception {
        class Semaphore {
        }

        Map<String, Object> testMap = fillTestMap();

        ServiceReference reference = bundleContext.getServiceReference(Semaphore.class);
        if (reference == null) {

            assertBundleStarted(bundleContext, "northwind-asm2jaxrsapi");

            dispatcher = new Dispatcher() {
                @Override
                public Map<String, Object> callOperation(String operationFqName, Map<String, Object> payload) {
                    log.log(LOG_INFO, "Dispatcher called - " + operationFqName + " Payload: " + payload.toString());

                    switch (operationFqName) {
                        case "demo.services.InternationalOrderInfo#createInternationalOrder":
                            checkArgument(!((Map) payload.get("input")).isEmpty(), "Payload of create must not be empty");
                            break;
                        case "demo.services.OrderInfo#deleteItem":
                            checkArgument(!((Map) payload.get("input")).isEmpty(), "Payload of delete must not be empty");
                            checkArgument(payload.get("__identifier") instanceof UUID, "Bound operations must have identifier");
                            break;
                    }

                    if (!testMap.containsKey(operationFqName)) {
                        return ImmutableMap.of();
                    } else {
                        return ImmutableMap.of(Dispatcher.OUTPUT_PARAMETER_NAME, testMap.get(operationFqName));
                    }
                }
            };
            bundleContext.registerService(Dispatcher.class, dispatcher, null);

            waitWebPage(BASE_URL + "/?_wadl");
            bundleContext.registerService(Semaphore.class, new Semaphore(), null);
        }
    }

    public WebTarget getWebTarget(String pathToMethod) {
        return ClientBuilder.newClient().register(
                new JacksonJaxbJsonProvider(objectMapper, JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS))
                .target(BASE_URL).path(pathToMethod);
    }

    //GET
    public Response getResponse(String pathToMethod) {
        try {
            testResponse = getWebTarget(pathToMethod)
                    .request("application/json")
                    .header("__identifier", UUID.randomUUID())
                    .get();
        } catch (Exception e) {
            log.log(LOG_ERROR, "EXCEPTION: ", e);
        }
        return testResponse;
    }

    //POST with input
    public Response getResponse(String pathToMethod, Object input) {
        try {
            testResponse = getWebTarget(pathToMethod)
                    .request("application/json")
                    .header("__identifier", UUID.randomUUID())
                    .post(Entity.entity(input, MediaType.APPLICATION_JSON));
        } catch (Exception e) {
            log.log(LOG_ERROR, "EXCEPTION: ", e);
        }
        return testResponse;
    }

    //POST with class type
    public Response getResponseWithEmptyPost(String pathToMethod) {
        try {
            testResponse = getWebTarget(pathToMethod)
                    .request("application/json")
                    .header("__identifier", UUID.randomUUID())
                    .post(Entity.entity("", MediaType.APPLICATION_JSON));
        } catch (Exception e) {
            log.log(LOG_ERROR, "EXCEPTION: ", e);
        }
        return testResponse;
    }

    @AfterEach
    public void clearTestResponse() {
        testResponse.close();
    }

    private void logTest(String path) {
        log.log(LOG_INFO, "==============================================\nTesting " + path + "...\n==============================================");
    }

    @Test
    public void testGetAllInternationalOrders() {
        logTest(DEMO_SERVICE_GET_ALL_INTERNATIONAL_ORDERS);

        Response response = getResponse(DEMO_SERVICE_GET_ALL_INTERNATIONAL_ORDERS);

        assertNotNull(response);
        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));

        List<InternationalOrderInfo> output = response.readEntity(new GenericType<List<InternationalOrderInfo>>() {
        });
        log.log(LOG_INFO, "Response Payload: " + output);

        assertTrue("shipperNameInInternationalOrderInfo0".equals(output.get(0).getShipperName()));
        assertTrue("shipperNameInInternationalOrderInfo1".equals(output.get(1).getShipperName()));
    }

    @Test
    public void testCreateInternationalOrder() {
        logTest(DEMO_SERVICE_CREATE_INTERNATIONAL_ORDER);

        Response response = getResponse(DEMO_SERVICE_CREATE_INTERNATIONAL_ORDER, orderInfoBuilder().orderDate(ZonedDateTime.now()).build());

        assertNotNull(response);
        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));

        InternationalOrderInfo output = response.readEntity(InternationalOrderInfo.class);
        log.log(LOG_INFO, "Response Payload: " + output);

        assertTrue("shipperNameInNewInternationalOrderInfo".equals(output.getShipperName()));
    }

    @Test
    public void testOrderItemDeleteOrder() {
        logTest(DEMO_SERVICE_DELETE_ORDER_ITEM);

        Response response = getResponse(DEMO_SERVICE_DELETE_ORDER_ITEM, orderItemBuilder().__identifier(UUID.randomUUID()).build());

        assertNotNull(response);
        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
    }
}