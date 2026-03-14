//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.cuiq;

import java.io.File;
import java.lang.reflect.Method;

public class Test extends RuntimeException
{
    public Test()
    {
    }
    
    private void filterMenuByAuthAndEnableModule()
    {
        System.err.println("------------");
    }
    
    public static void main(String[] args)
    {
        try
        {

            Class<?> logClazz = Class.forName("pers.bc.utils.pub.LoggerUtil");
            Method getInstance = logClazz.getMethod("getInstance", String.class);
            Object object = getInstance.invoke((Object)null, "monitorlogs");
            Method maLog = object.getClass().getMethod("info", String.class);
            maLog.invoke(object,"ppp");
//            
//            Class<?> logClazz = Class.forName("pers.bc.utils.pub.LoggerUtil");
//            Method getInstance = logClazz.getMethod("getInstance", String.class);
//            Object object = getInstance.invoke(null, "monitorlogs");//文件夹名称
//            Method maLog = object.getClass().getMethod("info", String.class);//文件名
//            maLog.invoke(object, "123456");
        }
        catch (Throwable e)
        {
            System.err.println(e);
        }
    }
}
