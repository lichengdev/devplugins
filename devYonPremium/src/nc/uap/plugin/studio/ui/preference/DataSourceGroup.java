package nc.uap.plugin.studio.ui.preference;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.text.MessageFormat;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.dialogs.ProgressMonitorDialog;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.eclipse.jface.operation.IThreadListener;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.events.VerifyEvent;
import org.eclipse.swt.events.VerifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.eclipse.swt.widgets.Widget;

import com.yonyou.uap.studio.connection.ConnectionService;
import com.yonyou.uap.studio.connection.exception.ConnectionException;
import com.yonyou.uap.studio.connection.model.DataSourceMetaInfo;

import nc.uap.plugin.studio.CommonPlugin;
import nc.uap.plugin.studio.ui.preference.dbdriver.DatabaseDriverInfo;
import nc.uap.plugin.studio.ui.preference.dbdriver.DriverInfo;
import nc.uap.plugin.studio.ui.preference.prop.DataSourceMeta;
import nc.uap.plugin.studio.ui.preference.xml.PropXml;
import nc.uap.plugin.studio.ui.preference.xml.ToolUtils;

public class DataSourceGroup extends Composite implements ModifyListener, SelectionListener
{
    private static final int TYPE_STRING = 0;
    private static final int TYPE_INTEGER = 1;
    private static final int TYPE_PASSWD = 2;
    private static final int TYPE_OID = 3;
    private Text dbconnHostText;
    private Text dbconnPortText;
    private Text dbconnNameText;
    private Text dbconnOIDText;
    private Text dbconnUserText;
    private Text dbconnPasswdText;
    private String connHost;
    private String connPort;
    private String connName;
    private String connOID;
    private String connUser;
    private String connPasswd;
    private Combo databaseTypeCombo;
    private Combo driverTypeCombo;
    private Combo driverListCombo;
    private Button asDesignButton;
    private Button testDatatSource;
    private Button copyToButton;
    private Button deleteButton;
    private PropXml propxml;
    private String nchome;
    private DataSourceMeta currmeta;
    private boolean dirty;
    private boolean switchFlag;
    private Composite topComposite;
    private Group composite_2;
    private Label label_1;
    private Combo fComboBases;
    private Label label_2;
    private Label label_3;
    private Composite composite_1;
    
    public DataSourceGroup(final Composite parent, final int style)
    {
        super(parent, style);
        this.dirty = false;
        this.switchFlag = false;
        this.setLayout((Layout) new GridLayout(1, false));
        this.setLayoutData((Object) new GridData(4, 4, true, false));
        this.createDSCfgUI();
        this.createBaseDSCfgUI();
    }
    
