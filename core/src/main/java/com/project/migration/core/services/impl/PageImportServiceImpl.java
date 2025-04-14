package com.project.migration.core.services.impl;

import com.project.migration.core.services.ComponentCreator;
import com.project.migration.core.services.HtmlProcessorService;
import com.project.migration.core.services.PageCreatorService;
import com.project.migration.core.services.PageImportService;
import com.project.migration.core.utils.JsonUtils;
import com.project.migration.core.utils.NodeNameUtils;
import com.project.migration.core.utils.ServiceUserUtils;
import com.day.cq.wcm.api.Page;
import com.day.cq.wcm.api.PageManager;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonPrimitive;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.jcr.RepositoryException;
import java.util.HashMap;
import java.util.Map;

@Component(service = PageImportService.class)
public class PageImportServiceImpl implements PageImportService {
    public static final String ROOT_PATH = "/content/import/us/en/migration";
    private static final Logger LOGGER = LoggerFactory.getLogger(PageImportServiceImpl.class);
    @Reference
    protected ResourceResolverFactory resolverFactory;
    @Reference
    protected PageCreatorService pageCreatorService;
    @Reference
    protected ComponentCreator componentCreator;
    @Reference
    protected HtmlProcessorService htmlProcessorService;

    @Override
    public JsonObject createPages(JsonElement jsonElement) {
        ResourceResolver serviceResolver = null;
        if (resolverFactory != null) {
            serviceResolver = ServiceUserUtils.getServiceResolver("pageImportService", resolverFactory);
        }
        PageManager pageManager = serviceResolver.adaptTo(PageManager.class);
        JsonObject responseJsonObject = new JsonObject();
        for (JsonElement elm : jsonElement.getAsJsonArray()) {
            JsonObject jsonObject = elm.getAsJsonObject();
            LOGGER.info("Json Object {}", jsonObject);
            String title = jsonObject.get("title").getAsString();
            LOGGER.info("title {}", title);
            String pageName = NodeNameUtils.sanitizeToJcrName(title);
            LOGGER.info("pageName {}", pageName);
            Page page = pageCreatorService.createPage(pageManager, ROOT_PATH, pageName, title, PageCreatorService.TEMPLATE);
            if (null != page) {
                responseJsonObject.add(page.getPath(), new JsonPrimitive("CREATED"));
                Map<String, Object> componentProps = new HashMap<>();
                String text = JsonUtils.getJsonPropertyValueAsString(jsonObject, "body");
/*                List<ElementOperation> ops = List.of(
                        new ElementOperation("div.accordionparagraph", REMOVE, null, null),
                        new ElementOperation("a", REMOVE_ATTR, Map.of("data-id", "") , null),
                        new ElementOperation("p", UPDATE_ATTR, Map.of("test-abc", "harsha-abc"), null)
                );
                String processedText = htmlProcessorService.processHtml(text, ops);
                LOGGER.info("processed text {}",processedText);
                LOGGER.info("extracted images {}",htmlProcessorService.extractImages(text));*/
                componentProps.put("text",text);
                componentProps.put("textIsRich", true);
                String targetResource = page.getContentResource().getPath().concat("/root/container/container");
                try {
                    componentCreator.createComponent(serviceResolver, targetResource, "import/components/text", "text", componentProps);
                } catch (RepositoryException | PersistenceException e) {
                    LOGGER.error("Problem while adding or updating properties", e);
                }
            }
        }
        return responseJsonObject;
    }
}
