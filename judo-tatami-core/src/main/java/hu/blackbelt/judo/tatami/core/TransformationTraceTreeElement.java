package hu.blackbelt.judo.tatami.core;

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
