package com.github.SE4AIResearch.DataLeakage_Fall2023.data.csv_data.finals;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.csv_data.OverlapLeakageData;
import org.jetbrains.annotations.NotNull;

/**
 * Corresponds to FinalOverlapLeak.csv
 */
public class OverlapLeakageFinal implements OverlapLeakageData {

    private String trainModel;
    private String train;
    private String invo;
    private
    String trainMeth;
    private String ctx;
    private String cnt;

    public OverlapLeakageFinal(String[] columns) {
        this.trainModel = columns[0];
        this.train = columns[1];
        this.invo = columns[2];
        this.trainMeth = columns[3];
        this.ctx = columns[4];
        this.cnt = columns[5];
    }


    @Override
    public String getTrainModel() {
        return trainModel;
    }

    @Override
    public String getTrain() {
        return train;
    }

    @Override
    public String getInvo() {
        return invo;
    }

    @Override
    public String getTrainMeth() {
        return trainMeth;
    }

    @Override
    public String getCtx() {
        return ctx;
    }

    public String getCnt() {
        return cnt;
    }



    @Override
    public boolean equals(@NotNull OverlapLeakageData o) {
        return (this.getTrainModel().equals(o.getTrainModel()) &&
                this.getTrain().equals(o.getTrain()) &&
                this.getInvo().equals(o.getInvo()) &&
                this.getTrainMeth().equals(o.getTrainMeth()) &&
                this.getCtx().equals(o.getCtx()));
    }

    @Override
    public String getCsvFileName() {
        return "FinalOverlapLeak.csv";
    }
}
