package openexplorer.preferences;

import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Composite;
import openexplorer.Activator;
import org.eclipse.jface.preference.IPreferenceStore;
import openexplorer.util.Messages;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.jface.preference.PreferencePage;

public class HomePreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
    public void init(final IWorkbench workbench)
    {
        this.setDescription(String.valueOf(Messages.Open_Explorer_Version) + "1.5.0");
    }
    
    protected IPreferenceStore doGetPreferenceStore()
    {
        return Activator.getDefault().getPreferenceStore();
    }
    
    protected Control createContents(final Composite parent)
    {
        final Composite composite = this.createComposite(parent);
        this.createLabel(composite, Messages.Expand_Instruction);
        return (Control) composite;
    }
    
    private Composite createComposite(final Composite parent)
    {
        final Composite composite = new Composite(parent, 0);
        final GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 5;
        composite.setLayout((Layout) layout);
        composite.setLayoutData((Object) new GridData(768));
        return composite;
    }
    
    protected Label createLabel(final Composite parent, final String text)
    {
        final Label label = new Label(parent, 16384);
        label.setText(text);
        final GridData data = new GridData();
        data.horizontalSpan = 1;
        data.horizontalAlignment = 4;
        label.setLayoutData((Object) data);
        return label;
    }
}
