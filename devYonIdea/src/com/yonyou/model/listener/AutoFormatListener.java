package com.yonyou.model.listener;

import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.swing.JTextField;

import com.yonyou.model.form.NCHomeConfigForm;

public class AutoFormatListener implements KeyListener
{
    private NCHomeConfigForm configFrom;
    
    public AutoFormatListener(NCHomeConfigForm configFrom)
    {
        this.configFrom = configFrom;
    }
    
    public void keyTyped(KeyEvent keyEvent)
    {
    }
    
    public void keyPressed(KeyEvent keyEvent)
    {
    }
    
    public void keyReleased(KeyEvent keyEvent)
    {
        JTextField autoTextField = (JTextField) this.configFrom.getComponent("autoTextField", JTextField.class);
        String text = autoTextField.getText();
        String pattern =
            "(?<usercode>\\w{1,})(\\/)(?<pwd>\\S{1,})(\\@)(?<ip>((\\d{1,3}\\.){3}\\d{1,3})|(localhost)):(?<port>[0-9]{1,})(\\/)(?<odbc>\\w{1,})";
        Pattern r = Pattern.compile(pattern);
        Matcher m = r.matcher(text);
        if (m.matches())
        {
            String usercode = m.group("usercode");
            System.out.println("usercode: " + usercode);
            String pwd = m.group("pwd");
            System.out.println("pwd: " + pwd);
            String ip = m.group("ip");
            System.out.println("ip: " + ip);
            String port = m.group("port");
            System.out.println("port: " + port);
            String odbc = m.group("odbc");
            System.out.println("odbc: " + odbc);
            ((JTextField) this.configFrom.getComponent("addressText", JTextField.class)).setText(ip);
            ((JTextField) this.configFrom.getComponent("portText", JTextField.class)).setText(port);
            ((JTextField) this.configFrom.getComponent("dbNameText", JTextField.class)).setText(odbc);
            ((JTextField) this.configFrom.getComponent("userNameText", JTextField.class)).setText(usercode);
           // ((JPasswordField) this.configFrom.getComponent("passwordText", JPasswordField.class)).setText(pwd);
            ((JTextField) this.configFrom.getComponent("passwText", JTextField.class)).setText(pwd); 
        }
        
    }
}
