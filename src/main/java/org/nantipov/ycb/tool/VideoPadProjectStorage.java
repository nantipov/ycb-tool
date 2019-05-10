package org.nantipov.ycb.tool;

import com.google.common.base.Charsets;
import com.google.common.io.Resources;
import org.nantipov.ycb.tool.domain.ProjectConfiguration;
import org.nantipov.ycb.tool.domain.VideoPadProject;
import org.nantipov.ycb.tool.domain.VideoPadTrack;

import java.io.IOException;
import java.io.PrintStream;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.AbstractMap;
import java.util.Arrays;
import java.util.Optional;
import java.util.Set;
import java.util.TreeSet;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.IntStream;

public class VideoPadProjectStorage {

    private static final Pattern LABELS_PATTERN = Pattern.compile("%26%26labels\\.(\\w+)");
    private static final String EDITION_TRACK_TOKEN = "__";
    private static final String PROPERTY_SEPARATOR = "&";
    private static final String PROPERTY_ASSIGMENT_SIGN = "=";
    private static final String PROPERTY_H = "h";

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
                 .map(line -> processLine(line, edition, projectConfig))
                 .forEach(out::println);
        }
    }

    /*
    h=921731
    &name=Audio%20Track%201&output=[0|1]
    &type=1 - video track
    &type=2 - audio track
    */

    private String processLine(String line, String edition, ProjectConfiguration projectConfig) {
        String outputLine = line;
        outputLine = processLabels(outputLine, edition, projectConfig);
        outputLine = processTracks(outputLine, edition, projectConfig);
        return outputLine;
    }

    private String processTracks(String line, String edition, ProjectConfiguration projectConfig) {
        if ((line.contains("&type=1&") || line.contains("&type=2&")) && line.contains(EDITION_TRACK_TOKEN)) {
            VideoPadTrack track = trackFromLine(line);
            String type = track.getProperties().getOrDefault("type", "unknown");
            if (type.equals("1") || type.equals("2")) {
                String trackName = track.getProperties().getOrDefault("name", "unknown");
                if (trackName.endsWith(EDITION_TRACK_TOKEN + edition)) {
                    track.getProperties().put("output", "1");
                } else if (trackName.contains(EDITION_TRACK_TOKEN)) {
                    track.getProperties().put("output", "0");
                }
                return trackToLine(track);
            }
        }
        return line;
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

    private static VideoPadTrack trackFromLine(String line) {
        VideoPadTrack track = new VideoPadTrack();
        String[] elements = line.split(PROPERTY_SEPARATOR);
        Arrays.stream(elements)
              .filter(element -> element.contains(PROPERTY_ASSIGMENT_SIGN))
              .map(String::trim)
              .map(element ->
                           new AbstractMap.SimpleImmutableEntry<>(
                                   element.substring(0, element.indexOf(PROPERTY_ASSIGMENT_SIGN)),
                                   element.substring(element.indexOf(PROPERTY_ASSIGMENT_SIGN) + 1)
                           )
              )
              .forEach(entry -> {
                  if (entry.getKey().equals(PROPERTY_H)) {
                      track.setH(entry.getValue());
                  } else {
                      track.getProperties().put(entry.getKey(), entry.getValue());
                  }
              });
        return track;
    }

    private static String trackToLine(VideoPadTrack track) {
        StringBuilder output = new StringBuilder();
        output.append(PROPERTY_H).append(PROPERTY_ASSIGMENT_SIGN).append(track.getH());
        track.getProperties()
             .forEach((key, value) -> output.append(PROPERTY_SEPARATOR)
                                            .append(key)
                                            .append(PROPERTY_ASSIGMENT_SIGN)
                                            .append(value)
             );
        return output.toString();
    }
}
