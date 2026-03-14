
package org.cuiq.monitor.parameter;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.serializer.SerializerFeature;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class Log {
    private static final Logger log = LoggerFactory.getLogger(Log.class);
    private static final ThreadLocal<Map<String, Long>> timeProcess = ThreadLocal.withInitial(HashMap::new);
    private static final ThreadLocal<Map<Long, TrackText>> trackProcess = ThreadLocal.withInitial(HashMap::new);

    public Log() {
    }

    public static void startMethod(String className, String methodName, String descriptor) {
        String key = getKey(className, methodName, descriptor);
        ((Map)timeProcess.get()).put(key, System.currentTimeMillis());
        log.debug("→→→→ {}", className);
    }

    public static void endMethod(String className, String methodName, String descriptor) {
        String key = getKey(className, methodName, descriptor);
        long aLong = System.currentTimeMillis() - (Long)((Map)timeProcess.get()).get(key);
        log.debug("↑↑↑↑ [{}s] ← {}", aLong, className);
    }

    private static String getKey(String className, String methodName, String descriptor) {
        return className + methodName + descriptor;
    }

    public static void printSpendTime(String methodName, long timeIndex) {
        log.debug("MethodName:{} Time:{}s", methodName, (System.currentTimeMillis() - timeIndex) / 1000L);
    }

    public static void printObject(String argumentType, Object argumentValue, String track_id) {
        log.debug("[{}] [{}] [{}]", new Object[]{track_id, argumentType, "参数值: " + (argumentValue == null ? "null" : JSON.toJSONString(argumentValue, new SerializerFeature[]{SerializerFeature.WriteNonStringKeyAsString}))});
    }

    public static void printTrackText(long key) {
        TrackText trackText = (TrackText)((Map)trackProcess.get()).remove(key);
        log.debug("退出{}", trackText.getDescriptor());
        log.debug("--------------------{}-------------------- 耗时:{}ms", trackText.region, System.currentTimeMillis() - trackText.getStartTime());
    }

    public static long printTrackText(String className, String methodName, String methodDesc) {
        TrackText trackText = new TrackText();
        trackText.setDescriptor(String.format("-> Class:%s Method:%s sign:%s", className, methodName, methodDesc));
        trackText.setStartTime(System.currentTimeMillis());
        StringBuilder region = getTrackUid();
        long key = Long.parseLong(region.toString());
        ((Map)trackProcess.get()).put(key, trackText);
        log.debug("********************{}********************", region);
        log.debug("进入{}", trackText.getDescriptor());
        trackText.setRegion(region.toString());
        return key;
    }

    public static String printTrackText(String methodTrackText) {
        StringBuilder track_id = getTrackUid();
        log.debug("[{}] 进入:{}", track_id, methodTrackText);
        return track_id.toString();
    }

    public static void printTrackText(String track_id, String methodTrackText, long nanoTime) {
        log.debug("[{}] 耗时:{}ms", track_id, (System.nanoTime() - nanoTime) / 1000L);
        log.debug("[{}] 退出:{}", track_id, methodTrackText);
    }

    private static StringBuilder getTrackUid() {
        StringBuilder region = new StringBuilder();
        Random random = new Random();

        for(int i = 0; i < 10; ++i) {
            region.append(random.nextInt(99));
        }

        String format;
        if (region.length() > 15) {
            format = region.substring(0, 15);
            region.setLength(0);
            region.append(format);
        } else if (region.length() < 15) {
            format = String.format("%15s", region).replaceAll("\\s", "0");
            region.setLength(0);
            region.append(format);
        }

        return region;
    }

    static class TrackText {
        private String region;
        private String descriptor;
        private long startTime;

        public TrackText() {
        }

        public String getRegion() {
            return this.region;
        }

        public String getDescriptor() {
            return this.descriptor;
        }

        public long getStartTime() {
            return this.startTime;
        }

        public void setRegion(String region) {
            this.region = region;
        }

        public void setDescriptor(String descriptor) {
            this.descriptor = descriptor;
        }

        public void setStartTime(long startTime) {
            this.startTime = startTime;
        }

        public boolean equals(Object o) {
            if (o == this) {
                return true;
            } else if (!(o instanceof TrackText)) {
                return false;
            } else {
                TrackText other = (TrackText)o;
                if (!other.canEqual(this)) {
                    return false;
                } else if (this.getStartTime() != other.getStartTime()) {
                    return false;
                } else {
                    Object this$region = this.getRegion();
                    Object other$region = other.getRegion();
                    if (this$region == null) {
                        if (other$region != null) {
                            return false;
                        }
                    } else if (!this$region.equals(other$region)) {
                        return false;
                    }

                    Object this$descriptor = this.getDescriptor();
                    Object other$descriptor = other.getDescriptor();
                    if (this$descriptor == null) {
                        if (other$descriptor != null) {
                            return false;
                        }
                    } else if (!this$descriptor.equals(other$descriptor)) {
                        return false;
                    }

                    return true;
                }
            }
        }

        protected boolean canEqual(Object other) {
            return other instanceof TrackText;
        }

        public int hashCode() {
            int result = 1;
            long $startTime = this.getStartTime();
            result = result * 59 + (int)($startTime >>> 32 ^ $startTime);
            Object $region = this.getRegion();
            result = result * 59 + ($region == null ? 43 : $region.hashCode());
            Object $descriptor = this.getDescriptor();
            result = result * 59 + ($descriptor == null ? 43 : $descriptor.hashCode());
            return result;
        }

        public String toString() {
            return "Log.TrackText(region=" + this.getRegion() + ", descriptor=" + this.getDescriptor() + ", startTime=" + this.getStartTime() + ")";
        }
    }
}
