package com.yonyou.model.utils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.util.Map;
import java.util.Set;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import org.apache.commons.io.FileUtils;
import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.progress.ProgressIndicator;
import com.intellij.openapi.progress.ProgressManager;
import com.intellij.openapi.progress.Task;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.ui.Messages;
import com.yonyou.model.form.NCHomeConfigForm;
import com.yonyou.model.service.NCHomeConfigService;
import com.yonyou.model.utils.project.LibraryUtil;
import com.yonyou.model.utils.project.ProjectManager;
import com.yonyou.model.vo.NCHomeConfigVO;
import com.yonyou.search.utils.ActionclassSearchToolUtil;
import com.yonyou.search.vo.UpdateClassEnum;
import com.yonyou.util.PropXml;
import com.yonyou.util.StringUtils;
import com.yonyou.util.prop.DataSourceMeta;
import com.yonyou.util.prop.DatabaseDriverInfo;
import com.yonyou.util.prop.DriverInfo;
import com.yonyou.util.prop.PropInfo;
import com.yonyou.util.rsa.ToolUtils;
import com.yonyou.util.task.CustomBackgroundableTask;
import com.yonyou.util.task.CustomModalTask;

public class ConfigUtil
{
    public static void applyFun(NCHomeConfigForm configFrom)
    {
        Project project = ProjectManager.getInstance().getProject();
        String oldHomePath = NCHomeConfigService.getInstance().getState().getHomePath();
        String newHomePath = ((JTextField) configFrom.getComponent("homePathText", JTextField.class)).getText();
        newHomePath = newHomePath == null ? "" : newHomePath;
        final String fnewHomePath = newHomePath;
        String databaseType = (String) ((JComboBox) configFrom.getComponent("databaseTypeBox", JComboBox.class)).getSelectedItem();
        String driverClassName = (String) ((JComboBox) configFrom.getComponent("driverClassNameBox", JComboBox.class)).getSelectedItem();
        DriverInfo[] driverInfos = (DriverInfo[]) configFrom.getDriverForDatabaseMap().get(databaseType);
        DriverInfo driverInfo = DataSourceUtil.getDriverInfo(driverInfos, driverClassName);
        String[] driverLibPaths = driverInfo.getDriverLib().split(",");
        String[] driverLibAllPaths = new String[driverLibPaths.length];
        
        for (int i = 0; i < driverLibPaths.length; ++i)
        {
            String driverLib = driverLibPaths[i];
            driverLibAllPaths[i] = driverLib.startsWith("driver") ? newHomePath + File.separator + driverLib
                : (driverLib.contains("/") ? newHomePath + File.separator + "driver" + File.separator + driverLib : null);
        }
        
        final String driverLibUrl = StringUtils.join(driverLibAllPaths, ",");
        NCHomeConfigVO configVO = NCHomeConfigService.getInstance().getState();
        if (!oldHomePath.equals(newHomePath))
        {
            configVO.setHomePath(newHomePath);
            LibraryUtil.delLibrary(true);
            ProgressManager.getInstance()
                .run((Task) (!configVO.isAsyncTask() ? new CustomModalTask(ProjectManager.getInstance().getProject(), "加载中")
                {
                    public void run(@NotNull ProgressIndicator indicator)
                    {
                        LibraryUtil.initLibrary(fnewHomePath, true, driverLibUrl);
                    }
                } : new CustomBackgroundableTask(ProjectManager.getInstance().getProject(), "加载中")
                {
                    public void run(@NotNull ProgressIndicator indicator)
                    {
                        LibraryUtil.initLibrary(fnewHomePath, true, driverLibUrl);
                    }
                }));
            System.out.println("-------------------更新类库 完成");
            JdkUtil.changeJdk(newHomePath);
            if ((new File(newHomePath + ActionclassSearchToolUtil.YYCONFIG_PATH)).exists())
            {
                ProgressManager.getInstance().run(new CustomBackgroundableTask(ProjectManager.getInstance().getProject(), "加载中")
                {
                    public void run(@NotNull ProgressIndicator indicator)
                    {
                        ActionclassSearchToolUtil.updateActionVOList(project, UpdateClassEnum.HOMEC);
                    }
                });
            }
        }
        else if (configFrom.isNeedUpdateDbLibrary())
        {
            LibraryUtil.delDBDriverLibrary();
            ProgressManager.getInstance()
                .run((Task) (!configVO.isAsyncTask() ? new CustomModalTask(ProjectManager.getInstance().getProject(), "加载中")
                {
                    public void run(@NotNull ProgressIndicator indicator)
                    {
                        LibraryUtil.initDBDriverLibrary(driverLibUrl);
                    }
                } : new CustomBackgroundableTask(ProjectManager.getInstance().getProject(), "加载中")
                {
                    public void run(@NotNull ProgressIndicator indicator)
                    {
                        LibraryUtil.initDBDriverLibrary(driverLibUrl);
                    }
                }));
        }
        
        DataSourceMeta dataSourceMeta = getDatasourceInfoForModel(configFrom);
        saveDatasourceInfo(dataSourceMeta, configFrom, false);
        if (getDirectJDKLogJdkVersion(configVO.getHomePath()) >= 50)
        {
            InputStream inputStream = ConfigUtil.class.getClassLoader().getResourceAsStream("replacement/DirectJDKLog.class");
            File targetFile = new File(configVO.getHomePath() + "/middleware/classes/org/apache/juli/logging/DirectJDKLog.class");
            if (!targetFile.exists())
            {
                try
                {
                    FileUtils.copyInputStreamToFile(inputStream, targetFile);
                }
                catch (IOException e)
                {
                    Messages.showErrorDialog(e.getMessage(), "错误");
                }
            }
        }
        
        System.out.println("-------------------全部 完成");
    }
    
