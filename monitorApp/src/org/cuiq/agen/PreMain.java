//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.cuiq.agen;

import java.io.IOException;
import java.lang.instrument.Instrumentation;

public class PreMain {
    public static ClassTransformer TRANSFORMER;

    public PreMain() {
    }

    public static void premain(String agentArgs, Instrumentation inst) throws IOException {
        System.out.println("Premain.....");
        TRANSFORMER = new ClassTransformer(agentArgs, inst);
        inst.addTransformer(TRANSFORMER);
    }
}
