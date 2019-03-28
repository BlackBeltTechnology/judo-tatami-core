package hu.blackbelt.judo.tatami.core;

import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;

/**
 * The API represents the tracing of model conversion pipeline.
 */
public interface TrackInfoService {

    /**
     * Install a {@link TrackInfo} instance.
     * @param instance
     */
    void add(TrackInfo instance);

    /**
     * Install a {@link TrackInfo} instance.
     * @param instance
     */
    void remove(TrackInfo instance);

    /**
     * Get the source model itself from the given type.
     * @param modelName
     * @param sourceModelType
     * @param <T>
     * @return
     */
    <T> T getSourceModel(String modelName, Class<T> sourceModelType);

    /**
     * Get the source model contents from the given type.
     * @param modelName
     * @param sourceModelType
     * @param <T>
     * @return
     */
    <T> Resource getSourceModelResource(String modelName, Class<T> sourceModelType);


    /**
     * Get the entry point model type of the given model instance.
     * @param modelName
     * @return
     */
    Class getOriginalSourceModelType(String modelName);

    /**
     * Get the original model type of the given element. That the first occurence of element in a pipeline.
     * @param modelName
     * @param targetElement
     * @return
     */
    Class getOriginalSourceModelType(String modelName, EObject targetElement);

    /**
     * Get the EMF element of the given model type..
     * @param modelName
     * @param sourceModelType
     * @param targetElement
     * @param <T>
     * @return
     */
    <T> EObject getSourceModelElement(String modelName, Class<T> sourceModelType, EObject targetElement);

    /**
     * Get the original EMF element of the given element. That the first occurence of element in a pipeline.
     * @param modelName
     * @param targetElement
     * @return
     */
    EObject getOriginalModelElement(String modelName, EObject targetElement);
}
