package javadoc;

import com.google.gson.GsonBuilder;
import dev.utils.JCLogUtils;
import dev.utils.common.ArrayUtils;
import dev.utils.common.MapUtils;
import dev.utils.common.StringUtils;

import java.io.File;
import java.util.*;

/**
 * detail: 内部工具类
 * @author Ttt
 */
public final class Utils {

    private Utils() {
    }

    static {
        JCLogUtils.setPrintLog(true);
        JCLogUtils.setControlPrintLog(true);
//        JCLogUtils.setPrint(new JCLogUtils.Print() {
//            @Override
//            public void printLog(int logType, String tag, String message) {
//                System.out.println(tag + " : " + message);
//            }
//        });
    }

    /**
     * 获取文件列表
     * @param path 文件路径
     * @return 文件列表
     */
    public static List<File> getFileLists(final String path) {
        List<File> lists = new ArrayList<>();
        // 获取文件路径
        File baseFile = new File(path);
        // 获取子文件
        File[] files = baseFile.listFiles();
        for (File file : files) {
            // 属于文件才处理
            if (file.isFile()) {
                lists.add(file);
            }
        }
        return lists;
    }

    /**
     * 获取文件目录列表
     * @param path 文件路径
     * @return 文件目录列表
     */
    public static List<File> getFileCatalogLists(final String path) {
        List<File> lists = new ArrayList<>();
        try {
            // 获取文件路径
            File baseFile = new File(path);
            // 获取子文件
            File[] files = baseFile.listFiles();
            for (File file : files) {
                lists.add(file);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return lists;
    }

    /**
     * HashMap 排序
     * @param map {@link Map}
     * @return 排序后的 Map
     */
    public static Map<String, List<String>> sortHashMap(final Map<String, List<String>> map) {
        Map<String, List<String>> sortedMap = new LinkedHashMap<>();
        List<String> list = new ArrayList<>();
        Iterator<String> item = map.keySet().iterator();
        while (item.hasNext()) {
            list.add(item.next());
        }
        Collections.sort(list);
        Iterator<String> item2 = list.iterator();
        while (item2.hasNext()) {
            String key = item2.next();
            sortedMap.put(key, map.get(key));
        }
        return sortedMap;
    }

    /**
     * HashSet 排序
     * @param set {@link Set}
     * @return 排序后的 Set
     */
    public static Set<String> sortHashSet(final Set<String> set) {
        List<String> list = new ArrayList<>();
        Iterator<String> item = set.iterator();
        while (item.hasNext()) {
            list.add(item.next());
        }
        Collections.sort(list);
        return new LinkedHashSet<>(list);
    }

    /**
     * 生成 Map 字符串
     * @param map     {@link HashMap}
     * @param mapName map 变量名
     * @return 生成指定格式字符串
     */
    public static String generateMapString(final HashMap<String, List<String>> map, final String mapName) {
        StringBuilder builder = new StringBuilder();
        // HashMap 排序
        Map<String, List<String>> sortHashMap = sortHashMap(map);
        // 空格前缀
        String space = "        ";
        // 格式化字符串
        String format = space + "%s.put(\"%s\", Utils.asListArgs(%s));";
        // 循环处理
        for (String className : sortHashMap.keySet()) {
            List<String> lists = sortHashMap.get(className);
            // 格式化追加
            builder.append(String.format(format, mapName, className,
                    ArrayUtils.appendToString(lists.toArray(new String[]{}))));
            builder.append(StringUtils.NEW_LINE_STR);
        }
        // 用于生成 Config 特殊处理
        builder.delete(0, space.length());
        return builder.toString();
    }

    /**
     * 生成 Set 字符串
     * @param set     {@link HashSet}
     * @param mapName map 变量名
     * @return 生成指定格式字符串
     */
    public static String generateSetString(final Set<String> set, final String mapName) {
        StringBuilder builder = new StringBuilder();
        // HashMap 排序
        Set<String> sortHashMap = sortHashSet(set);
        // 空格前缀
        String space = "        ";
        // 格式化字符串
        String format = space + "%s.remove(\"%s\");";
        // 循环处理
        for (String className : sortHashMap) {
            // 格式化追加
            builder.append(String.format(format, mapName, className));
            builder.append(StringUtils.NEW_LINE_STR);
        }
        // 用于生成 Config 特殊处理
        builder.delete(0, space.length());
        return builder.toString();
    }

    // ========
    // = Gson =
    // ========

    /**
     * 创建 GsonBuilder
     * @param serializeNulls 是否序列化null值
     * @return {@link GsonBuilder}
     */
    private static GsonBuilder createGson(final boolean serializeNulls) {
        final GsonBuilder builder = new GsonBuilder();
        if (serializeNulls) builder.serializeNulls();
        return builder;
    }

    /**
     * 转换 JSON 格式数据, 并且格式化
     * @param data         待转换对象
     * @param includeNulls 是否序列化null值
     * @return 格式化 JSON 数据
     */
    public static String toJsonFormat(final Object data, final boolean includeNulls) {
        if (data != null) {
            try {
                // 返回 JSON格式数据 - 格式化
                return createGson(includeNulls).setPrettyPrinting().create().toJson(data);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    // =====================
    // = 临时等 DevJava 发包 =
    // =====================

    /**
     * 转换数组为集合
     * @param array 数组
     * @param <T>   泛型
     * @return {@link List<T>}
     */
    public static <T> List<T> asListArgs(final T... array) {
        if (array != null) {
            try {
                return new ArrayList<>(Arrays.asList(array));
            } catch (Exception e) {
                JCLogUtils.eTag(ArrayUtils.class.getSimpleName(), e, "asListArgs");
            }
        }
        return null;
    }

    /**
     * 添加一条数据
     * @param map   待添加 {@link Map}
     * @param key   key
     * @param value value, add to list
     * @param <K>   key
     * @param <T>   value type
     * @return {@code true} success, {@code false} fail
     */
    public static <K, T> boolean putToList(final Map<K, List<T>> map, final K key, final T value) {
        return putToList(map, key, value, true);
    }

    /**
     * 添加一条数据
     * @param map   {@link Map}
     * @param key   key
     * @param value value, add to list
     * @param isNew 当指定 (key) 的 value 为 null, 是否创建
     * @param <K>   key
     * @param <T>   value type
     * @return {@code true} success, {@code false} fail
     */
    public static <K, T> boolean putToList(final Map<K, List<T>> map, final K key, final T value, final boolean isNew) {
        if (map != null) {
            if (map.containsKey(key)) {
                List<T> lists = map.get(key);
                if (lists != null) {
                    try {
                        lists.add(value);
                        map.put(key, lists);
                        return true;
                    } catch (Exception e) {
                        JCLogUtils.eTag(MapUtils.class.getSimpleName(), e, "putToList");
                    }
                }
            } else {
                // 判断是否创建
                if (isNew) {
                    try {
                        List<T> lists = new ArrayList<>();
                        lists.add(value);
                        map.put(key, lists);
                        return true;
                    } catch (Exception e) {
                        JCLogUtils.eTag(MapUtils.class.getSimpleName(), e, "putToList");
                    }
                }
            }
        }
        return false;
    }

    /**
     * 移除多条数据 ( 通过 Map 进行移除 )
     * @param map       {@link Map}
     * @param removeMap {@link Map} 移除对比数据源
     * @param <K>       key
     * @param <T>       value type
     * @return {@code true} success, {@code false} fail
     */
    public static <K, T> boolean removeToMap(final Map<K, List<T>> map, final Map<K, List<T>> removeMap) {
        return removeToMap(map, removeMap, true, false);
    }

    /**
     * 移除多条数据 ( 通过 Map 进行移除 )
     * @param map             {@link Map}
     * @param removeMap       {@link Map} 移除对比数据源
     * @param removeEmpty     是否移除 null、长度为 0 的数据
     * @param isNullRemoveAll 如果待移除的 List 是 null, 是否移除全部
     * @param <K>             key
     * @param <T>             value type
     * @return {@code true} success, {@code false} fail
     */
    public static <K, T> boolean removeToMap(final Map<K, List<T>> map, final Map<K, List<T>> removeMap,
                                             final boolean removeEmpty, final boolean isNullRemoveAll) {
        if (map != null && removeMap != null) {
            Iterator<Map.Entry<K, List<T>>> iterator = removeMap.entrySet().iterator();
            while (iterator.hasNext()) {
                Map.Entry<K, List<T>> entry = iterator.next();
                // 获取 key
                K key = entry.getKey();
                // 进行移除处理
                if (map.containsKey(key)) {
                    List<T> value = entry.getValue();
                    try {
                        if (value != null) {
                            map.get(key).removeAll(value);
                        } else {
                            if (isNullRemoveAll) {
                                map.remove(key);
                            }
                        }
                    } catch (Exception e) {
                        JCLogUtils.eTag(MapUtils.class.getSimpleName(), e, "removeToMap - removeAll");
                    }
                    // 判断是否移除 null、长度为 0 的数据
                    if (removeEmpty) {
                        List<T> lists = map.get(key);
                        try {
                            // 不存在数据了, 则移除
                            if (lists == null || lists.size() == 0) {
                                map.remove(key);
                            }
                        } catch (Exception e) {
                            JCLogUtils.eTag(MapUtils.class.getSimpleName(), e, "removeToMap");
                        }
                    }
                }
            }
            return true;
        }
        return false;
    }
}