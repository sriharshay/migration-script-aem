package com.project.migration.core.pojo;

import com.project.migration.core.enums.OperationType;
import com.project.migration.core.services.HtmlProcessorService;

import java.util.Collections;
import java.util.List;
import java.util.Map;

/**
 * Represents a single HTML element manipulation operation to be applied by {@link HtmlProcessorService}.
 *
 * <p>This immutable class combines four key components needed for DOM manipulation:
 * <ul>
 *   <li>A CSS selector to target elements</li>
 *   <li>An operation type defining what action to perform</li>
 *   <li>Attributes to modify (when applicable)</li>
 *   <li>HTML content to insert/update (when applicable)</li>
 * </ul>
 *
 * <h2>Example Usage:</h2>
 * <pre>{@code
 * // Remove all script tags
 * new ElementOperation("script", OperationType.REMOVE, null, null);
 *
 * // Add target="_blank" to external links
 * new ElementOperation("a[href^='http']", OperationType.UPDATE_ATTR,
 *     Map.of("target", "_blank"), null);
 *
 * // Insert a divider after h2 headings
 * new ElementOperation("h2", OperationType.INSERT_AFTER, null, "<hr class='section-divider'>");
 * }</pre>
 *
 * @see OperationType
 * @see HtmlProcessorService#processHtml(String, List)
 */
public class ElementOperation {
    private final String selector;
    private final OperationType type;
    private final Map<String, String> attributes;
    private final String content;

    /**
     * Constructs a new HTML element operation configuration.
     *
     * @param selector   CSS selector matching target elements (JSoup syntax)
     * @param type       Operation type to perform on matched elements
     * @param attributes Attribute map (usage varies by operation type):
     *                   <ul>
     *                     <li>UPDATE_ATTR: Map of attribute key/value pairs to set</li>
     *                     <li>REMOVE_ATTR: Map keys indicate attributes to remove (values ignored)</li>
     *                     <li>Other operations: May be null</li>
     *                   </ul>
     * @param content    HTML content used for:
     *                   <ul>
     *                     <li>UPDATE_CONTENT: Replacement inner HTML</li>
     *                     <li>INSERT_BEFORE/INSERT_AFTER: HTML to insert</li>
     *                     <li>Other operations: May be null</li>
     *                   </ul>
     */
    public ElementOperation(String selector, OperationType type, Map<String, String> attributes, String content) {
        this.selector = selector;
        this.type = type;
        this.attributes = attributes != null ? Collections.unmodifiableMap(attributes) : Collections.emptyMap();
        this.content = content;
    }

    /**
     * @return CSS selector string used to target elements (JSoup syntax)
     */
    public String getSelector() {
        return selector;
    }

    /**
     * @return Type of operation to perform on matched elements
     */
    public OperationType getType() {
        return type;
    }

    /**
     * @return Immutable map of attributes (usage depends on operation type)
     * @see #getType()
     */
    public Map<String, String> getAttributes() {
        return attributes;
    }

    /**
     * @return HTML content string (usage depends on operation type) or null
     * @see #getType()
     */
    public String getContent() {
        return content;
    }
}