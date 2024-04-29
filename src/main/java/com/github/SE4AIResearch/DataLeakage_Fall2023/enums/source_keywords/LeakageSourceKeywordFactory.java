package com.github.SE4AIResearch.DataLeakage_Fall2023.enums.source_keywords;

import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;

public class LeakageSourceKeywordFactory {
    public static LeakageSourceKeyword[] getSourceKeywordValuesForleakageType(LeakageType type) {
        return switch (type) {
            case OverlapLeakage -> OverlapLeakageSourceKeyword.values();
            case PreprocessingLeakage -> PreprocessingLeakageSourceKeyword.values();
            case MultiTestLeakage -> new LeakageSourceKeyword[0];//TODO: no need fo this case
        };
    }
}
