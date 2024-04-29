package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.leakage_inspections.leakage_sources;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances.OverlapLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.source_keywords.OverlapLeakageSourceKeyword;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_sources.OverlapLeakageSourceVisitor;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_sources.SourceElementVisitor;
import com.intellij.codeInspection.ProblemsHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OverlapLeakageSourceInspection extends SourceInspection<OverlapLeakageInstance, OverlapLeakageSourceKeyword> {

    @Override
    public LeakageType getLeakageType() {
        return LeakageType.OverlapLeakage;
    }


    @Override
    public SourceElementVisitor<OverlapLeakageInstance, OverlapLeakageSourceKeyword> sourceElementVisitor(List<OverlapLeakageInstance> overlapLeakageInstances, @NotNull ProblemsHolder holder) {

        return new OverlapLeakageSourceVisitor(overlapLeakageInstances, holder);
    }


}
