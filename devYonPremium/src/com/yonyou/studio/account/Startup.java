package com.yonyou.studio.account;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.yonyou.studio.account.console.ConsoleManager;
import com.yonyou.studio.account.dialog.LoginDialog;
import com.yonyou.studio.account.http.utils.HttpUtils;
import com.yonyou.studio.account.http.utils.LocalHostUtil;
import com.yonyou.studio.account.http.utils.StringUtil;
import com.yonyou.studio.account.listener.UserInfoItem;
import com.yonyou.studio.account.service.CheckUpdateService;
import com.yonyou.studio.account.service.LoginService;
import java.io.File;
import java.net.URISyntaxException;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.Timer;
import java.util.TimerTask;
import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IStatusLineManager;
import org.eclipse.jface.action.StatusLineContributionItem;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;

public class Startup implements IStartup
{
    private static final Set<String> msgIdSet = new HashSet();
    private LoginDialog loginDialog;
    private boolean isUpdate = false;
    
    public void earlyStartup()
    {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        workbench.getDisplay().asyncExec(new Runnable()
        {
            public void run()
            {
                IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
                if (window != null && window instanceof WorkbenchWindow)
                {
                    final IStatusLineManager statusLine = ((WorkbenchWindow) window).getStatusLineManager();
                    final IPreferenceStore ps = Activator.getDefault().getPreferenceStore();
                    String token = ps.getString("login_token");
                    boolean isLogin = LoginService.login(token);
                    Startup.this.loginDialog = new LoginDialog(((WorkbenchWindow) window).getShell());
                    final StatusLineContributionItem item = new UserInfoItem("userInfo", 10, Startup.this.loginDialog);
                    Startup.this.loginDialog.setUserInfo(item);
                    item.setVisible(true);
                    statusLine.appendToGroup("END_GROUP", item);
                    statusLine.update(true);
                    StringBuffer welcomeMsg = new StringBuffer();
                    welcomeMsg.append("************************************************ \r\n ");
                    welcomeMsg.append("  欢迎使用 YonBuilder Premium 低代码开发平台 \r\n ");
                    welcomeMsg.append("  当前版本：2025.010801(release) \r\n ");
                    welcomeMsg.append("  开发文档：https://nccdev.yonyou.com \r\n ");
                    welcomeMsg.append("  作   者：QQ/微信 550583975  \r\n");
                    welcomeMsg.append("************************************************ \r\n ");
                    ConsoleManager.printMessage(welcomeMsg.toString());
                    if (!isLogin)
                    {
//                        Startup.this.loginDialog.open();
                    }
                    else
                    {
                        ((UserInfoItem) item).updateText();
                    }
                    
                    IMenuManager menuManager = ((WorkbenchWindow) window).getMenuManager();
                    menuManager = menuManager.findMenuUsingPath("com.yonyou.studio.link.menus.sampleMenu");
                    long period = 3600000L;
                    
                    try
                    {
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask()
                        {
                            public void run()
                            {
                                try
                                {
                                    String token = ps.getString("login_token");
                                    boolean isLogin = LoginService.login(token);
                                    if (isLogin)
                                    {
                                        ((UserInfoItem) item).updateText();
                                    }
                                    else
                                    {
                                        Display.getDefault().asyncExec(new Runnable()
                                        {
                                            public void run()
                                            {
                                                Startup.this.loginDialog.open();
                                            }
                                        });
                                    }
                                }
                                catch (Exception var3)
                                {
                                }
                                
                            }
                        }, period, period);
                    }
                    catch (Exception var15)
                    {
                    }
                    
                    final MessageDialog checkUpdate = new MessageDialog(((WorkbenchWindow) window).getShell(), "更新", (Image) null,
                        "检测到有Studio插件更新，是否更新（更新后自动重启）?\n详情查看：https://nccdev.yonyou.com/article/detail/403", 5, 0,
                        new String[]{"马上更新", "稍后提醒"});
                    
                    try
                    {
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask()
                        {
                            public void run()
                            {
                                if (!Startup.this.isUpdate && CheckUpdateService.check())
                                {
                                    try
                                    {
                                        Display.getDefault().syncExec(new Runnable()
                                        {
                                            public void run()
                                            {
                                                int open = checkUpdate.open();
                                                if (open == 0)
                                                {
                                                    Startup.this.isUpdate = true;
                                                    
                                                    try
                                                    {
                                                        File file = new File(Platform.getInstallLocation().getURL().toURI());
                                                        CheckUpdateService.doUpdate(statusLine.getProgressMonitor(),
                                                            file.getAbsolutePath());
                                                        PlatformUI.getWorkbench().restart(true);
                                                    }
                                                    catch (URISyntaxException var3)
                                                    {
                                                    }
                                                }
                                                
                                            }
                                        });
                                    }
                                    catch (Exception var1)
                                    {
                                    }
                                }
                                
                            }
                        }, 10000L, 3600000L);
                    }
                    catch (Exception var14)
                    {
                    }
                    
                    try
                    {
                        Timer timer = new Timer();
                        timer.schedule(new TimerTask()
                        {
                            public void run()
                            {
                                String uid = ps.getString("socket_uid");
                                String email = ps.getString("eclipse_email");
                                if (uid == null || "".equals(uid.trim()))
                                {
                                    uid = StringUtil.genUUIDHexString();
                                    ps.setValue("socket_uid", uid);
                                }
                                
                                JSONObject params = new JSONObject();
                                params.put("uid", uid);
                                params.put("email", email);
                                params.put("os", LocalHostUtil.getOSName());
                                params.put("date", "2025-01-14");
                                params.put("ip", LocalHostUtil.getV4IP());
                                String url = "https://nccdev.yonyou.com/market-api/eclipse-api/getNotice";
                                String result = HttpUtils.sendPost(url, params.toJSONString(), (Map) null);
                                
                                try
                                {
                                    if (result != null && !"".equals(result.trim()))
                                    {
                                        JSONObject obj = JSON.parseObject(result);
                                        if (obj != null && obj.getJSONArray("data") != null && obj.getJSONArray("data").size() > 0)
                                        {
                                            JSONArray msgObjs = obj.getJSONArray("data");
                                            
                                            for (int i = 0; i < msgObjs.size(); ++i)
                                            {
                                                JSONObject msgObj = msgObjs.getJSONObject(i);
                                                if (msgObj.getString("id") != null && msgObj.getString("content") != null
                                                    && msgObj.getString("publishTime") != null)
                                                {
                                                    String msgId = msgObj.getString("id");
                                                    String msgContent = msgObj.getString("content");
                                                    String publishTime = msgObj.getString("publishTime");
                                                    if (!Startup.msgIdSet.contains(msgId))
                                                    {
                                                        msgContent =
                                                            "******************" + publishTime + "******************\n" + msgContent;
                                                        ConsoleManager.printMessage(msgContent);
                                                        Startup.msgIdSet.add(msgId);
                                                    }
                                                }
                                            }
                                        }
                                    }
                                }
                                catch (Exception var13)
                                {
                                }
                                
                            }
                        }, 0L, 180000L);
                    }
                    catch (Exception e)
                    {
                        e.printStackTrace();
                    }
                }
                
            }
        });
    }
}
