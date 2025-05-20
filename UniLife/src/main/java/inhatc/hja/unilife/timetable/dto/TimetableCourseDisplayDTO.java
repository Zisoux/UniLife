package inhatc.hja.unilife.timetable.dto;

import java.time.LocalTime;

import inhatc.hja.unilife.timetable.entity.TimetableCourse;

public class TimetableCourseDisplayDTO {
    private TimetableCourse course;
    private double topPercent;
    private double heightPercent;

    public TimetableCourseDisplayDTO(TimetableCourse course) {
        this.course = course;

        LocalTime start = course.getStartTime();
        LocalTime end = course.getEndTime();

        int startMinutes = start.getHour() * 60 + start.getMinute();
        int endMinutes = end.getHour() * 60 + end.getMinute();

        int hourStart = start.getHour() * 60;

        this.topPercent = ((startMinutes - hourStart) / 60.0) * 100;          // 시작 위치 (비율)
        this.heightPercent = ((endMinutes - startMinutes) / 60.0) * 100;      // 높이 비율
    }

    public TimetableCourse getCourse() { return course; }
    public double getTopPercent() { return topPercent; }
    public double getHeightPercent() { return heightPercent; }
}
