import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.util.Span;

import java.io.IOException;
import java.io.BufferedWriter;
import java.io.FileWriter;
import java.util.*;
import java.util.regex.*;

public class CharacterExtractor {

    //regex that captures multi-word uppercase names, group1 = name, group2 = parenthetical (optional)
    private static final Pattern MULTI_WORD_NAME = Pattern.compile("\\b((?:[A-Z][A-Z0-9'’\\.\\-]*)" +
            "(?:\\s+[A-Z][A-Z0-9'’\\.\\-]*)*)\\b(?:\\s*\\(([^)]*)\\))?");

    private static final Pattern NAME_WITH_PAREN = Pattern.compile("^\\s*([A-Z][A-Z0-9'’\\.\\-]*)\\s*\\((V\\.O\\.|O\\.S\\.|CONT'D)\\)\\s*$");
    //pattern looks for a string starting with one upper-cased letter
    // followed by more upper-cased letters, digits, apostrophes, periods, or dashes
    private static final Pattern NAME_TOKEN = Pattern.compile("^[A-Z][A-Z0-9'’\\.\\-]*$");
    //patterns looks for a person indicating uppercase adjective followed by one or two uppercase words
    private static final Pattern PERSON_ADJECTIVE = Pattern.compile("\\b(MALE|FEMALE|YOUNG|OLD|MIDDLE-AGED|MIDDLE AGED|BLACK|WHITE|ASIAN|CAUCASIAN|LATIN)(?:\\s+[A-Z][A-Z]+){1,2}\\b");
    //pattern looks for a person noun proceeded by an uppercase word
    private static final Pattern PERSON_NOUN = Pattern.compile("(\\b([A-Z]+)\\s+)?(MAN|WOMAN|BOY|GIRL|CHILD|TODDLER|TEENAGER|ADULT|ELDER)\\b");
    //pattern looks for a person's name when introducing characters
    private static final Pattern INLINE_INTRO = Pattern.compile("\\b(?:This is|Enter|Entering|Introducing)\\s+([A-Z][A-Z0-9'’\\.\\-]*(?:\\s+[A-Z][A-Z0-9'’\\.\\-]*){0,2})(?=\\s|$|,|\\.)",
            Pattern.CASE_INSENSITIVE);
    //set of black list words that are definitely not names
    private static final Set<String> BLACK_LIST = new HashSet<>(Arrays.asList(
            "A","AN","AND","OR","BUT","FOR","TO","IN","ON","AT","IS","ARE","WAS","WERE",
            "BEEN","HAVE","HAS","HAD","DO","DOES","DID","WILL","WOULD","SHALL","SHOULD","MAY",
            "MIGHT","CAN","COULD","NOT","NO","AS","THAN","IF","WHEN","WHILE","HOW","WHAT", "WHICH", "SHOT", "MOVING",
            "BOOM","BANG","CRASH","RUMBLE","SOUND","NOISE","FX","CUT","CUTTING","CUTS","CUTS TO","CUT TO",
            "CLOSE","CLOSES","CLOSE ON","PAN","PANNING","ZOOM","DOLLY","ANGLE","HER","HIS","THEM","THEMSELVES",
            "POV","BETWEEN","CUTTING","FX","SFX","CROWD","INT","EXT","OMITTED","DAY","NIGHT","SAME","TIME",
            "CONTINUOUS","PART","END","SCENE","PAGE","CLOSE","WIDE","MIDDLE","POV","OMITTED","CONTINUED",
            "PULLING", "PULLING BACK", "PUSHING", "SLOW MOTION", "FAST FORWARD", "FLASHBACK", "ON THE SCREEN",
            "SCREEN", "WITH"));

