package com.github.SE4AIResearch.DataLeakage_Fall2023.data.csv_data.telemetry;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageResult;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.csv_data.OverlapLeakageData;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.csv_data.finals.OverlapLeakageFinal;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Corresponds to Telemetry_OverlapLeak.csv
 */
public class OverlapLeakageTelemetry implements OverlapLeakageData {
    private String trainModel;
    private String train;
    private String trainInvo;
    private String trainMeth;
    private String ctx1;
    private String testModel;
    private String test;
    private String invo;
    private String testMeth;
    private String ctx2;

    public OverlapLeakageTelemetry(OverlapLeakageFinal overlapLeakageFinal) {
        String filePath = Paths.get(LeakageResult.getFolderPath()).resolve("Telemetry_OverlapLeak.csv").toString();
        File file = new File(filePath);
        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;

            while (((line = reader.readLine()) != null)) {

                String[] columns = line.split(("\t"));
                this.trainModel = columns[0];
                this.train = columns[1];
                this.trainInvo = columns[2];
                this.trainMeth = columns[3];
                this.ctx1 = columns[4];
                this.testModel = columns[5];
                this.test = columns[6];
                this.invo = columns[7];
                this.testMeth = columns[8];
                this.ctx2 = columns[9];


                if (this.equals(overlapLeakageFinal)) {
                    break;
                }


            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();

        }

    }

    public String getTest() {
        return test;
    }

    @Override
    public String getTrainModel() {
        return trainModel;
    }

    public String getTrain() {
        return train;
    }

    @Override
    public String getInvo() {
        return trainInvo;//not invo
    }

    @Override
    public String getTrainMeth() {
        return trainMeth;
    }

    @Override
    public String getCtx() {
        return ctx1;
    }


    @Override
    public boolean equals(@NotNull OverlapLeakageData o) {
        return (this.getTrainModel()).equals(o.getTrainModel()) &&
                this.getTrain().equals(o.getTrain()) &&
                this.getInvo().equals(o.getInvo()) &&
                this.getTrainMeth().equals(o.getTrainMeth());

    }

    @Override
    public String getCsvFileName() {
        return "Telemetry_OverlapLeak.csv";
    }
}
