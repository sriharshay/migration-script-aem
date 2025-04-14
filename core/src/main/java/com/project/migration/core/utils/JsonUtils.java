package com.project.migration.core.utils;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

/**
 * The type Json utils.
 */
public class JsonUtils {
    /**
     * Instantiates a new Json utils.
     */
    private JsonUtils() {
        throw new AssertionError("No instance for utility class");
    }
    private static final Logger LOG = LoggerFactory.getLogger(JsonUtils.class);
    
    /**
     * Gets property value as string.
     *
     * @param jsonObject   the json object
     * @param propertyName the property name
     * @param defaultValue the default value
     * @return the property value as string
     */
    public static String getPropertyValueAsString(final JsonObject jsonObject, final String propertyName,
            final String defaultValue) {
        String value = defaultValue;
        if (null != jsonObject && jsonObject.has(propertyName)) {
            JsonElement jsonElement = jsonObject.get(propertyName);
            if (!jsonElement.isJsonNull() && jsonElement.isJsonPrimitive()) {
                value = jsonElement.getAsString();
            }
        }
        return value;
    }
    
    /**
     * Gets json property value as string.
     *
     * @param jsonObject   the json object
     * @param propertyName the property name
     * @return the json property value as string
     */
    public static String getJsonPropertyValueAsString(final JsonObject jsonObject, final String propertyName) {
        return getPropertyValueAsString(jsonObject, propertyName, StringUtils.EMPTY);
    }
    
    /**
     * Convert object array to string array.
     *
     * @param array Object array
     * @return the string array
     */
    public static String[] toStringArray(Object[] array) {
        String[] result = new String[0];
        if (null != array && array.length > 0) {
            result = new String[array.length];
            for (int i = 0; i < array.length; ++i) {
                result[i] = array[i].toString();
            }
        }
        return result;
    }
    
    /**
     * Trim empty values string.
     *
     * @param jsonString the json string
     * @return the string
     */
    public static String trimEmptyValues(final String jsonString) {
        Type type = new TypeToken<Map<String, Object>>() {
        }.getType();
        if (StringUtils.isBlank(jsonString)) {
            return StringUtils.EMPTY;
        }
        Map<String, Object> data = null;
        try {
            data = new Gson().fromJson(jsonString, type);
        } catch (JsonSyntaxException e) {
            LOG.error("problem in converting string to json [{}]", jsonString, e);
        }
        return trimEmptyValues(data);
    }
    
    /**
     * Trim empty values string.
     *
     * @param map the map
     * @return the string
     */
    public static String trimEmptyValues(final Map<String, Object> map) {
        if (null == map) {
            return StringUtils.EMPTY;
        }
        Map<String, Object> data = new HashMap<>(map);
        for (Iterator<Map.Entry<String, Object>> it = data.entrySet().iterator(); it.hasNext(); ) {
            Map.Entry<String, Object> entry = it.next();
            if (isEmptyValue(entry)) { //removes empty string
                it.remove();
            } else if (entry.getValue().getClass().equals(ArrayList.class)) { //removes empty array
                if (((ArrayList<?>) entry.getValue()).isEmpty()) {
                    it.remove();
                }
            } else if (entry.getValue() instanceof Map) { //removes empty json object
                Map<?, ?> m = (Map<?, ?>) entry.getValue();
                if (m.isEmpty()) {
                    it.remove();
                }
            }
        }
        return new GsonBuilder().setPrettyPrinting().create().toJson(data);
    }
    
    private static boolean isEmptyValue(final Map.Entry<String, Object> entry) {
        boolean isEmptyValue = false;
        Object value = entry.getValue();
        if (null == value) {
            isEmptyValue = true;
        } else if (value.getClass().equals(String.class)) {
            String strValue = String.valueOf(value);
            if (StringUtils.isBlank(strValue)) {
                isEmptyValue = true;
            }
        }
        return isEmptyValue;
    }
}
