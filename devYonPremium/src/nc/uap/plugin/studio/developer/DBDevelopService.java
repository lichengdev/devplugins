package nc.uap.plugin.studio.developer;

import java.util.Date;
import java.text.SimpleDateFormat;
import com.yonyou.uap.studio.connection.exception.ConnectionException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import com.yonyou.uap.studio.connection.rsp.IResultSetProcessor;
import com.yonyou.uap.studio.connection.ConnectionService;
import java.util.List;
import nc.uap.plugin.studio.CommonPlugin;
import nc.uap.plugin.studio.database.meta.DataSourceChangeManager;
import nc.uap.studio.common.core.developer.IDevelopPubService;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.ui.internal.Workbench;
import nc.uap.studio.common.core.developer.DevelopPubServiceFactory;
import nc.uap.plugin.studio.database.meta.IDataSourceChangeListener;
import nc.uap.plugin.studio.ui.preference.developer.conn.DeveloperDBService;

import com.yonyou.uap.studio.connection.rsp.ObjectListResultSetProcessor;
import nc.uap.studio.common.core.developer.vo.DevelopOrg;
import nc.uap.studio.common.core.developer.vo.Developer;

public class DBDevelopService
{
    public static final String PREFERENCE_STORE_DEVELOPER = "ENV_PK_DEVELOPER";
    public static final String PREFERENCE_STORE_DEVELOPORG = "ENV_PK_DEVELOPORG";
    public static final String PREFERENCE_STORE_USERNAME = "ENV_USER_NAME";
    public static final String PREFERENCE_STORE_PWD = "ENV_USER_PWD";
    private static Developer developer;
    private static DevelopOrg developOrg;
    private static String username;
    private static String pwd;
    private static final ObjectListResultSetProcessor<Developer> RSP_DEVELOPER;
    private static final ObjectListResultSetProcessor<DevelopOrg> RSP_DEVELOPORG;
    
    static
    {
        RSP_DEVELOPER = new ObjectListResultSetProcessor((Class) Developer.class);
        RSP_DEVELOPORG = new ObjectListResultSetProcessor((Class) DevelopOrg.class);
        final IDataSourceChangeListener listener = (IDataSourceChangeListener) new IDataSourceChangeListener()
        {
            public void dataSourceChange()
            {
                this.designDataSourceChange();
            }
            
            public void designDataSourceChange()
            {
                DBDevelopService.refresh();
                if (DBDevelopService.developer == null || DBDevelopService.developOrg == null)
                {
                    final IDevelopPubService service = DevelopPubServiceFactory.getService();
                    if (service != null)
                    {
                        service.resetValidateFlag();
                    }
                    MessageDialog.openWarning(Workbench.getInstance().getActiveWorkbenchWindow().getShell(), (String) null,
                        Messages.DBDevelopService_Warning_ResetDev);
                }
            }
        };
        DataSourceChangeManager.registerListener(listener, 9);
    }
    
    public static Developer getDeveloper()
    {
        if (DBDevelopService.developer == null)
        {
            refresh();
        }
        return DBDevelopService.developer;
    }
    
    public static DevelopOrg getDevelopOrg()
    {
        return DBDevelopService.developOrg;
    }
    
    public static String getPwd()
    {
        return queryPWDByPreferenceStore();
    }
    
    public static String getUser()
    {
        return queryUserByPreferenceStore();
    }
    
    public static void refresh()
    {
        DBDevelopService.developer = queryDeveloperByPreferenceStore();
        DBDevelopService.developOrg = queryDevelopOrgByPreferenceStore();
        DBDevelopService.username = queryUserByPreferenceStore();
        DBDevelopService.pwd = queryPWDByPreferenceStore();
        setUserInfo(DBDevelopService.username, DBDevelopService.pwd);
    }
    
    private static String queryUserByPreferenceStore()
    {
        final String string = CommonPlugin.getDefault().getPreferenceStore().getString("ENV_USER_NAME");
        return (string == null) ? "" : string;
    }
    
    private static String queryPWDByPreferenceStore()
    {
        final String string = CommonPlugin.getDefault().getPreferenceStore().getString("ENV_USER_PWD");
        return (string == null) ? "" : string;
    }
    
