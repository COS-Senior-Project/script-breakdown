package bigbreak.dtos;

import java.util.List;

public class ScheduleDTO {
    private List<DayDTO> days;

    public List<DayDTO> getDays() {
        return days;
    }

    public void setDays(List<DayDTO> days) {
        this.days = days;
    }
}
