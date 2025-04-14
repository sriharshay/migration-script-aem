package com.project.migration.core.utils;

import org.apache.commons.lang3.StringUtils;
import org.apache.sling.api.resource.LoginException;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceResolverFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.HashMap;

public class ServiceUserUtils {

    /**
     * LOGGER
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ServiceUserUtils.class);

    private ServiceUserUtils() {
    }

    public static ResourceResolver getServiceResolver(String serviceUserName, ResourceResolverFactory resolverFactory) {
        ResourceResolver resolver = null;
        if(StringUtils.isNotBlank(serviceUserName) && resolverFactory!=null) {
            HashMap<String, Object> param = new HashMap<>();
            param.put(ResourceResolverFactory.SUBSERVICE, serviceUserName);
            try {
                resolver = resolverFactory.getServiceResourceResolver(param);
            } catch (LoginException e) {
                LOGGER.error("Error: Getting service user {} - {}", serviceUserName, e);
            }
        }
        return resolver;
    }

}
