package inhatc.hja.unilife.timetable.dto;

import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FreeTimeMatchDTO {
    private Long friendId;
    private String friendUsername;
    private String dayOfWeek;
    private int freeMinutes;
    private List<String> timeRanges;



    public void setTimeRanges(List<String> timeRanges) {
        this.timeRanges = timeRanges;
    }
    public List<String> getTimeRanges() {
        return timeRanges;
    }

    public String getTimeRangeSummary() {
        return String.join(", ", this.timeRanges);
    }

    public String getUsername() {
        return this.friendUsername;
    }

}
