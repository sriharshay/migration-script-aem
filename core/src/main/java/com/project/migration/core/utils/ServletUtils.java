package com.project.migration.core.utils;

import org.apache.sling.api.SlingHttpServletResponse;

import java.io.IOException;

/**
 * The type Servlet utils.
 */
public class ServletUtils {
    private ServletUtils() {
        throw new AssertionError("No instance for utility class");
    }
    
    /**
     * Write response.
     *
     * @param response    the response
     * @param message     the message
     * @param contentType the content type
     * @param statusCode  the status code
     * @throws IOException the io exception
     */
    public static void writeResponse(final SlingHttpServletResponse response, final String message, final String contentType,
            final int statusCode) throws IOException {
        response.getWriter().write(message);
        response.setContentType(contentType);
        response.setStatus(statusCode);
    }
}
