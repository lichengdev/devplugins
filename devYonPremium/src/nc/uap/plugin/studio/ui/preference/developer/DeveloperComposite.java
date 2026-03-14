package nc.uap.plugin.studio.ui.preference.developer;

import nc.uap.plugin.studio.util.MDHyCodeUtil;
import org.eclipse.core.runtime.SafeRunner;
import org.eclipse.core.runtime.ISafeRunnable;
import nc.uap.plugin.studio.developer.DBDevelopLabelService;
import java.util.Iterator;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.jface.dialogs.MessageDialog;
import java.util.ArrayList;
import nc.uap.plugin.studio.ui.preference.developer.conn.DeveloperDBService;
import java.util.HashMap;
import org.eclipse.swt.events.ModifyEvent;
import nc.uap.plugin.studio.developer.DBDevelopService;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Group;
import nc.uap.plugin.studio.database.meta.DataSourceChangeManager;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import nc.uap.plugin.studio.CommonPlugin;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Layout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import nc.uap.studio.common.core.developer.vo.Developer;
import java.util.Map;
import nc.uap.studio.common.core.developer.vo.DevelopOrg;
import java.util.List;
import org.eclipse.swt.events.ModifyListener;
import nc.uap.plugin.studio.database.meta.IDataSourceChangeListener;
import org.eclipse.swt.widgets.Composite;

public class DeveloperComposite extends Composite implements IDataSourceChangeListener, ModifyListener
{
    private static String DEFAULT_INDUSTRY = "0";
    private List<DevelopOrg> orgs;
    private Map<String, List<Developer>> orgDevelopersMap;
    private List<Developer> developers;
    private Text txt1_code;
    private Text txt1_name;
    private Text txt1_level;
    private Text txt1_module;
    private Text txt1_industry;
    private Text txt1_country;
    private Text txt1_phone;
    private Text txt1_mail;
    private Button btn1_isSys;
    private Text txt2_code;
    private Text txt2_name;
    private Text txt2_type;
    private Text txt2_admin;
    private Text txt2_address;
    private Text txt2_phone;
    private Text txt2_site;
    private Text txt2_mail;
    private Button btn2_isSys;
    private Button btnReset;
    private static int TEXT_WIDTH = 130;
    private Label label_4;
    private Label label_16;
    private Label label_17;
    private Text txt2_assetlayout;
    private Text txt2_parter;
    private Text txt2_industry;
    private boolean isInit;
    private Label label_18;
    private Combo fComboOrg;
    private Label label_19;
    private Combo fComboDeveloper;
    private Label label_21;
    
    public DeveloperComposite(Composite parent, int style)
    {
        super(parent, 0);
        this.isInit = false;
        GridLayout gridLayout = new GridLayout();
        this.setLayout((Layout) gridLayout);
        this.setLayoutData((Object) new GridData(4, 4, true, false));
        this.createBtnArea();
        this.createDevelopOrgArea();
        this.createDeveloperArea();
        this.createUserArea();
        this.initListener();
    }
    
    public void loadDataIfNotinit()
    {
        if (!this.isInit)
        {
            this.getDisplay().asyncExec((Runnable) new Runnable()
            {
                @Override
                public void run()
                {
                    try
                    {
                        DeveloperComposite.this.reloadDisplay();
                    }
                    catch (Exception e)
                    {
                        CommonPlugin.getDefault().getLog().log((IStatus) new Status(4, "com.yonyou.studio.common.core.ui",
                            Messages.DeveloperComposite_LOAD_FAILED, (Throwable) e));
                        return;
                    }
                    finally
                    {
                        DataSourceChangeManager.registerListener((IDataSourceChangeListener) DeveloperComposite.this, 0);
                    }
                    DataSourceChangeManager.registerListener((IDataSourceChangeListener) DeveloperComposite.this, 0);
                }
            });
        }
    }
    
    private void createUserArea()
    {
    }
    
