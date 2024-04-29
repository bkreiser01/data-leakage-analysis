package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.leakage_inspections.leakage_sources;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances.PreprocessingLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.source_keywords.PreprocessingLeakageSourceKeyword;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_sources.PreprocessingLeakageSourceVisitor;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_sources.SourceElementVisitor;
import com.intellij.codeInspection.ProblemsHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PreprocessingLeakageSourceInspection extends SourceInspection<PreprocessingLeakageInstance, PreprocessingLeakageSourceKeyword> {

    @Override
    public LeakageType getLeakageType() {
        return LeakageType.PreprocessingLeakage     ;
    }

    @Override
    public SourceElementVisitor<PreprocessingLeakageInstance, PreprocessingLeakageSourceKeyword> sourceElementVisitor(List<PreprocessingLeakageInstance> leakageInstances, @NotNull ProblemsHolder holder) {
        return new PreprocessingLeakageSourceVisitor(leakageInstances, holder);

    }


}
