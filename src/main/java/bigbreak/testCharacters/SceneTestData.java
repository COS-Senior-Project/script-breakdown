package bigbreak.testCharacters;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.util.Set;

public class SceneTestData {
    @JsonProperty("scene_number")
    public int sceneNumber;

    @JsonProperty("scene_heading")
    public String sceneHeading;

    @JsonProperty("characters")
    public Set<String> characters;

    public int getSceneNumber() {
        return sceneNumber;
    }

    public void setSceneNumber(int sceneNumber) {
        this.sceneNumber = sceneNumber;
    }

    public String getSceneHeading() {
        return sceneHeading;
    }

    public void setSceneHeading(String sceneHeading) {
        this.sceneHeading = sceneHeading;
    }

    public Set<String> getCharacters() {
        return characters;
    }

    public void setCharacters(Set<String> characters) {
        this.characters = characters;
    }
}

