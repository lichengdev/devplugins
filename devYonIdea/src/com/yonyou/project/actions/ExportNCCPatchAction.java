package com.yonyou.project.actions;

import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import org.jetbrains.annotations.NotNull;

import com.intellij.openapi.actionSystem.AnActionEvent;
import com.intellij.openapi.actionSystem.CommonDataKeys;
import com.intellij.openapi.application.ApplicationManager;
import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.module.ModuleType;
import com.intellij.openapi.module.ModuleTypeManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.newvfs.impl.FsRoot;
import com.yonyou.model.service.NCHomeConfigService;
import com.yonyou.model.vo.NCHomeConfigVO;
import com.yonyou.module.NCCModuleType;
import com.yonyou.patch.form.ExportNCCPatchConfigForm;
import com.yonyou.util.StringUtils;

public class ExportNCCPatchAction extends AbstractProjectAnAction
{
    
    @Override
    public void doAction(@NotNull AnActionEvent e)
    {
        if (e == null)
        {
            ExportNCCPatchAction.$$$reportNull$$$0(0);
        }
        ApplicationManager.getApplication().invokeLater(() -> {
            
            try
            {
                ExportNCCPatchConfigForm form = new ExportNCCPatchConfigForm(e);
                form.setTitle("👉🏻导出补丁包(◕‿◕🌸) 🎹🎼♩♩♪ ♬♩♪ ♫ ♪");
                form.setBounds(0, 0, 650, 600);
                form.setLocationRelativeTo(null);
                form.setVisible(true);
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        });
    }
    
    @Override
    public void update(@NotNull AnActionEvent e)
    {
        int i;
        if (e == null)
        {
            ExportNCCPatchAction.$$$reportNull$$$0(1);
        }
        super.update(e);
        if (!e.getPresentation().isEnabledAndVisible())
        {
            return;
        }
        NCHomeConfigVO state = NCHomeConfigService.getInstance().getState();
        if (state == null || StringUtils.isBlank(state.getHomePath()))
        {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        VirtualFile[] files = (VirtualFile[]) e.getData(CommonDataKeys.VIRTUAL_FILE_ARRAY);
        Module[] modules = ModuleManager.getInstance((Project) e.getProject()).getModules();
        if (modules == null || files == null)
        {
            e.getPresentation().setEnabledAndVisible(false);
            return;
        }
        ModuleType javaModule = ModuleTypeManager.getInstance().findByID("JAVA_MODULE");
        String moduleName = javaModule.getName();
        List<Module> moduleList = (List<Module>) Arrays.<Module> asList(modules).stream().filter(
            module -> (NCCModuleType.getModuleName().equals(module.getModuleTypeName()) || moduleName.equals(module.getModuleTypeName())))
            .collect(Collectors.toList());
        List<String> modulenameList = (List<String>) moduleList.stream().map(Module::getName).collect(Collectors.toList());
        boolean flag = true;
        ProjectFileIndex projectFileIndex = ProjectFileIndex.getInstance((Project) e.getProject());
        for (i = 0; i < files.length; ++i)
        {
            if (!flag) continue;
            flag = modulenameList.contains(projectFileIndex.getModuleForFile(files[i]).getName());
        }
        if (flag)
        {
            for (i = 0; i < files.length; ++i)
            {
                VirtualFile file = files[i];
                boolean bl = flag = file != null && !(file instanceof FsRoot);
                if (!flag) break;
            }
        }
        e.getPresentation().setEnabledAndVisible(flag);
    }
    
    private static void $$$reportNull$$$0(int n)
    {
        Object[] objectArray;
        Object[] objectArray2 = new Object[3];
        objectArray2[0] = "e";
        objectArray2[1] = "com/yonyou/project/actions/ExportNCCPatchAction";
        switch (n)
        {
            default :
            {
                objectArray = objectArray2;
                objectArray2[2] = "doAction";
                break;
            }
            case 1 :
            {
                objectArray = objectArray2;
                objectArray2[2] = "update";
                break;
            }
        }
        throw new IllegalArgumentException(String.format("Argument for @NotNull parameter '%s' of %s.%s must not be null", objectArray));
    }
}
