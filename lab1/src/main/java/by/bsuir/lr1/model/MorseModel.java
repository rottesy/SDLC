package by.bsuir.lr1.model;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class MorseModel {
    private static final Map<String, String> MORSE = Map.ofEntries(
        Map.entry(".-", "А"), Map.entry("-...", "Б"), Map.entry(".--", "В"),
        Map.entry("--.", "Г"), Map.entry("-..", "Д"), Map.entry(".", "Е"),
        Map.entry("...-", "Ж"), Map.entry("--..", "З"), Map.entry("..", "И"),
        Map.entry(".---", "Й"), Map.entry("-.-", "К"), Map.entry(".-..", "Л"),
        Map.entry("--", "М"), Map.entry("-.", "Н"), Map.entry("---", "О"),
        Map.entry(".--.", "П"), Map.entry(".-.", "Р"), Map.entry("...", "С"),
        Map.entry("-", "Т"), Map.entry("..-", "У"), Map.entry("..-.", "Ф"),
        Map.entry("....", "Х"), Map.entry("-.-.", "Ц"), Map.entry("---.", "Ч"),
        Map.entry("----", "Ш"), Map.entry("--.-", "Щ"), Map.entry("--.--", "Ъ"),
        Map.entry("-.--", "Ы"), Map.entry("-..-", "Ь"), Map.entry("..-..", "Э"),
        Map.entry("..--", "Ю"), Map.entry(".-.-", "Я"),
        Map.entry("-----", "0"), Map.entry(".----", "1"), Map.entry("..---", "2"),
        Map.entry("...--", "3"), Map.entry("....-", "4"), Map.entry(".....", "5"),
        Map.entry("-....", "6"), Map.entry("--...", "7"), Map.entry("---..", "8"),
        Map.entry("----.", "9"), Map.entry(".-.-.-", "."), Map.entry("--..--", ","),
        Map.entry("..--..", "?"), Map.entry("-....-", "-")
    );

    private String encodedText;
    private DecodeResult result;
    private final List<ModelListener> listeners = new ArrayList<>();

    public void addListener(ModelListener listener) { listeners.add(listener); }
    public void removeListener(ModelListener listener) { listeners.remove(listener); }

    private void fireModelChanged() {
        for (ModelListener listener : listeners) listener.modelChanged(this);
    }

    public String getEncodedText() { return encodedText; }
    public DecodeResult getResult() { return result; }

    public void setEncodedText(String encodedText) {
        DecodeResult newResult = decode(encodedText);
        this.encodedText = encodedText;
        this.result = newResult;
        fireModelChanged();
    }

    static DecodeResult decode(String encodedText) {
        String normalized = encodedText.trim().replaceAll("\\s*/\\s*", " / ");
        String[] tokens = normalized.split("\\s+");
        StringBuilder text = new StringBuilder();
        int signalCount = 0;
        int wordCount = 0;
        boolean inWord = false;

        for (int i = 0; i < tokens.length; i++) {
            String token = tokens[i];
            if ("/".equals(token)) {
                if (!inWord || i == tokens.length - 1) {
                    throw new IllegalArgumentException("Лишний разделитель слов в позиции " + (i + 1));
                }
                text.append(' ');
                inWord = false;
                continue;
            }
            String symbol = MORSE.get(token);
            if (symbol == null) {
                throw new IllegalArgumentException(
                    "Неизвестная последовательность Морзе: " + token + " (элемент " + (i + 1) + ")");
            }
            if (!inWord) {
                wordCount++;
                inWord = true;
            }
            text.append(symbol);
            signalCount++;
        }

        return new DecodeResult(text.toString(), signalCount, wordCount);
    }
}
