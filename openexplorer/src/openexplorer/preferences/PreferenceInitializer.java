package openexplorer.preferences;

import org.eclipse.jface.preference.IPreferenceStore;
import openexplorer.util.Utils;
import openexplorer.util.OperatingSystem;
import openexplorer.Activator;
import org.eclipse.core.runtime.preferences.AbstractPreferenceInitializer;

public class PreferenceInitializer extends AbstractPreferenceInitializer
{
    public void initializeDefaultPreferences()
    {
        final IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.setDefault("linuxFileManager", OperatingSystem.INSTANCE.isLinux() ? Utils.detectLinuxSystemBrowser() : "nautilus");
        store.setDefault("linuxFileManagerPath", "");
    }
}
