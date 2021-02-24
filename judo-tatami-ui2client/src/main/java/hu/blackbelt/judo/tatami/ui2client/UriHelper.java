package hu.blackbelt.judo.tatami.ui2client;

import lombok.SneakyThrows;

import java.net.URI;
import java.net.URISyntaxException;

public class UriHelper {

    @SneakyThrows(URISyntaxException.class)
    public static URI calculateRelativeURI(URI uri, String path) {
        URI uriRoot = uri;
        if (uriRoot.toString().endsWith(".jar")) {
            uriRoot = new URI("jar:" + uriRoot.toString() + "!/" + path);
        } else if (uriRoot.toString().startsWith("jar:bundle:")) {
            uriRoot = new URI(uriRoot.toString().substring(4, uriRoot.toString().indexOf("!")) + path);
        } else {
            uriRoot = new URI(uriRoot.toString() + "/" + path);
        }
        return uriRoot;
    }



}
