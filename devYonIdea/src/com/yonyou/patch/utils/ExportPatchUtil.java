package com.yonyou.patch.utils;

import com.intellij.openapi.module.Module;
import com.intellij.openapi.module.ModuleManager;
import com.intellij.openapi.project.Project;
import com.intellij.openapi.roots.CompilerModuleExtension;
import com.intellij.openapi.roots.ModuleRootManager;
import com.intellij.openapi.roots.ProjectFileIndex;
import com.intellij.openapi.ui.Messages;
import com.intellij.openapi.vfs.VfsUtilCore;
import com.intellij.openapi.vfs.VirtualFile;
import com.intellij.openapi.vfs.VirtualFileManager;
import com.yonyou.model.service.NCHomeConfigService;
import com.yonyou.model.utils.project.ZipUtil;
import com.yonyou.model.vo.NCHomeConfigVO;
import com.yonyou.patch.utils.CompatibleFileUtil;
import com.yonyou.patch.utils.TempleteUtil;
import com.yonyou.patch.vo.ExportFileVO;
import com.yonyou.patch.vo.PatchInfoVO;
import com.yonyou.util.StringUtils;
import com.yonyou.util.xml.XMLToObject;
import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.stream.Collectors;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;

public class ExportPatchUtil
{
    public static void exportPatchFile(PatchInfoVO patchInfoVO, Project project) throws Exception
    {
        File temp = new File(patchInfoVO.getExportPath() + File.separator + patchInfoVO.getPatchName() + File.separator + "replacement");
        if (!temp.exists())
        {
            temp.mkdirs();
        }
        Module[] modules = ModuleManager.getInstance((Project) project).getModules();
        HashMap<String, Module> moduleMap = new HashMap<String, Module>();
        HashMap<String, String> modulePathMap = new HashMap<String, String>();
        ArrayList<String> modulePathList = new ArrayList<String>();
        for (int i = 0; i < modules.length; ++i)
        {
            String moduleName;
            Module module = modules[i];
            if (module.getModuleTypeName() == null || module.getModuleFile() == null) continue;
            if (new File(module.getModuleFile().getParent().getPath() + File.separator + "META-INF" + File.separator + "module.xml")
                .exists())
            {
                moduleName = module.getName();
                moduleMap.put(moduleName, module);
                modulePathMap.put(module.getModuleFile().getParent().getPath(), moduleName);
                modulePathList.add(module.getModuleFile().getParent().getPath());
                continue;
            }
            moduleName = module.getName();
            moduleMap.put(moduleName, module);
            modulePathMap.put(module.getModuleFile().getParent().getPath(), moduleName);
            modulePathList.add(module.getModuleFile().getParent().getPath());
        }
        HashMap<String, ArrayList<String>> moduleExportPathMap = new HashMap<String, ArrayList<String>>();
        for (int i = 0; i < patchInfoVO.getSelectedFile().size(); ++i)
        {
            String filepath = patchInfoVO.getSelectedFile().get(i).getFilePath();
            List pathList = modulePathList.stream().filter(mp -> filepath.startsWith(mp + "/") || StringUtils.equals(mp, filepath))
                .collect(Collectors.toList());
            if (pathList == null || pathList.isEmpty()) continue;
            String modulePath = (String) pathList.stream().max(Comparator.comparing(String::length)).get();
            String moduleName = (String) modulePathMap.get(modulePath);
            ArrayList<String> paths = (ArrayList<String>) moduleExportPathMap.get(moduleName);
            if (paths == null)
            {
                paths = new ArrayList<String>();
            }
            paths.add(filepath);
            moduleExportPathMap.put(moduleName, paths);
        }
        if (moduleExportPathMap.isEmpty())
        {
            Messages.showErrorDialog((String) "💔👉🏻没有找到任何需要导出的文件，无法导出补丁❌", (String) "警告💔");
            return;
        }
        
        ArrayList<String> classNameList = new ArrayList<String>();
        NCHomeConfigVO configVO = NCHomeConfigService.getInstance().getState();
        File nccloudFile = new File(configVO.getHomePath() + "/hotwebs/nccloud");
        boolean isNCCHome = nccloudFile.exists();
        for (String moduleName : moduleExportPathMap.keySet())
        {
            Module module = (Module) moduleMap.get(moduleName);
            String moduleXMLName = module.getName();
            boolean isNCCModule = !new File(module.getModuleFile().getParent().getPath() + File.separator + "pom.xml").exists();
            CompilerModuleExtension instance = CompilerModuleExtension.getInstance((Module) module);
            VirtualFile outVF = instance.getCompilerOutputPath();
            if (outVF == null)
            {
                throw new Exception("👉🏻💔请先编译项目再导出补丁❌");
            }
            String outPath = outVF.getPath();
            List pathList = (List) moduleExportPathMap.get(moduleName);
            Map<String, List<File>> listMap = ExportPatchUtil.getFile(pathList);
            List<String> exportUrlList = ExportPatchUtil.getExportUrlList(project);
            for (String key : listMap.keySet())
            {
                Optional<ExportFileVO> op =
                    patchInfoVO.getSelectedFile().stream().filter(vo -> StringUtils.equals(vo.getFilePath(), key)).findFirst();
                if (op.isPresent())
                    moduleXMLName = op.get().getExportModuleName();
                List<File> fileList = listMap.get(key);
                for (File file : fileList)
                {
                    long builderTime;
                    long fileTime;
                    Optional<String> first;
                    VirtualFile fileByUrl;
                    if (file.getName().endsWith(".iml") || file.getAbsolutePath().toLowerCase().contains(".idea")
                        || (fileByUrl = VirtualFileManager.getInstance().findFileByNioPath(file.toPath())) == null
                        || (first = exportUrlList.stream()
                            .filter(url -> fileByUrl.getUrl().startsWith(url + "/") || StringUtils.equals(fileByUrl.getUrl(), url))
                            .findFirst()).isPresent())
                        continue;
                    File outFile = null;
                    String path = file.getAbsolutePath();
                    String[] split = path.split(Matcher.quoteReplacement(File.separator + "src"));
                    if (isNCCModule)
                    {
                        if (path.contains(File.separator + "src" + File.separator + "public"))
                        {
                            split = path.split(Matcher.quoteReplacement(File.separator + "src" + File.separator));
                            String lastpath = split[1].replaceFirst("public", "");
                            outFile = new File(outPath + lastpath);
                            if (lastpath.contains(".java"))
                            {
                                lastpath = ExportPatchUtil.getLastPath(fileByUrl, project);
                                outFile = new File(outPath + lastpath);
                                ExportPatchUtil.exportClassFile(lastpath, outPath, temp.getAbsolutePath() + File.separator + "modules"
                                    + File.separator + moduleXMLName + File.separator + "classes", classNameList);
                            }
                            else
                            {
                                File targetFile = new File(temp.getAbsolutePath() + File.separator + "modules" + File.separator
                                    + moduleXMLName + File.separator + "classes" + lastpath);
                                CompatibleFileUtil.copy(outFile, targetFile);
                            }
                            if (patchInfoVO.isIncludeSrc() && file.getName().contains(".java"))
                            {
                                File targetJavaFile = new File(temp.getAbsolutePath() + File.separator + "modules" + File.separator
                                    + moduleXMLName + File.separator + "classes" + lastpath);
                                CompatibleFileUtil.copy(file, targetJavaFile);
                            }
                        }
                        else if (path.contains(File.separator + "src" + File.separator + "private"))
                        {
                            split = path.split(Matcher.quoteReplacement(File.separator + "src" + File.separator));
                            String lastpath = split[1].replaceFirst("private", "");
                            outFile = new File(outPath + lastpath);
                            if (lastpath.contains(".java"))
                            {
                                lastpath = ExportPatchUtil.getLastPath(fileByUrl, project);
                                outFile = new File(outPath + lastpath);
                                ExportPatchUtil.exportClassFile(lastpath, outPath, temp.getAbsolutePath() + File.separator + "modules"
                                    + File.separator + moduleXMLName + File.separator + "META-INF" + File.separator + "classes",
                                    classNameList);
                            }
                            else
                            {
                                File targetFile = new File(temp.getAbsolutePath() + File.separator + "modules" + File.separator
                                    + moduleXMLName + File.separator + "META-INF" + File.separator + "classes" + lastpath);
                                CompatibleFileUtil.copy(outFile, targetFile);
                            }
                            if (patchInfoVO.isIncludeSrc() && file.getName().contains(".java"))
                            {
                                File targetJavaFile = new File(temp.getAbsolutePath() + File.separator + "modules" + File.separator
                                    + moduleXMLName + File.separator + "META-INF" + File.separator + "classes" + lastpath);
                                CompatibleFileUtil.copy(file, targetJavaFile);
                            }
                        }
                        else if (path.contains(File.separator + "src" + File.separator + "client"))
                        {
                            split = path.split(Matcher.quoteReplacement(File.separator + "src" + File.separator));
                            String lastpath = split[1].replaceFirst("client", "");
                            outFile = new File(outPath + lastpath);
                            boolean autoClient = configVO.isAutoClient();
                            // boolean isNccloud = autoClient ? isNCCHome && lastpath.contains("nccloud" +
                            // File.separator + "web")
                            // : !patchInfoVO.isClient2Modules();
                            boolean isNccloud = autoClient ? isNCCHome : !patchInfoVO.isClient2Modules();
                            if (patchInfoVO.isClient2Modules()) isNccloud = false;
                            boolean client2Ncchr = patchInfoVO.isClient2Ncchr();
                            if (lastpath.contains(File.separator + "yyconfig" + File.separator + "modules"))
                            {
                                File targetFile = new File(temp.getAbsolutePath() + File.separator + "hotwebs" + File.separator + "nccloud"
                                    + File.separator + "WEB-INF" + File.separator + "extend" + lastpath);
                                CompatibleFileUtil.copy(outFile, targetFile);
                            }
                            else
                            {
                                lastpath = ExportPatchUtil.getLastPath(fileByUrl, project);
                                outFile = new File(outPath + lastpath);
                                if (client2Ncchr)
                                {
                                    ExportPatchUtil.exportClassFile(lastpath, outPath, temp.getAbsolutePath() + File.separator + "hotwebs"
                                        + File.separator + "ncchr" + File.separator + "WEB-INF" + File.separator + "classes",
                                        classNameList);
                                }
                                else if (isNccloud)
                                {
                                    ExportPatchUtil.exportClassFile(lastpath, outPath, temp.getAbsolutePath() + File.separator + "hotwebs"
                                        + File.separator + "nccloud" + File.separator + "WEB-INF" + File.separator + "classes",
                                        classNameList);
                                }
                                else
                                {
                                    ExportPatchUtil.exportClassFile(lastpath, outPath, temp.getAbsolutePath() + File.separator + "modules"
                                        + File.separator + moduleXMLName + File.separator + "client" + File.separator + "classes",
                                        classNameList);
                                }
                            }
                            if (patchInfoVO.isIncludeSrc() && file.getName().contains(".java"))
                            {
                                String targetJavaFilePath = null;
                                targetJavaFilePath = client2Ncchr
                                    ? temp.getAbsolutePath() + File.separator + "hotwebs" + File.separator + "ncchr" + File.separator
                                        + "WEB-INF" + File.separator + "classes" + lastpath
                                    : (isNccloud
                                        ? temp.getAbsolutePath() + File.separator + "hotwebs" + File.separator + "nccloud" + File.separator
                                            + "WEB-INF" + File.separator + "classes" + lastpath
                                        : temp.getAbsolutePath() + File.separator + "modules" + File.separator + moduleXMLName
                                            + File.separator + "client" + File.separator + "classes" + lastpath);
                                File targetJavaFile = new File(targetJavaFilePath);
                                CompatibleFileUtil.copy(file, targetJavaFile);
                            }
                        }
                        else if (path.contains("uap_special" + File.separator + "src") && path.endsWith(".java")
                            && (split[1].startsWith(File.separator + "external") || split[1].startsWith(File.separator + "framework")
                                || split[1].startsWith(File.separator + "lib")))
                        {
                            int firstIndex = 0;
                            if (split[1].contains(File.separator + "nc" + File.separator))
                            {
                                firstIndex = split[1].indexOf(File.separator + "nc" + File.separator);
                            }
                            else if (split[1].contains(File.separator + "nccloud" + File.separator))
                            {
                                firstIndex = split[1].indexOf(File.separator + "nccloud" + File.separator);
                            }
                            else if (split[1].contains(File.separator + "uap" + File.separator))
                            {
                                firstIndex = split[1].indexOf(File.separator + "uap" + File.separator);
                            }
                            String lastpath = split[1].substring(firstIndex, split[1].length());
                            outFile = new File(outPath + lastpath);
                            ExportPatchUtil.exportClassFile(lastpath, outPath,
                                temp.getAbsolutePath() + File.separator + "external" + File.separator + "classes", classNameList);
                            if (patchInfoVO.isIncludeSrc())
                            {
                                File targetJavaFile =
                                    new File(temp.getAbsolutePath() + File.separator + "external" + File.separator + "classes" + lastpath);
                                CompatibleFileUtil.copy(file, targetJavaFile);
                            }
                        }
                        else if (path.contains(File.separator + "resources"))
                        {
                            String[] ressplit = path.split(Matcher.quoteReplacement(File.separator + "resources"));
                            String lastpath = ressplit[1];
                            String targetJavaFilePath = temp.getAbsolutePath() + File.separator + "resources" + lastpath;
                            File targetJavaFile = new File(targetJavaFilePath);
                            CompatibleFileUtil.copy(file, targetJavaFile);
                        }
                        else if (path.contains(File.separator + "META-INF"))
                        {
                            String[] metainfsplit = path.split(Matcher.quoteReplacement(File.separator + "META-INF"));
                            String lastpath = metainfsplit[1];
                            String targetJavaFilePath = temp.getAbsolutePath() + File.separator + "modules" + File.separator + moduleXMLName
                                + File.separator + "META-INF" + lastpath;
                            File targetJavaFile = new File(targetJavaFilePath);
                            CompatibleFileUtil.copy(file, targetJavaFile);
                        }
                        else if (path.contains(File.separator + "METADATA"))
                        {
                            String[] metasplit = path.split(Matcher.quoteReplacement(File.separator + "METADATA"));
                            String lastpath = metasplit[1];
                            String targetJavaFilePath = temp.getAbsolutePath() + File.separator + "modules" + File.separator + moduleXMLName
                                + File.separator + "METADATA" + lastpath;
                            File targetJavaFile = new File(targetJavaFilePath);
                            CompatibleFileUtil.copy(file, targetJavaFile);
                        }
                        else if (file.getName().toLowerCase().contains(".sql"))
                        {
                            String lastpath = path.replace(new File(module.getModuleFile().getParent().getPath()).getAbsolutePath(), "");
                            String targetJavaFilePath = temp.getParentFile() + File.separator + "sql" + lastpath;
                            File targetJavaFile = new File(targetJavaFilePath);
                            CompatibleFileUtil.copy(file, targetJavaFile);
                        }
                        else if (path.toLowerCase().contains("hotwebs" + File.separator + "src" + File.separator + "api")
                            && file.getName().toLowerCase().endsWith(".md"))
                        {
                            String lastpath = path.substring(path.indexOf("src") + 3);
                            String targetJavaFilePath = temp.getAbsolutePath() + File.separator + "hotwebs" + File.separator + "nccloud"
                                + File.separator + "resources" + lastpath;
                            File targetJavaFile = new File(targetJavaFilePath);
                            CompatibleFileUtil.copy(file, targetJavaFile);
                        }
                        else
                        {
                            if (path.toLowerCase().contains(File.separator + "script") && file.getName().toLowerCase().endsWith(".xml"))
                                continue;
                            String lastpath = path.replace(new File(module.getModuleFile().getParent().getPath()).getAbsolutePath(), "");
                            String targetJavaFilePath = temp.getParentFile() + lastpath;
                            File targetJavaFile = new File(targetJavaFilePath);
                            CompatibleFileUtil.copy(file, targetJavaFile);
                        }
                    }
                    else
                    {
                        String lastpath = split[1].replace(File.separator + "src", "");
                        outFile = new File(outPath + lastpath);
                        if (lastpath.contains(".java"))
                        {
                            lastpath = ExportPatchUtil.getLastPath(fileByUrl, project);
                            outFile = new File(outPath + lastpath);
                            ExportPatchUtil.exportClassFile(lastpath, outPath, temp.getAbsolutePath() + File.separator + "hotwebs"
                                + File.separator + "ncchr" + File.separator + "WEB-INF" + File.separator + "classes", classNameList);
                        }
                        else
                        {
                            File targetFile = new File(temp.getAbsolutePath() + File.separator + "modules" + File.separator + moduleXMLName
                                + File.separator + "classes" + lastpath);
                            CompatibleFileUtil.copy(outFile, targetFile);
                        }
                        if (patchInfoVO.isIncludeSrc() && file.getName().contains(".java"))
                        {
                            File targetJavaFile = new File(temp.getAbsolutePath() + File.separator + "hotwebs" + File.separator + "ncchr"
                                + File.separator + "WEB-INF" + File.separator + "classes" + lastpath);
                            CompatibleFileUtil.copy(file, targetJavaFile);
                        }
                    }
                    if (outFile == null || (fileTime = file.lastModified()) <= (builderTime =
                        new File(outFile.getAbsolutePath().replace(".java", ".class")).lastModified())) continue;
                    throw new Exception("💔👉🏻存在修改后文件未编译的情况，请重新编译项目后再导出补丁❌");
                }
            }
        }
        ExportPatchUtil.exportOtherFile(patchInfoVO, temp, classNameList);
        ZipUtil.toZip(temp.getParentFile());
    }
    
