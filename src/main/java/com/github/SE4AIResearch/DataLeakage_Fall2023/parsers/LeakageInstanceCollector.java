package com.github.SE4AIResearch.DataLeakage_Fall2023.parsers;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.leakage_detectors.LeakageDetector;
import com.github.SE4AIResearch.DataLeakage_Fall2023.leakage_detectors.MultiTestLeakageDetector;
import com.github.SE4AIResearch.DataLeakage_Fall2023.leakage_detectors.OverlapLeakageDetector;
import com.github.SE4AIResearch.DataLeakage_Fall2023.leakage_detectors.PreprocessingLeakageDetector;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileEditor.FileEditorManagerListener;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectManager;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.wm.WindowManager;
import org.jetbrains.annotations.NotNull;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;

public class LeakageInstanceCollector {
    private final List<LeakageDetector<? extends LeakageInstance>> leakageDetectors;
    private List<LeakageInstance> leakageInstances;

    private Project project;

    public LeakageInstanceCollector() {
        setProject();

        if (project != null) {
            project.getMessageBus().connect().subscribe(FileEditorManagerListener.FILE_EDITOR_MANAGER, new FileEditorManagerListener() {
                @Override
                public void fileClosed(@NotNull FileEditorManager source, @NotNull VirtualFile file) {
                    clearLeakageInstances();
                }

            });
        }
        this.leakageDetectors = new ArrayList<>();
        leakageDetectors.add(new OverlapLeakageDetector());
        leakageDetectors.add(new MultiTestLeakageDetector());
        leakageDetectors.add(new PreprocessingLeakageDetector());
        this.leakageInstances = LeakageInstances();
    }

    private void setProject() {
        ApplicationManager.getApplication().invokeLater(() -> {
            Project[] projects = ProjectManager.getInstance().getOpenProjects();

            Project activeProject = null;
            for (Project project : projects) {

                Window window = WindowManager.getInstance().suggestParentWindow(project);
                if (window != null && window.isActive()) {
                    activeProject = project;
                }
            }
            this.project = activeProject;

        });

    }

    private void clearLeakageInstances() {
        this.leakageInstances = new ArrayList<>();
    }

    public boolean isLeakageDetected() {
        for (var detector : leakageDetectors) {
            if (detector.isLeakageDetected()) {
                return true;
            }
        }
        return false;
    }

    public List<LeakageInstance> LeakageInstances() {
        List<LeakageInstance> instances = new ArrayList<>();
        for (var detector : leakageDetectors) {
            instances.addAll(detector.FindLeakageInstances());
        }
        return instances;

    }

    public List<LeakageInstance> GetLeakageInstances() {
        return this.leakageInstances;
    }

    public List<LeakageInstance> SetLeakageInstances(List<LeakageInstance> leakageInstances) {
        return this.leakageInstances;
    }

}

