package bigbreak;

public class JaroWinkler {
    public static double similarity (String s1, String s2) {
        //converts both strings to lower
        s1 = s1.toLowerCase();
        s2 = s2.toLowerCase();

        //if the strings are the same, returns similarity 1
        if (s1.equals(s2)) return 1.0;

        //the maximum distance between characters for them to be considered a match
        //only if within the larger string length / 2 - 1 positions
        int matchDistance = Math.max(s1.length(), s2.length()) / 2 - 1;

        //track which characters have already matched so not to match them twice
        boolean[] s1Matches = new boolean[s1.length()];
        boolean[] s2Matches = new boolean[s2.length()];

        //counts matches
        int matches = 0;
        //counts transpositions - two characters appear in different order
        int transpositions = 0;

        //loops through characters of s1
        for (int i = 0; i < s1.length(); i++) {
            //limits the search only to the allowed matching distance
            int start = Math.max(0, i - matchDistance);
            int end = Math.min(i + matchDistance + 1, s2.length());

            //searches for a matching character in s2
            for (int j = start; j < end; j++) {
                //if s2[j] is already matched, continue to the next s2 char
                if (s2Matches[j]) continue;
                //if characters are different, continue to the next s2 char
                if (s1.charAt(i) != s2.charAt(j)) continue;

                //in case a character matches
                //record the match in the arrays
                s1Matches[i] = true;
                s2Matches[j] = true;
                //increment the counter
                matches++;
                //move to the next s1 character
                break;
            }
        }
        //if no matches at all
        if (matches == 0) return 0.0;

        int k = 0;
        //loops through the s1 characters
        for (int i = 0; i < s1.length(); i++) {
            //skips if char is not matched
            if (!s1Matches[i]) continue;
            //moves k until it finds a matched index in s2
            while (!s2Matches[k]) k++;
            //if the matched characters differ, there is transposition
            if (s1.charAt(i) != s2.charAt(k)) transpositions++;
            //moves to the next s2 char
            k++;
        }

        double m = matches;
        //computes jaro similarity
        double jaro = ((m / s1.length())
                + (m / s2.length())
                + ((m - transpositions / 2.0) / m)) / 3.0;
        //prefix counter
        int prefix = 0;
        //loops through each character of the prefix (at most 4 characters)
        for (int i = 0; i < Math.min(4, Math.min(s1.length(), s2.length())); i++) {
            //if the characters at position i for both s1 and s2 are the same,
            // increment the prefix counter
            if (s1.charAt(i) == s2.charAt(i)) prefix++;
            //if one character differs, break the loop
            else break;
        }
        //winkler adjustment
        return jaro + prefix * 0.1 * (1 - jaro);
    }
}
