package pers.bc.utils.pub;

import java.io.File;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;

import pers.bc.utils.constant.IPubEvnCons;
import pers.bc.utils.file.FileUtilbc;

/**
 * 简单的日志保存操作
 *
 * @fileRelativePath: LoggerUtil.java
 * @Date: Created on 2021/11/25 13:46 <br>
 * @author: LiBencheng<br>
 * @version: 1.0<br>
 */
public final class LoggerUtil extends LoggerAbs
{

    private static ThreadLocal<LoggerUtil> loggerUtil = new ThreadLocal<LoggerUtil>();
    
    private static final long serialVersionUID = -4291438837102849653L;
    public final int STACKTRACE = 1;
    private volatile Boolean IS_LOGS = true;
    private volatile String folderName = null;
    private volatile String fileUrl = null;
    private volatile String FileDirectory;
    public static String className = "com.xisoft.hr.utils.LoggerUtil";
    public static String LEVEL_INFO = "INFO";
    public static String LEVEL_ERROR = "ERROR";
    public static String LEVEL_WARN = "WARN";
    public static String LEVEL_DEBUG = "DEBUG";
    
    public static void debugFormat(Boolean isDebug, String msg, Object... objs) { debug(isDebug, String.format(msg, objs)); }
    
    public static void debugFormat(String msg, Object... objs) { LoggerUtil.getInstance().debug(String.format(msg, objs)); }
    
    public static void debug(Object msg)  { debug(Boolean.TRUE, msg); }
    
    public static void debug(Boolean isDebug, Object msg)
    {
        if (isDebug)
        {
            System.out.println(" System.out.println(Debug调试日志--------->>> " + msg);
            LoggerUtil.getInstance().debug("Debug调试日志--------->" + StringUtil.toString(msg));
        }
    }
    
    public static void thr(Throwable th)  { LoggerUtil.getInstance().exception(th); }
    
    public static void thr(String msg, Throwable th) { LoggerUtil.getInstance().exception(msg, th); }
    
    public String getFileDirectory()  { return FileDirectory; }
    
    public void setFileDirectory(String fileDirectory) {  FileDirectory = fileDirectory; }
    
    // 默认值必须为false，中间件刚启动时，尚未配置System.getProperty("debug")，导致异常信息输出到控制台
    public static Boolean SYS_DEBUG = false;
    
    static
    {
        strDocTempletDirPath = getWorkLogPath() + File.separator + "temp" + File.separator;
        System.setProperty("logger", className);
        String strDebug = System.getProperty("debug");
        if (!PubEnvUtil.isEmpty(strDebug))
        {
            SYS_DEBUG = Boolean.valueOf(strDebug).booleanValue();
        }
    }
    
    /**
     * 堆栈信息
     */
    public void consoleout(Object msg)
    {
        String str = null;
        try
        {
            str = StringUtil.toString(msg);
        }
        catch (Exception e)
        {
        }
        System.err.println(str);
    }
    
    /**
     * *********************************************************** <br>
     * 说明：指定文件前缀，debug输出日志 <br>
     *
     * @param msg
     * @param filePrefix <br>
     * @void <br>
     * @methods pers.bc.utils.pub.LoggerUtilbc#debugAppointPrefix <br>
     * @author LiBencheng <br>
     * @date Created on 2020-12-18 <br>
     * @time 下午4:12:26 <br>
     * @version 1.0 <br>
     *          ************************************************************ <br>
     * @see <br>
     */
    public final void debugAppointPrefix(String msg, String filePrefix)  { exeSavaLogger(msg, false, filePrefix, LEVEL_ERROR); }
    
    public final void debug(String msg, String str)  { debug(msg + RN + str); }
    
    /**
     * *********************************************************** <br>
     * 说明： debug保存日志信息<br>
     *
     * @param msg <br>
     * @void <br>
     * @methods nc.pub.itf.tools.pub.LoggerUtils#debug <br>
     * @author licheng <br>
     * @date Created on 2019-10-31 <br>
     * @time 上午10:18:10 <br>
     * @version 1.0 <br>
     *          ************************************************************ <br>
     * @see <br>
     */
    public final void debug(String msg)  { exeSavaLogger(msg, false, "dbug", LEVEL_INFO); }
    
