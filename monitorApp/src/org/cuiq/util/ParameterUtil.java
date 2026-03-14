package org.cuiq.util;

import java.io.File;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSON;

import pers.bc.utils.pub.LoggerUtil;
import pers.bc.utils.pub.StringUtil;

public class ParameterUtil
{
    private static final Logger log = LoggerFactory.getLogger(ParameterUtil.class);
    private static final DateFormat fm = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
    
    public static void printValueOnStack(boolean value)
    {
        System.out.println("    " + value);
        debug("    " + value);
    }
    
    public static void printValueOnStack(byte value)
    {
        System.out.println("    " + value);
        debug("    " + value);
    }
    
    public static void printValueOnStack(char value)
    {
        System.out.println("    " + value);
        debug("    " + value);
    }
    
    public static void printValueOnStack(short value)
    {
        System.out.println("    " + value);
        debug("    " + value);
    }
    
    public static void printValueOnStack(int value)
    {
        System.out.println("    " + value);
        debug("    " + value);
    }
    
    public static void printValueOnStack(float value)
    {
        System.out.println("    " + value);
        debug("    " + value);
    }
    
    public static void printValueOnStack(long value)
    {
        System.out.println("    " + value);
        debug("    " + value);
    }
    
    public static void printValueOnStack(double value)
    {
        System.out.println("    " + value);
        debug("    " + value);
    }
    
    public static void printValueOnStack(Object value)
    {
        if (value == null)
        {
            System.out.println("    " + null);
            debug("    " + null);
        }
        else if ((value instanceof String))
        {
            System.out.println("    " + value);
            debug("    " + value);
        }
        else if ((value instanceof Date))
        {
            System.out.println("    " + fm.format(value));
            debug("    " + value);
        }
        else if ((value instanceof char[]))
        {
            System.out.println("    " + Arrays.toString((char[]) value));
            debug("    " + Arrays.toString((char[]) value));
        }
        else
        {
            System.out.println("    " + value.getClass() + ": " + value);
            debug(value);
        }
    }
    
    public static void debug(Object str)
    {
        LoggerUtil.getInstance(LoggerUtil.getWorkLogPath()+ File.separator + "monitorlogs").info(StringUtil.toString(str));
        log.debug(JSON.toJSONString(str));
    }
    
    public static void printText(String str)
    {
        System.out.println(str);
        debug(str);
    }
}
