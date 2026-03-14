package nc.uap.plugin.studio.developer;

import java.lang.reflect.Method;
import java.lang.reflect.InvocationTargetException;
import nc.uap.plugin.studio.StudioUtil;
import nc.uap.plugin.studio.ui.preference.developer.conn.DeveloperDBService;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import nc.uap.plugin.studio.CommonPlugin;
import nc.uap.plugin.studio.database.meta.DataSourceChangeManager;
import nc.uap.plugin.studio.database.meta.IDataSourceChangeListener;
import java.util.HashMap;
import java.util.Map;

public class DBDevelopLabelService
{
    private static final String[] ENUM_ORGTYPES;
    private static final String[] ENUM_ASSETLAYOUTS;
    private static Map<String, String> pkModuleMap;
    private static Map<String, String> pkAAMIndustryMap;
    private static Map<String, String> pkIndustryMap;
    private static Map<String, String> mainNameMap;
    private static Map<String, String> industryCodeMap;
    private static Map<String, String> mainNameCodeMap;
    private static Map<String, String> pkCountryMap;
    private static Map<String, String> pkCountryCodeMap;
    private static String curLangSuffix;
    
    static
    {
        ENUM_ORGTYPES = new String[]{
            "",
            Messages.LabelTextProvider_Enum_OrgType_0,
            Messages.LabelTextProvider_Enum_OrgType_1,
            Messages.LabelTextProvider_Enum_OrgType_2,
            Messages.LabelTextProvider_Enum_OrgType_3,
            Messages.LabelTextProvider_Enum_OrgType_4,
            Messages.LabelTextProvider_Enum_OrgType_5,
            Messages.LabelTextProvider_Enum_OrgType_6,
            Messages.LabelTextProvider_Enum_OrgType_7,
            Messages.LabelTextProvider_Enum_OrgType_8};
        ENUM_ASSETLAYOUTS = new String[]{
            Messages.LabelTextProvider_Enum_AssetLayout_7,
            Messages.LabelTextProvider_Enum_AssetLayout_0,
            Messages.LabelTextProvider_Enum_AssetLayout_1,
            Messages.LabelTextProvider_Enum_AssetLayout_2,
            Messages.LabelTextProvider_Enum_AssetLayout_3,
            Messages.LabelTextProvider_Enum_AssetLayout_4,
            Messages.LabelTextProvider_Enum_AssetLayout_5,
            Messages.LabelTextProvider_Enum_AssetLayout_6};
        DBDevelopLabelService.pkModuleMap = new HashMap<String, String>();
        DBDevelopLabelService.pkAAMIndustryMap = new HashMap<String, String>();
        DBDevelopLabelService.pkIndustryMap = new HashMap<String, String>();
        DBDevelopLabelService.mainNameMap = new HashMap<String, String>();
        DBDevelopLabelService.industryCodeMap = new HashMap<String, String>();
        DBDevelopLabelService.mainNameCodeMap = new HashMap<String, String>();
        DBDevelopLabelService.pkCountryMap = new HashMap<String, String>();
        DBDevelopLabelService.pkCountryCodeMap = new HashMap<String, String>();
        final IDataSourceChangeListener listener = (IDataSourceChangeListener) new IDataSourceChangeListener()
        {
            public void dataSourceChange()
            {
                this.designDataSourceChange();
            }
            
            public void designDataSourceChange()
            {
                DBDevelopLabelService.pkAAMIndustryMap.clear();
                DBDevelopLabelService.pkCountryMap.clear();
                DBDevelopLabelService.pkCountryCodeMap.clear();
                DBDevelopLabelService.pkIndustryMap.clear();
                DBDevelopLabelService.industryCodeMap.clear();
                DBDevelopLabelService.pkModuleMap.clear();
                DBDevelopLabelService.access$6(null);
            }
        };
        DataSourceChangeManager.registerListener(listener);
    }
    
    public static String getOrgType(final String orgType)
    {
        return getEnumValue(orgType, DBDevelopLabelService.ENUM_ORGTYPES);
    }
    
    public static String getAssetLayout(final String layout)
    {
        return getEnumValue(layout, DBDevelopLabelService.ENUM_ASSETLAYOUTS, 1);
    }
    
