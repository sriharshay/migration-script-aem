package com.project.migration.core.utils;

import com.day.cq.commons.jcr.JcrUtil;
import org.apache.commons.lang3.StringUtils;

public class NodeNameUtils {
    public static String sanitizeToJcrName(String input) {
        if (StringUtils.isBlank(input)) {
            return input;
        }
        // Step 1: Replace spaces and punctuation with hyphens (not just JCR-illegal chars)
        String sanitized = input
                .replaceAll("[\\s,]+", "-")  // Explicitly target spaces/commas first
                .replaceAll("[^a-zA-Z0-9-]", "-"); // Handle other special chars
        // Step 2: Use JcrUtil to ensure JCR compliance
        sanitized = JcrUtil.createValidName(sanitized);
        // Step 3: Collapse consecutive hyphens and trim
        sanitized = sanitized
                .replaceAll("-+", "-")
                .replaceAll("^-|-$", "");
        return sanitized.toLowerCase();
    }
}