    /**
     * *********************************************************** <br>
     * 说明：指定文件前缀，info输出日志 <br>
     *
     * @param msg
     * @param filePrefix <br>
     * @void <br>
     * @methods pers.bc.utils.pub.LoggerUtilbc#debugAppointPrefix <br>
     * @author LiBencheng <br>
     * @date Created on 2020-12-18 <br>
     * @time 下午4:12:26 <br>
     * @version 1.0 <br>
     *          ************************************************************ <br>
     * @see <br>
     */
    public final void infoAppointPrefix(String msg, String filePrefix) {  exeSavaLogger(msg, false, filePrefix, LEVEL_INFO); }
    
    public final void info(String msg, String str) {  info(msg + RN + str); }
    
    /**
     * *********************************************************** <br>
     * 说明：info保存日志信息 <br>
     *
     * @param msg <br>
     * @void <br>
     * @methods pers.bc.utils.pub.LoggerUtils#info <br>
     * @author LiBencheng <br>
     * @date Created on 2020-6-2 <br>
     * @time 下午2:43:02 <br>
     * @version 1.0 <br>
     *          ************************************************************ <br>
     * @see <br>
     */
    public final void info(String msg) { exeSavaLogger(msg, false, "info", LEVEL_INFO); }
    
    /**
     * *********************************************************** <br>
     * 说明：指定文件前缀，error输出日志 <br>
     *
     * @param msg
     * @param filePrefix <br>
     * @void <br>
     * @methods pers.bc.utils.pub.LoggerUtilbc#debugAppointPrefix <br>
     * @author LiBencheng <br>
     * @date Created on 2020-12-18 <br>
     * @time 下午4:12:26 <br>
     * @version 1.0 <br>
     *          ************************************************************ <br>
     * @see <br>
     */
    public final void errorAppointPrefix(String msg, String filePrefix) { exeSavaLogger(msg, false, filePrefix, LEVEL_ERROR); }
    
    /**
     * *********************************************************** <br>
     * 说明：错误日志保存 <br>
     *
     * @param msg <br>
     * @void <br>
     * @methods nc.pub.itf.tools.pub.LoggerUtils#error <br>
     * @author licheng <br>
     * @date Created on 2019-10-31 <br>
     * @time 上午10:17:16 <br>
     * @version 1.0 <br>
     *          ************************************************************ <br>
     * @see <br>
     */
    public final void error(String msg, String str)  { error(msg + RN + str); }
    
    /**
     * *********************************************************** <br>
     * 说明：错误日志保存 <br>
     *
     * @param msg <br>
     * @void <br>
     * @methods nc.pub.itf.tools.pub.LoggerUtils#error <br>
     * @author licheng <br>
     * @date Created on 2019-10-31 <br>
     * @time 上午10:17:16 <br>
     * @version 1.0 <br>
     *          ************************************************************ <br>
     * @see <br>
     */
    public final void error(String msg)
    {
        consoleout(msg);
        exeSavaLogger(msg, false, "error", LEVEL_ERROR);
    }
    
    /**
     * *********************************************************** <br>
     * 说明：异常日志保存 <br>
     *
     * @param th <br>
     * @void <br>
     * @methods nc.pub.itf.tools.pub.LoggerUtils#errExpLogger <br>
     * @author licheng <br>
     * @date Created on 2019-9-10 <br>
     * @time 下午12:00:27 <br>
     * @version 1.0 <br>
     *          ************************************************************ <br>
     * @see <br>
     */
    public final void exception(Throwable th) { exception(getInvokMethod(2), "程序遇到致命错误", th);  }
    
    /**
     * *********************************************************** <br>
     * 说明：debug异常日志保存 <br>
     *
     * @param msg
     * @param th <br>
     * @void <br>
     * @methods pers.bc.utils.pub.LoggerUtilbc#exception <br>
     * @author LiBencheng <br>
     * @date Created on 2020-11-8 <br>
     * @time 上午1:37:35 <br>
     * @version 1.0 <br>
     *          ************************************************************ <br>
     * @see <br>
     */
    public final void exception(String msg, Throwable th)  {  exception(getInvokMethod(2), msg, th); }
    
    public final void exception(String method, String msg, Throwable th)
    {
        debug("程序异常结束{[" + msg + "]}");
        debug(getSplitLine());
        savaLogErr(method, String.valueOf(th) + msg + "{[" + th.getMessage() + "]}");
        consoleout(method + String.valueOf(th) + msg + "{[" + th.getMessage() + "]}");
        savaLogger(method, msg, th);
    }
    
