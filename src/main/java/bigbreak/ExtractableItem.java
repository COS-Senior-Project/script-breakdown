package bigbreak;

public abstract class ExtractableItem {
    protected int sceneNumberInt;
    protected String sceneNumber;
    protected String nameItem;
    protected String rule;
    protected String contextSnippet;
    protected double confidenceScore;

    public ExtractableItem(int sceneNumberInt, String sceneNumber, String nameItem, String rule, String contextSnippet, double confidenceScore){
        this.sceneNumberInt = sceneNumberInt;
        this.sceneNumber = sceneNumber;
        this.nameItem = nameItem;
        this.rule = rule;
        this.contextSnippet = contextSnippet;
        this.confidenceScore = confidenceScore;
    }

    //getters and setters
    public int getSceneNumberInt() {
        return sceneNumberInt;
    }

    public void setSceneNumberInt(int sceneNumberInt) {
        this.sceneNumberInt = sceneNumberInt;
    }

    public String getSceneNumber() {
        return sceneNumber;
    }

    public String getNameItem(){
        return nameItem;
    }

    public void setNameItem(String nameItem){
        this.nameItem = nameItem;
    }

    public void setRule(String rule) { this.rule = rule; }

    public String getRule() {
        return rule;
    }

    public void setSceneNumber(String sceneNumber) {
        this.sceneNumber = sceneNumber;
    }

    public String getContextSnippet() {
        return contextSnippet;
    }

    public void setContextSnippet(String contextSnippet) {
        this.contextSnippet = contextSnippet;
    }

    public double getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(double confidenceScore) {
        this.confidenceScore = confidenceScore;
    }
}
