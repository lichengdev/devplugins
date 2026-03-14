package nc.uap.mde.tool;

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

import org.eclipse.core.commands.AbstractHandler;
import org.eclipse.core.commands.ExecutionEvent;
import org.eclipse.core.commands.ExecutionException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.FileFieldEditor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.handlers.HandlerUtil;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import nc.uap.mde.tool.console.MDEConsoleFactory;
import nc.uap.plugin.studio.classloader.util.UAPEnvHelper;
import nc.vo.framework.rsa.Encode;

public class ChangePassword extends AbstractHandler
{
    static FileFieldEditor filefield;
    private ClassLoader nclassloader;
    String homepathStr = "";
    
    public Object execute(final ExecutionEvent event) throws ExecutionException
    {
        MDEConsoleFactory.console("本城正在帮你修改[root/super]账号密码...");
        Display.getDefault().syncExec(new Runnable()
        {
            public void run()
            {
                IPath homepath = UAPEnvHelper.getUAPHomeFolderPath();
                homepathStr = homepath.toOSString();
                if (homepath != null)
                {
                    IWorkbenchWindow workbenchwindow = null;
                    try
                    {
                        workbenchwindow = HandlerUtil.getActiveWorkbenchWindowChecked(event);
                        ChangePassword.this.changePassword(homepathStr, workbenchwindow);
                    }
                    catch (Exception e)
                    {
                        MessageDialog.openError(workbenchwindow.getShell(), "出错了", e.getMessage());
                    }
                    finally
                    {
                        ChangePassword.this.nclassloader = null;
                    }
                }
                
            }
        });
        MDEConsoleFactory.console("📌🌈✄—————————(◕‿◕✿)💞ꦿ 完美分割线❤split line ❀ ———————————————\r\n");
        return null;
    }
    
    public void changePassword(String homepath, IWorkbenchWindow workbenchwindow) throws Exception
    {
        if (!homepath.endsWith(File.separator)) homepath += File.separator;
        File superadminpath = new File(homepath + ("ierp" + File.separator + "sf" + File.separator + "superadmin.xml"));
        if (superadminpath.exists())
        {
            boolean flg = MessageDialog.openQuestion(workbenchwindow.getShell(), "重置root密码",
                "以下文件将被修改，修改后root用户的密码将被置空。\n" + superadminpath.toURI().getPath() + "\n是否继续操作 ?(如果你不放心，请将该文件备份。)");
            if (flg)
            {
                homepath =
                    (homepath + ("ierp" + File.separator + "sf" + File.separator + "superadmin_" + System.currentTimeMillis() + ".xml"));
                File targetFile = new File(homepath);
                FileTool.copyFile(superadminpath, targetFile);
                this.changePwforNC6(workbenchwindow, superadminpath);
                // MDEConsoleFactory.console("root用户和super用户的密码已被成功修改，重启服务将生效。备份文件：" + homepath);
            }
        }
        else
        {
            File accountpath = new File(homepath + ("ierp" + File.separator + "bin" + File.separator + "account.xml"));
            if (accountpath.exists())
            {
                boolean flg = MessageDialog.openQuestion(workbenchwindow.getShell(), "重置root密码",
                    "以下文件将被修改，修改后root用户的密码将被置空。\n" + accountpath.toURI().getPath() + "\n是否继续操作 ?(如果你不放心，请将该文件备份。)");
                if (flg)
                {
                    homepath =
                        (homepath + ("ierp" + File.separator + "bin" + File.separator + "account_" + System.currentTimeMillis() + ".xml"));
                    File targetFile = new File(homepath);
                    FileTool.copyFile(accountpath, targetFile);
                    this.changePwforNC5(workbenchwindow, accountpath);
                    // MDEConsoleFactory.console("root用户和super用户的密码已被成功修改，重启服务将生效。备份文件：" + homepath);
                }
            }
            else
                MessageDialog.openInformation(workbenchwindow.getShell(), "提示", "该nchome不适用此操作。");
        }
        
    }
    
