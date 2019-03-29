package hu.blackbelt.judo.tatami.core;

import com.google.common.collect.Lists;
import lombok.Getter;
import lombok.Setter;
import org.eclipse.emf.ecore.resource.Resource;

import java.util.List;

public class TrackInfoTreeElement implements TrackInfo {

    @Getter
    TrackInfo delegatee;

    @Setter
    @Getter
    List<TrackInfoTreeElement> parent = Lists.newArrayList();

    TrackInfoTreeElement(TrackInfo delegatee) {
        this.delegatee = delegatee;
    }

    public void addParent(TrackInfoTreeElement p) {
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
    public <T> Resource getSourceResource(Class<T> sourceModelType) {
        return delegatee.getSourceResource(sourceModelType);
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
    public Resource getTargetResource() {
        return delegatee.getTargetResource();
    }

    @Override
    public Class<? extends TrackInfo> getType() {
        return delegatee.getType();
    }

    @Override
    public String getTrackInfoName() {
        return delegatee.getTrackInfoName();
    }

    @Override
    public String getModelName() {
        return delegatee.getModelName();
    }

    @Override
    public String getModelVersion() {
        return delegatee.getModelVersion();
    }
}