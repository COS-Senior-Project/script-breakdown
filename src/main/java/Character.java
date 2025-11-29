import java.io.BufferedWriter;
import java.io.IOException;
import java.util.List;
import java.util.Locale;

public class Character extends ExtractableItem {

    private final NameDatabase nameDb;
    public Character (int sceneNumberInt, String sceneNumber, String nameItem, String rule,
                      String contextSnippet, double confidenceScore, NameDatabase nameDb) {
        super(sceneNumberInt, sceneNumber, nameItem, rule, contextSnippet, confidenceScore);
        this.nameDb = nameDb;
    }

    public String toCSVRow() {
        String snippetSafe = contextSnippet.replaceAll("\\s+", " ").replace("\"", "\"\"");
        return sceneNumberInt + "," +
                sceneNumber + ",\"" +
                nameItem + "\"," +
                rule + ",\"" +
                contextSnippet + "\"," +
                confidenceScore + "," +
                nameDb;
    }


}
