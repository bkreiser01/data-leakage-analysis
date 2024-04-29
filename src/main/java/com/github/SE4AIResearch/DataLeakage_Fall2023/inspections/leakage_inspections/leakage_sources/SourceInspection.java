package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.leakage_inspections.leakage_sources;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.source_keywords.LeakageSourceKeyword;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.leakage_inspections.LeakageInspection;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_sources.SourceElementVisitor;
import com.intellij.codeInspection.ProblemsHolder;
import com.jetbrains.python.psi.PyElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class SourceInspection<T extends LeakageInstance, U extends LeakageSourceKeyword> extends LeakageInspection<T> {


    public abstract SourceElementVisitor<T, U> sourceElementVisitor(List<T> leakageInstances, @NotNull ProblemsHolder holder);



    @Override
    public PyElementVisitor getElementVisitor(@NotNull ProblemsHolder holder) {

        var leakageInstances = getLeakageInstancesForType(leakageInstanceCollector.LeakageInstances());
        return sourceElementVisitor(leakageInstances, holder);
    }

}