    /**
     * *********************************************************** <br>
     * 说明：保存详细日志
     *
     * @param method
     * @param info
     * @void
     * @author licheng
     * @date Created on 2019-8-1
     * @time 上午10:36:12
     * @version 1.0 <br>
     *          ************************************************************ <br>
     * @see
     */
    public void savaLogInfo(String method, String info) { exeSavaLogger(method, info, null, "loginfo", LEVEL_INFO); }
    
    /**
     * *********************************************************** <br>
     * 说明：保存错误信息日志
     *
     * @param method
     * @param err
     * @void
     * @author licheng
     * @date Created on 2019-8-1
     * @time 上午10:36:23
     * @version 1.0 <br>
     *          ************************************************************ <br>
     * @see
     */
    public final void savaLogErr(String method, String err) { savaLogger(method, "程序错误警告", err);}
    
    /**
     * *********************************************************** <br>
     * 说明：保存错误信息日志，和详细日志
     *
     * @param method
     * @param json
     * @param err
     * @void
     * @author licheng
     * @date Created on 2019-8-1
     * @time 上午10:36:16
     * @version 1.0 <br>
     *          ************************************************************ <br>
     * @see
     */
    public void savaLogger(String method, String json, String err)  {  exeSavaLogger(method, json, err, "err", LEVEL_ERROR); }
    
    /**
     * *********************************************************** <br>
     * 说明： 打印堆栈信息的日志
     *
     * @param method
     * @param th
     * @void
     * @author licheng
     * @date Created on 2019-8-1
     * @time 上午10:37:37
     * @version 1.0 <br>
     *          ************************************************************ <br>
     * @see
     */
    public final void savaLogger(String method, Throwable th)
    {
        consoleout(th);
        String msg = "";
        if (null != th)
        {  msg = th.getMessage(); }
        
        savaLogger(method, msg, th);
    }
    
    /**
     * *********************************************************** <br>
     * 说明： 打印堆栈信息的日志 ，和接受到的日志
     *
     * @param method
     * @param receiveStr
     * @param th
     * @void
     * @author licheng
     * @date Created on 2019-8-1
     * @time 上午10:36:30
     * @version 1.0 <br>
     *          ************************************************************ <br>
     * @see
     */
    public final void savaLogger(String method, String receiveStr, Throwable th) {  exeSavaLogger(method, receiveStr, StringUtil.toString(th), "thw", LEVEL_ERROR); }
    
    /*************************************************************
     * <br>
     *
     * 说明: <br>
     *
     * @Description <br>
     * @Date Created on 2022/6/23 22:43 <br>
     * @param msg
     * @param isErrExp
     * @param fileFix
     * @param level <br>
     * @return void <br>
     * @Author LIBENCHENG
     * @version 1.0 <br>
     *************************************************************          <br>
     */
    private void exeSavaLogger(String msg, boolean isErrExp, String fileFix, String level)
    {
        try
        {
            if (!IS_LOGS)
            {  return; }
           
            String fileName = new SimpleDateFormat(IPubEvnCons.DATE_FORMAT).format(new Date());
            String format = new SimpleDateFormat(IPubEvnCons.DATETIME_FORMAT2).format(new Date());
            String filePath = getLogsFilePatch(isErrExp, fileFix, fileName);
            setFileDirectory(filePath);
            File file = new File(filePath);
            fileName = file.getName();
            
            StringBuffer msgStr = getBeautyWomanStr();
            // msgStr = getXisoftStr();
            if (!PubEnvUtil.isEmptyObj(System.getProperty("nc.server.location"))
                && !PubEnvUtil.equals("develop", System.getProperty("nc.runMode")))
            {  msgStr = getYonYouStr(); }
            
            // if (PubEnvUtil.equals(level,LEVEL_INFO)) {msgStr.append(getBeautyWomanStr());}
            if (PubEnvUtil.equals(level, LEVEL_INFO))  { msgStr.append(getKeyBoardStr()); }
            if (PubEnvUtil.equals(level, LEVEL_ERROR)) { msgStr.append(getDangerStr()); }
            
            msgStr.append("Runing time：").append(format).append(" ").append(level).append(" -->>> ").append(msg).append(CRLF);
            if (!file.exists())
            {
                FileUtilbc.createFiles(filePath);
                FileUtilbc.write(file, msgStr.toString(), UTF_8);
            }
            else
            {
                // 文件大于 6.49MB新建文件，
                // 可以自己修改比较文件大小参数11475968f=10.94MB,
                // 6808055f =6.49MB ；
                // 5013547f =4.78MB；
                // 12623539f =12.04MB
                if (5013547f < file.length())
                {
                    // checkFile(filePath, fileName);
                    FileUtilbc.backToFile(filePath, fileName);
                    // 没有占用才能删除
                    // file.delete();
                    FileUtilbc.write(file, msgStr.toString(), UTF_8);
                    return;
                }
                msgStr = new StringBuffer();
                msgStr.append("Runing time：").append(format).append(" ").append(level).append(" -->>> ").append(msg).append(CRLF);
                FileUtilbc.writeAppend(file, msgStr.toString(), UTF_8);
            }
        }
        catch (IOException e)
        {
            System.err.println(StringUtil.toString(e));
        }
    }
    
