package com.github.SE4AIResearch.DataLeakage_Fall2023.leakage_detectors;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.*;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.csv_data.MultiUseTestLeak;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances.MultiTestLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.csv_data.telemetry.MultiTestLeakageTelemetry;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;

import java.util.ArrayList;
import java.util.List;

import static com.github.SE4AIResearch.DataLeakage_Fall2023.common_utils.Utils.getActualLineNumberFromInvocation;

public class MultiTestLeakageDetector extends LeakageDetector<MultiTestLeakageInstance> {
    private final List<LeakageInstance> leakageInstances;

    @Override
    public String getCsvFileName() {
        return "MultiUseTestLeak.csv";
    }

    @Override
    public int getCsvInvocationColumn() {
        return 1;
    }

    @Override
    public void addLeakageInstance(LeakageInstance instance) {
        this.leakageInstances.add(instance);
    }

    @Override
    public List<LeakageInstance> leakageInstances() {
        return leakageInstances;
    }


    @Override
    protected MultiTestLeakageInstance createLeakageInstanceFromLine(String line) {
        String[] columns = line.split(("\t"));

        final var multiUseTestLeak = new MultiUseTestLeak(columns);
        final var telemetry = new MultiTestLeakageTelemetry(multiUseTestLeak);

        Invocation invocation = new Invocation(columns[getCsvInvocationColumn()]);
        int actualLineNumber = getActualLineNumberFromInvocation(invocation);

        return new MultiTestLeakageInstance(actualLineNumber, invocation, telemetry.getTest());

    }

    public MultiTestLeakageDetector() {
        super();
        this.leakageType = LeakageType.MultiTestLeakage;
        this.leakageInstances = new ArrayList<>();
    }


    @Override
    public boolean isLeakageDetected() {
        return !this.leakageInstances.isEmpty();
    }


}
