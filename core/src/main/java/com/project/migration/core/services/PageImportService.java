package com.project.migration.core.services;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;

public interface PageImportService {
    JsonObject createPages(JsonElement jsonElement);
}
