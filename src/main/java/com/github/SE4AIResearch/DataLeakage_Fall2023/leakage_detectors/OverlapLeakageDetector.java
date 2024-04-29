package com.github.SE4AIResearch.DataLeakage_Fall2023.leakage_detectors;

import com.github.SE4AIResearch.DataLeakage_Fall2023.common_utils.Utils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.Invocation;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances.OverlapLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.csv_data.finals.OverlapLeakageFinal;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.csv_data.telemetry.OverlapLeakageTelemetry;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;


public class OverlapLeakageDetector extends LeakageDetector<OverlapLeakageInstance> {
    private final List<LeakageInstance> leakageInstances;

    public OverlapLeakageDetector() {
        super();
        this.leakageType = LeakageType.OverlapLeakage;
        this.leakageInstances = new ArrayList<>();
    }


    @Override
    public boolean isLeakageDetected() {
        return !this.leakageInstances.isEmpty();
    }


    @Override
    public int getCsvInvocationColumn() {
        return 2;
    }

    @Override
    public void addLeakageInstance(LeakageInstance instance) {
        this.leakageInstances.add(instance);
    }

    @Override
    public List<LeakageInstance> leakageInstances() {
        return leakageInstances;
    }


    @NotNull
    @Override
    protected OverlapLeakageInstance createLeakageInstanceFromLine(String line) {
        String[] columns = line.split(("\t"));

        final var leakageFinal = new OverlapLeakageFinal(columns);
        final var telemetry = new OverlapLeakageTelemetry(leakageFinal);

        Invocation invocation = new Invocation(leakageFinal.getInvo());
        int actualLineNumber = Utils.getActualLineNumberFromInvocation(invocation);


        return new OverlapLeakageInstance(actualLineNumber, invocation,
                telemetry.getTest(), telemetry.getTrain());
    }


    @Override
    public String getCsvFileName() {
        return "FinalOverlapLeak.csv";
    }
}