    /**
     * *********************************************************** <br>
     * 说明： 执行日志保存， 支持linux客服端<br>
     *
     * @param method
     * @param json
     * @param othMsg
     * @param fileFix
     * @void <br>
     * @methods pers.bc.utils.pub.LoggerUtilbc#exeSavaLogger <br>
     * @author LiBencheng <br>
     * @date Created on 2020-11-10 <br>
     * @time 下午4:17:05 <br>
     * @version 1.0 <br>
     *          ************************************************************ <br>
     * @see <br>
     */
    private void exeSavaLogger(String method, String json, String othMsg, String fileFix, String level)
    {
        try
        {
            if (!IS_LOGS)  {  return; }
            
            String fileName = new SimpleDateFormat(IPubEvnCons.DATE_FORMAT).format(new Date());
            String format = new SimpleDateFormat(IPubEvnCons.DATETIME_FORMAT2).format(new Date());
            String filePath = getLogsFilePatch(true, fileFix, fileName);
            setFileDirectory(filePath);
            File file = new File(filePath);
            fileName = file.getName();
            
            StringBuffer msgStr = getBeautyWomanStr();
            msgStr.append(getSystemInfo());
            // msgStr = getXisoftStr();
            if (!PubEnvUtil.isEmptyObj(System.getProperty("nc.server.location"))
                && !PubEnvUtil.equals("develop", System.getProperty("nc.runMode")))  { msgStr = getYonYouStr(); }
            
            if (PubEnvUtil.equals(level, LEVEL_ERROR)) {  msgStr.append(getThrowStr()); }
            if (PubEnvUtil.equals(level, LEVEL_WARN)) {  msgStr.append(getDangerStr()); }
            if (PubEnvUtil.equals(level, LEVEL_INFO)) {  msgStr.append(getOfficeStr()); }
            
            msgStr.append(" \uD83D\uDC1E VM memory usage details: ").append(CRLF).append("        ※ ").append(printMemory()).append(CRLF);
            msgStr.append("Runing time：").append(format).append(CRLF);
            msgStr.append("Runing method：").append(method).append(CRLF);
            msgStr.append("Receive  details：").append(json).append(CRLF);
            msgStr.append("Other details：").append(othMsg).append(CRLF);
            msgStr.append(getSplitLine()).append(CRLF);
            if (!file.exists())
            {
                FileUtilbc.createFiles(filePath);
                FileUtilbc.write(file, msgStr.toString(), UTF_8);
            }
            else
            {
                // 文件大于 6.49MB新建文件，
                // 可以自己修改比较文件大小参数11475968f=10.94MB,
                // 6808055f =6.49MB ；
                // 5013547f =4.78MB；
                // 12623539f =12.04MB
                if (5013547f < file.length())
                {
                    // checkFile(filePath, fileName);
                    FileUtilbc.backToFile(filePath, fileName);
                    // 没有占用才能删除
                    // file.delete();
                    FileUtilbc.write(file, msgStr.toString(), UTF_8);
                    return;
                }
                msgStr = new StringBuffer();
                msgStr.append("VM memory usage details: ").append(CRLF).append("        ※ ").append(printMemory()).append(CRLF);
                msgStr.append("Runing time：").append(format).append(CRLF);
                msgStr.append("Runing method：").append(method).append(CRLF);
                msgStr.append("Receive  details：").append(json).append(CRLF);
                msgStr.append("Other details：").append(othMsg).append(CRLF);
                msgStr.append(getSplitLine()).append(RN);
                FileUtilbc.writeAppend(file, msgStr.toString(), UTF_8);
            }
        }
        catch (IOException e)
        {
            System.err.println(StringUtil.toString(e));
        }
    }
    
