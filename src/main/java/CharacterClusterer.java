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

        int n = rawNamesList.size();
        //creates a union-find object which makes each name a separate cluster
        UnionFind uf = new UnionFind(n);

        //compares every pair of names
        for (int i = 0; i < n; i ++) {
            for (int j = i + 1; j < n; j++) {
                String a = rawNamesList.get(i);
                String b = rawNamesList.get(j);

                if (a.length() <= 2 || b.length() <= 2) continue;

                Set<String> ta = new HashSet<>(Arrays.asList(TextUnits.tokenize(a)));
                Set<String> tb = new HashSet<>(Arrays.asList(TextUnits.tokenize(b)));

                if (ta.containsAll(tb) || tb.containsAll(ta)) {
                    uf.union(i, j);
                    System.out.println("UNION CONTAINS: " + a + "    <-->    " + b);
                }
                else {
                    //calculates similarity and distance of the candidate and each cluster member
                    double sim = JaroWinkler.similarity(a, b);
                    int lev = Levenshtein.distance(a, b);

                    //sets different thresholds depending on how large the smallest compared word is
                    boolean similar;
                    similar = (sim >= 0.96) && (lev <= 1);

                    //if not similar, the inner loop breaks
                    if (similar) {
                        uf.union(i, j);
                        System.out.println("UNION ALGORITHMS: " + a + "    <-->    " + b);
                    }
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
                    .max(Comparator.comparingInt(s -> s.length()))
                    //if the cluster is empty, throws an exception
                    .orElseThrow();
            System.out.println("-------------------------------------------");
            System.out.println("CLUSTER CANONICAL NAME: " + canonicalRaw);
            System.out.println("Cluster members:");

            //loops through each element of the character cluster
            for (int index : cluster) {
                //sets the raw name, corresponding to the index
                String raw = rawNamesList.get(index);

                System.out.println("raw: [" + raw + "]");
                //creates an output map with the raw and canonical name
                output.put(raw, canonicalRaw);
            }
        }


        return output;
    }
}
