package org.cuiq.visitor;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Objects;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.Opcodes;
import jdk.internal.org.objectweb.asm.Type;
import org.cuiq.util.FileUtil;

public class AbstractVisitor implements Opcodes {
    public AbstractVisitor() {
    }

    public static ClassReader getClassReader(byte[] classfileBuffer) {
        return new ClassReader(classfileBuffer);
    }

    public static ClassReader getClassReader(InputStream inputStream) {
        return new ClassReader((byte[])Objects.requireNonNull(FileUtil.readStream(inputStream, true)));
    }

    public static ClassWriter getClassWriter() {
        return new ClassWriter(2) {
            protected String getCommonSuperClass(String type1, String type2) {
                ClassLoader classLoader = Thread.currentThread().getContextClassLoader();

                Class c;
                Class d;
                try {
                    c = Class.forName(type1.replace('/', '.'), false, classLoader);
                    d = Class.forName(type2.replace('/', '.'), false, classLoader);
                } catch (Exception var7) {
                    return "java/lang/Object";
                }

                if (c.isAssignableFrom(d)) {
                    return type1;
                } else if (d.isAssignableFrom(c)) {
                    return type2;
                } else if (!c.isInterface() && !d.isInterface()) {
                    do {
                        c = c.getSuperclass();
                    } while(!c.isAssignableFrom(d));

                    return c.getName().replace('.', '/');
                } else {
                    return "java/lang/Object";
                }
            }
        };
    }

    public static void outAsmClass(byte[] bytes, String className) {
        FileUtil.writeBytes(System.getProperty("user.home") + "\\Downloads\\" + className + ".class", bytes);
    }

    public static void outAsmText(byte[] bytes, Class<?> clazz) {
        String s = File.separator;
        String path = System.getProperty("user.home") + s + "Downloads" + s + "agentclass" + s + Type.getInternalName(clazz) + ".class";
        System.out.println("输出路径：" + path);
        (new File(path)).getParentFile().mkdirs();

        try {
            FileOutputStream fos = new FileOutputStream(path);
            Throwable var5 = null;

            try {
                fos.write(bytes);
            } catch (Throwable var15) {
                var5 = var15;
                throw var15;
            } finally {
                if (fos != null) {
                    if (var5 != null) {
                        try {
                            fos.close();
                        } catch (Throwable var14) {
                            var5.addSuppressed(var14);
                        }
                    } else {
                        fos.close();
                    }
                }

            }

        } catch (IOException var17) {
            IOException e = var17;
            throw new RuntimeException(e);
        }
    }
}
