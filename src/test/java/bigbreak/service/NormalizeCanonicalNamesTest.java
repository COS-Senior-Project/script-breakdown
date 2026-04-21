package bigbreak.service;

import bigbreak.dtos.ScheduleDTO;
import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

public class NormalizeCanonicalNamesTest {
    @Test
    void shouldCharacterVariantsToCanonicalName() throws Exception {

        String script = """
        INT. JASON'S LIVING ROOM - DAY
        GEORG (V.O.)
        I am here.
        
        INT. TERRACE - NIGHT
        George comes in.
        
        EXT. YARD - NIGHT
            GEORGE JONAS
         I speak again.
         
        INT/EXT POOL HOUSE - DAY
            MARK
        I am performative.
        
        EST. SUNFLOWER FIELD - DAY
        Introducing MARK.
        
        EXT FOOTBALL FIELD - DAY
        Loves Mark Smith, 24.
        
    """;

        ScriptProcessingService service = new ScriptProcessingService();
        ScheduleDTO schedule = service.processScript(script);

        var scene1 = schedule.getDays().get(0).getScenes().get(0);
        assertTrue(scene1.getCanonicalCharacterNames().contains("GEORGE JONAS"));
        var scene2 = schedule.getDays().get(0).getScenes().get(1);
        assertTrue(scene2.getCanonicalCharacterNames().contains("GEORGE JONAS"));
        var scene3 = schedule.getDays().get(0).getScenes().get(2);
        assertTrue(scene3.getCanonicalCharacterNames().contains("GEORGE JONAS"));

        schedule.getDays().get(0).getScenes().forEach(scene -> {
            if (scene.getCanonicalCharacterNames().contains("GEORGE JONAS")) {
                assertEquals(1, scene.getCanonicalCharacterNames().size());
            }
        });

        var scene4 = schedule.getDays().get(1).getScenes().get(0);
        assertTrue(scene4.getCanonicalCharacterNames().contains("MARK SMITH"));
        var scene5 = schedule.getDays().get(1).getScenes().get(1);
        assertTrue(scene5.getCanonicalCharacterNames().contains("MARK SMITH"));
        var scene6 = schedule.getDays().get(1).getScenes().get(2);
        assertTrue(scene6.getCanonicalCharacterNames().contains("MARK SMITH"));

        schedule.getDays().get(1).getScenes().forEach(scene -> {
            if (scene.getCanonicalCharacterNames().contains("MARK SMITH")) {
                assertEquals(1, scene.getCanonicalCharacterNames().size());
            }
        });
    }
}
