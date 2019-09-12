package hu.blackbelt.judo.tatami.workflow.osgi;

import com.google.common.base.Preconditions;
import lombok.AllArgsConstructor;
import lombok.Getter;
import org.osgi.framework.Bundle;

import java.net.URI;
import java.net.URISyntaxException;

@AllArgsConstructor
public class TransformationBundleHolder {
    @Getter
    Bundle bundle;

    public URI resolveURIByManifestName(String header) throws URISyntaxException {
        Preconditions.checkState(bundle.getHeaders().get(header) != null, "Header entry have to exists");
        return bundle.getEntry("META-INF/MANIFEST.MF").toURI().resolve(".." + bundle.getHeaders().get(header));
    }
}
