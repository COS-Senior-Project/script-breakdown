package bigbreak;

import opennlp.tools.namefind.NameFinderME;
import java.util.ArrayList;
import java.util.List;

public class CharacterPipeline {
    public static List<Character> extractAll(Scene scene, NameFinderME nameFinderME){
        List<Character> allCharacters = new ArrayList<>();

        allCharacters.addAll(CharacterExtractor.extractSpeakerCues(scene.getContent(), scene));
        allCharacters.addAll(CharacterExtractor.extractInlineName(scene.getContent(), scene, nameFinderME));
        allCharacters.addAll(CharacterExtractor.extractIntroCharacter(scene.getContent(), scene));

        return allCharacters;
    }
}
