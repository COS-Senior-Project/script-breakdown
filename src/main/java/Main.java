import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.*;
import java.util.Collection;

public class Main {
    public static void main(String[] args) throws IOException {
        //loads the film script
        String script = ScriptParser.loadScript("/scripts/eternal-sunshine.txt");
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


            //Populating the .csv and .train files + training code is here for reference/demo.
            //creates Path objects and converts the string path into a Path object
            //Path csvPath = Paths.get("output/character_candidates7.csv");
            //Path trainPath = Paths.get("output/characters_train4.train");
            //Path testPath = Paths.get("output/characters_test4.train");

            //ensures the parent directories of the paths exist
            //it creates all missing directories
            //if they already exist, it does nothing
            //Files.createDirectories(csvPath.getParent());
            //Files.createDirectories(trainPath.getParent());
            //Files.createDirectories(testPath.getParent());

            //list to contain all the train entries
            //List<String> trainEntries = new ArrayList<>();

            //for each scene, extract all characters
            for (Scene scene : scenes) {
                List<Character> extracted = CharacterPipeline.extractAll(scene, nameDb, nameFinderME);
                //prints out each scene
                //System.out.println(scene);

                //each extracted character is associated with a scene object
                for (Character c : extracted) {
                    scene.addCharacter(c);
                }
            }

            LinkedHashSet<String> allNames = new LinkedHashSet<>();

            //all names associated to scenes are added to a linked hash set
            for (Scene scene : scenes) {
                for (Character c : scene.getCharacters()) {
                    allNames.add(c.getNameItem());
                }
            }

            //creates a clusterer object
            CharacterClusterer clusterer = new CharacterClusterer();
            //builds a canonical map of all names
            Map<String, String> canonicalMap = clusterer.buildCanonicalMap(allNames);

            for (Scene scene : scenes) {
                for (Character c : scene.getCharacters()) {
                    String raw = c.getNameItem();
                    /*
                    System.out.println("RAW FROM CHARACTER: [" + raw + "]");
                    System.out.println("RAW LENGTH: " + raw.length());

                    for (String key : canonicalMap.keySet()) {
                        System.out.println("MAP KEY: [" + key + "] len=" + key.length());
                    }
                     */
                    String canonical = canonicalMap.get(raw.toUpperCase(Locale.ROOT));
                    c.setCanonicalName(canonical);
                }
            }

            //tries to open the file for writing using a memory buffer and closes the writer at the end
            try (BufferedWriter csvWriter = new BufferedWriter(new FileWriter("output/character_candidates39.csv", true))) {

                csvWriter.write("SceneNumber,CanonicalNames,SceneLengthsEights\n");
                for (Scene scene : scenes) {
                    Set<String> namesPerScene = new HashSet<>();
                    for (Character c : scene.getCharacters()) {
                        if (c.getConfidenceScore() < 0.65) continue;
                        namesPerScene.add(c.getCanonicalName());
                        /*
                        csvWriter.write(scene.getSceneNumber() + ","
                                        + c.getNameItem() + ","
                                        + c.getCanonicalName() + ","
                                        + c.getConfidenceScore());
                        csvWriter.newLine();
                         */
                    }
                    for (String name : namesPerScene) {
                        csvWriter.write(scene.getSceneNumber() + "," + name + "," + scene.getSceneLength());
                        csvWriter.newLine();
                    }
                }

                /*
                    //loops through each film character in the list
                    for (Character c : allCharacters) {
                        if (c.confidenceScore >= 0.65) {
                            finalCharacters.add(c.nameItem);
                            //CSV OUTPUT
                            csvWriter.write(c.toCSVRow());
                            csvWriter.newLine();

                            //collect .train entry in memory
                            //trainEntries.add(c.bootstrappingObjects("person"));
                        }
                    }


                 */


            } catch (IOException io) { //if the CSV file is not found, it handles the error
                System.out.println("CSV file not found.");
            }

            /*
            //shuffles the entries of the list using a random generator with the current time
            Collections.shuffle(trainEntries, new Random(System.currentTimeMillis()));

            //total number of entries
            int total = trainEntries.size();
            //the number of entries for training, leaving 20% for testing
            int trainSize = (int) (total * 0.8);

            //splits the data into training and testing subsets
            List<String> trainingData = trainEntries.subList(0, trainSize);
            List<String> testingData = trainEntries.subList(trainSize, total);


            //writes the lists into the files
            try (BufferedWriter trainWriter = new BufferedWriter(new FileWriter(trainPath.toFile(), true))) {
                for (String entry : trainingData) {
                    trainWriter.write(entry);
                    trainWriter.newLine();
                }
            }

            try (BufferedWriter testWriter = new BufferedWriter(new FileWriter(testPath.toFile(), true))) {
                for (String entry : testingData) {
                    testWriter.write(entry);
                    testWriter.newLine();
                }
            }

            /*
            System.out.println("Training entries: " + trainingData.size());
            System.out.println("Testing entries: " + testingData.size());


            TrainingRunner runner = new TrainingRunner();

            String trainFilePost = "output/characters_train_edited.train";
            String testFilePost = "output/characters_test_edited.train";
            String outputModel = "src/main/resources/models/en-ner-person-customer.bin";

            runner.trainAndEvaluate(trainFilePost, testFilePost, outputModel);

            System.out.println("Done!");



            try (OutputStream modelOut = new BufferedOutputStream(
                    new FileOutputStream("src/main/resources/models/en-ner-person-customer.bin"))) {
                model.serialize(modelOut);
            }

             */


        } catch (NullPointerException npe) { //if the model input stream is null
            System.out.println("Model input stream was null.");
        } catch (IOException e) { //if an input/output exception occurs
            System.out.println("File not found: " + e.getMessage());
        }
        catch (Exception e) { //general exception
            e.printStackTrace();
        }
    }
}