import java.util.*;

public class ShootingScheduler {
    //capacity limit of the shooting day
    private final int MAX_EIGHTS_PER_DAY;

    public ShootingScheduler(int maxEightsPerDay) {
        this.MAX_EIGHTS_PER_DAY = maxEightsPerDay;
    }

    //SSGS algorithm to construct the shooting days
    public List<ShootingDay> schedule(List<Scene> scenes) {
        //list of days in the schedule so far
        List<ShootingDay> schedule = new ArrayList<>();
        ShootingDay.Move move = ShootingDay.Move.NO_MOVE;
        HashMap<Scene, String> locations = new HashMap<>();

        //maps locations to scenes to create location groups
        Map<String, List<Scene>> locationGroups = new LinkedHashMap<>();
        for (Scene s : scenes) {
            //creates a new location group if new location appears and adds the scene to the group
            //if location has appeared before, it adds the scene to the already created location group
            locationGroups.computeIfAbsent(s.getLocation(), k -> new ArrayList<>()).add(s);
        }

        //loops through all scenes in each location group
        for (List<Scene> locationScenes : locationGroups.values()) {
            //night - true, day - false; false < true which makes all day scenes before the night ones at the same location
            locationScenes.sort(Comparator.comparing(scene -> scene.getShootPhase().equals(Scene.ShootPhase.NIGHT)));

            //loops through each scene of this location
            for (Scene scene : locationScenes) {
                //variable to check which day this scene fits the best
                ShootingDay bestDay = null;
                //checks which is the best day by this score
                double bestScore = Double.NEGATIVE_INFINITY;

                //loops through every day of the day that is scheduled
                for (ShootingDay day : schedule) {
                    //checks if day fits the basic requirements of location, time, and length
                    if (!feasible(day, scene)) continue;

                    //weighted score based on the priorities of the matching - cast similarity, scene order, and not too packed days
                    double score = castOverlapScore(day, scene) * 3.0 + orderScore(day, scene) * 2.0 + loadPenalty(day, scene);
                    //if the score of this scene is larger than the previous best one
                    if (score > bestScore) {
                        //best score and best day are set to the current score and day
                        bestScore = score;
                        bestDay = day;
                        locations.put(scene, scene.getLocation());
                        //checks location
                        if (!day.getLocation().equals(scene.getLocation())) {
                            move = ShootingDay.Move.MOVE;
                        }
                    }
                }
                //after checking all scheduled days and the best day for the scene is found, the scene is added to it
                if (bestDay != null) {
                    bestDay.addScene(scene, ShootingDay.Move.NO_MOVE);
                    locations.put(scene, scene.getLocation());
                } else { //if no best day - first scene or requirements not fulfilled
                    //new day is created and the scene is added to it
                    ShootingDay newDay = new ShootingDay(schedule.size() + 1, locations, scene.getShootPhase(), move);
                    newDay.addScene(scene, move);
                    schedule.add(newDay);
                }
            }
        }

        return schedule;
    }

    //checks if the basic requirements are met for scene to match a day
    private boolean feasible(ShootingDay day, Scene scene) {
        if (day.getUsedEights() + scene.getSceneLength() > MAX_EIGHTS_PER_DAY) //checks if it fits the limit
            return false;
        return true;
    }

    //checks how many cast members from the current scene overlap with the ones in the day already
    private double castOverlapScore(ShootingDay day, Scene scene) {
        Set<String> dayCast = day.getCastSet();
        Set<String> sceneCast = scene.getCanonicalCharacterNames();

        int overlap = 0;
        //checks each character in scene
        for (String c : sceneCast) {
            //if contained in the day, overlap score increases
            if (dayCast.contains(c)) overlap++;
        }
        return overlap;
    }
    //computes the difference of the current scene order to the average scene order in the day
    private double orderScore(ShootingDay day, Scene scene) {
        //computes average score of the day
        double avg = day.getAverageScriptOrder();
        //computes the distance and makes it negative so that it fits the score computation later (bigger number = better match)
        return -Math.abs(avg - scene.getSceneIntNumber());
    }
    //computes the load penalty depending on if the scene lengths of the day so far + the current scene
    private double loadPenalty(ShootingDay day, Scene scene) {
        //computes the load
        double load = day.getUsedEights() + scene.getSceneLength();
        //computes the penalty relative to the max eights per day6
        return load / MAX_EIGHTS_PER_DAY;
    }
}