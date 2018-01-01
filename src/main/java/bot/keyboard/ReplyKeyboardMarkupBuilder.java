package bot.keyboard;

import bot.exception.EmptyKeyboardException;
import com.google.common.collect.Lists;
import org.telegram.telegrambots.api.objects.replykeyboard.ReplyKeyboardMarkup;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardButton;
import org.telegram.telegrambots.api.objects.replykeyboard.buttons.KeyboardRow;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Luca Mosetti on 2017
 * <p>
 * It builds ReplyKeyboardMarkup
 */
public class ReplyKeyboardMarkupBuilder {

    /**
     * Each row of the ReplyKeyboardMarkup can have between 1 and 12 buttons
     */
    private final static int MAX_COLUMNS = 12;

    private ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
    private List<KeyboardRow> keyboardRows = new ArrayList<>();

    public ReplyKeyboardMarkupBuilder reset() {
        keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardRows = new ArrayList<>();

        return this;
    }

    public ReplyKeyboardMarkupBuilder setOneTimeKeyboard(Boolean oneTimeKeyboard) {
        keyboardMarkup.setOneTimeKeyboard(oneTimeKeyboard);
        return this;
    }

    public ReplyKeyboardMarkupBuilder setSelective(Boolean selective) {
        keyboardMarkup.setSelective(selective);
        return this;
    }

    public ReplyKeyboardMarkupBuilder setResizeKeyboard(Boolean resizeKeyboard) {
        keyboardMarkup.setResizeKeyboard(resizeKeyboard);
        return this;
    }

    public ReplyKeyboardMarkupBuilder addKeyboardRow(KeyboardRow row) {
        keyboardRows.add(row);
        return this;
    }

    public ReplyKeyboardMarkupBuilder addKeyboardButtons(int columns, List<String> textButtons) {
        columns = columns > MAX_COLUMNS ? MAX_COLUMNS : columns < 1 ? 1 : columns;
        List<List<String>> rows = Lists.partition(textButtons, columns);

        for (List<String> row : rows) {
            KeyboardRow keyboardRow = new KeyboardRow();
            for (String s : row) {
                KeyboardButton keyboardButton = new KeyboardButton(s);
                keyboardRow.add(keyboardButton);
            }
            keyboardRows.add(keyboardRow);
        }

        return this;
    }

    public ReplyKeyboardMarkupBuilder addRequestLocationButton() {
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton("\uD83D\uDCCD").setRequestLocation(true));
        keyboardRows.add(keyboardRow);

        return this;
    }

    public ReplyKeyboardMarkupBuilder addFullRowButton(String textButton) {
        KeyboardRow keyboardRow = new KeyboardRow();
        keyboardRow.add(new KeyboardButton(textButton));
        keyboardRows.add(keyboardRow);

        return this;
    }

    public ReplyKeyboardMarkup build(boolean reset) throws EmptyKeyboardException {
        if (keyboardRows.isEmpty())
            throw new EmptyKeyboardException();

        ReplyKeyboardMarkup tmp = keyboardMarkup.setKeyboard(keyboardRows);

        if (reset) reset();
        return tmp;
    }
}
