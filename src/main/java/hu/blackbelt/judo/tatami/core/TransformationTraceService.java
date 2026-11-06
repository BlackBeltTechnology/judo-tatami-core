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

    // ==================== NEW MULTI-SOURCE API ====================

    /**
     * Get all source ascendants of an instance by model type.
     *
     * <p>Unlike {@link #getAscendantOfInstanceByModelType(String, Class, EObject)} which returns
     * a single EObject, this method returns all sources when a trace entry has multiple sources.</p>
     *
     * @param modelName the model name
     * @param sourceModelType the source model type to filter by
     * @param targetElement the target element to find ascendants for
     * @return list of all source ascendants of the given type, empty list if none found
     */
    List<EObject> getAscendantsOfInstanceByModelType(String modelName, Class sourceModelType, EObject targetElement);

    /**
     * Get all root ascendants of an instance.
     *
     * <p>Unlike {@link #getRootAscendantOfInstance(String, EObject)} which returns a single
     * EObject, this method returns all root sources when trace chains involve multi-source entries.</p>
     *
     * @param modelName the model name
     * @param targetElement the target element to find root ascendants for
     * @return list of all root ascendants, empty list if none found
     */
    List<EObject> getRootAscendantsOfInstance(String modelName, EObject targetElement);

    /**
     * Get all ascendants with multi-source support.
     *
     * <p>Similar to {@link #getAllAscendantOfInstance(String, EObject)} but returns lists
     * of EObjects to support trace entries with multiple sources.</p>
     *
     * @param modelName the model name
     * @param targetElement the target element
     * @return map of TransformationTrace to list of source EObjects
     */
    Map<TransformationTrace, List<EObject>> getAllAscendantsOfInstanceMultiSource(String modelName, EObject targetElement);

    /**
     * Get descendants from multiple source instances.
     *
     * <p>Finds all descendants that were created from any of the given source instances.</p>
     *
     * @param modelName the model name
     * @param modelType the target model type to filter by
     * @param sourceInstances the source instances to find descendants for
     * @return list of descendant EObjects of the given type
     */
    List<EObject> getDescendantsOfInstancesByModelType(String modelName, Class modelType, EObject... sourceInstances);

    /**
     * Get all descendants with multi-source support.
     *
     * <p>Similar to {@link #getAllDescendantOfInstance(String, EObject)} but accepts multiple
     * source instances and handles multi-source trace entries.</p>
     *
     * @param modelName the model name
     * @param sourceInstances the source instances to find descendants for
     * @return map of TransformationTrace to list of target EObjects
     */
    Map<TransformationTrace, List<EObject>> getAllDescendantsOfInstancesMultiSource(String modelName, EObject... sourceInstances);

    /**
     * Get trace entries for a specific instance.
     *
     * <p>Returns all TraceEntry objects where the given instance appears
     * either as a source or as a target.</p>
     *
     * @param modelName the model name
     * @param instance the instance to search for
     * @return list of TraceEntry objects containing the instance
     */
    List<TraceEntry> getTraceEntriesForInstance(String modelName, EObject instance);

}