    private void createDSCfgUI()
    {
        (this.composite_2 = new Group((Composite) this, 0)).setText(Messages.DataSourceGroup_DataSourceConfig);
        final GridLayout gl_composite_2 = new GridLayout(4, false);
        gl_composite_2.marginHeight = 10;
        this.composite_2.setLayout((Layout) gl_composite_2);
        this.composite_2.setLayoutData((Object) new GridData(4, 4, true, false));
        this.topComposite = new Composite((Composite) this.composite_2, 0);
        final GridLayout gl_topComposite = new GridLayout(6, false);
        gl_topComposite.marginHeight = 0;
        this.topComposite.setLayout((Layout) gl_topComposite);
        this.topComposite.setLayoutData((Object) new GridData(4, 16777216, true, false, 5, 1));
        final Label label = new Label(this.topComposite, 16384);
        label.setText(Messages.DataSourceGroup_ComboDriverList);
        this.driverListCombo = new Combo(this.topComposite, 8);
        final GridData gd_driverListCombo = new GridData(16384, 16777216, false, false, 1, 1);
        gd_driverListCombo.widthHint = 180;
        this.driverListCombo.setLayoutData((Object) gd_driverListCombo);
        this.driverListCombo.addModifyListener((ModifyListener) this);
        (this.label_2 = new Label(this.topComposite, 0)).setLayoutData((Object) new GridData(4, 16777216, true, false, 1, 1));
        this.label_2.setText("");
        this.testDatatSource = new Button(this.topComposite, 0);
        final GridData gd_testDatatSource = new GridData(16384, 16777216, false, false, 1, 1);
        this.testDatatSource.setLayoutData((Object) gd_testDatatSource);
        this.testDatatSource.setText(Messages.DataSourceGroup_BtnTestConnection);
        this.copyToButton = new Button(this.topComposite, 0);
        final GridData gd_copyToButton = new GridData(16384, 16777216, false, false, 1, 1);
        this.copyToButton.setLayoutData((Object) gd_copyToButton);
        this.copyToButton.setText(Messages.DataSourceGroup_BtnCopyTo);
        this.deleteButton = new Button(this.topComposite, 0);
        final GridData gd_deleteButton = new GridData(16384, 16777216, false, false, 1, 1);
        this.deleteButton.setLayoutData((Object) gd_deleteButton);
        this.deleteButton.setText(Messages.DataSourceGroup_BtnDelete);
        this.deleteButton.addSelectionListener((SelectionListener) this);
        this.copyToButton.addSelectionListener((SelectionListener) this);
        this.testDatatSource.addSelectionListener((SelectionListener) this);
        this.composite_1 = new Composite((Composite) this.composite_2, 0);
        final GridLayout gl_composite_3 = new GridLayout(4, false);
        gl_composite_3.marginWidth = 23;
        this.composite_1.setLayout((Layout) gl_composite_3);
        this.composite_1.setLayoutData((Object) new GridData(4, 16777216, true, false, 4, 1));
        final Label label2 = new Label(this.composite_1, 16384);
        label2.setText(Messages.DataSourceGroup_ComboDBType);
        (this.databaseTypeCombo = new Combo(this.composite_1, 8)).setLayoutData((Object) new GridData(4, 16777216, false, false, 1, 1));
        this.databaseTypeCombo.addModifyListener((ModifyListener) this);
        final Label label3 = new Label(this.composite_1, 16384);
        label3.setText(Messages.DataSourceGroup_ComboDriverType);
        (this.driverTypeCombo = new Combo(this.composite_1, 8)).setLayoutData((Object) new GridData(4, 16777216, false, false, 1, 1));
        this.driverTypeCombo.addModifyListener((ModifyListener) this);
        final Label label4 = new Label(this.composite_1, 16384);
        label4.setText(Messages.DataSourceGroup_TextHostName);
        (this.dbconnHostText = new Text(this.composite_1, 2048)).setLayoutData((Object) new GridData(4, 2, true, false));
        this.dbconnHostText.addModifyListener((ModifyListener) this);
        this.dbconnHostText.addVerifyListener((VerifyListener) new InputVerifyListener(0));
        final Label label5 = new Label(this.composite_1, 16384);
        label5.setText(Messages.DataSourceGroup_TextPort);
        (this.dbconnPortText = new Text(this.composite_1, 2048)).setLayoutData((Object) new GridData(4, 2, true, false));
        this.dbconnPortText.addModifyListener((ModifyListener) this);
        this.dbconnPortText.addVerifyListener((VerifyListener) new InputVerifyListener(1));
        final Label label6 = new Label(this.composite_1, 16384);
        label6.setText(Messages.DataSourceGroup_TextDBName);
        (this.dbconnNameText = new Text(this.composite_1, 2048)).setLayoutData((Object) new GridData(4, 2, true, false));
        this.dbconnNameText.addModifyListener((ModifyListener) this);
        this.dbconnNameText.addVerifyListener((VerifyListener) new InputVerifyListener(0));
        final Label label7 = new Label(this.composite_1, 16384);
        label7.setText(Messages.DataSourceGroup_TextOIDMark);
        (this.dbconnOIDText = new Text(this.composite_1, 2048)).setLayoutData((Object) new GridData(4, 2, true, false));
        this.dbconnOIDText.addModifyListener((ModifyListener) this);
        this.dbconnOIDText.addVerifyListener((VerifyListener) new InputVerifyListener(3));
        final Label label8 = new Label(this.composite_1, 16384);
        label8.setText(Messages.DataSourceGroup_TextUserName);
        (this.dbconnUserText = new Text(this.composite_1, 2048)).setLayoutData((Object) new GridData(4, 2, true, false));
        this.dbconnUserText.addModifyListener((ModifyListener) this);
        this.dbconnUserText.addVerifyListener((VerifyListener) new InputVerifyListener(0));
        final Label label9 = new Label(this.composite_1, 16384);
        label9.setText(Messages.DataSourceGroup_TextPassword);
        // (this.dbconnPasswdText = new Text(this.composite_1, 4196352)).setLayoutData((Object) new
        // GridData(4, 2, true, false));
        (this.dbconnPasswdText = new Text(this.composite_1, 2048)).setLayoutData((Object) new GridData(4, 2, true, false));
        new Label((Composite) this.composite_2, 0);
        new Label((Composite) this.composite_2, 0);
        new Label((Composite) this.composite_2, 0);
        this.asDesignButton = new Button((Composite) this.composite_2, 0);
        final GridData gd_asDesignButton = new GridData(131072, 16777216, false, false, 1, 1);
        this.asDesignButton.setLayoutData((Object) gd_asDesignButton);
        this.asDesignButton.setText(Messages.DataSourceGroup_BtnAsDesign);
        this.asDesignButton.addSelectionListener((SelectionListener) this);
        this.dbconnPasswdText.addModifyListener((ModifyListener) this);
        this.dbconnPasswdText.addVerifyListener((VerifyListener) new InputVerifyListener(0));
        // this.dbconnPasswdText.addVerifyListener((VerifyListener) new InputVerifyListener(2));
    }
    
