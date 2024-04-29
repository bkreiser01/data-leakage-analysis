package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_instances;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionBundle;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.warning_renderers.DataLeakageWarningRenderer;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.StringJoiner;
import java.util.function.Predicate;

public abstract class InstanceElementVisitor<T extends LeakageInstance> extends PyElementVisitor {
    public ProblemsHolder holder;

    public abstract LeakageType getLeakageType();

    public abstract Predicate<T> leakageInstanceIsAssociatedWithNode(@NotNull PsiElement node);

    Collection<RangeHighlighter> collection = new ArrayList<>();


    public boolean leakageIsAssociatedWithNode(List<T> leakageInstances, @NotNull PsiElement node) {
        return leakageInstances.stream().anyMatch(leakageInstanceIsAssociatedWithNode(node));
    }

    public T getLeakageInstanceAssociatedWithNode(List<T> leakageInstances, @NotNull PsiElement node) {
        return leakageInstances.stream().filter(leakageInstanceIsAssociatedWithNode(node)).findFirst().get();
    }

    public void renderInspectionOnLeakageInstance(List<T> leakageInstances, PsiElement node) {
        if (leakageIsAssociatedWithNode(leakageInstances, node)) {
            var instance = getLeakageInstanceAssociatedWithNode(leakageInstances, node);
            LeakageType leakageType = getLeakageType();

            var sourceOptional = instance.getLeakageSource();
            String sb;
            if (sourceOptional.isPresent()) {
                var sourceLineNumbers = sourceOptional.get().getLineNumbers();
                sb = getViewSourceMessage(leakageType, sourceLineNumbers);
            } else {
                sb = InspectionBundle.get(leakageType.getInspectionTextKey());

            }
            DataLeakageWarningRenderer.renderDataLeakageWarning(instance, node,
                    holder, sb, collection
            );


        }
    }

    public void renderInspectionOnLeakageInstance(List<T> leakageInstances, PsiElement node, LocalQuickFix fix) {
        if (leakageIsAssociatedWithNode(leakageInstances, node)) {
            var instance = getLeakageInstanceAssociatedWithNode(leakageInstances, node);
            LeakageType leakageType = getLeakageType();

            var sourceOptional = instance.getLeakageSource();
            String sb;
            if (sourceOptional.isPresent()) {
                var sourceLineNumbers = sourceOptional.get().getLineNumbers();
                sb = getViewSourceMessage(leakageType, sourceLineNumbers);
            } else {
                sb = InspectionBundle.get(leakageType.getInspectionTextKey());
            }
            DataLeakageWarningRenderer.renderDataLeakageWarning(instance, node,
                    holder, sb, fix, collection
            );

        }
    }

    @NotNull
    private static String getViewSourceMessage(LeakageType leakageType, List<Integer> sourceLineNumbers) {
        var sb = new StringBuilder();


        sb.append(InspectionBundle.get(leakageType.getInspectionTextKey()));//TODO: duplicate of above
        sb.append(" ");

        if (sourceLineNumbers.size() == 1) {
            sb.append("See Line ");
            sb.append(sourceLineNumbers.get(0));
            sb.append(" which contains the source of the leakage.");
        } else {

            sb.append("See Lines: ");
            StringJoiner sj = new StringJoiner(", ");
            for (var l : sourceLineNumbers) {
                sj.add(l.toString());
            }
            sb.append(sj);
            sb.append(" which contain the source of the leakage.");
        }
        return sb.toString();
    }


}