    /**
     * *********************************************************** <br>
     * 说明： 打印内存信息
     *
     * @return
     * @String
     * @author licheng
     * @date Created on 2019-8-2
     * @time 下午3:36:16
     * @version 1.0 <br>
     *          ************************************************************ <br>
     * @see
     */
    public String printMemory()
    {
        final Runtime rt = Runtime.getRuntime();
        final long freeMemory = rt.freeMemory();
        final long totalMemory = rt.totalMemory();
        final StringBuilder buf = new StringBuilder(64);
        
        buf.append("FREE_MEMORY: ");
        buf.append(freeMemory / 1024);
        buf.append("KB(");
        buf.append(freeMemory / 1024 / 1024);
        buf.append("M)  TOTAL_MEMORY: ");
        buf.append(totalMemory / 1024);
        buf.append("KB(");
        buf.append(totalMemory / 1024 / 1024);
        buf.append("M)  FREE_RATE：");
        
        long hundredths = (freeMemory * 10000) / totalMemory;
        
        buf.append(hundredths / 100);
        hundredths %= 100;
        if (hundredths >= 10)   {  buf.append('.'); }
        else  {  buf.append(".0");  }
        buf.append(hundredths);
        buf.append('%');
        String log = buf.toString();
        return log;
    }
    
    /**
     * *********************************************************** <br>
     * 说明：日志存储目录 <br>
     *
     * @param isErrExp
     * @param fileFix
     * @param fileName
     * @return <br>
     * @String <br>
     * @methods pers.bc.utils.pub.LoggerUtilbc#getLogsFilePatch <br>
     * @author LiBencheng <br>
     * @date Created on 2020-10-13 <br>
     * @time 上午9:58:36 <br>
     * @version 1.0 <br>
     *          ************************************************************ <br>
     * @see <br>
     */
    public String getLogsFilePatch(boolean isErrExp, String fileFix, String fileName)
    {
        if (null == folderName)
        {
            if (!isErrExp) {  folderName = "buglogs";  }
            if (isErrExp)  { folderName = "errlogs";  }
        }
        
        StringBuffer logFilePath = new StringBuffer();
        logFilePath.append(getWorkLogPath());
        logFilePath.append(File.separator);
        if (!PubEnvUtil.isEmpty(getFileUrl()))
        {
            logFilePath.append(getFileUrl());
            logFilePath.append(File.separator);
        }
        logFilePath.append(folderName);
        logFilePath.append(File.separator);
        logFilePath.append(fileFix);
        logFilePath.append(fileName);
        logFilePath.append(".log");
        
        return logFilePath.toString();
    }
    
    /**
     * *********************************************************** <br>
     * 说明： 获取当前項目工程工作日志路径<br>
     *
     * @return <br>
     * @String <br>
     * @methods pers.bc.utils.pub.LoggerUtilbc#getWorkPath <br>
     * @author LiBencheng <br>
     * @date Created on 2020-10-13 <br>
     * @time 上午9:57:05 <br>
     * @version 1.0 <br>
     *          ************************************************************ <br>
     * @see <br>
     */
    public static String getWorkLogPath()
    {
        String patch = FileUtilbc.getWorkServerPath();
        // nc.server.location
        if (PubEnvUtil.isNotEmptyObj(System.getProperty("nc.server.location")))
        {  patch = patch + File.separator + "nclogs";  }
        else  patch += File.separator + "mylogs";
        
        return patch;
    }
    
    public static String strDocTempletDirPath;
    
    /**
     * *********************************************************** <br>
     * 说明： <br>
     *
     * @param strFileName 文件名
     * @param suffix 文件扩展名
     * @return <br>
     * @String <br>
     * @methods pers.bc.utils.pub.LoggerUtilbc#getFullFileName <br>
     * @author LiBencheng <br>
     * @date Created on 2021-3-20 <br>
     * @time 下午11:47:56 <br>
     * @version 1.0 <br>
     *          ************************************************************ <br>
     * @see <br>
     */
    public static String getFullFileName(String strFileName, String suffix) { return getFullFileName(strFileName + suffix); }
    
