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

    private int columns = 1;
    private ReplyKeyboardMarkup keyboardMarkup = new ReplyKeyboardMarkup();
    private List<KeyboardRow> keyboardRows = new ArrayList<>();

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

    public ReplyKeyboardMarkupBuilder setColumns(int columns) {
        this.columns = columns > MAX_COLUMNS ? MAX_COLUMNS : columns < 1 ? 1 : columns;
        return this;
    }

    public ReplyKeyboardMarkupBuilder setKeyboardButtons(List<String> textButtons) {

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

    public ReplyKeyboardMarkup build() throws EmptyKeyboardException {
        if (keyboardRows.isEmpty())
            throw new EmptyKeyboardException();

        ReplyKeyboardMarkup tmp = keyboardMarkup.setKeyboard(keyboardRows);
        clear();
        return tmp;
    }

    private void clear() {
        columns = 1;
        keyboardMarkup = new ReplyKeyboardMarkup();
        keyboardRows = new ArrayList<>();
    }
}
