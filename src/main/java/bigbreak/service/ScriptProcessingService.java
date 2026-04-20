package bigbreak.service;

import bigbreak.*;
import bigbreak.Character;
import bigbreak.dtos.DayDTO;
import bigbreak.dtos.SceneDTO;
import bigbreak.dtos.ScheduleDTO;
import bigbreak.test.CharacterExtractionEvaluationTest;
import bigbreak.test.SceneTestData;
import bigbreak.test.JsonLoader;
import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.util.*;

@Service
public class ScriptProcessingService {
    private Map<Integer, Scene> sceneStore = new HashMap<>();
    public ScheduleDTO processScript(String script)  throws Exception {
        sceneStore.clear();
        //creates an object of ScriptParser
        ScriptParser parser = new ScriptParser();
        //the parser splits the script into scenes and puts them in a list
        List<Scene> scenes = parser.splitScenes(script);
        //List<SceneTestData> groundTruth = JsonLoader.load("D:/AUBG/COS/senior-project/script-breakdown/src/main/resources/test/ground_truth_eternal_sunshine.json");
        for (Scene scene : scenes) {
            sceneStore.put(scene.getSceneIntNumber(), scene);
        }
        //creates a name database object
        //NameDatabase nameDb = new NameDatabase();

        //tries to load the NameFinderME file as a resource from the classpath
        try (InputStream modelIn = getClass().getResourceAsStream("/models/en-ner-person.bin")) {
            //reads the model from the input stream and loads it as an OpenNLP model to recognize human names
            TokenNameFinderModel model = new TokenNameFinderModel(modelIn);
            //creates an instance of NameFinderME
            NameFinderME nameFinderME = new NameFinderME(model);

            CharacterClusterer clusterer = new CharacterClusterer();
            CharacterOperations characterOperations = new CharacterOperations(nameFinderME, clusterer);
            characterOperations.processScenes(scenes);
            //assigns the shoot phase to the scene
            TimeClassifier.resolveTimes(scenes);
            //schedules the scenes
            ShootingScheduler scheduler = new ShootingScheduler(45);
            List<ShootingDay> shootingDays = scheduler.schedule(scenes);

            //CharacterExtractionEvaluationTest.evaluate(groundTruth, scenes);
            return mapToDTO(shootingDays);
        }
    }

    public ScheduleDTO updateSchedule(ScheduleDTO schedule) {
        List<ShootingDay> updatedDays = new ArrayList<>();

        for (DayDTO dayDTO : schedule.getDays()) {
            ShootingDay.Time time = ShootingDay.Time.valueOf(dayDTO.getTime());
            ShootingDay.Move move = ShootingDay.Move.valueOf(dayDTO.getMove());
            ShootingDay shootingDay = new ShootingDay(dayDTO.getDayNumber(), time, move);

            int sumEights = 0;
            for (SceneDTO sceneDTO : dayDTO.getScenes()) {
                if (sceneDTO != null) {
                    Scene scene = sceneStore.get(sceneDTO.getSceneIntNumber());

                    scene.setCharactersDisplayedHC(sceneDTO.getCharactersDisplayedHC());
                    scene.setCharactersDisplayedLC(sceneDTO.getCharactersDisplayedLC());
                    shootingDay.addScene(scene);
                }

            }
            shootingDay.recalculate();
            updatedDays.add(shootingDay);
        }
        return mapToDTO(updatedDays);
    }
    private ScheduleDTO mapToDTO(List<ShootingDay> shootingDays) {
       List<DayDTO> dayDTOs = new ArrayList<>();

       for (ShootingDay day : shootingDays) {
           DayDTO dayDTO = new DayDTO();
           dayDTO.setDayNumber(day.getDayNumber());
           dayDTO.setTime(day.getTime().name());
           dayDTO.setMove(day.getMove().name());
           dayDTO.setMoveCount((day.getMoveCount()));
           dayDTO.setPageCountEights(day.getUsedEights());
           dayDTO.setEightsWoMoves(day.getEightsWoMoves());
           dayDTO.setLocations(new HashSet<>(day.getLocationSet()));

           List<SceneDTO> sceneDTOs = new ArrayList<>();
           for (Scene scene : day.getScenes()) {
               SceneDTO sceneDTO = new SceneDTO();

               sceneDTO.setSceneIntNumber(scene.getSceneIntNumber());
               sceneDTO.setSceneNumber(scene.getSceneNumber());
               sceneDTO.setHeading(scene.getHeading());
               sceneDTO.setContent(scene.getContent());
               sceneDTO.setLocationKeyword(scene.getLocationKeyword());
               sceneDTO.setLocation(scene.getLocation());
               sceneDTO.setShootPhase(scene.getShootPhase());
               sceneDTO.setPageCountEights(scene.getSceneLength());

               sceneDTO.setCanonicalCharacterNames(new HashSet<>(scene.getCanonicalCharacterNames()));
               sceneDTO.setCharactersBelowConfidence(new HashSet<>(scene.getCharactersBelowConfidence()));
               sceneDTO.setCharactersDisplayedHC(new HashSet<>(scene.getCharactersDisplayedHC()));
               sceneDTO.setCharactersDisplayedLC(new HashSet<>(scene.getCharactersDisplayedLC()));
               sceneDTOs.add(sceneDTO);
           }
           dayDTO.setScenes(sceneDTOs);
           dayDTOs.add(dayDTO);
       }
       ScheduleDTO scheduleDTO = new ScheduleDTO();
       scheduleDTO.setDays(dayDTOs);

       return scheduleDTO;
    }
}
