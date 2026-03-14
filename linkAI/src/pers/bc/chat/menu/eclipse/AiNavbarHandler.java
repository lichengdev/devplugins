package pers.bc.chat.menu.eclipse;

import java.awt.Desktop;
import java.awt.Desktop.Action;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.jface.action.IContributionItem;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Event;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

public class AiNavbarHandler extends AbstractHandler
{
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        Event e = (Event) event.getTrigger();
        Object obj = e.widget.getData();
        if (obj instanceof IContributionItem)
        {
            
            Desktop desktop = Desktop.getDesktop();
            String id = ((IContributionItem) obj).getId();
            if ("HelpAction".equals(id))
            {
                IWorkbenchWindow workbenchwindow = HandlerUtil.getActiveWorkbenchWindowChecked(event);
                boolean b = MessageDialog.openQuestion(workbenchwindow.getShell(), "关于我^_^",
                    "作者：bencheng Email 550583975@qq.com,QQ(同微信) 550583975\r\n" //
                        + "开源项目地址,欢迎贡献代码：\r\n"//
                        + "github：https://github.com/lichengdev (推送地址)\r\n" //
                        + "gitee：https://gitee.com/lichengdev/watched (同步github)\r\n"//
                        + "业务联系：QQ/微信\r\n\r\n" + "版本：2025.010801（release）");
                if (b) try
                {
                    desktop.browse(new URI("https://mail.qq.com/"));
                }
                catch (IOException | URISyntaxException e1)
                {
                    e1.printStackTrace();
                }
                
                return null;
            }
            if (Desktop.isDesktopSupported() && desktop.isSupported(Action.BROWSE))
            {
                try
                {
                    String uri = "https://gitee.com/lichengdev/";
                    if (uri != null && !uri.isEmpty()) uri = id;
                    desktop.browse(new URI(uri));
                    /*---------------------------AI聊天产品‌：----------------------  */
                    // if ("https://openai.com/".equals(id)) uri = "https://openai.com/"; // ChatGPT
                    // // DeepSeek
                    // else if ("https://chat.deepseek.com/".equals(id)) uri = "https://chat.deepseek.com/";
                    // // 豆包
                    // else if ("https://www.doubao.com/".equals(id)) uri = "https://www.doubao.com/";
                    // // 腾讯元宝
                    // else if ("https://yuanbao.tencent.com/".equals(id)) uri =
                    // "https://yuanbao.tencent.com/";
                    // // 智谱清言‌：老牌国产大模型，适合编程和数学任务。
                    // else if ("https://ai-bot.cn/".equals(id)) uri = "https://ai-bot.cn/";
                    // // ‌Claude‌：由Anthropic公司开发，擅长长对话和复杂任务处理，重视用户隐私保护。
                    // else if ("https://www.anthropic.com/".equals(id)) uri = "https://www.anthropic.com/";
                    // // kimi
                    // else if ("https://kimi.moonshot.cn/".equals(id)) uri = "https://kimi.moonshot.cn/";
                    //
                    // /*---------------------------AI搜索产品‌：‌---------------------- */
                    // else if ("https://chat.baidu.com/".equals(id)) uri = "https://chat.baidu.com/";
                    // // 秘塔AI搜索‌：提供原生的AI搜索体验，支持多种搜索方式。
                    // else if ("https://metaso.cn/".equals(id)) uri = "https://metaso.cn/";
                    // // 功能全面，支持文字、语音、拍照等多种搜索方式。
                    // else if ("https://www.n.cn/".equals(id)) uri = "https://www.n.cn/";
                    // // ‌Perplexity‌：AI万花筒 老牌AI搜索产品，适合需要深度搜索的用户。
                    // else if ("http://www.aiwht.com/".equals(id)) uri = "http://www.aiwht.com/";
                    //
                    // /*---------------------------AI编程产品‌：---------------------- */
                    // // Trae‌：全中文界面，适合初学者，限时免费。
                    // else if ("https://www.trae.ai/".equals(id)) uri = "https://www.trae.ai/";
                    // // Cursor‌：专业开发者首选，提升编码效率。
                    // else if ("https://www.cursor.com/cn".equals(id)) uri = "https://www.cursor.com/cn";
                    // // 微软推出的AI编程助手，与Visual Studio Code等环境紧密结合，提供实时代码建议和自动完成功能。
                    // else if ("https://copilot.microsoft.com/".equals(id)) uri =
                    // "https://copilot.microsoft.com/";
                    //
                    // /*---------------------------其他AI产品‌‌：---------------------- */
                    // // 集成在智能办公软件中的AI助手，具备生成图片、流程图、PPT、思维导图、代码和写作等多种功能，适用于创意设计和商业演示。
                    // else if ("https://boardmix.cn/".equals(id)) uri = "https://boardmix.cn/";
                    // // 由谷歌DeepMind团队打造，擅长处理图像、音频等多种形式的数据，尤其在科研领域有广泛应用。
                    // else if ("https://gogemini.com/".equals(id)) uri = "https://gogemini.com/";
                    
                    // desktop.browse(new URI(uri));
                }
                catch (Exception var7)
                {
                }
            }
        }
        
        return null;
    }
}
