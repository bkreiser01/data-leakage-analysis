package com.github.SE4AIResearch.DataLeakage_Fall2023.data.csv_data;

import org.jetbrains.annotations.NotNull;

/**
 * Corresponds to MultiUseTestLeak.csv
 */
public class MultiUseTestLeak implements MultiTestData {
    private String test;
    private String invo;
    private String meth;
    private String
            ctx1;

    public MultiUseTestLeak(String[] columns) {
        this.test = columns[0];
        this.invo = columns[1];
        this.meth = columns[2];
        this.ctx1 = columns[3];

    }

    @Override
    public String getTest() {
        return test;
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
        return "MultiUseTestLeak.csv";
    }
}
