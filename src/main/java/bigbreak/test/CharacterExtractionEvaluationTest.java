package bigbreak.test;

import bigbreak.Scene;

import java.util.*;

public class CharacterExtractionEvaluationTest {
    public static void evaluate(List<SceneTestData> groundTruth, List<Scene> predicted) {
        Map<Integer, Scene> predictedMap = new HashMap<>();
        for (Scene s: predicted) {
            predictedMap.put(s.getSceneIntNumber(), s);
        }
        int truePositive = 0;
        int falsePositive = 0;
        int falseNegative = 0;

        for (SceneTestData gt : groundTruth) {
            Scene predScene = predictedMap.get(gt.sceneNumber);

            Set<String> gtCharacters = gt.characters;
            Set<String> predCharacters = predScene != null ? predScene.getCanonicalCharacterNames() : new HashSet<>();

            for (String c : predCharacters) {
                if (gtCharacters.contains(c)) {
                    truePositive++;
                } else {
                    falsePositive++;
                }
            }

            for (String c : gtCharacters) {
                if (!predCharacters.contains(c)) {
                    falseNegative++;
                }
            }
        }

        double precision = (double) truePositive / (truePositive + falsePositive);
        double recall = (double) truePositive / (truePositive + falseNegative);
        double f1 = 2 * precision * recall / (precision + recall);

        System.out.println("True Positives: " + truePositive);
        System.out.println("False Positives: " + falsePositive);
        System.out.println("False Negatives: " + falseNegative);
        System.out.println("Precision: " + precision);
        System.out.println("Recall: " + recall);
        System.out.println("F1: " + f1);
    }
}
