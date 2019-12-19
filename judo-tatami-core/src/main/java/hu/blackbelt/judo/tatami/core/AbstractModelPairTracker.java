package hu.blackbelt.judo.tatami.core;

import com.google.common.collect.Maps;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.osgi.framework.BundleContext;

import java.util.Map;
import java.util.function.Function;


@Slf4j
public abstract class AbstractModelPairTracker<T1, T2> {

    @Getter
    @Setter
    class Pair {
        String name;
        T1 t1;
        T2 t2;
    }

    Map<String, Pair> instances = Maps.newConcurrentMap();
    Pair getPair(String name) {
        Pair pair;
        if (!instances.containsKey(name)) {
            instances.put(name, new Pair());
        }
        return instances.get(name);
    }

    void updatePair(String name) {
        if (instances.containsKey(name)) {
            Pair pair = instances.put(name, new Pair());
            if (pair.getT1() == null && pair.getT2() == null) {
                instances.remove(name);
            }
        }
    }

    AbstractModelTracker<T1> t1Tracker = new AbstractModelTracker<T1>() {
        @Override
        public void install(T1 instance) {
            Pair pair = getPair(getModel1NameExtractorFunction().apply(instance));
            pair.setT1(instance);
            if (pair.getT2() != null) {
                AbstractModelPairTracker.this.install(pair.getT1(), pair.getT2());
            }
        }

        @Override
        public void uninstall(T1 instance) {
            Pair pair = getPair(getModel1NameExtractorFunction().apply(instance));
            if (pair.getT2() != null) {
                AbstractModelPairTracker.this.uninstall(pair.getT1(), pair.getT2());
            }
            pair.setT1(null);
            updatePair(getModel1NameExtractorFunction().apply(instance));
        }

        @Override
        public Class<T1> getModelClass() {
            return getModelClass1();
        }
    };


    AbstractModelTracker<T2> t2Tracker = new AbstractModelTracker<T2>() {
        @Override
        public void install(T2 instance) {
            Pair pair = getPair(getModel2NameExtractorFunction().apply(instance));
            pair.setT2(instance);
        }

        @Override
        public void uninstall(T2 instance) {
            Pair pair = getPair(getModel2NameExtractorFunction().apply(instance));
            if (pair.getT1() != null) {
                AbstractModelPairTracker.this.uninstall(pair.getT1(), pair.getT2());
            }
            pair.setT2(null);
            updatePair(getModel2NameExtractorFunction().apply(instance));
        }

        @Override
        public Class<T2> getModelClass() {
            return getModelClass2();
        }
    };

    public void openTracker(BundleContext bundleContext) {
        t1Tracker.openTracker(bundleContext);
        t2Tracker.openTracker(bundleContext);
    }

    public void closeTracker() {
        t2Tracker.closeTracker();
        t2Tracker.closeTracker();
    }

    public abstract void install(T1 t1, T2 t2);

    public abstract void uninstall(T1 t1, T2 t2);

    public abstract Class<T1> getModelClass1();

    public abstract Class<T2> getModelClass2();

    public abstract Function<T1, String> getModel1NameExtractorFunction();

    public abstract Function<T2, String> getModel2NameExtractorFunction();

}
