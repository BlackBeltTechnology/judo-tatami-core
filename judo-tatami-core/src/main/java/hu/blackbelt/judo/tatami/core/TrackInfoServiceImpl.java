package hu.blackbelt.judo.tatami.core;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
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
    public TrackInfo getParentTrackInfoByInstance(String modelName, EObject targetElement) {
        // Find target element's model
        EObject sourceRoot = EcoreUtil.getRootContainer(targetElement);

        for (TrackInfo t : modelNameCache.get(modelName)) {
            for (Resource r : t.getTargetResourceSet().getResources()) {
                if  (r.getContents().contains(sourceRoot)) {
                    return t;
                }
            }
        }
        return null;
    }

    // @Override
    public List<TrackInfo> getChildTrackInfosByInstance(String modelName, EObject instance) {
        // Find target element's model
        EObject sourceRoot = EcoreUtil.getRootContainer(instance);

        List<TrackInfo> trackInfoList = Lists.newArrayList();
        for (TrackInfo t : modelNameCache.get(modelName)) {
            for (Class sourceModelClass : t.getSourceModelTypes()) {
                for (Resource r : t.getSourceResourceSet(sourceModelClass).getResources()) {
                    if  (r.getContents().contains(sourceRoot)) {
                        trackInfoList.add(t);
                    }
                }

            }
        }
        return trackInfoList;
    }

    @Override
    public EObject getAscendantOfInstanceByModelType(String modelName, Class modelType, EObject instance) {
        EObject current = instance;
        while (current != null) {
            TrackInfo constructor = getParentTrackInfoByInstance(modelName, current);
            if (constructor != null) {
                if (constructor.getTargetModelType().equals(modelType)) {
                    return current;
                }
                current = getTraceSourceElementObjectByTargetElement(constructor, current);
            } else {
                if (modelType == null) {
                    return current;
                } else {
                    return null;
                }
            }
        }
        return null;
    }

    @Override
    public Map<TrackInfo, EObject> getAllAscendantOfInstance(String modelName, EObject instance) {
        EObject current = instance;
        Map<TrackInfo, EObject> trackInfoMap = Maps.newLinkedHashMap();
        while (current != null) {
            TrackInfo constructor = getParentTrackInfoByInstance(modelName, current);
            if (constructor != null) {
                current = getTraceSourceElementObjectByTargetElement(constructor, current);
                trackInfoMap.put(constructor, current);
            } else {
                return trackInfoMap;
            }
        }
        return null;
    }

    @Override
    public List<TrackInfo> getTrackInfoAscendantsByInstance(String modelName, EObject instance) {
        return Lists.newArrayList(getAllAscendantOfInstance(modelName, instance).keySet());
    }


    @Override
    public EObject getRootAscendantOfInstance(String modelName, EObject targetElement) {
        return getAscendantOfInstanceByModelType(modelName, null, targetElement);
    }

    @Override
    public Map<TrackInfo, List<EObject>> getAllDescendantOfInstance(String modelName, EObject instance) {
        List<EObject> currentList = ImmutableList.of(instance);
        Map<TrackInfo, List<EObject>> trackInfoMap = Maps.newLinkedHashMap();
        while (currentList.size() > 0) {
            List<EObject> childs = Lists.newArrayList();
            for (EObject current : currentList) {
                List<TrackInfo> trackInfoList = getChildTrackInfosByInstance(modelName, current);
                for (TrackInfo tr : trackInfoList) {
                    if (!trackInfoMap.containsKey(tr)) {
                        trackInfoMap.put(tr, Lists.newArrayList());
                    }
                    List trackInfoInstanceList = trackInfoMap.get(tr);
                    List elements = getTraceTargetElementObjectBySourceElement(tr, current);
                    if (elements != null) {
                        trackInfoInstanceList.addAll(elements);
                        childs.addAll(elements);
                    }
                }
            }
            currentList = childs;
        }

        // Remove all elements which have empty list
        for (TrackInfo k : ImmutableSet.copyOf(trackInfoMap.keySet())) {
            if (trackInfoMap.get(k).size() == 0) {
                trackInfoMap.remove(k);
            }
        }
        return trackInfoMap;
    }

    @Override
    public List<EObject> getDescendantOfInstanceByModelType(String modelName, Class modelType, EObject instance) {
        List<EObject> currentList = ImmutableList.of(instance);
        while (currentList.size() > 0) {
            List<EObject> childs = Lists.newArrayList();
            for (EObject current : currentList) {
                List<TrackInfo> trackInfoList = getChildTrackInfosByInstance(modelName, current);
                for (TrackInfo tr : trackInfoList) {
                    List<EObject> elements = getTraceTargetElementObjectBySourceElement(tr, current);
                    if (elements != null) {
                        if (tr.getTargetModelType().equals(modelType)) {
                            return elements;
                        }
                        childs.addAll(elements);
                    }
                }
            }
            currentList = childs;
        }
        return null;
    }

    private EObject getTraceSourceElementObjectByTargetElement(TrackInfo constructor, EObject targetElement) {
        for (Map.Entry<EObject, List<EObject>> e : constructor.getTransformationTrace().entrySet()) {
            for (EObject eo : e.getValue()) {
                if (eo.equals(targetElement)) {
                    return e.getKey();
                }
            }
        }
        return null;
    }

    private List<EObject> getTraceTargetElementObjectBySourceElement(TrackInfo constructor, EObject sourceElement) {
        return constructor.getTransformationTrace().get(sourceElement);
    }

}
