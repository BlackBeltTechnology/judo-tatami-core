package hu.blackbelt.judo.tatami.ui2client;

import com.github.jknack.handlebars.io.URLTemplateLoader;

import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URL;

public class ClientGeneratorTemplateLoader extends URLTemplateLoader {
    final URI root;

    /**
     * Creates a new {@link ClientGeneratorTemplateLoader}.
     *
     * @param root The base URI used for loading.. Required.
     * @param prefix The view prefix. Required.
     * @param suffix The view suffix. Required.
     */
    public ClientGeneratorTemplateLoader(final URI root, final String prefix, final String suffix) {
        this.root = root;
        setPrefix(prefix);
        setSuffix(suffix);
    }

    /**
     * Creates a new {@link ClientGeneratorTemplateLoader}.
     *
     * @param root The base URI used for loading.. Required.
     * @param prefix The view prefix. Required.
     */
    public ClientGeneratorTemplateLoader(final URI root, final String prefix) {
        this(root, prefix, DEFAULT_SUFFIX);
    }

    /**
     * Creates a new {@link ClientGeneratorTemplateLoader}. It looks for templates
     * stored in the root of the given path
     *
     * @param root The base URI used for loading.. Required.
     */
    public ClientGeneratorTemplateLoader(final URI root) {
        this(root, "/");
    }


    @Override
    protected URL getResource(String location) throws IOException {
        try {
            URL scriptUrl = new URI(root.toString() + location).toURL(); //root.resolve(location).toURL();
            try (InputStream is = scriptUrl.openStream()) {
                if (is != null && is.available() > 0) {
                    return scriptUrl;
                }
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }
        // Use this bundle
        return getClass().getResource(location);
    }
}
