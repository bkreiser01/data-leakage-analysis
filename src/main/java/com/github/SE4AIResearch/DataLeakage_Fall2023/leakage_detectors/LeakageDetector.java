package com.github.SE4AIResearch.DataLeakage_Fall2023.leakage_detectors;

import com.github.SE4AIResearch.DataLeakage_Fall2023.common_utils.Utils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.Invocation;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_instances.LeakageInstance;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageResult;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.nio.file.Paths;
import java.util.List;

public abstract class LeakageDetector<T extends LeakageInstance> {
    public LeakageType leakageType;

    /**
     * @return The name of the CSV file that contains relevant information
     * about the leakage of {@link #leakageType} of this detector.
     */
    public abstract String getCsvFileName();

    /**
     * @return The column of the CSV file (specified by {@link #getCsvFileName()}) that
     * contains the {@link Invocation} associated with this detector's {@link #leakageType}.
     */
    public abstract int getCsvInvocationColumn();

    /**
     * Adds a {@code LeakageInstance} to this detector's {@code leakageInstances()}.
     *
     * @param instance The {@link LeakageInstance} to add.
     */
    public abstract void addLeakageInstance(LeakageInstance instance);

    /**
     * @return A {@link List<LeakageInstance>} representing containing the {@link LeakageInstance}s caught by this detector.
     */
    public abstract List<LeakageInstance> leakageInstances();

    public List<LeakageInstance> FindLeakageInstances() {

        String csvFileName = Paths.get(LeakageResult.getFolderPath()).resolve(this.getCsvFileName()).toString();
        File file = new File(csvFileName);
        findLeakageInstancesInFile(file);

        return leakageInstances();


    }


    /**
     * Looks through the CSV files that provide information about the provided {@code leakageType}.
     * Adds leakage instances to the field {@link #leakageInstances}.
     */
    public void findLeakageInstancesInFile(File file) {
        if (file.exists()) {

            try {
                BufferedReader reader = new BufferedReader(new FileReader(file));

                String line;
                while ((line = reader.readLine()) != null) {
                    var leakageInstance = createLeakageInstanceFromLine(line);

                    addLeakageInstanceIfNotPresent(leakageInstance);


                }
                reader.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    protected abstract T createLeakageInstanceFromLine(String line);

    protected void addLeakageInstanceIfNotPresent(T leakageInstance) {
        var existingInstances = leakageInstances();
        if (!existingInstances.contains(leakageInstance)) {
            if (!anyLinesAreOnExclusionList(leakageInstance)) {
                addLeakageInstance(leakageInstance);
            }
        }
    }

    private boolean anyLinesAreOnExclusionList(LeakageInstance leakageInstance) {
        List<Integer> linesOnExlcusionList = Utils.linesOnExclusionList();

        if (linesOnExlcusionList.contains(leakageInstance.lineNumber())) {
            return true;
        }

        var sourceOptional = leakageInstance.getLeakageSource();
        if (sourceOptional.isPresent()) {

            for (Integer lineNo : sourceOptional.get().getLineNumbers()) {
                if (linesOnExlcusionList.contains(lineNo)) {
                    return true;
                }
            }
        }


        return false;
    }

    public LeakageDetector() {

    }

    public abstract boolean isLeakageDetected();

}
