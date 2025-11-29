import java.util.*;

public class CharacterClusterer {
    private static final double JW_THRESHOLD = 0.90;
    private static final int LEV_THRESHOLD = 2;

    public Map<String, String> buildCanonicalMap(Set<String> rawNames) {
        Map<String, String> normalizedMap = new HashMap<>();
        //loops through all raw names found
        //normalizes the names
        for (String name : rawNames) {
            //the key is the original name and the value is the normalized form
            normalizedMap.put(name, CharacterExtractor.normalizeName(name).toLowerCase());
        }

        //stores all final cluster of clusters
        //each cluster is a set of normalized names referring to the same character
        List<Set<String>> clusters = new ArrayList<>();
        //keeps track of normalized names already in th cluster and prevents duplicates
        Set<String> visited = new HashSet<>();
        //loops through the normalized names
        for (String n1 : normalizedMap.values()) {
            //if already in the cluster, skip it
            if (visited.contains(n1)) continue;
            //creates a new empty cluster for each character
            Set<String> cluster = new HashSet<>();
            //adds to the cluster and mark as visited
            cluster.add(n1);
            visited.add(n1);
            //loops through the normalized names to compare n1 to all n2 values
            for (String n2 : normalizedMap.values()) {
                if (visited.contains(n2)) continue;
                //computes Jaro-Winkler similarity score (0-1)
                double similarity = JaroWinkler.similarity(n1, n2);
                //computes Levenshtein distance
                int lev = Levenshtein.distance(n1, n2);
                //if the names are similar enough, they are considered as the same character
                if (similarity >= JW_THRESHOLD || lev <= LEV_THRESHOLD) {
                    //adds to the character cluster
                    cluster.add(n2);
                    //adds to visited so it's not clustered again
                    visited.add(n2);
                }
            }
            //add the character cluster to the master cluster
            clusters.add(cluster);
        }
        //creates a map that will hold the normalized name as a key
        // and the chosen canonical normalized name for the cluster
        Map<String, String> canonicalNormalized = new HashMap<>();
        //loops through each cluster of normalized names
        for (Set<String> cluster : clusters) {
            //converts the cluster set into a stream
            String canonical = cluster.stream()
                    //chooses the shortest name
                    .min(Comparator.comparingInt(String::length))
                    //if the cluster is empty, throws an exception
                    .orElseThrow();
            //loops through each element of the character cluster
            for (String member : cluster) {
                //maps every character cluster member to canonical normalized name
                canonicalNormalized.put(member, canonical);
            }
        }
        //final output map with raw name as key and the raw canonical name as the value
        Map <String, String> finalMap = new HashMap<>();
        //loops through the original raw names
        for (String raw : rawNames) {
            //gets the normalized name value from the normalizedMap
            String norm = normalizedMap.get(raw);
            //gets the canonical normalized name for the normalized value
            String canonicalNorm = canonicalNormalized.get(norm);
            //choose the best raw name to represent the cluster
            String bestRaw = rawNames.stream()
                    //keeps all raw names whose normalized form equals the canonical normalized name
                    .filter(r -> CharacterExtractor.normalizeName(r).toLowerCase().equals(canonicalNorm))
                    //picks the shortest raw name among the kept ones
                    .min(Comparator.comparingInt(String::length))
                    //if none match, use the raw name as the canonical
                    .orElse(raw);

            //maps the raw name values to the canonical name
            finalMap.put(raw, bestRaw);
        }
        //returns the final map
        return finalMap;
    }
}
