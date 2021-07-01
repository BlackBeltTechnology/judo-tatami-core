package hu.blackbelt.judo.tatami.ui2client;

import com.github.jknack.handlebars.io.TemplateSource;
import com.github.jknack.handlebars.io.URLTemplateLoader;
import lombok.extern.slf4j.Slf4j;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Objects;
import java.util.Stack;

@Slf4j
public class ClientGeneratorTemplateLoader extends URLTemplateLoader {
    final URI root;
    final ClientGeneratorTemplateLoader parent;
    final String contextPath;
    private final ThreadLocal<Stack<String>> pathOrder = ThreadLocal.withInitial(Stack::new);

    /**
     * Creates a new {@link ClientGeneratorTemplateLoader}.
     *
     * @param parent Parent template loader. When parent is defined, it is used when resource not found.
     * @param root The base URI used for loading.. Required.
     * @param prefix The view prefix. Required.
     * @param suffix The view suffix. Required.
     */
    public ClientGeneratorTemplateLoader(final ClientGeneratorTemplateLoader parent, final URI root, final String contextPath, final String prefix, final String suffix) {
        this.root = root;
        this.parent = parent;
        this.contextPath = contextPath;
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
        this(null, root, UriHelper.lastPart(root.toString()), prefix, DEFAULT_SUFFIX);
    }

    /**
     * Creates a new {@link ClientGeneratorTemplateLoader}.
     *
     * @param parent Parent template loader. When parent is defined, it is used when resource not found.
     * @param root The base URI used for loading.. Required.
     * @param prefix The view prefix. Required.
     */
    public ClientGeneratorTemplateLoader(final ClientGeneratorTemplateLoader parent, final URI root, final String prefix) {
        this(parent, root, UriHelper.lastPart(root.toString()), prefix, DEFAULT_SUFFIX);
    }

    /**
     * Creates a new {@link ClientGeneratorTemplateLoader}. It looks for templates
     * stored in the root of the given path
     *
     * @param root The base URI used for loading.. Required.
     */
    public ClientGeneratorTemplateLoader(final URI root) {
        this(null, root, "/");
    }

    /**
     * Creates a new {@link ClientGeneratorTemplateLoader}. It looks for templates
     * stored in the root of the given path
     *
     * @param root The base URI used for loading.. Required.
     */
    public ClientGeneratorTemplateLoader(final ClientGeneratorTemplateLoader parent, final URI root) {
        this(parent, root, UriHelper.lastPart(root.toString()), "/", DEFAULT_SUFFIX);
    }

    private String getOverriddenLocationRelativePath(String loc) {
        return loc.replace(getSuffix(), ".override" + getSuffix());
    }

    @Override
    public String getSuffix() {
        String original = super.getSuffix();
        return !Objects.equals(original, "") ? original : DEFAULT_SUFFIX;
    }

    @Override
    public TemplateSource sourceAt(final String location) throws IOException {
        String loc = location;
        if (location.startsWith(contextPath + "/")) {
            loc = location.substring(contextPath.length() + 1);
        }
        String overrideRelativePath = getOverriddenLocationRelativePath(loc);

        // Need to make sure:
        // - prevent infinite loops for normal flows where override falls back internally to original template
        // - devs can explicitly load overrides, e.g. recurse with override
        if (!loc.equals(overrideRelativePath) && loc.endsWith(getSuffix()) && pathOrder.get().size() > 0 && !pathOrder.get().lastElement().equals(overrideRelativePath)) {
            pathOrder.get().push(overrideRelativePath);
            try {
                URL overrideFullPath = new URI(this.root.toString() + overrideRelativePath).normalize().toURL();
                try (InputStream is = overrideFullPath.openStream()) {
                    if (is != null && is.available() > 0) {
                        return sourceAtInternal(overrideRelativePath, location);
                    }
                    return sourceAtInternal(loc, location);
                } catch (Exception e) {
                    return sourceAtInternal(loc, location);
                }
            } catch (URISyntaxException e) {
                return sourceAtInternal(loc, location);
            }
        }
        pathOrder.get().push(loc);
        return sourceAtInternal(loc, location);
    }

    public TemplateSource sourceAtInternal(final String loc, final String originalLoc) throws IOException {
        try {
            return super.sourceAt(loc);
        } catch (IOException ex) {
            // try next loader in the chain.
            log.trace("Unable to resolve: {}, trying next loader in the chain.", originalLoc);
        }
        if (parent != null) {
            return parent.sourceAt(loc);
        } else {
            throw new FileNotFoundException(originalLoc);
        }
    }

    @Override
    public String resolve(final String location) {
        try {
            //super.sourceAt(location);
            return super.resolve(location);
        } catch (Exception ex) {
            // try next loader in the chain.
            log.trace("Unable to resolve: {}, trying next loader in the chain.", location);
        }
        if (parent != null) {
            return parent.resolve(location);
        } else {
            throw new IllegalStateException("Can't resolve: '" + location + "'");
        }
    }

    @Override
    protected URL getResource(String location) throws IOException {
        try {
            String location_rel = location;
            if (root.toString().endsWith("/") && location.startsWith("/")) {
                location_rel = location.substring(1);
            }
            URL scriptUrl = new URI((root.toString() + location_rel)).normalize().toURL(); //root.resolve(location).toURL();
            try (InputStream is = scriptUrl.openStream()) {
                if (is != null && is.available() > 0) {
                    return scriptUrl;
                }
            } catch (Exception e) {
            }
        } catch (Exception e) {
        }

        URL url = null;
        try {
            url = getClass().getResource(location);
        } catch (Exception e) {
        }

        if (parent != null && url == null) {
            url = parent.getResource(location);
        }
        // Use this bundle
        return url;
    }
}
