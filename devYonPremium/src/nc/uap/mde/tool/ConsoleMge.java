package nc.uap.mde.tool;

import org.eclipse.ui.IStartup;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.PlatformUI;
import org.eclipse.ui.internal.WorkbenchWindow;

import com.yonyou.studio.account.console.ConsoleManager;

public class ConsoleMge implements IStartup
{
    @Override
    public void earlyStartup()
    {
        final IWorkbench workbench = PlatformUI.getWorkbench();
        IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        if (window != null && window instanceof WorkbenchWindow)
        {
            StringBuffer welcomeMsg = new StringBuffer();
            welcomeMsg.append("********************************************** <br> ");
            welcomeMsg.append("  欢迎使用 YonBuilder Premium 低代码开发平台 <br> ");
            welcomeMsg.append("  当前版本：2025.010801(release) <br> ");
            welcomeMsg.append("  开发文档：https://nccdev.yonyou.com <br> ");
            welcomeMsg.append("  作  者：QQ/微信 550583975  <br> ");
            welcomeMsg.append("********************************************** <br> ");
            ConsoleManager.printMessage(welcomeMsg.toString());
        }
        // workbench.getDisplay().asyncExec(new Runnable()
        // {
        // public void run()
        // {
        // IWorkbenchWindow window = workbench.getActiveWorkbenchWindow();
        // if (window != null && window instanceof WorkbenchWindow)
        // {
        // StringBuffer welcomeMsg = new StringBuffer();
        // welcomeMsg.append("********************************************** <br> ");
        // welcomeMsg.append(" 欢迎使用 YonBuilder Premium 低代码开发平台 <br> ");
        // welcomeMsg.append(" 当前版本：2025.010801(release) <br> ");
        // welcomeMsg.append(" 开发文档：https://nccdev.yonyou.com <br> ");
        // welcomeMsg.append(" 作 者：QQ/微信 550583975 <br> ");
        // welcomeMsg.append("********************************************** <br> ");
        // ConsoleManager.printMessage(welcomeMsg.toString());
        // }
        // }
        // });
    }
}
