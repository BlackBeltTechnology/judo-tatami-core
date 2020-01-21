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
import rest.demo.services.CategoryInfo;
import rest.demo.services.InternationalOrderInfo;
import rest.demo.services.OrderInfo;
import rest.demo.services.ProductInfo;
import rest.demo.services.ShipperInfo;

import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.time.ZonedDateTime;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

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
import static rest.demo.services.CategoryInfo.categoryInfoBuilder;
import static rest.demo.services.InternationalOrderInfo.internationalOrderInfoBuilder;
import static rest.demo.services.OrderInfo.orderInfoBuilder;
import static rest.demo.services.OrderItem.orderItemBuilder;
import static rest.demo.services.ProductInfo.productInfoBuilder;
import static rest.demo.services.ShipperInfo.shipperInfoBuilder;

@Category(JaxRSTestSuite.class)
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class JaxRSITest {
    private static final String BASE_URL = "http://localhost:8181/cxf/demo/internalAP";

    private static final String DEMO_SERVICE_GET_ALL_ORDERS = "/demo/services/getAllOrders";
    private static final String DEMO_SERVICE_GET_ALL_INTERNATIONAL_ORDERS = "/demo/services/getAllInternationalOrders";
    private static final String DEMO_SERVICE_CREATE_ORDER = "/demo/services/createOrder";
    private static final String DEMO_SERVICE_CREATE_INTERNATIONAL_ORDER = "/demo/services/createInternationalOrder";
    private static final String DEMO_SERVICE_CREATE_PRODUCT = "/demo/services/createProduct";
    private static final String DEMO_SERVICE_CREATE_SHIPPER = "/demo/services/createShipper";
    private static final String DEMO_SERVICE_CREATE_CATEGORY = "/demo/services/createCategory";

    private static final String DEMO_SERVICE_ORDERINFO_DELETE_ORDER = "/demo/services/OrderInfo/deleteOrder";
    private static final String DEMO_SERVICE_ORDERINFO_CHANGE_SHIPMENT = "/demo/services/OrderInfo/changeShipment";
    private static final String DEMO_SERVICE_ORDERINFO_UPDATE_ORDER = "/demo/services/OrderInfo/updateOrder";

    private static final String DEMO_SERVICE_ORDERINFO_CATEGORIES_GET = "/demo/services/OrderInfo/categories/get";

    private static final String DEMO_SERVICE_ORDERINFO_ITEMS_GET = "/demo/services/OrderInfo/items/get";
    private static final String DEMO_SERVICE_ORDERINFO_ITEMS_SET = "/demo/services/OrderInfo/items/set";
    private static final String DEMO_SERVICE_ORDERINFO_ITEMS_ADD_ALL = "/demo/services/OrderInfo/items/addAll";
    private static final String DEMO_SERVICE_ORDERINFO_ITEMS_REMOVE_ALL = "/demo/services/OrderInfo/items/removeAll";

    private static final String DEMO_SERVICE_PRODUCTINFO_CATEGORY_GET_RANGE = "/demo/services/ProductInfo/category/getRange";
    private static final String DEMO_SERVICE_PRODUCTINFO_CATEGORY_UNSET = "/demo/services/ProductInfo/category/unset";

    //TODO-check&clean: moved to pom
    //public static final String FRAMEWORK_COMPILER_API = "compiler-api";
    //public static final String FRAMEWORK_GROUPID = "hu.blackbelt.judo.framework";

    //public static final String BLACKBELT_CXF_GROUPID = "hu.blackbelt.cxf";
    //public static final String JAXRS_APPLICATION_MANAGER = "cxf-jaxrs-application-manager";
    //public static final String JAXRS_APPLICATION_MANAGER_VERSION = "0.5.0.develop_00081";

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
    public Option[] config () throws FileNotFoundException {
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

    private Map<String, Object> fillTestMap () {
        //key: path to endpoint method, value: object to return from dispatcher
        Map<String, Object> testMap = new HashMap<>();
        //UnboundServices
        //getAllOrders (input: none, output: collection<OrderInfo>), required (OrderInfo): orderDate
        testMap.put("demo.services.__UnboundServices#getAllOrders", ImmutableList.of(
                        orderInfoBuilder().shipperName("shipperNameInOrderInfo0").orderDate(ZonedDateTime.now()).build().toMap(),
                        orderInfoBuilder().shipperName("shipperNameInOrderInfo1").orderDate(ZonedDateTime.now()).build().toMap()));

        //getAllInternationalOrders (input: none, output: collection<InternationalOrderInfo>), required (InternationalOrderInfo): orderDate
        testMap.put("demo.services.__UnboundServices#getAllInternationalOrders", ImmutableList.of(
                        internationalOrderInfoBuilder().shipperName("shipperNameInInternationalOrderInfo0").orderDate(ZonedDateTime.now()).build().toMap(),
                        internationalOrderInfoBuilder().shipperName("shipperNameInInternationalOrderInfo1").orderDate(ZonedDateTime.now()).build().toMap()));

        //createOrder (input: OrderInfo, output: OrderInfo), required (OrderInfo): orderDate
        testMap.put("demo.services.__UnboundServices#createOrder", orderInfoBuilder().shipperName("shipperNameInNewOrderInfo").orderDate(ZonedDateTime.now()).build().toMap());

        //createInternationalOrder (input: InternationalOrderInfo, output: InternationalOrderInfo), req: excisetax, orderdate, customsdescr
        testMap.put("demo.services.__UnboundServices#createInternationalOrder",
                internationalOrderInfoBuilder().shipperName("shipperNameInNewInternationalOrderInfo").orderDate(ZonedDateTime.now()).exciseTax(3.14).customsDescription("customsDescription").build().toMap());

        //createProduct (input: ProductInfo, output: ProductInfo), req: unitprice, category, productname
        testMap.put("demo.services.__UnboundServices#createProduct",
                productInfoBuilder().productName("productNameInNewProductInfo").category(categoryInfoBuilder().categoryName("categoryInfoInCreateProduct").build()).unitPrice(3.14).build().toMap());

        //createShipper (input: ShipperInfo, output: ShipperInfo)
        testMap.put("demo.services.__UnboundServices#createShipper", shipperInfoBuilder().companyName("companyNameInNewShipperInfo").build().toMap());

        //createCategory (input: CategoryInfo output: CategoryInfo), req: categoryName
        testMap.put("demo.services.__UnboundServices#createCategory", categoryInfoBuilder().categoryName("categoryNameInNewCategoryInfo").build().toMap());

        //updateOrder (input: OrderInfo, output: OrderInfo)
        testMap.put("demo.services.OrderInfo#updateOrder", orderInfoBuilder().shipperName("newShipperNameInNewOrderInfo").orderDate(ZonedDateTime.now()).build().toMap());

        //changeShipment (input: ShipmentChange, output: OrderInfo)
        testMap.put("demo.services.OrderInfo#changeShipment", orderInfoBuilder().shipperName("shipperNameInNewShipmentChange").orderDate(ZonedDateTime.now()).build().toMap());

        //OrderInfo-categories (input: identifier only, output: collection<CategoryInfo>)
        testMap.put("demo.services.OrderInfo__categories#get", ImmutableList.of(
                        categoryInfoBuilder().categoryName("categoryName0").build().toMap(),
                        categoryInfoBuilder().categoryName("categoryName1").build().toMap()));

        //OrderInfo-items (input: identifier only, output: collection<OrderItem>)
        testMap.put("demo.services.OrderInfo__items#get", ImmutableList.of(
                        orderItemBuilder().productName("productName0").quantity(42).discount(2.71).product(
                                productInfoBuilder().productName("productName").unitPrice(3.14).category(
                                        categoryInfoBuilder().categoryName("categoryNameInProductInfo").build()
                                ).build()
                        ).category(
                                categoryInfoBuilder().categoryName("categoryNameInOrderItem").build()
                        ).build().toMap(),
                        orderItemBuilder().productName("productName1").quantity(42).discount(2.71).product(
                                productInfoBuilder().productName("productName").unitPrice(3.14).category(
                                        categoryInfoBuilder().categoryName("categoryNameInProductInfo").build()
                                ).build()
                        ).category(
                                categoryInfoBuilder().categoryName("categoryNameInOrderItem").build()
                        ).build().toMap()));

        //ProductInfo-category (input: identifier only, output: collection<CategoryInfo>)
        testMap.put("demo.services.ProductInfo__category#getRange", ImmutableList.of(
                        categoryInfoBuilder().categoryName("categoryName0").build().toMap(),
                        categoryInfoBuilder().categoryName("categoryName1").build().toMap()));
        return testMap;
    }

    @Before
    public void init () throws Exception {
        class Semaphore {
        }

        Map<String, Object> testMap = fillTestMap();

        ServiceReference reference = bundleContext.getServiceReference(Semaphore.class);
        if (reference == null) {

            assertBundleStarted(bundleContext, "northwind-asm2jaxrsapi");

            dispatcher = new Dispatcher() {
                @Override
                public Map<String, Object> callOperation (String operationFqName, Map<String, Object> payload) {
                    log.log(LOG_INFO, "Dispatcher called - " + operationFqName + " Payload: " + payload.toString());
                    if (!testMap.containsKey(operationFqName)) {
                        switch (operationFqName) {
                            case "demo.services.OrderInfo#deleteOrder": //(input: none)
                            case "demo.services.OrderInfo__items#set": //(input: identifier & collection<rest.demo.services.OrderInfo$items$Reference>)
                            case "demo.services.OrderInfo__items#addAll": //(input: identifier & collection<rest.demo.services.OrderInfo$items$Reference>)
                            case "demo.services.OrderInfo__items#removeAll": //(input: identifier & collection<rest.demo.services.OrderInfo$items$Reference>)
                            case "demo.services.ProductInfo__category#unset": //(input: identifier only)
                                return ImmutableMap.of();
                            default:
                                log.log(LOG_ERROR, "Operation not found by operationFqName! Given operationFqName was \"" + operationFqName + "\"");
                                return ImmutableMap.of();
                        }
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

    public WebTarget getWebTarget (String pathToMethod) {
        return ClientBuilder.newClient().register(
                new JacksonJaxbJsonProvider(objectMapper, JacksonJaxbJsonProvider.DEFAULT_ANNOTATIONS))
                .target(BASE_URL).path(pathToMethod);
    }

    //GET
    public Response getResponse (String pathToMethod) {
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
    public Response getResponse (String pathToMethod, Object input) {
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
    public Response getResponseWithEmptyPost (String pathToMethod) {
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
    public void clearTestResponse () {
        testResponse.close();
    }

    private void logTest (String path) {
        log.log(LOG_INFO, "==============================================\nTesting " + path + "...\n==============================================");
    }

    //Unbound Services
    @Test
    public void testGetAllOrders () throws IOException {
        logTest(DEMO_SERVICE_GET_ALL_ORDERS);

        Response response = getResponse(DEMO_SERVICE_GET_ALL_ORDERS);

        assertNotNull(response);
        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));


        //serializing the response containing collection
        List<OrderInfo> output = response.readEntity(new GenericType<List<OrderInfo>>() {
        });

        assertTrue("shipperNameInOrderInfo0".equals(output.get(0).getShipperName()));
        assertTrue("shipperNameInOrderInfo1".equals(output.get(1).getShipperName()));
    }

    @Test
    public void testGetAllInternationalOrders () {
        logTest(DEMO_SERVICE_GET_ALL_INTERNATIONAL_ORDERS);

        Response response = getResponse(DEMO_SERVICE_GET_ALL_INTERNATIONAL_ORDERS);

        assertNotNull(response);
        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));

        List<InternationalOrderInfo> output = response.readEntity(new GenericType<List<InternationalOrderInfo>>() {
        });

        assertTrue("shipperNameInInternationalOrderInfo0".equals(output.get(0).getShipperName()));
        assertTrue("shipperNameInInternationalOrderInfo1".equals(output.get(1).getShipperName()));
    }

    @Test
    public void testCreateOrder () {
        logTest(DEMO_SERVICE_CREATE_ORDER);

        Response response = getResponse(DEMO_SERVICE_CREATE_ORDER, orderInfoBuilder().orderDate(ZonedDateTime.now()).build());

        assertNotNull(response);
        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
        assertTrue("shipperNameInNewOrderInfo".equals(response.readEntity(OrderInfo.class).getShipperName()));
    }

    @Test
    public void testCreateInternationalOrder () {
        logTest(DEMO_SERVICE_CREATE_INTERNATIONAL_ORDER);

        Response response = getResponse(DEMO_SERVICE_CREATE_INTERNATIONAL_ORDER,
                internationalOrderInfoBuilder().exciseTax(3.14).orderDate(ZonedDateTime.now()).customsDescription("customsDescription").build());

        assertNotNull(response);
        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
        assertTrue("shipperNameInNewInternationalOrderInfo".equals(response.readEntity(InternationalOrderInfo.class).getShipperName()));
    }

    @Test
    public void testCreateProduct () {
        logTest(DEMO_SERVICE_CREATE_PRODUCT);

        Response response = getResponse(DEMO_SERVICE_CREATE_PRODUCT,
                productInfoBuilder().productName("productNameInNewProductInfo").category(categoryInfoBuilder().categoryName("categoryInfoInCreateProduct").build()).unitPrice(3.14).build());

        assertNotNull(response);
        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
        assertTrue("productNameInNewProductInfo".equals(response.readEntity(ProductInfo.class).getProductName()));
    }

    @Test
    public void testCreateShipper () {
        logTest(DEMO_SERVICE_CREATE_SHIPPER);

        Response response = getResponse(DEMO_SERVICE_CREATE_SHIPPER, shipperInfoBuilder().build());

        assertNotNull(response);
        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
        assertTrue("companyNameInNewShipperInfo".equals(response.readEntity(ShipperInfo.class).getCompanyName()));
    }

    //Bound Services
    @Test
    public void testOrderInfoDeleteOrder () {
        logTest(DEMO_SERVICE_ORDERINFO_DELETE_ORDER);

        Response response = getResponseWithEmptyPost(DEMO_SERVICE_ORDERINFO_DELETE_ORDER);

        assertNotNull(response);
        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
    }

    @Test
    public void testCreateCategory () {
        logTest(DEMO_SERVICE_CREATE_CATEGORY);

        Response response = getResponse(DEMO_SERVICE_CREATE_CATEGORY, categoryInfoBuilder().categoryName("categoryNameInNewCategoryInfo").build());

        assertNotNull(response);
        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
        assertTrue("categoryNameInNewCategoryInfo".equals(response.readEntity(CategoryInfo.class).getCategoryName()));
    }

    @Test
    public void testOrderInfoUpdateOrder () {
        logTest(DEMO_SERVICE_ORDERINFO_UPDATE_ORDER);

        Response response = getResponse(DEMO_SERVICE_ORDERINFO_UPDATE_ORDER,
                internationalOrderInfoBuilder().orderDate(ZonedDateTime.now()).exciseTax(3.14).customsDescription("customsDescription").build());

        assertNotNull(response);
        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));

        assertTrue("newShipperNameInNewOrderInfo".equals(response.readEntity(OrderInfo.class).getShipperName()));
    }

//    @Test
//    public void testOrderInfoChangeShipment () {
//        logTest(DEMO_SERVICE_ORDERINFO_CHANGE_SHIPMENT);
//
//        Response response = getResponse(DEMO_SERVICE_ORDERINFO_CHANGE_SHIPMENT, orderInfoBuilder().orderDate(ZonedDateTime.now()).build());
//
//        assertNotNull(response);
//        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
//        assertTrue("shipperNameInNewShipmentChange".equals(response.readEntity(ShipmentChange.class).getShipperName()));
//    }
//
//    @Test
//    public void testOrderInfoCategoriesGet () {
//        logTest(DEMO_SERVICE_ORDERINFO_CATEGORIES_GET);
//
//        Response response = getResponse(DEMO_SERVICE_ORDERINFO_CATEGORIES_GET);
//
//        assertNotNull(response);
//        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
//
//        List<CategoryInfo> output = response.readEntity(new GenericType<List<CategoryInfo>>() {
//        });
//
//        assertTrue("categoryName0".equals(output.get(0).getCategoryName()));
//        assertTrue("categoryName1".equals(output.get(1).getCategoryName()));
//    }
//
//    @Test
//    public void testOrderInfoItemsGet () {
//        logTest(DEMO_SERVICE_ORDERINFO_ITEMS_GET);
//
//        Response response = getResponse(DEMO_SERVICE_ORDERINFO_ITEMS_GET);
//
//        assertNotNull(response);
//        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
//
//        List<OrderItem> output = response.readEntity(new GenericType<List<OrderItem>>() {
//        });
//
//        assertTrue("productName0".equals(output.get(0).getProductName()));
//        assertTrue("productName1".equals(output.get(1).getProductName()));
//
//    }
//
//    @Test
//    public void testProductInfoCategoryGetRange () {
//        logTest(DEMO_SERVICE_PRODUCTINFO_CATEGORY_GET_RANGE);
//
//        Response response = getResponse(DEMO_SERVICE_PRODUCTINFO_CATEGORY_GET_RANGE);
//
//        assertNotNull(response);
//        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
//
//        List<CategoryInfo> output = response.readEntity(new GenericType<List<CategoryInfo>>() {
//        });
//
//        assertTrue("categoryName0".equals(output.get(0).getCategoryName()));
//        assertTrue("categoryName1".equals(output.get(1).getCategoryName()));
//    }
//
//    @Test
//    public void testProductInfoCategoryUnset () throws Exception {
//        logTest(DEMO_SERVICE_PRODUCTINFO_CATEGORY_UNSET);
//
//        Response response = getResponseWithEmptyPost(DEMO_SERVICE_PRODUCTINFO_CATEGORY_UNSET);
//
//        assertNotNull(response);
//        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
//    }
//
//    @Test
//    public void testOrderInfoItemsSet () {
//        logTest(DEMO_SERVICE_ORDERINFO_ITEMS_SET);
//
//        Response response = getResponse(DEMO_SERVICE_ORDERINFO_ITEMS_SET, ImmutableList.of(orderInfo$items$ReferenceBuilder().build()));
//
//        assertNotNull(response);
//        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
//    }
//
//    @Test
//    public void testOrderInfoItemsAddAll () {
//        logTest(DEMO_SERVICE_ORDERINFO_ITEMS_ADD_ALL);
//
//        Response response = getResponse(DEMO_SERVICE_ORDERINFO_ITEMS_ADD_ALL, ImmutableList.of(orderInfo$items$ReferenceBuilder().build()));
//        assertNotNull(response);
//        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
//    }
//
//    @Test
//    public void testOrderInfoItemsRemoveAll () {
//        logTest(DEMO_SERVICE_ORDERINFO_ITEMS_REMOVE_ALL);
//
//        Response response = getResponse(DEMO_SERVICE_ORDERINFO_ITEMS_REMOVE_ALL, ImmutableList.of(orderInfo$items$ReferenceBuilder().build()));
//
//        assertNotNull(response);
//        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
//    }
}