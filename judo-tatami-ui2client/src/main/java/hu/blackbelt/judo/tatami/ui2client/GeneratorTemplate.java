package hu.blackbelt.judo.tatami.ui2client;

import com.fasterxml.jackson.core.JsonFactory;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.github.jknack.handlebars.internal.lang3.builder.ReflectionToStringBuilder;
import com.github.jknack.handlebars.internal.lang3.builder.ToStringStyle;
import lombok.*;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

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
	private boolean copy = false;

	@AllArgsConstructor
	@NoArgsConstructor
	@Getter
	@Setter
	public static class Expression {
		private String name;
		private String expression;
		private String className;
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

}

