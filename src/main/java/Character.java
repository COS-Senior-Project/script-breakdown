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
                sceneNumber + "," +
                nameItem + "," +
                rule + "," +
                contextSnippet + "," +
                confidenceScore + "," +
                nameDb;
    }

    /*
    //Writes a CSV row
    private static void writeCSV(BufferedWriter writer, Scene scene, NameDatabase nameDb) throws IOException {
        List <Character> speakerCues = CharacterExtractor.extractSpeakerCues(scene.getContent(), scene, nameDb;)
        //name and rules are assumed safe (uppercase tokens); snippets need quoting
        StringBuilder sb = new StringBuilder();
        sb.append(sceneNumber).append(",")
                .append(nameItem).append(",")
                .append(rule).append(",")
                .append("\"").append(contextSnippet).append("\"");
        writer.write(sb.toString());
        writer.newLine();
    }
     */
}
