package hu.blackbelt.judo.tatami.itest;

import com.fasterxml.jackson.jaxrs.json.JacksonJaxbJsonProvider;
import com.google.common.collect.ImmutableMap;
import hu.blackbelt.judo.meta.asm.runtime.AsmUtils;
import hu.blackbelt.judo.meta.rdbms.RdbmsIdentifierField;
import hu.blackbelt.judo.meta.rdbms.RdbmsTable;
import hu.blackbelt.judo.meta.rdbms.RdbmsValueField;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.tatami.core.Dispatcher;
import hu.blackbelt.judo.tatami.core.TransformationTrace;
import org.eclipse.emf.ecore.EClass;
import org.eclipse.emf.ecore.EObject;
import org.junit.Test;
import org.junit.experimental.categories.Category;
import org.junit.runner.RunWith;
import org.ops4j.pax.exam.Option;
import org.ops4j.pax.exam.junit.PaxExam;
import org.ops4j.pax.exam.spi.reactors.ExamReactorStrategy;
import org.ops4j.pax.exam.spi.reactors.PerClass;
import org.osgi.framework.Constants;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;

import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import javax.ws.rs.core.Response;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.stream.Collectors;

import static hu.blackbelt.judo.tatami.itest.TestUtility.*;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.ops4j.pax.exam.CoreOptions.provision;
import static org.ops4j.pax.tinybundles.core.TinyBundles.bundle;
import static org.ops4j.pax.tinybundles.core.TinyBundles.withBnd;
import static org.osgi.service.log.LogService.LOG_ERROR;
import static org.osgi.service.log.LogService.LOG_INFO;

@Category(ESMTestSuite.class)
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class NorthwindTatamiESMTransformationPipelineITest extends TatamiESMTransformationPipelineITest {

    private static final String BASE_URL = "http://localhost:8181/cxf/northwind/InternalAP";
    private static final String NORTHWIND_ENTITIES_ORDER = "northwind.entities.Order";
    private static final String NORTHWIND = "northwind-esm";
//    private static final String NORTHWIND_SERVICE_GET_ALL_ORDERS = "/northwind/service/getAllOrders";
//    private static final String NORTHWIND_SERVICE_GET_ALL_INTERNATIONAL_ORDERS = "/northwind/service/getAllInternationalOrders";

    @Override
    public Option getProvisonModelBundle() throws FileNotFoundException {
        return provision(
                getEsmModelBundle()
        );
    }

    private InputStream getEsmModelBundle() throws FileNotFoundException {
        return bundle()
                .add("model/" + NORTHWIND + ".judo-meta-esm",
                        new FileInputStream(new File(testTargetDir(getClass()).getAbsolutePath(), "northwind-esm.model")))
                .set(Constants.BUNDLE_MANIFESTVERSION, "2")
                .set(Constants.BUNDLE_SYMBOLICNAME, NORTHWIND + "-model")
                //set( Constants.IMPORT_PACKAGE, "meta/psm;version=\"" + getConfiguration(META_PSM_IMPORT_RANGE) +"\"")
                .set("Esm-Models", "file=model/" + NORTHWIND + ".judo-meta-esm;version=1.0.0;name=" + NORTHWIND + ";checksum=notset;meta-version-range=\"[1.0.0,2)\"")
                .build(withBnd());
    }

    @Override
    public String getAppName() {
        return NORTHWIND;
    }

    @Test
    public void saveModels() throws Exception {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "== RUNNING TEST SAVE MODELS");
        log.log(LOG_INFO, "==============================================");

        super.saveModels();

        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "== STOPPING TEST SAVE MODELS");
        log.log(LOG_INFO, "==============================================");
    }

    @Test
    public void testTrace() throws InvalidSyntaxException {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "== RUNNING TEST TRACE METHOD");
        log.log(LOG_INFO, "==============================================");

        Collection<ServiceReference<TransformationTrace>> transformationTraces = bundleContext.getServiceReferences(TransformationTrace.class, null);

        assertThat(transformationTraces.stream().map(r -> bundleContext.getService(r).getTransformationTraceName()).collect(Collectors.toList()),
                containsInAnyOrder("esm2psm", "asm2openapi", "asm2rdbms", "psm2measure", "psm2asm"));


        AsmUtils asmUtils = new AsmUtils(asmModel.getResourceSet());
        // Get Order entity
        Optional<EClass> orderClass = asmUtils.getClassByFQName(NORTHWIND_ENTITIES_ORDER);

        List<EObject> orderRdbmsObjectList = transformationTraceService.getDescendantOfInstanceByModelType(NORTHWIND, RdbmsModel.class, orderClass.get());

        assertThat(orderRdbmsObjectList, hasSize(3));
        assertThat(orderRdbmsObjectList, hasItems(instanceOf(RdbmsTable.class), instanceOf(RdbmsIdentifierField.class), instanceOf(RdbmsValueField.class)));
        assertThat(orderRdbmsObjectList.stream()
                .filter(RdbmsTable.class::isInstance)
                .map(RdbmsTable.class::cast)
                .findFirst().get().getSqlName(), equalTo("T_ENTTS_ORDER"));

        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "== STOPPING TEST TRACE METHOD");
        log.log(LOG_INFO, "==============================================");

    }

    @Test
    public void testRest() throws Exception {
        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "== RUNNING TEST REST METHOD");
        log.log(LOG_INFO, "==============================================");

        Dispatcher dispatcher = new Dispatcher() {
            @Override
            public Map<String, Object> callOperation(String operation, Map<String, Object> payload) {
                log.log(LOG_INFO, "Dispatcher called - " + operation + " Payload: " + payload.toString());
                return ImmutableMap.<String, Object>of();
            }
        };
        bundleContext.registerService(Dispatcher.class, dispatcher, null);

        waitWebPage(BASE_URL + "/?_wadl");

        assertBundleStarted(bundleContext, NORTHWIND + "-asm2jaxrsapi");

//        assertNotNull(getResponse(NORTHWIND_SERVICE_GET_ALL_ORDERS));
//        assertNotNull(getResponse(NORTHWIND_SERVICE_GET_ALL_INTERNATIONAL_ORDERS));

        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "== STOPPING TEST REST METHOD");
        log.log(LOG_INFO, "==============================================");
    }

    private Response getResponse(String path) throws Exception {
        WebTarget wt = ClientBuilder.newClient().register(new JacksonJaxbJsonProvider()).target(BASE_URL);
        Response response = null;
        try {
            response = wt.path(path)
                    .request("application/json")
                    .get();
        } catch (Exception e) {
            log.log(LOG_ERROR, "EXCEPTION: ", e);
        }
        return response;
    }
}