    private void createDevelopOrgArea()
    {
        Group groupOrg = new Group((Composite) this, 0);
        groupOrg.setLayoutData((Object) new GridData(4, 16777216, true, false, 1, 1));
        groupOrg.setText(Messages.Develop_OrgBelong);
        groupOrg.setLayout((Layout) new GridLayout(6, false));
        Label label_8 = new Label((Composite) groupOrg, 0);
        label_8.setText(Messages.Develop_OrgCode);
        (this.txt2_code = new Text((Composite) groupOrg, 2048)).setEditable(false);
        GridData gd_txt2_code = new GridData(4, 16777216, true, false, 1, 1);
        gd_txt2_code.widthHint = 130;
        this.txt2_code.setLayoutData((Object) gd_txt2_code);
        Label label_9 = new Label((Composite) groupOrg, 0);
        label_9.setText(Messages.Develop_OrgName);
        (this.txt2_name = new Text((Composite) groupOrg, 2048)).setEditable(false);
        GridData gd_txt2_name = new GridData(4, 16777216, true, false, 1, 1);
        gd_txt2_name.widthHint = 130;
        this.txt2_name.setLayoutData((Object) gd_txt2_name);
        Label label_10 = new Label((Composite) groupOrg, 0);
        label_10.setText(Messages.Develop_OrgType);
        (this.txt2_type = new Text((Composite) groupOrg, 2048)).setEditable(false);
        GridData gd_txt2_type = new GridData(4, 16777216, true, false, 1, 1);
        gd_txt2_type.widthHint = 130;
        this.txt2_type.setLayoutData((Object) gd_txt2_type);
        (this.label_4 = new Label((Composite) groupOrg, 0)).setText(Messages.Develop_AssetLayout);
        (this.txt2_assetlayout = new Text((Composite) groupOrg, 2048)).setEditable(false);
        this.txt2_assetlayout.setLayoutData((Object) new GridData(4, 16777216, true, false, 1, 1));
        (this.label_16 = new Label((Composite) groupOrg, 0)).setText(Messages.Develop_ParterCode);
        (this.txt2_parter = new Text((Composite) groupOrg, 2048)).setEditable(false);
        this.txt2_parter.setLayoutData((Object) new GridData(4, 16777216, true, false, 1, 1));
        (this.label_17 = new Label((Composite) groupOrg, 0)).setText(Messages.Develop_IndustryName);
        (this.txt2_industry = new Text((Composite) groupOrg, 2048)).setEditable(false);
        this.txt2_industry.setLayoutData((Object) new GridData(4, 16777216, true, false, 1, 1));
        Label label_11 = new Label((Composite) groupOrg, 0);
        label_11.setText(Messages.Develop_Leader);
        (this.txt2_admin = new Text((Composite) groupOrg, 2048)).setEditable(false);
        this.txt2_admin.setLayoutData((Object) new GridData(4, 16777216, true, false, 1, 1));
        Label label_12 = new Label((Composite) groupOrg, 0);
        label_12.setText(Messages.Develop_Address);
        (this.txt2_address = new Text((Composite) groupOrg, 2048)).setEditable(false);
        GridData gd_txt2_address = new GridData(4, 16777216, true, false, 1, 1);
        gd_txt2_address.widthHint = 130;
        this.txt2_address.setLayoutData((Object) gd_txt2_address);
        Label label_13 = new Label((Composite) groupOrg, 0);
        label_13.setText(Messages.Develop_Phone);
        (this.txt2_phone = new Text((Composite) groupOrg, 2048)).setEditable(false);
        GridData gd_txt2_phone = new GridData(4, 16777216, true, false, 1, 1);
        gd_txt2_phone.widthHint = 130;
        this.txt2_phone.setLayoutData((Object) gd_txt2_phone);
        Label label_14 = new Label((Composite) groupOrg, 0);
        label_14.setText(Messages.Develop_MainPage);
        (this.txt2_site = new Text((Composite) groupOrg, 2048)).setEditable(false);
        GridData gd_txt2_site = new GridData(4, 16777216, true, false, 1, 1);
        gd_txt2_site.widthHint = 130;
        this.txt2_site.setLayoutData((Object) gd_txt2_site);
        Label label_15 = new Label((Composite) groupOrg, 0);
        label_15.setText(Messages.Develop_Email);
        (this.txt2_mail = new Text((Composite) groupOrg, 2048)).setEditable(false);
        GridData gd_txt2_mail = new GridData(4, 16777216, true, false, 1, 1);
        gd_txt2_mail.widthHint = 130;
        this.txt2_mail.setLayoutData((Object) gd_txt2_mail);
        new Label((Composite) groupOrg, 0);
        (this.btn2_isSys = new Button((Composite) groupOrg, 32)).setEnabled(false);
        this.btn2_isSys.setText(Messages.Develop_IsSystem);
    }
    
