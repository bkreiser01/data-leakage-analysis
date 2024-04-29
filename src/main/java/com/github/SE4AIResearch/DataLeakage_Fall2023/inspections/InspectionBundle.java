package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections;

import com.intellij.DynamicBundle;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.PropertyKey;

public class InspectionBundle extends DynamicBundle {
    public static final InspectionBundle INSTANCE = new InspectionBundle();

    /**
     * Gets inspection text from a .properties file within the resources folder.
     * @param key The identifier for the inspection text
     * @param params
     * @return The inspection text as a String.
     */
    @NotNull
    public static String get(@NotNull
                             @PropertyKey(resourceBundle = "messages.InspectionText")
                             String key,
                             Object @NotNull ... params) {
        return INSTANCE.getMessage(key, params);
    }

    private InspectionBundle() {
        super("messages.InspectionText");
    }
}