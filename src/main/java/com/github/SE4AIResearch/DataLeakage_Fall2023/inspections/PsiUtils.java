package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections;

import com.intellij.codeInspection.ProblemsHolder;
import com.intellij.lang.injection.InjectedLanguageManager;
import com.intellij.openapi.editor.Document;
import com.intellij.psi.PsiElement;
import com.intellij.psi.PsiFile;
import com.intellij.psi.PsiLanguageInjectionHost;
import org.jetbrains.annotations.NotNull;

public class PsiUtils {

    public static PsiFile getFile(@NotNull ProblemsHolder holder) {
        return holder.getFile();
    }

    public static PsiLanguageInjectionHost getInjectionHost(@NotNull ProblemsHolder holder) {
        var file = getFile(holder);
        return InjectedLanguageManager.getInstance(file.getProject()).getInjectionHost(file);
    }

    public static Document getDocument(@NotNull ProblemsHolder holder) {
        return getFile(holder).getViewProvider().getDocument();
    }

    public static int getNodeLineNumber(PsiElement node, @NotNull ProblemsHolder holder) {
        var offset = node.getTextOffset();
        return getDocument(holder).getLineNumber(offset) + 1; //getLineNumber is zero-based, must add 1
    }

}
