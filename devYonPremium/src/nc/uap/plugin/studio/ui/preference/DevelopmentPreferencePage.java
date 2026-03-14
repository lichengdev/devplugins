package nc.uap.plugin.studio.ui.preference;

import nc.uap.studio.common.core.developer.vo.Developer;
import nc.uap.plugin.studio.CommonPlugin;
import org.eclipse.ui.IWorkbench;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.TabItem;
import org.eclipse.swt.widgets.TabFolder;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.jface.dialogs.MessageDialog;
import java.io.File;
import nc.uap.plugin.studio.ui.preference.developer.DeveloperComposite;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.jface.preference.PreferencePage;

public class DevelopmentPreferencePage extends PreferencePage implements IWorkbenchPreferencePage
{
    private UAPHomeArea fNCHomeField;
    private ModuleJarComposite modulejarComposite;
    private OpenAPIComposite openAPIComposite;
    private DataSourceGroup dataSourceGroup;
    private DeveloperComposite developerGroup;
    private boolean homeChange;
    
    public boolean okToLeave()
    {
        final String nchome = this.fNCHomeField.getCurrentUAPHome();
        final File file = new File(nchome);
        if (!file.exists())
        {
            return MessageDialog.openConfirm(this.getShell(), Messages.DevelopmentPreferencePage_WarningDialogTitle,
                String.valueOf(nchome) + Messages.DevelopmentPreferencePage_NotExistWarning);
        }
        return super.okToLeave();
    }
    
    public DevelopmentPreferencePage()
    {
        this.homeChange = false;
        this.setTitle(Messages.DevelopmentPreferencePage_PageTitle);
    }
    
    protected Control createContents(final Composite parent)
    {
        final Composite composite = new Composite(parent, 0);
        composite.setLayout((Layout) new GridLayout());
        this.createSettingContents(composite);
        return (Control) composite;
    }
    
    private void createSettingContents(final Composite parent)
    {
        final Group group = new Group(parent, 128);
        group.setLayout((Layout) new FillLayout(256));
        group.setLayoutData((Object) new GridData(768));
        group.setText(Messages.DevelopmentPreferencePage_SettingGroupName);
        this.fNCHomeField = new UAPHomeArea((Composite) group, 0, (ModifyListener) new ModifyListener()
        {
            public void modifyText(final ModifyEvent e)
            {
                DevelopmentPreferencePage.access$0(DevelopmentPreferencePage.this, true);
                DevelopmentPreferencePage.this.iniByHome();
            }
        });
        final TabFolder tabfolder = new TabFolder(parent, 0);
        final GridData gd = new GridData(4, 4, true, true);
        gd.heightHint = 500;
        tabfolder.setLayoutData((Object) gd);
        final TabItem ds = new TabItem(tabfolder, 0);
        ds.setText(Messages.DevelopmentPreferencePage_TabDataSource);
        ds.setControl((Control) (this.dataSourceGroup = new DataSourceGroup((Composite) tabfolder, 0)));
        final TabItem mj = new TabItem(tabfolder, 0);
        mj.setText(Messages.DevelopmentPreferencePage_TabModuleSelection);
        mj.setControl((Control) (this.modulejarComposite = new ModuleJarComposite((Composite) tabfolder, 0)));
        final TabItem openapi = new TabItem(tabfolder, 0);
        openapi.setText("OpenAPI");
        openapi.setControl((Control) (this.openAPIComposite = new OpenAPIComposite((Composite) tabfolder, 0, this.getPreferenceStore())));
        final TabItem developLayout = new TabItem(tabfolder, 0);
        developLayout.setText(Messages.DevelopmentPreferencePage_TabDeveloper);
        developLayout.setControl((Control) (this.developerGroup = new DeveloperComposite((Composite) tabfolder, 0)));
        this.iniByHome();
        tabfolder.addSelectionListener((SelectionListener) new SelectionListener()
        {
            public void widgetSelected(final SelectionEvent e)
            {
                final TabItem[] tabItems = tabfolder.getSelection();
                TabItem[] array;
                for (int length = (array = tabItems).length, i = 0; i < length; ++i)
                {
                    final TabItem item = array[i];
                    if (item.getControl() == DevelopmentPreferencePage.this.developerGroup)
                    {
                        DevelopmentPreferencePage.this.developerGroup.loadDataIfNotinit();
                        if (DevelopmentPreferencePage.this.homeChange || DevelopmentPreferencePage.this.dataSourceGroup.isDirty())
                        {
                            MessageDialog.openWarning((Shell) null, (String) null, Messages.DevelopmentPreferencePage_SavingDBCfgFirst);
                        }
                    }
                }
            }
            
            public void widgetDefaultSelected(final SelectionEvent e)
            {
            }
        });
    }
    
