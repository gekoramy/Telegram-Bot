package bot.keyboard;

import bot.exception.EmptyKeyboardException;
import com.google.common.collect.Lists;
import org.telegram.telegrambots.api.objects.replykeyboard.InlineKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.InlineKeyboardButton;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * Created by Luca Mosetti on 2017
 * <p>
 * It builds InlineKeyboardMarkup
 * Each InlineKeyboardButton has a "label" and a Query
 */
public class InlineKeyboardMarkupBuilder {

    /**
     * Each row of the ReplyKeyboardMarkup can have between 1 and 7 buttons
     */
    private final static int MAX_COLUMNS = 7;

    private int columns = 5;
    private InlineKeyboardMarkup inlineKeyboardMarkup = new InlineKeyboardMarkup();
    private List<List<InlineKeyboardButton>> keyboardRows = new ArrayList<>();

    public InlineKeyboardMarkupBuilder setColumns(int columns) {
        this.columns = columns > MAX_COLUMNS ? MAX_COLUMNS : columns < 1 ? 1 : columns;
        return this;
    }

    public InlineKeyboardMarkupBuilder addSeparateRowsKeyboardButtons(List<Map.Entry<String, String>> entryButtons) {

        List<List<Map.Entry<String, String>>> rows = Lists.partition(entryButtons, columns);

        for (List<Map.Entry<String, String>> row : rows) {
            List<InlineKeyboardButton> keyboardRow = new ArrayList<>();
            for (Map.Entry<String, String> entry : row) {
                InlineKeyboardButton button = new InlineKeyboardButton();
                button.setText(entry.getKey());
                button.setCallbackData(entry.getValue());
                keyboardRow.add(button);
            }
            keyboardRows.add(keyboardRow);
        }

        return this;
    }

    public InlineKeyboardMarkupBuilder addFullRowUrlInlineButton(String text, String url) {
        List<InlineKeyboardButton> keyboardRow = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setUrl(url);
        keyboardRow.add(button);

        keyboardRows.add(keyboardRow);

        return this;
    }

    public InlineKeyboardMarkupBuilder addFullRowInlineButton(String text, String callbackData) {
        List<InlineKeyboardButton> keyboardRow = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setCallbackData(callbackData);
        keyboardRow.add(button);

        keyboardRows.add(keyboardRow);

        return this;
    }

    public InlineKeyboardMarkupBuilder addFullRowSwitchInlineButton(String text, String callbackData) {
        List<InlineKeyboardButton> keyboardRow = new ArrayList<>();
        InlineKeyboardButton button = new InlineKeyboardButton();
        button.setText(text);
        button.setSwitchInlineQuery(callbackData);
        keyboardRow.add(button);

        keyboardRows.add(keyboardRow);

        return this;
    }

    public InlineKeyboardMarkup build() throws EmptyKeyboardException {
        if (keyboardRows.isEmpty())
            throw new EmptyKeyboardException();

        InlineKeyboardMarkup tmp = inlineKeyboardMarkup.setKeyboard(keyboardRows);
        clear();
        return tmp;
    }

    private void clear() {
        columns = 5;
        inlineKeyboardMarkup = new InlineKeyboardMarkup();
        keyboardRows = new ArrayList<>();
    }
}
