package org.cuiq.monitor.jdbc;

import java.util.Map;

public interface SqlReplace {
    String replace(String var1, Map<Integer, Object> var2, String var3) throws Exception;
}
