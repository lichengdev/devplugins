package pers.bc.chat.menu.idea;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;

/**
 * 
 **
 * @qualiFild pers.bc.menu.action.LbcNavigateAction.java<br>
 * @author：LiBencheng<br>
 * @date Created on 2025年4月2日<br>
 * @version 1.0<br>
 */
public class HelpAction extends AnAction
{
    
    @Override
    public void actionPerformed(@NotNull AnActionEvent e)
    {
        try
        {
//            String msg = "作者：bencheng Email 550583975@qq.com,QQ(同微信) 550583975\r\n" + "开源项目地址,欢迎贡献代码：\r\n"
//                + "github：https://github.com/lichengdev (推送地址)\r\n" + "gitee：https://gitee.com/lichengdev/watched (同步github)\r\n"
//                + "业务联系：QQ/微信\r\n\r\n" + "版本：2025.010801（release）";
//            Messages.showInfoMessage("作者：bencheng Email 550583975@qq.com,QQ/微信 550583975\r\n" //
//                + "开源项目地址,欢迎贡献代码：\r\n" //
//                + "github：https://github.com/lichengdev (推送地址)\r\n" //
//                + "gitee：https://gitee.com/lichengdev/watched (同步github)"//
//                + "业务联系：QQ/微信\r\n\r\n" //
//                , "关于我：");
            
            int dialog = Messages.showDialog("作者：bencheng Email 550583975@qq.com,QQ/微信 550583975\r\n" //
                + "开源项目地址,欢迎贡献代码：\r\n" //
                + "github：https://github.com/lichengdev (推送地址)\r\n" //
                + "gitee：https://gitee.com/lichengdev/watched (同步github)\r\n"//
                + "业务联系：QQ/微信\r\n\r\n" //
                , "关于我：", new String[]{Messages.getOkButton()}, 0, Messages.getInformationIcon());
            
            if (dialog==0) try
            {
                Desktop.getDesktop().browse(new URI("https://mail.qq.com/"));
            } catch (IOException | URISyntaxException e1)
            {
                e1.printStackTrace();
            }
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }
    
}
