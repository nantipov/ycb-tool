package org.nantipov.ycb.tool.domain;

import lombok.Data;

import java.util.LinkedHashMap;
import java.util.Map;

@Data
public class VideoPadTrack {
    private String h;
    private Map<String, String> properties = new LinkedHashMap<>();
}
