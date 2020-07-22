package hu.blackbelt.judo.tatami.ui2client;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.io.File;
import java.util.Collection;
import java.util.HashSet;

@Getter
@Setter
@Builder(builderMethodName = "generatorTemplateBuilder")
public class GeneratorTemplate {
	private String factoryExpression;
	private String pathExpression;

	@Builder.Default
	private String overwriteExpression = "true";

	private String template;
	private String templateName;

	@Builder.Default
	private Collection<Expression> templateContext = new HashSet();

	@AllArgsConstructor
	@Getter
	public static class Expression {
		private String name;
		private String expression;
		private Class<?> clazz;
	}
}

