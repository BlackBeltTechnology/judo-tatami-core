package hu.blackbelt.judo.tatami.jql2expression;

import com.google.common.collect.ImmutableList;
import hu.blackbelt.judo.meta.expression.runtime.ExpressionModel;
import hu.blackbelt.judo.meta.psm.jql.extract.runtime.PsmJqlExtractModel;
import hu.blackbelt.judo.tatami.core.TrackInfo;
import lombok.Builder;
import lombok.Getter;
import lombok.NonNull;
import org.eclipse.emf.common.util.URI;
import org.eclipse.emf.ecore.EObject;
import org.eclipse.emf.ecore.resource.ResourceSet;

import java.util.List;
import java.util.Map;


@Builder(builderMethodName = "jqlExtract2ExpressionTrackInfoBuilder")
@Getter
public class JqlExtract2ExpressionTrackInfo implements TrackInfo {

    @NonNull
    PsmJqlExtractModel psmJqlExtractModel;

    @NonNull
    ExpressionModel expressionModel;

    @NonNull
    Map<EObject, List<EObject>> trace;

    @Override
    public List<Class> getSourceModelTypes() {
        return ImmutableList.of(PsmJqlExtractModel.class);
    }

    @Override
    public List<Object> getSourceModels() {
        return ImmutableList.of(psmJqlExtractModel);
    }

    @Override
    public <T> T getSourceModel(Class<T> sourceModelType) {
        return null;
    }

    @Override
    public <T> ResourceSet getSourceResourceSet(Class<T> sourceModelType) {
        if (sourceModelType == PsmJqlExtractModel.class) {
            return psmJqlExtractModel.getResourceSet();
        }
        throw new IllegalArgumentException("Unknown source model type: " + sourceModelType.getName());
    }

    @Override
    public <T> URI getSourceURI(Class<T> sourceModelType) {
        if (sourceModelType == PsmJqlExtractModel.class) {
            return psmJqlExtractModel.getUri();
        }
        throw new IllegalArgumentException("Unknown source model type: " + sourceModelType.getName());
    }

    @Override
    public Class getTargetModelType() {
        return ExpressionModel.class;
    }

    @Override
    public Object getTargetModel() {
        return expressionModel;
    }

    @Override
    public ResourceSet getTargetResourceSet() {
        return expressionModel.getResourceSet();
    }

    @Override
    public URI getTargetURI() {
        return expressionModel.getUri();
    }

    @Override
    public Class<? extends TrackInfo> getType() {
        return JqlExtract2ExpressionTrackInfo.class;
    }

    @Override
    public String getTrackInfoName() {
        return "jqlextract2expression";
    }

    @Override
    public String getModelVersion() {
        return psmJqlExtractModel.getVersion();
    }

    @Override
    public Map<EObject, List<EObject>> getTransformationTrace() {
        return trace;
    }

    @Override
    public String getModelName() {
        return psmJqlExtractModel.getName();
    }
}
