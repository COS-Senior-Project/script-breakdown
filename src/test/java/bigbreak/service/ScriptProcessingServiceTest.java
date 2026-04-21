package bigbreak.service;

import bigbreak.Scene;
import bigbreak.dtos.ScheduleDTO;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

public class ScriptProcessingServiceTest {
    @Test
    void shouldProcessScriptCorrectly() throws Exception {
        String script = """
                INT. CORGI CAFE - DAY
                Vili walks into the cafe.
                    VILI
                 I want to pet the corgi.

                2. EXT. CAPYBARA ZOO - NIGHT
                    JASON (V.O.)
                  I went to steal all capybaras.

                17 INT FUNNY CAT BAR - CONTINUOUS
                    VILI-AN
                 Look at this funny cats!
                Bella, 23, walks in the bar.

                EXT HELLO KITTY RETREAT - LATER
                    OLD WOMAN
                    Welcome, my dear senior project survivor!
                Introducing SOFIA.

                5                               5
                
                13A
                INT. CORGI CAFE - DAY
                        FEMALE EMPLOYEE
                   Seniors, get out of the pool.

                EXT/INT SILVIA'S APARTMENT BUILDING - DAY 16
                Mark went out to investigate.

                18 I/E BALCONY - DAY 18
                    VILI-AM
                   I am here.

                19B                   19B
                EXT/INT SILVIA'S APARTMENT BUILDING - CONTINUOUS
                        MARK
                    It is time to finish this.

                OMITTED CASTLE - NIGHT
                Bla bla bla.
                
                21 EXT/INT SILVIA'S APARTMENT BUILDING - DAY 22
                A field of sunflowers.
                """;

        ScriptProcessingService serivce = new ScriptProcessingService();
        ScheduleDTO schedule = serivce.processScript(script);
        assertNotNull(schedule);
        assertFalse(schedule.getDays().isEmpty());

        // Check at least one scene exists
        assertFalse(schedule.getDays().get(0).getScenes().isEmpty());

        // Check characters were extracted
        var firstScene = schedule.getDays().get(0).getScenes().get(0);
        //System.out.println(firstScene.getCanonicalCharacterNames());
        assertTrue(firstScene.getCanonicalCharacterNames().contains("VILI-AN"));
        var secondScene = schedule.getDays().get(0).getScenes().get(1);
        assertEquals("INT CORGI CAFE - DAY", secondScene.getHeading());
        assertTrue(secondScene.getCanonicalCharacterNames().contains("FEMALE EMPLOYEE"));
        var thirdScene = schedule.getDays().get(0).getScenes().get(2);
        assertTrue(thirdScene.getCanonicalCharacterNames().contains("JASON"));
        var forthScene = schedule.getDays().get(0).getScenes().get(3);
        assertTrue(forthScene.getCanonicalCharacterNames().contains("BELLA"));
        assertEquals(forthScene.getShootPhase(), Scene.ShootPhase.NIGHT);
        var fifthScene = schedule.getDays().get(0).getScenes().get(4);
        assertTrue(fifthScene.getCanonicalCharacterNames().contains("SOFIA"));
        var sixthScene = schedule.getDays().get(1).getScenes().get(0);
        assertEquals("EXT/INT SILVIA'S APARTMENT BUILDING - DAY", sixthScene.getHeading());
        assertTrue(sixthScene.getCanonicalCharacterNames().contains("MARK"));
        var seventhScene = schedule.getDays().get(1).getScenes().get(1);
        assertEquals("19B", seventhScene.getSceneNumber());
        var eightScene = schedule.getDays().get(1).getScenes().get(2);
        assertEquals("21", eightScene.getSceneNumber());
        var ninthScene = schedule.getDays().get(1).getScenes().get(3);
        assertTrue(ninthScene.getCanonicalCharacterNames().contains("VILI-AN"));
        assertEquals(4, schedule.getDays().get(1).getScenes().size());

    }
}
