package bigbreak;

public class Character extends ExtractableItem {
    private String canonicalName;
    public Character (int sceneNumberInt, String sceneNumber, String nameItem, String rule,
                      String contextSnippet, double confidenceScore) {
        super(sceneNumberInt, sceneNumber, nameItem, rule, contextSnippet, confidenceScore);
    }
    public void setCanonicalName(String canonicalName) { this.canonicalName = canonicalName; }
    public String getCanonicalName() { return canonicalName; }
}
