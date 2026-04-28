package com.mycom.springsandbox.user.domain.entity;
import com.mycom.springsandbox.user.domain.vo.UserInfo;
import java.util.Objects;

public class User {

    private final long id; // 내부 식별자
    private final String userCode; //외부 공개 식별자

    private UserInfo userInfo;

    public User(long id, String userCode, UserInfo userInfo) {

        this.id = id;
        this.userCode = userCode;
        this.userInfo = userInfo;
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
