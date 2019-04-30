package hu.blackbelt.judo.tatami.esm2psm;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.judo.meta.esm.runtime.EsmModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.tatami.core.TransformationTrace;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.util.List;
import java.util.Map;

@Builder(builderMethodName = "esm2PsmTransformationTraceBuilder")
@Getter
public class Esm2PsmTransformationTrace implements TransformationTrace {

    @NonNull
    EsmModel esmModel;

    @NonNull
    PsmModel psmModel;

    @NonNull
    Map<EObject, List<EObject>> trace;

    @Override
    public List<Class> getSourceModelTypes() {
        return ImmutableList.of(EsmModel.class);
    }

    @Override
    public List<Object> getSourceModels() {
        return ImmutableList.of(esmModel);
    }

    @Override
    public <T> T getSourceModel(Class<T> sourceModelType) {
        return null;
    }

    @Override
    public <T> ResourceSet getSourceResourceSet(Class<T> sourceModelType) {
        if (sourceModelType == EsmModel.class) {
            return esmModel.getResourceSet();
        }
        throw new IllegalArgumentException("Unknown source model type: " + sourceModelType.getName());
    }

    @Override
    public <T> URI getSourceURI(Class<T> sourceModelType) {
        if (sourceModelType == EsmModel.class) {
            return esmModel.getUri();
        }
        throw new IllegalArgumentException("Unknown source model type: " + sourceModelType.getName());
    }

    @Override
    public Class getTargetModelType() {
        return PsmModel.class;
    }

    @Override
    public Object getTargetModel() {
        return psmModel;
    }

    @Override
    public ResourceSet getTargetResourceSet() {
        return psmModel.getResourceSet();
    }

    @Override
    public URI getTargetURI() {
        return psmModel.getUri();
    }

    @Override
    public Class<? extends TransformationTrace> getType() {
        return Esm2PsmTransformationTrace.class;
    }

    @Override
    public String getTransformationTraceName() {
        return "esm2psm";
    }

    @Override
    public String getModelVersion() {
        return esmModel.getVersion();
    }

    @Override
    public Map<EObject, List<EObject>> getTransformationTrace() {
        return trace;
    }

    @Override
    public String getModelName() {
        return esmModel.getName();
    }
}
