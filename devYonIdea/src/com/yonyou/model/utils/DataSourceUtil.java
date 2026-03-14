package com.yonyou.model.utils;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

import javax.swing.DefaultComboBoxModel;
import javax.swing.JButton;
import javax.swing.JComboBox;
import javax.swing.JTextField;

import com.intellij.openapi.ui.Messages;
import com.yonyou.model.form.NCHomeConfigForm;
import com.yonyou.model.utils.project.FileTool;
import com.yonyou.util.PropXml;rceMeta;
import com.yonyou.util.prop.DatabaseDriverInfo;
import com.yonyou.util.prop.DriverInfo;
import com.yonyou.util.rsa.ToolUtils;

public class DataSourceUtil
{
    public static void initDataSourceConfig(NCHomeConfigForm configFrom, String selectDatasourceName)
    {
        String homePath = ((JTextField) configFrom.getComponent("homePathText", JTextField.class)).getText();
        if (StringUtils.isNotBlank(homePath))
        {
            PropXml propXml = new PropXml();
            configFrom.setDriverForDatabaseMap(new HashMap());
            configFrom.setDataSourceMetaMap(new HashMap());
            
            try
            {
                DatabaseDriverInfo[] driverInfos = propXml.getDriverSet(homePath).getDatabase();
                String[] databaseTypeItems = new String[driverInfos.length];
                
                for (int i = 0; i < driverInfos.length; ++i)
                {
                    String databaseType = driverInfos[i].toString();
                    databaseTypeItems[i] = databaseType;
                    configFrom.getDriverForDatabaseMap().put(databaseType, driverInfos[i].getDatabase());
                }
                
                ((JComboBox) configFrom.getComponent("databaseTypeBox", JComboBox.class))
                    .setModel(new DefaultComboBoxModel(databaseTypeItems));
                DataSourceMeta[] dataSourceMetas = propXml.getDSMetaWithDesign(homePath);
                String[] datasourceItems = new String[dataSourceMetas.length];
                String baseDatabase = "design";
                
                for (int i = 0; i < dataSourceMetas.length; ++i)
                {
                    String dataSourceName = dataSourceMetas[i].getDataSourceName();
                    if (dataSourceName.length() > 20)
                    {
                        dataSourceName = dataSourceName.substring(0, 18) + "...";
                    }
                    
                    if (dataSourceMetas[i].isBase())
                    {
                        baseDatabase = dataSourceName;
                    }
                    
                    datasourceItems[i] = dataSourceName;
                    configFrom.getDataSourceMetaMap().put(dataSourceName, dataSourceMetas[i]);
                }
                
                ((JComboBox) configFrom.getComponent("datasourceBox", JComboBox.class)).setModel(new DefaultComboBoxModel(datasourceItems));
                ((JComboBox) configFrom.getComponent("baseDatabaseBox", JComboBox.class))
                    .setModel(new DefaultComboBoxModel(datasourceItems));
                ((JComboBox) configFrom.getComponent("baseDatabaseBox", JComboBox.class)).setSelectedItem(baseDatabase);
                selectDatasourceName = selectDatasourceName == null ? "design" : selectDatasourceName;
                if (selectDatasourceName.length() > 20)
                {
                    selectDatasourceName = selectDatasourceName.substring(0, 18) + "...";
                }
                
                ((JComboBox) configFrom.getComponent("datasourceBox", JComboBox.class)).setSelectedItem(selectDatasourceName);
                initDatabaseConfig(configFrom, selectDatasourceName);
            }
            catch (Exception e)
            {
                Messages.showErrorDialog(e.getMessage(), "错误");
            }
        }
        
    }
    
