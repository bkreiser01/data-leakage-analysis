package com.github.SE4AIResearch.DataLeakage_Fall2023.listeners;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectLocator;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileContentChangeEvent;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class FileChangeDetector implements BulkFileListener {

    public FileChangeDetector() {

    }

    @Override
    public void after(@NotNull List<? extends @NotNull VFileEvent> events) {
        for (VFileEvent event : events) {
            //TODO: run leakage analysis

        }

    }

    private static boolean theChangedFileIsInTheCurrentProject(VirtualFile file) {
        var project = getProjectForFile(file);
        return project != null && ProjectFileIndex.getInstance(project).isInProject(file);
    }

    private boolean theChangedFileIsCurrentlyBeingEdited(VFileEvent event) {
        var editor = Utils.getEditorForFileChanged(event);
        if (editor == null) {
            return false;
        }
        var fileBeingEdited = editor.getVirtualFile();

        return fileBeingEdited.equals(event.getFile());
    }


    private boolean aPythonFileWasChanged(VFileEvent event) {
        return event.getPath().endsWith(".py") && event instanceof VFileContentChangeEvent;
    }


    private static Project getProjectForFile(VirtualFile file) {
        Project project = null;
        if (file != null) {
            project = ProjectLocator.getInstance().guessProjectForFile(file);
        }
        return project;
    }

}
