import java.util.ArrayList;
import java.util.List;

public class Scene {
    private final int sceneIntNumber;
    private final String sceneNumber;
    private final String heading;
    private final String content;
    private final String locationKeyword;
    private final String time;

    private final List<CharacterMention> mentions = new ArrayList<>();
    private final int sceneLength;

    private List<Character> characters = new ArrayList<>();

    public Scene(int sceneIntNumber, String sceneNumber, String heading, String content, String locationKeyword, String time, int sceneLength){
        this.sceneIntNumber = sceneIntNumber;
        this.sceneNumber = sceneNumber;
        this.heading = heading;
        this.content = content;
        this.locationKeyword = locationKeyword;
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
    public String getTime(){
        return  time;
    }

    public int getSceneLength() { return sceneLength; }

    //adds post-training character mentions to a list
    public void addCharacter(Character c) {
        characters.add(c);
    }


    public List<Character> getCharacters() { return characters; }

    @Override
    public String toString(){
        return "Scene: " + sceneNumber +
                "\nHeading: " + heading +
                "\nContent:\n" + content +
                "\n-----------------------------------\n";
    }
}
