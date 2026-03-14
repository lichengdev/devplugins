package openexplorer.preferences;

import java.io.File;
import java.util.ArrayList;
import openexplorer.Activator;
import openexplorer.util.Messages;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.swt.events.DisposeEvent;
import org.eclipse.swt.events.DisposeListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Event;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;

public class FMPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
    private ArrayList<Button> fileManagerButtons = new ArrayList();
    private Label fullPathLabel;
    private Text fileManagerPath;
    private Button browseButton;
    private String selectedFileManager;
    private String fileManagerPathString;
    
    public void init(IWorkbench workbench)
    {
        IPreferenceStore store = this.getPreferenceStore();
        this.selectedFileManager = store.getString("linuxFileManager");
        this.fileManagerPathString = store.getString("linuxFileManagerPath");
        this.setDescription(Messages.System_File_Manager_Preferences);
    }
    
    protected Control createContents(Composite parent)
    {
        Composite composite = this.createComposite(parent);
        this.createMacOSXFMGroup(composite);
        this.createWindowsFMGroup(composite);
        this.createLinuxFMGroup(composite);
        return composite;
    }
    
    protected Composite createComposite(Composite parent)
    {
        Composite composite = new Composite(parent, 0);
        GridLayout layout = new GridLayout();
        layout.marginWidth = 0;
        layout.marginHeight = 0;
        composite.setLayout(layout);
        composite.setLayoutData(new GridData(272));
        return composite;
    }
    
    private Group createGroup(Composite composite, String title)
    {
        Group groupComposite = new Group(composite, 16384);
        GridLayout layout = new GridLayout();
        groupComposite.setLayout(layout);
        GridData data = new GridData(768);
        groupComposite.setLayoutData(data);
        groupComposite.setText(title);
        return groupComposite;
    }
    
    protected void createMacOSXFMGroup(Composite composite)
    {
        Group groupComposite = this.createGroup(composite, Messages.MAC_OS_X);
        Button macOSXFMButton = this.createRadioButton(groupComposite, Messages.Finder, "open");
        macOSXFMButton.setSelection(true);
    }
    
    protected void createWindowsFMGroup(Composite composite)
    {
        Group groupComposite = this.createGroup(composite, Messages.WINDOWS);
        Button windowsFMButton = this.createRadioButton(groupComposite, Messages.Windows_Explorer, "explorer");
        windowsFMButton.setSelection(true);
    }
    
    protected void createLinuxFMGroup(Composite composite)
    {
        Group groupComposite = this.createGroup(composite, Messages.LINUX);
        String label = Messages.Nautilus;
        String value = "nautilus";
        this.createRadioButtonWithSelectionListener(groupComposite, label, value, false);
        label = Messages.Dolphin;
        value = "dolphin";
        this.createRadioButtonWithSelectionListener(groupComposite, label, value, false);
        label = Messages.Thunar;
        value = "thunar";
        this.createRadioButtonWithSelectionListener(groupComposite, label, value, false);
        label = Messages.PCManFM;
        value = "pcmanfm";
        this.createRadioButtonWithSelectionListener(groupComposite, label, value, false);
        label = Messages.ROX;
        value = "rox";
        this.createRadioButtonWithSelectionListener(groupComposite, label, value, false);
        label = Messages.Xdg_open;
        value = "xdg-open";
        this.createRadioButtonWithSelectionListener(groupComposite, label, value, false);
        label = Messages.Other;
        value = "other";
        this.createRadioButtonWithSelectionListener(groupComposite, label, value, true);
        Composite c = new Composite(groupComposite, 0);
        c.setLayoutData(new GridData(768));
        GridLayout layout = new GridLayout(3, false);
        layout.marginWidth = 0;
        c.setLayout(layout);
        this.fullPathLabel = new Label(c, 0);
        this.fullPathLabel.setText(Messages.Full_Path_Label_Text);
        this.fileManagerPath = new Text(c, 2048);
        if (this.fileManagerPathString != null)
        {
            this.fileManagerPath.setText(this.fileManagerPathString);
        }
        
        this.fileManagerPath.setLayoutData(new GridData(768));
        this.fileManagerPath.addModifyListener(new ModifyListener()
        {
            public void modifyText(ModifyEvent e)
            {
                if (FMPreferencePage.this.fileManagerPath.isEnabled())
                {
                    String path = FMPreferencePage.this.fileManagerPath.getText();
                    if (path == null || path.equals(""))
                    {
                        FMPreferencePage.this.setValid(false);
                        FMPreferencePage.this.setErrorMessage(Messages.Error_Path_Cant_be_blank);
                        return;
                    }
                    
                    File f = new File(path);
                    if (!f.exists() || !f.isFile())
                    {
                        FMPreferencePage.this.setValid(false);
                        FMPreferencePage.this.setErrorMessage(Messages.Erorr_Path_Not_Exist_or_Not_a_File);
                        return;
                    }
                    
                    FMPreferencePage.this.setErrorMessage((String) null);
                    FMPreferencePage.this.setValid(true);
                }
                
            }
        });
        this.browseButton = new Button(c, 8);
        this.browseButton.setText(Messages.Browse_Button_Text);
        this.browseButton.setFont(composite.getFont());
        this.browseButton.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent evt)
            {
                String newValue = FMPreferencePage.this.browsePressed();
                if (newValue != null)
                {
                    FMPreferencePage.this.setFileManagerPath(newValue);
                }
                
            }
        });
        this.browseButton.addDisposeListener(new DisposeListener()
        {
            public void widgetDisposed(DisposeEvent event)
            {
                FMPreferencePage.this.browseButton = null;
            }
        });
        if (!"other".equals(this.selectedFileManager))
        {
            this.fullPathLabel.setEnabled(false);
            this.fileManagerPath.setEnabled(false);
            this.browseButton.setEnabled(false);
        }
        
        this.createNoteComposite(composite.getFont(), groupComposite, Messages.Preference_note, Messages.FileManager_need_to_be_installed);
    }
    
    private void setFileManagerPath(String value)
    {
        if (this.fileManagerPath != null)
        {
            if (value == null)
            {
                value = "";
            }
            
            this.fileManagerPath.setText(value);
        }
        
    }
    
    private String browsePressed()
    {
        File f = new File(this.fileManagerPath.getText());
        if (!f.exists())
        {
            f = null;
        }
        
        File filePath = this.getFilePath(f);
        return filePath == null ? null : filePath.getAbsolutePath();
    }
    
    private File getFilePath(File startingDirectory)
    {
        FileDialog fileDialog = new FileDialog(this.getShell(), 268439552);
        if (startingDirectory != null)
        {
            fileDialog.setFilterPath(startingDirectory.getPath());
        }
        
        String filePath = fileDialog.open();
        if (filePath != null)
        {
            filePath = filePath.trim();
            if (filePath.length() > 0)
            {
                return new File(filePath);
            }
        }
        
        return null;
    }
    
    private void toggleOtherTextField(boolean enabled)
    {
        this.fullPathLabel.setEnabled(enabled);
        this.fileManagerPath.setEnabled(enabled);
        this.browseButton.setEnabled(enabled);
    }
    
    private Button createRadioButton(Composite parent, String label, String value)
    {
        Button button = new Button(parent, 16400);
        button.setText(label);
        button.setData(value);
        return button;
    }
    
    private Button createRadioButtonWithSelectionListener(Composite parent, String label, final String value,
            final boolean enableOtherTextFieldIfClick)
    {
        Button button = this.createRadioButton(parent, label, value);
        if (value != null && value.equals(this.selectedFileManager))
        {
            button.setSelection(true);
        }
        
        button.addSelectionListener(new SelectionAdapter()
        {
            public void widgetSelected(SelectionEvent e)
            {
                FMPreferencePage.this.selectedFileManager = (String) e.widget.getData();
                FMPreferencePage.this.toggleOtherTextField(enableOtherTextFieldIfClick);
                if ("other".equals(value))
                {
                    FMPreferencePage.this.fileManagerPath.notifyListeners(24, new Event());
                }
                else
                {
                    FMPreferencePage.this.setValid(true);
                    FMPreferencePage.this.setErrorMessage((String) null);
                }
                
            }
        });
        this.fileManagerButtons.add(button);
        return button;
    }
    
    protected IPreferenceStore doGetPreferenceStore()
    {
        return Activator.getDefault().getPreferenceStore();
    }
    
    protected void performDefaults()
    {
        IPreferenceStore store = this.getPreferenceStore();
        
        for (Button button : this.fileManagerButtons)
        {
            if (store.getDefaultString("linuxFileManager").equals((String) button.getData()))
            {
                button.setSelection(true);
                this.selectedFileManager = (String) button.getData();
            }
            else
            {
                button.setSelection(false);
            }
        }
        
        this.fileManagerPath.setText(store.getDefaultString("linuxFileManagerPath"));
        super.performDefaults();
    }
    
    public boolean performOk()
    {
        IPreferenceStore store = this.getPreferenceStore();
        store.setValue("linuxFileManager", this.selectedFileManager);
        store.setValue("linuxFileManagerPath", this.fileManagerPath.getText());
        return super.performOk();
    }
}
