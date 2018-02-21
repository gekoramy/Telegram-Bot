package gekoramy.telegram.bot.model.query;

import java.util.Map;

/**
 * Standard CallbackQuery
 * In order to work properly, all the CallbackQueries must specify the command
 *
 * @author Luca Mosetti
 * @since 2017
 */
public class Query implements Regex {

    private Map<String, String> map;

    public Query(Map<String, String> map) {
        this.map = map;
    }

    protected String get(String key) {
        return map.get(key);
    }

    public Map<String, String> getMap() {
        return map;
    }

    public String getCommandIdentifier() {
        return get(COMMAND);
    }
}
