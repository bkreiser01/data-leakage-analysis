package com.github.SE4AIResearch.DataLeakage_Fall2023.data.leakage_sources;

import com.github.SE4AIResearch.DataLeakage_Fall2023.common_utils.Utils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.Invocation;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.LeakageResult;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.taints.Taint;
import com.github.SE4AIResearch.DataLeakage_Fall2023.data.taints.TaintUtils;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageCause;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.LeakageType;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.source_keywords.LeakageSourceKeyword;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.taints.TaintLabel;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;


/**
 * The source of particular instance of data leakage exists on multiple line numbers and is associated with
 * a Taint.
 */

public abstract class LeakageSource {


    public List<Integer> setLineNumbers() {
        return this.getTaints().stream().map(taint ->
                Utils.getActualLineNumberFromInternalLineNumber(LeakageResult.getFolderPath(),
                        Invocation.getInternalLineNumberFromInvocation(LeakageResult.getFolderPath(),
                                taint.getInvocation()))

        ).distinct().collect(Collectors.toList());


    }

    public abstract void setLineNumbers(List<Integer> lineNumbers);

    public void setLineNumber(int oldLineNumber, int newLineNumber) {
        List<Integer> newLineNumbers = List.copyOf(this.getLineNumbers());
        for (int i = 0; i < newLineNumbers.size(); i++) {
            if (newLineNumbers.get(i) == oldLineNumber) {
                newLineNumbers.set(i, newLineNumber);
            }
        }
        this.setLineNumbers(newLineNumbers);
    }


    public abstract List<Taint> getTaints();


    public Taint findTaintThatMatchesText(String text) {
        return this.getTaints().stream().filter(
                taint -> taint.getPyCallExpression().equalsIgnoreCase(text) //equalsIgnoreCase MUST be used here
                //TODO: the pycall expression in the leakage analysis tool does not match what is actually in the test file
        ).findFirst().orElse(new Taint("", TaintLabel.dup));//TODO: better error handling
    }


    public void removeLineNumbers(List<Integer> lineNumbersToRemove) {

        this.setLineNumbers(this.getLineNumbers().stream().filter(lineNumber -> !lineNumbersToRemove.contains(lineNumber)).toList());
    }

    public abstract List<Integer> getLineNumbers();

    public abstract void setTaints(List<Taint> newTaints);

}
