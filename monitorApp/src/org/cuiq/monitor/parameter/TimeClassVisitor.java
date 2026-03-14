package org.cuiq.monitor.parameter;

import jdk.internal.org.objectweb.asm.ClassVisitor;
import jdk.internal.org.objectweb.asm.MethodVisitor;

public class TimeClassVisitor extends ClassVisitor {
    private String className;

    public TimeClassVisitor(int api, String className) {
        super(api);
        this.className = className.replace("/", ".");
    }

    public TimeClassVisitor(int api, ClassVisitor cv, String className) {
        super(api, cv);
        this.className = className.replace("/", ".");
    }

    public MethodVisitor visitMethod(int access, String name, String desc, String signature, String[] exceptions) {
        MethodVisitor mv = super.visitMethod(access, name, desc, signature, exceptions);
        if (mv != null) {
            mv = new TimeVisitor((MethodVisitor)mv, access, name, desc, this.className);
        }

        return (MethodVisitor)mv;
    }

    public void visitEnd() {
        super.visitEnd();
    }

    public void visit(int version, int access, String name, String signature, String superName, String[] interfaces) {
        this.className = name.replace("/", ".");
        super.visit(version, access, name, signature, superName, interfaces);
    }
}
