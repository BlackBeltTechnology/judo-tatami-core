package hu.blackbelt.judo.tatami.psm2jql;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.judo.meta.psm.jql.extract.runtime.PsmJqlExtractModel;
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


@Builder(builderMethodName = "psm2JqlExtractTransformationTraceBuilder")
@Getter
public class Psm2JqlExtractTransformationTrace implements TransformationTrace {

    @NonNull
    PsmModel psmModel;

    @NonNull
    PsmJqlExtractModel psmJqlExtractModel;

    @NonNull
    Map<EObject, List<EObject>> trace;

    @Override
    public List<Class> getSourceModelTypes() {
        return ImmutableList.of(PsmModel.class);
    }

    @Override
    public List<Object> getSourceModels() {
        return ImmutableList.of(psmModel);
    }

    @Override
    public <T> T getSourceModel(Class<T> sourceModelType) {
        return null;
    }

    @Override
    public <T> ResourceSet getSourceResourceSet(Class<T> sourceModelType) {
        if (sourceModelType == PsmModel.class) {
            return psmModel.getResourceSet();
        }
        throw new IllegalArgumentException("Unknown source model type: " + sourceModelType.getName());
    }

    @Override
    public <T> URI getSourceURI(Class<T> sourceModelType) {
        if (sourceModelType == PsmModel.class) {
            return psmModel.getUri();
        }
        throw new IllegalArgumentException("Unknown source model type: " + sourceModelType.getName());
    }

    @Override
    public Class getTargetModelType() {
        return PsmJqlExtractModel.class;
    }

    @Override
    public Object getTargetModel() {
        return psmJqlExtractModel;
    }

    @Override
    public ResourceSet getTargetResourceSet() {
        return psmJqlExtractModel.getResourceSet();
    }

    @Override
    public URI getTargetURI() {
        return psmJqlExtractModel.getUri();
    }

    @Override
    public Class<? extends TransformationTrace> getType() {
        return Psm2JqlExtractTransformationTrace.class;
    }

    @Override
    public String getTransformationTraceName() {
        return "psm2jqlextract";
    }

    @Override
    public String getModelVersion() {
        return psmModel.getVersion();
    }

    @Override
    public Map<EObject, List<EObject>> getTransformationTrace() {
        return trace;
    }

    @Override
    public String getModelName() {
        return psmModel.getName();
    }
}