    private static Developer queryDeveloperByPreferenceStore()
    {
        final String pk_developer = CommonPlugin.getDefault().getPreferenceStore().getString("ENV_PK_DEVELOPER");
        if (pk_developer != null && !pk_developer.equals(""))
        {
            return queryDeveloperByPK(pk_developer);
        }
        return null;
    }
    
    private static DevelopOrg queryDevelopOrgByPreferenceStore()
    {
        final String pk_developorg = CommonPlugin.getDefault().getPreferenceStore().getString("ENV_PK_DEVELOPORG");
        if (pk_developorg != null && !pk_developorg.equals(""))
        {
            return queryDevelopOrgByPK(pk_developorg);
        }
        return null;
    }
    
    public static void setDeveloperInfo(final Developer developer, final DevelopOrg developOrg)
    {
        DBDevelopService.developer = developer;
        DBDevelopService.developOrg = developOrg;
        // DBDevelopService.developer = getDeveloper2();
        // DBDevelopService.developOrg = getDevelopOrg2();
        String pk_developer = "";
        String pk_developOrg = "";
        if (developer != null && developer.getPk_developer() != null)
        {
            pk_developer = developer.getPk_developer();
        }
        if (developOrg != null && developOrg.getPk_developorg() != null)
        {
            pk_developOrg = developOrg.getPk_developorg();
        }
        CommonPlugin.getDefault().getPreferenceStore().setValue("ENV_PK_DEVELOPER", pk_developer);
        CommonPlugin.getDefault().getPreferenceStore().setValue("ENV_PK_DEVELOPORG", pk_developOrg);
    }
    
    public static void setUserInfo(final String username, final String password)
    {
        DBDevelopService.username = username;
        DBDevelopService.pwd = password;
        CommonPlugin.getDefault().getPreferenceStore().setValue("ENV_USER_NAME", (username == null) ? "" : username);
        CommonPlugin.getDefault().getPreferenceStore().setValue("ENV_USER_PWD", (password == null) ? "" : password);
    }
    
    protected static Developer queryDeveloperByPK(final String pk)
    {
        if (DeveloperDBService.isTestPass()) return DeveloperDBService.getDeveloper();
        final String sql = "select * from aam_developer where pk_developer='" + pk + "'";
        List<Developer> result = null;
        try
        {
            result = (List) ConnectionService.executeQuery(sql, (IResultSetProcessor) DBDevelopService.RSP_DEVELOPER);
        }
        catch (final RuntimeException e)
        {
            CommonPlugin.getDefault().getLog()
                .log((IStatus) new Status(4, "com.yonyou.studio.common.core.ui", e.getMessage(), (Throwable) e));
        }
        catch (final ConnectionException e2)
        {
            CommonPlugin.getDefault().getLog()
                .log((IStatus) new Status(4, "com.yonyou.studio.common.core.ui", e2.getMessage(), (Throwable) e2));
        }
        if (result != null && result.size() > 0)
        {
            return result.get(0);
        }
        return null;
    }
    
    protected static DevelopOrg queryDevelopOrgByPK(final String pk)
    {
        if (DeveloperDBService.isTestPass()) return DeveloperDBService.getDevelopOrg();
        final String sql = "select * from aam_developorg where pk_developorg='" + pk + "'";
        List<DevelopOrg> result = null;
        try
        {
            result = (List) ConnectionService.executeQuery(sql, (IResultSetProcessor) DBDevelopService.RSP_DEVELOPORG);
        }
        catch (final RuntimeException e)
        {
            CommonPlugin.getDefault().getLog()
                .log((IStatus) new Status(4, "com.yonyou.studio.common.core.ui", e.getMessage(), (Throwable) e));
        }
        catch (final ConnectionException e2)
        {
            CommonPlugin.getDefault().getLog()
                .log((IStatus) new Status(4, "com.yonyou.studio.common.core.ui", e2.getMessage(), (Throwable) e2));
        }
        if (result != null && result.size() > 0)
        {
            return result.get(0);
        }
        return null;
    }
    
    public static String getCurrentDateStr()
    {
        final SimpleDateFormat f = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return f.format(new Date(System.currentTimeMillis()));
    }
    
}
