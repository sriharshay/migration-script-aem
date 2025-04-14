package com.project.migration.core.services.impl;

import com.project.migration.core.services.PageCreatorService;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.day.cq.wcm.api.WCMException;
import org.apache.commons.lang3.StringUtils;
import org.osgi.service.component.annotations.Component;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * OSGi service implementation for AEM page creation with hierarchy management.
 */
@Component(service = PageCreatorService.class)
public class PageCreatorServiceImpl implements PageCreatorService {

    private static final String DEFAULT_TEMPLATE = "/conf/project/settings/wcm/templates/page-content";
    private static final Logger LOGGER = LoggerFactory.getLogger(PageCreatorServiceImpl.class);

    @Override
    public Page createPage(PageManager pageManager, String parentPath, String nodeName, String pageTitle, String template) {
        return createPageInternal(parentPath, nodeName, pageTitle, pageManager,
                StringUtils.defaultIfBlank(template, DEFAULT_TEMPLATE));
    }

    /**
     * Internal recursive method for page creation with parent validation.
     *
     * @param path         Target parent path
     * @param nodeName     Page node name
     * @param title        Page title
     * @param pageManager  PageManager instance
     * @param template     Template path to use
     * @return Created page or existing valid page
     */
    private Page createPageInternal(final String path, final String nodeName, final String title,
                                    final PageManager pageManager, final String template) {
        if (pageManager == null) {
            LOGGER.error("PageManager unavailable - check service user permissions");
            return null;
        }
        try {
            Page parentPage = pageManager.getPage(path);
            if (parentPage != null && parentPage.hasChild(nodeName)) {
                return handleExistingPage(path, nodeName, pageManager);
            }

            createParentHierarchy(path, title, pageManager);
            return createNewPage(path, nodeName, title, pageManager, template);

        } catch (WCMException | IllegalArgumentException e) {
            LOGGER.error("Page creation failed for path {}: {}", path, e.getMessage());
            return null;
        }
    }

    private Page handleExistingPage(String path, String nodeName, PageManager pageManager) {
        Page existingPage = pageManager.getPage(path + "/" + nodeName);
        if (existingPage != null && existingPage.isValid()) {
            LOGGER.info("Using existing valid page at {}", existingPage.getPath());
            return existingPage;
        }
        LOGGER.warn("Invalid page detected at {}", path + "/" + nodeName);
        return null;
    }

    private void createParentHierarchy(String path, String title, PageManager pageManager)
            throws WCMException {
        if (pageManager.getPage(path) == null) {
            String parentPath = StringUtils.substringBeforeLast(path, "/");
            String parentNode = StringUtils.substringAfterLast(path, "/");

            if (StringUtils.isNoneBlank(parentPath, parentNode)) {
                LOGGER.debug("Creating missing parent page: {}", path);
                createPageInternal(parentPath, parentNode, title, pageManager, DEFAULT_TEMPLATE);
            }
        }
    }

    private Page createNewPage(String path, String nodeName, String title,
                               PageManager pageManager, String template) throws WCMException {
        LOGGER.info("Creating new page at {}/{} with template {}", path, nodeName, template);
        return pageManager.create(path, nodeName, template, title, true);
    }
}