    private void createBaseDSCfgUI()
    {
        final Group composite = new Group((Composite) this, 0);
        composite.setText(Messages.DataSourceGroup_BasicBaseConfig);
        final GridLayout gl_composite = new GridLayout(2, false);
        gl_composite.marginHeight = 10;
        gl_composite.marginWidth = 10;
        composite.setLayout((Layout) gl_composite);
        composite.setLayoutData((Object) new GridData(4, 4, true, false));
        (this.label_1 = new Label((Composite) composite, 0)).setLayoutData((Object) new GridData(131072, 16777216, false, false, 1, 1));
        this.label_1.setText(Messages.DataSourceGroup_label_1_text);
        this.fComboBases = new Combo((Composite) composite, 8);
        final GridData gd_fComboBases = new GridData(4, 16777216, false, false, 1, 1);
        gd_fComboBases.widthHint = 180;
        this.fComboBases.setLayoutData((Object) gd_fComboBases);
        this.fComboBases.addModifyListener((ModifyListener) this);
    }
    
    public boolean doTest(final boolean showSuccessDialog, final boolean synced)
    {
        if (this.currmeta == null)
        {
            return false;
        }
        if (!synced)
        {
            this.syncCurrDataSourceMeta();
        }
        final DataSourceMetaInfo meta = new DataSourceMetaInfo();
        meta.setUser(this.currmeta.getUser());
        meta.setPwd(this.currmeta.getPassword());
        meta.setUrl(this.currmeta.getDatabaseUrl());
        meta.setDriver(this.currmeta.getDriverClassName());
        meta.setDbType(this.currmeta.getDatabaseType());
        final ConnectionTestWork task = new ConnectionTestWork(showSuccessDialog, meta);
        final ProgressMonitorDialog progressMonitorDialog = new ConnectionTestDialog(this.getShell(), task);
        try
        {
            progressMonitorDialog.run(true, true, (IRunnableWithProgress) task);
            System.setProperty("istestpass", "true");
        }
        catch (final Exception e)
        {
            System.setProperty("istestpass", "false");
            this.openErrorMessage(e);
        }
        return task.isFlag();
    }
    
    private void openErrorMessage(final Exception e)
    {
        Display.getDefault().asyncExec((Runnable) new Runnable()
        {
            @Override
            public void run()
            {
                MessageDialog.openError(DataSourceGroup.this.getShell(), Messages.DataSourceGroup_ConFailedTitle,
                    Messages.DataSourceGroup_ConFailedDescription);
                final String msg = MessageFormat.format(Messages.DataSourceGroup_TestFailedLogger,
                    DataSourceGroup.this.currmeta.getDatabaseUrl(), DataSourceGroup.this.currmeta.getUser(),
                    DataSourceGroup.this.currmeta.getPassword(), DataSourceGroup.this.currmeta.getDriverClassName());
                CommonPlugin.getDefault().getLog().log((IStatus) new Status(4, "com.yonyou.studio.common.core.ui", msg, (Throwable) e));
            }
        });
    }
    
    public String getConnName()
    {
        return this.connName;
    }
    
    public void setConnName(final String connName)
    {
        this.connName = connName;
    }
    
    public String getConnOID()
    {
        if ("".equals(this.connOID))
        {
            return "ZZ";
        }
        return this.connOID;
    }
    
