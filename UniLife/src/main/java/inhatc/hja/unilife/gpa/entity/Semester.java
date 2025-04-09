package inhatc.hja.unilife.gpa.entity;

public enum Semester {
    FIRST_YEAR_FIRST_SEMESTER("1-1"),
    FIRST_YEAR_SECOND_SEMESTER("1-2"),
    SECOND_YEAR_FIRST_SEMESTER("2-1"),
    SECOND_YEAR_SECOND_SEMESTER("2-2"),
    THIRD_YEAR_FIRST_SEMESTER("3-1"),
    THIRD_YEAR_SECOND_SEMESTER("3-2"),
    FOURTH_YEAR_FIRST_SEMESTER("4-1"),
    FOURTH_YEAR_SECOND_SEMESTER("4-2");

    private final String semesterCode;

    // 생성자
    Semester(String semesterCode) {
        this.semesterCode = semesterCode;
    }

    // 학기 코드 반환
    public String getSemesterCode() {
        return semesterCode;
    }

    // semesterCode에 맞는 enum 값을 반환하는 메서드
    public static Semester fromString(String semesterCode) {
        for (Semester semester : Semester.values()) {
            if (semester.getSemesterCode().equals(semesterCode)) {
                return semester;
            }
        }
        throw new IllegalArgumentException("Unknown semester code: " + semesterCode);
    }
}
