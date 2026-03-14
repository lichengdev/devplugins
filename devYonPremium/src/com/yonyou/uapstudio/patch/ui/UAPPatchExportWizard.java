package com.yonyou.uapstudio.patch.ui;

import com.yonyou.uapstudio.patch.model.UAPPatchResource;
import java.util.Set;
import com.yonyou.uapstudio.patch.model.PatchInfo;
import java.util.HashSet;
import com.yonyou.uapstudio.patch.handler.UAPStandardContentHandler;
import com.yonyou.uapstudio.patch.PatchPlugin;
import com.yonyou.uapstudio.patch.handler.IZipEntryHandler;
import java.io.IOException;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import com.yonyou.uapstudio.patch.handler.factory.HandlerRegistryContainer;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.jface.dialogs.ErrorDialog;
import org.eclipse.jface.dialogs.MessageDialog;
import java.text.MessageFormat;
import org.eclipse.core.runtime.IPath;
import com.yonyou.uapstudio.patch.model.ExportContentInfo;
import org.eclipse.core.runtime.Path;

import java.awt.Desktop;
import java.io.File;
import com.yonyou.uapstudio.patch.model.ExportZipInfo;
import com.yonyou.uapstudio.patch.ui.page.InstallScriptsWizardPage;
import com.yonyou.uapstudio.patch.ui.page.CustomContentWizardPage2;
import com.yonyou.uapstudio.patch.ui.page.ExportMainContentWizardPage;
import com.yonyou.uapstudio.patch.ui.page.PatchExtraInfoWizardPage;
import org.eclipse.jface.wizard.IWizardPage;
import com.yonyou.uapstudio.patch.ui.page.PatchBasicInfoWizardPage;
import org.eclipse.ui.IWorkbench;
import java.util.HashMap;
import org.eclipse.jface.viewers.IStructuredSelection;
import java.util.Map;
import org.eclipse.ui.IExportWizard;
import org.eclipse.jface.wizard.Wizard;

public class UAPPatchExportWizard extends Wizard implements IExportWizard
{
    private Map<String, Object> context;
    private IStructuredSelection fSelection;
    private boolean isExportV5Patch;
    
    public UAPPatchExportWizard()
    {
        this.context = new HashMap<String, Object>();
        this.isExportV5Patch = true;
    }
    
    public void init(final IWorkbench workbench, final IStructuredSelection selection)
    {
        this.fSelection = selection;
    }
    
    public void addPages()
    {
        this.addPage((IWizardPage) new PatchBasicInfoWizardPage(this.context, "1"));
        this.addPage((IWizardPage) new PatchExtraInfoWizardPage(this.context, "2"));
        this.addPage((IWizardPage) new ExportMainContentWizardPage(this.context, "3", this.fSelection));
        this.addPage((IWizardPage) new CustomContentWizardPage2(this.context, "4"));
        this.addPage((IWizardPage) new InstallScriptsWizardPage(this.context, "5"));
    }
    
    public boolean performFinish()
    {
        final IWizardPage[] arrayOfIWizardPage;
        final int i = (arrayOfIWizardPage = this.getPages()).length;
        for (byte b = 0; b < i; ++b)
        {
            final IWizardPage page = arrayOfIWizardPage[b];
            if (page instanceof IUAPPatchExportWizardPage)
            {
                final IUAPPatchExportWizardPage p = (IUAPPatchExportWizardPage) page;
                p.updateModel();
            }
        }
        final ExportZipInfo zipInfo = (ExportZipInfo) this.context.get("com.yonyou.uapstudio.patch.zipinfo");
        final File file = new File(zipInfo.getZipPath());
        final Path path = new Path(zipInfo.getZipPath());
        final Object object = this.context.get("com.yonyou.uapstudio.patch.content");
        if (object != null && object instanceof ExportContentInfo)
        {
            final ExportContentInfo info = (ExportContentInfo) object;
            if (info.getScriptRootFolder() != null && info.getScriptRootFolder().trim().length() > 0)
            {
                final Path path2 = new Path(info.getScriptRootFolder());
                if (path2.isPrefixOf((IPath) path))
                {
                    final String msg = MessageFormat.format("补丁路径{0}被指定到导出的脚本目录{1}，请返回修改后再执行导出。", path.toString(), path2.toString());
                    MessageDialog.openError(this.getShell(), "错误", msg);
                    return false;
                }
            }
        }
        if (file.exists()
            && !MessageDialog.openConfirm(this.getShell(), "确认", MessageFormat.format("文件{0}已存在，是否覆盖？", file.getAbsolutePath())))
        {
            return false;
        }
        try
        {
            this.generateComputedAttribute();
        }
        catch (final CoreException e)
        {
            ErrorDialog.openError(this.getShell(), "错误", "导出补丁失败，请查看详细信息。", e.getStatus());
            return false;
        }
        ZipArchiveOutputStream out = null;
        try
        {
            out = new ZipArchiveOutputStream(file);
            out.setMethod(zipInfo.getMethod());
            final IZipEntryHandler[] arrayOfIZipEntryHandler;
            final int j = (arrayOfIZipEntryHandler = HandlerRegistryContainer.getAllHandlers()).length;
            for (byte b2 = 0; b2 < j; ++b2)
            {
                final IZipEntryHandler handler = arrayOfIZipEntryHandler[b2];
                handler.compress(out, (Map) this.context);
            }
        }
        catch (final Exception e2)
        {
            Status status1 = null;
            IStatus status2 = null;
            if (e2 instanceof CoreException)
            {
                status2 = ((CoreException) e2).getStatus();
            }
            if (status2 == null)
            {
                status1 = new Status(4, "com.yonyou.uapstudio.patch", (String) null, (Throwable) e2);
            }
            ErrorDialog.openError(this.getShell(), "错误", "导出补丁失败，请查看详细信息。", (IStatus) status1);
            return false;
        }
        finally
        {
            try
            {
                if (out != null)
                {
                    out.close();
                }
            }
            catch (final IOException ex)
            {
            }
        }
        try
        {
            if (out != null)
            {
                out.close();
            }
        }
        catch (final IOException ex2)
        {
        }
        boolean export2Jar = this.export2Jar(zipInfo, file.getAbsolutePath());
        
        return export2Jar;
    }
    
