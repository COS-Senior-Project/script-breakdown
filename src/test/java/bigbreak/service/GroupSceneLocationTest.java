package bigbreak.service;

import bigbreak.dtos.ScheduleDTO;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

public class GroupSceneLocationTest {
    @Test
    void shouldGroupScenesLocationCorrectly() throws Exception {

        String script = """
        INT. HOUSE - DAY
        They went to the house.

        EXT. BEACH - DAY
        They went to the beach.

        INT. HOUSE - NIGHT
        They returned to the house.

        EXT. BEACH - NIGHT
        They returned to the beach.
    """;

        ScriptProcessingService service = new ScriptProcessingService();
        ScheduleDTO schedule = service.processScript(script);

        List<String> orderedLocations = schedule.getDays().stream()
                .flatMap(day -> day.getScenes().stream())
                .map(scene -> scene.getLocation())
                .toList();

        int transitions = 0;
        for (int i = 1; i < orderedLocations.size(); i++) {
            if (!orderedLocations.get(i).equals(orderedLocations.get(i - 1))) {
                transitions++;
            }
        }

        assertEquals(1, transitions);
    }
}
