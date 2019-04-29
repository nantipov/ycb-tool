package org.nantipov.ycb.tool;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.nantipov.ycb.tool.domain.ProjectConfiguration;
import org.nantipov.ycb.tool.domain.VideoPadProject;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class VideoPadProjectStorage {

    private static final Pattern LABELS_PATTERN = Pattern.compile("%26%26labels\\.(\\w+)");

    private final Path projectDirectory;

    public VideoPadProjectStorage(Path projectDirectory) {
        this.projectDirectory = projectDirectory;
    }

    public void putInitialProjectFile(String projectName) throws IOException {
        Resources.copy(
                Resources.getResource("template.vpj"),
                Files.newOutputStream(getInitialProjectFile(projectName))
        );
    }

    public VideoPadProject readProject(String projectName) throws IOException {
        VideoPadProject project = new VideoPadProject();
        Files.lines(getInitialProjectFile(projectName), Charsets.UTF_8)
             .map(this::extractLabels)
             .flatMap(Set::stream)
             .forEach(label -> project.getLabels().add(label));
        return project;
    }

    public void writeProjectEdition(String projectName, String edition,
                                    ProjectConfiguration projectConfig) throws IOException {
        try (PrintStream out = new PrintStream(Files.newOutputStream(getEditionProjectFile(projectName, edition)))) {
            Files.lines(getInitialProjectFile(projectName), Charsets.UTF_8)
                 .map(line -> processLabels(line, edition, projectConfig))
                 .forEach(out::println);
        }
    }

    private String processLabels(String line, String edition, ProjectConfiguration projectConfig) {
        StringBuilder builder = new StringBuilder(line);
        extractLabels(line)
                .stream()
                .filter(label -> projectConfig.getLabels().containsKey(label))
                .forEach(label -> replaceAll(builder, "%26%26labels." + label,
                                             getLabelValue(label, edition, projectConfig)
                                                     .map(VideoPadProjectStorage::encodeLabel)
                                                     .orElse(label))
                );
        return builder.toString();
    }

    private Set<String> extractLabels(String line) {
        Set<String> labels = new TreeSet<>();
        Matcher matcher = LABELS_PATTERN.matcher(line);
        while (matcher.find()) {
            IntStream.rangeClosed(1, matcher.groupCount())
                     .mapToObj(matcher::group)
                     .forEach(labels::add);
        }
        return labels;
    }

    private Path getInitialProjectFile(String projectName) {
        return projectDirectory.resolve(projectName + ".vpj");
    }

    private Path getEditionProjectFile(String projectName, String edition) {
        return projectDirectory.resolve(projectName + "_" + edition + ".vpj");
    }

    private static Optional<String> getLabelValue(String label, String edition, ProjectConfiguration projectConfig) {
        return Optional.ofNullable(projectConfig.getLabels().get(label))
                       .map(map ->
                                    map.getOrDefault(
                                            edition,
                                            projectConfig.getLabels()
                                                         .get(label)
                                                         .get(projectConfig.getEpisodeSettings().getDefaultEdition())
                                    )
                       );
    }

    private static String encodeLabel(String label) {
        try {
            return URLEncoder.encode(label, Charsets.UTF_8.name());
        } catch (UnsupportedEncodingException e) {
            return label;
        }
    }

    private static void replaceAll(StringBuilder builder, String from, String to) {
        int index = builder.indexOf(from);
        while (index != -1) {
            builder.replace(index, index + from.length(), to);
            index += to.length(); // Move to the end of the replacement
            index = builder.indexOf(from, index);
        }
    }
}