    public void setConnOID(final String connOID)
    {
        this.connOID = connOID;
    }
    
    public String getConnPasswd()
    {
        return this.connPasswd;
    }
    
    public void setConnPasswd(final String connPasswd)
    {
        this.connPasswd = connPasswd;
    }
    
    public String getConnPort()
    {
        return this.connPort;
    }
    
    public void setConnPort(final String connPort)
    {
        this.connPort = connPort;
    }
    
    public String getConnUser()
    {
        return this.connUser;
    }
    
    public void setConnUser(final String connUser)
    {
        this.connUser = connUser;
    }
    
    public String getConnHost()
    {
        return this.connHost;
    }
    
    public void setConnHost(final String connHost)
    {
        this.connHost = connHost;
    }
    
    public void modifyText(final ModifyEvent e)
    {
        final Object obj = e.getSource();
        if (obj != this.driverListCombo && !this.switchFlag)
        {
            this.dirty = true;
        }
        if (obj == this.driverListCombo)
        {
            final String dsname = this.driverListCombo.getText();
            if (!"".equals(dsname))
            {
                this.switchFlag = true;
                this.syncCurrDataSourceMeta();
                this.fillDataSourceMeta(this.currmeta = (DataSourceMeta) this.driverListCombo.getData(dsname));
                this.switchFlag = false;
            }
        }
        else if (obj == this.fComboBases)
        {
            final String[] items = this.fComboBases.getItems();
            String[] array;
            for (int length = (array = items).length, i = 0; i < length; ++i)
            {
                final String name = array[i];
                final DataSourceMeta meta = (DataSourceMeta) this.driverListCombo.getData(name);
                if (meta != null)
                {
                    final String currentBase = this.fComboBases.getText();
                    if (name.equals(currentBase))
                    {
                        meta.setBase(true);
                    }
                    else
                    {
                        meta.setBase(false);
                    }
                }
            }
        }
        else if (obj == this.databaseTypeCombo)
        {
            final String selected = this.databaseTypeCombo.getText();
            if (!"".equals(selected))
            {
                final DriverInfo[] infos = ((DatabaseDriverInfo) this.databaseTypeCombo.getData(selected)).getDatabase();
                this.fillCombo(this.driverTypeCombo, infos);
            }
        }
        else if (obj == this.driverTypeCombo)
        {
            final String selected = this.driverTypeCombo.getText();
            if (!"".equals(selected))
            {
                final DriverInfo info = (DriverInfo) this.driverTypeCombo.getData(selected);
                this.dbconnHostText.setEnabled(ToolUtils.isJDBCUrl(info.getDriverUrl()));
                this.dbconnPortText.setEnabled(ToolUtils.isJDBCUrl(info.getDriverUrl()));
                this.fillDBConnByInfo(info.getDriverUrl());
            }
        }
        else
        {
            this.setConnHost(this.dbconnHostText.getText());
            this.setConnName(this.dbconnNameText.getText());
            this.setConnOID(this.dbconnOIDText.getText());
            this.setConnPasswd(this.dbconnPasswdText.getText());
            this.setConnPort(this.dbconnPortText.getText());
            this.setConnUser(this.dbconnUserText.getText());
        }
    }
    
    private String findDriverType(final String driverClass, final DriverInfo[] infos)
    {
        for (int i = 0; i < infos.length; ++i)
        {
            final DriverInfo info = infos[i];
            if (info.getDriverClass().equals(driverClass))
            {
                return info.getDriverType();
            }
        }
        return "";
    }
    
    public void fillDataSourceMeta(final DataSourceMeta meta)
    {
        if (meta != null)
        {
            final String dbtye = meta.getDatabaseType();
            if (dbtye != null)
            {
                final String dt = dbtye.split("-")[0];
                this.databaseTypeCombo.setText(dt);
                final DatabaseDriverInfo data = (DatabaseDriverInfo) this.databaseTypeCombo.getData(dt);
                if (data == null)
                {
                    CommonPlugin.getDefault().getLog().log((IStatus) new Status(4, "com.yonyou.studio.common.core.ui",
                        MessageFormat.format(Messages.DataSourceGroup_DBTypeNotFound, dt)));
                }
                else
                {
                    final DriverInfo[] infos = data.getDatabase();
                    this.driverTypeCombo.setText(this.findDriverType(meta.getDriverClassName(), infos));
                }
            }
            this.fillDBConnUrl(meta.getDatabaseUrl());
            this.dbconnOIDText.setText((meta.getOidMark() != null) ? meta.getOidMark() : "XX");
            this.dbconnUserText.setText((meta.getUser() != null) ? meta.getUser() : "sa");
            this.dbconnPasswdText.setText((meta.getPassword() != null) ? meta.getPassword() : "sa");
        }
    }
    
