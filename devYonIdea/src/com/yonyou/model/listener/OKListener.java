package com.yonyou.model.listener;

import java.awt.event.ActionEvent;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import com.intellij.openapi.project.Project;
import com.intellij.openapi.wm.StatusBar;
import com.intellij.openapi.wm.WindowManager;
import com.yonyou.model.form.NCHomeConfigForm;
import com.yonyou.model.utils.ConfigUtil;
import com.yonyou.model.utils.DataSourceUtil;
import com.yonyou.model.utils.project.ProjectManager;
import com.yonyou.statusbar.factory.DbsourceSetPanel;
import com.yonyou.util.ConfigurationUtils;
import com.yonyou.util.MyNotifier;
import com.yonyou.util.prop.DriverInfo;

public class OKListener extends AbstractActionListener
{
    public OKListener(NCHomeConfigForm configFrom)
    {
        super(configFrom);
    }
    
    public void actionPerformed(ActionEvent actionEvent)
    {
        NCHomeConfigForm configFrom = this.getConfigFrom();
        ConfigUtil.applyFun(configFrom);
        configFrom.setNeedUpdateDbLibrary(false);
        configFrom.dispose();
        Project project = ProjectManager.getInstance().getProject();
        StatusBar statusBar = WindowManager.getInstance().getStatusBar(project);
        DbsourceSetPanel dbsourceSet = (DbsourceSetPanel) statusBar.getWidget("DbsourceSet");
        dbsourceSet.update();
        ConfigurationUtils.getInstance().updateMiscellaneousConfiguration();
        
        
        // ------------------ 数据源信息如下 ---------------------------
        String databaseType = (String) ((JComboBox) configFrom.getComponent("databaseTypeBox", (Class) JComboBox.class)).getSelectedItem();
        String driverClassName =
            (String) ((JComboBox) configFrom.getComponent("driverClassNameBox", (Class) JComboBox.class)).getSelectedItem();
        String homePath = ((JTextField) configFrom.getComponent("homePathText", (Class) JTextField.class)).getText();
        DriverInfo[] driverInfos = configFrom.getDriverForDatabaseMap().get(databaseType);
        String userName = ((JTextField) configFrom.getComponent("userNameText", (Class) JTextField.class)).getText();
        String password = ((JTextField) configFrom.getComponent("passwordText", (Class) JTextField.class)).getText();
        // String password = ((JPasswordField) configFrom.getComponent("passwordText", (Class)
        // JTextField.class)).getText();
        String dbName = ((JTextField) configFrom.getComponent("dbNameText", (Class) JTextField.class)).getText();
        String address = ((JTextField) configFrom.getComponent("addressText", (Class) JTextField.class)).getText();
        String port = ((JTextField) configFrom.getComponent("portText", (Class) JTextField.class)).getText();
        DriverInfo driverInfo = DataSourceUtil.getDriverInfo(driverInfos, driverClassName);
        
        StringBuilder sb = new StringBuilder();
        sb.append("***************************** <br> ");
        sb.append("userName：").append(userName).append("<br>");
        sb.append("password：").append(password).append("<br>");
        sb.append("dbName：").append(dbName).append("<br>");
        sb.append("address：").append(address).append("<br>");
        sb.append("port：").append(port).append("<br>");
        sb.append("driverInfo：").append(driverInfo).append("<br>");
        sb.append("homePath：").append(homePath).append("<br>");
        sb.append("databaseType：").append(databaseType).append("<br>");
        // "示例：yonbip_2023/password@127.0.0.1:1521/orcl"
        sb.append("信息识别：").append(userName).append("/").append(password);
        sb.append("@").append(address).append(":").append(port).append("/").append(dbName).append("<br>");
        sb.append("***************************** <br> ");
        
        MyNotifier.notifyInfo(configFrom.getEvn().getProject(), "確定数据源信息如下👇🏻...<br>" + sb);
        
    }
}
