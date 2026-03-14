package com.yonyou.uapstudio.patch.handler;

import com.yonyou.uapstudio.patch.handler.util.Helper;
import com.yonyou.uapstudio.patch.handler.util.UAPPackagePropertyStore;
import com.yonyou.uapstudio.patch.model.ExportContentInfo;
import com.yonyou.uapstudio.patch.model.UAPPatchResource;
import com.yonyou.uapstudio.patch.script.CopyInstruction;
import com.yonyou.uapstudio.patch.script.Instruction;
import com.yonyou.uapstudio.patch.util.ExceptionUtil;
import com.yonyou.uapstudio.patch.util.JavaElementExportUtil;
import com.yonyou.uapstudio.patch.util.ZipUtil;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.apache.commons.compress.archivers.zip.ZipArchiveOutputStream;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IPath;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.Path;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IJavaProject;
import org.eclipse.jdt.core.IPackageFragment;
import org.eclipse.jdt.core.IPackageFragmentRoot;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.internal.corext.util.JavaModelUtil;
import org.eclipse.jdt.internal.corext.util.Messages;

public class UAPStandardContentHandler extends AbstractListAllHandler
{
    protected HashMap<IProject, UAPPackagePropertyStore> hmPropStore = new HashMap();
    
    public UAPPatchResource[] compress(ZipArchiveOutputStream stream, Map<String, Object> context) throws CoreException
    {
        if (context.get("com.yonyou.uapstudio.patch.content") != null
            && context.get("com.yonyou.uapstudio.patch.content") instanceof ExportContentInfo)
        {
            ExportContentInfo info = (ExportContentInfo) context.get("com.yonyou.uapstudio.patch.content");
            Object[] sourceContents = info.getSourceContents();
            if (sourceContents != null && sourceContents.length > 0)
            {
                List<UAPPatchResource> results = new ArrayList();
                
                for (Object obj : sourceContents)
                {
                    UAPPatchResource[] elements = new UAPPatchResource[0];
                    
                    try
                    {
                        elements = this.compressElement(stream, info, obj);
                    }
                    catch (IOException e)
                    {
                        throw ExceptionUtil.getErrorCoreException(e);
                    }
                    
                    Collections.addAll(results, elements);
                }
                
                return (UAPPatchResource[]) results.toArray(new UAPPatchResource[0]);
            }
        }
        
        return new UAPPatchResource[0];
    }
    
    private UAPPatchResource[] compressElement(ZipArchiveOutputStream stream, ExportContentInfo info, Object element)
            throws IOException, CoreException
    {
        List<UAPPatchResource> results = new ArrayList();
        int leadSegmentsToRemove = 1;
        boolean isInJavaProject = false;
        IPackageFragmentRoot pkgRoot = null;
        IResource resource = null;
        IJavaProject jProject = null;
        if (element instanceof IJavaElement)
        {
            isInJavaProject = true;
            IJavaElement je = (IJavaElement) element;
            int type = je.getElementType();
            if (type != 6 && type != 5)
            {
                return new UAPPatchResource[0];
            }
            
            try
            {
                resource = je.getUnderlyingResource();
            }
            catch (JavaModelException var15)
            {
                String msg = Messages.format("未找到编译单元 {0}的文件资源。", je.getElementName());
                throw ExceptionUtil.getErrorCoreException((Exception) null, msg);
            }
            
            jProject = je.getJavaProject();
            pkgRoot = JavaModelUtil.getPackageFragmentRoot(je);
        }
        else
        {
            resource = (IResource) element;
        }
        
        if (!resource.isAccessible())
        {
            String msg = Messages.format("资源不存在或不可访问: {0}", resource.getFullPath());
            throw ExceptionUtil.getErrorCoreException((Exception) null, msg);
        }
        else
        {
            if (resource.getType() == 1)
            {
                if (!isInJavaProject)
                {
                    try
                    {
                        isInJavaProject = resource.getProject().hasNature("org.eclipse.jdt.core.javanature");
                    }
                    catch (CoreException var14)
                    {
                        return new UAPPatchResource[0];
                    }
                    
                    if (isInJavaProject)
                    {
                        jProject = JavaCore.create(resource.getProject());
                        
                        try
                        {
                            IPackageFragment pkgFragment = jProject.findPackageFragment(resource.getFullPath().removeLastSegments(1));
                            if (pkgFragment != null)
                            {
                                pkgRoot = JavaModelUtil.getPackageFragmentRoot(pkgFragment);
                            }
                            else
                            {
                                pkgRoot =
                                    JavaElementExportUtil.findPackageFragmentRoot(jProject, resource.getFullPath().removeLastSegments(1));
                            }
                        }
                        catch (JavaModelException var13)
                        {
                            return new UAPPatchResource[0];
                        }
                    }
                }
                
                if (pkgRoot != null && jProject != null)
                {
                    leadSegmentsToRemove = pkgRoot.getPath().segmentCount();
                }
                
                IPath destinationPath = resource.getFullPath().removeFirstSegments(leadSegmentsToRemove);
                UAPPatchResource[] exportClassFiles = this.exportClassFiles((IProgressMonitor) null, pkgRoot, resource, jProject,
                    destinationPath, info.isContainJavaSource(), info.isAllowErrors(), info.isAllowWarnings(), stream);
                Collections.addAll(results, exportClassFiles);
                UAPPatchResource[] exportResourceFiles =
                    this.exportResource((IProgressMonitor) null, pkgRoot, isInJavaProject, resource, destinationPath, stream);
                Collections.addAll(results, exportResourceFiles);
            }
            
            return (UAPPatchResource[]) results.toArray(new UAPPatchResource[0]);
        }
    }
    
