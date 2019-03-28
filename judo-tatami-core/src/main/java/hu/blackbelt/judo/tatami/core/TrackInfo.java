package hu.blackbelt.judo.tatami.core;

import org.eclipse.emf.ecore.resource.Resource;
import java.util.List;

/**
 * Represents a model conversion event.
 * It contains all important information about transformation, which helps
 * tracking the elements life in the pipeline.
 */
public interface TrackInfo {
    /**
     * The types of source models which contains the required resources.
     * @return the class of source model types which tracked by transformation service.
     */
    List<Class> getSourceModelTypes();

    /**
     * The instances of contains the required resources.
     * @return the instances of source models which tracked by transformation service.
     */
    List<Object> getSourceModels();

    /**
     * Get the source model instance of the given source model type.
     *
     * @param sourceModelType
     * @param <T> The generic type of the selected source model.
     * @return The source model instance
     */
    <T> T getSourceModel(Class<T> sourceModelType);

    /**
     * Get the EMF {@link Resource} instance of the given source model.
     *
     * @param sourceModelType
     * @param <T> The generic type of the selected source model.
     * @return EMF {@link Resource}
     */
    <T> Resource getSourceResource(Class<T> sourceModelType);

    /**
     * Get the target type which contains the required resources. It is the target type of a transformation.
     * @return
     */
    Class getTargetModelType();

    /**
     * Get the target type which contains the required resources. It is the target type of a transformation.
     * @return
     */
    Object getTargetModel();

    /**
     * Get the target instance which is the EMF {@link Resource} instance itself.
     * @return
     */
    Resource getTargetResource();

    /**
     * Get the {@link TrackInfo} specialized type
     * @return
     */
    Class<? extends TrackInfo> getType();

    /**
     * The logical name of the transformation.
     * @return
     */
    String getTrackInfoName();


    /**
     * The model name which was used for the transformation.
     * @return
     */
    String getModelName();

    /**
     * The model version which was used for the transformation.
     * @return
     */
    String getModelVersion();

}
