package com.github.SE4AIResearch.DataLeakage_Fall2023.common_utils;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.Invocation;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageResult;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class Utils {

    //TODO: write better docs, remove circular dependency

    /**
     * Looks through "LinenoMapping.facts" to find the actual line number that corresponds to an internal line number.
     * The "actual line number" is the line number of the end user's code.
     * The "internal line number" is meaningless to the end user.
     *
     * @param folderPath         The location of the leakage analysis tool output that contains "InvokeLineno.facts".
     * @param internalLineNumber A number used within and provided by the leakage analysis tool.
     * @return An {@code int} representing an actual line number in the user's code.
     */
    public static int getActualLineNumberFromInternalLineNumber(String folderPath, int internalLineNumber) {
        String filePath = Paths.get(folderPath).resolve("LinenoMapping.facts").toString();
//        File file = new File(folderPath + "LinenoMapping.facts");
        File file = new File(filePath);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            int actualLineNumber = 0;

            while ((line = reader.readLine()) != null) {

                String[] columns = line.split(("\t"));

                if (Integer.parseInt(columns[0]) == internalLineNumber) {

                    actualLineNumber = Integer.parseInt(columns[1]);
                    break;
                }


            }
            reader.close();
            return actualLineNumber;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return 0;
    }

    public static int getActualLineNumberFromInvocation(Invocation invocation) {

        int internalLineNumber = Invocation.getInternalLineNumberFromInvocation(LeakageResult.getFolderPath(), invocation);
        return getActualLineNumberFromInternalLineNumber(LeakageResult.getFolderPath(), internalLineNumber);
    }

    public static List<Integer> linesOnExclusionList() {
        String exclusionFilePath = Paths.get(LeakageResult.getFolderPath()).resolve(LeakageResult.getExclusionFileName()).toString();
        File file = new File(exclusionFilePath);


        List<Integer> linesToExclude = new ArrayList<>();
        if (file.exists()) {
            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));

                String line;
                while ((line = reader.readLine()) != null) {
                    try {
                        linesToExclude.add(Integer.parseInt(line.strip()));
                    } catch (NumberFormatException e) {
                        //ignore
                    }


                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return linesToExclude;
    }
}