    //set of stage verbs that are definitely not names
    private static final Set <String> STAGE_VERBS = new HashSet<>(Arrays.asList(
            "CUT","PAN","ZOOM","DOLLY","TRACK","FADE","SMASH","MATCH","WIPE"
    ));
    //extracts names above dialogue
    public static List <Character> extractSpeakerCues(String content, Scene scene, NameDatabase nameDb) {
        //creates a list of Character objects for the names obtained
        List<Character> result = new ArrayList<>();
        //splits the lines into an array but keeps formatting
        String[] lines = content.split("\n", -1);
        //loops through each line
        for (int i = 0; i < lines.length; i++) {
            //single line
            String line = lines[i].trim();
            //if line is empty, move to the next one
            if (line.isEmpty()) continue;

            //tries to match the character speaking above dialogue line
            Matcher matcherMultiWord = MULTI_WORD_NAME.matcher(line);
            //tries to match the character above dialogue with V.O. or O.S.
            Matcher matcherParen = NAME_WITH_PAREN.matcher(line);
            //if it finds a character with V.O.\O.S.
            if (matcherParen.find()) {
                //takes the character name only
                String rawName = matcherParen.group(1);
                //normalizes the name
                String normalized = normalizeName(rawName);
                //if the name is not included in the words that are clearly not names,
                // but often are all uppercase (the sets of the black list and the stage verbs)
                if (isStopPhrase(normalized)) continue;
                //sets base confidence level
                //higher because almost all names before V.O.\O.S. are character names
                double confidence = 0.7;
                //the confidence increases if the name is found in the name files
                confidence += nameDb.confidenceBoostMatchFile(normalized);
                //makes sure that the next line isn't after the last line (out of bounds)
                String nextLine = (i + 1 < lines.length) ? lines[i+1] : "";
                //the candidate name values are added as a Character object to the results
                result.add(new Character(
                        scene.getSceneIntNumber(),
                        scene.getSceneNumber(),
                        normalized,
                        "SPEAKER_WITH_VO_OS",
                        safeSnippet(lines[i] + "/" + nextLine),
                        Math.min(1.0, confidence),
                        nameDb
                ));
            }
            //if the line looks like a character cue line and a match is found
            else if (isCharacterCue(line) && matcherMultiWord.find()) {
                //j will be the index of the line following i
                int j = i + 1;
                //if the following line is empty, and it is not after the last,
                // move to the one after
                while (j < lines.length && lines[j].trim().isEmpty()) j++;
                //if the following line is not after the last, and
                //it is not a character cue
                if (j < lines.length && !isCharacterCue(lines[j])) {
                    //the raw name found by the regex matches to its group 1
                    String rawName = matcherMultiWord.group(1);
                    //normalizes the raw name
                    String normalized = normalizeName(rawName);
                    //if the name is not included in the words that are clearly not names,
                    // but often are all uppercase (the sets of the black list and the stage verbs)
                    if (isStopPhrase(normalized)) continue;
                    //if all those rules are met, the confidence starts from 40%
                    double confidence = 0.4;
                    //the confidence increases if the name is found in the name files
                    confidence += nameDb.confidenceBoostMatchFile(normalized);
                    //the candidate name values are added as a Character object to the results
                    result.add(new Character(
                            scene.getSceneIntNumber(),
                            scene.getSceneNumber(),
                            normalized,
                            "SPEAKER_ABOVE_DIALOGUE",
                            safeSnippet(lines[i] + "/" + lines[j]),
                            Math.min(1.0, confidence),
                            nameDb
                    ));
                }
            }
        }
        //returns all results from the speaker above dialogue + file names check
        return result;
    }

