package com.project.migration.core.pojo;

// ImageMetadata.java
public class ImageMetadata {
    private final String src;
    private final String alt;
    private final String width;
    private final String height;

    public ImageMetadata(String src, String alt, String width, String height) {
        this.src = src;
        this.alt = alt;
        this.width = width;
        this.height = height;
    }

    // Getters
    public String getSrc() { return src; }
    public String getAlt() { return alt; }
    public String getWidth() { return width; }
    public String getHeight() { return height; }

    @Override
    public String toString() {
        return "ImageMetadata{" +
                "src='" + src + '\'' +
                ", alt='" + alt + '\'' +
                ", width='" + width + '\'' +
                ", height='" + height + '\'' +
                '}';
    }
}