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

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.util.List;
import java.util.Map;

public class TransformationTraceTreeElement implements TransformationTrace {

    @Getter
    TransformationTrace delegatee;

    @Setter
    @Getter
    List<TransformationTraceTreeElement> parent = Lists.newArrayList();

    TransformationTraceTreeElement(TransformationTrace delegatee) {
        this.delegatee = delegatee;
    }

    public void addParent(TransformationTraceTreeElement p) {
        parent.add(p);
    }

    @Override
    public List<Class> getSourceModelTypes() {
        return delegatee.getSourceModelTypes();
    }

    @Override
    public List<Object> getSourceModels() {
        return delegatee.getSourceModels();
    }

    @Override
    public <T> T getSourceModel(Class<T> sourceModelType) {
        return delegatee.getSourceModel(sourceModelType);
    }

    @Override
    public <T> ResourceSet getSourceResourceSet(Class<T> sourceModelType) {
        return delegatee.getSourceResourceSet(sourceModelType);
    }

    @Override
    public <T> URI getSourceURI(Class<T> sourceModelType) {
        return delegatee.getSourceURI(sourceModelType);
    }

    @Override
    public Class getTargetModelType() {
        return delegatee.getTargetModelType();
    }

    @Override
    public Object getTargetModel() {
        return delegatee.getTargetModel();
    }

    @Override
    public ResourceSet getTargetResourceSet() {
        return delegatee.getTargetResourceSet();
    }

    @Override
    public URI getTargetURI() {
        return delegatee.getTargetURI();
    }

    @Override
    public Class<? extends TransformationTrace> getType() {
        return delegatee.getType();
    }

    @Override
    public String getTransformationTraceName() {
        return delegatee.getTransformationTraceName();
    }

    @Override
    public String getModelName() {
        return delegatee.getModelName();
    }

    @Override
    public String getModelVersion() {
        return delegatee.getModelVersion();
    }

    @Override
    public Map<EObject, List<EObject>> getTransformationTrace() {
        return delegatee.getTransformationTrace();
    }
}
