package hu.blackbelt.judo.tatami.psm2asm;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.judo.meta.asm.runtime.AsmModel;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import hu.blackbelt.judo.tatami.core.TrackInfo;
import lombok.Builder;
import lombok.Getter;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.Resource;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.util.List;
import java.util.Map;


@Builder(builderMethodName = "psm2AsmTrackInfoBuilder")
@Getter
public class Psm2AsmTrackInfo implements TrackInfo {
    PsmModel psmModel;
    AsmModel asmModel;
    String modelName;

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
        return AsmModel.class;
    }

    @Override
    public Object getTargetModel() {
        return asmModel;
    }

    @Override
    public ResourceSet getTargetResourceSet() {
        return asmModel.getResourceSet();
    }

    @Override
    public URI getTargetURI() {
        return asmModel.getUri();
    }

    @Override
    public Class<? extends TrackInfo> getType() {
        return Psm2AsmTrackInfo.class;
    }

    @Override
    public String getTrackInfoName() {
        return "psm2asm";
    }

    @Override
    public String getModelVersion() {
        return psmModel.getVersion();
    }
}
