import opennlp.tools.namefind.NameFinderME;

import java.util.ArrayList;
import java.util.List;

public class CharacterPipeline {
    public static List<Character> extractAll(Scene scene, NameDatabase nameDb, NameFinderME nameFinderME){
        List<Character> allCharacters = new ArrayList<>();

        allCharacters.addAll(CharacterExtractor.extractSpeakerCues(scene.getContent(), scene, nameDb));
        allCharacters.addAll(CharacterExtractor.extractInlineName(scene.getContent(), scene, nameFinderME, nameDb));
        allCharacters.addAll(CharacterExtractor.extractPersonWord(scene.getContent(), scene));
        allCharacters.addAll(CharacterExtractor.extractIntroCharacter(scene.getContent(), scene));

        return allCharacters;
    }
}
