package com.github.SE4AIResearch.DataLeakage_Fall2023.data.csv_data.telemetry;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageResult;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.csv_data.MultiTestData;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.csv_data.MultiUseTestLeak;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;

/**
 * Corresponds to Telemetry_MultiUseTestLeak.csv
 */
public class MultiTestLeakageTelemetry implements MultiTestData {

    private String invo;
    private String meth;
    private String ctx1;
    private String test;

    public MultiTestLeakageTelemetry(MultiUseTestLeak multiUseTestLeak) {
        String filePath = Paths.get(LeakageResult.getFolderPath()).resolve("Telemetry_MultiUseTestLeak.csv").toString();
        File file = new File(filePath);
        this.test = "";
        this.invo = "";
        this.ctx1 = "";

        try {
            BufferedReader reader = new BufferedReader(new FileReader(file));

            String line;

            while (((line = reader.readLine()) != null)) {

                String[] columns = line.split(("\t"));
                var testModel = columns[0];
                var _test = columns[1];
                var _invo = columns[2];
                var _meth = columns[3];
                var _ctx1 = columns[4];
                var testModel2 = columns[5];

                this.test = columns[6];
                this.invo = columns[7];
                this.meth = columns[8];
                this.ctx1 = columns[9];


                if (this.equals(multiUseTestLeak)) {
                    break;
                }

            }
            reader.close();

        } catch (IOException e) {
            e.printStackTrace();

        }
    }

    @Override
    public String getTest() {
        return this.test;
    }

    @Override
    public String getCtx1() {
        return ctx1;
    }

    @Override
    public String getMeth() {
        return meth;
    }

    @Override
    public String getInvo() {
        return invo;
    }

    @Override
    public boolean equals(@NotNull MultiTestData o) {
        return this.getInvo().equals(o.getInvo())
                && this.getTest().equals(o.getTest())
                && this.getMeth().equals(o.getMeth())
                && this.getCtx1().equals(o.getCtx1());

    }


    @Override
    public String getCsvFileName() {
        return "Telemetry_MultiUseTestLeak.csv";
    }
}
