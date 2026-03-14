
package org.cuiq.monitor.parameter;

import jdk.internal.org.objectweb.asm.MethodVisitor;
import jdk.internal.org.objectweb.asm.Type;
import jdk.internal.org.objectweb.asm.commons.AdviceAdapter;

public class TimeVisitor extends AdviceAdapter {
    public static final String LOG = "org/cuiq/monitor/parameter/MonitorLogUtils";
    public static final String START_METHOD = "startMethod";
    public static final String DESC = "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V";
    public static final String END_METHOD = "endMethod";
    private final String methodName;
    private final String descriptor;
    private final String className;
    private final boolean write;

    public TimeVisitor(MethodVisitor methodVisitor, int access, String methodName, String descriptor, String className) {
        super(327680, methodVisitor, access, methodName, descriptor);
        this.methodName = methodName;
        this.descriptor = descriptor;
        this.className = className;
        this.write = !"<init>".equals(methodName) && !"<clinit>".equals(methodName);
    }

    protected void onMethodEnter() {
        super.onMethodEnter();
        if (this.write) {
            this.mv.visitLdcInsn(this.className);
            this.mv.visitLdcInsn(this.methodName);
            this.mv.visitLdcInsn(this.descriptor);
            this.mv.visitMethodInsn(184, "org/cuiq/monitor/parameter/MonitorLogUtils", "startMethod", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false);
        }

    }

    protected void onMethodExit(int opcode) {
        super.onMethodExit(opcode);
        if (this.write) {
            this.mv.visitLdcInsn(this.className);
            this.mv.visitLdcInsn(this.methodName);
            this.mv.visitLdcInsn(this.descriptor);
            this.mv.visitMethodInsn(184, "org/cuiq/monitor/parameter/MonitorLogUtils", "endMethod", "(Ljava/lang/String;Ljava/lang/String;Ljava/lang/String;)V", false);
        }

    }

    public void visitCode() {
        super.visitCode();
        boolean isStatic = (this.methodAccess & 8) != 0;
        int slotIndex = isStatic ? 0 : 1;
        this.printMessage("Method Enter:visitCode: " + this.className + "- " + this.methodName + this.methodDesc);
        Type methodType = Type.getMethodType(this.methodDesc);
        Type[] argumentTypes = methodType.getArgumentTypes();
        Type[] var5 = argumentTypes;
        int var6 = argumentTypes.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            Type t = var5[var7];
            int sort = t.getSort();
            int size = t.getSize();
            String descriptor = t.getDescriptor();
            int opcode = t.getOpcode(21);
            super.visitVarInsn(opcode, slotIndex);
            if (sort >= 1 && sort <= 8) {
                String methodDesc = String.format("(%s)V", descriptor);
                this.printValueOnStack(methodDesc);
            } else {
                this.printValueOnStack("(Ljava/lang/Object;)V");
            }

            slotIndex += size;
        }

    }

    public void visitInsn(int opcode) {
        super.visitInsn(opcode);
        if (opcode >= 172 && opcode <= 177 || opcode == 191) {
            this.printMessage("Method Exit:visitInsn: " + this.className + "- " + this.methodName + this.methodDesc);
            if (opcode <= 175) {
                Type methodType = Type.getMethodType(this.methodDesc);
                Type returnType = methodType.getReturnType();
                int size = returnType.getSize();
                String descriptor = returnType.getDescriptor();
                if (size == 1) {
                    super.visitInsn(89);
                } else {
                    super.visitInsn(92);
                }

                String methodDesc = String.format("(%s)V", descriptor);
                this.printValueOnStack(methodDesc);
            } else if (opcode == 176) {
                super.visitInsn(89);
                this.printValueOnStack("(Ljava/lang/Object;)V");
            } else if (opcode == 177) {
                this.printMessage("    return void");
            } else {
                this.printMessage("    abnormal return");
            }
        }

    }

    private void printMessage(String str) {
        super.visitLdcInsn(str);
        super.visitMethodInsn(184, "org/cuiq/ParameterUtils", "printText", "(Ljava/lang/String;)V", false);
    }

    private void printValueOnStack(String descriptor) {
        super.visitMethodInsn(184, "org/cuiq/ParameterUtils", "printValueOnStack", descriptor, false);
    }
}
