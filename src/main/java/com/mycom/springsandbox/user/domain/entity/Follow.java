package com.mycom.springsandbox.user.domain.entity;

import java.util.Objects;

public class Follow {

    private final User follower;
    private final User following;

    public Follow(User follower, User following) {

        if (follower == null) {
            throw new IllegalArgumentException("팔로우 요청자는 필수입니다.");
        }

        if (following == null) {
            throw new IllegalArgumentException("팔로우 대상자는 필수입니다.");
        }

        if (follower.isSameUser(following)) {
            throw new IllegalArgumentException("자기 자신은 팔로우할 수 없습니다.");
        }

        this.follower = follower;
        this.following = following;
    }

    public void apply() {
        follower.increaseFollowingCount();
        following.increaseFollowerCount();
    }

    public void cancel() {
        follower.decreaseFollowingCount();
        following.decreaseFollowerCount();
    }

    public boolean isSameRelation(User follower, User following) {
        return this.follower.isSameUser(follower)
                && this.following.isSameUser(following);
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;

        Follow follow = (Follow) o;
        return Objects.equals(follower, follow.follower)
                && Objects.equals(following, follow.following);
    }

    @Override
    public int hashCode() {
        return Objects.hash(follower, following);
    }
}