    private void createDeveloperArea()
    {
        Group groupDeveloper = new Group((Composite) this, 0);
        groupDeveloper.setLayout((Layout) new GridLayout(6, false));
        groupDeveloper.setLayoutData((Object) new GridData(4, 16777216, true, false, 1, 1));
        groupDeveloper.setText(Messages.UI_GroupTitle);
        Label label = new Label((Composite) groupDeveloper, 0);
        label.setText(Messages.Develop_DevCode);
        (this.txt1_code = new Text((Composite) groupDeveloper, 2048)).setEditable(false);
        GridData gd_txt1_code = new GridData(4, 16777216, true, false, 1, 1);
        gd_txt1_code.widthHint = 130;
        this.txt1_code.setLayoutData((Object) gd_txt1_code);
        Label label_1 = new Label((Composite) groupDeveloper, 0);
        label_1.setText(Messages.Develop_DevName);
        (this.txt1_name = new Text((Composite) groupDeveloper, 2048)).setEditable(false);
        GridData gd_txt1_name = new GridData(4, 16777216, true, false, 1, 1);
        gd_txt1_name.widthHint = 130;
        this.txt1_name.setLayoutData((Object) gd_txt1_name);
        Label label_2 = new Label((Composite) groupDeveloper, 0);
        label_2.setText(Messages.Develop_AssetLayout);
        (this.txt1_level = new Text((Composite) groupDeveloper, 2048)).setEditable(false);
        GridData gd_txt1_level = new GridData(4, 16777216, true, false, 1, 1);
        gd_txt1_level.widthHint = 130;
        this.txt1_level.setLayoutData((Object) gd_txt1_level);
        Label label_3 = new Label((Composite) groupDeveloper, 0);
        label_3.setText(Messages.Develop_ModBelong);
        (this.txt1_module = new Text((Composite) groupDeveloper, 2048)).setEditable(false);
        GridData gd_txt1_module = new GridData(4, 16777216, true, false, 1, 1);
        gd_txt1_module.widthHint = 130;
        this.txt1_module.setLayoutData((Object) gd_txt1_module);
        Label lblnc = new Label((Composite) groupDeveloper, 0);
        lblnc.setText(Messages.Develop_IndustryName);
        (this.txt1_industry = new Text((Composite) groupDeveloper, 2048)).setEditable(false);
        GridData gd_txt1_ncindustry = new GridData(4, 16777216, true, false, 1, 1);
        gd_txt1_ncindustry.widthHint = 130;
        this.txt1_industry.setLayoutData((Object) gd_txt1_ncindustry);
        Label label_4 = new Label((Composite) groupDeveloper, 0);
        label_4.setText(Messages.Develop_Country);
        (this.txt1_country = new Text((Composite) groupDeveloper, 2048)).setEditable(false);
        GridData gd_txt1_country = new GridData(4, 16777216, true, false, 1, 1);
        gd_txt1_country.widthHint = 130;
        this.txt1_country.setLayoutData((Object) gd_txt1_country);
        Label label_5 = new Label((Composite) groupDeveloper, 0);
        label_5.setText(Messages.Develop_Phone);
        (this.txt1_phone = new Text((Composite) groupDeveloper, 2048)).setEditable(false);
        GridData gd_txt1_phone = new GridData(4, 16777216, true, false, 1, 1);
        gd_txt1_phone.widthHint = 130;
        this.txt1_phone.setLayoutData((Object) gd_txt1_phone);
        Label label_6 = new Label((Composite) groupDeveloper, 0);
        label_6.setText(Messages.Develop_Email);
        (this.txt1_mail = new Text((Composite) groupDeveloper, 2048)).setEditable(false);
        GridData gd_txt1_mail = new GridData(4, 16777216, true, false, 1, 1);
        gd_txt1_mail.widthHint = 130;
        this.txt1_mail.setLayoutData((Object) gd_txt1_mail);
        new Label((Composite) groupDeveloper, 0);
        (this.btn1_isSys = new Button((Composite) groupDeveloper, 32)).setEnabled(false);
        this.btn1_isSys.setText(Messages.Develop_IsSystem);
    }
    
