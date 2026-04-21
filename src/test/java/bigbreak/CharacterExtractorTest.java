package bigbreak;

import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class CharacterExtractorTest {
    @Test
    void shouldExtractSpeakCues(){


        String content = """
        JOHN
        Hello there.

        MARY (V.O.)
        Hi.
        """;

        Scene scene = new Scene(
                1, "1", "INT TEST - DAY",
                content, "INT", "TEST", "DAY", 1
        );

        CharacterExtractor.extractSpeakerCues(content, scene);
    }
}
