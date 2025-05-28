package inhatc.hja.unilife.user.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

public class UserDto {

    private Long id; // 유저 ID

    @NotBlank(message = "아이디는 필수입니다.")
    private String userId;

    @NotBlank(message = "이름은 필수입니다.")
    private String username;

    @NotBlank(message = "학과는 필수입니다.")
    private String department;

    @NotBlank(message = "이메일은 필수입니다.")
    @Email(message = "올바른 이메일 형식이어야 합니다.")
    private String email;

    @NotBlank(message = "비밀번호는 필수입니다.")
    @Size(min = 4, message = "비밀번호는 최소 4자 이상이어야 합니다.")
    private String password;

    @NotBlank(message = "비밀번호 확인은 필수입니다.")
    private String passwordck;

    @NotBlank(message = "이메일 인증 여부를 확인할 수 없습니다.")
    private String emailVerified;



    public Long getId() {
        return id;
    }
    public void setId(Long id) {
        this.id = id;
    }

    public String getUserId() {
        return userId;
    }
    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getUsername() {
        return username;
    }
    public void setUsername(String username) {
        this.username = username;
    }

    public String getDepartment() {
        return department;
    }
    public void setDepartment(String department) {
        this.department = department;
    }

    public String getEmail() {
        return email;
    }
    public void setEmail(String email) {
        this.email = email;
    }

    public String getPassword() {
        return password;
    }
    public void setPassword(String password) {
        this.password = password;
    }

    public String getPasswordck() {
        return passwordck;
    }
    public void setPasswordck(String passwordck) {
        this.passwordck = passwordck;
    }

    public String getEmailVerified() {
        return emailVerified;
    }
    public void setEmailVerified(String emailVerified) {
        this.emailVerified = emailVerified;
    }
}