    private static String getLastPath(VirtualFile fileByUrl, Project project)
    {
        if (fileByUrl != null)
        {
            VirtualFile sourceRootForFile = ProjectFileIndex.getInstance((Project) project).getSourceRootForFile(fileByUrl);
            return File.separator + VfsUtilCore.getRelativePath((VirtualFile) fileByUrl, (VirtualFile) sourceRootForFile);
        }
        return null;
    }
    
    private static List<String> getExportUrlList(Project project)
    {
        Module[] modules = ModuleManager.getInstance((Project) project).getModules();
        ArrayList<String> excludeUrls = new ArrayList();
        for (Module module : modules)
        {
            ModuleRootManager manager = ModuleRootManager.getInstance((Module) module);
            String[] excludeRootUrls = manager.getExcludeRootUrls();
            excludeUrls.addAll(new ArrayList<String>(Arrays.asList(excludeRootUrls)));
        }
        excludeUrls = (ArrayList<String>) excludeUrls.stream().distinct().collect(Collectors.toList());
        return excludeUrls;
    }
    
    private static void exportOtherFile(PatchInfoVO patchInfoVO, File temp, List<String> classNameList) throws Exception
    {
        long time = System.currentTimeMillis();
        Date date = new Date(time);
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        String datetimeStr = sdf.format(date);
        String id = UUID.randomUUID().toString();
        TempleteUtil templeteUtil = new TempleteUtil();
        String installpatchContent = templeteUtil.readTemplate("installpatch.xml");
        String[] list = temp.list();
        StringBuffer sbstr = new StringBuffer();
        for (String s : list)
        {
            sbstr.append("<copy><from>/replacement/" + s + "/</from><to>/" + s + "/</to></copy>\r\n");
        }
        installpatchContent = installpatchContent.replace("{0}", sbstr.toString());
        templeteUtil.outFile(new File(temp.getParentFile().getAbsolutePath() + File.separator + "installpatch.xml"), installpatchContent,
            "UTF-8");
        String packmetadataContent = templeteUtil.readTemplate("packmetadata.xml");
        StringBuilder modifiedJavaClasses = new StringBuilder();
        for (String className : classNameList)
        {
            if (className.endsWith(".class"))
            {
                className = className.replace(".class", "");
            }
            if (modifiedJavaClasses.length() > 0)
            {
                modifiedJavaClasses.append("," + className);
                continue;
            }
            modifiedJavaClasses.append(className);
        }
        Map<String, String> arguments = patchInfoVO.toMap();
        arguments.put("modifiedJavaClasses", modifiedJavaClasses.toString());
        arguments.put("description", patchInfoVO.getPatchDesc());
        arguments.put("modifiedModules", patchInfoVO.getEditModule());
        arguments.put("needRecreatedLoginJar", Boolean.toString(patchInfoVO.isRebuildAppletJar()));
        List<String> bugNumList = patchInfoVO.getBugs().stream().map(bug -> (String) bug.get("bugNum")).collect(Collectors.toList());
        arguments.put("bugs", StringUtils.join(bugNumList.toArray(new String[0]), ","));
        arguments.put("patchPriority", patchInfoVO.getPriority());
        arguments.put("patchVersion", patchInfoVO.getReferPatch());
        arguments.put("dependInfo", patchInfoVO.getRelyPatch());
        arguments.put("id", patchInfoVO.getPatchId());
        arguments.put("time", datetimeStr);
        arguments.put("department", patchInfoVO.getDepartment());
        arguments.put("searchKeys", patchInfoVO.getKeyword());
        packmetadataContent = StringUtils.format(packmetadataContent, arguments);
        templeteUtil.outFile(new File(temp.getParentFile().getAbsolutePath() + File.separator + "packmetadata.xml"), packmetadataContent,
            "UTF-8");
        String readmeContent = templeteUtil.readTemplate("readme.txt");
        Map<String, String> readmeArguments = patchInfoVO.toMap();
        List<String> bugList = patchInfoVO.getBugs().stream().map(bug -> (String) bug.get("bugNum") + " - " + (String) bug.get("bugDesc"))
            .collect(Collectors.toList());
        readmeArguments.put("pathTime", datetimeStr);
        readmeArguments.put("bugs", StringUtils.join(bugList.toArray(new String[0]), "\n\t"));
        readmeContent = StringUtils.format(readmeContent, readmeArguments);
        templeteUtil.outFile(new File(temp.getParentFile().getAbsolutePath() + File.separator + "readme.txt"), readmeContent, "UTF-8");
    }
    
