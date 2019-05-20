package hu.blackbelt.judo.tatami.core;

import com.google.common.collect.ImmutableList;
import lombok.Builder;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;


@Builder
public class TransformationTraceTest implements TransformationTrace {

    TestModel target;
    List<TestModel> source;
    String name;
    String modelName;
    String version;

    Map<TestModel, ResourceSet> sourceResourceSet;
    ResourceSet targetResourceSet;

    Map<TestModel, URI> sourceURIS;
    URI targetURI;

    Map<EObject, List<EObject>> trace;

    public String toString() {
        return name;
    }

    @Override
    public List<Class> getSourceModelTypes() {
        return source.stream().map(e -> e.getClass()).collect(Collectors.toList());
    }

    @Override
    public List<Object> getSourceModels() {
        return ImmutableList.copyOf(source);
    }

    @Override
    public <T> T getSourceModel(Class<T> sourceModelType) {
        return (T) source.stream().filter(e -> e.getClass().equals(sourceModelType)).findFirst().get();
    }

    @Override
    public <T> ResourceSet getSourceResourceSet(Class<T> sourceModelType) {
        return sourceResourceSet.get(source.stream().filter(e -> e.getClass().equals(sourceModelType)).findFirst().get());
    }

    @Override
    public <T> URI getSourceURI(Class<T> sourceModelType) {
        return sourceURIS.get(source.stream().filter(e -> e.getClass().equals(sourceModelType)).findFirst().get());
    }

    @Override
    public Class getTargetModelType() {
        return target.getClass();
    }

    @Override
    public Object getTargetModel() {
        return target;
    }

    @Override
    public ResourceSet getTargetResourceSet() {
        return targetResourceSet;
    }

    @Override
    public URI getTargetURI() {
        return targetURI;
    }

    @Override
    public Class<? extends TransformationTrace> getType() {
        return null;
    }

    @Override
    public String getTransformationTraceName() {
        return name;
    }

    @Override
    public String getModelName() {
        return modelName;
    }

    @Override
    public String getModelVersion() {
        return null;
    }

    @Override
    public Map<EObject, List<EObject>> getTransformationTrace() {
        return trace;
    }
}
