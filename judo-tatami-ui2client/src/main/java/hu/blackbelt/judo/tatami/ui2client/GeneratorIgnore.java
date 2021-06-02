package hu.blackbelt.judo.tatami.ui2client;

import hu.blackbelt.epsilon.runtime.execution.api.Log;
import hu.blackbelt.epsilon.runtime.execution.impl.Slf4jLog;
import org.slf4j.LoggerFactory;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.*;
import java.util.Collections;
import java.util.List;

public class GeneratorIgnore {
    public static final Log log = new Slf4jLog(LoggerFactory.getLogger(GeneratorIgnore.class));
    public static final String GENERATOR_IGNORE_FILE = ".generator-ignore";
    private static final String separator = System.getProperty("file.separator");
    private List<String> globs = Collections.emptyList();
    private final Path targetPath;

    public GeneratorIgnore(Path targetPath) {
        this.targetPath = targetPath;
        try {
            globs = Files.readAllLines(Paths.get(targetPath.toString(), GeneratorIgnore.GENERATOR_IGNORE_FILE), StandardCharsets.UTF_8);
        } catch (IOException e) {
            log.info("Generator ignore file(" + GENERATOR_IGNORE_FILE + ") not found at target path(" + targetPath + "), all sources will be generated.");
        }
    }

    public List<String> getGlobs() {
        return globs;
    }

    public boolean shouldExcludeFile(Path absolutePath) {
        Path relativePath = Paths.get(absolutePath.toString().replace(targetPath.toString() + separator, ""));
        return globs.stream().anyMatch((glob) -> {
            final PathMatcher pathMatcher = FileSystems.getDefault().getPathMatcher("glob:" + glob);
            return pathMatcher.matches(relativePath);
        });
    }
}