    /**
     * *********************************************************** <br>
     * 说明： <br>
     *
     * @param strFileName 文件名
     * @return <br>
     * @String <br>
     * @methods pers.bc.utils.pub.LoggerUtilbc#getFullFileName <br>
     * @author LiBencheng <br>
     * @date Created on 2021-3-20 <br>
     * @time 下午11:47:23 <br>
     * @version 1.0 <br>
     *          ************************************************************ <br>
     * @see <br>
     */
    public static String getFullFileName(String strFileName)
    {
        
        if (StringUtil.isBlank(strFileName))
        { strFileName = String.valueOf(System.currentTimeMillis()); }
        
        FileUtilbc.createFiles(strDocTempletDirPath);
        
        return strDocTempletDirPath + "templet_" + strFileName + ".png";
        
    }
    
    /**
     * *********************************************************** <br>
     * 说明：获取当前调用方法 -- 上一层调用方法
     *
     * @return
     * @String
     * @author licheng
     * @date Created on 2019-8-2
     * @time 下午3:47:53
     * @version 1.0 <br>
     *          ************************************************************ <br>
     * @see
     */
    public final String getInvokMethod()  { return getInvokMethod(1); }

    // 说明：获取当前调用方法 -- 上一层调用方法
    public final static String getInvokMethodUpStep()  { return getInvokMethod(1); }
    
    // 说明：获取当前调用方法 -- 当前调用方法
    public final static String getInvokMethodCurrent()  { return getInvokMethod(0); }
    // 获取所有的堆栈
    public final static String getInvokMethodAll()  { return getInvokMethod(0,-1); }
    
    /**
     * *********************************************************** <br>
     * 说明：获取当前调用方法
     *
     * @param i
     * @return
     * @String
     * @author licheng
     * @date Created on 2019-7-25
     * @time 下午10:31:59
     * @version 1.0 <br>
     * ************************************************************ <br>
     * @see
     */
    public final static String getInvokMethod(int i)  { return getInvokMethod(i + 2, 0 + 2 + i); }
    
    /**
     * *********************************************************** <br>
     * *说明： 获取当前调用方法的栈信息<br>
     * @see <br>
     * @param startIndex 开始下标
     * @param endIndex  结束下标
     * @return <br>
     * @String <br>
     * @methods pers.bc.utils.pub.LoggerUtil#getInvokMethod <br>
     * @author LiBencheng <br>
     * @date Created on 2020-3-1 <br>
     * @time 上午11:31:40 <br>
     * @version 1.0 <br>
     ************************************************************* <br>
     */
    public final static String getInvokMethod(int startIndex, int endIndex)
    {
        JudgeAssertUtil.checkAssert(startIndex < 0 || (startIndex > endIndex && endIndex != -1),
            "The starting index of the array must be less than the ending index" + " and the starting index cannot be less than 0！");
        StackTraceElement[] stackTrace = new Throwable().getStackTrace();
        if(startIndex==stackTrace.length)startIndex = startIndex-1;
        StackTraceElement stackTraceElement = stackTrace[startIndex];
        if(endIndex > stackTrace.length || endIndex == -1) endIndex = stackTrace.length;
        Boolean isEquals = (startIndex == endIndex);
        StringBuilder log = new StringBuilder();
        StackTraceElement[] copyStackTrace = Arrays.copyOfRange(stackTrace, startIndex, endIndex);
        for (int i = 0; i < copyStackTrace.length; i++) 
        {log.append("      at ").append(copyStackTrace[i]).append(RN);}
        
        return isEquals ? stackTraceElement.toString() : log.toString();
    }
    
    public String getFolderName() { return folderName; }
    
    public void setFolderName(String folderName) { this.folderName = folderName; }
    
    public void setIS_LOGS(boolean isflag) { IS_LOGS = isflag; }
    
    public String getFileUrl() { return fileUrl;  }
    
    public void setFileUrl(String fileUrl) { this.fileUrl = fileUrl;  }
    
    public boolean isIS_LOGS() { return IS_LOGS; }

