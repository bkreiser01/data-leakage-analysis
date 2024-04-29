package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_sources;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances.PreprocessingLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.source_keywords.PreprocessingLeakageSourceKeyword;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionBundle;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.PsiUtils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.QuickFixActionNotifier;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.Utils;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyFunction;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

public class PreprocessingLeakageSourceVisitor extends SourceElementVisitor<PreprocessingLeakageInstance, PreprocessingLeakageSourceKeyword> {
    private final List<PreprocessingLeakageInstance> preprocessingLeakageInstances;
    public PsiRecursiveElementVisitor recursiveElementVisitor;
    Collection<RangeHighlighter> collection = new ArrayList<>();
    private final ProblemsHolder holder;
    private final PreprocessingLeakageQuickFix myQuickFix;


    public PreprocessingLeakageSourceVisitor(List<PreprocessingLeakageInstance> preprocessingLeakageInstances, @NotNull ProblemsHolder holder) {
        this.preprocessingLeakageInstances = preprocessingLeakageInstances;
        this.holder = holder;
        this.myQuickFix = new PreprocessingLeakageQuickFix();
        this.recursiveElementVisitor = new PsiRecursiveElementVisitor() {
            @Override
            public void visitElement(@NotNull PsiElement element) {
                // super.visitElement(element);//TODO: do we need this?
                renderInspectionOnLeakageSource(element, holder, preprocessingLeakageInstances, myQuickFix);
            }
        };
    }

    @Override
    public LeakageType getLeakageType() {
        return LeakageType.PreprocessingLeakage;
    }


    @Override
    public void visitPyFunction(@NotNull PyFunction node) {
        this.recursiveElementVisitor.visitElement(node);

    }


    //TODO: consider different making different visitors for performance
    @Override
    public void visitPyCallExpression(@NotNull PyCallExpression node) {//TODO: consider moving this into visitPyReferenceExpression.. will require some refactoring.

        //TODO: extract

        if (!preprocessingLeakageInstances.isEmpty()) {
            if (leakageSourceIsAssociatedWithNode(preprocessingLeakageInstances, node, holder)) {
                if (holder.getResults().stream().noneMatch(problemDescriptor -> problemDescriptor.getLineNumber() + 1/*need plus one to account for zero based line number*/ == PsiUtils.getNodeLineNumber(node, holder))) {//TODO: naive solution, should refactor to look more closely at method calls. need to check if the correct psi element is being highlighted
                    renderInspectionOnLeakageSource(node, holder, preprocessingLeakageInstances, myQuickFix);
                }
            }


//            var keywords = Arrays.stream(PreprocessingLeakageSourceKeyword.values()).toList();
//            for (PreprocessingLeakageSourceKeyword keyword : keywords) {
//                preprocessingLeakageInstances.stream().filter(leakageSourceAssociatedWithNode(node)).findFirst().ifPresent(instance -> renderInspectionOnTaintWithCause(node, holder, instance.getLeakageSource().getCause(), keyword));
//
//
//            }
        }
    }

    private class PreprocessingLeakageQuickFix implements LocalQuickFix {

        @NotNull
        @Override
        public String getName() {
            return InspectionBundle.get("inspectionText.vectorizingTextData.quickfix.text");
        }

        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return getName();
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {


            var psiElement = descriptor.getPsiElement();
            var psiFile = psiElement.getContainingFile();
            PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
            Document document = documentManager.getDocument(psiFile);

            var lineNumber = descriptor.getLineNumber();
            int offset = document.getLineStartOffset(lineNumber);

            var potentialOffsetOfSplitCall = locatePotentialSplitCall(document, offset, lineNumber);
            //TODO: this won't work if assignment is split on multiple lines
            var potentialSplitCall =
                    document.getText(new TextRange(potentialOffsetOfSplitCall, document.getLineEndOffset(lineNumber + 1)));

            moveSplitCallIfItExists(potentialSplitCall, document, potentialOffsetOfSplitCall, offset);
            var newStr = "# TODO: Check the arguments provided to the call to split.\n";
            document.insertString(offset,  newStr);
            Utils.removeFixedLinesFromLeakageInstance(project, document, offset, lineNumber, potentialOffsetOfSplitCall);

            QuickFixActionNotifier publisher = project.getMessageBus()
                    .syncPublisher(QuickFixActionNotifier.QUICK_FIX_ACTION_TOPIC);
            try {
                // do action
            } finally {
                publisher.afterAction();
            }
        }

        private static void moveSplitCallIfItExists(String potentialSplitCall, Document document, int potentialOffsetOfSplitCall, int offset) {
            if (potentialSplitCall != null && potentialSplitCall.contains("split")) {

                document.replaceString(potentialOffsetOfSplitCall, potentialOffsetOfSplitCall +
                        potentialSplitCall.length(), "");

                document.insertString(offset, potentialSplitCall + "\n");


            } else {


                document.insertString(offset, "split()\n");
            }
        }

        private int locatePotentialSplitCall(Document document, int offset, int lineNumber) {
            int potentialOffsetOfSplitCall = document.getLineStartOffset(lineNumber + 1);

            return potentialOffsetOfSplitCall;

        }


    }


}