    public static DataSourceMeta getDatasourceInfoForModel(NCHomeConfigForm configFrom)
    {
        String datasource = (String) ((JComboBox) configFrom.getComponent("datasourceBox", JComboBox.class)).getSelectedItem();
        String oid = ((JTextField) configFrom.getComponent("oidText", JTextField.class)).getText();
        String databaseType = (String) ((JComboBox) configFrom.getComponent("databaseTypeBox", JComboBox.class)).getSelectedItem();
        String driverClassName = (String) ((JComboBox) configFrom.getComponent("driverClassNameBox", JComboBox.class)).getSelectedItem();
        String userName = ((JTextField) configFrom.getComponent("userNameText", JTextField.class)).getText();
        // String password = ((JPasswordField)
        // configFrom.getComponent("passwordText",JPasswordField.class)).getText();
        String password = configFrom.getComponent("passwordText", JTextField.class).getText();
        String dbName = ((JTextField) configFrom.getComponent("dbNameText", JTextField.class)).getText();
        String address = ((JTextField) configFrom.getComponent("addressText", JTextField.class)).getText();
        String port = ((JTextField) configFrom.getComponent("portText", JTextField.class)).getText();
        int driverIndex = ((JComboBox) configFrom.getComponent("driverClassNameBox", JComboBox.class)).getSelectedIndex();
        DriverInfo[] driverInfos = (DriverInfo[]) configFrom.getDriverForDatabaseMap().get(databaseType);
        DriverInfo driverInfo = driverInfos[driverIndex];
        String driverBaseUrl = driverInfo.getDriverUrl();
        String databaseUrl = "";
        if (ToolUtils.isJDBCUrl(driverBaseUrl))
        {
            databaseUrl = ToolUtils.getJDBCUrl(driverBaseUrl, dbName, address, port);
        }
        else
        {
            databaseUrl = ToolUtils.getODBCUrl(driverBaseUrl, dbName);
        }
        
        String databaseSubType = null;
        
        try
        {
            PropXml propXml = new PropXml();
            String newHomePath = ((JTextField) configFrom.getComponent("homePathText", JTextField.class)).getText();
            DatabaseDriverInfo[] database = propXml.getDriverSet(newHomePath).getDatabase();
            
            for (DatabaseDriverInfo databaseDriverInfo : database)
            {
                if (StringUtils.equals(databaseDriverInfo.toString(), databaseType))
                {
                    databaseSubType = databaseDriverInfo.getDatabaseSubType();
                    break;
                }
            }
        }
        catch (Exception e)
        {
            throw new RuntimeException(e);
        }
        
        DataSourceMeta meta = new DataSourceMeta();
        meta.setDataSourceName(datasource);
        meta.setOidMark(oid);
        meta.setDatabaseUrl(databaseUrl);
        meta.setUser(userName);
        meta.setPassword(password);
        meta.setDriverClassName(driverInfo.getDriverClass());
        meta.setDatabaseType(databaseType);
        meta.setDatabaseSubType(databaseSubType);
        meta.setMaxCon(50);
        meta.setMinCon(1);
        return meta;
    }
    
