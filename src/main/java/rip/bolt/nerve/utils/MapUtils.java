package rip.bolt.nerve.utils;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

public class MapUtils {

    public static Map<?, ?> merge(Map<?, ?> original, Map<?, ?> toAdd) {
        Map<Object, Object> result = new HashMap<Object, Object>(original);
        for (Entry<?, ?> entry : toAdd.entrySet()) {
            Object originalEntry = original.get(entry.getKey());
            if (originalEntry != null && originalEntry instanceof Map<?, ?>)
                result.put(entry.getKey(), merge((Map<?, ?>) originalEntry, (Map<?, ?>) entry.getValue()));
            else
                result.put(entry.getKey(), entry.getValue());

        }

        return result;
    }

}
