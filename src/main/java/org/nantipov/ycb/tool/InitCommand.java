package org.nantipov.ycb.tool;

import com.google.common.collect.ImmutableSortedMap;
import org.nantipov.ycb.tool.domain.EpisodeType;
import org.nantipov.ycb.tool.domain.ProjectConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

import static org.fusesource.jansi.Ansi.ansi;

public class InitCommand implements Runnable {

    private final String projectName;
    private final Path projectDir;
    private final YAMLConfigStorage configStorage;
    private final VideoPadProjectStorage videoPadProjectStorage;
    private final ProjectUpdatingService updatingService;

    public InitCommand(String projectName) {
        this.projectName = projectName;
        this.projectDir = Paths.get(".");
        this.configStorage = new YAMLConfigStorage(projectDir);
        this.videoPadProjectStorage = new VideoPadProjectStorage(projectDir);
        this.updatingService = new ProjectUpdatingService(projectDir);
    }

    public static void execute(String projectName) {
        InitCommand initCommand = new InitCommand(projectName);
        initCommand.run();
    }

    @Override
    public void run() {
        try {
            System.out.println("Initializing the project structure...");
            //            createProjectDirectory();
            createInitialProjectConfigFile();
            videoPadProjectStorage.putInitialProjectFile(projectName);
            updatingService.update();
            System.out.println(
                    ansi().render("@|green Done|@")
            );
        } catch (IOException e) {
            System.out.println(ansi().fgRed().a("Could not create project directory / files: " + e.getMessage()));
        }
    }

    private void createProjectDirectory() throws IOException {
        Files.createDirectories(projectDir);
    }

    private void createInitialProjectConfigFile() throws IOException {
        configStorage.writeProjectConfig(getDefaultProjectConfig());
    }

    private ProjectConfiguration getDefaultProjectConfig() {
        ProjectConfiguration config = new ProjectConfiguration();
        config.setProjectName(projectName);
        config.getEpisodeSettings().setType(EpisodeType.LAB_STORY);
        config.getEpisodeSettings().getEditions().add("en");
        config.getEpisodeSettings().setDefaultEdition("en");
        config.getLabels().put("episodeTitle", ImmutableSortedMap.of("en", projectName));
        config.getLabels().put("episodeAnnouncement", ImmutableSortedMap.of(
                "en", config.getEpisodeSettings().getType().getAnnouncementText())
        );
        return config;
    }
}
