package bigbreak.scheduler;

import bigbreak.*;
import bigbreak.service.ScriptProcessingService;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.nio.charset.StandardCharsets;
import java.util.List;

@RestController
@RequestMapping("/api")
public class ScheduleController {

    private final ScriptProcessingService service;

    public ScheduleController(ScriptProcessingService service) {
        this.service = service;
    }
    @PostMapping("/schedule")
    public List<ShootingDay> createSchedule(@RequestParam("file") MultipartFile file)  throws Exception {
        String script = new String(file.getBytes(), StandardCharsets.UTF_8);
        return service.processScript(script);

    }
}
