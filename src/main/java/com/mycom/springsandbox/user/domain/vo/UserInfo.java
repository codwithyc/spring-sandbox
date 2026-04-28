package com.mycom.springsandbox.user.domain.vo;

public class UserInfo {

    private final String username;
    private final String profileImageUrl;

    public UserInfo(String username, String profileImageUrl) {
        if(username == null || username.isBlank()){
            throw new IllegalArgumentException("사용자명은 필수입니다.");
        }

        this.username = username;
        this.profileImageUrl = profileImageUrl;
    }
}
