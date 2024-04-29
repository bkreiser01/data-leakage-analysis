package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_instances;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances.MultiTestLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionBundle;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.PsiUtils;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.find.FindManager;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.util.DocumentUtil;
import com.jetbrains.python.psi.PyReferenceExpression;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Predicate;

import static com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionUtils.addLinesToExclusion;

public class MultiTestLeakageInstanceVisitor extends InstanceElementVisitor<MultiTestLeakageInstance> {
    private final List<MultiTestLeakageInstance> multiTestLeakageInstances;
    private final PsiRecursiveElementVisitor recursiveElementVisitor;
    private final MultiTestLeakageQuickFix myQuickFix = new MultiTestLeakageQuickFix();

    public MultiTestLeakageInstanceVisitor(List<MultiTestLeakageInstance> multiTestLeakageInstances, @NotNull ProblemsHolder holder) {
        this.multiTestLeakageInstances = multiTestLeakageInstances;
        this.holder = holder;
        this.recursiveElementVisitor = new PsiRecursiveElementVisitor() {

            @Override
            public void visitElement(@NotNull PsiElement element) {
                //  super.visitElement(element);//TODO:
                renderInspectionOnLeakageInstance(multiTestLeakageInstances, element, myQuickFix);
            }
        };
    }


    @Override
    public LeakageType getLeakageType() {
        return LeakageType.MultiTestLeakage;
    }

    @Override
    public Predicate<MultiTestLeakageInstance> leakageInstanceIsAssociatedWithNode(@NotNull PsiElement node) {

        return instance -> (instance.lineNumber() == PsiUtils.getNodeLineNumber(node, holder))
                && instance.variableName().contains(node.getText());

    }

    @Override
    public void visitPyReferenceExpression(@NotNull PyReferenceExpression node) {
        renderInspectionOnLeakageInstance(multiTestLeakageInstances, node, myQuickFix);
    }

    class MultiTestLeakageQuickFix implements LocalQuickFix {
        @NotNull
        @Override
        public String getName() {
            return InspectionBundle.get("inspectionText.removeRedundantTestEvaluations.quickfix.text");
        }

        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return getName();
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {
            var instance = descriptor.getPsiElement();


            FindManager myFindManager = FindManager.getInstance(project);


            var psiElement = descriptor.getPsiElement();
            var psiFile = psiElement.getContainingFile();
            PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
            Document document = documentManager.getDocument(psiFile);
            var lineNumber = descriptor.getLineNumber();

            int offset = document.getLineStartOffset(lineNumber);
            var lineNumbersToRemove = new ArrayList<Integer>();

            renameVariablesInDocument(document, instance, lineNumbersToRemove);

            lineNumbersToRemove.add(document.getLineNumber(offset) + 1);
            addLinesToExclusion(lineNumbersToRemove);

            DaemonCodeAnalyzer.getInstance(project).restart();
        }

        private void renameVariablesInDocument(Document document, PsiElement instance, ArrayList<Integer> lineNumbersToRemove) {
            for (int i = 0; i < multiTestLeakageInstances.size(); i++) {
                //Doesn't always reload contents of document from disk
                var inst = multiTestLeakageInstances.get(i);
                var lineNumber = inst.lineNumber() - 1;

                var lineTextRange = DocumentUtil.getLineTextRange(document, lineNumber);
                var lineContent = document.getText(lineTextRange);
                var newStr = "# TODO: load the test data for the evaluation.\n" + lineContent.replace(instance.getText(), instance.getText() + "_" + i);
                document.replaceString(document.getLineStartOffset(lineNumber), document.getLineEndOffset(lineNumber), newStr);
                lineNumbersToRemove.add(lineNumber);

            }
        }


    }


}
