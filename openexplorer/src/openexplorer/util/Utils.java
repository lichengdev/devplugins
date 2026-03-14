package openexplorer.util;

import java.io.IOException;
import java.io.File;

public class Utils
{
    public static String detectLinuxSystemBrowser()
    {
        String result = executeCommand("which nautilus");
        if (result == null || result.trim().equals(""))
        {
            result = executeCommand("which dolphin");
        }
        if (result == null || result.trim().equals(""))
        {
            result = executeCommand("which thunar");
        }
        if (result == null || result.trim().equals(""))
        {
            result = executeCommand("which pcmanfm");
        }
        if (result == null || result.trim().equals(""))
        {
            result = executeCommand("which rox");
        }
        if (result == null || result.trim().equals(""))
        {
            result = executeCommand("xdg-open");
        }
        if (result == null || result.trim().equals(""))
        {
            result = "other";
        }
        final String[] pathnames = result.split(File.separator);
        return pathnames[pathnames.length - 1];
    }
    
    public static String executeCommand(final String command)
    {
        String stdout = null;
        try
        {
            final Process process = Runtime.getRuntime().exec(command);
            stdout = IOUtils.toString(process.getInputStream());
            stdout = stdout.trim();
            stdout = stdout.replace("\n", "");
            stdout = stdout.replace("\r", "");
        }
        catch (final IOException e)
        {
            e.printStackTrace();
        }
        return stdout;
    }
}
