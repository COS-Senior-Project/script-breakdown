package bigbreak;

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

        //maps locations to scenes to create location groups
        LinkedHashMap<String, List<Scene>> locationGroups = new LinkedHashMap<>();
        //scenes.sort(Comparator.comparing(Scene::getLocation));
        //LinkedHashMap<Scene, String> locations = new LinkedHashMap<>();
        for (Scene s : scenes) {
            //creates a new location group if new location appears and adds the scene to the group
            //if location has appeared before, it adds the scene to the already created location group
            locationGroups.computeIfAbsent(s.getLocation(), k -> new ArrayList<>()).add(s);
        }
        ShootingDay.Time time = null;
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
                    //moving locations variable
                    ShootingDay.Move move = ShootingDay.Move.NO_MOVE;
                    //checks if day fits the basic requirements of length
                    if (!feasible(day, scene, schedule)) continue;
                    //weighted score based on the priorities of the matching - cast similarity, scene order, and not too packed days
                    double score = 0;
                    score =  locationScore(day, scene) + timeScore(day, scene) + castOverlapScore(day, scene) * 20.0 + orderScore(day, scene) * 2.0 + loadPenalty(day, scene);
//                    System.out.println("Location score" + locationScore(day, scene) + "   Time score: " + timeScore(day, scene) + "     Cast overlap score: " +  castOverlapScore(day, scene) * 3.0
//                            + "     Order score: " + orderScore(day, scene) * 2.0 + "       Load Penalty:" + loadPenalty(day, scene) + "    Total score: " + score);
                    //if the score of this scene is larger than the previous best one
                    if (score > bestScore) {
//                        System.out.println("Best score: " + bestScore);
                        //best score and best day are set to the current score and day
                        bestScore = score;
                        bestDay = day;
                        //locations.put(scene, scene.getLocation());
                    }
                }
                //double newDayScore = -700;
                if (bestDay == null) { //if no best day - first scene or requirements not fulfilled
                    if (scene.getShootPhase() == Scene.ShootPhase.DAY) {
                        time = ShootingDay.Time.DAY;
                    } else {
                        time = ShootingDay.Time.NIGHT;
                    }
                    //new day is created and the scene is added to it
                    ShootingDay newDay = new ShootingDay(schedule.size() + 1, time, ShootingDay.Move.NO_MOVE);
                    scene.setCharactersDisplayedHC(scene.getCanonicalCharacterNames());
                    scene.setCharactersDisplayedLC(scene.getCharactersBelowConfidence());
                    newDay.addScene(scene);
                    newDay.setMove(ShootingDay.Move.NO_MOVE);
                    schedule.add(newDay);
                } else {
                    //after checking all scheduled days and the best day for the scene is found, the scene is added to it
                    if (!bestDay.getLocationSet().contains(scene.getLocation())) {
                        bestDay.setMove(ShootingDay.Move.MOVE);
                        //int moveCount = bestDay.getMoveCount() + 1;
                        //bestDay.setMoveCount(moveCount);

                        int moveWeight = bestDay.getUsedEights() + 8;
                        bestDay.setUsedEights(moveWeight);
                    }
                    if (scene.getShootPhase() == Scene.ShootPhase.NIGHT && bestDay.getTime() == ShootingDay.Time.DAY) {
                        bestDay.setTime(ShootingDay.Time.DAY_NIGHT);
                    }
                    scene.setCharactersDisplayedHC(scene.getCanonicalCharacterNames());
                    scene.setCharactersDisplayedLC(scene.getCharactersBelowConfidence());
                    bestDay.addScene(scene);
                    //locations.put(scene, scene.getLocation());
                }
            }
        }

        return schedule;
    }

    //checks if the basic requirements are met for scene to match a day
    private boolean feasible(ShootingDay day, Scene scene, List<ShootingDay> sch) {
        if (day.getUsedEights() + scene.getSceneLength() > MAX_EIGHTS_PER_DAY) //checks if it fits the limit
            return false;
        if (day.getMoveCount() > 2) return false;
        int dayIndex = sch.indexOf(day);
        if (dayIndex > 0) {
            ShootingDay previousDay = sch.get(dayIndex - 1);
            if (previousDay.getTime() == ShootingDay.Time.NIGHT && scene.getShootPhase() == Scene.ShootPhase.DAY) {
                return false;
            }
        }
        if ((day.getTime() == ShootingDay.Time.NIGHT || day.getTime() == ShootingDay.Time.DAY_NIGHT) && scene.getShootPhase() == Scene.ShootPhase.DAY) {
            return false;
        }
        return true;
    }

    private double locationScore(ShootingDay day, Scene scene) {
        if (day.getLocationSet().isEmpty()) return 0;
        for (String loc : day.getLocationSet()) {
            Set<String> dayLocTokenized = new HashSet<>(Arrays.asList(TextUnits.tokenize(loc)));
            Set<String> sceneLocTokenized = new HashSet<>(Arrays.asList(TextUnits.tokenize(scene.getLocation())));
            if (dayLocTokenized.containsAll(sceneLocTokenized)) {
                return 1000;
            }
            if (!Collections.disjoint(dayLocTokenized, sceneLocTokenized)) {
                return 400;
            }
        }
        return -300;
    }

    private double timeScore (ShootingDay day, Scene scene) {
        //if (day.getTime() == null) { return 0; }
        if (scene.getShootPhase().name().equals(day.getTime().name())) {
            return 200;
        }
        return -200;
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
        //computes the score relative to the max eights per day
        double ratio = load / (MAX_EIGHTS_PER_DAY - 5);

        if (ratio > 1.0) {
            return -200;
        }
        return ratio * 200;
    }
}