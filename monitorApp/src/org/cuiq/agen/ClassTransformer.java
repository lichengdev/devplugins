package org.cuiq.agen;

import java.io.InputStream;
import java.lang.instrument.ClassDefinition;
import java.lang.instrument.ClassFileTransformer;
import java.lang.instrument.Instrumentation;
import java.lang.instrument.UnmodifiableClassException;
import java.security.ProtectionDomain;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.ClassWriter;
import org.cuiq.monitor.jdbc.OracleJDBCVisitor;
import org.cuiq.monitor.parameter.PrintClassArgs;
import org.cuiq.util.FileUtil;
import org.cuiq.visitor.AbstractVisitor;
import org.cuiq.xisoft.business.EmptyVisitor;
import org.cuiq.xisoft.business.FrameServiceVisitor;
import org.cuiq.xisoft.business.NullPointExceptionVisitor;
import org.cuiq.xisoft.business.ThrowableVisitor;
import org.cuiq.xisoft.business.UserServiceVisitor;

public class ClassTransformer implements ClassFileTransformer {
    public static final boolean jdbc = System.getProperty("jdbc", "Y").equals("Y");
    public static final boolean authorize = System.getProperty("authorize", "Y").equals("Y");
    public static final boolean noPassword = System.getProperty("password", "Y").equals("Y");
    public static final boolean monitorPath = !System.getProperty("monitorPath", "Y").equals("N");
    public static final String[] packages = System.getProperty("monitorPath", "").replace("/", ".").replace("\\", ".").split(";");
    public static final String[] paths = System.getProperty("monitorPath", "cuiq.20240427.").split(";");
    private final String agentArgs;
    private final Instrumentation inst;
    boolean mdfnull = false;
    boolean mdfthrowable = false;

    public ClassTransformer(String agentArgs, Instrumentation inst) {
        this.agentArgs = agentArgs;
        this.inst = inst;
    }

    public byte[] transform(ClassLoader loader, String className, Class<?> classBeingRedefined, ProtectionDomain protectionDomain, byte[] classfileBuffer) {
        try {
            if (loader != null && className != null) {
                if (authorize) {
                    classfileBuffer = noAuthorize(className, classfileBuffer);
                }

                if (monitorPath) {
                    classfileBuffer = monitorProcess(className, classfileBuffer);
                }

                byte[] bytes;
                ClassWriter classWriter;
                if (noPassword && "com/xisoft/frame/service/UserService".equals(className)) {
                    System.out.println("...Login no password..." + className);
                    classWriter = AbstractVisitor.getClassWriter();
                    AbstractVisitor.getClassReader(classfileBuffer).accept(new UserServiceVisitor.MyClassVisitor(327680, classWriter), 8);
                    bytes = classWriter.toByteArray();
                    AbstractVisitor.outAsmClass(bytes, className);
                    return bytes;
                } else if (!jdbc || !"oracle/jdbc/driver/OracleDriver".equals(className) && !className.startsWith("oracle/jdbc/driver/OraclePreparedStatement")) {
                    if ("com/xisoft/frame/service/FrameService".equals(className)) {
                        System.out.println("...Proxy FrameService!..." + className);
                        classWriter = AbstractVisitor.getClassWriter();
                        AbstractVisitor.getClassReader(classfileBuffer).accept(new FrameServiceVisitor.MyClassVisitor(327680, classWriter, className), 8);
                        bytes = classWriter.toByteArray();
                        AbstractVisitor.outAsmClass(bytes, className);
                        return bytes;
                    } else {
                        return classfileBuffer;
                    }
                } else {
                    System.out.println("...Proxy jdbc..." + className);
                    classWriter = AbstractVisitor.getClassWriter();
                    AbstractVisitor.getClassReader(classfileBuffer).accept(new OracleJDBCVisitor.MyClassVisitor(327680, classWriter, className), 8);
                    bytes = classWriter.toByteArray();
                    AbstractVisitor.outAsmClass(bytes, className);
                    return bytes;
                }
            } else {
                return classfileBuffer;
            }
        } catch (Exception var8) {
            Exception e = var8;
            throw new RuntimeException(e);
        }
    }

