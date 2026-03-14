package pers.bc.chat.menu.idea;

import com.intellij.notification.NotificationGroupManager;
import com.intellij.notification.NotificationType;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.project.Project;

public class MyNotifier {
	private static String groupid;

	public static void notifyError(final Project project, final String content) {
		NotificationGroupManager.getInstance().getNotificationGroup(MyNotifier.groupid)
				.createNotification(content, NotificationType.ERROR).notify(project);
	}

	public static void notifyInfo(final Project project, final String content) {
		NotificationGroupManager.getInstance().getNotificationGroup(MyNotifier.groupid)
				.createNotification(content, NotificationType.INFORMATION).notify(project);
	}

	public static void notifyInfo(final Project project, final String content, final AnAction action) {
		NotificationGroupManager.getInstance().getNotificationGroup(MyNotifier.groupid)
				.createNotification(content, NotificationType.INFORMATION).addAction(action).notify(project);
	}

	public static void notifyWarning(final Project project, final String content) {
		NotificationGroupManager.getInstance().getNotificationGroup(MyNotifier.groupid)
				.createNotification(content, NotificationType.WARNING).notify(project);
	}

	static {
		MyNotifier.groupid = "NCCToolNotice";
	}
}