package com.project.migration.core.services;

import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;

/**
 * Service interface for AEM page creation operations.
 */
public interface PageCreatorService {
    String TEMPLATE = "/conf/import/settings/wcm/templates/page-content";

    /**
     * Creates an AEM page with proper hierarchy and validation.
     *
     * @param pageManager  Page manager
     * @param parentPath  Parent page path (e.g., "/content/project/en")
     * @param nodeName    JCR-valid node name for the new page
     * @param pageTitle   Display title for the page
     * @param template    Template path to use for page creation
     * @return Created page or null if creation failed
     */
    Page createPage(PageManager pageManager, String parentPath, String nodeName, String pageTitle, String template);
}