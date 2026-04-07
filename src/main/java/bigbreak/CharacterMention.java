package bigbreak;

public class CharacterMention {
    private final String rawName;
    private final int startToken;
    private final int endToken;
    private final double confidence;

    private String canonicalName;

    //class to store post-training characters
    public CharacterMention(String rawName, int startToken, int endToken, double confidence) {
        this.rawName = rawName;
        this.startToken = startToken;
        this.endToken = endToken;
        this.confidence = confidence;
    }

    public String getRawName() {
        return rawName;
    }

    public int getStartToken() {
        return startToken;
    }

    public int getEndToken() {
        return endToken;
    }

    public double getConfidence() {
        return confidence;
    }

    public void setCanonicalName (String canonicalName) {
        this.canonicalName = canonicalName;
    }

    public String getCanonicalName() {
        return canonicalName;
    }

    @Override
    public String toString() {
        return rawName + " [" + startToken + ":" + endToken + "]";
    }
}
