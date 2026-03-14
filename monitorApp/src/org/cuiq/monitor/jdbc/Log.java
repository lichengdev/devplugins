package org.cuiq.monitor.jdbc;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import org.cuiq.util.ColourLogUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log {
   // private static final Logger log = LoggerFactory.getLogger(Log.class);

    public Log() {
    }

    public static void info(String params, String sql, long execTime) {
        StringBuilder stacktrace = new StringBuilder();
        StackTraceElement[] var5 = Thread.currentThread().getStackTrace();
        int var6 = var5.length;

        for(int var7 = 0; var7 < var6; ++var7) {
            StackTraceElement element = var5[var7];
            String className = element.getClassName();
            if (className.startsWith("com.xisoft.")) {
                stacktrace.append(element).append("\n");
            } else {
                boolean b = className.startsWith("sun.reflect.");
                boolean b1 = className.startsWith("com.sun.");
                boolean b2 = className.startsWith("java.");
                boolean b3 = className.startsWith("org.");
                if (!b && !b1 && !b2 && !b3) {
                    stacktrace.append(element).append("\n");
                }
            }
        }

        sql = sql.replaceAll("\\s+", " ");
        String[] split = sql.trim().split("\\s+", 2);
        String sqlText = String.format("%sms %s %s %s", ColourLogUtil.redText(execTime, true), ColourLogUtil.greenText(params, true), ColourLogUtil.redText(split[0], true), ColourLogUtil.greenText(split[1], true));
        System.out.println(sqlText);
        sqlText = sqlText.replaceAll("%", "[OOxxOO]");
        String sqlLog = String.format("\n%s[stacktrace]\n%s", sqlText, stacktrace);
        sqlLog = sqlLog.replaceAll("\\[OOxxOO]", "%");
        String msg = sqlLog.replaceAll("\u001b\\[0m", "").replaceAll("\u001b\\[3(.)m", "").replaceAll("\u001b\\[3(.);1m", "");
        //log.debug(msg);

        try {
            Class<?> logClazz = Class.forName("pers.bc.utils.pub.LoggerUtil");
            Method getInstance = logClazz.getMethod("getInstance", String.class);
            Object object = getInstance.invoke((Object)null, "monitorlogs");
            Method maLog = object.getClass().getMethod("info", String.class);
            maLog.invoke(object, msg);
        } catch (NoSuchMethodException | IllegalAccessException | InvocationTargetException | ClassNotFoundException var14) {
            var14.printStackTrace(System.err);
        }

    }
}
