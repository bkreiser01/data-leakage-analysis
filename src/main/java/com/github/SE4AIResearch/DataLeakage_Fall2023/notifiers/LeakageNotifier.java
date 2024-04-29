package com.github.SE4AIResearch.DataLeakage_Fall2023.notifiers;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.project.Project;

public class LeakageNotifier {
    private static String notifGroup = "Leakage Notification Group";
    public static void notifyError(Project project, String content) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup(notifGroup)
                .createNotification(content, NotificationType.ERROR)
                .notify(project);
    }
    public static void mustBePython(Project project) {
        notifyError(project, "Must open a python file to run leakage analysis");
    }
    public static void notifyInformation(Project project, String content) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup(notifGroup)
                .createNotification(content, NotificationType.INFORMATION)
                .notify(project);
    }

    public static void notifyWarning(Project project, String content) {
        NotificationGroupManager.getInstance()
                .getNotificationGroup(notifGroup)
                .createNotification(content, NotificationType.WARNING)
                .notify(project);
    }
}
