package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_instances;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances.PreprocessingLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionBundle;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.PsiUtils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.QuickFixActionNotifier;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.Utils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.warning_renderers.DataLeakageWarningRenderer;
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
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyNamedParameter;
import com.jetbrains.python.psi.PyReferenceExpression;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.function.Predicate;

public class PreprocessingLeakageInstanceVisitor extends InstanceElementVisitor<PreprocessingLeakageInstance> {
    private final List<PreprocessingLeakageInstance> preprocessingLeakageInstances;
    private final PsiRecursiveElementVisitor recursiveElementVisitor;
    private final PreprocessingLeakageQuickFix myQuickFix;

    Collection<RangeHighlighter> collection = new ArrayList<>();

    public PreprocessingLeakageInstanceVisitor(List<PreprocessingLeakageInstance> preprocessingLeakageInstances, @NotNull ProblemsHolder holder) {
        this.preprocessingLeakageInstances = preprocessingLeakageInstances;
        this.holder = holder;
        this.myQuickFix = new PreprocessingLeakageQuickFix();
        this.recursiveElementVisitor = new PsiRecursiveElementVisitor() {

            @Override
            public void visitElement(@NotNull PsiElement element) {
                //  super.visitElement(element);//TODO:
                renderInspectionOnLeakageInstance(preprocessingLeakageInstances, element, myQuickFix);
            }
        };
    }

    @Override
    public LeakageType getLeakageType() {
        return LeakageType.PreprocessingLeakage;
    }

    @Override
    public Predicate<PreprocessingLeakageInstance> leakageInstanceIsAssociatedWithNode(@NotNull PsiElement node) {
        var nodeLineNumber = PsiUtils.getNodeLineNumber(node, holder);
        return instance -> (instance.lineNumber() == nodeLineNumber) && (instance.variableName().contains(node.getText())); //TODO: make sure it's ok to have text and not name
    }


    @Override
    public void visitPyReferenceExpression(@NotNull PyReferenceExpression node) {
//      var log=  Logger.getInstance(PreprocessingLeakageInstanceVisitor.class);
//        log.warn("HERE***********************************************************");
        renderInspectionOnLeakageInstance(preprocessingLeakageInstances, node, myQuickFix);

    }


    @Override
    public void visitPyNamedParameter(@NotNull PyNamedParameter node) {

        if (leakageIsAssociatedWithNode(preprocessingLeakageInstances, node)) {
            var inspectionMessage = InspectionBundle.get(LeakageType.PreprocessingLeakage.getInspectionTextKey());
            DataLeakageWarningRenderer.renderDataLeakageWarning(node, holder, inspectionMessage, collection);

        }

    }

    @Override
    public void visitPyFunction(@NotNull PyFunction node) {
        this.recursiveElementVisitor.visitElement(node);

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

            var instance = getLeakageInstanceAssociatedWithNode(preprocessingLeakageInstances, psiElement);
            var source = instance.getLeakageSource().get();

            var lineNumbers = source.getLineNumbers();
            for(var lineNumber : lineNumbers) {
                lineNumber--; //need this line
                int offset = document.getLineStartOffset(lineNumber);

                var potentialOffsetOfSplitCall = locatePotentialSplitCall(document, offset, lineNumber);
                //TODO: this won't work if assignment is split on multiple lines
                var potentialSplitCall =
                        document.getText(new TextRange(potentialOffsetOfSplitCall, document.getLineEndOffset(lineNumber + 1)));

                moveSplitCallIfItExists(potentialSplitCall, document, potentialOffsetOfSplitCall, offset);
                var newStr = "# TODO: Check the arguments provided to the call to split.\n";
                document.insertString(offset, newStr);
                Utils.removeFixedLinesFromLeakageInstance(project, document, offset, lineNumber, potentialOffsetOfSplitCall);
            }
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
