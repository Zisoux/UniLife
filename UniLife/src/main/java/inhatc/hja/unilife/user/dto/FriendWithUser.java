package inhatc.hja.unilife.user.dto;

import inhatc.hja.unilife.user.entity.Friend;
import inhatc.hja.unilife.user.entity.User;

public class FriendWithUser {

    private Friend friend;
    private User user;

    public FriendWithUser() {}

    public FriendWithUser(Friend friend, User user) {
        this.friend = friend;
        this.user = user;
    }

    public Friend getFriend() {
        return friend;
    }

    public void setFriend(Friend friend) {
        this.friend = friend;
    }

    public User getUser() {
        return user;
    }
    public String getUsername() {
        return user.getUsername();
    }

    public void setUser(User user) {
        this.user = user;
    }
}
