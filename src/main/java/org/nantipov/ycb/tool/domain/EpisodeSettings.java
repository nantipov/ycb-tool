package org.nantipov.ycb.tool.domain;

import lombok.Data;

import java.util.ArrayList;
import java.util.List;

@Data
public class EpisodeSettings {
    private EpisodeType type;
    private List<String> editions = new ArrayList<>();
    private String defaultEdition;
}
