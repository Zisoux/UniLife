package inhatc.hja.unilife.user.dto;

public class SimpleUserDto {
    private Long id;
    private String username;
	private String userId; // 추가: 사용자 ID
	private String email; // 추가: 이메일
	private String passwordHash; // 추가: 비밀번호

    public SimpleUserDto(Long id, String username, String userId, String email, String passwordHash) {
        this.setId(id);
        this.setUsername(username);
		this.setUserId(userId);
		this.setEmail(email);
		this.setPassword(passwordHash);
    }

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

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPassword(String passwordHash) {
		this.passwordHash = passwordHash; // 비밀번호는 암호화 처리 필요
	}

    // getter 생략 가능
}