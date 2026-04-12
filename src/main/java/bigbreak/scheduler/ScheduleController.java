package bigbreak.scheduler;

import bigbreak.*;
import bigbreak.dtos.ScheduleDTO;
import bigbreak.service.ScriptProcessingService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;

@CrossOrigin(origins = "http://localhost:5173")
@RestController
@RequestMapping("/api")
public class ScheduleController {

    private final ScriptProcessingService service;

    public ScheduleController(ScriptProcessingService service) {
        this.service = service;
    }
    @PostMapping("/schedule")
    public ScheduleDTO createSchedule(@RequestParam("file") MultipartFile file)  throws Exception {
        String script = new String(file.getBytes(), StandardCharsets.UTF_8);
        return service.processScript(script);
    }

    @PostMapping("/update-schedule")
    public ScheduleDTO updateSchedule(@RequestBody ScheduleDTO schedule) {
        return service.updateSchedule(schedule);
    }


}
