package bigbreak;

import org.w3c.dom.Text;

import java.util.*;

public class TimeClassifier {
    //times considered day
    private static final Set<String> DAY_TIMES = Set.of("DAY", "MORNING", "AFTERNOON", "NOON", "SUNRISE", "SUNSET");
    //times considered night
    private static final Set<String> NIGHT_TIMES = Set.of("NIGHT", "EVENING", "DUSK", "MIDNIGHT");
    //times considered continuous
    private static final Set<String> CONT_TIMES = Set.of("CONTINUOUS", "CONT'D", "CONT’D", "CONT.", "CONT",
            "CONTINUED", "SAME TIME", "IMMEDIATELY", "LATER");

    //classify basic day/night shoot phases based on the sets
    public static Scene.ShootPhase classify (String rawTime){
        //if no script time, sets to day
        if (rawTime == null) return Scene.ShootPhase.DAY;

        //tokenizing the script time and stores them into a set
        String[] tokens = TextUnits.tokenize(rawTime.toUpperCase(Locale.ROOT));
        Set<String> tokenSet = new HashSet<>(Arrays.asList(tokens));

        //if any of the indefinite time words are found, returns null and is handled later
        if (!Collections.disjoint(tokenSet, CONT_TIMES)) return null;
        //if any tokens match with any of the sets, the shoot phase is returned
        if (!Collections.disjoint(tokenSet, NIGHT_TIMES)) return Scene.ShootPhase.NIGHT;
        return Scene.ShootPhase.DAY;
    }

    public static void resolveTimes(List<Scene> scenes) {
        //stores last definite shoot phase
        Scene.ShootPhase lastDefinitePhase = null;
        for (Scene scene : scenes) {
            Scene.ShootPhase phase = TimeClassifier.classify(scene.getTime());
            //if classified as day/night, sets the shoot phase to scene and becomes the last definite time phase
            if (phase != null) {
                scene.setShootPhase(phase);
                lastDefinitePhase = phase;
            }
            else {
                //if classified as one of the continuous times
                //searches for the last definite script time and sets it to the current scene
                if (lastDefinitePhase != null) {
                    scene.setShootPhase(lastDefinitePhase);
                }
                //if no previous definite script time, sets it to day
                else {
                    scene.setShootPhase(Scene.ShootPhase.DAY);
                }
            }
        }
    }
}
