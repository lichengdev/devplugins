package org.cuiq.monitor.jdbc;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.sql.CallableStatement;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import java.util.stream.Stream;
import org.cuiq.agen.ClassTransformer;

public class MyJdbcProxy
{
    public static final String SQL_PROXY_FIELD = "sql$agent";
    public static final String PARAMS_PROXY_FIELD = "params$agent";
    private static final Set<String> EXECUTE_METHODS =
        (Set) Stream.of("execute", "executeQuery", "executeUpdate", "executeBatch").collect(Collectors.toSet());
    private static final Set<String> SET_PARAMETER_METHODS =
        (Set) Stream.of(PreparedStatement.class.getDeclaredMethods()).filter((method) -> {
            return method.getName().startsWith("set");
        }).filter((method) -> {
            return method.getParameterCount() > 1;
        }).map(Method::getName).collect(Collectors.toSet());
    private static final boolean inJar = "org.springframework.boot.loader.LaunchedURLClassLoader"
        .equals(Thread.currentThread().getContextClassLoader().getClass().getName());
    private static boolean shibai = false;
    
    public MyJdbcProxy()
    {
    }
    
    private static String proxySetSql(Object[] args, Object result) throws Exception
    {
        String sql = (String) args[0];
        Field sqlField = result.getClass().getDeclaredField("sql$agent");
        sqlField.setAccessible(true);
        sqlField.set(result, sql);
        return sql;
    }
    
    private static Map<Integer, Object> proxySetParam(Object[] args, PreparedStatement statement) throws Exception
    {
        int pos = (Integer) args[0];
        Object value = args[1];
        Field paramsField = statement.getClass().getDeclaredField("params$agent");
        paramsField.setAccessible(true);
        Map<Integer, Object> paramMap = (Map) paramsField.get(statement);
        if (paramMap == null)
        {
            paramMap = new HashMap();
            paramsField.set(statement, paramMap);
        }
        
        ((Map) paramMap).put(pos, value);
        return (Map) paramMap;
    }
    
    public static void jdbcLog(String sql, Map<Integer, Object> paramMap, String dbType, long execTime)
    {
        if (!"SELECT 1 FROM DUAL".equals(sql) && sql != null)
        {
            int maxParamIndex = (Integer) Optional.ofNullable(paramMap).filter((map) -> {
                return !map.isEmpty();
            }).flatMap((map) -> {
                return map.keySet().stream().max(Integer::compareTo);
            }).orElse(-1);
            String params = (String) IntStream.range(0, maxParamIndex).mapToObj((i) -> {
                Object param = ((Map) Objects.requireNonNull(paramMap)).get(i + 1);
                return param instanceof String ? "'" + param + "'" : String.valueOf(param);
            }).collect(Collectors.joining(", ", "[", "]"));
            Log.info(params, sql, execTime);
        }
    }
    
    private static String getDbType(Statement statement) throws SQLException
    {
        return statement.getConnection().getMetaData().getDatabaseProductName().toLowerCase();
    }
    
    private static Object proxyStatement(Statement statement, Method method, Object[] args) throws Exception
    {
        call_dbms_application_info(method, statement);
        Result result = new Result((Object) null, 0L);
        boolean var9 = false;
        
        try
        {
            var9 = true;
            result = getResult(statement, method, args);
            var9 = false;
        }
        catch (Exception var10)
        {
            Exception e = var10;
            e.printStackTrace(System.err);
            throw e;
        }
        finally
        {
            if (var9)
            {
                if (EXECUTE_METHODS.contains(method.getName()))
                {
                    String sql = (String) args[0];
                    jdbcLog(sql, Collections.emptyMap(), getDbType(statement), result.time);
                }
                
            }
        }
        
        if (EXECUTE_METHODS.contains(method.getName()))
        {
            String sql = (String) args[0];
            jdbcLog(sql, Collections.emptyMap(), getDbType(statement), result.time);
        }
        
        return result.value;
    }
    
    private static Object proxyPreparedStatement(PreparedStatement statement, Method method, Object[] args) throws Exception
    {
        Result result = new Result("", 0L);
        boolean var15 = false;
        
        try
        {
            var15 = true;
            result = getResult(statement, method, args);
            var15 = false;
        }
        catch (Exception var16)
        {
            Exception e = var16;
            e.printStackTrace(System.err);
            throw e;
        }
        finally
        {
            if (var15)
            {
                if (EXECUTE_METHODS.contains(method.getName()))
                {
                    Field sqlField = statement.getClass().getDeclaredField("sql$agent");
                    sqlField.setAccessible(true);
                    String sql = (String) sqlField.get(statement);
                    Field paramsField = statement.getClass().getDeclaredField("params$agent");
                    paramsField.setAccessible(true);
                    Map<Integer, Object> paramMap = (Map) paramsField.get(statement);
                    jdbcLog(sql, paramMap, getDbType(statement), result.time);
                }
                
            }
        }
        
        if (EXECUTE_METHODS.contains(method.getName()))
        {
            Field sqlField = statement.getClass().getDeclaredField("sql$agent");
            sqlField.setAccessible(true);
            String sql = (String) sqlField.get(statement);
            Field paramsField = statement.getClass().getDeclaredField("params$agent");
            paramsField.setAccessible(true);
            Map<Integer, Object> paramMap = (Map) paramsField.get(statement);
            jdbcLog(sql, paramMap, getDbType(statement), result.time);
        }
        
        return result.value;
    }
    