    protected static String getEnumValue(final String enumCode, final String[] enumValues)
    {
        if (enumCode != null && !enumCode.equals("") && !enumCode.equals(""))
        {
            try
            {
                final int code = Integer.valueOf(enumCode);
                if (code >= 0 && code < enumValues.length)
                {
                    return enumValues[code];
                }
            }
            catch (final NumberFormatException e)
            {
                CommonPlugin.getDefault().getLog()
                    .log((IStatus) new Status(4, "com.yonyou.studio.common.core.ui", e.getMessage(), (Throwable) e));
            }
        }
        return "";
    }
    
    protected static String getEnumValue(final String enumCode, final String[] enumValues, final int offset)
    {
        if (enumCode != null && !enumCode.equals("") && !enumCode.equals(""))
        {
            try
            {
                int code = Integer.valueOf(enumCode);
                code += offset;
                if (code >= 0 && code < enumValues.length)
                {
                    return enumValues[code];
                }
            }
            catch (final NumberFormatException e)
            {
                CommonPlugin.getDefault().getLog()
                    .log((IStatus) new Status(4, "com.yonyou.studio.common.core.ui", e.getMessage(), (Throwable) e));
            }
        }
        return "";
    }
    
    public static String getModule(final String moduleCode)
    {
        // if (DeveloperDBService.isTestPass()) return moduleCode;
        String name = DBDevelopLabelService.pkModuleMap.get(moduleCode);
        if (name == null)
        {
            try
            {
                name = DeveloperDBService.getLabel("dap_dapsystem", "moduleid", "systypename", moduleCode);
            }
            catch (final Exception ex)
            {
                name = "";
            }
            DBDevelopLabelService.pkModuleMap.put(moduleCode, name);
        }
        return name;
    }
    
    public static String getIndustry(final String industry)
    {
        // if (DeveloperDBService.isTestPass()) return industry;
        String name = DBDevelopLabelService.pkIndustryMap.get(industry);
        if (name == null)
        {
            try
            {
                name = DeveloperDBService.getLabel("bd_industry", "pk_industry", "name" + getLangSuffix(), "name", industry);
            }
            catch (final Exception ex)
            {
                name = "";
            }
            DBDevelopLabelService.pkIndustryMap.put(industry, name);
        }
        return name;
    }
    
    public static String getMainName(final String industry)
    {
        // if (DeveloperDBService.isTestPass()) return industry;
        String name = DBDevelopLabelService.mainNameMap.get(industry);
        if (name == null)
        {
            try
            {
                name = DeveloperDBService.getMainLevel("bd_industry", "pk_industry", "name" + getLangSuffix(), "name", industry);
            }
            catch (final Exception ex)
            {
                name = "";
            }
            DBDevelopLabelService.mainNameMap.put(industry, name);
        }
        return name;
    }
    
    public static String getIndustryCode(final String industry)
    {
        // if (DeveloperDBService.isTestPass()) return industry;
        String code = DBDevelopLabelService.industryCodeMap.get(industry);
        if (code == null)
        {
            try
            {
                code = DeveloperDBService.getLabel("bd_industry", "pk_industry", "code", industry);
            }
            catch (final Exception ex)
            {
                code = "";
            }
            DBDevelopLabelService.industryCodeMap.put(industry, code);
        }
        return code.trim();
    }
    
    public static String getmainNameCode(final String industry)
    {
        // if (DeveloperDBService.isTestPass()) return industry;
        String code = DBDevelopLabelService.mainNameCodeMap.get(industry);
        if (code == null)
        {
            try
            {
                code = DeveloperDBService.getmainNameLabel("bd_industry", "pk_industry", "parent_id", industry);
            }
            catch (final Exception ex)
            {
                code = "";
            }
            DBDevelopLabelService.mainNameCodeMap.put(industry, code);
        }
        return code.trim();
    }
    
    public static String getAAMIndustry(final String industry)
    {
        // if (DeveloperDBService.isTestPass()) return industry;
        
        String name = DBDevelopLabelService.pkAAMIndustryMap.get(industry);
        if (name == null)
        {
            try
            {
                name = DeveloperDBService.getLabel("aam_industry", "pk_industry", "name" + getLangSuffix(), "name", industry);
            }
            catch (final Exception ex)
            {
                name = "";
            }
            DBDevelopLabelService.pkAAMIndustryMap.put(industry, name);
        }
        return name;
    }
    
