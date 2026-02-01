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

    public Map<String, String> buildCanonicalMap(Set<String> rawNames) {
        //if the set is empty
        if (rawNames == null || rawNames.isEmpty()) return new HashMap<>();

        Set<String> orderedNames = new LinkedHashSet<>(rawNames);
        List<String> rawNamesList = new ArrayList<>(orderedNames);
        int n = rawNames.size();

        List<String> normalized = new ArrayList<>();
        for (String name : rawNamesList) {
            normalized.add(CharacterExtractor.normalizeName(name).toLowerCase());
        }

        UnionFind uf = new UnionFind(n);

        for (int i = 0; i < n; i ++) {
            for (int j = i + 1; j < n; j++) {
                String candidate = normalized.get(j);

                boolean similarToWholeCluster = true;

                for (int k = 0; k < n; k++) {
                    if (uf.find(k) == uf.find(i)) {
                        String member = normalized.get(k);

                        double sim = JaroWinkler.similarity(member, candidate);
                        int lev = Levenshtein.distance(member, candidate);

                        int minLen = Math.min(member.length(), candidate.length());

                        boolean similar;
                        if (minLen <= 4) {
                            //short names must be almost identical
                            similar = (sim >= 0.98) && (lev <= 1);
                        } else if (minLen <= 7) {
                            similar = (sim >= 0.95) && (lev <= 2);
                        } else {
                            similar = (sim >= 0.92) && (lev <= 3);
                        }

                        if (!similar) {
                            similarToWholeCluster = false;
                            break;
                        }
                    }
                }

                if (similarToWholeCluster) {
                    uf.union(i, j);
                }
            }
        }

        Map<Integer, List<Integer>> clusters = new HashMap<>();
        for (int i = 0; i < n; i++) {
            int root = uf.find(i);
            clusters.computeIfAbsent(root, k -> new ArrayList<>()).add(i);
        }

        //creates a map that will hold the raw name as a key
        // and the chosen canonical name for the cluster
        Map<String, String> output = new HashMap<>();
        //loops through each cluster of normalized names
        for (List<Integer> cluster : clusters.values()) {
            //converts the cluster set into a stream
            String canonicalRaw = cluster.stream()
                    .map(rawNamesList::get)
                    //chooses the longest name
                    .max(Comparator.comparingInt(s -> CharacterExtractor.normalizeName(s).toLowerCase().length()))
                    //if the cluster is empty, throws an exception
                    .orElseThrow();
            //loops through each element of the character cluster
            for (int index : cluster) {
                String raw = rawNamesList.get(index);
                output.put(raw, canonicalRaw);
            }
        }

        return output;

    }
}
