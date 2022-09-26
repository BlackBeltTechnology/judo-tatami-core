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

import static org.eclipse.emf.common.util.ECollections.asEList;
import static org.eclipse.emf.common.util.ECollections.newBasicEList;

import java.util.List;
import java.util.Map;
import java.util.Objects;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.util.EcoreUtil;
import org.osgi.framework.BundleContext;
import org.osgi.framework.InvalidSyntaxException;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Deactivate;

import com.google.common.collect.ImmutableSet;
import com.google.common.collect.Lists;
import com.google.common.collect.Maps;

import lombok.SneakyThrows;

@Component(service = TransformationTraceService.class)
public class TransformationTraceServiceImpl implements TransformationTraceService {

    private final Map<String, List<TransformationTrace>> modelNameCache = Maps.newConcurrentMap();

    private TransformationTraceTracker transformationTraceTracker;

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
        transformationTraceTracker = new TransformationTraceTracker(this, bundleContext, TransformationTrace.class);
        transformationTraceTracker.open(true);
    }

    public void closeTracker() {
        transformationTraceTracker.close();
    }

    public void add(TransformationTrace instance) {
        if (!modelNameCache.containsKey(instance.getModelName())) {
            modelNameCache.put(instance.getModelName(), Lists.newArrayList());
        }
        List<TransformationTrace> transformationTraceList = modelNameCache.get(instance.getModelName());
        transformationTraceList.add(instance);
    }

    public void remove(TransformationTrace instance) {
        List<TransformationTrace> transformationTraceList = modelNameCache.get(instance.getModelName());
        transformationTraceList.remove(instance);
        if (transformationTraceList.size() == 0) {
            modelNameCache.remove(instance.getModelName());
        }
    }

    @Override
    public TransformationTrace getParentTransformationTraceByInstance(String modelName, EObject targetElement) {
        if (!modelNameCache.containsKey(modelName)) {
            throw new IllegalArgumentException("No model defined: " + modelName);
        }

        // Find target element's model
        EObject sourceRoot = EcoreUtil.getRootContainer(targetElement);

        for (TransformationTrace t : modelNameCache.get(modelName)) {
            for (Resource r : t.getTargetResourceSet().getResources()) {
                //if (r.getContents().contains(sourceRoot)) {
                if (r.getContents().stream().filter(c -> equals(c, sourceRoot)).count() > 0) {
                    return t;
                }
            }
        }
        return null;
    }

    // @Override
    public List<TransformationTrace> getChildTransformationTracesByInstance(String modelName, EObject instance) {
        if (!modelNameCache.containsKey(modelName)) {
            throw new IllegalArgumentException("No model defined: " + modelName);
        }
        // Find target element's model
        EObject sourceRoot = EcoreUtil.getRootContainer(instance);

        List<TransformationTrace> transformationTraceList = Lists.newArrayList();
        for (TransformationTrace t : modelNameCache.get(modelName)) {
            for (Class sourceModelClass : t.getSourceModelTypes()) {
                for (Resource r : t.getSourceResourceSet(sourceModelClass).getResources()) {
//                    if  (r.getContents().contains(sourceRoot)) {
                    if (r.getContents().stream().filter(c -> equals(c, sourceRoot)).count() > 0) {
                        transformationTraceList.add(t);
                    }
                }

            }
        }
        return transformationTraceList;
    }

    @Override
    public EObject getAscendantOfInstanceByModelType(String modelName, Class modelType, EObject instance) {
        if (!modelNameCache.containsKey(modelName)) {
            throw new IllegalArgumentException("No model defined: " + modelName);
        }

        EObject current = instance;
        while (current != null) {
            TransformationTrace constructor = getParentTransformationTraceByInstance(modelName, current);
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
    public Map<TransformationTrace, EObject> getAllAscendantOfInstance(String modelName, EObject instance) {
        if (!modelNameCache.containsKey(modelName)) {
            throw new IllegalArgumentException("No model defined: " + modelName);
        }

        EObject current = instance;
        Map<TransformationTrace, EObject> transformationTraceMap = Maps.newLinkedHashMap();
        while (current != null) {
            TransformationTrace constructor = getParentTransformationTraceByInstance(modelName, current);
            if (constructor != null) {
                current = getTraceSourceElementObjectByTargetElement(constructor, current);
                transformationTraceMap.put(constructor, current);
            } else {
                return transformationTraceMap;
            }
        }
        return null;
    }

    @Override
    public List<TransformationTrace> getTransformationTraceAscendantsByInstance(String modelName, EObject instance) {
        if (!modelNameCache.containsKey(modelName)) {
            throw new IllegalArgumentException("No model defined: " + modelName);
        }

        return Lists.newArrayList(getAllAscendantOfInstance(modelName, instance).keySet());
    }


    @Override
    public EObject getRootAscendantOfInstance(String modelName, EObject targetElement) {
        if (!modelNameCache.containsKey(modelName)) {
            throw new IllegalArgumentException("No model defined: " + modelName);
        }

        return getAscendantOfInstanceByModelType(modelName, null, targetElement);
    }

    @Override
    public Map<TransformationTrace, List<EObject>> getAllDescendantOfInstance(String modelName, EObject instance) {
        if (!modelNameCache.containsKey(modelName)) {
            throw new IllegalArgumentException("No model defined: " + modelName);
        }

        List<EObject> currentList = asEList(instance); // ImmutableList.of(instance);
        Map<TransformationTrace, List<EObject>> transformationTraceMap = Maps.newLinkedHashMap();
        while (currentList.size() > 0) {
            List<EObject> childs = newBasicEList(); //Lists.newArrayList();
            for (EObject current : currentList) {
                List<TransformationTrace> transformationTraceList = getChildTransformationTracesByInstance(modelName, current);
                for (TransformationTrace tr : transformationTraceList) {
                    if (!transformationTraceMap.containsKey(tr)) {
                        transformationTraceMap.put(tr, Lists.newArrayList());
                    }
                    List transformationTraceInstanceList = transformationTraceMap.get(tr);
                    List elements = getTraceTargetElementObjectBySourceElement(tr, current);
                    if (elements != null) {
                        transformationTraceInstanceList.addAll(elements);
                        childs.addAll(elements);
                    }
                }
            }
            currentList = childs;
        }

        // Remove all elements which have empty list
        for (TransformationTrace k : ImmutableSet.copyOf(transformationTraceMap.keySet())) {
            if (transformationTraceMap.get(k).size() == 0) {
                transformationTraceMap.remove(k);
            }
        }
        return transformationTraceMap;
    }

    @Override
    public List<EObject> getDescendantOfInstanceByModelType(String modelName, Class modelType, EObject instance) {
        if (!modelNameCache.containsKey(modelName)) {
            throw new IllegalArgumentException("No model defined: " + modelName);
        }

        List<EObject> currentList = asEList(instance); // ImmutableList.of(instance);
        List<EObject> ret = newBasicEList(); // Lists.newArrayList();
        while (currentList.size() > 0) {
            List<EObject> childs = newBasicEList(); // Lists.newArrayList();
            for (EObject current : currentList) {
                List<TransformationTrace> transformationTraceList = getChildTransformationTracesByInstance(modelName, current);
                for (TransformationTrace tr : transformationTraceList) {
                    List<EObject> elements = getTraceTargetElementObjectBySourceElement(tr, current);
                    if (elements != null) {
                        if (tr.getTargetModelType().equals(modelType)) {
                            ret.addAll(elements);
                        }
                        childs.addAll(elements);
                    }
                }
            }
            currentList = childs;
        }
        return ret;
    }

    private EObject getTraceSourceElementObjectByTargetElement(TransformationTrace constructor, EObject targetElement) {
        for (Map.Entry<EObject, List<EObject>> e : constructor.getTransformationTrace().entrySet()) {
            for (EObject eo : e.getValue()) {
//                if (eo.equals(targetElement) != EcoreUtil.equals(eo, targetElement)) {
//                    log.error("EEEEE");
//                }
//                if (eo.equals(targetElement)) {
                if (equals(eo, targetElement)) {
                    return e.getKey();
                }
            }
        }
        return null;
    }

    private List<EObject> getTraceTargetElementObjectBySourceElement(TransformationTrace constructor, EObject sourceElement) {
        return constructor.getTransformationTrace().get(sourceElement);
        //return constructor.getTransformationTrace().get(
        //        constructor.getTransformationTrace().keySet().stream().filter(se -> EcoreUtil.equals(sourceElement, se)).findFirst().get());
    }

    public static boolean equals(EObject o1, EObject o2) {
        final Resource resource1 = o1.eResource();
        final Resource resource2 = o2.eResource();

        if (resource1 != null && resource2 != null && Objects.equals(resource1.getURI(), resource2.getURI())) {
            final String fragment1 = resource1.getURIFragment(o1);
            final String fragment2 = resource2.getURIFragment(o2);

            if (fragment1 != null && fragment2 != null) {
                return Objects.equals(fragment1, fragment2);
            } else {
                return EcoreUtil.equals(o1, o2);
            }
        } else {
            return EcoreUtil.equals(o1, o2);
        }
    }
}
