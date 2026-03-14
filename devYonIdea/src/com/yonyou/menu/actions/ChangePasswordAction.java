package com.yonyou.menu.actions;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.net.URL;
import java.net.URLClassLoader;
import java.util.ArrayList;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import com.intellij.openapi.actionSystem.AnAction;
import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.ui.Messages;
import com.yonyou.model.service.NCHomeConfigService;
import com.yonyou.model.utils.project.FileTool;
import com.yonyou.model.vo.NCHomeConfigVO;
import com.yonyou.util.MyNotifier;
import com.yonyou.util.StringUtils;

import nc.uap.plugin.studio.ui.preference.rsa.Encode; 

// app-client.jar
/**
 * 重置root密码
 **
 * @qualiFild com.yonyou.menu.actions.ChangePasswordAction.java<br>
 * @author：LiBencheng<br>
 * @date Created on 2025年4月2日<br>
 * @version 1.0<br>
 */
public class ChangePasswordAction extends AnAction
{
    private ClassLoader nclassloader;
    
    public void actionPerformed(AnActionEvent e)
    {
        try
        {
            NCHomeConfigVO configVO = NCHomeConfigService.getInstance().getState();
            String homepath = configVO.getHomePath();
            if (StringUtils.isNotBlank(configVO.getHomePath())) changePassword(homepath,e);
            else
            {
                Messages.showErrorDialog("请先进行Home配置(Alt+H)", "错误：");
            }
        }
        catch (Exception e1)
        {
            e1.printStackTrace();
        }
    }
    
    public void changePassword(String homepath,AnActionEvent e) throws Exception
    {
        if (!homepath.endsWith(File.separator)) homepath += File.separator;
        File superadminpath = new File(homepath + ("ierp" + File.separator + "sf" + File.separator + "superadmin.xml"));
        
        if (superadminpath.exists())
        {

            MyNotifier.notifyInfo(e.getProject(), "本城正在帮你修改[root/super]账号密码..." + superadminpath);
            changePwforNC6(superadminpath, homepath);
        }
        else
        {
            File configpath = new File(homepath + ("ierp" + File.separator + "bin" + File.separator + "account.xml"));
            if (superadminpath.exists())
            {
                MyNotifier.notifyInfo(e.getProject(), "本城正在帮你修改[root/super]账号密码..." + configpath);
                changePwforNC5(configpath, homepath);
            }
            else
                Messages.showErrorDialog("该nchome不适用此操作。", "错误：");
        }
    }
    
    private void changePwforNC5(File accountpath, String homepath) throws Exception
    {
        int showOk = Messages.showOkCancelDialog(
            "以下文件将被修改，修改后root用户的密码将被置空。\n" + accountpath.toURI().getPath() + "\n是否继续操作 ?会自动备份当前文件。(如果你不放心，请将该文件备份。)", "重置root密码", null);
        
        // boolean flg = MessageDialog.openQuestion(workbenchwindow.getShell(), "重置root密码",
        // "以下文件将被修改，修改后root用户的密码将被置空。\n" + accountpath.toOSString() + "\n是否继续操作 ?(如果你不放心，请将该文件备份。)");
        if (showOk == 0)
        {
            File targetFile =
                new File(homepath + ("ierp" + File.separator + "bin" + File.separator + "account_" + System.currentTimeMillis() + ".xml"));
            FileTool.copyFile(accountpath, targetFile);
            File accfile = accountpath;
            InputStream inputStream = null;
            boolean isencryp = this.isEncrypAccountFile(accfile);
            try
            {
                inputStream = new FileInputStream(accfile);
                if (isencryp)
                {
                    try
                    {
                        ClassLoader classloader = this.getNCClassLoader(homepath);
                        Class<?> CodeFileInOut = classloader.loadClass("nc.bs.sm.config.CodeFileInOut");
                        Method readObjFromFile = CodeFileInOut.getMethod("readObjFromFile", String.class);
                        Object configparameter = readObjFromFile.invoke((Object) null, inputStream);
                        this.setPassword(classloader, configparameter);
                        Method wirteToFile = CodeFileInOut.getMethod("wirteToFile", String.class, Object.class);
                        wirteToFile.invoke((Object) null, inputStream, configparameter);
                    }
                    catch (Exception e1)
                    {
                        e1.printStackTrace();
                    }
                }
                else
                {
                    ClassLoader classloader = this.getNCClassLoader(homepath);
                    Class<?> xml2object_class = classloader.loadClass("nc.bs.sm.config.XMLToJavaObject");
                    Object xml2object = xml2object_class.newInstance();
                    Method convertNodeToObject = xml2object_class.getDeclaredMethod("convertNodeToObject", Node.class);
                    convertNodeToObject.setAccessible(true);
                    DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
                    Document document = docBuilder.parse(inputStream);
                    Element root = document.getDocumentElement();
                    Object configparameter = convertNodeToObject.invoke(xml2object, root);
                    this.setPassword(classloader, configparameter);
                    Class<?> CodeFileInOut = classloader.loadClass("nc.bs.sm.config.CodeFileInOut");
                    Method wirteToFile = CodeFileInOut.getMethod("wirteToFile", String.class, Object.class);
                    wirteToFile.invoke((Object) null, inputStream, configparameter);
                }
            }
            catch (Exception e)
            {
                
            }
            finally
            {
                if (inputStream != null) inputStream.close();
            }
        }
        
    }
    
