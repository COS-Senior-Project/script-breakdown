import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;

import java.io.*;
import java.util.List;

public class Main {
    public static void main(String[] args) throws IOException {
        //loads the film script
        String script = ScriptParser.loadScript("/scripts/the-silence-of-the-lambs.txt");
        //creates an object of ScriptParser
        ScriptParser parser = new ScriptParser();
        //the parser splits the script into scenes and puts them in a list
        List<Scene> scenes = parser.splitScenes(script);
        //creates a name database object
        NameDatabase nameDb = new NameDatabase();
        //tries to load the NameFinderME file as a resource from the classpath
        try (InputStream modelIn = Main.class.getResourceAsStream("/models/en-ner-person.bin")) {
            //reads the model from the input stream and loads it as an OpenNLP model to recognize human names
            TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
            //creates an instance of NameFinderME
            NameFinderME nameFinderME = new NameFinderME(model);
            //tries to open the file for writing using a memory buffer and closes the writer at the end
            try (BufferedWriter writer = new BufferedWriter(new FileWriter("src/main/resources/data/character_candidates.csv"))) {
                //loops through each scene
                for (Scene scene : scenes) {
                    //prints out each scene
                    System.out.println(scene);
                    //creates lists of characters for all extractors
                    List <Character> speakerCharacters = CharacterExtractor.extractSpeakerCues(scene.getContent(), scene, nameDb);
                    List <Character> inlineCharacters = CharacterExtractor.extractInlineName(scene.getContent(), scene, nameFinderME, nameDb);
                    List <Character> personWordCharacters = CharacterExtractor.extractPersonWord(scene.getContent(), scene);
                    List <Character> personIntroCharacters = CharacterExtractor.extractIntroCharacter(scene.getContent(), scene);

                    //loops through each film character in the list
                    for (Character c : speakerCharacters) {
                        //if the confidence score is high enough, it writes into the CSV
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
                        //no need to check confidence here because it is a static number
                        //writes into the CSV
                        writer.write(c.toCSVRow());
                        writer.newLine();
                    }
                    for (Character c : personIntroCharacters) {
                        writer.write(c.toCSVRow());
                        writer.newLine();
                    }
                }
            } catch (IOException io) { //if the CSV file is not found, it handles the error
                System.out.println("CSV file not found.");
            } catch (NullPointerException npe) { //if the model input stream is null
                System.out.println("Model input stream was null.");
            } catch (Exception e) { //general exception
                e.printStackTrace();
            }

        }
    }
}