package bot.model.query;

import java.util.Map;

/**
 * Created by Luca Mosetti on 2017
 * <p>
 * Standard CallbackQuery
 * All the bot CallbackQuery have to have the viaggia.command specified
 */
public class Query implements Regex {

    private Map<String, String> map;

    public Query(Map<String, String> map) {
        this.map = map;
    }

    protected String get(String key) {
        return map.get(key);
    }

    public Map<String,String> getMap() {
        return map;
    }

    public String getCommandIdentifier() {
        return get(COMMAND);
    }
}
