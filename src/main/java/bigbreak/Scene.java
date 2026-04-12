package bigbreak;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class Scene {
    public enum ShootPhase {
        DAY,
        NIGHT
    }
    private final int sceneIntNumber;
    private final String sceneNumber;
    private final String heading;
    private final String content;
    private final String locationKeyword;
    private final String location;
    private final String time;
    private final int sceneLength;
    private List<Character> characters = new ArrayList<>();
    private ShootPhase shootPhase;
    private Set<String> canonicalCharacterNames = new HashSet<>();
    private Set<String> charactersBelowConfidence = new HashSet<>();
    private Set<String> charactersDisplayedHC = new HashSet<>();
    private Set<String> charactersDisplayedLC = new HashSet<>();
    public Scene(int sceneIntNumber, String sceneNumber, String heading, String content, String locationKeyword, String location, String time, int sceneLength){
        this.sceneIntNumber = sceneIntNumber;
        this.sceneNumber = sceneNumber;
        this.heading = heading;
        this.content = content;
        this.locationKeyword = locationKeyword;
        this.location = location;
        this.time = time;
        this.sceneLength = sceneLength;
    }

    public int getSceneIntNumber(){
        return sceneIntNumber;
    }
    public String getSceneNumber(){
        return sceneNumber;
    }
    public String getHeading(){
        return heading;
    }
    public String getContent(){
        return  content;
    }
    public String getLocationKeyword(){
        return  locationKeyword;
    }
    public String getLocation() { return location; }
    public String getTime(){
        return  time;
    }
    public int getSceneLength() { return sceneLength; }

    public void addCharacter(Character c) {
        characters.add(c);
    }
    public List<Character> getCharacters() { return characters; }
    public void setCharacters(List<Character> characters){ this.characters = characters; }

    public ShootPhase getShootPhase() { return shootPhase; }

    public void setShootPhase(ShootPhase shootPhase) { this.shootPhase = shootPhase; }

    public Set<String> getCanonicalCharacterNames() {
        Set<String> names = new HashSet<>();
        for (Character c : characters) {
            if (c.getCanonicalName() != null && c.getConfidenceScore() >= 0.80) {
                names.add(c.getCanonicalName());
            }
        }
        return names;
    }

    public void setCanonicalCharacterNames(Set<String> canonicalCharacterNames) {
        this.canonicalCharacterNames = canonicalCharacterNames;
    }

    public Set<String> getCharactersDisplayedHC() {
        return charactersDisplayedHC;
    }

    public void setCharactersDisplayedHC(Set<String> charactersDisplayedHC) {
        this.charactersDisplayedHC = charactersDisplayedHC;
    }

    public Set<String> getCharactersDisplayedLC() {
        return charactersDisplayedLC;
    }

    public void setCharactersDisplayedLC(Set<String> charactersDisplayedLC) {
        this.charactersDisplayedLC = charactersDisplayedLC;
    }

    public void setCharactersBelowConfidence(Set<String> charactersBelowConfidence) {
        this.charactersBelowConfidence = charactersBelowConfidence;
    }

    public Set<String> getCharactersBelowConfidence() {
        Set<String> names = new HashSet<>();
        for (Character c : characters) {
            if (c.getCanonicalName() != null && c.getConfidenceScore() < 0.80) {
                names.add(c.getCanonicalName());
            }
        }
        return names;
    }

    public Set<String> getCharactersWithScores() {
        Set<String> names = new HashSet<>();
        for (Character c : characters) {
            if (c.getCanonicalName() != null) {
                names.add(c.getCanonicalName() + " " + c.getConfidenceScore());
            }
        }
        return names;
    }

    @Override
    public String toString(){
        return "Scene: " + sceneNumber +
                "\nHeading: " + heading +
                "\nContent:\n" + content +
                "\n-----------------------------------\n";
    }
}
