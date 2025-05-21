package inhatc.hja.unilife.timetable.dto;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public class CourseBlockDTO {
    private Long courseId;
    private String courseName;
    private String dayOfWeek;
    private int hour;
    private double topPercent; // 칸 안에서 시작 위치
    private double heightPercent; // 칸 안에서 차지하는 높이 비율
    private String startTime;
    private String endTime;
    private boolean first; // 🔥 이미 있음
    private double leftPercent;
    private int startHour;
    private int endHour;
    private int topPx;
    private int heightPx;
    public int getTopPx() { return topPx; }
    public void setTopPx(int topPx) { this.topPx = topPx; }

    public int getHeightPx() { return heightPx; }
    public void setHeightPx(int heightPx) { this.heightPx = heightPx; }
    public int getStartHour() {
        return startHour;
    }

    public void setStartHour(int startHour) {
        this.startHour = startHour;
    }

    public int getEndHour() {
        return endHour;
    }

    public void setEndHour(int endHour) {
        this.endHour = endHour;
    }

    // 커스텀 생성자 (추가)
    public CourseBlockDTO(Long id, String courseName, String dayOfWeek, int hour, double topPercent, double heightPercent, String startTime, String endTime, boolean isMyCourse) {
        this.courseId = id;
        this.courseName = courseName;
        this.dayOfWeek = dayOfWeek;
        this.hour = hour;
        this.topPercent = topPercent;
        this.heightPercent = heightPercent;
        this.startTime = startTime;
        this.endTime = endTime;
        this.first = isMyCourse;  // 여기 수정!
    }

    // 왼쪽 퍼센트 게터/세터
    public double getLeftPercent() {
        return leftPercent;
    }

    public void setLeftPercent(double leftPercent) {
        this.leftPercent = leftPercent;
    }

    // first 필드 세터 (게터는 @Getter로 자동)
    public void setFirst(boolean first) {
        this.first = first;
    }
    private double widthPercent;

    public double getWidthPercent() {
        return widthPercent;
    }

    public void setWidthPercent(double widthPercent) {
        this.widthPercent = widthPercent;
    }
}

