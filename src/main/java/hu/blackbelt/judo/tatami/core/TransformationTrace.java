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

import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.util.List;
import java.util.Map;

/**
 * Represents a model conversion event.
 * It contains all important information about transformation, which helps
 * tracking the elements life in the pipeline.
 */
public interface TransformationTrace {
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
     * @return The source model instance or null when instance of the given type does not exist
     */
    <T> T getSourceModel(Class<T> sourceModelType);

    /**
     * Get the EMF {@link ResourceSet} instance of the given source model.
     *
     * @param sourceModelType
     * @param <T> The generic type of the selected source model.
     * @return EMF {@link ResourceSet} or null when instance of the given type does not exist
     */
    <T> ResourceSet getSourceResourceSet(Class<T> sourceModelType);


    /**
     * Get the EMF {@link URI} of the given source model.
     *
     * @param sourceModelType
     * @param <T> The generic type of the selected source model.
     * @return EMF {@link URI} or null when instance of the given type does not exist
     */
    <T> URI getSourceURI(Class<T> sourceModelType);

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
    ResourceSet getTargetResourceSet();

    /**
     * Get the target instance which is the EMF {@link URI} itself.
     * @return
     */
    URI getTargetURI();

    /**
     * Get the {@link TransformationTrace} specialized type
     * @return
     */
    Class<? extends TransformationTrace> getType();

    /**
     * The logical name of the transformation.
     * @return
     */
    String getTransformationTraceName();


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


    /**
     * Get transformed EObjects by source EObject to target's EObject intances.
     * Only the transformed instances are presented, not all of the source instances.
     */
    Map<EObject, List<EObject>> getTransformationTrace();

}
