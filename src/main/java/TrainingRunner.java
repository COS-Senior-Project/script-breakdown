import opennlp.tools.namefind.*;
import opennlp.tools.util.*;
import opennlp.tools.util.eval.FMeasure;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.Arrays;

public class TrainingRunner {

    public void trainAndEvaluate(String trainFilePath, String testFilePath, String outputModelPath) {
        //creates a file object to point to the train file
        File trainFile = new File(trainFilePath);
        //creates a file object to point to the test file
        //if null, the test and evaluation part is not executed
        File testFile = (testFilePath != null) ? new File(testFilePath) : null;

        try {
            //creates an object which produces an Input Stream
            //only when OpenNLP reads the training data
            InputStreamFactory trainISF = new InputStreamFactory() {
                @Override
                public InputStream createInputStream() throws IOException {
                    //it opens trainFile as a FileInputStream
                    return new FileInputStream(trainFile);
                }
            };

            //training parameters for the model
            TrainingParameters params = new TrainingParameters();
            //number of times the algorithm passes through the data
            params.put(TrainingParameters.ITERATIONS_PARAM, "100");
            //number of times a feature needs to appear in the train file
            //to be included in the character name decision
            params.put(TrainingParameters.CUTOFF_PARAM, "1");

            //reads the file line by line as a string
            ObjectStream<String> lineStream = new PlainTextByLineStream(trainISF, "UTF-8");
            //parses the lineStream object into a NameSample object - the format used for training examples (e.g. <START:PERSON> DR. LECTER <END>)
            //converts from string to NameSample objects lazily (only on demand)
            //makes it memory-efficient
            ObjectStream<NameSample> sampleStream = new NameSampleDataStream(lineStream);
            //uses default feature generators, default BIO codec, and default resources
            TokenNameFinderFactory factory = new TokenNameFinderFactory();

            //training call
            TokenNameFinderModel model = NameFinderME.train(
                    "en", //language (English)
                    "person", //type of training entity
                    sampleStream, //structured training data in NameSample objects
                    params, //training parameter
                    factory //TokenNameFinderFactory value
            );

            //tries to open a file output stream
            try (OutputStream modelOut = new FileOutputStream(outputModelPath)) {
                //writes the model in OpenNLP's binary format
                // so NameFinderME's constructor can later read it
                model.serialize(modelOut);
            }

            System.out.println("Model training completed! Saved at: " + outputModelPath);

            //evaluation if test file exists
            if (testFile != null) {

                //creates an input stream factory
                // lazily creates streams for reading data
                InputStreamFactory testISF = new InputStreamFactory() {
                    @Override
                    public InputStream createInputStream() throws IOException {
                        //opens the file
                        return new FileInputStream(testFile);
                    }
                };

                //converts the string lines of the test line streams
                // into structured data of tokens and labeled spans into the test sample stream
                try (ObjectStream<String> testLineStream = new PlainTextByLineStream(testISF, "UTF-8");
                ObjectStream<NameSample> testSampleStream = new NameSampleDataStream(testLineStream)){
                    //creates a NameFinderME object using the trained model
                    NameFinderME nameFinder = new NameFinderME(model);
                    //creates an object to track evaluation statistics
                    FMeasure fMeasure = new FMeasure();

                    NameSample sample;
                    //loops through the name samples
                    while (( sample = testSampleStream.read()) != null) {
                        //extracts the tokens in the sample
                        String[] tokens = sample.getSentence();
                        //predicts the spans (start index to end index) of
                        // character names in the token array
                        Span[] predictedSpans = nameFinder.find(tokens);
                        //compares the true spans to the predicted spans
                        //updates precision, recall, and f1 scores
                        fMeasure.updateScores(sample.getNames(), predictedSpans);

                        if (!Arrays.equals(predictedSpans, sample.getNames())) {
                            System.out.println("TEXT: " + String.join(" ", tokens));
                            System.out.println("PREDICTED: " + Arrays.toString(predictedSpans));
                            System.out.println("EXPECTED: " + Arrays.toString(sample.getNames()));
                            System.out.println("---------------------------");
                        }
                    }

                    //precision is the fraction of actually correct names compared to all predicted
                    System.out.println("Precision: " + fMeasure.getPrecisionScore());
                    //recall is the fraction of correctly predicted names compared to all that should have been correct
                    System.out.println("Recall: " + fMeasure.getRecallScore());
                    //f1 is a harmonic mean of precision and recall
                    System.out.println("F1: " + fMeasure.getFMeasure());
                }
            }
        } catch (IOException io) {
            System.out.println("Input/output exception: " + io.getMessage());
        } catch (Exception e) {
            System.out.println("General exception: " + e.getMessage());
        }
    }
}
