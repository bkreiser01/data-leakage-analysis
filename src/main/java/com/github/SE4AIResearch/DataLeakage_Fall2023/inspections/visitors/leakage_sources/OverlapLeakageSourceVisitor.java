package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.leakage_sources;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances.OverlapLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageCause;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.source_keywords.OverlapLeakageSourceKeyword;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.InspectionBundle;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.QuickFixActionNotifier;
import com.github.SE4AIResearch.DataLeakage_Fall2023.inspections.visitors.Utils;
import com.intellij.codeInspection.LocalQuickFix;
import com.intellij.codeInspection.ProblemDescriptor;
import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.codeInspection.util.IntentionFamilyName;
import com.intellij.openapi.editor.Document;
import com.intellij.openapi.editor.markup.RangeHighlighter;
import com.intellij.openapi.project.Project;
import com.intellij.psi.PsiDocumentManager;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiRecursiveElementVisitor;
import com.intellij.util.DocumentUtil;
import com.jetbrains.python.psi.PyCallExpression;
import com.jetbrains.python.psi.PyElementVisitor;
import com.jetbrains.python.psi.PyFunction;
import com.jetbrains.python.psi.PyReferenceExpression;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

/**
 * A type of {@link PyElementVisitor} that visits different types of elements within the PSI tree,
 * such as {@link PyReferenceExpression}s.
 */
public class OverlapLeakageSourceVisitor extends SourceElementVisitor<OverlapLeakageInstance, OverlapLeakageSourceKeyword> {
    private ProblemsHolder holder;
    private List<OverlapLeakageInstance> overlapLeakageInstances;
    private final PsiRecursiveElementVisitor recursiveElementVisitor;

    private final OverlapLeakageQuickFix myQuickFix = new OverlapLeakageQuickFix();

    Collection<RangeHighlighter> collection = new ArrayList<>();


    protected void removeInstance(OverlapLeakageInstance instance) {
        var newArr = new ArrayList<OverlapLeakageInstance>();
        var it = this.overlapLeakageInstances.iterator();
        while (it.hasNext()) {
            if (!it.next().equals(instance)) {
                newArr.add(it.next());

            }
        }
        this.overlapLeakageInstances = newArr;
    }

    public OverlapLeakageSourceVisitor(List<OverlapLeakageInstance> overlapLeakageInstances, @NotNull ProblemsHolder holder) {
        this.overlapLeakageInstances = new ArrayList<>(overlapLeakageInstances);
        this.holder = holder;
        this.recursiveElementVisitor = new PsiRecursiveElementVisitor() {

            @Override
            public void visitElement(@NotNull PsiElement element) {
                //  super.visitElement(element);//TODO:
                renderInspectionOnLeakageSource(element, holder, overlapLeakageInstances, myQuickFix);
            }
        };
        //  this.myQuickFix = new OverlapLeakageQuickFix(overlapLeakageInstances);
    }


    @Override
    public LeakageType getLeakageType() {
        return LeakageType.OverlapLeakage;
    }


    //TODO: consider different making different visitors for performance
    @Override
    public void visitPyCallExpression(@NotNull PyCallExpression node) {//TODO: consider moving this into visitPyReferenceExpression.. will require some refactoring.

        //TODO: extract

        if (!overlapLeakageInstances.isEmpty()) {
            if (leakageSourceIsAssociatedWithNode(overlapLeakageInstances, node, holder)) {

                renderInspectionOnLeakageSource(node, holder, overlapLeakageInstances, myQuickFix);
            }

            renderInspectionOnTaints(node, holder, Arrays.stream(OverlapLeakageSourceKeyword.values()).toList());


        }
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

            var lineNumberOfLeakageSource = descriptor.getLineNumber();

            var psiElement = descriptor.getPsiElement();
            var psiFile = psiElement.getContainingFile();
            PsiDocumentManager documentManager = PsiDocumentManager.getInstance(project);
            Document document = documentManager.getDocument(psiFile);


//TODO: this won't work if assignment is split on multiple lines
            var instance = getInstanceForLeakageSourceAssociatedWithNode(overlapLeakageInstances, psiElement, holder);
            var source = instance.getLeakageSource();//TODO: move

            if (instance.getCause().equals(LeakageCause.SplitBeforeSample)) {
                int offsetOfLeakageSource = document.getLineStartOffset(lineNumberOfLeakageSource);


                int offsetOfSplitCall = getOffsetOfSplitCall(offsetOfLeakageSource);

                swapSplitAndSample(document, offsetOfLeakageSource, offsetOfSplitCall);


                var newStr = "# TODO: Check the arguments provided to the call to split.\n";
                document.insertString(offsetOfLeakageSource,  newStr);

                //Remove split sample from leakage instances
                Utils.removeFixedLinesFromLeakageInstance(project, document, offsetOfLeakageSource, lineNumberOfLeakageSource, offsetOfSplitCall);

                QuickFixActionNotifier publisher = project.getMessageBus()
                        .syncPublisher(QuickFixActionNotifier.QUICK_FIX_ACTION_TOPIC);
                try {
                    // do action
                } finally {
                    publisher.afterAction();
                }
            }


        }

        private void swapSplitAndSample(Document document, int offset, int offsetOfSplitCall) {
            var contentOfSplitCall = getContentOfSplitCall();
            document.replaceString(offsetOfSplitCall, offsetOfSplitCall +
                    contentOfSplitCall.length(), "");

            document.insertString(offset, contentOfSplitCall + "\n");

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
    }
}

