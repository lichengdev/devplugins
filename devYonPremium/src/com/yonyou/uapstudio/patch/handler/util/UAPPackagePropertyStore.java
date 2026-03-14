package com.yonyou.uapstudio.patch.handler.util;

import com.yonyou.uapstudio.patch.PatchPlugin;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.List;
import java.util.Properties;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.Status;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;

public class UAPPackagePropertyStore
{
    private IProject project = null;
    private HashMap<String, String> sourceDestLocalHM = null;
    private String moduleName = null;
    private List<IPackageFragmentRoot> pfrRootsList = null;
    
    public UAPPackagePropertyStore(IProject project)
    {
        this.project = project;
    }
    
    protected HashMap<String, String> getSourceDestLocationHM()
    {
        if (this.sourceDestLocalHM == null)
        {
            this.sourceDestLocalHM = new HashMap<String, String>();
        }
        
        return this.sourceDestLocalHM;
    }
    
    public IPackageFragmentRoot findPackageFragmentRoot(IFile file)
    {
        if (this.pfrRootsList == null)
        {
            IJavaProject javaPro = JavaCore.create(this.project);
            this.pfrRootsList = Helper.getProjectAllSourceRoot(javaPro);
        }
        
        IPackageFragmentRoot root = null;
        String filePathString = file.getFullPath().makeAbsolute().toString();
        
        for (int i = 0; i < this.pfrRootsList.size(); ++i)
        {
            IPackageFragmentRoot temp = (IPackageFragmentRoot) this.pfrRootsList.get(i);
            String rootPathString = temp.getPath().makeAbsolute().toString();
            if (filePathString.startsWith(rootPathString))
            {
                if (root != null)
                {
                    String str = root.getPath().makeAbsolute().toString();
                    if (!rootPathString.startsWith(str))
                    {
                        continue;
                    }
                }
                
                root = temp;
            }
        }
        
        return root;
    }
    
    public String getSourceDestLocation(IPath sourcePath)
    {
        String source = sourcePath.toString();
        String dest = (String) this.getSourceDestLocationHM().get(source);
        if (dest == null)
        {
            StringBuilder sb = new StringBuilder();
            if (source.toLowerCase().indexOf("private") != -1)
            {
                sb.append("replacement/modules/").append(this.getModuleName().toLowerCase()).append("/");
                sb.append("META-INF/classes/");
            }
            else if (source.toLowerCase().indexOf("client") != -1)
            {
                sb.append("replacement/modules/").append(this.getModuleName().toLowerCase()).append("/");
                sb.append("client/classes/");
            }
            else if (source.toLowerCase().indexOf("public") != -1)
            {
                sb.append("replacement/modules/").append(this.getModuleName().toLowerCase()).append("/");
                sb.append("classes/");
            }
            else if (source.toLowerCase().indexOf("resources") != -1)
            {
                sb.append("replacement/resources/");
            }
            else
            {
                sb.append("");
            }
            
            dest = sb.toString();
            this.getSourceDestLocationHM().put(source, dest);
        }
        
        return dest;
    }
    
    protected String getModuleName()
    {
        if (this.moduleName == null)
        {
            IFile file = this.project.getFile(".module_prj");
            if (file.exists())
            {
                try
                {
                    InputStream in = file.getContents();
                    Properties prop = new Properties();
                    prop.load(in);
                    this.moduleName = prop.getProperty("module.name");
                }
                catch (Exception e)
                {
                    PatchPlugin.getDefault().getLog().log(new Status(4, "com.yonyou.uapstudio.patch", e.getMessage(), e));
                }
            }
            else
            {
                IFile file2 = this.project.getFile("META-INF/module.xml");
                if (file2.exists())
                {
                    InputStream contents = null;
                    StringBuilder fileContent = new StringBuilder();
                    
                    try
                    {
                        contents = file2.getContents();
                        BufferedReader reader = new BufferedReader(new InputStreamReader(contents));
                        
                        while (true)
                        {
                            String line = reader.readLine();
                            if (line == null)
                            {
                                break;
                            }
                            
                            fileContent.append(line.trim());
                        }
                    }
                    catch (CoreException e)
                    {
                        e.printStackTrace();
                    }
                    catch (IOException e)
                    {
                        e.printStackTrace();
                    }
                    finally
                    {
                        if (contents != null)
                        {
                            try
                            {
                                contents.close();
                            }
                            catch (Exception var17)
                            {
                            }
                        }
                        
                    }
                    
                    if (fileContent.length() > 0)
                    {
                        String content = fileContent.toString();
                        Pattern pattern = Pattern.compile("\\<\\s*module\\s*name=\"(\\S+)\"\\s*\\>");
                        Matcher matcher = pattern.matcher(content);
                        if (matcher.find())
                        {
                            String group = matcher.group(1);
                            this.moduleName = group;
                        }
                    }
                }
            }
        }
        
        if (this.moduleName == null)
        {
            this.moduleName = "testbill";
            PatchPlugin.getDefault().getLog()
                .log(new Status(4, "com.yonyou.uapstudio.patch", "项目" + this.project.getName() + "未找到所属模块配置，默认使用testbill替代，请手工修改导出的补丁。"));
        }
        
        return this.moduleName;
    }
}
