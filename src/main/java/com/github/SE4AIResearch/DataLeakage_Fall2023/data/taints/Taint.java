package com.github.SE4AIResearch.DataLeakage_Fall2023.data.taints;

import com.github.SE4AIResearch.DataLeakage_Fall2023.data.Invocation;
import com.github.SE4AIResearch.DataLeakage_Fall2023.enums.taints.TaintLabel;


/**
 * Very roughly speaking, taints correspond to sources of data leakage.
 */
public class Taint {

    private final Invocation invocation;
    private final String pyCallExpression;
    private final TaintLabel taintType;

    public Taint(String taintString, TaintLabel taintType) {
        this.taintType = taintType;
        this.invocation = getInvoFromTaint(taintString);
        this.pyCallExpression = getPyCallExpressionFromTaint(taintString);
    }

    public Taint(Invocation invocation, String pyCallExpression){
        this.taintType = TaintLabel.unknown;
        this.pyCallExpression = pyCallExpression;
        this.invocation= invocation;
    }
    @Override
    public boolean equals(Object obj) {
        var t = (Taint)(obj);
        return// this.taintType.equals(t.taintType) && this.invocation.equals(t.invocation) &&
                this.pyCallExpression.equals(t.pyCallExpression);

    }



    private Invocation getInvoFromTaint(String taint) {
        String[] taintSplit = taint.split("\t");
        if (taintSplit.length < 6) {
            //TODO: how to handle this case?
            return new Invocation("$invo0");
        }

        return new Invocation(taintSplit[4]);
    }



    private String getPyCallExpressionFromTaint(String taint) {
        String[] taintSplit = taint.split("\t");
        if (taintSplit.length < 6) {
            return "";
        }

        return (taintSplit[5]);
    }

    public Invocation getInvocation() {
        return invocation;
    }

    public String getPyCallExpression() {
        return pyCallExpression;
    }

    public boolean containsText(String text){
        return this.getPyCallExpression().toLowerCase().contains(text);
    }

}
