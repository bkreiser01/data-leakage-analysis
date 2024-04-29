package com.github.SE4AIResearch.DataLeakage_Fall2023.data;

import static com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageOutput.setFactFolderPath;

public class LeakageResult  {

    private  static String pythonFilePath;
    private static final String exclusionFileName="LinesToExclude.txt";


    public static String getFolderPath() {
        return LeakageOutput.folderPath();
    }

    public static String getPythonFilePath() {
        return pythonFilePath;
    }

    public static void setFilePaths(String pythonFile, String factFolderPath) {
        pythonFilePath = pythonFile;
        setFactFolderPath(factFolderPath);
    }
    public static String getExclusionFileName() {
        return exclusionFileName;
    }


}