    private void createBtnArea()
    {
        Composite composite = new Composite((Composite) this, 0);
        GridLayout gl_composite = new GridLayout(6, false);
        gl_composite.horizontalSpacing = 8;
        composite.setLayout((Layout) gl_composite);
        composite.setLayoutData((Object) new GridData(4, 4, true, false, 1, 1));
        (this.label_18 = new Label(composite, 0)).setLayoutData((Object) new GridData(131072, 16777216, false, false, 1, 1));
        this.label_18.setText(Messages.DeveloperComposite_label_18_text);
        this.fComboOrg = new Combo(composite, 8);
        GridData gd_fComboOrg = new GridData(16384, 16777216, false, false, 1, 1);
        gd_fComboOrg.widthHint = 180;
        this.fComboOrg.setLayoutData((Object) gd_fComboOrg);
        (this.label_19 = new Label(composite, 0)).setLayoutData((Object) new GridData(131072, 16777216, false, false, 1, 1));
        this.label_19.setText(Messages.DeveloperComposite_label_19_text);
        this.fComboDeveloper = new Combo(composite, 8);
        GridData gd_fComboDeveloper = new GridData(16384, 16777216, false, false, 1, 1);
        gd_fComboDeveloper.widthHint = 180;
        this.fComboDeveloper.setLayoutData((Object) gd_fComboDeveloper);
        this.label_21 = new Label(composite, 0);
        GridData gd_label_21 = new GridData(4, 16777216, true, false, 1, 1);
        gd_label_21.widthHint = 20;
        this.label_21.setLayoutData((Object) gd_label_21);
        this.label_21.setText("");
        this.btnReset = new Button(composite, 0);
        GridData gd_btnReset = new GridData(131072, 16777216, false, false, 1, 1);
        gd_btnReset.widthHint = 90;
        this.btnReset.setLayoutData((Object) gd_btnReset);
        this.btnReset.setText(Messages.UI_BtnReset);
        this.btnReset.addSelectionListener((SelectionListener) new SelectionListener()
        {
            public void widgetSelected(SelectionEvent e)
            {
                DBDevelopService.setDeveloperInfo((Developer) null, (DevelopOrg) null);
                DeveloperComposite.this.reloadDisplay();
            }
            
            public void widgetDefaultSelected(SelectionEvent e)
            {
                this.widgetSelected(e);
            }
        });
    }
    
    public void modifyText(ModifyEvent e)
    {
        if (e.getSource() == this.fComboOrg)
        {
            DevelopOrg developOrg = null;
            if (this.fComboOrg.getItemCount() > 0)
            {
                int i = (this.fComboOrg.getSelectionIndex() == -1) ? 0 : this.fComboOrg.getSelectionIndex();
                developOrg = this.orgs.get(i);
                this.display(developOrg, false, null, true);
            }
            else
            {
                this.display(null, false, null, true);
            }
            this.display(developOrg);
        }
        else if (e.getSource() == this.fComboDeveloper)
        {
            Developer developer = null;
            if (this.fComboDeveloper.getItemCount() > 0)
            {
                int i = (this.fComboDeveloper.getSelectionIndex() == -1) ? 0 : this.fComboDeveloper.getSelectionIndex();
                developer = this.developers.get(i);
            }
            this.display(developer);
        }
    }
    
    private void initListener()
    {
        this.fComboOrg.addModifyListener((ModifyListener) this);
        this.fComboDeveloper.addModifyListener((ModifyListener) this);
    }
    
    private void initDatas()
    {
        this.orgDevelopersMap = new HashMap<String, List<Developer>>();
        try
        {
            this.orgs = DeveloperDBService.getAllDevelopOrg();
            List<Developer> allDeveloper = DeveloperDBService.getAllDeveloper();
            for (Developer developer : allDeveloper)
            {
                List<Developer> list = this.orgDevelopersMap.get(developer.getDeveloporg());
                if (list == null)
                {
                    list = new ArrayList<Developer>();
                    this.orgDevelopersMap.put(developer.getDeveloporg(), list);
                }
                list.add(developer);
            }
        }
        catch (RuntimeException ex)
        {
            MessageDialog.openError((Shell) null, (String) null, Messages.UIMSG_Connection_Failed);
        }
    }
    
    private void reloadDisplay()
    {
        this.initDatas();
        DevelopOrg developOrg = DBDevelopService.getDevelopOrg();
        Developer developer = DBDevelopService.getDeveloper();
        this.display(developOrg, true, developer, true);
        this.isInit = true;
    }
    
