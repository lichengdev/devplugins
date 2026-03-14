package openexplorer;

import org.osgi.framework.BundleContext;
import org.eclipse.ui.plugin.AbstractUIPlugin;

public class Activator extends AbstractUIPlugin
{
    public static final String PLUGIN_ID = "OpenExplorer";
    public static final String VERSION = "1.5.0";
    private static Activator plugin;
    
    public void start(final BundleContext context) throws Exception
    {
        super.start(context);
        Activator.plugin = this;
    }
    
    public void stop(final BundleContext context) throws Exception
    {
        Activator.plugin = null;
        super.stop(context);
    }
    
    public static Activator getDefault()
    {
        return Activator.plugin;
    }
}
