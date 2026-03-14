package openexplorer.util;

import openexplorer.Activator;
import org.eclipse.jface.preference.IPreferenceStore;

public enum OperatingSystem
{
    INSTANCE;
    
    public static final String WINDOWS = "win32";
    public static final String LINUX = "linux";
    public static final String MACOSX = "macosx";
    private String os = System.getProperty("osgi.os");
    
    public String getSystemBrowser()
    {
        String systemBrowser = null;
        if (this.isWindows())
        {
            systemBrowser = "explorer";
        }
        else if (this.isLinux())
        {
            IPreferenceStore store = Activator.getDefault().getPreferenceStore();
            systemBrowser = store.getString("linuxFileManager");
            if (systemBrowser.equals("other"))
            {
                systemBrowser = store.getString("linuxFileManagerPath");
            }
        }
        else if (this.isMacOSX())
        {
            systemBrowser = "open";
        }
        
        return systemBrowser;
    }
    
    public String getOS()
    {
        return this.os;
    }
    
    public boolean isWindows()
    {
        return "win32".equalsIgnoreCase(this.os);
    }
    
    public boolean isMacOSX()
    {
        return "macosx".equalsIgnoreCase(this.os);
    }
    
    public boolean isLinux()
    {
        return "linux".equalsIgnoreCase(this.os);
    }
}
