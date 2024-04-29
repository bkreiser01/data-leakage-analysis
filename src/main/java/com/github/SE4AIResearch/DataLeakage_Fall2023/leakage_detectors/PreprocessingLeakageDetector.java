package com.github.SE4AIResearch.DataLeakage_Fall2023.leakage_detectors;

import com.github.SE4AIResearch.DataLeakage_Fall2023.common_utils.Utils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.Invocation;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances.PreprocessingLeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import org.jetbrains.annotations.NotNull;

import java.util.ArrayList;
import java.util.List;

public class PreprocessingLeakageDetector extends LeakageDetector<PreprocessingLeakageInstance> {
    private final List<LeakageInstance> leakageInstances;

    public PreprocessingLeakageDetector() {
        super();
        this.leakageType = LeakageType.PreprocessingLeakage;
        this.leakageInstances = new ArrayList<>();
    }

    @Override
    public String getCsvFileName() {
        return "PreProcessingLeak.csv";
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


    @NotNull
    protected PreprocessingLeakageInstance createLeakageInstanceFromLine(String line) {
        String[] columns = line.split(("\t"));
        Invocation invocation = new Invocation(columns[getCsvInvocationColumn()]);
        int actualLineNumber = Utils.getActualLineNumberFromInvocation(invocation);

        return new PreprocessingLeakageInstance(actualLineNumber, invocation);
    }

    @Override
    public boolean isLeakageDetected() {
        return !this.leakageInstances.isEmpty();
    }

}
