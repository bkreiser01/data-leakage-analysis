package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.leakage_inspections;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.PsiUtils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.parsers.LeakageInstanceCollector;
import com.intellij.codeInspection.LocalInspectionToolSession;
import com.intellij.codeInspection.ProblemsHolder;
import com.jetbrains.python.inspections.PyInspection;
import com.jetbrains.python.psi.PyElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public abstract class LeakageInspection<T extends LeakageInstance> extends PyInspection {
    public abstract LeakageType getLeakageType();
    public final LeakageInstanceCollector leakageInstanceCollector = new LeakageInstanceCollector();

    /**
     * @return A list of {@link LeakageInstance}s having the same type as this {@link LeakageInspection}.
     * //TODO: remove unchecked cast
     */
    public List<T> getLeakageInstancesForType(List<LeakageInstance> leakageInstances){
        return leakageInstances.stream()
                .filter(instance -> instance.type().equals(getLeakageType()))
                .map(instance -> (((T) instance))).toList();
    }

    public abstract PyElementVisitor getElementVisitor(@NotNull ProblemsHolder holder);
    @Override
    public  @NotNull PyElementVisitor buildVisitor(@NotNull ProblemsHolder holder, boolean isOnTheFly, @NotNull LocalInspectionToolSession session){
        if (PsiUtils.getInjectionHost(holder) != null) {
            return new PyElementVisitor();
        }

        var document = PsiUtils.getDocument(holder);
        if (document == null) return new PyElementVisitor();

        return getElementVisitor(holder);
    }

}