    private void iniByHome()
    {
        final String nchome = this.fNCHomeField.getCurrentUAPHome();
        final File file = new File(nchome);
        if (!file.exists())
        {
            return;
        }
        if (this.dataSourceGroup != null)
        {
            this.dataSourceGroup.initDataSourceComposite(nchome);
        }
        if (this.modulejarComposite != null)
        {
            this.modulejarComposite.initModulejarComposite(nchome);
        }
    }
    
    public void init(final IWorkbench workbench)
    {
        this.setPreferenceStore(CommonPlugin.getDefault().getPreferenceStore());
        this.setDescription(Messages.DevelopmentPreferencePage_PageDescription);
    }
    
    public boolean performOk()
    {
        final Developer developer = this.developerGroup.getSelectedDeveloper();
        final String pk_developer = CommonPlugin.getDefault().getPreferenceStore().getString("ENV_PK_DEVELOPER");
        if (developer == null && (pk_developer == null || "".equals(pk_developer.trim())))
        {
            MessageDialog.openInformation((Shell) null, "提示", "开发者信息未选择，可能会导致元数据无法打开");
        }
        if (this.fNCHomeField.getCurrentUAPHome() != null)
        {
            final boolean flag = this.homeChange || this.dataSourceGroup.isDirty();
            if (this.homeChange)
            {
                this.fNCHomeField.doSave();
            }
            if (this.dataSourceGroup.isDirty())
            {
                this.dataSourceGroup.saveDesignDataSourceMeta();
            }
            if (flag)
            {
                this.getPreferenceStore().setValue("PROP_XML_TS", System.currentTimeMillis());
                this.dataSourceGroup.doTest(false, false);
            }
            this.openAPIComposite.doSave();
        }
        final Object[] objs = this.modulejarComposite.getUnCheckedElements();
        final StringBuffer buffer = new StringBuffer();
        if (objs != null)
        {
            Object[] array;
            for (int length = (array = objs).length, i = 0; i < length; ++i)
            {
                final Object obj = array[i];
                buffer.append(obj);
                buffer.append(",");
            }
            if (buffer.length() > 1)
            {
                buffer.setLength(buffer.length() - 1);
            }
        }
        this.getPreferenceStore().setValue("FIELD_EX_MODULES", buffer.toString());
        this.getPreferenceStore().setValue("EXCEPT_JAR_NC_HOME", this.modulejarComposite.getExceptInput());
        this.developerGroup.save();
        this.homeChange = false;
        return super.performOk();
    }
    
    protected void performDefaults()
    {
        if (MessageDialog.openConfirm(this.getShell(), Messages.DevelopmentPreferencePage_HintDialogTitle,
            Messages.DevelopmentPreferencePage_HintDialogInfo))
        {
            this.fNCHomeField.loadDefault();
            this.developerGroup.loadDefault();
            this.modulejarComposite.setExceptInput("nc.bs.framework.tool.config.+.jar\ntestbill.+.jar\n.*_PROXY.jar");
            this.modulejarComposite.setUnCheckedElements((Object[]) new String[]{"testbill"});
            super.performDefaults();
        }
    }
    
    public void dispose()
    {
        super.dispose();
        if (this.developerGroup != null)
        {
            this.developerGroup.dispose();
        }
    }
    
    static void access$0(final DevelopmentPreferencePage developmentPreferencePage, final boolean homeChange)
    {
        developmentPreferencePage.homeChange = homeChange;
    }
}