    private static void exportClassFile(String lastpath, String outPath, String targetPath, List<String> classNameList) throws Exception
    {
        lastpath = lastpath.replace(".java", ".class");
        targetPath = (String) targetPath + lastpath;
        File outFile = new File(outPath + lastpath);
        String outfilename = outFile.getName().replace(".class", "");
        File[] files = outFile.getParentFile().listFiles();
        if (files == null || files.length == 0)
        {
            throw new Exception("未取到编译后文件目录：" + outFile.getParentFile());
        }
        for (int i = 0; i < files.length; ++i)
        {
            File file = files[i];
            if (!StringUtils.equals(file.getName(), outFile.getName()) && !file.getName().startsWith(outfilename + "$")) continue;
            CompatibleFileUtil.copy(file, new File(((String) targetPath).replace(outFile.getName(), file.getName())));
            String className =
                file.getAbsolutePath().replace(new File(outPath).getAbsolutePath() + File.separator, "").replace(File.separator, ".");
            classNameList.add(className);
        }
    }
    
    private static Map<String, List<File>> getFile(List<String> exportPathList)
    {
        ArrayList fileList = new ArrayList();
        HashMap<String, List<File>> map = new HashMap<String, List<File>>();
        for (int i = 0; i < exportPathList.size(); ++i)
        {
            String path = exportPathList.get(i);
            List<File> list = ExportPatchUtil.getFile(new File(path));
            map.put(path, list);
        }
        return map;
    }
    
