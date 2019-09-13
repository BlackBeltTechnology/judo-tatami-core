package hu.blackbelt.judo.tatami.workflow.osgi;

import com.google.common.collect.Maps;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.liquibase.runtime.LiquibaseModel;
import hu.blackbelt.judo.meta.measure.runtime.MeasureModel;
import hu.blackbelt.judo.meta.openapi.runtime.OpenapiModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.meta.rdbms.runtime.RdbmsModel;
import hu.blackbelt.judo.tatami.asm2openapi.Asm2OpenAPITransformationTrace;
import hu.blackbelt.judo.tatami.asm2rdbms.Asm2RdbmsTransformationTrace;
import hu.blackbelt.judo.tatami.core.TransformationTrace;
import hu.blackbelt.judo.tatami.core.workflow.work.TransformationContext;
import hu.blackbelt.judo.tatami.esm2psm.Esm2PsmTransformationTrace;
import hu.blackbelt.judo.tatami.psm2asm.Psm2AsmTransformationTrace;
import hu.blackbelt.judo.tatami.psm2measure.Psm2MeasureTransformationTrace;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.Bundle;
import org.osgi.framework.BundleContext;
import org.osgi.framework.BundleException;
import org.osgi.framework.ServiceRegistration;
import org.osgi.service.component.annotations.Component;

import java.io.IOException;
import java.io.InputStream;
import java.util.Dictionary;
import java.util.Hashtable;
import java.util.Map;

import static com.google.common.base.Preconditions.checkState;
import static hu.blackbelt.judo.tatami.core.ThrowingConsumer.throwingConsumerWrapper;

/**
 * This class add abstract methods to handle the OSGi lifecycle of transformed models / traces / bundles
 */
@Slf4j
public abstract class AbstractTransformationContextRegistrationService implements TransformationContextRegistrationService {

    Map<Object, ServiceRegistration> serviceRegistrationMap = Maps.newConcurrentMap();
    Map<Object, Bundle> bundleRegistrationMap = Maps.newConcurrentMap();

    BundleContext bundleContext;

    public void activate(BundleContext bundleContext) {
        this.bundleContext = bundleContext;
    }

    public <T> void registerModel(T o, Dictionary<String, ?> props) {
        log.info("\u001B[33m {}\u001B[0mRegistering model: " + o);
        ServiceRegistration<?> modelServiceRegistration =
                bundleContext.registerService((Class<T>) o.getClass(), o, props);
        serviceRegistrationMap.put(o, modelServiceRegistration);
    }

    public <T> void unregisterModel(T o) {
        log.info("\u001B[33m {}\u001B[0mUnregistering model: " + o);
        checkState(serviceRegistrationMap.containsKey(o), "The model is not registered");
        serviceRegistrationMap.get(o).unregister();
    }

    public void registerTrace(TransformationTrace trace) {
        log.info("\u001B[33m {}\u001B[0mRegistering trace model: " + trace);
        ServiceRegistration<?> modelServiceRegistration =
                bundleContext.registerService(TransformationTrace.class, trace, new Hashtable<>());
        serviceRegistrationMap.put(trace, modelServiceRegistration);
    }

    public void unregisterTrace(TransformationTrace trace) {
        log.info("\u001B[33m {}\u001B[0mUnregistering trace model: " + trace);
        checkState(serviceRegistrationMap.containsKey(trace), "The trace model is not registered");
        serviceRegistrationMap.get(trace).unregister();
    }

    public void registerInputStreamAsBundle(InputStream is) throws BundleException, IOException {
        log.info("\u001B[33m {}\u001B[0mInstalling stream as bundle" + is.toString()+ " - " + is.available());
        Bundle installedBundle = bundleContext.installBundle(this.getClass().getName(), is);
        bundleRegistrationMap.put(is, installedBundle);
    }

    public void ungisterInputStream(InputStream is) throws BundleException {
        log.info("\u001B[33m {}\u001B[0mUnregistering trace model: " + is);
        checkState(bundleRegistrationMap.containsKey(is), "The input stream is not registered");
        bundleRegistrationMap.get(is).uninstall();
    }

    @Override
    public abstract void registerTramsformationContext(TransformationContext transformationContext);

    @Override
    public abstract void unregisterTramsformationContext(TransformationContext transformationContext);
}
