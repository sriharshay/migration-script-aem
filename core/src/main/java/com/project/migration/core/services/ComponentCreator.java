package com.project.migration.core.services;

import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;

import javax.jcr.RepositoryException;
import java.util.Map;

/**
 * Provides utilities for programmatically creating AEM components on pages.
 * <p>
 * This class handles component creation using Sling/JCR APIs while enforcing AEM best practices:
 * <ul>
 *   <li>Transaction-safe resource operations</li>
 *   <li>Proper resource resolution with service accounts</li>
 *   <li>JCR node type validation</li>
 *   <li>Idempotent operations (safe for retries)</li>
 * </ul>
 */
public interface ComponentCreator {
    /**
     * Creates or updates a component on a specified AEM resource.
     * <p>
     * The component will be created under the target resource's {@code jcr:content} node
     * with the specified properties. This method handles:
     * <ul>
     *   <li>JCR node creation with proper node types</li>
     *   <li>Transaction management (auto-commit/revert)</li>
     *   <li>Property merging for existing components</li>
     * </ul>
     *
     * @param resolver              The authenticated ResourceResolver with write permissions
     * @param resourcePath              Full path to the target page (e.g., "/content/my-site/en/home/jcr:content/root/container/container")
     * @param componentResourceType Component's sling:resourceType (e.g., "project/components/text")
     * @param nodeName              Name for the component node (must be JCR-valid)
     * @param properties            Map of component properties to set (including required properties)
     * @throws PersistenceException     If JCR persistence operations fail
     * @throws RepositoryException      For general repository access issues
     * @throws IllegalArgumentException If target page doesn't exist
     * @example // Create a rich text component
     * Map<String, Object> props = new HashMap<>();
     * props.put("text", "<b>Sample</b>");
     * props.put("textIsRich", true);
     * ComponentCreator.createComponent(resolver,
     * "/content/wknd/en",
     * "wknd/components/text",
     * "hero-text",
     * props);
     */
    void createComponent(ResourceResolver resolver, String resourcePath, String componentResourceType, String nodeName, Map<String, Object> properties) throws PersistenceException, RepositoryException;
}