    private void throwableException(ClassLoader loader) throws ClassNotFoundException {
        if (!this.mdfthrowable) {
            InputStream inputStream = loader.getResourceAsStream("java/lang/Throwable.class");
            if (inputStream != null) {
                this.mdfthrowable = true;
                byte[] stream = FileUtil.readStream(inputStream, false);
                System.out.println("...Proxy Throwable!...");
                ClassWriter classWriter = AbstractVisitor.getClassWriter();
                AbstractVisitor.getClassReader(stream).accept(new ThrowableVisitor.MyClassVisitor(327680, classWriter), 8);
                byte[] bytes = classWriter.toByteArray();
                AbstractVisitor.outAsmClass(bytes, "java/lang/Throwable");

                try {
                    this.inst.redefineClasses(new ClassDefinition[]{new ClassDefinition(Throwable.class, bytes)});
                } catch (UnmodifiableClassException var7) {
                    UnmodifiableClassException e = var7;
                    e.printStackTrace(System.err);
                }
            }

        }
    }

    private void nullPointerException(ClassLoader loader) throws ClassNotFoundException {
        if (!this.mdfnull) {
            InputStream inputStream = loader.getResourceAsStream("java/lang/NullPointerException.class");
            if (inputStream != null) {
                this.mdfnull = true;
                byte[] stream = FileUtil.readStream(inputStream, false);
                System.out.println("...Proxy NullPointerException!...");
                ClassWriter classWriter = AbstractVisitor.getClassWriter();
                AbstractVisitor.getClassReader(stream).accept(new NullPointExceptionVisitor.MyClassVisitor(327680, classWriter), 8);
                byte[] bytes = classWriter.toByteArray();
                AbstractVisitor.outAsmClass(bytes, "java/lang/NullPointerException");

                try {
                    this.inst.redefineClasses(new ClassDefinition[]{new ClassDefinition(NullPointerException.class, bytes)});
                } catch (UnmodifiableClassException var7) {
                    UnmodifiableClassException e = var7;
                    e.printStackTrace(System.err);
                }
            }

        }
    }

    private static byte[] monitorProcess(String className, byte[] classfileBuffer) {
        if (className == null) {
            return classfileBuffer;
        } else {
            String[] var2 = paths;
            int var3 = var2.length;

            for(int var4 = 0; var4 < var3; ++var4) {
                String path = var2[var4];
                if (className.startsWith(path) && !className.contains("$$")) {
                    System.out.println("...Monitor..." + className);
                    ClassReader classReader = new ClassReader(classfileBuffer);
                    ClassWriter classWriter = AbstractVisitor.getClassWriter();
                    classReader.accept(new PrintClassArgs.MyClassVisitor(327680, classWriter, className), 8);
                    byte[] bytes = classWriter.toByteArray();
                    FileUtil.writeBytes(getFilepath(className), bytes);
                    return bytes;
                }
            }

            return classfileBuffer;
        }
    }

    private static byte[] noAuthorize(String className, byte[] classfileBuffer) {
        if (!"com/xisoft/security/Sd".equals(className) && !"com/xisoft/security/CAuth".equals(className)) {
            return classfileBuffer;
        } else {
            System.out.println("...No Authorize..." + className);
            ClassReader classReader = new ClassReader(classfileBuffer);
            ClassWriter classWriter = AbstractVisitor.getClassWriter();
            classReader.accept(new EmptyVisitor(327680, classWriter, className), 6);
            byte[] bytes = classWriter.toByteArray();
            FileUtil.writeBytes(getFilepath(className), bytes);
            return bytes;
        }
    }

    private static String getFilepath(String className) {
        return System.getProperty("user.home") + "\\Downloads\\" + className + ".class";
    }
}
