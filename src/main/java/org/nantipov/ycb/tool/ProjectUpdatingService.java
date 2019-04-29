package org.nantipov.ycb.tool;

import org.nantipov.ycb.tool.domain.ProjectConfiguration;
import org.nantipov.ycb.tool.domain.VideoPadProject;

import java.io.IOException;
import java.nio.file.Path;
import java.util.SortedMap;
import java.util.TreeMap;

import static org.fusesource.jansi.Ansi.ansi;

public class ProjectUpdatingService {

    private final Path projectDir;
    private final YAMLConfigStorage configStorage;
    private final VideoPadProjectStorage videoPadProjectStorage;

    public ProjectUpdatingService(Path projectDir) {
        this.projectDir = projectDir;
        this.configStorage = new YAMLConfigStorage(projectDir);
        this.videoPadProjectStorage = new VideoPadProjectStorage(projectDir);
    }

    public void update() throws IOException {
        ProjectConfiguration projectConfig = configStorage.readProjectConfig();
        String projectName = projectConfig.getProjectName();
        VideoPadProject videoPadProject = videoPadProjectStorage.readProject(projectName);

        // add new labels
        System.out.print("Processing labels...");
        videoPadProject.getLabels()
                       .forEach(
                               label -> projectConfig.getLabels()
                                                     .putIfAbsent(label, getInitialMap(label, projectConfig))
                       );
        System.out.println(
                ansi().render("@|green OK|@")
        );

        // add new editions
        System.out.print("Processing editions...");
        projectConfig.getLabels()
                     .entrySet()
                     .stream()
                     .filter(entry -> !entry.getValue()
                                            .entrySet()
                                            .containsAll(projectConfig.getEpisodeSettings().getEditions())
                     )
                     .forEach(entry -> projectConfig.getEpisodeSettings()
                                                    .getEditions()
                                                    .forEach(
                                                            edition -> entry.getValue()
                                                                            .putIfAbsent(edition,
                                                                                         entry.getKey() + "_" +
                                                                                         edition)
                                                    ));
        System.out.println(
                ansi().render("@|green OK|@")
        );

        // write project file
        System.out.print("Writing editions projects...");
        for (String edition : projectConfig.getEpisodeSettings().getEditions()) {
            videoPadProjectStorage.writeProjectEdition(projectName, edition, projectConfig);
        }
        System.out.println(
                ansi().render("@|green OK|@")
        );

        // write config file
        System.out.print("Writing project configuration...");
        configStorage.writeProjectConfig(projectConfig);
        System.out.println(
                ansi().render("@|green OK|@")
        );
    }

    private SortedMap<String, String> getInitialMap(String label, ProjectConfiguration projectConfig) {
        SortedMap<String, String> map = new TreeMap<>();
        projectConfig.getEpisodeSettings()
                     .getEditions()
                     .forEach(edition -> map.put(edition, label + "_" + edition));
        return map;
    }
}
