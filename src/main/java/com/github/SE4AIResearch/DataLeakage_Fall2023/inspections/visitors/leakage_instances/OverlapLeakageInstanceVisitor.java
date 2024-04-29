package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_instances;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances.OverlapLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageCause;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionBundle;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.PsiUtils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.QuickFixActionNotifier;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.Utils;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.util.TextRange;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.jetbrains.python.psi.PyElementVisitor;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyReferenceExpression;
import org.jetbrains.annotations.NotNull;

import java.util.List;
import java.util.function.Predicate;

/**
 * A type of {@link PyElementVisitor} that visits different types of elements within the PSI tree,
 * such as {@link PyReferenceExpression}s.
 */
public class OverlapLeakageInstanceVisitor extends InstanceElementVisitor<OverlapLeakageInstance> {
    private final List<OverlapLeakageInstance> overlapLeakageInstances;
    private final PsiRecursiveElementVisitor recursiveElementVisitor;
    private final OverlapLeakageQuickFix myQuickFix;

    public OverlapLeakageInstanceVisitor(List<OverlapLeakageInstance> overlapLeakageInstances, @NotNull ProblemsHolder holder) {
        this.overlapLeakageInstances = overlapLeakageInstances;
        this.holder = holder;
        this.myQuickFix = new OverlapLeakageQuickFix();
        this.recursiveElementVisitor = new PsiRecursiveElementVisitor() {

            @Override
            public void visitElement(@NotNull PsiElement element) {
                //  super.visitElement(element);//TODO:
                renderInspectionOnLeakageInstance(overlapLeakageInstances, element, myQuickFix);
            }
        };
    }


    @Override
    public LeakageType getLeakageType() {
        return LeakageType.OverlapLeakage;
    }

    @Override
    public Predicate<OverlapLeakageInstance> leakageInstanceIsAssociatedWithNode(@NotNull PsiElement node) {
        var nodeLineNumber = PsiUtils.getNodeLineNumber(node, holder);

        return instance -> (instance.lineNumber() == nodeLineNumber) &&
                (instance.variableName().contains(node.getText())
                        || (instance.train().contains(node.getText())));
        //Objects.equals(instance.test(), node.getText()); //TODO: make sure it's ok to have text and not name

    }

    public List<OverlapLeakageInstance> getLeakageInstances() {
        return overlapLeakageInstances;
    }

    @Override
    public void visitPyReferenceExpression(@NotNull PyReferenceExpression node) {

        renderInspectionOnLeakageInstance(overlapLeakageInstances, node, myQuickFix);
    }

    @Override
    public void visitPyFunction(@NotNull PyFunction node) {
        this.recursiveElementVisitor.visitElement(node);

    }


    private class OverlapLeakageQuickFix implements LocalQuickFix {


        public OverlapLeakageQuickFix() {
        }

        @NotNull
        @Override
        public String getName() {
            return InspectionBundle.get("inspectionText.swapSplitAndSample.quickfix.text");
        }

        @Override
        public @IntentionFamilyName @NotNull String getFamilyName() {
            return getName();
        }

        @Override
        public void applyFix(@NotNull Project project, @NotNull ProblemDescriptor descriptor) {

            var lineNumberOfLeakageInstance = descriptor.getLineNumber();

            var psiElement = descriptor.getPsiElement();
            var psiFile = psiElement.getContainingFile();
            PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
            Document document = documentManager.getDocument(psiFile);


//TODO: this won't work if assignment is split on multiple lines
            var instance = getLeakageInstanceAssociatedWithNode(overlapLeakageInstances, psiElement);
            var sourceLineNumbers = instance.getLeakageSource().get().getLineNumbers();//TODO: move

            if (instance.getCause().equals(LeakageCause.SplitBeforeSample)) {
                for (var lineNumber : sourceLineNumbers) {

                    int offsetOfLeakageSource = document.getLineStartOffset(lineNumber);


                    int potentialOffsetOfSplitCall = offsetOfLeakageSource;
                    var potentialSplitCall =
                            document.getText(new TextRange(potentialOffsetOfSplitCall, document.getLineEndOffset(lineNumber + 1)));

                    moveSplitCallIfItExists(potentialSplitCall, document, potentialOffsetOfSplitCall, document.getLineStartOffset(lineNumber-1));

                    //  swapSplitAndSample(document, offsetOfLeakageSource, offsetOfSplitCall,lineNumber);


                    var newStr = "# TODO: Check the arguments provided to the call to split.\n";
                    document.insertString(document.getLineStartOffset(lineNumber-1), newStr);

                    //Remove split sample from leakage instances
                    Utils.removeFixedLinesFromLeakageInstance(project, document, offsetOfLeakageSource, lineNumber, potentialOffsetOfSplitCall);
                }
                QuickFixActionNotifier publisher = project.getMessageBus()
                        .syncPublisher(QuickFixActionNotifier.QUICK_FIX_ACTION_TOPIC);
                try {
                    // do action
                } finally {
                    publisher.afterAction();
                }
            }


        }




        @NotNull
        private String getContentOfSplitCall() {
            return holder.getResults().stream().map(
                    problem -> problem.getPsiElement().getParent().getText()
            ).filter(taint -> taint.toLowerCase().contains("split")).findFirst().get();
        }

        @NotNull
        private Integer getOffsetOfSplitCall(int offset) {
            return holder.getResults().stream().map(
                            problem -> problem.getPsiElement().getParent()
                    ).filter(taint -> taint.getText().toLowerCase().contains("split"))
                    .map(taint -> taint.getTextOffset()).filter(splitOffset -> splitOffset > offset).findFirst().get();
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
