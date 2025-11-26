import java.util.Map;

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

    //abstract methods

    //non-abstract methods

    //returns a string in OpenNLP training format
    public String bootstrappingObjects(String classLabel) {
        //tokenizes the text around the nameItem
        String[] tokens = TextUnits.tokenize(this.contextSnippet);
        //string builder to store the training line at the end
        StringBuilder sb = new StringBuilder();
        //splits the name in separate words if more than one word
        String[] nameWords = nameItem.toLowerCase()
                .replaceAll("[^a-zA-Z\\.'’\\-]", " ")
                .replaceAll("\\.", " .")
                .replaceAll("\\s+", " ")
                //splits on space, dash, or apostrophe
                //keeps the dash/apostrophe as an array value
                //it makes working with - or ' names possible
                .trim().split("(?=['\\-])|(?<=['\\-])|\\s+");
        //the number of words in the character's name
        int nameWordCount = nameWords.length;
        //index to walk through tokens
        int i = 0;
        //loops through all tokens of the snippet
        while (i < tokens.length) {
            //will check if the current position of i matches the full name
            boolean match = true;
            //checks if there are enough tokens left for the name
            if (i + nameWordCount <= tokens.length) {
                //loops through each token of the name
                for (int j = 0; j < nameWordCount; j++) {
                    System.out.println(tokens[i+j]);
                    //normalizes the token to lowercase and removes punctuation
                    String cleaned = tokens[i + j].toLowerCase().replaceAll("[^a-zA-Z\\.'’\\-]", "");
                    //if any of the character name words, doesn't match this position of the snippet
                    //break out the name loop and move to the next snippet token
                    System.out.println("Clean: " + cleaned + "  NameWords[j]: " + nameWords[j]);
                    if (!cleaned.equals(nameWords[j])) {
                        match = false;
                        break;
                    }
                }
            } else { //if the tokens are not enough, no match
                match = false;
            }
            //if the match is found
            if (match) {
                //inserts <START:LABEL>
                sb.append("<START:").append(classLabel).append("> ");

                //appends the actual name tokens
                for (int j = 0; j < nameWordCount; j++) {
                    if (j > 0) sb.append(" ");
                    sb.append(tokens[i + j]);
                }

                //appends <END>
                sb.append(" <END> ");
                //skips past the name
                i += nameWordCount;
            }
            else { //if there is no match
                //appends the current token to the result snippet
                sb.append(tokens[i]).append(" ");
                //moves to the next snippet token
                i++;
            }
        }
        //returns the final training line
        return sb.toString().replace("/", "\n").trim();
    }

    /*
    @Override
    public String toString(){
        return "Extractable Item:" +
                "\nname = " + nameItem +
                "\nscene order number (integer) = " + sceneNumberInt +
                "\nscene script number (String) = " + sceneNumber +
                "\ncontext snippet = " + contextSnippet +
                "\nconfidence score = " + confidenceScore;
    }
     */
}
