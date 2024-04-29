package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections;

import com.github.SE4AIResearch.DataLeakage_Fall2023.common_utils.Utils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageResult;
import com.intellij.openapi.util.io.FileUtilRt;

import java.io.*;
import java.nio.file.Paths;
import java.util.List;

public class InspectionUtils {
    public static void addLinesToExclusion(List<Integer> lines) {
        // File destinationFile = new File(String.valueOf(Paths.get(LeakageResult.getFolderPath()).resolve(LeakageOutput.getExclusionFileName())));


        String exclusionFilePath = Paths.get(LeakageResult.getFolderPath()).resolve(LeakageResult.getExclusionFileName()).toString();
        File exclusionFile = new File(exclusionFilePath);

        FileUtilRt.createIfNotExists(exclusionFile);


        try {
            FileWriter fr = new FileWriter(exclusionFile.getPath(), true);
            for (var line : lines) {
                fr.write(line.toString());
                fr.write("\n");

            }
            fr.close();

        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public static boolean anyLinesAreOnExclusionList(int nodeLineNumber) {
        List<Integer> linesOnExlcusionList = Utils.linesOnExclusionList();


        return linesOnExlcusionList.contains(nodeLineNumber);
    }

    public static boolean anyLinesAreOnExclusionList(LeakageInstance leakageInstance, int nodeLineNumber) {
        List<Integer> linesOnExlcusionList = Utils.linesOnExclusionList();

        if (linesOnExlcusionList.contains(leakageInstance.lineNumber())) {
            return true;
        }
        if (linesOnExlcusionList.contains(nodeLineNumber)) {
            return true;
        }

        var sourceOptional = leakageInstance.getLeakageSource();
        if (sourceOptional.isPresent()) {
            for (Integer lineNo : sourceOptional.get().getLineNumbers()) {
                if (linesOnExlcusionList.contains(lineNo)) {
                    return true;
                }
            }
        }
        return false;
    }


}

