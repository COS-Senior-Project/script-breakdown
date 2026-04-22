package bigbreak;

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
    private int eightsWoMoves = 0;
    private List<Scene> scenes = new ArrayList<>();
    private Set<String> castSet = new HashSet<>();
    private Move move;
    private int moveCount;

    public ShootingDay(int dayNumber, Time time, Move move) {
        this.dayNumber = dayNumber;
        //this.location = location;
        this.time = time;
        this.move = move;
    }

    //adds scenes to the list
    //sums the eights of the scenes for the day
    //adds the cast needed in the set
    public void addScene(Scene scene) {
        scenes.add(scene);
        usedEights += scene.getSceneLength();
        castSet.addAll(scene.getCanonicalCharacterNames());
        locationSet.add(scene.getLocation());
        setMoveCount(locationSet.size() - 1);
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

    public int getEightsWoMoves() {

        for (Scene s : scenes) {
            this.eightsWoMoves += s.getSceneLength();
        }
        return eightsWoMoves;
    }

    public void setEightsWoMoves(int eightsWoMoves) {
        this.eightsWoMoves = eightsWoMoves;
    }

    public void setDayNumber(int dayNumber) {
        this.dayNumber = dayNumber;
    }

    public void setLocation(LinkedHashMap<Scene, String> location) {
        this.location = location;
    }

    public void setLocationSet(Set<String> locationSet) {
        this.locationSet = locationSet;
    }

    public void setScenes(List<Scene> scenes) {
        this.scenes = scenes;
    }

    public void setCastSet(Set<String> castSet) {
        this.castSet = castSet;
    }

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
    public void recalculate() {
        this.locationSet.clear();
        this.time = null;
        this.moveCount = 0;

        Set<String> seenLocations = new LinkedHashSet<>();

        for (Scene s : scenes) {
            if (s.getLocation() != null) {
                seenLocations.add(s.getLocation());
            }
            if (s.getShootPhase() == Scene.ShootPhase.DAY && (time == Time.DAY || time == null)) {
                time = Time.DAY;
            }
            else if (s.getShootPhase() == Scene.ShootPhase.NIGHT && (time == Time.NIGHT) || time == null) {
                time = Time.NIGHT;
            }
            else if ((s.getShootPhase() == Scene.ShootPhase.DAY && time == Time.NIGHT) || (s.getShootPhase() == Scene.ShootPhase.NIGHT && time == Time.DAY)) {
                time = Time.DAY_NIGHT;
            }
            else {
                continue;
            }
        }
        this.locationSet = seenLocations;
        this.moveCount = seenLocations.size() - 1;
    }
}
