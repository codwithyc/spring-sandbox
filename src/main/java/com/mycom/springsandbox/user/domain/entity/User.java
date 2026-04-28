package com.mycom.springsandbox.user.domain.entity;
import com.mycom.springsandbox.user.domain.vo.UserInfo;
import com.mycom.springsandbox.user.domain.vo.UserRelationCounter;

import java.util.Objects;

public class User {

    private final long id; // 내부 식별자
    private final String userCode; //외부 공개 식별자

    private UserInfo userInfo;

    private final UserRelationCounter followerCounter;
    private final UserRelationCounter followingCounter;

    public User(long id, String userCode, UserInfo userInfo, UserRelationCounter followerCounter, UserRelationCounter followingCounter) {

        this.id = id;
        this.userCode = userCode;
        this.userInfo = userInfo;
        this.followerCounter = followerCounter;
        this.followingCounter = followingCounter;
    }

    public void increaseFollowerCount() {
        this.followerCounter.increase();
    }

    public void decreaseFollowerCount() {
        this.followerCounter.decrease();
    }

    public void increaseFollowingCount() {
        this.followingCounter.increase();
    }

    public void decreaseFollowingCount() {
        this.followingCounter.decrease();
    }

    public boolean isSameUser(User other) {
        if (other == null) {
            return false;
        }

        return Objects.equals(this.userCode, other.userCode);
    }

    @Override
    public boolean equals(Object o) {
        if(this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return Objects.equals(userCode, user.userCode);
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userCode);
    }


}
