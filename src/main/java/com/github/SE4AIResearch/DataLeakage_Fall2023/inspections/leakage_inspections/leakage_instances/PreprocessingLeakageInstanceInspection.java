package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.leakage_inspections.leakage_instances;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances.PreprocessingLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_instances.InstanceElementVisitor;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_instances.PreprocessingLeakageInstanceVisitor;
import com.intellij.codeInspection.ProblemsHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class PreprocessingLeakageInstanceInspection extends InstanceInspection<PreprocessingLeakageInstance> {

    @Override
    public InstanceElementVisitor<PreprocessingLeakageInstance> instanceElementVisitor(List<PreprocessingLeakageInstance> leakageInstances, @NotNull ProblemsHolder holder) {
        return new PreprocessingLeakageInstanceVisitor(leakageInstances, holder);

    }

    @Override
    public LeakageType getLeakageType() {
        return LeakageType.PreprocessingLeakage;
    }


}
