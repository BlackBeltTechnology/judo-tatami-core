package hu.blackbelt.judo.tatami.core;

import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import java.util.List;
import java.util.Map;

@Slf4j
@Component(service = TrackInfoService.class)
public class TrackInfoServiceImpl implements TrackInfoService {

    private final Map<String, List<TrackInfo>> modelNameCache = Maps.newConcurrentMap();

    private final Map<String, List<TrackInfoTreeElement>> dependencyGraph = Maps.newConcurrentMap();


    private TrackInfoTracker trackInfoTracker;

    @Activate
    public void activate(BundleContext bundleContext) {
        openTracker(bundleContext);
    }

    @Deactivate
    public void deactivate() {
        closeTracker();
    }

    @SneakyThrows(InvalidSyntaxException.class)
    public void openTracker(BundleContext bundleContext) {
        trackInfoTracker = new TrackInfoTracker(this, bundleContext, TrackInfo.class);
        trackInfoTracker.open(true);
    }

    public void closeTracker() {
        trackInfoTracker.close();
    }

    public void add(TrackInfo instance) {
        if (!modelNameCache.containsKey(instance.getModelName())) {
            modelNameCache.put(instance.getModelName(), Lists.newArrayList());
        }
        List<TrackInfo> trackInfoList = modelNameCache.get(instance.getModelName());
        trackInfoList.add(instance);
    }

    public void remove(TrackInfo instance) {
        List<TrackInfo> trackInfoList = modelNameCache.get(instance.getModelName());
        trackInfoList.remove(instance);
        if (trackInfoList.size() == 0) {
            modelNameCache.remove(instance.getModelName());
        }
    }

    @Override
    public <T> T getSourceModel(String modelName, Class<T> sourceModelType) {
        for (TrackInfo t : modelNameCache.get(modelName)) {
            if (t.getSourceModelTypes().contains(sourceModelType)) {
                return t.getSourceModel(sourceModelType);
            }
        }
        throw new IllegalArgumentException("Source model type not found " + sourceModelType.getTypeName() + " on " + modelName);
    }

    @Override
    public <T> Resource getSourceModelResource(String modelName, Class<T> sourceModelType) {
        for (TrackInfo t : modelNameCache.get(modelName)) {
            if (t.getSourceModelTypes().contains(sourceModelType)) {
                return t.getSourceResource(sourceModelType);
            }
        }
        throw new IllegalArgumentException("Source model type not found " + sourceModelType.getTypeName() + " on " + modelName);
    }

    @Override
    public Class getOriginalSourceModelType(String modelName) {
        return null;
    }

    @Override
    public Class getOriginalSourceModelType(String modelName, EObject targetElement) {
        return null;
    }

    @Override
    public <T> EObject getSourceModelElement(String modelName, Class<T> sourceModelType, EObject targetElement) {
        return null;
    }

    @Override
    public EObject getOriginalModelElement(String modelName, EObject targetElement) {
        return null;
    }


    private void buildDependencyGraph(String modelName) {
        List<TrackInfoTreeElement> elements = Lists.newArrayList();

        for (TrackInfo tr : modelNameCache.get(modelName)) {
            elements.add(new TrackInfoTreeElement(tr));
        }
        // Get parent representations
        for (TrackInfoTreeElement ts : elements) {
            for (Class cl : ts.getDelegatee().getSourceModelTypes()) {
                getTrackInfoTreeElementForTarget(elements, cl).addParent(ts);
            }
        }
    }

    private TrackInfoTreeElement getTrackInfoTreeElementForTarget(List<TrackInfoTreeElement> elements, Class target) {
        for (TrackInfoTreeElement tt : elements) {
            if (tt.getDelegatee().getTargetModelType().equals(target)) {
                return tt;
            }
        }
        throw new RuntimeException("There is no TrackInfo for model type: " + target.getName());
    }
}
