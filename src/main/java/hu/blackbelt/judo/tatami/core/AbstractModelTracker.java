package hu.blackbelt.judo.tatami.core;

/*-
 * #%L
 * Judo :: Tatami :: Core
 * %%
 * Copyright (C) 2018 - 2022 BlackBelt Technology
 * %%
 * This program and the accompanying materials are made available under the
 * terms of the Eclipse Public License 2.0 which is available at
 * http://www.eclipse.org/legal/epl-2.0.
 * 
 * This Source Code may also be made available under the following Secondary
 * Licenses when the conditions for such availability set forth in the Eclipse
 * Public License, v. 2.0 are satisfied: GNU General Public License, version 2
 * with the GNU Classpath Exception which is
 * available at https://www.gnu.org/software/classpath/license.html.
 * 
 * SPDX-License-Identifier: EPL-2.0 OR GPL-2.0 WITH Classpath-exception-2.0
 * #L%
 */

import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.framework.ServiceReference;
import org.osgi.util.tracker.ServiceTracker;
import org.osgi.util.tracker.ServiceTrackerCustomizer;

import java.util.Map;


@Slf4j
public abstract class AbstractModelTracker<T> {

    private final Map<ServiceReference, T> instanceCache = Maps.newConcurrentMap();


    public void bindService(ServiceReference serviceReference, T instance) {
        instanceCache.put(serviceReference, instance);
        log.info("Preparing model for transformation: " + instance);
        install(instance);
    }

    public void unbindService(ServiceReference serviceReference) {
        T instance = instanceCache.get(serviceReference);
        if (instance == null) {
            return;
        }
        instanceCache.remove(serviceReference);
        log.info("Unbind transformation: " + instance);
        uninstall(instance);
    }

    private ModelServiceTracker psmModelServiceTracker;

    @SneakyThrows(InvalidSyntaxException.class)
    public void openTracker(BundleContext bundleContext) {
        psmModelServiceTracker = new ModelServiceTracker(this, bundleContext, getModelClass());
        psmModelServiceTracker.open(true);
    }

    public void closeTracker() {
        psmModelServiceTracker.close();
    }

    public abstract void install(T instance);

    public abstract void uninstall(T instance);

    public abstract Class<T> getModelClass();

    public class ModelServiceTracker extends ServiceTracker<T, T> {

        private AbstractModelTracker abstractModelTracker;

        ModelServiceTracker(AbstractModelTracker abstractModelTracker, BundleContext bundleContext, Class<T> clazz) throws InvalidSyntaxException {
            super(bundleContext, clazz.getName(), (ServiceTrackerCustomizer) null);
            this.abstractModelTracker = abstractModelTracker;
        }

        @Override
        public T addingService(ServiceReference<T> serviceReference) {
            if (serviceReference.isAssignableTo(super.context.getBundle(), getModelClass().getName())) {
                T instance = super.addingService(serviceReference);
                abstractModelTracker.bindService(serviceReference, instance);
                return instance;
            }
            return null;
        }

        @Override
        public void removedService(ServiceReference<T> serviceReference, T service) {
            abstractModelTracker.unbindService(serviceReference);
            super.removedService(serviceReference, service);
        }

        @Override
        public void modifiedService(ServiceReference<T> serviceReference,
                                    T service) {
            super.modifiedService(serviceReference, service);
        }

    }
}
