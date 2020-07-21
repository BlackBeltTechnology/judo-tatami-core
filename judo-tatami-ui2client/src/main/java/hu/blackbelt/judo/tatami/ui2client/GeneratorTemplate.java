package hu.blackbelt.judo.tatami.ui2client;

import lombok.Getter;
import lombok.Setter;

import java.util.Collection;

@Getter
@Setter
public class GeneratorTemplate {

	private String path;
	private String source;
	private Collection<> expression;
	private Boolean overwrite = true;
}