    public static LoggerUtil getInstance2() {
        LoggerUtil df = loggerUtil.get();
        //if (null == df) {
            synchronized (LoggerUtil.class) {
                if (null == df) {
                    df = new LoggerUtil();
                    loggerUtil.set(df);
                }
            }
        //}
        return df;
    }
    /**
     * *********************************************************** <br>
     * 说明：
     *
     * @methods nc.pub.itf.tools.pub.LoggerUtils#构造方法
     * @author licheng
     * @date Created on 2019-8-12
     * @time 上午10:20:09
     * @version 1.0 <br>
     *          ************************************************************ <br>
     * @see
     */
    @Deprecated
    private LoggerUtil()  { }
    
    /*************************************************************
     * <br>
     *
     * 说明: <br>
     *
     * @Description <br>
     * @Date Created on 2021/11/25 14:25 <br>
     * @param fileUrl
     * @param folderName <br>
     * @return <br>
     * @Author LIBENCHENG
     * @version 1.0 <br>
     *************************************************************          <br>
     */
    @Deprecated
    private LoggerUtil(String fileUrl, String folderName)
    {
        this.fileUrl = fileUrl;
        this.folderName = folderName;
    }
    
    /*************************************************************
     * <br>
     *
     * 说明: <br>
     *
     * @Description <br>
     * @Date Created on 2021/11/25 14:25 <br>
     * @param folderName <br>
     * @return <br>
     * @Author LIBENCHENG
     * @version 1.0 <br>
     *************************************************************          <br>
     */
    @Deprecated
    private LoggerUtil(String folderName) { this.folderName = folderName; }
    
    private static class InnerInstance
    {
        // private static final LoggerUtilbc logger = new LoggerUtilbc();
        private static volatile LoggerUtil logger = new LoggerUtil();
        
        // 懶漢式
        public static LoggerUtil getInstance()
        {
            if (null == logger) { synchronized (InnerInstance.class) { if (null == logger) {  logger = new LoggerUtil(); } } }
            
            return logger;
        }

        public static LoggerUtil getInstanceSafe(String fileUrl, String folderName) { synchronized (InnerInstance.class) {return new LoggerUtil(fileUrl, folderName);} }
        
        public static LoggerUtil getInstanceSafe(String folderName) { synchronized (InnerInstance.class) {return new LoggerUtil(folderName);} }

        public static LoggerUtil getInstance(String fileUrl, String folderName) {{return new LoggerUtil(fileUrl, folderName);} }
        
        public static LoggerUtil getInstance(String folderName) {{return new LoggerUtil(folderName);} }
        
    }
    
    /*************************************************************
     * <br>
     *
     * 说明: <br>
     *
     * @Description <br>
     * @Date Created on 2021/11/25 18:50 <br>
     * @return com.xisoft.hr.utils.LoggerUtil <br>
     * @Author LIBENCHENG
     * @version 1.0 <br>
     *************************************************************          <br>
     */
    public static LoggerUtil getInstance() { return InnerInstance.logger; }
    
    public static LoggerUtil getInstanceSafe(String folderName) {if(PubEnvUtil.isEmpty(folderName)) return InnerInstance.logger; return InnerInstance.getInstanceSafe(folderName); }
    
    public static LoggerUtil getInstanceSafe(String fileUrl, String folderName) { return InnerInstance.getInstanceSafe(fileUrl, folderName); }

    public static LoggerUtil getInstance(String folderName) {if(PubEnvUtil.isEmpty(folderName)) return InnerInstance.logger; return InnerInstance.getInstance(folderName); }
    
    public static LoggerUtil getInstance(String fileUrl, String folderName) { return InnerInstance.getInstance(fileUrl, folderName); }
    
    public static void main2(String[] args)
    {
        
        // System.err.println(LoggerUtilbc.getThrowStr());
        // System.err.println(LoggerUtilbc.getBeautyWomanStr());
        // System.err.println(LoggerUtilbc.getWomanStr());
        // System.err.println(LoggerUtilbc.getDangerStr());
        // System.err.println(LoggerUtilbc.getKeyBoardStr());
        // System.err.println(LoggerUtilbc.getOfficeStr());
        // System.err.println(LoggerUtilbc.getYONStr());
        // System.err.println(LoggerUtilbc.getYonYouStr());
        // System.err.println(LoggerUtilbc.getMsgStr());
        // System.err.println(LoggerUtilbc.getSystemInfoStr());
        
    }
}