    public static String getCountry(final String country)
    {
        // if (DeveloperDBService.isTestPass()) return country;
        
        String name = DBDevelopLabelService.pkCountryMap.get(country);
        if (name == null)
        {
            try
            {
                name = DeveloperDBService.getLabel("aam_countryzone", "pk_countryzone", "countryzonename" + getLangSuffix(),
                    "countryzonename", country);
            }
            catch (final Exception ex)
            {
                name = "";
            }
            DBDevelopLabelService.pkCountryMap.put(country, name);
        }
        return name;
    }
    
    public static String getCountryCode(final String country)
    {
        // if (DeveloperDBService.isTestPass()) return country;
        String code = DBDevelopLabelService.pkCountryCodeMap.get(country);
        if (code == null)
        {
            try
            {
                code = DeveloperDBService.getLabel("aam_countryzone", "pk_countryzone", "countryzonecode", country);
            }
            catch (final Exception ex)
            {
                code = "";
            }
            DBDevelopLabelService.pkCountryCodeMap.put(country, code);
        }
        return code;
    }
    
    public static String getLangSuffix()
    {
        if (DBDevelopLabelService.curLangSuffix == null)
        {
            String localLang = "ZH";
            if (StudioUtil.getStudioLang().equals("en"))
            {
                localLang = "EN";
            }
            String seq = "1";
            try
            {
                seq = DeveloperDBService.getLabel("pub_multilang", "locallang", "langseq", localLang);
            }
            catch (final Exception e)
            {
                CommonPlugin.getDefault().getLog().log((IStatus) new Status(4, "com.yonyou.studio.common.core.ui",
                    Messages.DBDevelopLabelService_QueryLangSuffixFailed, (Throwable) e));
            }
            if (seq.equals("1"))
            {
                DBDevelopLabelService.curLangSuffix = "";
            }
            else
            {
                DBDevelopLabelService.curLangSuffix = seq;
            }
        }
        return DBDevelopLabelService.curLangSuffix;
    }
    
    public static String getLabel(final String str)
    {
        return (str == null || str.equals("") || str.equals("~")) ? "" : str;
    }
    
    public static String getMultiLangLabel(final Object obj, final String field)
    {
        String returnStr = null;
        final String langField = String.valueOf(field) + getLangSuffix();
        String getMethodName = "get" + langField.substring(0, 1).toUpperCase() + langField.substring(1);
        try
        {
            final Method method = obj.getClass().getMethod(getMethodName, (Class<?>[]) new Class[0]);
            returnStr = (String) method.invoke(obj, new Object[0]);
            if (!getLangSuffix().equals("") && (returnStr == null || returnStr.trim().equals("")))
            {
                getMethodName = "get" + field.substring(0, 1).toUpperCase() + field.substring(1);
                final Method method2 = obj.getClass().getMethod(getMethodName, (Class<?>[]) new Class[0]);
                returnStr = (String) method2.invoke(obj, new Object[0]);
            }
        }
        catch (final SecurityException e)
        {
            CommonPlugin.getDefault().getLog()
                .log((IStatus) new Status(4, "com.yonyou.studio.common.core.ui", e.getMessage(), (Throwable) e));
        }
        catch (final NoSuchMethodException e2)
        {
            CommonPlugin.getDefault().getLog()
                .log((IStatus) new Status(4, "com.yonyou.studio.common.core.ui", e2.getMessage(), (Throwable) e2));
        }
        catch (final IllegalArgumentException e3)
        {
            CommonPlugin.getDefault().getLog()
                .log((IStatus) new Status(4, "com.yonyou.studio.common.core.ui", e3.getMessage(), (Throwable) e3));
        }
        catch (final IllegalAccessException e4)
        {
            CommonPlugin.getDefault().getLog()
                .log((IStatus) new Status(4, "com.yonyou.studio.common.core.ui", e4.getMessage(), (Throwable) e4));
        }
        catch (final InvocationTargetException e5)
        {
            CommonPlugin.getDefault().getLog()
                .log((IStatus) new Status(4, "com.yonyou.studio.common.core.ui", e5.getMessage(), (Throwable) e5));
        }
        return getLabel(returnStr);
    }
    
    static void access$6(final String curLangSuffix)
    {
        DBDevelopLabelService.curLangSuffix = curLangSuffix;
    }
}
