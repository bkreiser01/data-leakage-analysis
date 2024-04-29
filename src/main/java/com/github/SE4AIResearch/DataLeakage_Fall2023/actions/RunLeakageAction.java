package com.github.SE4AIResearch.DataLeakage_Fall2023.actions;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageResult;
import com.github.SE4AIResearch.DataLeakage_Fall2023.docker_api.ConnectClient;
import com.github.SE4AIResearch.DataLeakage_Fall2023.docker_api.FileChanger;
import com.github.SE4AIResearch.DataLeakage_Fall2023.notifiers.LeakageNotifier;
import com.intellij.codeInsight.daemon.DaemonCodeAnalyzer;
import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.fileEditor.FileEditorManager;
import com.intellij.openapi.fileTypes.FileType;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.project.ProjectLocator;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.util.io.FileUtilRt;
import com.intellij.openapi.vfs.VirtualFile;
import org.jetbrains.annotations.NotNull;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Paths;

public class RunLeakageAction extends AnAction {

   private final ConnectClient connectClient = new ConnectClient();
   private final FileChanger fileChanger = new FileChanger();
   private boolean isCompleted;
   private Project project;
//   private boolean isCompleted;

   /**
    * Constructor when created from the IDE (plugin.xml)
    * Not used anymore
    */
   public RunLeakageAction() {
      super("Run Leakage Analysis");
   }

   /**
    * Constructor when created from someplace other than the IDE such as a tool window
    *
    * @param project
    */
   public RunLeakageAction(Project project) {
      super("Run Leakage Analysis");
      this.project = project;
      this.isCompleted = false;
   }

   public boolean isCompleted() {
      return this.isCompleted;
   }

   private static Project getProjectForFile(VirtualFile file) {
      Project project = null;
      if (file != null) {
         project = ProjectLocator.getInstance().guessProjectForFile(file);
      }
      return project;
   }


   @Override
   public @NotNull ActionUpdateThread getActionUpdateThread() {
      return ActionUpdateThread.BGT;
   }

   @Override
   public void update(@NotNull AnActionEvent event) {
      event.getPresentation().setEnabledAndVisible(true);
   }

   @Override
   public void actionPerformed(@NotNull AnActionEvent event) {
      this.isCompleted = false;

      if (this.project == null) {
         try {
            this.project = event.getProject();
         } catch (NullPointerException e) {
//            LeakageNotifier.notifyNotLoaded("Please wait until the project is fully loaded before checking for data leakage");
            Messages.showMessageDialog(
                  "Please wait until the Python file is fully loaded before checking for data leakage.",
                  "",
                  Messages.getInformationIcon());
         }
      }

      runAnalysis();
   }

   private void runAnalysis() {
      VirtualFile file = null;
      String fileName;
      FileType fileType = null;

      FileEditorManager fileEditorManager = FileEditorManager.getInstance(project);

      // Get the currently selected or open file
      VirtualFile[] selectedFiles = fileEditorManager.getSelectedFiles();
      if (selectedFiles.length > 0) {
         file = selectedFiles[0];
      }

      if (file == null) {
         LeakageNotifier.mustBePython(project);
         return;
      }

      fileType = file.getFileType();
      fileName = file.getName();

      if (fileType.getName().equals("Python")) { // check that the file is a python file
         ProgressManager.getInstance().runProcessWithProgressSynchronously(
               createRunnable(file),
               "Running Data Leakage Analysis on " + fileName,
               false,
               project
         );
      } else {
         LeakageNotifier.mustBePython(project);
      }
   }

   private Runnable createRunnable(VirtualFile file) {
      String fileName = file.getName();
      Runnable runLeakage = () -> {
         ProgressIndicator indicator = ProgressManager.getInstance().getProgressIndicator();

         try {
            if (!connectClient.checkImageOnMachine()) {
               try {
                  indicator.setText("Pulling the leakage-analysis docker image");
                  indicator.setText2("Pulling");
                  connectClient.pullImage();
                  indicator.setFraction(0.5);
               } catch (InterruptedException e) {
                  throw new RuntimeException(e);
               }
            }
         } catch (NoClassDefFoundError e) {
            // Wrap UI call around runnable to invokeLater to prevent thread error
            Runnable showMessage = () -> {
               // UI-related code here
               Messages.showErrorDialog(
                     this.project,
                     "Please start the Docker Engine before running leakage analysis.",
                     ""
               );
            };

            SwingUtilities.invokeLater(showMessage);

            return;
         }
         catch (UnsatisfiedLinkError e) {
            // Wrap UI call around runnable to invokeLater to prevent thread error
            Runnable showMessage = () -> {
               // UI-related code here
               Messages.showErrorDialog(
                     this.project,
                     "Please reconfigure or restart the Docker Engine.",
                     ""
               );
            };

            SwingUtilities.invokeLater(showMessage);

            return;
         }

//               if (indicator.isCanceled()) { throw new ProcessCanceledException(); }

         File tempDirectory;

         try {
            if (fileChanger.getTempDirectory() == null) {
               indicator.setText("Creating temporary directory");
               fileChanger.initializeTempDir();
            } else {
               indicator.setText("Cleaning up temporary directory");
               fileChanger.clearTempDir();
            }
            indicator.setFraction(indicator.getFraction() + 0.1);

            tempDirectory = fileChanger.getTempDirectory();

            indicator.setText("Copying " + file.getName() + " to temporary directory");
            fileChanger.copyToTempDir(file.getPath());
            indicator.setFraction(indicator.getFraction() + 0.1);

             var factFolderPath =Paths.get(tempDirectory.getCanonicalPath(), file.getNameWithoutExtension()) + "-fact";
             LeakageResult.setFilePaths(file.getPath(), factFolderPath);



         } catch (IOException e) {
            throw new RuntimeException(e);
         }

         try {
            indicator.setText("Running analysis on " + fileName);
            indicator.setText2("Running");
            try {
               connectClient.runLeakageAnalysis(tempDirectory, fileName);
               isCompleted = true;
            } catch (RuntimeException e) {
               isCompleted = false;
               LeakageNotifier.notifyError(project, "File contains a syntax error");
            }
            indicator.setFraction(1);
         } catch (InterruptedException e) {
            LeakageNotifier.notifyError(project, "Leakage analysis interrupted");
         }

         String exclusionFilePath = Paths.get(LeakageResult.getFolderPath()).resolve(LeakageResult.getExclusionFileName()).toString();
         File exclusionFile = new File(exclusionFilePath);

         FileUtilRt.createIfNotExists(exclusionFile);
         indicator.stop();
      };

      DaemonCodeAnalyzer.getInstance(project).restart();//TODO: restart only for psifile

      return runLeakage;
   }
}

