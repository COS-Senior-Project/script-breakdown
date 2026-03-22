import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

public class ShootingDay {
    public enum Move {
        MOVE,
        NO_MOVE
    }
    private int dayNumber;
    private String location;
    private Scene.ShootPhase time;
    private int usedEights = 0;

    private List<Scene> scenes = new ArrayList<>();
    private Set<String> castSet = new HashSet<>();
    private Move move;

    public ShootingDay(int dayNumber, String location, Scene.ShootPhase time, Move move) {
        this.dayNumber = dayNumber;
        this.location = location;
        this.time = time;
        this.move = move;
    }

    //adds scenes to the list
    //sums the eights of the scenes for the day
    //adds the cast needed in the set
    public void addScene(Scene scene, Move move) {
        scenes.add(scene);
        if (move == Move.MOVE) {
            usedEights += scene.getSceneLength() + 8;
        } else {
            usedEights += scene.getSceneLength();
        }
        castSet.addAll(scene.getCanonicalCharacterNames());

    }

    public int getDayNumber() { return dayNumber; }
    public String getLocation() { return location; }
    public Scene.ShootPhase getTime() { return time; }
    public Move getMove() { return move; }

    public void setUsedEights(int usedEights) {
        this.usedEights = usedEights;
    }

    public int getUsedEights() { return usedEights; }
    public Set<String> getCastSet() { return castSet; }
    public List<Scene> getScenes() { return scenes; }

    //tracks how far apart the scenes are for continuity
    public double getAverageScriptOrder() {
        if (scenes.isEmpty()) return 0;

        int sum = 0;
        for (Scene s : scenes) {
            //sums the scene order of the scenes for the day
            sum += s.getSceneIntNumber();
        }
        //divides the sum with the scene size for the day
        //to check if the average scene order is close to most scenes for the day
        return (double) sum / scenes.size();
    }
}
