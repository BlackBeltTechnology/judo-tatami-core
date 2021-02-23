package hu.blackbelt.judo.tatami.ui2client;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.helper.ConditionalHelpers;
import com.google.common.base.Charsets;
import hu.blackbelt.judo.meta.ui.runtime.UiModel;
import hu.blackbelt.judo.meta.ui.support.UiModelResourceSupport;
import lombok.Getter;
import org.springframework.expression.spel.support.StandardEvaluationContext;

import java.io.IOException;
import java.net.URI;
import java.util.Collection;

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

    public ClientGenerator(UiModel uiModel, URI scriptDir, Collection<GeneratorTemplate> generatorTemplates) throws IOException, UiModelResourceSupport.UiValidationException {
        scriptDirectoryTemplateLoader = new ClientGeneratorTemplateLoader(scriptDir);
        //scriptDirectoryTemplateLoader.setSuffix(".hbs");
        scriptDirectoryTemplateLoader.setSuffix("");

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
