package com.mycom.springsandbox.user.domain.vo;

public class UserRelationCounter {

    private int count;

    public UserRelationCounter(){
        this(0);
    }

    public UserRelationCounter(int count){
        if(count < 0){
            throw new IllegalArgumentException("팔로우 수는 0보다 작을 수 없습니다.");
        }

        this.count = count;
    }

    public void increase(){
        this.count++;
    }

    public void decrease(){
        if(this.count <= 0 ){
            throw new IllegalArgumentException("팔로우 수는 0보다 작을 수 없습니다.");
        }
        this.count--;
    }

}
