package com.github.SE4AIResearch.DataLeakage_Fall2023.data;

import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageCause;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.source_keywords.LeakageSourceKeyword;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.source_keywords.LeakageSourceKeywordFactory;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.source_keywords.OverlapLeakageSourceKeyword;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.source_keywords.PreprocessingLeakageSourceKeyword;

import java.util.HashMap;
import java.util.List;

public class CauseMapFactory {

    public static HashMap<LeakageSourceKeyword, List<LeakageCause>> getPotentialCauses() {
        HashMap<LeakageSourceKeyword, List<LeakageCause>> map = new HashMap<>();

        map.put(OverlapLeakageSourceKeyword.flow, List.of(LeakageCause.DataAugmentation));
        map.put(OverlapLeakageSourceKeyword.sample, List.of(LeakageCause.SplitBeforeSample));
        map.put(PreprocessingLeakageSourceKeyword.predict, List.of(LeakageCause.VectorizingTextData));


        return map;
    }
}

