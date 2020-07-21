package hu.blackbelt.judo.tatami.ui2client;

import java.io.IOException;

import com.github.jknack.handlebars.Handlebars;
import com.github.jknack.handlebars.Template;
import com.github.jknack.handlebars.io.ClassPathTemplateLoader;

public class TemplateFacatory {

	public Template getTemplate(String templateName) throws IOException {
		ClassPathTemplateLoader templateLoader = new ClassPathTemplateLoader();
		templateLoader.setSuffix(".dart.hbs");
		Handlebars handlebars = new Handlebars(templateLoader);
		
		Template template = handlebars.compile(templateName);
		return template;
	}
	
}
