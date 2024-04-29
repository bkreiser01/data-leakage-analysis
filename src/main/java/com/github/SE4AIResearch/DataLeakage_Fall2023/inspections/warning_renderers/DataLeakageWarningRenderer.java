package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.warning_renderers;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.PsiUtils;
import com.intellij.codeInsight.highlighting.HighlightManager;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemHighlightType;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.editor.colors.EditorColors;
import com.intellij.openapi.editor.colors.TextAttributesKey;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.util.PsiEditorUtil;
import org.jetbrains.annotations.NotNull;

import java.util.Collection;

import static com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionUtils.anyLinesAreOnExclusionList;

public class DataLeakageWarningRenderer {

    public static void renderDataLeakageWarning(LeakageInstance instance, PsiElement node, @NotNull ProblemsHolder holder, String inspectionMessage, Collection<RangeHighlighter> collection) {
        int startoffset = node.getTextRange().getStartOffset();
        int endoffset = node.getTextRange().getEndOffset();
        Editor editor = PsiEditorUtil.findEditor(node); //Project curr_project = project[0];
        PsiFile containingFile = node.getContainingFile();
        Project project = containingFile.getProject();
        if (!

                anyLinesAreOnExclusionList(instance, PsiUtils.getNodeLineNumber(node, holder))) {
            holder.registerProblem(node, inspectionMessage, ProblemHighlightType.WARNING);

            highlight(project, editor, startoffset, endoffset, collection);
        }
    }

    public static void renderDataLeakageWarning(PsiElement node, @NotNull ProblemsHolder holder, String inspectionMessage, Collection<RangeHighlighter> collection) {
        int startoffset = node.getTextRange().getStartOffset();
        int endoffset = node.getTextRange().getEndOffset();
        Editor editor = PsiEditorUtil.findEditor(node); //Project curr_project = project[0];
        PsiFile containingFile = node.getContainingFile();
        Project project = containingFile.getProject();
        if (!

                anyLinesAreOnExclusionList(PsiUtils.getNodeLineNumber(node, holder))) {
            holder.registerProblem(node, inspectionMessage, ProblemHighlightType.WARNING);

            highlight(project, editor, startoffset, endoffset, collection);
        }
    }

    public static void renderDataLeakageWarning(LeakageInstance instance, PsiElement node, @NotNull ProblemsHolder holder, String inspectionMessage, LocalQuickFix fix, Collection<RangeHighlighter> collection) {
        int startoffset = node.getTextRange().getStartOffset();
        int endoffset = node.getTextRange().getEndOffset();
        Editor editor = PsiEditorUtil.findEditor(node); //Project curr_project = project[0];
        PsiFile containingFile = node.getContainingFile();
        Project project = containingFile.getProject();
        if (!

                anyLinesAreOnExclusionList(instance, PsiUtils.getNodeLineNumber(node, holder))) {
            holder.registerProblem(node, inspectionMessage, ProblemHighlightType.WARNING, fix);

            highlight(project, editor, startoffset, endoffset, collection);
        }
    }
    public static void renderDataLeakageWarning(LeakageInstance instance, PsiElement node, @NotNull ProblemsHolder holder, LocalQuickFix fix, Collection<RangeHighlighter> collection) {
        int startoffset = node.getTextRange().getStartOffset();
        int endoffset = node.getTextRange().getEndOffset();
        Editor editor = PsiEditorUtil.findEditor(node); //Project curr_project = project[0];
        PsiFile containingFile = node.getContainingFile();
        Project project = containingFile.getProject();
        if (!

                anyLinesAreOnExclusionList(instance, PsiUtils.getNodeLineNumber(node, holder))) {
            holder.registerProblem(node, "", ProblemHighlightType.WARNING, fix);

            highlight(project, editor, startoffset, endoffset, collection);
        }
    }

    public static void renderDataLeakageWarning(PsiElement node, @NotNull ProblemsHolder holder, String inspectionMessage, LocalQuickFix fix, Collection<RangeHighlighter> collection) {
        int startoffset = node.getTextRange().getStartOffset();
        int endoffset = node.getTextRange().getEndOffset();
        Editor editor = PsiEditorUtil.findEditor(node); //Project curr_project = project[0];
        PsiFile containingFile = node.getContainingFile();
        Project project = containingFile.getProject();
        if (!

                anyLinesAreOnExclusionList(PsiUtils.getNodeLineNumber(node, holder))) {
            holder.registerProblem(node, inspectionMessage, ProblemHighlightType.WARNING, fix);

            highlight(project, editor, startoffset, endoffset, collection);
        }
    }

    private static void highlight(Project project, Editor editor, int startoffset, int endoffset, Collection<RangeHighlighter> collection) {
        HighlightManager h1 = HighlightManager.getInstance(project);
        TextAttributesKey betterColor = EditorColors.SEARCH_RESULT_ATTRIBUTES;
        //Project curr_project = project[0];

        h1.addOccurrenceHighlight(editor, startoffset, endoffset, betterColor, 001, collection);

    }

}
