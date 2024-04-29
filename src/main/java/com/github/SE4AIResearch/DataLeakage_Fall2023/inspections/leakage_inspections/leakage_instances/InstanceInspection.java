package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.leakage_inspections.leakage_instances;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.leakage_inspections.LeakageInspection;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_instances.InstanceElementVisitor;
import com.intellij.codeInspection.ProblemsHolder;
import com.jetbrains.python.psi.PyElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class InstanceInspection<T extends LeakageInstance> extends LeakageInspection<T> {

    public abstract InstanceElementVisitor<T> instanceElementVisitor(List<T> leakageInstances, @NotNull ProblemsHolder holder);

    @Override
    public PyElementVisitor getElementVisitor(@NotNull ProblemsHolder holder) {
        var leakageInstances = getLeakageInstancesForType(leakageInstanceCollector.LeakageInstances());
        return instanceElementVisitor(leakageInstances, holder);
    }
}