    //extracts names that are not above dialogue but still character names
    public static List <Character> extractInlineName(String content, Scene scene, NameFinderME nameFinder, NameDatabase nameDb) {
        //list to store all the Character objects fitting the criteria
        List <Character> result = new ArrayList<>();
        //if there is no content
        if (content == null || content.trim().isEmpty()) return result;
        //string builder to hold all the content that has not been covered by the extractSpeakerCues extractor
        StringBuilder contentInline = new StringBuilder();
        //splits the regular content into lines without losing formatting
        String[] lines = content.split("\n", -1);
        //loops through all lines
        for (String line : lines) {
            //replaces V.O.\O.S. with "" so that it cn fit into the isCharacterCue
            String withoutPostfix = line.replaceAll("\\s*\\((V\\.O\\.|O\\.S\\.|CONT'D)\\)$", "").trim();

            //if the lines without V.O.\O.S. do not fit into the isCharacterCue,
            //they are appended to the new content
            //in this way, the new content omits all covered by the extractCharacterCues extractor
            if (!isCharacterCue(withoutPostfix)) {
                contentInline.append(withoutPostfix).append("\n");
            }
        }

        //tokenizes the text
        String[] contentTokens = TextUnits.tokenize(contentInline.toString());

        //a sequence of tokens that NameFinderME considers a person's name
        //a sequence of tokens that NameFinderME considers a person's name
        Span[] spans = nameFinder.find(contentTokens);
        //the confidence score returned by NameFinderME
        double[] probs = nameFinder.probs(spans);

        //loops through the span of candidate names
        for (int i = 0; i < spans.length; i++) {
            //current span
            Span s = spans[i];
            StringBuilder sb = new StringBuilder();

            //loops through each word of the name found by NameFinderME
            for (int t = s.getStart(); t < s.getEnd(); t++) {
                //puts spaces between the word names
                if (t > s.getStart()) sb.append(" ");
                //adds words to the string builder
                sb.append(contentTokens[t]);
            }

            String candidate = sb.toString();
            //checks if the candidate name is contained in the black list set or
            //the stage verbs set
            if (BLACK_LIST.contains(candidate.toUpperCase(Locale.ROOT)) ||
                    STAGE_VERBS.contains(candidate.toUpperCase(Locale.ROOT))) continue;
            ////builds a context snippet using 5 tokens before the candidate's name and 5 tokens after
            String snippet = safeSnippet(String.join(" ", Arrays.copyOfRange(contentTokens, Math.max(0, s.getStart() - 5),
                    Math.min(contentTokens.length, s.getEnd() + 5))));
            //evaluates confidence score according to NameFinderME
            double confidence = probs[i];
            //checks if the names are part of the name files and adds confidence if true
            confidence += nameDb.confidenceBoostMatchFile(candidate);
            //assigns confidence no larger than 1
            confidence = Math.min(1.0, confidence);

            //creates a new Character object in the results for the candidate's name mention
            result.add(new Character(
                    scene.getSceneIntNumber(),
                    scene.getSceneNumber(),
                    candidate,
                    "NAMEFINDER_ME",
                    snippet,
                    confidence,
                    nameDb
            ));
        }
        //returns all results of the NameFinderME + file names check
        return result;
    }

    //extractor to extract characters that include a person-related adjective or noun
    public static List <Character> extractPersonWord (String content, Scene scene) {
        //list to store all the Character objects fitting the criteria
        List <Character> result = new ArrayList<>();
        //if there is no content
        if (content == null || content.trim().isEmpty()) return result;
        //sets static confidence score
        // because the match cannot be contained in the name files
        double confidence = 0.7;
        //splits the lines into an array keeping the formatting
        String[] lines = content.split("\n", -1);
        //loops through the lines
        for (String line : lines) {
            //trims the line
            String trimmed = line.trim();
            //if nothing left, goes to the next iteration
            if (trimmed.isEmpty()) continue;

            //creates a matcher object for an uppercase adjective word combination
            // that is related to a personal characteristic
            Matcher matchAdj = PERSON_ADJECTIVE.matcher(trimmed);
            //creates a matcher object for an uppercase noun word combination
            // that is related to a person's identity
            Matcher matchNoun = PERSON_NOUN.matcher(trimmed);

            //if it finds the adjective word sequence, it creates a new character entry
            if (matchAdj.find()) {
                result.add(new Character(
                        scene.getSceneIntNumber(),
                        scene.getSceneNumber(),
                        matchAdj.group(),
                        "PERSON_ADJ",
                        safeSnippet(trimmed),
                        confidence,
                        null
                ));
            }
            //if it finds a word noun sequence, it creates a new character
            //using else if in case of a "YOUNG WOMAN" match
            else if (matchNoun.find()) {
                result.add(new Character(
                        scene.getSceneIntNumber(),
                        scene.getSceneNumber(),
                        matchNoun.group(),
                        "PERSON_NOUN",
                        safeSnippet(trimmed),
                        confidence,
                        null
                ));
            }
        }
        //returns the list of characters
        return result;
    }