    private void display(DevelopOrg developOrg, boolean isOrgComboReload, Developer developer, boolean isDevComoboReload)
    {
        int foundedOrg = -1;
        int foundedDeveloper = -1;
        this.fComboOrg.removeModifyListener((ModifyListener) this);
        if (isOrgComboReload)
        {
            String[] items = new String[this.orgs.size()];
            for (int i = 0; i < items.length; ++i)
            {
                DevelopOrg org = this.orgs.get(i);
                items[i] = String.valueOf(org.getOrgcode()) + " " + DBDevelopLabelService.getMultiLangLabel((Object) org, "orgname");
            }
            this.fComboOrg.setItems(items);
        }
        if (developOrg != null)
        {
            for (int j = 0; j < this.orgs.size(); ++j)
            {
                DevelopOrg it = this.orgs.get(j);
                if (developOrg.getPk_developorg().equals(it.getPk_developorg()))
                {
                    foundedOrg = j;
                    break;
                }
            }
        }
        if (isOrgComboReload || foundedOrg != this.fComboOrg.getSelectionIndex())
        {
            this.fComboOrg.select(foundedOrg);
            this.display(developOrg);
        }
        this.fComboOrg.addModifyListener((ModifyListener) this);
        if (isDevComoboReload && developOrg != null && developOrg.getPk_developorg() != null)
        {
            this.developers = this.orgDevelopersMap.get(developOrg.getPk_developorg());
            if (this.developers != null)
            {
                String[] items = new String[this.developers.size()];
                for (int i = 0; i < this.developers.size(); ++i)
                {
                    Developer d = this.developers.get(i);
                    items[i] =
                        String.valueOf(d.getDevelopercode()) + " " + DBDevelopLabelService.getMultiLangLabel((Object) d, "developername");
                }
                this.fComboDeveloper.setItems(items);
            }
            else
            {
                this.fComboDeveloper.setItems(new String[0]);
            }
        }
        else if (developOrg == null)
        {
            this.developers = new ArrayList<Developer>();
            this.fComboDeveloper.setItems(new String[0]);
        }
        if (developer != null && this.developers != null)
        {
            for (int j = 0; j < this.developers.size(); ++j)
            {
                Developer it2 = this.developers.get(j);
                if (developer.getPk_developer().equals(it2.getPk_developer()))
                {
                    foundedDeveloper = j;
                    break;
                }
            }
        }
        if (isDevComoboReload || foundedDeveloper != this.fComboDeveloper.getSelectionIndex())
        {
            this.fComboDeveloper.select(foundedDeveloper);
        }
        if (foundedDeveloper == -1)
        {
            this.display((Developer) null);
        }
    }
    
    private void display(DevelopOrg org)
    {
        ISafeRunnable code = (ISafeRunnable) new ISafeRunnable()
        {
            public void handleException(Throwable exception)
            {
            }
            
            public void run() throws Exception
            {
                DeveloperComposite.this.txt2_code.setText("");
                DeveloperComposite.this.txt2_type.setText("");
                DeveloperComposite.this.txt2_name.setText("");
                DeveloperComposite.this.txt2_admin.setText("");
                DeveloperComposite.this.txt2_phone.setText("");
                DeveloperComposite.this.txt2_mail.setText("");
                DeveloperComposite.this.txt2_address.setText("");
                DeveloperComposite.this.txt2_site.setText("");
                DeveloperComposite.this.btn2_isSys.setSelection(false);
                DeveloperComposite.this.txt2_industry.setText("");
                DeveloperComposite.this.txt2_parter.setText("");
                DeveloperComposite.this.txt2_assetlayout.setText("");
                if (org != null)
                {
                    DeveloperComposite.this.txt2_code.setText(DBDevelopLabelService.getLabel(org.getOrgcode()));
                    DeveloperComposite.this.txt2_type.setText(DBDevelopLabelService.getOrgType(org.getOrgtype()));
                    DeveloperComposite.this.txt2_name.setText(org.getOrgname());
                    // DeveloperComposite.this.txt2_name.setText(DBDevelopLabelService.getMultiLangLabel((Object)
                    // org, "orgname"));
                    DeveloperComposite.this.txt2_admin.setText(DBDevelopLabelService.getLabel(org.getOrgleader()));
                    DeveloperComposite.this.txt2_phone.setText(DBDevelopLabelService.getLabel(org.getOrgphone()));
                    DeveloperComposite.this.txt2_mail.setText(DBDevelopLabelService.getLabel(org.getEmail()));
                    DeveloperComposite.this.txt2_address.setText(DBDevelopLabelService.getLabel(org.getOrgaddress()));
                    DeveloperComposite.this.txt2_site.setText(DBDevelopLabelService.getLabel(org.getMainpage()));
                    DeveloperComposite.this.btn2_isSys.setSelection(org.isIssystem());
                    DeveloperComposite.this.txt2_industry.setText(DBDevelopLabelService.getIndustry(org.getPk_industry()));
                    DeveloperComposite.this.txt2_parter.setText(DBDevelopLabelService.getLabel(org.getPartnercode()));
                    DeveloperComposite.this.txt2_assetlayout.setText(DBDevelopLabelService.getAssetLayout(org.getAssetlayer()));
                }
            }
        };
        SafeRunner.run(code);
    }
    
