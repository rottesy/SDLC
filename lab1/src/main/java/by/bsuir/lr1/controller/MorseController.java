package by.bsuir.lr1.controller;

import by.bsuir.lr1.model.MorseModel;
import by.bsuir.lr1.view.InputDialog;
import by.bsuir.lr1.view.MainFrame;
import by.bsuir.lr1.view.ViewActionHandler;

public class MorseController implements ViewActionHandler {
    private static final int MAX_LENGTH = 1000;
    private final MorseModel model;
    private final MainFrame view;

    public MorseController(MorseModel model, MainFrame view) {
        this.model = model;
        this.view = view;
    }

    @Override
    public void onEnterDataRequested() {
        InputDialog dialog = new InputDialog(view);
        if (model.getEncodedText() != null) dialog.setInputText(model.getEncodedText());
        dialog.setVisible(true);
        if (!dialog.isConfirmed()) return;
        try {
            model.setEncodedText(validate(dialog.getInputText()));
        } catch (IllegalArgumentException ex) {
            view.showError(ex.getMessage());
        }
    }

    static String validate(String text) {
        String value = text == null ? "" : text.trim();
        value = value.replace('–', '-').replace('—', '-');
        if (value.isEmpty()) throw new IllegalArgumentException("Сигналы не введены");
        if (value.length() > MAX_LENGTH) {
            throw new IllegalArgumentException("Ввод слишком длинный: не более " + MAX_LENGTH + " символов");
        }
        if (!value.matches("[.\\-/\\s]+")) {
            throw new IllegalArgumentException("Допустимы только точки, дефисы, пробелы и знак /");
        }
        return value;
    }
}
