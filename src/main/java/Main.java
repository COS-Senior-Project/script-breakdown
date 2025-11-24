import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;

import java.io.*;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        String script = ScriptParser.loadScript("src/main/resources/scripts/the-silence-of-the-lambs.txt");

        ScriptParser parser = new ScriptParser();
        List<Scene> scenes = parser.splitScenes(script);
        //CharacterExtractor.extractCharacterToCSV(scenes, "src/main/resources/data/character_candidates.csv");

        NameDatabase nameDb = new NameDatabase();
        try (InputStream modelIn = Main.class.getResourceAsStream("/models/en-ner-person.bin")) {
            TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
            NameFinderME nameFinderME = new NameFinderME(model);

            try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/data/character_candidates.csv"))) {
                for (Scene scene : scenes) {
                    System.out.println(scene);
                    List <Character> speakerCharacters = CharacterExtractor.extractSpeakerCues(scene.getContent(), scene, nameDb);
                    List <Character> inlineCharacters = CharacterExtractor.extractInlineName(scene.getContent(), scene, nameFinderME, nameDb);
                    List <Character> personWordCharacters = CharacterExtractor.extractPersonWord(scene.getContent(), scene);
                    List <Character> personIntroCharacters = CharacterExtractor.extractIntroCharacter(scene.getContent(), scene);

                    for (Character c : speakerCharacters) {
                        if (c.confidenceScore >= 0.65){
                            writer.write(c.toCSVRow());
                            writer.newLine();
                        }
                    }
                    for (Character c : inlineCharacters) {
                        if (c.confidenceScore >= 0.65){
                            writer.write(c.toCSVRow());
                            writer.newLine();
                        }
                    }
                    for (Character c : personWordCharacters) {
                        writer.write(c.toCSVRow());
                        writer.newLine();
                    }
                    for (Character c : personIntroCharacters) {
                        writer.write(c.toCSVRow());
                        writer.newLine();
                    }
                }
            } catch (IOException e) {
                System.out.println("CSV file not found.");
            }

        }
    }
}