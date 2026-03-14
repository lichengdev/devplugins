package org.cuiq.monitor.jdbc;

import java.io.InputStream;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;
import java.util.stream.Stream;
import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.FieldVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Opcodes;
import oracle.jdbc.OracleDriver;
import org.cuiq.util.FileUtil;
import org.cuiq.visitor.AbstractVisitor;

public class OracleJDBCVisitor implements Opcodes {
    public OracleJDBCVisitor() {
    }

    public static void main(String[] args) {
        InputStream inputStream = FileUtil.getInputStream("oracle.jdbc.driver.OracleDriver");
        ClassWriter classWriter = AbstractVisitor.getClassWriter();
        AbstractVisitor.getClassReader(inputStream).accept(new MyClassVisitor(327680, classWriter, "oracle.jdbc.driver.OracleDriver"), 8);
        byte[] bytes = classWriter.toByteArray();
        AbstractVisitor.outAsmText(bytes, OracleDriver.class);
    }

    public static class MyMethodVisitor extends MethodVisitor {
        public MyMethodVisitor(int api, MethodVisitor methodVisitor) {
            super(api, methodVisitor);
        }

        public void visitMethodInsn(int opcode, String owner, String name, String desc, boolean itf) {
            boolean equals = desc.equals("(Ljava/lang/String;Ljava/util/Properties;)Ljava/sql/Connection;");
            boolean equals1 = "oracle/jdbc/driver/OracleDriver".equals(owner);
            if (equals1 && "connect".equals(name) && equals) {
                super.visitMethodInsn(opcode, owner, name + "$agent", desc, itf);
            } else {
                super.visitMethodInsn(opcode, owner, name, desc, itf);
            }

        }
    }

    public static class MyClassVisitor extends ClassVisitor {
        private final String className;
        private final String exceptions = Arrays.toString(((List)Stream.of("java/sql/SQLException").collect(Collectors.toList())).toArray());
        private boolean has_sql_proxy_field = true;
        private boolean has_params_proxy_field = true;

        public MyClassVisitor(int api, ClassVisitor cv, String className) {
            super(api, cv);
            this.className = className;
        }

        public FieldVisitor visitField(int access, String name, String desc, String signature, Object value) {
            if ("sql$agent".equals(name)) {
                this.has_sql_proxy_field = false;
            } else if ("params$agent".equals(name)) {
                this.has_params_proxy_field = false;
            }

            return super.visitField(access, name, desc, signature, value);
        }

        public void visitEnd() {
            if (!this.className.contains("PreparedStatement")) {
                super.visitEnd();
            } else {
                FieldVisitor fieldVisitor;
                if (this.has_sql_proxy_field) {
                    fieldVisitor = this.cv.visitField(2, "sql$agent", "Ljava/lang/String;", (String)null, (Object)null);
                    if (fieldVisitor != null) {
                        fieldVisitor.visitEnd();
                    }
                }

                if (this.has_params_proxy_field) {
                    fieldVisitor = this.cv.visitField(2, "params$agent", "Ljava/util/Map;", (String)null, (Object)null);
                    if (fieldVisitor != null) {
                        fieldVisitor.visitEnd();
                    }
                }

                super.visitEnd();
            }
        }

        public MethodVisitor visitMethod(int access, String methodName, String descriptor, String signature, String[] exceptions) {
            System.out.println("...Proxy jdbc method... " + methodName + ".." + descriptor + ".." + signature + ".." + Arrays.toString(exceptions));
            String desc = "(Ljava/lang/String;Ljava/util/Properties;)Ljava/sql/Connection;";
            boolean equals = desc.equals(descriptor);
            boolean equals1 = this.exceptions.equals(Arrays.toString(exceptions));
            boolean equals2 = "connect".equals(methodName);
            if (equals2 && equals && equals1) {
                MethodVisitor mv = this.cv.visitMethod(1, "connect", desc, (String)null, new String[]{"java/sql/SQLException"});
                mv.visitCode();
                mv.visitVarInsn(25, 0);
                mv.visitVarInsn(25, 1);
                mv.visitVarInsn(25, 2);
                mv.visitMethodInsn(183, "oracle/jdbc/driver/OracleDriver", "connect$agent", desc, false);
                mv.visitMethodInsn(184, "org/cuiq/monitor/jdbc/MyJdbcProxy", "proxyConnect", "(Ljava/sql/Connection;)Ljava/sql/Connection;", false);
                mv.visitInsn(176);
                mv.visitMaxs(3, 3);
                mv.visitEnd();
                MethodVisitor methodVisitor = super.visitMethod(access, methodName + "$agent", descriptor, signature, exceptions);
                return new MyMethodVisitor(327680, methodVisitor);
            } else {
                return super.visitMethod(access, methodName, descriptor, signature, exceptions);
            }
        }
    }
}