    public static void initDatabaseConfig(NCHomeConfigForm configFrom, String datasourceName)
    {
        DataSourceMeta dataSourceMeta = (DataSourceMeta) configFrom.getDataSourceMetaMap().get(datasourceName);
        ((JComboBox) configFrom.getComponent("databaseTypeBox", JComboBox.class)).setSelectedItem(dataSourceMeta.getDatabaseTypeName());
        ((JComboBox) configFrom.getComponent("driverClassNameBox", JComboBox.class)).setSelectedItem(dataSourceMeta.getDriverClassName());
        initDriverClassBoxItems(configFrom);
        if (ToolUtils.isJDBCUrl(dataSourceMeta.getDatabaseUrl()))
        {
            String[] info = ToolUtils.getJDBCInfo(dataSourceMeta.getDatabaseUrl());
            ((JTextField) configFrom.getComponent("addressText", JTextField.class)).setText(info[0]);
            ((JTextField) configFrom.getComponent("portText", JTextField.class)).setText(info[1]);
            String dbname = info[2];
            if (dbname.indexOf("?") != -1)
            {
                dbname = dbname.substring(0, dbname.indexOf("?"));
            }
            
            ((JTextField) configFrom.getComponent("dbNameText", JTextField.class)).setText(dbname);
        }
        else
        {
            ((JTextField) configFrom.getComponent("addressText", JTextField.class)).setText("");
            ((JTextField) configFrom.getComponent("portText", JTextField.class)).setText("");
            ((JTextField) configFrom.getComponent("dbNameText", JTextField.class)).setText("");
        }
        
        ((JTextField) configFrom.getComponent("oidText", JTextField.class)).setText(dataSourceMeta.getOidMark());
        ((JTextField) configFrom.getComponent("userNameText", JTextField.class)).setText(dataSourceMeta.getUser());
        // ((JPasswordField) configFrom.getComponent("passwordText",
        // JPasswordField.class)).setText(dataSourceMeta.getPassword());
        ((JTextField) configFrom.getComponent("passwordText", JTextField.class)).setText(dataSourceMeta.getPassword());
    }
    
    public static void initDriverClassBoxItems(NCHomeConfigForm configFrom)
    {
        String databaseType = (String) ((JComboBox) configFrom.getComponent("databaseTypeBox", JComboBox.class)).getSelectedItem();
        if (StringUtils.isNotBlank(databaseType))
        {
            DriverInfo[] driverInfos = (DriverInfo[]) configFrom.getDriverForDatabaseMap().get(databaseType);
            String[] driverItems = new String[driverInfos.length];
            
            for (int i = 0; i < driverInfos.length; ++i)
            {
                driverItems[i] = driverInfos[i].getDriverType();
            }
            
            ((JComboBox) configFrom.getComponent("driverClassNameBox", JComboBox.class)).setModel(new DefaultComboBoxModel(driverItems));
            changedDriverClassBox(configFrom);
        }
        
    }
    
    public static void changedDriverClassBox(NCHomeConfigForm configFrom)
    {
        String databaseType = (String) ((JComboBox) configFrom.getComponent("databaseTypeBox", JComboBox.class)).getSelectedItem();
        int driverClassNameIndex = ((JComboBox) configFrom.getComponent("driverClassNameBox", JComboBox.class)).getSelectedIndex();
        if (StringUtils.isNotBlank(databaseType) && driverClassNameIndex != -1)
        {
            DriverInfo[] driverInfos = (DriverInfo[]) configFrom.getDriverForDatabaseMap().get(databaseType);
            DriverInfo driverInfo = driverInfos[driverClassNameIndex];
            if (ToolUtils.isJDBCUrl(driverInfo.getDriverUrl()))
            {
                String[] info = ToolUtils.getJDBCInfo(driverInfo.getDriverUrl());
                ((JTextField) configFrom.getComponent("portText", JTextField.class)).setText(info[1]);
            }
            else
            {
                ((JTextField) configFrom.getComponent("portText", JTextField.class)).setText("");
            }
        }
        
    }
    
    public static boolean checkNCHomePath(NCHomeConfigForm configFrom, String homePath)
    {
        if (homePath == null)
        {
            homePath = ((JTextField) configFrom.getComponent("homePathText", JTextField.class)).getText();
        }
        
        String propFilePath = homePath + "/ierp/bin/prop.xml";
        File propFile = new File(propFilePath);
        boolean exists = propFile.exists();
        ((JComboBox) configFrom.getComponent("datasourceBox", JComboBox.class)).setEnabled(exists);
        ((JComboBox) configFrom.getComponent("databaseTypeBox", JComboBox.class)).setEnabled(exists);
        ((JComboBox) configFrom.getComponent("driverClassNameBox", JComboBox.class)).setEnabled(exists);
        ((JTextField) configFrom.getComponent("addressText", JTextField.class)).setEnabled(exists);
        ((JTextField) configFrom.getComponent("portText", JTextField.class)).setEnabled(exists);
        ((JTextField) configFrom.getComponent("dbNameText", JTextField.class)).setEnabled(exists);
        ((JTextField) configFrom.getComponent("oidText", JTextField.class)).setEnabled(exists);
        ((JTextField) configFrom.getComponent("userNameText", JTextField.class)).setEnabled(exists);
        ((JTextField) configFrom.getComponent("passwordText", JTextField.class)).setEnabled(exists);
        // ((JPasswordField) configFrom.getComponent("passwordText",  JPasswordField.class)).setEnabled(exists);
        ((JComboBox) configFrom.getComponent("baseDatabaseBox", JComboBox.class)).setEnabled(exists);
        ((JButton) configFrom.getComponent("buttonSetDesign", JButton.class)).setEnabled(exists);
        ((JButton) configFrom.getComponent("buttonTestConnect", JButton.class)).setEnabled(exists);
        ((JButton) configFrom.getComponent("buttonCopyDatabase", JButton.class)).setEnabled(exists);
        ((JButton) configFrom.getComponent("buttonDeleteDatabase", JButton.class)).setEnabled(exists);
        return exists;
    }
    
