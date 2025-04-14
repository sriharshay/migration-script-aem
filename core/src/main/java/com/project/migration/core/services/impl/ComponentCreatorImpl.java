package com.project.migration.core.services.impl;

import com.project.migration.core.services.ComponentCreator;
import org.apache.sling.api.resource.ModifiableValueMap;
import org.apache.sling.api.resource.PersistenceException;
import org.apache.sling.api.resource.Resource;
import org.apache.sling.api.resource.ResourceResolver;
import org.apache.sling.api.resource.ResourceUtil;
import org.osgi.service.component.annotations.Component;

import javax.jcr.RepositoryException;
import java.util.Map;

@Component(service = ComponentCreator.class)
public class ComponentCreatorImpl implements ComponentCreator {

    public void createComponent(ResourceResolver resolver, String resourcePath, String componentResourceType, String nodeName, Map<String, Object> properties) throws RepositoryException {
        // Path for new component
        String componentPath = String.format("%s/%s", resourcePath, nodeName);
        try {
            // Create or update component resource
            Resource componentResource = ResourceUtil.getOrCreateResource(resolver, componentPath, "nt:unstructured", "sling:Folder", true);
            ModifiableValueMap mvm = componentResource.adaptTo(ModifiableValueMap.class);
            // Set mandatory properties
            mvm.put("sling:resourceType", componentResourceType);
            // Add custom properties
            for (Map.Entry<String, Object> entry : properties.entrySet()) {
                mvm.put(entry.getKey(), entry.getValue());
            }
            // Commit changes
            if (resolver.hasChanges()) {
                resolver.commit();
            }
        } catch (PersistenceException e) {
            resolver.revert();
            throw new RepositoryException("Failed to create component", e);
        }
    }
}
