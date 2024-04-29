package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.leakage_inspections.leakage_instances;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances.MultiTestLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_instances.InstanceElementVisitor;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_instances.MultiTestLeakageInstanceVisitor;
import com.intellij.codeInspection.ProblemsHolder;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class MultiTestLeakageInstanceInspection extends InstanceInspection<MultiTestLeakageInstance> {

    @Override
    public LeakageType getLeakageType() {
        return LeakageType.MultiTestLeakage;
    }


    @Override
    public InstanceElementVisitor<MultiTestLeakageInstance> instanceElementVisitor(List<MultiTestLeakageInstance> leakageInstances, @NotNull ProblemsHolder holder) {
        return new MultiTestLeakageInstanceVisitor(leakageInstances, holder);

    }
}
