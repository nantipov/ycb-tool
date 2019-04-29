package org.nantipov.ycb.tool;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.dataformat.yaml.YAMLFactory;
import com.google.common.base.Charsets;
import org.nantipov.ycb.tool.domain.ProjectConfiguration;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class YAMLConfigStorage {

    private static final String FILENAME_PROJECT_CONFIG = "ycb.yaml";

    private final Path projectDirectory;
    private final ObjectMapper mapper = new ObjectMapper(new YAMLFactory());

    public YAMLConfigStorage(Path projectDirectory) {
        this.projectDirectory = projectDirectory;
    }

    public ProjectConfiguration readProjectConfig() throws IOException {
        return mapper.readValue(
                Files.newBufferedReader(projectDirectory.resolve(FILENAME_PROJECT_CONFIG), Charsets.UTF_8),
                ProjectConfiguration.class
        );
    }

    public void writeProjectConfig(ProjectConfiguration config) throws IOException {
        mapper.writeValue(
                Files.newBufferedWriter(projectDirectory.resolve(FILENAME_PROJECT_CONFIG), Charsets.UTF_8), config
        );
    }
}