    private void display(Developer developer)
    {
        ISafeRunnable code = (ISafeRunnable) new ISafeRunnable()
        {
            public void handleException(Throwable exception)
            {
            }
            
            public void run() throws Exception
            {
                DeveloperComposite.this.txt1_code.setText("");
                DeveloperComposite.this.txt1_name.setText("");
                DeveloperComposite.this.txt1_level.setText("");
                DeveloperComposite.this.txt1_module.setText("");
                DeveloperComposite.this.txt1_industry.setText("");
                DeveloperComposite.this.txt1_country.setText("");
                DeveloperComposite.this.txt1_phone.setText("");
                DeveloperComposite.this.txt1_mail.setText("");
                DeveloperComposite.this.btn1_isSys.setSelection(false);
                if (developer != null)
                {
                    DeveloperComposite.this.txt1_code.setText(DBDevelopLabelService.getLabel(developer.getDevelopercode()));
                    DeveloperComposite.this.txt1_name.setText(developer.getDevelopername());
                    // DeveloperComposite.this.txt1_name.setText(DBDevelopLabelService.getMultiLangLabel((Object)
                    // developer, "developername"));
                    DeveloperComposite.this.txt1_level.setText(DBDevelopLabelService.getAssetLayout(developer.getAssetlayer()));
                    DeveloperComposite.this.txt1_module.setText(DBDevelopLabelService.getModule(developer.getPk_module()));
                    DeveloperComposite.this.txt1_industry.setText(DBDevelopLabelService.getIndustry(developer.getPk_industry()));
                    // DeveloperComposite.this.txt1_country.setText(DBDevelopLabelService.getCountry(developer.getPk_countryzone()));
                    DeveloperComposite.this.txt1_country.setText("中华人民共和国");
                    DeveloperComposite.this.txt1_phone.setText(DBDevelopLabelService.getLabel(developer.getPhone()));
                    DeveloperComposite.this.txt1_mail.setText(DBDevelopLabelService.getLabel(developer.getEmail()));
                    DeveloperComposite.this.btn1_isSys.setSelection(developer.isIssystem());
                }
            }
        };
        SafeRunner.run(code);
    }
    
    public void dispose()
    {
        DataSourceChangeManager.unregisterListner((IDataSourceChangeListener) this);
        super.dispose();
    }
    
    public void dataSourceChange()
    {
        this.designDataSourceChange();
    }
    
    public void designDataSourceChange()
    {
        this.reloadDisplay();
    }
    
    public void loadDefault()
    {
        this.reloadDisplay();
    }
    
    public Developer getSelectedDeveloper()
    {
        int i = this.fComboDeveloper.getSelectionIndex();
        if (i >= 0 && i < this.developers.size())
        {
            return this.developers.get(i);
        }
        return null;
    }
    
    public DevelopOrg getSelectedDevelopOrg()
    {
        int i = this.fComboOrg.getSelectionIndex();
        if (i >= 0 && i < this.orgs.size())
        {
            return this.orgs.get(i);
        }
        return null;
    }
    
    public void save()
    {
        if (this.isInit)
        {
            Developer developer = this.getSelectedDeveloper();
            DevelopOrg org = this.getSelectedDevelopOrg();
            DBDevelopService.setDeveloperInfo(developer, org);
            MDHyCodeUtil.setSystemHyCode(
                isEmpty(developer.getPk_industry()) ? "0" : DBDevelopLabelService.getIndustryCode(developer.getPk_industry()));
        }
    }
    
    private static boolean isEmpty(String str)
    {
        return str == null || str.trim().length() == 0;
    }
}
