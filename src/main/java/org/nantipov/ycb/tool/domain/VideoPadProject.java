package org.nantipov.ycb.tool.domain;

import lombok.Data;

import java.util.LinkedHashSet;
import java.util.Set;

@Data
public class VideoPadProject {
    private Set<String> labels = new LinkedHashSet<>();
}
