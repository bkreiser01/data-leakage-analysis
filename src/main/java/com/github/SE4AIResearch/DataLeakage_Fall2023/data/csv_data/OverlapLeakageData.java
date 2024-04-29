package com.github.SE4AIResearch.DataLeakage_Fall2023.data.csv_data;

import org.jetbrains.annotations.NotNull;

public interface OverlapLeakageData extends CsvFileData {
    String getTrainModel();

    String getTrain();

    String getInvo();

    String getTrainMeth();

    String getCtx();


    boolean equals(@NotNull OverlapLeakageData o);
}