    //extracts characters that are first introduced
    public static List <Character> extractIntroCharacter (String content, Scene scene) {
        //list of characters to store the matches
        List <Character> result = new ArrayList<>();
        //if no content, it returns the list
        if (content == null || content.trim().isEmpty()) return result;
        //sets confidence score
        double confidence = 0.7;
        //splits the lines into an array of lines
        String[] lines = content.split("\n", -1);
        //loops through each line
        for (String line : lines) {
            //trims the line
            String trimmed = line.trim();
            //if empty, moves to the next iteration
            if (trimmed.isEmpty()) continue;

            //creates a matcher object to match the introduction of character
            Matcher matchIntro = INLINE_INTRO.matcher(trimmed);
            //if matched, adds the character to the result list
            if (matchIntro.find()) {
                result.add(new Character(
                        scene.getSceneIntNumber(),
                        scene.getSceneNumber(),
                        matchIntro.group(1),
                        "PERSON_INTRO",
                        safeSnippet(trimmed),
                        confidence,
                        null
                ));
            }
        }
        //returns the result list
        return result;
    }

    //normalizes name formatting
    public static String normalizeName(String raw){
        if (raw == null) return "";
        //converts to readable apostrophes and multiple spaces to just one space
        String s = raw.replaceAll("’", "'").replaceAll("\\s+", " ").trim();
        //removes (V.O.), (O.S.), and (CONT'D)
        s = s.replaceAll("\\s*\\((V\\.O\\.|O\\.S\\.|CONT'D)\\)\\s*$", "").trim();
        //removes stray trailing punctuation in the beginning and at the end of the string
        s = s.replaceAll("^[^A-Z0-9]+", "").replaceAll("[^A-Z0-9]+$", "");
        //removes any trailing scene-number formatting that might accidentally go into the name
        s = s.replaceAll("\\s+\\d+[A-Z]?\\.?\\s*$", "").trim();
        return s;
    }

    //checks if the line is a character cue line
    private static boolean isCharacterCue(String line){
        //Ignores very short lines with less than two characters/symbols
        if (line.length() < 2) return false;

        //tokenizes the line
        String[] toks = TextUnits.tokenize(line);

        if (toks.length == 0) return false;
        //name token counter
        int nameTokens = 0;
        //total token counter equals the total length of all tokens
        int tokenCount = toks.length;

        //loops through each token of the line
        for (String t : toks){
            //normalizes token for matching (strip punctuation that tokenizer may still keep)
            String clean = t.replaceAll("[^A-Z0-9'’\\.\\-]", "");
            //if the token matches the name token regex
            if (NAME_TOKEN.matcher(clean).matches()){
                //the name token counter increases
                nameTokens++;
            }
        }

        //if there are no name tokens, returns false
        if (nameTokens == 0) return false;

        //if the name token count is at least 80% of the total count and
        //the total token count is at most 5
        if (nameTokens >= Math.ceil(tokenCount * 0.8) && tokenCount <= 5){
            //rejects if non-word specific punctuation appears
            if (line.matches(".*[!\\?\\,\\.;:].*")) return false;
            return true;
        }
        return false;
    }

    //checks if the uppercase name is a black list or a stage verb
    private static boolean isStopPhrase(String candidateName){
        //if there is no value for the name
        if (candidateName == null || candidateName.isEmpty()) return true;
        //tokenizes the parts of the name
        String[] parts = TextUnits.tokenize(candidateName);
        //loops through every token in the name
        for (String p : parts){
            //keeps in up only uppercase characters
            String up = p.replaceAll("[^A-Z]", "");
            //if there is nothing in up, move to the next token
            if (up.isEmpty()) continue;
            //if up is contained in the black list or stage verbs set,
            //it returns it is a stop phrase
            if (BLACK_LIST.contains(up) || STAGE_VERBS.contains(up)){
                return true;
            }
        }
        //if nothing returns true, then it is not a stop phrase
        return false;
    }

    //Create a safe snippet for CSV (collapse newlines and escape quotes)
    private  static String safeSnippet(String raw){
        if (raw == null) return "";
        String oneLine = raw.replaceAll("\\s+", " ").trim();
        //Espace any double quotes inside snippet by doubling them (CSV-compliant)
        String esc = oneLine.replace("\"", "\"\"");
        return esc;
    }

}

