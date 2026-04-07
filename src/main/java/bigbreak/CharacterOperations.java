package bigbreak;

import opennlp.tools.namefind.NameFinderME;

import java.util.*;

public class CharacterOperations {
    private final NameDatabase nameDb;
    private final NameFinderME nameFinderME;
    private final CharacterClusterer clusterer;

    public CharacterOperations(NameDatabase nameDb, NameFinderME nameFinderME, CharacterClusterer clusterer) {
        this.nameDb = nameDb;
        this.nameFinderME = nameFinderME;
        this.clusterer = clusterer;
    }

    //for each scene, extract all characters
    public void processScenes(List<Scene> scenes) {
        for (Scene scene : scenes) {
            List<Character> extracted = CharacterPipeline.extractAll(scene, nameDb, nameFinderME);

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

        //builds a canonical map of all names
        Map<String, String> canonicalMap = clusterer.buildCanonicalMap(allNames);

        //sets a canonical name for each character
        for (Scene scene : scenes) {
            for (Character c : scene.getCharacters()) {
                String raw = c.getNameItem();
                String canonical = canonicalMap.get(raw.toUpperCase(Locale.ROOT));
                c.setCanonicalName(canonical);
            }
        }

        //overrides the original characters with the unique ones per each scene
        for (Scene scene : scenes) {
            //map to hold the canonical name and the character
            Map<String, Character> uniqueByCanonical = new LinkedHashMap<>();

            for (Character c : scene.getCharacters()){
                String key = c.getCanonicalName();
                //if no canonical name, the entry is skipped
                if (key == null) continue;
                //checks if the canonical name appears in this scene and adds it to the map if absent
                uniqueByCanonical.putIfAbsent(key, c);
            }
            //resets the characters per scene with the unique values
            scene.setCharacters(new ArrayList<>(uniqueByCanonical.values()));
        }
    }
}
