package com.github.SE4AIResearch.DataLeakage_Fall2023.listeners;

import com.intellij.openapi.editor.Editor;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.TextEditor;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectLocator;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileCreateEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;

public final class Utils {

    //TODO: docs
    public static boolean aCSVFileWasChanged(VFileEvent event) {
        return event.getPath().endsWith(".csv") && (event instanceof VFileContentChangeEvent || event instanceof VFileCreateEvent);
    }

    private static boolean aFactsFileWasChanged(VFileEvent event) {
        return event.getPath().endsWith(".facts") && (event instanceof VFileContentChangeEvent || event instanceof VFileCreateEvent);
    }

    public static Editor getEditorForFileChanged(VFileEvent event) {
        var file = event.getFile();
        var project = getProjectForFile(file);
        TextEditor currentEditor = null;
        if (project != null) {
            currentEditor = (TextEditor) FileEditorManager.getInstance(project).getSelectedEditor();
        }
        Editor editor = null;
        if (currentEditor != null) {
            editor = currentEditor.getEditor();
        }

        return editor;
    }

    private static Project getProjectForFile(VirtualFile file) {
        Project project = null;
        if (file != null) {
            project = ProjectLocator.getInstance().guessProjectForFile(file);
        }
        return project;
    }
}
