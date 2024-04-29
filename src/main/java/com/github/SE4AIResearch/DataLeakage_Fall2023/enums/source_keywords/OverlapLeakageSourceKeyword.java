package com.github.SE4AIResearch.DataLeakage_Fall2023.enums.source_keywords;

import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageCause;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.source_keywords.LeakageSourceKeyword;

import java.util.List;

/**
 * Corresponds to a source of overlap leakage
 */
public enum OverlapLeakageSourceKeyword implements LeakageSourceKeyword {
    sample("sample"),
    flow("flow");
    //TODO: add more

    private final String methodKeyword;

    OverlapLeakageSourceKeyword(String methodKeyword) {
        this.methodKeyword = methodKeyword;
    }


    @Override
    public String getTaintKeyword() {
        return switch (this) {
            case flow -> "flow"; //TODO: add keyword
            case sample -> "split";
        };
    }



    @Override
    public String toString() {
        return methodKeyword;
    }


}
