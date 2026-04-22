package bigbreak.dtos;

import bigbreak.Scene;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class SceneDTO {
    private int sceneIntNumber;
    private String sceneNumber;
    private String heading;
    private String content;
    private String locationKeyword;
    private String location;
    private String time;
    private Scene.ShootPhase shootPhase;
    private int pageCountEights;
    private List<bigbreak.Character> characters;
    private Set<String> canonicalCharacterNames = new HashSet<>();
    private Set<String> charactersBelowConfidence = new HashSet<>();
    private Set<String> charactersDisplayedHC = new HashSet<>();
    private Set<String> charactersDisplayedLC = new HashSet<>();

    public int getSceneIntNumber() {
        return sceneIntNumber;
    }

    public void setSceneIntNumber(int sceneIntNumber) {
        this.sceneIntNumber = sceneIntNumber;
    }

    public String getSceneNumber() {
        return sceneNumber;
    }

    public void setSceneNumber(String sceneNumber) {
        this.sceneNumber = sceneNumber;
    }

    public String getHeading() {
        return heading;
    }

    public void setHeading(String heading) {
        this.heading = heading;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    public String getLocationKeyword() {
        return locationKeyword;
    }

    public void setLocationKeyword(String locationKeyword) {
        this.locationKeyword = locationKeyword;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public Scene.ShootPhase getShootPhase() {
        return shootPhase;
    }

    public void setShootPhase(Scene.ShootPhase shootPhase) {
        this.shootPhase = shootPhase;
    }

    public int getPageCountEights() {
        return pageCountEights;
    }

    public void setPageCountEights(int pageCountEighths) {
        this.pageCountEights = pageCountEighths;
    }

    public List<bigbreak.Character> getCharacters() {
        return characters;
    }

    public void setCharacters(List<bigbreak.Character> characters) {
        this.characters = characters;
    }

    public Set<String> getCharactersDisplayedHC() {
        return charactersDisplayedHC;
    }

    public void setCharactersDisplayedHC(Set<String> charactersDisplayedHC) {
        this.charactersDisplayedHC = charactersDisplayedHC;
    }

    public Set<String> getCanonicalCharacterNames() {
        return canonicalCharacterNames;
    }

    public void setCanonicalCharacterNames(Set<String> canonicalCharacterNames) {
        this.canonicalCharacterNames = canonicalCharacterNames;
    }

    public Set<String> getCharactersDisplayedLC() {
        return charactersDisplayedLC;
    }

    public void setCharactersDisplayedLC(Set<String> charactersDisplayedLC) {
        this.charactersDisplayedLC = charactersDisplayedLC;
    }

    public Set<String> getCharactersBelowConfidence() {
        return charactersBelowConfidence;
    }

    public void setCharactersBelowConfidence(Set<String> charactersBelowConfidence) {
        this.charactersBelowConfidence = charactersBelowConfidence;
    }
}
