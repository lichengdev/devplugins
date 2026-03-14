package com.yonyou.menu.actions;

import java.awt.Desktop;
import java.io.File;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.yonyou.model.service.NCHomeConfigService;
import com.yonyou.model.vo.NCHomeConfigVO;
import com.yonyou.util.MyNotifier;
import com.yonyou.util.StringUtils;

// app-client.jar
/**
 * 打开NcHome
 **
 * @qualiFild com.yonyou.menu.actions.OpenNcHomeAction.java<br>
 * @author：LiBencheng<br>
 * @date Created on 2025年4月2日<br>
 * @version 1.0<br>
 */
public class OpenNcHomeAction extends AnAction
{
    public void actionPerformed(AnActionEvent e)
    {
        try
        {
            NCHomeConfigVO configVO = NCHomeConfigService.getInstance().getState();
            String homePath = configVO.getHomePath();
            if (StringUtils.isNotBlank(configVO.getHomePath()))
            {

                MyNotifier.notifyInfo(e.getProject(), "本城正在帮你打开NChome：" + homePath);
                Desktop.getDesktop().open(new File(homePath));
                // Desktop.getDesktop().browse(new URI(homePath));
                // Messages.showInfoMessage("OpenNcHomeAction成功", "成功");
            }
            else
            {
                Messages.showErrorDialog("请先进行Home配置(Alt+H)", "错误：");
            }
        }
        catch (Exception ex)
        {
            Messages.showErrorDialog("请先进行Home配置(Alt+H)", "错误：");
            // Messages.showErrorDialog(ex.getMessage(), "错误：");
        }
    }
    
}
