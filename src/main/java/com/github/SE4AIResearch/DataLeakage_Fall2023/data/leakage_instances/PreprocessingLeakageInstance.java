package com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.Invocation;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_sources.LeakageSource;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_sources.PreprocessingLeakageSource;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.Utils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.taints.Taint;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageCause;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.taints.TaintLabel;

import java.util.Objects;
import java.util.Optional;

public class PreprocessingLeakageInstance implements LeakageInstance {
    private final String train;
    private final int lineNumber;
    private final LeakageType type;
    private final Invocation invocation;
    private final LeakageSource leakageSource;

    public PreprocessingLeakageInstance(int lineNumber, Invocation invocation) {
        this.lineNumber = lineNumber;
        this.type = LeakageType.PreprocessingLeakage;
        this.invocation = invocation;
        this.train = Utils.getTrainFromPreprocessingLeakTelemetryFile();
        this.leakageSource = new PreprocessingLeakageSource();
    }


    @Override
    public int lineNumber() {
        return lineNumber;
    }

    @Override
    public LeakageType type() {
        return type;
    }

    @Override
    public Invocation invocation() {
        return invocation;
    }

    @Override
    public String variableName() {
        return train;
    }

    public Optional<LeakageSource> getLeakageSource() {
        return Optional.of(leakageSource);
    }

    @Override
    public LeakageCause getCause() {
        return null;
    }


    @Override
    public boolean equals(Object obj) {
        if (obj == null || getClass() != obj.getClass()) return false;
        PreprocessingLeakageInstance otherInstance = (PreprocessingLeakageInstance) obj;
        return this.lineNumber() == otherInstance.lineNumber()
                && this.invocation().getNumber() == otherInstance.invocation().getNumber();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.lineNumber(), this.invocation());
    }
}
