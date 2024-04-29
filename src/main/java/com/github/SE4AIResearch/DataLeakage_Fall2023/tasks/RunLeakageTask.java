package com.github.SE4AIResearch.DataLeakage_Fall2023.tasks;

import com.intellij.tasks.Comment;
import com.intellij.tasks.Task;
import com.intellij.tasks.TaskType;
import org.jetbrains.annotations.Nls;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import javax.swing.*;
import java.util.Date;

public class RunLeakageTask extends Task {

    @Override
    public @NotNull String getId() {
        return null;
    }

    @Override
    public @Nls @NotNull String getSummary() {
        return null;
    }

    @Override
    public @Nls @Nullable String getDescription() {
        return null;
    }

    @Override
    public Comment @NotNull [] getComments() {
        return new Comment[0];
    }

    @Override
    public @NotNull Icon getIcon() {
        return null;
    }

    @Override
    public @NotNull TaskType getType() {
        return null;
    }

    @Override
    public @Nullable Date getUpdated() {
        return null;
    }

    @Override
    public @Nullable Date getCreated() {
        return null;
    }

    @Override
    public boolean isClosed() {
        return false;
    }

    @Override
    public boolean isIssue() {
        return false;
    }

    @Override
    public @Nullable String getIssueUrl() {
        return null;
    }
}
