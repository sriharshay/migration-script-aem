package com.project.migration.core.utils;

import com.day.cq.wcm.api.Page;
import com.day.crx.JcrConstants;
import com.google.common.base.Stopwatch;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import com.google.gson.JsonSyntaxException;
import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ValueMap;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * The type Page export utils.
 */
public class PageExportUtils {
    /**
     * The constant LOG.
     */
    private static final Logger LOG = LoggerFactory.getLogger(PageExportUtils.class);
    /**
     * The constant gson.
     */
    private static final Gson gson = new GsonBuilder().setLenient().create();

    /**
     * Instantiates a new Page export utils.
     */
    private PageExportUtils() {
        throw new AssertionError("No instance for utility class");
    }

    /**
     * Gets page as json object.
     *
     * @param resource              the resource
     * @param includeChildNodes     the include child nodes
     * @param excludePropertiesList the exclude properties list
     * @return the page as json object
     */
    public static JsonObject getPageAsJsonObject(Resource resource, List<String> includeChildNodes, List<String> excludePropertiesList) {
        Stopwatch stopwatch = Stopwatch.createStarted();
        JsonObject jsonObject = null;
        if (null != resource) {
            final Resource contentResource = resource.getChild(JcrConstants.JCR_CONTENT);
            if (null != contentResource) {
                jsonObject = new JsonObject();
                ValueMap valueMap = contentResource.getValueMap();
                updateJsonObject(excludePropertiesList, jsonObject, valueMap);
                checkChildNodes(excludePropertiesList, includeChildNodes, contentResource, jsonObject); //For Cruise and Ship
            } else {
                LOG.debug("contentResource in null");
            }
        } else {
            LOG.debug("resource is null");
        }
        LOG.trace("Time took to get resource [{}] as json object [{}]", resource, stopwatch);
        return jsonObject;
    }

    /**
     * Check for Child nodes and adds valueMap to Json
     *
     * @param excludePropertiesList the exclude properties list
     * @param childNodesList        the child nodes list
     * @param contentResource       the content resource
     * @param jsonObject            the json object
     */
    private static void checkChildNodes(List<String> excludePropertiesList, List<String> childNodesList, Resource contentResource, JsonObject jsonObject) {
        if (contentResource.hasChildren()) {
            Iterator<Resource> children = contentResource.listChildren();
            while (children.hasNext()) {
                Resource childNode = children.next();
                if (childNodesList.contains(childNode.getName())) {
                    updateChildNodeDetails(excludePropertiesList, childNode, jsonObject);
                }
            }
        }
    }

    /**
     * Gets simple resource as json object.
     *
     * @param resource              the resource
     * @param excludePropertiesList the exclude properties list
     * @return the simple resource as json object
     */
    public static JsonObject getSimpleResourceAsJsonObject(Resource resource, List<String> excludePropertiesList) {
        JsonObject jsonObject = null;
        if (null != resource) {
            jsonObject = new JsonObject();
            ValueMap valueMap = resource.getValueMap();
            for (String key : valueMap.keySet()) {
                if (isValidProperty(excludePropertiesList, key)) {
                    String value = valueMap.get(key, String.class);
                    jsonObject.add(key, new JsonPrimitive((StringUtils.isNotBlank(value) ? value : StringUtils.EMPTY)));
                }
            }
        } else {
            LOG.debug("resource is null");
        }
        return jsonObject;
    }

    /**
     * Is valid property boolean.
     *
     * @param excludePropertiesList the exclude properties list
     * @param key                   the key
     * @return the boolean
     */
    public static boolean isValidProperty(List<String> excludePropertiesList, String key) {
        boolean isValid = true;
        if (null != excludePropertiesList) {
            for (String property : excludePropertiesList) {
                if (StringUtils.isNotBlank(key) && key.matches(property)) {
                    isValid = false;
                    break;
                }
            }
        }
        return isValid;
    }

    /**
     * Gets page as json string.
     *
     * @param page                  the page
     * @param includeChildNodes     the include child nodes
     * @param excludePropertiesList the exclude properties list
     * @return the page as json string
     */
    public static String getPageAsJsonString(Page page, List<String> includeChildNodes, List<String> excludePropertiesList) {
        String json = StringUtils.EMPTY;
        if (null != page) {
            Stopwatch stopwatch = Stopwatch.createStarted();
            Resource resource = page.adaptTo(Resource.class);
            JsonObject jsonObject = getPageAsJsonObject(resource, includeChildNodes, excludePropertiesList);
            Gson gson = new Gson();
            json = gson.toJson(jsonObject);
            LOG.trace("page [{}] json is [{}]", page.getPath(), json);
            LOG.debug("page [{}] is converted to json string in [{}]", page.getPath(), stopwatch);
        } else {
            LOG.error("getPageAsJsonString::Page can not be null");
        }
        return json;
    }

