package com.soap.search.util;

/**
 * @author Soap
 * @Date 2025/5/22 23:51
 * @Version 1.0
 */
import org.apache.commons.beanutils.BeanUtils;
import java.util.*;

public class DeepCopyUtil {

    public static Map<String, Object> deepCopyMap(Map<String, Object> original) {
        Map<String, Object> copy = new HashMap<>();

        for (Map.Entry<String, Object> entry : original.entrySet()) {
            String key = entry.getKey();
            Object value = entry.getValue();

            if (value instanceof Map) {
                // 递归深拷贝嵌套 Map
                copy.put(key, deepCopyMap((Map<String, Object>) value));
            } else if (value instanceof List) {
                // 深拷贝 List
                copy.put(key, deepCopyList((List<?>) value));
            } else if (value != null && isPrimitive(value)) {
                // 基础类型直接复制
                copy.put(key, value);
            } else {
                // 对象类型尝试使用 BeanUtils 进行克隆（如果实现了 clone 或有 copy 构造器）
                try {
                    Object cloned = BeanUtils.cloneBean(value); // 可用于 Java Bean 克隆
                    copy.put(key, cloned);
                } catch (Exception e) {
                    throw new RuntimeException("Deep copy failed for object: " + value.getClass(), e);
                }
            }
        }

        return copy;
    }

    private static List<Object> deepCopyList(List<?> original) {
        List<Object> listCopy = new ArrayList<>();
        for (Object item : original) {
            if (item instanceof Map) {
                listCopy.add(deepCopyMap((Map<String, Object>) item));
            } else if (isPrimitive(item)) {
                listCopy.add(item);
            } else {
                try {
                    listCopy.add(BeanUtils.cloneBean(item));
                } catch (Exception e) {
                    throw new RuntimeException("Deep copy failed for list item: " + item.getClass(), e);
                }
            }
        }
        return listCopy;
    }

    private static boolean isPrimitive(Object obj) {
        return obj instanceof String || obj instanceof Number || obj instanceof Boolean || obj instanceof Character;
    }
}
