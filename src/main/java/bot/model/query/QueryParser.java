package bot.model.query;

import java.util.HashMap;
import java.util.Map;

/**
 * Created by Luca Mosetti on 2017
 * <p>
 * Standard QueryParser
 * All the QueryParser use the same REGEX to split the CallbackQuery
 */
public class QueryParser implements Regex {

    public Query parse(String string) {
        return new Query(extractMap(string));
    }

    private Map<String, String> extractMap(String string) {
        Map<String, String> map = new HashMap<>();
        String[] parts = string.split(REGEX);

        for (String entry : parts) {
            map.put((entry.split(TWO_DOTS)[0]), entry.split(TWO_DOTS)[1]);
        }

        return map;
    }
}
