package com.github.SE4AIResearch.DataLeakage_Fall2023.data;


import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.Arrays;
import java.util.regex.Pattern;

public class Utils {

    public static String stripSuffixFromVariableName(String variableNameWithSuffix) {
        final Pattern variablePattern = Pattern.compile(".*_[0-9]+");

        var matcher = variablePattern.matcher(variableNameWithSuffix);

        if (matcher.find()) {
            var split = variableNameWithSuffix.split("_");

            return String.join("_", Arrays.stream(split).toList().subList(0, split.length - 1));
        } else {
            return variableNameWithSuffix;
        }
    }


    public static String getTrainFromPreprocessingLeakTelemetryFile() {
        String filePath = Paths.get(LeakageResult.getFolderPath()).resolve("Telemetry_PreProcessingLeak.csv").toString();
//        File file = new File(LeakageResult.getFolderPath() + "Telemetry_PreProcessingLeak.csv");
        File file = new File(filePath);

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;
            String train_to_return = "";

            while (((line = reader.readLine()) != null)) {

                String[] columns = line.split(("\t"));
                var trainModel = columns[0];
                var train = columns[1];
                var trainInvo = columns[2];
                var trainMeth = columns[3];
                var ctx1 = columns[4];
                var testModel = columns[5];
                var test = columns[6];
                var testInvo = columns[7];
                var testMeth = columns[8];
                var ctx2 = columns[9];
                var des = columns[10];
                var src = columns[11];

                train_to_return = train;
                break;

            }
            reader.close();
            return train_to_return;
        } catch (IOException e) {
            e.printStackTrace();

        }
        return "";
    }


}
