package hu.blackbelt.judo.tatami.ui2client.flutter;

import com.google.common.collect.ImmutableList;
import com.google.common.collect.ImmutableSet;
import hu.blackbelt.judo.meta.ui.Application;
import hu.blackbelt.judo.tatami.ui2client.ClientTemplateProvider;
import hu.blackbelt.judo.tatami.ui2client.GeneratorTemplate;

import java.util.Collection;

public class FlutterTemplateProvider implements ClientTemplateProvider {
    @Override
    public Collection<GeneratorTemplate> get() {
        ImmutableSet.Builder providers = ImmutableSet.builder();

        providers.add(
                GeneratorTemplate.generatorTemplateBuilder()
                        .overwriteExpression("true")
                        .factoryExpression("{#model}")
                        .pathExpression("#element.name.replaceAll('/', '__').concat('_test.dart')")
                        .templateName("templates/flutter/test.dart")
                        .templateContext(ImmutableList.of(
                                new GeneratorTemplate.Expression("application", "#self")
                        ))
                        .build()
        );

        return providers.build();
    }
}
