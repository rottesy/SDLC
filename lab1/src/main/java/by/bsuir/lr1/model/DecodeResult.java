package by.bsuir.lr1.model;

public final class DecodeResult {
    private final String decodedText;
    private final int signalCount;
    private final int wordCount;

    public DecodeResult(String decodedText, int signalCount, int wordCount) {
        this.decodedText = decodedText;
        this.signalCount = signalCount;
        this.wordCount = wordCount;
    }

    public String getDecodedText() { return decodedText; }
    public int getSignalCount() { return signalCount; }
    public int getWordCount() { return wordCount; }
}
