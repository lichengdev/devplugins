package nc.uap.plugin.studio.ui.preference.developer.conn;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;

import com.yonyou.uap.studio.connection.ConnectionService;
import com.yonyou.uap.studio.connection.exception.ConnectionException;
import com.yonyou.uap.studio.connection.rsp.IResultSetProcessor;
import com.yonyou.uap.studio.connection.rsp.ObjectListResultSetProcessor;

import nc.uap.plugin.studio.CommonPlugin;
import nc.uap.plugin.studio.developer.DBDevelopService;
import nc.uap.studio.common.core.developer.vo.DevelopOrg;
import nc.uap.studio.common.core.developer.vo.Developer;
import nc.uap.studio.common.core.developer.vo.UserVO;

public class DeveloperDBService
{
    private static IResultSetProcessor<List<Developer>> developerResultSetProcessor;
    private static IResultSetProcessor<List<DevelopOrg>> developOrgResultSetProcessor;
    private static IResultSetProcessor<List<UserVO>> userResultSetProcessor;
    private static IResultSetProcessor<List<String>> labelResultSetProcessor;
    
    static
    {
        DeveloperDBService.developerResultSetProcessor =
            (IResultSetProcessor<List<Developer>>) new ObjectListResultSetProcessor((Class) Developer.class);
        DeveloperDBService.developOrgResultSetProcessor =
            (IResultSetProcessor<List<DevelopOrg>>) new ObjectListResultSetProcessor((Class) DevelopOrg.class);
        DeveloperDBService.userResultSetProcessor =
            (IResultSetProcessor<List<UserVO>>) new ObjectListResultSetProcessor((Class) UserVO.class);
        DeveloperDBService.labelResultSetProcessor = (IResultSetProcessor<List<String>>) new LabelResultSetProcessor();
    }
    
    public static List<Developer> getAllDeveloper()
    {
        if (isTestPass()) return new ArrayList<Developer>()
        {
            {
                add(getDeveloper());
            }
        };
        try
        {
            return (List) ConnectionService.executeQuery("select * from aam_developer",
                (IResultSetProcessor) DeveloperDBService.developerResultSetProcessor);
        }
        catch (final ConnectionException e)
        {
            CommonPlugin.getDefault().getLog()
                .log((IStatus) new Status(4, "com.yonyou.studio.common.core.ui", e.getMessage(), (Throwable) e));
            
            return new ArrayList<Developer>()
            {
                {
                    add(getDeveloper());
                }
            };
        }
    }
    
    public static List<DevelopOrg> getAllDevelopOrg()
    {
        if (isTestPass()) return new ArrayList<DevelopOrg>()
        {
            {
                add(getDevelopOrg());
            }
        };
        try
        {
            return (List) ConnectionService.executeQuery("select * from aam_developorg",
                (IResultSetProcessor) DeveloperDBService.developOrgResultSetProcessor);
        }
        catch (final ConnectionException e)
        {
            CommonPlugin.getDefault().getLog()
                .log((IStatus) new Status(4, "com.yonyou.studio.common.core.ui", e.getMessage(), (Throwable) e));
            
            return new ArrayList<DevelopOrg>()
            {
                {
                    add(getDevelopOrg());
                }
            };
        }
    }
    
    public static Boolean isTestPass()
    {
        String istestpass = System.getProperty("istestpass");
        if (!"true".equals(istestpass)) return Boolean.TRUE;
        
        return Boolean.FALSE;
    }
    
    public static Developer getDeveloper()
    {
        return new Developer()
        {
            {
                setAssetlayer("0");
                setDevelopercode("BenChenBQ");
                setDevelopername("BenChenBQ");
                setDevelopername2("用友BQ");
                setDevelopername3("Yonyou BQ");
                setDeveloporg("1001ZE1000000002I2AB");
                setDr(0);
                setEmail("550583975@qq.com");
                setIsdefault(Boolean.TRUE);
                setIssystem(Boolean.TRUE);
                setPhone("13121055201");
                setPk_developer("1001ZE1000000002I2AD");
                setPk_module("YONBIP公共基础");
                setPk_countryzone("中华人民共和国");
            }
        };
    }
    
    public static DevelopOrg getDevelopOrg()
    {
        return new DevelopOrg()
        {
            {
                setAssetlayer("0");
                setEmail("550583975@qq.com");
                setIsdefault(Boolean.TRUE);
                setIssystem(Boolean.TRUE);
                setMainpage("https://gitee.com/lichengdev");
                setOrgaddress("北京市海淀区北清路68号用友软件园（100094）");
                setOrgcode("yonyouBQ");
                setOrgname("BQ产品部");
                setOrgleader("王文京");
                setOrgname2("BQ產品部");
                setOrgname3("BQ Product Dept");
                setOrgphone("18894148952");
                setOrgtype("1");
                setDr(0);
                setPk_developorg("1001ZE1000000002I2AB");
            }
        };
        
    }
    