    private void setPassword(ClassLoader classloader, Object configparameter) throws Exception
    {
        Class<?> configparameter_class = configparameter.getClass();
        Field m_arySysAdms = configparameter_class.getField("m_arySysAdms");
        Object[] SysAdms = (Object[]) m_arySysAdms.get(configparameter);
        
        for (Object o : SysAdms)
        {
            Class<?> SysAdm_class = o.getClass();
            Field m_password = SysAdm_class.getField("m_password");
            Field m_sysAdmCode = SysAdm_class.getField("m_sysAdmCode");
            Class<?> rbacpwdutil_class = null;
            
            try
            {
                rbacpwdutil_class = classloader.loadClass("nc.vo.uap.rbac.userpassword.RbacPwdUtil");
            }
            catch (Exception var16)
            {
            }
            
            if (rbacpwdutil_class != null)
            {
                Method getEncodedPwd = rbacpwdutil_class.getMethod("getEncodedPwd", String.class, String.class);
                String encodepw = (String) getEncodedPwd.invoke((Object) null, m_sysAdmCode.get(o), "");
                m_password.set(o, encodepw);
            }
            else
            {
                m_password.set(o, "");
            }
        }
        
    }
    
    private ClassLoader getNCClassLoader(String homepath) throws Exception
    {
        if (this.nclassloader == null)
        {
            List<URL> urls = new ArrayList();
            urls.add(new File(homepath + "external").toURI().toURL());
            urls.add(new File(homepath + "modules").toURI().toURL());
            urls.add(new File(homepath + ("modules" + File.separator + "uap")).toURI().toURL());
            urls.add(new File(homepath + ("modules" + File.separator + "uap" + File.separator + "client")).toURI().toURL());
            urls.add(new File(homepath + ("modules" + File.separator + "uap" + File.separator + "client" + File.separator + "META-INF"))
                .toURI().toURL());
            this.nclassloader = new URLClassLoader((URL[]) urls.toArray(new URL[0]));
        }
        
        return this.nclassloader;
    }
    
    private boolean isEncrypAccountFile(File accfile) throws Exception
    {
        InputStream in = null;
        byte[] bytes = new byte[9];
        boolean isencrypfile = true;
        
        try
        {
            in = new FileInputStream(accfile);
            in.read(bytes);
            
            for (int i = 1; i < bytes.length; ++i)
            {
                if (i != bytes[i - 1])
                {
                    isencrypfile = false;
                    break;
                }
            }
        }
        catch (Exception e)
        {
            throw new Exception("读取文件时出错。" + e.getMessage());
        }
        finally
        {
            if (in != null)
            {
                try
                {
                    in.close();
                }
                catch (IOException var11)
                {
                }
            }
        }
        
        return isencrypfile;
    }
    
    private void changePwforNC6(File superadminpath, String homepath) throws Exception
    {
        int showOk = Messages.showOkCancelDialog(
            "以下文件将被修改，修改后root用户的密码将被置空。\n" + superadminpath.toURI().getPath() + "\n是否继续操作 ? 会自动备份当前文件。(如果你不放心，请将该文件备份。)", "重置root密码", null);
        
        // boolean flg = MessageDialog.openQuestion(workbenchwindow.getShell(), "重置root/super密码",
        // "以下文件将被修改，修改后root/super用户的密码将被置空。\n" + superadminpath.toOSString() + "\n是否继续操作
        // ?(如果你不放心，请将该文件备份。)");
        if (showOk == 0)
        {
            File targetFile = new File(
                homepath + ("ierp" + File.separator + "sf" + File.separator + "superadmin_" + System.currentTimeMillis() + ".xml"));
            FileTool.copyFile(superadminpath, targetFile);
            File superadminfile = superadminpath;
            FileWriter w = null;
            try
            {
                w = new FileWriter(superadminfile);
                Encode encode = new Encode();
                w.write(encode.encode(this.getXML()));
                w.flush();
                Messages.showInfoMessage("root用户和super用户的密码已被成功修改，重启服务将生效。", "修改成功");
            }
            catch (Exception e)
            {
                throw e;
            }
            finally
            {
                if (w != null)
                {
                    try
                    {
                        w.close();
                    }
                    catch (IOException var12)
                    {
                    }
                }
                
            }
        }
    }
    
    String getXML()
    {
        return "<?xml version=\"1.0\" encoding=\"gb2312\"?>\r\n<admins>\t\r\n<admin>\t\t\r\n<code>root</code>\t\t\r\n<name>root</name>\t\t\r\n<password>U_U++--V93f182df06984208361b9689a94416ec</password>\r\n<oldpwd1></oldpwd1>\t\t\r\n<oldpwd2></oldpwd2>\t\t\r\n<oldpwd3></oldpwd3>\t\t\r\n<pwdinuse>2019-01-01</pwdinuse>\t\t\r\n<pwdlvl>update</pwdlvl>\t\t\r\n<identify>staticpwd</identify>\t\t\r\n<isLocked>N</isLocked>\t\r\n</admin>\t\r\n<admin>\t\t\r\n<code>super</code>\t\t\r\n<name>super</name>\t\t\r\n<password>U_U++--V93f182df06984208361b9689a94416ec</password>\r\n<oldpwd1></oldpwd1>\t\t\r\n<oldpwd2></oldpwd2>\t\t\r\n<oldpwd3></oldpwd3>\t\t\r\n<pwdinuse>2019-01-01</pwdinuse>\t\t\r\n<pwdlvl>update</pwdlvl>\t\t\r\n<identify>staticpwd</identify>\t\t\r\n<isLocked>N</isLocked>\t\r\n</admin>\r\n</admins>";
    }
}
