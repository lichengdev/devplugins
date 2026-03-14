package openexplorer.actions;

import org.eclipse.ui.console.IConsoleManager;
import org.eclipse.ui.console.IConsole;
import org.eclipse.ui.console.ConsolePlugin;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.console.MessageConsoleStream;
import org.eclipse.ui.console.MessageConsole;
import org.eclipse.ui.console.IConsoleFactory;

public class MDEConsoleFactory implements IConsoleFactory
{
    private static MessageConsole console;
    private static MessageConsoleStream stream;
    static boolean exists;
    
    static
    {
        MDEConsoleFactory.console = new MessageConsole("uap_devtool", (ImageDescriptor) null);
        MDEConsoleFactory.exists = false;
    }
    
    public void openConsole()
    {
        showConsole();
    }
    
    private static void showConsole()
    {
        if (MDEConsoleFactory.console != null)
        {
            final IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
            final IConsole[] existing = manager.getConsoles();
            MDEConsoleFactory.exists = false;
            for (int i = 0; i < existing.length; ++i)
            {
                if (MDEConsoleFactory.console == existing[i])
                {
                    MDEConsoleFactory.exists = true;
                }
            }
            if (!MDEConsoleFactory.exists)
            {
                manager.addConsoles(new IConsole[]{(IConsole) MDEConsoleFactory.console});
            }
            MDEConsoleFactory.console.activate();
        }
    }
    
    public static void closeConsole()
    {
        final IConsoleManager manager = ConsolePlugin.getDefault().getConsoleManager();
        if (MDEConsoleFactory.console != null)
        {
            manager.removeConsoles(new IConsole[]{(IConsole) MDEConsoleFactory.console});
        }
    }
    
    public static MessageConsole getConsole()
    {
        showConsole();
        return MDEConsoleFactory.console;
    }
    
    public static void console(final String msg)
    {
        if (MDEConsoleFactory.stream == null)
        {
            MDEConsoleFactory.stream = MDEConsoleFactory.console.newMessageStream();
        }
        showConsole();
        MDEConsoleFactory.stream.println(msg);
    }
}