    public static DriverInfo getDriverInfo(DriverInfo[] driverInfos, String driverType)
    {
        DriverInfo driverInfo = null;
        
        for (int i = 0; i < driverInfos.length; ++i)
        {
            if (driverInfos[i].getDriverType().equals(driverType))
            {
                driverInfo = driverInfos[i];
            }
        }
        
        return driverInfo;
    }
    
    public static DriverInfo getDriverInfoByDriverClass(DriverInfo[] driverInfos, String driverClassName)
    {
        DriverInfo driverInfo = null;
        
        for (int i = 0; i < driverInfos.length; ++i)
        {
            if (driverInfos[i].getDriverClass().equals(driverClassName))
            {
                driverInfo = driverInfos[i];
                break;
            }
        }
        
        return driverInfo;
    }
    
    public static void copyDb(NCHomeConfigForm configFrom, String oldHomePath)
    {
        String homePath = ((JTextField) configFrom.getComponent("homePathText", JTextField.class)).getText();
        PropXml propXml = new PropXml();
        
        try
        {
            List<DatabaseDriverInfo> newDbdriverList = new ArrayList();
            DatabaseDriverInfo[] driverInfos = propXml.getDriverSet(homePath).getDatabase();
            DatabaseDriverInfo[] oldDriverInfos = propXml.getDriverSet(oldHomePath).getDatabase();
            newDbdriverList.addAll(Arrays.asList(driverInfos));
            
            for (DatabaseDriverInfo dbDriver : oldDriverInfos)
            {
                Optional<DatabaseDriverInfo> first =
                    newDbdriverList.stream().filter((dbd) -> StringUtils.equals(dbd.toString(), dbDriver.toString())).findFirst();
                if (!first.isPresent())
                {
                    newDbdriverList.add(dbDriver);
                }
            }
            
            propXml.saveDatabaseDriver(homePath, (DatabaseDriverInfo[]) newDbdriverList.toArray(new DatabaseDriverInfo[0]));
            List<DataSourceMeta> newDbmeta = new ArrayList();
            DataSourceMeta[] dataSourceMetas = propXml.getDSMetaWithDesign(homePath);
            DataSourceMeta[] oldDataSourceMetas = propXml.getDSMetaWithDesign(oldHomePath);
            newDbmeta.addAll(Arrays.asList(dataSourceMetas));
            
            for (DataSourceMeta dsm : oldDataSourceMetas)
            {
                Optional<DataSourceMeta> optional = newDbmeta.stream().filter((ndm) -> ndm.equals(dsm)).findFirst();
                if (!optional.isPresent())
                {
                    Optional<DataSourceMeta> first = newDbmeta.stream()
                        .filter((ndm) -> StringUtils.equals(ndm.getDataSourceName(), dsm.getDataSourceName())).findFirst();
                    if (first.isPresent())
                    {
                        dsm.setDataSourceName(dsm.getDataSourceName() + "(old)");
                    }
                    
                    newDbmeta.add(dsm);
                }
            }
            
            propXml.saveMeta(homePath, (DataSourceMeta[]) newDbmeta.toArray(new DataSourceMeta[0]));
            FileTool.copyFolder(oldHomePath + File.separator + "driver", homePath + File.separator + "driver");
            initDataSourceConfig(configFrom, (String) null);
        }
        catch (Exception e)
        {
            Messages.showErrorDialog(e.getMessage(), "错误");
        }
        
    }
}
