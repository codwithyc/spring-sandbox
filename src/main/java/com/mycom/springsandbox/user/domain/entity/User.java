package com.mycom.springsandbox.user.domain.entity;

import com.mycom.springsandbox.user.domain.type.UserRole;
import com.mycom.springsandbox.user.domain.type.UserStatus;

import java.util.Objects;

public class User {

    private final long id; // 내부 식별자
    private final String userCode; //외부 공개 식별자

    // 필수 속성
    private String loginId; // 사용자가 입력하는 로그인 아이디
    private String username; // 사용자가 입력하는 사용자명
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
