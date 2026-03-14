package com.yonyou.listener;

import java.io.IOException;
import java.util.List;
import java.util.Optional;
import java.util.Timer;
import java.util.TimerTask;

import org.jetbrains.annotations.NotNull;

import com.intellij.credentialStore.CredentialAttributes;
import com.intellij.credentialStore.Credentials;
import com.intellij.ide.passwordSafe.PasswordSafe;
import com.intellij.ide.plugins.PluginManagerCore;
import com.intellij.ide.plugins.PluginNode;
import com.intellij.ide.plugins.RepositoryHelper;
import com.intellij.openapi.extensions.PluginId;
import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.startup.StartupActivity;
import com.intellij.openapi.util.BuildNumber;
import com.intellij.util.text.VersionComparatorUtil;
import com.yonyou.menu.actions.DevLoginAction;
import com.yonyou.menu.actions.PluginDownloadAction;
import com.yonyou.menu.util.LoadDatadictUtil;
import com.yonyou.model.actions.NCHomeConfigAction;
import com.yonyou.model.service.NCHomeConfigService;
import com.yonyou.model.utils.PwdUtil;
import com.yonyou.model.utils.project.ProjectManager;
import com.yonyou.model.vo.NCHomeConfigVO;
import com.yonyou.search.utils.ActionclassSearchToolUtil;
import com.yonyou.search.vo.UpdateClassEnum;
import com.yonyou.util.ConfigurationUtils;
import com.yonyou.util.MyNotifier;
import com.yonyou.util.NccdevUtil;
import com.yonyou.util.StringUtils;
import com.yonyou.util.task.CustomBackgroundableTask;

public class NCCToolListener implements StartupActivity.DumbAware {
    public void runActivity(@NotNull final Project project) {
        ProjectManager.getInstance().setProject(project);
        StringBuilder sb = new StringBuilder();
        sb.append("********************************************** <br> ");
        sb.append("  欢迎使用 YonBuilder Premium 低代码开发平台 <br> ");
        sb.append("  当前版本：2025.1030 <br> ");
        sb.append("  开发文档：https://nccdev.yonyou.com <br> ");
        sb.append("  作    者：QQ/微信 550583975  <br> ");
        sb.append("********************************************** <br> ");
        MyNotifier.notifyInfo(project, sb.toString());
        NCHomeConfigVO configVO = NCHomeConfigService.getInstance(project).getState();
        configVO.setLoginToken("");
        String devuser = configVO.getDevuser();
        boolean loginFlag = false;
        if (StringUtils.isNotBlank(devuser)) {
            if (StringUtils.isNotBlank(configVO.getDevpwd())) {
                MyNotifier.notifyInfo(project, "密码存储方式变更，请重新登录", new DevLoginAction());
            } else {
                String key = "devpwd";
                String devpwd = null;
                CredentialAttributes credentialAttributes = PwdUtil.createCredentialAttributes(key, devuser);
                Credentials credentials = PasswordSafe.getInstance().get(credentialAttributes);
                if (credentials != null) {
                    devpwd = credentials.getPasswordAsString();
                }

                if (StringUtils.isNotBlank(devpwd)) {
                    NccdevUtil.connect(devuser, devpwd);
                    loginFlag = true;
                }
            }
        }

        if (!loginFlag) {
            MyNotifier.notifyInfo(project, "开发者未登录", new DevLoginAction());
        }

        if (StringUtils.isBlank(configVO.getHomePath())) {
            MyNotifier.notifyInfo(project, "请先配置Home路径(Alt+H)", new NCHomeConfigAction());
        }

        LoadDatadictUtil.initDatadictAction();
        Timer timer = new Timer();
        timer.schedule(new TimerTask() {
            public void run() {
                NCCToolListener.updatePlugin(project);
            }
        }, 0L, 14400000L);
        ConfigurationUtils.getInstance().updateMiscellaneousConfiguration();
        ProgressManager.getInstance().run(new CustomBackgroundableTask(project, "加载Action类信息") {
            public void run(@NotNull ProgressIndicator progressIndicator) {
                ActionclassSearchToolUtil.updateActionVOList(project, UpdateClassEnum.ALL);
            }
        });
    }

    private static void updatePlugin(Project project) {
        PluginId studioPluginId = PluginId.getId("yonyou.dev.studio");
        boolean updateStatus = checkUpdateWithRepository(studioPluginId);
        if (updateStatus) {
            MyNotifier.notifyInfo(project, "检测到发布了新版YonBuilder Premium开发者工具插件，请及时更新", new PluginDownloadAction());
        }

    }

    private static boolean checkUpdateWithRepository(PluginId pluginId) {
        String installedVersion = PluginManagerCore.getPlugin(pluginId).getVersion();
        Optional<String> host = RepositoryHelper.getPluginHosts().stream().filter((str) -> str != null && str.startsWith("https://community.yonyou.com/ide/idea/latest/updatePlugin.xml")).findFirst();
        if (!host.isPresent()) {
            return false;
        } else {
            List<PluginNode> pluginNodes = null;

            try {
                pluginNodes = RepositoryHelper.loadPlugins((String)host.get(), (BuildNumber)null, (ProgressIndicator)null);
            } catch (IOException var5) {
                return false;
            }

            Optional<PluginNode> studioPlugin = pluginNodes.stream().filter((plugin) -> plugin.getPluginId().equals(pluginId) && PluginManagerCore.isCompatible(plugin)).findFirst();
            return studioPlugin.isPresent() && VersionComparatorUtil.compare(((PluginNode)studioPlugin.get()).getVersion(), installedVersion) > 0;
        }
    }
}
