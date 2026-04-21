package bigbreak.service;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;
import java.nio.file.Files;
import java.nio.file.Path;

public class ProcessScriptsUnder20s {
    @Test
    void shouldProcessScriptUnder20Seconds() throws Exception {

        ScriptProcessingService service = new ScriptProcessingService();

        String script = Files.readString(Path.of("src/main/resources/scripts/eternal-sunshine.txt"));

        long start = System.nanoTime();
        service.processScript(script);
        long duration = (System.nanoTime() - start) / 1_000_000;
        System.out.println("Processing took: " + duration + "ms");
        assertTrue(duration < 20000,
                "Processing took too long: " + duration + " ms");
    }
}
