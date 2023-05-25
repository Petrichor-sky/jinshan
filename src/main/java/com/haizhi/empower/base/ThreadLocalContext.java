package com.haizhi.empower.base;

import java.util.HashMap;
import java.util.Map;

/**
 * 本地线程变量
 *
 * @author CristianWindy
 */
public class ThreadLocalContext {

    private static final ThreadLocal<Map<String, Object>> threadLocal = new ThreadLocal() {
        protected Map<String, Object> initialValue() {
            return new HashMap();
        }
    };

    /**
     * 获取key对应value
     *
     * @param key
     * @param <T>
     * @return
     */
    public static <T> T get(String key) {
        Map map = threadLocal.get();
        return (T) map.get(key);
    }

    /**
     * 获取key对应value，不存在则返回defaultValue
     *
     * @param key
     * @param defaultValue 默认值，不会存储
     * @param <T>
     * @return
     */
    public static <T> T get(String key, T defaultValue) {
        Map map = threadLocal.get();
        return (T) map.get(key) == null ? defaultValue : (T) map.get(key);
    }

    /**
     * 放入key-value
     *
     * @param key
     * @param value
     */
    public static void set(String key, Object value) {
        Map map = threadLocal.get();
        map.put(key, value);
    }

    /**
     * 将指定map追加到当前map的后面
     *
     * @param keyValueMap
     */
    public static void set(Map<String, Object> keyValueMap) {
        Map map = (Map) threadLocal.get();
        map.putAll(keyValueMap);
    }

    /**
     * 清空全部
     */
    public static void remove() {
        threadLocal.remove();
    }

    /**
     * 清空指定key-value
     *
     * @param key
     * @param <T>
     * @return
     */
    public static <T> T remove(String key) {
        Map map = (Map) threadLocal.get();
        return (T) map.remove(key);
    }

}
