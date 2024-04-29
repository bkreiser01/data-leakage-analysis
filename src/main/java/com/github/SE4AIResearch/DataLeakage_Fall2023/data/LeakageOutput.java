package com.github.SE4AIResearch.DataLeakage_Fall2023.data;

/**
 * This record contains the absolute path to the output of the leakage analysis tool.
 */
 class LeakageOutput {
    private static String folderPath = "";

    protected static String folderPath() {
        return folderPath;
    }

    protected static void setFactFolderPath(String path) {

        folderPath = path;
    }


}
