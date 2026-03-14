package org.cuiq.util;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.Writer;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.OpenOption;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Objects;

public class FileUtil
{
    public FileUtil()
    {
    }
    
    public static String getFilePath(String relativePath)
    {
        String dir = ((URL) Objects.requireNonNull(FileUtil.class.getResource("/"))).getPath();
        return dir + relativePath;
    }
    
    public static String getFilePath(Class<?> clazz, String className)
    {
        String path = ((URL) Objects.requireNonNull(clazz.getResource("/"))).getPath();
        return String.format("%s%s.class", path, className.replace('.', File.separatorChar));
    }
    
    public static byte[] readBytes(String filepath)
    {
        File file = new File(filepath);
        if (!file.exists())
        {
            throw new IllegalArgumentException("File Not Exist: " + filepath);
        }
        else
        {
            InputStream in = null;
            
            try
            {
                in = Files.newInputStream(file.toPath());
                in = new BufferedInputStream(in);
                ByteArrayOutputStream bao = new ByteArrayOutputStream();
                IOUtil.copy(in, bao);
                byte[] var4 = bao.toByteArray();
                return var4;
            }
            catch (IOException var8)
            {
                IOException e = var8;
                e.printStackTrace();
            }
            finally
            {
                IOUtil.closeQuietly(in);
            }
            
            throw new RuntimeException("Can not read file: " + filepath);
        }
    }
    
    public static void writeBytes(String filepath, byte[] bytes)
    {
        File file = new File(filepath);
        File dirFile = file.getParentFile();
        mkdirs(dirFile);
        
        try
        {
            FileOutputStream fos = new FileOutputStream(filepath);
            Throwable var5 = null;
            
            try
            {
                fos.write(bytes);
            }
            catch (Throwable var15)
            {
                var5 = var15;
                throw var15;
            }
            finally
            {
                if (fos != null)
                {
                    if (var5 != null)
                    {
                        try
                        {
                            fos.close();
                        }
                        catch (Throwable var14)
                        {
                            var5.addSuppressed(var14);
                        }
                    }
                    else
                    {
                        fos.close();
                    }
                }
                
            }
            
        }
        catch (IOException var17)
        {
            IOException e = var17;
            throw new RuntimeException(e);
        }
    }
    
    public static List<String> readLines(String filepath)
    {
        return readLines(filepath, "UTF8");
    }
    
    public static List<String> readLines(String filepath, String charsetName)
    {
        File file = new File(filepath);
        if (!file.exists())
        {
            throw new IllegalArgumentException("File Not Exist: " + filepath);
        }
        else
        {
            InputStream in = null;
            Reader reader = null;
            BufferedReader bufferReader = null;
            
            try
            {
                in = Files.newInputStream(file.toPath(), new OpenOption[0]);
                reader = new InputStreamReader(in, charsetName);
                bufferReader = new BufferedReader(reader);
                List<String> list = new ArrayList();
                String line;
                while ((line = bufferReader.readLine()) != null)
                {
                    list.add(line);
                }
                return list;
            }
            catch (IOException var12)
            {
                IOException e = var12;
                e.printStackTrace();
            }
            finally
            {
                IOUtil.closeQuietly(bufferReader);
                IOUtil.closeQuietly(reader);
                IOUtil.closeQuietly(in);
            }
            
            assert false : "bytes is null";
            
            return null;
        }
    }
    
    public static void writeLines(String filepath, List<String> lines)
    {
        if (lines != null && lines.size() >= 1)
        {
            File file = new File(filepath);
            File dirFile = file.getParentFile();
            mkdirs(dirFile);
            OutputStream out = null;
            Writer writer = null;
            BufferedWriter bufferedWriter = null;
            
            try
            {
                out = Files.newOutputStream(file.toPath());
                writer = new OutputStreamWriter(out, StandardCharsets.UTF_8);
                bufferedWriter = new BufferedWriter(writer);
                Iterator var14 = lines.iterator();
                
                while (var14.hasNext())
                {
                    String line = (String) var14.next();
                    bufferedWriter.write(line);
                    bufferedWriter.newLine();
                }
            }
            catch (IOException var12)
            {
                IOException ex = var12;
                ex.printStackTrace();
            }
            finally
            {
                IOUtil.closeQuietly(bufferedWriter);
                IOUtil.closeQuietly(writer);
                IOUtil.closeQuietly(out);
            }
            
        }
    }
    
    public static void mkdirs(File dirFile)
    {
        boolean file_exists = dirFile.exists();
        if (!file_exists || !dirFile.isDirectory())
        {
            if (file_exists && dirFile.isFile())
            {
                throw new RuntimeException("Not A Directory: " + dirFile);
            }
            else
            {
                if (!file_exists)
                {
                    boolean flag = dirFile.mkdirs();
                    
                    assert flag : "Create Directory Failed: " + dirFile.getAbsolutePath();
                }
                
            }
        }
    }
    
    public static void clear(File file)
    {
        if (file.exists())
        {
            if (file.isDirectory())
            {
                File[] files = file.listFiles();
                if (files != null)
                {
                    File[] var2 = files;
                    int var3 = files.length;
                    
                    for (int var4 = 0; var4 < var3; ++var4)
                    {
                        File f = var2[var4];
                        delete(f);
                    }
                }
            }
            else
            {
                delete(file);
            }
            
        }
    }
    
    public static void delete(File file)
    {
        if (file.exists())
        {
            if (file.isFile())
            {
                boolean flag = file.delete();
                
                assert flag : "[Warning] delete file failed: " + file.getAbsolutePath();
            }
            
            if (file.isDirectory())
            {
                File[] files = file.listFiles();
                if (files != null)
                {
                    File[] var2 = files;
                    int var3 = files.length;
                    
                    for (int var4 = 0; var4 < var3; ++var4)
                    {
                        File f = var2[var4];
                        delete(f);
                    }
                }
                
                boolean flag = file.delete();
                
                assert flag : "[Warning] delete file failed: " + file.getAbsolutePath();
            }
            
        }
    }
    
    public static byte[] readStream(InputStream in, boolean close)
    {
        if (in == null)
        {
            throw new IllegalArgumentException("inputStream is null!!!");
        }
        else
        {
            try
            {
                ByteArrayOutputStream out = new ByteArrayOutputStream();
                IOUtil.copy(in, out);
                byte[] var3 = out.toByteArray();
                return var3;
            }
            catch (IOException var7)
            {
                IOException e = var7;
                e.printStackTrace();
            }
            finally
            {
                if (close)
                {
                    IOUtil.closeQuietly(in);
                }
                
            }
            
            return null;
        }
    }
    
    public static InputStream getInputStream(String className)
    {
        return ClassLoader.getSystemResourceAsStream(className.replace('.', '/') + ".class");
    }
}
