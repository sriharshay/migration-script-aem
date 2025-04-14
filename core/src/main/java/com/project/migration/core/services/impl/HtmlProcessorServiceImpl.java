package com.project.migration.core.services.impl;

import com.project.migration.core.enums.OperationType;
import com.project.migration.core.pojo.ElementOperation;
import com.project.migration.core.pojo.ImageMetadata;
import com.project.migration.core.services.HtmlProcessorService;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.osgi.service.component.annotations.Activate;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.metatype.annotations.AttributeDefinition;
import org.osgi.service.metatype.annotations.Designate;
import org.osgi.service.metatype.annotations.ObjectClassDefinition;

import java.util.List;
import java.util.stream.Collectors;

// HtmlProcessorServiceImpl.java
@Component(service = HtmlProcessorService.class)
@Designate(ocd = HtmlProcessorServiceImpl.HtmlProcessorConfig.class)
public class HtmlProcessorServiceImpl implements HtmlProcessorService {

    @ObjectClassDefinition(name = "HTML Processor Configuration")
    public @interface HtmlProcessorConfig {
        @AttributeDefinition(name = "Pretty Print HTML")
        boolean prettyPrint() default true;
    }

    private boolean prettyPrint;

    @Activate
    protected void activate(HtmlProcessorConfig config) {
        this.prettyPrint = config.prettyPrint();
    }

    @Override
    public String processHtml(String html, List<ElementOperation> operations) {
        // Use parseBodyFragment instead of parse
        Document doc = Jsoup.parseBodyFragment(html);
        doc.outputSettings().prettyPrint(prettyPrint);
        for (ElementOperation op : operations) {
            for (Element el : doc.select(op.getSelector())) {
                applyOperation(el, op);
            }
        }
        return doc.body().children().outerHtml();
    }

    @Override
    public List<ImageMetadata> extractImages(String html) {
        return Jsoup.parse(html).select("img").stream()
                .map(el -> new ImageMetadata(
                        el.attr("abs:src"),
                        el.attr("alt"),
                        el.attr("width"),
                        el.attr("height")
                ))
                .collect(Collectors.toList());
    }

    /**
     * Applies a specified HTML manipulation operation to a selected element using JSoup's DOM API.
     * Supports six types of operations defined via {@link ElementOperation} parameter.
     *
     * <h2>Operation Types and Examples:</h2>
     *
     * <h3>1. REMOVE - Delete element from DOM</h3>
     * <pre>{@code
     * new ElementOperation(
     *     "script",
     *     OperationType.REMOVE,
     *     null,
     *     null
     * );
     * }</pre>
     *
     * <h3>2. UPDATE_ATTR - Add/update element attributes</h3>
     * <pre>{@code
     * new ElementOperation(
     *     "img",
     *     OperationType.UPDATE_ATTR,
     *     Map.of("loading", "lazy", "alt", "placeholder"),
     *     null
     * );
     * }</pre>
     *
     * <h3>3. UPDATE_CONTENT - Replace inner HTML</h3>
     * <pre>{@code
     * new ElementOperation(
     *     "div.error",
     *     OperationType.UPDATE_CONTENT,
     *     null,
     *     "<p>New error message</p>"
     * );
     * }</pre>
     *
     * <h3>4. REMOVE_ATTR - Delete specific attributes</h3>
     * <pre>{@code
     * new ElementOperation(
     *     "table",
     *     OperationType.REMOVE_ATTR,
     *     Map.of("border", "", "cellpadding", ""),
     *     null
     * );
     * }</pre>
     *
     * <h3>5. INSERT_BEFORE - Add content before element</h3>
     * <pre>{@code
     * new ElementOperation(
     *     "h1",
     *     OperationType.INSERT_BEFORE,
     *     null,
     *     "<div class='breadcrumbs'>Home > Page</div>"
     * );
     * }</pre>
     *
     * <h3>6. INSERT_AFTER - Add content after element</h3>
     * <pre>{@code
     * new ElementOperation(
     *     "ul.gallery",
     *     OperationType.INSERT_AFTER,
     *     null,
     *     "<div class='pagination'></div>"
     * );
     * }</pre>
     *
     * <h2>Complete Usage Example:</h2>
     * <pre>{@code
     * List<ElementOperation> operations = Arrays.asList(
     *     new ElementOperation("meta[charset]", OperationType.REMOVE, null, null),
     *     new ElementOperation("img", OperationType.UPDATE_ATTR,
     *         Map.of("data-src", "${el.attr(\"src\")}", "src", "placeholder.jpg"), null),
     *     new ElementOperation("a[target='_blank']", OperationType.INSERT_AFTER,
     *         null, "<span class='external-icon'></span>")
     * );
     *
     * String processedHtml = processor.processHtml(originalHtml, operations);
     * }</pre>
     *
     * @param el JSoup Element to modify
     * @param op Operation configuration containing:
     *          - CSS selector for target elements
     *          - {@link OperationType} to execute
     *          - Attribute map (for UPDATE_ATTR/REMOVE_ATTR)
     *          - HTML content (for UPDATE_CONTENT/INSERT_* operations)
     *
     * @see ElementOperation
     * @see OperationType
     *
     * <h2>Implementation Notes:</h2>
     * <ul>
     *   <li>For INSERT_BEFORE/INSERT_AFTER, content is inserted as raw HTML</li>
     *   <li>Attribute operations (UPDATE/REMOVE) affect all matching attributes</li>
     *   <li>Element removal is permanent in the parsed document tree</li>
     *   <li>Use {@code Jsoup.clean()} for user-generated content sanitization</li>
     * </ul>
     */
    private void applyOperation(Element el, ElementOperation op) {
        switch (op.getType()) {
            case REMOVE:
                el.remove();
                break;
            case UPDATE_ATTR:
                op.getAttributes().forEach(el::attr);
                break;
            case UPDATE_CONTENT:
                el.html(op.getContent());
                break;
            case REMOVE_ATTR:
                op.getAttributes().keySet().forEach(el::removeAttr);
                break;
            case INSERT_BEFORE:
                el.before(op.getContent());
                break;
            case INSERT_AFTER:
                el.after(op.getContent());
                break;
        }
    }
}
