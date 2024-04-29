package com.github.SE4AIResearch.DataLeakage_Fall2023.tool_windows;

import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageCause;

import java.util.HashMap;

public class CauseMapFactory {
   public static HashMap<LeakageCause, String> getCauseMap() {
      HashMap<LeakageCause, String> map = new HashMap<>();

      map.put(LeakageCause.SplitBeforeSample, "Data split after sampling");
      map.put(LeakageCause.DataAugmentation, "Data augmentation creating dependency between rows");
      map.put(LeakageCause.VectorizingTextData, "Vectorizer fit on train and test data together");
      map.put(LeakageCause.RepeatDataEvaluation, "Repeat data evaluation");
      map.put(LeakageCause.unknownPreprocessing, "Feature selection before split");
      map.put(LeakageCause.unknownOverlap, "Test data not distinct from training data");
      map.put(LeakageCause.unknown, "Unknown");

      return map;
   }
}
