package com.github.SE4AIResearch.DataLeakage_Fall2023.tool_windows;

import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;

import java.util.HashMap;

public class LeakageTypeMapFactory {
   public static HashMap<LeakageType, String> getLeakageTypeMap() {
      HashMap<LeakageType, String> map = new HashMap<>();

      map.put(LeakageType.OverlapLeakage, "Overlap");
      map.put(LeakageType.PreprocessingLeakage, "Preprocessing");
      map.put(LeakageType.MultiTestLeakage, "Multi-Test");

      return map;
   }
}
