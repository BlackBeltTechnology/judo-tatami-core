package hu.blackbelt.judo.tatami.ui2client;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.helper.ConditionalHelpers;
import com.github.jknack.handlebars.io.CompositeTemplateLoader;
import com.github.jknack.handlebars.io.TemplateLoader;
import com.google.common.base.Charsets;
import hu.blackbelt.judo.meta.ui.runtime.UiModel;
import hu.blackbelt.judo.meta.ui.support.UiModelResourceSupport;
import lombok.Getter;
import lombok.SneakyThrows;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import static hu.blackbelt.judo.meta.ui.support.UiModelResourceSupport.LoadArguments.uiLoadArgumentsBuilder;
import static hu.blackbelt.judo.meta.ui.support.UiModelResourceSupport.loadUi;

public class ClientGenerator {
    @Getter
    private final ClientGeneratorTemplateLoader scriptDirectoryTemplateLoader;

    @Getter
    private final Handlebars handlebars;

    @Getter
    private final UiModelResourceSupport modelResourceSupport;

    @Getter
    private final Collection<GeneratorTemplate> generatorTemplates;

    @Getter
    private final StandardEvaluationContext spelEvaulationContext;

    public ClientGenerator(UiModel uiModel, List<URI> scriptUris, Collection<GeneratorTemplate> generatorTemplates) throws IOException, UiModelResourceSupport.UiValidationException {

        // Chain template loaders
        ClientGeneratorTemplateLoader clientGeneratorTemplateLoader = null;
        for (URI uri: scriptUris) {
            clientGeneratorTemplateLoader = new ClientGeneratorTemplateLoader(clientGeneratorTemplateLoader, uri);
            clientGeneratorTemplateLoader.setSuffix("");
        }

        /*
        List<TemplateLoader> templateLoaders = scriptUris.stream().map(u -> new ClientGeneratorTemplateLoader(null, u)).collect(Collectors.toList());
        templateLoaders.forEach(t -> t.setSuffix(""));

        if (templateLoaders.size() > 1) {
            scriptDirectoryTemplateLoader = new CompositeTemplateLoader(templateLoaders
                    .toArray(new TemplateLoader[templateLoaders.size()]));
        } else {
            scriptDirectoryTemplateLoader = templateLoaders.get(0);
        }

         */

        scriptDirectoryTemplateLoader = clientGeneratorTemplateLoader;
//        scriptDirectoryTemplateLoader.setSuffix(".hbs");
//        scriptDirectoryTemplateLoader.setSuffix("");

        handlebars = new Handlebars();
        handlebars.with(scriptDirectoryTemplateLoader);
        handlebars.setStringParams(true);
        handlebars.setCharset(Charsets.UTF_8);
        handlebars.registerHelpers(ConditionalHelpers.class);
        handlebars.prettyPrint(true);
        handlebars.setInfiniteLoops(true);

        modelResourceSupport = loadUi(uiLoadArgumentsBuilder()
                .uri(org.eclipse.emf.common.util.URI.createURI("ui:" + uiModel.getName()))
                .resourceSet(uiModel.getResourceSet()).build());

        this.generatorTemplates = generatorTemplates;

        spelEvaulationContext = new StandardEvaluationContext();
    }

    public void setVariable(String name, Object value) {
        spelEvaulationContext.setVariable(name, value);
    }

}
