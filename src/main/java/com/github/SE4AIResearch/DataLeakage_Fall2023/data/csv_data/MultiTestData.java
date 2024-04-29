package com.github.SE4AIResearch.DataLeakage_Fall2023.data.csv_data;

import org.jetbrains.annotations.NotNull;

/**
 * An interface for common fields shared by multiple CSV files
 * in the Leakage Analysis tool output.
 */
public interface MultiTestData extends CsvFileData {
    String getCtx1();

    String getMeth();

    String getInvo();

    String getTest();


    boolean equals(@NotNull MultiTestData o);

}
