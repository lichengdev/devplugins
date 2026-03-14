package org.cuiq.monitor.parameter;

import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.ClassWriter;
import jdk.internal.org.objectweb.asm.Label;
import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.commons.AdviceAdapter;
import org.cuiq.Test;
import org.cuiq.util.FileUtil;
import org.cuiq.visitor.AbstractVisitor;

public class PrintClassArgs extends AbstractVisitor {
    public PrintClassArgs() {
    }

    public static void main(String[] args) {
        byte[] readBytes = FileUtil.readBytes("F:\\xiso\\common\\frame-service\\target\\classes\\com\\xisoft\\frame\\service\\confClass\\ConfigClassService.class");
        ClassWriter classWriter = AbstractVisitor.getClassWriter();
        AbstractVisitor.getClassReader(readBytes).accept(new MyClassVisitor(327680, classWriter, "className"), 8);
        byte[] bytes = classWriter.toByteArray();
        AbstractVisitor.outAsmText(bytes, Test.class);
    }

    public static class MyMethodVisitor extends AdviceAdapter {
        private final String methodName;
        private final String className;
        private final String methodTrackText;
        int nanoTime;
        int trackUid;

        protected MyMethodVisitor(int api, MethodVisitor methodVisitor, int access, String methodName, String descriptor, String className) {
            super(api, methodVisitor, access, methodName, descriptor);
            this.methodName = methodName;
            this.className = className;
            this.methodTrackText = String.format("->%s.%s%s", className, methodName, descriptor);
        }

        protected void onMethodEnter() {
            this.mv.visitLabel(new Label());
            this.enter();
        }

        protected void onMethodExit(int opcode) {
            this.mv.visitLabel(new Label());
            this.exit(opcode);
        }

        private void enter() {
            this.nanoTime = this.newLocal(Type.LONG_TYPE);
            this.mv.visitMethodInsn(184, "java/lang/System", "nanoTime", "()J", false);
            this.mv.visitVarInsn(55, this.nanoTime);
            this.mv.visitLdcInsn(this.methodTrackText);
            this.trackUid = this.newLocal(Type.getType(String.class));
            this.mv.visitMethodInsn(184, "org/cuiq/monitor/parameter/Log", "printTrackText", "(Ljava/lang/String;)Ljava/lang/String;", false);
            this.mv.visitVarInsn(58, this.trackUid);
            Type methodType = Type.getMethodType(this.methodDesc);
            Type[] argumentTypes = methodType.getArgumentTypes();

            for(int i = 0; i < argumentTypes.length; ++i) {
                Type argumentType = argumentTypes[i];
                this.loadArg(i);
                this.box(argumentType);
                this.printObject("入参类型: " + argumentType);
            }

        }

        private void exit(int opcode) {
            Type methodType = Type.getMethodType(this.methodDesc);
            Type returnType = methodType.getReturnType();
            if (opcode == 191) {
                this.mv.visitLdcInsn("有异常抛出!");
            } else if (opcode == 177) {
                this.mv.visitLdcInsn("void,无返回值!");
            } else if (opcode == 176) {
                this.dup();
            } else if (opcode != 173 && opcode != 175) {
                this.dup();
                this.box(returnType);
            } else {
                this.dup2();
                this.box(returnType);
            }

            this.printObject("返回值类型: " + returnType);
            this.mv.visitVarInsn(25, this.trackUid);
            this.mv.visitLdcInsn(this.methodTrackText);
            this.mv.visitVarInsn(22, this.nanoTime);
            this.mv.visitMethodInsn(184, "org/cuiq/monitor/parameter/Log", "printTrackText", "(Ljava/lang/String;Ljava/lang/String;J)V", false);
        }

        private void printObject(String name) {
            this.mv.visitLdcInsn(name);
            this.swap();
            this.mv.visitVarInsn(25, this.trackUid);
            this.mv.visitMethodInsn(184, Type.getInternalName(Log.class), "printObject", "(Ljava/lang/String;Ljava/lang/Object;Ljava/lang/String;)V", false);
        }
    }

    public static class MyClassVisitor extends ClassVisitor {
        private final String className;

        public MyClassVisitor(int api, ClassVisitor classVisitor, String className) {
            super(api, classVisitor);
            this.className = className;
        }

        public MethodVisitor visitMethod(int access, String name, String descriptor, String signature, String[] exceptions) {
            MethodVisitor methodVisitor = super.visitMethod(access, name, descriptor, signature, exceptions);
            if (methodVisitor != null) {
                boolean isAbstractMethod = (access & 1024) != 0;
                boolean isNativeMethod = (access & 256) != 0;
                if (!isAbstractMethod && !isNativeMethod) {
                    methodVisitor = new MyMethodVisitor(this.api, (MethodVisitor)methodVisitor, access, name, descriptor, this.className);
                }
            }

            return (MethodVisitor)methodVisitor;
        }
    }
}
