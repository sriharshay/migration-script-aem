package com.project.migration.core.enums;

import com.project.migration.core.pojo.ElementOperation;
import com.project.migration.core.services.HtmlProcessorService;

import java.util.List;

/**
 * Defines the types of HTML element manipulation operations supported by
 * {@link HtmlProcessorService}.
 *
 * <h2>Operation Overview</h2>
 * <table>
 * <caption>Operation Types and Requirements</caption>
 * <tr><th>Type</th><th>Attributes Used</th><th>Content Used</th><th>Description</th></tr>
 * <tr><td>REMOVE</td><td>No</td><td>No</td><td>Remove element from DOM</td></tr>
 * <tr><td>UPDATE_ATTR</td><td>Yes</td><td>No</td><td>Add/update element attributes</td></tr>
 * <tr><td>UPDATE_CONTENT</td><td>No</td><td>Yes</td><td>Replace inner HTML</td></tr>
 * <tr><td>REMOVE_ATTR</td><td>Keys only</td><td>No</td><td>Remove specified attributes</td></tr>
 * <tr><td>INSERT_BEFORE</td><td>No</td><td>Yes</td><td>Insert HTML before element</td></tr>
 * <tr><td>INSERT_AFTER</td><td>No</td><td>Yes</td><td>Insert HTML after element</td></tr>
 * </table>
 *
 * <h2>Usage Examples</h2>
 * <pre>{@code
 * // Remove social media meta tags
 * new ElementOperation("meta[property^='og:']", REMOVE, null, null);
 *
 * // Add lazy loading to images
 * new ElementOperation("img", UPDATE_ATTR, Map.of("loading", "lazy"), null);
 *
 * // Insert section marker after headings
 * new ElementOperation("h2", INSERT_AFTER, null, "<div class='section-marker'></div>");
 * }</pre>
 *
 * @see ElementOperation
 * @see HtmlProcessorService#processHtml(String, List)
 */
public enum OperationType {
    /**
     * Remove matched elements from the document
     * <p>Example: {@code new ElementOperation("script", REMOVE, null, null)}
     */
    REMOVE,

    /**
     * Update element attributes - existing attributes are overwritten
     * <p>Example: {@code new ElementOperation("a", UPDATE_ATTR, Map.of("target", "_blank"), null)}
     */
    UPDATE_ATTR,

    /**
     * Replace element's inner HTML content
     * <p>Example: {@code new ElementOperation("div.error", UPDATE_CONTENT, null, "<p>New content</p>")}
     */
    UPDATE_CONTENT,

    /**
     * Remove specified attributes (uses map keys)
     * <p>Example: {@code new ElementOperation("table", REMOVE_ATTR, Map.of("border", "", "cellspacing", ""), null)}
     */
    REMOVE_ATTR,

    /**
     * Insert HTML content before matched elements
     * <p>Example: {@code new ElementOperation("h1", INSERT_BEFORE, null, "<div class='notice'>Important:</div>")}
     */
    INSERT_BEFORE,

    /**
     * Insert HTML content after matched elements
     * <p>Example: {@code new ElementOperation("ul.gallery", INSERT_AFTER, null, "<div class='pagination'></div>")}
     */
    INSERT_AFTER
}