package inhatc.hja.unilife.user.dto;

public class SimpleUserDto {
    private Long id;
    private String username;
    private String userId; // 학번
    private String email;

    public SimpleUserDto(Long id, String username, String userId, String email) {
        this.id = id;
        this.username = username;
        this.userId = userId;
        this.email = email;
    }

    // Getter & Setter
    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }
}