    private void fillDBConnUrl(final String url)
    {
        if (ToolUtils.isJDBCUrl(url))
        {
            final String[] jdbc = ToolUtils.getJDBCInfo(url);
            if (url.contains("zenith"))
            {
                this.dbconnHostText.setText(jdbc[0]);
                this.dbconnPortText.setText(jdbc[2]);
                this.dbconnNameText.setText(jdbc[1]);
            }
            else
            {
                this.dbconnHostText.setText(jdbc[0]);
                this.dbconnPortText.setText(jdbc[1]);
                this.dbconnNameText.setText(jdbc[2]);
            }
        }
        else
        {
            this.dbconnHostText.setText("");
            this.dbconnPortText.setText("");
            this.dbconnNameText.setText(ToolUtils.getODBCDBName(url));
        }
    }
    
    private void fillDBConnByInfo(final String url)
    {
        if (ToolUtils.isJDBCUrl(url))
        {
            final String[] jdbc = ToolUtils.getJDBCInfo(url);
            this.dbconnPortText.setText(jdbc[1]);
        }
        else
        {
            this.dbconnPortText.setText("");
        }
    }
    
    private void syncCurrDataSourceMeta()
    {
        if (this.currmeta == null)
        {
            return;
        }
        final DriverInfo info = (DriverInfo) this.driverTypeCombo.getData(this.driverTypeCombo.getText());
        final String exampleurl = info.getDriverUrl();
        this.currmeta.setOidMark(this.getConnOID());
        final String host = this.getConnHost();
        final String port = this.getConnPort();
        final String dbname = this.getConnName();
        if (ToolUtils.isJDBCUrl(exampleurl))
        {
            this.currmeta.setDatabaseUrl(ToolUtils.getJDBCUrl(exampleurl, dbname, host, port));
        }
        else
        {
            this.currmeta.setDatabaseUrl(ToolUtils.getODBCUrl(exampleurl, dbname));
        }
        this.currmeta.setUser(this.getConnUser());
        this.currmeta.setPassword(this.getConnPasswd());
        this.currmeta.setDriverClassName(info.getDriverClass());
        this.currmeta.setDatabaseType(this.databaseTypeCombo.getText());
        this.driverListCombo.setData(this.currmeta.getDataSourceName(), (Object) this.currmeta);
    }
    
    private void fillCombo(final Combo combo, final Object[] objs)
    {
        if (combo == this.driverListCombo)
        {
            this.currmeta = null;
        }
        final String[] items = new String[objs.length];
        for (int i = 0; i < objs.length; ++i)
        {
            final Object obj = objs[i];
            items[i] = obj.toString();
            combo.setData(obj.toString(), obj);
        }
        combo.setItems(items);
        combo.select(0);
    }
    
    public void saveDesignDataSourceMeta()
    {
        try
        {
            this.syncCurrDataSourceMeta();
            final String filename = String.valueOf(this.getNchome()) + "/ierp/bin/prop.xml";
            final File file = new File(filename);
            if (file.exists())
            {
                final String[] items = this.driverListCombo.getItems();
                final DataSourceMeta[] metas = new DataSourceMeta[items.length];
                for (int i = 0; i < metas.length; ++i)
                {
                    (metas[i] = (DataSourceMeta) this.driverListCombo.getData(items[i])).setMaxCon(50);
                    metas[i].setMinCon(1);
                    String gaussUrl = null;
                    if (metas[i].getDatabaseUrl().contains("gauss"))
                    {
                        gaussUrl = metas[i].getDatabaseUrl().replaceAll("gauss", "zenith").replaceAll("//", "@").replaceAll("/", "");
                        metas[i].setDatabaseUrl(gaussUrl);
                    }
                }
                this.getPropxml().saveMeta(filename, metas);
                this.dirty = false;
            }
        }
        catch (final Exception e)
        {
            LogUtility.logException(e);
        }
    }
    
