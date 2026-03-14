package com.yonyou.menu.actions;

import java.io.File;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

// app-client.jar
/**
 * 重置root密码
 **
 * @qualiFild com.yonyou.menu.actions.ChangePasswordAction.java<br>
 * @author：LiBencheng<br>
 * @date Created on 2025年4月2日<br>
 * @version 1.0<br>
 */
public class CleanChacheAction extends AnAction
{
    
    public void actionPerformed(AnActionEvent e)
    {
        String userhome = System.getProperty("user.home");
        File file = new File(userhome, "NCCACHE");
        if (file.exists())
        {
           
        }
    }
    
}
