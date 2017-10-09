package bot.model.query;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Luca Mosetti on 2017
 */
public class QueryBuilder implements Regex {
    private Map<String, String> map = new HashMap<>();
    private static final int MAX_BYTES = 64;

    protected QueryBuilder put(String key, String value) {
        if (build().getBytes().length >= MAX_BYTES)
            throw new OutOfMemoryError("callback_data cannot exceed " + MAX_BYTES + " bytes of memory");

        map.put(key, value);
        return this;
    }

    private String build() {
        StringBuilder result = new StringBuilder();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            result.append(entry.getKey()).append(TWO_DOTS).append(entry.getValue()).append(REGEX);
        }
        return result.toString();
    }

    public String build(boolean clear) {
        String tmp = build();

        if (tmp.getBytes().length >= MAX_BYTES)
            throw new OutOfMemoryError("callback_data cannot exceed " + MAX_BYTES + " bytes of memory\n" + tmp.getBytes().length + " Bytes : " + tmp);

        if (clear)
            map.clear();

        return tmp;
    }
}

