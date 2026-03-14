package nc.uap.mde.tool;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.Closeable;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.nio.ByteBuffer;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;
import java.util.jar.JarEntry;
import java.util.jar.JarFile;
import java.util.regex.Matcher;
import java.util.zip.CRC32;
import java.util.zip.CheckedOutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class FileTool
{
    /** Buffer的大小 */
    private static Integer BUFFER_SIZE = 1024 * 1024 * 10;
    private static final String NCC_CLASSES_PAHT;
    private static final String NCCHR_CLASSES_PAHT;
    private static final String NCCHR_LIB_PAHT;
    private static final String SRC_LIB_PAHT;
    private static final String AIM_EXTEND_PAHT;
    private static final String AIM_EXTERNAL_PATH;
    private static final String AIM_EXTERNALCLASSES_PATH;
    private static final String YYCONFIG = "yyconfig";
    private static final String DEFAULTCHARSET = "UTF-8";
    private static final String JARSUFFIX = ".jar";
    private static final String XMLSUFFIX = ".xml";
    private static final String JSONSUFFIX = ".json";
    
    public static void removeHotwebsJars(final String homePath) throws Exception
    {
        removeConfigFile(homePath);
        removeJarFile(homePath);
        removeClassesFile(homePath);
    }
    
    private static void removeConfigFile(final String homePath) throws IOException
    {
        final File filePath = new File(homePath + FileTool.SRC_LIB_PAHT);
        if (!filePath.exists())
        {
            return;
        }
        final File[] files = filePath.listFiles();
        if (files == null)
        {
            return;
        }
        final File[] array = files;
        for (int length = array.length, i = 0; i < length; ++i)
        {
            final File file = array[i];
            if (file.getName().contains(".jar"))
            {
                final JarFile jarFile = new JarFile(file.toString());
                final Enumeration<JarEntry> entries = jarFile.entries();
                while (entries.hasMoreElements())
                {
                    final JarEntry jarEntry = entries.nextElement();
                    final String fileName = jarEntry.getName();
                    if (!fileName.startsWith("yyconfig"))
                    {
                        continue;
                    }
                    if (isFile(fileName))
                    {
                        continue;
                    }
                    final File aimFilePath = new File(homePath + FileTool.AIM_EXTEND_PAHT + fileName);
                    if (aimFilePath.exists())
                    {
                        continue;
                    }
                    aimFilePath.mkdirs();
                }
                final Enumeration<JarEntry> entries2 = jarFile.entries();
                while (entries2.hasMoreElements())
                {
                    final JarEntry jarEntry2 = entries2.nextElement();
                    final String fileName2 = jarEntry2.getName();
                    if (!fileName2.startsWith("yyconfig"))
                    {
                        continue;
                    }
                    if (!isFile(fileName2))
                    {
                        continue;
                    }
                    final File xmlFile = new File(homePath + FileTool.AIM_EXTEND_PAHT + fileName2);
                    if (xmlFile.exists())
                    {
                        continue;
                    }
                    final InputStream input = jarFile.getInputStream(jarEntry2);
                    process(input, fileName2, homePath);
                }
                jarFile.close();
            }
        }
        System.out.println("-------------- 移动yyconfig文件成功 --------------");
    }
    
    private static void process(final InputStream input, final String fileName, final String homePath) throws IOException
    {
        final InputStreamReader isr = new InputStreamReader(input, "UTF-8");
        final BufferedReader reader = new BufferedReader(isr);
        final File file = new File(homePath + FileTool.AIM_EXTEND_PAHT + fileName);
        final OutputStreamWriter osw = new OutputStreamWriter(new FileOutputStream(file), "UTF-8");
        final BufferedWriter writer = new BufferedWriter(osw, 1024);
        String line = null;
        while ((line = reader.readLine()) != null)
        {
            writer.write(line);
            writer.newLine();
            writer.flush();
        }
        reader.close();
        writer.close();
    }
    
    private static Boolean isFile(final String fileName)
    {
        final String[] tmpArr = fileName.split(Matcher.quoteReplacement(File.separator));
        final int length = tmpArr.length;
        if (tmpArr[length - 1].contains(".xml") || tmpArr[length - 1].contains(".json"))
        {
            return true;
        }
        return false;
    }
    
    private static void removeJarFile(final String homePath)
    {
        final File srcFile = new File(homePath + FileTool.SRC_LIB_PAHT);
        final File[] files = srcFile.listFiles();
        if (files == null)
        {
            return;
        }
        final File aimFile = new File(homePath + FileTool.AIM_EXTERNAL_PATH);
        if (!aimFile.exists())
        {
            aimFile.mkdirs();
        }
        final File[] array = files;
        for (int length = array.length, i = 0; i < length; ++i)
        {
            final File file = array[i];
            final File tmpfile = new File(aimFile + File.separator + file.getName());
            if (tmpfile.exists())
            {
                tmpfile.delete();
            }
            file.renameTo(tmpfile);
        }
        System.out.println("---------------- 移动jar文件成功  -----------------");
    }
    
    private static void removeHrJarFile(final String homePath)
    {
        final File srcFile = new File(homePath + FileTool.NCCHR_LIB_PAHT);
        final File[] files = srcFile.listFiles();
        if (files == null)
        {
            return;
        }
        final File aimFile = new File(homePath + FileTool.AIM_EXTERNAL_PATH);
        if (!aimFile.exists())
        {
            aimFile.mkdirs();
        }
        final File[] array = files;
        for (int length = array.length, i = 0; i < length; ++i)
        {
            final File file = array[i];
            final File tmpfile = new File(aimFile + File.separator + file.getName());
            if (tmpfile.exists())
            {
                tmpfile.delete();
            }
            file.renameTo(tmpfile);
        }
        System.out.println("---------------- 移动jar文件成功  -----------------");
    }
    
    private static void removeClassesFile(final String homePath) throws Exception
    {
        final File srcFile = new File(homePath + FileTool.NCC_CLASSES_PAHT);
        if (!srcFile.exists())
        {
            return;
        }
        final File[] files = srcFile.listFiles();
        if (files == null)
        {
            return;
        }
        final File aimFile = new File(homePath + FileTool.AIM_EXTERNALCLASSES_PATH);
        if (!aimFile.exists())
        {
            aimFile.mkdirs();
        }
        copyFolder(homePath + FileTool.NCC_CLASSES_PAHT, homePath + FileTool.AIM_EXTERNALCLASSES_PATH);
        delete(srcFile);
    }
    
    private static void removeHrClassesFile(final String homePath) throws Exception
    {
        final File srcFile = new File(homePath + FileTool.NCCHR_CLASSES_PAHT);
        if (!srcFile.exists())
        {
            return;
        }
        final File[] files = srcFile.listFiles();
        if (files == null)
        {
            return;
        }
        final File aimFile = new File(homePath + FileTool.AIM_EXTERNAL_PATH + File.separator + "classes");
        if (!aimFile.exists())
        {
            aimFile.mkdirs();
        }
        copyFolder(homePath + FileTool.NCCHR_CLASSES_PAHT, homePath + FileTool.AIM_EXTERNAL_PATH + File.separator + "classes");
        delete(srcFile);
    }
    
    public static void delete(final File file)
    {
        if (file.isDirectory())
        {
            final File[] listFiles;
            final File[] children = listFiles = file.listFiles();
            for (final File child : listFiles)
            {
                delete(child);
            }
            file.delete();
        }
        else
        {
            file.delete();
        }
    }
    
    public static void copyFolder(final String sourceFolderPath, final String targetFolderPath) throws Exception
    {
        final File sourceFolder = new File(sourceFolderPath);
        final File targetFolder = new File(targetFolderPath);
        final List<File> allFile = getAllFile(sourceFolder);
        for (int i = 0; i < allFile.size(); ++i)
        {
            final File file = allFile.get(i);
            final String relativePath = file.getAbsolutePath().replace(sourceFolder.getAbsolutePath(), "");
            final File targetFile = new File(targetFolder.getAbsolutePath() + relativePath);
            copyFile(file, targetFile);
        }
    }
    
    public static void copyFile(final String sourceFilePath, final File targetFolder) throws Exception
    {
        final File file = new File(sourceFilePath);
        final File targetFile = new File(targetFolder.getAbsolutePath() + File.separator + file.getName());
        copyFile(file, targetFile);
    }
    
    public final static boolean copyFile(File sourceFile, File targetFile)
    {
        FileInputStream fin = null;
        FileOutputStream fout = null;
        
        try
        {
            fin = new FileInputStream(sourceFile);
            fout = new FileOutputStream(targetFile);
            FileChannel in = fin.getChannel();
            FileChannel out = fout.getChannel();
            // 设定缓冲区
            ByteBuffer buffer = ByteBuffer.allocate(BUFFER_SIZE);
            while (in.read(buffer) != -1)
            {
                // 准备写入，防止其他读取，锁住文件
                buffer.flip();
                out.write(buffer);
                // 准备读取。将缓冲区清理完毕，移动文件内部指针
                buffer.clear();
            }
            close(in, out);
        }
        catch (IOException e)
        {
        }
        return false;
    }
    
    public static void close(Closeable... closeables)
    {
        try
        {
            
            for (Closeable closeable : closeables)
            {
                if (null != closeable)
                {
                    closeable.close();
                }
            }
            
        }
        catch (IOException ioe)
        {
            throw new RuntimeException(ioe);
        }
    }
    
    private static List<File> getAllFile(final File folder)
    {
        final List<File> list = new ArrayList<File>();
        final File[] files = folder.listFiles();
        for (int i = 0; i < files.length; ++i)
        {
            final File file = files[i];
            if (file.isFile())
            {
                list.add(file);
            }
            else
            {
                final List<File> allFile = getAllFile(file);
                list.addAll(allFile);
            }
        }
        return list;
    }
    
    public static void toZip(final File patchFolder) throws Exception
    {
        final String zipPath = patchFolder.getAbsolutePath() + ".zip";
        final FileOutputStream fileOutputStream = new FileOutputStream(zipPath);
        final CheckedOutputStream cos = new CheckedOutputStream(fileOutputStream, new CRC32());
        final ZipOutputStream out = new ZipOutputStream(cos);
        compressFile(patchFolder, out, patchFolder.getAbsolutePath());
        out.close();
    }
    
    private static void compressFile(final File file, final ZipOutputStream out, final String patchFolderPath) throws Exception
    {
        if (file.isDirectory())
        {
            final File[] files = file.listFiles();
            if (files != null)
            {
                for (int i = 0; i < files.length; ++i)
                {
                    compressFile(files[i], out, patchFolderPath);
                }
            }
        }
        else
        {
            final BufferedInputStream bis = new BufferedInputStream(new FileInputStream(file));
            final String str = patchFolderPath + File.separator;
            final String filePath = file.getPath().substring(str.length());
            final ZipEntry entry = new ZipEntry(filePath);
            out.putNextEntry(entry);
            final byte[] data = new byte[8192];
            int count;
            while ((count = bis.read(data, 0, 8192)) != -1)
            {
                out.write(data, 0, count);
            }
            bis.close();
        }
    }
    
    static
    {
        NCC_CLASSES_PAHT = File.separator + "hotwebs" + File.separator + "nccloud" + File.separator + "WEB-INF" + File.separator + "classes"
            + File.separator;
        NCCHR_CLASSES_PAHT = File.separator + "hotwebs" + File.separator + "ncchr" + File.separator + "WEB-INF" + File.separator + "classes"
            + File.separator;
        NCCHR_LIB_PAHT =
            File.separator + "hotwebs" + File.separator + "ncchr" + File.separator + "WEB-INF" + File.separator + "lib" + File.separator;
        SRC_LIB_PAHT =
            File.separator + "hotwebs" + File.separator + "nccloud" + File.separator + "WEB-INF" + File.separator + "lib" + File.separator;
        AIM_EXTEND_PAHT = File.separator + "hotwebs" + File.separator + "nccloud" + File.separator + "WEB-INF" + File.separator + "extend"
            + File.separator;
        AIM_EXTERNAL_PATH = File.separator + "external" + File.separator + "lib";
        AIM_EXTERNALCLASSES_PATH = File.separator + "external" + File.separator + "classes";
    }
}
