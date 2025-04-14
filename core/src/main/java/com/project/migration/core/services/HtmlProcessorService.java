package com.project.migration.core.services;

import com.project.migration.core.pojo.ElementOperation;
import com.project.migration.core.pojo.ImageMetadata;

import java.util.List;

public interface HtmlProcessorService {
    String processHtml(String html, List<ElementOperation> operations);
    List<ImageMetadata> extractImages(String html);
}