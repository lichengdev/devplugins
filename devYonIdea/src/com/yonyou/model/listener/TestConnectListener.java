package com.yonyou.model.listener;

import java.awt.event.ActionEvent;

import javax.swing.JComboBox;
import javax.swing.JTextField;

import com.intellij.openapi.ui.Messages;
import com.yonyou.model.form.NCHomeConfigForm;
import com.yonyou.model.utils.DataSourceUtil;
import com.yonyou.util.MyNotifier;
import com.yonyou.util.connection.ConnectionManager;
import com.yonyou.util.prop.DriverInfo;

public class TestConnectListener extends AbstractInnerActionListener
{
    public TestConnectListener(NCHomeConfigForm configFrom)
    {
        super(configFrom);
    }
    
    public void doAction(ActionEvent actionEvent)
    {
        NCHomeConfigForm configFrom = this.getConfigFrom();
        String databaseType = (String) ((JComboBox) configFrom.getComponent("databaseTypeBox", JComboBox.class)).getSelectedItem();
        String driverClassName = (String) ((JComboBox) configFrom.getComponent("driverClassNameBox", JComboBox.class)).getSelectedItem();
        String homePath = ((JTextField) configFrom.getComponent("homePathText", JTextField.class)).getText();
        DriverInfo[] driverInfos = (DriverInfo[]) configFrom.getDriverForDatabaseMap().get(databaseType);
        String userName = ((JTextField) configFrom.getComponent("userNameText", JTextField.class)).getText();
        // String password = ((JPasswordField) configFrom.getComponent("passwordText",
        // JPasswordField.class)).getText();
        String password = ((JTextField) configFrom.getComponent("passwordText", (Class) JTextField.class)).getText();
        String dbName = ((JTextField) configFrom.getComponent("dbNameText", JTextField.class)).getText();
        String address = ((JTextField) configFrom.getComponent("addressText", JTextField.class)).getText();
        String port = ((JTextField) configFrom.getComponent("portText", JTextField.class)).getText();
        DriverInfo driverInfo = DataSourceUtil.getDriverInfo(driverInfos, driverClassName);
        
        try
        {
            boolean flag = ConnectionManager.testConnection(userName, password, dbName, address, port, driverInfo, homePath, databaseType);
            if (flag)
            {
                Messages.showInfoMessage("连接成功❤️👍", "⚠️成功✔️☑️");
            }
            else
            {
                Messages.showErrorDialog("连接失败❌", "⚠️错误❌❎");
            }
        }
        catch (Exception e)
        {
            Messages.showErrorDialog(e.getMessage(), "⚠️错误❌❎");
        }
        
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
        
        MyNotifier.notifyInfo(configFrom.getEvn().getProject(), "👊👊👊本城帮你查看到数据源信息如下👇🏻...<br>" + sb);

    }
}