    private void changePwforNC5(IWorkbenchWindow workbenchwindow, File accountpath) throws Exception
    {
        File accfile = accountpath;
        boolean isencryp = this.isEncrypAccountFile(accfile);
        if (isencryp)
        {
            try
            {
                ClassLoader classloader = this.getNCClassLoader(homepathStr);
                Class<?> CodeFileInOut = classloader.loadClass("nc.bs.sm.config.CodeFileInOut");
                Method readObjFromFile = CodeFileInOut.getMethod("readObjFromFile", String.class);
                Object configparameter = readObjFromFile.invoke((Object) null, accountpath);
                this.setPassword(classloader, configparameter);
                Method wirteToFile = CodeFileInOut.getMethod("wirteToFile", String.class, Object.class);
                wirteToFile.invoke((Object) null, accountpath, configparameter);
            }
            catch (Exception e1)
            {
                e1.printStackTrace();
            }
        }
        else
        {
            ClassLoader classloader = this.getNCClassLoader(homepathStr);
            Class<?> xml2object_class = classloader.loadClass("nc.bs.sm.config.XMLToJavaObject");
            Object xml2object = xml2object_class.newInstance();
            Method convertNodeToObject = xml2object_class.getDeclaredMethod("convertNodeToObject", Node.class);
            convertNodeToObject.setAccessible(true);
            DocumentBuilder docBuilder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            Document document = docBuilder.parse(accountpath);
            Element root = document.getDocumentElement();
            Object configparameter = convertNodeToObject.invoke(xml2object, root);
            this.setPassword(classloader, configparameter);
            Class<?> CodeFileInOut = classloader.loadClass("nc.bs.sm.config.CodeFileInOut");
            Method wirteToFile = CodeFileInOut.getMethod("wirteToFile", String.class, Object.class);
            wirteToFile.invoke((Object) null, accountpath, configparameter);
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
    
    private void changePwforNC6(IWorkbenchWindow workbenchwindow, File superadminfile) throws Exception
    {
        FileWriter w = null;
        try
        {
            w = new FileWriter(superadminfile);
            Encode encode = new Encode();
            w.write(encode.encode(this.getXML()));
            w.flush();
            MessageDialog.openConfirm(workbenchwindow.getShell(), "修改完成", "root用户和super用户的密码已被成功修改，重启服务将生效。");
            // MDEConsoleFactory.console("root用户和super用户的密码已被成功修改，重启服务将生效。备份文件：" + homepath);
            
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
    
    String getXML()
    {
        String xml =
            "<?xml version=\"1.0\" encoding=\"gb2312\"?>\r\n<admins>\t\r\n<admin>\t\t\r\n<code>root</code>\t\t\r\n<name>root</name>\t\t\r\n<password>U_U++--V93f182df06984208361b9689a94416ec</password>\r\n<oldpwd1></oldpwd1>\t\t\r\n<oldpwd2></oldpwd2>\t\t\r\n<oldpwd3></oldpwd3>\t\t\r\n<pwdinuse>2019-01-01</pwdinuse>\t\t\r\n<pwdlvl>update</pwdlvl>\t\t\r\n<identify>staticpwd</identify>\t\t\r\n<isLocked>N</isLocked>\t\r\n</admin>\t\r\n<admin>\t\t\r\n<code>super</code>\t\t\r\n<name>super</name>\t\t\r\n<password>U_U++--V93f182df06984208361b9689a94416ec</password>\r\n<oldpwd1></oldpwd1>\t\t\r\n<oldpwd2></oldpwd2>\t\t\r\n<oldpwd3></oldpwd3>\t\t\r\n<pwdinuse>2019-01-01</pwdinuse>\t\t\r\n<pwdlvl>update</pwdlvl>\t\t\r\n<identify>staticpwd</identify>\t\t\r\n<isLocked>N</isLocked>\t\r\n</admin>\r\n</admins>";
        return xml;
    }
}
