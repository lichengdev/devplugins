package nc.uap.mde.tool;

import java.awt.Desktop;
import java.io.IOException;

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IPath;

import nc.uap.mde.tool.console.MDEConsoleFactory;
import nc.uap.plugin.studio.classloader.util.UAPEnvHelper;

public class OpenHomeHandler extends AbstractHandler
{
    public Object execute(ExecutionEvent event) throws ExecutionException
    {
        IPath homepath = UAPEnvHelper.getUAPHomeFolderPath();
        // MDEConsoleFactory.console("homepath.toFile().toURI().getPath()" +
        // homepath.toFile().toURI().getPath());
        // MDEConsoleFactory.console("homepath.toOSString()" + homepath.toOSString());
        String osString = homepath.toOSString();
        if (homepath != null)
        {
            try
            {
                MDEConsoleFactory.console("本城正在帮你打开NChome：" + homepath.toFile());
                Desktop.getDesktop().open(homepath.toFile());
            }
            catch (IOException var3)
            {
            }
        }
        
        return null;
    }
}
