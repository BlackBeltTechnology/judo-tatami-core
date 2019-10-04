package hu.blackbelt.judo.tatami.itest;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.judo.tatami.core.Dispatcher;
import hu.blackbelt.osgi.utils.osgi.api.BundleTrackerManager;
import org.json.JSONObject;
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
import org.osgi.service.log.LogService;
import sdk.demo.service.*;

import javax.inject.Inject;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Entity;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.GenericType;
import javax.ws.rs.core.MediaType;
import javax.ws.rs.core.Response;
import java.io.FileNotFoundException;
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

@Category(JaxRSTestSuite.class)
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class JaxRSITest {
    private static final String BASE_URL = "http://localhost:8181/cxf/demo/internalAP";

    private static final String DEMO_SERVICE_GET_ALL_ORDERS = "/demo/service/getAllOrders";
    private static final String DEMO_SERVICE_GET_ALL_INTERNATIONAL_ORDERS = "/demo/service/getAllInternationalOrders";
    private static final String DEMO_SERVICE_CREATE_ORDER = "/demo/service/createOrder";
    private static final String DEMO_SERVICE_CREATE_INTERNATIONAL_ORDER = "/demo/service/createInternationalOrder";
    private static final String DEMO_SERVICE_CREATE_PRODUCT = "/demo/service/createProduct";
    private static final String DEMO_SERVICE_CREATE_SHIPPER = "/demo/service/createShipper";
    private static final String DEMO_SERVICE_CREATE_CATEGORY = "/demo/service/createCategory";
    private static final String DEMO_SERVICE_UPDATE_ORDER = "/demo/service/updateOrder";
    private static final String DEMO_SERVICE_CHANGE_SHIPMENT = "/demo/service/changeShipment";
    private static final String DEMO_SERVICE_DELETE_ORDER = "/demo/service/deleteOrder";

    private static final String DEMO_SERVICE_ORDERINFOQUERY_CATEGORIES_GET = "/demo/service/OrderInfoQuery/categories/get";

    private static final String DEMO_SERVICE_ORDERINFOQUERY_ITEMS_GET = "/demo/service/OrderInfoQuery/items/get";
    private static final String DEMO_SERVICE_ORDERINFOQUERY_ITEMS_SET = "/demo/service/OrderInfoQuery/items/set";
    private static final String DEMO_SERVICE_ORDERINFOQUERY_ITEMS_ADD_ALL = "/demo/service/OrderInfoQuery/items/addAll";
    private static final String DEMO_SERVICE_ORDERINFOQUERY_ITEMS_REMOVE_ALL = "/demo/service/OrderInfoQuery/items/removeAll";

    private static final String DEMO_SERVICE_PRODUCTINFOQUERY_CATEGORY_GET_RANGE = "/demo/service/ProductInfoQuery/category/getRange";
    private static final String DEMO_SERVICE_PRODUCTINFOQUERY_CATEGORY_UNSET = "/demo/service/ProductInfoQuery/category/unset";

    public static final String FRAMEWORK_GROUPID = "hu.blackbelt.judo.framework";

    public static final String FRAMEWORK_COMPILER_API = "compiler-api";

    public static final String BLACKBELT_CXF_GROUPID = "hu.blackbelt.cxf";
    public static final String JAXRS_APPLICATION_MANAGER = "cxf-jaxrs-application-manager";
    public static final String JAXRS_APPLICATION_MANAGER_VERSION = "0.4.0";
    public static final String FEATURE_JUDO_TATAMI_CORE = "judo-tatami-core";

    private Dispatcher dispatcher;
    private Response testResponse;

    @Inject
    LogService log;

    @Inject
    protected BundleTrackerManager bundleTrackerManager;

    @Inject
    BundleContext bundleContext;

    @Configuration
    public Option[] config () throws FileNotFoundException {
        return combine(karafConfig(this.getClass()),

                features(karafStandardRepo()),

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
                //TODO: #ZonedDateTime-misery
                mavenBundle()
                        .groupId("com.fasterxml.jackson.datatype")
                        .artifactId("jackson-datatype-jdk8")
                        .version("2.9.8").start()
        );
    }

    private Map<String, Object> fillTestMap () {
        //key: path to endpoint method, value: object to return from dispatcher
        Map<String, Object> testMap = new HashMap<>();
        //UnboundServices
        //getAllOrders (input: none, output: collection<OrderInfoQuery>), required (OrderInfoQuery): orderDate
        testMap.put("demo.service.ʘUnboundServices#getAllOrders",
                ImmutableList.of(
                        OrderInfoQuery.orderInfoQueryBuilder().shipperName("shipperNameInOrderInfoQuery0")/*.orderDate(ZonedDateTime.now())*/.build().toMap(),
                        OrderInfoQuery.orderInfoQueryBuilder().shipperName("shipperNameInOrderInfoQuery1")/*.orderDate(ZonedDateTime.now())*/.build().toMap()
                )
        );
        //getAllInternationalOrders (input: none, output: collection<InternationalOrderInfoQuery>), required (InternationalOrderInfoQuery): orderDate
        testMap.put("demo.service.ʘUnboundServices#getAllInternationalOrders",
                ImmutableList.of(
                        InternationalOrderInfoQuery.internationalOrderInfoQueryBuilder().shipperName("shipperNameInInternationalOrderInfoQuery0")/*.orderDate(ZonedDateTime.now())*/.build().toMap(),
                        InternationalOrderInfoQuery.internationalOrderInfoQueryBuilder().shipperName("shipperNameInInternationalOrderInfoQuery1")/*.orderDate(ZonedDateTime.now())*/.build().toMap()
                )
        );
        //createOrder (input: OrderInfo, output: OrderInfo), required (OrderInfo): orderDate
        testMap.put("demo.service.ʘUnboundServices#createOrder",
                OrderInfo.orderInfoBuilder().shipperName("shipperNameInNewOrderInfo")/*.orderDate(ZonedDateTime.now())*/.build().toMap()
        );
        //createInternationalOrder (input: InternationalOrderInfo, output: InternationalOrderInfo), req: excisetax, orderdate, customsdescr
        testMap.put("demo.service.ʘUnboundServices#createInternationalOrder",
                InternationalOrderInfo.internationalOrderInfoBuilder().shipperName("shipperNameInNewInternationalOrderInfo")/*.orderDate(ZonedDateTime.now())*/.exciseTax(3.14).customsDescription("customsDescription").build().toMap()
        );
        //createProduct (input: ProductInfo, output: ProductInfo), req: unitprice, category, productname
        testMap.put("demo.service.ʘUnboundServices#createProduct",
                ProductInfo.productInfoBuilder().productName("productNameInNewProductInfo").category(CategoryInfo.categoryInfoBuilder().categoryName("categoryInfoInCreateProduct").build()).unitPrice(3.14).build().toMap()
        );
        //createShipper (input: ShipperInfo, output: ShipperInfo)
        testMap.put("demo.service.ʘUnboundServices#createShipper",
                ShipperInfo.shipperInfoBuilder().companyName("companyNameInNewShipperInfo").build().toMap()
        );
        //createCategory (input: CategoryInfo output: CategoryInfo), req: categoryName
        testMap.put("demo.service.ʘUnboundServices#createCategory",
                CategoryInfo.categoryInfoBuilder().categoryName("categoryNameInNewCategoryInfo").build().toMap()
        );
        //updateOrder (input: OrderInfo, output: OrderInfo)
        testMap.put("demo.service.OrderInfo#updateOrder",
                OrderInfo.orderInfoBuilder().shipperName("newShipperNameInNewOrderInfo")/*.orderDate(ZonedDateTime.now())*/.build().toMap()
        );
        //changeShipment (input: ShipmentChange, output: OrderInfo)
        testMap.put("demo.service.OrderInfo#changeShipment",
                OrderInfo.orderInfoBuilder().shipperName("shipperNameInNewShipmentChange")/*.orderDate(ZonedDateTime.now())*/.build().toMap()
        );
        //OrderInfoQuery-categories (input: identifier only, output: collection<CategoryInfo>)
        testMap.put("demo.service.OrderInfoQueryʘcategories#get",
                ImmutableList.of(
                        CategoryInfo.categoryInfoBuilder().categoryName("categoryName0").build().toMap(),
                        CategoryInfo.categoryInfoBuilder().categoryName("categoryName1").build().toMap()
                )
        );
        //OrderInfoQuery-items (input: identifier only, output: collection<OrderItemQuery>)
        testMap.put("demo.service.OrderInfoQueryʘitems#get",
                ImmutableList.of(
                        OrderItemQuery.orderItemQueryBuilder().productName("productName0").quantity(42).discount(2.71).product(
                                ProductInfoQuery.productInfoQueryBuilder().productName("productName").unitPrice(3.14).category(
                                        CategoryInfo.categoryInfoBuilder().categoryName("categoryNameInProductInfoQuery").build()
                                ).build()
                        ).category(
                                CategoryInfo.categoryInfoBuilder().categoryName("categoryNameInOrderItemQuery").build()
                        ).build().toMap(),
                        OrderItemQuery.orderItemQueryBuilder().productName("productName1").quantity(42).discount(2.71).product(
                                ProductInfoQuery.productInfoQueryBuilder().productName("productName").unitPrice(3.14).category(
                                        CategoryInfo.categoryInfoBuilder().categoryName("categoryNameInProductInfoQuery").build()
                                ).build()
                        ).category(
                                CategoryInfo.categoryInfoBuilder().categoryName("categoryNameInOrderItemQuery").build()
                        ).build().toMap()
                )
        );
        //ProductInfoQuery-category (input: identifier only, output: collection<CategoryInfo>)
        testMap.put("demo.service.ProductInfoQueryʘcategory#getRange",
                ImmutableList.of(
                        CategoryInfo.categoryInfoBuilder().categoryName("categoryName0").build().toMap(),
                        CategoryInfo.categoryInfoBuilder().categoryName("categoryName1").build().toMap()
                )
        );
        return testMap;
    }

    @Before
    public void init () throws Exception {
        assertBundleStarted(bundleContext, "northwind-asm2jaxrsapi");

        Map<String, Object> testMap = fillTestMap();

        dispatcher = new Dispatcher() {
            @Override
            public Map<String, Object> callOperation (String target, String operationFqName, Map<String, Object> payload) {
                log.log(LOG_INFO, "Dispatcher called - " + target + " " + operationFqName + " Payload: " + payload.toString());

                if (!testMap.containsKey(operationFqName)) {
                    //todo: something smart
                    switch (operationFqName) {
                        case "demo.service.OrderInfo#deleteOrder": //UnboundServices/deleteOrder (input: none)
                        case "demo.service.OrderInfoQueryʘitems#set": //(input: identifier & collection<rest.demo.service.OrderInfoQuery$items$Reference>)
                        case "demo.service.OrderInfoQueryʘitems#addAll": //(input: identifier & collection<rest.demo.service.OrderInfoQuery$items$Reference>)
                        case "demo.service.OrderInfoQueryʘitems#removeAll": //(input: identifier & collection<rest.demo.service.OrderInfoQuery$items$Reference>)
                        case "demo.service.ProductInfoQueryʘcategory#unset": //(input: identifier only)
                            return ImmutableMap.of();
                        default:
                            log.log(LOG_ERROR, "Operation not found by operationFqName! Given operationFqName was \"" + operationFqName + "\"");
                            return ImmutableMap.of();
                    }
                } else {
                    return ImmutableMap.of("output", testMap.get(operationFqName));
                }
            }
        };
        bundleContext.registerService(Dispatcher.class, dispatcher, null);

        waitWebPage(BASE_URL + "/?_wadl");
    }

    //GET
    public Response getResponse (String pathToMethod) {
        WebTarget wt = ClientBuilder.newClient().register(new JacksonJaxbJsonProvider()).target(BASE_URL).path(pathToMethod); //breadcrumbs @ZDT .register()?
        log.log(LOG_INFO, "csecs: " + wt.getUri().getPath());
        try {
            testResponse = wt
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
        WebTarget wt = ClientBuilder.newClient().register(new JacksonJaxbJsonProvider()).target(BASE_URL).path(pathToMethod);
        try {
            testResponse = wt
                    .request("application/json")
                    .header("__identifier", UUID.randomUUID())
                    .post(Entity.entity(input, MediaType.APPLICATION_JSON));
        } catch (Exception e) {
            log.log(LOG_ERROR, "EXCEPTION: ", e);
        }
        return testResponse;
    }

    //POST with class type
    public Response getResponse (String pathToMethod, Class<?> clazz) {
        WebTarget wt = ClientBuilder.newClient().register(new JacksonJaxbJsonProvider()).target(BASE_URL);
        try {
            testResponse = wt.path(pathToMethod)
                    .request("application/json")
                    .header("__identifier", UUID.randomUUID()) //TODO: #BigFatNagyvonalusagi (Decapitate the Unbound!)
                    .post(Entity.json(clazz));
        } catch (Exception e) {
            log.log(LOG_ERROR, "EXCEPTION: ", e);
        }
        return testResponse;
    }

    @AfterEach
    public void clearTestResponse () {
        testResponse.close();
    }

    //todo-remove
    @Test
    public void jatektalaj () {
        //big-fat-TODO-ask: accessing asm2jaxrs output (@itestpomxml), whatis&howto: nortwind-rest.jar (why duplicates in ../r.d.service/*)
        //rest.demo.service.OrderInfo nonSDKorderInfo = rest.demo.service.OrderInfo.orderInfoBuilder().build();
        //log.log(LOG_INFO, "da spookiness: " + ZonedDateTime.now());
    }

    @Test
    public void testGetAllOrders () {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing UnboundServices/getAllOrders...");
        log.log(LOG_INFO, "=======================");
        Response respGetAllOrders = getResponse(DEMO_SERVICE_GET_ALL_ORDERS);

        assertNotNull(respGetAllOrders);
        assertTrue(respGetAllOrders.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)); //TODO: #ZonedDateTime
        //serializing the response containing collection
        List<OrderInfoQuery> output = respGetAllOrders.readEntity(new GenericType<List<OrderInfoQuery>>() {
        });

        assertTrue("shipperNameInOrderInfoQuery0".equals(output.get(0).getShipperName()));
        assertTrue("shipperNameInOrderInfoQuery1".equals(output.get(1).getShipperName()));
    }

    @Test
    public void testGetAllInternationalOrders () {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing UnboundServices/getAllInternationalOrders...");
        log.log(LOG_INFO, "=======================");

        Response response = getResponse(DEMO_SERVICE_GET_ALL_INTERNATIONAL_ORDERS);

        assertNotNull(response);
        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));

        List<InternationalOrderInfoQuery> output = response.readEntity(new GenericType<List<InternationalOrderInfoQuery>>() {
        }); //TODO: #ZonedDateTime

        assertTrue("shipperNameInInternationalOrderInfoQuery0".equals(output.get(0).getShipperName()));
        assertTrue("shipperNameInInternationalOrderInfoQuery1".equals(output.get(1).getShipperName()));
    }

    @Test
    public void testCreateOrder () {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing UnboundServices/createOrder...");
        log.log(LOG_INFO, "=======================");

        Response respCreateOrder = getResponse(DEMO_SERVICE_CREATE_ORDER, OrderInfo.orderInfoBuilder()/*.orderDate(ZonedDateTime.now())*/.build());

        assertNotNull(respCreateOrder);
        assertTrue(respCreateOrder.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)); //TODO: #ZonedDateTime
        assertTrue("shipperNameInNewOrderInfo".equals(new JSONObject(respCreateOrder.readEntity(String.class)).get("shipperName")));
    }

    @Test
    public void testCreateInternationalOrder () {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing UnboundServices/createInternationalOrder...");
        log.log(LOG_INFO, "=======================");

        Response respCreateInternationalOrder = getResponse(DEMO_SERVICE_CREATE_INTERNATIONAL_ORDER, InternationalOrderInfo.internationalOrderInfoBuilder().exciseTax(3.14)/*.orderDate(ZonedDateTime.now())*/.customsDescription("customsDescription").build());

        assertNotNull(respCreateInternationalOrder);
        assertTrue(respCreateInternationalOrder.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)); //TODO: #ZonedDateTime
        assertTrue("shipperNameInNewInternationalOrderInfo".equals(new JSONObject(respCreateInternationalOrder.readEntity(String.class)).get("shipperName")));
    }

    @Test
    public void testCreateProduct () {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing UnboundServices/createProduct...");
        log.log(LOG_INFO, "=======================");

        Response respCreateProduct = getResponse(DEMO_SERVICE_CREATE_PRODUCT, ProductInfo.productInfoBuilder().productName("productNameInNewProductInfo").category(CategoryInfo.categoryInfoBuilder().categoryName("categoryInfoInCreateProduct").build()).unitPrice(3.14).build());

        assertNotNull(respCreateProduct);
        assertTrue(respCreateProduct.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
        assertTrue("productNameInNewProductInfo".equals(new JSONObject(respCreateProduct.readEntity(String.class)).get("productName")));
    }

    @Test
    public void testCreateShipper () {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing UnboundServices/createShipper...");
        log.log(LOG_INFO, "=======================");

        Response respCreateShipper = getResponse(DEMO_SERVICE_CREATE_SHIPPER, ShipperInfo.shipperInfoBuilder().build());

        assertNotNull(respCreateShipper);
        assertTrue(respCreateShipper.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
        assertTrue("companyNameInNewShipperInfo".equals(new JSONObject(respCreateShipper.readEntity(String.class)).get("companyName")));
    }

    @Test
    public void testCreateCategory () {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing UnboundServices/createCategory...");
        log.log(LOG_INFO, "=======================");

        Response respCreateShipper = getResponse(DEMO_SERVICE_CREATE_CATEGORY, CategoryInfo.categoryInfoBuilder().categoryName("categoryNameInNewCategoryInfo").build());

        assertNotNull(respCreateShipper);
        assertTrue(respCreateShipper.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
        assertTrue("categoryNameInNewCategoryInfo".equals(new JSONObject(respCreateShipper.readEntity(String.class)).get("categoryName")));
    }

    @Test
    public void testUpdateOrder () {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing UnboundServices/updateOrder...");
        log.log(LOG_INFO, "=======================");

        Response response = getResponse(DEMO_SERVICE_UPDATE_ORDER, InternationalOrderInfo.internationalOrderInfoBuilder()/*.orderDate(ZonedDateTime.now())*/.exciseTax(3.14).customsDescription("customsDescription").build());

        assertNotNull(response);
        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)); //TODO: #ZonedDateTime
        assertTrue("newShipperNameInNewOrderInfo".equals(new JSONObject(response.readEntity(String.class)).get("shipperName")));
    }

    @Test
    public void testDeleteOrder () {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing UnboundServices/deleteOrder...");
        log.log(LOG_INFO, "=======================");

        Response response = getResponse(DEMO_SERVICE_DELETE_ORDER, OrderInfo.class);

        assertNotNull(response);
        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
    }

    @Test
    public void testChangeShipment () {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing UnboundServices/changeShipment...");
        log.log(LOG_INFO, "=======================");

        Response response = getResponse(DEMO_SERVICE_CHANGE_SHIPMENT, OrderInfo.orderInfoBuilder()/*.orderDate(ZonedDateTime.now())*/.build());

        assertNotNull(response);
        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL)); //TODO: #ZonedDateTime
        assertTrue("shipperNameInNewShipmentChange".equals(new JSONObject(response.readEntity(String.class)).get("shipperName")));
    }

    @Test
    public void testOrderInfoQueryCategoriesGet () {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing OrderInfoQuery$categories/get...");
        log.log(LOG_INFO, "=======================");

        Response response = getResponse(DEMO_SERVICE_ORDERINFOQUERY_CATEGORIES_GET);

        assertNotNull(response);
        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));

        List<CategoryInfo> output = response.readEntity(new GenericType<List<CategoryInfo>>() {
        });

        assertTrue("categoryName0".equals(output.get(0).getCategoryName()));
        assertTrue("categoryName1".equals(output.get(1).getCategoryName()));
    }

    @Test
    public void testOrderInfoQueryItemsGet () {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing OrderInfoQuery$Items/get...");
        log.log(LOG_INFO, "=======================");

        Response response = getResponse(DEMO_SERVICE_ORDERINFOQUERY_ITEMS_GET);

        assertNotNull(response);
        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));

        List<OrderItemQuery> output = response.readEntity(new GenericType<List<OrderItemQuery>>() {
        });

        assertTrue("productName0".equals(output.get(0).getProductName()));
        assertTrue("productName1".equals(output.get(1).getProductName()));

    }

    @Test
    public void testProductInfoQueryCategoryGetRange () {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing ProductInfoQuery$category/getRange...");
        log.log(LOG_INFO, "=======================");

        Response response = getResponse(DEMO_SERVICE_PRODUCTINFOQUERY_CATEGORY_GET_RANGE);

        assertNotNull(response);
        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));

        List<CategoryInfo> output = response.readEntity(new GenericType<List<CategoryInfo>>() {
        });

        assertTrue("categoryName0".equals(output.get(0).getCategoryName()));
        assertTrue("categoryName1".equals(output.get(1).getCategoryName()));
    }

    @Test
    public void testProductInfoQueryCategoryUnset () throws Exception {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing ProductInfoQuery$category/unset...");
        log.log(LOG_INFO, "=======================");

        Response response = getResponse(DEMO_SERVICE_PRODUCTINFOQUERY_CATEGORY_UNSET, ProductInfoQuery$category$Reference.class);

        assertNotNull(response);
        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
    }

    @Test
    public void testOrderInfoQueryItemsSet () {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing OrderInfoQuery/items/set...");
        log.log(LOG_INFO, "=======================");

        Response response = getResponse(DEMO_SERVICE_ORDERINFOQUERY_ITEMS_SET, ImmutableList.of(OrderInfoQuery$items$Reference.orderInfoQuery$items$ReferenceBuilder().build()));

        assertNotNull(response);
        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
    }

    @Test
    public void testOrderInfoQueryItemsAddAll () {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing OrderInfoQuery/items/addAll...");
        log.log(LOG_INFO, "=======================");

        Response response = getResponse(DEMO_SERVICE_ORDERINFOQUERY_ITEMS_ADD_ALL, ImmutableList.of(OrderInfoQuery$items$Reference.orderInfoQuery$items$ReferenceBuilder().build()));
        log.log(LOG_INFO, "responseHeaderInAddAll(): " + response.getHeaders());
        assertNotNull(response);
        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
    }

    @Test
    public void testOrderInfoQueryItemsRemoveAll () {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing OrderInfoQuery/items/removeAll...");
        log.log(LOG_INFO, "=======================");

        Response response = getResponse(DEMO_SERVICE_ORDERINFOQUERY_ITEMS_REMOVE_ALL, ImmutableList.of(OrderInfoQuery$items$Reference.orderInfoQuery$items$ReferenceBuilder().build()));

        assertNotNull(response);
        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
    }
}