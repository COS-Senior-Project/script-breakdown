package bigbreak;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;

import java.io.*;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.Files;
import java.util.*;
import java.util.Collection;
import java.util.stream.Collector;
import java.util.stream.Collectors;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;

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

            CharacterClusterer clusterer = new CharacterClusterer();
            CharacterOperations characterOperations = new CharacterOperations(nameFinderME, clusterer);
            characterOperations.processScenes(scenes);
            //assigns the shoot phase to the scene
            TimeClassifier.resolveTimes(scenes);
            //schedules the scenes
            ShootingScheduler scheduler = new ShootingScheduler(45);
            List<ShootingDay> schedule = scheduler.schedule(scenes);

            try {
                //converts Java to JSON syntax
                ObjectMapper mapper = new ObjectMapper();
                //makes the JSON in a more readable format
                mapper.enable(SerializationFeature.INDENT_OUTPUT);
                //top level container - list of shooting days in order
                List<Map<String, Object>> daysOutput = new ArrayList<>();

                for (ShootingDay day : schedule) {
                    //day map to model a JSON object in order
                    Map<String, Object> dayMap = new LinkedHashMap<>();
                    dayMap.put("dayNumber", day.getDayNumber());
                    dayMap.put("usedEights", day.getUsedEights() - 8 * day.getMoveCount());
                    dayMap.put("time", day.getTime());
                    dayMap.put("move", day.getMove());

                    //set for unique locations
                    Set<String> locations = new HashSet<>();
                    for (Scene s : day.getScenes()) {
                        locations.add(s.getLocation());
                    }

                    dayMap.put("locations", locations);

                    //list to store days in order of scheduling
                    List<Map<String, Object>> scenesList = new ArrayList<>();

                    for (Scene scene : day.getScenes()) {
                        //map to represent JSON structure in order
                        Map<String, Object> sceneMap = new LinkedHashMap<>();
                        sceneMap.put("sceneNumber", scene.getSceneNumber());
                        sceneMap.put("heading", scene.getHeading());
                        sceneMap.put("intExt", scene.getLocationKeyword());
                        sceneMap.put("location", scene.getLocation());
                        sceneMap.put("shootPhase", scene.getShootPhase());
                        sceneMap.put("lengthEights", scene.getSceneLength());
                        sceneMap.put("characters", scene.getCanonicalCharacterNames());
                        //adds each scene map to the list
                        scenesList.add(sceneMap);
                    }
                    //adds each scene list to the day
                    dayMap.put("scenes", scenesList);
                    //adds each day map to the day list
                    daysOutput.add(dayMap);
                }
                //creates a file reference
                File outFile = new File("output/schedule27.json");
                //ensures parent folder exists
                outFile.getParentFile().mkdirs();
                System.out.println("Writing JSON...");
                //writes in the JSON file with one key value pair - "days" (key) and the days list (value)
                mapper.writeValue(outFile,
                        Collections.singletonMap("days", daysOutput));
            } catch (IOException io) {
                System.out.println("JSON file not found.");
            }
            //tries to open the file for writing using a memory buffer and closes the writer at the end
//            try (BufferedWriter csvWriter = new BufferedWriter(new FileWriter("output/character_candidates110.csv",true))) {
//                //csvWriter.write("SceneNumber,SceneLengthsEights,SceneLocationKeyword,SceneLocation,SceneShootPhase\n");
//
//                csvWriter.write("DayNumber,SceneNumbers,EightsUsed,Time,Location\n");
//
//                for (ShootingDay day : schedule) {
//                    /*
//                    for (Character c : scene.getCharacters()) {
//                        if (c.getConfidenceScore() < 0.65) continue;
//                        csvWriter.write(scene.getSceneNumber() + "," + c.getCanonicalName() + "," + scene.getSceneLength() + "," + scene.getLocationKeyword() + "," + scene.getLocation() + "," + scene.getShootPhase());
//                        csvWriter.newLine();
//                    }
//                     */
//                    //csvWriter.write(scene.getSceneNumber() + "," + scene.getSceneLength() + "," + scene.getLocationKeyword() + "," + scene.getLocation() + "," + scene.getShootPhase());
//                    //csvWriter.newLine();
//                    String numbers = day.getScenes().stream().map(Scene::getSceneNumber).collect(Collectors.joining(":"));
//                    Set<String> locations = new HashSet<>();
//                    for (Scene s : day.getScenes()) {
//                        locations.add(s.getLocation());
//                    }
//                    StringBuilder locationsPerDay = new StringBuilder();
//                    for (String loc : locations) {
//                        locationsPerDay.append(loc).append("/");
//                    }
//                    csvWriter.write(day.getDayNumber() + "," + numbers + "," + day.getUsedEights() + "," + day.getTime() + "," + locationsPerDay);
//                    csvWriter.newLine();
//                }
//
//                /*
//                    //loops through each film character in the list
//                    for (Character c : allCharacters) {
//                        if (c.confidenceScore >= 0.65) {
//                            finalCharacters.add(c.nameItem);
//                            //CSV OUTPUT
//                            csvWriter.write(c.toCSVRow());
//                            csvWriter.newLine();
//
//                            //collect .train entry in memory
//                            //trainEntries.add(c.bootstrappingObjects("person"));
//                        }
//                    }
//
//
//                 */
//
//
//            } catch (IOException io) { //if the CSV file is not found, it handles the error
//                System.out.println("CSV file not found.");
//            }
//
//            for (Scene scene : scenes) {
//                System.out.println("Scene Numbers: " + scene.getSceneNumber() + "   Scene Heading: " + scene.getHeading() + "   Scene Location: " + scene.getLocation());
//            }

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