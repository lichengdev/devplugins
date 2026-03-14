package com.yonyou.model.actions;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.ActionUpdateThread;
import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.yonyou.model.form.NCHomeConfigForm;
import com.yonyou.model.utils.project.ProjectManager;
import com.yonyou.util.task.CustomBackgroundableTask;

public class NCHomeConfigAction extends AnAction {
    public NCHomeConfigAction() {
        super("Home配置");
    }

    public void actionPerformed(AnActionEvent e) {
        ProjectManager.getInstance().setProject(e.getProject());
        final NCHomeConfigForm form = new NCHomeConfigForm(e);
        form.setTitle("Home配置");
        ProgressManager.getInstance().run(new CustomBackgroundableTask(e.getProject(), ".") {
            public void run(@NotNull ProgressIndicator progressIndicator) {
                form.setVisible(true);
            }
        });
    }

    @NotNull
    public ActionUpdateThread getActionUpdateThread() {
        return ActionUpdateThread.BGT;
    }
}
