package bigbreak;

import opennlp.tools.namefind.NameFinderME;
import opennlp.tools.namefind.TokenNameFinderModel;
import opennlp.tools.util.Span;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

//extracts character mentions from tokens using an OpenNLP NER model
public class ExtractorPostTraining {
    //holds the loaded NER model
    private final TokenNameFinderModel model;

    //the actual ML object that performs the name detection
    private final NameFinderME nameFinder;

    //constructor using the model as the input
    public ExtractorPostTraining(TokenNameFinderModel model, NameFinderME nameFinder) {
        this.model = model;
        //creates NER inference engine using the model
        //the object actually predicts the spans
        this.nameFinder = new NameFinderME(model);
    }

    //takes an array of tokens
    //returns a list of CharacterMentions for the model detected characters
    public List<CharacterMention> extractMentions (String[] tokens) {
        //runs the model on the tokens and returns an array of span objects (index ranges)
        Span[] spans = nameFinder.find(tokens);
        //converts the spans into CharacterMention objects
        return convertSpansToMentions(tokens, spans);
    }
    //private helper method - converts OpenNLP's spans into CharacterMention objects
    private List<CharacterMention> convertSpansToMentions (String[] tokens, Span[] spans) {
        //empty list to store the results
        List<CharacterMention> mentions = new ArrayList<>();
        //loops through all the spans
        for (Span span : spans) {
            //extracts the start and end token indices from the span
            int start = span.getStart();
            int end = span.getEnd();
            //converts the span token into a single string
            String raw = String.join(" ", Arrays.copyOfRange(tokens, start, end));
            //creates a new CharacterMention object and adds it to the lsit
            mentions.add(new CharacterMention(raw, start, end, span.getProb()));
        }
        //returns the final list
        return mentions;
    }
}
