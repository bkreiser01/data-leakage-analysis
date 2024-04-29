package com.github.SE4AIResearch.DataLeakage_Fall2023.enums.taints;

public enum TaintLabel {
    dup("dup"),
    rowset("rowset"),
    unknown("unknown");
    private final String label;

    TaintLabel(String label) {
        this.label = label;
    }

    @Override
    public String toString() {
        return label;
    }
}
