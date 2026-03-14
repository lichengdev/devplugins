package nc.uap.mde.tool;

import java.io.BufferedReader;
import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IPath;

import nc.uap.mde.tool.console.MDEConsoleFactory;
import nc.uap.plugin.studio.classloader.util.UAPEnvHelper;

public class OpenncconfigHandler extends AbstractHandler
{
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        IPath homepath = UAPEnvHelper.getUAPHomeFolderPath();
        if (homepath != null)
        {
            try
            {
                IPath configpath = homepath.append("bin").append("sysconfig.bat");
                if (!configpath.toFile().exists())
                {
                    configpath = homepath.append("bin").append("ncsysconfig.bat");
                    if (!configpath.toFile().exists())
                    {
                        throw new ExecutionException("当前NChome不存在sysconfig.bat或ncsysconfig.bat");
                    }
                }
                
                callCmd(configpath.toOSString(), configpath.toFile().getParentFile());
            }
            catch (Exception var4)
            {
            }
        }
        
        return null;
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
                    
                    MDEConsoleFactory.console("^_^本城正在帮你打开 sysconfig：");
                    MDEConsoleFactory.console("正在打开 ：" + locationCmd);
                    MDEConsoleFactory.console( "首次打开SysConfig可能需要耗费一些时间，请耐心等待...");
                    BufferedReader reader = new BufferedReader(new InputStreamReader(in));
                    
                    String lines;
                    while ((lines = reader.readLine()) != null)
                    {
                        MDEConsoleFactory.console(lines);
                    }
                    MDEConsoleFactory.console("📌🌈✄—————————(◕‿◕✿)💞ꦿ 完美分割线❤split line ❀ ———————————————\r\n");
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
