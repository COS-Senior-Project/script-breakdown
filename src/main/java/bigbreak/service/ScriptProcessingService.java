package bigbreak.service;

import bigbreak.*;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.List;

@Service
public class ScriptProcessingService {
    public List<ShootingDay> processScript(String script)  throws Exception {
        //creates an object of ScriptParser
        ScriptParser parser = new ScriptParser();
        //the parser splits the script into scenes and puts them in a list
        List<Scene> scenes = parser.splitScenes(script);
        //creates a name database object
        NameDatabase nameDb = new NameDatabase();

        //tries to load the NameFinderME file as a resource from the classpath
        try (InputStream modelIn = getClass().getResourceAsStream("/models/en-ner-person.bin")) {
            //reads the model from the input stream and loads it as an OpenNLP model to recognize human names
            TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
            //creates an instance of NameFinderME
            NameFinderME nameFinderME = new NameFinderME(model);

            CharacterClusterer clusterer = new CharacterClusterer();
            CharacterOperations characterOperations = new CharacterOperations(nameDb, nameFinderME, clusterer);
            characterOperations.processScenes(scenes);
            //assigns the shoot phase to the scene
            TimeClassifier.resolveTimes(scenes);
            //schedules the scenes
            ShootingScheduler scheduler = new ShootingScheduler(45);
            return scheduler.schedule(scenes);
        }
    }
}
