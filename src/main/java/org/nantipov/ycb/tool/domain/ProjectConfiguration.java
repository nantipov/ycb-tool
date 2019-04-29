package org.nantipov.ycb.tool.domain;

import lombok.Data;

import java.util.Map;
import java.util.SortedMap;
import java.util.TreeMap;

@Data
public class ProjectConfiguration {
    private String projectName;
    private EpisodeSettings episodeSettings = new EpisodeSettings();
    private Map<String, SortedMap<String, String>> labels = new TreeMap<>();
}
