package hu.blackbelt.judo.tatami.workflow.maven.plugin;

import hu.blackbelt.judo.meta.psm.namespace.Model;
import hu.blackbelt.judo.meta.psm.namespace.util.builder.ModelBuilder;
import hu.blackbelt.judo.meta.psm.runtime.PsmModel;
import org.eclipse.emf.common.util.URI;

public class TestPsmModel {

    public PsmModel create() {
        PsmModel psmModel = PsmModel.buildPsmModel().name("test").uri(URI.createURI("urn:test")).build();
        psmModel.addContent(ModelBuilder.create().withName("testModel").build());
        return psmModel;
    }
}
