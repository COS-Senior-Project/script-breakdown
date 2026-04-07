package bigbreak;

public class Levenshtein {
    public static int distance (String a, String b) {
        //converts both inputs to lowercase
        a = a.toLowerCase();
        b = b.toLowerCase();

        //creates a matrix for all prefixes of transforming a into b
        // even when the prefix is empty string
        int[][] dp = new int[a.length() + 1][b.length() + 1];

        //initializes the first column of the matrix
        //as if transforming the first a characters
        // into the empty string of a
        for (int i = 0; i <= a.length(); i++) {
            dp[i][0] = i; //cost of deletions
        }

        //initializes the first row of the matrix again
        //as if transforming an empty string of a
        // into the first j characters of b
        for (int j = 0; j <= b.length(); j++) {
            dp[0][j] = j; //cost of insertions
        }

        //loops through each position of string a
        for (int i = 1; i < a.length(); i++) {
            //loops through each position of string b
            for (int j = 1; j <= b.length(); j++) {
                //cost of change
                int cost;
                //if characters are equal
                if (a.charAt(i - 1) == b.charAt(j - 1)) {
                    //cost of change is 0
                    cost = 0;
                } else { //if not the same
                    //cost of change is 1 (substitution of character required)
                    cost = 1;
                }

                //computes matrix values
                //chooses the least edit distance
                dp[i][j] = Math.min(
                        //deletion: checks how much needs to delete
                        // for char of a to become char of b and adds the change cost
                        Math.min(dp[i-1][j] + 1,
                                //insertion: checks how much needs to be inserted
                                // to insert char of a into b and adds the change cost
                                dp[i][j-1] + 1),
                        //substitution: computes the cost so far (without last char)
                        // and adds substitution cost
                        dp[i-1][j-1] + cost
                );
            }
        }
        //returns the bottom-right cell
        //represents the minimum edits to convert a into b
        return dp[a.length()][b.length()];
    }
}