    private UAPPatchResource[] exportClassFiles(IProgressMonitor progressMonitor, IPackageFragmentRoot pkgRoot, IResource javaResource,
            IJavaProject jProject, IPath destinationPath, boolean containsJavaFile, boolean allowErrors, boolean allowWarnings,
            ZipArchiveOutputStream stream) throws IOException, CoreException
    {
        if (JavaElementExportUtil.isJavaFile(javaResource) && pkgRoot != null)
        {
            try
            {
                if (!jProject.isOnClasspath(javaResource))
                {
                    return new UAPPatchResource[0];
                }
                else
                {
                    List<UAPPatchResource> entryPaths = new ArrayList();
                    List<IFile> files = JavaElementExportUtil.filesOnClasspath((IFile) javaResource, destinationPath, jProject, pkgRoot,
                        allowErrors, allowWarnings, progressMonitor);
                    IPath baseDestinationPath = destinationPath.removeLastSegments(1);
                    
                    for (IFile file : files)
                    {
                        IPath classFilePath = baseDestinationPath.append(file.getName());
                        IPath entryString = this.getEntryString((IFile) javaResource, classFilePath);
                        entryPaths.add(new UAPPatchResource(entryString, true, file.getLocation()));
                        if (stream != null)
                        {
                            ZipUtil.writeFile(stream, file.getLocation().toOSString(), entryString.toString());
                        }
                    }
                    
                    if (containsJavaFile)
                    {
                        IPath entryString = this.getEntryString((IFile) javaResource, destinationPath);
                        entryPaths.add(new UAPPatchResource(entryString, true, javaResource.getLocation()));
                        if (stream != null)
                        {
                            ZipUtil.writeFile(stream, javaResource.getLocation().toOSString(), entryString.toString());
                        }
                    }
                    
                    return (UAPPatchResource[]) entryPaths.toArray(new UAPPatchResource[0]);
                }
            }
            catch (CoreException ex)
            {
                throw ex;
            }
        }
        else
        {
            return new UAPPatchResource[0];
        }
    }
    
    private UAPPatchResource[] exportResource(IProgressMonitor progressMonitor, IPackageFragmentRoot pkgRoot, boolean isInJavaProject,
            IResource resource, IPath destinationPath, ZipArchiveOutputStream stream) throws CoreException
    {
        if (!JavaElementExportUtil.isJavaFile(resource) && !JavaElementExportUtil.isClassFile(resource))
        {
            IPath entryString = this.getEntryString((IFile) resource, destinationPath);
            if (stream != null)
            {
                ZipUtil.writeFile(stream, resource.getLocation().toOSString(), entryString.toString());
            }
            
            return new UAPPatchResource[]{new UAPPatchResource(entryString, true, resource.getLocation())};
        }
        else
        {
            return new UAPPatchResource[0];
        }
    }
    
    protected IPath getEntryString(IFile resource, IPath path)
    {
        StringBuilder sb = new StringBuilder();
        String str = path.toString().replace(File.separatorChar, '/');
        IProject project = resource.getProject();
        UAPPackagePropertyStore store = (UAPPackagePropertyStore) this.hmPropStore.get(project);
        if (store == null)
        {
            store = new UAPPackagePropertyStore(project);
            this.hmPropStore.put(project, store);
        }
        
        IPackageFragmentRoot root = null;
        root = store.findPackageFragmentRoot(resource);
        if (root != null)
        {   
            IPath rPath = root.getPath();
            String dest = store.getSourceDestLocation(rPath);
            if (!Helper.isEmptyString(dest))
            {
                dest = dest.replace('\\', '/');
                sb.append(dest);
                if (!dest.endsWith("/"))
                {
                    sb.append("/");
                }
            }
        }
        
        sb.append(str);
        return new Path(sb.toString());
    }
    
    public Instruction[] getSuggestInstructions(Map<String, Object> context) throws CoreException
    {
        List<Instruction> suggests = new ArrayList();
        UAPPatchResource[] listCompressContents = this.listCompressContents(context);
        boolean hasModule = false;
        boolean hasResource = false;
        
        for (UAPPatchResource content : listCompressContents)
        {
            if (hasModule && hasResource)
            {
                break;
            }
            
            if (!hasModule && content.getZipPath().toString().startsWith("replacement/modules/"))
            {
                hasModule = true;
            }
            
            if (!hasResource && content.getZipPath().toString().startsWith("replacement/resources/"))
            {
                hasResource = true;
            }
        }
        
        if (hasModule)
        {
            CopyInstruction copy = new CopyInstruction();
            copy.setFrom("/replacement/modules/");
            copy.setTo("/modules/");
            copy.setRemark("标准代码拷贝脚本");
            copy.setEditable(false);
            suggests.add(copy);
        }
        
        if (hasResource)
        {
            CopyInstruction copy = new CopyInstruction();
            copy.setFrom("/replacement/resources/");
            copy.setTo("/resources/");
            copy.setRemark("标准代码拷贝脚本");
            copy.setEditable(false);
            suggests.add(copy);
        }
        
        return (Instruction[]) suggests.toArray(new Instruction[0]);
    }
}
