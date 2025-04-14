package com.project.migration.core.servlets;

import com.project.migration.core.services.PageImportService;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.JsonPrimitive;
import org.apache.commons.io.IOUtils;
import org.apache.sling.api.SlingHttpServletRequest;
import org.apache.sling.api.SlingHttpServletResponse;
import org.apache.sling.api.servlets.HttpConstants;
import org.apache.sling.api.servlets.SlingAllMethodsServlet;
import org.osgi.framework.Constants;
import org.osgi.service.component.annotations.Component;
import org.osgi.service.component.annotations.Reference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.Servlet;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;

@Component(service = Servlet.class, property = {Constants.SERVICE_DESCRIPTION + "=Import Servlet", "sling.servlet.methods=" + HttpConstants.METHOD_POST, "sling.servlet.paths=/bin/import.json"})
public class ImportServlet extends SlingAllMethodsServlet {

    /**
     * LOGGER
     **/
    private static final Logger LOGGER = LoggerFactory.getLogger(ImportServlet.class);

    @Reference
    protected transient PageImportService importService;

    @Override
    protected void doPost(SlingHttpServletRequest request, SlingHttpServletResponse response) throws IOException {
        JsonObject responseJsonObject = new JsonObject();
        response.setContentType("application/json;charset=UTF-8");
        LOGGER.info("Inside ImportServlet doPost");
        String jsonString = IOUtils.toString(request.getReader());
        LOGGER.info("Json string is {}", jsonString);
        JsonArray jsonArray = JsonParser.parseString(jsonString).getAsJsonArray();
        LOGGER.info("jsonArray is {}", jsonArray);
        if (!jsonArray.isEmpty()) {
            responseJsonObject = importService.createPages(jsonArray);
            LOGGER.info("Successfully ran ImportServlet");
            responseJsonObject.add("message", new JsonPrimitive("OK"));
            response.getWriter().println(responseJsonObject);
            response.setStatus(HttpServletResponse.SC_OK);
            response.flushBuffer();
        } else {
            LOGGER.error("Invalid JSON");
            responseJsonObject.add("message", new JsonPrimitive(String.format("Check the response from microservice::%s",jsonString)));
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.getWriter().println(responseJsonObject);
        }
    }
}
