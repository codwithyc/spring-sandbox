package com.mycom.springsandbox.user.domain.entity;

import com.mycom.springsandbox.user.domain.type.UserRole;
import com.mycom.springsandbox.user.domain.type.UserStatus;

import java.util.Objects;

public class User {

    private final long id; // 내부 식별자
    private final String userCode; //외부 공개 식별자

    // 필수 속성
    private String username;
    private String email;
    private String phoneNumber;
    private String password;

    private UserRole userRole;
    private UserStatus userStatus;

    // 선택 속성
    private String ProfileImageUrl;

    public User(long id, String userCode) {
        this.id = id;
        this.userCode = userCode;
    }


    @Override
    public boolean equals(Object o) {
        if (o == null || getClass() != o.getClass()) return false;
        User user = (User) o;
        return userCode == user.userCode;
    }

    @Override
    public int hashCode() {
        return Objects.hashCode(userCode);
    }


}
