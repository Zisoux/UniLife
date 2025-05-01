package inhatc.hja.unilife.user.dto;

public class SimpleUserDto {
    private Long id;
    private String username;

    public SimpleUserDto(Long id, String username) {
        this.setId(id);
        this.setUsername(username);
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

    // getter 생략 가능
}