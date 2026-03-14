package nc.uap.mde.tool;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;

import nc.uap.mde.tool.console.MDEConsoleFactory;

public class CleanChacheHandler extends AbstractHandler
{
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        String userhome = System.getProperty("user.home");
        MDEConsoleFactory.console("BenCheng正在帮你执行清缓存操作。。。");
        MDEConsoleFactory.console("NCCACHE：" + userhome);
        File file = new File(userhome, "NCCACHE");
        if (!file.exists())
        {
            MDEConsoleFactory.console("📌🌈✄—————————(◕‿◕✿)💞ꦿ 完美分割线❤split line ❀ ———————————————\r\n");
            return true;
        }
        else
        {
            CleanChacheJob job = new CleanChacheJob("清理缓存");
            job.setEvent(event);
            job.setFiles(file.listFiles());
            job.schedule();
            return null;
        }
    }
    
    class CleanChacheJob extends Job
    {
        File[] files;
        ExecutionEvent event;
        public CleanChacheJob(String name) {  super(name);  }
        public ExecutionEvent getEvent() {  return this.event;  }
        public void setEvent(ExecutionEvent event) {  this.event = event;  }
        public File[] getFiles()  { return this.files; }
        public void setFiles(File[] files)  {  this.files = files; }
        protected IStatus run(IProgressMonitor monitor)
        {
            IWorkbenchWindow w = null;
            List list = new ArrayList();
            try
            {  w = HandlerUtil.getActiveWorkbenchWindowChecked(this.getEvent());  }
            catch (ExecutionException var7)  { }
            
            if (this.files != null)
            {
                monitor.beginTask("开始清理NcLogs", this.files.length);
                for (int i = 0; i < this.files.length && !monitor.isCanceled(); ++i)
                {
                    boolean flg = this.deleteDir(this.files[i]);
                    if (!flg)  list.add(this.files[i].getName());
                    monitor.subTask("正在清理NcLogs: " + this.files[i].getName());
                    MDEConsoleFactory.console("正在清理NcLogs: " + this.files[i].getPath());
                    monitor.worked(1);
                }
            }
            final IWorkbenchWindow w2 = w;
            monitor.subTask("NcLogs清理完成！");
            monitor.done();
            Display.getDefault().syncExec(new Runnable()
            {
                public void run()
                {
                    if (list.size() == 0)
                    {
                        MDEConsoleFactory.console("正在清理NcLogs。。。");
                        MDEConsoleFactory.console(list.toString());
                        MessageDialog.openInformation(w2.getShell(), "CleanCache-作者_本城_Email：550583975@qq.com,QQ/微信：550583975", "NcLogs清理完成！");
                    }
                    else
                    {
                        MDEConsoleFactory.console(list.toString() + "，文件可能被占用。如果需要清除请先关闭NC客户端！");
                        MessageDialog.openInformation(w2.getShell(), "CleanCache-作者_本城_Email：550583975@qq.com,QQ/微信：550583975",
                            list.toString() + "清除不了，如果需要清除请先关闭NC客户端！");
                    }
                    MDEConsoleFactory.console("清理NcLogs结束。");
                    MDEConsoleFactory.console("📌🌈✄—————————(◕‿◕✿)💞ꦿ 完美分割线❤split line ❀ ———————————————\r\n");
                }
            });
            return Status.OK_STATUS;
        }
        
        private boolean deleteDir(File dir)
        {
            if (!dir.exists()) return true;
            else
            {
                if (dir.isDirectory())
                {
                    String[] children = dir.list();
                    for (int i = 0; i < children.length; ++i)
                    {
                        boolean success = this.deleteDir(new File(dir, children[i]));
                        if (!success) return false;
                    }
                }
                return dir.delete();
            }
        }
    }
}