    private static void call_dbms_application_info(Method method, Statement statement) throws SQLException
    {
        if (EXECUTE_METHODS.contains(method.getName()))
        {
            Object transactionid = getTransactionid(statement);
            String thread = Thread.currentThread().getName();
            StackTraceElement[] stackTrace = Thread.currentThread().getStackTrace();
            String action = "???";
            StackTraceElement[] var6 = stackTrace;
            int var7 = stackTrace.length;
            
            for (int var8 = 0; var8 < var7; ++var8)
            {
                StackTraceElement element = var6[var8];
                String[] var10 = ClassTransformer.packages;
                int var11 = var10.length;
                
                for (int var12 = 0; var12 < var11; ++var12)
                {
                    String aPackage = var10[var12];
                    if (!aPackage.isEmpty() && element.getClassName().startsWith(aPackage) && element.getFileName() != null)
                    {
                        action =
                            element.getFileName().split("\\.")[0] + "." + element.getMethodName() + "[" + element.getLineNumber() + "]";
                        CallableStatement prepareCall = statement.getConnection().prepareCall(
                            String.format("call dbms_application_info.SET_MODULE('%s %s','%s')", thread, transactionid, action));
                        prepareCall.execute();
                        prepareCall.close();
                        return;
                    }
                }
            }
            
        }
    }
    
    private static Object getTransactionid(Statement statement)
    {
        Object transactionid = "???";
        if (shibai)
        {
            return transactionid;
        }
        else
        {
            Throwable e;
            if (inJar)
            {
                try
                {
                    transactionid = set_client_info_in_jar(statement);
                }
                catch (Throwable var4)
                {
                    e = var4;
                    shibai = true;
                    e.printStackTrace(System.err);
                }
            }
            
            return transactionid;
        }
    }
    
    private static Object set_client_info_in_jar(Statement statement) throws Exception
    {
        Class<?> ThreadTransactionId = null;
        
        try
        {
            ThreadTransactionId = Thread.currentThread().getContextClassLoader().loadClass("com.xisoft.security.ThreadTransactionId");
        }
        catch (ClassNotFoundException var13)
        {
            ThreadTransactionId =
                Thread.currentThread().getContextClassLoader().loadClass("com.xisoft.core.tdata.core.ThreadTransactionId");
        }
        
        Class<?> o = null;
        Method get = ThreadTransactionId.getMethod("get", (Class) o);
        Object transactionid = get.invoke(ThreadTransactionId, o);
        Class<?> CurrentUser = Thread.currentThread().getContextClassLoader().loadClass("com.xisoft.security.CurrentUser");
        Method getUserName = CurrentUser.getMethod("getUserName", (Class) o);
        Method getUserCode = CurrentUser.getMethod("getUserCode", (Class) o);
        Method getIP = CurrentUser.getMethod("getIP", (Class) o);
        Object name = getUserName.invoke(CurrentUser, o);
        Object code = getUserCode.invoke(CurrentUser, o);
        Object ip = getIP.invoke(CurrentUser, o);
        String format = String.format("call dbms_application_info.SET_CLIENT_INFO('%s %s[%s]')", ip, name, code);
        prepareCall(statement, format);
        return transactionid;
    }
    
    private static void prepareCall(Statement statement, String format) throws Exception
    {
        Connection connection = statement.getConnection();
        CallableStatement prepareCall = connection.prepareCall(format);
        prepareCall.execute();
        prepareCall.close();
    }
    
    private static Result getResult(Statement statement, Method method, Object[] args) throws Exception
    {
        long nanoTime = System.nanoTime();
        Object o = method.invoke(statement, args);
        long l = (System.nanoTime() - nanoTime) / 1000L / 1000L;
        return new Result(o, l);
    }
    
    public static Connection proxyConnect(Connection connection)
    {
        if (connection == null)
        {
            return null;
        }
        else
        {
            ClassLoader classLoader = MyJdbcProxy.class.getClassLoader();
            InvocationHandler invocationHandler = getInvocationHandler(connection, classLoader);
            Object o = Proxy.newProxyInstance(classLoader, new Class[]{Connection.class}, invocationHandler);
            return (Connection) o;
        }
    }
    
    private static InvocationHandler getInvocationHandler(Connection connection, ClassLoader classLoader)
    {
        return (proxy, connMethod, connArgs) -> {
            String methodName = connMethod.getName();
            Object result = connMethod.invoke(connection, connArgs);
            if ("prepareStatement".equals(methodName))
            {
                String sql = proxySetSql(connArgs, result);
                return getNewProxyInstance(classLoader, (PreparedStatement) result, sql);
            }
            else
            {
                return "createStatement".equals(methodName)
                    ? Proxy.newProxyInstance(classLoader, new Class[]{Statement.class}, (target, stmtMethod, stmtArgs) -> {
                        Statement statement = (Statement) result;
                        return proxyStatement(statement, stmtMethod, stmtArgs);
                    }) : result;
            }
        };
    }
    
    private static Object getNewProxyInstance(ClassLoader classLoader, PreparedStatement result, String sql)
    {
        return Proxy.newProxyInstance(classLoader, new Class[]{PreparedStatement.class}, (target, stmtMethod, stmtArgs) -> {
            if (SET_PARAMETER_METHODS.contains(stmtMethod.getName()))
            {
                Map<Integer, Object> paramMap = proxySetParam(stmtArgs, result);
                
                try
                {
                    return stmtMethod.invoke(result, stmtArgs);
                }
                catch (Exception var7)
                {
                    Exception e = var7;
                    jdbcLog(sql, paramMap, getDbType(result), -1L);
                    throw e;
                }
            }
            else
            {
                return stmtArgs != null && stmtArgs.length != 0 ? proxyStatement(result, stmtMethod, stmtArgs)
                    : proxyPreparedStatement(result, stmtMethod, stmtArgs);
            }
        });
    }
}
