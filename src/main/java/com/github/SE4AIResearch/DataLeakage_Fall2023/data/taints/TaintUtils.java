package com.github.SE4AIResearch.DataLeakage_Fall2023.data.taints;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageResult;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.taints.TaintLabel;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class TaintUtils {

    public static List<String> getTaintsFromFile(TaintLabel taintType) {
        String filePath = Paths.get(LeakageResult.getFolderPath()).resolve("TaintStartsTarget.csv").toString();
//        File file = new File(LeakageResult.getFolderPath() + "TaintStartsTarget.csv");
        File file = new File(filePath);
        List<String> taints = new ArrayList<>();
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;

            while (((line = reader.readLine()) != null)) {
                String[] columns = line.split(("\t"));
                if (Objects.equals(columns[6], taintType.toString())) {

                    taints.add( line);
                }

            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();

        }
        return taints;
    }
}