    private PropXml getPropxml()
    {
        if (this.propxml == null)
        {
            this.propxml = new PropXml();
        }
        return this.propxml;
    }
    
    public void initDataSourceComposite(final String nchome)
    {
        try
        {
            this.setNchome(nchome);
            final DatabaseDriverInfo[] driverinfos = this.getPropxml().getDriverSet(this.getNchome()).getDatabase();
            this.fillCombo(this.databaseTypeCombo, driverinfos);
            final String filename = String.valueOf(this.getNchome()) + "/ierp/bin/prop.xml";
            final File file = new File(filename);
            if (file.exists())
            {
                CoreUtility.silentSetWriterable(filename);
                final DataSourceMeta[] dsMetaWithDesign = this.getPropxml().getDSMetaWithDesign(filename, this.getNchome());
                this.fillCombo(this.driverListCombo, dsMetaWithDesign);
                String baseDataSourceName = null;
                DataSourceMeta[] array;
                for (int length = (array = dsMetaWithDesign).length, i = 0; i < length; ++i)
                {
                    final DataSourceMeta meta = array[i];
                    if (meta.isBase())
                    {
                        baseDataSourceName = meta.getDataSourceName();
                    }
                }
                this.fillCombo(this.fComboBases, dsMetaWithDesign);
                if (baseDataSourceName != null)
                {
                    this.fComboBases.setText(baseDataSourceName);
                }
            }
            this.dirty = false;
        }
        catch (final Exception e)
        {
            LogUtility.logException(e);
        }
    }
    
    protected void checkSubclass()
    {
    }
    
    public String getNchome()
    {
        return this.nchome;
    }
    
    public void setNchome(final String nchome)
    {
        this.nchome = nchome;
    }
    
    public void widgetDefaultSelected(final SelectionEvent e)
    {
    }
    
    public void widgetSelected(final SelectionEvent e)
    {
        this.syncCurrDataSourceMeta();
        final Widget widget = e.widget;
        if (widget == this.testDatatSource)
        {
            this.doTest(true, true);
        }
        else if (widget == this.asDesignButton)
        {
            final String dsname = this.driverListCombo.getText();
            if (!"".equals(dsname) && !"design".equals(dsname))
            {
                try
                {
                    this.dirty = true;
                    final DataSourceMeta meta = (DataSourceMeta) ((DataSourceMeta) this.driverListCombo.getData(dsname)).clone();
                    meta.setDataSourceName("design");
                    this.driverListCombo.setData(meta.getDataSourceName(), (Object) meta);
                    this.driverListCombo.select(0);
                }
                catch (final CloneNotSupportedException ex)
                {
                    LogUtility.logException((Exception) ex);
                }
            }
        }
        else if (widget == this.copyToButton)
        {
            final InputDialog dlg = new InputDialog(this.getShell(),
                String.valueOf(Messages.DataSourceGroup_CopyDialogTitle1) + this.currmeta.getDataSourceName()
                    + Messages.DataSourceGroup_CopyDialogTitle2,
                Messages.DataSourceGroup_CopyDialogDescription, (String) null, (IInputValidator) new IInputValidator()
                {
                    public String isValid(final String newText)
                    {
                        if (newText.length() > 0)
                        {
                            return null;
                        }
                        return "";
                    }
                });
            if (dlg.open() == 0)
            {
                try
                {
                    this.dirty = true;
                    final DataSourceMeta meta = (DataSourceMeta) this.currmeta.clone();
                    final String dsname2 = dlg.getValue();
                    meta.setDataSourceName(dsname2);
                    meta.setBase(false);
                    final String[] items = this.driverListCombo.getItems();
                    String[] array;
                    for (int length = (array = items).length, i = 0; i < length; ++i)
                    {
                        final String item = array[i];
                        if (dsname2.equals(item))
                        {
                            this.driverListCombo.setData(meta.getDataSourceName(), (Object) meta);
                            return;
                        }
                    }
                    this.driverListCombo.add(dsname2);
                    this.driverListCombo.setData(meta.getDataSourceName(), (Object) meta);
                    this.driverListCombo.select(this.driverListCombo.getItemCount() - 1);
                    this.fComboBases.add(dsname2);
                }
                catch (final CloneNotSupportedException ex)
                {
                    LogUtility.logException((Exception) ex);
                }
            }
        }
        else if (widget == this.deleteButton)
        {
            final String dsname = this.driverListCombo.getText();
            if (!"".equals(dsname) && !"design".equals(dsname))
            {
                this.dirty = true;
                this.driverListCombo.remove(dsname);
                this.driverListCombo.select(0);
                final String current = this.fComboBases.getText();
                this.fComboBases.remove(dsname);
                if (dsname.equals(current))
                {
                    this.fComboBases.select(0);
                }
            }
            else
            {
                MessageDialog.openError(this.getShell(), Messages.DataSourceGroup_DeleteErrorTitle,
                    Messages.DataSourceGroup_DeleteErrorMsg);
            }
        }
    }
    
