import java.util.*;

public class CharacterClusterer {
    //private static final double JW_THRESHOLD = 0.95;
    //private static final int LEV_THRESHOLD = 3;

    private static class UnionFind {
        private final int[] parent;

        public UnionFind(int size) {
            parent = new int[size];
            for (int i = 0; i < size; i++) parent[i] = i;
        }

        public int find (int x) {
            if (parent[x] != x) {
                parent[x] = find(parent[x]);
            }
            return parent[x];
        }

        public void union(int a, int b) {
            int rootA = find(a);
            int rootB = find(b);
            if (rootA != rootB) {
                parent[rootB] = rootA;
            }
        }
    }
    //graph clustering with each name in the cluster matched with all those in the cluster beforehand
    public Map<String, String> buildCanonicalMap(LinkedHashSet<String> rawNames) {
        //if the set is empty, nothing to cluster
        if (rawNames == null || rawNames.isEmpty()) return new HashMap<>();

        //Set<String> orderedNames = new LinkedHashSet<>(rawNames);
        //turns the linked hash set into a list with indices
        List<String> rawNamesList = new ArrayList<>(rawNames);
        //int n = rawNamesList.size();

        //list to store normalized names for comparison
        List<String> normalized = new ArrayList<>();

        for (String name : rawNamesList) {
            normalized.add(CharacterExtractor.normalizeName(name).toUpperCase(Locale.ROOT));
            //normalized.add(CharacterExtractor.normalizeName(name).toLowerCase());
        }

        int n = normalized.size();
        //creates a union-find object which makes each name a separate cluster
        UnionFind uf = new UnionFind(n);

        //compares every pair of names
        for (int i = 0; i < n; i ++) {
            for (int j = i + 1; j < n; j++) {
                String candidate = normalized.get(j);

                boolean similarToWholeCluster = true;

                //loops through every name and checks if it is already part of the same cluster as i
                for (int k = 0; k < n; k++) {
                    if (uf.find(k) == uf.find(i)) {
                        //sets a member of the cluster
                        String member = normalized.get(k);

                        //calculates similarity and distance of the candidate and each cluster member
                        double sim = JaroWinkler.similarity(member, candidate);
                        int lev = Levenshtein.distance(member, candidate);

                        //gets the size of the shorter word of the compared
                        int minLen = Math.min(member.length(), candidate.length());

                        //sets different thresholds depending on how large the smallest compared word is
                        boolean similar;
                        if (minLen <= 4) {
                            //short names must be almost identical
                            similar = (sim >= 0.98) && (lev <= 1);
                        } else if (minLen <= 7) {
                            similar = (sim >= 0.95) && (lev <= 2);
                        } else {
                            similar = (sim >= 0.92) && (lev <= 3);
                        }
                        //if not similar, the inner loop breaks
                        if (!similar) {
                            similarToWholeCluster = false;
                            break;
                        }
                    }
                }

                //if candidate is similar to all the members, it joins the cluster
                if (similarToWholeCluster) {
                    System.out.println("UNION: " + rawNamesList.get(i) + " <-> " + rawNamesList.get(j));
                    uf.union(i, j);
                }
            }
        }

        //builds the actual clusters from the parent pointers tree
        Map<Integer, List<Integer>> clusters = new HashMap<>();
        //goes through each name
        for (int i = 0; i < n; i++) {
            //find the actual cluster leader (the parent above all)
            int root = uf.find(i);
            //if the cluster already exists, add i
            //if not, create a new cluster
            clusters.computeIfAbsent(root, k -> new ArrayList<>()).add(i);
        }

        //creates a map that will hold the raw name as a key
        // and the chosen canonical name for the cluster
        Map<String, String> output = new HashMap<>();
        //loops through each cluster
        for (List<Integer> cluster : clusters.values()) {
            //converts the cluster set into a stream
            String canonicalRaw = cluster.stream()
                    .map(rawNamesList::get)
                    //chooses the longest name
                    .max(Comparator.comparingInt(s -> CharacterExtractor.normalizeName(s).toUpperCase(Locale.ROOT).length()))
                    //if the cluster is empty, throws an exception
                    .orElseThrow();
            System.out.println("-------------------------------------------");
            System.out.println("CLUSTER CANONICAL NAME: " + canonicalRaw);
            System.out.println("Cluster members:");

            //loops through each element of the character cluster
            for (int index : cluster) {
                //sets the raw name, corresponding to the index
                String raw = rawNamesList.get(index);
                String norm = normalized.get(index);

                System.out.println("raw: [" + raw + "]  normalized: [" + norm + "]");
                //creates an output map with the raw and canonical name
                output.put(raw, canonicalRaw);
            }
        }


        return output;
    }
}
