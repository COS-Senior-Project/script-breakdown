package bigbreak;

import org.junit.jupiter.api.Test;
import static org.junit.jupiter.api.Assertions.*;

import java.util.List;

public class ScriptParserTest {
    @Test
    void shouldSplitScenesCorrectly(){
        String script = """
                INT. CORGI CAFE - DAY
                Vili pets the corgi.

                2. EXT. CAPYBARA ZOO - NIGHT
                Gavrail went to steal capybaras.

                17 INT FUNNY CAT BAR - CONTINUOUS
                The cats are funny at the bar.

                EXT HELLO KITTY RETREAT - LATER
                    KITTY
                    Welcome, my dear senior project survivor!

                5                               5
                
                13A
                INT/EXT POOL HOUSE - DAY
                The graduated seniors went to the pool.

                EXT/INT SILVIA'S APARTMENT BUILDING - NIGHT 16
                Silvia went out to investigate.

                18 I/E BALCONY - DAY 18
                Maya and her friends ate watermelon on the balcony.

                19B                   19B
                E/I GARDEN - CONTINUOUS
                Mariyka read a book in the garden.

                OMITTED CASTLE - NIGHT
                Bla bla bla.
                
                21 EST. SUNFLOWER - DAY 22
                A field of sunflowers.
                
                11
                bla
                
                OMITTED HOUSE - NIGTH
                The girl went into the house.
                """;

        String script2 = """
                1               1
                
                EXT. BEACH - DAY
                Vili loves the beach but hates beach sand.
                """;

        ScriptParser parser = new ScriptParser();
        List<Scene> scenes = parser.splitScenes(script);

        assertEquals(9, scenes.size());
        assertEquals("INT CORGI CAFE - DAY", scenes.get(0).getHeading());
        assertEquals(1, scenes.get(0).getSceneIntNumber());
        assertEquals("1", scenes.get(0).getSceneNumber());
        assertEquals("Vili pets the corgi.", scenes.get(0).getContent());
        assertEquals("INT", scenes.get(0).getLocationKeyword());
        assertEquals("CORGI CAFE", scenes.get(0).getLocation());
        assertEquals("DAY", scenes.get(0).getTime());
        assertEquals(1, scenes.get(0).getSceneLength());
        assertEquals("EXT CAPYBARA ZOO - NIGHT", scenes.get(1).getHeading());
        assertEquals("INT FUNNY CAT BAR - CONTINUOUS", scenes.get(2).getHeading());
        assertEquals("EXT HELLO KITTY RETREAT - LATER", scenes.get(3).getHeading());
        assertEquals("INT/EXT POOL HOUSE - DAY", scenes.get(4).getHeading());
        assertEquals("13A", scenes.get(4).getSceneNumber());
        assertEquals("EXT/INT SILVIA'S APARTMENT BUILDING - NIGHT", scenes.get(5).getHeading());
        assertEquals("I/E BALCONY - DAY", scenes.get(6).getHeading());
        assertEquals("E/I GARDEN - CONTINUOUS", scenes.get(7).getHeading());
        assertEquals("EST SUNFLOWER - DAY", scenes.get(8).getHeading());

        ScriptParser parser2 = new ScriptParser();
        List<Scene> scenes2 = parser.splitScenes(script2);
        assertEquals("EXT BEACH - DAY", scenes2.get(0).getHeading());
        assertEquals("1", scenes2.get(0).getSceneNumber());
    }
}