    private static List<File> getFile(File file)
    {
        ArrayList<File> fileList = new ArrayList<File>();
        String lowfilename = file.getName().toLowerCase();
        String lowfilepath = file.getAbsolutePath().toLowerCase();
        if (lowfilename.equals("component.xml") || lowfilepath.contains(".svn") || lowfilepath.contains(".git")
            || lowfilepath.contains(".ds_store"))
        {
            return fileList;
        }
        if (file.isFile())
        {
            fileList.add(file);
        }
        else
        {
            File[] files = file.listFiles();
            if (files != null)
            {
                for (int i = 0; i < files.length; ++i)
                {
                    List<File> list = ExportPatchUtil.getFile(files[i]);
                    fileList.addAll(list);
                }
            }
        }
        return fileList;
    }
    
    public static String getModuleName(Module module)
    {
        String moduleName = module.getName();
        if (module.getModuleFile() != null && module.getModuleFile().getParent() != null
            && new File(module.getModuleFile().getParent().getPath() + File.separator + "META-INF" + File.separator + "module.xml")
                .exists())
        {
            File file =
                new File(module.getModuleFile().getParent().getPath() + File.separator + "META-INF" + File.separator + "module.xml");
            Node node = null;
            try
            {
                node = XMLToObject.getNodeFromFile(file, true);
                NamedNodeMap attributes = node.getAttributes();
                Node name = attributes.getNamedItem("name");
                moduleName = name.getNodeValue();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        return moduleName;
    }
    
    public String findModuleXmlName(VirtualFile file)
    {
        String moduleName = this.findModuleXmlNameDownward(file, 0);
        if (moduleName != null)
        {
            return moduleName;
        }
        for (VirtualFile current = file.getParent(); current != null; current = current.getParent())
        {
            VirtualFile moduleXml;
            VirtualFile metaInf = current.findChild("META-INF");
            if (metaInf == null || !metaInf.isDirectory() || (moduleXml = metaInf.findChild("module.xml")) == null
                || moduleXml.isDirectory()) continue;
            try
            {
                File moduleFile = new File(moduleXml.getPath());
                Node node = XMLToObject.getNodeFromFile(moduleFile, true);
                NamedNodeMap attributes = node.getAttributes();
                Node nameNode = attributes.getNamedItem("name");
                if (nameNode == null) continue;
                return nameNode.getNodeValue();
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        throw new RuntimeException(
            "1 未找到module.xml文件,请维护模块下的META-INF/module.xml文件\n2 规则为: \n 2.1 从选中的文件或目录先向下递归,找第一个META-INF下面的module.xml中的模块编码\n  2.2 再向上递归，找到第一个module.xml中的模块编码");
    }
    
    private String findModuleXmlNameDownward(VirtualFile file, int depth)
    {
        VirtualFile moduleXml;
        if (depth > 5)
        {
            return null;
        }
        VirtualFile metaInf = file.findChild("META-INF");
        if (metaInf != null && metaInf.isDirectory() && (moduleXml = metaInf.findChild("module.xml")) != null && !moduleXml.isDirectory())
        {
            try
            {
                File moduleFile = new File(moduleXml.getPath());
                Node node = XMLToObject.getNodeFromFile(moduleFile, true);
                NamedNodeMap attributes = node.getAttributes();
                Node nameNode = attributes.getNamedItem("name");
                if (nameNode != null)
                {
                    return nameNode.getNodeValue();
                }
            }
            catch (Exception e)
            {
                e.printStackTrace();
            }
        }
        for (VirtualFile child : file.getChildren())
        {
            String moduleName;
            if (!child.isDirectory() || (moduleName = this.findModuleXmlNameDownward(child, depth + 1)) == null) continue;
            return moduleName;
        }
        return null;
    }
    
    public Vector<Object> convertToVector(Object[] anArray)
    {
        if (anArray == null)
        {
            return null;
        }
        Vector<Object> v = new Vector<Object>(anArray.length);
        Object[] var2 = anArray;
        int var3 = anArray.length;
        for (int var4 = 0; var4 < var3; ++var4)
        {
            Object o = var2[var4];
            v.addElement(o);
        }
        return v;
    }
}
