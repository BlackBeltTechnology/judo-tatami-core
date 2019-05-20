package hu.blackbelt.judo.tatami.core;

import org.eclipse.emf.ecore.EObject;

import java.util.List;
import java.util.Map;

/**
 * The API represents the tracing of model conversion pipeline.
 *
 * The basic rules are:
 *    - Only one root model is presented. All of elem extension can be made via Hooks. Means the models
 *    cannot be composited from multiple sources.
 */
public interface TransformationTraceService {

    /**
     * Install a {@link TransformationTrace} instance.
     *
     * @param instance
     */
    void add(TransformationTrace instance);

    /**
     * Install a {@link TransformationTrace} instance.
     *
     * @param instance
     */
    void remove(TransformationTrace instance);

    /**
     * Get the TransformationTrace of the given element which responsible for the creation of EObject.
     *
     * @param modelName
     * @param targetElement
     * @return the {@link TransformationTrace} or null when the given element created directly (without trace info)
     */
    TransformationTrace getParentTransformationTraceByInstance(String modelName, EObject targetElement);


    /**
     * Get the original of EObject of the given EObject which is repsonsable of the creation of current element - the
     * original one is the first ancestor.
     *
     * @param modelName
     * @param targetElement
     * @return the first ascendant (ancesor) instance or null when the current element is first in chain.
     */
    EObject getRootAscendantOfInstance(String modelName, EObject targetElement);


    /**
     * Get the source of EObject on the given sourceModelType which is repsonsable of the creation of current element - the
     * one is the ascendant (ancesor) of given source model.
     *
     * @param modelName
     * @param sourceModelType the source model contains the source element.
     * @param targetElement
     * @return the ascendant (ancesor) instance or null when the sourceModelType is not on the creation chain.
     */
    EObject getAscendantOfInstanceByModelType(String modelName, Class sourceModelType, EObject targetElement);


    /**
     * Get all ascendant (ancesor) stack map by @{@link TransformationTrace} of the given target element.
     *
     * @param modelName
     * @param targetElement
     * @return
     */
    Map<TransformationTrace, EObject> getAllAscendantOfInstance(String modelName, EObject targetElement);

    /**
     * Get all ascendant {@link TransformationTrace} of the given element.
     * @param modelName
     * @param instance
     * @return
     */
    List<TransformationTrace> getTransformationTraceAscendantsByInstance(String modelName, EObject instance);


    /**
     * Get all descendant stack map by {@link TransformationTrace} of the given target element.  It traverse over all the trace
     * maps and collect elements by {@link TransformationTrace}.
     *
     * @param modelName
     * @param targetElement
     * @return
     */
    Map<TransformationTrace, List<EObject>> getAllDescendantOfInstance(String modelName, EObject targetElement);


    /**
     * Get all decendant insrances of the given model type and  instances.
     * @param modelName
     * @param modelType
     * @param instance
     * @return
     */
    List<EObject> getDescendantOfInstanceByModelType(String modelName, Class modelType, EObject instance);

}