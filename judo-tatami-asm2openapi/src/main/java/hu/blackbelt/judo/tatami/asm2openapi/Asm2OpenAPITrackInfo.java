package hu.blackbelt.judo.tatami.asm2openapi;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.openapi.runtime.OpenAPIModel;
import hu.blackbelt.judo.tatami.core.TrackInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.util.List;
import java.util.Map;


@Builder(builderMethodName = "asm2OpenAPITrackInfoBuilder")
@Getter
public class Asm2OpenAPITrackInfo implements TrackInfo {

    @NonNull
    AsmModel asmModel;

    @NonNull
    OpenAPIModel openAPIModel;

    @NonNull
    Map<EObject, List<EObject>> trace;

    @Override
    public List<Class> getSourceModelTypes() {
        return ImmutableList.of(AsmModel.class);
    }

    @Override
    public List<Object> getSourceModels() {
        return ImmutableList.of(asmModel);
    }

    @Override
    public <T> T getSourceModel(Class<T> sourceModelType) {
        return null;
    }

    @Override
    public <T> ResourceSet getSourceResourceSet(Class<T> sourceModelType) {
        if (sourceModelType == AsmModel.class) {
            return asmModel.getResourceSet();
        }
        throw new IllegalArgumentException("Unknown source model type: " + sourceModelType.getName());
    }

    @Override
    public <T> URI getSourceURI(Class<T> sourceModelType) {
        if (sourceModelType == AsmModel.class) {
            return asmModel.getUri();
        }
        throw new IllegalArgumentException("Unknown source model type: " + sourceModelType.getName());
    }

    @Override
    public Class getTargetModelType() {
        return OpenAPIModel.class;
    }

    @Override
    public Object getTargetModel() {
        return openAPIModel;
    }

    @Override
    public ResourceSet getTargetResourceSet() {
        return openAPIModel.getResourceSet();
    }

    @Override
    public URI getTargetURI() {
        return openAPIModel.getUri();
    }

    @Override
    public Class<? extends TrackInfo> getType() {
        return Asm2OpenAPITrackInfo.class;
    }

    @Override
    public String getTrackInfoName() {
        return "asm2openapi";
    }

    @Override
    public String getModelVersion() {
        return asmModel.getVersion();
    }

    @Override
    public Map<EObject, List<EObject>> getTransformationTrace() {
        return trace;
    }

    @Override
    public String getModelName() {
        return asmModel.getName();
    }
}