    /**
     * Adds valueMap to Json
     *
     * @param excludePropertiesList the exclude properties list
     * @param jsonObject            the json object
     * @param valueMap              the value map
     */
    public static void updateJsonObject(List<String> excludePropertiesList, JsonObject jsonObject, ValueMap valueMap) {
        if (null != valueMap) {
            for (String key : valueMap.keySet()) {
                if (!excludePropertiesList.contains(key)) {
                    JsonArray jsonArray = new JsonArray();
                    updateJsonArray(excludePropertiesList, jsonArray, valueMap, jsonObject);
                }
            }
        }
    }

    /**
     * Adds valueMap of Child nodes to Json
     *
     * @param excludePropertiesList the exclude properties list
     * @param childNode             the child node
     * @param jsonObject            the json object
     */
    private static void updateChildNodeDetails(List<String> excludePropertiesList, Resource childNode, JsonObject jsonObject) {
        JsonArray jsonArray = new JsonArray();
        if (null != childNode) {
            if (childNode.hasChildren()) { /*Keep two if block separate. It is to generate empty array, which is needed while generating the sha256 hash*/
                for (Resource itemResource : childNode.getChildren()) {
                    if (StringUtils.startsWith(itemResource.getName(), "item")) {
                        ValueMap vm = itemResource.getValueMap();
                        JsonObject jo = new JsonObject();
                        addItemNodeProperties(excludePropertiesList, itemResource, jo);
                        updateJsonArray(excludePropertiesList, jsonArray, vm, jo);
                    }
                }
            }
            jsonObject.add(childNode.getName(), jsonArray);
        }
    }

    /**
     * Adds valueMap of Item nodes to Json
     *
     * @param excludePropertiesList the exclude properties list
     * @param itemResource          the item resource
     * @param jsonObject            the json object
     */
    public static void addItemNodeProperties(List<String> excludePropertiesList, Resource itemResource, JsonObject jsonObject) {
        if (null != itemResource && itemResource.hasChildren()) {
            Iterator<Resource> subChildren = itemResource.listChildren();
            while (subChildren.hasNext()) {
                Resource subChildNode = subChildren.next();
                updateChildNodeDetails(excludePropertiesList, subChildNode, jsonObject);
            }
        }
    }

    /**
     * Adds valueMap to Json Array
     *
     * @param excludePropertiesList the exclude properties list
     * @param jsonArray             the json array
     * @param valueMap              the value map
     * @param jsonObject            the json object
     */
    public static void updateJsonArray(List<String> excludePropertiesList, JsonArray jsonArray, ValueMap valueMap, JsonObject jsonObject) {
        if (null != valueMap && null != jsonArray) {
            for (Map.Entry<String, Object> entry : valueMap.entrySet()) {
                String key = entry.getKey();
                if (isValidProperty(excludePropertiesList, key)) {
                    Object value = entry.getValue();
                    updateJsonArray(valueMap, jsonObject, key, value);
                }
            }
            jsonArray.add(jsonObject);
        }
    }

    /**
     * Adds valueMap to Json Array
     *
     * @param valueMap   the value map
     * @param jsonObject the json object
     * @param key        the key
     * @param value      the value
     */
    public static void updateJsonArray(ValueMap valueMap, JsonObject jsonObject, String key, Object value) {
        if (value instanceof String[]) {
            String[] array = valueMap.get(key, String[].class);
            JsonArray jsonArray = new JsonArray();
            try {
                if (null != array) {
                    for (String element : array) {
                        updateJsonObject(key, jsonArray, element);
                    }
                }
            } catch (JsonSyntaxException e) {
                LOG.error("Invalid Json element for key [{}]. Json element to validate [{}]", key, jsonArray, e);
            }
            jsonObject.add(key, jsonArray);
        } else {
            String propertyValue = valueMap.get(key, String.class);
            jsonObject.add(key, new JsonPrimitive(Objects.requireNonNullElse(propertyValue, StringUtils.EMPTY)));
        }
    }

    /**
     * Update json object.
     *
     * @param key       the key
     * @param jsonArray the json array
     * @param element   the element
     */
    private static void updateJsonObject(final String key, final JsonArray jsonArray, final String element) {
        if (StringUtils.isNotBlank(element)) {
            JsonElement jsonElement = getValidJsonElement(element);
            if (null != jsonElement && !jsonElement.isJsonNull()) {
                if (jsonElement.isJsonObject()) {
                    jsonElement = jsonElement.getAsJsonObject();
                } else if (jsonElement.isJsonArray()) {
                    jsonElement = jsonElement.getAsJsonArray();
                } else {
                    jsonElement = new JsonPrimitive(element);
                }
                jsonArray.add(jsonElement);
            } else {
                LOG.debug("Invalid json element [{}]", element);
            }
        } else {
            jsonArray.add(new JsonPrimitive(element));
            LOG.debug("Property [{}] is blank ", key);
        }
    }

    /**
     * Gets valid json element.
     *
     * @param element the element
     * @return the valid json element
     */
    private static JsonElement getValidJsonElement(final String element) {
        JsonElement jsonElement;
        try {
            jsonElement = new JsonParser().parse(element); // parse the objects
        } catch (JsonSyntaxException e) {
            jsonElement = gson.toJsonTree(element); // parse the string
        }
        return jsonElement;
    }

}
