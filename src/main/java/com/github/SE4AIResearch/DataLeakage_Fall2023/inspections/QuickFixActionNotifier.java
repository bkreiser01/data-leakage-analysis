package com.github.SE4AIResearch.DataLeakage_Fall2023.inspections;

import com.intellij.util.messages.Topic;

public interface QuickFixActionNotifier {
    @Topic.ProjectLevel
    Topic<QuickFixActionNotifier> QUICK_FIX_ACTION_TOPIC =
            Topic.create("Quick Fix Action Notifier", QuickFixActionNotifier.class);

    void afterAction();
}
