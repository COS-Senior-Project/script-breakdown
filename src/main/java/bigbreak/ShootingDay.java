import java.util.*;

public class ShootingDay {
    public enum Move {
        MOVE,
        NO_MOVE
    }
    public enum Time {
        DAY,
        NIGHT,
        DAY_NIGHT
    }
    private int dayNumber;
    private LinkedHashMap<Scene, String> location;
    private Set<String> locationSet = new HashSet<>();
    private Time time;
    private int usedEights = 0;

    private List<Scene> scenes = new ArrayList<>();
    private Set<String> castSet = new HashSet<>();
    private Move move;
    private int moveCount;
    private String primaryLocation;

    public ShootingDay(int dayNumber, Time time, Move move) {
        this.dayNumber = dayNumber;
        //this.location = location;
        this.time = time;
        this.move = move;
    }

    //adds scenes to the list
    //sums the eights of the scenes for the day
    //adds the cast needed in the set
    public void addScene(Scene scene, Move move) {
        scenes.add(scene);
        usedEights += scene.getSceneLength();
        castSet.addAll(scene.getCanonicalCharacterNames());
        locationSet.add(scene.getLocation());
    }

    public int getDayNumber() { return dayNumber; }
    public LinkedHashMap<Scene, String> getLocation() { return location; }
    public Set<String> getLocationSet() { return locationSet; }
    public Time getTime() { return time; }

    public void setTime(Time time) {
        this.time = time;
    }

    public Move getMove() { return move; }
    public int getMoveCount() { return moveCount; }
    public void setMoveCount(int moveCount) { this.moveCount = moveCount; }

    public void setMove(Move move) {
        this.move = move;
    }

    public void setUsedEights(int usedEights) {
        this.usedEights = usedEights;
    }

    public int getUsedEights() { return usedEights; }
    public Set<String> getCastSet() { return castSet; }
    public List<Scene> getScenes() { return scenes; }

    public String getPrimaryLocation() { return primaryLocation; }

    public void setPrimaryLocation(String primaryLocation) {
        this.primaryLocation = primaryLocation;
    }

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
