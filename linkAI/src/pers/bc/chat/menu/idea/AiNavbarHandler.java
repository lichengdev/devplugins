package pers.bc.chat.menu.idea;

import java.awt.Desktop;
import java.net.URI;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;

/**
 * 
 **
 * @qualiFild pers.bc.menu.action.LbcNavigateAction.java<br>
 * @author：LiBencheng<br>
 * @date Created on 2025年4月2日<br>
 * @version 1.0<br>
 */
public class AiNavbarHandler extends AnAction
{
    
    @Override
    public void actionPerformed(@NotNull AnActionEvent e)
    {
        String id = e.getActionManager().getId(this);
        try
        {
            // String lastUrl = "";
            // if ("https://aidh.net".equals(id)) lastUrl = "https://aidh.net";
            // if ("https://gitee.com/lichengdev/watched".equals(id)) lastUrl =
            // "https://gitee.com/lichengdev/watched";
            // if ("https://github.com/lichengdev".equals(id)) lastUrl = "https://github.com/lichengdev";
            // if ("https://www.douyin.com".equals(id)) lastUrl = "https://www.douyin.com";
            // if ("https://central.sonatype.com".equals(id)) lastUrl = "https://central.sonatype.com";
            // Desktop.getDesktop().browse(new URI(lastUrl));
            String uri = "https://github.com/lichengdev";
            if (uri != null && !uri.isEmpty()) uri = id;
            MyNotifier.notifyInfo(e.getProject(), "💞本城正在帮你打开：" + uri);
            Desktop.getDesktop().browse(new URI(uri));
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }
    
}
