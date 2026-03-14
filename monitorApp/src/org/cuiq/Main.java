package org.cuiq;

import java.io.PrintWriter;
import jdk.internal.org.objectweb.asm.ClassReader;
import jdk.internal.org.objectweb.asm.util.ASMifier;
import jdk.internal.org.objectweb.asm.util.Printer;
import jdk.internal.org.objectweb.asm.util.TraceClassVisitor;
import org.cuiq.util.FileUtil;

public class Main
{
    public static void main(String[] args)
    {
        int parsingOptions = 6;
        String filePath = FileUtil.getFilePath(Main.class, "org.cuiq.Test");
        print(parsingOptions, filePath);
    }
    
    private static void print(int parsingOptions, String filePath)
    {
        Printer printer = new ASMifier();
        PrintWriter printWriter = new PrintWriter(System.out, true);
        TraceClassVisitor traceClassVisitor = new TraceClassVisitor(null, printer, printWriter);
        byte[] bytes = FileUtil.readBytes(filePath);
        new ClassReader(bytes).accept(traceClassVisitor, parsingOptions);
    }
}
