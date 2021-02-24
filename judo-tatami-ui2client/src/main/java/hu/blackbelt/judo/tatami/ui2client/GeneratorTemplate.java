package hu.blackbelt.judo.tatami.ui2client;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.jknack.handlebars.Context;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.internal.lang3.builder.ReflectionToStringBuilder;
import com.github.jknack.handlebars.internal.lang3.builder.ToStringStyle;
import lombok.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.expression.Expression;
import org.springframework.expression.ExpressionParser;
import org.springframework.expression.spel.standard.SpelExpressionParser;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.*;

@Slf4j
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
@Builder(builderMethodName = "generatorTemplateBuilder")
public class GeneratorTemplate {

	private String factoryExpression;
	private String pathExpression;

	private String overwriteExpression = "true";

	private String template;
	private String templateName;

	@Builder.Default
	private Collection<Expression> templateContext = new HashSet();

	@Builder.Default
	@Getter
	private ExpressionParser parser = new SpelExpressionParser();

	@Builder.Default
	private boolean copy = false;

	public Map<String, org.springframework.expression.Expression> parseExpressions() {
		Map<String, org.springframework.expression.Expression> templateExpressions = new HashMap<>();
		templateContext.stream().forEach(ctx -> {
			final org.springframework.expression.Expression contextTemplate = parser.parseExpression(ctx.getExpression());
			templateExpressions.put(ctx.getName(), contextTemplate);
		});
		return templateExpressions;
	}


	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	@Setter
	public static class Expression {
		private String name;
		private String expression;
	}

	public static Collection<GeneratorTemplate> loadJsonString(String yaml) throws IOException {
		ObjectMapper mapper = new ObjectMapper(new JsonFactory());
		List<GeneratorTemplate> templates = mapper.readValue(yaml, new TypeReference<List<GeneratorTemplate>>(){});
		log.debug(ReflectionToStringBuilder.toString(templates, ToStringStyle.MULTI_LINE_STYLE));
		return templates;
	}

	public static Collection<GeneratorTemplate> loadJsonURL(URL yaml) throws IOException {
		ObjectMapper mapper = new ObjectMapper(new JsonFactory());
		List<GeneratorTemplate> templates = mapper.readValue(yaml, new TypeReference<List<GeneratorTemplate>>(){});
		log.debug(ReflectionToStringBuilder.toString(templates, ToStringStyle.MULTI_LINE_STYLE));
		return templates;
	}

	public static Collection<GeneratorTemplate> loadYamlString(String yaml) throws IOException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		List<GeneratorTemplate> templates = mapper.readValue(yaml, new TypeReference<List<GeneratorTemplate>>(){});
		log.debug(ReflectionToStringBuilder.toString(templates, ToStringStyle.MULTI_LINE_STYLE));
		return templates;
	}

	public static Collection<GeneratorTemplate> loadYamlURL(URL yaml) throws IOException {
		ObjectMapper mapper = new ObjectMapper(new YAMLFactory());
		List<GeneratorTemplate> templates = mapper.readValue(yaml, new TypeReference<List<GeneratorTemplate>>(){});
		log.debug(ReflectionToStringBuilder.toString(templates, ToStringStyle.MULTI_LINE_STYLE));
		return templates;
	}

	public TemplateEvaulator getTemplateEvalulator(ClientGenerator clientGenerator) throws IOException {
		return new TemplateEvaulator(clientGenerator, this);
	}

	public void evalToContextBuilder(TemplateEvaulator templateEvaulator, Context.Builder contextBuilder) {
		templateContext.stream().forEach(ctx -> {

			Class type = templateEvaulator.getTemplateExpressions().get(ctx.getName()).getValueType(templateEvaulator.getClientGenerator().getSpelEvaulationContext());
			contextBuilder.combine(ctx.getName(),
					templateEvaulator.getTemplateExpressions().get(ctx.getName()).getValue(templateEvaulator.getClientGenerator().getSpelEvaulationContext(),
							templateEvaulator.getTemplateExpressions().get(ctx.getName()).getValue(templateEvaulator.getClientGenerator().getSpelEvaulationContext(), type)));
		});
	}

	@Getter
	public static class TemplateEvaulator {
		final org.springframework.expression.Expression factoryExpression;
		final org.springframework.expression.Expression pathExpression;
		final org.springframework.expression.Expression overWriteExpression;
		final Template template;
		final Map<String, org.springframework.expression.Expression> templateExpressions;
		final ClientGenerator clientGenerator;

		public TemplateEvaulator(ClientGenerator clientGenerator, GeneratorTemplate generatorTemplate) throws IOException {
			this.clientGenerator = clientGenerator;
			ExpressionParser parser = generatorTemplate.getParser();
			templateExpressions = generatorTemplate.parseExpressions();
			factoryExpression = parser.parseExpression(generatorTemplate.getFactoryExpression());
			pathExpression = parser.parseExpression(generatorTemplate.getPathExpression());
			overWriteExpression = parser.parseExpression(generatorTemplate.getOverwriteExpression());

			if (generatorTemplate.isCopy()) {
				template = null;
			} else if (generatorTemplate.getTemplate() != null && !"".equals(generatorTemplate.getTemplate().trim())) {
				template = clientGenerator.getHandlebars().compileInline(generatorTemplate.getTemplate());
			} else if (generatorTemplate.getTemplateName() != null && !"".equals(generatorTemplate.getTemplateName().trim())) {
				template = clientGenerator.getHandlebars().compile(generatorTemplate.getTemplateName());
			} else {
				template = null;
			}
		}

		public <C> C getFactoryExpressionResult(Object value, Class<C> type) {
			return getFactoryExpression().getValue(getClientGenerator().getSpelEvaulationContext(), value, type);
		}

	}
}

