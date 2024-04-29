package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.leakage_inspections.leakage_instances;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances.OverlapLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_instances.InstanceElementVisitor;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_instances.OverlapLeakageInstanceVisitor;
import com.intellij.codeInspection.ProblemsHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class OverlapLeakageInstanceInspection extends InstanceInspection<OverlapLeakageInstance> {

    @Override
    public InstanceElementVisitor<OverlapLeakageInstance> instanceElementVisitor(List<OverlapLeakageInstance> leakageInstances
            , @NotNull ProblemsHolder holder) {
        return new OverlapLeakageInstanceVisitor(leakageInstances, holder);
    }

    @Override
    public LeakageType getLeakageType() {
        return LeakageType.OverlapLeakage;
    }


}
