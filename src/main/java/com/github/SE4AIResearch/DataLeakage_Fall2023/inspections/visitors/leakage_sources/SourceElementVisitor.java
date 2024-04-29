package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_sources;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.CauseMapFactory;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_sources.LeakageSource;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.taints.Taint;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageCause;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.source_keywords.LeakageSourceKeyword;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.source_keywords.LeakageSourceKeywordFactory;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionBundle;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.PsiUtils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.warning_renderers.DataLeakageWarningRenderer;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.psi.PsiElement;
import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyElementVisitor;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public abstract class SourceElementVisitor<T extends LeakageInstance, U extends LeakageSourceKeyword> extends PyElementVisitor {


    Collection<RangeHighlighter> collection = new ArrayList<>();

    public abstract LeakageType getLeakageType();

    public Predicate<T> leakageSourceAssociatedWithNode(@NotNull PsiElement node,
                                                        @NotNull ProblemsHolder holder) {
        var nodeLineNumber = PsiUtils.getNodeLineNumber(node, holder);
        boolean bol;
        return instance -> {
            var sourceOptional = instance.getLeakageSource();
            if (sourceOptional.isPresent()) {
                var source = sourceOptional.get();
                return source.getLineNumbers().stream().anyMatch(leakageSourceLineNumber -> leakageSourceLineNumber == nodeLineNumber);
            }
            return false;
        };


    }

    public T getInstanceForLeakageSourceAssociatedWithNode(List<T> leakageInstances, @NotNull PsiElement node, @NotNull ProblemsHolder holder) {
        return leakageInstances.stream().filter(leakageSourceAssociatedWithNode(node, holder)).findFirst().get();
    }


    public boolean leakageSourceIsAssociatedWithNode(List<T> leakageInstances, @NotNull PyCallExpression node, @NotNull ProblemsHolder holder) {
        return leakageInstances.stream().anyMatch(leakageSourceAssociatedWithNode(node, holder));
    }

    @NotNull
    public String getInspectionMessageForLeakageSource(Taint taintAssociatedWithLeakageInstance) {
        StringBuilder inspectionMessage = new StringBuilder(InspectionBundle.get(this.getLeakageType().getInspectionTextKey()));
        inspectionMessage.append(" ");

        //get method keyword associated with taint
        Arrays.stream(LeakageSourceKeywordFactory.getSourceKeywordValuesForleakageType(this.getLeakageType())).filter(value -> taintAssociatedWithLeakageInstance.containsText(value.toString()))//TODO: should just be the text on the right side of the period, not the whole thing
                .findFirst().ifPresent(keyword -> inspectionMessage.append(InspectionBundle.get(CauseMapFactory.getPotentialCauses().get(keyword).get(0).getInspectionTextKey())));//TODO: refactor?

        return inspectionMessage.toString();
    }

    public void renderInspectionOnLeakageSource(@NotNull PsiElement node, @NotNull ProblemsHolder holder, List<T> leakageInstances) {
        //TODO: change name?

        leakageInstances.stream().filter(leakageSourceAssociatedWithNode(node, holder)).findFirst().ifPresent(
                instance ->
                {
                    var inspectionMessage = getInspectionMessageForLeakageSource(instance.getLeakageSource().get().findTaintThatMatchesText(node.getFirstChild().getText()));
                    DataLeakageWarningRenderer.renderDataLeakageWarning(instance, node, holder, inspectionMessage, collection);
                }
        );

    }


    public void renderInspectionOnLeakageSource(@NotNull PsiElement node, @NotNull ProblemsHolder holder, List<T> leakageInstances, LocalQuickFix fix) {
//TODO: change name?

        leakageInstances.stream().filter(leakageSourceAssociatedWithNode(node, holder)).findFirst().ifPresent(
                instance -> {
                    var inspectionMessage = getInspectionMessageForLeakageSource(instance.getLeakageSource().get().findTaintThatMatchesText(node.getFirstChild().getText()));
                    DataLeakageWarningRenderer.renderDataLeakageWarning(instance, node, holder, inspectionMessage, fix, collection);
                }
        );


    }

    public void renderInspectionOnTaintForInstanceWithKeyword(@NotNull PyCallExpression node, @NotNull ProblemsHolder holder, U keyword) {

        var taintKeyword = keyword.getTaintKeyword();
        var cause = CauseMapFactory.getPotentialCauses().get(keyword).get(0);

        var key = cause.getInspectionTextKey();//TODO: refactor
//TODO: train test split is not necessarily a taint
        if (node.getText().toLowerCase().contains(taintKeyword)) {//TODO: not the whole node text, just the method itself
            var inspectionMessage = InspectionBundle.get(key);
            DataLeakageWarningRenderer.renderDataLeakageWarning(node, holder, inspectionMessage, collection);
        }
    }

    public void renderInspectionOnTaintWithCause(@NotNull PyCallExpression node, @NotNull ProblemsHolder holder, LeakageCause cause, U keyword) {
        var taintKeyword = keyword.getTaintKeyword();
        var key = cause.getInspectionTextKey();


        if (node.getText().toLowerCase().contains(taintKeyword)) {//TODO: not the whole node text, just the method itself
            var inspectionMessage = InspectionBundle.get(key);
            DataLeakageWarningRenderer.renderDataLeakageWarning(node,
                    holder, inspectionMessage, collection);

        }//TODO: the split call isn't flagged as a taint by the leakage tool, but it is considered as a taint here
    }

    public void renderInspectionOnTaints(@NotNull PyCallExpression node, @NotNull ProblemsHolder holder, List<U> keywords) {
        // for overlap leakage instancesWhoseSourcesHaveDataAugmentation, instancesWhoseSourcesHaveSampling

        keywords.forEach(keyword -> renderInspectionOnTaintForInstanceWithKeyword(node, holder, keyword));

    }


    public Taint getTaintForKeyword(LeakageSource source, @NotNull PsiElement node, U keyword) {

        var taintKeyword = keyword.getTaintKeyword();
        for (var taint : source.getTaints()) {
            if (taint.containsText(taintKeyword)) {
                return taint;
            }
        }
        return null;

    }


}
