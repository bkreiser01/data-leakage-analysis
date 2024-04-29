package com.github.SE4AIResearch.DataLeakage_Fall2023.listeners;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageResult;
import com.github.SE4AIResearch.DataLeakage_Fall2023.docker_api.ConnectClient;
import com.github.SE4AIResearch.DataLeakage_Fall2023.docker_api.FileChanger;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectLocator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.BulkFileListener;
import com.intellij.openapi.vfs.newvfs.events.VFileEvent;
import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public class PythonFileSaveListener implements BulkFileListener {
    ConnectClient connectClient;
    FileChanger fileChanger;

    public PythonFileSaveListener() {
        this.connectClient = new ConnectClient();
        this.fileChanger = new FileChanger();
    }

    private static Project getProjectForFile(VirtualFile file) {
        Project project = null;
        if (file != null) {
            project = ProjectLocator.getInstance().guessProjectForFile(file);
        }
        return project;
    }

    @Override
    public void after(@NotNull List<? extends @NotNull VFileEvent> events) {
        for (VFileEvent event : events) {
            if (event.isFromSave()) { // Check if the event is a save event
                FileType fileType = null;
                VirtualFile file = event.getFile();
                if (file != null) {
                    fileType = file.getFileType();
                }
                if (fileType != null && fileType.getName().equals("Python")) { // check that the saved file is a python file
                    // TODO: run leakage analysis code on the file
                    try {
                        connectClient.checkThenPull();
                    } catch (InterruptedException e) {
                        throw new RuntimeException(e);
                    }
                    // TODO: delete previous temp directory contents

                    File tempDirectory;
                    String fileName;
                    try {
                        fileChanger.initializeTempDir();
                        tempDirectory = fileChanger.getTempDirectory();
                        fileName = fileChanger.copyToTempDir(file.getPath());
                        LeakageResult.setFilePaths(file.getPath(),Paths.get(tempDirectory.getCanonicalPath(), file.getNameWithoutExtension()) + "-fact\\");
                    } catch (IOException e) {
                        throw new RuntimeException(e);
                    }
                        Messages.showMessageDialog(
                                getProjectForFile(event.getFile()),
                                "Your code is being analyzed for data leakage. You may close this dialog window.",
                                "",
                                Messages.getInformationIcon());
                    if (tempDirectory != null && fileName != null) {

//                        try {
//                            connectClient.runLeakageAnalysis(tempDirectory, fileName);
//                        } catch (InterruptedException e) {
//                            throw new RuntimeException(e);
//                        }

                    }



                    // TODO: delete container after running

                }
            }
        }
    }

}