    private boolean export2Jar(final ExportZipInfo zipInfo, final String okZip)
    {
        if (this.isExportV5Patch && zipInfo.isExportV5())
        {
            final File jarFile =
                new File(String.valueOf(String.valueOf(zipInfo.getZipPath().substring(0, zipInfo.getZipPath().length() - 4))) + ".jar");
            ZipArchiveOutputStream jarOut = null;
            if (jarFile.exists()
                && !MessageDialog.openConfirm(this.getShell(), "确认", MessageFormat.format("文件{0}已存在，是否覆盖？", jarFile.getAbsolutePath())))
            {
                MessageDialog.openInformation(this.getShell(), "导出成功", MessageFormat.format("补丁导出成功，文件路径为{0}", okZip));
                return false;
            }
            try
            {
                jarOut = new ZipArchiveOutputStream(jarFile);
                jarOut.setMethod(zipInfo.getMethod());
                final IZipEntryHandler[] arrayOfIZipEntryHandler;
                final int i = (arrayOfIZipEntryHandler = HandlerRegistryContainer.getAllV5Handlers()).length;
                for (byte b = 0; b < i; ++b)
                {
                    final IZipEntryHandler handler = arrayOfIZipEntryHandler[b];
                    handler.compress(jarOut, (Map) this.context);
                }
            }
            catch (final Exception e)
            {
                Status status1 = null;
                IStatus status2 = null;
                if (e instanceof CoreException)
                {
                    status2 = ((CoreException) e).getStatus();
                }
                if (status2 == null)
                {
                    status1 = new Status(4, "com.yonyou.uapstudio.patch", (String) null, (Throwable) e);
                }
                ErrorDialog.openError(this.getShell(), "错误", "导出补丁失败，请查看详细信息。", (IStatus) status1);
                PatchPlugin.getDefault().getLog().log((IStatus) status1);
                return false;
            }
            finally
            {
                try
                {
                    if (jarOut != null)
                    {
                        jarOut.close();
                    }
                }
                catch (final IOException ex)
                {
                }
            }
            try
            {
                if (jarOut != null)
                {
                    jarOut.close();
                }
            }
            catch (final IOException ex2)
            {
            }
            MessageDialog.openInformation(this.getShell(), "📌👊👊👊补丁导出成功❤",
                MessageFormat.format("你好程序媛(◕‿◕)，🍮其中zip格式补丁路径为{0}，⚛️jar格式补丁路径为{1}", okZip, jarFile.getAbsolutePath()));
            
        }
        else
            MessageDialog.openInformation(this.getShell(), "📌✔️👊👊👊补丁导出成功❤", MessageFormat.format("你好程序媛(◕‿◕)，⚛️🍮当前补丁文件路径为{0}", okZip));
        
        try
        {
            Desktop.getDesktop().open(new File(okZip).getParentFile());
        }
        catch (IOException e)
        {
            e.printStackTrace();
        }
        
        return true;
    }
    
    private void generateComputedAttribute() throws CoreException
    {
        final UAPStandardContentHandler uAPStandardContentHandler = new UAPStandardContentHandler();
        final Set<String> modifiedClasses = new HashSet<String>();
        final Set<String> modifiedModules = new HashSet<String>();
        for (final UAPPatchResource content : uAPStandardContentHandler.compress((ZipArchiveOutputStream) null, (Map) this.context))
        {
            final IPath path = content.getZipPath();
            if (path.segmentCount() > 2 && path.segment(1).equals("modules"))
            {
                modifiedModules.add(path.segment(2));
                if (content.getZipPath().lastSegment().endsWith(".class"))
                {
                    IPath classPath = null;
                    if (path.segment(3).equals("classes"))
                    {
                        classPath = path.removeFirstSegments(4);
                    }
                    else if (path.segmentCount() > 5 && path.segment(3).equals("META-INF") && path.segment(4).equals("classes"))
                    {
                        classPath = path.removeFirstSegments(5);
                    }
                    else if (path.segmentCount() > 5 && path.segment(3).equals("client") && path.segment(4).equals("classes"))
                    {
                        classPath = path.removeFirstSegments(5);
                    }
                    if (classPath != null)
                    {
                        final StringBuilder sb = new StringBuilder();
                        for (int j = 0; j < classPath.segmentCount() - 1; ++j)
                        {
                            sb.append(classPath.segment(j));
                            sb.append(".");
                        }
                        final String filename = classPath.lastSegment();
                        sb.append(filename.substring(0, filename.length() - 6));
                        modifiedClasses.add(sb.toString());
                    }
                }
            }
        }
        final Object object = this.context.get("com.yonyou.uapstudio.patch.patchinfo");
        if (object != null && object instanceof PatchInfo)
        {
            final PatchInfo info = (PatchInfo) object;
            info.getBasicData().setModifiedJavaClasses(this.join(modifiedClasses.toArray(new String[0]), ","));
        }
    }
    
    private String join(final String[] str, final String sep)
    {
        final StringBuilder sb = new StringBuilder();
        for (int i = 0; i < str.length; ++i)
        {
            sb.append(str[i]);
            if (i != str.length - 1)
            {
                sb.append(sep);
            }
        }
        return sb.toString();
    }
}