    public boolean isDirty()
    {
        return this.dirty;
    }
    
    private final class ConnectionTestDialog extends ProgressMonitorDialog
    {
        private final ConnectionTestWork task;
        private static final int COUNT = 100;
        private static final int SLEEP_TIME = 10;
        
        private ConnectionTestDialog(final Shell parent, final ConnectionTestWork task)
        {
            super(parent);
            this.task = task;
        }
        
        protected void cancelPressed()
        {
            Label_0082:
            {
                try
                {
                    int i = 0;
                    Block_3:
                    {
                        while (!Thread.currentThread().isInterrupted())
                        {
                            if (i++ >= 100)
                            {
                                break;
                            }
                            if (this.task.getThread() != null && this.task.getThread().isAlive())
                            {
                                break Block_3;
                            }
                            Thread.sleep(10L);
                        }
                        break Label_0082;
                    }
                    if (this.task.getThread() != Thread.currentThread())
                    {
                        this.task.getThread().stop();
                    }
                }
                catch (final Exception ex)
                {
                }
            }
            super.cancelPressed();
        }
    }
    
    private class ConnectionTestWork implements IRunnableWithProgress, IThreadListener
    {
        private volatile Thread thread;
        private volatile boolean flag;
        private final boolean showSuccessDialog;
        private final DataSourceMetaInfo meta;
        
        public ConnectionTestWork(final boolean showSuccessDialog, final DataSourceMetaInfo meta)
        {
            this.showSuccessDialog = showSuccessDialog;
            this.meta = meta;
        }
        
        public Thread getThread()
        {
            return this.thread;
        }
        
        public void threadChange(final Thread thread)
        {
            this.thread = thread;
        }
        
        public boolean isFlag()
        {
            return this.flag;
        }
        
        public void run(final IProgressMonitor monitor) throws InvocationTargetException, InterruptedException
        {
            monitor.beginTask(Messages.DataSourceGroup_0, -1);
            try
            {
                this.flag = ConnectionService.testConnection(this.meta);
                if (!this.showSuccessDialog)
                {
                    Thread.sleep(500L);
                }
                Display.getDefault().asyncExec((Runnable) new Runnable()
                {
                    @Override
                    public void run()
                    {
                        if (ConnectionTestWork.this.showSuccessDialog)
                        {
                            MessageDialog.openInformation(DataSourceGroup.this.getShell(), Messages.DataSourceGroup_ConOKTitle,
                                Messages.DataSourceGroup_ConOKDescription);
                        }
                    }
                });
            }
            catch (final ConnectionException e)
            {
                DataSourceGroup.this.openErrorMessage((Exception) e);
            }
            catch (final InterruptedException e2)
            {
                DataSourceGroup.this.openErrorMessage(e2);
            }
        }
    }
    
    class InputVerifyListener implements VerifyListener
    {
        int type;
        
        public InputVerifyListener(final int type)
        {
            this.type = 0;
            this.type = type;
        }
        
        public void verifyText(final VerifyEvent e)
        {
            if (this.type == 1)
            {
                final String text = e.text;
                if (text.matches("\\D+"))
                {
                    e.doit = false;
                }
                else
                {
                    e.doit = true;
                }
            }
            if (this.type == 3)
            {
                final String text = ((Text) e.getSource()).getText();
                final String firstPart = text.substring(0, e.start);
                final String secondPart = text.substring(e.end, text.length());
                final String str = String.valueOf(firstPart) + e.text + secondPart;
                if (str.matches("[YZPOM]?|([YZPOM][A-Z0-9])"))
                {
                    e.doit = true;
                }
                else
                {
                    e.doit = false;
                }
            }
        }
    }
}
