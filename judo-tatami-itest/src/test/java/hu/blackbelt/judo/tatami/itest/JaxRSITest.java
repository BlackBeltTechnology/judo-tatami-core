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
    //private static final String DEMO = "northwind-esm";//todo-ask: remove?

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
                        .versionAsInProject().start()
        );
    }

    //big-fat-TODO-ask: accessing asm2jaxrs output (@itestpomxml), whatis&howto: nortwind-rest.jar (why duplicates in ../r.d.service/*)
    private Map<String, Object> fillTestMap () {
        Map<String, Object> testMap = new HashMap<>();
        //--UnboundServices
        testMap.put("demo.service.ʘUnboundServices#getAllOrders",
                ImmutableList.of(
                        OrderInfoQuery.orderInfoQueryBuilder().shipperName("shipperNameInOrderInfoQuery0").build().toMap(),
                        OrderInfoQuery.orderInfoQueryBuilder().shipperName("shipperNameInOrderInfoQuery1").build().toMap()
                ));
        testMap.put("demo.service.ʘUnboundServices#getAllInternationalOrders",
                ImmutableList.of(
                        InternationalOrderInfoQuery.internationalOrderInfoQueryBuilder().shipperName("shipperNameInInternationalOrderInfoQuery0").build().toMap(),
                        InternationalOrderInfoQuery.internationalOrderInfoQueryBuilder().shipperName("shipperNameInInternationalOrderInfoQuery1").build().toMap()
                ));
        testMap.put("demo.service.ʘUnboundServices#createOrder",
                OrderInfo.orderInfoBuilder().shipperName("shipperNameInNewOrderInfo").build().toMap()
        );
        testMap.put("demo.service.ʘUnboundServices#createInternationalOrder",
                InternationalOrderInfo.internationalOrderInfoBuilder().shipperName("shipperNameInNewInternationalOrderInfo").build().toMap()
        );
        testMap.put("demo.service.ʘUnboundServices#createProduct",
                ProductInfo.productInfoBuilder().productName("productNameInNewProductInfo").build().toMap()
        );
        testMap.put("demo.service.ʘUnboundServices#createShipper",
                ShipperInfo.shipperInfoBuilder().companyName("companyNameInNewShipperInfo").build().toMap()
        );
        testMap.put("demo.service.ʘUnboundServices#createCategory",
                CategoryInfo.categoryInfoBuilder().categoryName("categoryNameInNewCategoryInfo").build().toMap()
        );
        testMap.put("demo.service.OrderInfo#updateOrder",
                OrderInfo.orderInfoBuilder().shipperName("newShipperNameInNewOrderInfo").build().toMap()
        );
        testMap.put("demo.service.OrderInfo#changeShipment",
                OrderInfo.orderInfoBuilder().shipperName("shipperNameInNewShipmentChange").build().toMap()
        );
        //--OrderInfoQuery-categories
        testMap.put("demo.service.OrderInfoQueryʘcategories#get",
                ImmutableList.of(
                        CategoryInfo.categoryInfoBuilder().categoryName("categoryName0").build().toMap(),
                        CategoryInfo.categoryInfoBuilder().categoryName("categoryName1").build().toMap()
                )
        );
        //--OrderInfoQuery-items
        testMap.put("demo.service.OrderInfoQueryʘitems#get",
                ImmutableList.of(
                        OrderItemQuery.orderItemQueryBuilder().productName("productName0").build().toMap(),
                        OrderItemQuery.orderItemQueryBuilder().productName("productName1").build().toMap()
                )
        );
        //--ProductInfoQuery-category
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
                    //todo: do something smart
                    switch (operationFqName) {
                        case "demo.service.OrderInfo#deleteOrder":
                        case "demo.service.OrderInfoQueryʘitems#set":
                        case "demo.service.OrderInfoQueryʘitems#addAll":
                        case "demo.service.OrderInfoQueryʘitems#removeAll":
                        case "demo.service.ProductInfoQueryʘcategory#unset":
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

    public Response getGETResponse (String pathToMethod) {
        WebTarget wt = ClientBuilder.newClient().register(new JacksonJaxbJsonProvider()).target(BASE_URL).path(pathToMethod);
        try {
            testResponse = wt.request("application/json").get();
        } catch (Exception e) {
            log.log(LOG_ERROR, "EXCEPTION: ", e);
        }
        return testResponse;
    }

    public Response getPOSTResponse (String pathToMethod, Object input) {
        WebTarget wt = ClientBuilder.newClient().register(new JacksonJaxbJsonProvider()).target(BASE_URL).path(pathToMethod);
        try {
            testResponse = wt.request("application/json").post(Entity.entity(input, MediaType.APPLICATION_JSON));
        } catch (Exception e) {
            log.log(LOG_ERROR, "EXCEPTION: ", e);
        }
        return testResponse;
    }

    @AfterEach
    public void clearTestResponse () {
        testResponse.close();
    }

    @Test
    public void testGetAllOrders () throws Exception {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing UnboundService/GetAllOrders...");
        log.log(LOG_INFO, "=======================");
        Response respGetAllOrders = getGETResponse(DEMO_SERVICE_GET_ALL_ORDERS);

        assertNotNull(respGetAllOrders);
        assertTrue(respGetAllOrders.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));

        List<OrderInfoQuery> output = respGetAllOrders.readEntity(new GenericType<List<OrderInfoQuery>>() {
        });

        assertTrue("shipperNameInOrderInfoQuery0".equals(output.get(0).getShipperName()));
        assertTrue("shipperNameInOrderInfoQuery1".equals(output.get(1).getShipperName()));
    }

    @Test
    public void testGetAllInternationalOrders () throws Exception {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing UnboundService/GetAllInternationalOrders...");
        log.log(LOG_INFO, "=======================");

        Response response = getGETResponse(DEMO_SERVICE_GET_ALL_INTERNATIONAL_ORDERS);

        assertNotNull(response);
        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));

        List<InternationalOrderInfoQuery> output = response.readEntity(new GenericType<List<InternationalOrderInfoQuery>>() {
        });

        assertTrue("shipperNameInInternationalOrderInfoQuery0".equals(output.get(0).getShipperName()));
        assertTrue("shipperNameInInternationalOrderInfoQuery1".equals(output.get(1).getShipperName()));
    }

    @Test
    public void testCreateOrder () throws Exception {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing UnboundService/CreateOrder...");
        log.log(LOG_INFO, "=======================");

        Response respCreateOrder = getPOSTResponse(DEMO_SERVICE_CREATE_ORDER, OrderInfo.orderInfoBuilder().build());

        assertNotNull(respCreateOrder);
        assertTrue(respCreateOrder.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
        assertTrue("shipperNameInNewOrderInfo".equals(new JSONObject(respCreateOrder.readEntity(String.class)).get("shipperName")));
    }

    @Test
    public void testCreateInternationalOrder () throws Exception {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing UnboundService/CreateInternationalOrder...");
        log.log(LOG_INFO, "=======================");

        Response respCreateInternationalOrder = getPOSTResponse(DEMO_SERVICE_CREATE_INTERNATIONAL_ORDER, InternationalOrderInfo.internationalOrderInfoBuilder().build());

        assertNotNull(respCreateInternationalOrder);
        assertTrue(respCreateInternationalOrder.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
        assertTrue("shipperNameInNewInternationalOrderInfo".equals(new JSONObject(respCreateInternationalOrder.readEntity(String.class)).get("shipperName")));
    }

    @Test
    public void testCreateProduct () throws Exception {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing UnboundService/CreateProduct...");
        log.log(LOG_INFO, "=======================");

        Response respCreateProduct = getPOSTResponse(DEMO_SERVICE_CREATE_PRODUCT, ProductInfo.productInfoBuilder().build());

        assertNotNull(respCreateProduct);
        assertTrue(respCreateProduct.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
        assertTrue("productNameInNewProductInfo".equals(new JSONObject(respCreateProduct.readEntity(String.class)).get("productName")));
    }

    @Test
    public void testCreateShipper () throws Exception {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing UnboundService/CreateShipper...");
        log.log(LOG_INFO, "=======================");

        Response respCreateShipper = getPOSTResponse(DEMO_SERVICE_CREATE_SHIPPER, ShipperInfo.shipperInfoBuilder().build());

        assertNotNull(respCreateShipper);
        assertTrue(respCreateShipper.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
        assertTrue("companyNameInNewShipperInfo".equals(new JSONObject(respCreateShipper.readEntity(String.class)).get("companyName")));
    }

    @Test
    public void testCreateCategory () throws Exception {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing UnboundService/CreateCategory...");
        log.log(LOG_INFO, "=======================");

        Response respCreateShipper = getPOSTResponse(DEMO_SERVICE_CREATE_CATEGORY, CategoryInfo.categoryInfoBuilder().build());

        assertNotNull(respCreateShipper);
        assertTrue(respCreateShipper.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
        assertTrue("categoryNameInNewCategoryInfo".equals(new JSONObject(respCreateShipper.readEntity(String.class)).get("categoryName")));
    }

    @Test
    public void testUpdateOrder () throws Exception {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing UnboundService/UpdateOrder...");
        log.log(LOG_INFO, "=======================");

        Response respUpdateOrder = getPOSTResponse(DEMO_SERVICE_UPDATE_ORDER, OrderInfo.orderInfoBuilder().build());

        assertNotNull(respUpdateOrder);
        assertTrue(respUpdateOrder.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
        assertTrue("newShipperNameInNewOrderInfo".equals(new JSONObject(respUpdateOrder.readEntity(String.class)).get("shipperName")));
    }

    @Test
    public void testChangeShipment () throws Exception {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing UnboundService/ChangeShipment...");
        log.log(LOG_INFO, "=======================");

        Response respChangeShipment = getPOSTResponse(DEMO_SERVICE_CHANGE_SHIPMENT, ShipmentChange.shipmentChangeBuilder().build());

        assertNotNull(respChangeShipment);
        assertTrue(respChangeShipment.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
        assertTrue("shipperNameInNewShipmentChange".equals(new JSONObject(respChangeShipment.readEntity(String.class)).get("shipperName")));
    }

    //todo: do something smart
    @Test
    public void testDeleteOrder () throws Exception {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing UnboundServices/deleteOrder...");
        log.log(LOG_INFO, "=======================");

        //wt hivasnal funct intrfc
        waitWebPage(BASE_URL + "/?_wadl");
        WebTarget wt = ClientBuilder.newClient().register(new JacksonJaxbJsonProvider()).target(BASE_URL);
        try {
            testResponse = wt.path(DEMO_SERVICE_DELETE_ORDER)
                    .request("application/json")
                    .post(Entity.json(OrderInfo.class));
        } catch (Exception e) {
            log.log(LOG_ERROR, "EXCEPTION: ", e);
        }

        assertNotNull(testResponse);
        assertTrue(testResponse.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
    }


    @Test
    public void testOrderInfoQueryCategoryGet () throws Exception {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing OrderInfoQuery$categories/ChangeShipment...");
        log.log(LOG_INFO, "=======================");

        Response response = getGETResponse(DEMO_SERVICE_ORDERINFOQUERY_CATEGORIES_GET);

        assertNotNull(response);
        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));

        List<CategoryInfo> output = response.readEntity(new GenericType<List<CategoryInfo>>() {
        });

        assertTrue("categoryName0".equals(output.get(0).getCategoryName()));
        assertTrue("categoryName1".equals(output.get(1).getCategoryName()));
    }

    @Test
    public void testOrderInfoQueryItemsGet () throws Exception {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing OrderInfoQuery$Items/ChangeShipment...");
        log.log(LOG_INFO, "=======================");

        Response response = getGETResponse(DEMO_SERVICE_ORDERINFOQUERY_ITEMS_GET);

        assertNotNull(response);
        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));

        List<OrderItemQuery> output = response.readEntity(new GenericType<List<OrderItemQuery>>() {
        });

        assertTrue("productName0".equals(output.get(0).getProductName()));
        assertTrue("productName1".equals(output.get(1).getProductName()));

    }

    @Test
    public void testProductInfoQueryCategoryGetRange () throws Exception {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing ProductInfoQuery/category/getRange...");
        log.log(LOG_INFO, "=======================");

        Response response = getGETResponse(DEMO_SERVICE_PRODUCTINFOQUERY_CATEGORY_GET_RANGE);

        assertNotNull(response);
        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));

        List<CategoryInfo> output = response.readEntity(new GenericType<List<CategoryInfo>>() {
        });

        assertTrue("categoryName0".equals(output.get(0).getCategoryName()));
        assertTrue("categoryName1".equals(output.get(1).getCategoryName()));
    }

    //todo: do something smart
    @Test
    public void testProductInfoQueryCategoryUnset () throws Exception {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing ProductInfoQuery$category/unset...");
        log.log(LOG_INFO, "=======================");

        waitWebPage(BASE_URL + "/?_wadl");
        WebTarget wt = ClientBuilder.newClient().register(new JacksonJaxbJsonProvider()).target(BASE_URL);
        try {
            testResponse = wt.path(DEMO_SERVICE_PRODUCTINFOQUERY_CATEGORY_UNSET)
                    .request("application/json")
                    .post(Entity.json(ProductInfoQuery$category$Reference.class));
        } catch (Exception e) {
            log.log(LOG_ERROR, "EXCEPTION: ", e);
        }

        assertNotNull(testResponse);
        assertTrue(testResponse.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
    }

    @Test
    public void testOrderInfoQueryItemsSet () throws Exception {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing OrderInfoQuery/items/set...");
        log.log(LOG_INFO, "=======================");

        Response response = getPOSTResponse(DEMO_SERVICE_ORDERINFOQUERY_ITEMS_SET, OrderInfoQuery$items$Reference.orderInfoQuery$items$ReferenceBuilder().build());

        assertNotNull(response);
        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
    }

    @Test
    public void testOrderInfoQueryItemsAddAll () throws Exception {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing OrderInfoQuery/items/addAll...");
        log.log(LOG_INFO, "=======================");

        Response response = getPOSTResponse(DEMO_SERVICE_ORDERINFOQUERY_ITEMS_ADD_ALL, OrderInfoQuery$items$Reference.orderInfoQuery$items$ReferenceBuilder().build());

        assertNotNull(response);
        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
    }

    @Test
    public void testOrderInfoQueryItemsRemoveAll () throws Exception {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "Testing OrderInfoQuery/items/removeAll...");
        log.log(LOG_INFO, "=======================");

        Response response = getPOSTResponse(DEMO_SERVICE_ORDERINFOQUERY_ITEMS_REMOVE_ALL, OrderInfoQuery$items$Reference.orderInfoQuery$items$ReferenceBuilder().build());

        assertNotNull(response);
        assertTrue(response.getStatusInfo().getFamily().equals(Response.Status.Family.SUCCESSFUL));
    }
}