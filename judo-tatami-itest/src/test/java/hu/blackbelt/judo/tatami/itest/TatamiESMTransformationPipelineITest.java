package hu.blackbelt.judo.tatami.itest;

import hu.blackbelt.judo.meta.asm.runtime.AsmUtils;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.rdbms.RdbmsTable;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
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

import javax.inject.Inject;
import java.io.*;
import java.util.Collection;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import static hu.blackbelt.judo.tatami.itest.TestUtility.testTargetDir;
import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.*;
import static org.ops4j.pax.exam.CoreOptions.provision;
import static org.ops4j.pax.tinybundles.core.TinyBundles.bundle;
import static org.ops4j.pax.tinybundles.core.TinyBundles.withBnd;
import static org.osgi.service.log.LogService.LOG_INFO;

@Category(ESMTestSuite.class)
@RunWith(PaxExam.class)
@ExamReactorStrategy(PerClass.class)
public class TatamiESMTransformationPipelineITest extends TatamiPSMTransformationPipelineITest {

    @Inject
    protected EsmModel esmModel;

    public Option getProvisonModelBundle() throws FileNotFoundException {
        return provision(
                testEsmModelBundle()
        );
    }

    public InputStream testEsmModelBundle() throws FileNotFoundException {
        return bundle()
                .add( "model/" + DEMO + ".judo-meta-esm",
                        new FileInputStream(new File(testTargetDir(getClass()).getAbsolutePath(),  "northwind-esm.model")))
                .set( Constants.BUNDLE_MANIFESTVERSION, "2")
                .set( Constants.BUNDLE_SYMBOLICNAME, DEMO + "-esm" )
                //set( Constants.IMPORT_PACKAGE, "meta/psm;version=\"" + getConfiguration(META_PSM_IMPORT_RANGE) +"\"")
                .set( "Esm-Models", "file=model/" + DEMO + ".judo-meta-esm;version=1.0.0;name=" + DEMO + ";checksum=notset;meta-version-range=\"[1.0.0,2)\"")
                .build( withBnd());
    }


    @Test
    public void saveModels() throws InvalidSyntaxException, IOException {
        super.saveModels();
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
        Optional<EClass> orderClass = asmUtils.getClassByFQName(DEMO_ENTITIES_ORDER);

        List<EObject> orderRdbmsObjectList = transformationTraceService.getDescendantOfInstanceByModelType(DEMO, RdbmsModel.class, orderClass.get());

        assertThat(orderRdbmsObjectList, hasSize(1));
        assertThat(orderRdbmsObjectList.get(0), is(instanceOf(RdbmsTable.class)));
        assertThat(((RdbmsTable) orderRdbmsObjectList.get(0)).getSqlName(), equalTo("T_ENTTS_ORDER"));

        log.log(LOG_INFO, "==============================================");
        log.log(LOG_INFO, "== STOPPING TEST TRACE METHOD");
        log.log(LOG_INFO, "==============================================");

    }

    @Test
    public void testRest() throws Exception {
        super.testRest();
    }


}