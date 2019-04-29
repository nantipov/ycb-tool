package org.nantipov.ycb.tool.domain;

import java.awt.*;

public enum EpisodeType {
    NON_STORY("Non-Story Episode", Color.decode("#4599FE"), Color.decode("#FFCC00")),
    LAB_STORY("Lab story", Color.decode("#FF9900"), Color.decode("#336699"));

    private String announcementText;
    private Color backgroundColor;
    private Color foregroundColor;

    EpisodeType(String announcementText, Color backgroundColor, Color foregroundColor) {
        this.announcementText = announcementText;
        this.backgroundColor = backgroundColor;
        this.foregroundColor = foregroundColor;
    }

    public String getAnnouncementText() {
        return announcementText;
    }

    public Color getBackgroundColor() {
        return backgroundColor;
    }

    public Color getForegroundColor() {
        return foregroundColor;
    }
}
