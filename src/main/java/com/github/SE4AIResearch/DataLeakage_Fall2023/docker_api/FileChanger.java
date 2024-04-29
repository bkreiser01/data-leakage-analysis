package com.github.SE4AIResearch.DataLeakage_Fall2023.docker_api;

import com.intellij.openapi.util.io.FileUtilRt;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;

public class FileChanger {
    File tempDirectory;
    public FileChanger() {
        this.tempDirectory = null;
    }

    public String initializeTempDir() throws IOException {
        tempDirectory = FileUtilRt.createTempDirectory("leakTemp", "");
        return this.tempDirectory.toString();
    }

    public String copyToTempDir(String filePathString) throws IOException {
        File fileToCopy = Paths.get(filePathString).toFile();
        String fileName = fileToCopy.getName();
        File destinationFile = FileUtilRt.createTempFile(tempDirectory, fileName, "");
        FileUtilRt.copy(fileToCopy, destinationFile);
        return fileName;
    }

    public boolean deleteTempDir() throws IOException {
        if (tempDirectory == null) {
            return false;
        }
        Path workingDirPath = this.tempDirectory.toPath();
        FileUtilRt.deleteRecursively(workingDirPath);
        return FileUtilRt.delete(this.tempDirectory);
    }

    public boolean clearTempDir() throws IOException {
        if(tempDirectory == null) {
            return false;
        }
        Path workingDirPath = this.tempDirectory.toPath();
        FileUtilRt.deleteRecursively(workingDirPath);
        return true;
    }

    public boolean deleteAllTempDir() throws IOException {
        Path workingDirPath = this.tempDirectory.toPath();
        Path parentPath = workingDirPath.getParent();
        return false;
    }

    public File getTempDirectory() {
        return tempDirectory;
    }


}