    public static void delDatabase(NCHomeConfigForm configFrom)
    {
        String datasource = (String) ((JComboBox) configFrom.getComponent("datasourceBox", JComboBox.class)).getSelectedItem();
        if ("design".equals(datasource))
        {
            Messages.showErrorDialog("禁止删除design开发库！", "错误");
        }
        else
        {
            DataSourceMeta dataSourceMeta = new DataSourceMeta();
            dataSourceMeta.setDataSourceName(datasource);
            saveDatasourceInfo(dataSourceMeta, configFrom, true);
        }
        
    }
    
    public static void setDesign(NCHomeConfigForm configFrom)
    {
        DataSourceMeta dataSourceMeta = getDatasourceInfoForModel(configFrom);
        dataSourceMeta.setDataSourceName("design");
        saveDatasourceInfo(dataSourceMeta, configFrom, false);
    }
    
    public static void copyDatabase(String newDataSourceName, NCHomeConfigForm configFrom)
    {
        DataSourceMeta dataSourceMeta = getDatasourceInfoForModel(configFrom);
        dataSourceMeta.setDataSourceName(newDataSourceName);
        saveDatasourceInfo(dataSourceMeta, configFrom, false);
    }
    
    public static void saveDatasourceInfo(DataSourceMeta newDataSourceMeta, NCHomeConfigForm configFrom, boolean isDel)
    {
        String baseDatabase = (String) ((JComboBox) configFrom.getComponent("baseDatabaseBox", JComboBox.class)).getSelectedItem();
        String newDataSourceName = newDataSourceMeta.getDataSourceName();
        Map<String, DataSourceMeta> dataSourceMetaMap = configFrom.getDataSourceMetaMap();
        if (isDel)
        {
            baseDatabase = baseDatabase.equals(newDataSourceName) ? "design" : baseDatabase;
            dataSourceMetaMap.remove(newDataSourceName);
        }
        else
        {
            dataSourceMetaMap.put(newDataSourceName, newDataSourceMeta);
        }
        
        Set<String> keySet = dataSourceMetaMap.keySet();
        String[] keys = (String[]) keySet.toArray(new String[keySet.size()]);
        DataSourceMeta[] metas = new DataSourceMeta[keys.length];
        
        for (int i = 0; i < keys.length; ++i)
        {
            DataSourceMeta dataSourceMeta = (DataSourceMeta) dataSourceMetaMap.get(keys[i]);
            dataSourceMeta.setBase(baseDatabase.equals(keys[i]));
            metas[i] = dataSourceMeta;
        }
        
        String homePath = ((JTextField) configFrom.getComponent("homePathText", JTextField.class)).getText();
        
        try
        {
            PropXml propXml = new PropXml();
            PropInfo propInfo = propXml.loadPropInfo(homePath);
            propInfo.setDataSource(metas);
            propInfo.setSecurityDataSource(isDel ? "design" : newDataSourceName);
            propXml.storePorpInfo(homePath, propInfo);
            String selectDatasourceName = isDel ? "design" : newDataSourceMeta.getDataSourceName();
            DataSourceUtil.initDataSourceConfig(configFrom, selectDatasourceName);
        }
        catch (Exception e)
        {
            Messages.showErrorDialog(e.getMessage(), "错误");
        }
        
    }
    
    private static int getDirectJDKLogJdkVersion(String homePath)
    {
        String jarFilePath = homePath + File.separator + "middleware" + File.separator + "tcsrc.jar";
        if (!(new File(jarFilePath)).exists())
        {
            jarFilePath = homePath + File.separator + "middleware" + File.separator + "tomcat.jar";
            if (!(new File(jarFilePath)).exists())
            {
                return -1;
            }
        }
        
        String className = "org.apache.juli.logging.DirectJDKLog";
        
        try
        {
            try (JarFile jarFile = new JarFile(new File(jarFilePath)))
            {
                JarEntry entry = jarFile.getJarEntry(className.replace(".", "/") + ".class");
                if (entry != null)
                {
                    byte[] classBytes = new byte[(int) entry.getSize()];
                    
                    try (InputStream inputStream = jarFile.getInputStream(entry))
                    {
                        inputStream.read(classBytes);
                    }
                    
                    int majorVersion = (classBytes[6] & 255) << 8 | classBytes[7] & 255;
                    int var7 = majorVersion;
                    return var7;
                }
            }
            
            return -1;
        }
        catch (IOException e)
        {
            throw new RuntimeException(e);
        }
    }
}