    public static List<UserVO> getAllValidUser(final String pkDeveloper)
    {
        final String dateStr = DBDevelopService.getCurrentDateStr();
        final String sql = "select cuserid,user_code,user_name from sm_user where enablestate = 2 and base_doc_type = 5 and pk_base_doc='"
            + pkDeveloper + "' " + "and  islocked = 'N' " + "and abledate <= '" + dateStr + "' and (disabledate > '" + dateStr
            + "' or disabledate  is null)";
        try
        {
            return (List) ConnectionService.executeQuery(sql, (IResultSetProcessor) DeveloperDBService.userResultSetProcessor);
        }
        catch (final ConnectionException e)
        {
            CommonPlugin.getDefault().getLog()
                .log((IStatus) new Status(4, "com.yonyou.studio.common.core.ui", e.getMessage(), (Throwable) e));
            return null;
        }
    }
    
    public static String getLabel(final String table, final String pkField, final String nameField, final String value)
    {
        String ret = "";
        if (isTestPass()) return value;
        String sql = "select $name from $table where $pk='$value'".replace("$name", nameField);
        sql = sql.replace("$table", table);
        sql = sql.replace("$pk", pkField);
        sql = sql.replace("$value", value);
        List<String> list = new ArrayList<String>();
        try
        {
            list = (List) ConnectionService.executeQuery(sql, (IResultSetProcessor) DeveloperDBService.labelResultSetProcessor);
        }
        catch (final ConnectionException e)
        {
            CommonPlugin.getDefault().getLog()
                .log((IStatus) new Status(4, "com.yonyou.studio.common.core.ui", e.getMessage(), (Throwable) e));
        }
        if (list.size() == 1)
        {
            final String label = list.get(0);
            ret = ((label == null || label.equals("") || label.equals("")) ? "" : label);
        }
        return ret;
    }
    
    public static String getmainNameLabel(final String table, final String pkField, final String nameField, final String value)
    {
        String ret = "";
        if (isTestPass()) return value;
        String sql = "select $parent_id from $table where $pk='$value'".replace("$parent_id", nameField);
        sql = sql.replace("$table", table);
        sql = sql.replace("$pk", pkField);
        sql = sql.replace("$value", value);
        List<String> list = new ArrayList<String>();
        try
        {
            list = (List) ConnectionService.executeQuery(sql, (IResultSetProcessor) DeveloperDBService.labelResultSetProcessor);
        }
        catch (final ConnectionException e)
        {
            CommonPlugin.getDefault().getLog()
                .log((IStatus) new Status(4, "com.yonyou.studio.common.core.ui", e.getMessage(), (Throwable) e));
        }
        if (list.size() == 1)
        {
            final String label = list.get(0);
            ret = ((label == null || label.equals("") || label.equals("")) ? "" : label);
        }
        return ret;
    }
    
    public static String getLabel(final String table, final String pkField, final String nameField, final String defaultField,
            final String value)
    {
        String ret = "";
        if (isTestPass()) return ret;
        String sql =
            "select case when $name is null then $defaultName else $name end from $table where $pk='$value'".replace("$name", nameField);
        sql = sql.replace("$table", table);
        sql = sql.replace("$pk", pkField);
        sql = sql.replace("$value", value);
        sql = sql.replace("$defaultName", defaultField);
        List<String> list = new ArrayList<String>();
        try
        {
            list = (List) ConnectionService.executeQuery(sql, (IResultSetProcessor) DeveloperDBService.labelResultSetProcessor);
        }
        catch (final ConnectionException e)
        {
            CommonPlugin.getDefault().getLog()
                .log((IStatus) new Status(4, "com.yonyou.studio.common.core.ui", e.getMessage(), (Throwable) e));
        }
        if (list.size() == 1)
        {
            final String label = list.get(0);
            ret = ((label == null || label.equals("") || label.equals("")) ? "" : label);
        }
        return ret;
    }
    
    public static String getMainLevel(final String table, final String pkField, final String nameField, final String defaultField,
            final String value)
    {
        String ret = "";
        if (isTestPass()) return ret;
        String sql = "select $name from $table where  $pk = (select parent_id from $table where $pk= '$value')".replace("$name", nameField);
        sql = sql.replace("$table", table);
        sql = sql.replace("$pk", pkField);
        sql = sql.replace("$value", value);
        List<String> list = new ArrayList<String>();
        try
        {
            list = (List) ConnectionService.executeQuery(sql, (IResultSetProcessor) DeveloperDBService.labelResultSetProcessor);
        }
        catch (final ConnectionException e)
        {
            CommonPlugin.getDefault().getLog()
                .log((IStatus) new Status(4, "com.yonyou.studio.common.core.ui", e.getMessage(), (Throwable) e));
        }
        if (list.size() == 1)
        {
            final String label = list.get(0);
            ret = ((label == null || label.equals("") || label.equals("")) ? "" : label);
        }
        return ret;
    }
}
