package com.yonyou.menu.actions;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.yonyou.model.service.NCHomeConfigService;
import com.yonyou.model.vo.NCHomeConfigVO;
import com.yonyou.util.MyNotifier;
import com.yonyou.util.StringUtils;

// app-client.jar
/**
 * 打开Sysconfig
 **
 * @qualiFild com.yonyou.menu.actions.OpenncconfigAction.java<br>
 * @author：LiBencheng<br>
 * @date Created on 2025年4月2日<br>
 * @version 1.0<br>
 */
public class OpenNcConfigAction extends AnAction
{
    public void actionPerformed(AnActionEvent e)
    {
        String configpath = "";
        try
        {
            NCHomeConfigVO configVO = NCHomeConfigService.getInstance().getState();
            String homepath = configVO.getHomePath();
            if (StringUtils.isNotBlank(configVO.getHomePath()))
            {
                if (!homepath.endsWith(File.separator)) homepath += File.separator;
                configpath = homepath + ("bin" + File.separator + "sysconfig.bat");
                File file = new File(homepath); 
                if (file.exists())
                {
                    configpath = homepath + ("bin" + File.separator + "sysconfig.bat");
                    file = new File(homepath);
                    if (!file.exists())
                    {
                        Messages.showErrorDialog("当前NChome不存在sysconfig.bat或ncsysconfig.bat", "错误：");
                        // throw new ExecutionException("当前NChome不存在sysconfig.bat或ncsysconfig.bat");
                    }
                    Messages.showInfoMessage(configpath, "即将执行"); 
                    if (file.exists()) {
                        MyNotifier.notifyInfo(e.getProject(), "^_^本城正在帮你打开 sysconfig：" + configpath); 
                        callCmd(configpath, file.getParentFile());
                    }
                }
            }
            else
            {
                Messages.showErrorDialog("请先进行Home配置(Alt+H)", "错误：");
            }
        }
        catch (Exception e1)
        {
            Messages.showErrorDialog(configpath + "/r/n" + e1.getMessage(), "错误：");
        }
    }
    
    public static void callCmd(final String locationCmd, final File dir)
    {
        Thread t = new Thread()
        {
            public void run()
            {
                InputStream in = null;
                
                try
                {
                    Process child = Runtime.getRuntime().exec(new String[]{locationCmd}, (String[]) null, dir);
                    in = child.getInputStream();
                    
                    // MDEConsoleFactory.console("^_^本城正在帮你打开 sysconfig ：");
                    // MDEConsoleFactory.console("正在打开 ：" + locationCmd);
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    
                    String lines;
                    while ((lines = reader.readLine()) != null)
                    {
                        System.out.println(lines);
                        // MDEConsoleFactory.console(lines);
                    }
                    // MDEConsoleFactory.console("📌🌈✄—————————(◕‿◕✿)💞ꦿ 完美分割线❤split line ❀
                    // ———————————————\r\n");
                    child.waitFor();
                }
                catch (Exception var12)
                {
                }
                finally
                {
                    if (in != null)
                    {
                        try
                        {
                            in.close();
                        }
                        catch (IOException var11)
                        {
                        }
                    }
                    
                }
                
            }
        };
        t.start();
    }
}
